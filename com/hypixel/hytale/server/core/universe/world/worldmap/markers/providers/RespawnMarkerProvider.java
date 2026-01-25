/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class RespawnMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider
/*    */ {
/* 17 */   public static final RespawnMarkerProvider INSTANCE = new RespawnMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 24 */     WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
/* 25 */     if (!worldMapConfig.isDisplayHome())
/*    */       return; 
/* 27 */     PlayerRespawnPointData[] respawnPoints = tracker.getPlayer().getPlayerConfigData().getPerWorldData(world.getName()).getRespawnPoints();
/* 28 */     if (respawnPoints == null)
/*    */       return; 
/* 30 */     for (int i = 0; i < respawnPoints.length; i++) {
/* 31 */       addRespawnMarker(tracker, playerChunkX, playerChunkZ, respawnPoints[i], i);
/*    */     }
/*    */   }
/*    */   
/*    */   private static void addRespawnMarker(@Nonnull MapMarkerTracker tracker, int playerChunkX, int playerChunkZ, @Nonnull PlayerRespawnPointData respawnPoint, int index) {
/* 36 */     String respawnPointName = respawnPoint.getName();
/* 37 */     Vector3i respawnPointPosition = respawnPoint.getBlockPosition();
/*    */ 
/*    */     
/* 40 */     tracker.trySendMarker(-1, playerChunkX, playerChunkZ, respawnPointPosition
/* 41 */         .toVector3d(), 0.0F, respawnPointName + respawnPointName, respawnPointName, respawnPointPosition, (id, name, rp) -> new MapMarker(id, name, "Home.png", PositionUtil.toTransformPacket(new Transform(rp)), null));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\RespawnMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */