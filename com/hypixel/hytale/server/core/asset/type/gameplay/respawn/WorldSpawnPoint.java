/*    */ package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldSpawnPoint
/*    */   implements RespawnController
/*    */ {
/* 20 */   public static final WorldSpawnPoint INSTANCE = new WorldSpawnPoint();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 26 */   public static final BuilderCodec<WorldSpawnPoint> CODEC = BuilderCodec.builder(WorldSpawnPoint.class, () -> INSTANCE).build();
/*    */ 
/*    */   
/*    */   public void respawnPlayer(@Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 30 */     ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
/* 31 */     assert spawnProvider != null;
/*    */     
/* 33 */     Transform spawnPoint = spawnProvider.getSpawnPoint(playerReference, (ComponentAccessor)commandBuffer);
/*    */     
/* 35 */     Teleport teleportComponent = Teleport.createForPlayer(spawnPoint);
/* 36 */     commandBuffer.addComponent(playerReference, Teleport.getComponentType(), (Component)teleportComponent);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\gameplay\respawn\WorldSpawnPoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */