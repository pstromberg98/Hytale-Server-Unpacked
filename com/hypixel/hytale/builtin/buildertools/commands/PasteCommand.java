/*     */ package com.hypixel.hytale.builtin.buildertools.commands;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeIntPosition;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class PasteCommand
/*     */   extends AbstractPlayerCommand
/*     */ {
/*     */   @Nonnull
/*  30 */   private final FlagArg technicalFlag = withFlagArg("technical", "server.commands.paste.technical.desc");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PasteCommand() {
/*  36 */     super("paste", "server.commands.paste.desc");
/*  37 */     setPermissionGroup(GameMode.Creative);
/*  38 */     requirePermission("hytale.editor.selection.clipboard");
/*  39 */     addUsageVariant((AbstractCommand)new PasteAtPositionCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  48 */     ChunkStore chunkStore = world.getChunkStore();
/*     */     
/*  50 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  51 */     assert playerComponent != null;
/*     */     
/*  53 */     TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*  54 */     assert transformComponent != null;
/*     */     
/*  56 */     Vector3d position = transformComponent.getPosition();
/*  57 */     int x = MathUtil.floor(position.x);
/*  58 */     int y = MathUtil.floor(position.y);
/*  59 */     int z = MathUtil.floor(position.z);
/*     */     
/*  61 */     boolean technical = ((Boolean)this.technicalFlag.get(context)).booleanValue();
/*  62 */     BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.paste(r, x, y, z, technical, componentAccessor));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class PasteAtPositionCommand
/*     */     extends AbstractPlayerCommand
/*     */   {
/*     */     @Nonnull
/*  72 */     private final RequiredArg<RelativeIntPosition> positionArg = withRequiredArg("position", "server.commands.paste.position.desc", ArgTypes.RELATIVE_BLOCK_POSITION);
/*     */     
/*     */     @Nonnull
/*  75 */     private final FlagArg technicalFlag = withFlagArg("technical", "server.commands.paste.technical.desc");
/*     */     
/*     */     public PasteAtPositionCommand() {
/*  78 */       super("server.commands.paste.desc");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  87 */       ChunkStore chunkStore = world.getChunkStore();
/*     */       
/*  89 */       Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  90 */       assert playerComponent != null;
/*     */       
/*  92 */       TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*  93 */       assert transformComponent != null;
/*     */       
/*  95 */       Vector3d position = transformComponent.getPosition();
/*  96 */       RelativeIntPosition relativePos = (RelativeIntPosition)this.positionArg.get(context);
/*  97 */       Vector3i blockPos = relativePos.getBlockPosition(position, chunkStore);
/*     */       
/*  99 */       boolean technical = ((Boolean)this.technicalFlag.get(context)).booleanValue();
/* 100 */       BuilderToolsPlugin.addToQueue(playerComponent, playerRef, (r, s, componentAccessor) -> s.paste(r, blockPos.x, blockPos.y, blockPos.z, technical, componentAccessor));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\commands\PasteCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */