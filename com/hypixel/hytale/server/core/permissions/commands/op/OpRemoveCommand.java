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
/*    */ public class OpRemoveCommand
/*    */   extends CommandBase
/*    */ {
/*    */   @Nonnull
/* 22 */   private static final Message MESSAGE_COMMANDS_OP_REMOVED_TARGET = Message.translation("server.commands.op.removed.target");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 28 */   private final RequiredArg<UUID> playerArg = withRequiredArg("player", "server.commands.op.remove.player.desc", (ArgumentType)ArgTypes.PLAYER_UUID);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OpRemoveCommand() {
/* 34 */     super("remove", "server.commands.op.remove.desc");
/* 35 */     requirePermission(HytalePermissions.fromCommand("op.remove"));
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
/* 51 */       permissionsModule.removeUserFromGroup(uuid, "OP");
/* 52 */       context.sendMessage(Message.translation("server.commands.op.removed")
/* 53 */           .param("username", displayMessage));
/*    */ 
/*    */       
/* 56 */       if (playerRef != null) {
/* 57 */         playerRef.sendMessage(MESSAGE_COMMANDS_OP_REMOVED_TARGET);
/*    */       }
/*    */     } else {
/* 60 */       context.sendMessage(Message.translation("server.commands.op.notOp")
/* 61 */           .param("username", displayMessage));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\permissions\commands\op\OpRemoveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */