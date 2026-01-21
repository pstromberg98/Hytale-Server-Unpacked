/*     */ package com.hypixel.hytale.builtin.teleport.commands.teleport;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Transform;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.command.system.exceptions.GeneralCommandException;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*     */ import com.hypixel.hytale.server.core.permissions.HytalePermissions;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpawnCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  37 */   private final OptionalArg<Integer> spawnIndexArg = withOptionalArg("spawnIndex", "server.commands.spawn.index.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SpawnCommand() {
/*  43 */     super("spawn", "server.commands.spawn.desc");
/*  44 */     requirePermission(HytalePermissions.fromCommand("spawn.self"));
/*  45 */     addUsageVariant((AbstractCommand)new SpawnOtherCommand());
/*  46 */     addSubCommand((AbstractCommand)new SpawnSetCommand());
/*  47 */     addSubCommand((AbstractCommand)new SpawnSetDefaultCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  56 */     Transform spawnTransform = resolveSpawn(context, world, playerRef, this.spawnIndexArg);
/*     */     
/*  58 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*  59 */     assert transformComponent != null;
/*     */     
/*  61 */     HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/*  62 */     assert headRotationComponent != null;
/*     */     
/*  64 */     Vector3d previousPos = transformComponent.getPosition().clone();
/*  65 */     Vector3f previousRotation = headRotationComponent.getRotation().clone();
/*     */     
/*  67 */     TeleportHistory teleportHistoryComponent = (TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
/*  68 */     teleportHistoryComponent.append(world, previousPos, previousRotation, "World " + world
/*  69 */         .getName() + "'s spawn");
/*     */     
/*  71 */     Teleport teleportComponent = Teleport.createForPlayer(world, spawnTransform);
/*  72 */     store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*     */     
/*  74 */     Vector3d position = spawnTransform.getPosition();
/*  75 */     context.sendMessage(Message.translation("server.commands.spawn.teleported")
/*  76 */         .param("x", position.getX())
/*  77 */         .param("y", position.getY())
/*  78 */         .param("z", position.getZ()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Transform resolveSpawn(@Nonnull CommandContext context, @Nonnull World world, @Nonnull PlayerRef playerRef, @Nonnull OptionalArg<Integer> spawnIndexArg) {
/*  88 */     ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
/*     */     
/*  90 */     if (spawnIndexArg.provided(context)) {
/*  91 */       int spawnIndex = ((Integer)spawnIndexArg.get(context)).intValue();
/*  92 */       Transform[] spawnPoints = spawnProvider.getSpawnPoints();
/*  93 */       if (spawnIndex < 0 || spawnIndex >= spawnPoints.length) {
/*  94 */         int maxIndex = spawnPoints.length - 1;
/*  95 */         context.sendMessage(Message.translation("server.commands.spawn.indexNotFound")
/*  96 */             .param("maxIndex", maxIndex));
/*  97 */         throw new GeneralCommandException(Message.translation("server.commands.errors.spawnIndexOutOfRange")
/*  98 */             .param("index", spawnIndex)
/*  99 */             .param("maxIndex", maxIndex));
/*     */       } 
/* 101 */       return spawnPoints[spawnIndex];
/*     */     } 
/* 103 */     return spawnProvider.getSpawnPoint(world, playerRef.getUuid());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class SpawnOtherCommand
/*     */     extends CommandBase
/*     */   {
/* 112 */     private static final Message MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD = Message.translation("server.commands.errors.playerNotInWorld");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 118 */     private final RequiredArg<PlayerRef> playerArg = withRequiredArg("player", "server.commands.argtype.player.desc", (ArgumentType)ArgTypes.PLAYER_REF);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 124 */     private final OptionalArg<Integer> spawnIndexArg = withOptionalArg("spawnIndex", "server.commands.spawn.index.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     SpawnOtherCommand() {
/* 130 */       super("server.commands.spawn.other.desc");
/* 131 */       requirePermission(HytalePermissions.fromCommand("spawn.other"));
/*     */     }
/*     */ 
/*     */     
/*     */     protected void executeSync(@Nonnull CommandContext context) {
/* 136 */       PlayerRef targetPlayerRef = (PlayerRef)this.playerArg.get(context);
/* 137 */       Ref<EntityStore> ref = targetPlayerRef.getReference();
/*     */       
/* 139 */       if (ref == null || !ref.isValid()) {
/* 140 */         context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */         
/*     */         return;
/*     */       } 
/* 144 */       Store<EntityStore> store = ref.getStore();
/* 145 */       World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */       
/* 147 */       world.execute(() -> {
/*     */             Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */             if (playerComponent == null) {
/*     */               context.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
/*     */               return;
/*     */             } 
/*     */             PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */             assert playerRefComponent != null;
/*     */             Transform spawn = SpawnCommand.resolveSpawn(context, world, targetPlayerRef, this.spawnIndexArg);
/*     */             TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*     */             assert transformComponent != null;
/*     */             HeadRotation headRotationComponent = (HeadRotation)store.getComponent(ref, HeadRotation.getComponentType());
/*     */             assert headRotationComponent != null;
/*     */             Vector3d previousPos = transformComponent.getPosition().clone();
/*     */             Vector3f previousRotation = headRotationComponent.getRotation().clone();
/*     */             TeleportHistory teleportHistoryComponent = (TeleportHistory)store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
/*     */             teleportHistoryComponent.append(world, previousPos, previousRotation, "World " + world.getName() + "'s spawn");
/*     */             Teleport teleportComponent = Teleport.createForPlayer(world, spawn);
/*     */             store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*     */             Vector3d position = spawn.getPosition();
/*     */             context.sendMessage(Message.translation("server.commands.spawn.teleportedOther").param("username", targetPlayerRef.getUsername()).param("x", position.getX()).param("y", position.getY()).param("z", position.getZ()));
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\commands\teleport\SpawnCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */