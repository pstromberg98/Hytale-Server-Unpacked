package org.bouncycastle.crypto.modes.kgcm;

public interface KGCMMultiplier {
  void init(long[] paramArrayOflong);
  
  void multiplyH(long[] paramArrayOflong);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\modes\kgcm\KGCMMultiplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */