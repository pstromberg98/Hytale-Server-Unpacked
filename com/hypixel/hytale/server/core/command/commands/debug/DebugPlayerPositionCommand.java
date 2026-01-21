/*    */ package com.hypixel.hytale.server.core.command.commands.debug;
/*    */ 
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class DebugPlayerPositionCommand
/*    */   extends AbstractPlayerCommand {
/*    */   public DebugPlayerPositionCommand() {
/* 24 */     super("debugplayerposition", "server.commands.debugplayerposition.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 29 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 30 */     assert transformComponent != null;
/* 31 */     Transform transform = transformComponent.getTransform();
/*    */     
/* 33 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/* 34 */     assert headRotationComponent != null;
/* 35 */     Vector3f headRotation = headRotationComponent.getRotation();
/*    */     
/* 37 */     Teleport teleport = (Teleport)store.getComponent(ref, Teleport.getComponentType());
/* 38 */     PendingTeleport pendingTeleport = (PendingTeleport)store.getComponent(ref, PendingTeleport.getComponentType());
/*    */     
/* 40 */     String teleportFmt = (teleport == null) ? "none" : fmtPos(teleport.getPosition());
/* 41 */     String pendingTeleportFmt = (pendingTeleport == null) ? "none" : fmtPos(pendingTeleport.getPosition());
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 48 */     Message message = Message.translation("server.commands.debugplayerposition.result").param("bodyPosition", fmtPos(transform.getPosition())).param("bodyRotation", fmtRot(transform.getRotation())).param("headRotation", fmtRot(headRotation)).param("teleport", teleportFmt).param("pendingTeleport", pendingTeleportFmt);
/*    */     
/* 50 */     playerRef.sendMessage(message);
/*    */     
/* 52 */     Vector3f blue = new Vector3f(0.137F, 0.867F, 0.882F);
/* 53 */     DebugUtils.addSphere(world, transform.getPosition(), blue, 0.5D, 30.0F);
/*    */     
/* 55 */     playerRef.sendMessage(Message.translation("server.commands.debugplayerposition.notify")
/* 56 */         .color("#23DDE1"));
/*    */   }
/*    */   
/*    */   private static String fmtPos(@Nonnull Vector3d vector) {
/* 60 */     String fmt = "%.1f";
/* 61 */     return String.format("%.1f", new Object[] { Double.valueOf(vector.getX()) }) + ", " + String.format("%.1f", new Object[] { Double.valueOf(vector.getX()) }) + ", " + String.format("%.1f", new Object[] { Double.valueOf(vector.getY()) });
/*    */   }
/*    */   
/*    */   private static String fmtRot(@Nonnull Vector3f vector) {
/* 65 */     return "Pitch=" + fmtDegrees(vector.getPitch()) + ", Yaw=" + fmtDegrees(vector.getYaw()) + ", Roll=" + fmtDegrees(vector.getRoll());
/*    */   }
/*    */   
/*    */   private static String fmtDegrees(float radians) {
/* 69 */     return String.format("%.1f", new Object[] { Double.valueOf(Math.toDegrees(radians)) }) + "°";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\DebugPlayerPositionCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */