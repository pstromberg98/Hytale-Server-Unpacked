/*     */ package com.hypixel.hytale.builtin.adventure.farming;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.TargetUtil;
/*     */ import java.time.Instant;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class Ticking
/*     */   extends EntityTickingSystem<ChunkStore>
/*     */ {
/* 193 */   private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] { (Query)BlockSection.getComponentType(), (Query)ChunkSection.getComponentType() });
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 197 */     BlockSection blocks = (BlockSection)archetypeChunk.getComponent(index, BlockSection.getComponentType());
/* 198 */     assert blocks != null;
/*     */     
/* 200 */     if (blocks.getTickingBlocksCountCopy() == 0)
/*     */       return; 
/* 202 */     ChunkSection section = (ChunkSection)archetypeChunk.getComponent(index, ChunkSection.getComponentType());
/* 203 */     assert section != null;
/* 204 */     if (section.getChunkColumnReference() == null || !section.getChunkColumnReference().isValid())
/*     */       return; 
/* 206 */     BlockComponentChunk blockComponentChunk = (BlockComponentChunk)commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());
/* 207 */     assert blockComponentChunk != null;
/*     */     
/* 209 */     Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(index);
/* 210 */     BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(section.getChunkColumnReference(), BlockChunk.getComponentType());
/* 211 */     assert blockChunk != null;
/*     */ 
/*     */     
/* 214 */     blocks.forEachTicking(blockComponentChunk, commandBuffer, section.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
/*     */           Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
/*     */           if (blockRef == null) {
/*     */             return BlockTickStrategy.IGNORED;
/*     */           }
/*     */           FarmingBlock farming = (FarmingBlock)commandBuffer1.getComponent(blockRef, FarmingBlock.getComponentType());
/*     */           if (farming != null) {
/*     */             FarmingUtil.tickFarming(commandBuffer1, blockChunk, blocks, ref, blockRef, farming, localX, localY, localZ, false);
/*     */             return BlockTickStrategy.SLEEP;
/*     */           } 
/*     */           TilledSoilBlock soil = (TilledSoilBlock)commandBuffer1.getComponent(blockRef, TilledSoilBlock.getComponentType());
/*     */           if (soil != null) {
/*     */             tickSoil(commandBuffer1, blockComponentChunk1, blockRef, soil);
/*     */             return BlockTickStrategy.SLEEP;
/*     */           } 
/*     */           CoopBlock coop = (CoopBlock)commandBuffer1.getComponent(blockRef, CoopBlock.getComponentType());
/*     */           if (coop != null) {
/*     */             tickCoop(commandBuffer1, blockComponentChunk1, blockRef, coop);
/*     */             return BlockTickStrategy.SLEEP;
/*     */           } 
/*     */           return BlockTickStrategy.IGNORED;
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void tickSoil(CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, TilledSoilBlock soilBlock) {
/* 241 */     BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/* 242 */     assert info != null;
/*     */     
/* 244 */     int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 245 */     int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 246 */     int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */     
/* 248 */     if (y >= 320)
/*     */       return; 
/* 250 */     assert info.getChunkRef() != null;
/* 251 */     BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 252 */     assert blockChunk != null;
/* 253 */     BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*     */     
/* 255 */     boolean hasCrop = FarmingSystems.hasCropAbove(blockChunk, x, y, z);
/*     */     
/* 257 */     BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/* 258 */     Instant currentTime = ((WorldTimeResource)((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())).getGameTime();
/*     */     
/* 260 */     Instant decayTime = soilBlock.getDecayTime();
/*     */ 
/*     */     
/* 263 */     if (soilBlock.isPlanted() && !hasCrop) {
/* 264 */       if (!FarmingSystems.updateSoilDecayTime(commandBuffer, soilBlock, blockType))
/* 265 */         return;  if (decayTime != null) {
/* 266 */         blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
/*     */       }
/* 268 */     } else if (!soilBlock.isPlanted() && !hasCrop) {
/*     */       
/* 270 */       if (decayTime == null || !decayTime.isAfter(currentTime)) {
/* 271 */         assert info.getChunkRef() != null;
/*     */         
/* 273 */         if (blockType == null || blockType.getFarming() == null || blockType.getFarming().getSoilConfig() == null)
/* 274 */           return;  FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
/* 275 */         String str = soilConfig.getTargetBlock();
/* 276 */         if (str == null)
/*     */           return; 
/* 278 */         int targetBlockId = BlockType.getAssetMap().getIndex(str);
/* 279 */         if (targetBlockId == Integer.MIN_VALUE)
/* 280 */           return;  BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/*     */         
/* 282 */         int rotation = blockSection.getRotationIndex(x, y, z);
/*     */         
/* 284 */         WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 285 */         commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 0));
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/* 290 */     } else if (hasCrop) {
/* 291 */       soilBlock.setDecayTime(null);
/*     */     } 
/*     */     
/* 294 */     String targetBlock = soilBlock.computeBlockType(currentTime, blockType);
/* 295 */     if (targetBlock != null && !targetBlock.equals(blockType.getId())) {
/* 296 */       WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 297 */       int rotation = blockSection.getRotationIndex(x, y, z);
/* 298 */       int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
/* 299 */       BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/* 300 */       commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 2));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 305 */     soilBlock.setPlanted(hasCrop);
/*     */   }
/*     */   
/*     */   private static void tickCoop(CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, CoopBlock coopBlock) {
/* 309 */     BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/* 310 */     assert info != null;
/*     */     
/* 312 */     Store<EntityStore> store = ((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore();
/* 313 */     WorldTimeResource worldTimeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/* 314 */     FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
/* 315 */     if (coopAsset == null)
/*     */       return; 
/* 317 */     int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 318 */     int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 319 */     int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */     
/* 321 */     BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 322 */     assert blockChunk != null;
/*     */     
/* 324 */     ChunkColumn column = (ChunkColumn)commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());
/* 325 */     assert column != null;
/* 326 */     Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 327 */     assert sectionRef != null;
/* 328 */     BlockSection blockSection = (BlockSection)commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
/* 329 */     assert blockSection != null;
/* 330 */     ChunkSection chunkSection = (ChunkSection)commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
/* 331 */     assert chunkSection != null;
/*     */     
/* 333 */     int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
/* 334 */     int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
/* 335 */     int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
/*     */     
/* 337 */     World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/* 338 */     WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
/* 339 */     double blockRotation = chunk.getRotation(worldX, worldY, worldZ).yaw().getRadians();
/* 340 */     Vector3d spawnOffset = (new Vector3d()).assign(coopAsset.getResidentSpawnOffset()).rotateY((float)blockRotation);
/* 341 */     Vector3i coopLocation = new Vector3i(worldX, worldY, worldZ);
/*     */ 
/*     */     
/* 344 */     boolean tryCapture = coopAsset.getCaptureWildNPCsInRange();
/* 345 */     float captureRange = coopAsset.getWildCaptureRadius();
/* 346 */     if (tryCapture && captureRange >= 0.0F) {
/* 347 */       world.execute(() -> {
/*     */             List<Ref<EntityStore>> entities = TargetUtil.getAllEntitiesInSphere(coopLocation.toVector3d(), captureRange, (ComponentAccessor)store);
/*     */ 
/*     */ 
/*     */             
/*     */             for (Ref<EntityStore> entity : entities) {
/*     */               coopBlock.tryPutWildResidentFromWild(store, entity, worldTimeResource, coopLocation);
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/* 359 */     if (coopBlock.shouldResidentsBeInCoop(worldTimeResource)) {
/* 360 */       world.execute(() -> coopBlock.ensureNoResidentsInWorld(store));
/*     */     } else {
/* 362 */       world.execute(() -> {
/*     */             coopBlock.ensureSpawnResidentsInWorld(world, store, coopLocation.toVector3d(), spawnOffset);
/*     */             
/*     */             coopBlock.generateProduceToInventory(worldTimeResource);
/*     */             
/*     */             Vector3i blockPos = new Vector3i(worldX, worldY, worldZ);
/*     */             
/*     */             BlockType currentBlockType = world.getBlockType(blockPos);
/*     */             
/*     */             assert currentBlockType != null;
/*     */             chunk.setBlockInteractionState(blockPos, currentBlockType, coopBlock.hasProduce() ? "Produce_Ready" : "default");
/*     */           });
/*     */     } 
/* 375 */     Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
/* 376 */     if (nextTickInstant != null) {
/* 377 */       blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Query<ChunkStore> getQuery() {
/* 384 */     return QUERY;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingSystems$Ticking.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */