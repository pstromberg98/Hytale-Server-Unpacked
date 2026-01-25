/*     */ package com.hypixel.hytale.builtin.buildertools.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PrefabSaveDirectCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  98 */   private final RequiredArg<String> nameArg = withRequiredArg("name", "server.commands.prefab.save.name.desc", (ArgumentType)ArgTypes.STRING);
/*     */   
/*     */   @Nonnull
/* 101 */   private final FlagArg overwriteFlag = withFlagArg("overwrite", "server.commands.prefab.save.overwrite.desc");
/*     */   
/*     */   @Nonnull
/* 104 */   private final FlagArg entitiesFlag = withFlagArg("entities", "server.commands.prefab.save.entities.desc");
/*     */   
/*     */   @Nonnull
/* 107 */   private final FlagArg emptyFlag = withFlagArg("empty", "server.commands.prefab.save.empty.desc");
/*     */   
/*     */   @Nonnull
/* 110 */   private final FlagArg playerAnchorFlag = withFlagArg("playerAnchor", "server.commands.prefab.save.playerAnchor.desc");
/*     */   
/*     */   public PrefabSaveDirectCommand() {
/* 113 */     super("server.commands.prefab.save.desc");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/* 122 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 123 */     assert playerComponent != null;
/*     */     
/* 125 */     String name = (String)this.nameArg.get(context);
/* 126 */     boolean overwrite = ((Boolean)this.overwriteFlag.get(context)).booleanValue();
/* 127 */     boolean entities = ((Boolean)this.entitiesFlag.get(context)).booleanValue();
/* 128 */     boolean empty = ((Boolean)this.emptyFlag.get(context)).booleanValue();
/*     */     
/* 130 */     Vector3i playerAnchor = getPlayerAnchor(ref, store, ((Boolean)this.playerAnchorFlag.get(context)).booleanValue());
/*     */     
/* 132 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.saveFromSelection(r, name, true, overwrite, entities, empty, playerAnchor, componentAccessor));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Vector3i getPlayerAnchor(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, boolean usePlayerAnchor) {
/* 138 */     if (!usePlayerAnchor) {
/* 139 */       return null;
/*     */     }
/* 141 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/* 142 */     if (transformComponent == null) {
/* 143 */       return null;
/*     */     }
/* 145 */     Vector3d position = transformComponent.getPosition();
/* 146 */     return new Vector3i(
/* 147 */         MathUtil.floor(position.getX()), 
/* 148 */         MathUtil.floor(position.getY()), 
/* 149 */         MathUtil.floor(position.getZ()));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\PrefabCommand$PrefabSaveDirectCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */