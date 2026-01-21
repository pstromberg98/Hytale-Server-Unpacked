/*    */ package com.hypixel.hytale.server.core.event.events.ecs;
/*    */ 
/*    */ import com.hypixel.hytale.component.system.ICancellableEcsEvent;
/*    */ import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Display
/*    */   extends DiscoverZoneEvent
/*    */   implements ICancellableEcsEvent
/*    */ {
/*    */   private boolean cancelled = false;
/*    */   
/*    */   public Display(@Nonnull WorldMapTracker.ZoneDiscoveryInfo discoveryInfo) {
/* 55 */     super(discoveryInfo);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isCancelled() {
/* 60 */     return this.cancelled;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setCancelled(boolean cancelled) {
/* 65 */     this.cancelled = cancelled;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\event\events\ecs\DiscoverZoneEvent$Display.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */