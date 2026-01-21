package io.sentry.logger;

import io.sentry.SentryLogEvent;
import org.jetbrains.annotations.NotNull;

public interface ILoggerBatchProcessor {
  void add(@NotNull SentryLogEvent paramSentryLogEvent);
  
  void close(boolean paramBoolean);
  
  void flush(long paramLong);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\logger\ILoggerBatchProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */