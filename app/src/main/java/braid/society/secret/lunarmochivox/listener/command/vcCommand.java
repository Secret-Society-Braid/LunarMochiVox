package braid.society.secret.lunarmochivox.listener.command;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class vcCommand extends CommandCascade {

  private static final Logger log = LoggerFactory.getLogger(vcCommand.class);

  public vcCommand() {
    super("vc", "Checks whether you are in a voice channel, and connects you to the first one if not.");
    this.addSupportedContext(InteractionContextType.GUILD);
  }

  @Override
  protected void respond(SlashCommandInteractionEvent event) {
    final User author = event.getUser();
    log.trace("Received VC command from user {}", event.getUser().getAsTag());


  }
}
