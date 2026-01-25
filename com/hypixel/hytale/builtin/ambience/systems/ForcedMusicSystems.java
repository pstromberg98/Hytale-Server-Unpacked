/*    */ package com.hypixel.hytale.builtin.ambience.systems;
/*    */ import com.hypixel.hytale.builtin.ambience.components.AmbienceTracker;
/*    */ import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.Holder;
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
/*    */ public class ForcedMusicSystems {
/* 18 */   private static final Query<EntityStore> TICK_QUERY = (Query<EntityStore>)Archetype.of(new ComponentType[] { Player.getComponentType(), PlayerRef.getComponentType(), AmbienceTracker.getComponentType() });
/*    */   
/*    */   public static class PlayerAdded
/*    */     extends HolderSystem<EntityStore> {
/*    */     public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
/* 23 */       holder.ensureComponent(AmbienceTracker.getComponentType());
/*    */     }
/*    */ 
/*    */     
/*    */     public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
/* 28 */       AmbienceTracker ambienceTrackerComponent = (AmbienceTracker)holder.getComponent(AmbienceTracker.getComponentType());
/* 29 */       assert ambienceTrackerComponent != null;
/*    */       
/* 31 */       PlayerRef playerRefComponent = (PlayerRef)holder.getComponent(PlayerRef.getComponentType());
/* 32 */       assert playerRefComponent != null;
/*    */       
/* 34 */       UpdateEnvironmentMusic pooledPacket = ambienceTrackerComponent.getMusicPacket();
/* 35 */       pooledPacket.environmentIndex = 0;
/* 36 */       playerRefComponent.getPacketHandler().write((Packet)pooledPacket);
/*    */     }
/*    */ 
/*    */     
/*    */     @Nullable
/*    */     public Query<EntityStore> getQuery() {
/* 42 */       return (Query<EntityStore>)Query.and(new Query[] { (Query)PlayerRef.getComponentType(), (Query)AmbienceTracker.getComponentType() });
/*    */     }
/*    */   }
/*    */   
/*    */   public static class Tick
/*    */     extends EntityTickingSystem<EntityStore> {
/*    */     public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 49 */       AmbienceResource ambienceResource = (AmbienceResource)store.getResource(AmbienceResource.getResourceType());
/* 50 */       AmbienceTracker tracker = (AmbienceTracker)archetypeChunk.getComponent(index, AmbienceTracker.getComponentType());
/* 51 */       PlayerRef playerRef = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/*    */       
/* 53 */       int have = tracker.getForcedMusicIndex();
/* 54 */       int desired = ambienceResource.getForcedMusicIndex();
/* 55 */       if (have == desired) {
/*    */         return;
/*    */       }
/*    */       
/* 59 */       tracker.setForcedMusicIndex(desired);
/*    */       
/* 61 */       UpdateEnvironmentMusic pooledPacket = tracker.getMusicPacket();
/* 62 */       pooledPacket.environmentIndex = desired;
/* 63 */       playerRef.getPacketHandler().write((Packet)pooledPacket);
/*    */     }
/*    */ 
/*    */     
/*    */     @Nullable
/*    */     public Query<EntityStore> getQuery() {
/* 69 */       return ForcedMusicSystems.TICK_QUERY;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\ambience\systems\ForcedMusicSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */