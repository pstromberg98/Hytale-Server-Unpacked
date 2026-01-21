/*    */ package io.netty.handler.codec.quic;
/*    */ 
/*    */ import io.netty.channel.ChannelOption;
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
/*    */ public final class QuicChannelOption<T>
/*    */   extends ChannelOption<T>
/*    */ {
/* 31 */   public static final ChannelOption<Boolean> READ_FRAMES = valueOf(QuicChannelOption.class, "READ_FRAMES");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 37 */   public static final ChannelOption<QLogConfiguration> QLOG = valueOf(QuicChannelOption.class, "QLOG");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 44 */   public static final ChannelOption<SegmentedDatagramPacketAllocator> SEGMENTED_DATAGRAM_PACKET_ALLOCATOR = valueOf(QuicChannelOption.class, "SEGMENTED_DATAGRAM_PACKET_ALLOCATOR");
/*    */ 
/*    */   
/*    */   private QuicChannelOption() {
/* 48 */     super(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\QuicChannelOption.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */