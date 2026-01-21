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
/* 250 */     int checkIndex = ChunkUtil.indexBlockInColumn(x, y + 1, z);
/* 251 */     Ref<ChunkStore> aboveBlockRef = blockComponentChunk.getEntityReference(checkIndex);
/*     */     
/* 253 */     boolean hasCrop = false;
/* 254 */     if (aboveBlockRef != null) {
/* 255 */       FarmingBlock farmingBlock = (FarmingBlock)commandBuffer.getComponent(aboveBlockRef, FarmingBlock.getComponentType());
/* 256 */       hasCrop = (farmingBlock != null);
/*     */     } 
/*     */     
/* 259 */     assert info.getChunkRef() != null;
/* 260 */     BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 261 */     assert blockChunk != null;
/* 262 */     BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*     */     
/* 264 */     BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/* 265 */     Instant currentTime = ((WorldTimeResource)((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())).getGameTime();
/*     */     
/* 267 */     Instant decayTime = soilBlock.getDecayTime();
/*     */ 
/*     */     
/* 270 */     if (soilBlock.isPlanted() && !hasCrop) {
/* 271 */       if (!FarmingSystems.updateSoilDecayTime(commandBuffer, soilBlock, blockType))
/* 272 */         return;  if (decayTime != null) {
/* 273 */         blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
/*     */       }
/* 275 */     } else if (!soilBlock.isPlanted() && !hasCrop) {
/*     */       
/* 277 */       if (decayTime == null || !decayTime.isAfter(currentTime)) {
/* 278 */         assert info.getChunkRef() != null;
/*     */         
/* 280 */         if (blockType == null || blockType.getFarming() == null || blockType.getFarming().getSoilConfig() == null)
/* 281 */           return;  FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
/* 282 */         String str = soilConfig.getTargetBlock();
/* 283 */         if (str == null)
/*     */           return; 
/* 285 */         int targetBlockId = BlockType.getAssetMap().getIndex(str);
/* 286 */         if (targetBlockId == Integer.MIN_VALUE)
/* 287 */           return;  BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/*     */         
/* 289 */         int rotation = blockSection.getRotationIndex(x, y, z);
/*     */         
/* 291 */         WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 292 */         commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 0));
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/* 297 */     } else if (hasCrop) {
/* 298 */       soilBlock.setDecayTime(null);
/*     */     } 
/*     */     
/* 301 */     String targetBlock = soilBlock.computeBlockType(currentTime, blockType);
/* 302 */     if (targetBlock != null && !targetBlock.equals(blockType.getId())) {
/* 303 */       WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
/* 304 */       int rotation = blockSection.getRotationIndex(x, y, z);
/* 305 */       int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
/* 306 */       BlockType targetBlockType = (BlockType)BlockType.getAssetMap().getAsset(targetBlockId);
/* 307 */       commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 2));
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 312 */     soilBlock.setPlanted(hasCrop);
/*     */   }
/*     */   
/*     */   private static void tickCoop(CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, CoopBlock coopBlock) {
/* 316 */     BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
/* 317 */     assert info != null;
/*     */     
/* 319 */     Store<EntityStore> store = ((ChunkStore)commandBuffer.getExternalData()).getWorld().getEntityStore().getStore();
/* 320 */     WorldTimeResource worldTimeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/* 321 */     FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
/* 322 */     if (coopAsset == null)
/*     */       return; 
/* 324 */     int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 325 */     int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 326 */     int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*     */     
/* 328 */     BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 329 */     assert blockChunk != null;
/*     */     
/* 331 */     ChunkColumn column = (ChunkColumn)commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());
/* 332 */     assert column != null;
/* 333 */     Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 334 */     assert sectionRef != null;
/* 335 */     BlockSection blockSection = (BlockSection)commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
/* 336 */     assert blockSection != null;
/* 337 */     ChunkSection chunkSection = (ChunkSection)commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
/* 338 */     assert chunkSection != null;
/*     */     
/* 340 */     int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
/* 341 */     int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
/* 342 */     int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
/*     */     
/* 344 */     World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/* 345 */     WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
/* 346 */     double blockRotation = chunk.getRotation(worldX, worldY, worldZ).yaw().getRadians();
/* 347 */     Vector3d spawnOffset = (new Vector3d()).assign(coopAsset.getResidentSpawnOffset()).rotateY((float)blockRotation);
/* 348 */     Vector3i coopLocation = new Vector3i(worldX, worldY, worldZ);
/*     */ 
/*     */     
/* 351 */     boolean tryCapture = coopAsset.getCaptureWildNPCsInRange();
/* 352 */     float captureRange = coopAsset.getWildCaptureRadius();
/* 353 */     if (tryCapture && captureRange >= 0.0F) {
/* 354 */       world.execute(() -> {
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
/* 366 */     if (coopBlock.shouldResidentsBeInCoop(worldTimeResource)) {
/* 367 */       world.execute(() -> coopBlock.ensureNoResidentsInWorld(store));
/*     */     } else {
/* 369 */       world.execute(() -> {
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
/* 382 */     Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
/* 383 */     if (nextTickInstant != null) {
/* 384 */       blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Query<ChunkStore> getQuery() {
/* 391 */     return QUERY;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingSystems$Ticking.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */