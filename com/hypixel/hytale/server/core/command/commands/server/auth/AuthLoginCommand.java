/*    */ package com.hypixel.hytale.server.core.command.commands.server.auth;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ public class AuthLoginCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public AuthLoginCommand() {
/* 11 */     super("login", "server.commands.auth.login.desc");
/* 12 */     addSubCommand((AbstractCommand)new AuthLoginBrowserCommand());
/* 13 */     addSubCommand((AbstractCommand)new AuthLoginDeviceCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\auth\AuthLoginCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */