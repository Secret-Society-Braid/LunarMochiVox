package braid.society.secret.lunarmochivox.listener.command;

import java.time.Instant;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingCommand extends CommandCascade {

  private static final Logger log = LoggerFactory.getLogger(PingCommand.class);

  public PingCommand() {
    super("ping", "Pings the bot");
    this.addSupportedContext(InteractionContextType.GUILD);
    this.addSupportedContext(InteractionContextType.BOT_DM);
  }

  @Override
  protected void respond(@NonNull SlashCommandInteractionEvent event) {
    log.trace("Received ping command from user {}", event.getUser().getAsTag());
    final Instant now = Instant.now();
    event.reply("ぽ…ぽんっ…!").queue(
      success -> {
        final long latency = Instant.now().toEpochMilli() - now.toEpochMilli();
        success.editOriginalFormat("ぽ…ぽんっ…! : ping -> %d ms", latency).queue();
      },
      error -> log.error("Failed to respond to ping command from user {}",
        event.getUser().getAsTag(), error)
    );
  }
}
