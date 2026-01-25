/*    */ package com.hypixel.hytale.builtin.adventure.objectives.markers;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.Objective;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.ObjectiveDataStore;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.task.ObjectiveTask;
/*    */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ObjectiveMarkerProvider
/*    */   implements WorldMapManager.MarkerProvider {
/* 18 */   public static final ObjectiveMarkerProvider INSTANCE = new ObjectiveMarkerProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 25 */     Player player = tracker.getPlayer();
/* 26 */     Set<UUID> activeObjectiveUUIDs = player.getPlayerConfigData().getActiveObjectiveUUIDs();
/*    */     
/* 28 */     if (activeObjectiveUUIDs.isEmpty())
/*    */       return; 
/* 30 */     UUID playerUUID = player.getUuid();
/* 31 */     ObjectiveDataStore objectiveDataStore = ObjectivePlugin.get().getObjectiveDataStore();
/*    */     
/* 33 */     for (UUID objectiveUUID : activeObjectiveUUIDs) {
/* 34 */       Objective objective = objectiveDataStore.getObjective(objectiveUUID);
/* 35 */       if (objective == null) {
/*    */         continue;
/*    */       }
/* 38 */       if (!objective.getActivePlayerUUIDs().contains(playerUUID))
/*    */         continue; 
/* 40 */       ObjectiveTask[] tasks = objective.getCurrentTasks();
/* 41 */       if (tasks == null)
/*    */         continue; 
/* 43 */       for (ObjectiveTask task : tasks) {
/* 44 */         for (MapMarker marker : task.getMarkers())
/* 45 */           tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker); 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\markers\ObjectiveMarkerProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */