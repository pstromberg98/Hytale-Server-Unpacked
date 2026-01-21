package com.google.protobuf;

import java.util.List;

public abstract class MapFieldReflectionAccessor {
  abstract List<Message> getList();
  
  abstract List<Message> getMutableList();
  
  abstract Message getMapEntryMessageDefaultInstance();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\protobuf\MapFieldReflectionAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */