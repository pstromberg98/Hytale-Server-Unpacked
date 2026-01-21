package org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.bouncycastle.crypto.InvalidCipherTextException;

public interface BlockCipherPadding {
  void init(SecureRandom paramSecureRandom) throws IllegalArgumentException;
  
  String getPaddingName();
  
  int addPadding(byte[] paramArrayOfbyte, int paramInt);
  
  int padCount(byte[] paramArrayOfbyte) throws InvalidCipherTextException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\paddings\BlockCipherPadding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */