/*     */ package com.hypixel.hytale.server.spawning.spawnmarkers;
/*     */ 
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.OrderPriority;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.protocol.AnimationSlot;
/*     */ import com.hypixel.hytale.server.core.entity.reference.InvalidatablePersistentRef;
/*     */ import com.hypixel.hytale.server.core.entity.reference.PersistentRefCount;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.flock.StoredFlock;
/*     */ import com.hypixel.hytale.server.npc.components.SpawnMarkerReference;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
/*     */ import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
/*     */ import it.unimi.dsi.fastutil.Pair;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Ticking
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType;
/*     */   @Nullable
/*     */   private final ComponentType<EntityStore, NPCEntity> npcComponentType;
/*     */   @Nonnull
/*     */   private final ComponentType<EntityStore, PersistentRefCount> referenceIdComponentType;
/*     */   @Nonnull
/* 455 */   private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 461 */   private final ComponentType<EntityStore, HeadRotation> headRotationComponentType = HeadRotation.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 467 */   private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Set<Dependency<EntityStore>> dependencies;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ticking(@Nonnull ComponentType<EntityStore, SpawnMarkerEntity> spawnMarkerEntityComponentType, @Nonnull ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent) {
/* 495 */     this.spawnMarkerEntityComponentType = spawnMarkerEntityComponentType;
/* 496 */     this.npcComponentType = NPCEntity.getComponentType();
/* 497 */     this.referenceIdComponentType = PersistentRefCount.getComponentType();
/*     */ 
/*     */     
/* 500 */     this.playerSpatialComponent = playerSpatialComponent;
/* 501 */     this.dependencies = Set.of(new SystemDependency(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
/*     */     
/* 503 */     this.query = (Query<EntityStore>)Archetype.of(new ComponentType[] { spawnMarkerEntityComponentType, this.transformComponentType });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<Dependency<EntityStore>> getDependencies() {
/* 509 */     return this.dependencies;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 515 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 520 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 525 */     SpawnMarkerEntity spawnMarkerEntityComponent = (SpawnMarkerEntity)archetypeChunk.getComponent(index, this.spawnMarkerEntityComponentType);
/* 526 */     assert spawnMarkerEntityComponent != null;
/*     */     
/* 528 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, this.transformComponentType);
/* 529 */     assert transformComponent != null;
/*     */     
/* 531 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 533 */     SpawnMarker cachedMarker = spawnMarkerEntityComponent.getCachedMarker();
/* 534 */     if (spawnMarkerEntityComponent.getSpawnCount() > 0) {
/*     */       
/* 536 */       StoredFlock storedFlock = spawnMarkerEntityComponent.getStoredFlock();
/* 537 */       if (storedFlock != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 542 */         SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(this.playerSpatialComponent);
/* 543 */         ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 544 */         spatialResource.getSpatialStructure().collect(transformComponent.getPosition(), cachedMarker.getDeactivationDistance(), (List)results);
/*     */         
/* 546 */         boolean hasPlayersInRange = !results.isEmpty();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 559 */         if (hasPlayersInRange) {
/* 560 */           if (storedFlock.hasStoredNPCs())
/*     */           {
/* 562 */             commandBuffer.run(_store -> {
/*     */                   ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();
/*     */                   
/*     */                   storedFlock.restoreNPCs((List)tempStorageList, _store);
/*     */                   
/*     */                   spawnMarkerEntityComponent.setSpawnCount(tempStorageList.size());
/*     */                   
/*     */                   Vector3d position = spawnMarkerEntityComponent.getSpawnPosition();
/*     */                   
/*     */                   Vector3f rotation = transformComponent.getRotation();
/*     */                   
/*     */                   InvalidatablePersistentRef[] npcReferences = new InvalidatablePersistentRef[tempStorageList.size()];
/*     */                   
/*     */                   int i = 0;
/*     */                   
/*     */                   int bound = tempStorageList.size();
/*     */                   
/*     */                   while (i < bound) {
/*     */                     Ref<EntityStore> ref = (Ref<EntityStore>)tempStorageList.get(i);
/*     */                     NPCEntity npcComponent = (NPCEntity)_store.getComponent(ref, this.npcComponentType);
/*     */                     assert npcComponent != null;
/*     */                     TransformComponent npcTransform = (TransformComponent)_store.getComponent(ref, this.transformComponentType);
/*     */                     assert npcTransform != null;
/*     */                     HeadRotation npcHeadRotation = (HeadRotation)_store.getComponent(ref, this.headRotationComponentType);
/*     */                     assert npcHeadRotation != null;
/*     */                     InvalidatablePersistentRef reference = new InvalidatablePersistentRef();
/*     */                     reference.setEntity(ref, (ComponentAccessor)_store);
/*     */                     npcReferences[i] = reference;
/*     */                     npcTransform.getPosition().assign(position);
/*     */                     npcTransform.getRotation().assign(rotation);
/*     */                     npcHeadRotation.setRotation(rotation);
/*     */                     npcComponent.playAnimation(ref, AnimationSlot.Status, null, (ComponentAccessor)commandBuffer);
/*     */                     i++;
/*     */                   } 
/*     */                   spawnMarkerEntityComponent.setNpcReferences(npcReferences);
/*     */                   spawnMarkerEntityComponent.setDespawnStarted(false);
/*     */                   spawnMarkerEntityComponent.setTimeToDeactivation(cachedMarker.getDeactivationTime());
/*     */                 });
/*     */           }
/*     */         } else {
/* 602 */           if (!storedFlock.hasStoredNPCs() && spawnMarkerEntityComponent.tickTimeToDeactivation(dt)) {
/*     */             
/* 604 */             InvalidatablePersistentRef[] npcReferences = spawnMarkerEntityComponent.getNpcReferences();
/* 605 */             if (npcReferences == null)
/*     */               return; 
/* 607 */             if (!spawnMarkerEntityComponent.isDespawnStarted()) {
/*     */ 
/*     */               
/* 610 */               List<Pair<Ref<EntityStore>, NPCEntity>> tempStorageList = spawnMarkerEntityComponent.getTempStorageList();
/* 611 */               for (InvalidatablePersistentRef reference : npcReferences) {
/*     */ 
/*     */                 
/* 614 */                 Ref<EntityStore> npcRef = reference.getEntity((ComponentAccessor)commandBuffer);
/* 615 */                 if (npcRef != null) {
/*     */                   
/* 617 */                   NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(npcRef, this.npcComponentType);
/* 618 */                   assert npcComponent != null;
/*     */                   
/* 620 */                   tempStorageList.add(Pair.of(npcRef, npcComponent));
/*     */                   
/* 622 */                   boolean isDead = commandBuffer.getArchetype(npcRef).contains(DeathComponent.getComponentType());
/*     */ 
/*     */                   
/* 625 */                   if (isDead || npcComponent.getRole().getStateSupport().isInBusyState()) {
/* 626 */                     spawnMarkerEntityComponent.setTimeToDeactivation(cachedMarker.getDeactivationTime());
/* 627 */                     tempStorageList.clear();
/*     */                     
/*     */                     return;
/*     */                   } 
/*     */                 } 
/*     */               } 
/* 633 */               for (int i = 0; i < tempStorageList.size(); i++) {
/* 634 */                 Pair<Ref<EntityStore>, NPCEntity> npcPair = tempStorageList.get(i);
/* 635 */                 Ref<EntityStore> npcRef = (Ref<EntityStore>)npcPair.first();
/* 636 */                 NPCEntity npcComponent = (NPCEntity)npcPair.second();
/*     */                 
/* 638 */                 ModelComponent modelComponent = (ModelComponent)commandBuffer.getComponent(npcRef, this.modelComponentType);
/* 639 */                 if (modelComponent != null && modelComponent.getModel().getAnimationSetMap().containsKey("Despawn")) {
/* 640 */                   Role role = npcComponent.getRole();
/* 641 */                   assert role != null;
/*     */                   
/* 643 */                   double despawnAnimationTime = role.getDespawnAnimationTime();
/*     */ 
/*     */                   
/* 646 */                   if (despawnAnimationTime > spawnMarkerEntityComponent.getTimeToDeactivation()) {
/* 647 */                     spawnMarkerEntityComponent.setTimeToDeactivation(despawnAnimationTime);
/*     */                   }
/* 649 */                   npcComponent.playAnimation(npcRef, AnimationSlot.Status, "Despawn", (ComponentAccessor)commandBuffer);
/*     */                 } 
/*     */               } 
/* 652 */               spawnMarkerEntityComponent.setDespawnStarted(true);
/* 653 */               tempStorageList.clear();
/*     */ 
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/* 659 */             PersistentRefCount refId = (PersistentRefCount)archetypeChunk.getComponent(index, this.referenceIdComponentType);
/* 660 */             if (refId != null) {
/* 661 */               refId.increment();
/*     */             }
/*     */             
/* 664 */             Ref<EntityStore> ref1 = archetypeChunk.getReferenceTo(index);
/* 665 */             commandBuffer.run(_store -> {
/*     */                   ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();
/*     */                   
/*     */                   for (InvalidatablePersistentRef reference : npcReferences) {
/*     */                     Ref<EntityStore> npcRef = reference.getEntity((ComponentAccessor)_store);
/*     */                     
/*     */                     if (npcRef == null || !npcRef.isValid()) {
/*     */                       ((HytaleLogger.Api)SpawnMarkerSystems.LOGGER.atWarning()).log("Connection with NPC from marker at %s lost due to being invalid/already unloaded", transformComponent.getPosition());
/*     */                     } else {
/*     */                       SpawnMarkerReference spawnMarkerReference = (SpawnMarkerReference)_store.ensureAndGetComponent(npcRef, SpawnMarkerReference.getComponentType());
/*     */                       
/*     */                       spawnMarkerReference.getReference().setEntity(ref, (ComponentAccessor)store);
/*     */                       
/*     */                       tempStorageList.add(npcRef);
/*     */                     } 
/*     */                   } 
/*     */                   storedFlock.storeNPCs((List)tempStorageList, _store);
/*     */                   spawnMarkerEntityComponent.setNpcReferences(null);
/*     */                 });
/*     */           } 
/*     */           return;
/*     */         } 
/*     */       } 
/* 688 */       if (spawnMarkerEntityComponent.tickSpawnLostTimeout(dt)) {
/*     */ 
/*     */ 
/*     */         
/* 692 */         PersistentRefCount refId = (PersistentRefCount)archetypeChunk.getComponent(index, this.referenceIdComponentType);
/* 693 */         if (refId != null) {
/* 694 */           refId.increment();
/* 695 */           SpawnMarkerSystems.LOGGER.at(Level.FINE).log("Marker lost spawned NPC and changed reference ID to %s", refId.get());
/*     */         } 
/* 697 */         Ref<EntityStore> ref1 = archetypeChunk.getReferenceTo(index);
/* 698 */         commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 703 */     if (!world.getWorldConfig().isSpawnMarkersEnabled() || cachedMarker.isManualTrigger() || (spawnMarkerEntityComponent.getSuppressedBy() != null && 
/* 704 */       !spawnMarkerEntityComponent.getSuppressedBy().isEmpty())) {
/*     */       return;
/*     */     }
/*     */     
/* 708 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/* 709 */     WorldTimeResource worldTimeResource = (WorldTimeResource)commandBuffer.getResource(WorldTimeResource.getResourceType());
/*     */ 
/*     */     
/* 712 */     if (cachedMarker.isRealtimeRespawn()) {
/* 713 */       if (spawnMarkerEntityComponent.tickRespawnTimer(dt)) {
/* 714 */         commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */       }
/* 716 */     } else if (spawnMarkerEntityComponent.getSpawnAfter() == null || worldTimeResource.getGameTime().isAfter(spawnMarkerEntityComponent.getSpawnAfter())) {
/* 717 */       commandBuffer.run(_store -> spawnMarkerEntityComponent.spawnNPC(ref, cachedMarker, _store));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\spawnmarkers\SpawnMarkerSystems$Ticking.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */