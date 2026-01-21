package org.bson.codecs.pojo;

public interface IdGenerator<T> {
  T generate();
  
  Class<T> getType();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\IdGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */