/*    */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import java.awt.Color;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthLogoutCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 17 */   private static final Message MESSAGE_SINGLEPLAYER = Message.translation("server.commands.auth.logout.singleplayer").color(Color.RED);
/*    */   @Nonnull
/* 19 */   private static final Message MESSAGE_NOT_AUTHENTICATED = Message.translation("server.commands.auth.logout.notAuthenticated").color(Color.YELLOW);
/*    */   
/*    */   public AuthLogoutCommand() {
/* 22 */     super("logout", "server.commands.auth.logout.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 27 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/*    */     
/* 29 */     if (authManager.isSingleplayer()) {
/* 30 */       context.sendMessage(MESSAGE_SINGLEPLAYER);
/*    */       
/*    */       return;
/*    */     } 
/* 34 */     if (!authManager.hasIdentityToken() && !authManager.hasSessionToken()) {
/* 35 */       context.sendMessage(MESSAGE_NOT_AUTHENTICATED);
/*    */       
/*    */       return;
/*    */     } 
/* 39 */     ServerAuthManager.AuthMode previousMode = authManager.getAuthMode();
/* 40 */     authManager.logout();
/* 41 */     context.sendMessage(Message.translation("server.commands.auth.logout.success")
/* 42 */         .color(Color.GREEN)
/* 43 */         .param("previousMode", previousMode.name()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthLogoutCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */