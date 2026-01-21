/*     */ package com.hypixel.hytale.builtin.path.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.path.WorldPathBuilder;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Transform;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.path.WorldPath;
/*     */ import com.hypixel.hytale.server.core.universe.world.path.WorldPathConfig;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WorldPathBuilderCommand
/*     */   extends AbstractCommandCollection
/*     */ {
/*     */   public WorldPathBuilderCommand() {
/*  40 */     super("builder", "server.commands.worldpath.builder.desc");
/*  41 */     addSubCommand((AbstractCommand)new WorldPathBuilderStopCommand());
/*  42 */     addSubCommand((AbstractCommand)new WorldPathBuilderLoadCommand());
/*  43 */     addSubCommand((AbstractCommand)new WorldPathBuilderSimulateCommand());
/*  44 */     addSubCommand((AbstractCommand)new WorldPathBuilderClearCommand());
/*  45 */     addSubCommand((AbstractCommand)new WorldPathBuilderAddCommand());
/*  46 */     addSubCommand((AbstractCommand)new WorldPathBuilderSetCommand());
/*  47 */     addSubCommand((AbstractCommand)new WorldPathBuilderGotoCommand());
/*  48 */     addSubCommand((AbstractCommand)new WorldPathBuilderRemoveCommand());
/*  49 */     addSubCommand((AbstractCommand)new WorldPathBuilderSaveCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderStopCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     public WorldPathBuilderStopCommand() {
/*  61 */       super("stop", "server.commands.worldpath.builder.stop.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  70 */       WorldPathBuilderCommand.removeBuilder(ref, store);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderLoadCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/*  83 */     private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.worldpath.builder.load.name.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderLoadCommand() {
/*  89 */       super("load", "server.commands.worldpath.builder.load.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  98 */       String name = (String)this.nameArg.get(context);
/*  99 */       WorldPath worldPath = world.getWorldPathConfig().getPath(name);
/*     */       
/* 101 */       if (worldPath == null) {
/* 102 */         context.sendMessage(Message.translation("server.universe.worldpath.noPathFound")
/* 103 */             .param("path", name));
/*     */         
/*     */         return;
/*     */       } 
/* 107 */       WorldPathBuilderCommand.putBuilder(ref, store, WorldPathBuilderCommand.createBuilder(ref, store, worldPath));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderSimulateCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     public WorldPathBuilderSimulateCommand() {
/* 120 */       super("simulate", "server.commands.worldpath.builder.simulate.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 129 */       WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
/* 130 */       if (builder == null)
/*     */         return; 
/* 132 */       ObjectArrayList<Transform> waypoints = new ObjectArrayList(builder.getPath().getWaypoints());
/* 133 */       CompletableFuture<Void> future = new CompletableFuture<>();
/* 134 */       ScheduledFuture[] arrayOfScheduledFuture = new ScheduledFuture[1];
/* 135 */       arrayOfScheduledFuture[0] = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> { Transform transform = (Transform)waypoints.removeFirst(); if (transform == null) { future.complete(null); scheduledFuture[0].cancel(false); } else { world.execute(()); }  }1L, 1L, TimeUnit.SECONDS);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderClearCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 155 */     private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINTS_CLEARED = Message.translation("server.universe.worldpath.pointsCleared");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderClearCommand() {
/* 161 */       super("clear", "server.commands.worldpath.builder.clear.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 170 */       WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
/* 171 */       if (builder == null)
/*     */         return; 
/* 173 */       builder.getPath().getWaypoints().clear();
/* 174 */       context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINTS_CLEARED);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderAddCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 183 */     private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINT_ADDED = Message.translation("server.universe.worldpath.pointAdded");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderAddCommand() {
/* 189 */       super("add", "server.commands.worldpath.builder.add.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 198 */       TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 199 */       assert transformComponent != null;
/*     */       
/* 201 */       Transform transform = transformComponent.getTransform().clone();
/* 202 */       WorldPathBuilderCommand.getOrCreateBuilder(ref, store).getPath().getWaypoints().add(transform);
/* 203 */       context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINT_ADDED);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderSetCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 212 */     private static final Message MESSAGE_UNIVERSE_WORLD_PATH_POINT_SET = Message.translation("server.universe.worldpath.pointSet");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 218 */     private final OptionalArg<Integer> indexArg = withOptionalArg("index", "server.commands.worldpath.builder.set.index.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderSetCommand() {
/* 224 */       super("set", "server.commands.worldpath.builder.set.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 233 */       WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
/* 234 */       if (builder == null)
/*     */         return; 
/* 236 */       TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 237 */       assert transformComponent != null;
/*     */       
/* 239 */       WorldPath worldPath = builder.getPath();
/* 240 */       int index = this.indexArg.provided(context) ? ((Integer)this.indexArg.get(context)).intValue() : (worldPath.getWaypoints().size() - 1);
/* 241 */       worldPath.getWaypoints().set(index, transformComponent.getTransform().clone());
/* 242 */       context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_POINT_SET);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderGotoCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 255 */     private final RequiredArg<Integer> indexArg = withRequiredArg("index", "server.commands.worldpath.builder.goto.index.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderGotoCommand() {
/* 261 */       super("goto", "server.commands.worldpath.builder.goto.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 270 */       WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
/* 271 */       if (builder == null)
/*     */         return; 
/* 273 */       Integer index = (Integer)this.indexArg.get(context);
/* 274 */       WorldPath worldPath = builder.getPath();
/* 275 */       Transform waypointTransform = worldPath.getWaypoints().get(index.intValue());
/* 276 */       Teleport teleportComponent = Teleport.createForPlayer(null, waypointTransform);
/*     */       
/* 278 */       store.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/* 279 */       context.sendMessage(Message.translation("server.universe.worldpath.teleportedToPoint")
/* 280 */           .param("index", index.intValue()));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderRemoveCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 293 */     private final RequiredArg<Integer> indexArg = withRequiredArg("index", "server.commands.worldpath.builder.remove.index.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderRemoveCommand() {
/* 299 */       super("remove", "server.commands.worldpath.builder.remove.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 308 */       WorldPathBuilder builder = WorldPathBuilderCommand.getBuilder(ref, store);
/* 309 */       if (builder == null)
/*     */         return; 
/* 311 */       int index = ((Integer)this.indexArg.get(context)).intValue();
/* 312 */       builder.getPath().getWaypoints().remove(index);
/* 313 */       context.sendMessage(Message.translation("server.universe.worldpath.removedIndex").param("index", index));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class WorldPathBuilderSaveCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 322 */     private static final Message MESSAGE_UNIVERSE_WORLD_PATH_NO_POINTS_DEFINED = Message.translation("server.universe.worldpath.noPointsDefined");
/*     */     @Nonnull
/* 324 */     private static final Message MESSAGE_UNIVERSE_WORLD_PATH_SAVED = Message.translation("server.universe.worldpath.saved");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 330 */     private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.worldpath.builder.save.name.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WorldPathBuilderSaveCommand() {
/* 336 */       super("save", "server.commands.worldpath.builder.save.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 345 */       String name = (String)this.nameArg.get(context);
/* 346 */       WorldPath path = WorldPathBuilderCommand.removeBuilder(ref, store);
/*     */       
/* 348 */       if (path == null || path.getWaypoints().isEmpty()) {
/* 349 */         context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_NO_POINTS_DEFINED);
/*     */         
/*     */         return;
/*     */       } 
/* 353 */       WorldPathConfig worldPathConfig = world.getWorldPathConfig();
/* 354 */       WorldPath worldPath = new WorldPath(name, path.getWaypoints());
/* 355 */       worldPathConfig.putPath(worldPath);
/* 356 */       worldPathConfig.save(world);
/*     */       
/* 358 */       context.sendMessage(MESSAGE_UNIVERSE_WORLD_PATH_SAVED);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static WorldPathBuilder createBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nullable WorldPath existing) {
/* 372 */     UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(ref, UUIDComponent.getComponentType());
/* 373 */     assert uuidComponent != null;
/*     */     
/* 375 */     String name = "Builder-" + String.valueOf(uuidComponent.getUuid());
/* 376 */     WorldPathBuilder builder = new WorldPathBuilder();
/* 377 */     if (existing == null) {
/* 378 */       builder.setPath(new WorldPath(name, (List)new ObjectArrayList()));
/*     */     } else {
/* 380 */       builder.setPath(new WorldPath(name, (List)new ObjectArrayList(existing.getWaypoints())));
/*     */     } 
/* 382 */     return builder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static WorldPathBuilder getBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 394 */     return (WorldPathBuilder)store.getComponent(ref, WorldPathBuilder.getComponentType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static WorldPathBuilder getOrCreateBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 406 */     WorldPathBuilder builder = (WorldPathBuilder)store.getComponent(ref, WorldPathBuilder.getComponentType());
/* 407 */     if (builder != null) return builder;
/*     */     
/* 409 */     return putBuilder(ref, store, createBuilder(ref, store, (WorldPath)null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static WorldPath removeBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 421 */     WorldPathBuilder worldPath = (WorldPathBuilder)store.getComponent(ref, WorldPathBuilder.getComponentType());
/* 422 */     if (worldPath != null) {
/* 423 */       store.removeComponent(ref, WorldPathBuilder.getComponentType());
/* 424 */       return worldPath.getPath();
/*     */     } 
/*     */     
/* 427 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static WorldPathBuilder putBuilder(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WorldPathBuilder builder) {
/* 440 */     store.putComponent(ref, WorldPathBuilder.getComponentType(), (Component)builder);
/* 441 */     return builder;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\path\commands\WorldPathBuilderCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */