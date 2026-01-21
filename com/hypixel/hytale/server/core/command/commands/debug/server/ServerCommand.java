/*    */ package com.hypixel.hytale.server.core.command.commands.debug.server;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServerCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public ServerCommand() {
/* 14 */     super("server", "server.commands.server.desc");
/* 15 */     addSubCommand((AbstractCommand)new ServerStatsCommand());
/* 16 */     addSubCommand((AbstractCommand)new ServerGCCommand());
/* 17 */     addSubCommand((AbstractCommand)new ServerDumpCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\server\ServerCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */