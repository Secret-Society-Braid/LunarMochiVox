package braid.society.secret.lunarmochivox.util;

import java.util.concurrent.ThreadFactory;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;

public class ConcurrencyUtil {

  private ConcurrencyUtil() {}

  public static ThreadFactory createThreadFactory(String threadName) {
    return new CountingThreadFactory(() -> "LunarMochiVox", threadName);
  }
}
