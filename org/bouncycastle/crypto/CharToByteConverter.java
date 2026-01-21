package org.bouncycastle.crypto;

public interface CharToByteConverter {
  String getType();
  
  byte[] convert(char[] paramArrayOfchar);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\CharToByteConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */