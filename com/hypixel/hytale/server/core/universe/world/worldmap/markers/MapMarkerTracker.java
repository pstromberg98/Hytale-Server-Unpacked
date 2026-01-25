/*     */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers;
/*     */ 
/*     */ import com.hypixel.hytale.function.function.TriFunction;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector2d;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.Position;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMap;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Predicate;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ public class MapMarkerTracker
/*     */ {
/*     */   private final WorldMapTracker worldMapTracker;
/*     */   private final Player player;
/*  28 */   private final Map<String, MapMarker> sentToClientById = new ConcurrentHashMap<>();
/*     */   
/*     */   public static final float SMALL_MOVEMENTS_UPDATE_INTERVAL = 10.0F;
/*     */   
/*     */   private float smallMovementsTimer;
/*     */   
/*     */   private Predicate<PlayerRef> playerMapFilter;
/*     */   
/*     */   @Nonnull
/*  37 */   private final Set<String> tempToRemove = new HashSet<>();
/*     */   @Nonnull
/*  39 */   private final Set<MapMarker> tempToAdd = new HashSet<>();
/*     */   @Nonnull
/*  41 */   private final Set<String> tempTestedMarkers = new HashSet<>();
/*     */ 
/*     */   
/*     */   public MapMarkerTracker(WorldMapTracker worldMapTracker) {
/*  45 */     this.worldMapTracker = worldMapTracker;
/*  46 */     this.player = worldMapTracker.getPlayer();
/*     */   }
/*     */   
/*     */   public Player getPlayer() {
/*  50 */     return this.player;
/*     */   }
/*     */   
/*     */   public Map<String, MapMarker> getSentMarkers() {
/*  54 */     return this.sentToClientById;
/*     */   }
/*     */   
/*     */   public Predicate<PlayerRef> getPlayerMapFilter() {
/*  58 */     return this.playerMapFilter;
/*     */   }
/*     */   
/*     */   public void setPlayerMapFilter(Predicate<PlayerRef> playerMapFilter) {
/*  62 */     this.playerMapFilter = playerMapFilter;
/*     */   }
/*     */   
/*     */   private boolean isSendingSmallMovements() {
/*  66 */     return (this.smallMovementsTimer <= 0.0F);
/*     */   }
/*     */   
/*     */   private void resetSmallMovementTimer() {
/*  70 */     this.smallMovementsTimer = 10.0F;
/*     */   }
/*     */ 
/*     */   
/*     */   public void updatePointsOfInterest(float dt, @Nonnull World world, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/*  75 */     if (this.worldMapTracker.getTransformComponent() == null)
/*     */       return; 
/*  77 */     this.smallMovementsTimer -= dt;
/*     */     
/*  79 */     WorldMapManager worldMapManager = world.getWorldMapManager();
/*  80 */     Map<String, WorldMapManager.MarkerProvider> markerProviders = worldMapManager.getMarkerProviders();
/*     */     
/*  82 */     this.tempToAdd.clear();
/*  83 */     this.tempTestedMarkers.clear();
/*     */     
/*  85 */     for (WorldMapManager.MarkerProvider provider : markerProviders.values()) {
/*  86 */       provider.update(world, this, chunkViewRadius, playerChunkX, playerChunkZ);
/*     */     }
/*     */     
/*  89 */     if (isSendingSmallMovements()) {
/*  90 */       resetSmallMovementTimer();
/*     */     }
/*     */ 
/*     */     
/*  94 */     this.tempToRemove.clear();
/*  95 */     this.tempToRemove.addAll(this.sentToClientById.keySet());
/*     */     
/*  97 */     if (!this.tempTestedMarkers.isEmpty()) {
/*  98 */       this.tempToRemove.removeAll(this.tempTestedMarkers);
/*     */     }
/*     */     
/* 101 */     for (String removedMarkerId : this.tempToRemove) {
/* 102 */       this.sentToClientById.remove(removedMarkerId);
/*     */     }
/*     */     
/* 105 */     if (!this.tempToAdd.isEmpty() || !this.tempToRemove.isEmpty()) {
/* 106 */       MapMarker[] addedMarkers = !this.tempToAdd.isEmpty() ? (MapMarker[])this.tempToAdd.toArray(x$0 -> new MapMarker[x$0]) : null;
/* 107 */       String[] removedMarkers = !this.tempToRemove.isEmpty() ? (String[])this.tempToRemove.toArray(x$0 -> new String[x$0]) : null;
/*     */       
/* 109 */       this.player.getPlayerConnection().writeNoCache((Packet)new UpdateWorldMap(null, addedMarkers, removedMarkers));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, @Nonnull MapMarker marker) {
/* 120 */     trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker.transform.position.x, marker.transform.position.z, marker.transform.orientation.yaw, marker.id, marker.name, marker, (id, name, m) -> m);
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
/*     */   public <T> void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, @Nonnull Vector3d markerPos, float markerYaw, @Nonnull String markerId, @Nonnull String markerDisplayName, @Nonnull T param, @Nonnull TriFunction<String, String, T, MapMarker> markerSupplier) {
/* 137 */     trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, markerPos.x, markerPos.z, markerYaw, markerId, markerDisplayName, param, markerSupplier);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T> void trySendMarker(int chunkViewRadius, int playerChunkX, int playerChunkZ, double markerX, double markerZ, float markerYaw, @Nonnull String markerId, @Nonnull String markerName, @Nonnull T param, @Nonnull TriFunction<String, String, T, MapMarker> markerSupplier) {
/* 147 */     boolean shouldBeVisible = (chunkViewRadius == -1 || WorldMapTracker.shouldBeVisible(chunkViewRadius, 
/*     */         
/* 149 */         MathUtil.floor(markerX) >> 5, 
/* 150 */         MathUtil.floor(markerZ) >> 5, playerChunkX, playerChunkZ));
/*     */ 
/*     */     
/* 153 */     if (!shouldBeVisible)
/*     */       return; 
/* 155 */     this.tempTestedMarkers.add(markerId);
/*     */     
/* 157 */     boolean needsUpdate = false;
/* 158 */     MapMarker oldMarker = this.sentToClientById.get(markerId);
/* 159 */     if (oldMarker != null) {
/* 160 */       if (!markerName.equals(oldMarker.name)) needsUpdate = true;
/*     */       
/* 162 */       if (!needsUpdate) {
/* 163 */         double distance = Math.abs(oldMarker.transform.orientation.yaw - markerYaw);
/* 164 */         needsUpdate = (distance > 0.05D || (isSendingSmallMovements() && distance > 0.001D));
/*     */       } 
/*     */       
/* 167 */       if (!needsUpdate) {
/* 168 */         Position oldPosition = oldMarker.transform.position;
/* 169 */         double distance = Vector2d.distance(oldPosition.x, oldPosition.z, markerX, markerZ);
/* 170 */         needsUpdate = (distance > 5.0D || (isSendingSmallMovements() && distance > 0.1D));
/*     */       } 
/*     */     } else {
/* 173 */       needsUpdate = true;
/*     */     } 
/*     */     
/* 176 */     if (!needsUpdate)
/*     */       return; 
/* 178 */     MapMarker marker = (MapMarker)markerSupplier.apply(markerId, markerName, param);
/* 179 */     this.sentToClientById.put(markerId, marker);
/* 180 */     this.tempToAdd.add(marker);
/*     */   }
/*     */   
/*     */   public void copyFrom(@Nonnull MapMarkerTracker other) {
/* 184 */     for (Map.Entry<String, MapMarker> entry : other.sentToClientById.entrySet())
/* 185 */       this.sentToClientById.put(entry.getKey(), new MapMarker(entry.getValue())); 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\MapMarkerTracker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */