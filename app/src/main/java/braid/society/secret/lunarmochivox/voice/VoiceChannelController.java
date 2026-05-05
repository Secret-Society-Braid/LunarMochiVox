package braid.society.secret.lunarmochivox.voice;

import braid.society.secret.lunarmochivox.util.ConcurrencyUtil;
import com.google.common.base.Strings;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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

public class VoiceChannelController {

  private final Map<VoiceChannel, AudioManager> openChannelList;
  private final Map<VoiceChannel, TextChannel> boundTextChannelList;
  private final Set<Guild> boundGuildList;
  private static VoiceChannelController instance;
  private static final ThreadFactory afkCheckThreadFactory = ConcurrencyUtil.createThreadFactory("VoiceChannelController-AFKCheck");
  private final Map<VoiceChannel, ScheduledExecutorService> afkCheckSchedulerList;

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
      deferred.thenCompose(h -> h.editOriginal("すでに接続されているようです。二重参加はできません！").submit());
      return;
    }
    TextChannel boundTextChannel = event.getGuildChannel().asTextChannel();
    VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
    Guild guild = event.getGuild();
    CompletableFuture<Message> channelValid = deferred.thenCompose(h -> h.editOriginal("ボイスチャンネルが見つかりました！接続しています……").submit());
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
    afkCheckScheduler.scheduleAtFixedRate(new AFKChecker(voiceChannel, audioManager), 1, 5, TimeUnit.SECONDS);
    this.afkCheckSchedulerList.put(voiceChannel, afkCheckScheduler);
    channelValid
      .thenCompose(m -> m.editMessage("接続が完了しました！").submit())
      .thenCompose(_ -> {
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("接続完了");
        embedBuilder.setDescription(String.format("ボイスチャンネル「%s」に接続しました！", voiceChannel.getName()));
        embedBuilder.appendDescription("このBotではVOICEVOXを使用しています。ライセンス条項を確認する場合は `/license`コマンドを使用してください。");
        embedBuilder.setFooter("LunarMochiVox", null);
        embedBuilder.setColor(0x00FF00);
        return boundTextChannel.sendMessageEmbeds(embedBuilder.build()).submit();
      });
  }

  private String orElseEnv(String envKey, String defaultValue) {
    final String value = System.getenv(envKey);
    return Strings.isNullOrEmpty(value) ? defaultValue : value;
  }
}
