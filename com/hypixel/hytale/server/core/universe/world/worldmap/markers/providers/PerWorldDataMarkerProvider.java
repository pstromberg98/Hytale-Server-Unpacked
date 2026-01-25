/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class PerWorldDataMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider
/*    */ {
/* 14 */   public static final PerWorldDataMarkerProvider INSTANCE = new PerWorldDataMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 21 */     Player player = tracker.getPlayer();
/* 22 */     PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
/* 23 */     MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
/*    */     
/* 25 */     if (worldMapMarkers == null)
/*    */       return; 
/* 27 */     for (MapMarker marker : worldMapMarkers)
/* 28 */       tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\PerWorldDataMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */