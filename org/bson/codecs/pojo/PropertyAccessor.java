package org.bson.codecs.pojo;

public interface PropertyAccessor<T> {
  <S> T get(S paramS);
  
  <S> void set(S paramS, T paramT);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\PropertyAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */