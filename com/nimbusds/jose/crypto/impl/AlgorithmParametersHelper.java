/*    */ package com.nimbusds.jose.crypto.impl;
/*    */ 
/*    */ import java.security.AlgorithmParameters;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.Provider;
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
/*    */ 
/*    */ public class AlgorithmParametersHelper
/*    */ {
/*    */   public static AlgorithmParameters getInstance(String name, Provider provider) throws NoSuchAlgorithmException {
/* 52 */     if (provider == null) {
/* 53 */       return AlgorithmParameters.getInstance(name);
/*    */     }
/* 55 */     return AlgorithmParameters.getInstance(name, provider);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\crypto\impl\AlgorithmParametersHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */