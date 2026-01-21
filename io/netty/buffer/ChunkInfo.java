package io.netty.buffer;

interface ChunkInfo {
  int capacity();
  
  boolean isDirect();
  
  long memoryAddress();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\buffer\ChunkInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */