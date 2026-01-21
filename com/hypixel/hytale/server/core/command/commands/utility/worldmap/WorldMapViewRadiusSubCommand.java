/*    */ package com.hypixel.hytale.server.core.command.commands.utility.worldmap;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ public class WorldMapViewRadiusSubCommand extends AbstractCommandCollection {
/*    */   public WorldMapViewRadiusSubCommand() {
/*  7 */     super("viewradius", "server.commands.worldmap.viewradius.desc");
/*  8 */     addSubCommand((AbstractCommand)new WorldMapViewRadiusGetCommand());
/*  9 */     addSubCommand((AbstractCommand)new WorldMapViewRadiusSetCommand());
/* 10 */     addSubCommand((AbstractCommand)new WorldMapViewRadiusRemoveCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\worldmap\WorldMapViewRadiusSubCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */