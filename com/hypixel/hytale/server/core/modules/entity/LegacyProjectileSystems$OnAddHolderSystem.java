/*     */ package com.hypixel.hytale.server.core.modules.entity;
/*     */ 
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.HolderSystem;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*     */ import com.hypixel.hytale.server.core.asset.type.projectile.config.Projectile;
/*     */ import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OnAddHolderSystem
/*     */   extends HolderSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*  80 */   private static final ComponentType<EntityStore, ProjectileComponent> PROJECTILE_COMPONENT_TYPE = ProjectileComponent.getComponentType();
/*     */   
/*     */   public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*     */     BoundingBox boundingBox;
/*  84 */     if (!holder.getArchetype().contains(NetworkId.getComponentType())) {
/*  85 */       holder.addComponent(NetworkId.getComponentType(), (Component)new NetworkId(((EntityStore)store.getExternalData()).takeNextNetworkId()));
/*     */     }
/*     */     
/*  88 */     ProjectileComponent projectileComponent = (ProjectileComponent)holder.getComponent(PROJECTILE_COMPONENT_TYPE);
/*  89 */     assert projectileComponent != null;
/*     */     
/*  91 */     projectileComponent.initialize();
/*     */ 
/*     */     
/*  94 */     ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(projectileComponent.getAppearance());
/*  95 */     if (modelAsset != null) {
/*  96 */       Model model = Model.createUnitScaleModel(modelAsset);
/*  97 */       holder.putComponent(ModelComponent.getComponentType(), (Component)new ModelComponent(model));
/*  98 */       holder.putComponent(PersistentModel.getComponentType(), (Component)new PersistentModel(model.toReference()));
/*  99 */       boundingBox = new BoundingBox(model.getBoundingBox());
/*     */     } else {
/* 101 */       Projectile projectileAsset = projectileComponent.getProjectile();
/* 102 */       if (projectileAsset != null) {
/* 103 */         boundingBox = new BoundingBox(Box.horizontallyCentered(projectileAsset.getRadius(), projectileAsset.getHeight(), projectileAsset.getRadius()));
/*     */       } else {
/* 105 */         boundingBox = new BoundingBox(Box.horizontallyCentered(0.25D, 0.25D, 0.25D));
/*     */       } 
/*     */     } 
/* 108 */     holder.putComponent(BoundingBox.getComponentType(), (Component)boundingBox);
/*     */     
/* 110 */     projectileComponent.initializePhysics(boundingBox);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */   
/*     */   public Query<EntityStore> getQuery() {
/* 119 */     return (Query)PROJECTILE_COMPONENT_TYPE;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\LegacyProjectileSystems$OnAddHolderSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */