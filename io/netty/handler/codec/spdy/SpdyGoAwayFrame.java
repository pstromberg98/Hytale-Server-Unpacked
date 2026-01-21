package io.netty.handler.codec.spdy;

public interface SpdyGoAwayFrame extends SpdyFrame {
  int lastGoodStreamId();
  
  SpdyGoAwayFrame setLastGoodStreamId(int paramInt);
  
  SpdySessionStatus status();
  
  SpdyGoAwayFrame setStatus(SpdySessionStatus paramSpdySessionStatus);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\spdy\SpdyGoAwayFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */