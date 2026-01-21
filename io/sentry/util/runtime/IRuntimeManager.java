package io.sentry.util.runtime;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface IRuntimeManager {
  <T> T runWithRelaxedPolicy(@NotNull IRuntimeManagerCallback<T> paramIRuntimeManagerCallback);
  
  void runWithRelaxedPolicy(@NotNull Runnable paramRunnable);
  
  public static interface IRuntimeManagerCallback<T> {
    T run();
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentr\\util\runtime\IRuntimeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */