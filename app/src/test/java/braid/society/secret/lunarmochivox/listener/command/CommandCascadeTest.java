package braid.society.secret.lunarmochivox.listener.command;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.Test;

class CommandCascadeTest {

  @Test
  void onSlashCommandInteraction_ignoresNonMatchingCommandName() {
    TestCommandCascade command = new TestCommandCascade("ping");
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    when(event.getName()).thenReturn("pong");

    command.onSlashCommandInteraction(event);

    assertThat(command.respondCallCount()).isEqualTo(0);
  }

  @Test
  void onSlashCommandInteraction_invokesRespondOnceForMatchingCommandName() {
    TestCommandCascade command = new TestCommandCascade("ping");
    SlashCommandInteractionEvent event = mock(SlashCommandInteractionEvent.class);
    when(event.getName()).thenReturn("ping");

    command.onSlashCommandInteraction(event);

    assertThat(command.respondCallCount()).isEqualTo(1);
    assertThat(command.lastRespondEvent()).isSameInstanceAs(event);
  }

  private static final class TestCommandCascade extends CommandCascade {

    private int respondCallCount;
    private SlashCommandInteractionEvent lastRespondEvent;

    private TestCommandCascade(String commandName) {
      super(commandName, "test command");
    }

    @Override
    protected void respond(SlashCommandInteractionEvent event) {
      respondCallCount++;
      lastRespondEvent = event;
    }

    private int respondCallCount() {
      return respondCallCount;
    }

    private SlashCommandInteractionEvent lastRespondEvent() {
      return lastRespondEvent;
    }
  }
}

