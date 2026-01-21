package io.netty.util.concurrent;

import java.util.concurrent.Executor;

public interface ThreadAwareExecutor extends Executor {
  boolean isExecutorThread(Thread paramThread);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\concurrent\ThreadAwareExecutor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */