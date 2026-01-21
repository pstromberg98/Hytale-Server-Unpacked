package io.sentry.profiling;

import io.sentry.IProfileConverter;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

@Internal
public interface JavaProfileConverterProvider {
  @NotNull
  IProfileConverter getProfileConverter();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\profiling\JavaProfileConverterProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */