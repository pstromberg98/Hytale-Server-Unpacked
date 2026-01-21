package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import java.net.InetSocketAddress;

public interface QuicTokenHandler {
  boolean writeToken(ByteBuf paramByteBuf1, ByteBuf paramByteBuf2, InetSocketAddress paramInetSocketAddress);
  
  int validateToken(ByteBuf paramByteBuf, InetSocketAddress paramInetSocketAddress);
  
  int maxTokenLength();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\QuicTokenHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */