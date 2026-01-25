/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdate;
/*     */ import com.hypixel.hytale.protocol.ComponentUpdateType;
/*     */ import com.hypixel.hytale.protocol.EntityUpdate;
/*     */ import com.hypixel.hytale.protocol.Equipment;
/*     */ import com.hypixel.hytale.protocol.ItemArmorSlot;
/*     */ import com.hypixel.hytale.protocol.ModelTransform;
/*     */ import com.hypixel.hytale.protocol.Nameplate;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.PlayerConfig;
/*     */ import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.RespondToHit;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.projectile.component.PredictedProjectile;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
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
/*     */ public class PlayerSpawnedSystem
/*     */   extends RefSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  74 */     return (Query<EntityStore>)Player.getComponentType();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  79 */     sendPlayerSelf(ref, store);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void sendPlayerSelf(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
/*  96 */     EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)store.getComponent(viewerRef, EntityTrackerSystems.EntityViewer.getComponentType());
/*  97 */     if (entityViewerComponent == null) throw new IllegalArgumentException("Viewer is missing EntityViewer component");
/*     */     
/*  99 */     NetworkId networkIdComponent = (NetworkId)store.getComponent(viewerRef, NetworkId.getComponentType());
/* 100 */     if (networkIdComponent == null) throw new IllegalArgumentException("Viewer is missing NetworkId component");
/*     */     
/* 102 */     Player playerComponent = (Player)store.getComponent(viewerRef, Player.getComponentType());
/* 103 */     if (playerComponent == null) throw new IllegalArgumentException("Viewer is missing Player component");
/*     */     
/* 105 */     EntityUpdate entityUpdate = new EntityUpdate();
/* 106 */     entityUpdate.networkId = networkIdComponent.getId();
/*     */     
/* 108 */     ObjectArrayList<ComponentUpdate> list = new ObjectArrayList();
/*     */     
/* 110 */     Archetype<EntityStore> viewerArchetype = store.getArchetype(viewerRef);
/* 111 */     if (viewerArchetype.contains(Interactable.getComponentType())) {
/* 112 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 113 */       componentUpdate.type = ComponentUpdateType.Interactable;
/* 114 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 117 */     if (viewerArchetype.contains(Intangible.getComponentType())) {
/* 118 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 119 */       componentUpdate.type = ComponentUpdateType.Intangible;
/* 120 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 123 */     if (viewerArchetype.contains(Invulnerable.getComponentType())) {
/* 124 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 125 */       componentUpdate.type = ComponentUpdateType.Invulnerable;
/* 126 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 129 */     if (viewerArchetype.contains(RespondToHit.getComponentType())) {
/* 130 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 131 */       componentUpdate.type = ComponentUpdateType.RespondToHit;
/* 132 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 135 */     Nameplate nameplateComponent = (Nameplate)store.getComponent(viewerRef, Nameplate.getComponentType());
/* 136 */     if (nameplateComponent != null) {
/* 137 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 138 */       componentUpdate.type = ComponentUpdateType.Nameplate;
/* 139 */       componentUpdate.nameplate = new Nameplate();
/* 140 */       componentUpdate.nameplate.text = nameplateComponent.getText();
/* 141 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 144 */     PredictedProjectile predictionComponent = (PredictedProjectile)store.getComponent(viewerRef, PredictedProjectile.getComponentType());
/* 145 */     if (predictionComponent != null) {
/* 146 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 147 */       componentUpdate.type = ComponentUpdateType.Prediction;
/* 148 */       componentUpdate.predictionId = predictionComponent.getUuid();
/* 149 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 152 */     ModelComponent modelComponent = (ModelComponent)store.getComponent(viewerRef, ModelComponent.getComponentType());
/*     */     
/* 154 */     ComponentUpdate update = new ComponentUpdate();
/* 155 */     update.type = ComponentUpdateType.Model;
/* 156 */     update.model = (modelComponent != null) ? modelComponent.getModel().toPacket() : null;
/*     */     
/* 158 */     EntityScaleComponent entityScaleComponent = (EntityScaleComponent)store.getComponent(viewerRef, EntityScaleComponent.getComponentType());
/* 159 */     if (entityScaleComponent != null) {
/* 160 */       update.entityScale = entityScaleComponent.getScale();
/*     */     }
/* 162 */     list.add(update);
/*     */ 
/*     */ 
/*     */     
/* 166 */     update = new ComponentUpdate();
/* 167 */     update.type = ComponentUpdateType.PlayerSkin;
/*     */     
/* 169 */     PlayerSkinComponent playerSkinComponent = (PlayerSkinComponent)store.getComponent(viewerRef, PlayerSkinComponent.getComponentType());
/* 170 */     update.skin = (playerSkinComponent != null) ? playerSkinComponent.getPlayerSkin() : null;
/* 171 */     list.add(update);
/*     */ 
/*     */ 
/*     */     
/* 175 */     Inventory inventory = playerComponent.getInventory();
/* 176 */     ComponentUpdate componentUpdate1 = new ComponentUpdate();
/* 177 */     componentUpdate1.type = ComponentUpdateType.Equipment;
/* 178 */     componentUpdate1.equipment = new Equipment();
/*     */     
/* 180 */     ItemContainer armor = inventory.getArmor();
/* 181 */     componentUpdate1.equipment.armorIds = new String[armor.getCapacity()];
/* 182 */     Arrays.fill((Object[])componentUpdate1.equipment.armorIds, "");
/* 183 */     armor.forEachWithMeta((slot, itemStack, armorIds) -> armorIds[slot] = itemStack.getItemId(), componentUpdate1.equipment.armorIds);
/*     */ 
/*     */ 
/*     */     
/* 187 */     PlayerSettings playerSettingsComponent = (PlayerSettings)store.getComponent(viewerRef, PlayerSettings.getComponentType());
/* 188 */     if (playerSettingsComponent != null) {
/* 189 */       PlayerConfig.ArmorVisibilityOption armorVisibilityOption = ((EntityStore)store.getExternalData()).getWorld().getGameplayConfig().getPlayerConfig().getArmorVisibilityOption();
/* 190 */       if (armorVisibilityOption.canHideHelmet() && playerSettingsComponent.hideHelmet()) {
/* 191 */         componentUpdate1.equipment.armorIds[ItemArmorSlot.Head.ordinal()] = "";
/*     */       }
/* 193 */       if (armorVisibilityOption.canHideCuirass() && playerSettingsComponent.hideCuirass()) {
/* 194 */         componentUpdate1.equipment.armorIds[ItemArmorSlot.Chest.ordinal()] = "";
/*     */       }
/* 196 */       if (armorVisibilityOption.canHideGauntlets() && playerSettingsComponent.hideGauntlets()) {
/* 197 */         componentUpdate1.equipment.armorIds[ItemArmorSlot.Hands.ordinal()] = "";
/*     */       }
/* 199 */       if (armorVisibilityOption.canHidePants() && playerSettingsComponent.hidePants()) {
/* 200 */         componentUpdate1.equipment.armorIds[ItemArmorSlot.Legs.ordinal()] = "";
/*     */       }
/*     */     } 
/*     */     
/* 204 */     ItemStack itemInHand = inventory.getItemInHand();
/* 205 */     componentUpdate1.equipment.rightHandItemId = (itemInHand != null) ? itemInHand.getItemId() : "Empty";
/*     */     
/* 207 */     ItemStack utilityItem = inventory.getUtilityItem();
/* 208 */     componentUpdate1.equipment.leftHandItemId = (utilityItem != null) ? utilityItem.getItemId() : "Empty";
/* 209 */     list.add(componentUpdate1);
/*     */ 
/*     */     
/* 212 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(viewerRef, TransformComponent.getComponentType());
/* 213 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(viewerRef, HeadRotation.getComponentType());
/* 214 */     if (transformComponent != null && headRotationComponent != null) {
/* 215 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 216 */       componentUpdate.type = ComponentUpdateType.Transform;
/* 217 */       componentUpdate.transform = new ModelTransform();
/* 218 */       componentUpdate.transform.position = PositionUtil.toPositionPacket(transformComponent.getPosition());
/* 219 */       componentUpdate.transform.bodyOrientation = PositionUtil.toDirectionPacket(transformComponent.getRotation());
/* 220 */       componentUpdate.transform.lookOrientation = PositionUtil.toDirectionPacket(headRotationComponent.getRotation());
/* 221 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 224 */     EffectControllerComponent effectControllerComponent = (EffectControllerComponent)store.getComponent(viewerRef, EffectControllerComponent.getComponentType());
/* 225 */     if (effectControllerComponent != null) {
/* 226 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 227 */       componentUpdate.type = ComponentUpdateType.EntityEffects;
/* 228 */       componentUpdate.entityEffectUpdates = effectControllerComponent.createInitUpdates();
/* 229 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 232 */     EntityStatMap statMapComponent = (EntityStatMap)store.getComponent(viewerRef, EntityStatMap.getComponentType());
/* 233 */     if (statMapComponent != null) {
/* 234 */       ComponentUpdate componentUpdate = new ComponentUpdate();
/* 235 */       componentUpdate.type = ComponentUpdateType.EntityStats;
/* 236 */       componentUpdate.entityStatUpdates = (Map)statMapComponent.createInitUpdate(true);
/* 237 */       list.add(componentUpdate);
/*     */     } 
/*     */     
/* 240 */     entityUpdate.updates = (ComponentUpdate[])list.toArray(x$0 -> new ComponentUpdate[x$0]);
/* 241 */     entityViewerComponent.packetReceiver.writeNoCache((Packet)new EntityUpdates(null, new EntityUpdate[] { entityUpdate }));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerSystems$PlayerSpawnedSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */