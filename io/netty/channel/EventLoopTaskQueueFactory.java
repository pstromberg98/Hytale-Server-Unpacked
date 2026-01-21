package io.netty.channel;

import java.util.Queue;

@Deprecated
public interface EventLoopTaskQueueFactory {
  Queue<Runnable> newTaskQueue(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\channel\EventLoopTaskQueueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */