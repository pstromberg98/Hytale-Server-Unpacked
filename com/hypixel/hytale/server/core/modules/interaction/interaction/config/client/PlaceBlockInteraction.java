/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.BlockRotation;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.Rotation;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
/*     */ public class PlaceBlockInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   public static final int MAX_ADVENTURE_PLACEMENT_RANGE_SQUARED = 36;
/*     */   @Nonnull
/*     */   public static final BuilderCodec<PlaceBlockInteraction> CODEC;
/*     */   @Nullable
/*     */   protected String blockTypeKey;
/*     */   
/*     */   static {
/*  67 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlaceBlockInteraction.class, PlaceBlockInteraction::new, SimpleInteraction.CODEC).documentation("Places the current or given block.")).append(new KeyedCodec("BlockTypeToPlace", (Codec)Codec.STRING), (placeBlockInteraction, blockTypeKey) -> placeBlockInteraction.blockTypeKey = blockTypeKey, placeBlockInteraction -> placeBlockInteraction.blockTypeKey).addValidatorLate(() -> BlockType.VALIDATOR_CACHE.getValidator().late()).documentation("Overrides the placed block type of the held item with the provided block type.").add()).append(new KeyedCodec("RemoveItemInHand", (Codec)Codec.BOOLEAN), (placeBlockInteraction, aBoolean) -> placeBlockInteraction.removeItemInHand = aBoolean.booleanValue(), placeBlockInteraction -> Boolean.valueOf(placeBlockInteraction.removeItemInHand)).documentation("Determines whether to remove the item that is in the instigating entities hand.").add()).appendInherited(new KeyedCodec("AllowDragPlacement", (Codec)Codec.BOOLEAN), (placeBlockInteraction, aBoolean) -> placeBlockInteraction.allowDragPlacement = aBoolean.booleanValue(), placeBlockInteraction -> Boolean.valueOf(placeBlockInteraction.allowDragPlacement), (placeBlockInteraction, parent) -> placeBlockInteraction.allowDragPlacement = parent.allowDragPlacement).documentation("If drag placement should be used when click is held for this interaction.").add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean removeItemInHand = true;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean allowDragPlacement = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  88 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected final void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  94 */     InteractionSyncData clientState = context.getClientState();
/*  95 */     assert clientState != null;
/*     */     
/*  97 */     if (!firstRun) {
/*  98 */       (context.getState()).state = clientState.state;
/*     */       
/*     */       return;
/*     */     } 
/* 102 */     Ref<EntityStore> ref = context.getEntity();
/* 103 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 104 */     assert commandBuffer != null;
/*     */ 
/*     */ 
/*     */     
/* 108 */     BlockPosition blockPosition = clientState.blockPosition;
/* 109 */     BlockRotation blockRotation = clientState.blockRotation;
/*     */ 
/*     */     
/* 112 */     if (blockPosition != null && blockRotation != null) {
/* 113 */       World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*     */       
/* 115 */       Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/* 116 */       long chunkIndex = ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z);
/* 117 */       Ref<ChunkStore> chunkReference = ((ChunkStore)chunkStore.getExternalData()).getChunkReference(chunkIndex);
/*     */ 
/*     */       
/* 120 */       if (chunkReference == null || !chunkReference.isValid()) {
/*     */         return;
/*     */       }
/* 123 */       ItemStack heldItemStack = context.getHeldItem();
/* 124 */       if (heldItemStack == null) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 129 */       ItemContainer heldItemContainer = context.getHeldItemContainer();
/* 130 */       if (heldItemContainer == null) {
/*     */         return;
/*     */       }
/* 133 */       TransformComponent transformComponent = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 134 */       Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/*     */       
/* 136 */       if (transformComponent != null && playerComponent != null && playerComponent.getGameMode() != GameMode.Creative) {
/* 137 */         Vector3d position = transformComponent.getPosition();
/* 138 */         Vector3d blockCenter = new Vector3d(blockPosition.x + 0.5D, blockPosition.y + 0.5D, blockPosition.z + 0.5D);
/*     */         
/* 140 */         if (position.distanceSquaredTo(blockCenter) > 36.0D) {
/*     */           return;
/*     */         }
/*     */       } 
/*     */       
/* 145 */       Inventory inventory = null;
/* 146 */       Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)entity;
/* 147 */         inventory = livingEntity.getInventory(); }
/*     */ 
/*     */ 
/*     */       
/* 151 */       Vector3i targetBlockPosition = new Vector3i(blockPosition.x, blockPosition.y, blockPosition.z);
/*     */ 
/*     */       
/* 154 */       String interactionBlockTypeKey = (this.blockTypeKey != null) ? this.blockTypeKey : heldItemStack.getBlockKey();
/* 155 */       if (interactionBlockTypeKey == null)
/* 156 */         return;  BlockType interactionBlockType = (BlockType)BlockType.getAssetMap().getAsset(interactionBlockTypeKey);
/*     */       
/* 158 */       int clientPlacedBlockId = clientState.placedBlockId;
/* 159 */       String clientPlacedBlockTypeKey = (clientPlacedBlockId == -1) ? null : ((BlockType)BlockType.getAssetMap().getAsset(clientPlacedBlockId)).getId();
/*     */ 
/*     */       
/* 162 */       if (clientPlacedBlockTypeKey != null && !clientPlacedBlockTypeKey.equals(this.blockTypeKey) && (interactionBlockType == null || !BlockPlaceUtils.canPlaceBlock(interactionBlockType, clientPlacedBlockTypeKey))) {
/* 163 */         clientPlacedBlockTypeKey = null;
/*     */       }
/*     */ 
/*     */       
/* 167 */       if (blockPosition.y < 0 || blockPosition.y >= 320) {
/*     */         return;
/*     */       }
/*     */       
/* 171 */       BlockPlaceUtils.placeBlock(ref, heldItemStack, 
/*     */ 
/*     */           
/* 174 */           (clientPlacedBlockTypeKey != null) ? clientPlacedBlockTypeKey : this.blockTypeKey, heldItemContainer, 
/*     */           
/* 176 */           BlockFace.fromProtocolFace((context.getClientState()).blockFace).getDirection(), targetBlockPosition, blockRotation, inventory, context
/*     */ 
/*     */ 
/*     */           
/* 180 */           .getHeldItemSlot(), this.removeItemInHand, chunkReference, (ComponentAccessor)chunkStore, (ComponentAccessor)commandBuffer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 187 */       boolean isAdventure = (playerComponent == null || playerComponent.getGameMode() == GameMode.Adventure);
/* 188 */       if (isAdventure && heldItemStack.getQuantity() == 1 && this.removeItemInHand) {
/* 189 */         context.setHeldItem(null);
/*     */       }
/*     */       
/* 192 */       BlockChunk blockChunk = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/* 193 */       BlockSection section = blockChunk.getSectionAtBlockY(blockPosition.y);
/* 194 */       (context.getState()).blockPosition = blockPosition;
/* 195 */       (context.getState()).placedBlockId = section.get(blockPosition.x, blockPosition.y, blockPosition.z);
/* 196 */       RotationTuple resultRotation = section.getRotation(blockPosition.x, blockPosition.y, blockPosition.z);
/* 197 */       (context.getState()).blockRotation = new BlockRotation(resultRotation.yaw().toPacket(), resultRotation.pitch().toPacket(), resultRotation.roll().toPacket());
/*     */     } 
/*     */     
/* 200 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 206 */     super.simulateTick0(firstRun, time, type, context, cooldownHandler);
/* 207 */     if (Interaction.failed((context.getState()).state))
/*     */       return; 
/* 209 */     InteractionSyncData clientState = context.getClientState();
/* 210 */     assert clientState != null;
/*     */     
/* 212 */     if (!firstRun) {
/* 213 */       (context.getState()).state = (context.getClientState()).state;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 218 */     clientState.blockRotation = new BlockRotation(Rotation.None, Rotation.None, Rotation.None);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 224 */     return (Interaction)new com.hypixel.hytale.protocol.PlaceBlockInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 229 */     super.configurePacket(packet);
/* 230 */     com.hypixel.hytale.protocol.PlaceBlockInteraction p = (com.hypixel.hytale.protocol.PlaceBlockInteraction)packet;
/* 231 */     p.blockId = (this.blockTypeKey == null) ? -1 : BlockType.getAssetMap().getIndex(this.blockTypeKey);
/* 232 */     p.removeItemInHand = this.removeItemInHand;
/* 233 */     p.allowDragPlacement = this.allowDragPlacement;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 238 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 243 */     return "PlaceBlockInteraction{blockTypeKey='" + this.blockTypeKey + "', removeItemInHand=" + this.removeItemInHand + ", allowDragPlacement=" + this.allowDragPlacement + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\PlaceBlockInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */