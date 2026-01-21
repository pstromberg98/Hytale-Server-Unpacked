/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.EnumCodec;
/*     */ import com.hypixel.hytale.protocol.CameraActionType;
/*     */ import com.hypixel.hytale.protocol.CameraPerspectiveType;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
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
/*     */ public class CameraInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<CameraInteraction> CODEC;
/*     */   protected CameraActionType action;
/*     */   protected CameraPerspectiveType perspective;
/*     */   protected boolean persistCameraState;
/*     */   protected float cameraInteractionTime;
/*     */   
/*     */   static {
/*  57 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CameraInteraction.class, CameraInteraction::new, SimpleInteraction.CODEC).documentation("Adjusts the camera of the user.")).appendInherited(new KeyedCodec("PersistCameraState", (Codec)Codec.BOOLEAN), (i, s) -> i.persistCameraState = s.booleanValue(), i -> Boolean.valueOf(i.persistCameraState), (i, parent) -> i.persistCameraState = parent.persistCameraState).documentation("Should the camera state from this interaction persist to the next camera interaction. If the next interaction is null or not a camera interaction then this field does nothing.").add()).appendInherited(new KeyedCodec("Action", (Codec)new EnumCodec(CameraActionType.class)), (i, s) -> i.action = s, i -> i.action, (i, parent) -> i.action = parent.action).documentation("What kind of camera action should we take").add()).appendInherited(new KeyedCodec("Perspective", (Codec)new EnumCodec(CameraPerspectiveType.class)), (i, s) -> i.perspective = s, i -> i.perspective, (i, parent) -> i.perspective = parent.perspective).documentation("What camera perspective we want this interaction to take place in").add()).appendInherited(new KeyedCodec("CameraInteractionTime", (Codec)Codec.FLOAT), (i, s) -> i.cameraInteractionTime = s.floatValue(), i -> Float.valueOf(i.cameraInteractionTime), (i, parent) -> i.cameraInteractionTime = parent.cameraInteractionTime).documentation("How long this camera action lasts for").add()).build();
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
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/*  79 */     return (Interaction)new com.hypixel.hytale.protocol.CameraInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/*  84 */     super.configurePacket(packet);
/*  85 */     com.hypixel.hytale.protocol.CameraInteraction p = (com.hypixel.hytale.protocol.CameraInteraction)packet;
/*  86 */     p.cameraAction = this.action;
/*  87 */     p.cameraPerspective = this.perspective;
/*  88 */     p.cameraPersist = this.persistCameraState;
/*  89 */     p.cameraInteractionTime = this.cameraInteractionTime;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/*  95 */     return "CameraInteraction{action=" + String.valueOf(this.action) + ", perspective='" + String.valueOf(this.perspective) + "', persistCameraState='" + this.persistCameraState + "', cameraInteractionTime='" + this.cameraInteractionTime + "'} " + super
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 100 */       .toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 105 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */     
/* 107 */     InteractionSyncData clientState = context.getClientState();
/* 108 */     assert clientState != null;
/*     */     
/* 110 */     (context.getState()).state = clientState.state;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 116 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 121 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\CameraInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */