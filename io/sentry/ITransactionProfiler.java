package io.sentry;

import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public interface ITransactionProfiler {
  boolean isRunning();
  
  void start();
  
  void bindTransaction(@NotNull ITransaction paramITransaction);
  
  @Nullable
  ProfilingTraceData onTransactionFinish(@NotNull ITransaction paramITransaction, @Nullable List<PerformanceCollectionData> paramList, @NotNull SentryOptions paramSentryOptions);
  
  void close();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\ITransactionProfiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */