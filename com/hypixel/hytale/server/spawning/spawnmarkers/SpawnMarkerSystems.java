/*     */ package com.hypixel.hytale.server.spawning.spawnmarkers;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.data.unknown.UnknownComponents;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.protocol.AnimationSlot;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
/*     */ import com.hypixel.hytale.server.core.entity.reference.InvalidatablePersistentRef;
/*     */ import com.hypixel.hytale.server.core.entity.reference.PersistentRefCount;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.FromWorldGen;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.WorldGenId;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.flock.StoredFlock;
/*     */ import com.hypixel.hytale.server.npc.components.SpawnMarkerReference;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
/*     */ import it.unimi.dsi.fastutil.Pair;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bson.BsonDocument;
/*     */ 
/*     */ public class SpawnMarkerSystems {
/*     */   @Nonnull
/*  52 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public static class LegacyEntityMigration
/*     */     extends EntityModule.MigrationSystem
/*     */   {
/*     */     @Nonnull
/*  65 */     private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  71 */     private final ComponentType<EntityStore, Nameplate> nameplateComponentType = Nameplate.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  77 */     private final ComponentType<EntityStore, UUIDComponent> uuidComponentType = UUIDComponent.getComponentType();
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  82 */     private final ComponentType<EntityStore, UnknownComponents<EntityStore>> unknownComponentsComponentType = EntityStore.REGISTRY
/*  83 */       .getUnknownComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  89 */     private final Query<EntityStore> query = (Query<EntityStore>)Query.and(new Query[] { (Query)this.unknownComponentsComponentType, (Query)Query.not((Query)AllLegacyEntityTypesQuery.INSTANCE) });
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/*  93 */       UnknownComponents<EntityStore> unknownComponentsComponent = (UnknownComponents<EntityStore>)holder.getComponent(this.unknownComponentsComponentType);
/*  94 */       assert unknownComponentsComponent != null;
/*     */       
/*  96 */       Map<String, BsonDocument> unknownComponents = unknownComponentsComponent.getUnknownComponents();
/*     */       
/*  98 */       BsonDocument spawnMarker = unknownComponents.remove("SpawnMarker");
/*  99 */       if (spawnMarker == null)
/*     */         return; 
/* 101 */       Archetype<EntityStore> archetype = holder.getArchetype();
/* 102 */       assert archetype != null;
/*     */       
/* 104 */       if (!archetype.contains(this.persistentModelComponentType)) {
/* 105 */         Model.ModelReference modelReference = Entity.MODEL.get(spawnMarker).get();
/* 106 */         holder.addComponent(this.persistentModelComponentType, (Component)new PersistentModel(modelReference));
/*     */       } 
/*     */       
/* 109 */       if (!archetype.contains(this.nameplateComponentType)) {
/* 110 */         holder.addComponent(this.nameplateComponentType, (Component)new Nameplate(Entity.DISPLAY_NAME.get(spawnMarker).get()));
/*     */       }
/*     */       
/* 113 */       if (!archetype.contains(this.uuidComponentType)) {
/* 114 */         holder.addComponent(this.uuidComponentType, (Component)new UUIDComponent(Entity.UUID.get(spawnMarker).get()));
/*     */       }
/*     */       
/* 117 */       holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
/*     */       
/* 119 */       int worldGenId = ((Integer)Codec.INTEGER.decode(spawnMarker.get("WorldgenId"))).intValue();
/* 120 */       if (worldGenId != 0) holder.addComponent(WorldGenId.getComponentType(), (Component)new WorldGenId(worldGenId));
/*     */       
/* 122 */       SpawnMarkerEntity marker = (SpawnMarkerEntity)SpawnMarkerEntity.CODEC.decode((BsonValue)spawnMarker, new ExtraInfo(5));
/* 123 */       holder.addComponent(SpawnMarkerEntity.getComponentType(), marker);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 133 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 139 */       return RootDependency.firstSet();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class EnsureNetworkSendable
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/* 153 */     private final Query<EntityStore> query = (Query)SpawnMarkerEntity.getComponentType();
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/* 157 */       Archetype<EntityStore> archetype = holder.getArchetype();
/* 158 */       assert archetype != null;
/*     */       
/* 160 */       ComponentType<EntityStore, NetworkId> networkIdComponentType = NetworkId.getComponentType();
/*     */       
/* 162 */       if (!archetype.contains(networkIdComponentType)) {
/* 163 */         holder.addComponent(networkIdComponentType, (Component)new NetworkId(((EntityStore)store.getExternalData()).takeNextNetworkId()));
/*     */       }
/* 165 */       holder.ensureComponent(Intangible.getComponentType());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 175 */       return this.query;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CacheMarker
/*     */     extends RefSystem<EntityStore>
/*     */   {
/*     */     private final ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CacheMarker(@Nonnull ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerComponentType) {
/* 195 */       this.spawnMarkerComponentType = spawnMarkerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 200 */       return (Query)this.spawnMarkerComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 205 */       SpawnMarkerEntity spawnMarkerEntityComponent = (SpawnMarkerEntity)store.getComponent(ref, this.spawnMarkerComponentType);
/* 206 */       assert spawnMarkerEntityComponent != null;
/*     */       
/* 208 */       SpawnMarker spawnMarker = (SpawnMarker)SpawnMarker.getAssetMap().getAsset(spawnMarkerEntityComponent.getSpawnMarkerId());
/* 209 */       if (spawnMarker == null) {
/*     */         
/* 211 */         SpawnMarkerSystems.LOGGER.at(Level.SEVERE).log("Marker %s removed due to missing spawn marker type: %s", ref, spawnMarkerEntityComponent.getSpawnMarkerId());
/* 212 */         commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/*     */         return;
/*     */       } 
/* 215 */       spawnMarkerEntityComponent.setCachedMarker(spawnMarker);
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
/*     */   public static class EntityAdded
/*     */     extends RefSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, UUIDComponent> uuidComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Set<Dependency<EntityStore>> dependencies;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EntityAdded(@Nonnull ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType) {
/* 258 */       this.spawnMarkerEntityComponentType = spawnMarkerEntityComponentType;
/* 259 */       this.uuidComponentType = UUIDComponent.getComponentType();
/* 260 */       this.dependencies = Set.of(new SystemDependency(Order.AFTER, SpawnMarkerSystems.CacheMarker.class));
/* 261 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)spawnMarkerEntityComponentType, (Query)this.uuidComponentType });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 267 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 273 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 278 */       SpawnMarkerEntity spawnMarkerEntityComponent = (SpawnMarkerEntity)store.getComponent(ref, this.spawnMarkerEntityComponentType);
/* 279 */       assert spawnMarkerEntityComponent != null;
/*     */       
/* 281 */       HytaleLogger.Api context = SpawnMarkerSystems.LOGGER.at(Level.FINE);
/* 282 */       if (context.isEnabled()) {
/* 283 */         context.log("Loaded marker %s", store.getComponent(ref, this.uuidComponentType));
/*     */       }
/*     */       
/* 286 */       if (spawnMarkerEntityComponent.getStoredFlock() != null) {
/* 287 */         spawnMarkerEntityComponent.setTempStorageList((List<Pair<Ref<EntityStore>, NPCEntity>>)new ObjectArrayList());
/*     */       }
/*     */       
/* 290 */       if (spawnMarkerEntityComponent.getSpawnCount() != 0) {
/* 291 */         spawnMarkerEntityComponent.refreshTimeout();
/*     */       }
/* 293 */       commandBuffer.ensureComponent(ref, PrefabCopyableComponent.getComponentType());
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
/*     */   public static class EntityAddedFromExternal
/*     */     extends RefSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Set<Dependency<EntityStore>> dependencies;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public EntityAddedFromExternal(@Nonnull ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType) {
/* 330 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)spawnMarkerEntityComponentType, (Query)Query.or(new Query[] { (Query)FromPrefab.getComponentType(), (Query)FromWorldGen.getComponentType() }) });
/* 331 */       this.spawnMarkerEntityComponentType = spawnMarkerEntityComponentType;
/* 332 */       this.dependencies = Set.of(new SystemDependency(Order.BEFORE, SpawnMarkerSystems.EntityAdded.class), new SystemDependency(Order.AFTER, SpawnMarkerSystems.CacheMarker.class));
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 337 */       SpawnMarkerEntity spawnMarkerEntityComponent = (SpawnMarkerEntity)store.getComponent(ref, this.spawnMarkerEntityComponentType);
/* 338 */       assert spawnMarkerEntityComponent != null;
/*     */       
/* 340 */       spawnMarkerEntityComponent.setSpawnCount(0);
/* 341 */       spawnMarkerEntityComponent.setRespawnCounter(0.0D);
/* 342 */       spawnMarkerEntityComponent.setSpawnAfter(null);
/* 343 */       spawnMarkerEntityComponent.setGameTimeRespawn(null);
/*     */       
/* 345 */       if (spawnMarkerEntityComponent.getCachedMarker().getDeactivationDistance() > 0.0D) {
/* 346 */         spawnMarkerEntityComponent.setStoredFlock(new StoredFlock());
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 357 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 363 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 369 */       return EntityModule.get().getPreClearMarkersGroup();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class AddedFromWorldGen
/*     */     extends HolderSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/* 382 */     private final ComponentType<EntityStore, SpawnMarkerEntity> componentType = SpawnMarkerEntity.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 388 */     private final ComponentType<EntityStore, WorldGenId> worldGenIdComponentType = WorldGenId.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 394 */     private final ComponentType<EntityStore, FromWorldGen> fromWorldGenComponentType = FromWorldGen.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 400 */     private final Query<EntityStore> query = (Query<EntityStore>)Query.and(new Query[] { (Query)this.componentType, (Query)this.fromWorldGenComponentType });
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 405 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public SystemGroup<EntityStore> getGroup() {
/* 411 */       return EntityModule.get().getPreClearMarkersGroup();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/* 418 */       FromWorldGen fromWorldGenComponent = (FromWorldGen)holder.getComponent(this.fromWorldGenComponentType);
/* 419 */       assert fromWorldGenComponent != null;
/* 420 */       holder.putComponent(this.worldGenIdComponentType, (Component)new WorldGenId(fromWorldGenComponent.getWorldGenId()));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Ticking
/*     */     extends EntityTickingSystem<EntityStore>
/*     */   {
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     private final ComponentType<EntityStore, NPCEntity> npcComponentType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ComponentType<EntityStore, PersistentRefCount> referenceIdComponentType;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 455 */     private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 461 */     private final ComponentType<EntityStore, HeadRotation> headRotationComponentType = HeadRotation.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 467 */     private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Set<Dependency<EntityStore>> dependencies;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Ticking(@Nonnull ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType, @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent) {
/* 495 */       this.spawnMarkerEntityComponentType = spawnMarkerEntityComponentType;
/* 496 */       this.npcComponentType = NPCEntity.getComponentType();
/* 497 */       this.referenceIdComponentType = PersistentRefCount.getComponentType();
/*     */ 
/*     */       
/* 500 */       this.playerSpatialComponent = playerSpatialComponent;
/* 501 */       this.dependencies = Set.of(new SystemDependency(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
/*     */       
/* 503 */       this.query = (Query<EntityStore>)Archetype.of(new ComponentType[] { spawnMarkerEntityComponentType, this.transformComponentType });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 509 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 515 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 520 */       return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 525 */       SpawnMarkerEntity spawnMarkerEntityComponent = (SpawnMarkerEntity)archetypeChunk.getComponent(index, this.spawnMarkerEntityComponentType);
/* 526 */       assert spawnMarkerEntityComponent != null;
/*     */       
/* 528 */       TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/* 529 */       assert transformComponent != null;
/*     */       
/* 531 */       World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */       
/* 533 */       SpawnMarker cachedMarker = spawnMarkerEntityComponent.getCachedMarker();
/* 534 */       if (spawnMarkerEntityComponent.getSpawnCount() > 0) {
/*     */         
/* 536 */         StoredFlock storedFlock = spawnMarkerEntityComponent.getStoredFlock();
/* 537 */         if (storedFlock != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 542 */           SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(this.playerSpatialComponent);
/* 543 */           ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 544 */           spatialResource.getSpatialStructure().collect(transformComponent.getPosition(), cachedMarker.getDeactivationDistance(), (List)results);
/*     */           
/* 546 */           boolean hasPlayersInRange = !results.isEmpty();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 559 */           if (hasPlayersInRange) {
/* 560 */             if (storedFlock.hasStoredNPCs())
/*     */             {
/* 562 */               commandBuffer.run(_store -> {
/*     */                     ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();
/*     */                     
/*     */                     storedFlock.restoreNPCs((List)tempStorageList, _store);
/*     */                     
/*     */                     spawnMarkerEntityComponent.setSpawnCount(tempStorageList.size());
/*     */                     
/*     */                     Vector3d position = spawnMarkerEntityComponent.getSpawnPosition();
/*     */                     
/*     */                     Vector3f rotation = transformComponent.getRotation();
/*     */                     
/*     */                     InvalidatablePersistentRef[] npcReferences = new InvalidatablePersistentRef[tempStorageList.size()];
/*     */                     
/*     */                     int i = 0;
/*     */                     
/*     */                     int bound = tempStorageList.size();
/*     */                     
/*     */                     while (i < bound) {
/*     */                       Ref<EntityStore> ref = (Ref<EntityStore>)tempStorageList.get(i);
/*     */                       NPCEntity npcComponent = (NPCEntity)_store.getComponent(ref, this.npcComponentType);
/*     */                       assert npcComponent != null;
/*     */                       TransformComponent npcTransform = (TransformComponent)_store.getComponent(ref, this.transformComponentType);
/*     */                       assert npcTransform != null;
/*     */                       HeadRotation npcHeadRotation = (HeadRotation)_store.getComponent(ref, this.headRotationComponentType);
/*     */                       assert npcHeadRotation != null;
/*     */                       InvalidatablePersistentRef reference = new InvalidatablePersistentRef();
/*     */                       reference.setEntity(ref, (ComponentAccessor)_store);
/*     */                       npcReferences[i] = reference;
/*     */                       npcTransform.getPosition().assign(position);
/*     */                       npcTransform.getRotation().assign(rotation);
/*     */                       npcHeadRotation.setRotation(rotation);
/*     */                       npcComponent.playAnimation(ref, AnimationSlot.Status, null, (ComponentAccessor)commandBuffer);
/*     */                       i++;
/*     */                     } 
/*     */                     spawnMarkerEntityComponent.setNpcReferences(npcReferences);
/*     */                     spawnMarkerEntityComponent.setDespawnStarted(false);
/*     */                     spawnMarkerEntityComponent.setTimeToDeactivation(cachedMarker.getDeactivationTime());
/*     */                   });
/*     */             }
/*     */           } else {
/* 602 */             if (!storedFlock.hasStoredNPCs() && spawnMarkerEntityComponent.tickTimeToDeactivation(dt)) {
/*     */               
/* 604 */               InvalidatablePersistentRef[] npcReferences = spawnMarkerEntityComponent.getNpcReferences();
/* 605 */               if (npcReferences == null)
/*     */                 return; 
/* 607 */               if (!spawnMarkerEntityComponent.isDespawnStarted()) {
/*     */ 
/*     */                 
/* 610 */                 List<Pair<Ref<EntityStore>, NPCEntity>> tempStorageList = spawnMarkerEntityComponent.getTempStorageList();
/* 611 */                 for (InvalidatablePersistentRef reference : npcReferences) {
/*     */ 
/*     */                   
/* 614 */                   Ref<EntityStore> npcRef = reference.getEntity((ComponentAccessor)commandBuffer);
/* 615 */                   if (npcRef != null) {
/*     */                     
/* 617 */                     NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(npcRef, this.npcComponentType);
/* 618 */                     assert npcComponent != null;
/*     */                     
/* 620 */                     tempStorageList.add(Pair.of(npcRef, npcComponent));
/*     */                     
/* 622 */                     boolean isDead = commandBuffer.getArchetype(npcRef).contains(DeathComponent.getComponentType());
/*     */ 
/*     */                     
/* 625 */                     if (isDead || npcComponent.getRole().getStateSupport().isInBusyState()) {
/* 626 */                       spawnMarkerEntityComponent.setTimeToDeactivation(cachedMarker.getDeactivationTime());
/* 627 */                       tempStorageList.clear();
/*     */                       
/*     */                       return;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/* 633 */                 for (int i = 0; i < tempStorageList.size(); i++) {
/* 634 */                   Pair<Ref<EntityStore>, NPCEntity> npcPair = tempStorageList.get(i);
/* 635 */                   Ref<EntityStore> npcRef = (Ref<EntityStore>)npcPair.first();
/* 636 */                   NPCEntity npcComponent = (NPCEntity)npcPair.second();
/*     */                   
/* 638 */                   ModelComponent modelComponent = (ModelComponent)commandBuffer.getComponent(npcRef, this.modelComponentType);
/* 639 */                   if (modelComponent != null && modelComponent.getModel().getAnimationSetMap().containsKey("Despawn")) {
/* 640 */                     Role role = npcComponent.getRole();
/* 641 */                     assert role != null;
/*     */                     
/* 643 */                     double despawnAnimationTime = role.getDespawnAnimationTime();
/*     */ 
/*     */                     
/* 646 */                     if (despawnAnimationTime > spawnMarkerEntityComponent.getTimeToDeactivation()) {
/* 647 */                       spawnMarkerEntityComponent.setTimeToDeactivation(despawnAnimationTime);
/*     */                     }
/* 649 */                     npcComponent.playAnimation(npcRef, AnimationSlot.Status, "Despawn", (ComponentAccessor)commandBuffer);
/*     */                   } 
/*     */                 } 
/* 652 */                 spawnMarkerEntityComponent.setDespawnStarted(true);
/* 653 */                 tempStorageList.clear();
/*     */ 
/*     */                 
/*     */                 return;
/*     */               } 
/*     */               
/* 659 */               PersistentRefCount refId = (PersistentRefCount)archetypeChunk.getComponent(index, this.referenceIdComponentType);
/* 660 */               if (refId != null) {
/* 661 */                 refId.increment();
/*     */               }
/*     */               
/* 664 */               Ref<EntityStore> ref1 = archetypeChunk.getReferenceTo(index);
/* 665 */               commandBuffer.run(_store -> {
/*     */                     ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();
/*     */                     
/*     */                     for (InvalidatablePersistentRef reference : npcReferences) {
/*     */                       Ref<EntityStore> npcRef = reference.getEntity((ComponentAccessor)_store);
/*     */                       
/*     */                       if (npcRef == null || !npcRef.isValid()) {
/*     */                         ((HytaleLogger.Api)SpawnMarkerSystems.LOGGER.atWarning()).log("Connection with NPC from marker at %s lost due to being invalid/already unloaded", transformComponent.getPosition());
/*     */                       } else {
/*     */                         SpawnMarkerReference spawnMarkerReference = (SpawnMarkerReference)_store.ensureAndGetComponent(npcRef, SpawnMarkerReference.getComponentType());
/*     */                         
/*     */                         spawnMarkerReference.getReference().setEntity(ref, (ComponentAccessor)store);
/*     */                         
/*     */                         tempStorageList.add(npcRef);
/*     */                       } 
/*     */                     } 
/*     */                     storedFlock.storeNPCs((List)tempStorageList, _store);
/*     */                     spawnMarkerEntityComponent.setNpcReferences(null);
/*     */                   });
/*     */             } 
/*     */             return;
/*     */           } 
/*     */         } 
/* 688 */         if (spawnMarkerEntityComponent.tickSpawnLostTimeout(dt)) {
/*     */ 
/*     */ 
/*     */           
/* 692 */           PersistentRefCount refId = (PersistentRefCount)archetypeChunk.getComponent(index, this.referenceIdComponentType);
/* 693 */           if (refId != null) {
/* 694 */             refId.increment();
/* 695 */             SpawnMarkerSystems.LOGGER.at(Level.FINE).log("Marker lost spawned NPC and changed reference ID to %s", refId.get());
/*     */           } 
/* 697 */           Ref<EntityStore> ref1 = archetypeChunk.getReferenceTo(index);
/* 698 */           commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 703 */       if (!world.getWorldConfig().isSpawnMarkersEnabled() || cachedMarker.isManualTrigger() || (spawnMarkerEntityComponent.getSuppressedBy() != null && 
/* 704 */         !spawnMarkerEntityComponent.getSuppressedBy().isEmpty())) {
/*     */         return;
/*     */       }
/*     */       
/* 708 */       Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/* 709 */       WorldTimeResource worldTimeResource = (WorldTimeResource)commandBuffer.getResource(WorldTimeResource.getResourceType());
/*     */ 
/*     */       
/* 712 */       if (cachedMarker.isRealtimeRespawn()) {
/* 713 */         if (spawnMarkerEntityComponent.tickRespawnTimer(dt)) {
/* 714 */           commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */         }
/* 716 */       } else if (spawnMarkerEntityComponent.getSpawnAfter() == null || worldTimeResource.getGameTime().isAfter(spawnMarkerEntityComponent.getSpawnAfter())) {
/* 717 */         commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\spawnmarkers\SpawnMarkerSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */