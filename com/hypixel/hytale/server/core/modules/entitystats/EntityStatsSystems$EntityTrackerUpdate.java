/*     */ package com.hypixel.hytale.server.core.modules.entitystats;
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
/*     */ import com.hypixel.hytale.component.dependency.SystemTypeDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdate;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdateType;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class EntityTrackerUpdate
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   private final ComponentType<EntityStore, EntityStatMap> componentType;
/* 351 */   private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityTrackerSystems.Visible.getComponentType();
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/* 354 */   private final Set<Dependency<EntityStore>> dependencies = (Set)Set.of(new SystemDependency(Order.BEFORE, EntityTrackerSystems.EffectControllerSystem.class), new SystemTypeDependency(Order.AFTER, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 360 */         EntityStatsModule.get().getStatModifyingSystemType()));
/*     */ 
/*     */   
/*     */   public EntityTrackerUpdate(ComponentType<EntityStore, EntityStatMap> componentType) {
/* 364 */     this.componentType = componentType;
/* 365 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)this.visibleComponentType, (Query)componentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 371 */     return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 377 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 383 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 388 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 393 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/* 394 */     EntityTrackerSystems.Visible visible = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.visibleComponentType);
/* 395 */     EntityStatMap statMap = (EntityStatMap)archetypeChunk.getComponent(index, this.componentType);
/*     */ 
/*     */     
/* 398 */     if (!visible.newlyVisibleTo.isEmpty()) {
/* 399 */       queueUpdatesForNewlyVisible(ref, statMap, visible.newlyVisibleTo);
/*     */     }
/*     */     
/* 402 */     if (statMap.consumeSelfNetworkOutdated()) {
/*     */       
/* 404 */       EntityTrackerSystems.EntityViewer selfEntityViewer = (EntityTrackerSystems.EntityViewer)visible.visibleTo.get(ref);
/* 405 */       if (selfEntityViewer != null && !visible.newlyVisibleTo.containsKey(ref)) {
/* 406 */         ComponentUpdate update = new ComponentUpdate();
/* 407 */         update.type = ComponentUpdateType.EntityStats;
/* 408 */         update.entityStatUpdates = (Map)statMap.consumeSelfUpdates();
/* 409 */         selfEntityViewer.queueUpdate(ref, update);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 414 */     if (statMap.consumeNetworkOutdated()) {
/* 415 */       ComponentUpdate update = new ComponentUpdate();
/* 416 */       update.type = ComponentUpdateType.EntityStats;
/* 417 */       update.entityStatUpdates = (Map)statMap.consumeOtherUpdates();
/*     */       
/* 419 */       for (Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : (Iterable<Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer>>)visible.visibleTo.entrySet()) {
/* 420 */         Ref<EntityStore> viewerRef = entry.getKey();
/*     */ 
/*     */         
/* 423 */         if (visible.newlyVisibleTo.containsKey(viewerRef)) {
/*     */           continue;
/*     */         }
/* 426 */         if (ref.equals(viewerRef))
/*     */           continue; 
/* 428 */         ((EntityTrackerSystems.EntityViewer)entry.getValue()).queueUpdate(ref, update);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void queueUpdatesForNewlyVisible(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> newlyVisibleTo) {
/* 434 */     ComponentUpdate update = new ComponentUpdate();
/* 435 */     update.type = ComponentUpdateType.EntityStats;
/* 436 */     update.entityStatUpdates = (Map)statMap.createInitUpdate(false);
/*     */     
/* 438 */     for (Map.Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : newlyVisibleTo.entrySet()) {
/*     */       
/* 440 */       if (ref.equals(entry.getKey())) {
/* 441 */         queueUpdateForNewlyVisibleSelf(ref, statMap, entry.getValue());
/*     */         
/*     */         continue;
/*     */       } 
/* 445 */       ((EntityTrackerSystems.EntityViewer)entry.getValue()).queueUpdate(ref, update);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void queueUpdateForNewlyVisibleSelf(Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull EntityTrackerSystems.EntityViewer viewer) {
/* 450 */     ComponentUpdate update = new ComponentUpdate();
/* 451 */     update.type = ComponentUpdateType.EntityStats;
/* 452 */     update.entityStatUpdates = (Map)statMap.createInitUpdate(true);
/*     */     
/* 454 */     viewer.queueUpdate(ref, update);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entitystats\EntityStatsSystems$EntityTrackerUpdate.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */