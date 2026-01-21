package org.bouncycastle.crypto.threshold;

import java.io.IOException;

public interface SplitSecret {
  SecretShare[] getSecretShares();
  
  byte[] getSecret() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\threshold\SplitSecret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */