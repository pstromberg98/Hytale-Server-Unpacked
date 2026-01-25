package com.hypixel.hytale.server.core.entity.entities.player.windows;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public interface ValidatedWindow {
  boolean validate(@Nonnull Ref<EntityStore> paramRef, @Nonnull ComponentAccessor<EntityStore> paramComponentAccessor);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\windows\ValidatedWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */