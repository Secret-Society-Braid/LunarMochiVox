package braid.society.secret.lunarmochivox.voice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.dv8tion.jda.api.entities.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VOICEVOXTtsEngine implements TtsEngine {

  public static final OkHttpClient client = new Builder().build();
  // 48,000 (frames per sec) / 50 (number of 20ms in a second) * 2 (16-bit PCM) * 2 (channels)
  private static final int AUDIO_FRAME = 3840;
  private final HttpUrl engineApiUrl;
  private static VOICEVOXTtsEngine instance;
  private static final Logger log = LoggerFactory.getLogger(VOICEVOXTtsEngine.class);
  private static List<Speaker> speakerCache;
  private static ObjectMapper MAPPER = new ObjectMapper();
  private final Map<Integer, Set<User>> tiedSpeakerCache = new ConcurrentHashMap<>(10);
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final AtomicBoolean prevAttemptFailed = new AtomicBoolean(false);

  private VOICEVOXTtsEngine(String engineApiBaseUrl, int engineApiPort) {
    this.engineApiUrl = new HttpUrl.Builder()
      .scheme("http")
      .host(engineApiBaseUrl)
      .port(engineApiPort)
      .build();
  }

  public int getAudioFrame() {
    return AUDIO_FRAME;
  }

  public static VOICEVOXTtsEngine getInstance(String engineApiBaseUrl, int engineApiPort) {
    if(prevAttemptFailed.get()) {
      log.error("Initialization of this engine failed previously.");
      log.error("Please reboot the bot to retry.");
      throw new RuntimeException("Initialization of this engine failed previously.");
    }
    if (instance == null) {
      instance = new VOICEVOXTtsEngine(engineApiBaseUrl, engineApiPort);
    }
    instance.loadSpeakers();
    return instance;
  }

  public static VOICEVOXTtsEngine getDefaultIfAbsent() {
    return getInstance("localhost", 50021);
  }

  private void loadSpeakers() {
    HttpUrl speakerUri = engineApiUrl.newBuilder().addPathSegment("speakers").build();
    Request request = new Request.Builder()
      .get()
      .url(speakerUri)
      .addHeader("Accept", "application/json")
      .build();
    client.newCall(request).enqueue(new SpeakerLoadCallback());
  }

  private int selectSpeakerId(User user, int defaultSpeakerId) {
    if (speakerCache == null || speakerCache.isEmpty()) {
      throw new IllegalStateException("No speakers loaded from VoiceVox API");
    }
    for (Map.Entry<Integer, Set<User>> entry : tiedSpeakerCache.entrySet()) {
      if (entry.getValue().contains(user)) {
        return speakerCache.stream()
          .flatMap(s -> s.styles().stream())
          .filter(s -> s.id() == entry.getKey())
          .findFirst()
          .map(Speaker.Style::id)
          .orElse(defaultSpeakerId); // Default to a common speaker ID if not found
      }
    }
    // Randomly select a speaker and style
    Speaker randomSpeaker = speakerCache.get(secureRandom.nextInt(speakerCache.size()));
    Speaker.Style style = randomSpeaker.styles()
      .get(secureRandom.nextInt(randomSpeaker.styles().size()));
    tiedSpeakerCache
      .computeIfAbsent(style.id(), _ -> ConcurrentHashMap.newKeySet())
      .add(user);
    return style.id();
  }

  private String retrieveJsonAudioQuery(String phrase, int speakerId) {
    HttpUrl constructedUrl = this.engineApiUrl
      .newBuilder()
      .addPathSegment("audio_query")
      .addQueryParameter("speaker", String.valueOf(speakerId))
      .addEncodedQueryParameter("text", phrase)
      .build();
    final RequestBody body = RequestBody.create("", MediaType.get("application/json"));
    Request audioQueryRequest = new Request.Builder()
      .url(constructedUrl)
      .post(body)
      .addHeader("Accept", "application/json")
      .build();
    String jsonAudioQuery;
    try (Response response = client.newCall(audioQueryRequest).execute()) {
      jsonAudioQuery = Objects.requireNonNull(response.body()).string();
      log.trace("audio query created for phrase '{}'", phrase);
    } catch (IOException e) {
      throw new RuntimeException("Failed to retrieve audio query from VoiceVox API", e);
    }
    return jsonAudioQuery;
  }

  private byte[] tts(String audioQuery, int speakerId) throws IOException {
    HttpUrl constructedUrl = this.engineApiUrl
      .newBuilder()
      .addPathSegment("synthesis")
      .addQueryParameter("speaker", String.valueOf(speakerId))
      .build();
    RequestBody body = RequestBody.create(audioQuery, MediaType.get("application/json"));
    Request synthesisRequest = new Request.Builder()
      .url(constructedUrl)
      .post(body)
      .addHeader("Accept", "audio/wav")
      .build();

    try (Response response = client.newCall(synthesisRequest).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }
      log.trace("synthesize completed.");
      return Objects.requireNonNull(response.body()).bytes();
    }
  }

  private byte[] resampling(byte[] input, int bitsPerSample, int sourceSampleRate,
    int targetSampleRate) {
    int bytePerSample = bitsPerSample / 8;
    int numSamples = input.length / bytePerSample;
    short[] amplitudes = new short[numSamples];
    int pointer = 0;
    for (int i = 0; i < numSamples; i++) {
      short amplitude = 0;
      for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
        amplitude |= (short) ((input[pointer++] & 0xFF) << (byteNumber * 8));
      }
      amplitudes[i] = amplitude;
    }
    // Linear interpolation
    short[] targetSample = interpolate(sourceSampleRate, targetSampleRate, amplitudes);
    int targetLength = targetSample.length;
    byte[] bytes;
    if (bytePerSample == 1) {
      bytes = new byte[targetLength];
      for (int i = 0; i < targetLength; i++) {
        bytes[i] = (byte) targetSample[i];
      }
    } else {
      bytes = new byte[targetLength * 2];
      for (int i = 0; i < targetSample.length; i++) {
        bytes[i * 2] = (byte) (targetSample[i] & 0xff);
        bytes[i * 2 + 1] = (byte) ((targetSample[i] >> 8) & 0xff);
      }
    }
    return bytes;
  }

  private short[] interpolate(int beforeSampleRate, int newSampleRate, short[] samples) {
    if (beforeSampleRate == newSampleRate) {
      return samples;
    }
    int newLength = Math.round((float) samples.length / beforeSampleRate * newSampleRate);
    float lengthMultiplier = (float) newLength / samples.length;
    short[] interpolatedSamples = new short[newLength];
    for (int i = 0; i < newLength; i++) {
      float currentPosition = i / lengthMultiplier;
      int nearestLeft = (int) currentPosition;
      int nearestRight = nearestLeft + 1;
      if (nearestRight >= samples.length) {
        nearestRight = samples.length - 1;
      }
      float slope = samples[nearestRight] - samples[nearestLeft];
      float positionFromLeft = currentPosition - nearestLeft;
      interpolatedSamples[i] = (short) (slope * positionFromLeft + samples[nearestLeft]);
    }
    return interpolatedSamples;
  }

  private byte[] convertToDiscordCompatible(byte[] pcm) {
    // Add a bit of silence to the end of the audio data to avoid cutting off the last frame
    byte[] converted = new byte[AUDIO_FRAME + pcm.length * 2 + (AUDIO_FRAME
      - pcm.length * 2 % AUDIO_FRAME)]; // Ensures the length is a multiple of AUDIO_FRAME
    for (int i = AUDIO_FRAME; i < pcm.length; i += 2) {
      short reversed = Short.reverseBytes((short) ((pcm[i] << 8) | (
        pcm[i + 1] & 0xFF)));
      byte low = (byte) (reversed >> 8);
      byte high = (byte) (reversed & 0x00FF);

      // reverse bytes and double to convert to stereo
      converted[i * 2] = low;
      converted[i * 2 + 1] = high;
      converted[i * 2 + 2] = low;
      converted[i * 2 + 3] = high;
    }
    return converted;
  }

  @Override
  public byte[] say(String phrase, User author) throws IOException {
    // TODO: implement caching
    final int speakerId = selectSpeakerId(author, 8);
    byte[] ttsData = tts(retrieveJsonAudioQuery(phrase, speakerId), speakerId);
    return convertToDiscordCompatible(resampling(ttsData, 16, 24000, 48000));
  }

  private static class SpeakerLoadCallback implements Callback {

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
      if(!response.isSuccessful()) {
        prevAttemptFailed.set(true);
        log.error("Engine API server responded with status code {} : {}", response.code(), response.body());
        log.error("No Engine API available at {}, unable to initialize.", call.request().url());
        throw new IllegalStateException("Engine API server responded with status code " + response.code());
      }
      InputStream is = response.body().byteStream();
      speakerCache = MAPPER.readValue(is, MAPPER.getTypeFactory().constructCollectionType(Set.class, Speaker.class));
      log.debug("Parsed response from Engine API at {} : {} speaker(s).", call.request().url(), speakerCache.size());
      prevAttemptFailed.set(false);
      log.info("Successfully loaded {} speaker(s) from {}", speakerCache.size(), call.request().url());
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
      prevAttemptFailed.set(true);
      log.error("Exception while loading speakers from Engine API", e);
      log.error("Engine API located at {} is temporarily unavailable. reboot Bot to retry.", call.request().url());
    }
  }
}
