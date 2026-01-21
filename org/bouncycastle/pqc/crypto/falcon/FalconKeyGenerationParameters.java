package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class FalconKeyGenerationParameters extends KeyGenerationParameters {
  private final FalconParameters params;
  
  public FalconKeyGenerationParameters(SecureRandom paramSecureRandom, FalconParameters paramFalconParameters) {
    super(paramSecureRandom, 320);
    this.params = paramFalconParameters;
  }
  
  public FalconParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\falcon\FalconKeyGenerationParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */