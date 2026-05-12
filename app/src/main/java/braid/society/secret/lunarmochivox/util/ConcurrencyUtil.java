package braid.society.secret.lunarmochivox.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import org.jspecify.annotations.NonNull;

public class ConcurrencyUtil {

  private ConcurrencyUtil() {}

  public static ThreadFactory createThreadFactory(String threadName) {
    return new NumberedThreadFactory(threadName);
  }

  private static class NumberedThreadFactory implements ThreadFactory {
    private final Supplier<String> nameSupplier;
    private final AtomicLong counter = new AtomicLong(0);
    private final boolean daemon;

    NumberedThreadFactory(String specifier, boolean daemon) {
      this.nameSupplier = () -> "LunarMochiVox-%s-%s".formatted(specifier, counter.getAndIncrement());
      this.daemon = daemon;
    }

    NumberedThreadFactory(String specifier) {
      this(specifier, true);
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
      Thread thread = new Thread(r, nameSupplier.get());
      thread.setDaemon(daemon);
      return thread;
    }
  }
}
