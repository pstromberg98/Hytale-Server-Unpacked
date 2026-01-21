package com.hypixel.hytale.server.core.modules.projectile.config;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@FunctionalInterface
public interface ImpactConsumer {
  void onImpact(@Nonnull Ref<EntityStore> paramRef1, @Nonnull Vector3d paramVector3d, @Nullable Ref<EntityStore> paramRef2, @Nullable String paramString, @Nonnull CommandBuffer<EntityStore> paramCommandBuffer);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\projectile\config\ImpactConsumer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */