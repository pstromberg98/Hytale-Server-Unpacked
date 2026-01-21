package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface HmacKeyFormatOrBuilder extends MessageOrBuilder {
  boolean hasParams();
  
  HmacParams getParams();
  
  HmacParamsOrBuilder getParamsOrBuilder();
  
  int getKeySize();
  
  int getVersion();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\HmacKeyFormatOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */