/*     */ package com.hypixel.hytale.builtin.adventure.farming.interactions;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
/*     */ import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
/*     */ import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validator;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
/*     */ import java.util.function.Supplier;
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
/*     */ public class UseCaptureCrateInteraction
/*     */   extends SimpleBlockInteraction
/*     */ {
/*     */   public static final BuilderCodec<UseCaptureCrateInteraction> CODEC;
/*     */   protected String[] acceptedNpcGroupIds;
/*     */   protected int[] acceptedNpcGroupIndexes;
/*     */   protected String fullIcon;
/*     */   
/*     */   static {
/*  65 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(UseCaptureCrateInteraction.class, UseCaptureCrateInteraction::new, SimpleInteraction.CODEC).appendInherited(new KeyedCodec("AcceptedNpcGroups", NPCGroup.CHILD_ASSET_CODEC_ARRAY), (o, v) -> o.acceptedNpcGroupIds = v, o -> o.acceptedNpcGroupIds, (o, p) -> o.acceptedNpcGroupIds = p.acceptedNpcGroupIds).addValidator((Validator)NPCGroup.VALIDATOR_CACHE.getArrayValidator()).add()).appendInherited(new KeyedCodec("FullIcon", (Codec)Codec.STRING), (o, v) -> o.fullIcon = v, o -> o.fullIcon, (o, p) -> o.fullIcon = p.fullIcon).add()).afterDecode(captureData -> { if (captureData.acceptedNpcGroupIds != null) { captureData.acceptedNpcGroupIndexes = new int[captureData.acceptedNpcGroupIds.length]; for (int i = 0; i < captureData.acceptedNpcGroupIds.length; i++) { int assetIdx = NPCGroup.getAssetMap().getIndex(captureData.acceptedNpcGroupIds[i]); captureData.acceptedNpcGroupIndexes[i] = assetIdx; }  }  })).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  73 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/*     */     
/*  75 */     if (commandBuffer == null) {
/*  76 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/*  80 */     ItemStack item = context.getHeldItem();
/*  81 */     if (item == null) {
/*  82 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/*  86 */     Ref<EntityStore> playerRef = context.getEntity();
/*  87 */     LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, (ComponentAccessor)commandBuffer);
/*  88 */     Inventory playerInventory = playerEntity.getInventory();
/*  89 */     byte activeHotbarSlot = playerInventory.getActiveHotbarSlot();
/*  90 */     ItemStack inHandItemStack = playerInventory.getActiveHotbarItem();
/*     */ 
/*     */     
/*  93 */     CapturedNPCMetadata existingMeta = (CapturedNPCMetadata)item.getFromMetadataOrNull("CapturedEntity", (Codec)CapturedNPCMetadata.CODEC);
/*     */     
/*  95 */     if (existingMeta == null) {
/*  96 */       Ref<EntityStore> targetEntity = context.getTargetEntity();
/*  97 */       if (targetEntity == null) {
/*  98 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 102 */       NPCEntity npc = (NPCEntity)commandBuffer.getComponent(targetEntity, NPCEntity.getComponentType());
/* 103 */       if (npc == null) {
/* 104 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 109 */       TagSetPlugin.TagSetLookup tagSetPlugin = TagSetPlugin.get(NPCGroup.class);
/* 110 */       boolean tagFound = false;
/* 111 */       for (int group : this.acceptedNpcGroupIndexes) {
/* 112 */         if (tagSetPlugin.tagInSet(group, npc.getRoleIndex())) {
/* 113 */           tagFound = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 118 */       if (!tagFound) {
/* 119 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 123 */       PersistentModel persistentModel = (PersistentModel)commandBuffer.getComponent(targetEntity, PersistentModel.getComponentType());
/* 124 */       if (persistentModel == null) {
/* 125 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 129 */       ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(persistentModel.getModelReference().getModelAssetId());
/* 130 */       CapturedNPCMetadata meta = (CapturedNPCMetadata)inHandItemStack.getFromMetadataOrDefault("CapturedEntity", CapturedNPCMetadata.CODEC);
/*     */       
/* 132 */       if (modelAsset != null) {
/* 133 */         meta.setIconPath(modelAsset.getIcon());
/*     */       }
/* 135 */       meta.setRoleIndex(npc.getRoleIndex());
/*     */ 
/*     */       
/* 138 */       String npcName = NPCPlugin.get().getName(npc.getRoleIndex());
/* 139 */       if (npcName != null) {
/* 140 */         meta.setNpcNameKey(npcName);
/*     */       }
/*     */ 
/*     */       
/* 144 */       if (this.fullIcon != null) {
/* 145 */         meta.setFullItemIcon(this.fullIcon);
/*     */       }
/*     */       
/* 148 */       ItemStack itemWithNPC = inHandItemStack.withMetadata(CapturedNPCMetadata.KEYED_CODEC, meta);
/* 149 */       playerInventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, itemWithNPC);
/*     */       
/* 151 */       commandBuffer.removeEntity(targetEntity, RemoveReason.REMOVE);
/*     */       
/*     */       return;
/*     */     } 
/* 155 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
/* 160 */     ItemStack item = context.getHeldItem();
/* 161 */     if (item == null) {
/* 162 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 166 */     Ref<EntityStore> playerRef = context.getEntity();
/* 167 */     LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, (ComponentAccessor)commandBuffer);
/* 168 */     Inventory playerInventory = playerEntity.getInventory();
/* 169 */     byte activeHotbarSlot = playerInventory.getActiveHotbarSlot();
/*     */ 
/*     */     
/* 172 */     CapturedNPCMetadata existingMeta = (CapturedNPCMetadata)item.getFromMetadataOrNull("CapturedEntity", (Codec)CapturedNPCMetadata.CODEC);
/* 173 */     if (existingMeta == null) {
/* 174 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 178 */     BlockPosition pos = context.getTargetBlock();
/* 179 */     if (pos == null) {
/* 180 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 185 */     WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
/* 186 */     Ref<ChunkStore> blockRef = worldChunk.getBlockComponentEntity(pos.x, pos.y, pos.z);
/* 187 */     if (blockRef == null) {
/* 188 */       blockRef = BlockModule.ensureBlockEntity(worldChunk, pos.x, pos.y, pos.z);
/*     */     }
/*     */     
/* 191 */     ItemStack noMetaItemStack = item.withMetadata(null);
/*     */     
/* 193 */     if (blockRef != null) {
/* 194 */       Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*     */       
/* 196 */       CoopBlock coopBlockState = (CoopBlock)chunkStore.getComponent(blockRef, CoopBlock.getComponentType());
/* 197 */       if (coopBlockState != null) {
/* 198 */         WorldTimeResource worldTimeResource = (WorldTimeResource)commandBuffer.getResource(WorldTimeResource.getResourceType());
/* 199 */         if (coopBlockState.tryPutResident(existingMeta, worldTimeResource)) {
/* 200 */           world.execute(() -> coopBlockState.ensureSpawnResidentsInWorld(world, world.getEntityStore().getStore(), new Vector3d(pos.x, pos.y, pos.z), (new Vector3d()).assign(Vector3d.FORWARD)));
/* 201 */           playerInventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, noMetaItemStack);
/*     */         } else {
/* 203 */           (context.getState()).state = InteractionState.Failed;
/*     */         } 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 211 */     Vector3d spawnPos = new Vector3d((pos.x + 0.5F), pos.y, (pos.z + 0.5F));
/*     */     
/* 213 */     if (context.getClientState() != null) {
/* 214 */       BlockFace blockFace = BlockFace.fromProtocolFace((context.getClientState()).blockFace);
/* 215 */       if (blockFace != null) {
/* 216 */         spawnPos.add(blockFace.getDirection());
/*     */       }
/*     */     } 
/* 219 */     NPCPlugin npcModule = NPCPlugin.get();
/* 220 */     Store<EntityStore> store = context.getCommandBuffer().getStore();
/* 221 */     int roleIndex = existingMeta.getRoleIndex();
/*     */     
/* 223 */     commandBuffer.run(_store -> npcModule.spawnEntity(store, roleIndex, spawnPos, Vector3f.ZERO, null, null));
/*     */ 
/*     */ 
/*     */     
/* 227 */     playerInventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, noMetaItemStack);
/*     */   }
/*     */   
/*     */   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\interactions\UseCaptureCrateInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */