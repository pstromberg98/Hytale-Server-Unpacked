/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.Color;
/*     */ import com.hypixel.hytale.protocol.MovementSettings;
/*     */ import com.hypixel.hytale.protocol.MovementStates;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.entities.ApplyKnockback;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
/*     */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*     */ import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
/*     */ import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*     */ import com.hypixel.hytale.server.core.modules.physics.util.PhysicsMath;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class SimulateKnockback
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/* 251 */   private static final ComponentType<EntityStore, BoundingBox> BOUNDING_BOX_COMPONENT_TYPE = BoundingBox.getComponentType();
/*     */   
/* 253 */   private static final Query<EntityStore> QUERY = (Query<EntityStore>)Query.and(new Query[] {
/* 254 */         (Query)Player.getComponentType(), 
/* 255 */         (Query)TransformComponent.getComponentType(), 
/* 256 */         (Query)KnockbackSimulation.getComponentType(), (Query)BOUNDING_BOX_COMPONENT_TYPE, 
/*     */         
/* 258 */         (Query)MovementStatesComponent.getComponentType(), 
/* 259 */         (Query)MovementManager.getComponentType(), 
/* 260 */         (Query)PlayerRef.getComponentType()
/*     */       });
/* 262 */   private static final Set<Dependency<EntityStore>> DEPENDENCIES = (Set)Set.of(new SystemDependency(Order.AFTER, PlayerSystems.ProcessPlayerInput.class));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 269 */     return QUERY;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 275 */     return DEPENDENCIES;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 280 */     KnockbackSimulation knockbackSimulationComponent = (KnockbackSimulation)archetypeChunk.getComponent(index, KnockbackSimulation.getComponentType());
/* 281 */     assert knockbackSimulationComponent != null;
/*     */     
/* 283 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/* 284 */     assert transformComponent != null;
/*     */     
/* 286 */     Player playerComponent = (Player)archetypeChunk.getComponent(index, Player.getComponentType());
/* 287 */     assert playerComponent != null;
/*     */     
/* 289 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 291 */     float time = knockbackSimulationComponent.getRemainingTime();
/* 292 */     time -= dt;
/* 293 */     knockbackSimulationComponent.setRemainingTime(time);
/* 294 */     if (time < 0.0F || archetypeChunk.getArchetype().contains(DeathComponent.getComponentType())) {
/* 295 */       commandBuffer.removeComponent(archetypeChunk.getReferenceTo(index), KnockbackSimulation.getComponentType());
/*     */       
/*     */       return;
/*     */     } 
/* 299 */     if (KnockbackPredictionSystems.DEBUG_KNOCKBACK_POSITION) {
/* 300 */       Vector3d particlePosition = knockbackSimulationComponent.getClientPosition();
/* 301 */       SpatialResource<Ref<EntityStore>, EntityStore> playerSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(EntityModule.get().getPlayerSpatialResourceType());
/* 302 */       ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 303 */       playerSpatialResource.getSpatialStructure().collect(particlePosition, 75.0D, (List)results);
/*     */       
/* 305 */       Color color = knockbackSimulationComponent.hadWishMovement() ? new Color((byte)-1, (byte)0, (byte)0) : new Color((byte)0, (byte)0, (byte)-1);
/* 306 */       ParticleUtil.spawnParticleEffect("Example_Simple", particlePosition, 0.0F, 0.0F, 0.0F, 1.0F, color, (List)results, (ComponentAccessor)commandBuffer);
/*     */     } 
/*     */     
/* 309 */     knockbackSimulationComponent.setTickBuffer(knockbackSimulationComponent.getTickBuffer() + dt);
/*     */     
/* 311 */     MovementStates clientStates = knockbackSimulationComponent.getClientMovementStates();
/*     */     
/* 313 */     while (knockbackSimulationComponent.getTickBuffer() >= 0.016666668F) {
/* 314 */       knockbackSimulationComponent.setTickBuffer(knockbackSimulationComponent.getTickBuffer() - 0.016666668F);
/*     */ 
/*     */       
/* 317 */       Vector3d rel = knockbackSimulationComponent.getRelativeMovement();
/* 318 */       Vector3d requestedVelocity = knockbackSimulationComponent.getRequestedVelocity();
/* 319 */       Vector3d simPos = knockbackSimulationComponent.getSimPosition();
/* 320 */       Vector3d velocity = knockbackSimulationComponent.getSimVelocity();
/*     */       
/* 322 */       MovementStatesComponent movementStatesComponent = (MovementStatesComponent)archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
/* 323 */       assert movementStatesComponent != null;
/*     */       
/* 325 */       MovementStates movementStates = movementStatesComponent.getMovementStates();
/*     */       
/* 327 */       MovementManager movementManagerComponent = (MovementManager)archetypeChunk.getComponent(index, MovementManager.getComponentType());
/* 328 */       assert movementManagerComponent != null;
/*     */       
/* 330 */       MovementSettings movementManagerSettings = movementManagerComponent.getSettings();
/*     */       
/* 332 */       BoundingBox boundingBoxComponent = (BoundingBox)archetypeChunk.getComponent(index, BOUNDING_BOX_COMPONENT_TYPE);
/* 333 */       assert boundingBoxComponent != null;
/* 334 */       Box hitBox = boundingBoxComponent.getBoundingBox();
/*     */       
/* 336 */       if (clientStates.flying) {
/*     */         
/* 338 */         knockbackSimulationComponent.setRemainingTime(0.0F); return;
/*     */       } 
/* 340 */       if (clientStates.climbing && !clientStates.onGround) {
/*     */         
/* 342 */         knockbackSimulationComponent.setRemainingTime(0.0F); return;
/*     */       } 
/* 344 */       if (clientStates.swimming) {
/*     */         
/* 346 */         knockbackSimulationComponent.setRemainingTime(0.0F);
/*     */         return;
/*     */       } 
/* 349 */       int invertedGravityModifier = movementManagerSettings.invertedGravity ? 1 : -1;
/* 350 */       double terminalVelocity = invertedGravityModifier * PhysicsMath.getTerminalVelocity(movementManagerSettings.mass, 0.0012250000145286322D, 
/*     */           
/* 352 */           Math.abs(hitBox.width() * hitBox.depth()), movementManagerSettings.dragCoefficient);
/*     */ 
/*     */       
/* 355 */       double gravityStep = invertedGravityModifier * PhysicsMath.getAcceleration(velocity.y, terminalVelocity) * 0.01666666753590107D;
/* 356 */       if (velocity.y < terminalVelocity && gravityStep > 0.0D) {
/* 357 */         velocity.y = Math.min(velocity.y + gravityStep, terminalVelocity);
/* 358 */       } else if (velocity.y > terminalVelocity && gravityStep < 0.0D) {
/* 359 */         velocity.y = Math.max(velocity.y + gravityStep, terminalVelocity);
/*     */       } 
/*     */       
/* 362 */       if (movementStates.onGround) {
/* 363 */         movementStates.falling = false;
/*     */         
/* 365 */         if (knockbackSimulationComponent.wasOnGround() && knockbackSimulationComponent.consumeWasJumping()) {
/* 366 */           velocity.y = movementManagerSettings.jumpForce;
/* 367 */           movementStates.onGround = false;
/* 368 */           knockbackSimulationComponent.setJumpCombo(Math.min(knockbackSimulationComponent.getJumpCombo() + 1, 3));
/*     */         } 
/*     */       } else {
/* 371 */         Vector3d checkPosition = simPos.clone();
/* 372 */         checkPosition.y += 0.10000000149011612D;
/* 373 */         movementStates.falling = (velocity.y < 0.0D && CollisionModule.get().validatePosition(world, hitBox, checkPosition, new CollisionResult()) != 0);
/*     */         
/* 375 */         if (movementStates.falling) {
/* 376 */           movementStates.jumping = false;
/* 377 */           movementStates.swimJumping = false;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 382 */       if (knockbackSimulationComponent.getJumpCombo() != 0 && ((movementStates.onGround && knockbackSimulationComponent.wasOnGround()) || velocity.x == 0.0D || velocity.z == 0.0D)) {
/* 383 */         knockbackSimulationComponent.setJumpCombo(0);
/*     */       }
/*     */       
/* 386 */       float friction = computeMoveForce(knockbackSimulationComponent, movementStates, movementManagerSettings);
/*     */       
/* 388 */       if (!movementStates.flying && knockbackSimulationComponent.getRequestedVelocityChangeType() != null) {
/* 389 */         switch (KnockbackPredictionSystems.null.$SwitchMap$com$hypixel$hytale$protocol$ChangeVelocityType[knockbackSimulationComponent.getRequestedVelocityChangeType().ordinal()]) { case 1:
/* 390 */             velocity.add(requestedVelocity.x * 0.18000000715255737D * movementManagerSettings.velocityResistance, requestedVelocity.y, requestedVelocity.z * 0.18000000715255737D * movementManagerSettings.velocityResistance);
/*     */             break;
/*     */ 
/*     */           
/*     */           case 2:
/* 395 */             velocity.assign(requestedVelocity);
/*     */             break; }
/*     */         
/* 398 */         PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/* 399 */         assert playerRefComponent != null;
/*     */         
/* 401 */         playerRefComponent.getPacketHandler().write((Packet)new ApplyKnockback(
/* 402 */               PositionUtil.toPositionPacket(transformComponent.getPosition()), (float)requestedVelocity.x, (float)requestedVelocity.y, (float)requestedVelocity.z, knockbackSimulationComponent
/*     */ 
/*     */ 
/*     */               
/* 406 */               .getRequestedVelocityChangeType()));
/*     */       } 
/*     */       
/* 409 */       requestedVelocity.assign(0.0D);
/* 410 */       knockbackSimulationComponent.setRequestedVelocityChangeType(null);
/*     */       
/* 412 */       Vector3d movementOffset = knockbackSimulationComponent.getMovementOffset();
/* 413 */       movementOffset.assign(0.0D);
/*     */       
/* 415 */       if (knockbackSimulationComponent.hadWishMovement()) {
/* 416 */         float converter = convertWishMovement(knockbackSimulationComponent, movementStates, movementManagerSettings);
/* 417 */         velocity.addScaled(rel, converter);
/*     */       } else {
/* 419 */         movementOffset.addScaled(rel, friction);
/* 420 */         rel.assign(0.0D);
/*     */       } 
/*     */       
/* 423 */       movementOffset.addScaled(velocity, 0.01666666753590107D);
/*     */       
/* 425 */       applyMovementOffset(world, hitBox, knockbackSimulationComponent, movementStates, movementOffset);
/*     */       
/* 427 */       Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */       
/* 429 */       if (time < 0.2F) {
/* 430 */         Vector3d move = Vector3d.lerp(knockbackSimulationComponent.getClientPosition(), simPos, (time / 0.2F));
/* 431 */         playerComponent.moveTo(ref, move.x, move.y, move.z, (ComponentAccessor)commandBuffer); continue;
/*     */       } 
/* 433 */       playerComponent.moveTo(ref, simPos.x, simPos.y, simPos.z, (ComponentAccessor)commandBuffer);
/*     */     } 
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
/*     */   private float convertWishMovement(@Nonnull KnockbackSimulation simulation, @Nonnull MovementStates movementStates, @Nonnull MovementSettings movementSettings) {
/* 450 */     MovementStates clientStates = simulation.getClientMovementStates();
/* 451 */     if (clientStates == null) clientStates = movementStates;
/*     */     
/* 453 */     Vector3d velocity = simulation.getSimVelocity();
/* 454 */     float horizontalSpeed = (float)Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
/*     */     
/* 456 */     float drag = 0.0F;
/* 457 */     float friction = 1.0F;
/* 458 */     if (!movementStates.flying && !movementStates.climbing) {
/* 459 */       drag = (!movementStates.onGround && !movementStates.swimming) ? convertToNewRange(horizontalSpeed, movementSettings.airDragMinSpeed, movementSettings.airDragMaxSpeed, movementSettings.airDragMin, movementSettings.airDragMax) : 0.82F;
/* 460 */       friction = (!movementStates.onGround && !movementStates.swimming) ? convertToNewRange(horizontalSpeed, movementSettings.airFrictionMinSpeed, movementSettings.airFrictionMaxSpeed, movementSettings.airFrictionMax, movementSettings.airFrictionMin) : (1.0F - drag);
/*     */     } 
/*     */     
/* 463 */     float clientDrag = 0.0F;
/* 464 */     float clientFriction = 1.0F;
/* 465 */     if (!clientStates.flying && !clientStates.climbing) {
/* 466 */       clientDrag = (!clientStates.onGround && !clientStates.swimming) ? convertToNewRange(horizontalSpeed, movementSettings.airDragMinSpeed, movementSettings.airDragMaxSpeed, movementSettings.airDragMin, movementSettings.airDragMax) : 0.82F;
/* 467 */       clientFriction = (!clientStates.onGround && !clientStates.swimming) ? convertToNewRange(horizontalSpeed, movementSettings.airFrictionMinSpeed, movementSettings.airFrictionMaxSpeed, movementSettings.airFrictionMax, movementSettings.airFrictionMin) : (1.0F - clientDrag);
/*     */     } 
/*     */     
/* 470 */     return friction / clientFriction;
/*     */   }
/*     */   
/*     */   private float computeMoveForce(@Nonnull KnockbackSimulation simulation, @Nonnull MovementStates movementStates, @Nonnull MovementSettings movementSettings) {
/* 474 */     float drag = 0.0F;
/* 475 */     float friction = 1.0F;
/*     */     
/* 477 */     Vector3d velocity = simulation.getSimVelocity();
/* 478 */     float horizontalSpeed = (float)Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
/*     */ 
/*     */     
/* 481 */     if (!movementStates.flying && !movementStates.climbing) {
/* 482 */       drag = (!movementStates.onGround && !movementStates.swimming) ? convertToNewRange(horizontalSpeed, movementSettings.airDragMinSpeed, movementSettings.airDragMaxSpeed, movementSettings.airDragMin, movementSettings.airDragMax) : 0.82F;
/* 483 */       friction = (!movementStates.onGround && !movementStates.swimming) ? (convertToNewRange(horizontalSpeed, movementSettings.airFrictionMinSpeed, movementSettings.airFrictionMaxSpeed, movementSettings.airFrictionMax, movementSettings.airFrictionMin) / (1.0F - drag)) : 1.0F;
/*     */     } 
/*     */     
/* 486 */     velocity.x *= drag;
/* 487 */     velocity.z *= drag;
/*     */     
/* 489 */     return friction;
/*     */   }
/*     */   
/*     */   private static float convertToNewRange(float value, float oldMinRange, float oldMaxRange, float newMinRange, float newMaxRange) {
/* 493 */     if (newMinRange == newMaxRange || oldMinRange == oldMaxRange) return newMinRange;
/*     */     
/* 495 */     float newValue = (value - oldMinRange) * (newMaxRange - newMinRange) / (oldMaxRange - oldMinRange) + newMinRange;
/* 496 */     return MathUtil.clamp(newValue, Math.min(newMinRange, newMaxRange), Math.max(newMinRange, newMaxRange));
/*     */   }
/*     */   
/*     */   public void applyMovementOffset(@Nonnull World world, @Nonnull Box hitBox, @Nonnull KnockbackSimulation simulation, @Nonnull MovementStates movementStates, @Nonnull Vector3d movementOffset) {
/* 500 */     int moveCycles = (int)Math.ceil(movementOffset.length() / 0.25D);
/* 501 */     Vector3d cycleMovementOffset = (moveCycles == 1) ? movementOffset : movementOffset.clone().scale((1.0F / moveCycles));
/*     */     
/* 503 */     simulation.setWasOnGround(movementStates.onGround);
/*     */     
/* 505 */     for (int i = 0; i < moveCycles; i++) {
/* 506 */       doMoveCycle(world, hitBox, simulation, movementStates, cycleMovementOffset);
/*     */     }
/*     */     
/* 509 */     if (movementStates.onGround) {
/* 510 */       (simulation.getSimVelocity()).y = 0.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void doMoveCycle(@Nonnull World world, @Nonnull Box hitBox, @Nonnull KnockbackSimulation simulation, @Nonnull MovementStates movementStates, @Nonnull Vector3d offset) {
/* 517 */     Vector3d simPos = simulation.getSimPosition();
/* 518 */     Vector3d velocity = simulation.getSimVelocity();
/* 519 */     Vector3d checkPosition = simulation.getCheckPosition();
/* 520 */     checkPosition.assign(simPos);
/* 521 */     CollisionResult collisionResult = simulation.getCollisionResult();
/* 522 */     collisionResult.reset();
/*     */ 
/*     */     
/* 525 */     checkPosition.y += offset.y;
/* 526 */     boolean hasCollidedY = checkCollision(simulation, world, hitBox, checkPosition, offset, KnockbackPredictionSystems.CollisionAxis.Y, collisionResult);
/*     */     
/* 528 */     if (movementStates.onGround && offset.y < 0.0D) {
/* 529 */       if (!hasCollidedY) {
/* 530 */         movementStates.onGround = false;
/*     */       } else {
/* 532 */         checkPosition.y -= offset.y;
/*     */       } 
/* 534 */     } else if (hasCollidedY) {
/* 535 */       if (offset.y <= 0.0D) {
/* 536 */         movementStates.onGround = true;
/* 537 */         checkPosition.y -= offset.y;
/*     */       } else {
/* 539 */         movementStates.onGround = false;
/* 540 */         checkPosition.y -= offset.y;
/*     */       } 
/*     */       
/* 543 */       velocity.y = 0.0D;
/*     */     } else {
/* 545 */       movementStates.onGround = false;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 550 */     if (offset.x != 0.0D) {
/* 551 */       checkPosition.x += offset.x;
/* 552 */       collisionResult.reset();
/* 553 */       boolean hasCollidedX = checkCollision(simulation, world, hitBox, checkPosition, offset, KnockbackPredictionSystems.CollisionAxis.Y, collisionResult);
/* 554 */       if (hasCollidedX) {
/* 555 */         checkPosition.x -= offset.x;
/*     */       }
/*     */     } 
/*     */     
/* 559 */     if (offset.z != 0.0D) {
/* 560 */       checkPosition.z += offset.z;
/* 561 */       collisionResult.reset();
/* 562 */       boolean hasCollidedZ = checkCollision(simulation, world, hitBox, checkPosition, offset, KnockbackPredictionSystems.CollisionAxis.Y, collisionResult);
/* 563 */       if (hasCollidedZ) {
/* 564 */         checkPosition.z -= offset.z;
/*     */       }
/*     */     } 
/*     */     
/* 568 */     simPos.assign(checkPosition);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean checkCollision(@Nonnull KnockbackSimulation simulation, @Nonnull World world, @Nonnull Box hitBox, @Nonnull Vector3d position, Vector3d moveOffset, KnockbackPredictionSystems.CollisionAxis axis, @Nonnull CollisionResult result) {
/* 573 */     Vector3d tempPosition = simulation.getTempPosition();
/* 574 */     tempPosition.assign(position);
/* 575 */     tempPosition.add(0.0D, 9.999999747378752E-5D, 0.0D);
/*     */     
/* 577 */     return (CollisionModule.get().validatePosition(world, hitBox, tempPosition, result) != 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\KnockbackPredictionSystems$SimulateKnockback.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */