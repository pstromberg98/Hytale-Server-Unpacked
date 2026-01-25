/*    */ package com.hypixel.hytale.server.core.command.commands.server;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KickCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 20 */   private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.kick.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public KickCommand() {
/* 26 */     super("kick", "server.commands.kick.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 31 */     PlayerRef playerToKick = (PlayerRef)this.playerArg.get(context);
/* 32 */     playerToKick.getPacketHandler().disconnect("You were kicked.");
/* 33 */     context.sendMessage(Message.translation("server.commands.kick.success")
/* 34 */         .param("username", playerToKick.getUsername()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\server\KickCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */