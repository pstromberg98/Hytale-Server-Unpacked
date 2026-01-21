/*    */ package com.hypixel.hytale.server.core.universe.playerdata;
/*    */ 
/*    */ import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface PlayerStorageProvider
/*    */ {
/* 11 */   public static final BuilderCodecMapCodec<PlayerStorageProvider> CODEC = new BuilderCodecMapCodec("Type", true);
/*    */   
/*    */   PlayerStorage getPlayerStorage();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\playerdata\PlayerStorageProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */