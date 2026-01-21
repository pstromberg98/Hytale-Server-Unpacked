package io.netty.channel.unix;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import java.net.SocketAddress;

public interface DomainDatagramChannel extends UnixChannel, Channel {
  DomainDatagramChannelConfig config();
  
  boolean isConnected();
  
  DomainSocketAddress localAddress();
  
  DomainSocketAddress remoteAddress();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channe\\unix\DomainDatagramChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */