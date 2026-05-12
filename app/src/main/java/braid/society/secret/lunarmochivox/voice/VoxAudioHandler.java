package braid.society.secret.lunarmochivox.voice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.User;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoxAudioHandler implements AudioSendHandler {

  private byte[] out;
  private int position;
  private ByteBuffer lastFrame;
  private boolean isSpeaking;
  private final VOICEVOXTtsEngine engine;
  private static final Logger log = LoggerFactory.getLogger(VoxAudioHandler.class);
  private final Lock lock = new ReentrantLock();
  private final Condition speakingCondition = lock.newCondition();

  public VoxAudioHandler(VOICEVOXTtsEngine engine) {
    this.engine = engine;
  }

  public VoxAudioHandler(String webApiHost, int webApiPort) {
    this(VOICEVOXTtsEngine.getInstance(webApiHost, webApiPort));
  }

  void setSpeaking(boolean speaking) {
    lock.lock();
    try {
      isSpeaking = speaking;
      speakingCondition.signalAll();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean canProvide() {
    // Defensive: if we don't have any audio yet, we can't provide
    if (out == null) {
      return false;
    }

    boolean canProvide = position < out.length;

    if (canProvide) {
      // Ensure we don't request more than remaining bytes
      int frame = Math.min(engine.getAudioFrame(), out.length - position);
      lastFrame = ByteBuffer.wrap(out, position, frame);
      position += frame;

      if (position >= out.length) {
        log.info("Bot may finish speaking.");
        setSpeaking(false);
      }
    }

    return canProvide;
  }

  public void speak(String phrase, User author) throws IOException {
    // Wait while someone else is speaking, then mark ourselves speaking while holding the lock
    lock.lock();
    try {
      while (this.isSpeaking) {
        speakingCondition.await();
      }
      // Mark speaking while still under lock to avoid races where canProvide is invoked
      // before out is assigned
      this.isSpeaking = true;
    } catch (InterruptedException e) {
      log.error("Someone interrupted me while waiting for speaking condition", e);
      // If interrupted, ensure we don't stay in a speaking state
      this.isSpeaking = false;
      throw new IOException("Interrupted while waiting to speak", e);
    } finally {
      lock.unlock();
    }

    // Generate audio outside the lock (potentially slow/blocking)
    this.out = engine.say(phrase, author);
    this.position = 0;
  }

  @Nullable
  @Override
  public ByteBuffer provide20MsAudio() {
    return this.lastFrame;
  }
}
