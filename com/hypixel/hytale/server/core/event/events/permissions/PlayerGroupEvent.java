/*    */ package com.hypixel.hytale.server.core.event.events.permissions;
/*    */ 
/*    */ import java.util.UUID;
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
/*    */ public class PlayerGroupEvent
/*    */   extends PlayerPermissionChangeEvent
/*    */ {
/*    */   @Nonnull
/*    */   private final String groupName;
/*    */   
/*    */   public PlayerGroupEvent(@Nonnull UUID playerUuid, @Nonnull String groupName) {
/* 27 */     super(playerUuid);
/* 28 */     this.groupName = groupName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String getGroupName() {
/* 36 */     return this.groupName;
/*    */   }
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
/*    */   public static class Added
/*    */     extends PlayerGroupEvent
/*    */   {
/*    */     public Added(@Nonnull UUID playerUuid, @Nonnull String groupName) {
/* 54 */       super(playerUuid, groupName);
/*    */     }
/*    */   }
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
/*    */   public static class Removed
/*    */     extends PlayerGroupEvent
/*    */   {
/*    */     public Removed(@Nonnull UUID playerUuid, @Nonnull String groupName) {
/* 73 */       super(playerUuid, groupName);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\event\events\permissions\PlayerGroupEvent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */