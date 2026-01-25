/*    */ package com.hypixel.hytale.builtin.hytalegenerator.positionproviders;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.delimiters.RangeDouble;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class SimpleHorizontalPositionProvider extends PositionProvider {
/*    */   private final RangeDouble rangeY;
/*    */   @Nonnull
/*    */   private final PositionProvider positionProvider;
/*    */   
/*    */   public SimpleHorizontalPositionProvider(@Nonnull RangeDouble rangeY, @Nonnull PositionProvider positionProvider) {
/* 13 */     this.rangeY = rangeY;
/* 14 */     this.positionProvider = positionProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   public void positionsIn(@Nonnull PositionProvider.Context context) {
/* 19 */     PositionProvider.Context childContext = new PositionProvider.Context(context);
/* 20 */     childContext.consumer = (positions -> {
/*    */         if (!this.rangeY.contains(positions.y))
/*    */           return; 
/*    */         context.consumer.accept(positions);
/*    */       });
/* 25 */     this.positionProvider.positionsIn(childContext);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\positionproviders\SimpleHorizontalPositionProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */