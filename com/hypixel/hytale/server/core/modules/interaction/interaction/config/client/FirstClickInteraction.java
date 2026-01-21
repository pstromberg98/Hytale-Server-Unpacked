/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.IInteractionSimulationHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.function.Supplier;
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
/*     */ public class FirstClickInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<FirstClickInteraction> CODEC;
/*     */   
/*     */   static {
/*  52 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FirstClickInteraction.class, FirstClickInteraction::new, Interaction.ABSTRACT_CODEC).documentation("An interaction that runs a different interaction based on if this chain was from a click or due to the key being held down.")).appendInherited(new KeyedCodec("Click", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.click = s, interaction -> interaction.click, (interaction, parent) -> interaction.click = parent.click).documentation("The interaction to run if this chain was initiated by a click.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Held", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.held = s, interaction -> interaction.held, (interaction, parent) -> interaction.held = parent.held).documentation("The interaction to run if this chain was initiated by holding down the key.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  58 */   public static final StringTag TAG_CLICK = StringTag.of("Click");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  64 */   public static final StringTag TAG_HELD = StringTag.of("Held");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int HELD_LABEL_INDEX = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String click;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String held;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  86 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  91 */     InteractionSyncData clientState = context.getClientState();
/*  92 */     assert clientState != null;
/*     */     
/*  94 */     if (clientState.state == InteractionState.Failed && context.hasLabels()) {
/*  95 */       (context.getState()).state = InteractionState.Failed;
/*  96 */       context.jump(context.getLabel(0));
/*     */       return;
/*     */     } 
/*  99 */     (context.getState()).state = InteractionState.Finished;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 104 */     Ref<EntityStore> ref = context.getEntity();
/* 105 */     InteractionManager interactionManager = context.getInteractionManager();
/* 106 */     assert interactionManager != null;
/*     */     
/* 108 */     IInteractionSimulationHandler simulationHandler = interactionManager.getInteractionSimulationHandler();
/* 109 */     if (!simulationHandler.isCharging(firstRun, time, type, context, ref, cooldownHandler)) {
/* 110 */       (context.getState()).state = InteractionState.Finished;
/*     */     } else {
/* 112 */       (context.getState()).state = InteractionState.Failed;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 118 */     if (this.click == null && this.held == null) {
/* 119 */       builder.addOperation((Operation)this);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 125 */     Label failedLabel = builder.createUnresolvedLabel();
/* 126 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 128 */     builder.addOperation((Operation)this, new Label[] { failedLabel });
/*     */ 
/*     */     
/* 131 */     if (this.click != null) {
/* 132 */       Interaction nextInteraction = Interaction.getInteractionOrUnknown(this.click);
/* 133 */       nextInteraction.compile(builder);
/*     */     } 
/*     */     
/* 136 */     if (this.held != null) {
/* 137 */       builder.jump(endLabel);
/*     */     }
/*     */ 
/*     */     
/* 141 */     builder.resolveLabel(failedLabel);
/* 142 */     if (this.held != null) {
/* 143 */       Interaction failedInteraction = Interaction.getInteractionOrUnknown(this.held);
/* 144 */       failedInteraction.compile(builder);
/*     */     } 
/*     */     
/* 147 */     builder.resolveLabel(endLabel);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 152 */     if (this.click != null && 
/* 153 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_CLICK, this.click)) return true;
/*     */     
/* 155 */     if (this.held != null && 
/* 156 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_HELD, this.held)) {
/* 157 */       return true;
/*     */     }
/*     */     
/* 160 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 166 */     return (Interaction)new com.hypixel.hytale.protocol.FirstClickInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 171 */     super.configurePacket(packet);
/* 172 */     com.hypixel.hytale.protocol.FirstClickInteraction p = (com.hypixel.hytale.protocol.FirstClickInteraction)packet;
/* 173 */     p.click = Interaction.getInteractionIdOrUnknown(this.click);
/* 174 */     p.held = Interaction.getInteractionIdOrUnknown(this.held);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 179 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 185 */     return "FirstClickInteraction{click='" + this.click + "', held='" + this.held + "'} " + super
/*     */ 
/*     */       
/* 188 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\FirstClickInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */