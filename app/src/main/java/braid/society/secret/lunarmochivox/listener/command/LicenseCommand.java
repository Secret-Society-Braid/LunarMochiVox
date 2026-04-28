package braid.society.secret.lunarmochivox.listener.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class LicenseCommand extends CommandCascade {

  protected LicenseCommand() {
    super("license", "Show OSS licenses used in this bot application.");
    this.addSupportedContext(InteractionContextType.GUILD);
    this.addSupportedContext(InteractionContextType.BOT_DM);
  }

  @Override
  protected void respond(SlashCommandInteractionEvent event) {
    final MessageEmbed embed = new EmbedBuilder()
      .setTitle("オープンソースライセンス")
      .addField("オンライン上での確認", "オンライン上での確認は[こちら]()から行えます。", false)
      .addField("オフラインでの確認を希望する場合",
        "個別対応となります。開発者：idol_ranfa_master まで要件を添えてメッセージをお願いします。",
        false)
      .build();
    event.deferReply()
      .queue(success -> success.editOriginalEmbeds(embed).queue());
  }
}
