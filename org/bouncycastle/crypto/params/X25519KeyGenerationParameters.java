package org.bouncycastle.crypto.params;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class X25519KeyGenerationParameters extends KeyGenerationParameters {
  public X25519KeyGenerationParameters(SecureRandom paramSecureRandom) {
    super(paramSecureRandom, 255);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\params\X25519KeyGenerationParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */