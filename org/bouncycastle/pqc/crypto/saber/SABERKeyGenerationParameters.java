package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SABERKeyGenerationParameters extends KeyGenerationParameters {
  private SABERParameters params;
  
  public SABERKeyGenerationParameters(SecureRandom paramSecureRandom, SABERParameters paramSABERParameters) {
    super(paramSecureRandom, 256);
    this.params = paramSABERParameters;
  }
  
  public SABERParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\saber\SABERKeyGenerationParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */