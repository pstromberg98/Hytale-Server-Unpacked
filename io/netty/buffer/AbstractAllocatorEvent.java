package io.netty.buffer;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;

@Enabled(false)
@Category({"Netty"})
abstract class AbstractAllocatorEvent extends Event {
  @Label("Allocator type")
  @Description("The type of allocator this event is for")
  public Class<? extends AbstractByteBufAllocator> allocatorType;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\buffer\AbstractAllocatorEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */