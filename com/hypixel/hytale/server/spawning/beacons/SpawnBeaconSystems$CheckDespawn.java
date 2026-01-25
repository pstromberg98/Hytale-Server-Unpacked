/*     */ package com.hypixel.hytale.server.spawning.beacons;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.spawning.controllers.BeaconSpawnController;
/*     */ import com.hypixel.hytale.server.spawning.wrappers.BeaconSpawnWrapper;
/*     */ import java.time.Instant;
/*     */ import java.util.List;
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
/*     */ public class CheckDespawn
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/* 211 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private final ComponentType<EntityStore, LegacySpawnBeaconEntity> componentType;
/*     */   
/*     */   @Nullable
/*     */   private final ComponentType<EntityStore, NPCEntity> npcComponentType;
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*     */   
/*     */   public CheckDespawn(ComponentType<EntityStore, LegacySpawnBeaconEntity> componentType, ComponentType<EntityStore, InitialBeaconDelay> initialBeaconDelayComponentType) {
/* 221 */     this.componentType = componentType;
/* 222 */     this.npcComponentType = NPCEntity.getComponentType();
/* 223 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)componentType, 
/*     */           
/* 225 */           (Query)UUIDComponent.getComponentType(), 
/* 226 */           (Query)Query.not((Query)initialBeaconDelayComponentType) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 233 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 238 */     return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 243 */     LegacySpawnBeaconEntity legacySpawnBeaconComponent = (LegacySpawnBeaconEntity)archetypeChunk.getComponent(index, this.componentType);
/* 244 */     assert legacySpawnBeaconComponent != null;
/*     */     
/* 246 */     UUIDComponent uuidComponent = (UUIDComponent)archetypeChunk.getComponent(index, UUIDComponent.getComponentType());
/* 247 */     assert uuidComponent != null;
/*     */     
/* 249 */     BeaconSpawnController spawnController = legacySpawnBeaconComponent.getSpawnController();
/* 250 */     Instant despawnSelfAfter = legacySpawnBeaconComponent.getDespawnSelfAfter();
/*     */     
/* 252 */     WorldTimeResource worldTimeResource = (WorldTimeResource)commandBuffer.getResource(WorldTimeResource.getResourceType());
/*     */     
/* 254 */     if (despawnSelfAfter != null && worldTimeResource.getGameTime().isAfter(despawnSelfAfter)) {
/* 255 */       despawnAllSpawns(spawnController.getSpawnedEntities(), commandBuffer);
/* 256 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */       
/*     */       return;
/*     */     } 
/* 260 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 261 */     BeaconSpawnWrapper spawnWrapper = legacySpawnBeaconComponent.getSpawnWrapper();
/* 262 */     if (spawnWrapper.shouldDespawn(world, worldTimeResource)) {
/* 263 */       LOGGER.at(Level.FINE).log("Removing spawn beacon %s due to matching despawn parameters", uuidComponent.getUuid());
/* 264 */       despawnAllSpawns(spawnController.getSpawnedEntities(), commandBuffer);
/* 265 */       commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void despawnAllSpawns(@Nonnull List<Ref<EntityStore>> spawnedEntities, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 270 */     for (int i = 0; i < spawnedEntities.size(); i++) {
/* 271 */       Ref<EntityStore> ref = spawnedEntities.get(i);
/* 272 */       if (ref.isValid()) {
/*     */         
/* 274 */         NPCEntity npc = (NPCEntity)commandBuffer.getComponent(ref, this.npcComponentType);
/* 275 */         if (npc != null)
/*     */         {
/*     */           
/* 278 */           if (!npc.getRole().getStateSupport().isInBusyState() && !npc.isDespawning())
/*     */           {
/* 280 */             npc.setToDespawn(); }  } 
/*     */       } 
/* 282 */     }  spawnedEntities.clear();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\beacons\SpawnBeaconSystems$CheckDespawn.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */