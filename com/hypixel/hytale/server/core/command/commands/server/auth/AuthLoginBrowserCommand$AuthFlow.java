/*    */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.auth.oauth.OAuthBrowserFlow;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import java.awt.Desktop;
/*    */ import java.net.URI;
/*    */ import java.util.logging.Level;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class AuthFlow
/*    */   extends OAuthBrowserFlow
/*    */ {
/*    */   public void onFlowInfo(String authUrl) {
/* 39 */     AbstractCommand.LOGGER.at(Level.INFO).log("Starting OAuth browser flow...");
/* 40 */     AbstractCommand.LOGGER.at(Level.INFO).log("===================================================================");
/* 41 */     AbstractCommand.LOGGER.at(Level.INFO).log("Please open this URL in your browser to authenticate:");
/* 42 */     AbstractCommand.LOGGER.at(Level.INFO).log("%s", authUrl);
/* 43 */     AbstractCommand.LOGGER.at(Level.INFO).log("===================================================================");
/*    */ 
/*    */     
/* 46 */     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
/*    */       try {
/* 48 */         Desktop.getDesktop().browse(new URI(authUrl));
/* 49 */         AbstractCommand.LOGGER.at(Level.INFO).log("Browser opened automatically.");
/* 50 */       } catch (Exception e) {
/* 51 */         AbstractCommand.LOGGER.at(Level.INFO).log("Could not open browser automatically. Please open the URL manually.");
/*    */       }  
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthLoginBrowserCommand$AuthFlow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */