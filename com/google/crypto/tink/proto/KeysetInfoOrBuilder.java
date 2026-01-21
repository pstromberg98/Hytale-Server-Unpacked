package com.google.crypto.tink.proto;

import com.google.protobuf.MessageOrBuilder;
import java.util.List;

public interface KeysetInfoOrBuilder extends MessageOrBuilder {
  int getPrimaryKeyId();
  
  List<KeysetInfo.KeyInfo> getKeyInfoList();
  
  KeysetInfo.KeyInfo getKeyInfo(int paramInt);
  
  int getKeyInfoCount();
  
  List<? extends KeysetInfo.KeyInfoOrBuilder> getKeyInfoOrBuilderList();
  
  KeysetInfo.KeyInfoOrBuilder getKeyInfoOrBuilder(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\KeysetInfoOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */