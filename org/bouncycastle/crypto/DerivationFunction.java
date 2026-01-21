package org.bouncycastle.crypto;

public interface DerivationFunction {
  void init(DerivationParameters paramDerivationParameters);
  
  int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\DerivationFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */