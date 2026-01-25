/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.component.Resource;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.metrics.MetricProvider;
/*     */ import com.hypixel.hytale.metrics.MetricResults;
/*     */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import com.hypixel.hytale.storage.IndexedStorageFile;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.ints.IntListIterator;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSets;
/*     */ import java.io.Closeable;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Iterator;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Stream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexedStorageCache
/*     */   implements Closeable, MetricProvider, Resource<ChunkStore>
/*     */ {
/*     */   @Nonnull
/*     */   public static final MetricsRegistry<IndexedStorageCache> METRICS_REGISTRY;
/*     */   
/*     */   static {
/* 235 */     METRICS_REGISTRY = (new MetricsRegistry()).register("Files", cache -> (CacheEntryMetricData[])cache.cache.long2ObjectEntrySet().stream().map(CacheEntryMetricData::new).toArray(()), (Codec)new ArrayCodec(CacheEntryMetricData.CODEC, x$0 -> new CacheEntryMetricData[x$0]));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static ResourceType<ChunkStore, IndexedStorageCache> getResourceType() {
/* 241 */     return Universe.get().getIndexedStorageCacheResourceType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 248 */   private final Long2ObjectConcurrentHashMap<IndexedStorageFile> cache = new Long2ObjectConcurrentHashMap(true, ChunkUtil.NOT_FOUND);
/*     */ 
/*     */   
/*     */   private Path path;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Long2ObjectConcurrentHashMap<IndexedStorageFile> getCache() {
/* 257 */     return this.cache;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 262 */     IOException exception = null;
/* 263 */     for (Iterator<IndexedStorageFile> iterator = this.cache.values().iterator(); iterator.hasNext();) {
/*     */       try {
/* 265 */         ((IndexedStorageFile)iterator.next()).close();
/* 266 */         iterator.remove();
/* 267 */       } catch (Exception e) {
/* 268 */         if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 269 */         exception.addSuppressed(e);
/*     */       } 
/*     */     } 
/* 272 */     if (exception != null) throw exception;
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public IndexedStorageFile getOrTryOpen(int regionX, int regionZ, boolean flushOnWrite) {
/* 284 */     return (IndexedStorageFile)this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
/*     */           Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
/*     */           
/*     */           if (!Files.exists(regionFile, new java.nio.file.LinkOption[0])) {
/*     */             return null;
/*     */           }
/*     */           try {
/*     */             IndexedStorageFile open = IndexedStorageFile.open(regionFile, new OpenOption[] { StandardOpenOption.READ, StandardOpenOption.WRITE });
/*     */             open.setFlushOnWrite(flushOnWrite);
/*     */             return open;
/* 294 */           } catch (FileNotFoundException e) {
/*     */             return null;
/* 296 */           } catch (IOException e) {
/*     */             throw SneakyThrow.sneakyThrow(e);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public IndexedStorageFile getOrCreate(int regionX, int regionZ, boolean flushOnWrite) {
/* 311 */     return (IndexedStorageFile)this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
/*     */           try {
/*     */             if (!Files.exists(this.path, new java.nio.file.LinkOption[0])) {
/*     */               try {
/*     */                 Files.createDirectory(this.path, (FileAttribute<?>[])new FileAttribute[0]);
/* 316 */               } catch (FileAlreadyExistsException fileAlreadyExistsException) {}
/*     */             }
/*     */             
/*     */             Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
/*     */             
/*     */             IndexedStorageFile open = IndexedStorageFile.open(regionFile, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE });
/*     */             open.setFlushOnWrite(flushOnWrite);
/*     */             return open;
/* 324 */           } catch (IOException e) {
/*     */             throw SneakyThrow.sneakyThrow(e);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public LongSet getIndexes() throws IOException {
/* 338 */     if (!Files.exists(this.path, new java.nio.file.LinkOption[0])) return (LongSet)LongSets.EMPTY_SET; 
/* 339 */     LongOpenHashSet chunkIndexes = new LongOpenHashSet();
/* 340 */     Stream<Path> stream = Files.list(this.path); 
/* 341 */     try { stream.forEach(path -> {
/*     */             long regionIndex;
/*     */             if (Files.isDirectory(path, new java.nio.file.LinkOption[0]))
/*     */               return; 
/*     */             try {
/*     */               regionIndex = IndexedStorageChunkStorageProvider.fromFileName(path.getFileName().toString());
/* 347 */             } catch (IllegalArgumentException e) {
/*     */               return;
/*     */             } 
/*     */             
/*     */             int regionX = ChunkUtil.xOfChunkIndex(regionIndex);
/*     */             
/*     */             int regionZ = ChunkUtil.zOfChunkIndex(regionIndex);
/*     */             
/*     */             IndexedStorageFile regionFile = getOrTryOpen(regionX, regionZ, true);
/*     */             if (regionFile == null) {
/*     */               return;
/*     */             }
/*     */             IntList blobIndexes = regionFile.keys();
/*     */             IntListIterator iterator = blobIndexes.iterator();
/*     */             while (iterator.hasNext()) {
/*     */               int blobIndex = iterator.nextInt();
/*     */               int localX = ChunkUtil.xFromColumn(blobIndex);
/*     */               int localZ = ChunkUtil.zFromColumn(blobIndex);
/*     */               int chunkX = regionX << 5 | localX;
/*     */               int chunkZ = regionZ << 5 | localZ;
/*     */               chunkIndexes.add(ChunkUtil.indexChunk(chunkX, chunkZ));
/*     */             } 
/*     */           });
/* 370 */       if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
/* 371 */         try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return (LongSet)chunkIndexes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() throws IOException {
/* 380 */     IOException exception = null;
/*     */     
/* 382 */     for (IndexedStorageFile indexedStorageFile : this.cache.values()) {
/*     */       try {
/* 384 */         indexedStorageFile.force(false);
/* 385 */       } catch (Exception e) {
/* 386 */         if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 387 */         exception.addSuppressed(e);
/*     */       } 
/*     */     } 
/* 390 */     if (exception != null) throw exception;
/*     */   
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public MetricResults toMetricResults() {
/* 396 */     return METRICS_REGISTRY.toMetricResults(this);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Resource<ChunkStore> clone() {
/* 402 */     return new IndexedStorageCache();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class CacheEntryMetricData
/*     */   {
/*     */     @Nonnull
/*     */     private static final Codec<CacheEntryMetricData> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     private long key;
/*     */ 
/*     */ 
/*     */     
/*     */     private IndexedStorageFile value;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 425 */       CODEC = (Codec<CacheEntryMetricData>)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CacheEntryMetricData.class, CacheEntryMetricData::new).append(new KeyedCodec("Key", (Codec)Codec.LONG), (entry, o) -> entry.key = o.longValue(), entry -> Long.valueOf(entry.key)).add()).append(new KeyedCodec("File", (Codec)IndexedStorageFile.METRICS_REGISTRY), (entry, o) -> entry.value = o, entry -> entry.value).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CacheEntryMetricData() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public CacheEntryMetricData(@Nonnull Long2ObjectMap.Entry<IndexedStorageFile> entry) {
/* 445 */       this.key = entry.getLongKey();
/* 446 */       this.value = (IndexedStorageFile)entry.getValue();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\IndexedStorageChunkStorageProvider$IndexedStorageCache.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */