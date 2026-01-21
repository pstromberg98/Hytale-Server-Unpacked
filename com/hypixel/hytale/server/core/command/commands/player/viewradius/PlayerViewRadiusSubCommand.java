/*    */ package com.hypixel.hytale.server.core.command.commands.player.viewradius;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerViewRadiusSubCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public PlayerViewRadiusSubCommand() {
/* 14 */     super("viewradius", "server.commands.player.viewradius.desc");
/* 15 */     addSubCommand((AbstractCommand)new PlayerViewRadiusGetCommand());
/* 16 */     addSubCommand((AbstractCommand)new PlayerViewRadiusSetCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\viewradius\PlayerViewRadiusSubCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */