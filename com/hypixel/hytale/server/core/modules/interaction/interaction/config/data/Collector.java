package com.hypixel.hytale.server.core.modules.interaction.interaction.config.data;

import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Collector {
  void start();
  
  void into(@Nonnull InteractionContext paramInteractionContext, @Nullable Interaction paramInteraction);
  
  boolean collect(@Nonnull CollectorTag paramCollectorTag, @Nonnull InteractionContext paramInteractionContext, @Nonnull Interaction paramInteraction);
  
  void outof();
  
  void finished();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\data\Collector.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */