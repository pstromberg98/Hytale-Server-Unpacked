package io.netty.util.internal;

public interface LongCounter {
  void add(long paramLong);
  
  void increment();
  
  void decrement();
  
  long value();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\internal\LongCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */