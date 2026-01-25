/*     */ package com.hypixel.hytale.server.core.command.commands.world.chunk;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector2i;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.lighting.ChunkLightingManager;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class ChunkFixHeightMapCommand
/*     */   extends AbstractWorldCommand {
/*     */   @Nonnull
/*  28 */   private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_STARTED = Message.translation("server.commands.chunk.fixHeightMap.started");
/*     */   @Nonnull
/*  30 */   private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE = Message.translation("server.commands.chunk.fixHeightMap.done");
/*     */   @Nonnull
/*  32 */   private static final Message MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_INVALIDATING_LIGHTING = Message.translation("server.commands.chunk.fixHeightMap.invalidatingLighting");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  38 */   private final RequiredArg<RelativeChunkPosition> chunkPosArg = withRequiredArg("x z", "server.commands.chunk.fixheight.position.desc", ArgTypes.RELATIVE_CHUNK_POSITION);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChunkFixHeightMapCommand() {
/*  44 */     super("fixheight", "server.commands.chunk.fixheight.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void execute(@Nonnull CommandContext context, @Nonnull World world, @Nonnull Store<EntityStore> store) {
/*  49 */     RelativeChunkPosition chunkPosition = (RelativeChunkPosition)this.chunkPosArg.get(context);
/*  50 */     Vector2i position = chunkPosition.getChunkPosition(context, (ComponentAccessor)store);
/*  51 */     fixHeightMap(context, world, position.x, position.y);
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
/*     */   private static void fixHeightMap(@Nonnull CommandContext context, @Nonnull World world, int chunkX, int chunkZ) {
/*  63 */     ChunkLightingManager chunkLighting = world.getChunkLighting();
/*  64 */     ChunkStore chunkStore = world.getChunkStore();
/*  65 */     Store<ChunkStore> chunkStoreStore = chunkStore.getStore();
/*  66 */     long chunkIndex = ChunkUtil.indexChunk(chunkX, chunkZ);
/*  67 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*     */ 
/*     */     
/*  70 */     if (chunkRef == null || !chunkRef.isValid()) {
/*  71 */       context.sendMessage(Message.translation("server.commands.errors.chunkNotLoaded")
/*  72 */           .param("chunkX", chunkX)
/*  73 */           .param("chunkZ", chunkZ)
/*  74 */           .param("world", world.getName()));
/*     */       
/*     */       return;
/*     */     } 
/*  78 */     WorldChunk worldChunkComponent = (WorldChunk)chunkStoreStore.getComponent(chunkRef, WorldChunk.getComponentType());
/*  79 */     assert worldChunkComponent != null;
/*     */     
/*  81 */     BlockChunk blockChunkComponent = (BlockChunk)chunkStoreStore.getComponent(chunkRef, BlockChunk.getComponentType());
/*  82 */     assert blockChunkComponent != null;
/*     */     
/*  84 */     context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_STARTED);
/*  85 */     blockChunkComponent.updateHeightmap();
/*  86 */     context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE);
/*     */     
/*  88 */     context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_INVALIDATING_LIGHTING);
/*     */ 
/*     */     
/*  91 */     for (int chunkSectionY = 0; chunkSectionY < 10; chunkSectionY++) {
/*  92 */       blockChunkComponent.getSectionAtIndex(chunkSectionY).invalidateLocalLight();
/*     */     }
/*     */ 
/*     */     
/*  96 */     chunkLighting.invalidateLightInChunk(worldChunkComponent);
/*  97 */     context.sendMessage(MESSAGE_COMMANDS_CHUNK_FIXHEIGHTMAP_DONE);
/*     */     
/*  99 */     context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.waitingForLighting")
/* 100 */         .param("x", chunkX)
/* 101 */         .param("z", chunkZ));
/* 102 */     int[] count = { 0 };
/* 103 */     ScheduledFuture[] arrayOfScheduledFuture = new ScheduledFuture[1];
/* 104 */     arrayOfScheduledFuture[0] = HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> { if (chunkLighting.isQueued(chunkX, chunkZ)) { count[0] = count[0] + 1; if (count[0] > 60) { scheduledFuture[0].cancel(false); context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.lightingError").param("x", chunkX).param("z", chunkZ)); }  return; }  world.getNotificationHandler().updateChunk(chunkIndex); context.sendMessage(Message.translation("server.commands.chunk.fixHeightMap.lightingFinished").param("x", chunkX).param("z", chunkZ)); scheduledFuture[0].cancel(false); }1L, 1L, TimeUnit.SECONDS);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\world\chunk\ChunkFixHeightMapCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */