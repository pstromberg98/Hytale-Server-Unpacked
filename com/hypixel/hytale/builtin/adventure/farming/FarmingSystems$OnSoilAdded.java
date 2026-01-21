/*    */ package com.hypixel.hytale.builtin.adventure.farming;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
/*    */ import com.hypixel.hytale.component.AddReason;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.RemoveReason;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.RefSystem;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*    */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import java.time.Instant;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OnSoilAdded
/*    */   extends RefSystem<ChunkStore>
/*    */ {
/* 44 */   private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] {
/* 45 */         (Query)BlockModule.BlockStateInfo.getComponentType(), 
/* 46 */         (Query)TilledSoilBlock.getComponentType()
/*    */       });
/*    */ 
/*    */   
/*    */   public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 51 */     TilledSoilBlock soil = (TilledSoilBlock)commandBuffer.getComponent(ref, TilledSoilBlock.getComponentType());
/* 52 */     assert soil != null;
/* 53 */     BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());
/* 54 */     assert info != null;
/*    */     
/* 56 */     if (!soil.isPlanted()) {
/* 57 */       int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
/* 58 */       int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 59 */       int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
/*    */       
/* 61 */       assert info.getChunkRef() != null;
/* 62 */       BlockChunk blockChunk = (BlockChunk)commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
/* 63 */       assert blockChunk != null;
/* 64 */       BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
/*    */ 
/*    */       
/* 67 */       Instant decayTime = soil.getDecayTime();
/* 68 */       if (decayTime == null) {
/*    */ 
/*    */         
/* 71 */         BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
/* 72 */         FarmingSystems.updateSoilDecayTime(commandBuffer, soil, blockType);
/*    */       } 
/* 74 */       if (decayTime == null) {
/*    */         return;
/*    */       }
/*    */       
/* 78 */       blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Query<ChunkStore> getQuery() {
/* 90 */     return QUERY;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\FarmingSystems$OnSoilAdded.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */