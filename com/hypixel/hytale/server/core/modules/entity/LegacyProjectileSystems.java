/*     */ package com.hypixel.hytale.server.core.modules.entity;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*     */ import com.hypixel.hytale.server.core.asset.type.projectile.config.Projectile;
/*     */ import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class LegacyProjectileSystems {
/*     */   @Nonnull
/*  33 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class OnAddRefSystem
/*     */     extends RefSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  45 */     private static final ComponentType<EntityStore, ProjectileComponent> PROJECTILE_COMPONENT_TYPE = ProjectileComponent.getComponentType();
/*     */ 
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/*  50 */       return (Query)PROJECTILE_COMPONENT_TYPE;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  55 */       ProjectileComponent projectileComponent = (ProjectileComponent)commandBuffer.getComponent(ref, PROJECTILE_COMPONENT_TYPE);
/*  56 */       assert projectileComponent != null;
/*     */ 
/*     */       
/*  59 */       if (projectileComponent.getProjectile() == null) {
/*  60 */         LegacyProjectileSystems.LOGGER.at(Level.WARNING).log("Removing projectile entity %s as it failed to initialize correctly!", ref);
/*  61 */         commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class OnAddHolderSystem
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*  80 */     private static final ComponentType<EntityStore, ProjectileComponent> PROJECTILE_COMPONENT_TYPE = ProjectileComponent.getComponentType();
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*     */       BoundingBox boundingBox;
/*  84 */       if (!holder.getArchetype().contains(NetworkId.getComponentType())) {
/*  85 */         holder.addComponent(NetworkId.getComponentType(), (Component)new NetworkId(((EntityStore)store.getExternalData()).takeNextNetworkId()));
/*     */       }
/*     */       
/*  88 */       ProjectileComponent projectileComponent = (ProjectileComponent)holder.getComponent(PROJECTILE_COMPONENT_TYPE);
/*  89 */       assert projectileComponent != null;
/*     */       
/*  91 */       projectileComponent.initialize();
/*     */ 
/*     */       
/*  94 */       ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(projectileComponent.getAppearance());
/*  95 */       if (modelAsset != null) {
/*  96 */         Model model = Model.createUnitScaleModel(modelAsset);
/*  97 */         holder.putComponent(ModelComponent.getComponentType(), (Component)new ModelComponent(model));
/*  98 */         holder.putComponent(PersistentModel.getComponentType(), (Component)new PersistentModel(model.toReference()));
/*  99 */         boundingBox = new BoundingBox(model.getBoundingBox());
/*     */       } else {
/* 101 */         Projectile projectileAsset = projectileComponent.getProjectile();
/* 102 */         if (projectileAsset != null) {
/* 103 */           boundingBox = new BoundingBox(Box.horizontallyCentered(projectileAsset.getRadius(), projectileAsset.getHeight(), projectileAsset.getRadius()));
/*     */         } else {
/* 105 */           boundingBox = new BoundingBox(Box.horizontallyCentered(0.25D, 0.25D, 0.25D));
/*     */         } 
/*     */       } 
/* 108 */       holder.putComponent(BoundingBox.getComponentType(), (Component)boundingBox);
/*     */       
/* 110 */       projectileComponent.initializePhysics(boundingBox);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 119 */       return (Query)PROJECTILE_COMPONENT_TYPE;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class TickingSystem
/*     */     extends EntityTickingSystem<EntityStore>
/*     */     implements DisableProcessingAssert
/*     */   {
/*     */     @Nonnull
/* 132 */     private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, TransformComponent> transformComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, Velocity> velocityComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, ProjectileComponent> projectileComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Archetype<EntityStore> archetype;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public TickingSystem(@Nonnull ComponentType<EntityStore, ProjectileComponent> projectileComponentType, @Nonnull ComponentType<EntityStore, TransformComponent> transformComponentType, @Nonnull ComponentType<EntityStore, Velocity> velocityComponentType, @Nonnull ComponentType<EntityStore, BoundingBox> boundingBoxComponentType) {
/* 176 */       this.projectileComponentType = projectileComponentType;
/* 177 */       this.velocityComponentType = velocityComponentType;
/* 178 */       this.boundingBoxComponentType = boundingBoxComponentType;
/* 179 */       this.transformComponentType = transformComponentType;
/* 180 */       this.archetype = Archetype.of(new ComponentType[] { projectileComponentType, transformComponentType, velocityComponentType, boundingBoxComponentType });
/*     */     }
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 185 */       return (Query<EntityStore>)this.archetype;
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 190 */       TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/* 191 */       assert transformComponent != null;
/*     */       
/* 193 */       ProjectileComponent projectileComponent = (ProjectileComponent)archetypeChunk.getComponent(index, this.projectileComponentType);
/* 194 */       assert projectileComponent != null;
/*     */       
/* 196 */       Velocity velocityComponent = (Velocity)archetypeChunk.getComponent(index, this.velocityComponentType);
/* 197 */       assert velocityComponent != null;
/*     */ 
/*     */ 
/*     */       
/* 201 */       BoundingBox boundingBox = (BoundingBox)archetypeChunk.getComponent(index, this.boundingBoxComponentType);
/*     */       
/* 203 */       Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */       
/*     */       try {
/* 206 */         if (projectileComponent.consumeDeadTimer(dt)) {
/* 207 */           projectileComponent.onProjectileDeath(ref, transformComponent.getPosition(), commandBuffer);
/* 208 */           commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/*     */           
/*     */           return;
/*     */         } 
/* 212 */         World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/* 213 */         projectileComponent.getSimplePhysicsProvider().tick(dt, velocityComponent, world, transformComponent, ref, (ComponentAccessor)commandBuffer);
/* 214 */       } catch (Throwable throwable) {
/* 215 */         ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Exception while ticking entity! Removing!! %s", projectileComponent);
/* 216 */         commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\LegacyProjectileSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */