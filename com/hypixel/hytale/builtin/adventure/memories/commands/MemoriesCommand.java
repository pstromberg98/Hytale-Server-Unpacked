/*    */ package com.hypixel.hytale.builtin.adventure.memories.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemoriesCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public MemoriesCommand() {
/* 14 */     super("memories", "server.commands.memories.desc");
/* 15 */     addSubCommand((AbstractCommand)new MemoriesClearCommand());
/* 16 */     addSubCommand((AbstractCommand)new MemoriesCapacityCommand());
/* 17 */     addSubCommand((AbstractCommand)new MemoriesLevelCommand());
/* 18 */     addSubCommand((AbstractCommand)new MemoriesUnlockCommand());
/* 19 */     addSubCommand((AbstractCommand)new MemoriesSetCountCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\commands\MemoriesCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */