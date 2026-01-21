/*    */ package com.hypixel.hytale.builtin.adventure.objectives.config.completion;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ClearObjectiveItemsCompletionAsset extends ObjectiveCompletionAsset {
/*  8 */   public static final BuilderCodec<ClearObjectiveItemsCompletionAsset> CODEC = BuilderCodec.builder(ClearObjectiveItemsCompletionAsset.class, ClearObjectiveItemsCompletionAsset::new, BASE_CODEC)
/*  9 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 17 */     return "ClearObjectiveItemsCompletionAsset{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\config\completion\ClearObjectiveItemsCompletionAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */