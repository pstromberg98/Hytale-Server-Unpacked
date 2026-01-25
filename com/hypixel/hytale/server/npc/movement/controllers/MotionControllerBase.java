/*      */ package com.hypixel.hytale.server.npc.movement.controllers;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.math.shape.Box;
/*      */ import com.hypixel.hytale.math.util.MathUtil;
/*      */ import com.hypixel.hytale.math.vector.Vector3d;
/*      */ import com.hypixel.hytale.math.vector.Vector3f;
/*      */ import com.hypixel.hytale.protocol.GameMode;
/*      */ import com.hypixel.hytale.protocol.MovementSettings;
/*      */ import com.hypixel.hytale.protocol.MovementStates;
/*      */ import com.hypixel.hytale.protocol.Rangef;
/*      */ import com.hypixel.hytale.protocol.VelocityThresholdStyle;
/*      */ import com.hypixel.hytale.server.core.asset.modifiers.MovementEffects;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*      */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*      */ import com.hypixel.hytale.server.core.asset.type.model.config.camera.CameraSettings;
/*      */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*      */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
/*      */ import com.hypixel.hytale.server.core.modules.collision.BlockCollisionData;
/*      */ import com.hypixel.hytale.server.core.modules.collision.BoxBlockIntersectionEvaluator;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionConfig;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionModuleConfig;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
/*      */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
/*      */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
/*      */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*      */ import com.hypixel.hytale.server.core.modules.physics.component.PhysicsValues;
/*      */ import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
/*      */ import com.hypixel.hytale.server.core.modules.splitvelocity.SplitVelocity;
/*      */ import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*      */ import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
/*      */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*      */ import com.hypixel.hytale.server.npc.movement.MotionKind;
/*      */ import com.hypixel.hytale.server.npc.movement.NavState;
/*      */ import com.hypixel.hytale.server.npc.movement.Steering;
/*      */ import com.hypixel.hytale.server.npc.movement.controllers.builders.BuilderMotionControllerBase;
/*      */ import com.hypixel.hytale.server.npc.role.Role;
/*      */ import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
/*      */ import com.hypixel.hytale.server.npc.util.NPCPhysicsMath;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import java.util.List;
/*      */ import java.util.function.BiPredicate;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ public abstract class MotionControllerBase implements MotionController {
/*      */   public static final double FORCE_SCALE = 5.0D;
/*   63 */   protected static final HytaleLogger LOGGER = NPCPlugin.get().getLogger();
/*      */   
/*      */   public static final double BISECT_DIST = 0.05D;
/*      */   
/*      */   public static final double FILTER_COEFFICIENT = 0.7D;
/*      */   
/*      */   public static final double DOT_PRODUCT_EPSILON = 0.001D;
/*      */   public static final double DEFAULT_BLOCK_DRAG = 0.82D;
/*      */   public static final boolean DEBUG_APPLIED_FORCES = false;
/*      */   @Nonnull
/*      */   protected final NPCEntity entity;
/*      */   protected final String type;
/*      */   protected final double epsilonSpeed;
/*      */   protected final float epsilonAngle;
/*      */   protected final double forceVelocityDamping;
/*      */   protected final double maxHorizontalSpeed;
/*      */   protected final double fastMotionThreshold;
/*      */   protected final double fastMotionThresholdRange;
/*      */   protected final float maxHeadRotationSpeed;
/*      */   protected Role role;
/*      */   protected double inertia;
/*      */   protected double knockbackScale;
/*      */   protected double gravity;
/*      */   @Nullable
/*      */   protected float[] headPitchAngleRange;
/*      */   protected boolean debugModeSteer;
/*      */   protected boolean debugModeMove;
/*      */   protected boolean debugModeCollisions;
/*      */   protected boolean debugModeBlockCollisions;
/*      */   protected boolean debugModeProbeBlockCollisions;
/*      */   protected boolean debugModeValidatePositions;
/*      */   protected boolean debugModeOverlaps;
/*      */   protected boolean debugModeValidateMath;
/*   96 */   protected final Vector3d position = new Vector3d();
/*   97 */   protected final Box collisionBoundingBox = new Box();
/*   98 */   protected final CollisionResult collisionResult = new CollisionResult();
/*   99 */   protected final Vector3d translation = new Vector3d();
/*      */   
/*  101 */   protected final Vector3d bisectValidPosition = new Vector3d();
/*  102 */   protected final Vector3d bisectInvalidPosition = new Vector3d();
/*  103 */   protected final Vector3d lastValidPosition = new Vector3d();
/*      */   
/*  105 */   protected final Vector3d forceVelocity = new Vector3d();
/*  106 */   protected final Vector3d appliedForce = new Vector3d();
/*      */   protected boolean ignoreDamping;
/*  108 */   protected final List<AppliedVelocity> appliedVelocities = (List<AppliedVelocity>)new ObjectArrayList();
/*      */   
/*      */   protected boolean isObstructed;
/*      */   
/*      */   protected NavState navState;
/*      */   
/*      */   protected double throttleDuration;
/*      */   
/*      */   protected double targetDeltaSquared;
/*      */   
/*      */   protected boolean recomputePath;
/*  119 */   protected final Vector3d worldNormal = Vector3d.UP;
/*      */   
/*  121 */   protected final Vector3d worldAntiNormal = Vector3d.DOWN;
/*      */ 
/*      */   
/*  124 */   protected final Vector3d componentSelector = new Vector3d(1.0D, 0.0D, 1.0D);
/*  125 */   protected final Vector3d planarComponentSelector = new Vector3d(1.0D, 0.0D, 1.0D);
/*      */   protected boolean enableTriggers = true;
/*      */   protected boolean enableBlockDamage = true;
/*      */   protected boolean isReceivingBlockDamage;
/*      */   protected boolean isAvoidingBlockDamage = true;
/*      */   protected boolean requiresPreciseMovement;
/*      */   protected boolean requiresDepthProbing;
/*      */   protected boolean havePreciseMovementTarget;
/*      */   @Nonnull
/*  134 */   protected Vector3d preciseMovementTarget = new Vector3d();
/*      */   protected boolean isRelaxedMoveConstraints;
/*      */   protected boolean isBlendingHeading;
/*      */   protected double blendHeading;
/*      */   protected boolean haveBlendHeadingPosition;
/*      */   @Nonnull
/*  140 */   protected Vector3d blendHeadingPosition = new Vector3d();
/*      */   
/*  142 */   protected double blendLevelAtTargetPosition = 0.5D;
/*      */   
/*      */   protected boolean fastMotionKind;
/*      */   
/*      */   protected boolean idleMotionKind;
/*      */   
/*      */   protected boolean horizontalIdleKind;
/*      */   
/*      */   protected double moveSpeed;
/*      */   
/*      */   protected double previousSpeed;
/*      */   protected MotionKind motionKind;
/*      */   protected MotionKind lastMovementStateUpdatedMotionKind;
/*      */   protected MotionKind previousMotionKind;
/*      */   protected double effectHorizontalSpeedMultiplier;
/*      */   protected boolean cachedMovementBlocked;
/*      */   private float yaw;
/*      */   private float pitch;
/*      */   private float roll;
/*  161 */   private final Vector3d beforeTriggerForce = new Vector3d();
/*  162 */   private final Vector3d beforeTriggerPosition = new Vector3d();
/*      */   
/*      */   private boolean processTriggersHasMoved;
/*      */   protected MovementSettings movementSettings;
/*      */   
/*      */   public MotionControllerBase(@Nonnull BuilderSupport builderSupport, @Nonnull BuilderMotionControllerBase builder) {
/*  168 */     this.entity = builderSupport.getEntity();
/*  169 */     this.type = builder.getType();
/*  170 */     this.epsilonSpeed = builder.getEpsilonSpeed();
/*  171 */     this.epsilonAngle = builder.getEpsilonAngle();
/*  172 */     this.forceVelocityDamping = builder.getForceVelocityDamping();
/*  173 */     this.maxHorizontalSpeed = builder.getMaxHorizontalSpeed(builderSupport);
/*  174 */     this.fastMotionThreshold = builder.getFastHorizontalThreshold(builderSupport);
/*  175 */     this.fastMotionThresholdRange = builder.getFastHorizontalThresholdRange();
/*  176 */     this.maxHeadRotationSpeed = builder.getMaxHeadRotationSpeed(builderSupport);
/*  177 */     setInertia(1.0D);
/*  178 */     setKnockbackScale(1.0D);
/*  179 */     setGravity(10.0D);
/*      */   }
/*      */ 
/*      */   
/*      */   public Role getRole() {
/*  184 */     return this.role;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRole(Role role) {
/*  189 */     this.role = role;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setInertia(double inertia) {
/*  194 */     this.inertia = Math.max(inertia, 1.0E-4D);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setKnockbackScale(double knockbackScale) {
/*  199 */     this.knockbackScale = Math.max(0.0D, knockbackScale);
/*      */   }
/*      */ 
/*      */   
/*      */   public void updateModelParameters(Ref<EntityStore> ref, Model model, @Nonnull Box boundingBox, ComponentAccessor<EntityStore> componentAccessor) {
/*  204 */     Objects.requireNonNull(boundingBox, "updateModelParameters: MotionController needs a bounding box");
/*      */ 
/*      */     
/*  207 */     this.collisionBoundingBox.assign(boundingBox);
/*      */   }
/*      */ 
/*      */   
/*      */   public void setHeadPitchAngleRange(float[] headPitchAngleRange) {
/*  212 */     if (headPitchAngleRange == null) {
/*  213 */       this.headPitchAngleRange = null;
/*      */       return;
/*      */     } 
/*  216 */     assert headPitchAngleRange.length == 2;
/*  217 */     this.headPitchAngleRange = (float[])headPitchAngleRange.clone();
/*      */   }
/*      */   
/*      */   protected void readEntityPosition(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  221 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  222 */     assert transformComponent != null;
/*      */     
/*  224 */     Vector3f bodyRotation = transformComponent.getRotation();
/*      */     
/*  226 */     this.position.assign(transformComponent.getPosition());
/*  227 */     this.yaw = bodyRotation.getY();
/*  228 */     this.pitch = bodyRotation.getPitch();
/*  229 */     this.roll = bodyRotation.getRoll();
/*  230 */     adjustReadPosition(ref, componentAccessor);
/*  231 */     postReadPosition(ref, componentAccessor);
/*      */   }
/*      */ 
/*      */   
/*      */   public void postReadPosition(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {}
/*      */ 
/*      */   
/*      */   public void moveEntity(@Nonnull Ref<EntityStore> ref, double dt, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  239 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  240 */     assert transformComponent != null;
/*      */     
/*  242 */     adjustWritePosition(ref, dt, componentAccessor);
/*      */     
/*  244 */     Vector3f bodyRotation = transformComponent.getRotation();
/*  245 */     bodyRotation.setYaw(this.yaw);
/*  246 */     bodyRotation.setPitch(this.pitch);
/*  247 */     bodyRotation.setRoll(this.roll);
/*      */     
/*  249 */     this.entity.moveTo(ref, this.position.x, this.position.y, this.position.z, componentAccessor);
/*      */   }
/*      */   
/*      */   public float getYaw() {
/*  253 */     return this.yaw;
/*      */   }
/*      */   
/*      */   public float getPitch() {
/*  257 */     return this.pitch;
/*      */   }
/*      */   
/*      */   public float getRoll() {
/*  261 */     return this.roll;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean touchesWater(boolean defaultValue, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  272 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*  273 */     ChunkStore chunkStore = world.getChunkStore();
/*      */ 
/*      */     
/*  276 */     long chunkIndex = ChunkUtil.indexChunkFromBlock(this.position.getX(), this.position.getZ());
/*  277 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*      */     
/*  279 */     if (chunkRef == null || !chunkRef.isValid()) {
/*  280 */       return defaultValue;
/*      */     }
/*      */     
/*  283 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
/*  284 */     assert worldChunkComponent != null;
/*      */     
/*  286 */     int blockX = MathUtil.floor(this.position.getX());
/*  287 */     int blockY = MathUtil.floor(this.position.getY() + this.collisionBoundingBox.min.y);
/*  288 */     int blockZ = MathUtil.floor(this.position.getZ());
/*  289 */     int fluidId = worldChunkComponent.getFluidId(blockX, blockY, blockZ);
/*  290 */     return (fluidId != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void updateMovementState(@Nonnull Ref<EntityStore> ref, @Nonnull MovementStates movementStates, @Nonnull Steering steering, @Nonnull Vector3d velocity, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  297 */     boolean lastFastMotion = movementStates.running;
/*      */ 
/*      */     
/*  300 */     movementStates.climbing = false;
/*  301 */     movementStates.swimJumping = false;
/*      */     
/*  303 */     movementStates.inFluid = touchesWater(movementStates.inFluid, componentAccessor);
/*  304 */     movementStates.onGround = this.role.isOnGround();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  309 */     double speed = waypointDistance(Vector3d.ZERO, velocity);
/*      */     
/*  311 */     speed = 0.7D * this.previousSpeed + 0.30000000000000004D * speed;
/*  312 */     this.previousSpeed = speed;
/*      */     
/*  314 */     this.fastMotionKind = isFastMotionKind(speed);
/*  315 */     this.idleMotionKind = steering.getTranslation().equals(Vector3d.ZERO);
/*  316 */     this.horizontalIdleKind = isHorizontalIdle(speed);
/*      */     
/*  318 */     if (this.motionKind != this.lastMovementStateUpdatedMotionKind || lastFastMotion != this.fastMotionKind || movementStates.idle != this.idleMotionKind || movementStates.horizontalIdle != this.horizontalIdleKind) {
/*  319 */       NPCEntity npcComponent; switch (this.motionKind) {
/*      */         case Linear:
/*  321 */           updateFlyingStates(movementStates, this.idleMotionKind, this.fastMotionKind);
/*      */           break;
/*      */         case Exp:
/*  324 */           updateSwimmingStates(movementStates, this.idleMotionKind, this.fastMotionKind, this.horizontalIdleKind);
/*      */           break;
/*      */         case null:
/*  327 */           updateSwimmingStates(movementStates, false, true, false);
/*      */           break;
/*      */         case null:
/*  330 */           updateAscendingStates(ref, movementStates, this.fastMotionKind, this.horizontalIdleKind, componentAccessor);
/*      */           break;
/*      */         case null:
/*  333 */           updateMovingStates(ref, movementStates, this.fastMotionKind, componentAccessor);
/*      */           break;
/*      */         case null:
/*  336 */           npcComponent = (NPCEntity)componentAccessor.getComponent(ref, NPCEntity.getComponentType());
/*  337 */           assert npcComponent != null;
/*  338 */           updateDescendingStates(ref, movementStates, this.fastMotionKind, (npcComponent.getHoverHeight() > 0.0D), componentAccessor);
/*      */           break;
/*      */         
/*      */         case null:
/*  342 */           updateDroppingStates(movementStates);
/*      */           break;
/*      */         
/*      */         default:
/*  346 */           npcComponent = (NPCEntity)componentAccessor.getComponent(ref, NPCEntity.getComponentType());
/*  347 */           assert npcComponent != null;
/*  348 */           updateStandingStates(movementStates, this.motionKind, (npcComponent.getHoverHeight() > 0.0D));
/*      */           break;
/*      */       } 
/*      */     
/*      */     } 
/*  353 */     this.lastMovementStateUpdatedMotionKind = this.motionKind;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   protected void updateFlyingStates(@Nonnull MovementStates movementStates, boolean idle, boolean fastMotionKind) {
/*  359 */     movementStates.flying = true;
/*  360 */     movementStates.idle = idle;
/*  361 */     movementStates.horizontalIdle = false;
/*  362 */     movementStates.walking = !fastMotionKind;
/*  363 */     movementStates.running = fastMotionKind;
/*  364 */     movementStates.falling = false;
/*  365 */     movementStates.swimming = false;
/*  366 */     movementStates.jumping = false;
/*      */   }
/*      */   
/*      */   protected void updateSwimmingStates(@Nonnull MovementStates movementStates, boolean idle, boolean fastMotionKind, boolean horizontalIdleKind) {
/*  370 */     movementStates.flying = false;
/*  371 */     movementStates.idle = idle;
/*  372 */     movementStates.horizontalIdle = horizontalIdleKind;
/*  373 */     movementStates.walking = !fastMotionKind;
/*  374 */     movementStates.running = fastMotionKind;
/*  375 */     movementStates.falling = false;
/*  376 */     movementStates.swimming = true;
/*  377 */     movementStates.jumping = false;
/*      */   }
/*      */   
/*      */   protected static void updateMovingStates(@Nonnull Ref<EntityStore> ref, @Nonnull MovementStates movementStates, boolean fastMotionKind, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  381 */     NPCEntity npcComponent = (NPCEntity)componentAccessor.getComponent(ref, NPCEntity.getComponentType());
/*  382 */     assert npcComponent != null;
/*      */     
/*  384 */     movementStates.flying = (npcComponent.getHoverHeight() > 0.0D);
/*  385 */     movementStates.idle = false;
/*  386 */     movementStates.horizontalIdle = false;
/*  387 */     movementStates.falling = false;
/*  388 */     movementStates.walking = !fastMotionKind;
/*  389 */     movementStates.running = fastMotionKind;
/*  390 */     movementStates.swimming = false;
/*  391 */     movementStates.jumping = false;
/*      */   }
/*      */   
/*      */   protected void updateAscendingStates(@Nonnull Ref<EntityStore> ref, @Nonnull MovementStates movementStates, boolean fastMotionKind, boolean horizontalIdleKind, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  395 */     updateMovingStates(ref, movementStates, fastMotionKind, componentAccessor);
/*      */   }
/*      */   
/*      */   protected void updateDescendingStates(@Nonnull Ref<EntityStore> ref, @Nonnull MovementStates movementStates, boolean fastMotionKind, boolean hovering, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  399 */     updateMovingStates(ref, movementStates, fastMotionKind, componentAccessor);
/*      */   }
/*      */   
/*      */   protected void updateDroppingStates(@Nonnull MovementStates movementStates) {
/*  403 */     movementStates.falling = true;
/*      */   }
/*      */   
/*      */   protected void updateStandingStates(@Nonnull MovementStates movementStates, @Nonnull MotionKind motionKind, boolean hovering) {
/*  407 */     movementStates.flying = hovering;
/*  408 */     movementStates.idle = true;
/*  409 */     movementStates.horizontalIdle = true;
/*  410 */     movementStates.walking = false;
/*  411 */     movementStates.running = false;
/*  412 */     movementStates.falling = false;
/*  413 */     movementStates.swimming = false;
/*  414 */     movementStates.jumping = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public double steer(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull Steering bodySteering, @Nonnull Steering headSteering, double interval, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  419 */     readEntityPosition(ref, componentAccessor);
/*      */     
/*  421 */     if (this.debugModeSteer) {
/*  422 */       double dx = this.position.x;
/*  423 */       double dz = this.position.z;
/*  424 */       double st = steer0(ref, role, bodySteering, headSteering, interval, componentAccessor);
/*  425 */       double t = interval - st;
/*      */       
/*  427 */       dx = this.position.x - dx;
/*  428 */       dz = this.position.z - dz;
/*      */       
/*  430 */       double l = Math.sqrt(dx * dx + dz * dz);
/*  431 */       double v = (t > 0.0D) ? (l / t) : 0.0D;
/*      */       
/*  433 */       LOGGER.at(Level.INFO).log("==  Steer %s  = t =%.4f dt=%.4f h =%.4f l =%.4f v =%.4f motion=%s", getType(), Double.valueOf(interval), Double.valueOf(t), Float.valueOf(57.295776F * this.yaw), Double.valueOf(l), Double.valueOf(v), role.getSteeringMotionName());
/*      */       
/*  435 */       return st;
/*      */     } 
/*  437 */     return steer0(ref, role, bodySteering, headSteering, interval, componentAccessor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public double steer0(@Nonnull Ref<EntityStore> ref, @Nonnull Role role, @Nonnull Steering bodySteering, @Nonnull Steering headSteering, double interval, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  447 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*      */     
/*  449 */     NPCEntity npcComponent = (NPCEntity)componentAccessor.getComponent(ref, NPCEntity.getComponentType());
/*  450 */     assert npcComponent != null;
/*      */     
/*  452 */     this.effectHorizontalSpeedMultiplier = npcComponent.getCurrentHorizontalSpeedMultiplier(ref, componentAccessor);
/*      */ 
/*      */     
/*  455 */     setAvoidingBlockDamage((this.isAvoidingBlockDamage && !this.isReceivingBlockDamage));
/*      */     
/*  457 */     this.translation.assign(0.0D);
/*  458 */     this.cachedMovementBlocked = isMovementBlocked(ref, componentAccessor);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  470 */     computeMove(ref, role, bodySteering, interval, this.translation, componentAccessor);
/*  471 */     if (this.debugModeValidateMath)
/*      */     {
/*  473 */       if (!NPCPhysicsMath.isValid(this.translation)) throw new IllegalArgumentException(String.valueOf(this.translation)); 
/*      */     }
/*  475 */     if (this.translation.squaredLength() > 1000000.0D) {
/*      */       
/*  477 */       if (this.debugModeValidateMath) {
/*  478 */         LOGGER.at(Level.WARNING).log("NPC with role %s has abnormal high speed! (Distance=%s, MotionController=%s)", role.getRoleName(), Double.valueOf(this.translation.length()), this.type);
/*      */       }
/*  480 */       this.translation.assign(Vector3d.ZERO);
/*      */     } 
/*  482 */     executeMove(ref, role, interval, this.translation, componentAccessor);
/*      */     
/*  484 */     postExecuteMove();
/*  485 */     clearRequirePreciseMovement();
/*  486 */     clearRequireDepthProbing();
/*  487 */     clearBlendHeading();
/*  488 */     setAvoidingBlockDamage(!this.isReceivingBlockDamage);
/*  489 */     setRelaxedMoveConstraints(false);
/*      */     
/*  491 */     float maxBodyRotation = (float)(interval * getCurrentMaxBodyRotationSpeed() * bodySteering.getRelativeTurnSpeed());
/*  492 */     float maxHeadRotation = (float)(interval * this.maxHeadRotationSpeed * headSteering.getRelativeTurnSpeed() * this.effectHorizontalSpeedMultiplier);
/*  493 */     calculateYaw(ref, bodySteering, headSteering, maxHeadRotation, maxBodyRotation, componentAccessor);
/*  494 */     calculatePitch(ref, bodySteering, headSteering, maxHeadRotation, componentAccessor);
/*  495 */     calculateRoll(bodySteering, headSteering);
/*      */     
/*  497 */     moveEntity(ref, interval, componentAccessor);
/*      */     
/*  499 */     HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());
/*  500 */     assert headRotationComponent != null;
/*      */     
/*  502 */     Vector3f headRotation = headRotationComponent.getRotation();
/*  503 */     headRotation.setYaw(headSteering.getYaw());
/*  504 */     headRotation.setPitch(headSteering.getPitch());
/*  505 */     headRotation.setRoll(headSteering.getRoll());
/*      */     
/*  507 */     if (!this.forceVelocity.equals(Vector3d.ZERO) && !this.ignoreDamping) {
/*  508 */       double movementThresholdSquared = 1.0000000000000002E-10D;
/*  509 */       if (this.forceVelocity.squaredLength() >= movementThresholdSquared) {
/*  510 */         dampForceVelocity(this.forceVelocity, this.forceVelocityDamping, interval, componentAccessor);
/*      */       } else {
/*  512 */         this.forceVelocity.assign(Vector3d.ZERO);
/*      */       } 
/*      */     } 
/*      */     
/*  516 */     double clientTps = 60.0D;
/*  517 */     int serverTps = world.getTps();
/*  518 */     double rate = clientTps / serverTps;
/*  519 */     boolean dampenY = shouldDampenAppliedVelocitiesY();
/*  520 */     boolean useGroundResistance = (shouldAlwaysUseGroundResistance() || onGround());
/*      */     
/*  522 */     for (int i = 0; i < this.appliedVelocities.size(); i++) {
/*  523 */       float min, max; AppliedVelocity entry = this.appliedVelocities.get(i);
/*      */ 
/*      */       
/*  526 */       if (useGroundResistance) {
/*  527 */         min = entry.config.getGroundResistance();
/*  528 */         max = entry.config.getGroundResistanceMax();
/*      */       } else {
/*  530 */         min = entry.config.getAirResistance();
/*  531 */         max = entry.config.getAirResistanceMax();
/*      */       } 
/*      */       
/*  534 */       float resistance = min;
/*  535 */       if (max >= 0.0F) {
/*  536 */         float len; switch (entry.config.getStyle()) { default: throw new MatchException(null, null);
/*      */           case Linear:
/*  538 */             len = (float)entry.velocity.length();
/*  539 */             if (len < entry.config.getThreshold()) {
/*  540 */               float mul = len / entry.config.getThreshold();
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           case Exp:
/*  547 */             len = (float)entry.velocity.squaredLength();
/*  548 */             if (len < entry.config.getThreshold() * entry.config.getThreshold()) {
/*  549 */               float mul = len / entry.config.getThreshold() * entry.config.getThreshold();
/*      */             } }
/*      */         
/*  552 */         resistance = min;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  558 */       double resistanceScale = Math.pow(resistance, rate);
/*  559 */       entry.velocity.x *= resistanceScale;
/*  560 */       entry.velocity.z *= resistanceScale;
/*  561 */       if (dampenY) {
/*  562 */         entry.velocity.y *= resistanceScale;
/*      */       }
/*      */     } 
/*      */     
/*  566 */     this.appliedVelocities.removeIf(v -> (v.velocity.squaredLength() < 0.001D));
/*      */     
/*  568 */     return interval;
/*      */   }
/*      */   
/*      */   protected boolean shouldDampenAppliedVelocitiesY() {
/*  572 */     return false;
/*      */   }
/*      */   
/*      */   protected boolean shouldAlwaysUseGroundResistance() {
/*  576 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void calculateYaw(@Nonnull Ref<EntityStore> ref, @Nonnull Steering bodySteering, @Nonnull Steering headSteering, float maxHeadRotation, float maxBodyRotation, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  585 */     if (bodySteering.hasYaw()) {
/*  586 */       this.yaw = bodySteering.getYaw();
/*  587 */     } else if (NPCPhysicsMath.dotProduct(this.translation.x, 0.0D, this.translation.z) > 0.001D) {
/*  588 */       this.yaw = PhysicsMath.headingFromDirection(this.translation.x, this.translation.z);
/*      */     } 
/*      */     
/*  591 */     boolean hasHeadSteering = headSteering.hasYaw();
/*  592 */     if (!hasHeadSteering)
/*      */     {
/*  594 */       headSteering.setYaw(this.yaw);
/*      */     }
/*      */ 
/*      */     
/*  598 */     HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());
/*  599 */     assert headRotationComponent != null;
/*      */     
/*  601 */     ModelComponent modelComponent = (ModelComponent)componentAccessor.getComponent(ref, ModelComponent.getComponentType());
/*  602 */     assert modelComponent != null;
/*      */     
/*  604 */     Vector3f headRotation = headRotationComponent.getRotation();
/*      */ 
/*      */     
/*  607 */     float currentYaw = headRotation.getYaw();
/*  608 */     float targetYaw = headSteering.getYaw();
/*  609 */     float turnAngle = MathUtil.clamp(NPCPhysicsMath.turnAngle(currentYaw, targetYaw), -maxHeadRotation, maxHeadRotation);
/*  610 */     headSteering.setYaw(PhysicsMath.normalizeTurnAngle(currentYaw + turnAngle));
/*      */     
/*  612 */     if (hasHeadSteering) {
/*      */ 
/*      */       
/*  615 */       float yawMin, yawMax, yawOffset = MathUtil.wrapAngle(headSteering.getYaw() - this.yaw);
/*      */ 
/*      */       
/*  618 */       CameraSettings headRotationRestrictions = modelComponent.getModel().getCamera();
/*  619 */       if (headRotationRestrictions != null && headRotationRestrictions.getYaw() != null && headRotationRestrictions.getYaw().getAngleRange() != null) {
/*      */         
/*  621 */         Rangef yawRange = headRotationRestrictions.getYaw().getAngleRange();
/*  622 */         yawMin = yawRange.min * 0.017453292F;
/*  623 */         yawMax = yawRange.max * 0.017453292F;
/*      */       }
/*      */       else {
/*      */         
/*  627 */         yawMin = -0.7853982F;
/*  628 */         yawMax = 0.7853982F;
/*      */       } 
/*  630 */       if (yawOffset > yawMax) {
/*      */         
/*  632 */         float initialBodyYaw = this.yaw;
/*  633 */         if (!bodySteering.hasYaw())
/*      */         {
/*  635 */           this.yaw = blendBodyYaw(ref, yawOffset, maxBodyRotation, componentAccessor);
/*      */         }
/*  637 */         headSteering.setYaw(MathUtil.wrapAngle(initialBodyYaw + yawMax));
/*  638 */       } else if (yawOffset < yawMin) {
/*      */         
/*  640 */         float initialBodyYaw = this.yaw;
/*  641 */         if (!bodySteering.hasYaw())
/*      */         {
/*  643 */           this.yaw = blendBodyYaw(ref, yawOffset, maxBodyRotation, componentAccessor);
/*      */         }
/*  645 */         headSteering.setYaw(MathUtil.wrapAngle(initialBodyYaw + yawMin));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected float blendBodyYaw(@Nonnull Ref<EntityStore> ref, float yawOffset, float maxBodyRotation, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  654 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  655 */     assert transformComponent != null;
/*      */     
/*  657 */     Vector3f bodyRotation = transformComponent.getRotation();
/*  658 */     float currentBodyYaw = bodyRotation.getYaw();
/*  659 */     float targetBodyYaw = MathUtil.wrapAngle(this.yaw + yawOffset);
/*  660 */     float bodyTurnAngle = MathUtil.clamp(NPCPhysicsMath.turnAngle(currentBodyYaw, targetBodyYaw), -maxBodyRotation, maxBodyRotation);
/*  661 */     return MathUtil.wrapAngle(this.yaw + bodyTurnAngle);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void calculatePitch(@Nonnull Ref<EntityStore> ref, @Nonnull Steering bodySteering, @Nonnull Steering headSteering, float maxHeadRotation, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  669 */     if (bodySteering.hasPitch()) {
/*  670 */       this.pitch = bodySteering.getPitch();
/*  671 */     } else if (NPCPhysicsMath.dotProduct(this.translation.x, this.translation.y, this.translation.z) > 0.001D) {
/*  672 */       this.pitch = PhysicsMath.pitchFromDirection(this.translation.x, this.translation.y, this.translation.z);
/*      */     } 
/*      */     
/*  675 */     boolean hasHeadSteering = headSteering.hasPitch();
/*  676 */     if (!hasHeadSteering)
/*      */     {
/*  678 */       headSteering.setPitch(this.pitch);
/*      */     }
/*      */ 
/*      */     
/*  682 */     HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());
/*  683 */     assert headRotationComponent != null;
/*      */     
/*  685 */     Vector3f headRotation = headRotationComponent.getRotation();
/*      */ 
/*      */     
/*  688 */     float currentPitch = headRotation.getPitch();
/*  689 */     float targetPitch = headSteering.getPitch();
/*  690 */     float turnAngle = MathUtil.clamp(NPCPhysicsMath.turnAngle(currentPitch, targetPitch), -maxHeadRotation, maxHeadRotation);
/*  691 */     headSteering.setPitch(PhysicsMath.normalizeTurnAngle(currentPitch + turnAngle));
/*      */     
/*  693 */     if (hasHeadSteering) {
/*  694 */       float pitchMin, pitchMax; ModelComponent modelComponent = (ModelComponent)componentAccessor.getComponent(ref, ModelComponent.getComponentType());
/*  695 */       assert modelComponent != null;
/*      */ 
/*      */       
/*  698 */       float bodyPitch = this.pitch;
/*  699 */       float pitchOffset = MathUtil.wrapAngle(headSteering.getPitch() - bodyPitch);
/*      */ 
/*      */       
/*  702 */       CameraSettings headRotationRestrictions = modelComponent.getModel().getCamera();
/*  703 */       if (this.headPitchAngleRange != null) {
/*  704 */         pitchMin = this.headPitchAngleRange[0];
/*  705 */         pitchMax = this.headPitchAngleRange[1];
/*  706 */       } else if (headRotationRestrictions != null && headRotationRestrictions.getPitch() != null && headRotationRestrictions.getPitch().getAngleRange() != null) {
/*  707 */         Rangef pitchRange = headRotationRestrictions.getPitch().getAngleRange();
/*  708 */         pitchMin = pitchRange.min * 0.017453292F;
/*  709 */         pitchMax = pitchRange.max * 0.017453292F;
/*      */       } else {
/*      */         
/*  712 */         pitchMin = -0.7853982F;
/*  713 */         pitchMax = 0.7853982F;
/*      */       } 
/*  715 */       if (pitchOffset > pitchMax) {
/*  716 */         headSteering.setPitch(MathUtil.wrapAngle(bodyPitch + pitchMax));
/*  717 */       } else if (pitchOffset < pitchMin) {
/*  718 */         headSteering.setPitch(MathUtil.wrapAngle(bodyPitch + pitchMin));
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   protected void calculateRoll(@Nonnull Steering bodySteering, @Nonnull Steering headSteering) {
/*  724 */     if (bodySteering.hasRoll()) {
/*  725 */       this.roll = bodySteering.getRoll();
/*      */     }
/*      */     
/*  728 */     if (!headSteering.hasRoll()) {
/*  729 */       headSteering.setRoll(this.roll);
/*      */     }
/*      */   }
/*      */   
/*      */   protected void dampForceVelocity(@Nonnull Vector3d forceVelocity, double forceVelocityDamping, double interval, ComponentAccessor<EntityStore> componentAccessor) {
/*  734 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*      */     
/*  736 */     double drag = 0.0D;
/*      */     
/*  738 */     if (this.motionKind != MotionKind.FLYING) {
/*  739 */       if (!onGround() && this.motionKind != MotionKind.SWIMMING && this.motionKind != MotionKind.SWIMMING_TURNING) {
/*  740 */         double horizontalSpeed = Math.sqrt(forceVelocity.x * forceVelocity.x + forceVelocity.z * forceVelocity.z);
/*  741 */         drag = convertToNewRange(horizontalSpeed, this.movementSettings.airDragMinSpeed, this.movementSettings.airDragMaxSpeed, this.movementSettings.airDragMin, this.movementSettings.airDragMax);
/*      */       } else {
/*  743 */         drag = 0.82D;
/*      */       } 
/*      */     }
/*      */     
/*  747 */     double clientTps = 60.0D;
/*  748 */     int serverTps = world.getTps();
/*  749 */     double rate = 60.0D / serverTps;
/*  750 */     drag = Math.pow(drag, rate);
/*      */     
/*  752 */     forceVelocity.x *= drag;
/*  753 */     forceVelocity.z *= drag;
/*      */ 
/*      */     
/*  756 */     float velocityEpsilon = 0.1F;
/*      */     
/*  758 */     if (Math.abs(forceVelocity.x) <= velocityEpsilon) forceVelocity.x = 0.0D; 
/*  759 */     if (Math.abs(forceVelocity.y) <= velocityEpsilon) forceVelocity.y = 0.0D; 
/*  760 */     if (Math.abs(forceVelocity.z) <= velocityEpsilon) forceVelocity.z = 0.0D; 
/*      */   }
/*      */   
/*      */   private static double convertToNewRange(double value, double oldMinRange, double oldMaxRange, double newMinRange, double newMaxRange) {
/*  764 */     if (newMinRange == newMaxRange || oldMinRange == oldMaxRange) return newMinRange;
/*      */     
/*  766 */     double newValue = (value - oldMinRange) * (newMaxRange - newMinRange) / (oldMaxRange - oldMinRange) + newMinRange;
/*  767 */     return MathUtil.clamp(newValue, Math.min(newMinRange, newMaxRange), Math.max(newMinRange, newMaxRange));
/*      */   }
/*      */ 
/*      */   
/*      */   public double probeMove(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3d position, @Nonnull Vector3d direction, @Nonnull ProbeMoveData probeMoveData, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  772 */     probeMoveData.setPosition(position).setDirection(direction);
/*  773 */     return probeMove(ref, probeMoveData, componentAccessor);
/*      */   }
/*      */ 
/*      */   
/*      */   protected void postExecuteMove() {}
/*      */ 
/*      */   
/*      */   protected void adjustReadPosition(Ref<EntityStore> ref, ComponentAccessor<EntityStore> componentAccessor) {}
/*      */ 
/*      */   
/*      */   protected void adjustWritePosition(Ref<EntityStore> ref, double dt, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {}
/*      */ 
/*      */   
/*      */   public boolean isInProgress() {
/*  787 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isObstructed() {
/*  792 */     return this.isObstructed;
/*      */   }
/*      */ 
/*      */   
/*      */   public NavState getNavState() {
/*  797 */     return this.navState;
/*      */   }
/*      */ 
/*      */   
/*      */   public double getThrottleDuration() {
/*  802 */     return this.throttleDuration;
/*      */   }
/*      */ 
/*      */   
/*      */   public double getTargetDeltaSquared() {
/*  807 */     return this.targetDeltaSquared;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setNavState(NavState navState, double throttleDuration, double targetDeltaSquared) {
/*  812 */     this.navState = navState;
/*  813 */     this.throttleDuration = throttleDuration;
/*  814 */     this.targetDeltaSquared = targetDeltaSquared;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isForceRecomputePath() {
/*  819 */     return this.recomputePath;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setForceRecomputePath(boolean recomputePath) {
/*  824 */     this.recomputePath = recomputePath;
/*      */   }
/*      */ 
/*      */   
/*      */   public void beforeInstructionSensorsAndActions(double physicsTickDuration) {
/*  829 */     this.recomputePath = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void beforeInstructionMotion(double physicsTickDuration) {
/*  834 */     resetNavState();
/*      */   }
/*      */   
/*      */   public boolean isHorizontalIdle(double speed) {
/*  838 */     return (speed == 0.0D);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canAct(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  843 */     return (isAlive(ref, componentAccessor) && this.role.couldBreatheCached() && this.forceVelocity.equals(Vector3d.ZERO) && this.appliedVelocities.isEmpty());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isMovementBlocked(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  848 */     InteractionManager interactionManager = (InteractionManager)componentAccessor.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
/*  849 */     if (interactionManager != null) {
/*  850 */       Boolean movementBlocked = (Boolean)interactionManager.forEachInteraction((chain, interaction, val) -> { if (val.booleanValue()) return Boolean.TRUE;  MovementEffects movementEffects = interaction.getEffects().getMovementEffects(); return (movementEffects != null) ? Boolean.valueOf(movementEffects.isDisableAll()) : Boolean.FALSE; }Boolean.FALSE);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  860 */       return movementBlocked.booleanValue();
/*      */     } 
/*  862 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> double bisect(@Nonnull Vector3d validPosition, @Nonnull Vector3d invalidPosition, @Nonnull T t, @Nonnull BiPredicate<T, Vector3d> validate, @Nonnull Vector3d result) {
/*  894 */     return bisect(validPosition, invalidPosition, t, validate, 0.05D, result);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public <T> double bisect(@Nonnull Vector3d validPosition, @Nonnull Vector3d invalidPosition, @Nonnull T t, @Nonnull BiPredicate<T, Vector3d> validate, double maxDistance, @Nonnull Vector3d result) {
/*  913 */     double validDistance = 0.0D;
/*  914 */     double invalidDistance = 1.0D;
/*      */ 
/*      */     
/*  917 */     this.bisectValidPosition.assign(validPosition);
/*  918 */     this.bisectInvalidPosition.assign(invalidPosition);
/*  919 */     maxDistance *= maxDistance;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  924 */     double validWeight = 0.1D;
/*  925 */     double invalidWeight = 0.9D;
/*      */     
/*  927 */     while (this.bisectValidPosition.distanceSquaredTo(this.bisectInvalidPosition) > maxDistance) {
/*      */       
/*  929 */       double distance = validWeight * validDistance + invalidWeight * invalidDistance;
/*  930 */       result.x = validWeight * this.bisectValidPosition.x + invalidWeight * this.bisectInvalidPosition.x;
/*  931 */       result.y = validWeight * this.bisectValidPosition.y + invalidWeight * this.bisectInvalidPosition.y;
/*  932 */       result.z = validWeight * this.bisectValidPosition.z + invalidWeight * this.bisectInvalidPosition.z;
/*  933 */       if (validate.test(t, result)) {
/*      */         
/*  935 */         validDistance = distance;
/*  936 */         this.bisectValidPosition.assign(result);
/*      */         continue;
/*      */       } 
/*  939 */       invalidDistance = distance;
/*  940 */       this.bisectInvalidPosition.assign(result);
/*      */       
/*  942 */       validWeight = 0.5D;
/*  943 */       invalidWeight = 0.5D;
/*      */     } 
/*      */     
/*  946 */     result.assign(this.bisectValidPosition);
/*  947 */     return validDistance;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Vector3d getForce() {
/*  953 */     return this.forceVelocity;
/*      */   }
/*      */ 
/*      */   
/*      */   public void addForce(@Nonnull Vector3d force, VelocityConfig velocityConfig) {
/*  958 */     double scale = this.knockbackScale;
/*  959 */     if (!SplitVelocity.SHOULD_MODIFY_VELOCITY && velocityConfig != null) {
/*  960 */       this.appliedVelocities.add(new AppliedVelocity(new Vector3d(force.x * scale, force.y * scale, force.z * scale), velocityConfig));
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*  967 */     double horzMul = 0.18000000000000005D * this.movementSettings.velocityResistance;
/*  968 */     this.forceVelocity.add(force.x * scale * horzMul, force.y * scale, force.z * scale * horzMul);
/*  969 */     this.appliedForce.assign(this.forceVelocity);
/*  970 */     this.ignoreDamping = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void forceVelocity(@Nonnull Vector3d velocity, @Nullable VelocityConfig velocityConfig, boolean ignoreDamping) {
/*  975 */     if (!SplitVelocity.SHOULD_MODIFY_VELOCITY && velocityConfig != null) {
/*  976 */       this.appliedVelocities.clear();
/*  977 */       this.appliedVelocities.add(new AppliedVelocity(velocity
/*  978 */             .clone(), velocityConfig));
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  983 */     this.forceVelocity.assign(velocity);
/*  984 */     this.ignoreDamping = ignoreDamping;
/*      */   }
/*      */   
/*      */   public void clearForce() {
/*  988 */     this.forceVelocity.assign(Vector3d.ZERO);
/*      */   }
/*      */   
/*      */   protected void dumpCollisionResults() {
/*  992 */     String slideString = "";
/*      */     
/*  994 */     if (this.collisionResult.isSliding) {
/*  995 */       slideString = String.format("SLIDE: start/end=%f/%f", new Object[] { Double.valueOf(this.collisionResult.slideStart), Double.valueOf(this.collisionResult.slideEnd) });
/*      */     }
/*  997 */     LOGGER.at(Level.INFO).log("CollRes: pos=%s yaw=%f count=%d %s", Vector3d.formatShortString(this.position), Float.valueOf(57.295776F * this.yaw), Integer.valueOf(this.collisionResult.getBlockCollisionCount()), slideString);
/*  998 */     if (this.collisionResult.getBlockCollisionCount() > 0) {
/*  999 */       for (int i = 0; i < this.collisionResult.getBlockCollisionCount(); i++) {
/* 1000 */         String rotation; BlockCollisionData cd = this.collisionResult.getBlockCollision(i);
/* 1001 */         String materialName = (cd.blockMaterial != null) ? cd.blockMaterial.name() : "none";
/* 1002 */         String typeName = (cd.blockType != null) ? cd.blockType.getId() : "none";
/* 1003 */         String hitboxName = (cd.blockType != null) ? cd.blockType.getHitboxType() : "none";
/*      */         
/* 1005 */         if (cd.blockType != null) {
/* 1006 */           RotationTuple blockRotation = RotationTuple.get(cd.rotation);
/* 1007 */           rotation = String.valueOf(blockRotation.yaw()) + " " + String.valueOf(blockRotation.yaw());
/*      */         } else {
/* 1009 */           rotation = "none";
/*      */         } 
/* 1011 */         LOGGER.at(Level.INFO).log("   COLL: blk=%s/%s/%s start=%f norm=%s pos=%s mat=%s block=%s hitbox=%s rot=%s", 
/* 1012 */             Integer.valueOf(cd.x), Integer.valueOf(cd.y), Integer.valueOf(cd.z), Double.valueOf(cd.collisionStart), Vector3d.formatShortString(cd.collisionNormal), Vector3d.formatShortString(cd.collisionPoint), materialName, typeName, hitboxName, rotation);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   public void setEnableTriggers(boolean enableTriggers) {
/* 1018 */     this.enableTriggers = enableTriggers;
/*      */   }
/*      */   
/*      */   public void setEnableBlockDamage(boolean enableBlockDamage) {
/* 1022 */     this.enableBlockDamage = enableBlockDamage;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean willReceiveBlockDamage() {
/* 1027 */     return this.isReceivingBlockDamage;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setAvoidingBlockDamage(boolean avoid) {
/* 1032 */     this.isAvoidingBlockDamage = avoid;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isAvoidingBlockDamage() {
/* 1037 */     return this.isAvoidingBlockDamage;
/*      */   }
/*      */   
/*      */   public void processTriggers(@Nonnull Ref<EntityStore> ref, @Nonnull CollisionResult collisionResult, double t, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1041 */     this.processTriggersHasMoved = false;
/* 1042 */     this.isReceivingBlockDamage = false;
/*      */     
/* 1044 */     if (!this.enableTriggers && !this.enableBlockDamage) {
/*      */       return;
/*      */     }
/*      */     
/* 1048 */     collisionResult.pruneTriggerBlocks(t);
/*      */     
/* 1050 */     int count = collisionResult.getTriggerBlocks().size();
/* 1051 */     if (count == 0) {
/*      */       return;
/*      */     }
/*      */     
/* 1055 */     if (this.enableTriggers) {
/* 1056 */       this.beforeTriggerForce.assign(getForce());
/* 1057 */       this.beforeTriggerPosition.assign(this.position);
/*      */     } 
/* 1059 */     moveEntity(ref, 0.0D, componentAccessor);
/*      */ 
/*      */     
/* 1062 */     InteractionManager interactionManagerComponent = (InteractionManager)componentAccessor.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
/* 1063 */     assert interactionManagerComponent != null;
/*      */     
/* 1065 */     int damageToEntity = collisionResult.defaultTriggerBlocksProcessing(interactionManagerComponent, (Entity)this.entity, ref, this.enableTriggers, componentAccessor);
/*      */     
/* 1067 */     if (this.enableBlockDamage && damageToEntity > 0) {
/* 1068 */       Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.ENVIRONMENT, damageToEntity);
/* 1069 */       DamageSystems.executeDamage(ref, componentAccessor, damage);
/*      */       
/* 1071 */       this.isReceivingBlockDamage = true;
/*      */     } 
/*      */     
/* 1074 */     readEntityPosition(ref, componentAccessor);
/* 1075 */     if (this.enableTriggers) {
/* 1076 */       this.processTriggersHasMoved = (!this.beforeTriggerForce.equals(getForce()) || !this.beforeTriggerPosition.equals(this.position));
/*      */     }
/*      */   }
/*      */   
/*      */   protected boolean isDebugMode(RoleDebugFlags mode) {
/* 1081 */     return (getRole() != null && getRole().getDebugSupport().getDebugFlags().contains(mode));
/*      */   }
/*      */   
/*      */   public boolean isProcessTriggersHasMoved() {
/* 1085 */     return this.processTriggersHasMoved;
/*      */   }
/*      */   
/*      */   protected boolean isAlive(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1089 */     return !componentAccessor.getArchetype(ref).contains(DeathComponent.getComponentType());
/*      */   }
/*      */ 
/*      */   
/*      */   public void activate() {
/* 1094 */     this.debugModeSteer = isDebugMode(RoleDebugFlags.MotionControllerSteer);
/* 1095 */     this.debugModeMove = isDebugMode(RoleDebugFlags.MotionControllerMove);
/* 1096 */     this.debugModeCollisions = isDebugMode(RoleDebugFlags.Collisions);
/* 1097 */     this.debugModeBlockCollisions = isDebugMode(RoleDebugFlags.BlockCollisions);
/* 1098 */     this.debugModeProbeBlockCollisions = isDebugMode(RoleDebugFlags.ProbeBlockCollisions);
/* 1099 */     this.debugModeValidatePositions = isDebugMode(RoleDebugFlags.ValidatePositions);
/* 1100 */     this.debugModeOverlaps = isDebugMode(RoleDebugFlags.Overlaps);
/* 1101 */     this.debugModeValidateMath = isDebugMode(RoleDebugFlags.ValidateMath);
/* 1102 */     resetObstructedFlags();
/* 1103 */     resetNavState();
/*      */   }
/*      */   
/*      */   public void resetNavState() {
/* 1107 */     this.navState = NavState.AT_GOAL;
/* 1108 */     this.throttleDuration = 0.0D;
/* 1109 */     this.targetDeltaSquared = 0.0D;
/*      */   }
/*      */   
/*      */   public void resetObstructedFlags() {
/* 1113 */     this.isObstructed = false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void deactivate() {}
/*      */ 
/*      */   
/*      */   public double getEpsilonSpeed() {
/* 1122 */     return this.epsilonSpeed;
/*      */   }
/*      */   
/*      */   public float getEpsilonAngle() {
/* 1126 */     return this.epsilonAngle;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Vector3d getComponentSelector() {
/* 1132 */     return this.componentSelector;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Vector3d getPlanarComponentSelector() {
/* 1138 */     return this.planarComponentSelector;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setComponentSelector(@Nonnull Vector3d componentSelector) {
/* 1143 */     this.componentSelector.assign(componentSelector);
/*      */   }
/*      */ 
/*      */   
/*      */   public Vector3d getWorldNormal() {
/* 1148 */     return this.worldNormal;
/*      */   }
/*      */ 
/*      */   
/*      */   public Vector3d getWorldAntiNormal() {
/* 1153 */     return this.worldAntiNormal;
/*      */   }
/*      */ 
/*      */   
/*      */   public double waypointDistance(@Nonnull Vector3d p, @Nonnull Vector3d q) {
/* 1158 */     return Math.sqrt(waypointDistanceSquared(p, q));
/*      */   }
/*      */ 
/*      */   
/*      */   public double waypointDistanceSquared(@Nonnull Vector3d p, @Nonnull Vector3d q) {
/* 1163 */     double dx = (p.x - q.x) * (getComponentSelector()).x;
/* 1164 */     double dy = (p.y - q.y) * (getComponentSelector()).y;
/* 1165 */     double dz = (p.z - q.z) * (getComponentSelector()).z;
/* 1166 */     return dx * dx + dy * dy + dz * dz;
/*      */   }
/*      */ 
/*      */   
/*      */   public double waypointDistance(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3d p, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1171 */     return Math.sqrt(waypointDistanceSquared(ref, p, componentAccessor));
/*      */   }
/*      */ 
/*      */   
/*      */   public double waypointDistanceSquared(@Nonnull Ref<EntityStore> ref, @Nonnull Vector3d p, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1176 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/* 1177 */     assert transformComponent != null;
/*      */     
/* 1179 */     Vector3d position = transformComponent.getPosition();
/* 1180 */     double dx = (p.x - position.getX()) * (getComponentSelector()).x;
/* 1181 */     double dy = (p.y - position.getY()) * (getComponentSelector()).y;
/* 1182 */     double dz = (p.z - position.getZ()) * (getComponentSelector()).z;
/* 1183 */     return dx * dx + dy * dy + dz * dz;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isValidPosition(@Nonnull Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1188 */     return isValidPosition(position, this.collisionResult, componentAccessor);
/*      */   }
/*      */   
/*      */   public boolean isValidPosition(@Nonnull Vector3d position, @Nonnull CollisionResult collisionResult, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1192 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*      */     
/* 1194 */     CollisionModule module = CollisionModule.get();
/* 1195 */     CollisionModuleConfig config = module.getConfig();
/* 1196 */     boolean saveDebugModeOverlaps = config.isDumpInvalidBlocks();
/* 1197 */     config.setDumpInvalidBlocks(this.debugModeOverlaps);
/*      */     
/* 1199 */     boolean isValid = (module.validatePosition(world, this.collisionBoundingBox, position, getInvalidOverlapMaterials(), null, (_this, collisionCode, collision, collisionConfig) -> (collisionConfig.blockId != Integer.MIN_VALUE), collisionResult) != -1);
/*      */     
/* 1201 */     config.setDumpInvalidBlocks(saveDebugModeOverlaps);
/* 1202 */     return isValid;
/*      */   }
/*      */   
/*      */   public int getInvalidOverlapMaterials() {
/* 1206 */     return 4;
/*      */   }
/*      */   
/*      */   protected void saveMotionKind() {
/* 1210 */     this.previousMotionKind = getMotionKind();
/*      */   }
/*      */   
/*      */   protected boolean switchedToMotionKind(MotionKind motionKind) {
/* 1214 */     return (getMotionKind() == motionKind && this.previousMotionKind != motionKind);
/*      */   }
/*      */   
/*      */   public MotionKind getMotionKind() {
/* 1218 */     return this.motionKind;
/*      */   }
/*      */   
/*      */   public void setMotionKind(MotionKind motionKind) {
/* 1222 */     this.motionKind = motionKind;
/*      */   }
/*      */ 
/*      */   
/*      */   public double getGravity() {
/* 1227 */     return this.gravity;
/*      */   }
/*      */   
/*      */   public void setGravity(double gravity) {
/* 1231 */     this.gravity = gravity;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean translateToAccessiblePosition(Vector3d position, Box boundingBox, double minYValue, double maxYValue, ComponentAccessor<EntityStore> componentAccessor) {
/* 1236 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean standingOnBlockOfType(int blockSet) {
/* 1241 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void requirePreciseMovement(@Nullable Vector3d positionHint) {
/* 1246 */     this.requiresPreciseMovement = true;
/* 1247 */     this.havePreciseMovementTarget = (positionHint != null);
/* 1248 */     if (this.havePreciseMovementTarget) this.preciseMovementTarget.assign(positionHint); 
/*      */   }
/*      */   
/*      */   public void clearRequirePreciseMovement() {
/* 1252 */     this.requiresPreciseMovement = false;
/* 1253 */     this.havePreciseMovementTarget = false;
/*      */   }
/*      */   
/*      */   public boolean isRequiresPreciseMovement() {
/* 1257 */     return this.requiresPreciseMovement;
/*      */   }
/*      */ 
/*      */   
/*      */   public void requireDepthProbing() {
/* 1262 */     this.requiresDepthProbing = true;
/*      */   }
/*      */   
/*      */   public void clearRequireDepthProbing() {
/* 1266 */     this.requiresDepthProbing = false;
/*      */   }
/*      */   
/*      */   public boolean isRequiresDepthProbing() {
/* 1270 */     return this.requiresDepthProbing;
/*      */   }
/*      */ 
/*      */   
/*      */   public void enableHeadingBlending(double heading, @Nullable Vector3d targetPosition, double blendLevel) {
/* 1275 */     this.isBlendingHeading = true;
/* 1276 */     this.blendHeading = heading;
/* 1277 */     this.haveBlendHeadingPosition = (targetPosition != null);
/* 1278 */     if (this.haveBlendHeadingPosition) this.blendHeadingPosition.assign(targetPosition); 
/* 1279 */     this.blendLevelAtTargetPosition = blendLevel;
/*      */   }
/*      */ 
/*      */   
/*      */   public void enableHeadingBlending() {
/* 1284 */     enableHeadingBlending(Double.NaN, (Vector3d)null, 0.0D);
/*      */   }
/*      */   
/*      */   public void clearBlendHeading() {
/* 1288 */     this.isBlendingHeading = false;
/* 1289 */     this.haveBlendHeadingPosition = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void setRelaxedMoveConstraints(boolean relax) {
/* 1294 */     this.isRelaxedMoveConstraints = relax;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isRelaxedMoveConstraints() {
/* 1299 */     return this.isRelaxedMoveConstraints;
/*      */   }
/*      */ 
/*      */   
/*      */   public void updatePhysicsValues(PhysicsValues values) {
/* 1304 */     this.movementSettings = MovementManager.MASTER_DEFAULT.apply(values, GameMode.Adventure);
/*      */   }
/*      */   protected abstract boolean isFastMotionKind(double paramDouble);
/*      */   protected abstract double computeMove(@Nonnull Ref<EntityStore> paramRef, @Nonnull Role paramRole, Steering paramSteering, double paramDouble, Vector3d paramVector3d, @Nonnull ComponentAccessor<EntityStore> paramComponentAccessor);
/*      */   protected abstract double executeMove(@Nonnull Ref<EntityStore> paramRef, @Nonnull Role paramRole, double paramDouble, Vector3d paramVector3d, @Nonnull ComponentAccessor<EntityStore> paramComponentAccessor);
/*      */   
/*      */   protected static class AppliedVelocity { protected final Vector3d velocity;
/*      */     
/*      */     public AppliedVelocity(Vector3d velocity, VelocityConfig config) {
/* 1313 */       this.velocity = velocity;
/* 1314 */       this.config = config;
/*      */     }
/*      */     
/*      */     protected final VelocityConfig config;
/*      */     protected boolean canClear; }
/*      */ 
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\npc\movement\controllers\MotionControllerBase.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */