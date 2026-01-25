/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PositionDataComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.logging.Level;
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
/*     */ public class PlayerProcessMovementSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, Player> playerComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, Velocity> velocityComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, CollisionResultComponent> collisionResultComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, PlayerRef> playerRefComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, TransformComponent> transformComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, PositionDataComponent> positionDataComponentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   
/*     */   public PlayerProcessMovementSystem(@Nonnull ComponentType<EntityStore, Player> playerComponentType, @Nonnull ComponentType<EntityStore, Velocity> velocityComponentType, @Nonnull ComponentType<EntityStore, CollisionResultComponent> collisionResultComponentType) {
/*  92 */     this.playerComponentType = playerComponentType;
/*  93 */     this.velocityComponentType = velocityComponentType;
/*  94 */     this.collisionResultComponentType = collisionResultComponentType;
/*  95 */     this.boundingBoxComponentType = BoundingBox.getComponentType();
/*  96 */     this.playerRefComponentType = PlayerRef.getComponentType();
/*  97 */     this.transformComponentType = TransformComponent.getComponentType();
/*  98 */     this.positionDataComponentType = PositionDataComponent.getComponentType();
/*  99 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)playerComponentType, (Query)this.playerRefComponentType, (Query)this.transformComponentType, (Query)this.boundingBoxComponentType, (Query)velocityComponentType, (Query)collisionResultComponentType, (Query)this.positionDataComponentType });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 106 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 111 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 116 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 117 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */     
/* 119 */     Player playerComponent = (Player)archetypeChunk.getComponent(index, this.playerComponentType);
/* 120 */     assert playerComponent != null;
/*     */     
/* 122 */     Velocity velocityComponent = (Velocity)archetypeChunk.getComponent(index, this.velocityComponentType);
/* 123 */     assert velocityComponent != null;
/*     */     
/* 125 */     CollisionResultComponent collisionResultComponent = (CollisionResultComponent)archetypeChunk.getComponent(index, this.collisionResultComponentType);
/* 126 */     assert collisionResultComponent != null;
/*     */     
/* 128 */     InteractionManager interactionManagerComponent = (InteractionManager)archetypeChunk.getComponent(index, InteractionModule.get().getInteractionManagerComponent());
/* 129 */     if (interactionManagerComponent == null) {
/*     */       return;
/*     */     }
/*     */     
/* 133 */     PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, this.playerRefComponentType);
/* 134 */     assert playerRefComponent != null;
/*     */     
/* 136 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/* 137 */     assert transformComponent != null;
/*     */ 
/*     */ 
/*     */     
/* 141 */     boolean pendingCollisionCheck = collisionResultComponent.isPendingCollisionCheck();
/*     */     
/* 143 */     collisionResultComponent.getCollisionStartPositionCopy().assign(pendingCollisionCheck ? collisionResultComponent.getCollisionStartPosition() : transformComponent.getPosition());
/* 144 */     collisionResultComponent.getCollisionPositionOffsetCopy().assign(collisionResultComponent.getCollisionPositionOffset());
/* 145 */     collisionResultComponent.resetLocationChange();
/*     */ 
/*     */     
/* 148 */     if (collisionResultComponent.getCollisionPositionOffsetCopy().squaredLength() >= 100.0D) {
/* 149 */       if (playerComponent.getGameMode() == GameMode.Adventure) {
/* 150 */         Entity.LOGGER.at(Level.WARNING).log("%s, %s: Jump in location in processMovementBlockCollisions %s", playerRefComponent.getUsername(), playerRefComponent.getUuid(), Double.valueOf(collisionResultComponent.getCollisionPositionOffsetCopy().length()));
/*     */       }
/*     */       
/* 153 */       playerComponent.resetVelocity(velocityComponent);
/*     */       
/*     */       return;
/*     */     } 
/* 157 */     BoundingBox boundingBoxComponent = (BoundingBox)archetypeChunk.getComponent(index, this.boundingBoxComponentType);
/* 158 */     assert boundingBoxComponent != null;
/*     */     
/* 160 */     Box boundingBox = boundingBoxComponent.getBoundingBox();
/*     */ 
/*     */     
/* 163 */     if (pendingCollisionCheck);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 184 */     CollisionModule.get().findIntersections(world, boundingBox, collisionResultComponent.getCollisionStartPositionCopy(), collisionResultComponent.getCollisionResult(), true, false);
/*     */ 
/*     */     
/* 187 */     playerComponent.processVelocitySample(dt, collisionResultComponent.getCollisionPositionOffsetCopy(), velocityComponent);
/*     */ 
/*     */     
/* 190 */     Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
/* 191 */     if (chunkRef != null && chunkRef.isValid()) {
/* 192 */       Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/* 193 */       WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getComponent(chunkRef, WorldChunk.getComponentType());
/* 194 */       assert worldChunkComponent != null;
/*     */       
/* 196 */       PositionDataComponent positionDataComponent = (PositionDataComponent)archetypeChunk.getComponent(index, this.positionDataComponentType);
/* 197 */       assert positionDataComponent != null;
/*     */       
/* 199 */       Vector3i blockPosition = transformComponent.getPosition().toVector3i();
/* 200 */       positionDataComponent.setInsideBlockTypeId(worldChunkComponent.getBlock(blockPosition));
/* 201 */       positionDataComponent.setStandingOnBlockTypeId(worldChunkComponent.getBlock(blockPosition.x, blockPosition.y - 1, blockPosition.z));
/*     */     } 
/*     */ 
/*     */     
/* 205 */     commandBuffer.run(_store -> {
/*     */           int damageToEntity = collisionResultComponent.getCollisionResult().defaultTriggerBlocksProcessing(interactionManagerComponent, (Entity)playerComponent, ref, playerComponent.executeTriggers, (ComponentAccessor)commandBuffer);
/*     */           if (playerComponent.executeBlockDamage && damageToEntity > 0) {
/*     */             Damage damage = new Damage(Damage.NULL_SOURCE, DamageCause.ENVIRONMENT, damageToEntity);
/*     */             DamageSystems.executeDamage(index, archetypeChunk, commandBuffer, damage);
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerProcessMovementSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */