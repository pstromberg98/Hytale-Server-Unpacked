/*     */ package com.hypixel.hytale.server.worldgen.util.condition;
/*     */ 
/*     */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MaskEntry
/*     */ {
/* 112 */   public static final MaskEntry WILDCARD_TRUE = new MaskEntry(true, true);
/* 113 */   public static final MaskEntry WILDCARD_FALSE = new MaskEntry(true, false);
/*     */   
/* 115 */   private ResolvedBlockArray blocks = ResolvedBlockArray.EMPTY;
/*     */   private final boolean any;
/*     */   private boolean replace;
/*     */   
/*     */   public MaskEntry() {
/* 120 */     this(false, false);
/*     */   }
/*     */   
/*     */   private MaskEntry(boolean any, boolean replace) {
/* 124 */     this.any = any;
/* 125 */     this.replace = replace;
/*     */   }
/*     */   
/*     */   public void set(ResolvedBlockArray blocks, boolean replace) {
/* 129 */     this.blocks = blocks;
/* 130 */     this.replace = replace;
/*     */   }
/*     */   
/*     */   public boolean shouldHandle(int current, int fluid) {
/* 134 */     return (this.any || this.blocks.contains(current, fluid));
/*     */   }
/*     */   
/*     */   public boolean shouldReplace() {
/* 138 */     return this.replace;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/* 143 */     if (this == o) return true; 
/* 144 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 146 */     MaskEntry that = (MaskEntry)o;
/*     */     
/* 148 */     if (this.any != that.any) return false; 
/* 149 */     if (this.replace != that.replace) return false; 
/* 150 */     return this.blocks.equals(that.blocks);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 155 */     int result = this.blocks.hashCode();
/* 156 */     result = 31 * result + (this.any ? 1 : 0);
/* 157 */     result = 31 * result + (this.replace ? 1 : 0);
/* 158 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 164 */     return "MaskEntry{blocks=" + String.valueOf(this.blocks) + ", replace=" + this.replace + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldge\\util\condition\BlockMaskCondition$MaskEntry.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */