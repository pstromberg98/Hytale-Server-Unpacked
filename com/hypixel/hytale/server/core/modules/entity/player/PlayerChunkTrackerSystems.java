/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.HolderSystem;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlayerChunkTrackerSystems
/*     */ {
/*     */   public static class AddSystem
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  28 */     private static final ComponentType<EntityStore, ChunkTracker> CHUNK_TRACKER_COMPONENT_TYPE = ChunkTracker.getComponentType();
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/*  32 */       return (Query)CHUNK_TRACKER_COMPONENT_TYPE;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*  37 */       ChunkTracker chunkTrackerComponent = (ChunkTracker)holder.getComponent(CHUNK_TRACKER_COMPONENT_TYPE);
/*  38 */       assert chunkTrackerComponent != null;
/*     */       
/*  40 */       chunkTrackerComponent.setReadyForChunks(true);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class UpdateSystem
/*     */     extends EntityTickingSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  57 */     private static final ComponentType<EntityStore, ChunkTracker> CHUNK_TRACKER_COMPONENT_TYPE = ChunkTracker.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  63 */     private static final ComponentType<EntityStore, Player> PLAYER_COMPONENT_TYPE = Player.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  69 */     private static final ComponentType<EntityStore, PlayerRef> PLAYER_REF_COMPONENT_TYPE = PlayerRef.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  75 */     private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/*  79 */       return (Query<EntityStore>)Query.and(new Query[] { (Query)CHUNK_TRACKER_COMPONENT_TYPE, (Query)PLAYER_COMPONENT_TYPE, (Query)PLAYER_REF_COMPONENT_TYPE, (Query)TRANSFORM_COMPONENT_TYPE });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  89 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  94 */       ChunkTracker chunkTrackerComponent = (ChunkTracker)archetypeChunk.getComponent(index, CHUNK_TRACKER_COMPONENT_TYPE);
/*  95 */       assert chunkTrackerComponent != null;
/*     */       
/*  97 */       Player playerComponent = (Player)archetypeChunk.getComponent(index, PLAYER_COMPONENT_TYPE);
/*  98 */       assert playerComponent != null;
/*     */       
/* 100 */       PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, PLAYER_REF_COMPONENT_TYPE);
/* 101 */       assert playerRefComponent != null;
/*     */       
/* 103 */       TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
/* 104 */       assert transformComponent != null;
/*     */       
/* 106 */       chunkTrackerComponent.tick(playerComponent, playerRefComponent, transformComponent, dt, commandBuffer);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerChunkTrackerSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */