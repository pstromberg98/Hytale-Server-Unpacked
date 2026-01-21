package io.netty.channel.pool;

public interface ChannelPoolMap<K, P extends ChannelPool> {
  P get(K paramK);
  
  boolean contains(K paramK);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\pool\ChannelPoolMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */