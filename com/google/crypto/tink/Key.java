package com.google.crypto.tink;

import com.google.errorprone.annotations.Immutable;
import javax.annotation.Nullable;

@Immutable
public abstract class Key {
  public abstract Parameters getParameters();
  
  @Nullable
  public abstract Integer getIdRequirementOrNull();
  
  public abstract boolean equalsKey(Key paramKey);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */