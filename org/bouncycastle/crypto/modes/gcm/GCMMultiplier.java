package org.bouncycastle.crypto.modes.gcm;

public interface GCMMultiplier {
  void init(byte[] paramArrayOfbyte);
  
  void multiplyH(byte[] paramArrayOfbyte);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\modes\gcm\GCMMultiplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */