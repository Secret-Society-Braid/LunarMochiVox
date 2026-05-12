package braid.society.secret.lunarmochivox.listener;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import braid.society.secret.lunarmochivox.voice.VoiceChannelController;
import braid.society.secret.lunarmochivox.voice.VoxAudioHandler;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class BoundTextChannelSpeechListenerTest {

  private final ExecutorService speechExecutor = Executors.newSingleThreadExecutor();
  private final VoiceChannelController controller = mock(VoiceChannelController.class);
  private final BoundTextChannelSpeechListener listener =
    new BoundTextChannelSpeechListener(controller, speechExecutor);

  @AfterEach
  void tearDown() {
    listener.shutdown();
  }

  @Test
  void onMessageReceived_ignoresMessageFromUnboundChannel() {
    MessageReceivedEvent event = mockMessageEvent("hello");
    TextChannel textChannel = event.getChannel().asTextChannel();
    when(controller.isBoundTextChannel(textChannel)).thenReturn(false);

    listener.onMessageReceived(event);

    verify(controller, never()).findBoundAudioHandler(any());
  }

  @Test
  void onMessageReceived_ignoresBlankMessage() {
    MessageReceivedEvent event = mockMessageEvent("  ");
    TextChannel textChannel = event.getChannel().asTextChannel();
    when(controller.isBoundTextChannel(textChannel)).thenReturn(true);

    listener.onMessageReceived(event);

    verify(controller, never()).findBoundAudioHandler(any());
  }

  @Test
  void onMessageReceived_enqueuesSpeakForBoundChannel() throws Exception {
    MessageReceivedEvent event = mockMessageEvent("mochi");
    TextChannel textChannel = event.getChannel().asTextChannel();
    User author = event.getAuthor();
    VoxAudioHandler handler = mock(VoxAudioHandler.class);
    when(controller.isBoundTextChannel(textChannel)).thenReturn(true);
    when(controller.findBoundAudioHandler(textChannel)).thenReturn(Optional.of(handler));

    listener.onMessageReceived(event);

    verify(handler, timeout(500)).speak("mochi", author);
  }

  private MessageReceivedEvent mockMessageEvent(String messageBody) {
    MessageReceivedEvent event = mock(MessageReceivedEvent.class, RETURNS_DEEP_STUBS);
    TextChannel textChannel = mock(TextChannel.class);
    User author = mock(User.class);
    Message message = mock(Message.class);

    when(event.getAuthor()).thenReturn(author);
    when(event.getMessage()).thenReturn(message);
    when(event.getChannel().asTextChannel()).thenReturn(textChannel);
    when(event.isFromGuild()).thenReturn(true);
    when(event.isFromType(ChannelType.TEXT)).thenReturn(true);

    when(author.isBot()).thenReturn(false);
    when(message.getContentDisplay()).thenReturn(messageBody);

    return event;
  }
}




