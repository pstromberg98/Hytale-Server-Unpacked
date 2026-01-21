package io.netty.util.concurrent;

public interface ThreadProperties {
  Thread.State state();
  
  int priority();
  
  boolean isInterrupted();
  
  boolean isDaemon();
  
  String name();
  
  long id();
  
  StackTraceElement[] stackTrace();
  
  boolean isAlive();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\concurrent\ThreadProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */