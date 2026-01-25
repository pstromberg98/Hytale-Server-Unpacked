/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.lookup.CodecMapCodec;
/*    */ import com.hypixel.hytale.protocol.Selector;
/*    */ import com.hypixel.hytale.server.core.io.NetworkSerializable;
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
/*    */ public abstract class SelectorType
/*    */   implements NetworkSerializable<Selector>
/*    */ {
/*    */   @Nonnull
/* 29 */   public static final CodecMapCodec<SelectorType> CODEC = new CodecMapCodec();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 35 */   public static final BuilderCodec<SelectorType> BASE_CODEC = BuilderCodec.abstractBuilder(SelectorType.class)
/* 36 */     .build();
/*    */   
/*    */   @Nonnull
/*    */   public abstract Selector newSelector();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\selector\SelectorType.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */