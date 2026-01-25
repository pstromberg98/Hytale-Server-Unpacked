/*    */ package com.hypixel.hytale.builtin.portals.integrations;
/*    */ 
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*    */ import com.hypixel.hytale.server.core.universe.world.spawn.IndividualSpawnProvider;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*    */ 
/*    */ public class PortalMarkerProvider implements WorldMapManager.MarkerProvider {
/* 13 */   public static final PortalMarkerProvider INSTANCE = new PortalMarkerProvider();
/*    */   
/*    */   public void update(World world, MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/*    */     IndividualSpawnProvider individualSpawnProvider;
/* 17 */     ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
/* 18 */     if (spawnProvider instanceof IndividualSpawnProvider) { individualSpawnProvider = (IndividualSpawnProvider)spawnProvider; }
/*    */     else { return; }
/* 20 */      Transform spawnPoint = individualSpawnProvider.getFirstSpawnPoint();
/* 21 */     if (spawnPoint == null)
/*    */       return; 
/* 23 */     tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, spawnPoint
/* 24 */         .getPosition(), spawnPoint.getRotation().getYaw(), "Portal", "Fragment Exit", spawnPoint, (id, name, sp) -> new MapMarker(id, name, "Portal.png", PositionUtil.toTransformPacket(sp), null));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\portals\integrations\PortalMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */