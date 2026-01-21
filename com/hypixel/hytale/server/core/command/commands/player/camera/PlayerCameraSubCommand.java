/*    */ package com.hypixel.hytale.server.core.command.commands.player.camera;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerCameraSubCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public PlayerCameraSubCommand() {
/* 14 */     super("camera", "server.commands.camera.desc");
/* 15 */     addSubCommand((AbstractCommand)new PlayerCameraResetCommand());
/* 16 */     addSubCommand((AbstractCommand)new PlayerCameraTopdownCommand());
/* 17 */     addSubCommand((AbstractCommand)new PlayerCameraSideScrollerCommand());
/* 18 */     addSubCommand((AbstractCommand)new PlayerCameraDemoSubCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\camera\PlayerCameraSubCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */