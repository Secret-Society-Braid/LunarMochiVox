package braid.society.secret.lunarmochivox.listener.command;

import braid.society.secret.lunarmochivox.util.MetaPropertyUtil;
import java.awt.Color;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportCommand extends CommandCascade {
  private static final Logger log = LoggerFactory.getLogger(ReportCommand.class);

  protected ReportCommand() {
    super("report", "Report abnormality to developer.");
    this.addSupportedContext(InteractionContextType.GUILD);
    this.addOption(
      new OptionData(OptionType.STRING, "description", "報告したい内容を入力してください", true, true)
    );
  }

  @Override
  protected void respond(SlashCommandInteractionEvent event) {
    // We don't have to care about nullness since `description` is required.
    Instant reportTime = Instant.now();
    CompletableFuture<InteractionHook> deferred = event.deferReply(true).submit();
    String content = Objects.requireNonNull(event.getOption("description")).getAsString();
    User author = event.getUser();
    log.info("Report received by: {}", author);
    log.info("Report content: {}", content);
    EmbedBuilder replyEmbed = new EmbedBuilder();
    replyEmbed.setTitle("不具合報告完了");
    replyEmbed.setColor(Color.GREEN);
    replyEmbed.setDescription("ご報告ありがとうございます。開発者が確認次第、対応いたします。");
    replyEmbed.setThumbnail(author.getAvatarUrl());
    replyEmbed.addField("報告内容", content, true);
    replyEmbed.setFooter("追加の情報がございます場合は、開発者 @idol_ranfa_master までご連絡くださいませ。この度はご不便おかけし申し訳ございません。");

    deferred.thenComposeAsync(h -> h.editOriginalEmbeds(replyEmbed.build()).submit());
    event.getJDA().retrieveUserById(MetaPropertyUtil.getDevUserId())
      .submit()
      .thenComposeAsync(u -> u.openPrivateChannel().submit())
      .thenComposeAsync(channel -> channel.sendMessageEmbeds(
        new EmbedBuilder()
          .setTitle("新しい不具合報告")
          .setColor(Color.RED)
          .addField("報告者", author.getName(), true)
          .addField("報告日時", reportTime.toString(), true)
          .addField("報告内容", content, false)
          .build()
      ).submit());
  }
}
