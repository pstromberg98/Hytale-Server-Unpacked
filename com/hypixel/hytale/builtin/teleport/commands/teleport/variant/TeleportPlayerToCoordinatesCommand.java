/*     */ package com.hypixel.hytale.builtin.teleport.commands.teleport.variant;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.Coord;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeFloat;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*     */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class TeleportPlayerToCoordinatesCommand extends CommandBase {
/*     */   @Nonnull
/*  30 */   private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  36 */   private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.teleport.targetPlayer.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  42 */   private final RequiredArg<Coord> xArg = withRequiredArg("x", "server.commands.teleport.x.desc", (ArgumentType)ArgTypes.RELATIVE_DOUBLE_COORD);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  48 */   private final RequiredArg<Coord> yArg = withRequiredArg("y", "server.commands.teleport.y.desc", (ArgumentType)ArgTypes.RELATIVE_DOUBLE_COORD);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  54 */   private final RequiredArg<Coord> zArg = withRequiredArg("z", "server.commands.teleport.z.desc", (ArgumentType)ArgTypes.RELATIVE_DOUBLE_COORD);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  60 */   private final OptionalArg<RelativeFloat> yawArg = withOptionalArg("yaw", "server.commands.teleport.yaw.desc", (ArgumentType)ArgTypes.RELATIVE_FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  66 */   private final OptionalArg<RelativeFloat> pitchArg = withOptionalArg("pitch", "server.commands.teleport.pitch.desc", (ArgumentType)ArgTypes.RELATIVE_FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  72 */   private final OptionalArg<RelativeFloat> rollArg = withOptionalArg("roll", "server.commands.teleport.roll.desc", (ArgumentType)ArgTypes.RELATIVE_FLOAT);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TeleportPlayerToCoordinatesCommand() {
/*  78 */     super("server.commands.teleport.toCoordinates.desc");
/*  79 */     requirePermission(HytalePermissions.fromCommand("teleport.other"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*  90 */     PlayerRef targetPlayerRef = (PlayerRef)this.playerArg.get(context);
/*     */ 
/*     */     
/*  93 */     Ref<EntityStore> ref = targetPlayerRef.getReference();
/*  94 */     if (ref == null || !ref.isValid()) {
/*  95 */       context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 100 */     Store<EntityStore> store = ref.getStore();
/* 101 */     World targetWorld = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 103 */     targetWorld.execute(() -> {
/*     */           TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*     */           
/*     */           assert transformComponent != null;
/*     */           
/*     */           HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/*     */           
/*     */           assert headRotationComponent != null;
/*     */           
/*     */           Vector3d previousPos = transformComponent.getPosition().clone();
/*     */           
/*     */           Vector3f previousHeadRotation = headRotationComponent.getRotation().clone();
/*     */           
/*     */           Vector3f previousBodyRotation = transformComponent.getRotation().clone();
/*     */           
/*     */           Coord relX = (Coord)this.xArg.get(context);
/*     */           
/*     */           Coord relY = (Coord)this.yArg.get(context);
/*     */           
/*     */           Coord relZ = (Coord)this.zArg.get(context);
/*     */           
/*     */           double x = relX.resolveXZ(previousPos.getX());
/*     */           
/*     */           double z = relZ.resolveXZ(previousPos.getZ());
/*     */           
/*     */           double y = relY.resolveYAtWorldCoords(previousPos.getY(), targetWorld, x, z);
/*     */           
/*     */           float yaw = this.yawArg.provided(context) ? (((RelativeFloat)this.yawArg.get(context)).resolve(previousHeadRotation.getYaw() * 57.295776F) * 0.017453292F) : Float.NaN;
/*     */           
/*     */           float pitch = this.pitchArg.provided(context) ? (((RelativeFloat)this.pitchArg.get(context)).resolve(previousHeadRotation.getPitch() * 57.295776F) * 0.017453292F) : Float.NaN;
/*     */           
/*     */           float roll = this.rollArg.provided(context) ? (((RelativeFloat)this.rollArg.get(context)).resolve(previousHeadRotation.getRoll() * 57.295776F) * 0.017453292F) : Float.NaN;
/*     */           
/*     */           Teleport teleport = Teleport.createExact(new Vector3d(x, y, z), new Vector3f(previousBodyRotation.getPitch(), yaw, previousBodyRotation.getRoll()), new Vector3f(pitch, yaw, roll));
/*     */           
/*     */           store.addComponent(ref, Teleport.getComponentType(), (Component)teleport);
/*     */           
/*     */           Player player = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           
/*     */           if (player != null) {
/*     */             ((TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType())).append(targetWorld, previousPos, previousHeadRotation, String.format("Teleport to (%s, %s, %s)", new Object[] { Double.valueOf(x), Double.valueOf(y), Double.valueOf(z) }));
/*     */           }
/*     */           
/* 146 */           boolean hasRotation = (this.yawArg.provided(context) || this.pitchArg.provided(context) || this.rollArg.provided(context));
/*     */           if (hasRotation) {
/*     */             float displayYaw = Float.isNaN(yaw) ? (previousHeadRotation.getYaw() * 57.295776F) : (yaw * 57.295776F);
/*     */             float displayPitch = Float.isNaN(pitch) ? (previousHeadRotation.getPitch() * 57.295776F) : (pitch * 57.295776F);
/*     */             float displayRoll = Float.isNaN(roll) ? (previousHeadRotation.getRoll() * 57.295776F) : (roll * 57.295776F);
/*     */             context.sendMessage(Message.translation("server.commands.teleport.teleportedToCoordinatesWithLook").param("x", x).param("y", y).param("z", z).param("yaw", displayYaw).param("pitch", displayPitch).param("roll", displayRoll));
/*     */           } else {
/*     */             context.sendMessage(Message.translation("server.commands.teleport.teleportedToCoordinates").param("x", x).param("y", y).param("z", z));
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\teleport\variant\TeleportPlayerToCoordinatesCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */