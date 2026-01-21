package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface JwtHmacKeyFormatOrBuilder extends MessageOrBuilder {
  int getVersion();
  
  int getAlgorithmValue();
  
  JwtHmacAlgorithm getAlgorithm();
  
  int getKeySize();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\JwtHmacKeyFormatOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */