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
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
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
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*     */     LivingEntity livingEntity;
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
/*  86 */     Ref<EntityStore> ref = context.getEntity();
/*  87 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*  88 */     else { (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return; }
/*     */     
/*  92 */     Inventory inventory = livingEntity.getInventory();
/*  93 */     byte activeHotbarSlot = inventory.getActiveHotbarSlot();
/*  94 */     ItemStack inHandItemStack = inventory.getActiveHotbarItem();
/*     */ 
/*     */     
/*  97 */     CapturedNPCMetadata existingMeta = (CapturedNPCMetadata)item.getFromMetadataOrNull("CapturedEntity", (Codec)CapturedNPCMetadata.CODEC);
/*     */     
/*  99 */     if (existingMeta == null) {
/* 100 */       Ref<EntityStore> targetEntity = context.getTargetEntity();
/* 101 */       if (targetEntity == null) {
/* 102 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 106 */       NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(targetEntity, NPCEntity.getComponentType());
/* 107 */       if (npcComponent == null) {
/* 108 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 113 */       TagSetPlugin.TagSetLookup tagSetPlugin = TagSetPlugin.get(NPCGroup.class);
/* 114 */       boolean tagFound = false;
/* 115 */       for (int group : this.acceptedNpcGroupIndexes) {
/* 116 */         if (tagSetPlugin.tagInSet(group, npcComponent.getRoleIndex())) {
/* 117 */           tagFound = true;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 122 */       if (!tagFound) {
/* 123 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 127 */       PersistentModel persistentModel = (PersistentModel)commandBuffer.getComponent(targetEntity, PersistentModel.getComponentType());
/* 128 */       if (persistentModel == null) {
/* 129 */         (context.getState()).state = InteractionState.Failed;
/*     */         
/*     */         return;
/*     */       } 
/* 133 */       ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(persistentModel.getModelReference().getModelAssetId());
/* 134 */       CapturedNPCMetadata meta = (CapturedNPCMetadata)inHandItemStack.getFromMetadataOrDefault("CapturedEntity", CapturedNPCMetadata.CODEC);
/*     */       
/* 136 */       if (modelAsset != null) {
/* 137 */         meta.setIconPath(modelAsset.getIcon());
/*     */       }
/* 139 */       meta.setRoleIndex(npcComponent.getRoleIndex());
/*     */ 
/*     */       
/* 142 */       String npcName = NPCPlugin.get().getName(npcComponent.getRoleIndex());
/* 143 */       if (npcName != null) {
/* 144 */         meta.setNpcNameKey(npcName);
/*     */       }
/*     */ 
/*     */       
/* 148 */       if (this.fullIcon != null) {
/* 149 */         meta.setFullItemIcon(this.fullIcon);
/*     */       }
/*     */       
/* 152 */       ItemStack itemWithNPC = inHandItemStack.withMetadata(CapturedNPCMetadata.KEYED_CODEC, meta);
/* 153 */       inventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, itemWithNPC);
/*     */       
/* 155 */       commandBuffer.removeEntity(targetEntity, RemoveReason.REMOVE);
/*     */       
/*     */       return;
/*     */     } 
/* 159 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */   
/*     */   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
/*     */     LivingEntity livingEntity;
/* 164 */     ItemStack item = context.getHeldItem();
/* 165 */     if (item == null) {
/* 166 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 170 */     Ref<EntityStore> ref = context.getEntity();
/* 171 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/* 172 */     else { (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return; }
/*     */     
/* 176 */     Inventory inventory = livingEntity.getInventory();
/* 177 */     byte activeHotbarSlot = inventory.getActiveHotbarSlot();
/*     */ 
/*     */     
/* 180 */     CapturedNPCMetadata existingMeta = (CapturedNPCMetadata)item.getFromMetadataOrNull("CapturedEntity", (Codec)CapturedNPCMetadata.CODEC);
/* 181 */     if (existingMeta == null) {
/* 182 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 186 */     BlockPosition pos = context.getTargetBlock();
/* 187 */     if (pos == null) {
/* 188 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 193 */     WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
/* 194 */     Ref<ChunkStore> blockRef = worldChunk.getBlockComponentEntity(pos.x, pos.y, pos.z);
/* 195 */     if (blockRef == null) {
/* 196 */       blockRef = BlockModule.ensureBlockEntity(worldChunk, pos.x, pos.y, pos.z);
/*     */     }
/*     */     
/* 199 */     ItemStack noMetaItemStack = item.withMetadata(null);
/*     */     
/* 201 */     if (blockRef != null) {
/* 202 */       Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*     */       
/* 204 */       CoopBlock coopBlockState = (CoopBlock)chunkStore.getComponent(blockRef, CoopBlock.getComponentType());
/* 205 */       if (coopBlockState != null) {
/* 206 */         WorldTimeResource worldTimeResource = (WorldTimeResource)commandBuffer.getResource(WorldTimeResource.getResourceType());
/* 207 */         if (coopBlockState.tryPutResident(existingMeta, worldTimeResource)) {
/* 208 */           world.execute(() -> coopBlockState.ensureSpawnResidentsInWorld(world, world.getEntityStore().getStore(), new Vector3d(pos.x, pos.y, pos.z), (new Vector3d()).assign(Vector3d.FORWARD)));
/* 209 */           inventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, noMetaItemStack);
/*     */         } else {
/* 211 */           (context.getState()).state = InteractionState.Failed;
/*     */         } 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 219 */     Vector3d spawnPos = new Vector3d((pos.x + 0.5F), pos.y, (pos.z + 0.5F));
/*     */     
/* 221 */     if (context.getClientState() != null) {
/* 222 */       BlockFace blockFace = BlockFace.fromProtocolFace((context.getClientState()).blockFace);
/* 223 */       if (blockFace != null) {
/* 224 */         spawnPos.add(blockFace.getDirection());
/*     */       }
/*     */     } 
/*     */     
/* 228 */     NPCPlugin npcModule = NPCPlugin.get();
/* 229 */     Store<EntityStore> store = context.getCommandBuffer().getStore();
/* 230 */     int roleIndex = existingMeta.getRoleIndex();
/*     */     
/* 232 */     commandBuffer.run(_store -> npcModule.spawnEntity(store, roleIndex, spawnPos, Vector3f.ZERO, null, null));
/*     */ 
/*     */ 
/*     */     
/* 236 */     inventory.getHotbar().replaceItemStackInSlot((short)activeHotbarSlot, item, noMetaItemStack);
/*     */   }
/*     */   
/*     */   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\interactions\UseCaptureCrateInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */