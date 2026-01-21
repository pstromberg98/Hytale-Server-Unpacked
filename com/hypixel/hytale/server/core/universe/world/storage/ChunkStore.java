/*      */ package com.hypixel.hytale.server.core.universe.world.storage;
/*      */ import com.hypixel.fastutil.longs.Long2ObjectConcurrentHashMap;
/*      */ import com.hypixel.hytale.codec.Codec;
/*      */ import com.hypixel.hytale.codec.store.CodecKey;
/*      */ import com.hypixel.hytale.common.util.FormatUtil;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.ComponentRegistry;
/*      */ import com.hypixel.hytale.component.Holder;
/*      */ import com.hypixel.hytale.component.IResourceStorage;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.component.RemoveReason;
/*      */ import com.hypixel.hytale.component.ResourceType;
/*      */ import com.hypixel.hytale.component.Store;
/*      */ import com.hypixel.hytale.component.SystemGroup;
/*      */ import com.hypixel.hytale.component.SystemType;
/*      */ import com.hypixel.hytale.component.system.ISystem;
/*      */ import com.hypixel.hytale.component.system.data.EntityDataSystem;
/*      */ import com.hypixel.hytale.event.IEventDispatcher;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.metrics.MetricProvider;
/*      */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.component.ChunkSavingSystems;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.component.ChunkUnloadingSystem;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.provider.IChunkStorageProvider;
/*      */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*      */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*      */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*      */ import it.unimi.dsi.fastutil.longs.LongSet;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*      */ import java.io.IOException;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.CompletionStage;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.locks.StampedLock;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ public class ChunkStore implements WorldProvider {
/*      */   @Nonnull
/*   53 */   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final MetricsRegistry<ChunkStore> METRICS_REGISTRY;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*   64 */     METRICS_REGISTRY = (new MetricsRegistry()).register("Store", ChunkStore::getStore, (Codec)Store.METRICS_REGISTRY).register("ChunkLoader", MetricProvider.maybe(ChunkStore::getLoader)).register("ChunkSaver", MetricProvider.maybe(ChunkStore::getSaver)).register("WorldGen", MetricProvider.maybe(ChunkStore::getGenerator)).register("TotalGeneratedChunkCount", chunkComponentStore -> Long.valueOf(chunkComponentStore.totalGeneratedChunksCount.get()), (Codec)Codec.LONG).register("TotalLoadedChunkCount", chunkComponentStore -> Long.valueOf(chunkComponentStore.totalLoadedChunksCount.get()), (Codec)Codec.LONG);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*   69 */   public static final long MAX_FAILURE_BACKOFF_NANOS = TimeUnit.SECONDS.toNanos(10L);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   74 */   public static final long FAILURE_BACKOFF_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   79 */   public static final ComponentRegistry<ChunkStore> REGISTRY = new ComponentRegistry();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*   84 */   public static final CodecKey<Holder<ChunkStore>> HOLDER_CODEC_KEY = new CodecKey("ChunkHolder");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*   90 */     Objects.requireNonNull(REGISTRY); CodecStore.STATIC.putCodecSupplier(HOLDER_CODEC_KEY, REGISTRY::getEntityCodec);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*   97 */   public static final SystemType<ChunkStore, LoadPacketDataQuerySystem> LOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(LoadPacketDataQuerySystem.class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  103 */   public static final SystemType<ChunkStore, LoadFuturePacketDataQuerySystem> LOAD_FUTURE_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(LoadFuturePacketDataQuerySystem.class);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  109 */   public static final SystemType<ChunkStore, UnloadPacketDataQuerySystem> UNLOAD_PACKETS_DATA_QUERY_SYSTEM_TYPE = REGISTRY.registerSystemType(UnloadPacketDataQuerySystem.class);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  116 */   public static final ResourceType<ChunkStore, ChunkUnloadingSystem.Data> UNLOAD_RESOURCE = REGISTRY.registerResource(ChunkUnloadingSystem.Data.class, com.hypixel.hytale.server.core.universe.world.storage.component.ChunkUnloadingSystem.Data::new);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  122 */   public static final ResourceType<ChunkStore, ChunkSavingSystems.Data> SAVE_RESOURCE = REGISTRY.registerResource(ChunkSavingSystems.Data.class, com.hypixel.hytale.server.core.universe.world.storage.component.ChunkSavingSystems.Data::new);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  127 */   public static final SystemGroup<ChunkStore> INIT_GROUP = REGISTRY.registerSystemGroup();
/*      */   
/*      */   @Nonnull
/*      */   private final World world;
/*      */   
/*      */   static {
/*  133 */     REGISTRY.registerSystem((ISystem)new ChunkLoaderSaverSetupSystem());
/*  134 */     REGISTRY.registerSystem((ISystem)new ChunkUnloadingSystem());
/*      */     
/*  136 */     REGISTRY.registerSystem((ISystem)new ChunkSavingSystems.WorldRemoved());
/*  137 */     REGISTRY.registerSystem((ISystem)new ChunkSavingSystems.Ticking());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  149 */   private final Long2ObjectConcurrentHashMap<ChunkLoadState> chunks = new Long2ObjectConcurrentHashMap(true, ChunkUtil.NOT_FOUND);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Store<ChunkStore> store;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private IChunkLoader loader;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private IChunkSaver saver;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private IWorldGen generator;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  180 */   private CompletableFuture<Void> generatorLoaded = new CompletableFuture<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  188 */   private final StampedLock generatorLock = new StampedLock();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  195 */   private final AtomicInteger totalGeneratedChunksCount = new AtomicInteger();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  202 */   private final AtomicInteger totalLoadedChunksCount = new AtomicInteger();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ChunkStore(@Nonnull World world) {
/*  210 */     this.world = world;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public World getWorld() {
/*  216 */     return this.world;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Store<ChunkStore> getStore() {
/*  224 */     return this.store;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public IChunkLoader getLoader() {
/*  232 */     return this.loader;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public IChunkSaver getSaver() {
/*  240 */     return this.saver;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public IWorldGen getGenerator() {
/*  248 */     long readStamp = this.generatorLock.readLock();
/*      */     try {
/*  250 */       return this.generator;
/*      */     } finally {
/*  252 */       this.generatorLock.unlockRead(readStamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdownGenerator() {
/*  260 */     setGenerator(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGenerator(@Nullable IWorldGen generator) {
/*  269 */     long writeStamp = this.generatorLock.writeLock();
/*      */     try {
/*  271 */       if (this.generator != null) {
/*  272 */         this.generator.shutdown();
/*      */       }
/*      */       
/*  275 */       this.totalGeneratedChunksCount.set(0);
/*  276 */       this.generator = generator;
/*      */       
/*  278 */       if (generator != null) {
/*      */         
/*  280 */         this.generatorLoaded.complete(null);
/*  281 */         this.generatorLoaded = new CompletableFuture<>();
/*      */       } 
/*      */     } finally {
/*  284 */       this.generatorLock.unlockWrite(writeStamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public LongSet getChunkIndexes() {
/*  293 */     return LongSets.unmodifiable((LongSet)this.chunks.keySet());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getLoadedChunksCount() {
/*  300 */     return this.chunks.size();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTotalGeneratedChunksCount() {
/*  307 */     return this.totalGeneratedChunksCount.get();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getTotalLoadedChunksCount() {
/*  314 */     return this.totalLoadedChunksCount.get();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void start(@Nonnull IResourceStorage resourceStorage) {
/*  323 */     this.store = REGISTRY.addStore(this, resourceStorage, store -> this.store = store);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void waitForLoadingChunks() {
/*  331 */     long start = System.nanoTime();
/*      */     do {
/*  333 */       this.world.consumeTaskQueue();
/*      */       
/*  335 */       Thread.yield();
/*      */       
/*  337 */       boolean hasLoadingChunks = false;
/*  338 */       for (ObjectIterator<Long2ObjectMap.Entry<ChunkLoadState>> objectIterator = this.chunks.long2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Long2ObjectMap.Entry<ChunkLoadState> entry = objectIterator.next();
/*  339 */         ChunkLoadState chunkState = (ChunkLoadState)entry.getValue();
/*  340 */         long stamp = chunkState.lock.readLock();
/*      */         
/*  342 */         try { CompletableFuture<Ref<ChunkStore>> future = chunkState.future;
/*  343 */           if (future != null && !future.isDone())
/*  344 */           { hasLoadingChunks = true;
/*      */ 
/*      */ 
/*      */             
/*  348 */             chunkState.lock.unlockRead(stamp); break; }  } finally { chunkState.lock.unlockRead(stamp); }
/*      */          }
/*      */ 
/*      */       
/*  352 */       if (!hasLoadingChunks) {
/*      */         break;
/*      */       }
/*  355 */     } while (System.nanoTime() - start <= 5000000000L);
/*      */     
/*  357 */     this.world.consumeTaskQueue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void shutdown() {
/*  364 */     this.store.shutdown();
/*  365 */     this.chunks.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private Ref<ChunkStore> add(@Nonnull Holder<ChunkStore> holder) {
/*  379 */     this.world.debugAssertInTickingThread();
/*      */     
/*  381 */     WorldChunk worldChunkComponent = (WorldChunk)holder.getComponent(WorldChunk.getComponentType());
/*  382 */     assert worldChunkComponent != null;
/*      */     
/*  384 */     ChunkLoadState chunkState = (ChunkLoadState)this.chunks.get(worldChunkComponent.getIndex());
/*  385 */     if (chunkState == null) {
/*  386 */       throw new IllegalStateException("Expected the ChunkLoadState to exist!");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  393 */     Ref<ChunkStore> oldReference = null;
/*  394 */     long stamp = chunkState.lock.writeLock();
/*      */     try {
/*  396 */       if (chunkState.future == null) {
/*  397 */         throw new IllegalStateException("Expected the ChunkLoadState to have a future!");
/*      */       }
/*      */       
/*  400 */       if (chunkState.reference != null) {
/*  401 */         oldReference = chunkState.reference;
/*  402 */         chunkState.reference = null;
/*      */       } 
/*      */     } finally {
/*  405 */       chunkState.lock.unlockWrite(stamp);
/*      */     } 
/*      */     
/*  408 */     if (oldReference != null) {
/*  409 */       WorldChunk oldWorldChunkComponent = (WorldChunk)this.store.getComponent(oldReference, WorldChunk.getComponentType());
/*  410 */       assert oldWorldChunkComponent != null;
/*      */       
/*  412 */       oldWorldChunkComponent.setFlag(ChunkFlag.TICKING, false);
/*  413 */       this.store.removeEntity(oldReference, RemoveReason.REMOVE);
/*      */ 
/*      */       
/*  416 */       this.world.getNotificationHandler().updateChunk(worldChunkComponent.getIndex());
/*      */     } 
/*      */ 
/*      */     
/*  420 */     Ref<ChunkStore> ref = this.store.addEntity(holder, AddReason.SPAWN);
/*  421 */     if (ref == null) throw new UnsupportedOperationException("Unable to add the chunk to the world!");
/*      */     
/*  423 */     worldChunkComponent.setReference(ref);
/*      */     
/*  425 */     stamp = chunkState.lock.writeLock();
/*      */     try {
/*  427 */       chunkState.reference = ref;
/*      */ 
/*      */       
/*  430 */       chunkState.flags = 0;
/*  431 */       chunkState.future = null;
/*      */ 
/*      */       
/*  434 */       chunkState.throwable = null;
/*  435 */       chunkState.failedWhen = 0L;
/*  436 */       chunkState.failedCounter = 0;
/*      */       
/*  438 */       return ref;
/*      */     } finally {
/*  440 */       chunkState.lock.unlockWrite(stamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void remove(@Nonnull Ref<ChunkStore> reference, @Nonnull RemoveReason reason) {
/*  451 */     this.world.debugAssertInTickingThread();
/*      */     
/*  453 */     WorldChunk worldChunkComponent = (WorldChunk)this.store.getComponent(reference, WorldChunk.getComponentType());
/*  454 */     assert worldChunkComponent != null;
/*      */     
/*  456 */     long index = worldChunkComponent.getIndex();
/*  457 */     ChunkLoadState chunkState = (ChunkLoadState)this.chunks.get(index);
/*  458 */     long stamp = chunkState.lock.readLock();
/*      */     try {
/*  460 */       worldChunkComponent.setFlag(ChunkFlag.TICKING, false);
/*  461 */       this.store.removeEntity(reference, reason);
/*      */ 
/*      */       
/*  464 */       if (chunkState.future != null) {
/*  465 */         chunkState.reference = null;
/*      */       } else {
/*  467 */         this.chunks.remove(index, chunkState);
/*      */       } 
/*      */     } finally {
/*  470 */       chunkState.lock.unlockRead(stamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Ref<ChunkStore> getChunkReference(long index) {
/*  482 */     ChunkLoadState chunkState = (ChunkLoadState)this.chunks.get(index);
/*  483 */     if (chunkState == null) return null;
/*      */     
/*  485 */     long stamp = chunkState.lock.tryOptimisticRead();
/*  486 */     Ref<ChunkStore> reference = chunkState.reference;
/*  487 */     if (chunkState.lock.validate(stamp)) {
/*  488 */       return reference;
/*      */     }
/*      */     
/*  491 */     stamp = chunkState.lock.readLock();
/*      */     try {
/*  493 */       return chunkState.reference;
/*      */     } finally {
/*  495 */       chunkState.lock.unlockRead(stamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Ref<ChunkStore> getChunkSectionReference(int x, int y, int z) {
/*  510 */     Ref<ChunkStore> ref = getChunkReference(ChunkUtil.indexChunk(x, z));
/*  511 */     if (ref == null) return null;
/*      */     
/*  513 */     ChunkColumn chunkColumnComponent = (ChunkColumn)this.store.getComponent(ref, ChunkColumn.getComponentType());
/*  514 */     if (chunkColumnComponent == null) return null;
/*      */     
/*  516 */     return chunkColumnComponent.getSection(y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public Ref<ChunkStore> getChunkSectionReference(@Nonnull ComponentAccessor<ChunkStore> commandBuffer, int x, int y, int z) {
/*  531 */     Ref<ChunkStore> ref = getChunkReference(ChunkUtil.indexChunk(x, z));
/*  532 */     if (ref == null) return null;
/*      */     
/*  534 */     ChunkColumn chunkColumnComponent = (ChunkColumn)commandBuffer.getComponent(ref, ChunkColumn.getComponentType());
/*  535 */     if (chunkColumnComponent == null) return null;
/*      */     
/*  537 */     return chunkColumnComponent.getSection(y);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public CompletableFuture<Ref<ChunkStore>> getChunkSectionReferenceAsync(int x, int y, int z) {
/*  552 */     if (y < 0 || y >= 10) {
/*  553 */       return CompletableFuture.failedFuture(new IndexOutOfBoundsException("Invalid y: " + y));
/*      */     }
/*      */     
/*  556 */     return getChunkReferenceAsync(ChunkUtil.indexChunk(x, z))
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  561 */       .thenApplyAsync(ref -> {
/*      */           if (ref == null || !ref.isValid()) {
/*      */             return null;
/*      */           }
/*      */           
/*      */           Store<ChunkStore> store = ref.getStore();
/*      */           ChunkColumn chunkColumnComponent = (ChunkColumn)store.getComponent(ref, ChunkColumn.getComponentType());
/*      */           return (chunkColumnComponent == null) ? null : chunkColumnComponent.getSection(y);
/*  569 */         }(Executor)((ChunkStore)this.store.getExternalData()).getWorld());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public <T extends com.hypixel.hytale.component.Component<ChunkStore>> T getChunkComponent(long index, @Nonnull ComponentType<ChunkStore, T> componentType) {
/*  582 */     Ref<ChunkStore> reference = getChunkReference(index);
/*  583 */     return (reference == null || !reference.isValid()) ? null : (T)this.store.getComponent(reference, componentType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public CompletableFuture<Ref<ChunkStore>> getChunkReferenceAsync(long index) {
/*  597 */     return getChunkReferenceAsync(index, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public CompletableFuture<Ref<ChunkStore>> getChunkReferenceAsync(long index, int flags) {
/*      */     ChunkLoadState chunkState;
/*  610 */     if (this.store.isShutdown()) return CompletableFuture.completedFuture(null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  617 */     if ((flags & 0x3) == 3) {
/*  618 */       chunkState = (ChunkLoadState)this.chunks.get(index);
/*      */ 
/*      */       
/*  621 */       if (chunkState == null) return CompletableFuture.completedFuture(null);
/*      */ 
/*      */       
/*  624 */       long l = chunkState.lock.readLock();
/*      */ 
/*      */       
/*      */       try {
/*  628 */         if ((flags & 0x4) == 0 || (chunkState.flags & 0x4) != 0) {
/*  629 */           if (chunkState.reference != null) return CompletableFuture.completedFuture(chunkState.reference); 
/*  630 */           if (chunkState.future == null) return (CompletableFuture)CompletableFuture.completedFuture(null); 
/*  631 */           return chunkState.future;
/*      */         }
/*      */       
/*      */       } finally {
/*      */         
/*  636 */         chunkState.lock.unlockRead(l);
/*      */       } 
/*      */     } else {
/*      */       
/*  640 */       chunkState = (ChunkLoadState)this.chunks.computeIfAbsent(index, l -> new ChunkLoadState());
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  645 */     long stamp = chunkState.lock.writeLock();
/*      */ 
/*      */     
/*  648 */     if (chunkState.future == null && chunkState.reference != null && (flags & 0x8) == 0) {
/*  649 */       Ref<ChunkStore> reference = chunkState.reference;
/*      */       
/*  651 */       if ((flags & 0x4) == 0) {
/*  652 */         chunkState.lock.unlockWrite(stamp);
/*  653 */         return CompletableFuture.completedFuture(reference);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  658 */       if (this.world.isInThread() && (flags & Integer.MIN_VALUE) == 0) {
/*  659 */         chunkState.lock.unlockWrite(stamp);
/*      */ 
/*      */ 
/*      */         
/*  663 */         WorldChunk worldChunkComponent = (WorldChunk)this.store.getComponent(reference, WorldChunk.getComponentType());
/*  664 */         assert worldChunkComponent != null;
/*      */         
/*  666 */         worldChunkComponent.setFlag(ChunkFlag.TICKING, true);
/*  667 */         return CompletableFuture.completedFuture(reference);
/*      */       } 
/*      */       
/*  670 */       chunkState.lock.unlockWrite(stamp);
/*  671 */       return CompletableFuture.supplyAsync(() -> { WorldChunk worldChunkComponent = (WorldChunk)this.store.getComponent(reference, WorldChunk.getComponentType()); assert worldChunkComponent != null; worldChunkComponent.setFlag(ChunkFlag.TICKING, true); return reference; }(Executor)this.world);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/*  683 */       if (chunkState.throwable != null) {
/*  684 */         long nanosSince = System.nanoTime() - chunkState.failedWhen;
/*  685 */         int count = chunkState.failedCounter;
/*  686 */         if (nanosSince < Math.min(MAX_FAILURE_BACKOFF_NANOS, (count * count) * FAILURE_BACKOFF_NANOS)) {
/*  687 */           return (CompletableFuture)CompletableFuture.failedFuture(new RuntimeException("Chunk failure backoff", chunkState.throwable));
/*      */         }
/*      */ 
/*      */         
/*  691 */         chunkState.throwable = null;
/*  692 */         chunkState.failedWhen = 0L;
/*      */       } 
/*      */       
/*  695 */       boolean isNew = (chunkState.future == null);
/*  696 */       if (isNew) chunkState.flags = flags;
/*      */       
/*  698 */       int x = ChunkUtil.xOfChunkIndex(index);
/*  699 */       int z = ChunkUtil.zOfChunkIndex(index);
/*      */ 
/*      */ 
/*      */       
/*  703 */       if ((isNew || (chunkState.flags & 0x1) != 0) && (flags & 0x1) == 0)
/*      */       {
/*      */         
/*  706 */         if (chunkState.future == null) {
/*  707 */           chunkState
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  714 */             .future = this.loader.loadHolder(x, z).thenApplyAsync(holder -> { if (holder == null || this.store.isShutdown()) return null;  this.totalLoadedChunksCount.getAndIncrement(); return preLoadChunkAsync(index, holder, false); }).thenApplyAsync(this::postLoadChunk, (Executor)this.world).exceptionally(throwable -> {
/*      */                 ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to load chunk! %s, %s", x, z);
/*      */                 chunkState.fail(throwable);
/*      */                 throw SneakyThrow.sneakyThrow(throwable);
/*      */               });
/*      */         } else {
/*  720 */           chunkState.flags &= 0xFFFFFFFE;
/*  721 */           chunkState.future = chunkState.future.thenCompose(reference -> (reference != null) ? CompletableFuture.completedFuture(reference) : this.loader.loadHolder(x, z).thenApplyAsync(()).thenApplyAsync(this::postLoadChunk, (Executor)this.world).exceptionally(()));
/*      */         } 
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  742 */       if ((isNew || (chunkState.flags & 0x2) != 0) && (flags & 0x2) == 0) {
/*      */         
/*  744 */         int seed = (int)this.world.getWorldConfig().getSeed();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  749 */         if (chunkState.future == null) {
/*      */           CompletableFuture<GeneratedChunk> future;
/*  751 */           long readStamp = this.generatorLock.readLock();
/*      */           try {
/*  753 */             if (this.generator == null) {
/*  754 */               future = this.generatorLoaded.thenCompose(aVoid -> this.generator.generate(seed, index, x, z, ((flags & 0x10) != 0) ? this::isChunkStillNeeded : null));
/*      */             } else {
/*  756 */               future = this.generator.generate(seed, index, x, z, ((flags & 0x10) != 0) ? this::isChunkStillNeeded : null);
/*      */             } 
/*      */           } finally {
/*  759 */             this.generatorLock.unlockRead(readStamp);
/*      */           } 
/*  761 */           chunkState
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  768 */             .future = future.thenApplyAsync(generatedChunk -> { if (generatedChunk == null || this.store.isShutdown()) return null;  this.totalGeneratedChunksCount.getAndIncrement(); return preLoadChunkAsync(index, generatedChunk.toHolder(this.world), true); }).thenApplyAsync(this::postLoadChunk, (Executor)this.world).exceptionally(throwable -> {
/*      */                 ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to generate chunk! %s, %s", x, z);
/*      */                 chunkState.fail(throwable);
/*      */                 throw SneakyThrow.sneakyThrow(throwable);
/*      */               });
/*      */         } else {
/*  774 */           chunkState.flags &= 0xFFFFFFFD;
/*  775 */           chunkState.future = chunkState.future.thenCompose(reference -> {
/*      */                 CompletableFuture<GeneratedChunk> future;
/*      */ 
/*      */ 
/*      */                 
/*      */                 if (reference != null) {
/*      */                   return CompletableFuture.completedFuture(reference);
/*      */                 }
/*      */ 
/*      */ 
/*      */                 
/*      */                 long readStamp = this.generatorLock.readLock();
/*      */ 
/*      */                 
/*      */                 try {
/*      */                   if (this.generator == null) {
/*      */                     future = this.generatorLoaded.thenCompose(());
/*      */                   } else {
/*      */                     future = this.generator.generate(seed, index, x, z, null);
/*      */                   } 
/*      */                 } finally {
/*      */                   this.generatorLock.unlockRead(readStamp);
/*      */                 } 
/*      */ 
/*      */                 
/*      */                 return future.thenApplyAsync(()).thenApplyAsync(this::postLoadChunk, (Executor)this.world).exceptionally(());
/*      */               });
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  806 */       if ((isNew || (chunkState.flags & 0x4) == 0) && (flags & 0x4) != 0) {
/*  807 */         chunkState.flags |= 0x4;
/*      */         
/*  809 */         if (chunkState.future != null) {
/*  810 */           chunkState
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  820 */             .future = chunkState.future.<Ref<ChunkStore>>thenApplyAsync(reference -> { if (reference != null) { WorldChunk worldChunkComponent = (WorldChunk)this.store.getComponent(reference, WorldChunk.getComponentType()); assert worldChunkComponent != null; worldChunkComponent.setFlag(ChunkFlag.TICKING, true); }  return reference; }(Executor)this.world).exceptionally(throwable -> {
/*      */                 ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to set chunk ticking! %s, %s", x, z);
/*      */                 
/*      */                 chunkState.fail(throwable);
/*      */                 throw SneakyThrow.sneakyThrow(throwable);
/*      */               });
/*      */         }
/*      */       } 
/*  828 */       if (chunkState.future == null) {
/*  829 */         return (CompletableFuture)CompletableFuture.completedFuture(null);
/*      */       }
/*  831 */       return chunkState.future;
/*      */     } finally {
/*  833 */       chunkState.lock.unlockWrite(stamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isChunkStillNeeded(long index) {
/*  844 */     for (PlayerRef playerRef : this.world.getPlayerRefs()) {
/*  845 */       if (playerRef.getChunkTracker().shouldBeVisible(index)) {
/*  846 */         return true;
/*      */       }
/*      */     } 
/*  849 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isChunkOnBackoff(long index, long maxFailureBackoffNanos) {
/*  860 */     ChunkLoadState chunkState = (ChunkLoadState)this.chunks.get(index);
/*  861 */     if (chunkState == null) return false;
/*      */     
/*  863 */     long stamp = chunkState.lock.readLock();
/*      */     try {
/*  865 */       if (chunkState.throwable == null) return false;
/*      */       
/*  867 */       long nanosSince = System.nanoTime() - chunkState.failedWhen;
/*  868 */       int count = chunkState.failedCounter;
/*  869 */       return (nanosSince < Math.min(maxFailureBackoffNanos, (count * count) * FAILURE_BACKOFF_NANOS));
/*      */     } finally {
/*  871 */       chunkState.lock.unlockRead(stamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private Holder<ChunkStore> preLoadChunkAsync(long index, @Nonnull Holder<ChunkStore> holder, boolean newlyGenerated) {
/*  885 */     WorldChunk worldChunkComponent = (WorldChunk)holder.getComponent(WorldChunk.getComponentType());
/*      */     
/*  887 */     if (worldChunkComponent == null) {
/*  888 */       throw new IllegalStateException(String.format("Holder missing WorldChunk component! (%d, %d)", new Object[] {
/*  889 */               Integer.valueOf(ChunkUtil.xOfChunkIndex(index)), Integer.valueOf(ChunkUtil.zOfChunkIndex(index))
/*      */             }));
/*      */     }
/*  892 */     if (worldChunkComponent.getIndex() != index) {
/*  893 */       throw new IllegalStateException(String.format("Incorrect chunk index! Got (%d, %d) expected (%d, %d)", new Object[] {
/*  894 */               Integer.valueOf(worldChunkComponent.getX()), Integer.valueOf(worldChunkComponent.getZ()), 
/*  895 */               Integer.valueOf(ChunkUtil.xOfChunkIndex(index)), Integer.valueOf(ChunkUtil.zOfChunkIndex(index))
/*      */             }));
/*      */     }
/*  898 */     BlockChunk blockChunk = (BlockChunk)holder.getComponent(BlockChunk.getComponentType());
/*  899 */     if (blockChunk == null) {
/*  900 */       throw new IllegalStateException(String.format("Holder missing BlockChunk component! (%d, %d)", new Object[] {
/*  901 */               Integer.valueOf(ChunkUtil.xOfChunkIndex(index)), Integer.valueOf(ChunkUtil.zOfChunkIndex(index))
/*      */             }));
/*      */     }
/*  904 */     blockChunk.loadFromHolder(holder);
/*      */     
/*  906 */     worldChunkComponent.setFlag(ChunkFlag.NEWLY_GENERATED, newlyGenerated);
/*  907 */     worldChunkComponent.setLightingUpdatesEnabled(false);
/*      */     
/*  909 */     if (newlyGenerated && this.world.getWorldConfig().shouldSaveNewChunks()) {
/*  910 */       worldChunkComponent.markNeedsSaving();
/*      */     }
/*      */     
/*      */     try {
/*  914 */       long start = System.nanoTime();
/*      */       
/*  916 */       IEventDispatcher<ChunkPreLoadProcessEvent, ChunkPreLoadProcessEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(ChunkPreLoadProcessEvent.class, this.world.getName());
/*  917 */       if (dispatcher.hasListener()) {
/*  918 */         ChunkPreLoadProcessEvent event = (ChunkPreLoadProcessEvent)dispatcher.dispatch((IBaseEvent)new ChunkPreLoadProcessEvent(holder, worldChunkComponent, newlyGenerated, start));
/*  919 */         if (!event.didLog()) {
/*  920 */           long end = System.nanoTime();
/*  921 */           long diff = end - start;
/*  922 */           if (diff > this.world.getTickStepNanos()) {
/*  923 */             LOGGER.at(Level.SEVERE).log("Took too long to pre-load process chunk: %s > TICK_STEP, Has GC Run: %s, %s", FormatUtil.nanosToString(diff), Boolean.valueOf(this.world.consumeGCHasRun()), worldChunkComponent);
/*      */           }
/*      */         } 
/*      */       } 
/*      */     } finally {
/*  928 */       worldChunkComponent.setLightingUpdatesEnabled(true);
/*      */     } 
/*      */     
/*  931 */     return holder;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private Ref<ChunkStore> postLoadChunk(@Nullable Holder<ChunkStore> holder) {
/*  942 */     this.world.debugAssertInTickingThread();
/*  943 */     if (holder == null || this.store.isShutdown()) return null;
/*      */     
/*  945 */     long start = System.nanoTime();
/*      */     
/*  947 */     WorldChunk worldChunkComponent = (WorldChunk)holder.getComponent(WorldChunk.getComponentType());
/*  948 */     assert worldChunkComponent != null;
/*      */     
/*  950 */     worldChunkComponent.setFlag(ChunkFlag.START_INIT, true);
/*      */ 
/*      */     
/*  953 */     if (worldChunkComponent.is(ChunkFlag.TICKING)) {
/*  954 */       holder.tryRemoveComponent(REGISTRY.getNonTickingComponentType());
/*      */     } else {
/*  956 */       holder.ensureComponent(REGISTRY.getNonTickingComponentType());
/*      */     } 
/*      */     
/*  959 */     Ref<ChunkStore> reference = add(holder);
/*      */     
/*  961 */     worldChunkComponent.initFlags();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  966 */     this.world.getChunkLighting().init(worldChunkComponent);
/*      */     
/*  968 */     long end = System.nanoTime();
/*  969 */     long diff = end - start;
/*  970 */     if (diff > this.world.getTickStepNanos()) {
/*  971 */       LOGGER.at(Level.SEVERE).log("Took too long to post-load process chunk: %s > TICK_STEP, Has GC Run: %s, %s", FormatUtil.nanosToString(diff), Boolean.valueOf(this.world.consumeGCHasRun()), worldChunkComponent);
/*      */     }
/*      */     
/*  974 */     return reference;
/*      */   }
/*      */   
/*      */   private static class ChunkLoadState {
/*  978 */     private final StampedLock lock = new StampedLock();
/*      */     
/*  980 */     private int flags = 0;
/*      */     
/*      */     @Nullable
/*      */     private CompletableFuture<Ref<ChunkStore>> future;
/*      */     @Nullable
/*      */     private Ref<ChunkStore> reference;
/*      */     @Nullable
/*      */     private Throwable throwable;
/*      */     private long failedWhen;
/*      */     private int failedCounter;
/*      */     
/*      */     private void fail(Throwable throwable) {
/*  992 */       long stamp = this.lock.writeLock();
/*      */       try {
/*  994 */         this.flags = 0;
/*  995 */         this.future = null;
/*      */ 
/*      */         
/*  998 */         this.throwable = throwable;
/*  999 */         this.failedWhen = System.nanoTime();
/* 1000 */         this.failedCounter++;
/*      */       } finally {
/* 1002 */         this.lock.unlockWrite(stamp);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class LoadPacketDataQuerySystem
/*      */     extends EntityDataSystem<ChunkStore, PlayerRef, Packet> {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class LoadFuturePacketDataQuerySystem
/*      */     extends EntityDataSystem<ChunkStore, PlayerRef, CompletableFuture<Packet>> {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class UnloadPacketDataQuerySystem
/*      */     extends EntityDataSystem<ChunkStore, PlayerRef, Packet> {}
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ChunkLoaderSaverSetupSystem
/*      */     extends StoreSystem<ChunkStore>
/*      */   {
/*      */     @Nullable
/*      */     public SystemGroup<ChunkStore> getGroup() {
/* 1033 */       return ChunkStore.INIT_GROUP;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {
/* 1043 */       ChunkStore data = (ChunkStore)store.getExternalData();
/* 1044 */       World world = data.getWorld();
/* 1045 */       IChunkStorageProvider chunkStorageProvider = world.getWorldConfig().getChunkStorageProvider();
/*      */       try {
/* 1047 */         data.loader = chunkStorageProvider.getLoader(store);
/* 1048 */         data.saver = chunkStorageProvider.getSaver(store);
/* 1049 */       } catch (IOException e) {
/* 1050 */         throw SneakyThrow.sneakyThrow(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {
/* 1056 */       ChunkStore data = (ChunkStore)store.getExternalData();
/*      */       try {
/* 1058 */         if (data.loader != null) {
/* 1059 */           IChunkLoader oldLoader = data.loader;
/* 1060 */           data.loader = null;
/* 1061 */           oldLoader.close();
/*      */         } 
/* 1063 */         if (data.saver != null) {
/* 1064 */           IChunkSaver oldSaver = data.saver;
/* 1065 */           data.saver = null;
/* 1066 */           oldSaver.close();
/*      */         } 
/* 1068 */       } catch (IOException e) {
/* 1069 */         ((HytaleLogger.Api)ChunkStore.LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to close storage!");
/*      */       } 
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\ChunkStore.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */