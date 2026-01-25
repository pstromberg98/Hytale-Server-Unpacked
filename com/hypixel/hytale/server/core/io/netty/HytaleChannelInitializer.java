/*     */ package com.hypixel.hytale.server.core.io.netty;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.io.PacketStatsRecorder;
/*     */ import com.hypixel.hytale.protocol.io.netty.PacketDecoder;
/*     */ import com.hypixel.hytale.protocol.io.netty.PacketEncoder;
/*     */ import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*     */ import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.PacketStatsRecorderImpl;
/*     */ import com.hypixel.hytale.server.core.io.handlers.InitialPacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.transport.QUICTransport;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import io.netty.channel.ChannelInitializer;
/*     */ import io.netty.handler.codec.quic.QuicChannel;
/*     */ import io.netty.handler.codec.quic.QuicStreamChannel;
/*     */ import io.netty.util.concurrent.Future;
/*     */ import io.netty.util.concurrent.GenericFutureListener;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.time.Duration;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class HytaleChannelInitializer
/*     */   extends ChannelInitializer<Channel> {
/*     */   protected void initChannel(Channel channel) {
/*  36 */     if (channel instanceof QuicStreamChannel) { QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
/*  37 */       HytaleLogger.getLogger().at(Level.INFO).log("Received stream %s to %s", 
/*  38 */           NettyUtil.formatRemoteAddress(channel), 
/*  39 */           NettyUtil.formatLocalAddress(channel));
/*     */ 
/*     */       
/*  42 */       QuicChannel parentChannel = quicStreamChannel.parent();
/*  43 */       Integer rejectErrorCode = (Integer)parentChannel.attr(QUICTransport.ALPN_REJECT_ERROR_CODE_ATTR).get();
/*  44 */       if (rejectErrorCode != null) {
/*     */ 
/*     */         
/*  47 */         HytaleLogger.getLogger().at(Level.INFO).log("Rejecting stream from %s: client outdated (ALPN mismatch)", 
/*  48 */             NettyUtil.formatRemoteAddress(channel));
/*     */ 
/*     */         
/*  51 */         channel.config().setAutoRead(false);
/*     */ 
/*     */         
/*  54 */         channel.pipeline().addLast("packetEncoder", (ChannelHandler)new PacketEncoder());
/*  55 */         channel.writeAndFlush(new Disconnect("Your game client needs to be updated.", DisconnectType.Disconnect))
/*  56 */           .addListener(future -> channel.eventLoop().schedule((), 100L, TimeUnit.MILLISECONDS));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  68 */       X509Certificate clientCert = (X509Certificate)parentChannel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).get();
/*  69 */       if (clientCert != null) {
/*  70 */         channel.attr(QUICTransport.CLIENT_CERTIFICATE_ATTR).set(clientCert);
/*  71 */         HytaleLogger.getLogger().at(Level.FINE).log("Copied client certificate to stream: %s", clientCert
/*  72 */             .getSubjectX500Principal().getName());
/*     */       }  }
/*     */     else
/*  75 */     { HytaleLogger.getLogger().at(Level.INFO).log("Received connection from %s to %s", 
/*  76 */           NettyUtil.formatRemoteAddress(channel), 
/*  77 */           NettyUtil.formatLocalAddress(channel)); }
/*     */ 
/*     */ 
/*     */     
/*  81 */     PacketStatsRecorderImpl statsRecorder = new PacketStatsRecorderImpl();
/*  82 */     channel.attr(PacketStatsRecorder.CHANNEL_KEY).set(statsRecorder);
/*     */ 
/*     */     
/*  85 */     Duration initialTimeout = HytaleServer.get().getConfig().getConnectionTimeouts().getInitial();
/*  86 */     channel.attr(ProtocolUtil.PACKET_TIMEOUT_KEY).set(initialTimeout);
/*     */ 
/*     */     
/*  89 */     channel.pipeline().addLast("packetDecoder", (ChannelHandler)new PacketDecoder());
/*     */ 
/*     */     
/*  92 */     HytaleServerConfig.RateLimitConfig rateLimitConfig = HytaleServer.get().getConfig().getRateLimitConfig();
/*  93 */     if (rateLimitConfig.isEnabled()) {
/*  94 */       channel.pipeline().addLast("rateLimit", (ChannelHandler)new RateLimitHandler(rateLimitConfig
/*  95 */             .getBurstCapacity(), rateLimitConfig.getPacketsPerSecond()));
/*     */     }
/*     */ 
/*     */     
/*  99 */     channel.pipeline().addLast("packetEncoder", (ChannelHandler)new PacketEncoder());
/* 100 */     channel.pipeline().addLast("packetArrayEncoder", (ChannelHandler)NettyUtil.PACKET_ARRAY_ENCODER_INSTANCE);
/*     */     
/* 102 */     if (NettyUtil.PACKET_LOGGER.getLevel() != Level.OFF) {
/* 103 */       channel.pipeline().addLast("logger", (ChannelHandler)NettyUtil.LOGGER);
/*     */     }
/*     */ 
/*     */     
/* 107 */     InitialPacketHandler playerConnection = new InitialPacketHandler(channel);
/* 108 */     channel.pipeline().addLast("handler", (ChannelHandler)new PlayerChannelHandler((PacketHandler)playerConnection));
/*     */ 
/*     */     
/* 111 */     channel.pipeline().addLast(new ChannelHandler[] { (ChannelHandler)new ExceptionHandler() });
/*     */     
/* 113 */     playerConnection.registered(null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
/* 118 */     ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.WARNING).withCause(cause)).log("Got exception from netty pipeline in HytaleChannelInitializer!");
/* 119 */     if (ctx.channel().isWritable()) {
/* 120 */       ctx.channel().writeAndFlush(new Disconnect("Internal server error!", DisconnectType.Crash))
/* 121 */         .addListener((GenericFutureListener)ProtocolUtil.CLOSE_ON_COMPLETE);
/*     */     } else {
/* 123 */       ProtocolUtil.closeApplicationConnection(ctx.channel());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void channelInactive(@Nonnull ChannelHandlerContext ctx) throws Exception {
/* 130 */     ProtocolUtil.closeApplicationConnection(ctx.channel());
/* 131 */     super.channelInactive(ctx);
/*     */   }
/*     */   
/*     */   private static class ExceptionHandler extends ChannelInboundHandlerAdapter {
/* 135 */     private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/* 136 */     private final AtomicBoolean handled = new AtomicBoolean();
/*     */     
/*     */     public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
/*     */       String identifier;
/* 140 */       if (cause instanceof java.nio.channels.ClosedChannelException) {
/*     */         return;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 146 */       ChannelHandler handler = ctx.pipeline().get("handler");
/* 147 */       if (handler instanceof PlayerChannelHandler) {
/* 148 */         identifier = ((PlayerChannelHandler)handler).getHandler().getIdentifier();
/*     */       } else {
/* 150 */         identifier = NettyUtil.formatRemoteAddress(ctx.channel());
/*     */       } 
/*     */       
/* 153 */       if (this.handled.getAndSet(true)) {
/* 154 */         if (cause instanceof java.io.IOException && cause.getMessage() != null) {
/* 155 */           switch (cause.getMessage()) {
/*     */             case "Broken pipe":
/*     */             case "Connection reset by peer":
/*     */             case "An existing connection was forcibly closed by the remote host":
/*     */               return;
/*     */           } 
/*     */         
/*     */         }
/* 163 */         ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(cause)).log("Already handled exception in ExceptionHandler but got another!");
/*     */         
/*     */         return;
/*     */       } 
/* 167 */       if (cause instanceof io.netty.handler.timeout.TimeoutException) {
/* 168 */         handleTimeout(ctx, cause, identifier);
/*     */         return;
/*     */       } 
/* 171 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(cause)).log("Got exception from netty pipeline in ExceptionHandler: %s", cause.getMessage());
/*     */ 
/*     */       
/* 174 */       gracefulDisconnect(ctx, identifier, "Internal server error!");
/*     */     }
/*     */     
/*     */     private void handleTimeout(@Nonnull ChannelHandlerContext ctx, Throwable cause, String identifier) {
/* 178 */       boolean readTimeout = cause instanceof io.netty.handler.timeout.ReadTimeoutException;
/* 179 */       boolean writeTimeout = cause instanceof io.netty.handler.timeout.WriteTimeoutException;
/* 180 */       String timeoutType = readTimeout ? "Read" : (writeTimeout ? "Write" : "Connection");
/*     */ 
/*     */       
/* 183 */       NettyUtil.TimeoutContext context = (NettyUtil.TimeoutContext)ctx.channel().attr(NettyUtil.TimeoutContext.KEY).get();
/* 184 */       String stage = (context != null) ? context.stage() : "unknown";
/* 185 */       String duration = (context != null) ? FormatUtil.nanosToString(System.nanoTime() - context.connectionStartNs()) : "unknown";
/*     */       
/* 187 */       LOGGER.at(Level.INFO).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
/*     */       
/* 189 */       ((HytaleLogger.Api)NettyUtil.CONNECTION_EXCEPTION_LOGGER.at(Level.FINE).withCause(cause)).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
/*     */ 
/*     */ 
/*     */       
/* 193 */       gracefulDisconnect(ctx, identifier, timeoutType + " timeout");
/*     */     }
/*     */     
/*     */     private void gracefulDisconnect(@Nonnull ChannelHandlerContext ctx, String identifier, String reason) {
/* 197 */       Channel channel = ctx.channel();
/*     */ 
/*     */       
/* 200 */       if (channel.isWritable()) {
/* 201 */         channel.writeAndFlush(new Disconnect(reason, DisconnectType.Disconnect))
/* 202 */           .addListener(future -> ProtocolUtil.closeApplicationConnection(channel, 4));
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 207 */         channel.eventLoop().schedule(() -> { if (channel.isOpen()) { LOGGER.at(Level.FINE).log("Force closing %s after graceful disconnect attempt", identifier); ProtocolUtil.closeApplicationConnection(channel, 4); }  }1L, TimeUnit.SECONDS);
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 214 */         ProtocolUtil.closeApplicationConnection(channel, 4);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\netty\HytaleChannelInitializer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */