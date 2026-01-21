/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class ReplaceInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<ReplaceInteraction> CODEC;
/*     */   
/*     */   static {
/*  54 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ReplaceInteraction.class, ReplaceInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Runs the interaction defined by the interaction variables if defined.")).appendInherited(new KeyedCodec("DefaultValue", (Codec)RootInteraction.CHILD_ASSET_CODEC), (i, s) -> i.defaultValue = s, i -> i.defaultValue, (i, parent) -> i.defaultValue = parent.defaultValue).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Var", (Codec)Codec.STRING), (i, s) -> i.variable = s, i -> i.variable, (i, parent) -> i.variable = parent.variable).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("DefaultOk", (Codec)Codec.BOOLEAN), (i, s) -> i.defaultOk = s.booleanValue(), i -> Boolean.valueOf(i.defaultOk), (i, parent) -> i.defaultOk = parent.defaultOk).add()).build();
/*  55 */   } private static final StringTag TAG_DEFAULT = StringTag.of("Default");
/*  56 */   private static final StringTag TAG_VARS = StringTag.of("Vars");
/*     */   
/*     */   @Nullable
/*     */   protected String defaultValue;
/*     */   
/*     */   protected String variable;
/*     */   
/*     */   protected boolean defaultOk;
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  67 */     return WaitForDataFrom.None;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  72 */     if (Interaction.failed((context.getState()).state))
/*  73 */       return;  if (!firstRun)
/*     */       return; 
/*  75 */     doReplace(context, true);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  80 */     if (Interaction.failed((context.getState()).state))
/*  81 */       return;  if (!firstRun)
/*     */       return; 
/*  83 */     doReplace(context, false);
/*     */   }
/*     */   
/*     */   private void doReplace(@Nonnull InteractionContext context, boolean log) {
/*  87 */     Map<String, String> vars = context.getInteractionVars();
/*  88 */     String next = (vars == null) ? null : vars.get(this.variable);
/*     */     
/*  90 */     if (next == null && !this.defaultOk && log)
/*     */     {
/*  92 */       ((HytaleLogger.Api)HytaleLogger.getLogger()
/*  93 */         .at(Level.SEVERE)
/*  94 */         .atMostEvery(1, TimeUnit.MINUTES))
/*  95 */         .log("Missing replacement interactions for interaction: %s for var %s on item %s", this.id, this.variable, context.getHeldItem());
/*     */     }
/*     */     
/*  98 */     if (next == null) next = this.defaultValue;
/*     */     
/* 100 */     if (next == null) {
/* 101 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 105 */     RootInteraction nextInteraction = RootInteraction.getRootInteractionOrUnknown(next);
/* 106 */     (context.getState()).state = InteractionState.Finished;
/* 107 */     context.execute(nextInteraction);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 119 */     if (this.defaultValue != null && 
/* 120 */       InteractionManager.walkInteractions(collector, context, (CollectorTag)TAG_DEFAULT, RootInteraction.getRootInteractionOrUnknown(this.defaultValue).getInteractionIds())) {
/* 121 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 125 */     Map<String, String> vars = context.getInteractionVars();
/* 126 */     if (vars == null) return false;
/*     */     
/* 128 */     String interactionIds = vars.get(this.variable);
/* 129 */     if (interactionIds == null) return false; 
/* 130 */     return InteractionManager.walkInteractions(collector, context, (CollectorTag)TAG_VARS, RootInteraction.getRootInteractionOrUnknown(interactionIds).getInteractionIds());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 136 */     return (Interaction)new com.hypixel.hytale.protocol.ReplaceInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 141 */     super.configurePacket(packet);
/* 142 */     com.hypixel.hytale.protocol.ReplaceInteraction p = (com.hypixel.hytale.protocol.ReplaceInteraction)packet;
/* 143 */     p.defaultValue = RootInteraction.getRootInteractionIdOrUnknown(this.defaultValue);
/* 144 */     p.variable = this.variable;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 150 */     return "ReplaceInteraction{defaultValue='" + this.defaultValue + "', variable='" + this.variable + "', defaultOk=" + this.defaultOk + "} " + super
/*     */ 
/*     */ 
/*     */       
/* 154 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\ReplaceInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */