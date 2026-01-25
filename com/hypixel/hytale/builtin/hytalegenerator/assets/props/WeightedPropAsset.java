/*    */ package com.hypixel.hytale.builtin.hytalegenerator.assets.props;
/*    */ 
/*    */ import com.hypixel.hytale.assetstore.AssetExtraInfo;
/*    */ import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
/*    */ import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
/*    */ import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.props.WeightedProp;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
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
/*    */ public class WeightedPropAsset
/*    */   extends PropAsset
/*    */ {
/*    */   public static final BuilderCodec<WeightedPropAsset> CODEC;
/*    */   
/*    */   static {
/* 33 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WeightedPropAsset.class, WeightedPropAsset::new, PropAsset.ABSTRACT_CODEC).append(new KeyedCodec("Entries", (Codec)new ArrayCodec((Codec)EntryAsset.CODEC, x$0 -> new EntryAsset[x$0]), true), (asset, value) -> asset.entryAssets = value, asset -> asset.entryAssets).add()).append(new KeyedCodec("Seed", (Codec)Codec.STRING, true), (asset, value) -> asset.seed = value, asset -> asset.seed).add()).build();
/*    */   }
/* 35 */   private EntryAsset[] entryAssets = new EntryAsset[0];
/* 36 */   private String seed = "";
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Prop build(@Nonnull PropAsset.Argument argument) {
/* 41 */     if (skip() || this.entryAssets.length == 0) return Prop.noProp();
/*    */     
/* 43 */     WeightedMap<Prop> weightedProps = new WeightedMap(this.entryAssets.length);
/*    */     
/* 45 */     PropAsset.Argument childArgument = new PropAsset.Argument(argument);
/* 46 */     childArgument.parentSeed = argument.parentSeed.child(this.seed);
/*    */     
/* 48 */     for (EntryAsset entryAsset : this.entryAssets) {
/* 49 */       weightedProps.add(entryAsset.propAsset.build(childArgument), entryAsset.weight);
/*    */     }
/*    */     
/* 52 */     return (Prop)new WeightedProp(weightedProps, ((Integer)childArgument.parentSeed.createSupplier().get()).intValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public void cleanUp() {
/* 57 */     for (EntryAsset asset : this.entryAssets) {
/* 58 */       asset.cleanUp();
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static class EntryAsset
/*    */     implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, EntryAsset>>
/*    */   {
/*    */     public static final AssetBuilderCodec<String, EntryAsset> CODEC;
/*    */ 
/*    */ 
/*    */     
/*    */     private String id;
/*    */ 
/*    */ 
/*    */     
/*    */     private AssetExtraInfo.Data data;
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     static {
/* 83 */       CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(EntryAsset.class, EntryAsset::new, (Codec)Codec.STRING, (asset, id) -> asset.id = id, config -> config.id, (config, data) -> config.data = data, config -> config.data).append(new KeyedCodec("Weight", (Codec)Codec.DOUBLE, true), (asset, value) -> asset.weight = value.doubleValue(), asset -> Double.valueOf(asset.weight)).addValidator(Validators.greaterThan(Double.valueOf(0.0D))).add()).append(new KeyedCodec("Prop", (Codec)PropAsset.CODEC, true), (asset, value) -> asset.propAsset = value, asset -> asset.propAsset).add()).build();
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 88 */     private double weight = 1.0D;
/* 89 */     private PropAsset propAsset = new NoPropAsset();
/*    */ 
/*    */     
/*    */     public String getId() {
/* 93 */       return this.id;
/*    */     }
/*    */ 
/*    */     
/*    */     public void cleanUp() {
/* 98 */       this.propAsset.cleanUp();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\props\WeightedPropAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */