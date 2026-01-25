/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerDeathPositionData;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class DeathMarkerProvider implements WorldMapManager.MarkerProvider {
/* 17 */   public static final DeathMarkerProvider INSTANCE = new DeathMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 24 */     WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
/* 25 */     if (!worldMapConfig.isDisplayDeathMarker())
/*    */       return; 
/* 27 */     Player player = tracker.getPlayer();
/* 28 */     PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
/* 29 */     List<PlayerDeathPositionData> deathPositions = perWorldData.getDeathPositions();
/*    */     
/* 31 */     for (PlayerDeathPositionData deathPosition : deathPositions) {
/* 32 */       addDeathMarker(tracker, playerChunkX, playerChunkZ, deathPosition);
/*    */     }
/*    */   }
/*    */   
/*    */   private static void addDeathMarker(@Nonnull MapMarkerTracker tracker, int playerChunkX, int playerChunkZ, @Nonnull PlayerDeathPositionData deathPosition) {
/* 37 */     String markerId = deathPosition.getMarkerId();
/* 38 */     Transform transform = deathPosition.getTransform();
/* 39 */     int deathDay = deathPosition.getDay();
/*    */ 
/*    */     
/* 42 */     tracker.trySendMarker(-1, playerChunkX, playerChunkZ, transform
/* 43 */         .getPosition(), transform.getRotation().getYaw(), markerId, "Death (Day " + deathDay + ")", transform, (id, name, t) -> new MapMarker(id, name, "Death.png", PositionUtil.toTransformPacket(t), null));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\DeathMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */