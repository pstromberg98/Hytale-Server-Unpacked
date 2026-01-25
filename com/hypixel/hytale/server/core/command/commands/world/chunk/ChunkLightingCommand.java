/*    */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*    */ 
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.logger.HytaleLogger;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.math.vector.Vector2i;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeIntPosition;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.logging.Level;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ChunkLightingCommand extends AbstractWorldCommand {
/*    */   @Nonnull
/* 26 */   private static final Message MESSAGE_COMMANDS_CHUNKINFO_SERIALIZED = Message.translation("server.commands.chunkinfo.serialized");
/*    */   @Nonnull
/* 28 */   private static final Message MESSAGE_COMMANDS_CHUNKINFO_SERIALIZED_FAILED = Message.translation("server.commands.chunkinfo.serialized.failed");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 34 */   private final RequiredArg<RelativeIntPosition> positionArg = withRequiredArg("x y z", "server.commands.chunk.lighting.position.desc", ArgTypes.RELATIVE_BLOCK_POSITION);
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ChunkLightingCommand() {
/* 40 */     super("lighting", "server.commands.chunklighting.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/* 45 */     Vector3i position = ((RelativeIntPosition)this.positionArg.get(context)).getBlockPosition(context, (ComponentAccessor)store);
/*    */     
/* 47 */     ChunkStore chunkStore = world.getChunkStore();
/* 48 */     Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
/* 49 */     Vector2i chunkPos = new Vector2i(ChunkUtil.chunkCoordinate(position.getX()), ChunkUtil.chunkCoordinate(position.getZ()));
/* 50 */     long chunkIndex = ChunkUtil.indexChunk(chunkPos.x, chunkPos.y);
/* 51 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*    */ 
/*    */     
/* 54 */     if (chunkRef == null || !chunkRef.isValid()) {
/* 55 */       context.sendMessage(Message.translation("server.commands.errors.chunkNotLoaded")
/* 56 */           .param("chunkX", chunkPos.x)
/* 57 */           .param("chunkZ", chunkPos.y)
/* 58 */           .param("world", world.getName()));
/*    */ 
/*    */       
/* 61 */       context.sendMessage(Message.translation("server.commands.chunkinfo.load.usage")
/* 62 */           .param("chunkX", chunkPos.x)
/* 63 */           .param("chunkZ", chunkPos.y));
/*    */       
/*    */       return;
/*    */     } 
/* 67 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
/* 68 */     assert blockChunkComponent != null;
/*    */     
/*    */     try {
/* 71 */       BlockSection section = blockChunkComponent.getSectionAtBlockY(position.y);
/* 72 */       String s = section.getLocalLight().octreeToString();
/* 73 */       HytaleLogger.getLogger().at(Level.INFO).log("Chunk light output for (%d, %d, %d): %s", 
/* 74 */           Integer.valueOf(position.x), Integer.valueOf(position.y), Integer.valueOf(position.z), s);
/* 75 */       context.sendMessage(MESSAGE_COMMANDS_CHUNKINFO_SERIALIZED);
/* 76 */     } catch (Throwable t) {
/* 77 */       HytaleLogger.getLogger().at(Level.SEVERE).log("Failed to print chunk:", t);
/* 78 */       context.sendMessage(MESSAGE_COMMANDS_CHUNKINFO_SERIALIZED_FAILED);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkLightingCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */