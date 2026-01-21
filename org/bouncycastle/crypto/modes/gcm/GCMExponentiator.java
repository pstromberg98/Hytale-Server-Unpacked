package org.bouncycastle.crypto.modes.gcm;

public interface GCMExponentiator {
  void init(byte[] paramArrayOfbyte);
  
  void exponentiateX(long paramLong, byte[] paramArrayOfbyte);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\modes\gcm\GCMExponentiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */