/*     */ package com.hypixel.hytale.protocol.io.netty;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.PacketRegistry;
/*     */ import com.hypixel.hytale.protocol.io.PacketIO;
/*     */ import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
/*     */ import com.hypixel.hytale.protocol.io.ProtocolException;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.handler.codec.ByteToMessageDecoder;
/*     */ import io.netty.handler.timeout.ReadTimeoutException;
/*     */ import java.time.Duration;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PacketDecoder
/*     */   extends ByteToMessageDecoder
/*     */ {
/*     */   private static final int LENGTH_PREFIX_SIZE = 4;
/*     */   private static final int PACKET_ID_SIZE = 4;
/*     */   private static final int MIN_FRAME_SIZE = 8;
/*     */   private static final long CHECK_INTERVAL_MS = 1000L;
/*     */   private volatile long lastPacketTimeNanos;
/*     */   private ScheduledFuture<?> timeoutCheckFuture;
/*     */   
/*     */   public void handlerAdded(@Nonnull ChannelHandlerContext ctx) throws Exception {
/*  49 */     if (ctx.channel().isActive()) {
/*  50 */       initialize(ctx);
/*     */     }
/*  52 */     super.handlerAdded(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelActive(@Nonnull ChannelHandlerContext ctx) throws Exception {
/*  57 */     initialize(ctx);
/*  58 */     super.channelActive(ctx);
/*     */   }
/*     */ 
/*     */   
/*     */   public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
/*  63 */     cancelTimeoutCheck();
/*  64 */     super.channelInactive(ctx);
/*     */   }
/*     */   
/*     */   private void initialize(@Nonnull ChannelHandlerContext ctx) {
/*  68 */     if (this.timeoutCheckFuture != null)
/*  69 */       return;  this.lastPacketTimeNanos = System.nanoTime();
/*  70 */     this.timeoutCheckFuture = (ScheduledFuture<?>)ctx.executor().scheduleAtFixedRate(() -> checkTimeout(ctx), 1000L, 1000L, TimeUnit.MILLISECONDS);
/*     */   }
/*     */   
/*     */   private void cancelTimeoutCheck() {
/*  74 */     if (this.timeoutCheckFuture != null) {
/*  75 */       this.timeoutCheckFuture.cancel(false);
/*  76 */       this.timeoutCheckFuture = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkTimeout(@Nonnull ChannelHandlerContext ctx) {
/*  81 */     if (!ctx.channel().isActive()) {
/*  82 */       cancelTimeoutCheck();
/*     */       
/*     */       return;
/*     */     } 
/*  86 */     Duration timeout = (Duration)ctx.channel().attr(ProtocolUtil.PACKET_TIMEOUT_KEY).get();
/*  87 */     if (timeout == null)
/*     */       return; 
/*  89 */     long elapsedNanos = System.nanoTime() - this.lastPacketTimeNanos;
/*  90 */     if (elapsedNanos >= timeout.toNanos()) {
/*  91 */       cancelTimeoutCheck();
/*  92 */       ctx.fireExceptionCaught((Throwable)ReadTimeoutException.INSTANCE);
/*  93 */       ctx.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void decode(@Nonnull ChannelHandlerContext ctx, @Nonnull ByteBuf in, @Nonnull List<Object> out) {
/* 100 */     if (in.readableBytes() < 8) {
/*     */       return;
/*     */     }
/*     */     
/* 104 */     in.markReaderIndex();
/*     */     
/* 106 */     int payloadLength = in.readIntLE();
/*     */ 
/*     */     
/* 109 */     if (payloadLength < 0 || payloadLength > 1677721600) {
/* 110 */       in.skipBytes(in.readableBytes());
/* 111 */       ProtocolUtil.closeConnection(ctx.channel());
/*     */       
/*     */       return;
/*     */     } 
/* 115 */     int packetId = in.readIntLE();
/*     */ 
/*     */ 
/*     */     
/* 119 */     PacketRegistry.PacketInfo packetInfo = PacketRegistry.getById(packetId);
/* 120 */     if (packetInfo == null) {
/* 121 */       in.skipBytes(in.readableBytes());
/* 122 */       ProtocolUtil.closeConnection(ctx.channel());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 127 */     if (payloadLength > packetInfo.maxSize()) {
/* 128 */       in.skipBytes(in.readableBytes());
/* 129 */       ProtocolUtil.closeConnection(ctx.channel());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 134 */     if (in.readableBytes() < payloadLength) {
/* 135 */       in.resetReaderIndex();
/*     */       
/*     */       return;
/*     */     } 
/* 139 */     PacketStatsRecorder statsRecorder = (PacketStatsRecorder)ctx.channel().attr(PacketStatsRecorder.CHANNEL_KEY).get();
/* 140 */     if (statsRecorder == null) {
/* 141 */       statsRecorder = PacketStatsRecorder.NOOP;
/*     */     }
/*     */     
/*     */     try {
/* 145 */       out.add(PacketIO.readFramedPacketWithInfo(in, payloadLength, packetInfo, statsRecorder));
/* 146 */       this.lastPacketTimeNanos = System.nanoTime();
/* 147 */     } catch (ProtocolException e) {
/* 148 */       in.skipBytes(in.readableBytes());
/* 149 */       ProtocolUtil.closeConnection(ctx.channel());
/* 150 */     } catch (IndexOutOfBoundsException e) {
/* 151 */       in.skipBytes(in.readableBytes());
/* 152 */       ProtocolUtil.closeConnection(ctx.channel());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\io\netty\PacketDecoder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */