package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface SlhDsaParamsOrBuilder extends MessageOrBuilder {
  int getKeySize();
  
  int getHashTypeValue();
  
  SlhDsaHashType getHashType();
  
  int getSigTypeValue();
  
  SlhDsaSignatureType getSigType();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\SlhDsaParamsOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */