/*     */ package com.hypixel.hytale.server.core.universe.world;
/*     */ 
/*     */ import com.hypixel.hytale.common.fastutil.HLongOpenHashSet;
/*     */ import com.hypixel.hytale.common.fastutil.HLongSet;
/*     */ import com.hypixel.hytale.common.thread.ticking.Tickable;
/*     */ import com.hypixel.hytale.common.util.CompletableFutureUtil;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.system.EcsEvent;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.iterator.CircleSpiralIterator;
/*     */ import com.hypixel.hytale.math.shape.Box2D;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.SoundCategory;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.ClearWorldMap;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapChunk;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMap;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapSettings;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.event.events.ecs.DiscoverZoneEvent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapSettings;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*     */ import com.hypixel.hytale.server.core.util.EventTitleUtil;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.longs.LongCollection;
/*     */ import it.unimi.dsi.fastutil.longs.LongIterator;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class WorldMapTracker
/*     */   implements Tickable {
/*  54 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final float UPDATE_SPEED = 1.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int RADIUS_MAX = 512;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int EMPTY_UPDATE_WORLD_MAP_SIZE = 13;
/*     */ 
/*     */   
/*     */   private static final int EMPTY_MAP_CHUNK_SIZE = 10;
/*     */ 
/*     */   
/*     */   private static final int FULL_MAP_CHUNK_SIZE = 23;
/*     */ 
/*     */   
/*     */   public static final int MAX_IMAGE_GENERATION = 20;
/*     */ 
/*     */   
/*     */   public static final int MAX_FRAME = 2621440;
/*     */ 
/*     */   
/*     */   private final Player player;
/*     */ 
/*     */   
/*  84 */   private final CircleSpiralIterator spiralIterator = new CircleSpiralIterator();
/*     */   
/*  86 */   private final ReentrantReadWriteLock loadedLock = new ReentrantReadWriteLock();
/*     */   
/*  88 */   private final HLongSet loaded = (HLongSet)new HLongOpenHashSet();
/*  89 */   private final HLongSet pendingReloadChunks = (HLongSet)new HLongOpenHashSet();
/*     */   
/*  91 */   private final Long2ObjectOpenHashMap<CompletableFuture<MapImage>> pendingReloadFutures = new Long2ObjectOpenHashMap();
/*     */ 
/*     */   
/*     */   private final MapMarkerTracker markerTracker;
/*     */   
/*     */   private float updateTimer;
/*     */   
/*     */   private Integer viewRadiusOverride;
/*     */   
/*     */   private boolean started;
/*     */   
/*     */   private int sentViewRadius;
/*     */   
/*     */   private int lastChunkX;
/*     */   
/*     */   private int lastChunkZ;
/*     */   
/*     */   @Nullable
/*     */   private String currentBiomeName;
/*     */   
/*     */   @Nullable
/*     */   private ZoneDiscoveryInfo currentZone;
/*     */   
/*     */   private boolean clientHasWorldMapVisible;
/*     */   
/*     */   @Nullable
/*     */   private TransformComponent transformComponent;
/*     */ 
/*     */   
/*     */   public WorldMapTracker(@Nonnull Player player) {
/* 121 */     this.player = player;
/* 122 */     this.markerTracker = new MapMarkerTracker(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt) {
/*     */     int viewRadius;
/* 128 */     if (!this.started) {
/* 129 */       this.started = true;
/* 130 */       LOGGER.at(Level.INFO).log("Started Generating Map!");
/*     */     } 
/*     */     
/* 133 */     World world = this.player.getWorld();
/* 134 */     if (world == null) {
/*     */       return;
/*     */     }
/* 137 */     if (this.transformComponent == null) {
/* 138 */       this.transformComponent = this.player.getTransformComponent();
/* 139 */       if (this.transformComponent == null)
/*     */         return; 
/*     */     } 
/* 142 */     WorldMapManager worldMapManager = world.getWorldMapManager();
/* 143 */     WorldMapSettings worldMapSettings = worldMapManager.getWorldMapSettings();
/*     */ 
/*     */     
/* 146 */     if (this.viewRadiusOverride != null) {
/* 147 */       viewRadius = this.viewRadiusOverride.intValue();
/*     */     } else {
/* 149 */       viewRadius = worldMapSettings.getViewRadius(this.player.getViewRadius());
/*     */     } 
/*     */     
/* 152 */     Vector3d position = this.transformComponent.getPosition();
/* 153 */     int playerX = MathUtil.floor(position.getX());
/* 154 */     int playerZ = MathUtil.floor(position.getZ());
/* 155 */     int playerChunkX = playerX >> 5;
/* 156 */     int playerChunkZ = playerZ >> 5;
/*     */     
/* 158 */     if (world.isCompassUpdating()) {
/* 159 */       this.markerTracker.updatePointsOfInterest(dt, world, viewRadius, playerChunkX, playerChunkZ);
/*     */     }
/*     */     
/* 162 */     if (worldMapManager.isWorldMapEnabled()) {
/* 163 */       updateWorldMap(world, dt, worldMapSettings, viewRadius, playerChunkX, playerChunkZ);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateCurrentZoneAndBiome(@Nonnull Ref<EntityStore> ref, @Nullable ZoneDiscoveryInfo zoneDiscoveryInfo, @Nullable String biomeName, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 179 */     this.currentBiomeName = biomeName;
/* 180 */     this.currentZone = zoneDiscoveryInfo;
/*     */     
/* 182 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 183 */     assert playerComponent != null;
/*     */ 
/*     */ 
/*     */     
/* 187 */     if (!playerComponent.isWaitingForClientReady()) {
/*     */ 
/*     */       
/* 190 */       World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 191 */       if (zoneDiscoveryInfo != null && discoverZone(world, zoneDiscoveryInfo.regionName()))
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 196 */         onZoneDiscovered(ref, zoneDiscoveryInfo, componentAccessor);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onZoneDiscovered(@Nonnull Ref<EntityStore> ref, @Nonnull ZoneDiscoveryInfo zoneDiscoveryInfo, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 212 */     ZoneDiscoveryInfo discoverZoneEventInfo = zoneDiscoveryInfo.clone();
/* 213 */     DiscoverZoneEvent.Display discoverZoneEvent = new DiscoverZoneEvent.Display(discoverZoneEventInfo);
/* 214 */     componentAccessor.invoke(ref, (EcsEvent)discoverZoneEvent);
/*     */ 
/*     */     
/* 217 */     if (discoverZoneEvent.isCancelled() || !discoverZoneEventInfo.display()) {
/*     */       return;
/*     */     }
/*     */     
/* 221 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/* 222 */     assert playerRefComponent != null;
/*     */     
/* 224 */     EventTitleUtil.showEventTitleToPlayer(playerRefComponent, 
/* 225 */         Message.translation(String.format("server.map.region.%s", new Object[] { discoverZoneEventInfo.regionName()
/* 226 */             })), Message.translation(String.format("server.map.zone.%s", new Object[] { discoverZoneEventInfo.zoneName() })), discoverZoneEventInfo
/* 227 */         .major(), discoverZoneEventInfo
/* 228 */         .icon(), discoverZoneEventInfo
/* 229 */         .duration(), discoverZoneEventInfo
/* 230 */         .fadeInDuration(), discoverZoneEventInfo
/* 231 */         .fadeOutDuration());
/*     */     
/* 233 */     String discoverySoundEventId = discoverZoneEventInfo.discoverySoundEventId();
/* 234 */     if (discoverySoundEventId != null) {
/* 235 */       int assetIndex = SoundEvent.getAssetMap().getIndex(discoverySoundEventId);
/* 236 */       if (assetIndex != Integer.MIN_VALUE)
/* 237 */         SoundUtil.playSoundEvent2d(ref, assetIndex, SoundCategory.UI, componentAccessor); 
/*     */     } 
/*     */   }
/*     */   public static final class ZoneDiscoveryInfo extends Record {
/*     */     @Nonnull
/*     */     private final String zoneName; @Nonnull
/*     */     private final String regionName; private final boolean display; @Nullable
/*     */     private final String discoverySoundEventId; @Nullable
/*     */     private final String icon; private final boolean major; private final float duration; private final float fadeInDuration; private final float fadeOutDuration;
/*     */     
/*     */     public final String toString() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #255	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;
/*     */     }
/*     */     
/*     */     public final int hashCode() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #255	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;
/*     */     }
/*     */     
/* 255 */     public ZoneDiscoveryInfo(@Nonnull String zoneName, @Nonnull String regionName, boolean display, @Nullable String discoverySoundEventId, @Nullable String icon, boolean major, float duration, float fadeInDuration, float fadeOutDuration) { this.zoneName = zoneName; this.regionName = regionName; this.display = display; this.discoverySoundEventId = discoverySoundEventId; this.icon = icon; this.major = major; this.duration = duration; this.fadeInDuration = fadeInDuration; this.fadeOutDuration = fadeOutDuration; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #255	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/universe/world/WorldMapTracker$ZoneDiscoveryInfo;
/* 255 */       //   0	8	1	o	Ljava/lang/Object; } @Nonnull public String zoneName() { return this.zoneName; } @Nonnull public String regionName() { return this.regionName; } public boolean display() { return this.display; } @Nullable public String discoverySoundEventId() { return this.discoverySoundEventId; } @Nullable public String icon() { return this.icon; } public boolean major() { return this.major; } public float duration() { return this.duration; } public float fadeInDuration() { return this.fadeInDuration; } public float fadeOutDuration() { return this.fadeOutDuration; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public ZoneDiscoveryInfo clone() {
/* 270 */       return new ZoneDiscoveryInfo(this.zoneName, this.regionName, this.display, this.discoverySoundEventId, this.icon, this.major, this.duration, this.fadeInDuration, this.fadeOutDuration);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateWorldMap(@Nonnull World world, float dt, @Nonnull WorldMapSettings worldMapSettings, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 294 */     processPendingReloadChunks(world);
/*     */     
/* 296 */     Box2D worldMapArea = worldMapSettings.getWorldMapArea();
/* 297 */     if (worldMapArea == null) {
/* 298 */       int xDiff = Math.abs(this.lastChunkX - playerChunkX);
/* 299 */       int zDiff = Math.abs(this.lastChunkZ - playerChunkZ);
/* 300 */       int chunkMoveDistance = (xDiff > 0 || zDiff > 0) ? (int)Math.ceil(Math.sqrt((xDiff * xDiff + zDiff * zDiff))) : 0;
/* 301 */       this.sentViewRadius = Math.max(0, this.sentViewRadius - chunkMoveDistance);
/*     */       
/* 303 */       this.lastChunkX = playerChunkX;
/* 304 */       this.lastChunkZ = playerChunkZ;
/*     */ 
/*     */       
/* 307 */       this.updateTimer -= dt;
/* 308 */       if (this.updateTimer > 0.0F) {
/*     */         return;
/*     */       }
/* 311 */       if (this.sentViewRadius != chunkViewRadius) {
/* 312 */         if (this.sentViewRadius > chunkViewRadius) this.sentViewRadius = chunkViewRadius;
/*     */         
/* 314 */         unloadImages(chunkViewRadius, playerChunkX, playerChunkZ);
/*     */ 
/*     */ 
/*     */         
/* 318 */         if (this.sentViewRadius < chunkViewRadius) {
/* 319 */           loadImages(world, chunkViewRadius, playerChunkX, playerChunkZ, 20);
/*     */         }
/*     */       } else {
/* 322 */         this.updateTimer = 1.0F;
/*     */       } 
/*     */     } else {
/*     */       
/* 326 */       this.updateTimer -= dt;
/* 327 */       if (this.updateTimer > 0.0F) {
/*     */         return;
/*     */       }
/* 330 */       loadWorldMap(world, worldMapArea, 20);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void unloadImages(int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 335 */     List<MapChunk> currentUnloadList = null;
/* 336 */     List<List<MapChunk>> allUnloadLists = null;
/*     */     
/* 338 */     this.loadedLock.writeLock().lock(); try {
/*     */       ObjectArrayList<MapChunk> objectArrayList; ObjectArrayList<ObjectArrayList<MapChunk>> objectArrayList1;
/* 340 */       int packetSize = 2621427;
/*     */       
/* 342 */       LongIterator iterator = this.loaded.iterator();
/* 343 */       while (iterator.hasNext()) {
/* 344 */         long chunkCoordinates = iterator.nextLong();
/*     */         
/* 346 */         int mapChunkX = ChunkUtil.xOfChunkIndex(chunkCoordinates);
/* 347 */         int mapChunkZ = ChunkUtil.zOfChunkIndex(chunkCoordinates);
/*     */         
/* 349 */         if (shouldBeVisible(chunkViewRadius, playerChunkX, playerChunkZ, mapChunkX, mapChunkZ)) {
/*     */           continue;
/*     */         }
/*     */         
/* 353 */         if (currentUnloadList == null) {
/* 354 */           objectArrayList = new ObjectArrayList(packetSize / 10);
/*     */         }
/* 356 */         objectArrayList.add(new MapChunk(mapChunkX, mapChunkZ, null));
/* 357 */         packetSize -= 10;
/* 358 */         iterator.remove();
/*     */ 
/*     */         
/* 361 */         if (packetSize < 10) {
/* 362 */           packetSize = 2621427;
/*     */           
/* 364 */           if (allUnloadLists == null) {
/* 365 */             objectArrayList1 = new ObjectArrayList(this.loaded.size() / packetSize / 10);
/*     */           }
/* 367 */           objectArrayList1.add(objectArrayList);
/*     */           
/* 369 */           objectArrayList = new ObjectArrayList(packetSize / 10);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 374 */       if (objectArrayList1 != null) {
/* 375 */         for (List<MapChunk> unloadList : objectArrayList1) {
/* 376 */           writeUpdatePacket(unloadList);
/*     */         }
/*     */       }
/* 379 */       writeUpdatePacket((List<MapChunk>)objectArrayList);
/*     */     } finally {
/* 381 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void processPendingReloadChunks(@Nonnull World world) {
/* 386 */     List<MapChunk> chunksToSend = null;
/*     */     
/* 388 */     this.loadedLock.writeLock().lock(); try {
/*     */       ObjectArrayList<MapChunk> objectArrayList;
/* 390 */       if (this.pendingReloadChunks.isEmpty())
/*     */         return; 
/* 392 */       int imageSize = MathUtil.fastFloor(32.0F * world.getWorldMapManager().getWorldMapSettings().getImageScale());
/* 393 */       int fullMapChunkSize = 23 + 4 * imageSize * imageSize;
/* 394 */       int packetSize = 2621427;
/*     */       
/* 396 */       LongIterator iterator = this.pendingReloadChunks.iterator();
/* 397 */       while (iterator.hasNext()) {
/* 398 */         long chunkCoordinates = iterator.nextLong();
/*     */ 
/*     */         
/* 401 */         CompletableFuture<MapImage> future = (CompletableFuture<MapImage>)this.pendingReloadFutures.get(chunkCoordinates);
/* 402 */         if (future == null) {
/* 403 */           future = world.getWorldMapManager().getImageAsync(chunkCoordinates);
/* 404 */           this.pendingReloadFutures.put(chunkCoordinates, future);
/*     */         } 
/*     */         
/* 407 */         if (!future.isDone()) {
/*     */           continue;
/*     */         }
/*     */         
/* 411 */         iterator.remove();
/* 412 */         this.pendingReloadFutures.remove(chunkCoordinates);
/*     */         
/* 414 */         if (chunksToSend == null) {
/* 415 */           objectArrayList = new ObjectArrayList(packetSize / fullMapChunkSize);
/*     */         }
/*     */         
/* 418 */         int mapChunkX = ChunkUtil.xOfChunkIndex(chunkCoordinates);
/* 419 */         int mapChunkZ = ChunkUtil.zOfChunkIndex(chunkCoordinates);
/* 420 */         objectArrayList.add(new MapChunk(mapChunkX, mapChunkZ, future.getNow(null)));
/* 421 */         this.loaded.add(chunkCoordinates);
/* 422 */         packetSize -= fullMapChunkSize;
/*     */         
/* 424 */         if (packetSize < fullMapChunkSize) {
/* 425 */           writeUpdatePacket((List<MapChunk>)objectArrayList);
/* 426 */           objectArrayList = new ObjectArrayList(2621440 - 13 / fullMapChunkSize);
/* 427 */           packetSize = 2621427;
/*     */         } 
/*     */       } 
/*     */       
/* 431 */       writeUpdatePacket((List<MapChunk>)objectArrayList);
/*     */     } finally {
/* 433 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private int loadImages(@Nonnull World world, int chunkViewRadius, int playerChunkX, int playerChunkZ, int maxGeneration) {
/* 438 */     List<MapChunk> currentLoadList = null;
/* 439 */     List<List<MapChunk>> allLoadLists = null;
/*     */     
/* 441 */     this.loadedLock.writeLock().lock(); try {
/*     */       ObjectArrayList<MapChunk> objectArrayList; ObjectArrayList<ObjectArrayList<MapChunk>> objectArrayList1;
/* 443 */       int packetSize = 2621427;
/* 444 */       int imageSize = MathUtil.fastFloor(32.0F * world.getWorldMapManager().getWorldMapSettings().getImageScale());
/* 445 */       int fullMapChunkSize = 23 + 4 * imageSize * imageSize;
/*     */       
/* 447 */       boolean areAllLoaded = true;
/*     */       
/* 449 */       this.spiralIterator.init(playerChunkX, playerChunkZ, this.sentViewRadius, chunkViewRadius);
/* 450 */       while (maxGeneration > 0 && this.spiralIterator.hasNext()) {
/* 451 */         long chunkCoordinates = this.spiralIterator.next();
/* 452 */         if (!this.loaded.contains(chunkCoordinates)) {
/* 453 */           areAllLoaded = false;
/*     */ 
/*     */           
/* 456 */           CompletableFuture<MapImage> future = world.getWorldMapManager().getImageAsync(chunkCoordinates);
/* 457 */           if (!future.isDone()) {
/* 458 */             maxGeneration--;
/*     */             
/*     */             continue;
/*     */           } 
/* 462 */           if (this.loaded.add(chunkCoordinates)) {
/*     */ 
/*     */             
/* 465 */             if (currentLoadList == null) {
/* 466 */               objectArrayList = new ObjectArrayList(packetSize / fullMapChunkSize);
/*     */             }
/*     */             
/* 469 */             int mapChunkX = ChunkUtil.xOfChunkIndex(chunkCoordinates);
/* 470 */             int mapChunkZ = ChunkUtil.zOfChunkIndex(chunkCoordinates);
/* 471 */             objectArrayList.add(new MapChunk(mapChunkX, mapChunkZ, future.getNow(null)));
/* 472 */             packetSize -= fullMapChunkSize;
/*     */ 
/*     */             
/* 475 */             if (packetSize < fullMapChunkSize) {
/* 476 */               packetSize = 2621427;
/*     */               
/* 478 */               if (allLoadLists == null) {
/* 479 */                 objectArrayList1 = new ObjectArrayList();
/*     */               }
/* 481 */               objectArrayList1.add(objectArrayList);
/*     */               
/* 483 */               objectArrayList = new ObjectArrayList(packetSize / fullMapChunkSize);
/*     */             } 
/*     */           }  continue;
/* 486 */         }  if (areAllLoaded) {
/* 487 */           this.sentViewRadius = this.spiralIterator.getCompletedRadius();
/*     */         }
/*     */       } 
/* 490 */       if (areAllLoaded) {
/* 491 */         this.sentViewRadius = this.spiralIterator.getCompletedRadius();
/*     */       }
/*     */ 
/*     */       
/* 495 */       if (objectArrayList1 != null) {
/* 496 */         for (List<MapChunk> unloadList : objectArrayList1) {
/* 497 */           writeUpdatePacket(unloadList);
/*     */         }
/*     */       }
/* 500 */       writeUpdatePacket((List<MapChunk>)objectArrayList);
/*     */     } finally {
/* 502 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/* 504 */     return maxGeneration;
/*     */   }
/*     */   private int loadWorldMap(@Nonnull World world, @Nonnull Box2D worldMapArea, int maxGeneration) {
/*     */     ObjectArrayList<MapChunk> objectArrayList;
/*     */     ObjectArrayList<ObjectArrayList<MapChunk>> objectArrayList1;
/* 509 */     List<MapChunk> currentLoadList = null;
/* 510 */     List<List<MapChunk>> allLoadLists = null;
/*     */     
/* 512 */     this.loadedLock.writeLock().lock();
/*     */     
/* 514 */     try { int packetSize = 2621427;
/* 515 */       int imageSize = MathUtil.fastFloor(32.0F * world.getWorldMapManager().getWorldMapSettings().getImageScale());
/* 516 */       int fullMapChunkSize = 23 + 4 * imageSize * imageSize;
/*     */       
/* 518 */       for (int mapChunkX = MathUtil.floor(worldMapArea.min.x); mapChunkX < MathUtil.ceil(worldMapArea.max.x) && maxGeneration > 0; mapChunkX++) {
/* 519 */         for (int mapChunkZ = MathUtil.floor(worldMapArea.min.y); mapChunkZ < MathUtil.ceil(worldMapArea.max.y) && maxGeneration > 0; mapChunkZ++) {
/* 520 */           long chunkCoordinates = ChunkUtil.indexChunk(mapChunkX, mapChunkZ);
/* 521 */           if (!this.loaded.contains(chunkCoordinates)) {
/*     */             
/* 523 */             CompletableFuture<MapImage> future = CompletableFutureUtil._catch(world.getWorldMapManager().getImageAsync(chunkCoordinates));
/* 524 */             if (!future.isDone()) {
/* 525 */               maxGeneration--;
/*     */             
/*     */             }
/*     */             else {
/*     */ 
/*     */               
/* 531 */               if (currentLoadList == null) {
/* 532 */                 objectArrayList = new ObjectArrayList(packetSize / fullMapChunkSize);
/*     */               }
/* 534 */               objectArrayList.add(new MapChunk(mapChunkX, mapChunkZ, future.getNow(null)));
/* 535 */               this.loaded.add(chunkCoordinates);
/* 536 */               packetSize -= fullMapChunkSize;
/*     */ 
/*     */               
/* 539 */               if (packetSize < fullMapChunkSize) {
/* 540 */                 packetSize = 2621427;
/*     */                 
/* 542 */                 if (allLoadLists == null) {
/* 543 */                   objectArrayList1 = new ObjectArrayList(Math.max(packetSize / fullMapChunkSize, 1));
/*     */                 }
/* 545 */                 objectArrayList1.add(objectArrayList);
/*     */                 
/* 547 */                 objectArrayList = new ObjectArrayList(packetSize / fullMapChunkSize);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }  }
/* 553 */     finally { this.loadedLock.writeLock().unlock(); }
/*     */ 
/*     */     
/* 556 */     if (objectArrayList1 != null) {
/* 557 */       for (List<MapChunk> unloadList : objectArrayList1) {
/* 558 */         writeUpdatePacket(unloadList);
/*     */       }
/*     */     }
/* 561 */     writeUpdatePacket((List<MapChunk>)objectArrayList);
/*     */     
/* 563 */     return maxGeneration;
/*     */   }
/*     */   
/*     */   private void writeUpdatePacket(@Nullable List<MapChunk> list) {
/* 567 */     if (list != null) {
/* 568 */       UpdateWorldMap packet = new UpdateWorldMap((MapChunk[])list.toArray(x$0 -> new MapChunk[x$0]), null, null);
/* 569 */       LOGGER.at(Level.FINE).log("Sending world map update to %s - %d chunks", this.player.getUuid(), list.size());
/* 570 */       this.player.getPlayerConnection().write((Packet)packet);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Map<String, MapMarker> getSentMarkers() {
/* 576 */     return this.markerTracker.getSentMarkers();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Player getPlayer() {
/* 581 */     return this.player;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public TransformComponent getTransformComponent() {
/* 586 */     return this.transformComponent;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 590 */     this.loadedLock.writeLock().lock();
/*     */     try {
/* 592 */       this.loaded.clear();
/* 593 */       this.sentViewRadius = 0;
/* 594 */       this.markerTracker.getSentMarkers().clear();
/*     */     } finally {
/* 596 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/* 598 */     this.player.getPlayerConnection().write((Packet)new ClearWorldMap());
/*     */   }
/*     */   
/*     */   public void clearChunks(@Nonnull LongSet chunkIndices) {
/* 602 */     this.loadedLock.writeLock().lock();
/*     */     try {
/* 604 */       chunkIndices.forEach(index -> {
/*     */             this.loaded.remove(index);
/*     */             
/*     */             this.pendingReloadChunks.add(index);
/*     */             this.pendingReloadFutures.remove(index);
/*     */           });
/*     */     } finally {
/* 611 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/* 613 */     this.updateTimer = 0.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void sendSettings(@Nonnull World world) {
/* 618 */     UpdateWorldMapSettings worldMapSettingsPacket = new UpdateWorldMapSettings(world.getWorldMapManager().getWorldMapSettings().getSettingsPacket());
/*     */     
/* 620 */     world.execute(() -> {
/*     */           Store<EntityStore> store = world.getEntityStore().getStore();
/*     */           Ref<EntityStore> ref = this.player.getReference();
/*     */           if (ref == null) {
/*     */             return;
/*     */           }
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */           assert playerRefComponent != null;
/*     */           worldMapSettingsPacket.allowTeleportToCoordinates = isAllowTeleportToCoordinates();
/*     */           worldMapSettingsPacket.allowTeleportToMarkers = isAllowTeleportToMarkers();
/*     */           playerRefComponent.getPacketHandler().write((Packet)worldMapSettingsPacket);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean hasDiscoveredZone(@Nonnull String zoneName) {
/* 646 */     return this.player.getPlayerConfigData().getDiscoveredZones().contains(zoneName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean discoverZone(@Nonnull World world, @Nonnull String zoneName) {
/* 657 */     Set<String> discoveredZones = this.player.getPlayerConfigData().getDiscoveredZones();
/* 658 */     if (!discoveredZones.contains(zoneName)) {
/* 659 */       discoveredZones = new HashSet<>(discoveredZones);
/* 660 */       discoveredZones.add(zoneName);
/* 661 */       this.player.getPlayerConfigData().setDiscoveredZones(discoveredZones);
/* 662 */       sendSettings(world);
/*     */       
/* 664 */       return true;
/*     */     } 
/* 666 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean undiscoverZone(@Nonnull World world, @Nonnull String zoneName) {
/* 677 */     Set<String> discoveredZones = this.player.getPlayerConfigData().getDiscoveredZones();
/* 678 */     if (discoveredZones.contains(zoneName)) {
/* 679 */       discoveredZones = new HashSet<>(discoveredZones);
/* 680 */       discoveredZones.remove(zoneName);
/* 681 */       this.player.getPlayerConfigData().setDiscoveredZones(discoveredZones);
/* 682 */       sendSettings(world);
/*     */       
/* 684 */       return true;
/*     */     } 
/* 686 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean discoverZones(@Nonnull World world, @Nonnull Set<String> zoneNames) {
/* 697 */     Set<String> discoveredZones = this.player.getPlayerConfigData().getDiscoveredZones();
/* 698 */     if (!discoveredZones.containsAll(zoneNames)) {
/* 699 */       discoveredZones = new HashSet<>(discoveredZones);
/* 700 */       discoveredZones.addAll(zoneNames);
/* 701 */       this.player.getPlayerConfigData().setDiscoveredZones(discoveredZones);
/* 702 */       sendSettings(world);
/*     */       
/* 704 */       return true;
/*     */     } 
/* 706 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean undiscoverZones(@Nonnull World world, @Nonnull Set<String> zoneNames) {
/* 717 */     Set<String> discoveredZones = this.player.getPlayerConfigData().getDiscoveredZones();
/* 718 */     if (discoveredZones.containsAll(zoneNames)) {
/* 719 */       discoveredZones = new HashSet<>(discoveredZones);
/* 720 */       discoveredZones.removeAll(zoneNames);
/* 721 */       this.player.getPlayerConfigData().setDiscoveredZones(discoveredZones);
/* 722 */       sendSettings(world);
/*     */       
/* 724 */       return true;
/*     */     } 
/* 726 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowTeleportToCoordinates() {
/* 733 */     return this.player.hasPermission("hytale.world_map.teleport.coordinate");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAllowTeleportToMarkers() {
/* 740 */     return this.player.hasPermission("hytale.world_map.teleport.marker");
/*     */   }
/*     */   
/*     */   public void setPlayerMapFilter(Predicate<PlayerRef> playerMapFilter) {
/* 744 */     this.markerTracker.setPlayerMapFilter(playerMapFilter);
/*     */   }
/*     */   
/*     */   public void setClientHasWorldMapVisible(boolean visible) {
/* 748 */     this.clientHasWorldMapVisible = visible;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Integer getViewRadiusOverride() {
/* 753 */     return this.viewRadiusOverride;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getCurrentBiomeName() {
/* 758 */     return this.currentBiomeName;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ZoneDiscoveryInfo getCurrentZone() {
/* 763 */     return this.currentZone;
/*     */   }
/*     */   
/*     */   public void setViewRadiusOverride(@Nullable Integer viewRadiusOverride) {
/* 767 */     this.viewRadiusOverride = viewRadiusOverride;
/* 768 */     clear();
/*     */   }
/*     */   
/*     */   public int getEffectiveViewRadius(@Nonnull World world) {
/* 772 */     if (this.viewRadiusOverride != null) {
/* 773 */       return this.viewRadiusOverride.intValue();
/*     */     }
/* 775 */     return world.getWorldMapManager().getWorldMapSettings().getViewRadius(this.player.getViewRadius());
/*     */   }
/*     */   
/*     */   public boolean shouldBeVisible(int chunkViewRadius, long chunkCoordinates) {
/* 779 */     if (this.player == null || this.transformComponent == null) return false; 
/* 780 */     Vector3d position = this.transformComponent.getPosition();
/* 781 */     int chunkX = MathUtil.floor(position.getX()) >> 5;
/* 782 */     int chunkZ = MathUtil.floor(position.getZ()) >> 5;
/*     */     
/* 784 */     int x = ChunkUtil.xOfChunkIndex(chunkCoordinates);
/* 785 */     int z = ChunkUtil.zOfChunkIndex(chunkCoordinates);
/* 786 */     return shouldBeVisible(chunkViewRadius, chunkX, chunkZ, x, z);
/*     */   }
/*     */   
/*     */   public void copyFrom(@Nonnull WorldMapTracker worldMapTracker) {
/* 790 */     this.loadedLock.writeLock().lock();
/*     */     try {
/* 792 */       worldMapTracker.loadedLock.readLock().lock();
/*     */       try {
/* 794 */         this.loaded.addAll((LongCollection)worldMapTracker.loaded);
/* 795 */         this.markerTracker.copyFrom(worldMapTracker.markerTracker);
/*     */       } finally {
/* 797 */         worldMapTracker.loadedLock.readLock().unlock();
/*     */       } 
/*     */     } finally {
/* 800 */       this.loadedLock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static boolean shouldBeVisible(int chunkViewRadius, int chunkX, int chunkZ, int x, int z) {
/* 805 */     int xDiff = Math.abs(x - chunkX);
/* 806 */     int zDiff = Math.abs(z - chunkZ);
/* 807 */     int distanceSq = xDiff * xDiff + zDiff * zDiff;
/* 808 */     return (distanceSq <= chunkViewRadius * chunkViewRadius);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\WorldMapTracker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */