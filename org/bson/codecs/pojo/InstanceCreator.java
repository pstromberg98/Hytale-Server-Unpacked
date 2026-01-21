package org.bson.codecs.pojo;

public interface InstanceCreator<T> {
  <S> void set(S paramS, PropertyModel<S> paramPropertyModel);
  
  T getInstance();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\InstanceCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */