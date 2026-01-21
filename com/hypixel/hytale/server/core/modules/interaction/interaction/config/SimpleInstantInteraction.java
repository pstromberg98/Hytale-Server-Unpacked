/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.protocol.InteractionState;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class SimpleInstantInteraction
/*    */   extends SimpleInteraction
/*    */ {
/* 18 */   public static final BuilderCodec<SimpleInstantInteraction> CODEC = BuilderCodec.abstractBuilder(SimpleInstantInteraction.class, SimpleInteraction.CODEC)
/* 19 */     .build();
/*    */   
/*    */   public SimpleInstantInteraction(String id) {
/* 22 */     super(id);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected SimpleInstantInteraction() {}
/*    */ 
/*    */   
/*    */   protected final void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 31 */     if (!firstRun)
/*    */       return; 
/* 33 */     firstRun(type, context, cooldownHandler);
/* 34 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 39 */     if (!firstRun)
/*    */       return; 
/* 41 */     simulateFirstRun(type, context, cooldownHandler);
/*    */ 
/*    */     
/* 44 */     if (getWaitForDataFrom() == WaitForDataFrom.Server && context.getServerState() != null && (context.getServerState()).state == InteractionState.Failed) {
/* 45 */       (context.getState()).state = InteractionState.Failed;
/*    */     }
/* 47 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract void firstRun(@Nonnull InteractionType paramInteractionType, @Nonnull InteractionContext paramInteractionContext, @Nonnull CooldownHandler paramCooldownHandler);
/*    */ 
/*    */ 
/*    */   
/*    */   protected void simulateFirstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 58 */     firstRun(type, context, cooldownHandler);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 63 */     return "SimpleInstantInteraction{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\SimpleInstantInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */