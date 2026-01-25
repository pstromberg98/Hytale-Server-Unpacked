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
/*    */   extends AbstractPlayerCommand
/*    */ {
/*    */   @Nonnull
/* 31 */   private final RequiredArg<String> worldNameArg = withRequiredArg("worldName", "server.commands.worldport.worldName.desc", (ArgumentType)ArgTypes.STRING);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public TeleportWorldCommand() {
/* 37 */     super("world", "server.commands.worldport.desc");
/* 38 */     setPermissionGroup(null);
/* 39 */     requirePermission(HytalePermissions.fromCommand("teleport.world"));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 44 */     String worldName = (String)this.worldNameArg.get(context);
/*    */ 
/*    */     
/* 47 */     World targetWorld = Universe.get().getWorld(worldName);
/* 48 */     if (targetWorld == null) {
/* 49 */       context.sendMessage(Message.translation("server.world.notFound")
/* 50 */           .param("worldName", worldName));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 55 */     Transform spawnPoint = targetWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, (ComponentAccessor)store);
/* 56 */     if (spawnPoint == null) {
/* 57 */       context.sendMessage(Message.translation("server.world.spawn.notSet")
/* 58 */           .param("worldName", worldName));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 63 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 64 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/*    */     
/* 66 */     if (transformComponent != null && headRotationComponent != null) {
/* 67 */       Vector3d previousPos = transformComponent.getPosition().clone();
/* 68 */       Vector3f previousRotation = headRotationComponent.getRotation().clone();
/*    */       
/* 70 */       TeleportHistory teleportHistoryComponent = (TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
/* 71 */       teleportHistoryComponent.append(world, previousPos, previousRotation, "World " + targetWorld
/* 72 */           .getName());
/*    */     } 
/*    */     
/* 75 */     Teleport teleportComponent = Teleport.createForPlayer(targetWorld, spawnPoint);
/* 76 */     store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*    */     
/* 78 */     Vector3d spawnPos = spawnPoint.getPosition();
/* 79 */     context.sendMessage(Message.translation("server.commands.teleport.teleportedToWorld")
/* 80 */         .param("worldName", worldName)
/* 81 */         .param("x", spawnPos.getX())
/* 82 */         .param("y", spawnPos.getY())
/* 83 */         .param("z", spawnPos.getZ()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\teleport\TeleportWorldCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */