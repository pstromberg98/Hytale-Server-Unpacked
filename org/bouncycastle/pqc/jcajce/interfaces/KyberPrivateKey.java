package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;

public interface KyberPrivateKey extends PrivateKey, KyberKey {
  KyberPublicKey getPublicKey();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\jcajce\interfaces\KyberPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */