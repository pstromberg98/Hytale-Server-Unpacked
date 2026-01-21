/*     */ package com.hypixel.hytale.server.core.asset;
/*     */ import com.hypixel.hytale.assetstore.AssetLoadResult;
/*     */ import com.hypixel.hytale.assetstore.AssetMap;
/*     */ import com.hypixel.hytale.assetstore.AssetPack;
/*     */ import com.hypixel.hytale.assetstore.AssetRegistry;
/*     */ import com.hypixel.hytale.assetstore.AssetStore;
/*     */ import com.hypixel.hytale.assetstore.event.RegisterAssetStoreEvent;
/*     */ import com.hypixel.hytale.assetstore.event.RemoveAssetStoreEvent;
/*     */ import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.util.RawJsonReader;
/*     */ import com.hypixel.hytale.common.plugin.PluginIdentifier;
/*     */ import com.hypixel.hytale.common.plugin.PluginManifest;
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.event.IBaseEvent;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.Options;
/*     */ import com.hypixel.hytale.server.core.asset.monitor.AssetMonitor;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.HomeOrSpawnPoint;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.WorldSpawnPoint;
/*     */ import com.hypixel.hytale.server.core.event.events.BootEvent;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.IWorldMap;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.provider.IWorldMapProvider;
/*     */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.FileSystem;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class AssetModule extends JavaPlugin {
/*  51 */   public static final PluginManifest MANIFEST = PluginManifest.corePlugin(AssetModule.class)
/*  52 */     .build(); private static AssetModule instance;
/*     */   @Nullable
/*     */   private AssetMonitor assetMonitor;
/*     */   
/*     */   public static AssetModule get() {
/*  57 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*  62 */   private final List<AssetPack> assetPacks = new CopyOnWriteArrayList<>();
/*     */   
/*     */   private boolean hasLoaded = false;
/*  65 */   private final List<AssetStore<?, ?, ?>> pendingAssetStores = new CopyOnWriteArrayList<>();
/*     */   
/*     */   public AssetModule(@Nonnull JavaPluginInit init) {
/*  68 */     super(init);
/*  69 */     instance = this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  74 */     if (Options.getOptionSet().has(Options.DISABLE_FILE_WATCHER)) {
/*  75 */       getLogger().at(Level.WARNING).log("Not running asset watcher because --disable-file-watcher was set");
/*     */     } else {
/*     */       try {
/*  78 */         this.assetMonitor = new AssetMonitor();
/*  79 */         getLogger().at(Level.INFO).log("Asset monitor enabled!");
/*  80 */       } catch (IOException e) {
/*  81 */         ((HytaleLogger.Api)getLogger().at(Level.SEVERE).withCause(e)).log("Failed to create asset monitor!");
/*     */       } 
/*     */     } 
/*     */     
/*  85 */     List<Path> paths = Options.getOptionSet().valuesOf(Options.ASSET_DIRECTORY);
/*  86 */     for (Path path : paths) {
/*  87 */       loadAndRegisterPack(path);
/*     */     }
/*     */ 
/*     */     
/*  91 */     loadPacksFromDirectory(PluginManager.MODS_PATH);
/*     */ 
/*     */     
/*  94 */     for (Path modsPath : Options.getOptionSet().valuesOf(Options.MODS_DIRECTORIES)) {
/*  95 */       loadPacksFromDirectory(modsPath);
/*     */     }
/*     */     
/*  98 */     if (this.assetPacks.isEmpty()) {
/*  99 */       HytaleServer.get().shutdownServer(ShutdownReason.MISSING_ASSETS.withMessage("Failed to load any asset packs"));
/*     */       
/*     */       return;
/*     */     } 
/* 103 */     getEventRegistry().register((short)-16, LoadAssetEvent.class, event -> {
/*     */           if (this.hasLoaded) {
/*     */             throw new IllegalStateException("LoadAssetEvent has already been dispatched");
/*     */           }
/*     */           
/*     */           AssetRegistry.ASSET_LOCK.writeLock().lock();
/*     */           
/*     */           try {
/*     */             this.hasLoaded = true;
/*     */             AssetRegistryLoader.preLoadAssets(event);
/*     */             for (AssetPack pack : this.assetPacks) {
/*     */               AssetRegistryLoader.loadAssets(event, pack);
/*     */             }
/*     */           } finally {
/*     */             AssetRegistry.ASSET_LOCK.writeLock().unlock();
/*     */           } 
/*     */         });
/* 120 */     getEventRegistry().register((short)-16, AssetPackRegisterEvent.class, event -> AssetRegistryLoader.loadAssets(null, event.getAssetPack()));
/*     */ 
/*     */     
/* 123 */     getEventRegistry().register(AssetPackUnregisterEvent.class, event -> {
/*     */           for (AssetStore<?, ?, ?> assetStore : (Iterable<AssetStore<?, ?, ?>>)AssetRegistry.getStoreMap().values()) {
/*     */             assetStore.removeAssetPack(event.getAssetPack().getName());
/*     */           }
/*     */         });
/*     */     
/* 129 */     getEventRegistry().register(LoadAssetEvent.class, AssetModule::validateWorldGen);
/* 130 */     getEventRegistry().register(EventPriority.FIRST, LoadAssetEvent.class, SneakyThrow.sneakyConsumer(AssetRegistryLoader::writeSchemas));
/* 131 */     getEventRegistry().register(RegisterAssetStoreEvent.class, this::onNewStore);
/* 132 */     getEventRegistry().register(RemoveAssetStoreEvent.class, this::onRemoveStore);
/*     */     
/* 134 */     getEventRegistry().registerGlobal(BootEvent.class, event -> {
/*     */           StringBuilder sb = new StringBuilder("Total Loaded Assets: ");
/*     */           
/*     */           AssetStore[] assetStores = (AssetStore[])AssetRegistry.getStoreMap().values().toArray(());
/*     */           Arrays.sort(assetStores, Comparator.comparingInt(()));
/*     */           for (int i = assetStores.length - 1; i >= 0; i--) {
/*     */             AssetStore assetStore = assetStores[i];
/*     */             String simpleName = assetStore.getAssetClass().getSimpleName();
/*     */             int assetCount = assetStore.getAssetMap().getAssetCount();
/*     */             sb.append(simpleName).append(": ").append(assetCount).append(", ");
/*     */           } 
/*     */           sb.setLength(sb.length() - 2);
/*     */           getLogger().at(Level.INFO).log(sb.toString());
/*     */         });
/* 148 */     RespawnController.CODEC.register("HomeOrSpawnPoint", HomeOrSpawnPoint.class, (Codec)HomeOrSpawnPoint.CODEC);
/* 149 */     RespawnController.CODEC.register("WorldSpawnPoint", WorldSpawnPoint.class, (Codec)WorldSpawnPoint.CODEC);
/*     */     
/* 151 */     getCommandRegistry().registerCommand((AbstractCommand)new DroplistCommand());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void shutdown() {
/* 156 */     if (this.assetMonitor != null) {
/* 157 */       this.assetMonitor.shutdown();
/* 158 */       this.assetMonitor = null;
/*     */     } 
/*     */     
/* 161 */     for (AssetPack pack : this.assetPacks) {
/* 162 */       if (pack.getFileSystem() != null) {
/*     */         try {
/* 164 */           pack.getFileSystem().close();
/* 165 */         } catch (IOException e) {
/*     */           
/* 167 */           ((HytaleLogger.Api)getLogger().at(Level.WARNING).withCause(e)).log("Failed to close asset pack filesystem: %s", pack.getName());
/*     */         } 
/*     */       }
/*     */     } 
/* 171 */     this.assetPacks.clear();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public AssetPack getBaseAssetPack() {
/* 176 */     return this.assetPacks.getFirst();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public List<AssetPack> getAssetPacks() {
/* 181 */     return this.assetPacks;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public AssetMonitor getAssetMonitor() {
/* 186 */     return this.assetMonitor;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public AssetPack findAssetPackForPath(Path path) {
/* 191 */     path = path.toAbsolutePath().normalize();
/* 192 */     for (AssetPack pack : this.assetPacks) {
/* 193 */       if (path.startsWith(pack.getRoot())) return pack; 
/*     */     } 
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAssetPathImmutable(@Nonnull Path path) {
/* 203 */     AssetPack pack = findAssetPackForPath(path);
/* 204 */     return (pack != null && pack.isImmutable());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PluginManifest loadPackManifest(Path packPath) throws IOException {
/* 216 */     if (packPath.getFileName().toString().toLowerCase().endsWith(".zip"))
/* 217 */     { FileSystem fs = FileSystems.newFileSystem(packPath, (ClassLoader)null); 
/* 218 */       try { Path manifestPath = fs.getPath("manifest.json", new String[0]);
/* 219 */         if (Files.exists(manifestPath, new java.nio.file.LinkOption[0]))
/* 220 */         { BufferedReader reader = Files.newBufferedReader(manifestPath, StandardCharsets.UTF_8); 
/* 221 */           try { char[] buffer = RawJsonReader.READ_BUFFER.get();
/* 222 */             RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
/* 223 */             ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/* 224 */             PluginManifest manifest = (PluginManifest)PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
/* 225 */             extraInfo.getValidationResults().logOrThrowValidatorExceptions(getLogger());
/* 226 */             PluginManifest pluginManifest1 = manifest;
/* 227 */             if (reader != null) reader.close();
/*     */             
/* 229 */             if (fs != null) fs.close();  return pluginManifest1; } catch (Throwable throwable) { if (reader != null) try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  }  if (fs != null) fs.close();  } catch (Throwable throwable) { if (fs != null)
/* 230 */           try { fs.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } else if (Files.isDirectory(packPath, new java.nio.file.LinkOption[0]))
/*     */     
/* 232 */     { Path manifestPath = packPath.resolve("manifest.json");
/* 233 */       if (Files.exists(manifestPath, new java.nio.file.LinkOption[0])) {
/* 234 */         FileReader reader = new FileReader(manifestPath.toFile(), StandardCharsets.UTF_8); 
/* 235 */         try { char[] buffer = RawJsonReader.READ_BUFFER.get();
/* 236 */           RawJsonReader rawJsonReader = new RawJsonReader(reader, buffer);
/* 237 */           ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
/* 238 */           PluginManifest manifest = (PluginManifest)PluginManifest.CODEC.decodeJson(rawJsonReader, extraInfo);
/* 239 */           extraInfo.getValidationResults().logOrThrowValidatorExceptions(getLogger());
/* 240 */           PluginManifest pluginManifest1 = manifest;
/* 241 */           reader.close(); return pluginManifest1; } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */            throw throwable; }
/*     */       
/*     */       }  }
/* 245 */      return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadPacksFromDirectory(Path modsPath) {
/* 254 */     if (!Files.isDirectory(modsPath, new java.nio.file.LinkOption[0]))
/*     */       return; 
/* 256 */     getLogger().at(Level.INFO).log("Loading packs from directory: %s", modsPath); 
/* 257 */     try { DirectoryStream<Path> stream = Files.newDirectoryStream(modsPath); 
/* 258 */       try { for (Path packPath : stream) {
/* 259 */           if (packPath.getFileName() == null || packPath.getFileName().toString().toLowerCase().endsWith(".jar"))
/* 260 */             continue;  loadAndRegisterPack(packPath);
/*     */         } 
/* 262 */         if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null) try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/* 263 */     { ((HytaleLogger.Api)getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load mods from: %s", modsPath); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void loadAndRegisterPack(Path packPath) {
/*     */     PluginManifest manifest;
/*     */     try {
/* 277 */       manifest = loadPackManifest(packPath);
/* 278 */       if (manifest == null) {
/* 279 */         getLogger().at(Level.WARNING).log("Skipping pack at %s: missing or invalid manifest.json", packPath.getFileName());
/*     */         return;
/*     */       } 
/* 282 */     } catch (Exception e) {
/* 283 */       ((HytaleLogger.Api)getLogger().at(Level.WARNING).withCause(e)).log("Failed to load manifest for pack at %s", packPath);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 288 */     PluginIdentifier packIdentifier = new PluginIdentifier(manifest);
/*     */ 
/*     */     
/* 291 */     HytaleServerConfig.ModConfig modConfig = (HytaleServerConfig.ModConfig)HytaleServer.get().getConfig().getModConfig().get(packIdentifier);
/* 292 */     boolean enabled = (modConfig == null || modConfig.getEnabled() == null || modConfig.getEnabled().booleanValue());
/*     */     
/* 294 */     String packId = packIdentifier.toString();
/* 295 */     if (enabled) {
/* 296 */       registerPack(packId, packPath, manifest);
/* 297 */       getLogger().at(Level.INFO).log("Loaded pack: %s from %s", packId, packPath.getFileName());
/*     */     } else {
/* 299 */       getLogger().at(Level.INFO).log("Skipped disabled pack: %s", packId);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void registerPack(@Nonnull String name, @Nonnull Path path, @Nonnull PluginManifest manifest) {
/* 304 */     Path absolutePath = path.toAbsolutePath().normalize();
/* 305 */     Path packLocation = absolutePath;
/* 306 */     FileSystem fileSystem = null;
/* 307 */     boolean isImmutable = false;
/*     */ 
/*     */     
/* 310 */     String lowerFileName = absolutePath.getFileName().toString().toLowerCase();
/* 311 */     if (lowerFileName.endsWith(".zip") || lowerFileName.endsWith(".jar")) {
/*     */       try {
/* 313 */         fileSystem = FileSystems.newFileSystem(absolutePath, (ClassLoader)null);
/* 314 */         absolutePath = fileSystem.getPath("", new String[0]).toAbsolutePath().normalize();
/* 315 */         isImmutable = true;
/* 316 */       } catch (IOException e) {
/* 317 */         throw SneakyThrow.sneakyThrow(e);
/*     */       } 
/*     */     } else {
/*     */       
/* 321 */       isImmutable = Files.isRegularFile(absolutePath.resolve("CommonAssetsIndex.hashes"), new java.nio.file.LinkOption[0]);
/*     */     } 
/*     */     
/* 324 */     AssetPack pack = new AssetPack(packLocation, name, absolutePath, fileSystem, isImmutable, manifest);
/* 325 */     this.assetPacks.add(pack);
/*     */     
/* 327 */     AssetRegistry.ASSET_LOCK.writeLock().lock();
/*     */     try {
/* 329 */       if (!this.hasLoaded)
/*     */         return; 
/* 331 */       HytaleServer.get().getEventBus()
/* 332 */         .dispatchFor(AssetPackRegisterEvent.class)
/* 333 */         .dispatch((IBaseEvent)new AssetPackRegisterEvent(pack));
/*     */     } finally {
/* 335 */       AssetRegistry.ASSET_LOCK.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void unregisterPack(@Nonnull String name) {
/* 340 */     AssetPack pack = getAssetPack(name);
/* 341 */     if (pack == null) {
/* 342 */       getLogger().at(Level.WARNING).log("Tried to unregister non-existent asset pack: %s", name);
/*     */       
/*     */       return;
/*     */     } 
/* 346 */     this.assetPacks.remove(pack);
/*     */     
/* 348 */     if (pack.getFileSystem() != null) {
/*     */       try {
/* 350 */         pack.getFileSystem().close();
/* 351 */       } catch (IOException e) {
/* 352 */         throw SneakyThrow.sneakyThrow(e);
/*     */       } 
/*     */     }
/*     */     
/* 356 */     AssetRegistry.ASSET_LOCK.writeLock().lock();
/*     */     try {
/* 358 */       HytaleServer.get().getEventBus()
/* 359 */         .dispatchFor(AssetPackUnregisterEvent.class)
/* 360 */         .dispatch((IBaseEvent)new AssetPackUnregisterEvent(pack));
/*     */     } finally {
/* 362 */       AssetRegistry.ASSET_LOCK.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public AssetPack getAssetPack(@Nonnull String name) {
/* 367 */     for (AssetPack pack : this.assetPacks) {
/* 368 */       if (name.equals(pack.getName())) return pack; 
/*     */     } 
/* 370 */     return null;
/*     */   }
/*     */   
/*     */   private void onRemoveStore(@Nonnull RemoveAssetStoreEvent event) {
/* 374 */     AssetStore<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>, ? extends AssetMap<?, ? extends JsonAssetWithMap<?, ?>>> assetStore = event.getAssetStore();
/* 375 */     String path = assetStore.getPath();
/* 376 */     if (path == null)
/*     */       return; 
/* 378 */     for (AssetPack pack : this.assetPacks) {
/* 379 */       if (pack.isImmutable())
/*     */         continue; 
/* 381 */       Path assetsPath = pack.getRoot().resolve("Server").resolve(path);
/* 382 */       if (Files.isDirectory(assetsPath, new java.nio.file.LinkOption[0])) {
/* 383 */         assetStore.removeFileMonitor(assetsPath);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void onNewStore(@Nonnull RegisterAssetStoreEvent event) {
/* 390 */     if (!AssetRegistry.HAS_INIT)
/* 391 */       return;  this.pendingAssetStores.add(event.getAssetStore());
/*     */   }
/*     */   
/*     */   public void initPendingStores() {
/* 395 */     for (int i = 0; i < this.pendingAssetStores.size(); i++) {
/* 396 */       initStore(this.pendingAssetStores.get(i));
/*     */     }
/* 398 */     this.pendingAssetStores.clear();
/*     */   }
/*     */   
/*     */   private void initStore(@Nonnull AssetStore<?, ?, ?> assetStore) {
/* 402 */     AssetRegistry.ASSET_LOCK.writeLock().lock();
/*     */     try {
/* 404 */       List<?> preAddedAssets = assetStore.getPreAddedAssets();
/* 405 */       if (preAddedAssets != null && !preAddedAssets.isEmpty()) {
/*     */         
/* 407 */         AssetLoadResult loadResult = assetStore.loadAssets("Hytale:Hytale", preAddedAssets);
/* 408 */         if (loadResult.hasFailed()) throw new RuntimeException("Failed to load asset store: " + String.valueOf(assetStore.getAssetClass()));
/*     */       
/*     */       } 
/* 411 */       for (AssetPack pack : this.assetPacks) {
/* 412 */         Path serverAssetDirectory = pack.getRoot().resolve("Server");
/* 413 */         String path = assetStore.getPath();
/* 414 */         if (path != null) {
/* 415 */           Path assetsPath = serverAssetDirectory.resolve(path);
/* 416 */           if (Files.isDirectory(assetsPath, new java.nio.file.LinkOption[0])) {
/* 417 */             AssetLoadResult<?, ? extends JsonAssetWithMap<?, ? extends AssetMap<?, ?>>> loadResult = assetStore.loadAssetsFromDirectory(pack.getName(), assetsPath);
/* 418 */             if (loadResult.hasFailed()) throw new RuntimeException("Failed to load asset store: " + String.valueOf(assetStore.getAssetClass())); 
/*     */           } else {
/* 420 */             getLogger().at(Level.SEVERE).log("Path for %s isn't a directory or doesn't exist: %s", assetStore.getAssetClass().getSimpleName(), assetsPath);
/*     */           } 
/*     */         } 
/*     */         
/* 424 */         assetStore.validateCodecDefaults();
/* 425 */         if (path != null) {
/* 426 */           Path assetsPath = serverAssetDirectory.resolve(path);
/* 427 */           if (Files.isDirectory(assetsPath, new java.nio.file.LinkOption[0])) {
/* 428 */             assetStore.addFileMonitor(pack.getName(), assetsPath);
/*     */           }
/*     */         } 
/*     */       } 
/* 432 */     } catch (IOException e) {
/* 433 */       throw SneakyThrow.sneakyThrow(e);
/*     */     } finally {
/* 435 */       AssetRegistry.ASSET_LOCK.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void validateWorldGen(@Nonnull LoadAssetEvent event) {
/* 441 */     if (!Options.getOptionSet().has(Options.VALIDATE_WORLD_GEN))
/* 442 */       return;  long start = System.nanoTime();
/*     */     
/*     */     try {
/* 445 */       IWorldGenProvider provider = (IWorldGenProvider)IWorldGenProvider.CODEC.getDefault();
/*     */ 
/*     */       
/* 448 */       IWorldGen generator = provider.getGenerator();
/*     */ 
/*     */       
/* 451 */       generator.getDefaultSpawnProvider(0);
/*     */       
/* 453 */       if (generator instanceof ValidatableWorldGen) {
/* 454 */         boolean valid = ((ValidatableWorldGen)generator).validate();
/* 455 */         if (!valid) event.failed(true, "failed to validate world gen");
/*     */       
/*     */       } 
/* 458 */       if (generator instanceof IWorldMapProvider) { IWorldMapProvider worldMapProvider = (IWorldMapProvider)generator;
/* 459 */         IWorldMap worldMap = worldMapProvider.getGenerator(null);
/*     */ 
/*     */         
/* 462 */         worldMap.getWorldMapSettings(); }
/*     */     
/* 464 */     } catch (WorldGenLoadException e) {
/* 465 */       ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause((Throwable)e)).log("Failed to load default world gen!");
/* 466 */       HytaleLogger.getLogger().at(Level.SEVERE).log("\n" + e.getTraceMessage("\n"));
/* 467 */       event.failed(true, "failed to validate world gen: " + e.getTraceMessage(" -> "));
/* 468 */     } catch (Throwable e) {
/* 469 */       ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Failed to load default world gen!");
/* 470 */       event.failed(true, "failed to validate world gen");
/*     */     } 
/*     */     
/* 473 */     HytaleLogger.getLogger().at(Level.INFO).log("Validate world gen phase completed! Boot time %s, Took %s", FormatUtil.nanosToString(System.nanoTime() - event.getBootStart()), FormatUtil.nanosToString(System.nanoTime() - start));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\AssetModule.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */