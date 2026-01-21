/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.MovementDirection;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
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
/*     */ public class MovementConditionInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<MovementConditionInteraction> CODEC;
/*     */   private static final int FAILED_LABEL_INDEX = 0;
/*     */   private static final int FORWARD_LABEL_INDEX = 1;
/*     */   private static final int BACK_LABEL_INDEX = 2;
/*     */   private static final int LEFT_LABEL_INDEX = 3;
/*     */   private static final int RIGHT_LABEL_INDEX = 4;
/*     */   private static final int FORWARD_LEFT_LABEL_INDEX = 5;
/*     */   private static final int FORWARD_RIGHT_LABEL_INDEX = 6;
/*     */   private static final int BACK_LEFT_LABEL_INDEX = 7;
/*     */   private static final int BACK_RIGHT_LABEL_INDEX = 8;
/*     */   @Nullable
/*     */   private String forward;
/*     */   @Nullable
/*     */   private String back;
/*     */   @Nullable
/*     */   private String left;
/*     */   @Nullable
/*     */   private String right;
/*     */   @Nullable
/*     */   private String forwardLeft;
/*     */   @Nullable
/*     */   private String forwardRight;
/*     */   @Nullable
/*     */   private String backLeft;
/*     */   @Nullable
/*     */   private String backRight;
/*     */   
/*     */   static {
/* 104 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MovementConditionInteraction.class, MovementConditionInteraction::new, SimpleInteraction.CODEC).documentation("An interaction that runs different interactions based on the movement the user is current performing.")).appendInherited(new KeyedCodec("Forward", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.forward = s, interaction -> interaction.forward, (interaction, parent) -> interaction.forward = parent.forward).documentation("The interaction to run if the player is moving forward.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Back", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.back = s, interaction -> interaction.back, (interaction, parent) -> interaction.back = parent.back).documentation("The interaction to run if the player is moving backwards.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Left", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.left = s, interaction -> interaction.left, (interaction, parent) -> interaction.left = parent.left).documentation("The interaction to run if the player is moving left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Right", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.right = s, interaction -> interaction.right, (interaction, parent) -> interaction.right = parent.right).documentation("The interaction to run if the player is moving right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("ForwardLeft", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.forwardLeft = s, interaction -> interaction.forwardLeft, (interaction, parent) -> interaction.forwardLeft = parent.forwardLeft).documentation("The interaction to run if the player is moving forward and left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("ForwardRight", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.forwardRight = s, interaction -> interaction.forwardRight, (interaction, parent) -> interaction.forwardRight = parent.forwardRight).documentation("The interaction to run if the player is moving forward and right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("BackLeft", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.backLeft = s, interaction -> interaction.backLeft, (interaction, parent) -> interaction.backLeft = parent.backLeft).documentation("The interaction to run if the player is moving backwards and left.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("BackRight", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.backRight = s, interaction -> interaction.backRight, (interaction, parent) -> interaction.backRight = parent.backRight).documentation("The interaction to run if the player is moving backwards and right.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
/*     */   }
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
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 166 */     (context.getState()).state = InteractionState.Finished;
/*     */     
/* 168 */     switch ((context.getClientState()).movementDirection) { default: throw new MatchException(null, null);case None: case Forward: case Back: case Left: case Right: case ForwardLeft: case ForwardRight: case BackLeft: case BackRight: break; }  context.jump(context.getLabel(
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 177 */           8));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 184 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 189 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 194 */     (context.getState()).movementDirection = MovementDirection.None;
/* 195 */     context.jump(context.getLabel(0));
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 200 */     Label[] labels = new Label[9];
/*     */     
/* 202 */     for (int i = 0; i < labels.length; i++) {
/* 203 */       labels[i] = builder.createUnresolvedLabel();
/*     */     }
/*     */     
/* 206 */     builder.addOperation((Operation)this, labels);
/*     */     
/* 208 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 210 */     resolve(builder, this.failed, labels[0], endLabel);
/* 211 */     resolve(builder, this.forward, labels[1], endLabel);
/* 212 */     resolve(builder, this.back, labels[2], endLabel);
/* 213 */     resolve(builder, this.left, labels[3], endLabel);
/* 214 */     resolve(builder, this.right, labels[4], endLabel);
/* 215 */     resolve(builder, this.forwardLeft, labels[5], endLabel);
/* 216 */     resolve(builder, this.forwardRight, labels[6], endLabel);
/* 217 */     resolve(builder, this.backLeft, labels[7], endLabel);
/* 218 */     resolve(builder, this.backRight, labels[8], endLabel);
/*     */     
/* 220 */     builder.resolveLabel(endLabel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void resolve(@Nonnull OperationsBuilder builder, @Nullable String id, @Nonnull Label label, @Nonnull Label endLabel) {
/* 232 */     builder.resolveLabel(label);
/*     */     
/* 234 */     if (id != null) {
/* 235 */       Interaction interaction = Interaction.getInteractionOrUnknown(id);
/* 236 */       interaction.compile(builder);
/*     */     } 
/*     */     
/* 239 */     builder.jump(endLabel);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 245 */     return (Interaction)new com.hypixel.hytale.protocol.MovementConditionInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 250 */     super.configurePacket(packet);
/* 251 */     com.hypixel.hytale.protocol.MovementConditionInteraction p = (com.hypixel.hytale.protocol.MovementConditionInteraction)packet;
/* 252 */     p.forward = Interaction.getInteractionIdOrUnknown(this.forward);
/* 253 */     p.back = Interaction.getInteractionIdOrUnknown(this.back);
/* 254 */     p.left = Interaction.getInteractionIdOrUnknown(this.left);
/* 255 */     p.right = Interaction.getInteractionIdOrUnknown(this.right);
/* 256 */     p.forwardLeft = Interaction.getInteractionIdOrUnknown(this.forwardLeft);
/* 257 */     p.forwardRight = Interaction.getInteractionIdOrUnknown(this.forwardRight);
/* 258 */     p.backLeft = Interaction.getInteractionIdOrUnknown(this.backLeft);
/* 259 */     p.backRight = Interaction.getInteractionIdOrUnknown(this.backRight);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\MovementConditionInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */