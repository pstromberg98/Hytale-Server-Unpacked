/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.MovementStates;
/*     */ import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
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
/*     */ public class ConditionInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<ConditionInteraction> CODEC;
/*     */   @Nullable
/*     */   private GameMode requiredGameMode;
/*     */   @Nullable
/*     */   private Boolean jumping;
/*     */   @Nullable
/*     */   private Boolean swimming;
/*     */   @Nullable
/*     */   private Boolean crouching;
/*     */   @Nullable
/*     */   private Boolean running;
/*     */   @Nullable
/*     */   private Boolean flying;
/*     */   
/*     */   static {
/*  70 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConditionInteraction.class, ConditionInteraction::new, SimpleInteraction.CODEC).documentation("An interaction that is successful if the given conditions are met.")).appendInherited(new KeyedCodec("RequiredGameMode", (Codec)ProtocolCodecs.GAMEMODE), (interaction, s) -> interaction.requiredGameMode = s, interaction -> interaction.requiredGameMode, (interaction, parent) -> interaction.requiredGameMode = parent.requiredGameMode).add()).appendInherited(new KeyedCodec("Jumping", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.jumping = s, interaction -> interaction.jumping, (interaction, parent) -> interaction.jumping = parent.jumping).add()).appendInherited(new KeyedCodec("Swimming", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.swimming = s, interaction -> interaction.swimming, (interaction, parent) -> interaction.swimming = parent.swimming).add()).appendInherited(new KeyedCodec("Crouching", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.crouching = s, interaction -> interaction.crouching, (interaction, parent) -> interaction.crouching = parent.crouching).add()).appendInherited(new KeyedCodec("Running", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.running = s, interaction -> interaction.running, (interaction, parent) -> interaction.running = parent.running).add()).appendInherited(new KeyedCodec("Flying", (Codec)Codec.BOOLEAN), (interaction, s) -> interaction.flying = s, interaction -> interaction.flying, (interaction, parent) -> interaction.flying = parent.flying).documentation("Whether the entity can be flying.").add()).build();
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
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 110 */     boolean success = true;
/*     */     
/* 112 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 113 */     assert commandBuffer != null;
/*     */     
/* 115 */     Ref<EntityStore> ref = context.getEntity();
/* 116 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/*     */ 
/*     */     
/* 119 */     if (this.requiredGameMode != null && playerComponent != null && this.requiredGameMode != playerComponent.getGameMode()) {
/* 120 */       success = false;
/*     */     }
/*     */     
/* 123 */     MovementStatesComponent movementStatesComponent = (MovementStatesComponent)commandBuffer.getComponent(ref, MovementStatesComponent.getComponentType());
/* 124 */     assert movementStatesComponent != null;
/*     */     
/* 126 */     MovementStates movementStates = movementStatesComponent.getMovementStates();
/*     */     
/* 128 */     if (this.jumping != null && this.jumping.booleanValue() != movementStates.jumping) {
/* 129 */       success = false;
/*     */     }
/*     */     
/* 132 */     if (this.swimming != null && this.swimming.booleanValue() != movementStates.swimming) {
/* 133 */       success = false;
/*     */     }
/*     */     
/* 136 */     if (this.crouching != null && this.crouching.booleanValue() != movementStates.crouching) {
/* 137 */       success = false;
/*     */     }
/*     */     
/* 140 */     if (this.running != null && this.running.booleanValue() != movementStates.running) {
/* 141 */       success = false;
/*     */     }
/*     */     
/* 144 */     if (this.flying != null && this.flying.booleanValue() != movementStates.flying) {
/* 145 */       success = false;
/*     */     }
/*     */     
/* 148 */     (context.getState()).state = success ? InteractionState.Finished : InteractionState.Failed;
/*     */     
/* 150 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 156 */     return (Interaction)new com.hypixel.hytale.protocol.ConditionInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 161 */     super.configurePacket(packet);
/* 162 */     com.hypixel.hytale.protocol.ConditionInteraction p = (com.hypixel.hytale.protocol.ConditionInteraction)packet;
/* 163 */     p.requiredGameMode = this.requiredGameMode;
/* 164 */     p.jumping = this.jumping;
/* 165 */     p.swimming = this.swimming;
/* 166 */     p.crouching = this.crouching;
/* 167 */     p.running = this.running;
/* 168 */     p.flying = this.flying;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 174 */     return "ConditionInteraction{requiredGameMode=" + String.valueOf(this.requiredGameMode) + ", jumping=" + this.jumping + ", swimming=" + this.swimming + ", crouching=" + this.crouching + ", running=" + this.running + ", flying=" + this.flying + "} " + super
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 181 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\ConditionInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */