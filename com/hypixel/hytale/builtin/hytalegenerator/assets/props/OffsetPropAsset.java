/*    */ package com.hypixel.hytale.builtin.hytalegenerator.assets.props;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.props.OffsetProp;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
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
/*    */ public class OffsetPropAsset
/*    */   extends PropAsset
/*    */ {
/*    */   public static final BuilderCodec<OffsetPropAsset> CODEC;
/*    */   
/*    */   static {
/* 28 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(OffsetPropAsset.class, OffsetPropAsset::new, PropAsset.ABSTRACT_CODEC).append(new KeyedCodec("Offset", (Codec)Vector3i.CODEC, true), (asset, value) -> asset.offset_voxelGrid = value, asset -> asset.offset_voxelGrid).add()).append(new KeyedCodec("Prop", (Codec)PropAsset.CODEC, true), (asset, value) -> asset.propAsset = value, asset -> asset.propAsset).add()).build();
/*    */   }
/* 30 */   private Vector3i offset_voxelGrid = new Vector3i();
/* 31 */   private PropAsset propAsset = new NoPropAsset();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Prop build(@Nonnull PropAsset.Argument argument) {
/* 36 */     if (skip()) {
/* 37 */       return Prop.noProp();
/*    */     }
/*    */     
/* 40 */     return (Prop)new OffsetProp(this.offset_voxelGrid, this.propAsset.build(argument));
/*    */   }
/*    */ 
/*    */   
/*    */   public void cleanUp() {
/* 45 */     this.propAsset.cleanUp();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\assets\props\OffsetPropAsset.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */