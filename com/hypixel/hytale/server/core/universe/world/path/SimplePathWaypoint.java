/*    */ package com.hypixel.hytale.server.core.universe.world.path;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimplePathWaypoint
/*    */   implements IPathWaypoint
/*    */ {
/*    */   private int order;
/*    */   private Transform transform;
/*    */   
/*    */   public SimplePathWaypoint(int order, Transform transform) {
/* 22 */     this.order = order;
/* 23 */     this.transform = transform;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getOrder() {
/* 28 */     return this.order;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Vector3d getWaypointPosition(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 34 */     return this.transform.getPosition();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Vector3f getWaypointRotation(@Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 40 */     return this.transform.getRotation();
/*    */   }
/*    */ 
/*    */   
/*    */   public double getPauseTime() {
/* 45 */     return 0.0D;
/*    */   }
/*    */ 
/*    */   
/*    */   public float getObservationAngle() {
/* 50 */     return 0.0F;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\path\SimplePathWaypoint.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */