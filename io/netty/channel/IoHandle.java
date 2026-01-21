package io.netty.channel;

public interface IoHandle extends AutoCloseable {
  void handle(IoRegistration paramIoRegistration, IoEvent paramIoEvent);
  
  default void registered() {}
  
  default void unregistered() {}
  
  void close() throws Exception;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\IoHandle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */