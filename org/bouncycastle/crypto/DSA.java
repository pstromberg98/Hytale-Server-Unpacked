package org.bouncycastle.crypto;

import java.math.BigInteger;

public interface DSA {
  void init(boolean paramBoolean, CipherParameters paramCipherParameters);
  
  BigInteger[] generateSignature(byte[] paramArrayOfbyte);
  
  boolean verifySignature(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\DSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */