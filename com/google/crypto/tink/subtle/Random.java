/*    */ package com.google.crypto.tink.subtle;
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
/*    */ public final class Random
/*    */ {
/*    */   public static byte[] randBytes(int size) {
/* 28 */     return com.google.crypto.tink.internal.Random.randBytes(size);
/*    */   }
/*    */ 
/*    */   
/*    */   public static final int randInt(int max) {
/* 33 */     return com.google.crypto.tink.internal.Random.randInt(max);
/*    */   }
/*    */ 
/*    */   
/*    */   public static final int randInt() {
/* 38 */     return com.google.crypto.tink.internal.Random.randInt();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\subtle\Random.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */