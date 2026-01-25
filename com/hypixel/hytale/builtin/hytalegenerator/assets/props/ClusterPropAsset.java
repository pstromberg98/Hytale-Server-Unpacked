/*     */ package com.hypixel.hytale.builtin.hytalegenerator.assets.props;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.AssetExtraInfo;
/*     */ import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
/*     */ import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
/*     */ import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.ConstantCurveAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.curves.CurveAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.ConstantPatternAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.patterns.PatternAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.OriginScannerAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.scanners.ScannerAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.ClusterProp;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.scanners.OriginScanner;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
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
/*     */ public class ClusterPropAsset
/*     */   extends PropAsset
/*     */ {
/*     */   public static final BuilderCodec<ClusterPropAsset> CODEC;
/*     */   
/*     */   static {
/*  63 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ClusterPropAsset.class, ClusterPropAsset::new, PropAsset.ABSTRACT_CODEC).append(new KeyedCodec("Range", (Codec)Codec.INTEGER, false), (asset, v) -> asset.range = v.intValue(), asset -> Integer.valueOf(asset.range)).addValidator(Validators.greaterThanOrEqual(Integer.valueOf(0))).add()).append(new KeyedCodec("DistanceCurve", (Codec)CurveAsset.CODEC, true), (asset, v) -> asset.distanceCurve = v, asset -> asset.distanceCurve).add()).append(new KeyedCodec("Seed", (Codec)Codec.STRING, false), (asset, v) -> asset.seed = v, asset -> asset.seed).add()).append(new KeyedCodec("WeightedProps", (Codec)new ArrayCodec((Codec)WeightedPropAsset.CODEC, x$0 -> new WeightedPropAsset[x$0]), true), (asset, v) -> asset.weightedPropAssets = v, asset -> asset.weightedPropAssets).add()).append(new KeyedCodec("Pattern", (Codec)PatternAsset.CODEC, false), (asset, v) -> asset.patternAsset = v, asset -> asset.patternAsset).add()).append(new KeyedCodec("Scanner", (Codec)ScannerAsset.CODEC, false), (asset, v) -> asset.scannerAsset = v, asset -> asset.scannerAsset).add()).build();
/*     */   }
/*  65 */   private int range = 0;
/*  66 */   private CurveAsset distanceCurve = (CurveAsset)new ConstantCurveAsset();
/*  67 */   private String seed = "A";
/*  68 */   private WeightedPropAsset[] weightedPropAssets = new WeightedPropAsset[0];
/*  69 */   private PatternAsset patternAsset = (PatternAsset)new ConstantPatternAsset();
/*  70 */   private ScannerAsset scannerAsset = (ScannerAsset)new OriginScannerAsset();
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Prop build(@Nonnull PropAsset.Argument argument) {
/*  75 */     if (skip()) return Prop.noProp();
/*     */     
/*  77 */     WeightedMap<Prop> weightedMap = new WeightedMap();
/*  78 */     for (WeightedPropAsset entry : this.weightedPropAssets) {
/*  79 */       Prop columnProp = entry.propAsset.build(argument);
/*     */       
/*  81 */       Vector3i readSize = columnProp.getReadBounds_voxelGrid().getSize();
/*  82 */       Vector3i writeSize = columnProp.getWriteBounds_voxelGrid().getSize();
/*  83 */       if (readSize.x != 1 || readSize.z != 1) {
/*  84 */         LoggerUtil.getLogger().log(Level.WARNING, "Cluster Prop child has a read area larger than a column.");
/*     */       
/*     */       }
/*  87 */       else if (writeSize.x != 1 || writeSize.z != 1) {
/*  88 */         LoggerUtil.getLogger().log(Level.WARNING, "Cluster Prop child has a write area larger than a column.");
/*     */       }
/*     */       else {
/*     */         
/*  92 */         weightedMap.add(entry.propAsset.build(argument), entry.weight);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  97 */     Pattern pattern = (this.patternAsset == null) ? Pattern.yesPattern() : this.patternAsset.build(PatternAsset.argumentFrom(argument));
/*     */ 
/*     */     
/* 100 */     Scanner scanner = (this.scannerAsset == null) ? (Scanner)OriginScanner.getInstance() : this.scannerAsset.build(ScannerAsset.argumentFrom(argument));
/*     */     
/* 102 */     int intSeed = ((Integer)argument.parentSeed.child(this.seed).createSupplier().get()).intValue();
/* 103 */     return (Prop)new ClusterProp(this.range, this.distanceCurve.build(), intSeed, weightedMap, pattern, scanner);
/*     */   }
/*     */ 
/*     */   
/*     */   public void cleanUp() {
/* 108 */     this.distanceCurve.cleanUp();
/* 109 */     for (WeightedPropAsset weightedPropAsset : this.weightedPropAssets) {
/* 110 */       weightedPropAsset.cleanUp();
/*     */     }
/* 112 */     this.patternAsset.cleanUp();
/* 113 */     this.scannerAsset.cleanUp();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class WeightedPropAsset
/*     */     implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, WeightedPropAsset>>
/*     */   {
/*     */     public static final AssetBuilderCodec<String, WeightedPropAsset> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     private String id;
/*     */ 
/*     */ 
/*     */     
/*     */     private AssetExtraInfo.Data data;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 136 */       CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(WeightedPropAsset.class, WeightedPropAsset::new, (Codec)Codec.STRING, (asset, id) -> asset.id = id, config -> config.id, (config, data) -> config.data = data, config -> config.data).append(new KeyedCodec("Weight", (Codec)Codec.DOUBLE, true), (t, w) -> t.weight = w.doubleValue(), t -> Double.valueOf(t.weight)).addValidator(Validators.greaterThan(Double.valueOf(0.0D))).add()).append(new KeyedCodec("ColumnProp", (Codec)PropAsset.CODEC, true), (t, out) -> t.propAsset = out, t -> t.propAsset).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 141 */     private double weight = 1.0D;
/*     */     
/*     */     private PropAsset propAsset;
/*     */     
/*     */     public String getId() {
/* 146 */       return this.id;
/*     */     }
/*     */ 
/*     */     
/*     */     public void cleanUp() {
/* 151 */       this.propAsset.cleanUp();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\props\ClusterPropAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */