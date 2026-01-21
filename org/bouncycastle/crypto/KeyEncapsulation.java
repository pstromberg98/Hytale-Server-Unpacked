package org.bouncycastle.crypto;

public interface KeyEncapsulation {
  void init(CipherParameters paramCipherParameters);
  
  CipherParameters encrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  CipherParameters decrypt(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\KeyEncapsulation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */