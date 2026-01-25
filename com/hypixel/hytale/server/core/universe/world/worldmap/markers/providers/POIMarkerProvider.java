/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class POIMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider
/*    */ {
/* 15 */   public static final POIMarkerProvider INSTANCE = new POIMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 22 */     Map<String, MapMarker> globalMarkers = world.getWorldMapManager().getPointsOfInterest();
/* 23 */     if (globalMarkers.isEmpty())
/*    */       return; 
/* 25 */     for (MapMarker marker : globalMarkers.values())
/* 26 */       tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker); 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\POIMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */