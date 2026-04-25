package braid.society.secret.lunarmochivox;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LunarMochiVoxApp {

  private static final Logger log = LoggerFactory.getLogger(LunarMochiVoxApp.class);

  private LunarMochiVoxApp() {
    // Utility class
  }

  static void main(String[] args) {
    log.info("LunarMochiVox is starting... : {}", Arrays.deepToString(args));
  }
}
