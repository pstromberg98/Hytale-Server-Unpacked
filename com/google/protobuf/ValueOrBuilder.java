package com.google.protobuf;

public interface ValueOrBuilder extends MessageOrBuilder {
  boolean hasNullValue();
  
  int getNullValueValue();
  
  NullValue getNullValue();
  
  boolean hasNumberValue();
  
  double getNumberValue();
  
  boolean hasStringValue();
  
  String getStringValue();
  
  ByteString getStringValueBytes();
  
  boolean hasBoolValue();
  
  boolean getBoolValue();
  
  boolean hasStructValue();
  
  Struct getStructValue();
  
  StructOrBuilder getStructValueOrBuilder();
  
  boolean hasListValue();
  
  ListValue getListValue();
  
  ListValueOrBuilder getListValueOrBuilder();
  
  Value.KindCase getKindCase();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\protobuf\ValueOrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */