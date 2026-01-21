/*    */ package com.hypixel.hytale.builtin.portals.systems;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.portals.components.PortalDevice;
/*    */ import com.hypixel.hytale.component.AddReason;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.RemoveReason;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.RefSystem;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import javax.annotation.Nonnull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EntityRemoved
/*    */   extends RefSystem<ChunkStore>
/*    */ {
/*    */   public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
/*    */   
/*    */   public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 52 */     PortalDevice device = (PortalDevice)store.getComponent(ref, PortalDevice.getComponentType());
/* 53 */     CloseWorldWhenBreakingDeviceSystems.maybeCloseFragmentWorld(device);
/*    */   }
/*    */ 
/*    */   
/*    */   public Query<ChunkStore> getQuery() {
/* 58 */     return (Query<ChunkStore>)PortalDevice.getComponentType();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\portals\systems\CloseWorldWhenBreakingDeviceSystems$EntityRemoved.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */