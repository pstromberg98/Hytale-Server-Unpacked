/*    */ package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldSpawnPoint
/*    */   implements RespawnController
/*    */ {
/* 21 */   public static final WorldSpawnPoint INSTANCE = new WorldSpawnPoint();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 27 */   public static final BuilderCodec<WorldSpawnPoint> CODEC = BuilderCodec.builder(WorldSpawnPoint.class, () -> INSTANCE).build();
/*    */ 
/*    */   
/*    */   public CompletableFuture<Void> respawnPlayer(@Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull ComponentAccessor<EntityStore> commandBuffer) {
/* 31 */     ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
/* 32 */     assert spawnProvider != null;
/*    */     
/* 34 */     Transform spawnPoint = spawnProvider.getSpawnPoint(playerReference, commandBuffer);
/*    */     
/* 36 */     Teleport teleportComponent = Teleport.createForPlayer(spawnPoint);
/* 37 */     commandBuffer.addComponent(playerReference, Teleport.getComponentType(), (Component)teleportComponent);
/* 38 */     return CompletableFuture.completedFuture(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\gameplay\respawn\WorldSpawnPoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */