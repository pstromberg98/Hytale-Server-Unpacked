/*    */ package com.hypixel.hytale.builtin.ambience.systems;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.ambience.components.AmbienceTracker;
/*    */ import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*    */ import com.hypixel.hytale.protocol.Packet;
/*    */ import com.hypixel.hytale.protocol.packets.world.UpdateEnvironmentMusic;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Tick
/*    */   extends EntityTickingSystem<EntityStore>
/*    */ {
/*    */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 49 */     AmbienceResource ambienceResource = (AmbienceResource)store.getResource(AmbienceResource.getResourceType());
/* 50 */     AmbienceTracker tracker = (AmbienceTracker)archetypeChunk.getComponent(index, AmbienceTracker.getComponentType());
/* 51 */     PlayerRef playerRef = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/*    */     
/* 53 */     int have = tracker.getForcedMusicIndex();
/* 54 */     int desired = ambienceResource.getForcedMusicIndex();
/* 55 */     if (have == desired) {
/*    */       return;
/*    */     }
/*    */     
/* 59 */     tracker.setForcedMusicIndex(desired);
/*    */     
/* 61 */     UpdateEnvironmentMusic pooledPacket = tracker.getMusicPacket();
/* 62 */     pooledPacket.environmentIndex = desired;
/* 63 */     playerRef.getPacketHandler().write((Packet)pooledPacket);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Query<EntityStore> getQuery() {
/* 69 */     return ForcedMusicSystems.TICK_QUERY;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\ambience\systems\ForcedMusicSystems$Tick.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */