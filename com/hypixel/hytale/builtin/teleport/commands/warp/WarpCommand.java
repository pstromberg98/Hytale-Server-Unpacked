/*    */ package com.hypixel.hytale.builtin.teleport.commands.warp;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*    */ import com.hypixel.hytale.builtin.teleport.Warp;
/*    */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.Universe;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class WarpCommand extends AbstractCommandCollection {
/*    */   @Nonnull
/* 25 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED = Message.translation("server.commands.teleport.warp.notLoaded");
/*    */   @Nonnull
/* 27 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_UNKNOWN_WARP = Message.translation("server.commands.teleport.warp.unknownWarp");
/*    */   @Nonnull
/* 29 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_WORLD_NAME_FOR_WARP_NOT_FOUND = Message.translation("server.commands.teleport.warp.worldNameForWarpNotFound");
/*    */   @Nonnull
/* 31 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_WARPED_TO = Message.translation("server.commands.teleport.warp.warpedTo");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WarpCommand() {
/* 37 */     super("warp", "server.commands.warp.desc");
/*    */ 
/*    */     
/* 40 */     addUsageVariant((AbstractCommand)new WarpGoVariantCommand());
/*    */ 
/*    */     
/* 43 */     addSubCommand((AbstractCommand)new WarpGoCommand());
/* 44 */     addSubCommand((AbstractCommand)new WarpSetCommand());
/* 45 */     addSubCommand((AbstractCommand)new WarpListCommand());
/* 46 */     addSubCommand((AbstractCommand)new WarpRemoveCommand());
/* 47 */     addSubCommand((AbstractCommand)new WarpReloadCommand());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static void tryGo(@Nonnull CommandContext context, @Nonnull String warp, @Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 66 */     if (!TeleportPlugin.get().isWarpsLoaded()) {
/* 67 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 72 */     Warp targetWarp = (Warp)TeleportPlugin.get().getWarps().get(warp.toLowerCase());
/* 73 */     if (targetWarp == null) {
/* 74 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_UNKNOWN_WARP.param("name", warp));
/*    */       
/*    */       return;
/*    */     } 
/* 78 */     String worldName = targetWarp.getWorld();
/* 79 */     World world = Universe.get().getWorld(worldName);
/* 80 */     Teleport teleportComponent = targetWarp.toTeleport();
/* 81 */     if (world == null || teleportComponent == null) {
/* 82 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_WORLD_NAME_FOR_WARP_NOT_FOUND.param("worldName", worldName).param("warp", warp));
/*    */       
/*    */       return;
/*    */     } 
/* 86 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 87 */     assert transformComponent != null;
/*    */     
/* 89 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/* 90 */     assert headRotationComponent != null;
/*    */     
/* 92 */     Vector3d playerPosition = transformComponent.getPosition();
/* 93 */     Vector3f playerHeadRotation = headRotationComponent.getRotation();
/*    */     
/* 95 */     ((TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType())).append(world, playerPosition.clone(), playerHeadRotation.clone(), "Warp '" + warp + "'");
/*    */     
/* 97 */     store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/* 98 */     context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_WARPED_TO.param("name", warp));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\warp\WarpCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */