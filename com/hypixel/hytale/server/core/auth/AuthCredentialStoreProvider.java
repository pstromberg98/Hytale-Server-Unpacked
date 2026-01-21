/*    */ package com.hypixel.hytale.server.core.auth;
/*    */ 
/*    */ import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
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
/*    */ public interface AuthCredentialStoreProvider
/*    */ {
/* 18 */   public static final BuilderCodecMapCodec<AuthCredentialStoreProvider> CODEC = new BuilderCodecMapCodec("Type", true);
/*    */   
/*    */   @Nonnull
/*    */   IAuthCredentialStore createStore();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\auth\AuthCredentialStoreProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */