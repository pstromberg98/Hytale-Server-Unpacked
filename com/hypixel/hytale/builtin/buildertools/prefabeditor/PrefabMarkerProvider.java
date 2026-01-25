/*    */ package com.hypixel.hytale.builtin.buildertools.prefabeditor;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class PrefabMarkerProvider implements WorldMapManager.MarkerProvider {
/* 12 */   public static final PrefabMarkerProvider INSTANCE = new PrefabMarkerProvider();
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 16 */     PrefabEditSessionManager sessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
/* 17 */     Player player = tracker.getPlayer();
/* 18 */     PrefabEditSession session = sessionManager.getPrefabEditSession(player.getUuid());
/*    */     
/* 20 */     if (session == null || !session.getWorldName().equals(world.getName()))
/*    */       return; 
/* 22 */     for (PrefabEditingMetadata metadata : session.getLoadedPrefabMetadata().values()) {
/* 23 */       MapMarker marker = PrefabEditSession.createPrefabMarker(metadata);
/* 24 */       tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\prefabeditor\PrefabMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */