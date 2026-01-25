/*    */ package com.hypixel.hytale.builtin.hytalegenerator.assets.density;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.GradientWarpDensity;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
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
/*    */ public class GradientWarpDensityAsset
/*    */   extends DensityAsset
/*    */ {
/*    */   public static final BuilderCodec<GradientWarpDensityAsset> CODEC;
/*    */   
/*    */   static {
/* 36 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GradientWarpDensityAsset.class, GradientWarpDensityAsset::new, DensityAsset.ABSTRACT_CODEC).append(new KeyedCodec("SampleRange", (Codec)Codec.DOUBLE, false), (t, k) -> t.sampleRange = k.doubleValue(), t -> Double.valueOf(t.sampleRange)).addValidator(Validators.greaterThan(Double.valueOf(0.0D))).add()).append(new KeyedCodec("WarpFactor", (Codec)Codec.DOUBLE, false), (t, k) -> t.warpFactor = k.doubleValue(), t -> Double.valueOf(t.warpFactor)).add()).append(new KeyedCodec("2D", (Codec)Codec.BOOLEAN, false), (t, k) -> t.is2d = k.booleanValue(), t -> Boolean.valueOf(t.is2d)).add()).append(new KeyedCodec("YFor2D", (Codec)Codec.DOUBLE, false), (t, k) -> t.y2d = k.doubleValue(), t -> Double.valueOf(t.y2d)).add()).build();
/*    */   }
/* 38 */   private double sampleRange = 1.0D;
/* 39 */   private double warpFactor = 1.0D;
/*    */   private boolean is2d = false;
/* 41 */   private double y2d = 0.0D;
/*    */   
/*    */   @Nonnull
/*    */   public Density build(@Nonnull DensityAsset.Argument argument) {
/* 45 */     if (isSkipped()) return (Density)new ConstantValueDensity(0.0D);
/*    */ 
/*    */     
/* 48 */     GradientWarpDensity node = new GradientWarpDensity(buildFirstInput(argument), buildSecondInput(argument), this.sampleRange, this.warpFactor);
/*    */ 
/*    */     
/* 51 */     return (Density)node;
/*    */   }
/*    */ 
/*    */   
/*    */   public void cleanUp() {
/* 56 */     cleanUpInputs();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\density\GradientWarpDensityAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */