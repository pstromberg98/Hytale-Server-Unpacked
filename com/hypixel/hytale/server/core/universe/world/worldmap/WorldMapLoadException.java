/*    */ package com.hypixel.hytale.server.core.universe.world.worldmap;
/*    */ 
/*    */ import com.hypixel.hytale.common.util.ExceptionUtil;
/*    */ import java.util.Objects;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorldMapLoadException
/*    */   extends Exception
/*    */ {
/*    */   public WorldMapLoadException(@Nonnull String message) {
/* 15 */     super(Objects.<String>requireNonNull(message));
/*    */   }
/*    */   
/*    */   public WorldMapLoadException(@Nonnull String message, Throwable cause) {
/* 19 */     super(Objects.<String>requireNonNull(message), cause);
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public String getTraceMessage() {
/* 24 */     return getTraceMessage(", ");
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public String getTraceMessage(@Nonnull String joiner) {
/* 29 */     return ExceptionUtil.combineMessages(this, joiner);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\WorldMapLoadException.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */