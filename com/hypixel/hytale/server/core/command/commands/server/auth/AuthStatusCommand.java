/*     */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*     */ 
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.Options;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.auth.SessionServiceClient;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import java.awt.Color;
/*     */ import java.time.Instant;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AuthStatusCommand
/*     */   extends CommandBase
/*     */ {
/*     */   @Nonnull
/*  20 */   private static final Message MESSAGE_STATUS_CONNECTION_MODE_AUTHENTICATED = Message.translation("server.commands.auth.status.connectionMode.authenticated").color(Color.GREEN);
/*     */   @Nonnull
/*  22 */   private static final Message MESSAGE_STATUS_CONNECTION_MODE_OFFLINE = Message.translation("server.commands.auth.status.connectionMode.offline").color(Color.YELLOW);
/*     */   @Nonnull
/*  24 */   private static final Message MESSAGE_STATUS_CONNECTION_MODE_INSECURE = Message.translation("server.commands.auth.status.connectionMode.insecure").color(Color.ORANGE);
/*     */   @Nonnull
/*  26 */   private static final Message MESSAGE_STATUS_MODE_NONE = Message.translation("server.commands.auth.status.mode.none").color(Color.RED);
/*     */   @Nonnull
/*  28 */   private static final Message MESSAGE_STATUS_MODE_SINGLEPLAYER = Message.translation("server.commands.auth.status.mode.singleplayer").color(Color.GREEN);
/*     */   @Nonnull
/*  30 */   private static final Message MESSAGE_STATUS_MODE_EXTERNAL = Message.translation("server.commands.auth.status.mode.external").color(Color.GREEN);
/*     */   @Nonnull
/*  32 */   private static final Message MESSAGE_STATUS_MODE_OAUTH_BROWSER = Message.translation("server.commands.auth.status.mode.oauthBrowser").color(Color.GREEN);
/*     */   @Nonnull
/*  34 */   private static final Message MESSAGE_STATUS_MODE_OAUTH_DEVICE = Message.translation("server.commands.auth.status.mode.oauthDevice").color(Color.GREEN);
/*     */   @Nonnull
/*  36 */   private static final Message MESSAGE_STATUS_MODE_OAUTH_STORE = Message.translation("server.commands.auth.status.mode.oauthStore").color(Color.GREEN);
/*     */   @Nonnull
/*  38 */   private static final Message MESSAGE_STATUS_TOKEN_PRESENT = Message.translation("server.commands.auth.status.tokenPresent").color(Color.GREEN);
/*     */   @Nonnull
/*  40 */   private static final Message MESSAGE_STATUS_TOKEN_MISSING = Message.translation("server.commands.auth.status.tokenMissing").color(Color.RED);
/*     */   @Nonnull
/*  42 */   private static final Message MESSAGE_STATUS_HELP = Message.translation("server.commands.auth.status.help").color(Color.YELLOW);
/*     */   @Nonnull
/*  44 */   private static final Message MESSAGE_STATUS_PENDING = Message.translation("server.commands.auth.status.pending").color(Color.YELLOW);
/*     */   
/*     */   public AuthStatusCommand() {
/*  47 */     super("status", "server.commands.auth.status.desc");
/*     */   }
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*     */     String certificateStatus;
/*  52 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/*     */     
/*  54 */     switch ((Options.AuthMode)Options.getOptionSet().valueOf(Options.AUTH_MODE)) { default: throw new MatchException(null, null);case NONE: case SINGLEPLAYER: case EXTERNAL_SESSION: break; }  Message connectionModeMessage = 
/*     */ 
/*     */       
/*  57 */       MESSAGE_STATUS_CONNECTION_MODE_INSECURE;
/*     */ 
/*     */     
/*  60 */     ServerAuthManager.AuthMode mode = authManager.getAuthMode();
/*  61 */     switch (mode) { default: throw new MatchException(null, null);case NONE: case SINGLEPLAYER: case EXTERNAL_SESSION: case OAUTH_BROWSER: case OAUTH_DEVICE: case OAUTH_STORE: break; }  Message modeMessage = 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  67 */       MESSAGE_STATUS_MODE_OAUTH_STORE;
/*     */ 
/*     */     
/*  70 */     String profileInfo = "";
/*  71 */     SessionServiceClient.GameProfile profile = authManager.getSelectedProfile();
/*  72 */     if (profile != null) {
/*  73 */       String name = (profile.username != null) ? profile.username : "Unknown";
/*  74 */       profileInfo = name + " (" + name + ")";
/*     */     } 
/*     */     
/*  77 */     Message sessionTokenStatus = authManager.hasSessionToken() ? MESSAGE_STATUS_TOKEN_PRESENT : MESSAGE_STATUS_TOKEN_MISSING;
/*  78 */     Message identityTokenStatus = authManager.hasIdentityToken() ? MESSAGE_STATUS_TOKEN_PRESENT : MESSAGE_STATUS_TOKEN_MISSING;
/*     */     
/*  80 */     String expiryStatus = "";
/*  81 */     Instant expiry = authManager.getTokenExpiry();
/*  82 */     if (expiry != null) {
/*  83 */       long secondsRemaining = expiry.getEpochSecond() - Instant.now().getEpochSecond();
/*  84 */       if (secondsRemaining > 0L) {
/*  85 */         long hours = secondsRemaining / 3600L;
/*  86 */         long minutes = secondsRemaining % 3600L / 60L;
/*  87 */         long seconds = secondsRemaining % 60L;
/*  88 */         expiryStatus = String.format("%02d:%02d:%02d remaining", new Object[] { Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds) });
/*     */       } else {
/*  90 */         expiryStatus = "EXPIRED";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  95 */     if (authManager.getServerCertificate() != null) {
/*  96 */       String fingerprint = authManager.getServerCertificateFingerprint();
/*  97 */       certificateStatus = (fingerprint != null) ? (fingerprint.substring(0, 16) + "...") : "Unknown";
/*     */     } else {
/*  99 */       certificateStatus = "Not loaded";
/*     */     } 
/*     */     
/* 102 */     context.sendMessage(Message.translation("server.commands.auth.status.format")
/* 103 */         .param("connectionMode", connectionModeMessage)
/* 104 */         .param("tokenMode", modeMessage)
/* 105 */         .param("profile", profileInfo)
/* 106 */         .param("sessionToken", sessionTokenStatus)
/* 107 */         .param("identityToken", identityTokenStatus)
/* 108 */         .param("expiry", expiryStatus)
/* 109 */         .param("certificate", certificateStatus));
/*     */     
/* 111 */     if (mode == ServerAuthManager.AuthMode.NONE && !authManager.isSingleplayer())
/* 112 */       if (authManager.hasPendingProfiles()) {
/* 113 */         context.sendMessage(MESSAGE_STATUS_PENDING);
/* 114 */         SessionServiceClient.GameProfile[] profiles = authManager.getPendingProfiles();
/* 115 */         if (profiles != null) {
/* 116 */           AuthSelectCommand.sendProfileList(context, profiles);
/*     */         }
/*     */       } else {
/* 119 */         context.sendMessage(MESSAGE_STATUS_HELP);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthStatusCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */