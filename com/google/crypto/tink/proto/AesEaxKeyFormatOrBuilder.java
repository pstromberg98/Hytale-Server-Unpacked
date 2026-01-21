package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesEaxKeyFormatOrBuilder extends MessageOrBuilder {
  boolean hasParams();
  
  AesEaxParams getParams();
  
  AesEaxParamsOrBuilder getParamsOrBuilder();
  
  int getKeySize();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\AesEaxKeyFormatOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */