package com.google.crypto.tink.keyderivation;

import com.google.crypto.tink.KeysetHandle;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface KeysetDeriver {
  KeysetHandle deriveKeyset(byte[] paramArrayOfbyte) throws GeneralSecurityException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\keyderivation\KeysetDeriver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */