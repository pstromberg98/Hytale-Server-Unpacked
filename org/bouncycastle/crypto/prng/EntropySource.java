package org.bouncycastle.crypto.prng;

public interface EntropySource {
  boolean isPredictionResistant();
  
  byte[] getEntropy();
  
  int entropySize();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\prng\EntropySource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */