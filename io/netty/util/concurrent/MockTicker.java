/*    */ package io.netty.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
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
/*    */ public interface MockTicker
/*    */   extends Ticker
/*    */ {
/*    */   default long initialNanoTime() {
/* 29 */     return 0L;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void advance(long paramLong, TimeUnit paramTimeUnit);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default void advanceMillis(long amountMillis) {
/* 46 */     advance(amountMillis, TimeUnit.MILLISECONDS);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\concurrent\MockTicker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */