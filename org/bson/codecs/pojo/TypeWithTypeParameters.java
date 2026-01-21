package org.bson.codecs.pojo;

import java.util.List;

public interface TypeWithTypeParameters<T> {
  Class<T> getType();
  
  List<? extends TypeWithTypeParameters<?>> getTypeParameters();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\TypeWithTypeParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */