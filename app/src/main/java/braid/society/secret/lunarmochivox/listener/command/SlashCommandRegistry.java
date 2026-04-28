package braid.society.secret.lunarmochivox.listener.command;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlashCommandRegistry extends ListenerAdapter {

  private static final Logger log = LoggerFactory.getLogger(SlashCommandRegistry.class);
  private static final List<CommandCascade> commands;

  static {
    commands = List.of(
      new PingCommand(),
      new LicenseCommand()
    );
  }

  public SlashCommandRegistry() {
  }

  @Override
  public void onReady(@NonNull ReadyEvent event) {
    log.info("Registering {} slash command(s)...", commands.size());
    JDA jda = event.getJDA();
    CommandListUpdateAction update = jda.updateCommands();
    commands.stream()
      .map(CommandCascade::getCommandName)
      .forEach(name -> log.debug("Registering slash command : {}", name));
    update.addCommands(commands.stream().map(CommandCascade::constructSlashCommand).toList()).queue(
      _ -> log.info("Successfully registered {} slash command(s)", commands.size()),
      fail -> log.error("Failed to register slash commands", fail)
    );
  }

  public List<ListenerAdapter> getCommandListeners() {
    return commands.stream().map(command -> (ListenerAdapter) command).toList();
  }
}
