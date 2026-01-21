/*    */ package com.hypixel.hytale.server.core.modules.entity.system;
/*    */ 
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.SystemGroup;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*    */ import com.hypixel.hytale.protocol.ComponentUpdate;
/*    */ import com.hypixel.hytale.protocol.ComponentUpdateType;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.RespondToHit;
/*    */ import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EntityTrackerUpdate
/*    */   extends EntityTickingSystem<EntityStore>
/*    */ {
/*    */   private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
/*    */   @Nonnull
/*    */   private final Query<EntityStore> query;
/*    */   
/*    */   public EntityTrackerUpdate(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
/* 52 */     this.componentType = componentType;
/* 53 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)componentType, (Query)RespondToHit.getComponentType() });
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public SystemGroup<EntityStore> getGroup() {
/* 59 */     return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Query<EntityStore> getQuery() {
/* 65 */     return this.query;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 70 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
/* 75 */     super.tick(dt, systemIndex, store);
/*    */ 
/*    */     
/* 78 */     ((RespondToHitSystems.QueueResource)store.getResource(RespondToHitSystems.QueueResource.getResourceType())).queue.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 83 */     EntityTrackerSystems.Visible visible = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.componentType);
/*    */     
/* 85 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*    */ 
/*    */     
/* 88 */     if (((RespondToHitSystems.QueueResource)commandBuffer.getResource(RespondToHitSystems.QueueResource.getResourceType())).queue.remove(ref)) {
/* 89 */       queueUpdatesFor(ref, visible.visibleTo);
/* 90 */     } else if (!visible.newlyVisibleTo.isEmpty()) {
/* 91 */       queueUpdatesFor(ref, visible.newlyVisibleTo);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static void queueUpdatesFor(Ref<EntityStore> ref, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
/* 96 */     ComponentUpdate update = new ComponentUpdate();
/* 97 */     update.type = ComponentUpdateType.RespondToHit;
/* 98 */     for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values())
/* 99 */       viewer.queueUpdate(ref, update); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\system\RespondToHitSystems$EntityTrackerUpdate.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */