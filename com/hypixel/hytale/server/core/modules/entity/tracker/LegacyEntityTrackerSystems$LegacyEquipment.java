/*     */ package com.hypixel.hytale.server.core.modules.entity.tracker;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdate;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdateType;
/*     */ import com.hypixel.hytale.protocol.Equipment;
/*     */ import com.hypixel.hytale.protocol.ItemArmorSlot;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.PlayerConfig;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
/*     */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
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
/*     */ public class LegacyEquipment
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   
/*     */   public LegacyEquipment(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
/* 338 */     this.componentType = componentType;
/* 339 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)componentType, (Query)AllLegacyLivingEntityTypesQuery.INSTANCE });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 345 */     return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 351 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 356 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 361 */     EntityTrackerSystems.Visible visibleComponent = (EntityTrackerSystems.Visible)archetypeChunk.getComponent(index, this.componentType);
/* 362 */     assert visibleComponent != null;
/*     */     
/* 364 */     LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);
/* 365 */     assert entity != null;
/*     */ 
/*     */     
/* 368 */     if (entity.consumeEquipmentNetworkOutdated()) {
/* 369 */       queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.visibleTo);
/* 370 */     } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
/* 371 */       queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.newlyVisibleTo);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void queueUpdatesFor(@Nonnull Ref<EntityStore> ref, @Nonnull LivingEntity entity, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo) {
/* 378 */     ComponentUpdate update = new ComponentUpdate();
/* 379 */     update.type = ComponentUpdateType.Equipment;
/* 380 */     update.equipment = new Equipment();
/*     */     
/* 382 */     Inventory inventory = entity.getInventory();
/*     */     
/* 384 */     ItemContainer armor = inventory.getArmor();
/* 385 */     update.equipment.armorIds = new String[armor.getCapacity()];
/* 386 */     Arrays.fill((Object[])update.equipment.armorIds, "");
/* 387 */     armor.forEachWithMeta((slot, itemStack, armorIds) -> armorIds[slot] = itemStack.getItemId(), update.equipment.armorIds);
/*     */ 
/*     */ 
/*     */     
/* 391 */     Store<EntityStore> store = ref.getStore();
/* 392 */     PlayerSettings playerSettings = (PlayerSettings)store.getComponent(ref, PlayerSettings.getComponentType());
/* 393 */     if (playerSettings != null) {
/* 394 */       PlayerConfig.ArmorVisibilityOption armorVisibilityOption = ((EntityStore)store.getExternalData()).getWorld().getGameplayConfig().getPlayerConfig().getArmorVisibilityOption();
/* 395 */       if (armorVisibilityOption.canHideHelmet() && playerSettings.hideHelmet()) {
/* 396 */         update.equipment.armorIds[ItemArmorSlot.Head.ordinal()] = "";
/*     */       }
/* 398 */       if (armorVisibilityOption.canHideCuirass() && playerSettings.hideCuirass()) {
/* 399 */         update.equipment.armorIds[ItemArmorSlot.Chest.ordinal()] = "";
/*     */       }
/* 401 */       if (armorVisibilityOption.canHideGauntlets() && playerSettings.hideGauntlets()) {
/* 402 */         update.equipment.armorIds[ItemArmorSlot.Hands.ordinal()] = "";
/*     */       }
/* 404 */       if (armorVisibilityOption.canHidePants() && playerSettings.hidePants()) {
/* 405 */         update.equipment.armorIds[ItemArmorSlot.Legs.ordinal()] = "";
/*     */       }
/*     */     } 
/*     */     
/* 409 */     ItemStack itemInHand = inventory.getItemInHand();
/* 410 */     update.equipment.rightHandItemId = (itemInHand != null) ? itemInHand.getItemId() : "Empty";
/*     */     
/* 412 */     ItemStack utilityItem = inventory.getUtilityItem();
/* 413 */     update.equipment.leftHandItemId = (utilityItem != null) ? utilityItem.getItemId() : "Empty";
/*     */     
/* 415 */     for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values())
/* 416 */       viewer.queueUpdate(ref, update); 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\tracker\LegacyEntityTrackerSystems$LegacyEquipment.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */