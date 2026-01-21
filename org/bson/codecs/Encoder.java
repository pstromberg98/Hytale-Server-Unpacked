package org.bson.codecs;

import org.bson.BsonWriter;

public interface Encoder<T> {
  void encode(BsonWriter paramBsonWriter, T paramT, EncoderContext paramEncoderContext);
  
  Class<T> getEncoderClass();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */