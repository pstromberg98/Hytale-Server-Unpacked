/*    */ package com.hypixel.hytale.builtin.hytalegenerator.delimiters;
/*    */ 
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class DelimiterDouble<V> {
/*    */   @Nonnull
/*    */   private final RangeDouble range;
/*    */   @Nullable
/*    */   private final V value;
/*    */   
/*    */   public DelimiterDouble(@Nonnull RangeDouble range, @Nullable V value) {
/* 13 */     this.range = range;
/* 14 */     this.value = value;
/*    */   }
/*    */   @Nonnull
/*    */   public RangeDouble getRange() {
/* 18 */     return this.range;
/*    */   }
/*    */   @Nullable
/*    */   public V getValue() {
/* 22 */     return this.value;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\delimiters\DelimiterDouble.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */