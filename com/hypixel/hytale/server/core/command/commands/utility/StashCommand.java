/*     */ package com.hypixel.hytale.server.core.command.commands.utility;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ import com.hypixel.hytale.server.core.util.TargetUtil;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class StashCommand extends AbstractPlayerCommand {
/*     */   @Nonnull
/*  29 */   private static final Message MESSAGE_COMMANDS_STASH_DROP_LIST_SET = Message.translation("server.commands.stash.droplistSet");
/*     */   @Nonnull
/*  31 */   private static final Message MESSAGE_COMMANDS_STASH_NO_DROP_LIST = Message.translation("server.commands.stash.noDroplist");
/*     */   @Nonnull
/*  33 */   private static final Message MESSAGE_GENERAL_BLOCK_TARGET_NOT_IN_RANGE = Message.translation("server.general.blockTargetNotInRange");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int DISTANCE_MAX = 10;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  44 */   private final OptionalArg<String> setArg = withOptionalArg("set", "server.commands.stash.setDroplist.desc", (ArgumentType)ArgTypes.STRING);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StashCommand() {
/*  50 */     super("stash", "server.commands.stash.getDroplist.desc");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
/*  60 */     ItemContainerState itemContainerState = getItemContainerState(ref, world, context, (ComponentAccessor<EntityStore>)store);
/*  61 */     if (itemContainerState == null)
/*     */       return; 
/*  63 */     if (this.setArg.provided(context)) {
/*  64 */       String dropList = (String)this.setArg.get(context);
/*  65 */       itemContainerState.setDroplist(dropList);
/*  66 */       context.sendMessage(MESSAGE_COMMANDS_STASH_DROP_LIST_SET);
/*     */     } else {
/*  68 */       String droplist = itemContainerState.getDroplist();
/*  69 */       if (droplist != null) {
/*  70 */         context.sendMessage(Message.translation("server.commands.stash.currentDroplist")
/*  71 */             .param("droplist", droplist));
/*     */       } else {
/*  73 */         context.sendMessage(MESSAGE_COMMANDS_STASH_NO_DROP_LIST);
/*     */       } 
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
/*     */   @Nullable
/*     */   private ItemContainerState getItemContainerState(@Nonnull Ref<EntityStore> ref, @Nonnull World world, @Nonnull CommandContext context, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  92 */     Vector3i block = TargetUtil.getTargetBlock(ref, 10.0D, componentAccessor);
/*  93 */     if (block == null) {
/*  94 */       context.sendMessage(MESSAGE_GENERAL_BLOCK_TARGET_NOT_IN_RANGE);
/*  95 */       return null;
/*     */     } 
/*     */     
/*  98 */     ChunkStore chunkStore = world.getChunkStore();
/*  99 */     long chunkIndex = ChunkUtil.indexChunkFromBlock(block.x, block.z);
/* 100 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*     */ 
/*     */     
/* 103 */     if (chunkRef == null || !chunkRef.isValid()) {
/* 104 */       int chunkX = ChunkUtil.chunkCoordinate(block.x);
/* 105 */       int chunkZ = ChunkUtil.chunkCoordinate(block.z);
/*     */       
/* 107 */       context.sendMessage(Message.translation("server.commands.errors.chunkNotLoaded")
/* 108 */           .param("chunkX", chunkX)
/* 109 */           .param("chunkZ", chunkZ)
/* 110 */           .param("world", world.getName()));
/* 111 */       return null;
/*     */     } 
/*     */     
/* 114 */     Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
/*     */     
/* 116 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
/* 117 */     assert blockChunkComponent != null;
/*     */     
/* 119 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStoreStore.getComponent(chunkRef, WorldChunk.getComponentType());
/* 120 */     assert worldChunkComponent != null;
/*     */     
/* 122 */     BlockSection section = blockChunkComponent.getSectionAtBlockY(block.y);
/* 123 */     int filler = section.getFiller(block.x, block.y, block.z);
/* 124 */     if (filler != 0) {
/* 125 */       block.x -= FillerBlockUtil.unpackX(filler);
/* 126 */       block.y -= FillerBlockUtil.unpackY(filler);
/* 127 */       block.z -= FillerBlockUtil.unpackZ(filler);
/*     */     } 
/*     */     
/* 130 */     BlockState state = worldChunkComponent.getState(block.x, block.y, block.z);
/* 131 */     if (!(state instanceof ItemContainerState)) {
/* 132 */       context.sendMessage(Message.translation("server.general.containerNotFound")
/* 133 */           .param("block", block.toString()));
/* 134 */       return null;
/*     */     } 
/*     */     
/* 137 */     return (ItemContainerState)state;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\StashCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */