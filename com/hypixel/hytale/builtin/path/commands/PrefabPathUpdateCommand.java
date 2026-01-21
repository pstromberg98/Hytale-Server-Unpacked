/*    */ package com.hypixel.hytale.builtin.path.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrefabPathUpdateCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public PrefabPathUpdateCommand() {
/* 14 */     super("update", "server.commands.npcpath.update.desc");
/* 15 */     addSubCommand((AbstractCommand)new PrefabPathUpdatePauseCommand());
/* 16 */     addSubCommand((AbstractCommand)new PrefabPathUpdateObservationAngleCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\path\commands\PrefabPathUpdateCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */