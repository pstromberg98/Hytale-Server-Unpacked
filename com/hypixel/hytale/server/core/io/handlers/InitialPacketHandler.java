/*     */ package com.hypixel.hytale.server.core.io.handlers;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
/*     */ import com.hypixel.hytale.protocol.packets.auth.ConnectAccept;
/*     */ import com.hypixel.hytale.protocol.packets.connection.ClientType;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Connect;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*     */ import com.hypixel.hytale.server.core.Constants;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.Options;
/*     */ import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.ProtocolVersion;
/*     */ import com.hypixel.hytale.server.core.io.handlers.login.AuthenticationPacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.handlers.login.PasswordPacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.netty.NettyUtil;
/*     */ import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginManager;
/*     */ import io.netty.channel.Channel;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class InitialPacketHandler
/*     */   extends PacketHandler
/*     */ {
/*     */   private static final int MAX_REFERRAL_DATA_SIZE = 4096;
/*     */   @Nullable
/*     */   public static AuthenticationPacketHandler.AuthHandlerSupplier EDITOR_PACKET_HANDLER_SUPPLIER;
/*     */   private boolean receivedConnect;
/*     */   
/*     */   public InitialPacketHandler(@Nonnull Channel channel) {
/*  46 */     super(channel, null);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public String getIdentifier() {
/*  51 */     return "{Initial(" + NettyUtil.formatRemoteAddress(this.channel) + ")}";
/*     */   }
/*     */ 
/*     */   
/*     */   public void registered0(PacketHandler oldHandler) {
/*  56 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/*  57 */     initStage("initial", timeouts.getInitial(), () -> !this.registered);
/*  58 */     PacketHandler.logConnectionTimings(this.channel, "Registered", Level.FINE);
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(@Nonnull Packet packet) {
/*  63 */     if (packet.getId() == 0) {
/*  64 */       handle((Connect)packet);
/*  65 */     } else if (packet.getId() == 1) {
/*  66 */       handle((Disconnect)packet);
/*     */     } else {
/*  68 */       disconnect("Protocol error: unexpected packet " + packet.getId());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void disconnect(@Nonnull String message) {
/*  74 */     if (this.receivedConnect) {
/*  75 */       super.disconnect(message);
/*     */     } else {
/*  77 */       HytaleLogger.getLogger().at(Level.INFO).log("Silently disconnecting %s because no Connect packet!", NettyUtil.formatRemoteAddress(this.channel));
/*  78 */       ProtocolUtil.closeConnection(this.channel);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull Connect packet) {
/*  83 */     this.receivedConnect = true;
/*  84 */     clearTimeout();
/*     */     
/*  86 */     PacketHandler.logConnectionTimings(this.channel, "Connect", Level.FINE);
/*     */     
/*  88 */     if (packet.protocolCrc != 1789265863) {
/*     */       
/*  90 */       int errorCode, clientBuild = packet.protocolBuildNumber;
/*  91 */       int serverBuild = 2;
/*     */ 
/*     */       
/*  94 */       if (clientBuild < serverBuild) {
/*  95 */         errorCode = 5;
/*     */       } else {
/*  97 */         errorCode = 6;
/*     */       } 
/*     */       
/* 100 */       String serverVersion = ManifestUtil.getImplementationVersion();
/* 101 */       ProtocolUtil.closeApplicationConnection(this.channel, errorCode, (serverVersion != null) ? serverVersion : "unknown");
/*     */       
/*     */       return;
/*     */     } 
/* 105 */     if (HytaleServer.get().isShuttingDown()) {
/* 106 */       disconnect("Server is shutting down!");
/*     */       
/*     */       return;
/*     */     } 
/* 110 */     if (!HytaleServer.get().isBooted()) {
/* 111 */       disconnect("Server is booting up! Please try again in a moment. [" + String.valueOf(PluginManager.get().getState()) + "]");
/*     */       
/*     */       return;
/*     */     } 
/* 115 */     ProtocolVersion protocolVersion = new ProtocolVersion(packet.protocolCrc);
/*     */     
/* 117 */     String language = packet.language;
/* 118 */     if (language == null) language = "en-US";
/*     */ 
/*     */     
/* 121 */     boolean isTcpConnection = !(this.channel instanceof io.netty.handler.codec.quic.QuicStreamChannel);
/* 122 */     if (isTcpConnection) {
/* 123 */       HytaleLogger.getLogger().at(Level.INFO).log("TCP connection from %s - only insecure auth supported", 
/* 124 */           NettyUtil.formatRemoteAddress(this.channel));
/*     */     }
/*     */ 
/*     */     
/* 128 */     if (packet.uuid == null) {
/* 129 */       disconnect("Missing UUID");
/*     */       return;
/*     */     } 
/* 132 */     if (packet.username == null || packet.username.isEmpty()) {
/* 133 */       disconnect("Missing username");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 138 */     if (packet.referralData != null && packet.referralData.length > 4096) {
/* 139 */       HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting connection from %s - referral data too large: %d bytes (max: %d)", packet.username, 
/*     */           
/* 141 */           Integer.valueOf(packet.referralData.length), Integer.valueOf(4096));
/* 142 */       disconnect("Referral data exceeds maximum size of 4096 bytes");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 147 */     if (packet.referralData != null) {
/* 148 */       if (packet.referralSource == null) {
/* 149 */         HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting connection from %s - referral data provided without source address", packet.username);
/*     */ 
/*     */         
/* 152 */         disconnect("Referral connections must include source server address");
/*     */         return;
/*     */       } 
/* 155 */       if (packet.referralSource.host == null || packet.referralSource.host.isEmpty()) {
/* 156 */         HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting connection from %s - referral source has empty host", packet.username);
/*     */ 
/*     */         
/* 159 */         disconnect("Referral source address is invalid");
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 165 */     boolean hasIdentityToken = (packet.identityToken != null && !packet.identityToken.isEmpty());
/* 166 */     boolean isEditorClient = (packet.clientType == ClientType.Editor);
/*     */     
/* 168 */     Options.AuthMode authMode = (Options.AuthMode)Options.getOptionSet().valueOf(Options.AUTH_MODE);
/* 169 */     if (hasIdentityToken && authMode == Options.AuthMode.AUTHENTICATED) {
/*     */       
/* 171 */       if (isTcpConnection) {
/* 172 */         HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting authenticated connection from %s - TCP only supports insecure auth", 
/*     */             
/* 174 */             NettyUtil.formatRemoteAddress(this.channel));
/* 175 */         disconnect("TCP connections only support insecure authentication. Use QUIC for authenticated connections.");
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 181 */       AuthenticationPacketHandler.AuthHandlerSupplier supplier = isEditorClient ? EDITOR_PACKET_HANDLER_SUPPLIER : SetupPacketHandler::new;
/* 182 */       if (isEditorClient && supplier == null) {
/* 183 */         disconnect("Editor isn't supported on this server!");
/*     */         
/*     */         return;
/*     */       } 
/* 187 */       HytaleLogger.getLogger().at(Level.INFO).log("Starting authenticated flow for %s (%s) from %s", packet.username, packet.uuid, 
/* 188 */           NettyUtil.formatRemoteAddress(this.channel));
/*     */       
/* 190 */       NettyUtil.setChannelHandler(this.channel, (PacketHandler)new AuthenticationPacketHandler(this.channel, protocolVersion, language, supplier, packet.clientType, packet.identityToken, packet.uuid, packet.username, packet.referralData, packet.referralSource));
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 196 */       if (authMode == Options.AuthMode.AUTHENTICATED) {
/* 197 */         HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting development connection from %s - server requires authentication (auth-mode=%s)", 
/*     */             
/* 199 */             NettyUtil.formatRemoteAddress(this.channel), authMode);
/* 200 */         disconnect("This server requires authentication!");
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 205 */       if (authMode == Options.AuthMode.OFFLINE) {
/* 206 */         if (!Constants.SINGLEPLAYER) {
/* 207 */           HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting connection from %s - offline mode is only valid in singleplayer", 
/*     */               
/* 209 */               NettyUtil.formatRemoteAddress(this.channel));
/* 210 */           disconnect("Offline mode is only available in singleplayer.");
/*     */           return;
/*     */         } 
/* 213 */         if (!SingleplayerModule.isOwner(null, packet.uuid)) {
/* 214 */           HytaleLogger.getLogger().at(Level.WARNING).log("Rejecting connection from %s (%s) - offline mode only allows the world owner (%s)", packet.username, packet.uuid, 
/*     */               
/* 216 */               SingleplayerModule.getUuid());
/* 217 */           disconnect("This world is in offline mode and only the owner can connect.");
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/* 222 */       HytaleLogger.getLogger().at(Level.INFO).log("Starting development flow for %s (%s) from %s", packet.username, packet.uuid, 
/* 223 */           NettyUtil.formatRemoteAddress(this.channel));
/*     */ 
/*     */       
/* 226 */       byte[] passwordChallenge = generatePasswordChallengeIfNeeded(packet.uuid);
/* 227 */       write((Packet)new ConnectAccept(passwordChallenge));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 233 */       PasswordPacketHandler.SetupHandlerSupplier setupSupplier = (isEditorClient && EDITOR_PACKET_HANDLER_SUPPLIER != null) ? ((ch, pv, lang, auth) -> EDITOR_PACKET_HANDLER_SUPPLIER.create(ch, pv, lang, auth)) : SetupPacketHandler::new;
/*     */       
/* 235 */       NettyUtil.setChannelHandler(this.channel, (PacketHandler)new PasswordPacketHandler(this.channel, protocolVersion, language, packet.uuid, packet.username, packet.referralData, packet.referralSource, passwordChallenge, setupSupplier));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private byte[] generatePasswordChallengeIfNeeded(UUID playerUuid) {
/* 244 */     String password = HytaleServer.get().getConfig().getPassword();
/* 245 */     if (password == null || password.isEmpty()) {
/* 246 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 250 */     if (Constants.SINGLEPLAYER) {
/* 251 */       UUID ownerUuid = SingleplayerModule.getUuid();
/* 252 */       if (ownerUuid != null && ownerUuid.equals(playerUuid)) {
/* 253 */         return null;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 258 */     byte[] challenge = new byte[32];
/* 259 */     (new SecureRandom()).nextBytes(challenge);
/* 260 */     return challenge;
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull Disconnect packet) {
/* 264 */     this.disconnectReason.setClientDisconnectType(packet.type);
/* 265 */     HytaleLogger.getLogger().at(Level.WARNING).log("Disconnecting %s - Sent disconnect packet???", NettyUtil.formatRemoteAddress(this.channel));
/* 266 */     ProtocolUtil.closeApplicationConnection(this.channel);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\handlers\InitialPacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */