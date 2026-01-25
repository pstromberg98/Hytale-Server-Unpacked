/*    */ package com.hypixel.hytale.server.worldgen;
/*    */ 
/*    */ import com.hypixel.hytale.procedurallib.condition.IIntCondition;
/*    */ import com.hypixel.hytale.procedurallib.json.SeedResource;
/*    */ import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
/*    */ import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
/*    */ import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabLoader;
/*    */ import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskRegistry;
/*    */ import com.hypixel.hytale.server.worldgen.loader.util.FileMaskCache;
/*    */ import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
/*    */ import com.hypixel.hytale.server.worldgen.util.LogUtil;
/*    */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*    */ import java.nio.file.Path;
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ import java.util.logging.Level;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SeedStringResource
/*    */   implements SeedResource
/*    */ {
/*    */   @Nonnull
/*    */   protected final FileMaskCache<IIntCondition> biomeMaskRegistry;
/*    */   @Nonnull
/*    */   protected final BlockPlacementMaskRegistry blockMaskRegistry;
/*    */   @Nonnull
/*    */   protected Path dataFolder;
/*    */   @Nonnull
/*    */   protected WorldGenPrefabLoader loader;
/*    */   @Nonnull
/*    */   protected final Map<String, AtomicInteger> uniqueIds;
/*    */   
/*    */   public SeedStringResource(@Nonnull PrefabStoreRoot prefabStore, @Nonnull Path dataFolder) {
/* 37 */     this.dataFolder = dataFolder;
/* 38 */     this.loader = new WorldGenPrefabLoader(prefabStore, dataFolder);
/* 39 */     this.biomeMaskRegistry = new FileMaskCache();
/* 40 */     this.blockMaskRegistry = new BlockPlacementMaskRegistry();
/* 41 */     this.uniqueIds = (Map<String, AtomicInteger>)new Object2ObjectOpenHashMap();
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public String getUniqueName(@Nonnull String prefix) {
/* 46 */     return prefix + prefix;
/*    */   }
/*    */ 
/*    */   
/*    */   public WorldGenPrefabLoader getLoader() {
/* 51 */     return this.loader;
/*    */   }
/*    */   
/*    */   public void setPrefabStore(@Nonnull PrefabStoreRoot prefabStore) {
/* 55 */     if (prefabStore != this.loader.getStore()) {
/* 56 */       LogUtil.getLogger().at(Level.INFO).log("Set prefab-store to: %s", prefabStore.name());
/* 57 */       this.loader = new WorldGenPrefabLoader(prefabStore, this.dataFolder);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void setDataFolder(@Nonnull Path dataFolder) {
/* 62 */     if (!dataFolder.equals(this.dataFolder)) {
/* 63 */       LogUtil.getLogger().at(Level.INFO).log("Set data-folder to: %s", dataFolder);
/* 64 */       this.dataFolder = dataFolder;
/* 65 */       this.loader = new WorldGenPrefabLoader(this.loader.getStore(), dataFolder);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ResultBuffer.Bounds2d localBounds2d() {
/* 72 */     return (ChunkGenerator.getResource()).bounds2d;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ResultBuffer.ResultBuffer2d localBuffer2d() {
/* 78 */     return (ChunkGenerator.getResource()).resultBuffer2d;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ResultBuffer.ResultBuffer3d localBuffer3d() {
/* 84 */     return (ChunkGenerator.getResource()).resultBuffer3d;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeSeedReport(String seedReport) {
/* 89 */     LogUtil.getLogger().at(Level.FINE).log(seedReport);
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public FileMaskCache<IIntCondition> getBiomeMaskRegistry() {
/* 94 */     return this.biomeMaskRegistry;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public BlockPlacementMaskRegistry getBlockMaskRegistry() {
/* 99 */     return this.blockMaskRegistry;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\SeedStringResource.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */