/*    */ package io.netty.handler.codec.quic;
/*    */ 
/*    */ import java.net.SocketAddress;
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
/*    */ final class QuicheQuicChannelAddress
/*    */   extends SocketAddress
/*    */ {
/*    */   final QuicheQuicChannel channel;
/*    */   
/*    */   QuicheQuicChannelAddress(QuicheQuicChannel channel) {
/* 28 */     this.channel = channel;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\QuicheQuicChannelAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */