package io.netty.handler.codec.quic;

public interface QuicConnectionStats {
  long recv();
  
  long sent();
  
  long lost();
  
  long retrans();
  
  long sentBytes();
  
  long recvBytes();
  
  long lostBytes();
  
  long streamRetransBytes();
  
  long pathsCount();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\QuicConnectionStats.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */