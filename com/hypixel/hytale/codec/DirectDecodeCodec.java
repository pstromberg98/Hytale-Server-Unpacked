package com.hypixel.hytale.codec;

import org.bson.BsonValue;

public interface DirectDecodeCodec<T> extends Codec<T> {
  void decode(BsonValue paramBsonValue, T paramT, ExtraInfo paramExtraInfo);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\codec\DirectDecodeCodec.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */