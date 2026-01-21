package io.netty.channel.epoll;

import io.netty.channel.IoEvent;

public interface EpollIoEvent extends IoEvent {
  EpollIoOps ops();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\epoll\EpollIoEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */