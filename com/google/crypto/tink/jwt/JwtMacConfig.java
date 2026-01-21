/*    */ package com.google.crypto.tink.jwt;
/*    */ 
/*    */ import java.security.GeneralSecurityException;
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
/*    */ public final class JwtMacConfig
/*    */ {
/* 35 */   public static final String JWT_HMAC_TYPE_URL = JwtHmacKeyManager.getKeyType();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void register() throws GeneralSecurityException {
/* 42 */     JwtHmacKeyManager.register(true);
/* 43 */     JwtMacWrapper.register();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\jwt\JwtMacConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */