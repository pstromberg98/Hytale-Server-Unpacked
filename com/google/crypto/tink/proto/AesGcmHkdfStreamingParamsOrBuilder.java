package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;

public interface AesGcmHkdfStreamingParamsOrBuilder extends MessageOrBuilder {
  int getCiphertextSegmentSize();
  
  int getDerivedKeySize();
  
  int getHkdfHashTypeValue();
  
  HashType getHkdfHashType();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\AesGcmHkdfStreamingParamsOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */