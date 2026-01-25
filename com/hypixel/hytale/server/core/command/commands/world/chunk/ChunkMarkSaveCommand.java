/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.math.vector.Vector2i;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkMarkSaveCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 27 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.marksave.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkMarkSaveCommand() {
/* 33 */     super("marksave", "server.commands.chunk.marksave.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 38 */     RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
/* 39 */     Vector2i position = chunkPosition.getChunkPosition(context, (ComponentAccessor)store);
/*    */     
/* 41 */     ChunkStore chunkStore = world.getChunkStore();
/* 42 */     Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
/* 43 */     long chunkIndex = ChunkUtil.indexChunk(position.x, position.y);
/* 44 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*    */ 
/*    */     
/* 47 */     if (chunkRef != null && chunkRef.isValid()) {
/* 48 */       WorldChunk worldChunkComponent = (WorldChunk)chunkStoreStore.getComponent(chunkRef, WorldChunk.getComponentType());
/* 49 */       assert worldChunkComponent != null;
/* 50 */       worldChunkComponent.markNeedsSaving();
/* 51 */       context.sendMessage(Message.translation("server.commands.chunk.marksave.markingAlreadyLoaded")
/* 52 */           .param("x", position.x)
/* 53 */           .param("z", position.y)
/* 54 */           .param("worldName", world.getName()));
/*    */       
/*    */       return;
/*    */     } 
/* 58 */     context.sendMessage(Message.translation("server.commands.chunk.load.loading")
/* 59 */         .param("chunkX", position.x)
/* 60 */         .param("chunkZ", position.y)
/* 61 */         .param("worldName", world.getName()));
/*    */     
/* 63 */     world.getChunkAsync(position.x, position.y).thenAccept(worldChunk -> world.execute(()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkMarkSaveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */