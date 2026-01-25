/*    */ package com.hypixel.hytale.server.core.command.commands.utility.git;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
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
/*    */ public class GitCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public GitCommand() {
/* 20 */     super("git", "server.commands.git.desc");
/* 21 */     addSubCommand((AbstractCommand)new UpdateAssetsCommand());
/* 22 */     addSubCommand((AbstractCommand)new UpdatePrefabsCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\git\GitCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */