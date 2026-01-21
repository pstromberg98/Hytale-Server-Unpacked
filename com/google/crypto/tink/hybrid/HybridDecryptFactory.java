/*    */ package com.google.crypto.tink.hybrid;
/*    */ 
/*    */ import com.google.crypto.tink.HybridDecrypt;
/*    */ import com.google.crypto.tink.KeysetHandle;
/*    */ import com.google.crypto.tink.RegistryConfiguration;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public final class HybridDecryptFactory
/*    */ {
/*    */   @Deprecated
/*    */   public static HybridDecrypt getPrimitive(KeysetHandle keysetHandle) throws GeneralSecurityException {
/* 48 */     HybridDecryptWrapper.register();
/* 49 */     return (HybridDecrypt)keysetHandle.getPrimitive(RegistryConfiguration.get(), HybridDecrypt.class);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\hybrid\HybridDecryptFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */