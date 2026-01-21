package org.bson.codecs.pojo;

import org.bson.codecs.Codec;

public interface PropertyCodecRegistry {
  <T> Codec<T> get(TypeWithTypeParameters<T> paramTypeWithTypeParameters);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\PropertyCodecRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */