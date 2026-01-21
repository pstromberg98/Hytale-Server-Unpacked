package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface PrfBasedDeriverKeyFormatOrBuilder extends MessageOrBuilder {
  boolean hasPrfKeyTemplate();
  
  KeyTemplate getPrfKeyTemplate();
  
  KeyTemplateOrBuilder getPrfKeyTemplateOrBuilder();
  
  boolean hasParams();
  
  PrfBasedDeriverParams getParams();
  
  PrfBasedDeriverParamsOrBuilder getParamsOrBuilder();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\PrfBasedDeriverKeyFormatOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */