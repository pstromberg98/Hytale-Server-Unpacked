/*    */ package com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.assets.delimiters.RangeDoubleAsset;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.SimpleHorizontalPositionProvider;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
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
/*    */ public class SimpleHorizontalPositionProviderAsset
/*    */   extends PositionProviderAsset
/*    */ {
/*    */   public static final BuilderCodec<SimpleHorizontalPositionProviderAsset> CODEC;
/*    */   
/*    */   static {
/* 26 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SimpleHorizontalPositionProviderAsset.class, SimpleHorizontalPositionProviderAsset::new, PositionProviderAsset.ABSTRACT_CODEC).append(new KeyedCodec("RangeY", (Codec)RangeDoubleAsset.CODEC, true), (asset, value) -> asset.rangeYAsset = value, asset -> asset.rangeYAsset).add()).append(new KeyedCodec("Positions", (Codec)PositionProviderAsset.CODEC, true), (asset, v) -> asset.positionProviderAsset = v, asset -> asset.positionProviderAsset).add()).build();
/*    */   }
/* 28 */   private RangeDoubleAsset rangeYAsset = new RangeDoubleAsset();
/* 29 */   private PositionProviderAsset positionProviderAsset = new ListPositionProviderAsset();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public PositionProvider build(@Nonnull PositionProviderAsset.Argument argument) {
/* 34 */     if (skip()) {
/* 35 */       return PositionProvider.noPositionProvider();
/*    */     }
/*    */     
/* 38 */     PositionProvider positionProvider = this.positionProviderAsset.build(argument);
/* 39 */     return (PositionProvider)new SimpleHorizontalPositionProvider(this.rangeYAsset.build(), positionProvider);
/*    */   }
/*    */ 
/*    */   
/*    */   public void cleanUp() {
/* 44 */     this.positionProviderAsset.cleanUp();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\positionproviders\SimpleHorizontalPositionProviderAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */