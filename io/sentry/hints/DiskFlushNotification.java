package io.sentry.hints;

import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DiskFlushNotification {
  void markFlushed();
  
  boolean isFlushable(@Nullable SentryId paramSentryId);
  
  void setFlushable(@NotNull SentryId paramSentryId);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\hints\DiskFlushNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */