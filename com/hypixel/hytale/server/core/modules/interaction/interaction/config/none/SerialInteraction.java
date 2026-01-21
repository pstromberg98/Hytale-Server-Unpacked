/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
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
/*     */ public class SerialInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<SerialInteraction> CODEC;
/*     */   protected String[] interactions;
/*     */   
/*     */   static {
/*  42 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SerialInteraction.class, SerialInteraction::new, BuilderCodec.abstractBuilder(Interaction.class).build()).documentation("Runs the given interactions in order.")).appendInherited(new KeyedCodec("Interactions", CHILD_ASSET_CODEC_ARRAY), (o, i) -> o.interactions = i, o -> o.interactions, (o, p) -> o.interactions = p.interactions).documentation("A list of interactions to run. They will be executed in the order specified sequentially.").addValidator(Validators.nonNull()).addValidator((Validator)Validators.nonNullArrayElements()).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  48 */     throw new IllegalStateException("Should not be reached");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*  53 */     throw new IllegalStateException("Should not be reached");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/*  58 */     for (int i = 0; i < this.interactions.length; i++) {
/*  59 */       if (InteractionManager.walkInteraction(collector, context, SerialTag.of(i), this.interactions[i])) return true; 
/*     */     } 
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/*  66 */     for (String interaction : this.interactions) {
/*  67 */       Interaction.getInteractionOrUnknown(interaction).compile(builder);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/*  74 */     return (Interaction)new com.hypixel.hytale.protocol.SerialInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/*  79 */     super.configurePacket(packet);
/*  80 */     com.hypixel.hytale.protocol.SerialInteraction p = (com.hypixel.hytale.protocol.SerialInteraction)packet;
/*  81 */     int[] serialInteractions = p.serialInteractions = new int[this.interactions.length];
/*  82 */     for (int i = 0; i < this.interactions.length; i++) {
/*  83 */       serialInteractions[i] = Interaction.getInteractionIdOrUnknown(this.interactions[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  95 */     return WaitForDataFrom.None;
/*     */   }
/*     */   
/*     */   private static class SerialTag implements CollectorTag {
/*     */     private final int index;
/*     */     
/*     */     private SerialTag(int index) {
/* 102 */       this.index = index;
/*     */     }
/*     */     
/*     */     public int getIndex() {
/* 106 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(@Nullable Object o) {
/* 111 */       if (this == o) return true; 
/* 112 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 114 */       SerialTag that = (SerialTag)o;
/* 115 */       return (this.index == that.index);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 120 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 126 */       return "SerialTag{index=" + this.index + "}";
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public static SerialTag of(int index) {
/* 133 */       return new SerialTag(index);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\SerialInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */