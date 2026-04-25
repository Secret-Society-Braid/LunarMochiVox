package braid.society.secret.lunarmochivox;

import braid.society.secret.lunarmochivox.listener.ReadyListener;
import braid.society.secret.lunarmochivox.listener.command.SlashCommandRegistry;
import java.util.List;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LunarMochiVoxApp {

  private static final Logger log = LoggerFactory.getLogger(LunarMochiVoxApp.class);

  private LunarMochiVoxApp() {
    // Utility class
  }

  static void main(String[] args) {
    log.info("LunarMochiVox is starting...");
    String token = System.getenv("LUNAR_MOCHI_VOX_TOKEN");
    if (token == null || token.isBlank()) {
      if (args.length == 0) {
        log.error(
          "LUNAR_MOCHI_VOX_TOKEN environment variable or first argument is missing. Exiting.");
        System.exit(-1);
      } else {
        log.warn("LUNAR_MOCHI_VOX_TOKEN environment variable is missing.");
        log.warn("This behaviour is used for development purposes only.");
        log.warn("Please consider setting the LUNAR_MOCHI_VOX_TOKEN environment variable.");
        token = args[0];
      }
    }
    log.debug("The lunar key has found. initiate launching...");
    new LunarMochiVoxApp().start(token);
  }

  private void start(String token) {
    JDABuilder builder = JDABuilder.createDefault(token);
    log.debug("Building JDA with token : (omitted)");
    final SlashCommandRegistry slashCommandRegistry = new SlashCommandRegistry();
    final List<CacheFlag> cacheFlagsToDisable = List.of(CacheFlag.MEMBER_OVERRIDES);
    final List<GatewayIntent> gatewayIntentsToEnable = List.of(
      GatewayIntent.MESSAGE_CONTENT,
      GatewayIntent.GUILD_VOICE_STATES,
      GatewayIntent.GUILD_MESSAGES,
      GatewayIntent.GUILD_MESSAGE_REACTIONS,
      GatewayIntent.GUILD_EXPRESSIONS,
      GatewayIntent.GUILD_MEMBERS
    );
    builder
      .disableCache(cacheFlagsToDisable)
      .enableIntents(gatewayIntentsToEnable)
      .setActivity(Activity.listening("the sound of the moon"))
      .addEventListeners(
        new ReadyListener(),
        // Register command registry to push all slash commands metadata
        slashCommandRegistry)
      // Register all command responding to make commands work
      .addEventListeners(slashCommandRegistry.getCommandListeners().toArray());
    builder.build();
  }
}
