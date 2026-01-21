/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config;
/*     */ 
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.StringTag;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
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
/*     */ public class SimpleInteraction
/*     */   extends Interaction
/*     */ {
/*     */   public static final BuilderCodec<SimpleInteraction> CODEC;
/*     */   
/*     */   static {
/*  58 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SimpleInteraction.class, SimpleInteraction::new, Interaction.ABSTRACT_CODEC).documentation("A interaction that does nothing other than base interaction features. Can be used for simple delays or triggering animations in between other interactions.")).appendInherited(new KeyedCodec("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.next = s, interaction -> interaction.next, (interaction, parent) -> interaction.next = parent.next).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Failed", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.failed = s, interaction -> interaction.failed, (interaction, parent) -> interaction.failed = parent.failed).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
/*     */   }
/*  60 */   private static final StringTag TAG_NEXT = StringTag.of("Next");
/*  61 */   private static final StringTag TAG_FAILED = StringTag.of("Failed");
/*     */   
/*     */   private static final int FAILED_LABEL_INDEX = 0;
/*     */   
/*     */   @Nullable
/*     */   protected String next;
/*     */   
/*     */   @Nullable
/*     */   protected String failed;
/*     */   
/*     */   protected SimpleInteraction() {}
/*     */   
/*     */   public SimpleInteraction(String id) {
/*  74 */     super(id);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  80 */     return WaitForDataFrom.None;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  85 */     if ((context.getState()).state == InteractionState.Failed && context.hasLabels()) {
/*  86 */       context.jump(context.getLabel(0));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  94 */     if (getWaitForDataFrom() == WaitForDataFrom.Server && context.getServerState() != null && (context.getServerState()).state == InteractionState.Failed) {
/*  95 */       (context.getState()).state = InteractionState.Failed;
/*     */     }
/*     */     
/*  98 */     tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 103 */     if (this.next == null && this.failed == null) {
/* 104 */       builder.addOperation(this);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 110 */     Label failedLabel = builder.createUnresolvedLabel();
/* 111 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 113 */     builder.addOperation(this, new Label[] { failedLabel });
/*     */ 
/*     */     
/* 116 */     if (this.next != null) {
/* 117 */       Interaction nextInteraction = Interaction.getInteractionOrUnknown(this.next);
/* 118 */       nextInteraction.compile(builder);
/*     */     } 
/*     */     
/* 121 */     if (this.failed != null) builder.jump(endLabel);
/*     */ 
/*     */     
/* 124 */     builder.resolveLabel(failedLabel);
/* 125 */     if (this.failed != null) {
/* 126 */       Interaction failedInteraction = Interaction.getInteractionOrUnknown(this.failed);
/* 127 */       failedInteraction.compile(builder);
/*     */     } 
/*     */     
/* 130 */     builder.resolveLabel(endLabel);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 135 */     if (this.next != null && 
/* 136 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_NEXT, this.next)) return true;
/*     */     
/* 138 */     if (this.failed != null && 
/* 139 */       InteractionManager.walkInteraction(collector, context, (CollectorTag)TAG_FAILED, this.failed)) return true;
/*     */     
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 147 */     return (Interaction)new com.hypixel.hytale.protocol.SimpleInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 152 */     super.configurePacket(packet);
/* 153 */     com.hypixel.hytale.protocol.SimpleInteraction p = (com.hypixel.hytale.protocol.SimpleInteraction)packet;
/* 154 */     p.next = Interaction.getInteractionIdOrUnknown(this.next);
/* 155 */     p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 160 */     return (needsRemoteSync(this.next) || needsRemoteSync(this.failed));
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 165 */     return "SimpleInteraction{next='" + this.next + "'failed='" + this.failed + "'} " + super
/*     */ 
/*     */       
/* 168 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\SimpleInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */