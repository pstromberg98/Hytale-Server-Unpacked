/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.metrics.MetricProvider;
/*     */ import com.hypixel.hytale.metrics.MetricResults;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkLoader;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import com.hypixel.hytale.storage.IndexedStorageFile;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class IndexedStorageChunkLoader
/*     */   extends BufferChunkLoader
/*     */   implements MetricProvider
/*     */ {
/*     */   private final boolean flushOnWrite;
/*     */   
/*     */   public IndexedStorageChunkLoader(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
/* 100 */     super(store);
/* 101 */     this.flushOnWrite = flushOnWrite;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 106 */     ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).close();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<ByteBuffer> loadBuffer(int x, int z) {
/* 112 */     int regionX = x >> 5;
/* 113 */     int regionZ = z >> 5;
/* 114 */     int localX = x & 0x1F;
/* 115 */     int localZ = z & 0x1F;
/* 116 */     int index = ChunkUtil.indexColumn(localX, localZ);
/*     */     
/* 118 */     IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 119 */     return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
/*     */             IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
/*     */             return (chunks == null) ? null : chunks.readBlob(index);
/*     */           }));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public LongSet getIndexes() throws IOException {
/* 129 */     return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).getIndexes();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public MetricResults toMetricResults() {
/* 136 */     if (((ChunkStore)getStore().getExternalData()).getSaver() instanceof IndexedStorageChunkStorageProvider.IndexedStorageChunkSaver) return null; 
/* 137 */     return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).toMetricResults();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\IndexedStorageChunkStorageProvider$IndexedStorageChunkLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */