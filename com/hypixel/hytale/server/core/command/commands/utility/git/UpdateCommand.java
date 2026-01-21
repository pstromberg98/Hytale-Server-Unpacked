/*    */ package com.hypixel.hytale.server.core.command.commands.utility.git;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public UpdateCommand() {
/* 14 */     super("update", "server.commands.update.desc");
/* 15 */     addSubCommand((AbstractCommand)new UpdateAssetsCommand());
/* 16 */     addSubCommand((AbstractCommand)new UpdatePrefabsCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\git\UpdateCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */