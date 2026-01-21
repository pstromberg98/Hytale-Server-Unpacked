package io.sentry.clientreport;

import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IClientReportStorage {
  void addCount(ClientReportKey paramClientReportKey, Long paramLong);
  
  List<DiscardedEvent> resetCountsAndGet();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\clientreport\IClientReportStorage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */