/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.protocol.Interaction;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BuilderToolInteraction
/*    */   extends SimpleInteraction
/*    */ {
/*    */   @Nonnull
/* 22 */   public static final BuilderCodec<BuilderToolInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BuilderToolInteraction.class, BuilderToolInteraction::new, SimpleInteraction.CODEC)
/* 23 */     .documentation("Runs a builder tool"))
/* 24 */     .build();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   protected Interaction generatePacket() {
/* 29 */     return (Interaction)new com.hypixel.hytale.protocol.BuilderToolInteraction();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean needsRemoteSync() {
/* 34 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public WaitForDataFrom getWaitForDataFrom() {
/* 40 */     return WaitForDataFrom.Client;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 45 */     (context.getState()).state = (context.getClientState()).state;
/* 46 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 52 */     return "BuilderToolInteraction{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\BuilderToolInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */