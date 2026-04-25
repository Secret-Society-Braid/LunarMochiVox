package braid.society.secret.lunarmochivox.listener;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadyListener extends ListenerAdapter {

  private static final Logger log = LoggerFactory.getLogger(ReadyListener.class);

  @Override
  public void onReady(@NonNull ReadyEvent event) {
    log.info("Bot is ready to sing! Let our moon adventure begin! : {}",
      event.getJDA().getSelfUser().getAsTag());
  }
}
