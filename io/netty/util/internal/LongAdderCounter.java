/*    */ package io.netty.util.internal;
/*    */ 
/*    */ import java.util.concurrent.atomic.LongAdder;
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
/*    */ final class LongAdderCounter
/*    */   extends LongAdder
/*    */   implements LongCounter
/*    */ {
/*    */   public long value() {
/* 28 */     return longValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\internal\LongAdderCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */