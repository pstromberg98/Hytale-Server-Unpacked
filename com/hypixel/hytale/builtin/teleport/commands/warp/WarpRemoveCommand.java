/*    */ package com.hypixel.hytale.builtin.teleport.commands.warp;
/*    */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*    */ import com.hypixel.hytale.builtin.teleport.Warp;
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.ComponentType;
/*    */ import com.hypixel.hytale.component.RemoveReason;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.universe.Universe;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class WarpRemoveCommand extends CommandBase {
/* 24 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED = Message.translation("server.commands.teleport.warp.notLoaded");
/*    */   
/*    */   @Nonnull
/* 27 */   private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.warp.remove.name.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WarpRemoveCommand() {
/* 33 */     super("remove", "server.commands.warp.remove.desc");
/* 34 */     requirePermission(HytalePermissions.fromCommand("warp.remove"));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 44 */     if (!TeleportPlugin.get().isWarpsLoaded()) {
/* 45 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED);
/*    */       
/*    */       return;
/*    */     } 
/* 49 */     Map<String, Warp> warps = TeleportPlugin.get().getWarps();
/* 50 */     String warpName = ((String)this.nameArg.get(context)).toLowerCase();
/* 51 */     Warp old = warps.remove(warpName);
/*    */     
/* 53 */     if (old == null) {
/* 54 */       context.sendMessage(Message.translation("server.commands.teleport.warp.unknownWarp")
/* 55 */           .param("name", warpName));
/*    */     } else {
/* 57 */       TeleportPlugin.get().saveWarps();
/* 58 */       context.sendMessage(Message.translation("server.commands.teleport.warp.removedWarp")
/* 59 */           .param("name", warpName));
/*    */ 
/*    */       
/* 62 */       World targetWorld = Universe.get().getWorld(old.getWorld());
/* 63 */       if (targetWorld != null) {
/* 64 */         ComponentType<EntityStore, TeleportPlugin.WarpComponent> warpComponentType = TeleportPlugin.WarpComponent.getComponentType();
/* 65 */         Store<EntityStore> store = targetWorld.getEntityStore().getStore();
/* 66 */         targetWorld.execute(() -> store.forEachEntityParallel((Query)warpComponentType, ()));
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\warp\WarpRemoveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */