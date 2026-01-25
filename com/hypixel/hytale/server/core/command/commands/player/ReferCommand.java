/*    */ package com.hypixel.hytale.server.core.command.commands.player;
/*    */ 
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandUtil;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.permissions.PermissionHolder;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReferCommand
/*    */   extends AbstractTargetPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 30 */   private final RequiredArg<String> hostArg = withRequiredArg("host", "Target server hostname or IP", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 36 */   private final RequiredArg<Integer> portArg = withRequiredArg("port", "Target server port", (ArgumentType)ArgTypes.INTEGER);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ReferCommand() {
/* 42 */     super("refer", "Refer a player to another server for testing");
/* 43 */     addAliases(new String[] { "transfer" });
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 48 */     String host = (String)this.hostArg.get(context);
/* 49 */     int port = ((Integer)this.portArg.get(context)).intValue();
/* 50 */     boolean isTargetingOther = !ref.equals(sourceRef);
/*    */ 
/*    */     
/* 53 */     if (isTargetingOther) {
/* 54 */       CommandUtil.requirePermission((PermissionHolder)context.sender(), HytalePermissions.fromCommand("refer.other"));
/*    */     } else {
/* 56 */       CommandUtil.requirePermission((PermissionHolder)context.sender(), HytalePermissions.fromCommand("refer.self"));
/*    */     } 
/*    */ 
/*    */     
/* 60 */     if (port <= 0 || port > 65535) {
/* 61 */       context.sendMessage(Message.translation("server.commands.refer.invalidPort"));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/*    */     try {
/* 67 */       playerRef.referToServer(host, port);
/*    */       
/* 69 */       if (isTargetingOther) {
/* 70 */         context.sendMessage(Message.translation("server.commands.refer.success.other")
/* 71 */             .param("username", playerRef.getUsername())
/* 72 */             .param("host", host)
/* 73 */             .param("port", port));
/*    */       } else {
/* 75 */         context.sendMessage(Message.translation("server.commands.refer.success.self")
/* 76 */             .param("host", host)
/* 77 */             .param("port", port));
/*    */       } 
/* 79 */     } catch (Exception e) {
/* 80 */       context.sendMessage(Message.translation("server.commands.refer.failed").param("error", e.getMessage()));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\player\ReferCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */