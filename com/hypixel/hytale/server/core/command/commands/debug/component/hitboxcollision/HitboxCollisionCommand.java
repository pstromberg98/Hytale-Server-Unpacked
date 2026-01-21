/*    */ package com.hypixel.hytale.server.core.command.commands.debug.component.hitboxcollision;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HitboxCollisionCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public HitboxCollisionCommand() {
/* 14 */     super("hitboxcollision", "server.commands.hitboxcollision.desc");
/* 15 */     addSubCommand((AbstractCommand)new HitboxCollisionAddCommand());
/* 16 */     addSubCommand((AbstractCommand)new HitboxCollisionRemoveCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\component\hitboxcollision\HitboxCollisionCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */