package org.bson.codecs.pojo;

import org.bson.codecs.Codec;

public interface PropertyCodecProvider {
  <T> Codec<T> get(TypeWithTypeParameters<T> paramTypeWithTypeParameters, PropertyCodecRegistry paramPropertyCodecRegistry);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\pojo\PropertyCodecProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */