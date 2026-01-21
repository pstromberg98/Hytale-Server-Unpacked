package com.google.crypto.tink;

import java.security.GeneralSecurityException;

public interface DeterministicAead {
  byte[] encryptDeterministically(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws GeneralSecurityException;
  
  byte[] decryptDeterministically(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws GeneralSecurityException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\DeterministicAead.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */