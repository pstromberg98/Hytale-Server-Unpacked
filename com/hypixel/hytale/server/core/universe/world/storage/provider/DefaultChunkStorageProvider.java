/*    */ package com.hypixel.hytale.server.core.universe.world.storage.provider;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
/*    */ import java.io.IOException;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DefaultChunkStorageProvider
/*    */   implements IChunkStorageProvider
/*    */ {
/*    */   @Nonnull
/* 21 */   public static final DefaultChunkStorageProvider INSTANCE = new DefaultChunkStorageProvider();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static final String ID = "Hytale";
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 32 */   public static final BuilderCodec<DefaultChunkStorageProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(DefaultChunkStorageProvider.class, () -> INSTANCE)
/* 33 */     .documentation("Selects the default recommended storage as decided by the server."))
/* 34 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/* 40 */   public static final IChunkStorageProvider DEFAULT = new IndexedStorageChunkStorageProvider();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public IChunkLoader getLoader(@Nonnull Store<ChunkStore> store) throws IOException {
/* 45 */     return DEFAULT.getLoader(store);
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public IChunkSaver getSaver(@Nonnull Store<ChunkStore> store) throws IOException {
/* 50 */     return DEFAULT.getSaver(store);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 56 */     return "DefaultChunkStorageProvider{DEFAULT=" + String.valueOf(DEFAULT) + "}";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\storage\provider\DefaultChunkStorageProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */