package braid.society.secret.lunarmochivox.commands;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class CommandCascade extends ListenerAdapter {

  private final String commandName;
  private final String inlineDescription;
  private final List<OptionData> commandOptions;
  private final List<InteractionContextType> supportedContexts;

  protected CommandCascade(String commandName, String inlineDescription,
    List<OptionData> commandOptions, List<InteractionContextType> supportedContexts) {
    this.commandName = commandName;
    this.inlineDescription = inlineDescription;
    this.commandOptions = commandOptions;
    this.supportedContexts = supportedContexts;
  }

  protected CommandCascade(String commandName, String inlineDescription) {
    this(commandName, inlineDescription, new ArrayList<>(), new ArrayList<>());
  }

  protected final CommandData constructSlashCommand() {
    SlashCommandData commandData = Commands.slash(getCommandName(), getInlineDescription());
    commandData.addOptions(getCommandOptions());
    commandData.setContexts(getSupportedContexts());
    return commandData;
  }

  final String getCommandName() {
    return commandName;
  }

  final String getInlineDescription() {
    return inlineDescription;
  }

  final List<OptionData> getCommandOptions() {
    return commandOptions;
  }

  final List<InteractionContextType> getSupportedContexts() {
    return supportedContexts;
  }

  protected void addOption(OptionData option) {
    this.commandOptions.add(option);
  }

  protected void addSupportedContext(InteractionContextType context) {
    this.supportedContexts.add(context);
  }

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    if (!event.getName().equals(commandName)) {
      return;
    }
    this.respond(event);
  }

  protected abstract void respond(SlashCommandInteractionEvent event);
}
