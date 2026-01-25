/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.metrics.MetricProvider;
/*     */ import com.hypixel.hytale.metrics.MetricResults;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkSaver;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import com.hypixel.hytale.storage.IndexedStorageFile;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.CompletableFuture;
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
/*     */ public class IndexedStorageChunkSaver
/*     */   extends BufferChunkSaver
/*     */   implements MetricProvider
/*     */ {
/*     */   private final boolean flushOnWrite;
/*     */   
/*     */   protected IndexedStorageChunkSaver(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
/* 149 */     super(store);
/* 150 */     this.flushOnWrite = flushOnWrite;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 155 */     IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 156 */     indexedStorageCache.close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<Void> saveBuffer(int x, int z, @Nonnull ByteBuffer buffer) {
/* 162 */     int regionX = x >> 5;
/* 163 */     int regionZ = z >> 5;
/* 164 */     int localX = x & 0x1F;
/* 165 */     int localZ = z & 0x1F;
/* 166 */     int index = ChunkUtil.indexColumn(localX, localZ);
/*     */     
/* 168 */     IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 169 */     return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
/*     */             IndexedStorageFile chunks = indexedStorageCache.getOrCreate(regionX, regionZ, this.flushOnWrite);
/*     */             chunks.writeBlob(index, buffer);
/*     */           }));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<Void> removeBuffer(int x, int z) {
/* 178 */     int regionX = x >> 5;
/* 179 */     int regionZ = z >> 5;
/* 180 */     int localX = x & 0x1F;
/* 181 */     int localZ = z & 0x1F;
/* 182 */     int index = ChunkUtil.indexColumn(localX, localZ);
/*     */     
/* 184 */     IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 185 */     return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
/*     */             IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
/*     */             if (chunks != null)
/*     */               chunks.removeBlob(index); 
/*     */           }));
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public LongSet getIndexes() throws IOException {
/* 194 */     return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).getIndexes();
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 199 */     ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public MetricResults toMetricResults() {
/* 204 */     return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).toMetricResults();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\IndexedStorageChunkStorageProvider$IndexedStorageChunkSaver.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */