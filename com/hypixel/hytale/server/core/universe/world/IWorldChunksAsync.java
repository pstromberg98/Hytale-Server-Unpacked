/*    */ package com.hypixel.hytale.server.core.universe.world;
/*    */ 
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public interface IWorldChunksAsync
/*    */ {
/*    */   CompletableFuture<WorldChunk> getChunkAsync(long paramLong);
/*    */   
/*    */   CompletableFuture<WorldChunk> getNonTickingChunkAsync(long paramLong);
/*    */   
/*    */   default CompletableFuture<WorldChunk> getChunkAsync(int x, int z) {
/* 42 */     return getChunkAsync(ChunkUtil.indexChunk(x, z));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default CompletableFuture<WorldChunk> getNonTickingChunkAsync(int x, int z) {
/* 55 */     return getNonTickingChunkAsync(ChunkUtil.indexChunk(x, z));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\IWorldChunksAsync.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */