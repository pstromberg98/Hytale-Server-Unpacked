/*    */ package com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.distancefunctions;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.distancefunctions.DistanceFunction;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.distancefunctions.ManhattanDistanceFunction;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ManhattanDistanceFunctionAsset
/*    */   extends DistanceFunctionAsset {
/* 12 */   public static final BuilderCodec<ManhattanDistanceFunctionAsset> CODEC = BuilderCodec.builder(ManhattanDistanceFunctionAsset.class, ManhattanDistanceFunctionAsset::new, DistanceFunctionAsset.ABSTRACT_CODEC)
/* 13 */     .build();
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public DistanceFunction build(@Nonnull SeedBox parentSeed, double maxDistance) {
/* 19 */     return (DistanceFunction)new ManhattanDistanceFunction();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\density\positions\distancefunctions\ManhattanDistanceFunctionAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */