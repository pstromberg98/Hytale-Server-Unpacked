/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.RemoveReason;
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
/*    */ public class ChunkUnloadCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 27 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.unload.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkUnloadCommand() {
/* 33 */     super("unload", "server.commands.chunk.unload.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 38 */     RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
/* 39 */     Vector2i position = chunkPosition.getChunkPosition(context, (ComponentAccessor)store);
/*    */     
/* 41 */     ChunkStore chunkComponentStore = world.getChunkStore();
/* 42 */     long indexChunk = ChunkUtil.indexChunk(position.x, position.y);
/* 43 */     Ref<ChunkStore> chunkRef = chunkComponentStore.getChunkReference(indexChunk);
/* 44 */     if (chunkRef == null) {
/* 45 */       context.sendMessage(Message.translation("server.commands.chunk.unload.alreadyUnloaded")
/* 46 */           .param("chunkX", position.x)
/* 47 */           .param("chunkZ", position.y)
/* 48 */           .param("worldName", world.getName()));
/*    */       
/*    */       return;
/*    */     } 
/* 52 */     chunkComponentStore.remove(chunkRef, RemoveReason.UNLOAD);
/* 53 */     world.getNotificationHandler().updateChunk(indexChunk);
/* 54 */     context.sendMessage(Message.translation("server.commands.chunk.unload.success")
/* 55 */         .param("chunkX", position.x)
/* 56 */         .param("chunkZ", position.y)
/* 57 */         .param("worldName", world.getName()));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkUnloadCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */