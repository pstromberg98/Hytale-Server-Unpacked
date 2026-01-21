package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface RsaSsaPssParamsOrBuilder extends MessageOrBuilder {
  int getSigHashValue();
  
  HashType getSigHash();
  
  int getMgf1HashValue();
  
  HashType getMgf1Hash();
  
  int getSaltLength();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\RsaSsaPssParamsOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */