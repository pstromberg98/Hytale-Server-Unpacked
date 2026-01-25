/*     */ package com.hypixel.hytale.server.core.modules.entity.item;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.modules.collision.BlockCollisionData;
/*     */ import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
/*     */ import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class ItemPhysicsSystem extends EntityTickingSystem<EntityStore> {
/*     */   @Nonnull
/*  23 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, ItemPhysicsComponent> itemPhysicsComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, Velocity> velocityComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, TransformComponent> transformComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemPhysicsSystem(@Nonnull ComponentType<EntityStore, ItemPhysicsComponent> itemPhysicsComponentType, @Nonnull ComponentType<EntityStore, Velocity> velocityComponentType, @Nonnull ComponentType<EntityStore, BoundingBox> boundingBoxComponentType) {
/*  63 */     this.itemPhysicsComponentType = itemPhysicsComponentType;
/*  64 */     this.velocityComponentType = velocityComponentType;
/*  65 */     this.boundingBoxComponentType = boundingBoxComponentType;
/*  66 */     this.transformComponentType = TransformComponent.getComponentType();
/*  67 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)itemPhysicsComponentType, (Query)boundingBoxComponentType, (Query)velocityComponentType, (Query)this.transformComponentType });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  78 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  88 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/*  90 */     ItemPhysicsComponent itemPhysicsComponent = (ItemPhysicsComponent)archetypeChunk.getComponent(index, this.itemPhysicsComponentType);
/*  91 */     assert itemPhysicsComponent != null;
/*     */     
/*  93 */     Velocity velocityComponent = (Velocity)archetypeChunk.getComponent(index, this.velocityComponentType);
/*  94 */     assert velocityComponent != null;
/*     */     
/*  96 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/*  97 */     assert transformComponent != null;
/*     */     
/*  99 */     Vector3d position = transformComponent.getPosition();
/*     */     
/* 101 */     Vector3d scaledVelocity = itemPhysicsComponent.scaledVelocity;
/* 102 */     CollisionResult collisionResult = itemPhysicsComponent.collisionResult;
/* 103 */     velocityComponent.assignVelocityTo(scaledVelocity).scale(dt);
/*     */ 
/*     */     
/* 106 */     BoundingBox boundingBoxComponent = (BoundingBox)archetypeChunk.getComponent(index, this.boundingBoxComponentType);
/* 107 */     assert boundingBoxComponent != null;
/* 108 */     Box boundingBox = boundingBoxComponent.getBoundingBox();
/*     */     
/* 110 */     if (CollisionModule.isBelowMovementThreshold(scaledVelocity)) {
/*     */       
/* 112 */       CollisionModule.findBlockCollisionsShortDistance(world, boundingBox, position, scaledVelocity, collisionResult);
/*     */     } else {
/* 114 */       CollisionModule.findBlockCollisionsIterative(world, boundingBox, position, scaledVelocity, true, collisionResult);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 120 */     BlockCollisionData blockCollisionData = collisionResult.getFirstBlockCollision();
/* 121 */     if (blockCollisionData != null) {
/* 122 */       if (blockCollisionData.collisionNormal.equals(Vector3d.UP)) {
/* 123 */         velocityComponent.setZero();
/* 124 */         position.assign(blockCollisionData.collisionPoint);
/*     */       } else {
/* 126 */         Vector3d velocity = velocityComponent.getVelocity();
/* 127 */         double dot = velocity.dot(blockCollisionData.collisionNormal);
/* 128 */         Vector3d velocityToCancel = blockCollisionData.collisionNormal.clone().scale(dot);
/* 129 */         velocity.subtract(velocityToCancel);
/*     */       } 
/*     */     } else {
/* 132 */       velocityComponent.assignVelocityTo(scaledVelocity).scale(dt);
/* 133 */       position.add(scaledVelocity);
/*     */     } 
/*     */ 
/*     */     
/* 137 */     collisionResult.reset();
/*     */ 
/*     */     
/* 140 */     if (position.getY() < -32.0D) {
/* 141 */       LOGGER.at(Level.WARNING).log("Item fell out of the world %s", archetypeChunk.getReferenceTo(index));
/* 142 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\item\ItemPhysicsSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */