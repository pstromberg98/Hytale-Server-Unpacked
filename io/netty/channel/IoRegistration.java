package io.netty.channel;

public interface IoRegistration {
  <T> T attachment();
  
  long submit(IoOps paramIoOps);
  
  boolean isValid();
  
  boolean cancel();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\IoRegistration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */