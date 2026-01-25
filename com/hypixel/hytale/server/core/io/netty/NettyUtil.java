/*     */ package com.hypixel.hytale.server.core.io.netty;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.util.concurrent.ThreadUtil;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandler;
/*     */ import io.netty.channel.EventLoopGroup;
/*     */ import io.netty.channel.epoll.Epoll;
/*     */ import io.netty.channel.epoll.EpollDatagramChannel;
/*     */ import io.netty.channel.epoll.EpollEventLoopGroup;
/*     */ import io.netty.channel.epoll.EpollServerSocketChannel;
/*     */ import io.netty.channel.kqueue.KQueue;
/*     */ import io.netty.channel.kqueue.KQueueDatagramChannel;
/*     */ import io.netty.channel.kqueue.KQueueServerSocketChannel;
/*     */ import io.netty.channel.nio.NioEventLoopGroup;
/*     */ import io.netty.channel.socket.DatagramChannel;
/*     */ import io.netty.channel.socket.SocketProtocolFamily;
/*     */ import io.netty.channel.socket.nio.NioDatagramChannel;
/*     */ import io.netty.channel.socket.nio.NioServerSocketChannel;
/*     */ import io.netty.handler.codec.quic.QuicChannel;
/*     */ import io.netty.handler.codec.quic.QuicStreamChannel;
/*     */ import io.netty.handler.logging.LogLevel;
/*     */ import io.netty.handler.logging.LoggingHandler;
/*     */ import io.netty.util.AttributeKey;
/*     */ import io.netty.util.internal.ObjectUtil;
/*     */ import io.netty.util.internal.StringUtil;
/*     */ import io.netty.util.internal.SystemPropertyUtil;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class NettyUtil {
/*  40 */   public static final HytaleLogger CONNECTION_EXCEPTION_LOGGER = HytaleLogger.get("ConnectionExceptionLogging");
/*  41 */   public static final HytaleLogger PACKET_LOGGER = HytaleLogger.get("PacketLogging"); public static final String PACKET_DECODER = "packetDecoder";
/*     */   public static final String PACKET_ARRAY_ENCODER = "packetArrayEncoder";
/*     */   
/*     */   static {
/*  45 */     HytaleLoggerBackend loggerBackend = HytaleLoggerBackend.getLogger(PACKET_LOGGER.getName());
/*  46 */     loggerBackend.setOnLevelChange((oldLevel, newLevel) -> {
/*     */           Universe universe = Universe.get();
/*     */           if (universe != null) {
/*     */             if (newLevel == Level.OFF) {
/*     */               for (PlayerRef p : universe.getPlayers()) {
/*     */                 uninjectLogger(p.getPacketHandler().getChannel());
/*     */               }
/*     */             } else {
/*     */               for (PlayerRef p : universe.getPlayers()) {
/*     */                 injectLogger(p.getPacketHandler().getChannel());
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*  60 */     PACKET_LOGGER.setLevel(Level.OFF);
/*  61 */     loggerBackend.loadLogLevel();
/*     */     
/*  63 */     CONNECTION_EXCEPTION_LOGGER.setLevel(Level.ALL);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  68 */   public static final PacketArrayEncoder PACKET_ARRAY_ENCODER_INSTANCE = new PacketArrayEncoder();
/*     */   public static final String PACKET_ENCODER = "packetEncoder";
/*     */   public static final String LOGGER_KEY = "logger";
/*  71 */   public static final LoggingHandler LOGGER = new LoggingHandler("PacketLogging", LogLevel.INFO);
/*     */   
/*     */   public static final String HANDLER = "handler";
/*     */   public static final String RATE_LIMIT = "rateLimit";
/*     */   
/*     */   public static void init() {}
/*     */   
/*     */   private static void injectLogger(@Nonnull Channel channel) {
/*  79 */     if (channel.pipeline().get("logger") == null) {
/*  80 */       channel.pipeline().addAfter("packetArrayEncoder", "logger", (ChannelHandler)LOGGER);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void uninjectLogger(@Nonnull Channel channel) {
/*  85 */     channel.pipeline().remove("logger");
/*     */   }
/*     */ 
/*     */   
/*     */   public static void setChannelHandler(@Nonnull Channel channel, @Nonnull PacketHandler packetHandler) {
/*  90 */     ChannelHandler oldHandler = channel.pipeline().replace("handler", "handler", (ChannelHandler)new PlayerChannelHandler(packetHandler));
/*     */     
/*  92 */     PacketHandler oldPlayerConnection = null;
/*  93 */     if (oldHandler instanceof PlayerChannelHandler) {
/*  94 */       oldPlayerConnection = ((PlayerChannelHandler)oldHandler).getHandler();
/*  95 */       oldPlayerConnection.unregistered(packetHandler);
/*     */     } 
/*     */     
/*  98 */     packetHandler.registered(oldPlayerConnection);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static EventLoopGroup getEventLoopGroup(String name) {
/* 103 */     return getEventLoopGroup(0, name);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static EventLoopGroup getEventLoopGroup(int nThreads, String name) {
/* 108 */     if (nThreads == 0) {
/* 109 */       nThreads = Math.max(1, SystemPropertyUtil.getInt("server.io.netty.eventLoopThreads", Runtime.getRuntime().availableProcessors() * 2));
/*     */     }
/* 111 */     ThreadFactory factory = ThreadUtil.daemonCounted(name + " - %d");
/* 112 */     if (Epoll.isAvailable())
/* 113 */       return (EventLoopGroup)new EpollEventLoopGroup(nThreads, factory); 
/* 114 */     if (KQueue.isAvailable()) {
/* 115 */       return (EventLoopGroup)new KQueueEventLoopGroup(nThreads, factory);
/*     */     }
/* 117 */     return (EventLoopGroup)new NioEventLoopGroup(nThreads, factory);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static Class<? extends ServerChannel> getServerChannel() {
/* 123 */     if (Epoll.isAvailable())
/* 124 */       return (Class)EpollServerSocketChannel.class; 
/* 125 */     if (KQueue.isAvailable()) {
/* 126 */       return (Class)KQueueServerSocketChannel.class;
/*     */     }
/* 128 */     return (Class)NioServerSocketChannel.class;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static ReflectiveChannelFactory<? extends DatagramChannel> getDatagramChannelFactory(SocketProtocolFamily family) {
/* 134 */     if (Epoll.isAvailable())
/* 135 */       return new ReflectiveChannelFactory(EpollDatagramChannel.class, family); 
/* 136 */     if (KQueue.isAvailable()) {
/* 137 */       return new ReflectiveChannelFactory(KQueueDatagramChannel.class, family);
/*     */     }
/* 139 */     return new ReflectiveChannelFactory(NioDatagramChannel.class, family);
/*     */   }
/*     */ 
/*     */   
/*     */   public static String formatRemoteAddress(Channel channel) {
/* 144 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/* 145 */       return String.valueOf(quicChannel.remoteAddress()) + " (" + String.valueOf(quicChannel.remoteAddress()) + ")"; }
/* 146 */      if (channel instanceof QuicStreamChannel) { QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
/*     */       
/* 148 */       return String.valueOf(quicStreamChannel.parent().localAddress()) + " (" + String.valueOf(quicStreamChannel.parent().localAddress()) + ", streamId=" + String.valueOf(quicStreamChannel.parent().remoteSocketAddress()) + ")"; }
/*     */     
/* 150 */     return channel.remoteAddress().toString();
/*     */   }
/*     */   
/*     */   public static String formatLocalAddress(Channel channel) {
/* 154 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/* 155 */       return String.valueOf(quicChannel.localAddress()) + " (" + String.valueOf(quicChannel.localAddress()) + ")"; }
/* 156 */      if (channel instanceof QuicStreamChannel) { QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
/* 157 */       return String.valueOf(quicStreamChannel.parent().localAddress()) + " (" + String.valueOf(quicStreamChannel.parent().localAddress()) + ", streamId=" + String.valueOf(quicStreamChannel.parent().localSocketAddress()) + ")"; }
/*     */     
/* 159 */     return channel.localAddress().toString();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static SocketAddress getRemoteSocketAddress(Channel channel) {
/* 164 */     if (channel instanceof QuicChannel) { QuicChannel quicChannel = (QuicChannel)channel;
/* 165 */       return quicChannel.remoteSocketAddress(); }
/* 166 */      if (channel instanceof QuicStreamChannel) { QuicStreamChannel quicStreamChannel = (QuicStreamChannel)channel;
/* 167 */       return quicStreamChannel.parent().remoteSocketAddress(); }
/*     */     
/* 169 */     return channel.remoteAddress();
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
/*     */   public static boolean isFromSameOrigin(Channel channel1, Channel channel2) {
/* 182 */     SocketAddress remoteSocketAddress1 = getRemoteSocketAddress(channel1);
/* 183 */     SocketAddress remoteSocketAddress2 = getRemoteSocketAddress(channel2);
/*     */ 
/*     */     
/* 186 */     if (remoteSocketAddress1 == null || remoteSocketAddress2 == null) return false;
/*     */ 
/*     */     
/* 189 */     if (Objects.equals(remoteSocketAddress1, remoteSocketAddress2)) return true;
/*     */ 
/*     */     
/* 192 */     if (!remoteSocketAddress1.getClass().equals(remoteSocketAddress2.getClass())) return false;
/*     */ 
/*     */     
/* 195 */     if (remoteSocketAddress1 instanceof InetSocketAddress) { InetSocketAddress remoteInetSocketAddress1 = (InetSocketAddress)remoteSocketAddress1; if (remoteSocketAddress2 instanceof InetSocketAddress) {
/* 196 */         InetSocketAddress remoteInetSocketAddress2 = (InetSocketAddress)remoteSocketAddress2;
/* 197 */         if (remoteInetSocketAddress1.getAddress().isLoopbackAddress() && remoteInetSocketAddress2.getAddress().isLoopbackAddress())
/*     */         {
/* 199 */           return true;
/*     */         }
/* 201 */         return remoteInetSocketAddress1.getAddress().equals(remoteInetSocketAddress2.getAddress());
/*     */       }  }
/*     */     
/* 204 */     return false;
/*     */   }
/*     */   
/*     */   public static class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {
/*     */     @Nonnull
/*     */     private final Constructor<? extends T> constructor;
/*     */     private final SocketProtocolFamily family;
/*     */     
/*     */     public ReflectiveChannelFactory(@Nonnull Class<? extends T> clazz, SocketProtocolFamily family) {
/* 213 */       ObjectUtil.checkNotNull(clazz, "clazz");
/*     */       try {
/* 215 */         this.constructor = clazz.getConstructor(new Class[] { SocketProtocolFamily.class });
/* 216 */         this.family = family;
/* 217 */       } catch (NoSuchMethodException e) {
/* 218 */         throw new IllegalArgumentException("Class " + StringUtil.simpleClassName(clazz) + " does not have a public non-arg constructor", e);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public T newChannel() {
/*     */       try {
/* 227 */         return this.constructor.newInstance(new Object[] { this.family });
/* 228 */       } catch (Throwable t) {
/* 229 */         throw new ChannelException("Unable to create Channel from class " + String.valueOf(this.constructor.getDeclaringClass()), t);
/*     */       } 
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     public String getSimpleName() {
/* 235 */       return StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + "(" + StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ")";
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 241 */       return StringUtil.simpleClassName(io.netty.channel.ReflectiveChannelFactory.class) + "(" + StringUtil.simpleClassName(io.netty.channel.ReflectiveChannelFactory.class) + ".class, " + 
/* 242 */         StringUtil.simpleClassName(this.constructor.getDeclaringClass()) + ")";
/*     */     } }
/*     */   public static final class TimeoutContext extends Record { @Nonnull
/*     */     private final String stage; private final long connectionStartNs; @Nonnull
/*     */     private final String playerIdentifier;
/*     */     public final String toString() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #254	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;
/*     */     }
/*     */     public final int hashCode() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #254	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;
/*     */     }
/*     */     
/* 254 */     public TimeoutContext(@Nonnull String stage, long connectionStartNs, @Nonnull String playerIdentifier) { this.stage = stage; this.connectionStartNs = connectionStartNs; this.playerIdentifier = playerIdentifier; } @Nonnull public String stage() { return this.stage; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #254	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/io/netty/NettyUtil$TimeoutContext;
/* 254 */       //   0	8	1	o	Ljava/lang/Object; } public long connectionStartNs() { return this.connectionStartNs; } @Nonnull public String playerIdentifier() { return this.playerIdentifier; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 259 */     public static final AttributeKey<TimeoutContext> KEY = AttributeKey.newInstance("TIMEOUT_CONTEXT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static void init(@Nonnull Channel channel, @Nonnull String stage, @Nonnull String identifier) {
/* 270 */       channel.attr(KEY).set(new TimeoutContext(stage, System.nanoTime(), identifier));
/*     */     }
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
/*     */     public static void update(@Nonnull Channel channel, @Nonnull String stage, @Nonnull String identifier) {
/* 283 */       TimeoutContext existing = get(channel);
/* 284 */       channel.attr(KEY).set(new TimeoutContext(stage, existing.connectionStartNs, identifier));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static void update(@Nonnull Channel channel, @Nonnull String stage) {
/* 296 */       TimeoutContext existing = get(channel);
/* 297 */       channel.attr(KEY).set(new TimeoutContext(stage, existing.connectionStartNs, existing.playerIdentifier));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public static TimeoutContext get(@Nonnull Channel channel) {
/* 309 */       TimeoutContext context = (TimeoutContext)channel.attr(KEY).get();
/* 310 */       if (context == null) {
/* 311 */         throw new IllegalStateException("TimeoutContext not initialized - this indicates a bug in the connection flow");
/*     */       }
/* 313 */       return context;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\netty\NettyUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */