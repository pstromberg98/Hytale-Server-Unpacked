/*     */ package com.hypixel.hytale.server.core.modules.entity.tracker;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LegacyHideFromEntity
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
/*     */   private final ComponentType<EntityStore, PlayerSettings> playerSettingsComponentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
/*     */   
/*     */   public LegacyHideFromEntity(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
/* 153 */     this.entityViewerComponentType = entityViewerComponentType;
/* 154 */     this.playerSettingsComponentType = EntityModule.get().getPlayerSettingsComponentType();
/* 155 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)entityViewerComponentType, (Query)AllLegacyLivingEntityTypesQuery.INSTANCE });
/* 156 */     this.dependencies = Collections.singleton(new SystemDependency(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 162 */     return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 168 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 174 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 179 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 184 */     Ref<EntityStore> viewerRef = archetypeChunk.getReferenceTo(index);
/* 185 */     PlayerSettings settings = (PlayerSettings)archetypeChunk.getComponent(index, this.playerSettingsComponentType);
/* 186 */     if (settings == null) {
/* 187 */       settings = PlayerSettings.defaults();
/*     */     }
/*     */     
/* 190 */     EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)archetypeChunk.getComponent(index, this.entityViewerComponentType);
/* 191 */     assert entityViewerComponent != null;
/*     */     
/* 193 */     for (Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator(); iterator.hasNext(); ) {
/* 194 */       Ref<EntityStore> ref = iterator.next();
/*     */       
/* 196 */       Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer);
/* 197 */       if (entity == null)
/*     */         continue; 
/* 199 */       if (entity.isHiddenFromLivingEntity(ref, viewerRef, (ComponentAccessor)commandBuffer) && canHideEntities(entity, settings)) {
/* 200 */         entityViewerComponent.hiddenCount++;
/* 201 */         iterator.remove();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static boolean canHideEntities(Entity entity, @Nonnull PlayerSettings settings) {
/* 207 */     return (entity instanceof com.hypixel.hytale.server.core.entity.entities.Player && !settings.showEntityMarkers());
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\tracker\LegacyEntityTrackerSystems$LegacyHideFromEntity.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */