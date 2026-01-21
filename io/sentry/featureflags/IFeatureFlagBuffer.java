package io.sentry.featureflags;

import io.sentry.protocol.FeatureFlags;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Internal
public interface IFeatureFlagBuffer {
  void add(@Nullable String paramString, @Nullable Boolean paramBoolean);
  
  @Nullable
  FeatureFlags getFeatureFlags();
  
  @NotNull
  IFeatureFlagBuffer clone();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\featureflags\IFeatureFlagBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */