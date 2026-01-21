/*    */ package io.netty.handler.codec.quic;
/*    */ 
/*    */ import io.netty.util.internal.ObjectUtil;
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
/*    */ public interface FlushStrategy
/*    */ {
/* 28 */   public static final FlushStrategy DEFAULT = afterNumBytes(27000);
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
/*    */   static FlushStrategy afterNumBytes(int bytes) {
/* 46 */     ObjectUtil.checkPositive(bytes, "bytes");
/* 47 */     return (numPackets, numBytes) -> (numBytes > bytes);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static FlushStrategy afterNumPackets(int packets) {
/* 57 */     ObjectUtil.checkPositive(packets, "packets");
/* 58 */     return (numPackets, numBytes) -> (numPackets > packets);
/*    */   }
/*    */   
/*    */   boolean shouldFlushNow(int paramInt1, int paramInt2);
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\FlushStrategy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */