/*     */ package com.hypixel.hytale.protocol.io.netty;
/*     */ 
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import io.netty.buffer.Unpooled;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelFuture;
/*     */ import io.netty.channel.ChannelFutureListener;
/*     */ import io.netty.handler.codec.quic.QuicChannel;
/*     */ import io.netty.handler.codec.quic.QuicTransportError;
/*     */ import io.netty.util.AttributeKey;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.Duration;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ProtocolUtil
/*     */ {
/*  24 */   public static final AttributeKey<Duration> PACKET_TIMEOUT_KEY = AttributeKey.newInstance("PACKET_TIMEOUT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_NO_ERROR = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_RATE_LIMITED = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_AUTH_FAILED = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_INVALID_VERSION = 3;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_TIMEOUT = 4;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_CLIENT_OUTDATED = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int APPLICATION_SERVER_OUTDATED = 6;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   public static final ChannelFutureListener CLOSE_ON_COMPLETE = ProtocolUtil::closeApplicationOnComplete;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeConnection(@Nonnull Channel channel) {
/*  75 */     closeConnection(channel, QuicTransportError.PROTOCOL_VIOLATION);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeConnection(@Nonnull Channel channel, @Nonnull QuicTransportError error) {
/*  86 */     int errorCode = (int)error.code();
/*     */     
/*  88 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/*  89 */       quicChannel.close(false, errorCode, Unpooled.EMPTY_BUFFER);
/*     */       
/*     */       return; }
/*     */     
/*  93 */     Channel parent = channel.parent();
/*  94 */     if (parent instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)parent;
/*  95 */       quicChannel.close(false, errorCode, Unpooled.EMPTY_BUFFER); }
/*     */     else
/*  97 */     { channel.close(); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeApplicationConnection(@Nonnull Channel channel) {
/* 106 */     closeApplicationConnection(channel, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void closeApplicationConnection(@Nonnull Channel channel, int errorCode) {
/* 117 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/* 118 */       quicChannel.close(true, errorCode, Unpooled.EMPTY_BUFFER);
/*     */       
/*     */       return; }
/*     */     
/* 122 */     Channel parent = channel.parent();
/* 123 */     if (parent instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)parent;
/* 124 */       quicChannel.close(true, errorCode, Unpooled.EMPTY_BUFFER); }
/*     */     else
/* 126 */     { channel.close(); }
/*     */   
/*     */   }
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
/*     */   public static void closeApplicationConnection(@Nonnull Channel channel, int errorCode, @Nonnull String reason) {
/* 140 */     ByteBuf reasonBuf = Unpooled.copiedBuffer(reason, StandardCharsets.UTF_8);
/*     */     
/* 142 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/* 143 */       quicChannel.close(true, errorCode, reasonBuf);
/*     */       
/*     */       return; }
/*     */     
/* 147 */     Channel parent = channel.parent();
/* 148 */     if (parent instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)parent;
/* 149 */       quicChannel.close(true, errorCode, reasonBuf); }
/*     */     else
/* 151 */     { reasonBuf.release();
/* 152 */       channel.close(); }
/*     */   
/*     */   }
/*     */   
/*     */   private static void closeApplicationOnComplete(ChannelFuture future) {
/* 157 */     closeApplicationConnection(future.channel());
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\io\netty\ProtocolUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */