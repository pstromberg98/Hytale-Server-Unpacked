/*     */ package com.hypixel.hytale.server.core.universe.world.worldmap;
/*     */ 
/*     */ import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.lookup.CodecMapCodec;
/*     */ import com.hypixel.hytale.common.util.ArrayUtil;
/*     */ import com.hypixel.hytale.common.util.CompletableFutureUtil;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.map.WorldMap;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.DeathMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.POIMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.PerWorldDataMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.PlayerIconMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.RespawnMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers.SpawnMarkerProvider;
/*     */ import com.hypixel.hytale.server.core.util.thread.TickingThread;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WorldMapManager
/*     */   extends TickingThread
/*     */ {
/*     */   private static final int IMAGE_KEEP_ALIVE = 60;
/*     */   private static final float DEFAULT_UNLOAD_DELAY = 1.0F;
/*     */   @Nonnull
/*     */   private final HytaleLogger logger;
/*     */   @Nonnull
/*     */   private final World world;
/*  60 */   private final Long2ObjectConcurrentHashMap<ImageEntry> images = new Long2ObjectConcurrentHashMap(true, ChunkUtil.indexChunk(-2147483648, -2147483648));
/*  61 */   private final Long2ObjectConcurrentHashMap<CompletableFuture<MapImage>> generating = new Long2ObjectConcurrentHashMap(true, ChunkUtil.indexChunk(-2147483648, -2147483648));
/*     */   
/*  63 */   private final Map<String, MarkerProvider> markerProviders = new ConcurrentHashMap<>();
/*  64 */   private final Map<String, MapMarker> pointsOfInterest = new ConcurrentHashMap<>();
/*     */   @Nonnull
/*  66 */   private WorldMapSettings worldMapSettings = WorldMapSettings.DISABLED;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private IWorldMap generator;
/*     */   
/*     */   @Nonnull
/*  73 */   private CompletableFuture<Void> generatorLoaded = new CompletableFuture<>();
/*     */ 
/*     */   
/*  76 */   private float unloadDelay = 1.0F;
/*     */   
/*     */   public WorldMapManager(@Nonnull World world) {
/*  79 */     super("WorldMap - " + world.getName(), 10, true);
/*  80 */     this.logger = HytaleLogger.get("World|" + world.getName() + "|M");
/*  81 */     this.world = world;
/*     */ 
/*     */     
/*  84 */     addMarkerProvider("spawn", (MarkerProvider)SpawnMarkerProvider.INSTANCE);
/*  85 */     addMarkerProvider("playerIcons", (MarkerProvider)PlayerIconMarkerProvider.INSTANCE);
/*  86 */     addMarkerProvider("death", (MarkerProvider)DeathMarkerProvider.INSTANCE);
/*  87 */     addMarkerProvider("respawn", (MarkerProvider)RespawnMarkerProvider.INSTANCE);
/*  88 */     addMarkerProvider("playerMarkers", (MarkerProvider)PerWorldDataMarkerProvider.INSTANCE);
/*  89 */     addMarkerProvider("poi", (MarkerProvider)POIMarkerProvider.INSTANCE);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public IWorldMap getGenerator() {
/*  94 */     return this.generator;
/*     */   }
/*     */   
/*     */   public void setGenerator(@Nullable IWorldMap generator) {
/*  98 */     boolean before = shouldTick();
/*     */     
/* 100 */     if (this.generator != null) {
/* 101 */       this.generator.shutdown();
/*     */     }
/*     */ 
/*     */     
/* 105 */     this.generator = generator;
/*     */     
/* 107 */     if (generator != null) {
/* 108 */       this.logger.at(Level.INFO).log("Initializing world map generator: %s", generator.toString());
/*     */ 
/*     */       
/* 111 */       this.generatorLoaded.complete(null);
/* 112 */       this.generatorLoaded = new CompletableFuture<>();
/*     */       
/* 114 */       this.worldMapSettings = generator.getWorldMapSettings();
/*     */ 
/*     */       
/* 117 */       this.images.clear();
/* 118 */       this.generating.clear();
/* 119 */       for (Player worldPlayer : this.world.getPlayers()) {
/* 120 */         worldPlayer.getWorldMapTracker().clear();
/*     */       }
/*     */       
/* 123 */       updateTickingState(before);
/*     */       
/* 125 */       sendSettings();
/*     */       
/* 127 */       this.logger.at(Level.INFO).log("Generating Points of Interest...");
/* 128 */       CompletableFutureUtil._catch(generator.generatePointsOfInterest(this.world)
/* 129 */           .thenAcceptAsync(pointsOfInterest -> {
/*     */               this.pointsOfInterest.putAll(pointsOfInterest);
/*     */               this.logger.at(Level.INFO).log("Finished Generating Points of Interest!");
/*     */             }));
/*     */     } else {
/* 134 */       this.logger.at(Level.INFO).log("World map disabled!");
/* 135 */       this.worldMapSettings = WorldMapSettings.DISABLED;
/* 136 */       sendSettings();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean isIdle() {
/* 142 */     return (this.world.getPlayerCount() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick(float dt) {
/* 147 */     for (Player player : this.world.getPlayers()) {
/* 148 */       player.getWorldMapTracker().tick(dt);
/*     */     }
/*     */     
/* 151 */     this.unloadDelay -= dt;
/* 152 */     if (this.unloadDelay <= 0.0F) {
/* 153 */       this.unloadDelay = 1.0F;
/* 154 */       unloadImages();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onShutdown() {}
/*     */ 
/*     */   
/*     */   public void unloadImages() {
/* 163 */     int imagesCount = this.images.size();
/* 164 */     if (imagesCount == 0)
/*     */       return; 
/* 166 */     List<Player> players = this.world.getPlayers();
/*     */     
/* 168 */     LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
/* 169 */     this.images.forEach((index, chunk) -> {
/*     */           if (isWorldMapEnabled() && isWorldMapImageVisibleToAnyPlayer(players, index, this.worldMapSettings)) {
/*     */             chunk.keepAlive.set(60);
/*     */             
/*     */             return;
/*     */           } 
/*     */           if (chunk.keepAlive.decrementAndGet() <= 0) {
/*     */             toRemove.add(index);
/*     */           }
/*     */         });
/* 179 */     if (!longOpenHashSet.isEmpty()) {
/* 180 */       longOpenHashSet.forEach(value -> {
/*     */             this.logger.at(Level.FINE).log("Unloading world map image: %s", value);
/*     */             
/*     */             this.images.remove(value);
/*     */           });
/*     */     }
/*     */     
/* 187 */     int toRemoveSize = longOpenHashSet.size();
/* 188 */     if (toRemoveSize > 0) {
/* 189 */       this.logger.at(Level.FINE).log("Cleaned %s world map images from memory, with %s images remaining in memory.", toRemoveSize, imagesCount - toRemoveSize);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isWorldMapEnabled() {
/* 194 */     return (this.worldMapSettings.getSettingsPacket()).enabled;
/*     */   }
/*     */   
/*     */   public static boolean isWorldMapImageVisibleToAnyPlayer(@Nonnull List<Player> players, long imageIndex, @Nonnull WorldMapSettings settings) {
/* 198 */     for (Player player : players) {
/* 199 */       int viewRadius = settings.getViewRadius(player.getViewRadius());
/* 200 */       if (player.getWorldMapTracker().shouldBeVisible(viewRadius, imageIndex)) return true; 
/*     */     } 
/* 202 */     return false;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public World getWorld() {
/* 207 */     return this.world;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public WorldMapSettings getWorldMapSettings() {
/* 212 */     return this.worldMapSettings;
/*     */   }
/*     */   
/*     */   public Map<String, MarkerProvider> getMarkerProviders() {
/* 216 */     return this.markerProviders;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addMarkerProvider(@Nonnull String key, @Nonnull MarkerProvider provider) {
/* 221 */     this.markerProviders.put(key, provider);
/*     */   }
/*     */   
/*     */   public Map<String, MapMarker> getPointsOfInterest() {
/* 225 */     return this.pointsOfInterest;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MapImage getImageIfInMemory(int x, int z) {
/* 230 */     return getImageIfInMemory(ChunkUtil.indexChunk(x, z));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MapImage getImageIfInMemory(long index) {
/* 235 */     ImageEntry pair = (ImageEntry)this.images.get(index);
/* 236 */     return (pair != null) ? pair.image : null;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<MapImage> getImageAsync(int x, int z) {
/* 241 */     return getImageAsync(ChunkUtil.indexChunk(x, z));
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<MapImage> getImageAsync(long index) {
/* 246 */     ImageEntry pair = (ImageEntry)this.images.get(index);
/* 247 */     MapImage image = (pair != null) ? pair.image : null;
/* 248 */     if (image != null) return CompletableFuture.completedFuture(image);
/*     */     
/* 250 */     CompletableFuture<MapImage> gen = (CompletableFuture<MapImage>)this.generating.get(index);
/* 251 */     if (gen != null) return gen;
/*     */     
/* 253 */     int imageSize = MathUtil.fastFloor(32.0F * this.worldMapSettings.getImageScale());
/*     */     
/* 255 */     LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
/* 256 */     longOpenHashSet.add(index);
/* 257 */     CompletableFuture<MapImage> future = CompletableFutureUtil._catch(this.generator
/* 258 */         .generate(this.world, imageSize, imageSize, (LongSet)longOpenHashSet)
/*     */ 
/*     */         
/* 261 */         .thenApplyAsync(worldMap -> {
/*     */             MapImage newImage = (MapImage)worldMap.getChunks().get(index);
/*     */             
/*     */             if (this.generating.remove(index) != null) {
/*     */               this.images.put(index, new ImageEntry(newImage));
/*     */             }
/*     */             
/*     */             return newImage;
/*     */           }));
/* 270 */     this.generating.put(index, future);
/* 271 */     return future;
/*     */   }
/*     */ 
/*     */   
/*     */   public void generate() {}
/*     */   
/*     */   public void sendSettings() {
/* 278 */     for (Player player : this.world.getPlayers()) {
/* 279 */       player.getWorldMapTracker().sendSettings(this.world);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean shouldTick() {
/* 284 */     return (this.world.getWorldConfig().isCompassUpdating() || isWorldMapEnabled());
/*     */   }
/*     */   
/*     */   public void updateTickingState(boolean before) {
/* 288 */     boolean after = shouldTick();
/* 289 */     if (before != after) {
/* 290 */       if (after) {
/* 291 */         start();
/*     */       } else {
/* 293 */         stop();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static {
/* 299 */     MarkerReference.CODEC.register("Player", PlayerMarkerReference.class, (Codec)PlayerMarkerReference.CODEC);
/*     */   }
/*     */   
/*     */   public void clearImages() {
/* 303 */     this.images.clear();
/* 304 */     this.generating.clear();
/*     */   }
/*     */   
/*     */   public void clearImagesInChunks(@Nonnull LongSet chunkIndices) {
/* 308 */     chunkIndices.forEach(index -> {
/*     */           this.images.remove(index);
/*     */           this.generating.remove(index);
/*     */         });
/*     */   }
/*     */   
/*     */   public static interface MarkerReference {
/* 315 */     public static final CodecMapCodec<MarkerReference> CODEC = new CodecMapCodec();
/*     */ 
/*     */ 
/*     */     
/*     */     String getMarkerId();
/*     */ 
/*     */     
/*     */     void remove();
/*     */   }
/*     */ 
/*     */   
/*     */   public static class PlayerMarkerReference
/*     */     implements MarkerReference
/*     */   {
/*     */     public static final BuilderCodec<PlayerMarkerReference> CODEC;
/*     */     
/*     */     private UUID player;
/*     */     
/*     */     private String world;
/*     */     
/*     */     private String markerId;
/*     */ 
/*     */     
/*     */     static {
/* 339 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerMarkerReference.class, PlayerMarkerReference::new).addField(new KeyedCodec("Player", (Codec)Codec.UUID_BINARY), (playerMarkerReference, uuid) -> playerMarkerReference.player = uuid, playerMarkerReference -> playerMarkerReference.player)).addField(new KeyedCodec("World", (Codec)Codec.STRING), (playerMarkerReference, s) -> playerMarkerReference.world = s, playerMarkerReference -> playerMarkerReference.world)).addField(new KeyedCodec("MarkerId", (Codec)Codec.STRING), (playerMarkerReference, s) -> playerMarkerReference.markerId = s, playerMarkerReference -> playerMarkerReference.markerId)).build();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private PlayerMarkerReference() {}
/*     */ 
/*     */ 
/*     */     
/*     */     public PlayerMarkerReference(@Nonnull UUID player, @Nonnull String world, @Nonnull String markerId) {
/* 349 */       this.player = player;
/* 350 */       this.world = world;
/* 351 */       this.markerId = markerId;
/*     */     }
/*     */     
/*     */     public UUID getPlayer() {
/* 355 */       return this.player;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getMarkerId() {
/* 360 */       return this.markerId;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 365 */       PlayerRef playerRef = Universe.get().getPlayer(this.player);
/* 366 */       if (playerRef != null) {
/*     */         
/* 368 */         Player playerComponent = (Player)playerRef.getComponent(Player.getComponentType());
/* 369 */         removeMarkerFromOnlinePlayer(playerComponent);
/*     */       } else {
/* 371 */         removeMarkerFromOfflinePlayer();
/*     */       } 
/*     */     }
/*     */     
/*     */     private void removeMarkerFromOnlinePlayer(@Nonnull Player player) {
/* 376 */       PlayerConfigData data = player.getPlayerConfigData();
/*     */ 
/*     */ 
/*     */       
/* 380 */       String world = this.world;
/* 381 */       if (world == null) world = player.getWorld().getName();
/*     */       
/* 383 */       removeMarkerFromData(data, world, this.markerId);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void removeMarkerFromOfflinePlayer() {
/* 390 */       Universe.get().getPlayerStorage().load(this.player)
/* 391 */         .thenApply(holder -> {
/*     */             Player player = (Player)holder.getComponent(Player.getComponentType());
/*     */             
/*     */             PlayerConfigData data = player.getPlayerConfigData();
/*     */             
/*     */             String world = this.world;
/*     */             
/*     */             if (world == null) {
/*     */               world = data.getWorld();
/*     */             }
/*     */             removeMarkerFromData(data, world, this.markerId);
/*     */             return holder;
/* 403 */           }).thenCompose(holder -> Universe.get().getPlayerStorage().save(this.player, holder));
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private static MapMarker removeMarkerFromData(@Nonnull PlayerConfigData data, @Nonnull String worldName, @Nonnull String markerId) {
/* 408 */       PlayerWorldData perWorldData = data.getPerWorldData(worldName);
/* 409 */       MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
/* 410 */       if (worldMapMarkers == null) return null;
/*     */       
/* 412 */       int index = -1;
/* 413 */       for (int i = 0; i < worldMapMarkers.length; i++) {
/* 414 */         if ((worldMapMarkers[i]).id.equals(markerId)) {
/* 415 */           index = i;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 420 */       if (index == -1) return null;
/*     */       
/* 422 */       MapMarker[] newWorldMapMarkers = new MapMarker[worldMapMarkers.length - 1];
/* 423 */       System.arraycopy(worldMapMarkers, 0, newWorldMapMarkers, 0, index);
/* 424 */       System.arraycopy(worldMapMarkers, index + 1, newWorldMapMarkers, index, newWorldMapMarkers.length - index);
/* 425 */       perWorldData.setWorldMapMarkers(newWorldMapMarkers);
/*     */       
/* 427 */       return worldMapMarkers[index];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static PlayerMarkerReference createPlayerMarker(@Nonnull Ref<EntityStore> playerRef, @Nonnull MapMarker marker, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 435 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*     */     
/* 437 */     Player playerComponent = (Player)componentAccessor.getComponent(playerRef, Player.getComponentType());
/* 438 */     assert playerComponent != null;
/*     */     
/* 440 */     UUIDComponent uuidComponent = (UUIDComponent)componentAccessor.getComponent(playerRef, UUIDComponent.getComponentType());
/* 441 */     assert uuidComponent != null;
/*     */     
/* 443 */     PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
/* 444 */     MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
/*     */     
/* 446 */     perWorldData.setWorldMapMarkers((MapMarker[])ArrayUtil.append((Object[])worldMapMarkers, marker));
/*     */     
/* 448 */     return new PlayerMarkerReference(uuidComponent.getUuid(), world.getName(), marker.id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class ImageEntry
/*     */   {
/* 459 */     private final AtomicInteger keepAlive = new AtomicInteger();
/*     */     private final MapImage image;
/*     */     
/*     */     public ImageEntry(MapImage image) {
/* 463 */       this.image = image;
/*     */     }
/*     */   }
/*     */   
/*     */   public static interface MarkerProvider {
/*     */     void update(World param1World, MapMarkerTracker param1MapMarkerTracker, int param1Int1, int param1Int2, int param1Int3);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\WorldMapManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */