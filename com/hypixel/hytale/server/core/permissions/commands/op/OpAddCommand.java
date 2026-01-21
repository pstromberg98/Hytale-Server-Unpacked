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
/* 22 */   private static final Message MESSAGE_COMMANDS_OP_ADDED = Message.translation("server.commands.op.added");
/*    */   @Nonnull
/* 24 */   private static final Message MESSAGE_COMMANDS_OP_ADDED_TARGET = Message.translation("server.commands.op.added.target");
/*    */   @Nonnull
/* 26 */   private static final Message MESSAGE_COMMANDS_OP_ALREADY = Message.translation("server.commands.op.already");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 32 */   private final RequiredArg<UUID> playerArg = withRequiredArg("player", "server.commands.op.add.player.desc", (ArgumentType)ArgTypes.PLAYER_UUID);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OpAddCommand() {
/* 38 */     super("add", "server.commands.op.add.desc");
/* 39 */     requirePermission(HytalePermissions.fromCommand("op.add"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 44 */     UUID uuid = (UUID)this.playerArg.get(context);
/* 45 */     PermissionsModule permissionsModule = PermissionsModule.get();
/* 46 */     String opGroup = "OP";
/*    */ 
/*    */     
/* 49 */     PlayerRef playerRef = Universe.get().getPlayer(uuid);
/* 50 */     String displayName = (playerRef != null) ? playerRef.getUsername() : uuid.toString();
/* 51 */     Message displayMessage = Message.raw(displayName).bold(true);
/*    */     
/* 53 */     Set<String> groups = permissionsModule.getGroupsForUser(uuid);
/* 54 */     if (groups.contains("OP")) {
/* 55 */       context.sendMessage(MESSAGE_COMMANDS_OP_ALREADY
/* 56 */           .param("username", displayMessage));
/*    */     } else {
/* 58 */       permissionsModule.addUserToGroup(uuid, "OP");
/* 59 */       context.sendMessage(MESSAGE_COMMANDS_OP_ADDED
/* 60 */           .param("username", displayMessage));
/*    */ 
/*    */       
/* 63 */       if (playerRef != null)
/* 64 */         playerRef.sendMessage(MESSAGE_COMMANDS_OP_ADDED_TARGET); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\permissions\commands\op\OpAddCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */