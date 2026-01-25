/*    */ package com.hypixel.hytale.server.core.permissions.commands.op;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.Universe;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ public class OpAddCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 22 */   private static final Message MESSAGE_COMMANDS_OP_ADDED_TARGET = Message.translation("server.commands.op.added.target");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 28 */   private final RequiredArg<UUID> playerArg = withRequiredArg("player", "server.commands.op.add.player.desc", (ArgumentType)ArgTypes.PLAYER_UUID);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OpAddCommand() {
/* 34 */     super("add", "server.commands.op.add.desc");
/* 35 */     requirePermission(HytalePermissions.fromCommand("op.add"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 40 */     UUID uuid = (UUID)this.playerArg.get(context);
/* 41 */     PermissionsModule permissionsModule = PermissionsModule.get();
/* 42 */     String opGroup = "OP";
/*    */ 
/*    */     
/* 45 */     PlayerRef playerRef = Universe.get().getPlayer(uuid);
/* 46 */     String displayName = (playerRef != null) ? playerRef.getUsername() : uuid.toString();
/* 47 */     Message displayMessage = Message.raw(displayName).bold(true);
/*    */     
/* 49 */     Set<String> groups = permissionsModule.getGroupsForUser(uuid);
/* 50 */     if (groups.contains("OP")) {
/* 51 */       context.sendMessage(Message.translation("server.commands.op.already")
/* 52 */           .param("username", displayMessage));
/*    */     } else {
/* 54 */       permissionsModule.addUserToGroup(uuid, "OP");
/* 55 */       context.sendMessage(Message.translation("server.commands.op.added")
/* 56 */           .param("username", displayMessage));
/*    */ 
/*    */       
/* 59 */       if (playerRef != null)
/* 60 */         playerRef.sendMessage(MESSAGE_COMMANDS_OP_ADDED_TARGET); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\permissions\commands\op\OpAddCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */