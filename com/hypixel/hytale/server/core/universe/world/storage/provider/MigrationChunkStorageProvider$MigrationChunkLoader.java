/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import it.unimi.dsi.fastutil.longs.LongCollection;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import javax.annotation.Nonnull;
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
/*     */ 
/*     */ public class MigrationChunkLoader
/*     */   implements IChunkLoader
/*     */ {
/*     */   @Nonnull
/*     */   private final IChunkLoader[] loaders;
/*     */   
/*     */   public MigrationChunkLoader(@Nonnull IChunkLoader... loaders) {
/* 126 */     this.loaders = loaders;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 131 */     IOException exception = null;
/* 132 */     for (IChunkLoader loader : this.loaders) {
/*     */       try {
/* 134 */         loader.close();
/* 135 */       } catch (Exception e) {
/* 136 */         if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 137 */         exception.addSuppressed(e);
/*     */       } 
/*     */     } 
/* 140 */     if (exception != null) throw exception;
/*     */   
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<Holder<ChunkStore>> loadHolder(int x, int z) {
/* 146 */     CompletableFuture<Holder<ChunkStore>> future = this.loaders[0].loadHolder(x, z);
/* 147 */     for (int i = 1; i < this.loaders.length; i++) {
/*     */       
/* 149 */       IChunkLoader loader = this.loaders[i];
/* 150 */       CompletableFuture<Holder<ChunkStore>> previous = future;
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
/*     */ 
/*     */       
/* 166 */       future = previous.handle((worldChunk, throwable) -> (throwable != null) ? loader.loadHolder(x, z).exceptionally(()) : ((worldChunk == null) ? loader.loadHolder(x, z) : previous)).thenCompose(Function.identity());
/*     */     } 
/* 168 */     return future;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public LongSet getIndexes() throws IOException {
/* 174 */     LongOpenHashSet indexes = new LongOpenHashSet();
/* 175 */     for (IChunkLoader loader : this.loaders) {
/* 176 */       indexes.addAll((LongCollection)loader.getIndexes());
/*     */     }
/* 178 */     return (LongSet)indexes;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\MigrationChunkStorageProvider$MigrationChunkLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */