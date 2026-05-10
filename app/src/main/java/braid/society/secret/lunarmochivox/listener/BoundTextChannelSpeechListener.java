package braid.society.secret.lunarmochivox.listener;

import braid.society.secret.lunarmochivox.util.ConcurrencyUtil;
import braid.society.secret.lunarmochivox.voice.VoiceChannelController;
import braid.society.secret.lunarmochivox.voice.VoxAudioHandler;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundTextChannelSpeechListener extends ListenerAdapter {

  private static final Logger log = LoggerFactory.getLogger(BoundTextChannelSpeechListener.class);

  private final VoiceChannelController controller;
  private final ExecutorService speechExecutor;

  public BoundTextChannelSpeechListener(VoiceChannelController controller) {
    this(controller, Executors.newSingleThreadExecutor(
      ConcurrencyUtil.createThreadFactory("LunarMochiVox-BoundTextChannelSpeech")));
  }

  BoundTextChannelSpeechListener(VoiceChannelController controller, ExecutorService speechExecutor) {
    this.controller = controller;
    this.speechExecutor = speechExecutor;
  }

  @Override
  public void onMessageReceived(@NonNull MessageReceivedEvent event) {
    if (!event.isFromGuild() || event.getAuthor().isBot() || !event.isFromType(ChannelType.TEXT)) {
      return;
    }
    TextChannel textChannel = event.getChannel().asTextChannel();
    if (!controller.isBoundTextChannel(textChannel)) {
      return;
    }

    Message message = event.getMessage();
    String text = message.getContentDisplay();
    if (Strings.isNullOrEmpty(text) || text.isBlank()) {
      return;
    }

    controller.findBoundAudioHandler(textChannel).ifPresent(handler -> this.enqueueSpeak(handler, text, event));
  }

  private void enqueueSpeak(VoxAudioHandler handler, String text, MessageReceivedEvent event) {
    speechExecutor.submit(() -> {
      try {
        handler.speak(text, event.getAuthor());
      } catch (IOException e) {
        log.warn("Failed to synthesize message from {} in #{}", event.getAuthor().getAsTag(),
          event.getChannel().getName(), e);
      }
    });
  }

  public void shutdown() {
    speechExecutor.shutdown();
  }
}



