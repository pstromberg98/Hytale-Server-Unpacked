package com.google.crypto.tink.jwt;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import java.util.Optional;

public abstract class JwtMacKey extends Key {
  public abstract Optional<String> getKid();
  
  public abstract JwtMacParameters getParameters();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\jwt\JwtMacKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */