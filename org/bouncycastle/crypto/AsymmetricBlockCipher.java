package org.bouncycastle.crypto;

public interface AsymmetricBlockCipher {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  int getInputBlockSize();
  
  int getOutputBlockSize();
  
  byte[] processBlock(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws InvalidCipherTextException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\AsymmetricBlockCipher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */