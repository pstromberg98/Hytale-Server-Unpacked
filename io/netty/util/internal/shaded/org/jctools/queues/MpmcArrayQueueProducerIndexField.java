/*    */ package io.netty.util.internal.shaded.org.jctools.queues;
/*    */ 
/*    */ import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
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
/*    */ 
/*    */ abstract class MpmcArrayQueueProducerIndexField<E>
/*    */   extends MpmcArrayQueueL1Pad<E>
/*    */ {
/* 51 */   private static final long P_INDEX_OFFSET = UnsafeAccess.fieldOffset(MpmcArrayQueueProducerIndexField.class, "producerIndex");
/*    */   
/*    */   private volatile long producerIndex;
/*    */ 
/*    */   
/*    */   MpmcArrayQueueProducerIndexField(int capacity) {
/* 57 */     super(capacity);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public final long lvProducerIndex() {
/* 63 */     return this.producerIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   final boolean casProducerIndex(long expect, long newValue) {
/* 68 */     return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\nett\\util\internal\shaded\org\jctools\queues\MpmcArrayQueueProducerIndexField.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */