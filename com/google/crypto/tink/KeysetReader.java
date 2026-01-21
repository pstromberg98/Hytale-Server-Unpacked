package com.google.crypto.tink;

import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.Keyset;
import java.io.IOException;

public interface KeysetReader {
  Keyset read() throws IOException;
  
  EncryptedKeyset readEncrypted() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\KeysetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */