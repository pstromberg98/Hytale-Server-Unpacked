/*     */ package com.hypixel.hytale.builtin.adventure.farming;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.Rangef;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
/*     */ import java.time.Instant;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class FarmingUtil {
/*     */   private static final int MAX_SECONDS_BETWEEN_TICKS = 15;
/*     */   
/*     */   public static void tickFarming(CommandBuffer<ChunkStore> commandBuffer, BlockChunk blockChunk, BlockSection blockSection, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, FarmingBlock farmingBlock, int x, int y, int z, boolean initialTick) {
/*  36 */     World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/*  37 */     WorldTimeResource worldTimeResource = (WorldTimeResource)world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
/*  38 */     Instant currentTime = worldTimeResource.getGameTime();
/*     */ 
/*     */ 
/*     */     
/*  42 */     BlockType blockType = (farmingBlock.getPreviousBlockType() != null) ? (BlockType)BlockType.getAssetMap().getAsset(farmingBlock.getPreviousBlockType()) : (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/*  43 */     if (blockType == null)
/*  44 */       return;  if (blockType.getFarming() == null)
/*     */       return; 
/*  46 */     FarmingData farmingConfig = blockType.getFarming();
/*  47 */     if (farmingConfig.getStages() == null)
/*     */       return; 
/*  49 */     float currentProgress = farmingBlock.getGrowthProgress();
/*  50 */     int currentStage = (int)currentProgress;
/*  51 */     String currentStageSet = farmingBlock.getCurrentStageSet();
/*     */     
/*  53 */     FarmingStageData[] stages = (currentStageSet != null) ? (FarmingStageData[])farmingConfig.getStages().get(currentStageSet) : null;
/*  54 */     if (stages == null) {
/*  55 */       currentStageSet = farmingConfig.getStartingStageSet();
/*  56 */       if (currentStageSet == null)
/*  57 */         return;  farmingBlock.setCurrentStageSet(currentStageSet);
/*  58 */       stages = (FarmingStageData[])farmingConfig.getStages().get(currentStageSet);
/*  59 */       blockChunk.markNeedsSaving();
/*     */     } 
/*     */     
/*  62 */     if (stages == null)
/*     */       return; 
/*  64 */     if (currentStage < 0) {
/*  65 */       currentStage = 0;
/*  66 */       currentProgress = 0.0F;
/*  67 */       farmingBlock.setGrowthProgress(0.0F);
/*     */     } 
/*     */     
/*  70 */     if (currentStage >= stages.length) {
/*     */       
/*  72 */       commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
/*     */       
/*     */       return;
/*     */     } 
/*  76 */     long remainingTimeSeconds = currentTime.getEpochSecond() - farmingBlock.getLastTickGameTime().getEpochSecond();
/*     */     
/*  78 */     ChunkSection section = (ChunkSection)commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
/*     */     
/*  80 */     int worldX = ChunkUtil.worldCoordFromLocalCoord(section.getX(), x);
/*  81 */     int worldY = ChunkUtil.worldCoordFromLocalCoord(section.getY(), y);
/*  82 */     int worldZ = ChunkUtil.worldCoordFromLocalCoord(section.getZ(), z);
/*     */     
/*  84 */     while (currentStage < stages.length) {
/*  85 */       FarmingStageData stage = stages[currentStage];
/*     */       
/*  87 */       if (stage.shouldStop((ComponentAccessor)commandBuffer, sectionRef, blockRef, x, y, z)) {
/*  88 */         blockChunk.markNeedsSaving();
/*     */         
/*  90 */         farmingBlock.setGrowthProgress(stages.length);
/*     */         
/*  92 */         commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/*  97 */       Rangef range = stage.getDuration();
/*  98 */       if (range == null) {
/*  99 */         blockChunk.markNeedsSaving();
/*     */         
/* 101 */         commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
/*     */         
/*     */         break;
/*     */       } 
/* 105 */       double rand = HashUtil.random(farmingBlock.getGeneration(), worldX, worldY, worldZ);
/*     */       
/* 107 */       double baseDuration = range.min + (range.max - range.min) * rand;
/* 108 */       long remainingDurationSeconds = Math.round(baseDuration * (1.0D - currentProgress % 1.0D));
/*     */ 
/*     */       
/* 111 */       double growthMultiplier = 1.0D;
/* 112 */       if (farmingConfig.getGrowthModifiers() != null) {
/* 113 */         for (String modifierName : farmingConfig.getGrowthModifiers()) {
/* 114 */           GrowthModifierAsset modifier = (GrowthModifierAsset)GrowthModifierAsset.getAssetMap().getAsset(modifierName);
/* 115 */           if (modifier != null) {
/* 116 */             growthMultiplier *= modifier.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick);
/*     */           }
/*     */         } 
/*     */       }
/*     */       
/* 121 */       remainingDurationSeconds = Math.round(remainingDurationSeconds / growthMultiplier);
/*     */       
/* 123 */       if (remainingTimeSeconds < remainingDurationSeconds) {
/*     */         
/* 125 */         currentProgress += (float)(remainingTimeSeconds / baseDuration / growthMultiplier);
/* 126 */         farmingBlock.setGrowthProgress(currentProgress);
/* 127 */         long nextGrowthInNanos = (remainingDurationSeconds - remainingTimeSeconds) * 1000000000L;
/* 128 */         long randCap = (long)((15.0D + 10.0D * HashUtil.random(farmingBlock.getGeneration() ^ 0xCAFEBEEFL, worldX, worldY, worldZ)) * world.getTps() * WorldTimeResource.getSecondsPerTick(world) * 1.0E9D);
/* 129 */         long cappedNextGrowthInNanos = Math.min(nextGrowthInNanos, randCap);
/* 130 */         blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), currentTime.plusNanos(cappedNextGrowthInNanos));
/*     */         
/*     */         break;
/*     */       } 
/* 134 */       remainingTimeSeconds -= remainingDurationSeconds;
/* 135 */       currentStage++;
/* 136 */       currentProgress = currentStage;
/* 137 */       farmingBlock.setGrowthProgress(currentProgress);
/* 138 */       blockChunk.markNeedsSaving();
/*     */ 
/*     */       
/* 141 */       farmingBlock.setGeneration(farmingBlock.getGeneration() + 1);
/* 142 */       if (currentStage >= stages.length) {
/* 143 */         if (stages[currentStage - 1].implementsShouldStop()) {
/* 144 */           currentStage = stages.length - 1;
/* 145 */           farmingBlock.setGrowthProgress(currentStage);
/* 146 */           stages[currentStage].apply((ComponentAccessor)commandBuffer, sectionRef, blockRef, x, y, z, stages[currentStage]); continue;
/*     */         } 
/* 148 */         farmingBlock.setGrowthProgress(stages.length);
/*     */         
/* 150 */         commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
/*     */         
/*     */         continue;
/*     */       } 
/* 154 */       farmingBlock.setExecutions(0);
/* 155 */       stages[currentStage].apply((ComponentAccessor)commandBuffer, sectionRef, blockRef, x, y, z, stages[currentStage - 1]);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 160 */     farmingBlock.setLastTickGameTime(currentTime);
/*     */   }
/*     */   private static final int BETWEEN_RANDOM = 10;
/*     */   
/*     */   public static void harvest(@Nonnull World world, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull BlockType blockType, int rotationIndex, @Nonnull Vector3i blockPosition) {
/* 165 */     if (world.getGameplayConfig().getWorldConfig().isBlockGatheringAllowed()) {
/* 166 */       harvest0(store, ref, blockType, rotationIndex, blockPosition);
/*     */     }
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static CapturedNPCMetadata generateCapturedNPCMetadata(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> entityRef, int roleIndex) {
/* 172 */     PersistentModel persistentModel = (PersistentModel)componentAccessor.getComponent(entityRef, PersistentModel.getComponentType());
/* 173 */     if (persistentModel == null) {
/* 174 */       return null;
/*     */     }
/*     */     
/* 177 */     ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(persistentModel.getModelReference().getModelAssetId());
/* 178 */     CapturedNPCMetadata meta = new CapturedNPCMetadata();
/*     */     
/* 180 */     if (modelAsset != null) {
/* 181 */       meta.setIconPath(modelAsset.getIcon());
/*     */     }
/* 183 */     meta.setRoleIndex(roleIndex);
/*     */     
/* 185 */     return meta;
/*     */   }
/*     */   protected static boolean harvest0(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull BlockType blockType, int rotationIndex, @Nonnull Vector3i blockPosition) {
/*     */     FarmingBlock farmingBlock;
/* 189 */     FarmingData farmingConfig = blockType.getFarming();
/* 190 */     if (farmingConfig == null || farmingConfig.getStages() == null) {
/* 191 */       return false;
/*     */     }
/*     */     
/* 194 */     if (blockType.getGathering().getHarvest() == null) {
/* 195 */       return false;
/*     */     }
/*     */     
/* 198 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 200 */     Vector3d centerPosition = new Vector3d();
/* 201 */     blockType.getBlockCenter(rotationIndex, centerPosition);
/* 202 */     centerPosition.add(blockPosition);
/*     */     
/* 204 */     if (farmingConfig.getStageSetAfterHarvest() == null) {
/*     */ 
/*     */       
/* 207 */       giveDrops(store, ref, centerPosition, blockType);
/*     */       
/* 209 */       WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
/* 210 */       if (chunk != null) {
/* 211 */         chunk.breakBlock(blockPosition.x, blockPosition.y, blockPosition.z);
/*     */       }
/* 213 */       return true;
/*     */     } 
/*     */     
/* 216 */     giveDrops(store, ref, centerPosition, blockType);
/*     */     
/* 218 */     Map<String, FarmingStageData[]> stageSets = farmingConfig.getStages();
/* 219 */     FarmingStageData[] stages = stageSets.get(farmingConfig.getStartingStageSet());
/* 220 */     if (stages == null) {
/* 221 */       return false;
/*     */     }
/* 223 */     int currentStageIndex = stages.length - 1;
/* 224 */     FarmingStageData previousStage = stages[currentStageIndex];
/*     */ 
/*     */     
/* 227 */     String newStageSet = farmingConfig.getStageSetAfterHarvest();
/* 228 */     FarmingStageData[] newStages = stageSets.get(newStageSet);
/* 229 */     if (newStages == null || newStages.length == 0) {
/* 230 */       WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
/* 231 */       if (chunk != null) {
/* 232 */         chunk.breakBlock(blockPosition.x, blockPosition.y, blockPosition.z);
/*     */       }
/* 234 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 239 */     Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/* 240 */     Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
/* 241 */     if (chunkRef == null) return false; 
/* 242 */     BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
/* 243 */     if (blockComponentChunk == null) return false;
/*     */ 
/*     */     
/* 246 */     Instant now = ((WorldTimeResource)((EntityStore)store.getExternalData()).getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType())).getGameTime();
/*     */     
/* 248 */     int blockIndexColumn = ChunkUtil.indexBlockInColumn(blockPosition.x, blockPosition.y, blockPosition.z);
/* 249 */     Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndexColumn);
/* 250 */     if (blockRef == null) {
/* 251 */       Holder<ChunkStore> blockEntity = ChunkStore.REGISTRY.newHolder();
/* 252 */       blockEntity.putComponent(BlockModule.BlockStateInfo.getComponentType(), (Component)new BlockModule.BlockStateInfo(blockIndexColumn, chunkRef));
/* 253 */       farmingBlock = new FarmingBlock();
/*     */       
/* 255 */       farmingBlock.setLastTickGameTime(now);
/* 256 */       farmingBlock.setCurrentStageSet(newStageSet);
/* 257 */       blockEntity.addComponent(FarmingBlock.getComponentType(), (Component)farmingBlock);
/* 258 */       blockRef = chunkStore.addEntity(blockEntity, AddReason.SPAWN);
/*     */     } else {
/* 260 */       farmingBlock = (FarmingBlock)chunkStore.ensureAndGetComponent(blockRef, FarmingBlock.getComponentType());
/*     */     } 
/*     */     
/* 263 */     farmingBlock.setCurrentStageSet(newStageSet);
/* 264 */     farmingBlock.setGrowthProgress(0.0F);
/* 265 */     farmingBlock.setExecutions(0);
/* 266 */     farmingBlock.setGeneration(farmingBlock.getGeneration() + 1);
/*     */     
/* 268 */     farmingBlock.setLastTickGameTime(now);
/*     */     
/* 270 */     Ref<ChunkStore> sectionRef = world.getChunkStore().getChunkSectionReference(ChunkUtil.chunkCoordinate(blockPosition.x), ChunkUtil.chunkCoordinate(blockPosition.y), ChunkUtil.chunkCoordinate(blockPosition.z));
/* 271 */     if (sectionRef == null) {
/* 272 */       return false;
/*     */     }
/*     */     
/* 275 */     if (blockRef == null) {
/* 276 */       return false;
/*     */     }
/*     */     
/* 279 */     BlockSection section = (BlockSection)chunkStore.getComponent(sectionRef, BlockSection.getComponentType());
/* 280 */     if (section != null) {
/* 281 */       section.scheduleTick(ChunkUtil.indexBlock(blockPosition.x, blockPosition.y, blockPosition.z), now);
/*     */     }
/*     */     
/* 284 */     newStages[0].apply((ComponentAccessor)chunkStore, sectionRef, blockRef, blockPosition.x, blockPosition.y, blockPosition.z, previousStage);
/* 285 */     return true;
/*     */   }
/*     */   
/*     */   protected static void giveDrops(@Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Vector3d origin, @Nonnull BlockType blockType) {
/* 289 */     HarvestingDropType harvest = blockType.getGathering().getHarvest();
/* 290 */     String itemId = harvest.getItemId();
/* 291 */     String dropListId = harvest.getDropListId();
/* 292 */     BlockHarvestUtils.getDrops(blockType, 1, itemId, dropListId).forEach(itemStack -> ItemUtils.interactivelyPickupItem(ref, itemStack, origin, store));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */