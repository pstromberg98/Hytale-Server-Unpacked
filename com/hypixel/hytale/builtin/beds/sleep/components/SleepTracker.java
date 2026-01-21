/*    */ package com.hypixel.hytale.builtin.beds.sleep.components;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.beds.BedsPlugin;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.protocol.packets.world.UpdateSleepState;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class SleepTracker
/*    */   implements Component<EntityStore> {
/*    */   public static ComponentType<EntityStore, SleepTracker> getComponentType() {
/* 13 */     return BedsPlugin.getInstance().getSleepTrackerComponentType();
/*    */   }
/*    */   
/* 16 */   private UpdateSleepState lastSentPacket = new UpdateSleepState(false, false, null, null);
/*    */   
/*    */   @Nullable
/*    */   public UpdateSleepState generatePacketToSend(UpdateSleepState state) {
/* 20 */     if (this.lastSentPacket.equals(state)) {
/* 21 */       return null;
/*    */     }
/*    */     
/* 24 */     this.lastSentPacket = state;
/* 25 */     return this.lastSentPacket;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Component<EntityStore> clone() {
/* 31 */     return new SleepTracker();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\sleep\components\SleepTracker.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */