/*     */ package com.hypixel.hytale.server.core.io.handlers.login;
/*     */ 
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.HostAddress;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
/*     */ import com.hypixel.hytale.protocol.packets.auth.PasswordAccepted;
/*     */ import com.hypixel.hytale.protocol.packets.auth.PasswordRejected;
/*     */ import com.hypixel.hytale.protocol.packets.auth.PasswordResponse;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.ProtocolVersion;
/*     */ import com.hypixel.hytale.server.core.io.handlers.GenericConnectionPacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.netty.NettyUtil;
/*     */ import io.netty.channel.Channel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PasswordPacketHandler
/*     */   extends GenericConnectionPacketHandler
/*     */ {
/*  33 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private static final int MAX_PASSWORD_ATTEMPTS = 3;
/*     */   private static final int CHALLENGE_LENGTH = 32;
/*     */   private final UUID playerUuid;
/*     */   private final String username;
/*     */   private final byte[] referralData;
/*     */   private final HostAddress referralSource;
/*     */   private byte[] passwordChallenge;
/*     */   private final SetupHandlerSupplier setupHandlerSupplier;
/*  43 */   private int attemptsRemaining = 3;
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
/*     */   public PasswordPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, @Nonnull String language, @Nonnull UUID playerUuid, @Nonnull String username, @Nullable byte[] referralData, @Nullable HostAddress referralSource, @Nullable byte[] passwordChallenge, @Nonnull SetupHandlerSupplier setupHandlerSupplier) {
/*  56 */     super(channel, protocolVersion, language);
/*  57 */     this.playerUuid = playerUuid;
/*  58 */     this.username = username;
/*  59 */     this.referralData = referralData;
/*  60 */     this.referralSource = referralSource;
/*  61 */     this.passwordChallenge = passwordChallenge;
/*  62 */     this.setupHandlerSupplier = setupHandlerSupplier;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public String getIdentifier() {
/*  67 */     return "{Password(" + NettyUtil.formatRemoteAddress(this.channel) + "), " + this.username + "}";
/*     */   }
/*     */ 
/*     */   
/*     */   public void registered0(PacketHandler oldHandler) {
/*  72 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/*     */     
/*  74 */     if (this.passwordChallenge == null || this.passwordChallenge.length == 0) {
/*     */       
/*  76 */       LOGGER.at(Level.FINE).log("No password required for %s, proceeding to setup", this.username);
/*  77 */       proceedToSetup();
/*     */     } else {
/*     */       
/*  80 */       LOGGER.at(Level.FINE).log("Waiting for password response from %s", this.username);
/*  81 */       enterStage("password", timeouts.getPassword(), () -> !this.registered);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(@Nonnull Packet packet) {
/*  87 */     switch (packet.getId()) { case 1:
/*  88 */         handle((Disconnect)packet); return;
/*  89 */       case 15: handle((PasswordResponse)packet); return; }
/*  90 */      disconnect("Protocol error: unexpected packet " + packet.getId());
/*     */   }
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull Disconnect packet) {
/*  95 */     this.disconnectReason.setClientDisconnectType(packet.type);
/*  96 */     LOGGER.at(Level.INFO).log("%s (%s) at %s left with reason: %s - %s", this.playerUuid, this.username, 
/*     */         
/*  98 */         NettyUtil.formatRemoteAddress(this.channel), packet.type
/*  99 */         .name(), packet.reason);
/* 100 */     ProtocolUtil.closeApplicationConnection(this.channel);
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull PasswordResponse packet) {
/* 104 */     clearTimeout();
/*     */     
/* 106 */     if (this.passwordChallenge == null || this.passwordChallenge.length == 0) {
/* 107 */       LOGGER.at(Level.WARNING).log("Received unexpected PasswordResponse from %s - no password required", 
/* 108 */           NettyUtil.formatRemoteAddress(this.channel));
/* 109 */       disconnect("Protocol error: unexpected PasswordResponse");
/*     */       
/*     */       return;
/*     */     } 
/* 113 */     byte[] clientHash = packet.hash;
/* 114 */     if (clientHash == null || clientHash.length == 0) {
/* 115 */       LOGGER.at(Level.WARNING).log("Received empty password hash from %s", 
/* 116 */           NettyUtil.formatRemoteAddress(this.channel));
/* 117 */       disconnect("Invalid password response");
/*     */       
/*     */       return;
/*     */     } 
/* 121 */     String password = HytaleServer.get().getConfig().getPassword();
/* 122 */     if (password == null || password.isEmpty()) {
/*     */       
/* 124 */       LOGGER.at(Level.SEVERE).log("Password validation failed - no password configured but challenge was sent");
/* 125 */       disconnect("Server configuration error");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 130 */     byte[] expectedHash = computePasswordHash(this.passwordChallenge, password);
/* 131 */     if (expectedHash == null) {
/* 132 */       LOGGER.at(Level.SEVERE).log("Failed to compute password hash");
/* 133 */       disconnect("Server error");
/*     */       
/*     */       return;
/*     */     } 
/* 137 */     if (!MessageDigest.isEqual(expectedHash, clientHash)) {
/* 138 */       this.attemptsRemaining--;
/* 139 */       LOGGER.at(Level.WARNING).log("Invalid password from %s (%s), %d attempts remaining", this.username, 
/* 140 */           NettyUtil.formatRemoteAddress(this.channel), Integer.valueOf(this.attemptsRemaining));
/*     */       
/* 142 */       if (this.attemptsRemaining <= 0) {
/* 143 */         disconnect("Too many failed password attempts");
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 148 */       this.passwordChallenge = generateChallenge();
/* 149 */       write((Packet)new PasswordRejected(this.passwordChallenge, this.attemptsRemaining));
/*     */ 
/*     */       
/* 152 */       HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/* 153 */       continueStage("password", timeouts.getPassword(), () -> !this.registered);
/*     */       
/*     */       return;
/*     */     } 
/* 157 */     LOGGER.at(Level.INFO).log("Password accepted for %s (%s)", this.username, this.playerUuid);
/* 158 */     write((Packet)new PasswordAccepted());
/* 159 */     proceedToSetup();
/*     */   }
/*     */   
/*     */   private static byte[] generateChallenge() {
/* 163 */     byte[] challenge = new byte[32];
/* 164 */     (new SecureRandom()).nextBytes(challenge);
/* 165 */     return challenge;
/*     */   }
/*     */ 
/*     */   
/*     */   private void proceedToSetup() {
/* 170 */     this.auth = new PlayerAuthentication(this.playerUuid, this.username);
/* 171 */     if (this.referralData != null) {
/* 172 */       this.auth.setReferralData(this.referralData);
/*     */     }
/* 174 */     if (this.referralSource != null) {
/* 175 */       this.auth.setReferralSource(this.referralSource);
/*     */     }
/*     */     
/* 178 */     LOGGER.at(Level.INFO).log("Connection complete for %s (%s), transitioning to setup", this.username, this.playerUuid);
/*     */ 
/*     */     
/* 181 */     NettyUtil.setChannelHandler(this.channel, this.setupHandlerSupplier.create(this.channel, this.protocolVersion, this.language, this.auth));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static byte[] computePasswordHash(byte[] challenge, String password) {
/*     */     try {
/* 189 */       MessageDigest digest = MessageDigest.getInstance("SHA-256");
/* 190 */       digest.update(challenge);
/* 191 */       digest.update(password.getBytes(StandardCharsets.UTF_8));
/* 192 */       return digest.digest();
/* 193 */     } catch (NoSuchAlgorithmException e) {
/* 194 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface SetupHandlerSupplier {
/*     */     PacketHandler create(Channel param1Channel, ProtocolVersion param1ProtocolVersion, String param1String, PlayerAuthentication param1PlayerAuthentication);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\handlers\login\PasswordPacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */