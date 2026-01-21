/*    */ package com.hypixel.hytale.builtin.hytalegenerator.tintproviders;
/*    */ 
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ConstantTintProvider extends TintProvider {
/*    */   @Nonnull
/*    */   private final TintProvider.Result result;
/*    */   
/*    */   public ConstantTintProvider(int value) {
/* 10 */     this.result = new TintProvider.Result(value);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public TintProvider.Result getValue(@Nonnull TintProvider.Context context) {
/* 16 */     return this.result;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\tintproviders\ConstantTintProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */