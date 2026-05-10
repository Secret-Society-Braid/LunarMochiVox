package braid.society.secret.lunarmochivox.voice;

import braid.society.secret.lunarmochivox.util.ConcurrencyUtil;
import com.google.common.base.Strings;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoiceChannelController {

  private final Map<VoiceChannel, AudioManager> openChannelList;
  private final Map<VoiceChannel, TextChannel> boundTextChannelList;
  private final Set<Guild> boundGuildList;
  private static VoiceChannelController instance;
  private static final ThreadFactory afkCheckThreadFactory = ConcurrencyUtil.createThreadFactory("VoiceChannelController-AFKCheck");
  private final Map<VoiceChannel, ScheduledExecutorService> afkCheckSchedulerList;
  private static final Logger log = LoggerFactory.getLogger(VoiceChannelController.class);

  private VoiceChannelController() {
    this.openChannelList = new ConcurrentHashMap<>(5);
    this.boundTextChannelList = new ConcurrentHashMap<>(5);
    this.afkCheckSchedulerList = new ConcurrentHashMap<>(5);
    this.boundGuildList = ConcurrentHashMap.newKeySet(5);
  }

  public static VoiceChannelController getInstance() {
    if (instance == null) {
      instance = new VoiceChannelController();
    }
    return instance;
  }

  public boolean checkConnected(SlashCommandInteractionEvent event) {
    Guild guild = event.getGuild();
    return this.boundGuildList.contains(guild);
  }

  public void onConnect(SlashCommandInteractionEvent event) {
    CompletableFuture<InteractionHook> deferred = event.deferReply().submit();
    if(this.checkConnected(event)) {
      deferred.thenComposeAsync(h -> h.editOriginal("すでに接続されているようです。二重参加はできません！").submit());
      return;
    }
    TextChannel boundTextChannel = event.getGuildChannel().asTextChannel();
    VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
    Guild guild = event.getGuild();
    CompletableFuture<Message> channelValid = deferred.thenComposeAsync(h -> h.editOriginal("ボイスチャンネルが見つかりました！接続しています……").submit());
    AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
    audioManager.openAudioConnection(voiceChannel);
    audioManager.setSendingHandler(
      new VoxAudioHandler(orElseEnv("LUNAR_MOCHI_VOX_ENGINE_HOST", "localhost"),
        Integer.parseInt(orElseEnv("LUNAR_MOCHI_VOX_ENGINE_PORT", "50021")))
    );
    this.openChannelList.put(voiceChannel, audioManager);
    this.boundTextChannelList.put(voiceChannel, boundTextChannel);
    this.boundGuildList.add(guild);
    ScheduledExecutorService afkCheckScheduler = Executors.newSingleThreadScheduledExecutor(afkCheckThreadFactory);
    afkCheckScheduler.scheduleAtFixedRate(
      () -> this.checkVoiceChannelAfk(boundTextChannel, audioManager),
      1, 5, TimeUnit.SECONDS);
    this.afkCheckSchedulerList.put(voiceChannel, afkCheckScheduler);
    channelValid
      .thenComposeAsync(m -> m.editMessage("接続が完了しました！").submit())
      .thenComposeAsync(_ -> {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("接続完了");
        embedBuilder.setDescription(String.format("ボイスチャンネル「%s」に接続しました！", voiceChannel.getName()));
        embedBuilder.appendDescription("このBotではVOICEVOXを使用しています。ライセンス条項を確認する場合は `/license`コマンドを使用してください。");
        embedBuilder.setFooter("LunarMochiVox", null);
        embedBuilder.setColor(0x00FF00);
        return boundTextChannel.sendMessageEmbeds(embedBuilder.build()).submit();
      }).whenCompleteAsync((m, t) -> {
        if(t != null) {
          log.warn("Failed to send connection confirmation message to TextChannel {} in Guild {}. Disconnecting from the VoiceChannel {} to prevent being stuck in a channel without control.", boundTextChannel.getName(), guild.getName(), voiceChannel.getName(), t);
          return;
        }
        log.debug("Successfully connected to VoiceChannel {} in Guild {} and sent confirmation message to TextChannel {}", voiceChannel.getName(), guild.getName(), boundTextChannel.getName());
      });
  }

  public void onDisconnect(SlashCommandInteractionEvent event) {
    CompletableFuture<InteractionHook> deferred = event.deferReply().submit();
    VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
    AudioManager audioManager = voiceChannel.getGuild().getAudioManager();
    if(!audioManager.isConnected()) {
      deferred.thenCompose(h -> h.editOriginal("Botを入室させたVCとは別のチャンネルにいるようです。Botがいるチャンネルに入りなおして再度お試しください。").submit());
    }
    deferred.thenCompose(
      h -> h.editOriginal("切断しています……おやすみなさい。").submit()
    );
    this.postCleanUp(voiceChannel, audioManager, this.boundTextChannelList.get(voiceChannel));
  }

  private void checkVoiceChannelAfk(TextChannel boundTextChannel, AudioManager audioManager) {
    if(audioManager.isConnected() && audioManager.getConnectedChannel().getMembers().size() <= 1) {
      boundTextChannel.sendMessage("誰もいないみたいなので、接続を切断しました。おやすみなさい。")
        .queue();
      this.postCleanUp(audioManager.getConnectedChannel().asVoiceChannel(), audioManager,
        boundTextChannel);
    }
  }

  public boolean isBoundTextChannel(TextChannel textChannel) {
    return this.boundTextChannelList.values().stream()
      .anyMatch(channel -> channel.getIdLong() == textChannel.getIdLong());
  }

  public Optional<VoxAudioHandler> findBoundAudioHandler(TextChannel textChannel) {
    return this.boundTextChannelList.entrySet().stream()
      .filter(entry -> entry.getValue().getIdLong() == textChannel.getIdLong())
      .findFirst()
      .map(Map.Entry::getKey)
      .map(this.openChannelList::get)
      .filter(AudioManager::isConnected)
      .map(AudioManager::getSendingHandler)
      .filter(VoxAudioHandler.class::isInstance)
      .map(VoxAudioHandler.class::cast);
  }

  private void postCleanUp(VoiceChannel voiceChannel, AudioManager audioManager,
    TextChannel boundTextChannel) {
    if (voiceChannel == null || boundTextChannel == null) {
      log.warn("Skip cleanup because voice/text channel binding is missing.");
      audioManager.closeAudioConnection();
      return;
    }
    audioManager.closeAudioConnection();
    Guild connectedGuild = boundTextChannel.getGuild();
    boundGuildList.remove(connectedGuild);
    boundTextChannelList.remove(voiceChannel);
    openChannelList.remove(voiceChannel);
    ExecutorService service = this.afkCheckSchedulerList.remove(voiceChannel);
    if (service != null) {
      service.shutdown();
    }
    log.info("Disconnected from VoiceChannel {} in {}", voiceChannel.getName(),
      connectedGuild.getName());
  }

  private String orElseEnv(String envKey, String defaultValue) {
    final String value = System.getenv(envKey);
    return Strings.isNullOrEmpty(value) ? defaultValue : value;
  }

}
