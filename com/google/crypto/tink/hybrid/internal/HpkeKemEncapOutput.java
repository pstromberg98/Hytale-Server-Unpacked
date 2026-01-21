/*    */ package com.google.crypto.tink.hybrid.internal;
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
/*    */ final class HpkeKemEncapOutput
/*    */ {
/*    */   private final byte[] sharedSecret;
/*    */   private final byte[] encapsulatedKey;
/*    */   
/*    */   HpkeKemEncapOutput(byte[] sharedSecret, byte[] encapsulatedKey) {
/* 27 */     this.sharedSecret = sharedSecret;
/* 28 */     this.encapsulatedKey = encapsulatedKey;
/*    */   }
/*    */   
/*    */   byte[] getSharedSecret() {
/* 32 */     return this.sharedSecret;
/*    */   }
/*    */   
/*    */   byte[] getEncapsulatedKey() {
/* 36 */     return this.encapsulatedKey;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\hybrid\internal\HpkeKemEncapOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */