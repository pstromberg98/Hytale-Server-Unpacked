package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FrodoKeyGenerationParameters extends KeyGenerationParameters {
  private FrodoParameters params;
  
  public FrodoKeyGenerationParameters(SecureRandom paramSecureRandom, FrodoParameters paramFrodoParameters) {
    super(paramSecureRandom, 256);
    this.params = paramFrodoParameters;
  }
  
  public FrodoParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\frodo\FrodoKeyGenerationParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */