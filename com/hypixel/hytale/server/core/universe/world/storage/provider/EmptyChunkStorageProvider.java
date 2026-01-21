/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSets;
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
/*     */ public class EmptyChunkStorageProvider
/*     */   implements IChunkStorageProvider
/*     */ {
/*     */   public static final String ID = "Empty";
/*     */   @Nonnull
/*  29 */   public static final EmptyChunkStorageProvider INSTANCE = new EmptyChunkStorageProvider();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  35 */   public static final BuilderCodec<EmptyChunkStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EmptyChunkStorageProvider.class, () -> INSTANCE)
/*  36 */     .documentation("A chunk storage provider that discards any chunks to save and will always fail to find chunks."))
/*  37 */     .build();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  43 */   public static final EmptyChunkLoader EMPTY_CHUNK_LOADER = new EmptyChunkLoader();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  49 */   public static final EmptyChunkSaver EMPTY_CHUNK_SAVER = new EmptyChunkSaver();
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) {
/*  54 */     return EMPTY_CHUNK_LOADER;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) {
/*  59 */     return EMPTY_CHUNK_SAVER;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/*  65 */     return "EmptyChunkStorageProvider{}";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class EmptyChunkLoader
/*     */     implements IChunkLoader
/*     */   {
/*     */     public void close() {}
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Holder<ChunkStore>> loadHolder(int x, int z) {
/*  79 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() {
/*  85 */       return (LongSet)LongSets.EMPTY_SET;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class EmptyChunkSaver
/*     */     implements IChunkSaver
/*     */   {
/*     */     public void close() {}
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Void> saveHolder(int x, int z, @Nonnull Holder<ChunkStore> holder) {
/* 100 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Void> removeHolder(int x, int z) {
/* 106 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() {
/* 112 */       return (LongSet)LongSets.EMPTY_SET;
/*     */     }
/*     */     
/*     */     public void flush() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\EmptyChunkStorageProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */