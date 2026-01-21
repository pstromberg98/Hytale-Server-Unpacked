package org.bouncycastle.crypto;

public interface SignerWithRecovery extends Signer {
  boolean hasFullMessage();
  
  byte[] getRecoveredMessage();
  
  void updateWithRecoveredMessage(byte[] paramArrayOfbyte) throws InvalidCipherTextException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\SignerWithRecovery.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */