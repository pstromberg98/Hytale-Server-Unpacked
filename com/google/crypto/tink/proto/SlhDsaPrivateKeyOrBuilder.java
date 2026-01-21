package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaPrivateKeyOrBuilder extends MessageOrBuilder {
  int getVersion();
  
  ByteString getKeyValue();
  
  boolean hasPublicKey();
  
  SlhDsaPublicKey getPublicKey();
  
  SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\SlhDsaPrivateKeyOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */