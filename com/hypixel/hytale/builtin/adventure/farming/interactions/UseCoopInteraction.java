/*    */ package com.hypixel.hytale.builtin.adventure.farming.interactions;
/*    */ import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.protocol.InteractionState;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class UseCoopInteraction extends SimpleBlockInteraction {
/* 26 */   public static final BuilderCodec<UseCoopInteraction> CODEC = BuilderCodec.builder(UseCoopInteraction.class, UseCoopInteraction::new, SimpleBlockInteraction.CODEC)
/* 27 */     .build();
/*    */ 
/*    */   
/*    */   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i targetBlock, @Nonnull CooldownHandler cooldownHandler) {
/* 31 */     int x = targetBlock.getX();
/* 32 */     int z = targetBlock.getZ();
/*    */     
/* 34 */     WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
/* 35 */     if (worldChunk == null) {
/* 36 */       (context.getState()).state = InteractionState.Failed;
/*    */       
/*    */       return;
/*    */     } 
/* 40 */     Ref<ChunkStore> blockRef = worldChunk.getBlockComponentEntity(x, targetBlock.getY(), z);
/* 41 */     if (blockRef == null) {
/* 42 */       blockRef = BlockModule.ensureBlockEntity(worldChunk, targetBlock.x, targetBlock.y, targetBlock.z);
/*    */     }
/*    */     
/* 45 */     if (blockRef == null) {
/* 46 */       (context.getState()).state = InteractionState.Failed;
/*    */       
/*    */       return;
/*    */     } 
/* 50 */     Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*    */     
/* 52 */     CoopBlock coopBlockState = (CoopBlock)chunkStore.getComponent(blockRef, CoopBlock.getComponentType());
/* 53 */     if (coopBlockState == null) {
/* 54 */       (context.getState()).state = InteractionState.Failed;
/*    */       
/*    */       return;
/*    */     } 
/* 58 */     Ref<EntityStore> playerRef = context.getEntity();
/* 59 */     LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, (ComponentAccessor)commandBuffer);
/* 60 */     if (playerEntity == null) {
/* 61 */       (context.getState()).state = InteractionState.Failed;
/*    */       
/*    */       return;
/*    */     } 
/* 65 */     CombinedItemContainer playerInventoryContainer = playerEntity.getInventory().getCombinedHotbarFirst();
/* 66 */     if (playerInventoryContainer == null) {
/*    */       return;
/*    */     }
/* 69 */     coopBlockState.gatherProduceFromInventory((ItemContainer)playerInventoryContainer);
/*    */     
/* 71 */     BlockType currentBlockType = worldChunk.getBlockType(targetBlock);
/* 72 */     assert currentBlockType != null;
/*    */     
/* 74 */     worldChunk.setBlockInteractionState(targetBlock, currentBlockType, 
/* 75 */         coopBlockState.hasProduce() ? "Produce_Ready" : "default");
/*    */   }
/*    */   
/*    */   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\interactions\UseCoopInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */