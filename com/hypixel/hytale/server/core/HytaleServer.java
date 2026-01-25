/*     */ package com.hypixel.hytale.server.core;
/*     */ import com.hypixel.hytale.assetstore.AssetPack;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.common.plugin.PluginManifest;
/*     */ import com.hypixel.hytale.common.thread.HytaleForkJoinThreadFactory;
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.common.util.GCUtil;
/*     */ import com.hypixel.hytale.common.util.HardwareUtil;
/*     */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*     */ import com.hypixel.hytale.event.EventBus;
/*     */ import com.hypixel.hytale.event.IBaseEvent;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.logger.backend.HytaleLogManager;
/*     */ import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.util.TrigMathUtil;
/*     */ import com.hypixel.hytale.metrics.JVMMetrics;
/*     */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*     */ import com.hypixel.hytale.plugin.early.EarlyPluginLoader;
/*     */ import com.hypixel.hytale.server.core.asset.AssetModule;
/*     */ import com.hypixel.hytale.server.core.asset.AssetRegistryLoader;
/*     */ import com.hypixel.hytale.server.core.asset.LoadAssetEvent;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.auth.SessionServiceClient;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandManager;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*     */ import com.hypixel.hytale.server.core.console.ConsoleSender;
/*     */ import com.hypixel.hytale.server.core.event.events.BootEvent;
/*     */ import com.hypixel.hytale.server.core.event.events.ShutdownEvent;
/*     */ import com.hypixel.hytale.server.core.io.ServerManager;
/*     */ import com.hypixel.hytale.server.core.io.netty.NettyUtil;
/*     */ import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginBase;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginClassLoader;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginManager;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginState;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.datastore.DataStoreProvider;
/*     */ import com.hypixel.hytale.server.core.universe.datastore.DiskDataStoreProvider;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.update.UpdateModule;
/*     */ import com.hypixel.hytale.server.core.util.concurrent.ThreadUtil;
/*     */ import com.sun.management.GarbageCollectionNotificationInfo;
/*     */ import io.netty.handler.codec.quic.Quic;
/*     */ import io.sentry.Hint;
/*     */ import io.sentry.IScope;
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.SentryEvent;
/*     */ import io.sentry.SentryOptions;
/*     */ import io.sentry.protocol.Contexts;
/*     */ import io.sentry.protocol.OperatingSystem;
/*     */ import io.sentry.protocol.User;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.io.IOException;
/*     */ import java.time.Instant;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.Semaphore;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import joptsimple.OptionSet;
/*     */ 
/*     */ public class HytaleServer {
/*  74 */   public static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor(ThreadUtil.daemon("Scheduler"));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int DEFAULT_PORT = 5520;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static final MetricsRegistry<HytaleServer> METRICS_REGISTRY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  93 */     METRICS_REGISTRY = (new MetricsRegistry()).register("Time", server -> Instant.now(), (Codec)Codec.INSTANT).register("Boot", server -> server.boot, (Codec)Codec.INSTANT).register("BootStart", server -> Long.valueOf(server.bootStart), (Codec)Codec.LONG).register("Booting", server -> Boolean.valueOf(server.booting.get()), (Codec)Codec.BOOLEAN).register("ShutdownReason", server -> { ShutdownReason reason = server.shutdown.get(); return (Function)((reason == null) ? null : reason.toString()); }(Codec)Codec.STRING).register("PluginManager", HytaleServer::getPluginManager, (Codec)PluginManager.METRICS_REGISTRY).register("Config", HytaleServer::getConfig, (Codec)HytaleServerConfig.CODEC).register("JVM", JVMMetrics.METRICS_REGISTRY);
/*     */   }
/*  95 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   private static HytaleServer instance;
/*     */ 
/*     */ 
/*     */   
/* 103 */   private final Semaphore aliveLock = new Semaphore(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 109 */   private final AtomicBoolean booting = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   private final AtomicBoolean booted = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 121 */   private final AtomicReference<ShutdownReason> shutdown = new AtomicReference<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 126 */   private final EventBus eventBus = new EventBus(Options.getOptionSet().has(Options.EVENT_DEBUG));
/* 127 */   private final PluginManager pluginManager = new PluginManager();
/* 128 */   private final CommandManager commandManager = new CommandManager();
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final HytaleServerConfig hytaleServerConfig;
/*     */ 
/*     */   
/*     */   private final Instant boot;
/*     */   
/*     */   private final long bootStart;
/*     */   
/*     */   private int pluginsProgress;
/*     */ 
/*     */   
/*     */   public HytaleServer() throws IOException {
/* 143 */     instance = this;
/*     */     
/* 145 */     Quic.ensureAvailability();
/*     */     
/* 147 */     HytaleLoggerBackend.setIndent(25);
/*     */     
/* 149 */     ThreadUtil.forceTimeHighResolution();
/* 150 */     ThreadUtil.createKeepAliveThread(this.aliveLock);
/*     */     
/* 152 */     this.boot = Instant.now();
/* 153 */     this.bootStart = System.nanoTime();
/* 154 */     LOGGER.at(Level.INFO).log("Starting HytaleServer");
/*     */     
/* 156 */     Constants.init();
/*     */     
/* 158 */     DataStoreProvider.CODEC.register("Disk", DiskDataStoreProvider.class, (Codec)DiskDataStoreProvider.CODEC);
/*     */ 
/*     */     
/* 161 */     LOGGER.at(Level.INFO).log("Loading config...");
/* 162 */     this.hytaleServerConfig = HytaleServerConfig.load();
/* 163 */     HytaleLoggerBackend.reloadLogLevels();
/*     */ 
/*     */     
/* 166 */     System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", HytaleForkJoinThreadFactory.class.getName());
/*     */     
/* 168 */     OptionSet optionSet = Options.getOptionSet();
/*     */ 
/*     */     
/* 171 */     LOGGER.at(Level.INFO).log("Authentication mode: %s", optionSet.valueOf(Options.AUTH_MODE));
/* 172 */     ServerAuthManager.getInstance().initialize();
/*     */     
/* 174 */     if (EarlyPluginLoader.hasTransformers()) {
/* 175 */       HytaleLogger.getLogger().at(Level.INFO).log("Early plugins loaded!! Disabling Sentry!!");
/* 176 */     } else if (!optionSet.has(Options.DISABLE_SENTRY)) {
/* 177 */       LOGGER.at(Level.INFO).log("Enabling Sentry");
/* 178 */       SentryOptions options = new SentryOptions();
/* 179 */       options.setDsn("https://6043a13c7b5c45b5c834b6d896fb378e@sentry.hytale.com/4");
/* 180 */       options.setRelease(ManifestUtil.getImplementationVersion());
/* 181 */       options.setDist(ManifestUtil.getImplementationRevisionId());
/* 182 */       options.setEnvironment("release");
/* 183 */       options.setTag("patchline", ManifestUtil.getPatchline());
/* 184 */       options.setServerName(NetworkUtil.getHostName());
/*     */       
/* 186 */       options.setBeforeSend((event, hint) -> {
/*     */             Throwable throwable = event.getThrowable();
/*     */             
/*     */             if (PluginClassLoader.isFromThirdPartyPlugin(throwable)) {
/*     */               return null;
/*     */             }
/*     */             
/*     */             Contexts contexts = event.getContexts();
/*     */             
/*     */             HashMap<String, Object> serverContext = new HashMap<>();
/*     */             
/*     */             serverContext.put("name", getServerName());
/*     */             
/*     */             serverContext.put("max-players", Integer.valueOf(getConfig().getMaxPlayers()));
/*     */             
/*     */             ServerManager serverManager = ServerManager.get();
/*     */             
/*     */             if (serverManager != null) {
/*     */               serverContext.put("listeners", serverManager.getListeners().stream().map(Object::toString).toList());
/*     */             }
/*     */             
/*     */             contexts.put("server", serverContext);
/*     */             
/*     */             Universe universe = Universe.get();
/*     */             
/*     */             if (universe != null) {
/*     */               HashMap<String, Object> universeContext = new HashMap<>();
/*     */               
/*     */               universeContext.put("path", universe.getPath().toString());
/*     */               
/*     */               universeContext.put("player-count", Integer.valueOf(universe.getPlayerCount()));
/*     */               universeContext.put("worlds", universe.getWorlds().keySet().stream().toList());
/*     */               contexts.put("universe", universeContext);
/*     */             } 
/*     */             HashMap<String, Object> pluginsContext = new HashMap<>();
/*     */             for (PluginBase plugin : this.pluginManager.getPlugins()) {
/*     */               PluginManifest manifest = plugin.getManifest();
/*     */               HashMap<String, Object> pluginInfo = new HashMap<>();
/*     */               pluginInfo.put("version", manifest.getVersion().toString());
/*     */               pluginInfo.put("state", plugin.getState().name());
/*     */               pluginsContext.put(plugin.getIdentifier().toString(), pluginInfo);
/*     */             } 
/*     */             contexts.put("plugins", pluginsContext);
/*     */             AssetModule assetModule = AssetModule.get();
/*     */             if (assetModule != null) {
/*     */               HashMap<String, Object> packsContext = new HashMap<>();
/*     */               for (AssetPack pack : assetModule.getAssetPacks()) {
/*     */                 HashMap<String, Object> packInfo = new HashMap<>();
/*     */                 PluginManifest manifest = pack.getManifest();
/*     */                 if (manifest != null && manifest.getVersion() != null) {
/*     */                   packInfo.put("version", manifest.getVersion().toString());
/*     */                 }
/*     */                 packInfo.put("immutable", Boolean.valueOf(pack.isImmutable()));
/*     */                 packsContext.put(pack.getName(), packInfo);
/*     */               } 
/*     */               contexts.put("packs", packsContext);
/*     */             } 
/*     */             User user = new User();
/*     */             HashMap<String, Object> unknown = new HashMap<>();
/*     */             user.setUnknown(unknown);
/*     */             UUID hardwareUUID = HardwareUtil.getUUID();
/*     */             if (hardwareUUID != null) {
/*     */               unknown.put("hardware-uuid", hardwareUUID.toString());
/*     */             }
/*     */             ServerAuthManager authManager = ServerAuthManager.getInstance();
/*     */             unknown.put("auth-mode", authManager.getAuthMode().toString());
/*     */             SessionServiceClient.GameProfile profile = authManager.getSelectedProfile();
/*     */             if (profile != null) {
/*     */               user.setUsername(profile.username);
/*     */               user.setId(profile.uuid.toString());
/*     */             } 
/*     */             user.setIpAddress("{{auto}}");
/*     */             event.setUser(user);
/*     */             return event;
/*     */           });
/* 261 */       Sentry.init(options);
/*     */       
/* 263 */       Sentry.configureScope(scope -> {
/*     */             UUID hardwareUUID = HardwareUtil.getUUID();
/*     */ 
/*     */             
/*     */             if (hardwareUUID != null) {
/*     */               scope.setContexts("hardware", Map.of("uuid", hardwareUUID.toString()));
/*     */             }
/*     */ 
/*     */             
/*     */             OperatingSystem os = new OperatingSystem();
/*     */ 
/*     */             
/*     */             os.setName(System.getProperty("os.name"));
/*     */ 
/*     */             
/*     */             os.setVersion(System.getProperty("os.version"));
/*     */             
/*     */             scope.getContexts().setOperatingSystem(os);
/*     */             
/*     */             scope.setContexts("build", Map.of("version", String.valueOf(ManifestUtil.getImplementationVersion()), "revision-id", String.valueOf(ManifestUtil.getImplementationRevisionId()), "patchline", String.valueOf(ManifestUtil.getPatchline()), "environment", "release"));
/*     */             
/*     */             if (Constants.SINGLEPLAYER) {
/*     */               scope.setContexts("singleplayer", Map.of("owner-uuid", String.valueOf(SingleplayerModule.getUuid()), "owner-name", SingleplayerModule.getUsername()));
/*     */             }
/*     */           });
/*     */       
/* 289 */       HytaleLogger.getLogger().setSentryClient(Sentry.getCurrentScopes());
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 295 */     ServerAuthManager.getInstance().checkPendingFatalError();
/*     */ 
/*     */     
/* 298 */     NettyUtil.init();
/*     */ 
/*     */     
/* 301 */     float sin = TrigMathUtil.sin(0.0F);
/* 302 */     float atan2 = TrigMathUtil.atan2(0.0F, 0.0F);
/*     */ 
/*     */     
/* 305 */     Thread shutdownHook = new Thread(() -> { if (this.shutdown.getAndSet(ShutdownReason.SIGINT) != null) return;  shutdown0(ShutdownReason.SIGINT); }"ShutdownHook");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 310 */     shutdownHook.setDaemon(false);
/* 311 */     Runtime.getRuntime().addShutdownHook(shutdownHook);
/*     */     
/* 313 */     AssetRegistryLoader.init();
/*     */ 
/*     */     
/* 316 */     for (PluginManifest manifest : Constants.CORE_PLUGINS) {
/* 317 */       this.pluginManager.registerCorePlugin(manifest);
/*     */     }
/*     */     
/* 320 */     GCUtil.register(info -> {
/*     */           Universe universe = Universe.get();
/*     */           
/*     */           if (universe == null) {
/*     */             return;
/*     */           }
/*     */           
/*     */           for (World world : universe.getWorlds().values()) {
/*     */             world.markGCHasRun();
/*     */           }
/*     */         });
/* 331 */     boot();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public EventBus getEventBus() {
/* 336 */     return this.eventBus;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public PluginManager getPluginManager() {
/* 341 */     return this.pluginManager;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CommandManager getCommandManager() {
/* 346 */     return this.commandManager;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public HytaleServerConfig getConfig() {
/* 351 */     return this.hytaleServerConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void boot() {
/* 359 */     if (this.booting.getAndSet(true))
/*     */       return; 
/* 361 */     LOGGER.at(Level.INFO).log("Booting up HytaleServer - Version: " + 
/* 362 */         ManifestUtil.getImplementationVersion() + ", Revision: " + 
/* 363 */         ManifestUtil.getImplementationRevisionId());
/*     */     
/*     */     try {
/* 366 */       this.pluginsProgress = 0;
/* 367 */       sendSingleplayerProgress();
/* 368 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 371 */       LOGGER.at(Level.INFO).log("Setup phase...");
/* 372 */       this.commandManager.registerCommands();
/* 373 */       this.pluginManager.setup();
/*     */ 
/*     */       
/* 376 */       ServerAuthManager.getInstance().initializeCredentialStore();
/*     */       
/* 378 */       LOGGER.at(Level.INFO).log("Setup phase completed! Boot time %s", FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/* 379 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 382 */       LoadAssetEvent loadAssetEvent = (LoadAssetEvent)get().getEventBus().dispatchFor(LoadAssetEvent.class).dispatch((IBaseEvent)new LoadAssetEvent(this.bootStart));
/* 383 */       if (isShuttingDown())
/*     */         return; 
/* 385 */       if (loadAssetEvent.isShouldShutdown()) {
/* 386 */         List<String> reasons = loadAssetEvent.getReasons();
/* 387 */         String join = String.join(", ", (Iterable)reasons);
/* 388 */         LOGGER.at(Level.SEVERE).log("Asset validation FAILED with %d reason(s): %s", reasons.size(), join);
/* 389 */         shutdownServer(ShutdownReason.VALIDATE_ERROR.withMessage(join));
/*     */         
/*     */         return;
/*     */       } 
/* 393 */       if (Options.getOptionSet().has(Options.SHUTDOWN_AFTER_VALIDATE)) {
/* 394 */         LOGGER.at(Level.INFO).log("Asset validation passed");
/* 395 */         shutdownServer(ShutdownReason.SHUTDOWN);
/*     */         
/*     */         return;
/*     */       } 
/* 399 */       this.pluginsProgress = 0;
/* 400 */       sendSingleplayerProgress();
/* 401 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 404 */       LOGGER.at(Level.INFO).log("Starting plugin manager...");
/* 405 */       this.pluginManager.start();
/* 406 */       LOGGER.at(Level.INFO).log("Plugin manager started! Startup time so far: %s", FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/* 407 */       if (isShuttingDown())
/*     */         return; 
/* 409 */       sendSingleplayerSignal("-=|Enabled|0");
/* 410 */     } catch (Throwable throwable) {
/* 411 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to boot HytaleServer!");
/*     */       
/* 413 */       Throwable t = throwable;
/* 414 */       for (; t.getCause() != null; t = t.getCause());
/* 415 */       shutdownServer(ShutdownReason.CRASH.withMessage("Failed to start server! " + t.getMessage()));
/*     */     } 
/*     */ 
/*     */     
/* 419 */     if (this.hytaleServerConfig.consumeHasChanged()) {
/* 420 */       HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */     }
/*     */ 
/*     */     
/* 424 */     SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
/*     */           try {
/*     */             if (this.hytaleServerConfig.consumeHasChanged()) {
/*     */               HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */             }
/* 429 */           } catch (Exception e) {
/*     */             ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to save server config!");
/*     */           } 
/*     */         }1L, 1L, TimeUnit.MINUTES);
/*     */     
/* 434 */     LOGGER.at(Level.INFO).log("Getting Hytale Universe ready...");
/*     */ 
/*     */     
/* 437 */     Universe.get().getUniverseReady().join();
/* 438 */     LOGGER.at(Level.INFO).log("Universe ready!");
/*     */     
/* 440 */     ObjectArrayList<String> objectArrayList = new ObjectArrayList();
/* 441 */     if (Constants.SINGLEPLAYER) {
/* 442 */       objectArrayList.add("Singleplayer");
/*     */     } else {
/* 444 */       objectArrayList.add("Multiplayer");
/*     */     } 
/* 446 */     if (Constants.FRESH_UNIVERSE) {
/* 447 */       objectArrayList.add("Fresh Universe");
/*     */     }
/*     */     
/* 450 */     this.booted.set(true);
/* 451 */     ServerManager.get().waitForBindComplete();
/* 452 */     this.eventBus.dispatch(BootEvent.class);
/*     */ 
/*     */     
/* 455 */     List<String> bootCommands = Options.getOptionSet().valuesOf(Options.BOOT_COMMAND);
/* 456 */     if (!bootCommands.isEmpty()) {
/* 457 */       CommandManager.get().handleCommands((CommandSender)ConsoleSender.INSTANCE, new ArrayDeque<>(bootCommands)).join();
/*     */     }
/*     */     
/* 460 */     String border = "\033[0;32m===============================================================================================";
/* 461 */     LOGGER.at(Level.INFO).log("\033[0;32m===============================================================================================");
/* 462 */     LOGGER.at(Level.INFO).log("%s         Hytale Server Booted! [%s] took %s", "\033[0;32m", 
/*     */         
/* 464 */         String.join(", ", (Iterable)objectArrayList), 
/* 465 */         FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/*     */     
/* 467 */     LOGGER.at(Level.INFO).log("\033[0;32m===============================================================================================");
/*     */ 
/*     */     
/* 470 */     UpdateModule updateModule = UpdateModule.get();
/* 471 */     if (updateModule != null) {
/* 472 */       updateModule.onServerReady();
/*     */     }
/*     */     
/* 475 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/* 476 */     if (!authManager.isSingleplayer() && authManager.getAuthMode() == ServerAuthManager.AuthMode.NONE) {
/* 477 */       LOGGER.at(Level.WARNING).log("%sNo server tokens configured. Use /auth login to authenticate.", "\033[0;31m");
/*     */     }
/*     */     
/* 480 */     sendSingleplayerSignal(">> Singleplayer Ready <<");
/*     */   }
/*     */   
/*     */   public void shutdownServer() {
/* 484 */     shutdownServer(ShutdownReason.SHUTDOWN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownServer(@Nonnull ShutdownReason reason) {
/* 493 */     Objects.requireNonNull(reason, "Server shutdown reason can't be null!");
/*     */ 
/*     */     
/* 496 */     if (this.shutdown.getAndSet(reason) != null)
/* 497 */       return;  if (reason.getMessage() != null) sendSingleplayerSignal("-=|Shutdown|" + reason.getMessage());
/*     */     
/* 499 */     Thread shutdownThread = new Thread(() -> shutdown0(reason), "ShutdownThread");
/* 500 */     shutdownThread.setDaemon(false);
/* 501 */     shutdownThread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void shutdown0(@Nonnull ShutdownReason reason) {
/* 509 */     LOGGER.at(Level.INFO).log("Shutdown triggered!!!");
/*     */     
/*     */     try {
/* 512 */       LOGGER.at(Level.INFO).log("Shutting down... %d  '%s'", reason.getExitCode(), reason.getMessage());
/*     */ 
/*     */       
/* 515 */       this.eventBus.dispatch(ShutdownEvent.class);
/* 516 */       this.pluginManager.shutdown();
/* 517 */       this.commandManager.shutdown();
/* 518 */       this.eventBus.shutdown();
/* 519 */       ServerAuthManager.getInstance().shutdown();
/*     */ 
/*     */       
/* 522 */       LOGGER.at(Level.INFO).log("Saving config...");
/* 523 */       if (this.hytaleServerConfig.consumeHasChanged()) {
/* 524 */         HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */       }
/*     */       
/* 527 */       LOGGER.at(Level.INFO).log("Shutdown completed!");
/* 528 */     } catch (Throwable t) {
/* 529 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(t)).log("Exception while shutting down:");
/*     */     } 
/*     */ 
/*     */     
/* 533 */     this.aliveLock.release();
/*     */     
/* 535 */     HytaleLogManager.resetFinally();
/*     */ 
/*     */     
/* 538 */     SCHEDULED_EXECUTOR.schedule(() -> { LOGGER.at(Level.SEVERE).log("Forcing shutdown!"); Runtime.getRuntime().halt(reason.getExitCode()); }3L, TimeUnit.SECONDS);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 544 */     if (reason != ShutdownReason.SIGINT) System.exit(reason.getExitCode()); 
/*     */   }
/*     */   
/*     */   public void doneSetup(PluginBase plugin) {
/* 548 */     this.pluginsProgress++;
/* 549 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void doneStart(PluginBase plugin) {
/* 553 */     this.pluginsProgress++;
/* 554 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void doneStop(PluginBase plugin) {
/* 558 */     this.pluginsProgress--;
/* 559 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void sendSingleplayerProgress() {
/* 563 */     List<PluginBase> plugins = this.pluginManager.getPlugins();
/* 564 */     if (this.shutdown.get() != null) {
/* 565 */       sendSingleplayerSignal("-=|Shutdown Modules|" + MathUtil.round((plugins.size() - this.pluginsProgress) / plugins.size(), 2) * 100.0D);
/* 566 */     } else if (this.pluginManager.getState() == PluginState.SETUP) {
/* 567 */       sendSingleplayerSignal("-=|Setup|" + MathUtil.round(this.pluginsProgress / plugins.size(), 2) * 100.0D);
/* 568 */     } else if (this.pluginManager.getState() == PluginState.START) {
/* 569 */       sendSingleplayerSignal("-=|Starting|" + MathUtil.round(this.pluginsProgress / plugins.size(), 2) * 100.0D);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 574 */     return getConfig().getServerName();
/*     */   }
/*     */   
/*     */   public boolean isBooting() {
/* 578 */     return this.booting.get();
/*     */   }
/*     */   
/*     */   public boolean isBooted() {
/* 582 */     return this.booted.get();
/*     */   }
/*     */   
/*     */   public boolean isShuttingDown() {
/* 586 */     return (this.shutdown.get() != null);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Instant getBoot() {
/* 591 */     return this.boot;
/*     */   }
/*     */   
/*     */   public long getBootStart() {
/* 595 */     return this.bootStart;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ShutdownReason getShutdownReason() {
/* 600 */     return this.shutdown.get();
/*     */   }
/*     */   
/*     */   private void sendSingleplayerSignal(String message) {
/* 604 */     if (Constants.SINGLEPLAYER) {
/* 605 */       HytaleLoggerBackend.rawLog(message);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportSingleplayerStatus(String message) {
/* 610 */     if (Constants.SINGLEPLAYER) {
/* 611 */       HytaleLoggerBackend.rawLog("-=|" + message + "|0");
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportSaveProgress(@Nonnull World world, int saved, int total) {
/* 616 */     if (!isShuttingDown())
/* 617 */       return;  double progress = MathUtil.round(saved / total, 2) * 100.0D;
/* 618 */     if (Constants.SINGLEPLAYER) {
/* 619 */       sendSingleplayerSignal("-=|Saving world " + world.getName() + " chunks|" + progress);
/*     */     
/*     */     }
/* 622 */     else if (total < 10 || saved % total / 10 == 0) {
/* 623 */       world.getLogger().at(Level.INFO).log("Saving chunks: %.0f%%", Double.valueOf(progress));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static HytaleServer get() {
/* 634 */     return instance;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\HytaleServer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */