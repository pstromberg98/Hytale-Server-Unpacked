/*    */ package com.hypixel.hytale.server.core.permissions.commands.op;
/*    */ 
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Constants;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
/*    */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class OpSelfCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 26 */   private static final Message MESSAGE_COMMANDS_OP_ADDED = Message.translation("server.commands.op.self.added");
/*    */   @Nonnull
/* 28 */   private static final Message MESSAGE_COMMANDS_OP_REMOVED = Message.translation("server.commands.op.self.removed");
/*    */   @Nonnull
/* 30 */   private static final Message MESSAGE_COMMANDS_NON_VANILLA_PERMISSIONS = Message.translation("server.commands.op.self.nonVanillaPermissions");
/*    */   @Nonnull
/* 32 */   private static final Message MESSAGE_COMMANDS_SINGLEPLAYER_OWNER_REQ = Message.translation("server.commands.op.self.singleplayerOwnerReq");
/*    */   @Nonnull
/* 34 */   private static final Message MESSAGE_COMMANDS_MULTIPLAYER_TIP = Message.translation("server.commands.op.self.multiplayerTip");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OpSelfCommand() {
/* 40 */     super("self", "server.commands.op.self.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean canGeneratePermission() {
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 50 */     if (PermissionsModule.get().areProvidersTampered()) {
/* 51 */       playerRef.sendMessage(MESSAGE_COMMANDS_NON_VANILLA_PERMISSIONS);
/*    */       
/*    */       return;
/*    */     } 
/* 55 */     if (Constants.SINGLEPLAYER && !SingleplayerModule.isOwner(playerRef)) {
/* 56 */       playerRef.sendMessage(MESSAGE_COMMANDS_SINGLEPLAYER_OWNER_REQ);
/*    */       
/*    */       return;
/*    */     } 
/* 60 */     if (!Constants.SINGLEPLAYER && !Constants.ALLOWS_SELF_OP_COMMAND) {
/* 61 */       playerRef.sendMessage(MESSAGE_COMMANDS_MULTIPLAYER_TIP
/* 62 */           .param("uuidCommand", "uuid")
/* 63 */           .param("permissionFile", "permissions.json")
/* 64 */           .param("launchArg", "--allow-op"));
/*    */       
/*    */       return;
/*    */     } 
/* 68 */     UUID uuid = playerRef.getUuid();
/*    */     
/* 70 */     PermissionsModule perms = PermissionsModule.get();
/* 71 */     String opGroup = "OP";
/* 72 */     Set<String> groups = perms.getGroupsForUser(uuid);
/*    */     
/* 74 */     if (groups.contains("OP")) {
/* 75 */       perms.removeUserFromGroup(uuid, "OP");
/* 76 */       context.sendMessage(MESSAGE_COMMANDS_OP_REMOVED);
/*    */     } else {
/* 78 */       perms.addUserToGroup(uuid, "OP");
/* 79 */       context.sendMessage(MESSAGE_COMMANDS_OP_ADDED);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\permissions\commands\op\OpSelfCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */