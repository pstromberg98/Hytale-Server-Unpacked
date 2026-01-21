/*    */ package io.netty.handler.codec.quic;
/*    */ 
/*    */ import io.netty.util.internal.ObjectUtil;
/*    */ import java.nio.ByteBuffer;
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
/*    */ final class HmacSignQuicResetTokenGenerator
/*    */   implements QuicResetTokenGenerator
/*    */ {
/* 27 */   static final QuicResetTokenGenerator INSTANCE = new HmacSignQuicResetTokenGenerator();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ByteBuffer newResetToken(ByteBuffer cid) {
/* 34 */     ObjectUtil.checkNotNull(cid, "cid");
/* 35 */     ObjectUtil.checkPositive(cid.remaining(), "cid");
/* 36 */     return Hmac.sign(cid, 16);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\HmacSignQuicResetTokenGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */