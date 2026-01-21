package org.bouncycastle.openssl;

public interface PEMEncryptor {
  String getAlgorithm();
  
  byte[] getIV();
  
  byte[] encrypt(byte[] paramArrayOfbyte) throws PEMException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\openssl\PEMEncryptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */