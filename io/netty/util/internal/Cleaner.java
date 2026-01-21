package io.netty.util.internal;

import java.nio.ByteBuffer;

interface Cleaner {
  CleanableDirectBuffer allocate(int paramInt);
  
  @Deprecated
  void freeDirectBuffer(ByteBuffer paramByteBuffer);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\internal\Cleaner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */