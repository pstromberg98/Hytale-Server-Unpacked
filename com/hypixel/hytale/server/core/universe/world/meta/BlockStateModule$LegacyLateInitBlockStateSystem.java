/*     */ package com.hypixel.hytale.server.core.universe.world.meta;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.DisableProcessingAssert;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.RootDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LegacyLateInitBlockStateSystem<T extends BlockState>
/*     */   extends EntityTickingSystem<ChunkStore>
/*     */   implements DisableProcessingAssert
/*     */ {
/* 288 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private final ComponentType<ChunkStore, T> componentType;
/*     */   @Nonnull
/*     */   private final Query<ChunkStore> query;
/*     */   
/*     */   public LegacyLateInitBlockStateSystem(ComponentType<ChunkStore, T> componentType) {
/* 295 */     this.componentType = componentType;
/* 296 */     this.query = (Query<ChunkStore>)Query.and(new Query[] { (Query)componentType, (Query)BlockModule.BlockStateInfo.getComponentType() });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<ChunkStore> getQuery() {
/* 302 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<ChunkStore>> getDependencies() {
/* 308 */     return RootDependency.firstSet();
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 313 */     BlockState blockState = (BlockState)archetypeChunk.getComponent(index, this.componentType);
/* 314 */     assert blockState != null;
/*     */     
/* 316 */     BlockModule.BlockStateInfo blockStateInfoComponent = (BlockModule.BlockStateInfo)archetypeChunk.getComponent(index, BlockModule.BlockStateInfo.getComponentType());
/* 317 */     assert blockStateInfoComponent != null;
/*     */     
/*     */     try {
/* 320 */       if (!blockState.initialized.get()) {
/* 321 */         blockState.initialized.set(true);
/*     */         
/* 323 */         if (blockState.getReference() == null || !blockState.getReference().isValid()) {
/* 324 */           blockState.setReference(archetypeChunk.getReferenceTo(index));
/*     */         }
/*     */         
/* 327 */         World world = ((ChunkStore)store.getExternalData()).getWorld();
/* 328 */         Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*     */         
/* 330 */         WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(blockStateInfoComponent.getChunkRef(), WorldChunk.getComponentType());
/* 331 */         assert worldChunkComponent != null;
/*     */         
/* 333 */         int x = ChunkUtil.xFromBlockInColumn(blockStateInfoComponent.getIndex());
/* 334 */         int y = ChunkUtil.yFromBlockInColumn(blockStateInfoComponent.getIndex());
/* 335 */         int z = ChunkUtil.zFromBlockInColumn(blockStateInfoComponent.getIndex());
/*     */         
/* 337 */         blockState.setPosition(worldChunkComponent, new Vector3i(x, y, z));
/*     */         
/* 339 */         int blockIndex = worldChunkComponent.getBlock(x, y, z);
/* 340 */         BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockIndex);
/* 341 */         if (!blockState.initialize(blockType)) {
/* 342 */           LOGGER.at(Level.SEVERE).log("Removing invalid block state %s", blockState);
/* 343 */           commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */         } 
/*     */       } 
/* 346 */     } catch (Exception throwable) {
/* 347 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Exception while re-init BlockState! Removing!! %s", blockState);
/* 348 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 355 */     return "LegacyLateInitBlockStateSystem{componentType=" + String.valueOf(this.componentType) + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\meta\BlockStateModule$LegacyLateInitBlockStateSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */