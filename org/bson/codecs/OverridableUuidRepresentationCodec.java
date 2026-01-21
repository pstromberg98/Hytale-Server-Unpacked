package org.bson.codecs;

import org.bson.UuidRepresentation;

public interface OverridableUuidRepresentationCodec<T> {
  Codec<T> withUuidRepresentation(UuidRepresentation paramUuidRepresentation);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\OverridableUuidRepresentationCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */