/*     */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*     */ 
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.auth.SessionServiceClient;
/*     */ import com.hypixel.hytale.server.core.auth.oauth.OAuthBrowserFlow;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import java.awt.Color;
/*     */ import java.awt.Desktop;
/*     */ import java.net.URI;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AuthLoginBrowserCommand
/*     */   extends CommandBase
/*     */ {
/*     */   @Nonnull
/*  24 */   private static final Message MESSAGE_SINGLEPLAYER = Message.translation("server.commands.auth.login.singleplayer").color(Color.RED);
/*     */   @Nonnull
/*  26 */   private static final Message MESSAGE_ALREADY_AUTHENTICATED = Message.translation("server.commands.auth.login.alreadyAuthenticated").color(Color.YELLOW);
/*     */   @Nonnull
/*  28 */   private static final Message MESSAGE_STARTING = Message.translation("server.commands.auth.login.browser.starting").color(Color.YELLOW);
/*     */   @Nonnull
/*  30 */   private static final Message MESSAGE_SUCCESS = Message.translation("server.commands.auth.login.browser.success").color(Color.GREEN);
/*     */   @Nonnull
/*  32 */   private static final Message MESSAGE_FAILED = Message.translation("server.commands.auth.login.browser.failed").color(Color.RED);
/*     */   @Nonnull
/*  34 */   private static final Message MESSAGE_PENDING = Message.translation("server.commands.auth.login.pending").color(Color.YELLOW);
/*     */   
/*     */   private static class AuthFlow
/*     */     extends OAuthBrowserFlow {
/*     */     public void onFlowInfo(String authUrl) {
/*  39 */       AbstractCommand.LOGGER.at(Level.INFO).log("Starting OAuth browser flow...");
/*  40 */       AbstractCommand.LOGGER.at(Level.INFO).log("===================================================================");
/*  41 */       AbstractCommand.LOGGER.at(Level.INFO).log("Please open this URL in your browser to authenticate:");
/*  42 */       AbstractCommand.LOGGER.at(Level.INFO).log("%s", authUrl);
/*  43 */       AbstractCommand.LOGGER.at(Level.INFO).log("===================================================================");
/*     */ 
/*     */       
/*  46 */       if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
/*     */         try {
/*  48 */           Desktop.getDesktop().browse(new URI(authUrl));
/*  49 */           AbstractCommand.LOGGER.at(Level.INFO).log("Browser opened automatically.");
/*  50 */         } catch (Exception e) {
/*  51 */           AbstractCommand.LOGGER.at(Level.INFO).log("Could not open browser automatically. Please open the URL manually.");
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public AuthLoginBrowserCommand() {
/*  58 */     super("browser", "server.commands.auth.login.browser.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*  63 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/*     */     
/*  65 */     if (authManager.isSingleplayer()) {
/*  66 */       context.sendMessage(MESSAGE_SINGLEPLAYER);
/*     */       
/*     */       return;
/*     */     } 
/*  70 */     if (authManager.hasSessionToken() && authManager.hasIdentityToken()) {
/*  71 */       context.sendMessage(MESSAGE_ALREADY_AUTHENTICATED);
/*     */       
/*     */       return;
/*     */     } 
/*  75 */     context.sendMessage(MESSAGE_STARTING);
/*     */     
/*  77 */     authManager.startFlowAsync(new AuthFlow()).thenAccept(result -> {
/*     */           SessionServiceClient.GameProfile[] profiles;
/*     */           switch (result) {
/*     */             case SUCCESS:
/*     */               context.sendMessage(MESSAGE_SUCCESS);
/*     */               sendPersistenceFeedback(context);
/*     */               break;
/*     */             case PENDING_PROFILE_SELECTION:
/*     */               context.sendMessage(MESSAGE_PENDING);
/*     */               profiles = authManager.getPendingProfiles();
/*     */               if (profiles != null)
/*     */                 AuthSelectCommand.sendProfileList(context, profiles); 
/*     */               break;
/*     */             case FAILED:
/*     */               context.sendMessage(MESSAGE_FAILED);
/*     */               break;
/*     */           } 
/*     */         });
/*     */   } static void sendPersistenceFeedback(@Nonnull CommandContext context) {
/*  96 */     AuthCredentialStoreProvider provider = HytaleServer.get().getConfig().getAuthCredentialStoreProvider();
/*  97 */     if (provider instanceof com.hypixel.hytale.server.core.auth.MemoryAuthCredentialStoreProvider) {
/*  98 */       String availableTypes = String.join(", ", AuthCredentialStoreProvider.CODEC.getRegisteredIds());
/*  99 */       context.sendMessage(Message.translation("server.commands.auth.login.persistence.memory")
/* 100 */           .color(Color.ORANGE)
/* 101 */           .param("types", availableTypes));
/*     */     } else {
/* 103 */       String typeName = (String)AuthCredentialStoreProvider.CODEC.getIdFor(provider.getClass());
/* 104 */       context.sendMessage(Message.translation("server.commands.auth.login.persistence.saved")
/* 105 */           .color(Color.GREEN)
/* 106 */           .param("type", typeName));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthLoginBrowserCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */