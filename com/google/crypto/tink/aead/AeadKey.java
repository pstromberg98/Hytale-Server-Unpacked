package com.google.crypto.tink.aead;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.util.Bytes;

public abstract class AeadKey extends Key {
  public abstract Bytes getOutputPrefix();
  
  public abstract AeadParameters getParameters();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\aead\AeadKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */