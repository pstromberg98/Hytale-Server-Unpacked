/*     */ package com.hypixel.hytale.builtin.adventure.farming.states;
/*     */ import com.hypixel.fastutil.shorts.Short2ObjectConcurrentHashMap;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
/*     */ import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
/*     */ import com.hypixel.hytale.builtin.tagset.TagSetPlugin;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.range.IntRange;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemDrop;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.reference.PersistentRef;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.Builder;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
/*     */ import com.hypixel.hytale.server.npc.role.Role;
/*     */ import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
/*     */ import com.hypixel.hytale.server.spawning.SpawningContext;
/*     */ import it.unimi.dsi.fastutil.Pair;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.temporal.ChronoUnit;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class CoopBlock implements Component<ChunkStore> {
/*     */   public static ComponentType<ChunkStore, CoopBlock> getComponentType() {
/*  57 */     return FarmingPlugin.get().getCoopBlockStateComponentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String STATE_PRODUCE = "Produce_Ready";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final BuilderCodec<CoopBlock> CODEC;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String coopAssetId;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  80 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CoopBlock.class, CoopBlock::new).append(new KeyedCodec("FarmingCoopId", (Codec)Codec.STRING, true), (coop, s) -> coop.coopAssetId = s, coop -> coop.coopAssetId).add()).append(new KeyedCodec("Residents", (Codec)new ArrayCodec((Codec)CoopResident.CODEC, x$0 -> new CoopResident[x$0])), (coop, residents) -> coop.residents = new ArrayList<>(Arrays.asList(residents)), coop -> (CoopResident[])coop.residents.toArray(())).add()).append(new KeyedCodec("Storage", (Codec)ItemContainer.CODEC), (coop, storage) -> coop.itemContainer = storage, coop -> coop.itemContainer).add()).build();
/*     */   }
/*     */   
/*  83 */   protected List<CoopResident> residents = new ArrayList<>();
/*  84 */   protected ItemContainer itemContainer = (ItemContainer)EmptyItemContainer.INSTANCE;
/*     */   
/*     */   public CoopBlock() {
/*  87 */     ArrayList<ItemStack> remainder = new ArrayList<>();
/*  88 */     this.itemContainer = ItemContainer.ensureContainerCapacity(this.itemContainer, (short)5, com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer::new, remainder);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public FarmingCoopAsset getCoopAsset() {
/*  93 */     return (FarmingCoopAsset)FarmingCoopAsset.getAssetMap().getAsset(this.coopAssetId);
/*     */   }
/*     */   
/*     */   public CoopBlock(String farmingCoopId, List<CoopResident> residents, ItemContainer itemContainer) {
/*  97 */     this.coopAssetId = farmingCoopId;
/*  98 */     this.residents.addAll(residents);
/*  99 */     this.itemContainer = itemContainer.clone();
/*     */     
/* 101 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/* 102 */     this.itemContainer = ItemContainer.ensureContainerCapacity(this.itemContainer, (short)5, com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer::new, (List)objectArrayList);
/*     */   }
/*     */   
/*     */   public boolean tryPutResident(CapturedNPCMetadata metadata, WorldTimeResource worldTimeResource) {
/* 106 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 107 */     if (coopAsset == null) {
/* 108 */       return false;
/*     */     }
/*     */     
/* 111 */     if (this.residents.size() >= coopAsset.getMaxResidents()) {
/* 112 */       return false;
/*     */     }
/*     */     
/* 115 */     if (!getCoopAcceptsNPCGroup(metadata.getRoleIndex())) {
/* 116 */       return false;
/*     */     }
/*     */     
/* 119 */     this.residents.add(new CoopResident(metadata, null, worldTimeResource.getGameTime()));
/* 120 */     return true;
/*     */   }
/*     */   
/*     */   public boolean tryPutWildResidentFromWild(Store<EntityStore> store, Ref<EntityStore> entityRef, WorldTimeResource worldTimeResource, Vector3i coopLocation) {
/* 124 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 125 */     if (coopAsset == null) {
/* 126 */       return false;
/*     */     }
/*     */     
/* 129 */     NPCEntity npcComponent = (NPCEntity)store.getComponent(entityRef, NPCEntity.getComponentType());
/* 130 */     if (npcComponent == null) {
/* 131 */       return false;
/*     */     }
/*     */     
/* 134 */     CoopResidentComponent coopResidentComponent = (CoopResidentComponent)store.getComponent(entityRef, CoopResidentComponent.getComponentType());
/* 135 */     if (coopResidentComponent != null) {
/* 136 */       return false;
/*     */     }
/*     */     
/* 139 */     if (!getCoopAcceptsNPCGroup(npcComponent.getRoleIndex())) {
/* 140 */       return false;
/*     */     }
/*     */     
/* 143 */     if (this.residents.size() >= coopAsset.getMaxResidents()) {
/* 144 */       return false;
/*     */     }
/*     */     
/* 147 */     coopResidentComponent = (CoopResidentComponent)store.ensureAndGetComponent(entityRef, CoopResidentComponent.getComponentType());
/* 148 */     coopResidentComponent.setCoopLocation(coopLocation);
/*     */     
/* 150 */     UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(entityRef, UUIDComponent.getComponentType());
/* 151 */     if (uuidComponent == null) {
/* 152 */       return false;
/*     */     }
/*     */     
/* 155 */     PersistentRef persistentRef = new PersistentRef();
/* 156 */     persistentRef.setEntity(entityRef, uuidComponent.getUuid());
/*     */     
/* 158 */     CapturedNPCMetadata metadata = FarmingUtil.generateCapturedNPCMetadata((ComponentAccessor)store, entityRef, npcComponent.getRoleIndex());
/* 159 */     CoopResident residentRecord = new CoopResident(metadata, persistentRef, worldTimeResource.getGameTime());
/* 160 */     residentRecord.deployedToWorld = true;
/* 161 */     this.residents.add(residentRecord);
/*     */     
/* 163 */     return true;
/*     */   }
/*     */   
/*     */   public boolean getCoopAcceptsNPCGroup(int npcRoleIndex) {
/* 167 */     TagSetPlugin.TagSetLookup tagSetPlugin = TagSetPlugin.get(NPCGroup.class);
/* 168 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 169 */     if (coopAsset == null) {
/* 170 */       return false;
/*     */     }
/*     */     
/* 173 */     int[] acceptedNpcGroupIndexes = coopAsset.getAcceptedNpcGroupIndexes();
/* 174 */     if (acceptedNpcGroupIndexes == null) {
/* 175 */       return true;
/*     */     }
/*     */     
/* 178 */     for (int group : acceptedNpcGroupIndexes) {
/* 179 */       if (tagSetPlugin.tagInSet(group, npcRoleIndex)) {
/* 180 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 184 */     return false;
/*     */   }
/*     */   
/*     */   public void generateProduceToInventory(WorldTimeResource worldTimeResource) {
/* 188 */     Instant currentTime = worldTimeResource.getGameTime();
/*     */     
/* 190 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 191 */     if (coopAsset == null) {
/*     */       return;
/*     */     }
/*     */     
/* 195 */     Map<String, String> produceDropsMap = coopAsset.getProduceDrops();
/* 196 */     if (produceDropsMap.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 200 */     ThreadLocalRandom random = ThreadLocalRandom.current();
/*     */ 
/*     */     
/* 203 */     List<ItemStack> generatedItemDrops = new ArrayList<>();
/* 204 */     for (CoopResident resident : this.residents) {
/* 205 */       Instant lastProduced = resident.getLastProduced();
/*     */       
/* 207 */       if (lastProduced == null) {
/* 208 */         resident.setLastProduced(currentTime);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 213 */       CapturedNPCMetadata residentMeta = resident.getMetadata();
/* 214 */       int npcRoleIndex = residentMeta.getRoleIndex();
/* 215 */       String npcName = NPCPlugin.get().getName(npcRoleIndex);
/* 216 */       String npcDropListName = produceDropsMap.get(npcName);
/* 217 */       if (npcDropListName == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 221 */       ItemDropList dropListAsset = (ItemDropList)ItemDropList.getAssetMap().getAsset(npcDropListName);
/* 222 */       if (dropListAsset == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 226 */       Duration harvestDiff = Duration.between(lastProduced, currentTime);
/* 227 */       long hoursSinceLastHarvest = harvestDiff.toHours();
/* 228 */       int produceCount = MathUtil.ceil(((float)hoursSinceLastHarvest / WorldTimeResource.HOURS_PER_DAY));
/*     */ 
/*     */       
/* 231 */       List<ItemDrop> configuredItemDrops = new ArrayList<>();
/* 232 */       for (int i = 0; i < produceCount; i++) {
/* 233 */         Objects.requireNonNull(random); dropListAsset.getContainer().populateDrops(configuredItemDrops, random::nextDouble, npcDropListName);
/*     */         
/* 235 */         for (ItemDrop drop : configuredItemDrops) {
/* 236 */           if (drop == null || drop.getItemId() == null) {
/* 237 */             ((HytaleLogger.Api)HytaleLogger.forEnclosingClass().atWarning()).log("Tried to create ItemDrop for non-existent item in drop list id '%s'", npcDropListName);
/*     */             
/*     */             continue;
/*     */           } 
/* 241 */           int amount = drop.getRandomQuantity(random);
/* 242 */           if (amount > 0) {
/* 243 */             generatedItemDrops.add(new ItemStack(drop.getItemId(), amount, drop.getMetadata()));
/*     */           }
/*     */         } 
/* 246 */         configuredItemDrops.clear();
/*     */       } 
/*     */       
/* 249 */       resident.setLastProduced(currentTime);
/*     */     } 
/*     */     
/* 252 */     this.itemContainer.addItemStacks(generatedItemDrops);
/*     */   }
/*     */   
/*     */   public void gatherProduceFromInventory(ItemContainer playerInventory) {
/* 256 */     for (ItemStack item : this.itemContainer.removeAllItemStacks()) {
/* 257 */       playerInventory.addItemStack(item);
/*     */     }
/*     */   }
/*     */   
/*     */   public void ensureSpawnResidentsInWorld(World world, Store<EntityStore> store, Vector3d coopLocation, Vector3d spawnOffset) {
/* 262 */     NPCPlugin npcModule = NPCPlugin.get();
/*     */     
/* 264 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 265 */     if (coopAsset == null) {
/*     */       return;
/*     */     }
/*     */     
/* 269 */     float radiansPerSpawn = 6.2831855F / coopAsset.getMaxResidents();
/* 270 */     Vector3d spawnOffsetIteration = spawnOffset;
/*     */     
/* 272 */     SpawningContext spawningContext = new SpawningContext();
/*     */     
/* 274 */     for (CoopResident resident : this.residents) {
/* 275 */       CapturedNPCMetadata residentMeta = resident.getMetadata();
/* 276 */       int npcRoleIndex = residentMeta.getRoleIndex();
/*     */       
/* 278 */       boolean residentDeployed = resident.getDeployedToWorld();
/* 279 */       PersistentRef residentEntityId = resident.getPersistentRef();
/*     */       
/* 281 */       if (residentDeployed || residentEntityId != null) {
/*     */         continue;
/*     */       }
/*     */       
/* 285 */       Vector3d residentSpawnLocation = (new Vector3d()).assign(coopLocation).add(spawnOffsetIteration);
/* 286 */       Builder<Role> roleBuilder = NPCPlugin.get().tryGetCachedValidRole(npcRoleIndex);
/* 287 */       if (roleBuilder == null) {
/*     */         continue;
/*     */       }
/* 290 */       spawningContext.setSpawnable((ISpawnableWithModel)roleBuilder);
/* 291 */       if (!spawningContext.set(world, residentSpawnLocation.x, residentSpawnLocation.y, residentSpawnLocation.z) || spawningContext
/* 292 */         .canSpawn() != SpawnTestResult.TEST_OK) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       
/* 297 */       Pair<Ref<EntityStore>, NPCEntity> npcPair = npcModule.spawnEntity(store, npcRoleIndex, spawningContext.newPosition(), Vector3f.ZERO, null, null);
/* 298 */       if (npcPair == null) {
/* 299 */         resident.setPersistentRef(null);
/* 300 */         resident.setDeployedToWorld(false);
/*     */         
/*     */         continue;
/*     */       } 
/* 304 */       Ref<EntityStore> npcRef = (Ref<EntityStore>)npcPair.first();
/* 305 */       NPCEntity npcComponent = (NPCEntity)npcPair.second();
/*     */       
/* 307 */       npcComponent.getLeashPoint().assign(coopLocation);
/*     */       
/* 309 */       if (npcRef == null || !npcRef.isValid()) {
/* 310 */         resident.setPersistentRef(null);
/* 311 */         resident.setDeployedToWorld(false);
/*     */         
/*     */         continue;
/*     */       } 
/* 315 */       UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(npcRef, UUIDComponent.getComponentType());
/* 316 */       if (uuidComponent == null) {
/* 317 */         resident.setPersistentRef(null);
/* 318 */         resident.setDeployedToWorld(false);
/*     */         
/*     */         continue;
/*     */       } 
/* 322 */       CoopResidentComponent coopResidentComponent = new CoopResidentComponent();
/* 323 */       coopResidentComponent.setCoopLocation(coopLocation.toVector3i());
/* 324 */       store.addComponent(npcRef, CoopResidentComponent.getComponentType(), (Component)coopResidentComponent);
/*     */       
/* 326 */       PersistentRef persistentRef = new PersistentRef();
/* 327 */       persistentRef.setEntity(npcRef, uuidComponent.getUuid());
/*     */       
/* 329 */       resident.setPersistentRef(persistentRef);
/* 330 */       resident.setDeployedToWorld(true);
/*     */       
/* 332 */       spawnOffsetIteration = spawnOffsetIteration.rotateY(radiansPerSpawn);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void ensureNoResidentsInWorld(Store<EntityStore> store) {
/* 337 */     ArrayList<CoopResident> residentsToRemove = new ArrayList<>();
/*     */     
/* 339 */     for (CoopResident resident : this.residents) {
/* 340 */       boolean deployed = resident.getDeployedToWorld();
/* 341 */       PersistentRef entityUuid = resident.getPersistentRef();
/* 342 */       if (!deployed && entityUuid == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 346 */       Ref<EntityStore> entityRef = entityUuid.getEntity((ComponentAccessor)store);
/* 347 */       if (entityRef == null) {
/* 348 */         residentsToRemove.add(resident);
/*     */         
/*     */         continue;
/*     */       } 
/* 352 */       CoopResidentComponent coopResidentComponent = (CoopResidentComponent)store.getComponent(entityRef, CoopResidentComponent.getComponentType());
/* 353 */       if (coopResidentComponent == null) {
/* 354 */         residentsToRemove.add(resident);
/*     */         
/*     */         continue;
/*     */       } 
/* 358 */       DeathComponent deathComponent = (DeathComponent)store.getComponent(entityRef, DeathComponent.getComponentType());
/* 359 */       if (deathComponent != null) {
/* 360 */         residentsToRemove.add(resident);
/*     */         
/*     */         continue;
/*     */       } 
/* 364 */       coopResidentComponent.setMarkedForDespawn(true);
/* 365 */       resident.setPersistentRef(null);
/* 366 */       resident.setDeployedToWorld(false);
/*     */     } 
/*     */     
/* 369 */     for (CoopResident resident : residentsToRemove) {
/* 370 */       this.residents.remove(resident);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean shouldResidentsBeInCoop(WorldTimeResource worldTimeResource) {
/* 375 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 376 */     if (coopAsset == null) {
/* 377 */       return true;
/*     */     }
/*     */     
/* 380 */     IntRange roamTimeRange = coopAsset.getResidentRoamTime();
/* 381 */     if (roamTimeRange == null) {
/* 382 */       return true;
/*     */     }
/*     */     
/* 385 */     int gameHour = worldTimeResource.getCurrentHour();
/* 386 */     return !roamTimeRange.includes(gameHour);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Instant getNextScheduledTick(WorldTimeResource worldTimeResource) {
/* 391 */     Instant gameTime = worldTimeResource.getGameTime();
/* 392 */     LocalDateTime gameDateTime = worldTimeResource.getGameDateTime();
/* 393 */     int gameHour = worldTimeResource.getCurrentHour();
/* 394 */     int minutes = gameDateTime.getMinute();
/*     */     
/* 396 */     FarmingCoopAsset coopAsset = getCoopAsset();
/* 397 */     if (coopAsset == null) {
/* 398 */       return null;
/*     */     }
/*     */     
/* 401 */     IntRange roamTimeRange = coopAsset.getResidentRoamTime();
/* 402 */     if (roamTimeRange == null) {
/* 403 */       return null;
/*     */     }
/*     */     
/* 406 */     int nextScheduledHour = 0;
/* 407 */     int minTime = roamTimeRange.getInclusiveMin();
/* 408 */     int maxTime = roamTimeRange.getInclusiveMax();
/*     */     
/* 410 */     if (coopAsset.getResidentRoamTime().includes(gameHour)) {
/* 411 */       nextScheduledHour = coopAsset.getResidentRoamTime().getInclusiveMax() + 1 - gameHour;
/*     */     }
/* 413 */     else if (gameHour > maxTime) {
/* 414 */       nextScheduledHour = WorldTimeResource.HOURS_PER_DAY - gameHour + minTime;
/*     */     } else {
/* 416 */       nextScheduledHour = minTime - gameHour;
/*     */     } 
/*     */ 
/*     */     
/* 420 */     return gameTime.plus(nextScheduledHour * 60L - minutes, ChronoUnit.MINUTES);
/*     */   }
/*     */   
/*     */   public void handleResidentDespawn(UUID entityUuid) {
/* 424 */     CoopResident removedResident = null;
/*     */     
/* 426 */     for (CoopResident resident : this.residents) {
/* 427 */       if (resident.persistentRef == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 431 */       if (resident.persistentRef.getUuid() == entityUuid) {
/* 432 */         removedResident = resident;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 437 */     if (removedResident == null) {
/*     */       return;
/*     */     }
/*     */     
/* 441 */     this.residents.remove(removedResident);
/*     */   }
/*     */   
/*     */   public void handleBlockBroken(World world, WorldTimeResource worldTimeResource, Store<EntityStore> store, int blockX, int blockY, int blockZ) {
/* 445 */     Vector3i location = new Vector3i(blockX, blockY, blockZ);
/* 446 */     world.execute(() -> ensureSpawnResidentsInWorld(world, store, location.toVector3d(), (new Vector3d()).assign(Vector3d.FORWARD)));
/*     */     
/* 448 */     generateProduceToInventory(worldTimeResource);
/*     */     
/* 450 */     Vector3d dropPosition = new Vector3d((blockX + 0.5F), blockY, (blockZ + 0.5F));
/*     */     
/* 452 */     Holder[] arrayOfHolder = ItemComponent.generateItemDrops((ComponentAccessor)store, this.itemContainer.removeAllItemStacks(), dropPosition, Vector3f.ZERO);
/* 453 */     if (arrayOfHolder.length > 0) {
/* 454 */       world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
/*     */     }
/*     */     
/* 457 */     world.execute(() -> {
/*     */           for (CoopResident resident : this.residents) {
/*     */             PersistentRef persistentRef = resident.getPersistentRef();
/*     */             if (persistentRef == null) {
/*     */               continue;
/*     */             }
/*     */             Ref<EntityStore> ref = persistentRef.getEntity((ComponentAccessor)store);
/*     */             if (ref == null) {
/*     */               return;
/*     */             }
/*     */             store.tryRemoveComponent(ref, CoopResidentComponent.getComponentType());
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasProduce() {
/* 475 */     return !this.itemContainer.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public Component<ChunkStore> clone() {
/* 480 */     return new CoopBlock(this.coopAssetId, this.residents, this.itemContainer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class CoopResident
/*     */   {
/*     */     public static final BuilderCodec<CoopResident> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     protected CapturedNPCMetadata metadata;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected PersistentRef persistentRef;
/*     */ 
/*     */ 
/*     */     
/*     */     protected boolean deployedToWorld;
/*     */ 
/*     */     
/*     */     protected Instant lastProduced;
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 509 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CoopResident.class, CoopResident::new).append(new KeyedCodec("Metadata", (Codec)CapturedNPCMetadata.CODEC), (coop, meta) -> coop.metadata = meta, coop -> coop.metadata).add()).append(new KeyedCodec("PersistentRef", (Codec)PersistentRef.CODEC), (coop, persistentRef) -> coop.persistentRef = persistentRef, coop -> coop.persistentRef).add()).append(new KeyedCodec("DeployedToWorld", (Codec)Codec.BOOLEAN), (coop, deployedToWorld) -> coop.deployedToWorld = deployedToWorld.booleanValue(), coop -> Boolean.valueOf(coop.deployedToWorld)).add()).append(new KeyedCodec("LastHarvested", (Codec)Codec.INSTANT), (coop, instant) -> coop.lastProduced = instant, coop -> coop.lastProduced).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CoopResident() {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CoopResident(CapturedNPCMetadata metadata, PersistentRef persistentRef, Instant lastProduced) {
/* 522 */       this.metadata = metadata;
/* 523 */       this.persistentRef = persistentRef;
/* 524 */       this.lastProduced = lastProduced;
/*     */     }
/*     */     
/*     */     public CapturedNPCMetadata getMetadata() {
/* 528 */       return this.metadata;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public PersistentRef getPersistentRef() {
/* 533 */       return this.persistentRef;
/*     */     }
/*     */     
/*     */     public void setPersistentRef(@Nullable PersistentRef persistentRef) {
/* 537 */       this.persistentRef = persistentRef;
/*     */     }
/*     */     public boolean getDeployedToWorld() {
/* 540 */       return this.deployedToWorld;
/*     */     } public void setDeployedToWorld(boolean deployedToWorld) {
/* 542 */       this.deployedToWorld = deployedToWorld;
/*     */     }
/*     */     public Instant getLastProduced() {
/* 545 */       return this.lastProduced;
/*     */     }
/*     */     
/*     */     public void setLastProduced(Instant lastProduced) {
/* 549 */       this.lastProduced = lastProduced;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\farming\states\CoopBlock.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */