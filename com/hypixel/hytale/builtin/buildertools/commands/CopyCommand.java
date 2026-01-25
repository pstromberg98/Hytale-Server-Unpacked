/*     */ package com.hypixel.hytale.builtin.buildertools.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.builtin.buildertools.PrefabCopyException;
/*     */ import com.hypixel.hytale.builtin.buildertools.PrototypePlayerBuilderToolSettings;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.protocol.SoundCategory;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ public class CopyCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  38 */   private static final Message MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION = Message.translation("server.builderTools.copycut.noSelection");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  44 */   private final FlagArg noEntitiesFlag = withFlagArg("noEntities", "server.commands.copy.noEntities.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  50 */   private final FlagArg entitiesOnlyFlag = withFlagArg("onlyEntities", "server.commands.copy.entitiesonly.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  56 */   private final FlagArg emptyFlag = withFlagArg("empty", "server.commands.copy.empty.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  62 */   private final FlagArg keepAnchorsFlag = withFlagArg("keepanchors", "server.commands.copy.keepanchors.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  68 */   private final FlagArg playerAnchorFlag = withFlagArg("playerAnchor", "server.commands.copy.playerAnchor.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CopyCommand() {
/*  74 */     super("copy", "server.commands.copy.desc");
/*  75 */     setPermissionGroup(GameMode.Creative);
/*  76 */     requirePermission("hytale.editor.selection.clipboard");
/*  77 */     addUsageVariant((AbstractCommand)new CopyRegionCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  86 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  87 */     assert playerComponent != null;
/*     */     
/*  89 */     if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, (ComponentAccessor)store))
/*     */       return; 
/*  91 */     BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
/*  92 */     boolean entitiesOnly = ((Boolean)this.entitiesOnlyFlag.get(context)).booleanValue();
/*  93 */     boolean noEntities = ((Boolean)this.noEntitiesFlag.get(context)).booleanValue();
/*     */     
/*  95 */     int settings = 0;
/*  96 */     if (!entitiesOnly) settings |= 0x8; 
/*  97 */     if (((Boolean)this.emptyFlag.get(context)).booleanValue()) settings |= 0x4; 
/*  98 */     if (((Boolean)this.keepAnchorsFlag.get(context)).booleanValue()) settings |= 0x40;
/*     */     
/* 100 */     if (!noEntities || entitiesOnly) settings |= 0x10;
/*     */     
/* 102 */     int settingsFinal = settings;
/* 103 */     Vector3i playerAnchor = getPlayerAnchor(ref, store, ((Boolean)this.playerAnchorFlag.get(context)).booleanValue());
/* 104 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
/*     */           try {
/*     */             BlockSelection selection = builderState.getSelection();
/*     */             
/*     */             if (selection == null || !selection.hasSelectionBounds()) {
/*     */               context.sendMessage(MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION);
/*     */               return;
/*     */             } 
/*     */             Vector3i min = selection.getSelectionMin();
/*     */             Vector3i max = selection.getSelectionMax();
/*     */             builderState.copyOrCut(r, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), settingsFinal, playerAnchor, componentAccessor);
/* 115 */           } catch (PrefabCopyException e) {
/*     */             context.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static Vector3i getPlayerAnchor(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, boolean usePlayerAnchor) {
/* 124 */     if (!usePlayerAnchor) {
/* 125 */       return null;
/*     */     }
/* 127 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 128 */     if (transformComponent == null) {
/* 129 */       return null;
/*     */     }
/* 131 */     Vector3d position = transformComponent.getPosition();
/* 132 */     return new Vector3i(
/* 133 */         MathUtil.floor(position.getX()), 
/* 134 */         MathUtil.floor(position.getY()), 
/* 135 */         MathUtil.floor(position.getZ()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CopyRegionCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/* 148 */     private final RequiredArg<Integer> xMinArg = withRequiredArg("xMin", "server.commands.copy.xMin.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 154 */     private final RequiredArg<Integer> yMinArg = withRequiredArg("yMin", "server.commands.copy.yMin.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 160 */     private final RequiredArg<Integer> zMinArg = withRequiredArg("zMin", "server.commands.copy.zMin.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 166 */     private final RequiredArg<Integer> xMaxArg = withRequiredArg("xMax", "server.commands.copy.xMax.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 172 */     private final RequiredArg<Integer> yMaxArg = withRequiredArg("yMax", "server.commands.copy.yMax.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 178 */     private final RequiredArg<Integer> zMaxArg = withRequiredArg("zMax", "server.commands.copy.zMax.desc", (ArgumentType)ArgTypes.INTEGER);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 184 */     private final FlagArg noEntitiesFlag = withFlagArg("noEntities", "server.commands.copy.noEntities.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 190 */     private final FlagArg entitiesOnlyFlag = withFlagArg("onlyEntities", "server.commands.copy.entitiesonly.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 196 */     private final FlagArg emptyFlag = withFlagArg("empty", "server.commands.copy.empty.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 202 */     private final FlagArg keepAnchorsFlag = withFlagArg("keepanchors", "server.commands.copy.keepanchors.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 208 */     private final FlagArg playerAnchorFlag = withFlagArg("playerAnchor", "server.commands.copy.playerAnchor.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CopyRegionCommand() {
/* 214 */       super("server.commands.copy.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 219 */       Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 220 */       assert playerComponent != null;
/*     */       
/* 222 */       if (!PrototypePlayerBuilderToolSettings.isOkayToDoCommandsOnSelection(ref, playerComponent, (ComponentAccessor)store))
/*     */         return; 
/* 224 */       BuilderToolsPlugin.BuilderState builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
/* 225 */       boolean entitiesOnly = ((Boolean)this.entitiesOnlyFlag.get(context)).booleanValue();
/* 226 */       boolean noEntities = ((Boolean)this.noEntitiesFlag.get(context)).booleanValue();
/*     */       
/* 228 */       int settings = 0;
/* 229 */       if (!entitiesOnly) settings |= 0x8; 
/* 230 */       if (((Boolean)this.emptyFlag.get(context)).booleanValue()) settings |= 0x4; 
/* 231 */       if (((Boolean)this.keepAnchorsFlag.get(context)).booleanValue()) settings |= 0x40;
/*     */       
/* 233 */       if (!noEntities || entitiesOnly) settings |= 0x10;
/*     */       
/* 235 */       int xMin = ((Integer)this.xMinArg.get(context)).intValue();
/* 236 */       int yMin = ((Integer)this.yMinArg.get(context)).intValue();
/* 237 */       int zMin = ((Integer)this.zMinArg.get(context)).intValue();
/* 238 */       int xMax = ((Integer)this.xMaxArg.get(context)).intValue();
/* 239 */       int yMax = ((Integer)this.yMaxArg.get(context)).intValue();
/* 240 */       int zMax = ((Integer)this.zMaxArg.get(context)).intValue();
/*     */       
/* 242 */       int copySettings = settings;
/* 243 */       Vector3i playerAnchor = CopyCommand.getPlayerAnchor(ref, store, ((Boolean)this.playerAnchorFlag.get(context)).booleanValue());
/* 244 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> {
/*     */             try {
/*     */               builderState.copyOrCut(r, xMin, yMin, zMin, xMax, yMax, zMax, copySettings, playerAnchor, componentAccessor);
/* 247 */             } catch (PrefabCopyException e) {
/*     */               context.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
/*     */               SoundUtil.playSoundEvent2d(r, TempAssetIdUtil.getSoundEventIndex("CREATE_ERROR"), SoundCategory.UI, componentAccessor);
/*     */             } 
/*     */           });
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
/*     */   public static void copySelection(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 263 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 264 */     assert playerComponent != null;
/*     */     
/* 266 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/* 267 */     assert playerRefComponent != null;
/*     */     
/* 269 */     copySelection(ref, componentAccessor, BuilderToolsPlugin.getState(playerComponent, playerRefComponent), 24);
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
/*     */   public static void copySelection(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull BuilderToolsPlugin.BuilderState builderState, int settings) {
/* 281 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 282 */     assert playerComponent != null;
/*     */     
/* 284 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/* 285 */     assert playerRefComponent != null;
/*     */     
/* 287 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRefComponent, (r, s, c) -> {
/*     */           try {
/*     */             BlockSelection selection = builderState.getSelection();
/*     */             
/*     */             if (selection == null || !selection.hasSelectionBounds()) {
/*     */               playerComponent.sendMessage(MESSAGE_BUILDER_TOOLS_COPY_CUT_NO_SELECTION);
/*     */               return;
/*     */             } 
/*     */             Vector3i min = selection.getSelectionMin();
/*     */             Vector3i max = selection.getSelectionMax();
/*     */             builderState.copyOrCut(r, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), settings, c);
/* 298 */           } catch (PrefabCopyException e) {
/*     */             playerComponent.sendMessage(Message.translation("server.builderTools.copycut.copyFailedReason").param("reason", e.getMessage()));
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\CopyCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */