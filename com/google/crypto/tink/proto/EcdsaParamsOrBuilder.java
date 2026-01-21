package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface EcdsaParamsOrBuilder extends MessageOrBuilder {
  int getHashTypeValue();
  
  HashType getHashType();
  
  int getCurveValue();
  
  EllipticCurveType getCurve();
  
  int getEncodingValue();
  
  EcdsaSignatureEncoding getEncoding();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\EcdsaParamsOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */