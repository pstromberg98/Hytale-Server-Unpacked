package com.google.crypto.tink.proto;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import java.util.List;

@Deprecated
public interface RegistryConfigOrBuilder extends MessageOrBuilder {
  String getConfigName();
  
  ByteString getConfigNameBytes();
  
  List<KeyTypeEntry> getEntryList();
  
  KeyTypeEntry getEntry(int paramInt);
  
  int getEntryCount();
  
  List<? extends KeyTypeEntryOrBuilder> getEntryOrBuilderList();
  
  KeyTypeEntryOrBuilder getEntryOrBuilder(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\proto\RegistryConfigOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */