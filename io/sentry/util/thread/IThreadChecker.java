package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface IThreadChecker {
  boolean isMainThread(long paramLong);
  
  boolean isMainThread(@NotNull Thread paramThread);
  
  boolean isMainThread();
  
  boolean isMainThread(@NotNull SentryThread paramSentryThread);
  
  @NotNull
  String getCurrentThreadName();
  
  long currentThreadSystemId();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentr\\util\thread\IThreadChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */