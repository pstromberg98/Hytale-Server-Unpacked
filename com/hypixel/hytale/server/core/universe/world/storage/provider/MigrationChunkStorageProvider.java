/*     */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import it.unimi.dsi.fastutil.longs.LongCollection;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
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
/*     */ public class MigrationChunkStorageProvider
/*     */   implements IChunkStorageProvider
/*     */ {
/*     */   public static final String ID = "Migration";
/*     */   @Nonnull
/*     */   public static final BuilderCodec<MigrationChunkStorageProvider> CODEC;
/*     */   private IChunkStorageProvider[] from;
/*     */   private IChunkStorageProvider to;
/*     */   
/*     */   static {
/*  54 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MigrationChunkStorageProvider.class, MigrationChunkStorageProvider::new).documentation("A provider that combines multiple storage providers in a chain to assist with migrating worlds between storage formats.\n\nCan also be used to set storage to load chunks but block saving them if combined with the **Empty** storage provider")).append(new KeyedCodec("Loaders", (Codec)new ArrayCodec((Codec)IChunkStorageProvider.CODEC, x$0 -> new IChunkStorageProvider[x$0])), (migration, o) -> migration.from = o, migration -> migration.from).documentation("A list of storage providers to use as chunk loaders.\n\nEach loader will be tried in order to load a chunk, returning the chunk if found otherwise trying the next loaded until found or none are left.").add()).append(new KeyedCodec("Saver", (Codec)IChunkStorageProvider.CODEC), (migration, o) -> migration.to = o, migration -> migration.to).documentation("The storage provider to use to save chunks.").add()).build();
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
/*     */ 
/*     */   
/*     */   public MigrationChunkStorageProvider() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MigrationChunkStorageProvider(@Nonnull IChunkStorageProvider[] from, @Nonnull IChunkStorageProvider to) {
/*  81 */     this.from = from;
/*  82 */     this.to = to;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) throws IOException {
/*  88 */     IChunkLoader[] loaders = new IChunkLoader[this.from.length];
/*  89 */     for (int i = 0; i < this.from.length; i++) {
/*  90 */       loaders[i] = this.from[i].getLoader(store);
/*     */     }
/*  92 */     return new MigrationChunkLoader(loaders);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) throws IOException {
/*  97 */     return this.to.getSaver(store);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 104 */     return "MigrationChunkStorageProvider{from=" + Arrays.toString((Object[])this.from) + ", to=" + String.valueOf(this.to) + "}";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class MigrationChunkLoader
/*     */     implements IChunkLoader
/*     */   {
/*     */     @Nonnull
/*     */     private final IChunkLoader[] loaders;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public MigrationChunkLoader(@Nonnull IChunkLoader... loaders) {
/* 126 */       this.loaders = loaders;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 131 */       IOException exception = null;
/* 132 */       for (IChunkLoader loader : this.loaders) {
/*     */         try {
/* 134 */           loader.close();
/* 135 */         } catch (Exception e) {
/* 136 */           if (exception == null) exception = new IOException("Failed to close one or more loaders!"); 
/* 137 */           exception.addSuppressed(e);
/*     */         } 
/*     */       } 
/* 140 */       if (exception != null) throw exception;
/*     */     
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     public CompletableFuture<Holder<ChunkStore>> loadHolder(int x, int z) {
/* 146 */       CompletableFuture<Holder<ChunkStore>> future = this.loaders[0].loadHolder(x, z);
/* 147 */       for (int i = 1; i < this.loaders.length; i++) {
/*     */         
/* 149 */         IChunkLoader loader = this.loaders[i];
/* 150 */         CompletableFuture<Holder<ChunkStore>> previous = future;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 166 */         future = previous.handle((worldChunk, throwable) -> (throwable != null) ? loader.loadHolder(x, z).exceptionally(()) : ((worldChunk == null) ? loader.loadHolder(x, z) : previous)).thenCompose(Function.identity());
/*     */       } 
/* 168 */       return future;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public LongSet getIndexes() throws IOException {
/* 174 */       LongOpenHashSet indexes = new LongOpenHashSet();
/* 175 */       for (IChunkLoader loader : this.loaders) {
/* 176 */         indexes.addAll((LongCollection)loader.getIndexes());
/*     */       }
/* 178 */       return (LongSet)indexes;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\MigrationChunkStorageProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */