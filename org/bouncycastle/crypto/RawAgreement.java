package org.bouncycastle.crypto;

public interface RawAgreement {
  void init(CipherParameters paramCipherParameters);
  
  int getAgreementSize();
  
  void calculateAgreement(CipherParameters paramCipherParameters, byte[] paramArrayOfbyte, int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\RawAgreement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */