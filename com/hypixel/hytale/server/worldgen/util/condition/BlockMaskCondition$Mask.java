/*     */ package com.hypixel.hytale.server.worldgen.util.condition;
/*     */ 
/*     */ import java.util.Arrays;
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
/*     */ public class Mask
/*     */ {
/*     */   private final boolean matchEmpty;
/*     */   private final BlockMaskCondition.MaskEntry[] entries;
/*     */   
/*     */   public Mask(@Nonnull BlockMaskCondition.MaskEntry[] entries) {
/*  70 */     this(false, entries);
/*     */   }
/*     */   
/*     */   private Mask(boolean matchEmpty, @Nonnull BlockMaskCondition.MaskEntry[] entries) {
/*  74 */     this.entries = entries;
/*  75 */     this.matchEmpty = matchEmpty;
/*     */   }
/*     */   
/*     */   public boolean shouldReplace(int current, int fluid) {
/*  79 */     for (BlockMaskCondition.MaskEntry entry : this.entries) {
/*  80 */       if (entry.shouldHandle(current, fluid)) {
/*  81 */         return entry.shouldReplace();
/*     */       }
/*     */     } 
/*  84 */     return (this.matchEmpty && (current == 0 || fluid == 0));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/*  89 */     if (this == o) return true; 
/*  90 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/*  92 */     Mask mask = (Mask)o;
/*     */     
/*  94 */     return Arrays.equals((Object[])this.entries, (Object[])mask.entries);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  99 */     return Arrays.hashCode((Object[])this.entries);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 106 */     return "Mask{entries=" + Arrays.toString((Object[])this.entries) + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldge\\util\condition\BlockMaskCondition$Mask.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */