/*    */ package com.hypixel.hytale.builtin.teleport.commands.warp;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.teleport.TeleportPlugin;
/*    */ import com.hypixel.hytale.builtin.teleport.Warp;
/*    */ import com.hypixel.hytale.component.AddReason;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.time.Instant;
/*    */ import java.util.Map;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class WarpSetCommand
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 31 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED = Message.translation("server.commands.teleport.warp.notLoaded");
/*    */   @Nonnull
/* 33 */   private static final Message MESSAGE_COMMANDS_TELEPORT_WARP_RESERVED_KEYWORD = Message.translation("server.commands.teleport.warp.reservedKeyword");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 39 */   private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.warp.set.name.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WarpSetCommand() {
/* 45 */     super("set", "server.commands.warp.set.desc");
/* 46 */     requirePermission(HytalePermissions.fromCommand("warp.set"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 51 */     if (!TeleportPlugin.get().isWarpsLoaded()) {
/* 52 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_NOT_LOADED);
/*    */       
/*    */       return;
/*    */     } 
/* 56 */     Map<String, Warp> warps = TeleportPlugin.get().getWarps();
/* 57 */     String newId = ((String)this.nameArg.get(context)).toLowerCase();
/*    */     
/* 59 */     if ("reload".equals(newId) || "remove".equals(newId) || "set".equals(newId) || "list".equals(newId) || "go".equals(newId)) {
/* 60 */       context.sendMessage(MESSAGE_COMMANDS_TELEPORT_WARP_RESERVED_KEYWORD);
/*    */       
/*    */       return;
/*    */     } 
/* 64 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 65 */     assert transformComponent != null;
/*    */     
/* 67 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/* 68 */     assert headRotationComponent != null;
/*    */     
/* 70 */     Vector3d position = transformComponent.getPosition();
/* 71 */     Vector3f headRotation = headRotationComponent.getRotation();
/*    */ 
/*    */ 
/*    */     
/* 75 */     Transform transform = new Transform(position.clone(), headRotation.clone());
/*    */ 
/*    */     
/* 78 */     Warp newWarp = new Warp(transform, newId, world, playerRef.getUsername(), Instant.now());
/*    */     
/* 80 */     warps.put(newWarp.getId().toLowerCase(), newWarp);
/*    */     
/* 82 */     TeleportPlugin plugin = TeleportPlugin.get();
/* 83 */     plugin.saveWarps();
/*    */     
/* 85 */     store.addEntity(plugin.createWarp(newWarp, store), AddReason.LOAD);
/*    */     
/* 87 */     context.sendMessage(Message.translation("server.commands.teleport.warp.setWarp")
/* 88 */         .param("name", newWarp.getId()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\warp\WarpSetCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */