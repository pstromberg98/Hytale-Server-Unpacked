/*    */ package com.hypixel.hytale.server.core.modules.accesscontrol.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleWhitelistProvider;
/*    */ import com.hypixel.hytale.server.core.util.message.MessageFormat;
/*    */ import javax.annotation.Nonnull;
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
/*    */ public class WhitelistStatusCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/*    */   private final HytaleWhitelistProvider whitelistProvider;
/*    */   
/*    */   public WhitelistStatusCommand(@Nonnull HytaleWhitelistProvider whitelistProvider) {
/* 27 */     super("status", "server.commands.whitelist.status.desc");
/* 28 */     this.whitelistProvider = whitelistProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 33 */     context.sendMessage(Message.translation("server.modules.whitelist.status")
/* 34 */         .param("status", MessageFormat.enabled(this.whitelistProvider.isEnabled())));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\commands\WhitelistStatusCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */