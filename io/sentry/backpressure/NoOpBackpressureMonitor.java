/*    */ package io.sentry.backpressure;
/*    */ 
/*    */ public final class NoOpBackpressureMonitor
/*    */   implements IBackpressureMonitor {
/*  5 */   private static final NoOpBackpressureMonitor instance = new NoOpBackpressureMonitor();
/*    */ 
/*    */ 
/*    */   
/*    */   public static NoOpBackpressureMonitor getInstance() {
/* 10 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void start() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public int getDownsampleFactor() {
/* 20 */     return 0;
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\backpressure\NoOpBackpressureMonitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */