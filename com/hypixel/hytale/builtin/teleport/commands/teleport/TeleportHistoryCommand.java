/*    */ package com.hypixel.hytale.builtin.teleport.commands.teleport;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*    */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.logging.Level;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TeleportHistoryCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 24 */   private static final Message MESSAGE_COMMANDS_TELEPORT_HISTORY_EMPTY = Message.translation("server.commands.teleport.history.empty");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TeleportHistoryCommand() {
/* 30 */     super("history", "server.commands.teleport.dump.desc");
/* 31 */     requirePermission(HytalePermissions.fromCommand("teleport.history"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 36 */     PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/* 37 */     assert playerRefComponent != null;
/*    */     
/* 39 */     TeleportHistory history = (TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
/*    */     
/* 41 */     TeleportPlugin.get().getLogger().at(Level.INFO).log("Got history for player %s: %s", playerRefComponent
/* 42 */         .getUsername(), history);
/*    */ 
/*    */     
/* 45 */     int backSize = history.getBackSize();
/* 46 */     int forwardSize = history.getForwardSize();
/*    */     
/* 48 */     if (backSize == 0 && forwardSize == 0) {
/* 49 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_HISTORY_EMPTY);
/*    */     } else {
/* 51 */       context.sendMessage(Message.translation("server.commands.teleport.history.info")
/* 52 */           .param("backCount", backSize)
/* 53 */           .param("forwardCount", forwardSize));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\teleport\TeleportHistoryCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */