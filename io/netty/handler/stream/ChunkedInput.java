package io.netty.handler.stream;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

public interface ChunkedInput<B> {
  boolean isEndOfInput() throws Exception;
  
  void close() throws Exception;
  
  @Deprecated
  B readChunk(ChannelHandlerContext paramChannelHandlerContext) throws Exception;
  
  B readChunk(ByteBufAllocator paramByteBufAllocator) throws Exception;
  
  long length();
  
  long progress();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\stream\ChunkedInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */