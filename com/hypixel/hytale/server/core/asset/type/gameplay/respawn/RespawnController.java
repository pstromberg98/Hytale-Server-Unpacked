/*    */ package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.lookup.CodecMapCodec;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.concurrent.CompletableFuture;
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
/*    */ 
/*    */ public interface RespawnController
/*    */ {
/*    */   @Nonnull
/* 23 */   public static final CodecMapCodec<RespawnController> CODEC = new CodecMapCodec("Type");
/*    */   
/*    */   CompletableFuture<Void> respawnPlayer(@Nonnull World paramWorld, @Nonnull Ref<EntityStore> paramRef, @Nonnull ComponentAccessor<EntityStore> paramComponentAccessor);
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\gameplay\respawn\RespawnController.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */