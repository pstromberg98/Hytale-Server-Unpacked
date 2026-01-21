/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.client;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.EnumCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.spatial.SpatialStructure;
/*     */ import com.hypixel.hytale.math.range.FloatRange;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.protocol.AppliedForce;
/*     */ import com.hypixel.hytale.protocol.ApplyForceState;
/*     */ import com.hypixel.hytale.protocol.ChangeVelocityType;
/*     */ import com.hypixel.hytale.protocol.FloatRange;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.MovementStates;
/*     */ import com.hypixel.hytale.protocol.RaycastMode;
/*     */ import com.hypixel.hytale.protocol.Vector3f;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*     */ import com.hypixel.hytale.server.core.io.NetworkSerializable;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
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
/*     */ public class ApplyForceInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<ApplyForceInteraction> CODEC;
/*     */   private static final int LABEL_COUNT = 3;
/*     */   private static final int NEXT_LABEL_INDEX = 0;
/*     */   private static final int GROUND_LABEL_INDEX = 1;
/*     */   private static final int COLLISION_LABEL_INDEX = 2;
/*     */   private static final float SPATIAL_STRUCTURE_RADIUS = 1.5F;
/*     */   
/*     */   static {
/* 194 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ApplyForceInteraction.class, ApplyForceInteraction::new, SimpleInteraction.CODEC).documentation("Applies a force to the user, optionally waiting for a condition to met before continuing.")).appendInherited(new KeyedCodec("Direction", (Codec)Vector3d.CODEC), (o, i) -> (o.forces[0]).direction = i.normalize(), o -> null, (o, p) -> {  }).documentation("The direction of the force to apply.").add()).appendInherited(new KeyedCodec("AdjustVertical", (Codec)Codec.BOOLEAN), (o, i) -> (o.forces[0]).adjustVertical = i.booleanValue(), o -> null, (o, p) -> {  }).documentation("Whether the force should be adjusted based on the vertical look of the user.").add()).appendInherited(new KeyedCodec("Force", (Codec)Codec.DOUBLE), (o, i) -> (o.forces[0]).force = i.doubleValue(), o -> null, (o, p) -> {  }).documentation("The size of the force to apply.").add()).appendInherited(new KeyedCodec("Forces", (Codec)new ArrayCodec((Codec)Force.CODEC, x$0 -> new Force[x$0])), (o, i) -> o.forces = i, o -> o.forces, (o, p) -> o.forces = p.forces).documentation("A collection of forces to apply to the user.\nReplaces `Direction`, `AdjustVertical` and `Force` if used.").add()).appendInherited(new KeyedCodec("Duration", (Codec)Codec.FLOAT), (o, f) -> o.duration = f.floatValue(), o -> Float.valueOf(o.duration), (o, p) -> o.duration = p.duration).addValidator(Validators.greaterThanOrEqual(Float.valueOf(0.0F))).documentation("The duration for which force should be continuously applied. If 0, force is applied on first run.").add()).appendInherited(new KeyedCodec("VerticalClamp", (Codec)FloatRange.CODEC), (o, i) -> o.verticalClamp = i, o -> o.verticalClamp, (o, p) -> o.verticalClamp = p.verticalClamp).documentation("The angles in degrees to clamp the look angle to when adjusting the force").add()).appendInherited(new KeyedCodec("WaitForGround", (Codec)Codec.BOOLEAN), (o, i) -> o.waitForGround = i.booleanValue(), o -> Boolean.valueOf(o.waitForGround), (o, p) -> o.waitForGround = p.waitForGround).documentation("Determines whether or not on ground should be checked").add()).appendInherited(new KeyedCodec("WaitForCollision", (Codec)Codec.BOOLEAN), (o, i) -> o.waitForCollision = i.booleanValue(), o -> Boolean.valueOf(o.waitForCollision), (o, p) -> o.waitForCollision = p.waitForCollision).documentation("Determines whether or not collision should be checked").add()).appendInherited(new KeyedCodec("RaycastDistance", (Codec)Codec.FLOAT), (o, i) -> o.raycastDistance = i.floatValue(), o -> Float.valueOf(o.raycastDistance), (o, p) -> o.raycastDistance = p.raycastDistance).documentation("The distance of the raycast for the collision check").add()).appendInherited(new KeyedCodec("RaycastHeightOffset", (Codec)Codec.FLOAT), (o, i) -> o.raycastHeightOffset = i.floatValue(), o -> Float.valueOf(o.raycastHeightOffset), (o, p) -> o.raycastHeightOffset = p.raycastHeightOffset).documentation("The height offset of the raycast for the collision check (default 0)").add()).appendInherited(new KeyedCodec("RaycastMode", (Codec)new EnumCodec(RaycastMode.class)), (o, i) -> o.raycastMode = i, o -> o.raycastMode, (o, p) -> o.raycastMode = p.raycastMode).documentation("The type of raycast performed for the collision check").add()).appendInherited(new KeyedCodec("GroundCheckDelay", (Codec)Codec.FLOAT), (o, i) -> o.groundCheckDelay = i.floatValue(), o -> Float.valueOf(o.groundCheckDelay), (o, p) -> o.groundCheckDelay = p.groundCheckDelay).documentation("The delay in seconds before checking if on ground").add()).appendInherited(new KeyedCodec("CollisionCheckDelay", (Codec)Codec.FLOAT), (o, i) -> o.collisionCheckDelay = i.floatValue(), o -> Float.valueOf(o.collisionCheckDelay), (o, p) -> o.collisionCheckDelay = p.collisionCheckDelay).documentation("The delay in seconds before checking entity collision").add()).appendInherited(new KeyedCodec("ChangeVelocityType", (Codec)ProtocolCodecs.CHANGE_VELOCITY_TYPE_CODEC), (o, i) -> o.changeVelocityType = i, o -> o.changeVelocityType, (o, p) -> o.changeVelocityType = p.changeVelocityType).documentation("Configures how the velocity gets applied to the user.").add()).appendInherited(new KeyedCodec("VelocityConfig", (Codec)VelocityConfig.CODEC), (o, i) -> o.velocityConfig = i, o -> o.velocityConfig, (o, p) -> o.velocityConfig = p.velocityConfig).documentation("Specific configuration options that control how the velocity is affected.").add()).appendInherited(new KeyedCodec("GroundNext", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.groundInteraction = s, interaction -> interaction.groundInteraction, (interaction, parent) -> interaction.groundInteraction = parent.groundInteraction).documentation("The interaction to run if on-ground is apparent.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("CollisionNext", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.collisionInteraction = s, interaction -> interaction.collisionInteraction, (interaction, parent) -> interaction.collisionInteraction = parent.collisionInteraction).documentation("The interaction to run if collision is apparent.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).build();
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
/*     */   @Nonnull
/* 224 */   private ChangeVelocityType changeVelocityType = ChangeVelocityType.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 230 */   private Force[] forces = new Force[] { new Force() };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 236 */   private float duration = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean waitForGround = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean waitForCollision = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 255 */   private float groundCheckDelay = 0.1F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 260 */   private float collisionCheckDelay = 0.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 265 */   private float raycastDistance = 1.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 270 */   private float raycastHeightOffset = 0.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 275 */   private RaycastMode raycastMode = RaycastMode.FollowMotion;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 281 */   private VelocityConfig velocityConfig = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 287 */   private FloatRange verticalClamp = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 293 */   private String groundInteraction = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 299 */   private String collisionInteraction = null;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 305 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 310 */     InteractionSyncData contextState = context.getState();
/*     */     
/* 312 */     if (firstRun) {
/* 313 */       contextState.state = InteractionState.NotFinished;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 318 */     InteractionSyncData clientState = context.getClientState();
/* 319 */     assert clientState != null;
/*     */     
/* 321 */     ApplyForceState applyForceState = clientState.applyForceState;
/*     */     
/* 323 */     switch (applyForceState) {
/*     */       case Add:
/* 325 */         contextState.state = InteractionState.Finished;
/* 326 */         context.jump(context.getLabel(1));
/*     */         break;
/*     */       case Set:
/* 329 */         contextState.state = InteractionState.Finished;
/* 330 */         context.jump(context.getLabel(2));
/*     */         break;
/*     */       case null:
/* 333 */         contextState.state = InteractionState.Finished;
/* 334 */         context.jump(context.getLabel(0)); break;
/*     */       default:
/* 336 */         contextState.state = InteractionState.NotFinished;
/*     */         break;
/*     */     } 
/* 339 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 344 */     InteractionSyncData contextState = context.getState();
/* 345 */     Ref<EntityStore> entityRef = context.getEntity();
/*     */     
/* 347 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 348 */     assert commandBuffer != null;
/*     */     
/* 350 */     Store<EntityStore> entityStore = commandBuffer.getStore();
/*     */     
/* 352 */     if (firstRun || (this.duration > 0.0F && time < this.duration)) {
/* 353 */       HeadRotation headRotationComponent = (HeadRotation)entityStore.getComponent(entityRef, HeadRotation.getComponentType());
/* 354 */       assert headRotationComponent != null;
/*     */       
/* 356 */       Velocity velocityComponent = (Velocity)entityStore.getComponent(entityRef, Velocity.getComponentType());
/* 357 */       assert velocityComponent != null;
/*     */       
/* 359 */       Vector3f entityHeadRotation = headRotationComponent.getRotation();
/*     */       
/* 361 */       ChangeVelocityType velocityType = this.changeVelocityType;
/*     */       
/* 363 */       for (Force force : this.forces) {
/* 364 */         Vector3d forceDirection = force.direction.clone();
/* 365 */         if (force.adjustVertical) {
/* 366 */           float lookX = entityHeadRotation.x;
/* 367 */           if (this.verticalClamp != null) {
/* 368 */             lookX = MathUtil.clamp(lookX, this.verticalClamp
/*     */                 
/* 370 */                 .getInclusiveMin() * 0.017453292F, this.verticalClamp
/* 371 */                 .getInclusiveMax() * 0.017453292F);
/*     */           }
/*     */           
/* 374 */           forceDirection.rotateX(lookX);
/*     */         } 
/*     */         
/* 377 */         forceDirection.scale(force.force);
/* 378 */         forceDirection.rotateY(entityHeadRotation.y);
/*     */         
/* 380 */         switch (velocityType) { case Add:
/* 381 */             velocityComponent.addInstruction(forceDirection, null, ChangeVelocityType.Add); break;
/* 382 */           case Set: velocityComponent.addInstruction(forceDirection, null, ChangeVelocityType.Set);
/*     */             break; }
/*     */         
/* 385 */         velocityType = ChangeVelocityType.Add;
/*     */       } 
/* 387 */       contextState.state = InteractionState.NotFinished;
/*     */       
/*     */       return;
/*     */     } 
/* 391 */     MovementStatesComponent movementStatesComponent = (MovementStatesComponent)entityStore.getComponent(entityRef, MovementStatesComponent.getComponentType());
/* 392 */     assert movementStatesComponent != null;
/*     */     
/* 394 */     TransformComponent transformComponent = (TransformComponent)entityStore.getComponent(entityRef, TransformComponent.getComponentType());
/* 395 */     assert transformComponent != null;
/*     */     
/* 397 */     MovementStates entityMovementStates = movementStatesComponent.getMovementStates();
/*     */ 
/*     */ 
/*     */     
/* 401 */     SpatialResource<Ref<EntityStore>, EntityStore> networkSendableSpatialComponent = (SpatialResource<Ref<EntityStore>, EntityStore>)entityStore.getResource(EntityModule.get().getNetworkSendableSpatialResourceType());
/* 402 */     SpatialStructure<Ref<EntityStore>> spatialStructure = networkSendableSpatialComponent.getSpatialStructure();
/* 403 */     ObjectList<Ref<EntityStore>> entities = SpatialResource.getThreadLocalReferenceList();
/* 404 */     spatialStructure.collect(transformComponent.getPosition(), 1.5D, (List)entities);
/*     */     
/* 406 */     boolean checkGround = (time >= this.groundCheckDelay);
/* 407 */     boolean onGround = (checkGround && this.waitForGround && (entityMovementStates.onGround || entityMovementStates.inFluid || entityMovementStates.climbing));
/*     */     
/* 409 */     boolean checkCollision = (time >= this.collisionCheckDelay);
/* 410 */     boolean collided = (checkCollision && this.waitForCollision && entities.size() > 1);
/*     */     
/* 412 */     boolean instantlyComplete = (this.runTime <= 0.0F && !this.waitForGround && !this.waitForCollision);
/* 413 */     boolean timerFinished = (instantlyComplete || (this.runTime > 0.0F && time >= this.runTime));
/*     */     
/* 415 */     contextState.applyForceState = ApplyForceState.Waiting;
/*     */     
/* 417 */     if (onGround) {
/* 418 */       contextState.applyForceState = ApplyForceState.Ground;
/* 419 */       contextState.state = InteractionState.Finished;
/* 420 */       context.jump(context.getLabel(1));
/* 421 */     } else if (collided) {
/* 422 */       contextState.applyForceState = ApplyForceState.Collision;
/* 423 */       contextState.state = InteractionState.Finished;
/* 424 */       context.jump(context.getLabel(2));
/* 425 */     } else if (timerFinished) {
/* 426 */       contextState.applyForceState = ApplyForceState.Timer;
/* 427 */       contextState.state = InteractionState.Finished;
/* 428 */       context.jump(context.getLabel(0));
/*     */     } else {
/* 430 */       contextState.state = InteractionState.NotFinished;
/*     */     } 
/*     */     
/* 433 */     super.simulateTick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 438 */     Label[] labels = new Label[3];
/*     */     
/* 440 */     for (int i = 0; i < labels.length; i++) {
/* 441 */       labels[i] = builder.createUnresolvedLabel();
/*     */     }
/*     */     
/* 444 */     builder.addOperation((Operation)this, labels);
/*     */     
/* 446 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 448 */     resolve(builder, this.next, labels[0], endLabel);
/* 449 */     resolve(builder, (this.groundInteraction == null) ? this.next : this.groundInteraction, labels[1], endLabel);
/* 450 */     resolve(builder, (this.collisionInteraction == null) ? this.next : this.collisionInteraction, labels[2], endLabel);
/*     */     
/* 452 */     builder.resolveLabel(endLabel);
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
/* 464 */     builder.resolveLabel(label);
/*     */     
/* 466 */     if (id != null) {
/* 467 */       Interaction interaction = Interaction.getInteractionOrUnknown(id);
/* 468 */       interaction.compile(builder);
/*     */     } 
/*     */     
/* 471 */     builder.jump(endLabel);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 476 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 482 */     return (Interaction)new com.hypixel.hytale.protocol.ApplyForceInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 487 */     super.configurePacket(packet);
/* 488 */     com.hypixel.hytale.protocol.ApplyForceInteraction p = (com.hypixel.hytale.protocol.ApplyForceInteraction)packet;
/* 489 */     p.changeVelocityType = this.changeVelocityType;
/* 490 */     p.forces = (AppliedForce[])Arrays.<Force>stream(this.forces).map(Force::toPacket).toArray(x$0 -> new AppliedForce[x$0]);
/* 491 */     p.duration = this.duration;
/* 492 */     p.waitForGround = this.waitForGround;
/* 493 */     p.waitForCollision = this.waitForCollision;
/* 494 */     p.groundCheckDelay = this.groundCheckDelay;
/* 495 */     p.collisionCheckDelay = this.collisionCheckDelay;
/* 496 */     p.velocityConfig = (this.velocityConfig == null) ? null : this.velocityConfig.toPacket();
/* 497 */     if (this.verticalClamp != null) {
/* 498 */       p.verticalClamp = new FloatRange(this.verticalClamp.getInclusiveMin() * 0.017453292F, this.verticalClamp.getInclusiveMax() * 0.017453292F);
/*     */     }
/* 500 */     p.collisionNext = Interaction.getInteractionIdOrUnknown((this.collisionInteraction == null) ? this.next : this.collisionInteraction);
/* 501 */     p.groundNext = Interaction.getInteractionIdOrUnknown((this.groundInteraction == null) ? this.next : this.groundInteraction);
/* 502 */     p.raycastDistance = this.raycastDistance;
/* 503 */     p.raycastHeightOffset = this.raycastHeightOffset;
/* 504 */     p.raycastMode = this.raycastMode;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 510 */     return "ApplyForceInteraction{changeVelocityType=" + String.valueOf(this.changeVelocityType) + ", waitForGround=" + this.waitForGround + "} " + super
/*     */ 
/*     */       
/* 513 */       .toString();
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
/*     */   public static class Force
/*     */     implements NetworkSerializable<AppliedForce>
/*     */   {
/*     */     public static final BuilderCodec<Force> CODEC;
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
/*     */     static {
/* 550 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Force.class, Force::new).appendInherited(new KeyedCodec("Direction", (Codec)Vector3d.CODEC), (o, i) -> o.direction = i, o -> o.direction, (o, p) -> o.direction = p.direction).documentation("The direction of the force to apply.").addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("AdjustVertical", (Codec)Codec.BOOLEAN), (o, i) -> o.adjustVertical = i.booleanValue(), o -> Boolean.valueOf(o.adjustVertical), (o, p) -> o.adjustVertical = p.adjustVertical).documentation("Whether the force should be adjusted based on the vertical look of the user.").add()).appendInherited(new KeyedCodec("Force", (Codec)Codec.DOUBLE), (o, i) -> o.force = i.doubleValue(), o -> Double.valueOf(o.force), (o, p) -> o.force = p.force).documentation("The size of the force to apply.").add()).afterDecode(o -> o.direction.normalize())).build();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/* 555 */     private Vector3d direction = Vector3d.UP;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean adjustVertical = false;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 566 */     private double force = 1.0D;
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public AppliedForce toPacket() {
/* 571 */       return new AppliedForce(new Vector3f((float)this.direction.x, (float)this.direction.y, (float)this.direction.z), this.adjustVertical, (float)this.force);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\client\ApplyForceInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */