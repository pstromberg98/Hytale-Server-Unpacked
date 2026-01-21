package com.hypixel.fastutil.longs;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public interface Entry<V> extends Long2ObjectMap.Entry<V> {
  boolean isEmpty();
  
  @Deprecated
  Long getKey();
  
  long getLongKey();
  
  V getValue();
  
  int hashCode();
  
  String toString();
  
  boolean equals(Object paramObject);
  
  V setValue(V paramV);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\fastutil\longs\Long2ObjectConcurrentHashMap$Entry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */