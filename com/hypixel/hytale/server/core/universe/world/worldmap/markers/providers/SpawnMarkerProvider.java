/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
/*    */ import com.hypixel.hytale.server.core.entity.Entity;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SpawnMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider
/*    */ {
/* 22 */   public static final SpawnMarkerProvider INSTANCE = new SpawnMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 32 */     WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
/* 33 */     if (!worldMapConfig.isDisplaySpawn())
/*    */       return; 
/* 35 */     Player player = tracker.getPlayer();
/* 36 */     Transform spawnPoint = world.getWorldConfig().getSpawnProvider().getSpawnPoint((Entity)player);
/* 37 */     if (spawnPoint == null)
/*    */       return; 
/* 39 */     Vector3d spawnPosition = spawnPoint.getPosition();
/*    */ 
/*    */     
/* 42 */     tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, spawnPosition, spawnPoint
/* 43 */         .getRotation().getYaw(), "Spawn", "Spawn", spawnPosition, (id, name, pos) -> new MapMarker(id, name, "Spawn.png", PositionUtil.toTransformPacket(new Transform(pos)), null));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\SpawnMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */