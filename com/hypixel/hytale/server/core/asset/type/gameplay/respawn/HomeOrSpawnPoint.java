/*    */ package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.concurrent.Executor;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HomeOrSpawnPoint
/*    */   implements RespawnController
/*    */ {
/*    */   @Nonnull
/* 23 */   public static final HomeOrSpawnPoint INSTANCE = new HomeOrSpawnPoint();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 29 */   public static final BuilderCodec<HomeOrSpawnPoint> CODEC = BuilderCodec.builder(HomeOrSpawnPoint.class, () -> INSTANCE)
/* 30 */     .build();
/*    */ 
/*    */   
/*    */   public CompletableFuture<Void> respawnPlayer(@Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull ComponentAccessor<EntityStore> commandBuffer) {
/* 34 */     return Player.getRespawnPosition(playerReference, world.getName(), commandBuffer).thenAcceptAsync(homeTransform -> { if (!playerReference.isValid()) return;  Teleport teleportComponent = Teleport.createForPlayer(homeTransform); playerReference.getStore().addComponent(playerReference, Teleport.getComponentType(), (Component)teleportComponent); }(Executor)world);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\gameplay\respawn\HomeOrSpawnPoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */