/*     */ package com.hypixel.hytale.server.core.modules.entity.tracker;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdate;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdateType;
/*     */ import com.hypixel.hytale.protocol.Equipment;
/*     */ import com.hypixel.hytale.protocol.ItemArmorSlot;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.PlayerConfig;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
/*     */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class LegacyEntityTrackerSystems {
/*     */   @Deprecated
/*     */   public static boolean clear(@Nonnull Player player, @Nonnull Holder<EntityStore> holder) {
/*  47 */     World world = player.getWorld();
/*  48 */     if (world != null && world.isInThread()) {
/*  49 */       Ref<EntityStore> ref = player.getReference();
/*  50 */       if (ref == null || !ref.isValid()) return false;
/*     */       
/*  52 */       return EntityTrackerSystems.clear(ref, world.getEntityStore().getStore());
/*     */     } 
/*     */     
/*  55 */     EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)holder.getComponent(EntityTrackerSystems.EntityViewer.getComponentType());
/*  56 */     if (entityViewerComponent == null) return false;
/*     */ 
/*     */     
/*  59 */     entityViewerComponent.sent.clear();
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class LegacyLODCull
/*     */     extends EntityTickingSystem<EntityStore>
/*     */   {
/*     */     public static final double ENTITY_LOD_RATIO_DEFAULT = 3.5E-5D;
/*     */     
/*  69 */     public static double ENTITY_LOD_RATIO = 3.5E-5D;
/*     */     
/*     */     private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
/*     */     private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     @Nonnull
/*     */     private final Set<Dependency<EntityStore>> dependencies;
/*     */     
/*     */     public LegacyLODCull(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
/*  79 */       this.entityViewerComponentType = entityViewerComponentType;
/*  80 */       this.boundingBoxComponentType = BoundingBox.getComponentType();
/*  81 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)entityViewerComponentType, (Query)TransformComponent.getComponentType() });
/*  82 */       this.dependencies = Collections.singleton(new SystemDependency(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/*  88 */       return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/*  94 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 100 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 105 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 110 */       EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)archetypeChunk.getComponent(index, this.entityViewerComponentType);
/* 111 */       assert entityViewerComponent != null;
/*     */       
/* 113 */       TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/* 114 */       assert transformComponent != null;
/* 115 */       Vector3d position = transformComponent.getPosition();
/*     */       
/* 117 */       for (Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator(); iterator.hasNext(); ) {
/* 118 */         Ref<EntityStore> targetRef = iterator.next();
/*     */ 
/*     */         
/* 121 */         BoundingBox targetBoundingBoxComponent = (BoundingBox)commandBuffer.getComponent(targetRef, this.boundingBoxComponentType);
/* 122 */         if (targetBoundingBoxComponent == null)
/*     */           continue; 
/* 124 */         TransformComponent targetTransformComponent = (TransformComponent)commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
/* 125 */         if (targetTransformComponent == null) {
/*     */           continue;
/*     */         }
/* 128 */         double distanceSq = targetTransformComponent.getPosition().distanceSquaredTo(position);
/* 129 */         double maximumThickness = targetBoundingBoxComponent.getBoundingBox().getMaximumThickness();
/* 130 */         if (maximumThickness < ENTITY_LOD_RATIO * distanceSq) {
/* 131 */           entityViewerComponent.lodExcludedCount++;
/* 132 */           iterator.remove();
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class LegacyHideFromEntity
/*     */     extends EntityTickingSystem<EntityStore>
/*     */   {
/*     */     private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
/*     */     
/*     */     private final ComponentType<EntityStore, PlayerSettings> playerSettingsComponentType;
/*     */     
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     
/*     */     @Nonnull
/*     */     private final Set<Dependency<EntityStore>> dependencies;
/*     */     
/*     */     public LegacyHideFromEntity(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
/* 153 */       this.entityViewerComponentType = entityViewerComponentType;
/* 154 */       this.playerSettingsComponentType = EntityModule.get().getPlayerSettingsComponentType();
/* 155 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)entityViewerComponentType, (Query)AllLegacyLivingEntityTypesQuery.INSTANCE });
/* 156 */       this.dependencies = Collections.singleton(new SystemDependency(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 162 */       return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 168 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 174 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 179 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 184 */       Ref<EntityStore> viewerRef = archetypeChunk.getReferenceTo(index);
/* 185 */       PlayerSettings settings = (PlayerSettings)archetypeChunk.getComponent(index, this.playerSettingsComponentType);
/* 186 */       if (settings == null) {
/* 187 */         settings = PlayerSettings.defaults();
/*     */       }
/*     */       
/* 190 */       EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)archetypeChunk.getComponent(index, this.entityViewerComponentType);
/* 191 */       assert entityViewerComponent != null;
/*     */       
/* 193 */       for (Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator(); iterator.hasNext(); ) {
/* 194 */         Ref<EntityStore> ref = iterator.next();
/*     */         
/* 196 */         Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer);
/* 197 */         if (entity == null)
/*     */           continue; 
/* 199 */         if (entity.isHiddenFromLivingEntity(ref, viewerRef, (ComponentAccessor)commandBuffer) && canHideEntities(entity, settings)) {
/* 200 */           entityViewerComponent.hiddenCount++;
/* 201 */           iterator.remove();
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     private static boolean canHideEntities(Entity entity, @Nonnull PlayerSettings settings) {
/* 207 */       return (entity instanceof Player && !settings.showEntityMarkers());
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LegacyEntityModel extends EntityTickingSystem<EntityStore> {
/*     */     private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
/*     */     private final ComponentType<EntityStore, ModelComponent> modelComponentType;
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     
/*     */     public LegacyEntityModel(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
/* 218 */       this.componentType = componentType;
/* 219 */       this.modelComponentType = ModelComponent.getComponentType();
/* 220 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)componentType, (Query)this.modelComponentType });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 226 */       return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 232 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 237 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 242 */       EntityTrackerSystems.Visible visibleComponent = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.componentType);
/* 243 */       assert visibleComponent != null;
/*     */       
/* 245 */       ModelComponent modelComponent = (ModelComponent)archetypeChunk.getComponent(index, this.modelComponentType);
/* 246 */       assert modelComponent != null;
/*     */       
/* 248 */       float entityScale = 0.0F;
/* 249 */       boolean scaleOutdated = false;
/* 250 */       EntityScaleComponent entityScaleComponent = (EntityScaleComponent)archetypeChunk.getComponent(index, EntityScaleComponent.getComponentType());
/* 251 */       if (entityScaleComponent != null) {
/* 252 */         entityScale = entityScaleComponent.getScale();
/* 253 */         scaleOutdated = entityScaleComponent.consumeNetworkOutdated();
/*     */       } 
/*     */       
/* 256 */       boolean modelOutdated = modelComponent.consumeNetworkOutdated();
/* 257 */       if (modelOutdated || scaleOutdated) {
/* 258 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), modelComponent, entityScale, visibleComponent.visibleTo);
/* 259 */       } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
/* 260 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), modelComponent, entityScale, visibleComponent.newlyVisibleTo);
/*     */       } 
/*     */     }
/*     */     
/*     */     private static void queueUpdatesFor(Ref<EntityStore> ref, @Nullable ModelComponent model, float entityScale, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
/* 265 */       ComponentUpdate update = new ComponentUpdate();
/* 266 */       update.type = ComponentUpdateType.Model;
/* 267 */       update.model = (model != null) ? model.getModel().toPacket() : null;
/* 268 */       update.entityScale = entityScale;
/*     */       
/* 270 */       for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values())
/* 271 */         viewer.queueUpdate(ref, update); 
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LegacyEntitySkin
/*     */     extends EntityTickingSystem<EntityStore> {
/*     */     private final ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType;
/*     */     private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType;
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     
/*     */     public LegacyEntitySkin(ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType, ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType) {
/* 283 */       this.visibleComponentType = visibleComponentType;
/* 284 */       this.playerSkinComponentComponentType = playerSkinComponentComponentType;
/* 285 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)visibleComponentType, (Query)playerSkinComponentComponentType });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 291 */       return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 297 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 302 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 307 */       EntityTrackerSystems.Visible visibleComponent = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.visibleComponentType);
/* 308 */       assert visibleComponent != null;
/*     */       
/* 310 */       PlayerSkinComponent playerSkinComponent = (PlayerSkinComponent)archetypeChunk.getComponent(index, this.playerSkinComponentComponentType);
/* 311 */       assert playerSkinComponent != null;
/*     */ 
/*     */       
/* 314 */       if (playerSkinComponent.consumeNetworkOutdated()) {
/* 315 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), playerSkinComponent, visibleComponent.visibleTo);
/* 316 */       } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
/* 317 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), playerSkinComponent, visibleComponent.newlyVisibleTo);
/*     */       } 
/*     */     }
/*     */     
/*     */     private static void queueUpdatesFor(Ref<EntityStore> ref, @Nonnull PlayerSkinComponent component, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
/* 322 */       ComponentUpdate update = new ComponentUpdate();
/* 323 */       update.type = ComponentUpdateType.PlayerSkin;
/* 324 */       update.skin = component.getPlayerSkin();
/*     */       
/* 326 */       for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values())
/* 327 */         viewer.queueUpdate(ref, update); 
/*     */     }
/*     */   }
/*     */   
/*     */   public static class LegacyEquipment
/*     */     extends EntityTickingSystem<EntityStore> {
/*     */     private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     
/*     */     public LegacyEquipment(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
/* 338 */       this.componentType = componentType;
/* 339 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)componentType, (Query)AllLegacyLivingEntityTypesQuery.INSTANCE });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 345 */       return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 351 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 356 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 361 */       EntityTrackerSystems.Visible visibleComponent = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.componentType);
/* 362 */       assert visibleComponent != null;
/*     */       
/* 364 */       LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
/* 365 */       assert entity != null;
/*     */ 
/*     */       
/* 368 */       if (entity.consumeEquipmentNetworkOutdated()) {
/* 369 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.visibleTo);
/* 370 */       } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
/* 371 */         queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.newlyVisibleTo);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static void queueUpdatesFor(@Nonnull Ref<EntityStore> ref, @Nonnull LivingEntity entity, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
/* 378 */       ComponentUpdate update = new ComponentUpdate();
/* 379 */       update.type = ComponentUpdateType.Equipment;
/* 380 */       update.equipment = new Equipment();
/*     */       
/* 382 */       Inventory inventory = entity.getInventory();
/*     */       
/* 384 */       ItemContainer armor = inventory.getArmor();
/* 385 */       update.equipment.armorIds = new String[armor.getCapacity()];
/* 386 */       Arrays.fill((Object[])update.equipment.armorIds, "");
/* 387 */       armor.forEachWithMeta((slot, itemStack, armorIds) -> armorIds[slot] = itemStack.getItemId(), update.equipment.armorIds);
/*     */ 
/*     */ 
/*     */       
/* 391 */       Store<EntityStore> store = ref.getStore();
/* 392 */       PlayerSettings playerSettings = (PlayerSettings)store.getComponent(ref, PlayerSettings.getComponentType());
/* 393 */       if (playerSettings != null) {
/* 394 */         PlayerConfig.ArmorVisibilityOption armorVisibilityOption = ((EntityStore)store.getExternalData()).getWorld().getGameplayConfig().getPlayerConfig().getArmorVisibilityOption();
/* 395 */         if (armorVisibilityOption.canHideHelmet() && playerSettings.hideHelmet()) {
/* 396 */           update.equipment.armorIds[ItemArmorSlot.Head.ordinal()] = "";
/*     */         }
/* 398 */         if (armorVisibilityOption.canHideCuirass() && playerSettings.hideCuirass()) {
/* 399 */           update.equipment.armorIds[ItemArmorSlot.Chest.ordinal()] = "";
/*     */         }
/* 401 */         if (armorVisibilityOption.canHideGauntlets() && playerSettings.hideGauntlets()) {
/* 402 */           update.equipment.armorIds[ItemArmorSlot.Hands.ordinal()] = "";
/*     */         }
/* 404 */         if (armorVisibilityOption.canHidePants() && playerSettings.hidePants()) {
/* 405 */           update.equipment.armorIds[ItemArmorSlot.Legs.ordinal()] = "";
/*     */         }
/*     */       } 
/*     */       
/* 409 */       ItemStack itemInHand = inventory.getItemInHand();
/* 410 */       update.equipment.rightHandItemId = (itemInHand != null) ? itemInHand.getItemId() : "Empty";
/*     */       
/* 412 */       ItemStack utilityItem = inventory.getUtilityItem();
/* 413 */       update.equipment.leftHandItemId = (utilityItem != null) ? utilityItem.getItemId() : "Empty";
/*     */       
/* 415 */       for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values())
/* 416 */         viewer.queueUpdate(ref, update); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\tracker\LegacyEntityTrackerSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */