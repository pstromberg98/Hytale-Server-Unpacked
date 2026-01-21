/*    */ package com.hypixel.hytale.server.core.util;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.asset.AssetModule;
/*    */ import java.nio.file.Path;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AssetUtil
/*    */ {
/*    */   @Deprecated(forRemoval = true)
/*    */   public static Path getHytaleAssetsPath() {
/* 15 */     return AssetModule.get().getBaseAssetPack().getRoot();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\util\AssetUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */