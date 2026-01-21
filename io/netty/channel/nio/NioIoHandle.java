package io.netty.channel.nio;

import io.netty.channel.IoHandle;
import java.nio.channels.SelectableChannel;

public interface NioIoHandle extends IoHandle {
  SelectableChannel selectableChannel();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\nio\NioIoHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */