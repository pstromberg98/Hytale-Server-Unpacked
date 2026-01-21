package com.hypixel.fastutil.ints;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public interface Entry<V> extends Int2ObjectMap.Entry<V> {
  boolean isEmpty();
  
  @Deprecated
  Integer getKey();
  
  int getIntKey();
  
  V getValue();
  
  int hashCode();
  
  String toString();
  
  boolean equals(Object paramObject);
  
  V setValue(V paramV);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\fastutil\ints\Int2ObjectConcurrentHashMap$Entry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */