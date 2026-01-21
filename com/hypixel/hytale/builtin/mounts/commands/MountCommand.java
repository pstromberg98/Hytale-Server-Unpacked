/*    */ package com.hypixel.hytale.builtin.mounts.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MountCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public MountCommand() {
/* 14 */     super("mount", "server.commands.mount");
/* 15 */     addSubCommand((AbstractCommand)new DismountCommand());
/* 16 */     addSubCommand((AbstractCommand)new MountCheckCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\mounts\commands\MountCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */