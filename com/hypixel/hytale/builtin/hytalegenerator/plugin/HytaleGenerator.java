/*     */ package com.hypixel.hytale.builtin.hytalegenerator.plugin;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.PropField;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.AssetManager;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.SettingsAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures.WorldStructureAsset;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.biomemap.BiomeMap;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkGenerator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkRequest;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.FallbackGenerator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.NStagedChunkGenerator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NBuffer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NCountedPixelBuffer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NSimplePixelBuffer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NVoxelBuffer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NBufferType;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.type.NParametrizedBufferType;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NBiomeDistanceStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NBiomeStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NEnvironmentStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NPropStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NTerrainStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.stages.NTintStage;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Semaphore;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class HytaleGenerator extends JavaPlugin {
/*     */   private AssetManager assetManager;
/*  50 */   private final Map<ChunkRequest.GeneratorProfile, ChunkGenerator> generators = new HashMap<>(); private Runnable assetReloadListener;
/*  51 */   private final Semaphore chunkGenerationSemaphore = new Semaphore(1);
/*     */   
/*     */   private int concurrency;
/*     */   
/*     */   private ExecutorService mainExecutor;
/*     */   private ThreadPoolExecutor concurrentExecutor;
/*     */   
/*     */   protected void start() {
/*  59 */     super.start();
/*     */     
/*  61 */     if (this.mainExecutor == null) {
/*  62 */       loadExecutors(this.assetManager.getSettingsAsset());
/*     */     }
/*  64 */     if (this.assetReloadListener == null) {
/*  65 */       this.assetReloadListener = (() -> reloadGenerators());
/*     */ 
/*     */       
/*  68 */       this.assetManager.registerReloadListener(this.assetReloadListener);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CompletableFuture<GeneratedChunk> submitChunkRequest(@Nonnull ChunkRequest request) {
/*  74 */     return CompletableFuture.supplyAsync(() -> { try { this.chunkGenerationSemaphore.acquireUninterruptibly(); ChunkGenerator generator = getGenerator(request.generatorProfile()); return generator.generate(request.arguments()); } finally { this.chunkGenerationSemaphore.release(); }  }this.mainExecutor)
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  87 */       .handle((r, e) -> {
/*     */           if (e == null) {
/*     */             return r;
/*     */           }
/*     */           LoggerUtil.logException("generation of the chunk with request " + String.valueOf(request), e, LoggerUtil.getLogger());
/*     */           return FallbackGenerator.INSTANCE.generate(request.arguments());
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  98 */     this.assetManager = new AssetManager(getEventRegistry(), getLogger());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     BuilderCodec<HandleProvider> generatorProvider = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HandleProvider.class, () -> new HandleProvider(this)).documentation("The standard generator for Hytale.")).append(new KeyedCodec("WorldStructure", (Codec)Codec.STRING), HandleProvider::setWorldStructureName, HandleProvider::getWorldStructureName).documentation("The world structure to be used for this world.").add()).build();
/* 111 */     IWorldGenProvider.CODEC.register("HytaleGenerator", HandleProvider.class, (Codec)generatorProvider);
/*     */     
/* 113 */     getCommandRegistry().registerCommand((AbstractCommand)new ViewportCommand(this.assetManager));
/*     */ 
/*     */     
/* 116 */     getEventRegistry().registerGlobal(RemoveWorldEvent.class, event -> {
/*     */           IWorldGen generator = event.getWorld().getChunkStore().getGenerator();
/*     */           if (generator instanceof Handle) {
/*     */             Handle handle = (Handle)generator;
/*     */             this.generators.remove(handle.getProfile());
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public NStagedChunkGenerator createStagedChunkGenerator(@Nonnull ChunkRequest.GeneratorProfile generatorProfile, @Nonnull WorldStructureAsset worldStructureAsset, @Nonnull SettingsAsset settingsAsset) {
/* 130 */     WorkerIndexer workerIndexer = new WorkerIndexer(this.concurrency);
/* 131 */     SeedBox seed = new SeedBox(generatorProfile.seed());
/* 132 */     MaterialCache materialCache = new MaterialCache();
/* 133 */     BiomeMap<SolidMaterial> biomeMap = worldStructureAsset.buildBiomeMap(new WorldStructureAsset.Argument(materialCache, seed, workerIndexer));
/* 134 */     worldStructureAsset.cleanUp();
/* 135 */     NStagedChunkGenerator.Builder generatorBuilder = new NStagedChunkGenerator.Builder();
/*     */     
/* 137 */     List<BiomeType> allBiomes = biomeMap.allPossibleValues();
/* 138 */     List<Integer> allRuntimes = new ArrayList<>(getAllPossibleRuntimeIndices(allBiomes));
/* 139 */     allRuntimes.sort(Comparator.naturalOrder());
/*     */     
/* 141 */     int bufferTypeIndexCounter = 0;
/*     */ 
/*     */ 
/*     */     
/* 145 */     NParametrizedBufferType biome_bufferType = new NParametrizedBufferType("Biome", bufferTypeIndexCounter++, NBiomeStage.bufferClass, NBiomeStage.biomeTypeClass, () -> new NCountedPixelBuffer(NBiomeStage.biomeTypeClass));
/*     */     
/* 147 */     NBiomeStage nBiomeStage = new NBiomeStage("BiomeStage", biome_bufferType, (BiCarta)biomeMap);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     generatorBuilder.appendStage((NStage)nBiomeStage);
/*     */ 
/*     */ 
/*     */     
/* 156 */     NParametrizedBufferType biomeDistance_bufferType = new NParametrizedBufferType("BiomeDistance", bufferTypeIndexCounter++, NBiomeDistanceStage.biomeDistanceBufferClass, NBiomeDistanceStage.biomeDistanceClass, () -> new NSimplePixelBuffer(NBiomeDistanceStage.biomeDistanceClass));
/*     */     
/* 158 */     int MAX_BIOME_DISTANCE_RADIUS = 512;
/* 159 */     int interpolationRadius = Math.clamp((worldStructureAsset.getBiomeTransitionDistance() / 2), 0, 512);
/* 160 */     int biomeEdgeRadius = Math.clamp(worldStructureAsset.getMaxBiomeEdgeDistance(), 0, 512);
/* 161 */     int maxDistance = Math.max(interpolationRadius, biomeEdgeRadius);
/* 162 */     NBiomeDistanceStage nBiomeDistanceStage = new NBiomeDistanceStage("BiomeDistanceStage", biome_bufferType, biomeDistance_bufferType, maxDistance);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 168 */     generatorBuilder.appendStage((NStage)nBiomeDistanceStage);
/*     */ 
/*     */ 
/*     */     
/* 172 */     int materialBufferIndexCounter = 0;
/* 173 */     NParametrizedBufferType material0_bufferType = generatorBuilder.MATERIAL_OUTPUT_BUFFER_TYPE;
/* 174 */     if (!allRuntimes.isEmpty()) {
/* 175 */       material0_bufferType = new NParametrizedBufferType("Material" + materialBufferIndexCounter, bufferTypeIndexCounter++, NTerrainStage.materialBufferClass, NTerrainStage.materialClass, () -> new NVoxelBuffer(NTerrainStage.materialClass));
/*     */       
/* 177 */       materialBufferIndexCounter++;
/*     */     } 
/*     */     
/* 180 */     NTerrainStage nTerrainStage = new NTerrainStage("TerrainStage", biome_bufferType, biomeDistance_bufferType, material0_bufferType, interpolationRadius, materialCache, workerIndexer);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 189 */     generatorBuilder.appendStage((NStage)nTerrainStage);
/*     */ 
/*     */ 
/*     */     
/* 193 */     NParametrizedBufferType materialInput_bufferType = material0_bufferType;
/* 194 */     NBufferType entityInput_bufferType = null;
/*     */     
/* 196 */     for (int i = 0; i < allRuntimes.size() - 1; i++) {
/* 197 */       int runtime = ((Integer)allRuntimes.get(i)).intValue();
/* 198 */       String runtimeString = Integer.toString(runtime);
/*     */       
/* 200 */       NParametrizedBufferType materialOutput_bufferType = new NParametrizedBufferType("Material" + materialBufferIndexCounter, bufferTypeIndexCounter++, NTerrainStage.materialBufferClass, NTerrainStage.materialClass, () -> new NVoxelBuffer(NTerrainStage.materialClass));
/* 201 */       NBufferType entityOutput_bufferType = new NBufferType("Entity" + materialBufferIndexCounter, bufferTypeIndexCounter++, NEntityBuffer.class, NEntityBuffer::new);
/*     */       
/* 203 */       NPropStage nPropStage = new NPropStage("PropStage" + runtimeString, biome_bufferType, biomeDistance_bufferType, materialInput_bufferType, entityInput_bufferType, materialOutput_bufferType, entityOutput_bufferType, materialCache, allBiomes, runtime);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 216 */       generatorBuilder.appendStage((NStage)nPropStage);
/*     */ 
/*     */       
/* 219 */       materialInput_bufferType = materialOutput_bufferType;
/* 220 */       entityInput_bufferType = entityOutput_bufferType;
/* 221 */       materialBufferIndexCounter++;
/*     */     } 
/*     */ 
/*     */     
/* 225 */     if (!allRuntimes.isEmpty()) {
/* 226 */       int runtime = ((Integer)allRuntimes.getLast()).intValue();
/* 227 */       String runtimeString = Integer.toString(runtime);
/*     */       
/* 229 */       NPropStage nPropStage = new NPropStage("PropStage" + runtimeString, biome_bufferType, biomeDistance_bufferType, materialInput_bufferType, entityInput_bufferType, generatorBuilder.MATERIAL_OUTPUT_BUFFER_TYPE, generatorBuilder.ENTITY_OUTPUT_BUFFER_TYPE, materialCache, allBiomes, runtime);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 242 */       generatorBuilder.appendStage((NStage)nPropStage);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 247 */     NTintStage nTintStage = new NTintStage("TintStage", biome_bufferType, generatorBuilder.TINT_OUTPUT_BUFFER_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 252 */     generatorBuilder.appendStage((NStage)nTintStage);
/*     */ 
/*     */ 
/*     */     
/* 256 */     NEnvironmentStage nEnvironmentStage = new NEnvironmentStage("EnvironmentStage", biome_bufferType, generatorBuilder.ENVIRONMENT_OUTPUT_BUFFER_TYPE);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 261 */     generatorBuilder.appendStage((NStage)nEnvironmentStage);
/*     */     
/* 263 */     double bufferCapacityFactor = Math.max(0.0D, settingsAsset.getBufferCapacityFactor());
/* 264 */     double targetViewDistance = Math.max(0.0D, settingsAsset.getTargetViewDistance());
/* 265 */     double targetPlayerCount = Math.max(0.0D, settingsAsset.getTargetPlayerCount());
/* 266 */     Set<Integer> statsCheckpoints = new HashSet<>(settingsAsset.getStatsCheckpoints());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 273 */     NStagedChunkGenerator generator = generatorBuilder.withStats("WorldStructure Name: " + generatorProfile.worldStructureName(), statsCheckpoints).withMaterialCache(materialCache).withConcurrentExecutor(this.concurrentExecutor, workerIndexer).withBufferCapacity(bufferCapacityFactor, targetViewDistance, targetPlayerCount).build();
/*     */     
/* 275 */     return generator;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private static Set<Integer> getAllPossibleRuntimeIndices(@Nonnull List<BiomeType> biomes) {
/* 280 */     Set<Integer> allRuntimes = new HashSet<>();
/* 281 */     for (BiomeType biome : biomes) {
/* 282 */       for (PropField propField : biome.getPropFields()) {
/* 283 */         allRuntimes.add(Integer.valueOf(propField.getRuntime()));
/*     */       }
/*     */     } 
/* 286 */     return allRuntimes;
/*     */   }
/*     */   @Nonnull
/*     */   private ChunkGenerator getGenerator(@Nonnull ChunkRequest.GeneratorProfile profile) {
/*     */     NStagedChunkGenerator nStagedChunkGenerator;
/* 291 */     ChunkGenerator generator = this.generators.get(profile);
/*     */     
/* 293 */     if (generator == null) {
/* 294 */       if (profile.worldStructureName() == null) {
/* 295 */         LoggerUtil.getLogger().warning("World Structure asset not loaded.");
/* 296 */         return (ChunkGenerator)FallbackGenerator.INSTANCE;
/*     */       } 
/*     */       
/* 299 */       WorldStructureAsset worldStructureAsset = this.assetManager.getWorldStructureAsset(profile.worldStructureName());
/* 300 */       if (worldStructureAsset == null) {
/* 301 */         LoggerUtil.getLogger().warning("World Structure asset not found: " + profile.worldStructureName());
/* 302 */         return (ChunkGenerator)FallbackGenerator.INSTANCE;
/*     */       } 
/*     */       
/* 305 */       SettingsAsset settingsAsset = this.assetManager.getSettingsAsset();
/* 306 */       if (settingsAsset == null) {
/* 307 */         LoggerUtil.getLogger().warning("Settings asset not found.");
/* 308 */         return (ChunkGenerator)FallbackGenerator.INSTANCE;
/*     */       } 
/*     */       
/* 311 */       nStagedChunkGenerator = createStagedChunkGenerator(profile, worldStructureAsset, settingsAsset);
/* 312 */       this.generators.put(profile, nStagedChunkGenerator);
/*     */     } 
/*     */     
/* 315 */     return (ChunkGenerator)nStagedChunkGenerator;
/*     */   }
/*     */   
/*     */   private void loadExecutors(@Nonnull SettingsAsset settingsAsset) {
/* 319 */     int newConcurrency = getConcurrency(settingsAsset);
/*     */     
/* 321 */     if (newConcurrency == this.concurrency && this.mainExecutor != null && this.concurrentExecutor != null)
/*     */       return; 
/* 323 */     this.concurrency = newConcurrency;
/*     */     
/* 325 */     if (this.mainExecutor == null) {
/* 326 */       this.mainExecutor = Executors.newSingleThreadExecutor();
/*     */     }
/*     */     
/* 329 */     if (this.concurrentExecutor != null && !this.concurrentExecutor.isShutdown()) {
/*     */       try {
/* 331 */         this.concurrentExecutor.shutdown();
/* 332 */         if (!this.concurrentExecutor.awaitTermination(1L, TimeUnit.MINUTES));
/*     */ 
/*     */       
/*     */       }
/* 336 */       catch (InterruptedException e) {
/* 337 */         throw new RuntimeException(e);
/*     */       } 
/*     */     }
/*     */     
/* 341 */     this.concurrentExecutor = new ThreadPoolExecutor(this.concurrency, this.concurrency, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> {
/*     */           Thread t = new Thread(r, "HytaleGenerator-Worker");
/*     */           
/*     */           t.setPriority(1);
/*     */           t.setDaemon(true);
/*     */           return t;
/*     */         });
/* 348 */     if (this.mainExecutor == null || this.mainExecutor.isShutdown())
/*     */     {
/* 350 */       this.mainExecutor = Executors.newSingleThreadExecutor();
/*     */     }
/*     */   }
/*     */   
/*     */   private static int getConcurrency(@Nonnull SettingsAsset settingsAsset) {
/* 355 */     int concurrencySetting = settingsAsset.getCustomConcurrency();
/* 356 */     int availableProcessors = Runtime.getRuntime().availableProcessors();
/* 357 */     int value = 1;
/*     */     
/* 359 */     if (concurrencySetting < 1) {
/* 360 */       value = Math.max(availableProcessors, 1);
/*     */     } else {
/* 362 */       if (concurrencySetting > availableProcessors) {
/* 363 */         LoggerUtil.getLogger().warning("Concurrency setting " + concurrencySetting + " exceeds available processors " + availableProcessors);
/*     */       }
/* 365 */       value = concurrencySetting;
/*     */     } 
/*     */     
/* 368 */     return value;
/*     */   }
/*     */   
/*     */   private void reloadGenerators() {
/*     */     try {
/* 373 */       this.chunkGenerationSemaphore.acquireUninterruptibly();
/*     */ 
/*     */       
/* 376 */       loadExecutors(this.assetManager.getSettingsAsset());
/* 377 */       this.generators.clear();
/*     */     } finally {
/*     */       
/* 380 */       this.chunkGenerationSemaphore.release();
/*     */     } 
/*     */     
/* 383 */     LoggerUtil.getLogger().info("Reloaded HytaleGenerator.");
/*     */   }
/*     */   
/*     */   public HytaleGenerator(@Nonnull JavaPluginInit init) {
/* 387 */     super(init);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\plugin\HytaleGenerator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */