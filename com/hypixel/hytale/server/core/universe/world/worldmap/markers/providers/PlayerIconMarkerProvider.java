/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap.markers.providers;
/*    */ 
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*    */ import java.util.function.Predicate;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ public class PlayerIconMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider
/*    */ {
/* 20 */   public static final PlayerIconMarkerProvider INSTANCE = new PlayerIconMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 27 */     WorldMapConfig worldMapConfig = world.getGameplayConfig().getWorldMapConfig();
/* 28 */     if (!worldMapConfig.isDisplayPlayers())
/*    */       return; 
/* 30 */     Player player = tracker.getPlayer();
/* 31 */     int chunkViewRadiusSq = chunkViewRadius * chunkViewRadius;
/*    */     
/* 33 */     Predicate<PlayerRef> playerMapFilter = tracker.getPlayerMapFilter();
/*    */     
/* 35 */     for (PlayerRef otherPlayer : world.getPlayerRefs()) {
/* 36 */       if (otherPlayer.getUuid().equals(player.getUuid()))
/*    */         continue; 
/* 38 */       Transform otherPlayerTransform = otherPlayer.getTransform();
/* 39 */       Vector3d otherPos = otherPlayerTransform.getPosition();
/*    */       
/* 41 */       int otherChunkX = (int)otherPos.x >> 5;
/* 42 */       int otherChunkZ = (int)otherPos.z >> 5;
/* 43 */       int chunkDiffX = otherChunkX - playerChunkX;
/* 44 */       int chunkDiffZ = otherChunkZ - playerChunkZ;
/*    */       
/* 46 */       int chunkDistSq = chunkDiffX * chunkDiffX + chunkDiffZ * chunkDiffZ;
/* 47 */       if (chunkDistSq > chunkViewRadiusSq)
/*    */         continue; 
/* 49 */       if (playerMapFilter != null && playerMapFilter.test(otherPlayer))
/*    */         continue; 
/* 51 */       tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, otherPos, otherPlayer
/* 52 */           .getHeadRotation().getYaw(), "Player-" + 
/* 53 */           String.valueOf(otherPlayer.getUuid()), otherPlayer.getUsername(), otherPlayer, (id, name, op) -> new MapMarker(id, name, "Player.png", PositionUtil.toTransformPacket(op.getTransform()), null));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\markers\providers\PlayerIconMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */