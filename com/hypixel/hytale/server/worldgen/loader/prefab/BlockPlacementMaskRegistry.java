/*    */ package com.hypixel.hytale.server.worldgen.loader.prefab;
/*    */ 
/*    */ import com.hypixel.hytale.server.worldgen.loader.util.FileMaskCache;
/*    */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
/*    */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*    */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BlockPlacementMaskRegistry
/*    */   extends FileMaskCache<BlockMaskCondition>
/*    */ {
/*    */   @Nonnull
/* 22 */   private final Map<BlockMaskCondition, BlockMaskCondition> masks = new HashMap<>(); @Nonnull
/* 23 */   private final Map<BlockMaskCondition.MaskEntry, BlockMaskCondition.MaskEntry> entries = new HashMap<>();
/* 24 */   private BlockMaskCondition tempMask = new BlockMaskCondition();
/* 25 */   private BlockMaskCondition.MaskEntry tempEntry = new BlockMaskCondition.MaskEntry();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public BlockMaskCondition retainOrAllocateMask(@Nonnull BlockMaskCondition.Mask defaultMask, @Nonnull Long2ObjectMap<BlockMaskCondition.Mask> specificMasks) {
/* 30 */     BlockMaskCondition mask = this.tempMask;
/* 31 */     mask.set(defaultMask, specificMasks);
/*    */     
/* 33 */     BlockMaskCondition old = this.masks.putIfAbsent(mask, mask);
/* 34 */     if (old != null) return old;
/*    */     
/* 36 */     this.tempMask = new BlockMaskCondition();
/* 37 */     return mask;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public BlockMaskCondition.MaskEntry retainOrAllocateEntry(@Nonnull ResolvedBlockArray blocks, boolean replace) {
/* 42 */     BlockMaskCondition.MaskEntry entry = this.tempEntry;
/* 43 */     entry.set(blocks, replace);
/*    */     
/* 45 */     BlockMaskCondition.MaskEntry old = this.entries.putIfAbsent(entry, entry);
/* 46 */     if (old != null) return old;
/*    */     
/* 48 */     this.tempEntry = new BlockMaskCondition.MaskEntry();
/* 49 */     return entry;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\prefab\BlockPlacementMaskRegistry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */