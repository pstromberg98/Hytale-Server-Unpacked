/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockFace;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.BlockRotation;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.TargetUtil;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class SimpleBlockInteraction
/*     */   extends SimpleInteraction {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<SimpleBlockInteraction> CODEC;
/*     */   private boolean useLatestTarget;
/*     */   
/*     */   static {
/*  47 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.abstractBuilder(SimpleBlockInteraction.class, SimpleInteraction.CODEC).appendInherited(new KeyedCodec("UseLatestTarget", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.useLatestTarget = s.booleanValue(), interaction -> Boolean.valueOf(interaction.useLatestTarget), (interaction, parent) -> interaction.useLatestTarget = parent.useLatestTarget).documentation("Determines whether to use the clients latest target block position for this interaction.").add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SimpleBlockInteraction(@Nonnull String id) {
/*  55 */     super(id);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     this.useLatestTarget = false; } protected SimpleBlockInteraction() { this.useLatestTarget = false; }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  75 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*     */     LivingEntity livingEntity;
/*  81 */     if (!firstRun)
/*     */       return; 
/*  83 */     Ref<EntityStore> ref = context.getEntity();
/*  84 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/*  85 */     assert commandBuffer != null;
/*     */     
/*  87 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*     */ 
/*     */     
/*  90 */     if (this.useLatestTarget) {
/*  91 */       InteractionSyncData clientState = context.getClientState();
/*  92 */       if (clientState != null && clientState.blockPosition != null) {
/*  93 */         BlockPosition latestBlockPos = clientState.blockPosition;
/*     */         
/*  95 */         TransformComponent transformComponent = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/*  96 */         assert transformComponent != null;
/*     */ 
/*     */ 
/*     */         
/* 100 */         double distanceSquared = transformComponent.getPosition().distanceSquaredTo(latestBlockPos.x + 0.5D, latestBlockPos.y + 0.5D, latestBlockPos.z + 0.5D);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 108 */         BlockPosition baseBlock = world.getBaseBlock(latestBlockPos);
/* 109 */         context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, baseBlock);
/* 110 */         context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, latestBlockPos);
/*     */       } else {
/* 112 */         (context.getState()).state = InteractionState.Failed;
/* 113 */         super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 119 */     BlockPosition targetBlockPos = context.getTargetBlock();
/* 120 */     if (targetBlockPos == null) {
/* 121 */       (context.getState()).state = InteractionState.Failed;
/* 122 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 126 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer);
/* 127 */     if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*     */     else { return; }
/* 129 */      Inventory inventory = livingEntity.getInventory();
/* 130 */     ItemStack itemInHand = inventory.getItemInHand();
/* 131 */     Vector3i targetBlock = new Vector3i(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/*     */     
/* 133 */     WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
/* 134 */     if (chunk == null) {
/* 135 */       (context.getState()).state = InteractionState.Failed;
/* 136 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 140 */     int blockId = chunk.getBlock(targetBlock);
/* 141 */     if (blockId == 1 || blockId == 0) {
/* 142 */       (context.getState()).state = InteractionState.Failed;
/* 143 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 147 */     interactWithBlock(world, commandBuffer, type, context, itemInHand, targetBlock, cooldownHandler);
/*     */     
/* 149 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*     */     LivingEntity livingEntity;
/*     */     Vector3i targetBlock;
/* 175 */     if (!firstRun)
/*     */       return; 
/* 177 */     Ref<EntityStore> ref = context.getEntity();
/* 178 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 179 */     assert commandBuffer != null;
/*     */     
/* 181 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/* 182 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer);
/* 183 */     if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*     */     else { return; }
/* 185 */      Inventory inventory = livingEntity.getInventory();
/* 186 */     ItemStack itemInHand = inventory.getItemInHand();
/*     */     
/* 188 */     (context.getState()).blockFace = BlockFace.Up;
/*     */ 
/*     */ 
/*     */     
/* 192 */     BlockPosition contextTargetBlock = context.getTargetBlock();
/* 193 */     if (contextTargetBlock == null) {
/* 194 */       targetBlock = TargetUtil.getTargetBlock(ref, 8.0D, (ComponentAccessor)commandBuffer);
/* 195 */       if (targetBlock == null) {
/* 196 */         (context.getState()).state = InteractionState.Failed;
/* 197 */         super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */         return;
/*     */       } 
/* 200 */       (context.getState()).blockPosition = new BlockPosition(targetBlock.x, targetBlock.y, targetBlock.z);
/*     */     } else {
/* 202 */       (context.getState()).blockPosition = contextTargetBlock;
/* 203 */       targetBlock = new Vector3i(contextTargetBlock.x, contextTargetBlock.y, contextTargetBlock.z);
/*     */     } 
/*     */     
/* 206 */     WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z));
/* 207 */     if (chunk == null) {
/* 208 */       (context.getState()).state = InteractionState.Failed;
/* 209 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 213 */     int blockId = chunk.getBlock(targetBlock);
/* 214 */     if (blockId == 1 || blockId == 0) {
/* 215 */       (context.getState()).state = InteractionState.Failed;
/* 216 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 220 */     simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
/* 221 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void computeCurrentBlockSyncData(@Nonnull InteractionContext context) {
/* 242 */     BlockPosition targetBlockPos = context.getTargetBlock();
/* 243 */     if (targetBlockPos == null)
/*     */       return; 
/* 245 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 246 */     assert commandBuffer != null;
/*     */     
/* 248 */     World world = ((EntityStore)commandBuffer.getStore().getExternalData()).getWorld();
/* 249 */     ChunkStore chunkStore = world.getChunkStore();
/*     */     
/* 251 */     long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlockPos.x, targetBlockPos.z);
/* 252 */     Ref<ChunkStore> chunkReference = chunkStore.getChunkReference(chunkIndex);
/* 253 */     if (chunkReference == null || !chunkReference.isValid())
/*     */       return; 
/* 255 */     BlockChunk blockChunk = (BlockChunk)chunkStore.getStore().getComponent(chunkReference, BlockChunk.getComponentType());
/* 256 */     if (targetBlockPos.y < 0 || targetBlockPos.y >= 320)
/* 257 */       return;  BlockSection section = blockChunk.getSectionAtBlockY(targetBlockPos.y);
/*     */     
/* 259 */     (context.getState()).blockPosition = new BlockPosition(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/* 260 */     (context.getState()).placedBlockId = section.get(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/* 261 */     RotationTuple resultRotation = section.getRotation(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z);
/* 262 */     (context.getState()).blockRotation = new BlockRotation(resultRotation.yaw().toPacket(), resultRotation.pitch().toPacket(), resultRotation.roll().toPacket());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 268 */     return (Interaction)new com.hypixel.hytale.protocol.SimpleBlockInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 273 */     super.configurePacket(packet);
/* 274 */     com.hypixel.hytale.protocol.SimpleBlockInteraction p = (com.hypixel.hytale.protocol.SimpleBlockInteraction)packet;
/* 275 */     p.useLatestTarget = this.useLatestTarget;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 280 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 286 */     return "SimpleBlockInteraction{} " + super.toString();
/*     */   }
/*     */   
/*     */   protected abstract void interactWithBlock(@Nonnull World paramWorld, @Nonnull CommandBuffer<EntityStore> paramCommandBuffer, @Nonnull InteractionType paramInteractionType, @Nonnull InteractionContext paramInteractionContext, @Nullable ItemStack paramItemStack, @Nonnull Vector3i paramVector3i, @Nonnull CooldownHandler paramCooldownHandler);
/*     */   
/*     */   protected abstract void simulateInteractWithBlock(@Nonnull InteractionType paramInteractionType, @Nonnull InteractionContext paramInteractionContext, @Nullable ItemStack paramItemStack, @Nonnull World paramWorld, @Nonnull Vector3i paramVector3i);
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\SimpleBlockInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */