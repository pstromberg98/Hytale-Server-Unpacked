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
/*  67 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)itemPhysicsComponentType, (Query)boundingBoxComponentType, (Query)velocityComponentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  73 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  83 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/*  85 */     ItemPhysicsComponent itemPhysicsComponent = (ItemPhysicsComponent)archetypeChunk.getComponent(index, this.itemPhysicsComponentType);
/*  86 */     assert itemPhysicsComponent != null;
/*     */     
/*  88 */     Velocity velocityComponent = (Velocity)archetypeChunk.getComponent(index, this.velocityComponentType);
/*  89 */     assert velocityComponent != null;
/*     */     
/*  91 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/*  92 */     assert transformComponent != null;
/*     */     
/*  94 */     Vector3d position = transformComponent.getPosition();
/*     */     
/*  96 */     Vector3d scaledVelocity = itemPhysicsComponent.scaledVelocity;
/*  97 */     CollisionResult collisionResult = itemPhysicsComponent.collisionResult;
/*  98 */     velocityComponent.assignVelocityTo(scaledVelocity).scale(dt);
/*     */ 
/*     */     
/* 101 */     BoundingBox boundingBoxComponent = (BoundingBox)archetypeChunk.getComponent(index, this.boundingBoxComponentType);
/* 102 */     assert boundingBoxComponent != null;
/* 103 */     Box boundingBox = boundingBoxComponent.getBoundingBox();
/*     */     
/* 105 */     if (CollisionModule.isBelowMovementThreshold(scaledVelocity)) {
/*     */       
/* 107 */       CollisionModule.findBlockCollisionsShortDistance(world, boundingBox, position, scaledVelocity, collisionResult);
/*     */     } else {
/* 109 */       CollisionModule.findBlockCollisionsIterative(world, boundingBox, position, scaledVelocity, true, collisionResult);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     BlockCollisionData blockCollisionData = collisionResult.getFirstBlockCollision();
/* 116 */     if (blockCollisionData != null) {
/* 117 */       if (blockCollisionData.collisionNormal.equals(Vector3d.UP)) {
/* 118 */         velocityComponent.setZero();
/* 119 */         position.assign(blockCollisionData.collisionPoint);
/*     */       } else {
/* 121 */         Vector3d velocity = velocityComponent.getVelocity();
/* 122 */         double dot = velocity.dot(blockCollisionData.collisionNormal);
/* 123 */         Vector3d velocityToCancel = blockCollisionData.collisionNormal.clone().scale(dot);
/* 124 */         velocity.subtract(velocityToCancel);
/*     */       } 
/*     */     } else {
/* 127 */       velocityComponent.assignVelocityTo(scaledVelocity).scale(dt);
/* 128 */       position.add(scaledVelocity);
/*     */     } 
/*     */ 
/*     */     
/* 132 */     collisionResult.reset();
/*     */ 
/*     */     
/* 135 */     if (position.getY() < -32.0D) {
/* 136 */       LOGGER.at(Level.WARNING).log("Item fell out of the world %s", archetypeChunk.getReferenceTo(index));
/* 137 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\item\ItemPhysicsSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */