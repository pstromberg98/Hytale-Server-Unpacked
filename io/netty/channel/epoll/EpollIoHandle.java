package io.netty.channel.epoll;

import io.netty.channel.IoHandle;
import io.netty.channel.unix.FileDescriptor;

public interface EpollIoHandle extends IoHandle {
  FileDescriptor fd();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\epoll\EpollIoHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */