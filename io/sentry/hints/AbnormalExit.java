package io.sentry.hints;

import org.jetbrains.annotations.Nullable;

public interface AbnormalExit {
  @Nullable
  String mechanism();
  
  boolean ignoreCurrentThread();
  
  @Nullable
  Long timestamp();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\hints\AbnormalExit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */