/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.component.Resource;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.system.StoreSystem;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.metrics.MetricProvider;
/*     */ import com.hypixel.hytale.metrics.MetricResults;
/*     */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkLoader;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.BufferChunkSaver;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
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
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.FileAlreadyExistsException;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.CompletableFuture;
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
/*     */ public class IndexedStorageChunkStorageProvider
/*     */   implements IChunkStorageProvider
/*     */ {
/*     */   public static final String ID = "IndexedStorage";
/*     */   @Nonnull
/*     */   public static final BuilderCodec<IndexedStorageChunkStorageProvider> CODEC;
/*     */   
/*     */   static {
/*  66 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IndexedStorageChunkStorageProvider.class, IndexedStorageChunkStorageProvider::new).documentation("Uses the indexed storage file format to store chunks.")).appendInherited(new KeyedCodec("FlushOnWrite", (Codec)Codec.BOOLEAN), (o, i) -> o.flushOnWrite = i.booleanValue(), o -> Boolean.valueOf(o.flushOnWrite), (o, p) -> o.flushOnWrite = p.flushOnWrite).documentation("Controls whether the indexed storage flushes during writes.\nRecommended to be enabled to prevent corruption of chunks during unclean shutdowns.").add()).build();
/*     */   }
/*     */   
/*     */   private boolean flushOnWrite = true;
/*     */   
/*     */   @Nonnull
/*     */   public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) {
/*  73 */     return (IChunkLoader)new IndexedStorageChunkLoader(store, this.flushOnWrite);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) {
/*  78 */     return (IChunkSaver)new IndexedStorageChunkSaver(store, this.flushOnWrite);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/*  84 */     return "IndexedStorageChunkStorageProvider{}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IndexedStorageChunkLoader
/*     */     extends BufferChunkLoader
/*     */     implements MetricProvider
/*     */   {
/*     */     private final boolean flushOnWrite;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IndexedStorageChunkLoader(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
/* 100 */       super(store);
/* 101 */       this.flushOnWrite = flushOnWrite;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 106 */       ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).close();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<ByteBuffer> loadBuffer(int x, int z) {
/* 112 */       int regionX = x >> 5;
/* 113 */       int regionZ = z >> 5;
/* 114 */       int localX = x & 0x1F;
/* 115 */       int localZ = z & 0x1F;
/* 116 */       int index = ChunkUtil.indexColumn(localX, localZ);
/*     */       
/* 118 */       IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 119 */       return CompletableFuture.supplyAsync(SneakyThrow.sneakySupplier(() -> {
/*     */               IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
/*     */               return (chunks == null) ? null : chunks.readBlob(index);
/*     */             }));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() throws IOException {
/* 129 */       return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).getIndexes();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public MetricResults toMetricResults() {
/* 136 */       if (((ChunkStore)getStore().getExternalData()).getSaver() instanceof IndexedStorageChunkStorageProvider.IndexedStorageChunkSaver) return null; 
/* 137 */       return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).toMetricResults();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class IndexedStorageChunkSaver
/*     */     extends BufferChunkSaver
/*     */     implements MetricProvider
/*     */   {
/*     */     private final boolean flushOnWrite;
/*     */     
/*     */     protected IndexedStorageChunkSaver(@Nonnull Store<ChunkStore> store, boolean flushOnWrite) {
/* 149 */       super(store);
/* 150 */       this.flushOnWrite = flushOnWrite;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 155 */       IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 156 */       indexedStorageCache.close();
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Void> saveBuffer(int x, int z, @Nonnull ByteBuffer buffer) {
/* 162 */       int regionX = x >> 5;
/* 163 */       int regionZ = z >> 5;
/* 164 */       int localX = x & 0x1F;
/* 165 */       int localZ = z & 0x1F;
/* 166 */       int index = ChunkUtil.indexColumn(localX, localZ);
/*     */       
/* 168 */       IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 169 */       return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
/*     */               IndexedStorageFile chunks = indexedStorageCache.getOrCreate(regionX, regionZ, this.flushOnWrite);
/*     */               chunks.writeBlob(index, buffer);
/*     */             }));
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Void> removeBuffer(int x, int z) {
/* 178 */       int regionX = x >> 5;
/* 179 */       int regionZ = z >> 5;
/* 180 */       int localX = x & 0x1F;
/* 181 */       int localZ = z & 0x1F;
/* 182 */       int index = ChunkUtil.indexColumn(localX, localZ);
/*     */       
/* 184 */       IndexedStorageChunkStorageProvider.IndexedStorageCache indexedStorageCache = (IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType());
/* 185 */       return CompletableFuture.runAsync(SneakyThrow.sneakyRunnable(() -> {
/*     */               IndexedStorageFile chunks = indexedStorageCache.getOrTryOpen(regionX, regionZ, this.flushOnWrite);
/*     */               if (chunks != null)
/*     */                 chunks.removeBlob(index); 
/*     */             }));
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() throws IOException {
/* 194 */       return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).getIndexes();
/*     */     }
/*     */ 
/*     */     
/*     */     public void flush() throws IOException {
/* 199 */       ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).flush();
/*     */     }
/*     */ 
/*     */     
/*     */     public MetricResults toMetricResults() {
/* 204 */       return ((IndexedStorageChunkStorageProvider.IndexedStorageCache)getStore().getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).toMetricResults();
/*     */     }
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private static String toFileName(int regionX, int regionZ) {
/* 210 */     return "" + regionX + "." + regionX + ".region.bin";
/*     */   }
/*     */   
/*     */   private static long fromFileName(@Nonnull String fileName) {
/* 214 */     String[] split = fileName.split("\\.");
/*     */     
/* 216 */     if (split.length != 4) throw new IllegalArgumentException("Unexpected file name format!"); 
/* 217 */     if (!"region".equals(split[2])) throw new IllegalArgumentException("Unexpected file name format!"); 
/* 218 */     if (!"bin".equals(split[3])) throw new IllegalArgumentException("Unexpected file extension!");
/*     */     
/* 220 */     int regionX = Integer.parseInt(split[0]);
/* 221 */     int regionZ = Integer.parseInt(split[1]);
/* 222 */     return ChunkUtil.indexChunk(regionX, regionZ);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class IndexedStorageCache
/*     */     implements Closeable, MetricProvider, Resource<ChunkStore>
/*     */   {
/*     */     @Nonnull
/*     */     public static final MetricsRegistry<IndexedStorageCache> METRICS_REGISTRY;
/*     */ 
/*     */     
/*     */     static {
/* 235 */       METRICS_REGISTRY = (new MetricsRegistry()).register("Files", cache -> (CacheEntryMetricData[])cache.cache.long2ObjectEntrySet().stream().map(CacheEntryMetricData::new).toArray(()), (Codec)new ArrayCodec(CacheEntryMetricData.CODEC, x$0 -> new CacheEntryMetricData[x$0]));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public static ResourceType<ChunkStore, IndexedStorageCache> getResourceType() {
/* 241 */       return Universe.get().getIndexedStorageCacheResourceType();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 248 */     private final Long2ObjectConcurrentHashMap<IndexedStorageFile> cache = new Long2ObjectConcurrentHashMap(true, ChunkUtil.NOT_FOUND);
/*     */ 
/*     */     
/*     */     private Path path;
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Long2ObjectConcurrentHashMap<IndexedStorageFile> getCache() {
/* 257 */       return this.cache;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 262 */       IOException exception = null;
/* 263 */       for (Iterator<IndexedStorageFile> iterator = this.cache.values().iterator(); iterator.hasNext();) {
/*     */         try {
/* 265 */           ((IndexedStorageFile)iterator.next()).close();
/* 266 */           iterator.remove();
/* 267 */         } catch (Exception e) {
/* 268 */           if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 269 */           exception.addSuppressed(e);
/*     */         } 
/*     */       } 
/* 272 */       if (exception != null) throw exception;
/*     */     
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public IndexedStorageFile getOrTryOpen(int regionX, int regionZ, boolean flushOnWrite) {
/* 284 */       return (IndexedStorageFile)this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
/*     */             Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
/*     */             
/*     */             if (!Files.exists(regionFile, new java.nio.file.LinkOption[0])) {
/*     */               return null;
/*     */             }
/*     */             try {
/*     */               IndexedStorageFile open = IndexedStorageFile.open(regionFile, new OpenOption[] { StandardOpenOption.READ, StandardOpenOption.WRITE });
/*     */               open.setFlushOnWrite(flushOnWrite);
/*     */               return open;
/* 294 */             } catch (FileNotFoundException e) {
/*     */               return null;
/* 296 */             } catch (IOException e) {
/*     */               throw SneakyThrow.sneakyThrow(e);
/*     */             } 
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public IndexedStorageFile getOrCreate(int regionX, int regionZ, boolean flushOnWrite) {
/* 311 */       return (IndexedStorageFile)this.cache.computeIfAbsent(ChunkUtil.indexChunk(regionX, regionZ), k -> {
/*     */             try {
/*     */               if (!Files.exists(this.path, new java.nio.file.LinkOption[0])) {
/*     */                 try {
/*     */                   Files.createDirectory(this.path, (FileAttribute<?>[])new FileAttribute[0]);
/* 316 */                 } catch (FileAlreadyExistsException fileAlreadyExistsException) {}
/*     */               }
/*     */               
/*     */               Path regionFile = this.path.resolve(IndexedStorageChunkStorageProvider.toFileName(regionX, regionZ));
/*     */               
/*     */               IndexedStorageFile open = IndexedStorageFile.open(regionFile, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE });
/*     */               open.setFlushOnWrite(flushOnWrite);
/*     */               return open;
/* 324 */             } catch (IOException e) {
/*     */               throw SneakyThrow.sneakyThrow(e);
/*     */             } 
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() throws IOException {
/* 338 */       if (!Files.exists(this.path, new java.nio.file.LinkOption[0])) return (LongSet)LongSets.EMPTY_SET; 
/* 339 */       LongOpenHashSet chunkIndexes = new LongOpenHashSet();
/* 340 */       Stream<Path> stream = Files.list(this.path); 
/* 341 */       try { stream.forEach(path -> {
/*     */               long regionIndex;
/*     */               if (Files.isDirectory(path, new java.nio.file.LinkOption[0]))
/*     */                 return; 
/*     */               try {
/*     */                 regionIndex = IndexedStorageChunkStorageProvider.fromFileName(path.getFileName().toString());
/* 347 */               } catch (IllegalArgumentException e) {
/*     */                 return;
/*     */               } 
/*     */               
/*     */               int regionX = ChunkUtil.xOfChunkIndex(regionIndex);
/*     */               
/*     */               int regionZ = ChunkUtil.zOfChunkIndex(regionIndex);
/*     */               
/*     */               IndexedStorageFile regionFile = getOrTryOpen(regionX, regionZ, true);
/*     */               if (regionFile == null) {
/*     */                 return;
/*     */               }
/*     */               IntList blobIndexes = regionFile.keys();
/*     */               IntListIterator iterator = blobIndexes.iterator();
/*     */               while (iterator.hasNext()) {
/*     */                 int blobIndex = iterator.nextInt();
/*     */                 int localX = ChunkUtil.xFromColumn(blobIndex);
/*     */                 int localZ = ChunkUtil.zFromColumn(blobIndex);
/*     */                 int chunkX = regionX << 5 | localX;
/*     */                 int chunkZ = regionZ << 5 | localZ;
/*     */                 chunkIndexes.add(ChunkUtil.indexChunk(chunkX, chunkZ));
/*     */               } 
/*     */             });
/* 370 */         if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
/* 371 */           try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  return (LongSet)chunkIndexes;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void flush() throws IOException {
/* 380 */       IOException exception = null;
/*     */       
/* 382 */       for (IndexedStorageFile indexedStorageFile : this.cache.values()) {
/*     */         try {
/* 384 */           indexedStorageFile.force(false);
/* 385 */         } catch (Exception e) {
/* 386 */           if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 387 */           exception.addSuppressed(e);
/*     */         } 
/*     */       } 
/* 390 */       if (exception != null) throw exception;
/*     */     
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     public MetricResults toMetricResults() {
/* 396 */       return METRICS_REGISTRY.toMetricResults(this);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Resource<ChunkStore> clone() {
/* 402 */       return new IndexedStorageCache();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static class CacheEntryMetricData
/*     */     {
/*     */       @Nonnull
/*     */       private static final Codec<CacheEntryMetricData> CODEC;
/*     */ 
/*     */ 
/*     */       
/*     */       private long key;
/*     */ 
/*     */ 
/*     */       
/*     */       private IndexedStorageFile value;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       static {
/* 425 */         CODEC = (Codec<CacheEntryMetricData>)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CacheEntryMetricData.class, CacheEntryMetricData::new).append(new KeyedCodec("Key", (Codec)Codec.LONG), (entry, o) -> entry.key = o.longValue(), entry -> Long.valueOf(entry.key)).add()).append(new KeyedCodec("File", (Codec)IndexedStorageFile.METRICS_REGISTRY), (entry, o) -> entry.value = o, entry -> entry.value).add()).build();
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public CacheEntryMetricData() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public CacheEntryMetricData(@Nonnull Long2ObjectMap.Entry<IndexedStorageFile> entry)
/*     */       {
/* 445 */         this.key = entry.getLongKey();
/* 446 */         this.value = (IndexedStorageFile)entry.getValue(); } } } private static class CacheEntryMetricData { @Nonnull private static final Codec<CacheEntryMetricData> CODEC; public CacheEntryMetricData(@Nonnull Long2ObjectMap.Entry<IndexedStorageFile> entry) { this.key = entry.getLongKey(); this.value = (IndexedStorageFile)entry.getValue(); }
/*     */     
/*     */     private long key; private IndexedStorageFile value;
/*     */     
/*     */     static {
/*     */       CODEC = (Codec<CacheEntryMetricData>)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(CacheEntryMetricData.class, CacheEntryMetricData::new).append(new KeyedCodec("Key", (Codec)Codec.LONG), (entry, o) -> entry.key = o.longValue(), entry -> Long.valueOf(entry.key)).add()).append(new KeyedCodec("File", (Codec)IndexedStorageFile.METRICS_REGISTRY), (entry, o) -> entry.value = o, entry -> entry.value).add()).build();
/*     */     }
/*     */     
/*     */     public CacheEntryMetricData() {} }
/*     */   
/*     */   public static class IndexedStorageCacheSetupSystem extends StoreSystem<ChunkStore> { @Nullable
/*     */     public SystemGroup<ChunkStore> getGroup() {
/* 458 */       return ChunkStore.INIT_GROUP;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {
/* 465 */       World world = ((ChunkStore)store.getExternalData()).getWorld();
/* 466 */       ((IndexedStorageChunkStorageProvider.IndexedStorageCache)store.getResource(IndexedStorageChunkStorageProvider.IndexedStorageCache.getResourceType())).path = world.getSavePath().resolve("chunks");
/*     */     }
/*     */     
/*     */     public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {} }
/*     */ 
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\IndexedStorageChunkStorageProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */