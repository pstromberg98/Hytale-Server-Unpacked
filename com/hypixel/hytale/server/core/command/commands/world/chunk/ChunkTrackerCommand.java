/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractTargetPlayerCommand;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.modules.entity.player.ChunkTracker;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkTrackerCommand
/*    */   extends AbstractTargetPlayerCommand
/*    */ {
/*    */   public ChunkTrackerCommand() {
/* 25 */     super("tracker", "server.commands.chunk.tracker.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nullable Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 30 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 31 */     assert playerComponent != null;
/*    */     
/* 33 */     ChunkTracker chunkTrackerComponent = (ChunkTracker)store.getComponent(ref, ChunkTracker.getComponentType());
/* 34 */     assert chunkTrackerComponent != null;
/*    */     
/* 36 */     ChunkStore chunkStore = world.getChunkStore();
/*    */     
/* 38 */     String loadedWorldChunks = Integer.toString(chunkStore.getLoadedChunksCount());
/* 39 */     context.sendMessage(Message.translation("server.commands.chunkTracker.summary")
/* 40 */         .param("maxChunksPerSecond", chunkTrackerComponent.getMaxChunksPerSecond())
/* 41 */         .param("maxChunksPerTick", chunkTrackerComponent.getMaxChunksPerTick())
/* 42 */         .param("minChunkLoadedRadius", chunkTrackerComponent.getMinLoadedChunksRadius())
/* 43 */         .param("maxHotChunkLoadedRadius", chunkTrackerComponent.getMaxHotLoadedChunksRadius())
/* 44 */         .param("loadedPlayerChunks", chunkTrackerComponent.getLoadedChunksCount())
/* 45 */         .param("loadingPlayerChunks", chunkTrackerComponent.getLoadingChunksCount())
/* 46 */         .param("loadedWorldChunks", loadedWorldChunks));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkTrackerCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */