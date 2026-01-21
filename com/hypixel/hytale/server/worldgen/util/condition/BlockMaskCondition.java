/*     */ package com.hypixel.hytale.server.worldgen.util.condition;
/*     */ 
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
/*     */ import java.util.Arrays;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockMaskCondition
/*     */ {
/*  18 */   public static final Mask DEFAULT_MASK = new Mask(true, new MaskEntry[0]);
/*  19 */   public static final BlockMaskCondition DEFAULT_TRUE = new BlockMaskCondition();
/*  20 */   public static final BlockMaskCondition DEFAULT_FALSE = new BlockMaskCondition();
/*     */   @Nonnull
/*  22 */   private Mask defaultMask = DEFAULT_MASK; @Nonnull
/*  23 */   private Long2ObjectMap<Mask> specificMasks = Long2ObjectMaps.emptyMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void set(@Nonnull Mask defaultMask, @Nonnull Long2ObjectMap<Mask> specificMasks) {
/*  29 */     this.defaultMask = defaultMask;
/*  30 */     this.specificMasks = specificMasks;
/*     */   }
/*     */   
/*     */   public boolean eval(int currentBlock, int currentFluid, int nextBlockId, int nextFluidId) {
/*  34 */     Mask mask = (Mask)this.specificMasks.getOrDefault(MathUtil.packLong(nextBlockId, nextFluidId), this.defaultMask);
/*  35 */     return mask.shouldReplace(currentBlock, currentFluid);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/*  40 */     if (this == o) return true; 
/*  41 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/*  43 */     BlockMaskCondition that = (BlockMaskCondition)o;
/*     */     
/*  45 */     if (!this.defaultMask.equals(that.defaultMask)) return false; 
/*  46 */     return Objects.equals(this.specificMasks, that.specificMasks);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  51 */     int result = this.defaultMask.hashCode();
/*  52 */     result = 31 * result + ((this.specificMasks != null) ? this.specificMasks.hashCode() : 0);
/*  53 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/*  59 */     return "BlockMaskCondition{defaultMask=" + String.valueOf(this.defaultMask) + ", specificMasks=" + String.valueOf(this.specificMasks) + "}";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Mask
/*     */   {
/*     */     private final boolean matchEmpty;
/*     */     
/*     */     private final BlockMaskCondition.MaskEntry[] entries;
/*     */     
/*     */     public Mask(@Nonnull BlockMaskCondition.MaskEntry[] entries) {
/*  70 */       this(false, entries);
/*     */     }
/*     */     
/*     */     private Mask(boolean matchEmpty, @Nonnull BlockMaskCondition.MaskEntry[] entries) {
/*  74 */       this.entries = entries;
/*  75 */       this.matchEmpty = matchEmpty;
/*     */     }
/*     */     
/*     */     public boolean shouldReplace(int current, int fluid) {
/*  79 */       for (BlockMaskCondition.MaskEntry entry : this.entries) {
/*  80 */         if (entry.shouldHandle(current, fluid)) {
/*  81 */           return entry.shouldReplace();
/*     */         }
/*     */       } 
/*  84 */       return (this.matchEmpty && (current == 0 || fluid == 0));
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/*  89 */       if (this == o) return true; 
/*  90 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/*  92 */       Mask mask = (Mask)o;
/*     */       
/*  94 */       return Arrays.equals((Object[])this.entries, (Object[])mask.entries);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/*  99 */       return Arrays.hashCode((Object[])this.entries);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 106 */       return "Mask{entries=" + Arrays.toString((Object[])this.entries) + "}";
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MaskEntry
/*     */   {
/* 112 */     public static final MaskEntry WILDCARD_TRUE = new MaskEntry(true, true);
/* 113 */     public static final MaskEntry WILDCARD_FALSE = new MaskEntry(true, false);
/*     */     
/* 115 */     private ResolvedBlockArray blocks = ResolvedBlockArray.EMPTY;
/*     */     private final boolean any;
/*     */     private boolean replace;
/*     */     
/*     */     public MaskEntry() {
/* 120 */       this(false, false);
/*     */     }
/*     */     
/*     */     private MaskEntry(boolean any, boolean replace) {
/* 124 */       this.any = any;
/* 125 */       this.replace = replace;
/*     */     }
/*     */     
/*     */     public void set(ResolvedBlockArray blocks, boolean replace) {
/* 129 */       this.blocks = blocks;
/* 130 */       this.replace = replace;
/*     */     }
/*     */     
/*     */     public boolean shouldHandle(int current, int fluid) {
/* 134 */       return (this.any || this.blocks.contains(current, fluid));
/*     */     }
/*     */     
/*     */     public boolean shouldReplace() {
/* 138 */       return this.replace;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/* 143 */       if (this == o) return true; 
/* 144 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 146 */       MaskEntry that = (MaskEntry)o;
/*     */       
/* 148 */       if (this.any != that.any) return false; 
/* 149 */       if (this.replace != that.replace) return false; 
/* 150 */       return this.blocks.equals(that.blocks);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 155 */       int result = this.blocks.hashCode();
/* 156 */       result = 31 * result + (this.any ? 1 : 0);
/* 157 */       result = 31 * result + (this.replace ? 1 : 0);
/* 158 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 164 */       return "MaskEntry{blocks=" + String.valueOf(this.blocks) + ", replace=" + this.replace + "}";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldge\\util\condition\BlockMaskCondition.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */