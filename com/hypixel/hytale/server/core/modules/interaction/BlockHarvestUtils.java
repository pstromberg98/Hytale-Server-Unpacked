/*      */ package com.hypixel.hytale.server.core.modules.interaction;
/*      */ 
/*      */ import com.hypixel.hytale.component.AddReason;
/*      */ import com.hypixel.hytale.component.CommandBuffer;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.ComponentType;
/*      */ import com.hypixel.hytale.component.Holder;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.component.Store;
/*      */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*      */ import com.hypixel.hytale.component.system.EcsEvent;
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.math.vector.Vector3d;
/*      */ import com.hypixel.hytale.math.vector.Vector3f;
/*      */ import com.hypixel.hytale.math.vector.Vector3i;
/*      */ import com.hypixel.hytale.protocol.BlockSoundEvent;
/*      */ import com.hypixel.hytale.protocol.SoundCategory;
/*      */ import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocksound.config.BlockSoundSet;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockGathering;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.PhysicsDropType;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.SoftBlockDropType;
/*      */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*      */ import com.hypixel.hytale.server.core.asset.type.gameplay.BrokenPenalties;
/*      */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*      */ import com.hypixel.hytale.server.core.asset.type.gameplay.GatheringEffectsConfig;
/*      */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*      */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemTool;
/*      */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemToolSpec;
/*      */ import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
/*      */ import com.hypixel.hytale.server.core.entity.ItemUtils;
/*      */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*      */ import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
/*      */ import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
/*      */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*      */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*      */ import com.hypixel.hytale.server.core.modules.blockhealth.BlockHealth;
/*      */ import com.hypixel.hytale.server.core.modules.blockhealth.BlockHealthChunk;
/*      */ import com.hypixel.hytale.server.core.modules.blockhealth.BlockHealthModule;
/*      */ import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
/*      */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*      */ import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
/*      */ import com.hypixel.hytale.server.core.modules.item.ItemModule;
/*      */ import com.hypixel.hytale.server.core.modules.time.TimeResource;
/*      */ import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
/*      */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*      */ import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectLists;
/*      */ import java.util.List;
/*      */ import java.util.Objects;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ public class BlockHarvestUtils
/*      */ {
/*      */   @Nullable
/*      */   public static ItemToolSpec getSpecPowerDamageBlock(@Nullable Item item, @Nullable BlockType blockType, @Nullable ItemTool tool) {
/*   72 */     if (blockType == null) return null;
/*      */     
/*   74 */     BlockGathering gathering = blockType.getGathering();
/*   75 */     if (gathering == null) return null;
/*      */     
/*   77 */     BlockBreakingDropType breaking = gathering.getBreaking();
/*   78 */     if (breaking == null) return null;
/*      */     
/*   80 */     String gatherType = breaking.getGatherType();
/*   81 */     if (gatherType == null) return null;
/*      */     
/*   83 */     if (item != null && (
/*   84 */       item.getWeapon() != null || item.getBuilderToolData() != null)) {
/*   85 */       return null;
/*      */     }
/*      */ 
/*      */     
/*   89 */     int requiredQuality = breaking.getQuality();
/*      */     
/*   91 */     if (tool != null) {
/*   92 */       if (tool.getSpecs() != null) {
/*   93 */         for (ItemToolSpec spec : tool.getSpecs()) {
/*   94 */           if (Objects.equals(spec.getGatherType(), gatherType)) {
/*   95 */             if (spec.getQuality() < requiredQuality) {
/*   96 */               return null;
/*      */             }
/*   98 */             return spec;
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/*  103 */       return null;
/*      */     } 
/*      */     
/*  106 */     ItemToolSpec defaultSpec = (ItemToolSpec)ItemToolSpec.getAssetMap().getAsset(gatherType);
/*  107 */     if (defaultSpec != null && defaultSpec.getQuality() < requiredQuality) {
/*  108 */       return null;
/*      */     }
/*  110 */     return defaultSpec;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double calculateDurabilityUse(@Nonnull Item item, @Nullable BlockType blockType) {
/*  121 */     if (blockType == null) return 0.0D; 
/*  122 */     if (blockType.getGathering().isSoft()) return 0.0D; 
/*  123 */     if (item.getTool() == null) return 0.0D;
/*      */     
/*  125 */     ItemTool itemTool = item.getTool();
/*  126 */     ItemTool.DurabilityLossBlockTypes[] durabilityLossBlockTypes = itemTool.getDurabilityLossBlockTypes();
/*  127 */     if (durabilityLossBlockTypes == null) return item.getDurabilityLossOnHit();
/*      */     
/*  129 */     String hitBlockTypeId = blockType.getId();
/*  130 */     int hitBlockTypeIndex = BlockType.getAssetMap().getIndex(hitBlockTypeId);
/*  131 */     if (hitBlockTypeIndex == Integer.MIN_VALUE) throw new IllegalArgumentException("Unknown key! " + hitBlockTypeId); 
/*  132 */     BlockSetModule blockSetModule = BlockSetModule.getInstance();
/*      */     
/*  134 */     for (ItemTool.DurabilityLossBlockTypes durabilityLossBlockType : durabilityLossBlockTypes) {
/*  135 */       int[] blockTypeIndexes = durabilityLossBlockType.getBlockTypeIndexes();
/*  136 */       if (blockTypeIndexes != null) {
/*  137 */         for (int blockTypeIndex : blockTypeIndexes) {
/*  138 */           if (blockTypeIndex == hitBlockTypeIndex) {
/*  139 */             return durabilityLossBlockType.getDurabilityLossOnHit();
/*      */           }
/*      */         } 
/*      */       }
/*      */       
/*  144 */       int[] blockSetIndexes = durabilityLossBlockType.getBlockSetIndexes();
/*  145 */       if (blockSetIndexes != null) {
/*  146 */         for (int blockSetIndex : blockSetIndexes) {
/*  147 */           if (blockSetModule.blockInSet(blockSetIndex, hitBlockTypeId)) {
/*  148 */             return durabilityLossBlockType.getDurabilityLossOnHit();
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/*  154 */     return item.getDurabilityLossOnHit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean performBlockDamage(@Nonnull Vector3i targetBlock, @Nullable ItemStack itemStack, @Nullable ItemTool tool, float damageScale, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  178 */     return performBlockDamage(null, null, targetBlock, itemStack, tool, null, false, damageScale, setBlockSettings, chunkReference, (ComponentAccessor<EntityStore>)commandBuffer, chunkStore);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean performBlockDamage(@Nullable LivingEntity entity, @Nullable Ref<EntityStore> ref, @Nonnull Vector3i targetBlockPos, @Nullable ItemStack itemStack, @Nullable ItemTool tool, @Nullable String toolId, boolean matchTool, float damageScale, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*      */     String itemId, dropListId;
/*  211 */     World world = ((EntityStore)entityStore.getExternalData()).getWorld();
/*  212 */     GameplayConfig gameplayConfig = world.getGameplayConfig();
/*      */     
/*  214 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkReference, WorldChunk.getComponentType());
/*  215 */     if (worldChunkComponent == null) return false;
/*      */     
/*  217 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  218 */     assert blockChunkComponent != null;
/*      */     
/*  220 */     BlockSection targetSection = blockChunkComponent.getSectionAtBlockY(targetBlockPos.y);
/*  221 */     int targetRotationIndex = targetSection.getRotationIndex(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*      */     
/*  223 */     boolean brokeBlock = false;
/*  224 */     int environmentId = blockChunkComponent.getEnvironment(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  225 */     Environment environmentAsset = (Environment)Environment.getAssetMap().getAsset(environmentId);
/*      */     
/*  227 */     SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)entityStore.getResource(EntityModule.get().getPlayerSpatialResourceType());
/*      */ 
/*      */ 
/*      */     
/*  231 */     if (environmentAsset != null && !environmentAsset.isBlockModificationAllowed()) {
/*  232 */       targetSection.invalidateBlock(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  233 */       return false;
/*      */     } 
/*      */     
/*  236 */     BlockType targetBlockType = worldChunkComponent.getBlockType(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  237 */     if (targetBlockType == null) return false;
/*      */     
/*  239 */     BlockGathering blockGathering = targetBlockType.getGathering();
/*  240 */     if (blockGathering == null) return false;
/*      */ 
/*      */     
/*  243 */     if (matchTool && !blockGathering.getToolData().containsKey(toolId)) return false;
/*      */     
/*  245 */     Vector3d targetBlockCenterPos = new Vector3d();
/*  246 */     targetBlockType.getBlockCenter(targetRotationIndex, targetBlockCenterPos);
/*  247 */     targetBlockCenterPos.add(targetBlockPos);
/*      */     
/*  249 */     Vector3i originBlock = new Vector3i(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*      */     
/*  251 */     if (!targetBlockType.isUnknown()) {
/*  252 */       int filler = targetSection.getFiller(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  253 */       int fillerX = FillerBlockUtil.unpackX(filler);
/*  254 */       int fillerY = FillerBlockUtil.unpackY(filler);
/*  255 */       int fillerZ = FillerBlockUtil.unpackZ(filler);
/*      */       
/*  257 */       if (fillerX != 0 || fillerY != 0 || fillerZ != 0) {
/*      */         
/*  259 */         originBlock = originBlock.clone().subtract(fillerX, fillerY, fillerZ);
/*      */         
/*  261 */         String oldBlockTypeKey = targetBlockType.getId();
/*  262 */         targetBlockType = world.getBlockType(originBlock.getX(), originBlock.getY(), originBlock.getZ());
/*  263 */         if (targetBlockType == null) return false;
/*      */         
/*  265 */         if (!oldBlockTypeKey.equals(targetBlockType.getId())) {
/*  266 */           worldChunkComponent.breakBlock(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  267 */           return true;
/*      */         } 
/*      */         
/*  270 */         blockGathering = targetBlockType.getGathering();
/*  271 */         if (blockGathering == null) return false;
/*      */       
/*      */       } 
/*      */     } 
/*  275 */     Item heldItem = (itemStack != null) ? itemStack.getItem() : null;
/*  276 */     if (tool == null && heldItem != null) {
/*  277 */       tool = heldItem.getTool();
/*      */     }
/*      */     
/*  280 */     ItemToolSpec itemToolSpec = getSpecPowerDamageBlock(heldItem, targetBlockType, tool);
/*  281 */     float specPower = (itemToolSpec != null) ? itemToolSpec.getPower() : 0.0F;
/*      */     
/*  283 */     boolean canApplyItemStackPenalties = (entity != null && entity.canApplyItemStackPenalties(ref, entityStore));
/*  284 */     if (specPower != 0.0F && heldItem != null && heldItem.getTool() != null && itemStack.isBroken() && canApplyItemStackPenalties) {
/*  285 */       BrokenPenalties brokenPenalties = gameplayConfig.getItemDurabilityConfig().getBrokenPenalties();
/*  286 */       specPower *= 1.0F - (float)brokenPenalties.getTool(0.0D);
/*      */     } 
/*      */ 
/*      */     
/*  290 */     int dropQuantity = 1;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  295 */     if (specPower != 0.0F) {
/*  296 */       BlockBreakingDropType breaking = blockGathering.getBreaking();
/*      */       
/*  298 */       damage = specPower;
/*  299 */       dropQuantity = breaking.getQuantity();
/*  300 */       itemId = breaking.getItemId();
/*  301 */       dropListId = breaking.getDropListId();
/*      */     
/*      */     }
/*  304 */     else if (blockGathering.isSoft()) {
/*  305 */       SoftBlockDropType soft = blockGathering.getSoft();
/*      */       
/*  307 */       if (!soft.isWeaponBreakable() && heldItem != null && heldItem.getWeapon() != null) {
/*  308 */         return false;
/*      */       }
/*  310 */       damage = 1.0F;
/*  311 */       itemId = soft.getItemId();
/*  312 */       dropListId = soft.getDropListId();
/*      */ 
/*      */       
/*  315 */       damageScale = 1.0F;
/*      */     }
/*      */     else {
/*      */       
/*  319 */       if (heldItem != null && heldItem.getWeapon() == null) {
/*  320 */         if (ref != null && entity != null) {
/*  321 */           GatheringEffectsConfig unbreakableBlockConfig = gameplayConfig.getGatheringConfig().getUnbreakableBlockConfig();
/*  322 */           if ((setBlockSettings & 0x4) == 0) {
/*  323 */             String particleSystemId = unbreakableBlockConfig.getParticleSystemId();
/*  324 */             if (particleSystemId != null) {
/*  325 */               ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/*  326 */               playerSpatialResource.getSpatialStructure().collect(targetBlockCenterPos, 75.0D, (List)results);
/*      */               
/*  328 */               ParticleUtil.spawnParticleEffect(particleSystemId, targetBlockCenterPos, (List)results, entityStore);
/*      */             } 
/*      */           } 
/*      */           
/*  332 */           if ((setBlockSettings & 0x400) == 0) {
/*  333 */             int soundEventIndex = unbreakableBlockConfig.getSoundEventIndex();
/*  334 */             if (soundEventIndex != 0) {
/*  335 */               SoundUtil.playSoundEvent3d(ref, soundEventIndex, targetBlockCenterPos, entityStore);
/*      */             }
/*      */             
/*  338 */             if (heldItem.getTool() != null) {
/*  339 */               int hitSoundEventLayerIndex = heldItem.getTool().getIncorrectMaterialSoundLayerIndex();
/*  340 */               if (hitSoundEventLayerIndex != 0) {
/*  341 */                 SoundUtil.playSoundEvent3d(ref, hitSoundEventLayerIndex, targetBlockCenterPos, entityStore);
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */         
/*  347 */         return false;
/*      */       } 
/*  349 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  353 */     damage *= damageScale;
/*      */     
/*  355 */     ChunkColumn chunkColumnComponent = (ChunkColumn)chunkStore.getComponent(chunkReference, ChunkColumn.getComponentType());
/*  356 */     Ref<ChunkStore> chunkSectionRef = (chunkColumnComponent != null) ? chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(targetBlockPos.y)) : null;
/*      */     
/*  358 */     if (targetBlockType.getGathering().shouldUseDefaultDropWhenPlaced()) {
/*  359 */       BlockPhysics decoBlocks = (chunkSectionRef != null) ? (BlockPhysics)chunkStore.getComponent(chunkSectionRef, BlockPhysics.getComponentType()) : null;
/*  360 */       boolean isDeco = (decoBlocks != null && decoBlocks.isDeco(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z));
/*  361 */       if (isDeco) {
/*  362 */         itemId = null;
/*  363 */         dropListId = null;
/*      */       } 
/*      */     } 
/*      */     
/*  367 */     TimeResource timeResource = (TimeResource)entityStore.getResource(TimeResource.getResourceType());
/*  368 */     BlockHealthChunk blockHealthComponent = (BlockHealthChunk)chunkStore.getComponent(chunkReference, BlockHealthModule.get().getBlockHealthChunkComponentType());
/*  369 */     assert blockHealthComponent != null;
/*      */     
/*  371 */     float current = blockHealthComponent.getBlockHealth(originBlock);
/*  372 */     DamageBlockEvent event = new DamageBlockEvent(itemStack, originBlock, targetBlockType, current, damage);
/*      */     
/*  374 */     if (ref != null) {
/*  375 */       entityStore.invoke(ref, (EcsEvent)event);
/*      */     } else {
/*  377 */       entityStore.invoke((EcsEvent)event);
/*      */     } 
/*      */ 
/*      */     
/*  381 */     if (event.isCancelled()) {
/*  382 */       targetSection.invalidateBlock(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  383 */       return false;
/*      */     } 
/*  385 */     float damage = event.getDamage();
/*      */ 
/*      */     
/*  388 */     targetBlockType = event.getBlockType();
/*  389 */     targetBlockPos = event.getTargetBlock();
/*  390 */     targetSection = blockChunkComponent.getSectionAtBlockY(targetBlockPos.y);
/*  391 */     targetRotationIndex = targetSection.getRotationIndex(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*  392 */     targetBlockType.getBlockCenter(targetRotationIndex, targetBlockCenterPos);
/*  393 */     targetBlockCenterPos.add(targetBlockPos);
/*      */     
/*  395 */     BlockHealth blockDamage = blockHealthComponent.damageBlock(timeResource.getNow(), world, targetBlockPos, damage);
/*      */     
/*  397 */     if (blockHealthComponent.isBlockFragile(targetBlockPos) || blockDamage.isDestroyed()) {
/*  398 */       BlockGathering.BlockToolData requiredTool = (BlockGathering.BlockToolData)blockGathering.getToolData().get(toolId);
/*  399 */       boolean toolsMatch = (requiredTool != null);
/*      */       
/*  401 */       if (!toolsMatch) {
/*  402 */         performBlockBreak(world, targetBlockPos, targetBlockType, itemStack, dropQuantity, itemId, dropListId, setBlockSettings, ref, chunkReference, entityStore, chunkStore);
/*  403 */         brokeBlock = true;
/*      */       } else {
/*      */         
/*  406 */         String toolStateId = requiredTool.getStateId();
/*  407 */         BlockType newBlockType = (toolStateId != null) ? targetBlockType.getBlockForState(toolStateId) : null;
/*  408 */         boolean shouldChangeState = (newBlockType != null && !targetBlockType.getId().equals(newBlockType.getId()));
/*      */         
/*  410 */         if (shouldChangeState) {
/*      */           
/*  412 */           blockDamage.setHealth(1.0F);
/*      */           
/*  414 */           worldChunkComponent.setBlock(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z, newBlockType);
/*      */           
/*  416 */           if ((setBlockSettings & 0x400) == 0) {
/*  417 */             BlockSoundSet soundSet = (BlockSoundSet)BlockSoundSet.getAssetMap().getAsset(targetBlockType.getBlockSoundSetIndex());
/*  418 */             if (soundSet != null) {
/*  419 */               int soundEventIndex = soundSet.getSoundEventIndices().getOrDefault(BlockSoundEvent.Break, 0);
/*  420 */               if (soundEventIndex != 0) {
/*  421 */                 SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, targetBlockCenterPos, entityStore);
/*      */               }
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/*  427 */           if ((setBlockSettings & 0x800) == 0) {
/*  428 */             List<ItemStack> itemStacks = getDrops(targetBlockType, 1, requiredTool.getItemId(), requiredTool.getDropListId());
/*  429 */             Vector3d dropPosition = new Vector3d(targetBlockPos.x + 0.5D, targetBlockPos.y, targetBlockPos.z + 0.5D);
/*  430 */             Holder[] arrayOfHolder = ItemComponent.generateItemDrops(entityStore, itemStacks, dropPosition, Vector3f.ZERO);
/*  431 */             entityStore.addEntities(arrayOfHolder, AddReason.SPAWN);
/*      */           }
/*      */         
/*      */         } else {
/*      */           
/*  436 */           performBlockBreak(world, targetBlockPos, targetBlockType, itemStack, dropQuantity, itemId, dropListId, setBlockSettings | 0x800, ref, chunkReference, entityStore, chunkStore);
/*  437 */           brokeBlock = true;
/*      */ 
/*      */           
/*  440 */           if ((setBlockSettings & 0x800) == 0) {
/*  441 */             List<ItemStack> toolDrops = getDrops(targetBlockType, 1, requiredTool.getItemId(), requiredTool.getDropListId());
/*  442 */             if (!toolDrops.isEmpty()) {
/*  443 */               Vector3d dropPosition = new Vector3d(targetBlockPos.x + 0.5D, targetBlockPos.y, targetBlockPos.z + 0.5D);
/*  444 */               Holder[] arrayOfHolder = ItemComponent.generateItemDrops(entityStore, toolDrops, dropPosition, Vector3f.ZERO);
/*  445 */               entityStore.addEntities(arrayOfHolder, AddReason.SPAWN);
/*      */             }
/*      */           
/*      */           }
/*      */         
/*      */         } 
/*      */       } 
/*  452 */     } else if ((setBlockSettings & 0x400) == 0) {
/*  453 */       BlockSoundSet soundSet = (BlockSoundSet)BlockSoundSet.getAssetMap().getAsset(targetBlockType.getBlockSoundSetIndex());
/*  454 */       if (soundSet != null) {
/*  455 */         int soundEventIndex = soundSet.getSoundEventIndices().getOrDefault(BlockSoundEvent.Hit, 0);
/*  456 */         if (soundEventIndex != 0) {
/*  457 */           SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, targetBlockCenterPos, entityStore);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  463 */     if (ref != null && entity != null) {
/*  464 */       if ((setBlockSettings & 0x400) == 0 && 
/*  465 */         !targetBlockCenterPos.equals(Vector3d.MAX)) {
/*  466 */         int hitSoundEventLayerIndex = 0;
/*  467 */         if (itemToolSpec != null) {
/*  468 */           hitSoundEventLayerIndex = itemToolSpec.getHitSoundLayerIndex();
/*      */         }
/*      */         
/*  471 */         if (hitSoundEventLayerIndex == 0 && heldItem != null && heldItem.getTool() != null) {
/*  472 */           hitSoundEventLayerIndex = heldItem.getTool().getHitSoundLayerIndex();
/*      */         }
/*      */         
/*  475 */         if (hitSoundEventLayerIndex != 0) {
/*  476 */           SoundUtil.playSoundEvent3d(ref, hitSoundEventLayerIndex, targetBlockCenterPos.getX(), targetBlockCenterPos.getY(), targetBlockCenterPos.getZ(), entityStore);
/*      */         }
/*      */       } 
/*      */ 
/*      */       
/*  481 */       if (itemToolSpec != null && itemToolSpec.isIncorrect()) {
/*  482 */         GatheringEffectsConfig incorrectToolConfig = gameplayConfig.getGatheringConfig().getIncorrectToolConfig();
/*  483 */         if (incorrectToolConfig != null) {
/*  484 */           if ((setBlockSettings & 0x4) == 0) {
/*  485 */             String particleSystemId = incorrectToolConfig.getParticleSystemId();
/*  486 */             if (particleSystemId != null) {
/*  487 */               ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/*  488 */               playerSpatialResource.getSpatialStructure().collect(targetBlockCenterPos, 75.0D, (List)results);
/*      */               
/*  490 */               ParticleUtil.spawnParticleEffect(particleSystemId, targetBlockCenterPos, (List)results, entityStore);
/*      */             } 
/*      */           } 
/*      */           
/*  494 */           if ((setBlockSettings & 0x400) == 0) {
/*  495 */             int soundEventIndex = incorrectToolConfig.getSoundEventIndex();
/*  496 */             if (soundEventIndex != 0) {
/*  497 */               SoundUtil.playSoundEvent3d(ref, soundEventIndex, targetBlockCenterPos, entityStore);
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  504 */     if (entity != null && ref != null && 
/*  505 */       entity.canDecreaseItemStackDurability(ref, entityStore) && itemStack != null && !itemStack.isUnbreakable()) {
/*  506 */       byte activeHotbarSlot = entity.getInventory().getActiveHotbarSlot();
/*  507 */       if (activeHotbarSlot != -1) {
/*  508 */         double durability = calculateDurabilityUse(heldItem, targetBlockType);
/*  509 */         ItemContainer hotbar = entity.getInventory().getHotbar();
/*  510 */         entity.updateItemStackDurability(ref, itemStack, hotbar, activeHotbarSlot, -durability, entityStore);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  515 */     return brokeBlock;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void performBlockBreak(@Nullable Ref<EntityStore> ref, @Nullable ItemStack heldItemStack, @Nonnull Vector3i targetBlock, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  534 */     performBlockBreak(ref, heldItemStack, targetBlock, 0, chunkReference, entityStore, chunkStore);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void performBlockBreak(@Nullable Ref<EntityStore> ref, @Nullable ItemStack heldItemStack, @Nonnull Vector3i targetBlock, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  555 */     World world = ((ChunkStore)chunkStore.getExternalData()).getWorld();
/*      */     
/*  557 */     int targetBlockX = targetBlock.getX();
/*  558 */     int targetBlockY = targetBlock.getY();
/*  559 */     int targetBlockZ = targetBlock.getZ();
/*      */     
/*  561 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkReference, WorldChunk.getComponentType());
/*  562 */     assert worldChunkComponent != null;
/*      */     
/*  564 */     int targetBlockTypeIndex = worldChunkComponent.getBlock(targetBlockX, targetBlockY, targetBlockZ);
/*  565 */     BlockType targetBlockTypeAsset = (BlockType)BlockType.getAssetMap().getAsset(targetBlockTypeIndex);
/*  566 */     if (targetBlockTypeAsset == null)
/*      */       return; 
/*  568 */     Vector3i affectedBlock = targetBlock;
/*  569 */     if (!targetBlockTypeAsset.isUnknown()) {
/*  570 */       BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  571 */       assert blockChunkComponent != null;
/*      */       
/*  573 */       BlockSection targetBlockSection = blockChunkComponent.getSectionAtBlockY(targetBlockY);
/*  574 */       int filler = targetBlockSection.getFiller(targetBlockX, targetBlockY, targetBlockZ);
/*      */       
/*  576 */       int fillerX = FillerBlockUtil.unpackX(filler);
/*  577 */       int fillerY = FillerBlockUtil.unpackY(filler);
/*  578 */       int fillerZ = FillerBlockUtil.unpackZ(filler);
/*      */       
/*  580 */       if (fillerX != 0 || fillerY != 0 || fillerZ != 0) {
/*      */         
/*  582 */         affectedBlock = affectedBlock.clone().subtract(fillerX, fillerY, fillerZ);
/*      */         
/*  584 */         BlockType originBlock = world.getBlockType(affectedBlock);
/*  585 */         if (originBlock != null && !targetBlockTypeAsset.getId().equals(originBlock.getId())) {
/*  586 */           world.breakBlock(targetBlockX, targetBlockY, targetBlockZ, setBlockSettings);
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/*  592 */     performBlockBreak(world, affectedBlock, targetBlockTypeAsset, heldItemStack, 0, null, null, setBlockSettings, ref, chunkReference, entityStore, chunkStore);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void performBlockBreak(@Nonnull World world, @Nonnull Vector3i blockPosition, @Nonnull BlockType targetBlockTypeKey, @Nullable ItemStack heldItemStack, int dropQuantity, @Nullable String dropItemId, @Nullable String dropListId, int setBlockSettings, @Nullable Ref<EntityStore> ref, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*      */     Store store;
/*  623 */     World targetWorld = world;
/*  624 */     Vector3i targetBlockPosition = blockPosition;
/*  625 */     Ref<ChunkStore> targetChunkReference = chunkReference;
/*  626 */     ComponentAccessor<ChunkStore> targetChunkStore = chunkStore;
/*      */     
/*  628 */     if (ref != null) {
/*  629 */       BreakBlockEvent event = new BreakBlockEvent(heldItemStack, targetBlockPosition, targetBlockTypeKey);
/*  630 */       entityStore.invoke(ref, (EcsEvent)event);
/*      */       
/*  632 */       if (event.isCancelled()) {
/*  633 */         BlockChunk blockChunk = (BlockChunk)chunkStore.getComponent(targetChunkReference, BlockChunk.getComponentType());
/*  634 */         assert blockChunk != null;
/*      */         
/*  636 */         BlockSection blockSection1 = blockChunk.getSectionAtBlockY(targetBlockPosition.getY());
/*  637 */         blockSection1.invalidateBlock(targetBlockPosition.getX(), targetBlockPosition.getY(), targetBlockPosition.getZ());
/*      */         
/*      */         return;
/*      */       } 
/*  641 */       targetBlockPosition = event.getTargetBlock();
/*  642 */       store = targetWorld.getChunkStore().getStore();
/*      */       
/*  644 */       long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlockPosition.x, targetBlockPosition.z);
/*  645 */       targetChunkReference = ((ChunkStore)store.getExternalData()).getChunkReference(chunkIndex);
/*  646 */       if (targetChunkReference == null || !targetChunkReference.isValid()) {
/*      */         return;
/*      */       }
/*      */     } 
/*  650 */     if (!targetBlockPosition.equals(blockPosition) || !targetWorld.equals(world)) {
/*      */ 
/*      */       
/*  653 */       BlockChunk blockChunk = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  654 */       assert blockChunk != null;
/*      */       
/*  656 */       BlockSection blockSection1 = blockChunk.getSectionAtBlockY(blockPosition.getY());
/*  657 */       blockSection1.invalidateBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
/*      */     } 
/*      */     
/*  660 */     int x = blockPosition.getX();
/*  661 */     int y = blockPosition.getY();
/*  662 */     int z = blockPosition.getZ();
/*      */     
/*  664 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(targetChunkReference, BlockChunk.getComponentType());
/*  665 */     assert blockChunkComponent != null;
/*      */     
/*  667 */     BlockSection blockSection = blockChunkComponent.getSectionAtBlockY(y);
/*  668 */     int filler = blockSection.getFiller(x, y, z);
/*      */     
/*  670 */     int blockTypeIndex = blockSection.get(x, y, z);
/*  671 */     BlockType blockTypeAsset = (BlockType)BlockType.getAssetMap().getAsset(blockTypeIndex);
/*      */ 
/*      */     
/*  674 */     boolean isNaturalBlockBreak = BlockInteractionUtils.isNaturalAction(ref, entityStore);
/*  675 */     setBlockSettings |= 0x100;
/*      */     
/*  677 */     if (!isNaturalBlockBreak) {
/*  678 */       setBlockSettings |= 0x800;
/*      */     }
/*      */     
/*  681 */     naturallyRemoveBlock(targetBlockPosition, blockTypeAsset, filler, dropQuantity, dropItemId, dropListId, setBlockSettings, targetChunkReference, entityStore, (ComponentAccessor<ChunkStore>)store);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public static void naturallyRemoveBlockByPhysics(@Nonnull Vector3i blockPosition, @Nonnull BlockType blockType, int filler, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  713 */     int quantity = 1;
/*  714 */     String itemId = null;
/*  715 */     String dropListId = null;
/*      */ 
/*      */     
/*  718 */     BlockGathering blockGathering = blockType.getGathering();
/*  719 */     if (blockGathering != null) {
/*  720 */       PhysicsDropType physics = blockGathering.getPhysics();
/*  721 */       BlockBreakingDropType breaking = blockGathering.getBreaking();
/*  722 */       SoftBlockDropType soft = blockGathering.getSoft();
/*  723 */       HarvestingDropType harvest = blockGathering.getHarvest();
/*      */       
/*  725 */       if (physics != null) {
/*  726 */         itemId = physics.getItemId();
/*  727 */         dropListId = physics.getDropListId();
/*  728 */       } else if (breaking != null) {
/*  729 */         quantity = breaking.getQuantity();
/*  730 */         itemId = breaking.getItemId();
/*  731 */         dropListId = breaking.getDropListId();
/*  732 */       } else if (soft != null) {
/*  733 */         itemId = soft.getItemId();
/*  734 */         dropListId = soft.getDropListId();
/*  735 */       } else if (harvest != null) {
/*  736 */         itemId = harvest.getItemId();
/*  737 */         dropListId = harvest.getDropListId();
/*      */       } 
/*      */     } 
/*      */     
/*  741 */     setBlockSettings |= 0x20;
/*  742 */     naturallyRemoveBlock(blockPosition, blockType, filler, quantity, itemId, dropListId, setBlockSettings, chunkReference, entityStore, chunkStore);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void naturallyRemoveBlock(@Nonnull Vector3i blockPosition, @Nullable BlockType blockType, int filler, int quantity, String itemId, String dropListId, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  769 */     if (blockType == null)
/*      */       return; 
/*  771 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkReference, WorldChunk.getComponentType());
/*  772 */     assert worldChunkComponent != null;
/*      */     
/*  774 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  775 */     assert blockChunkComponent != null;
/*      */     
/*  777 */     Vector3i affectedBlock = blockPosition;
/*  778 */     if (!blockType.isUnknown()) {
/*  779 */       int fillerX = FillerBlockUtil.unpackX(filler);
/*  780 */       int fillerY = FillerBlockUtil.unpackY(filler);
/*  781 */       int fillerZ = FillerBlockUtil.unpackZ(filler);
/*      */ 
/*      */       
/*  784 */       if (fillerX != 0 || fillerY != 0 || fillerZ != 0) {
/*      */         
/*  786 */         affectedBlock = affectedBlock.clone().subtract(fillerX, fillerY, fillerZ);
/*      */         
/*  788 */         String oldBlockTypeKey = blockType.getId();
/*  789 */         blockType = worldChunkComponent.getBlockType(affectedBlock.getX(), affectedBlock.getY(), affectedBlock.getZ());
/*  790 */         if (blockType == null) {
/*  791 */           throw new IllegalStateException("Null block type fetched for " + String.valueOf(affectedBlock) + " during block break");
/*      */         }
/*      */         
/*  794 */         if (!oldBlockTypeKey.equals(blockType.getId())) {
/*  795 */           worldChunkComponent.breakBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), setBlockSettings);
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  802 */     if ((setBlockSettings & 0x400) == 0) {
/*  803 */       BlockSoundSet soundSet = (BlockSoundSet)BlockSoundSet.getAssetMap().getAsset(blockType.getBlockSoundSetIndex());
/*      */       
/*  805 */       if (soundSet != null) {
/*  806 */         int soundEventIndex = soundSet.getSoundEventIndices().getOrDefault(BlockSoundEvent.Break, 0);
/*      */         
/*  808 */         if (soundEventIndex != 0) {
/*  809 */           BlockSection section = blockChunkComponent.getSectionAtBlockY(blockPosition.getY());
/*  810 */           int rotationIndex = section.getRotationIndex(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
/*  811 */           Vector3d centerPosition = new Vector3d();
/*  812 */           blockType.getBlockCenter(rotationIndex, centerPosition);
/*  813 */           centerPosition.add(blockPosition);
/*      */           
/*  815 */           SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, centerPosition, entityStore);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  820 */     removeBlock(affectedBlock, blockType, setBlockSettings, chunkReference, chunkStore);
/*      */ 
/*      */     
/*  823 */     if ((setBlockSettings & 0x800) == 0 && quantity > 0) {
/*  824 */       Vector3d dropPosition = blockPosition.toVector3d().add(0.5D, 0.0D, 0.5D);
/*  825 */       List<ItemStack> itemStacks = getDrops(blockType, quantity, itemId, dropListId);
/*  826 */       Holder[] arrayOfHolder = ItemComponent.generateItemDrops(entityStore, itemStacks, dropPosition, Vector3f.ZERO);
/*  827 */       entityStore.addEntities(arrayOfHolder, AddReason.SPAWN);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean shouldPickupByInteraction(@Nullable BlockType blockType) {
/*  838 */     return (blockType != null && blockType.getGathering() != null && blockType.getGathering().isHarvestable());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void performPickupByInteraction(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3i targetBlock, @Nonnull BlockType blockType, int filler, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<EntityStore> entityStore, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  859 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkReference, WorldChunk.getComponentType());
/*  860 */     assert worldChunkComponent != null;
/*      */     
/*  862 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  863 */     assert blockChunkComponent != null;
/*      */     
/*  865 */     Vector3i affectedBlock = targetBlock;
/*      */     
/*  867 */     if (!blockType.isUnknown()) {
/*  868 */       int fillerX = FillerBlockUtil.unpackX(filler);
/*  869 */       int fillerY = FillerBlockUtil.unpackY(filler);
/*  870 */       int fillerZ = FillerBlockUtil.unpackZ(filler);
/*      */       
/*  872 */       if (fillerX != 0 || fillerY != 0 || fillerZ != 0) {
/*      */         
/*  874 */         affectedBlock = affectedBlock.clone().subtract(fillerX, fillerY, fillerZ);
/*      */         
/*  876 */         String oldBlockTypeKey = blockType.getId();
/*  877 */         blockType = worldChunkComponent.getBlockType(affectedBlock.getX(), affectedBlock.getY(), affectedBlock.getZ());
/*  878 */         if (blockType == null)
/*      */           return; 
/*  880 */         if (!oldBlockTypeKey.equals(blockType.getId())) {
/*  881 */           worldChunkComponent.breakBlock(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
/*      */           
/*      */           return;
/*      */         } 
/*      */       } 
/*      */     } 
/*  887 */     BlockSection section = blockChunkComponent.getSectionAtBlockY(targetBlock.getY());
/*  888 */     Vector3d centerPosition = new Vector3d();
/*  889 */     int rotationIndex = section.getRotationIndex(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());
/*  890 */     blockType.getBlockCenter(rotationIndex, centerPosition);
/*  891 */     centerPosition.add(targetBlock);
/*      */     
/*  893 */     int setBlockSettings = 0;
/*  894 */     setBlockSettings |= 0x100;
/*      */     
/*  896 */     if (!BlockInteractionUtils.isNaturalAction(ref, entityStore)) {
/*  897 */       setBlockSettings |= 0x800;
/*      */     }
/*      */     
/*  900 */     removeBlock(affectedBlock, blockType, setBlockSettings, chunkReference, chunkStore);
/*      */     
/*  902 */     HarvestingDropType harvest = blockType.getGathering().getHarvest();
/*  903 */     String itemId = harvest.getItemId();
/*  904 */     String dropListId = harvest.getDropListId();
/*  905 */     for (ItemStack itemStack : getDrops(blockType, 1, itemId, dropListId)) {
/*  906 */       ItemUtils.interactivelyPickupItem(ref, itemStack, centerPosition, entityStore);
/*      */     }
/*      */ 
/*      */     
/*  910 */     if ((setBlockSettings & 0x400) == 0) {
/*  911 */       BlockSoundSet soundSet = (BlockSoundSet)BlockSoundSet.getAssetMap().getAsset(blockType.getBlockSoundSetIndex());
/*  912 */       if (soundSet != null) {
/*  913 */         int soundEventIndex = soundSet.getSoundEventIndices().getOrDefault(BlockSoundEvent.Harvest, 0);
/*  914 */         if (soundEventIndex != 0) {
/*  915 */           SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, centerPosition, entityStore);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected static void removeBlock(@Nonnull Vector3i blockPosition, @Nonnull BlockType blockType, int setBlockSettings, @Nonnull Ref<ChunkStore> chunkReference, @Nonnull ComponentAccessor<ChunkStore> chunkStore) {
/*  935 */     World world = ((ChunkStore)chunkStore.getExternalData()).getWorld();
/*      */     
/*  937 */     ComponentType<ChunkStore, BlockHealthChunk> blockHealthComponentType = BlockHealthModule.get().getBlockHealthChunkComponentType();
/*  938 */     BlockHealthChunk blockHealthComponent = (BlockHealthChunk)chunkStore.getComponent(chunkReference, blockHealthComponentType);
/*  939 */     assert blockHealthComponent != null;
/*      */     
/*  941 */     blockHealthComponent.removeBlock(world, blockPosition);
/*      */     
/*  943 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkReference, WorldChunk.getComponentType());
/*  944 */     assert worldChunkComponent != null;
/*      */     
/*  946 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*  947 */     assert blockChunkComponent != null;
/*      */     
/*  949 */     worldChunkComponent.breakBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), setBlockSettings);
/*      */     
/*  951 */     if ((setBlockSettings & 0x100) != 0) {
/*  952 */       BlockSection section = blockChunkComponent.getSectionAtBlockY(blockPosition.y);
/*  953 */       int rotationIndex = section.getRotationIndex(blockPosition.x, blockPosition.y, blockPosition.z);
/*  954 */       BlockBoundingBoxes hitBoxType = (BlockBoundingBoxes)BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
/*  955 */       if (hitBoxType != null) {
/*  956 */         FillerBlockUtil.forEachFillerBlock(hitBoxType.get(rotationIndex), (x, y, z) -> world.performBlockUpdate(blockPosition.getX() + x, blockPosition.getY() + y, blockPosition.getZ() + z, false));
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  961 */     ConnectedBlocksUtil.setConnectedBlockAndNotifyNeighbors(
/*  962 */         BlockType.getAssetMap().getIndex("Empty"), RotationTuple.NONE, Vector3i.ZERO, blockPosition, worldChunkComponent, blockChunkComponent);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static List<ItemStack> getDrops(@Nonnull BlockType blockType, int quantity, @Nullable String itemId, @Nullable String dropListId) {
/*  981 */     if (dropListId == null && itemId == null) {
/*  982 */       Item item = blockType.getItem();
/*  983 */       if (item == null) return (List<ItemStack>)ObjectLists.emptyList(); 
/*  984 */       return (List<ItemStack>)ObjectLists.singleton(new ItemStack(item.getId(), quantity));
/*      */     } 
/*      */     
/*  987 */     ObjectArrayList<ItemStack> objectArrayList = new ObjectArrayList();
/*      */     
/*  989 */     if (dropListId != null) {
/*  990 */       ItemModule itemModule = ItemModule.get();
/*      */       
/*  992 */       if (itemModule.isEnabled()) {
/*  993 */         for (int i = 0; i < quantity; i++) {
/*  994 */           List<ItemStack> randomItemsToDrop = itemModule.getRandomItemDrops(dropListId);
/*  995 */           objectArrayList.addAll(randomItemsToDrop);
/*      */         } 
/*      */       }
/*      */     } 
/*      */     
/* 1000 */     if (itemId != null) {
/* 1001 */       objectArrayList.add(new ItemStack(itemId, quantity));
/*      */     }
/*      */     
/* 1004 */     return (List<ItemStack>)objectArrayList;
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\BlockHarvestUtils.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */