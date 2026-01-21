package io.netty.channel;

public interface IoHandler {
  default void initialize() {}
  
  int run(IoHandlerContext paramIoHandlerContext);
  
  default void prepareToDestroy() {}
  
  default void destroy() {}
  
  IoRegistration register(IoHandle paramIoHandle) throws Exception;
  
  void wakeup();
  
  boolean isCompatible(Class<? extends IoHandle> paramClass);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\IoHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */