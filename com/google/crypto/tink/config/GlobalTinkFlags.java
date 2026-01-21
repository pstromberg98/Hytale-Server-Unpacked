/*    */ package com.google.crypto.tink.config;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
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
/*    */ public final class GlobalTinkFlags
/*    */ {
/* 38 */   public static final TinkFlag validateKeysetsOnParsing = new TinkFlagImpl(false);
/*    */   
/*    */   private static class TinkFlagImpl implements TinkFlag {
/*    */     private final AtomicBoolean b;
/*    */     
/*    */     TinkFlagImpl(boolean b) {
/* 44 */       this.b = new AtomicBoolean(b);
/*    */     }
/*    */ 
/*    */     
/*    */     public boolean getValue() {
/* 49 */       return this.b.get();
/*    */     }
/*    */ 
/*    */     
/*    */     public void setValue(boolean t) {
/* 54 */       this.b.set(t);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\config\GlobalTinkFlags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */