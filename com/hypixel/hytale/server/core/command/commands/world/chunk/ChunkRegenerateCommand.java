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
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkRegenerateCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 26 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.regenerate.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkRegenerateCommand() {
/* 32 */     super("regenerate", "server.commands.chunk.regenerate.desc", true);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 37 */     Vector2i chunkPosition = ((RelativeChunkPosition)this.chunkPosArg.get(context)).getChunkPosition(context, (ComponentAccessor)store);
/* 38 */     long chunkIndex = ChunkUtil.indexChunk(chunkPosition.x, chunkPosition.y);
/*    */     
/* 40 */     ChunkStore chunkStore = world.getChunkStore();
/* 41 */     chunkStore.getChunkReferenceAsync(chunkIndex, 9)
/* 42 */       .thenAccept(chunkRef -> world.execute(()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkRegenerateCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */