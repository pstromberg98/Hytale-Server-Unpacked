/*     */ package com.hypixel.hytale.server.spawning.spawnmarkers;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.data.unknown.UnknownComponents;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.RootDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
/*     */ import com.hypixel.hytale.server.core.modules.entity.AllLegacyEntityTypesQuery;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.WorldGenId;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bson.BsonDocument;
/*     */ import org.bson.BsonValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated(forRemoval = true)
/*     */ public class LegacyEntityMigration
/*     */   extends EntityModule.MigrationSystem
/*     */ {
/*     */   @Nonnull
/*  65 */   private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  71 */   private final ComponentType<EntityStore, Nameplate> nameplateComponentType = Nameplate.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  77 */   private final ComponentType<EntityStore, UUIDComponent> uuidComponentType = UUIDComponent.getComponentType();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  82 */   private final ComponentType<EntityStore, UnknownComponents<EntityStore>> unknownComponentsComponentType = EntityStore.REGISTRY
/*  83 */     .getUnknownComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  89 */   private final Query<EntityStore> query = (Query<EntityStore>)Query.and(new Query[] { (Query)this.unknownComponentsComponentType, (Query)Query.not((Query)AllLegacyEntityTypesQuery.INSTANCE) });
/*     */ 
/*     */   
/*     */   public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*  93 */     UnknownComponents<EntityStore> unknownComponentsComponent = (UnknownComponents<EntityStore>)holder.getComponent(this.unknownComponentsComponentType);
/*  94 */     assert unknownComponentsComponent != null;
/*     */     
/*  96 */     Map<String, BsonDocument> unknownComponents = unknownComponentsComponent.getUnknownComponents();
/*     */     
/*  98 */     BsonDocument spawnMarker = unknownComponents.remove("SpawnMarker");
/*  99 */     if (spawnMarker == null)
/*     */       return; 
/* 101 */     Archetype<EntityStore> archetype = holder.getArchetype();
/* 102 */     assert archetype != null;
/*     */     
/* 104 */     if (!archetype.contains(this.persistentModelComponentType)) {
/* 105 */       Model.ModelReference modelReference = Entity.MODEL.get(spawnMarker).get();
/* 106 */       holder.addComponent(this.persistentModelComponentType, (Component)new PersistentModel(modelReference));
/*     */     } 
/*     */     
/* 109 */     if (!archetype.contains(this.nameplateComponentType)) {
/* 110 */       holder.addComponent(this.nameplateComponentType, (Component)new Nameplate(Entity.DISPLAY_NAME.get(spawnMarker).get()));
/*     */     }
/*     */     
/* 113 */     if (!archetype.contains(this.uuidComponentType)) {
/* 114 */       holder.addComponent(this.uuidComponentType, (Component)new UUIDComponent(Entity.UUID.get(spawnMarker).get()));
/*     */     }
/*     */     
/* 117 */     holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
/*     */     
/* 119 */     int worldGenId = ((Integer)Codec.INTEGER.decode(spawnMarker.get("WorldgenId"))).intValue();
/* 120 */     if (worldGenId != 0) holder.addComponent(WorldGenId.getComponentType(), (Component)new WorldGenId(worldGenId));
/*     */     
/* 122 */     SpawnMarkerEntity marker = (SpawnMarkerEntity)SpawnMarkerEntity.CODEC.decode((BsonValue)spawnMarker, new ExtraInfo(5));
/* 123 */     holder.addComponent(SpawnMarkerEntity.getComponentType(), marker);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 133 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 139 */     return RootDependency.firstSet();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\spawnmarkers\SpawnMarkerSystems$LegacyEntityMigration.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */