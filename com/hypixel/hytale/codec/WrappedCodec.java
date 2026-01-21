package com.hypixel.hytale.codec;

public interface WrappedCodec<T> {
  Codec<T> getChildCodec();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\codec\WrappedCodec.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */