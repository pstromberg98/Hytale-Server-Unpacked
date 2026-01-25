/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.math.vector.Vector2i;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
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
/*    */ public class ChunkLoadCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 27 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.load.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 33 */   private final FlagArg markDirtyArg = withFlagArg("markdirty", "server.commands.chunk.load.markdirty.desc");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkLoadCommand() {
/* 39 */     super("load", "server.commands.chunk.load.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 44 */     RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
/* 45 */     Vector2i position = chunkPosition.getChunkPosition(context, (ComponentAccessor)store);
/*    */     
/* 47 */     ChunkStore chunkStore = world.getChunkStore();
/* 48 */     long chunkIndex = ChunkUtil.indexChunk(position.x, position.y);
/* 49 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*    */ 
/*    */     
/* 52 */     if (chunkRef != null && chunkRef.isValid()) {
/* 53 */       context.sendMessage(Message.translation("server.commands.chunk.load.alreadyLoaded")
/* 54 */           .param("chunkX", position.x)
/* 55 */           .param("chunkZ", position.y)
/* 56 */           .param("worldName", world.getName()));
/*    */       
/*    */       return;
/*    */     } 
/* 60 */     context.sendMessage(Message.translation("server.commands.chunk.load.loading")
/* 61 */         .param("chunkX", position.x)
/* 62 */         .param("chunkZ", position.y)
/* 63 */         .param("worldName", world.getName()));
/*    */     
/* 65 */     world.getChunkAsync(position.x, position.y).thenAccept(worldChunk -> world.execute(()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkLoadCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */