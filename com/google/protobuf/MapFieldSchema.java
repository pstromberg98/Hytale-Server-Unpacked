package com.google.protobuf;

import java.util.Map;

@CheckReturnValue
interface MapFieldSchema {
  Map<?, ?> forMutableMapData(Object paramObject);
  
  Map<?, ?> forMapData(Object paramObject);
  
  boolean isImmutable(Object paramObject);
  
  Object toImmutable(Object paramObject);
  
  Object newMapField(Object paramObject);
  
  MapEntryLite.Metadata<?, ?> forMapMetadata(Object paramObject);
  
  @CanIgnoreReturnValue
  Object mergeFrom(Object paramObject1, Object paramObject2);
  
  int getSerializedSize(int paramInt, Object paramObject1, Object paramObject2);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\protobuf\MapFieldSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */