/*     */ package com.hypixel.hytale.server.core.io.netty;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*     */ import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import io.netty.channel.ChannelInboundHandlerAdapter;
/*     */ import io.netty.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.logging.Level;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class ExceptionHandler
/*     */   extends ChannelInboundHandlerAdapter
/*     */ {
/* 135 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/* 136 */   private final AtomicBoolean handled = new AtomicBoolean();
/*     */   
/*     */   public void exceptionCaught(@Nonnull ChannelHandlerContext ctx, Throwable cause) {
/*     */     String identifier;
/* 140 */     if (cause instanceof java.nio.channels.ClosedChannelException) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 146 */     ChannelHandler handler = ctx.pipeline().get("handler");
/* 147 */     if (handler instanceof PlayerChannelHandler) {
/* 148 */       identifier = ((PlayerChannelHandler)handler).getHandler().getIdentifier();
/*     */     } else {
/* 150 */       identifier = NettyUtil.formatRemoteAddress(ctx.channel());
/*     */     } 
/*     */     
/* 153 */     if (this.handled.getAndSet(true)) {
/* 154 */       if (cause instanceof java.io.IOException && cause.getMessage() != null) {
/* 155 */         switch (cause.getMessage()) {
/*     */           case "Broken pipe":
/*     */           case "Connection reset by peer":
/*     */           case "An existing connection was forcibly closed by the remote host":
/*     */             return;
/*     */         } 
/*     */       
/*     */       }
/* 163 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(cause)).log("Already handled exception in ExceptionHandler but got another!");
/*     */       
/*     */       return;
/*     */     } 
/* 167 */     if (cause instanceof io.netty.handler.timeout.TimeoutException) {
/* 168 */       handleTimeout(ctx, cause, identifier);
/*     */       return;
/*     */     } 
/* 171 */     ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(cause)).log("Got exception from netty pipeline in ExceptionHandler: %s", cause.getMessage());
/*     */ 
/*     */     
/* 174 */     gracefulDisconnect(ctx, identifier, "Internal server error!");
/*     */   }
/*     */   
/*     */   private void handleTimeout(@Nonnull ChannelHandlerContext ctx, Throwable cause, String identifier) {
/* 178 */     boolean readTimeout = cause instanceof io.netty.handler.timeout.ReadTimeoutException;
/* 179 */     boolean writeTimeout = cause instanceof io.netty.handler.timeout.WriteTimeoutException;
/* 180 */     String timeoutType = readTimeout ? "Read" : (writeTimeout ? "Write" : "Connection");
/*     */ 
/*     */     
/* 183 */     NettyUtil.TimeoutContext context = (NettyUtil.TimeoutContext)ctx.channel().attr(NettyUtil.TimeoutContext.KEY).get();
/* 184 */     String stage = (context != null) ? context.stage() : "unknown";
/* 185 */     String duration = (context != null) ? FormatUtil.nanosToString(System.nanoTime() - context.connectionStartNs()) : "unknown";
/*     */     
/* 187 */     LOGGER.at(Level.INFO).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
/*     */     
/* 189 */     ((HytaleLogger.Api)NettyUtil.CONNECTION_EXCEPTION_LOGGER.at(Level.FINE).withCause(cause)).log("%s timeout for %s at stage '%s' after %s connected", timeoutType, identifier, stage, duration);
/*     */ 
/*     */ 
/*     */     
/* 193 */     gracefulDisconnect(ctx, identifier, timeoutType + " timeout");
/*     */   }
/*     */   
/*     */   private void gracefulDisconnect(@Nonnull ChannelHandlerContext ctx, String identifier, String reason) {
/* 197 */     Channel channel = ctx.channel();
/*     */ 
/*     */     
/* 200 */     if (channel.isWritable()) {
/* 201 */       channel.writeAndFlush(new Disconnect(reason, DisconnectType.Disconnect))
/* 202 */         .addListener(future -> ProtocolUtil.closeApplicationConnection(channel, 4));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 207 */       channel.eventLoop().schedule(() -> { if (channel.isOpen()) { LOGGER.at(Level.FINE).log("Force closing %s after graceful disconnect attempt", identifier); ProtocolUtil.closeApplicationConnection(channel, 4); }  }1L, TimeUnit.SECONDS);
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 214 */       ProtocolUtil.closeApplicationConnection(channel, 4);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\netty\HytaleChannelInitializer$ExceptionHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */