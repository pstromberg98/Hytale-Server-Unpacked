/*    */ package com.hypixel.hytale.server.core.codec;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LayerEntryCodec
/*    */ {
/*    */   public static final BuilderCodec<LayerEntryCodec> CODEC;
/*    */   private Integer depth;
/*    */   private String material;
/*    */   
/*    */   static {
/* 42 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LayerEntryCodec.class, LayerEntryCodec::new).append(new KeyedCodec("Left", (Codec)Codec.INTEGER), (entry, depth) -> entry.depth = depth, entry -> entry.depth).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("Right", (Codec)Codec.STRING), (entry, material) -> entry.material = material, entry -> entry.material).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("UseToolArg", (Codec)Codec.BOOLEAN), (entry, useToolArg) -> entry.useToolArg = (useToolArg != null && useToolArg.booleanValue()), entry -> Boolean.valueOf(entry.useToolArg)).add()).append(new KeyedCodec("Skip", (Codec)Codec.BOOLEAN), (entry, skip) -> entry.skip = (skip != null && skip.booleanValue()), entry -> Boolean.valueOf(entry.skip)).add()).build();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean useToolArg = false;
/*    */   
/*    */   private boolean skip = false;
/*    */ 
/*    */   
/*    */   public LayerEntryCodec(Integer depth, String material, boolean useToolArg) {
/* 53 */     this.depth = depth;
/* 54 */     this.material = material;
/* 55 */     this.useToolArg = useToolArg;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public Integer getDepth() {
/* 60 */     return this.depth;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public String getMaterial() {
/* 65 */     return this.material;
/*    */   }
/*    */   
/*    */   public boolean isUseToolArg() {
/* 69 */     return this.useToolArg;
/*    */   }
/*    */   
/*    */   public boolean isSkip() {
/* 73 */     return this.skip;
/*    */   }
/*    */   
/*    */   public LayerEntryCodec() {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\codec\LayerEntryCodec.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */