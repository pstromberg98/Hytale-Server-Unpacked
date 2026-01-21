/*    */ package com.hypixel.hytale.builtin.teleport.commands.teleport;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*    */ import com.hypixel.hytale.component.Component;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
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
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.Universe;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class TeleportWorldCommand
/*    */   extends AbstractPlayerCommand {
/*    */   @Nonnull
/* 30 */   private static final Message MESSAGE_WORLD_NOT_FOUND = Message.translation("server.world.notFound");
/*    */   @Nonnull
/* 32 */   private static final Message MESSAGE_WORLD_SPAWN_NOT_SET = Message.translation("server.world.spawn.notSet");
/*    */   @Nonnull
/* 34 */   private static final Message MESSAGE_COMMANDS_TELEPORT_TELEPORTED_TO_WORLD = Message.translation("server.commands.teleport.teleportedToWorld");
/*    */   
/*    */   @Nonnull
/* 37 */   private final RequiredArg<String> worldNameArg = withRequiredArg("worldName", "server.commands.worldport.worldName.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TeleportWorldCommand() {
/* 43 */     super("world", "server.commands.worldport.desc");
/* 44 */     setPermissionGroup(null);
/* 45 */     requirePermission(HytalePermissions.fromCommand("teleport.world"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 50 */     String worldName = (String)this.worldNameArg.get(context);
/*    */ 
/*    */     
/* 53 */     World targetWorld = Universe.get().getWorld(worldName);
/* 54 */     if (targetWorld == null) {
/* 55 */       context.sendMessage(MESSAGE_WORLD_NOT_FOUND.param("worldName", worldName));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 60 */     Transform spawnPoint = targetWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, (ComponentAccessor)store);
/* 61 */     if (spawnPoint == null) {
/* 62 */       context.sendMessage(MESSAGE_WORLD_SPAWN_NOT_SET.param("worldName", worldName));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 67 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 68 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/*    */     
/* 70 */     if (transformComponent != null && headRotationComponent != null) {
/* 71 */       Vector3d previousPos = transformComponent.getPosition().clone();
/* 72 */       Vector3f previousRotation = headRotationComponent.getRotation().clone();
/*    */       
/* 74 */       TeleportHistory teleportHistoryComponent = (TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
/* 75 */       teleportHistoryComponent.append(world, previousPos, previousRotation, "World " + targetWorld
/* 76 */           .getName());
/*    */     } 
/*    */     
/* 79 */     Teleport teleportComponent = Teleport.createForPlayer(targetWorld, spawnPoint);
/* 80 */     store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*    */     
/* 82 */     Vector3d spawnPos = spawnPoint.getPosition();
/* 83 */     context.sendMessage(MESSAGE_COMMANDS_TELEPORT_TELEPORTED_TO_WORLD
/* 84 */         .param("worldName", worldName)
/* 85 */         .param("x", spawnPos.getX())
/* 86 */         .param("y", spawnPos.getY())
/* 87 */         .param("z", spawnPos.getZ()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\teleport\TeleportWorldCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */