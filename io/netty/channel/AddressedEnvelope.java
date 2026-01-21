package io.netty.channel;

import io.netty.util.ReferenceCounted;

public interface AddressedEnvelope<M, A extends java.net.SocketAddress> extends ReferenceCounted {
  M content();
  
  A sender();
  
  A recipient();
  
  AddressedEnvelope<M, A> retain();
  
  AddressedEnvelope<M, A> retain(int paramInt);
  
  AddressedEnvelope<M, A> touch();
  
  AddressedEnvelope<M, A> touch(Object paramObject);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\AddressedEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */