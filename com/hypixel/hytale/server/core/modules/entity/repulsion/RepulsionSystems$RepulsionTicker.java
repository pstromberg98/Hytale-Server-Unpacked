/*     */ package com.hypixel.hytale.server.core.modules.entity.repulsion;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.OrderPriority;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.vector.Vector2d;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.ChangeVelocityType;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.modules.physics.systems.IVelocityModifyingSystem;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RepulsionTicker
/*     */   extends EntityTickingSystem<EntityStore>
/*     */   implements IVelocityModifyingSystem
/*     */ {
/*     */   private final ComponentType<EntityStore, Repulsion> repulsionComponentType;
/*     */   private final ComponentType<EntityStore, TransformComponent> transformComponentComponentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
/*     */   private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> spatialComponent;
/*     */   
/*     */   public RepulsionTicker(ComponentType<EntityStore, Repulsion> repulsionComponentType, ComponentType<EntityStore, TransformComponent> transformComponentComponentType, ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> spatialComponent) {
/* 179 */     this.repulsionComponentType = repulsionComponentType;
/* 180 */     this.transformComponentComponentType = transformComponentComponentType;
/* 181 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)repulsionComponentType, (Query)transformComponentComponentType });
/*     */     
/* 183 */     this.spatialComponent = spatialComponent;
/* 184 */     this.dependencies = Set.of(new SystemDependency(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 190 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 196 */     return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 202 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 208 */     Repulsion repulsionComponent = (Repulsion)archetypeChunk.getComponent(index, this.repulsionComponentType);
/* 209 */     assert repulsionComponent != null;
/*     */     
/* 211 */     int repulsionConfigIndex = repulsionComponent.getRepulsionConfigIndex();
/* 212 */     if (repulsionConfigIndex == -1)
/*     */       return; 
/* 214 */     RepulsionConfig repulsion = (RepulsionConfig)RepulsionConfig.getAssetMap().getAsset(repulsionConfigIndex);
/*     */     
/* 216 */     float radius = repulsion.radius;
/* 217 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentComponentType);
/* 218 */     assert transformComponent != null;
/*     */     
/* 220 */     Vector2d position = new Vector2d((transformComponent.getPosition()).x, (transformComponent.getPosition()).z);
/*     */     
/* 222 */     SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(this.spatialComponent);
/* 223 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/* 224 */     spatialResource.getSpatialStructure().ordered(transformComponent.getPosition(), radius, (List)objectArrayList);
/*     */     
/* 226 */     for (Ref<EntityStore> entityRef : (Iterable<Ref<EntityStore>>)objectArrayList) {
/* 227 */       TransformComponent entityTransformComponent = (TransformComponent)commandBuffer.getComponent(entityRef, this.transformComponentComponentType);
/* 228 */       if (entityTransformComponent == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 232 */       Vector2d entityPosition = new Vector2d((entityTransformComponent.getPosition()).x, (entityTransformComponent.getPosition()).z);
/*     */       
/* 234 */       if (entityPosition.equals(position))
/*     */         continue; 
/* 236 */       double distance = position.distanceTo(entityPosition);
/*     */       
/* 238 */       if (distance < 0.1D)
/*     */         continue; 
/* 240 */       double fraction = (radius - distance) / radius;
/* 241 */       float maxForce = repulsion.maxForce;
/* 242 */       int flip = 1;
/* 243 */       if (maxForce < 0.0F) {
/* 244 */         flip = -1;
/* 245 */         maxForce *= flip;
/*     */       } 
/* 247 */       double force = Math.max(repulsion.minForce, maxForce * fraction);
/* 248 */       force *= flip;
/*     */       
/* 250 */       Vector2d push = entityPosition.subtract(position);
/* 251 */       push.normalize();
/* 252 */       push.scale(force);
/*     */       
/* 254 */       Velocity entityVelocityComponent = (Velocity)commandBuffer.getComponent(entityRef, Velocity.getComponentType());
/* 255 */       if (entityVelocityComponent == null) {
/*     */         continue;
/*     */       }
/* 258 */       Vector3d addedVelocity = new Vector3d((float)push.x, 0.0D, (float)push.y);
/* 259 */       entityVelocityComponent.addInstruction(addedVelocity, null, ChangeVelocityType.Add);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\repulsion\RepulsionSystems$RepulsionTicker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */