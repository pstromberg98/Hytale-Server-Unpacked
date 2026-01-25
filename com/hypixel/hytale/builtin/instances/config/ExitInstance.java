/*    */ package com.hypixel.hytale.builtin.instances.config;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.instances.InstancesPlugin;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.logger.HytaleLogger;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.HomeOrSpawnPoint;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import java.util.function.Supplier;
/*    */ import java.util.logging.Level;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExitInstance
/*    */   implements RespawnController
/*    */ {
/*    */   @Nonnull
/*    */   public static final BuilderCodec<ExitInstance> CODEC;
/*    */   
/*    */   static {
/* 36 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(ExitInstance.class, ExitInstance::new).append(new KeyedCodec("Fallback", (Codec)RespawnController.CODEC), (o, i) -> o.fallback = i, o -> o.fallback).addValidator(Validators.nonNull()).add()).build();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/* 41 */   private RespawnController fallback = (RespawnController)HomeOrSpawnPoint.INSTANCE;
/*    */ 
/*    */ 
/*    */   
/*    */   public CompletableFuture<Void> respawnPlayer(@Nonnull World world, @Nonnull Ref<EntityStore> playerReference, @Nonnull ComponentAccessor<EntityStore> commandBuffer) {
/*    */     try {
/* 47 */       InstancesPlugin.exitInstance(playerReference, commandBuffer);
/* 48 */       return CompletableFuture.completedFuture(null);
/* 49 */     } catch (Exception e) {
/* 50 */       PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(playerReference, PlayerRef.getComponentType());
/* 51 */       assert playerRefComponent != null;
/* 52 */       ((HytaleLogger.Api)InstancesPlugin.get().getLogger().at(Level.WARNING).withCause(e)).log(playerRefComponent.getUsername() + " failed to leave an instance");
/* 53 */       return this.fallback.respawnPlayer(world, playerReference, commandBuffer);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\instances\config\ExitInstance.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */