/*    */ package com.hypixel.hytale.builtin.ambience.commands;
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
/*    */ public class AmbienceEmitterCommands
/*    */   extends AbstractCommandCollection
/*    */ {
/*    */   public AmbienceEmitterCommands() {
/* 18 */     super("emitter", "server.commands.ambience.emitter.desc");
/*    */     
/* 20 */     addSubCommand((AbstractCommand)new AmbienceEmitterAddCommand());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\ambience\commands\AmbienceCommands$AmbienceEmitterCommands.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */