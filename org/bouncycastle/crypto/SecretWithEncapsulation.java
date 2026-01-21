package org.bouncycastle.crypto;

import javax.security.auth.Destroyable;

public interface SecretWithEncapsulation extends Destroyable {
  byte[] getSecret();
  
  byte[] getEncapsulation();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\SecretWithEncapsulation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */