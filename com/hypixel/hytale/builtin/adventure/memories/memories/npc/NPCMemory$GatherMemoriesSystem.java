/*     */ package com.hypixel.hytale.builtin.adventure.memories.memories.npc;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesGameplayConfig;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
/*     */ import com.hypixel.hytale.builtin.instances.config.InstanceDiscoveryConfig;
/*     */ import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.spatial.SpatialStructure;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.protocol.ModelParticle;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.entities.SpawnModelParticles;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.item.PickupItemComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*     */ import com.hypixel.hytale.server.core.util.NotificationUtil;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
/*     */ import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
/*     */ import com.hypixel.hytale.server.worldgen.chunk.ZoneBiomeResult;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GatherMemoriesSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/* 213 */   public static final Query<EntityStore> QUERY = (Query<EntityStore>)Query.and(new Query[] {
/* 214 */         (Query)TransformComponent.getComponentType(), 
/* 215 */         (Query)Player.getComponentType(), 
/* 216 */         (Query)PlayerMemories.getComponentType()
/*     */       });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final double radius;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GatherMemoriesSystem(double radius) {
/* 230 */     this.radius = radius;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 235 */     Player playerComponent = (Player)archetypeChunk.getComponent(index, Player.getComponentType());
/* 236 */     assert playerComponent != null;
/* 237 */     if (playerComponent.getGameMode() != GameMode.Adventure)
/*     */       return; 
/* 239 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/* 240 */     assert transformComponent != null;
/* 241 */     Vector3d position = transformComponent.getPosition();
/*     */     
/* 243 */     SpatialResource<Ref<EntityStore>, EntityStore> npcSpatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(NPCPlugin.get().getNpcSpatialResource());
/* 244 */     ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 245 */     npcSpatialResource.getSpatialStructure().collect(position, this.radius, (List)results);
/* 246 */     if (results.isEmpty())
/*     */       return; 
/* 248 */     PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/* 249 */     assert playerRefComponent != null;
/*     */     
/* 251 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/*     */     
/* 253 */     MemoriesPlugin memoriesPlugin = MemoriesPlugin.get();
/* 254 */     PlayerMemories playerMemoriesComponent = (PlayerMemories)archetypeChunk.getComponent(index, PlayerMemories.getComponentType());
/* 255 */     assert playerMemoriesComponent != null;
/*     */     
/* 257 */     NPCMemory temp = new NPCMemory();
/*     */     
/* 259 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/* 260 */     String foundLocationZoneNameKey = findLocationZoneName(world, position);
/*     */     
/* 262 */     for (ObjectListIterator<Ref<EntityStore>> objectListIterator = results.iterator(); objectListIterator.hasNext(); ) { Ref<EntityStore> npcRef = objectListIterator.next();
/* 263 */       NPCEntity npcComponent = (NPCEntity)commandBuffer.getComponent(npcRef, NPCEntity.getComponentType());
/* 264 */       if (npcComponent == null)
/*     */         continue; 
/* 266 */       Role role = npcComponent.getRole();
/* 267 */       assert role != null;
/* 268 */       if (!role.isMemory()) {
/*     */         continue;
/*     */       }
/*     */       
/* 272 */       temp.isMemoriesNameOverridden = role.isMemoriesNameOverriden();
/* 273 */       temp.npcRole = temp.isMemoriesNameOverridden ? role.getMemoriesNameOverride() : npcComponent.getRoleName();
/* 274 */       temp.memoryTitleKey = role.getNameTranslationKey();
/* 275 */       temp.capturedTimestamp = System.currentTimeMillis();
/* 276 */       temp.foundLocationGeneralNameKey = foundLocationZoneNameKey;
/*     */       
/* 278 */       if (memoriesPlugin.hasRecordedMemory(temp)) {
/*     */         continue;
/*     */       }
/* 281 */       temp.processConfig();
/*     */ 
/*     */ 
/*     */       
/* 285 */       if (playerMemoriesComponent.recordMemory(temp)) {
/* 286 */         NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), Message.translation("server.memories.general.collected")
/* 287 */             .param("memoryTitle", Message.translation(temp.getTitle())), null, "NotificationIcons/MemoriesIcon.png");
/*     */         
/* 289 */         temp = new NPCMemory();
/*     */         
/* 291 */         TransformComponent npcTransformComponent = (TransformComponent)commandBuffer.getComponent(npcRef, TransformComponent.getComponentType());
/* 292 */         assert npcTransformComponent != null;
/*     */         
/* 294 */         MemoriesGameplayConfig memoriesGameplayConfig = MemoriesGameplayConfig.get(((EntityStore)store.getExternalData()).getWorld().getGameplayConfig());
/* 295 */         if (memoriesGameplayConfig != null) {
/* 296 */           ItemStack memoryItemStack = new ItemStack(memoriesGameplayConfig.getMemoriesCatchItemId());
/* 297 */           Vector3d memoryItemHolderPosition = npcTransformComponent.getPosition().clone();
/*     */           
/* 299 */           BoundingBox boundingBoxComponent = (BoundingBox)commandBuffer.getComponent(npcRef, BoundingBox.getComponentType());
/* 300 */           if (boundingBoxComponent != null) {
/* 301 */             memoryItemHolderPosition.y += boundingBoxComponent.getBoundingBox().middleY();
/*     */           }
/*     */           
/* 304 */           Holder<EntityStore> memoryItemHolder = ItemComponent.generatePickedUpItem(memoryItemStack, memoryItemHolderPosition, (ComponentAccessor)commandBuffer, ref);
/* 305 */           float memoryCatchItemLifetimeS = 0.62F;
/*     */           
/* 307 */           PickupItemComponent pickupItemComponent = (PickupItemComponent)memoryItemHolder.getComponent(PickupItemComponent.getComponentType());
/* 308 */           assert pickupItemComponent != null;
/*     */           
/* 310 */           pickupItemComponent.setInitialLifeTime(0.62F);
/* 311 */           commandBuffer.addEntity(memoryItemHolder, AddReason.SPAWN);
/*     */           
/* 313 */           displayCatchEntityParticles(memoriesGameplayConfig, memoryItemHolderPosition, npcRef, commandBuffer);
/*     */         } 
/*     */       }  }
/*     */   
/*     */   }
/*     */   
/*     */   private static String findLocationZoneName(World world, Vector3d position) {
/* 320 */     IWorldGen worldGen = world.getChunkStore().getGenerator();
/* 321 */     if (worldGen instanceof ChunkGenerator) { ChunkGenerator generator = (ChunkGenerator)worldGen;
/* 322 */       int seed = (int)world.getWorldConfig().getSeed();
/* 323 */       ZoneBiomeResult result = generator.getZoneBiomeResultAt(seed, MathUtil.floor(position.x), MathUtil.floor(position.z));
/* 324 */       return "server.map.region." + result.getZoneResult().getZone().name(); }
/*     */ 
/*     */     
/* 327 */     InstanceWorldConfig instanceConfig = (InstanceWorldConfig)world.getWorldConfig().getPluginConfig().get(InstanceWorldConfig.class);
/* 328 */     if (instanceConfig != null) {
/* 329 */       InstanceDiscoveryConfig discovery = instanceConfig.getDiscovery();
/* 330 */       if (discovery != null && discovery.getTitleKey() != null) {
/* 331 */         return discovery.getTitleKey();
/*     */       }
/*     */     } 
/*     */     
/* 335 */     return "???";
/*     */   }
/*     */   
/*     */   private static void displayCatchEntityParticles(MemoriesGameplayConfig memoriesGameplayConfig, Vector3d targetPosition, Ref<EntityStore> targetRef, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 339 */     ModelParticle particle = memoriesGameplayConfig.getMemoriesCatchEntityParticle();
/* 340 */     if (particle == null)
/*     */       return; 
/* 342 */     NetworkId networkIdComponent = (NetworkId)commandBuffer.getComponent(targetRef, NetworkId.getComponentType());
/* 343 */     if (networkIdComponent == null)
/*     */       return; 
/* 345 */     ModelParticle[] modelParticlesProtocol = { particle.toPacket() };
/* 346 */     SpawnModelParticles packet = new SpawnModelParticles(networkIdComponent.getId(), modelParticlesProtocol);
/*     */     
/* 348 */     SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)commandBuffer.getResource(EntityModule.get().getPlayerSpatialResourceType());
/* 349 */     SpatialStructure<Ref<EntityStore>> spatialStructure = spatialResource.getSpatialStructure();
/* 350 */     ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
/* 351 */     spatialStructure.ordered(targetPosition, memoriesGameplayConfig.getMemoriesCatchParticleViewDistance(), (List)results);
/*     */     
/* 353 */     for (ObjectListIterator<Ref<EntityStore>> objectListIterator = results.iterator(); objectListIterator.hasNext(); ) { Ref<EntityStore> ref = objectListIterator.next();
/* 354 */       PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
/* 355 */       assert playerRefComponent != null;
/*     */       
/* 357 */       playerRefComponent.getPacketHandler().write((Packet)packet); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 364 */     return QUERY;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\memories\npc\NPCMemory$GatherMemoriesSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */