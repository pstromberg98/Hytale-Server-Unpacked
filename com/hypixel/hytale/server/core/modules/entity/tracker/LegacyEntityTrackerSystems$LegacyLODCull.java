/*     */ package com.hypixel.hytale.server.core.modules.entity.tracker;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ public class LegacyLODCull
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   public static final double ENTITY_LOD_RATIO_DEFAULT = 3.5E-5D;
/*  69 */   public static double ENTITY_LOD_RATIO = 3.5E-5D;
/*     */   
/*     */   private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
/*     */   private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
/*     */   
/*     */   public LegacyLODCull(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
/*  79 */     this.entityViewerComponentType = entityViewerComponentType;
/*  80 */     this.boundingBoxComponentType = BoundingBox.getComponentType();
/*  81 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)entityViewerComponentType, (Query)TransformComponent.getComponentType() });
/*  82 */     this.dependencies = Collections.singleton(new SystemDependency(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/*  88 */     return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/*  94 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 100 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 105 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 110 */     EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)archetypeChunk.getComponent(index, this.entityViewerComponentType);
/* 111 */     assert entityViewerComponent != null;
/*     */     
/* 113 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/* 114 */     assert transformComponent != null;
/* 115 */     Vector3d position = transformComponent.getPosition();
/*     */     
/* 117 */     for (Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator(); iterator.hasNext(); ) {
/* 118 */       Ref<EntityStore> targetRef = iterator.next();
/*     */ 
/*     */       
/* 121 */       BoundingBox targetBoundingBoxComponent = (BoundingBox)commandBuffer.getComponent(targetRef, this.boundingBoxComponentType);
/* 122 */       if (targetBoundingBoxComponent == null)
/*     */         continue; 
/* 124 */       TransformComponent targetTransformComponent = (TransformComponent)commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
/* 125 */       if (targetTransformComponent == null) {
/*     */         continue;
/*     */       }
/* 128 */       double distanceSq = targetTransformComponent.getPosition().distanceSquaredTo(position);
/* 129 */       double maximumThickness = targetBoundingBoxComponent.getBoundingBox().getMaximumThickness();
/* 130 */       if (maximumThickness < ENTITY_LOD_RATIO * distanceSq) {
/* 131 */         entityViewerComponent.lodExcludedCount++;
/* 132 */         iterator.remove();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\tracker\LegacyEntityTrackerSystems$LegacyLODCull.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */