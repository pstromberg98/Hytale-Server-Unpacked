package com.hypixel.hytale.protocol;

import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;

public interface Packet {
  int getId();
  
  void serialize(@Nonnull ByteBuf paramByteBuf);
  
  int computeSize();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\Packet.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */