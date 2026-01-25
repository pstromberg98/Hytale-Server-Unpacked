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
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChunkForceTickCommand
/*    */   extends AbstractWorldCommand
/*    */ {
/*    */   @Nonnull
/* 27 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.forcetick.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkForceTickCommand() {
/* 33 */     super("forcetick", "server.commands.chunk.forcetick.desc");
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
/* 47 */     if (chunkRef == null || !chunkRef.isValid()) {
/* 48 */       context.sendMessage(Message.translation("server.commands.errors.chunkNotLoaded")
/* 49 */           .param("chunkX", position.x)
/* 50 */           .param("chunkZ", position.y)
/* 51 */           .param("world", world.getName()));
/*    */ 
/*    */       
/* 54 */       context.sendMessage(Message.translation("server.commands.forcechunktick.chunkLoadUsage")
/* 55 */           .param("chunkX", position.x)
/* 56 */           .param("chunkZ", position.y));
/*    */       
/*    */       return;
/*    */     } 
/* 60 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
/* 61 */     assert blockChunkComponent != null;
/*    */     
/* 63 */     for (int x = 0; x < 32; x++) {
/* 64 */       for (int y = 0; y < 320; y++) {
/* 65 */         for (int z = 0; z < 32; z++) {
/* 66 */           blockChunkComponent.setTicking(x, y, z, true);
/*    */         }
/*    */       } 
/*    */     } 
/* 70 */     context.sendMessage(Message.translation("server.commands.forcechunktick.blocksInChunkTick")
/* 71 */         .param("chunkX", position.x)
/* 72 */         .param("chunkZ", position.y));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkForceTickCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */