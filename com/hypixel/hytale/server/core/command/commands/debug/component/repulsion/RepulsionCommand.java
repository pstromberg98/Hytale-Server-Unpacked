/*    */ package com.hypixel.hytale.server.core.command.commands.debug.component.repulsion;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RepulsionCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public RepulsionCommand() {
/* 14 */     super("repulsion", "server.commands.repulsion.desc");
/* 15 */     addSubCommand((AbstractCommand)new RepulsionAddCommand());
/* 16 */     addSubCommand((AbstractCommand)new RepulsionRemoveCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\component\repulsion\RepulsionCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */