package braid.society.secret.lunarmochivox.listener.command;

import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class LicenseCommand extends CommandCascade {

  protected LicenseCommand() {
    super("license", "Show OSS licenses used in this bot application.");
    this.addSupportedContext(InteractionContextType.GUILD);
    this.addSupportedContext(InteractionContextType.BOT_DM);
  }

  @Override
  protected void respond(SlashCommandInteractionEvent event) {
    CompletableFuture<InteractionHook> deferred = event.deferReply(true).submit();
    final MessageEmbed licenseEmbed = new EmbedBuilder()
      .setTitle("オープンソースライセンス",
        "https://github.com/Secret-Society-Braid/LunarMochiVox/blob/main/docs/clauses/OPEN_SOURCE_LICENSES.md")
      .addField("オンラインでの確認", "このメッセージタイトルのリンクより確認できます。", false)
      .addField("オフラインでの確認を希望する場合",
        "個別対応となります。開発者 idol_ranfa_master まで要件を添えてご連絡ください。", false)
      .setFooter("LunarMochiVox - Secret Society Braid")
      .build();
    deferred.thenAccept(h -> h.sendMessageEmbeds(licenseEmbed).queue());
  }
}
