package io.netty.channel.socket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;

public interface DuplexChannel extends Channel {
  boolean isInputShutdown();
  
  ChannelFuture shutdownInput();
  
  ChannelFuture shutdownInput(ChannelPromise paramChannelPromise);
  
  boolean isOutputShutdown();
  
  ChannelFuture shutdownOutput();
  
  ChannelFuture shutdownOutput(ChannelPromise paramChannelPromise);
  
  boolean isShutdown();
  
  ChannelFuture shutdown();
  
  ChannelFuture shutdown(ChannelPromise paramChannelPromise);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\socket\DuplexChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */