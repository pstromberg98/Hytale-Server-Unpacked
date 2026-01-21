/*     */ package com.hypixel.hytale.server.core.universe.world.storage.component;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Resource;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.dependency.Order;
/*     */ import com.hypixel.hytale.component.dependency.RootDependency;
/*     */ import com.hypixel.hytale.component.dependency.SystemDependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.EcsEvent;
/*     */ import com.hypixel.hytale.component.system.StoreSystem;
/*     */ import com.hypixel.hytale.component.system.tick.RunWhenPausedSystem;
/*     */ import com.hypixel.hytale.component.system.tick.TickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkFlag;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.events.ecs.ChunkSaveEvent;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.Deque;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ForkJoinPool;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class ChunkSavingSystems {
/*     */   @Nonnull
/*  42 */   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  48 */   private static final ComponentType<ChunkStore, WorldChunk> WORLD_CHUNK_COMPONENT_TYPE = WorldChunk.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  54 */   public static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] { (Query)WORLD_CHUNK_COMPONENT_TYPE, (Query)Query.not((Query)ChunkStore.REGISTRY.getNonSerializedComponentType()) });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class WorldRemoved
/*     */     extends StoreSystem<ChunkStore>
/*     */   {
/*     */     @Nonnull
/*  65 */     private final Set<Dependency<ChunkStore>> dependencies = (Set)Set.of(new SystemDependency(Order.AFTER, ChunkStore.ChunkLoaderSaverSetupSystem.class));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<ChunkStore>> getDependencies() {
/*  74 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onSystemAddedToStore(@Nonnull Store<ChunkStore> store) {}
/*     */ 
/*     */     
/*     */     public void onSystemRemovedFromStore(@Nonnull Store<ChunkStore> store) {
/*  83 */       World world = ((ChunkStore)store.getExternalData()).getWorld();
/*     */       
/*  85 */       world.getLogger().at(Level.INFO).log("Shutting down chunk generator...");
/*  86 */       world.getChunkStore().shutdownGenerator();
/*     */       
/*  88 */       if (!world.getWorldConfig().canSaveChunks()) {
/*  89 */         world.getLogger().at(Level.INFO).log("This world has opted to disable chunk saving so it will not be saved on shutdown");
/*     */         
/*     */         return;
/*     */       } 
/*  93 */       world.getLogger().at(Level.INFO).log("Saving Chunks...");
/*  94 */       ChunkSavingSystems.Data data = (ChunkSavingSystems.Data)store.getResource(ChunkStore.SAVE_RESOURCE);
/*     */       
/*  96 */       data.savedCount.set(0);
/*  97 */       data.toSaveTotal.set(0);
/*     */       
/*  99 */       ChunkSavingSystems.saveChunksInWorld(store).join();
/* 100 */       world.getLogger().at(Level.INFO).log("Done Saving Chunks!");
/*     */     }
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static CompletableFuture<Void> saveChunksInWorld(@Nonnull Store<ChunkStore> store) {
/* 106 */     HytaleLogger logger = ((ChunkStore)store.getExternalData()).getWorld().getLogger();
/* 107 */     Data data = (Data)store.getResource(ChunkStore.SAVE_RESOURCE);
/*     */ 
/*     */     
/* 110 */     logger.at(Level.INFO).log("Queuing Chunks...");
/* 111 */     store.forEachChunk(QUERY, (archetypeChunk, b) -> {
/*     */           for (int index = 0; index < archetypeChunk.size(); index++) {
/*     */             tryQueue(index, archetypeChunk, b.getStore());
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 118 */     logger.at(Level.INFO).log("Saving All Chunks...");
/*     */     Ref<ChunkStore> reference;
/* 120 */     while ((reference = data.poll()) != null) {
/* 121 */       saveChunk(reference, data, true, store);
/*     */     }
/*     */ 
/*     */     
/* 125 */     logger.at(Level.INFO).log("Waiting for Saving Chunks...");
/* 126 */     return data.waitForSavingChunks();
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Ticking
/*     */     extends TickingSystem<ChunkStore>
/*     */     implements RunWhenPausedSystem<ChunkStore>
/*     */   {
/*     */     @Nonnull
/*     */     public Set<Dependency<ChunkStore>> getDependencies() {
/* 136 */       return RootDependency.lastSet();
/*     */     }
/*     */ 
/*     */     
/*     */     public void tick(float dt, int systemIndex, @Nonnull Store<ChunkStore> store) {
/* 141 */       ChunkSavingSystems.Data data = (ChunkSavingSystems.Data)store.getResource(ChunkStore.SAVE_RESOURCE);
/* 142 */       if (!data.isSaving || !((ChunkStore)store.getExternalData()).getWorld().getWorldConfig().canSaveChunks()) {
/*     */         return;
/*     */       }
/* 145 */       data.chunkSavingFutures.removeIf(CompletableFuture::isDone);
/*     */ 
/*     */       
/* 148 */       if (data.checkTimer(dt))
/*     */       {
/* 150 */         store.forEachChunk(ChunkSavingSystems.QUERY, ChunkSavingSystems::tryQueueSync);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 156 */       World world = ((ChunkStore)store.getExternalData()).getWorld();
/* 157 */       IChunkSaver saver = ((ChunkStore)store.getExternalData()).getSaver();
/*     */       
/* 159 */       int parallelSaves = ForkJoinPool.commonPool().getParallelism();
/* 160 */       for (int i = 0; i < parallelSaves; i++) {
/* 161 */         Ref<ChunkStore> reference = data.poll();
/* 162 */         if (reference == null)
/*     */           break; 
/* 164 */         if (!reference.isValid()) {
/* 165 */           ChunkSavingSystems.LOGGER.at(Level.FINEST).log("Chunk reference in queue is for a chunk that has been removed!");
/*     */           
/*     */           return;
/*     */         } 
/* 169 */         WorldChunk chunk = (WorldChunk)store.getComponent(reference, ChunkSavingSystems.WORLD_CHUNK_COMPONENT_TYPE);
/* 170 */         chunk.setSaving(true);
/*     */         
/* 172 */         Holder<ChunkStore> holder = store.copySerializableEntity(reference);
/*     */         
/* 174 */         data.toSaveTotal.getAndIncrement();
/* 175 */         data.chunkSavingFutures.add(CompletableFuture.supplyAsync(() -> saver.saveHolder(chunk.getX(), chunk.getZ(), holder))
/* 176 */             .thenCompose(Function.identity())
/* 177 */             .whenCompleteAsync((aVoid, throwable) -> {
/*     */                 if (throwable != null) {
/*     */                   ((HytaleLogger.Api)ChunkSavingSystems.LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to save chunk (%d, %d):", chunk.getX(), chunk.getZ());
/*     */                 } else {
/*     */                   chunk.setFlag(ChunkFlag.ON_DISK, true);
/*     */                   ChunkSavingSystems.LOGGER.at(Level.FINEST).log("Finished saving chunk (%d, %d)", chunk.getX(), chunk.getZ());
/*     */                 } 
/*     */                 chunk.consumeNeedsSaving();
/*     */                 chunk.setSaving(false);
/*     */               }(Executor)world));
/*     */       } 
/*     */     }
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
/*     */   
/*     */   public static void tryQueue(int index, @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store) {
/* 201 */     WorldChunk worldChunkComponent = (WorldChunk)archetypeChunk.getComponent(index, WORLD_CHUNK_COMPONENT_TYPE);
/* 202 */     assert worldChunkComponent != null;
/*     */     
/* 204 */     if (!worldChunkComponent.getNeedsSaving() || worldChunkComponent.isSaving())
/*     */       return; 
/* 206 */     Ref<ChunkStore> chunkRef = archetypeChunk.getReferenceTo(index);
/* 207 */     ChunkSaveEvent event = new ChunkSaveEvent(worldChunkComponent);
/* 208 */     store.invoke(chunkRef, (EcsEvent)event);
/* 209 */     if (event.isCancelled())
/*     */       return; 
/* 211 */     ((Data)store.getResource(ChunkStore.SAVE_RESOURCE)).push(chunkRef);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void tryQueueSync(@Nonnull ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 221 */     Store<ChunkStore> store = commandBuffer.getStore();
/* 222 */     Data data = (Data)store.getResource(ChunkStore.SAVE_RESOURCE);
/*     */     
/* 224 */     for (int index = 0; index < archetypeChunk.size(); index++) {
/* 225 */       WorldChunk worldChunkComponent = (WorldChunk)archetypeChunk.getComponent(index, WORLD_CHUNK_COMPONENT_TYPE);
/* 226 */       assert worldChunkComponent != null;
/*     */       
/* 228 */       if (worldChunkComponent.getNeedsSaving() && !worldChunkComponent.isSaving()) {
/*     */ 
/*     */ 
/*     */         
/* 232 */         Ref<ChunkStore> chunkRef = archetypeChunk.getReferenceTo(index);
/* 233 */         ChunkSaveEvent event = new ChunkSaveEvent(worldChunkComponent);
/* 234 */         store.invoke(chunkRef, (EcsEvent)event);
/* 235 */         if (!event.isCancelled())
/*     */         {
/* 237 */           data.push(chunkRef);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void saveChunk(@Nonnull Ref<ChunkStore> reference, @Nonnull Data data, boolean report, @Nonnull Store<ChunkStore> store) {
/* 250 */     if (!reference.isValid()) {
/* 251 */       LOGGER.at(Level.FINEST).log("Chunk reference in queue is for a chunk that has been removed!");
/*     */       
/*     */       return;
/*     */     } 
/* 255 */     data.toSaveTotal.getAndIncrement();
/*     */     
/* 257 */     WorldChunk worldChunkComponent = (WorldChunk)store.getComponent(reference, WORLD_CHUNK_COMPONENT_TYPE);
/* 258 */     assert worldChunkComponent != null;
/*     */     
/* 260 */     Holder<ChunkStore> holder = worldChunkComponent.toHolder();
/*     */     
/* 262 */     ChunkStore chunkStore = (ChunkStore)store.getExternalData();
/* 263 */     World world = chunkStore.getWorld();
/* 264 */     IChunkSaver saver = chunkStore.getSaver();
/*     */ 
/*     */     
/* 267 */     CompletableFuture<Void> future = saver.saveHolder(worldChunkComponent.getX(), worldChunkComponent.getZ(), holder).whenComplete((aVoid, throwable) -> {
/*     */           if (throwable != null) {
/*     */             ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to save chunk (%d, %d):", worldChunkComponent.getX(), worldChunkComponent.getZ());
/*     */           } else {
/*     */             worldChunkComponent.setFlag(ChunkFlag.ON_DISK, true);
/*     */             
/*     */             LOGGER.at(Level.FINEST).log("Finished saving chunk (%d, %d)", worldChunkComponent.getX(), worldChunkComponent.getZ());
/*     */           } 
/*     */         });
/* 276 */     data.chunkSavingFutures.add(future);
/*     */ 
/*     */     
/* 279 */     if (report) {
/* 280 */       future.thenRunAsync(() -> HytaleServer.get().reportSaveProgress(world, data.savedCount.incrementAndGet(), data.toSaveTotal.get() + data.queue.size()));
/*     */     }
/*     */ 
/*     */     
/* 284 */     worldChunkComponent.consumeNeedsSaving();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Data
/*     */     implements Resource<ChunkStore>
/*     */   {
/*     */     public static final float QUEUE_UPDATE_INTERVAL = 0.5F;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 301 */     private final Set<Ref<ChunkStore>> set = ConcurrentHashMap.newKeySet();
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 306 */     private final Deque<Ref<ChunkStore>> queue = new ConcurrentLinkedDeque<>();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 312 */     private final List<CompletableFuture<Void>> chunkSavingFutures = (List<CompletableFuture<Void>>)new ObjectArrayList();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private float time;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isSaving = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 331 */     private final AtomicInteger savedCount = new AtomicInteger();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/* 339 */     private final AtomicInteger toSaveTotal = new AtomicInteger();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Data() {
/* 346 */       this.time = 0.5F;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Data(float time) {
/* 355 */       this.time = time;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Resource<ChunkStore> clone() {
/* 361 */       return new Data(this.time);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void clearSaveQueue() {
/* 368 */       this.queue.clear();
/* 369 */       this.set.clear();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void push(@Nonnull Ref<ChunkStore> reference) {
/* 378 */       if (this.set.add(reference)) {
/* 379 */         this.queue.push(reference);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Ref<ChunkStore> poll() {
/* 390 */       Ref<ChunkStore> reference = this.queue.poll();
/* 391 */       if (reference == null) return null; 
/* 392 */       this.set.remove(reference);
/* 393 */       return reference;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean checkTimer(float dt) {
/* 403 */       this.time -= dt;
/* 404 */       if (this.time <= 0.0F) {
/* 405 */         this.time += 0.5F;
/* 406 */         return true;
/*     */       } 
/* 408 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Void> waitForSavingChunks() {
/* 416 */       return CompletableFuture.allOf((CompletableFuture<?>[])this.chunkSavingFutures.toArray(x$0 -> new CompletableFuture[x$0]));
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\component\ChunkSavingSystems.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */