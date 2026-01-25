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
/*    */ public class WarpCommand
/*    */   extends AbstractCommandCollection {
/*    */   @Nonnull
/* 26 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED = Message.translation("server.commands.teleport.warp.notLoaded");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WarpCommand() {
/* 32 */     super("warp", "server.commands.warp.desc");
/*    */ 
/*    */     
/* 35 */     addUsageVariant((AbstractCommand)new WarpGoVariantCommand());
/*    */ 
/*    */     
/* 38 */     addSubCommand((AbstractCommand)new WarpGoCommand());
/* 39 */     addSubCommand((AbstractCommand)new WarpSetCommand());
/* 40 */     addSubCommand((AbstractCommand)new WarpListCommand());
/* 41 */     addSubCommand((AbstractCommand)new WarpRemoveCommand());
/* 42 */     addSubCommand((AbstractCommand)new WarpReloadCommand());
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
/* 61 */     if (!TeleportPlugin.get().isWarpsLoaded()) {
/* 62 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED);
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 67 */     Warp targetWarp = (Warp)TeleportPlugin.get().getWarps().get(warp.toLowerCase());
/* 68 */     if (targetWarp == null) {
/* 69 */       context.sendMessage(Message.translation("server.commands.teleport.warp.unknownWarp")
/* 70 */           .param("name", warp));
/*    */       
/*    */       return;
/*    */     } 
/* 74 */     String worldName = targetWarp.getWorld();
/* 75 */     World world = Universe.get().getWorld(worldName);
/* 76 */     Teleport teleportComponent = targetWarp.toTeleport();
/* 77 */     if (world == null || teleportComponent == null) {
/* 78 */       context.sendMessage(Message.translation("server.commands.teleport.warp.worldNameForWarpNotFound")
/* 79 */           .param("worldName", worldName)
/* 80 */           .param("warp", warp));
/*    */       
/*    */       return;
/*    */     } 
/* 84 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 85 */     assert transformComponent != null;
/*    */     
/* 87 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/* 88 */     assert headRotationComponent != null;
/*    */     
/* 90 */     Vector3d playerPosition = transformComponent.getPosition();
/* 91 */     Vector3f playerHeadRotation = headRotationComponent.getRotation();
/*    */     
/* 93 */     ((TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType())).append(world, playerPosition.clone(), playerHeadRotation.clone(), "Warp '" + warp + "'");
/*    */     
/* 95 */     store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/* 96 */     context.sendMessage(Message.translation("server.commands.teleport.warp.warpedTo")
/* 97 */         .param("name", warp));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\warp\WarpCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */