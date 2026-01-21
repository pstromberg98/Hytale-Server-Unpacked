/*     */ package com.hypixel.hytale.server.core;
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
/*     */ import com.hypixel.hytale.server.core.util.concurrent.ThreadUtil;
/*     */ import com.sun.management.GarbageCollectionNotificationInfo;
/*     */ import io.netty.handler.codec.quic.Quic;
/*     */ import io.sentry.Hint;
/*     */ import io.sentry.IScope;
/*     */ import io.sentry.Sentry;
/*     */ import io.sentry.SentryEvent;
/*     */ import io.sentry.SentryOptions;
/*     */ import io.sentry.protocol.Contexts;
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
/*     */   public static final int DEFAULT_PORT = 5520;
/*  71 */   public static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newSingleThreadScheduledExecutor(ThreadUtil.daemon("Scheduler"));
/*     */ 
/*     */ 
/*     */ 
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
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  90 */     METRICS_REGISTRY = (new MetricsRegistry()).register("Time", server -> Instant.now(), (Codec)Codec.INSTANT).register("Boot", server -> server.boot, (Codec)Codec.INSTANT).register("BootStart", server -> Long.valueOf(server.bootStart), (Codec)Codec.LONG).register("Booting", server -> Boolean.valueOf(server.booting.get()), (Codec)Codec.BOOLEAN).register("ShutdownReason", server -> { ShutdownReason reason = server.shutdown.get(); return (Function)((reason == null) ? null : reason.toString()); }(Codec)Codec.STRING).register("PluginManager", HytaleServer::getPluginManager, (Codec)PluginManager.METRICS_REGISTRY).register("Config", HytaleServer::getConfig, (Codec)HytaleServerConfig.CODEC).register("JVM", JVMMetrics.METRICS_REGISTRY);
/*     */   }
/*  92 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   private static HytaleServer instance;
/*     */ 
/*     */ 
/*     */   
/* 100 */   private final Semaphore aliveLock = new Semaphore(0);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 106 */   private final AtomicBoolean booting = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   private final AtomicBoolean booted = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 118 */   private final AtomicReference<ShutdownReason> shutdown = new AtomicReference<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 123 */   private final EventBus eventBus = new EventBus(Options.getOptionSet().has(Options.EVENT_DEBUG));
/* 124 */   private final PluginManager pluginManager = new PluginManager();
/* 125 */   private final CommandManager commandManager = new CommandManager();
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
/* 140 */     instance = this;
/*     */     
/* 142 */     Quic.ensureAvailability();
/*     */     
/* 144 */     HytaleLoggerBackend.setIndent(25);
/*     */     
/* 146 */     ThreadUtil.forceTimeHighResolution();
/* 147 */     ThreadUtil.createKeepAliveThread(this.aliveLock);
/*     */     
/* 149 */     this.boot = Instant.now();
/* 150 */     this.bootStart = System.nanoTime();
/* 151 */     LOGGER.at(Level.INFO).log("Starting HytaleServer");
/*     */     
/* 153 */     Constants.init();
/*     */     
/* 155 */     DataStoreProvider.CODEC.register("Disk", DiskDataStoreProvider.class, (Codec)DiskDataStoreProvider.CODEC);
/*     */ 
/*     */     
/* 158 */     LOGGER.at(Level.INFO).log("Loading config...");
/* 159 */     this.hytaleServerConfig = HytaleServerConfig.load();
/* 160 */     HytaleLoggerBackend.reloadLogLevels();
/*     */ 
/*     */     
/* 163 */     System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory", HytaleForkJoinThreadFactory.class.getName());
/*     */     
/* 165 */     OptionSet optionSet = Options.getOptionSet();
/*     */ 
/*     */     
/* 168 */     LOGGER.at(Level.INFO).log("Authentication mode: %s", optionSet.valueOf(Options.AUTH_MODE));
/* 169 */     ServerAuthManager.getInstance().initialize();
/*     */     
/* 171 */     if (EarlyPluginLoader.hasTransformers()) {
/* 172 */       HytaleLogger.getLogger().at(Level.INFO).log("Early plugins loaded!! Disabling Sentry!!");
/* 173 */     } else if (!optionSet.has(Options.DISABLE_SENTRY)) {
/* 174 */       LOGGER.at(Level.INFO).log("Enabling Sentry");
/* 175 */       SentryOptions options = new SentryOptions();
/* 176 */       options.setDsn("https://6043a13c7b5c45b5c834b6d896fb378e@sentry.hytale.com/4");
/* 177 */       options.setRelease(ManifestUtil.getImplementationVersion());
/* 178 */       options.setDist(ManifestUtil.getImplementationRevisionId());
/* 179 */       options.setEnvironment("release");
/* 180 */       options.setTag("patchline", ManifestUtil.getPatchline());
/* 181 */       options.setServerName(NetworkUtil.getHostName());
/*     */       
/* 183 */       options.setBeforeSend((event, hint) -> {
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
/* 243 */       Sentry.init(options);
/*     */       
/* 245 */       Sentry.configureScope(scope -> {
/*     */             UUID hardwareUUID = HardwareUtil.getUUID();
/*     */ 
/*     */ 
/*     */             
/*     */             if (hardwareUUID != null) {
/*     */               scope.setContexts("hardware", Map.of("uuid", hardwareUUID.toString()));
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             scope.setContexts("build", Map.of("version", String.valueOf(ManifestUtil.getImplementationVersion()), "revision-id", String.valueOf(ManifestUtil.getImplementationRevisionId()), "patchline", String.valueOf(ManifestUtil.getPatchline()), "environment", "release"));
/*     */ 
/*     */ 
/*     */             
/*     */             if (Constants.SINGLEPLAYER) {
/*     */               scope.setContexts("singleplayer", Map.of("owner-uuid", String.valueOf(SingleplayerModule.getUuid()), "owner-name", SingleplayerModule.getUsername()));
/*     */             }
/*     */           });
/*     */ 
/*     */       
/* 266 */       HytaleLogger.getLogger().setSentryClient(Sentry.getCurrentScopes());
/*     */     } 
/*     */ 
/*     */     
/* 270 */     NettyUtil.init();
/*     */ 
/*     */     
/* 273 */     float sin = TrigMathUtil.sin(0.0F);
/* 274 */     float atan2 = TrigMathUtil.atan2(0.0F, 0.0F);
/*     */ 
/*     */     
/* 277 */     Thread shutdownHook = new Thread(() -> { if (this.shutdown.getAndSet(ShutdownReason.SIGINT) != null) return;  shutdown0(ShutdownReason.SIGINT); }"ShutdownHook");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 282 */     shutdownHook.setDaemon(false);
/* 283 */     Runtime.getRuntime().addShutdownHook(shutdownHook);
/*     */     
/* 285 */     AssetRegistryLoader.init();
/*     */ 
/*     */     
/* 288 */     for (PluginManifest manifest : Constants.CORE_PLUGINS) {
/* 289 */       this.pluginManager.registerCorePlugin(manifest);
/*     */     }
/*     */     
/* 292 */     GCUtil.register(info -> {
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
/* 303 */     boot();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public EventBus getEventBus() {
/* 308 */     return this.eventBus;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public PluginManager getPluginManager() {
/* 313 */     return this.pluginManager;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CommandManager getCommandManager() {
/* 318 */     return this.commandManager;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public HytaleServerConfig getConfig() {
/* 323 */     return this.hytaleServerConfig;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void boot() {
/* 331 */     if (this.booting.getAndSet(true))
/*     */       return; 
/* 333 */     LOGGER.at(Level.INFO).log("Booting up HytaleServer - Version: " + 
/* 334 */         ManifestUtil.getImplementationVersion() + ", Revision: " + 
/* 335 */         ManifestUtil.getImplementationRevisionId());
/*     */     
/*     */     try {
/* 338 */       this.pluginsProgress = 0;
/* 339 */       sendSingleplayerProgress();
/* 340 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 343 */       LOGGER.at(Level.INFO).log("Setup phase...");
/* 344 */       this.commandManager.registerCommands();
/* 345 */       this.pluginManager.setup();
/*     */ 
/*     */       
/* 348 */       ServerAuthManager.getInstance().initializeCredentialStore();
/*     */       
/* 350 */       LOGGER.at(Level.INFO).log("Setup phase completed! Boot time %s", FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/* 351 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 354 */       LoadAssetEvent loadAssetEvent = (LoadAssetEvent)get().getEventBus().dispatchFor(LoadAssetEvent.class).dispatch((IBaseEvent)new LoadAssetEvent(this.bootStart));
/* 355 */       if (isShuttingDown())
/*     */         return; 
/* 357 */       if (loadAssetEvent.isShouldShutdown()) {
/* 358 */         List<String> reasons = loadAssetEvent.getReasons();
/* 359 */         String join = String.join(", ", (Iterable)reasons);
/* 360 */         LOGGER.at(Level.SEVERE).log("Asset validation FAILED with %d reason(s): %s", reasons.size(), join);
/* 361 */         shutdownServer(ShutdownReason.VALIDATE_ERROR.withMessage(join));
/*     */         
/*     */         return;
/*     */       } 
/* 365 */       if (Options.getOptionSet().has(Options.SHUTDOWN_AFTER_VALIDATE)) {
/* 366 */         LOGGER.at(Level.INFO).log("Asset validation passed");
/* 367 */         shutdownServer(ShutdownReason.SHUTDOWN);
/*     */         
/*     */         return;
/*     */       } 
/* 371 */       this.pluginsProgress = 0;
/* 372 */       sendSingleplayerProgress();
/* 373 */       if (isShuttingDown()) {
/*     */         return;
/*     */       }
/* 376 */       LOGGER.at(Level.INFO).log("Starting plugin manager...");
/* 377 */       this.pluginManager.start();
/* 378 */       LOGGER.at(Level.INFO).log("Plugin manager started! Startup time so far: %s", FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/* 379 */       if (isShuttingDown())
/*     */         return; 
/* 381 */       sendSingleplayerSignal("-=|Enabled|0");
/* 382 */     } catch (Throwable throwable) {
/* 383 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Failed to boot HytaleServer!");
/*     */       
/* 385 */       Throwable t = throwable;
/* 386 */       for (; t.getCause() != null; t = t.getCause());
/* 387 */       shutdownServer(ShutdownReason.CRASH.withMessage("Failed to start server! " + t.getMessage()));
/*     */     } 
/*     */ 
/*     */     
/* 391 */     if (this.hytaleServerConfig.consumeHasChanged()) {
/* 392 */       HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */     }
/*     */ 
/*     */     
/* 396 */     SCHEDULED_EXECUTOR.scheduleWithFixedDelay(() -> {
/*     */           try {
/*     */             if (this.hytaleServerConfig.consumeHasChanged()) {
/*     */               HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */             }
/* 401 */           } catch (Exception e) {
/*     */             ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to save server config!");
/*     */           } 
/*     */         }1L, 1L, TimeUnit.MINUTES);
/*     */     
/* 406 */     LOGGER.at(Level.INFO).log("Getting Hytale Universe ready...");
/*     */ 
/*     */     
/* 409 */     Universe.get().getUniverseReady().join();
/* 410 */     LOGGER.at(Level.INFO).log("Universe ready!");
/*     */     
/* 412 */     ObjectArrayList<String> objectArrayList = new ObjectArrayList();
/* 413 */     if (Constants.SINGLEPLAYER) {
/* 414 */       objectArrayList.add("Singleplayer");
/*     */     } else {
/* 416 */       objectArrayList.add("Multiplayer");
/*     */     } 
/* 418 */     if (Constants.FRESH_UNIVERSE) {
/* 419 */       objectArrayList.add("Fresh Universe");
/*     */     }
/*     */     
/* 422 */     this.booted.set(true);
/* 423 */     ServerManager.get().waitForBindComplete();
/* 424 */     this.eventBus.dispatch(BootEvent.class);
/*     */ 
/*     */     
/* 427 */     List<String> bootCommands = Options.getOptionSet().valuesOf(Options.BOOT_COMMAND);
/* 428 */     if (!bootCommands.isEmpty()) {
/* 429 */       CommandManager.get().handleCommands((CommandSender)ConsoleSender.INSTANCE, new ArrayDeque<>(bootCommands)).join();
/*     */     }
/*     */     
/* 432 */     String border = "\033[0;32m===============================================================================================";
/* 433 */     LOGGER.at(Level.INFO).log("\033[0;32m===============================================================================================");
/* 434 */     LOGGER.at(Level.INFO).log("%s         Hytale Server Booted! [%s] took %s", "\033[0;32m", 
/*     */         
/* 436 */         String.join(", ", (Iterable)objectArrayList), 
/* 437 */         FormatUtil.nanosToString(System.nanoTime() - this.bootStart));
/*     */     
/* 439 */     LOGGER.at(Level.INFO).log("\033[0;32m===============================================================================================");
/*     */     
/* 441 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/* 442 */     if (!authManager.isSingleplayer() && authManager.getAuthMode() == ServerAuthManager.AuthMode.NONE) {
/* 443 */       LOGGER.at(Level.WARNING).log("%sNo server tokens configured. Use /auth login to authenticate.", "\033[0;31m");
/*     */     }
/*     */     
/* 446 */     sendSingleplayerSignal(">> Singleplayer Ready <<");
/*     */   }
/*     */   
/*     */   public void shutdownServer() {
/* 450 */     shutdownServer(ShutdownReason.SHUTDOWN);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void shutdownServer(@Nonnull ShutdownReason reason) {
/* 459 */     Objects.requireNonNull(reason, "Server shutdown reason can't be null!");
/*     */ 
/*     */     
/* 462 */     if (this.shutdown.getAndSet(reason) != null)
/* 463 */       return;  if (reason.getMessage() != null) sendSingleplayerSignal("-=|Shutdown|" + reason.getMessage());
/*     */     
/* 465 */     Thread shutdownThread = new Thread(() -> shutdown0(reason), "ShutdownThread");
/* 466 */     shutdownThread.setDaemon(false);
/* 467 */     shutdownThread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void shutdown0(@Nonnull ShutdownReason reason) {
/* 475 */     LOGGER.at(Level.INFO).log("Shutdown triggered!!!");
/*     */     
/*     */     try {
/* 478 */       LOGGER.at(Level.INFO).log("Shutting down... %d  '%s'", reason.getExitCode(), reason.getMessage());
/*     */ 
/*     */       
/* 481 */       this.eventBus.dispatch(ShutdownEvent.class);
/* 482 */       this.pluginManager.shutdown();
/* 483 */       this.commandManager.shutdown();
/* 484 */       this.eventBus.shutdown();
/* 485 */       ServerAuthManager.getInstance().shutdown();
/*     */ 
/*     */       
/* 488 */       LOGGER.at(Level.INFO).log("Saving config...");
/* 489 */       if (this.hytaleServerConfig.consumeHasChanged()) {
/* 490 */         HytaleServerConfig.save(this.hytaleServerConfig).join();
/*     */       }
/*     */       
/* 493 */       LOGGER.at(Level.INFO).log("Shutdown completed!");
/* 494 */     } catch (Throwable t) {
/* 495 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(t)).log("Exception while shutting down:");
/*     */     } 
/*     */ 
/*     */     
/* 499 */     this.aliveLock.release();
/*     */     
/* 501 */     HytaleLogManager.resetFinally();
/*     */ 
/*     */     
/* 504 */     SCHEDULED_EXECUTOR.schedule(() -> { LOGGER.at(Level.SEVERE).log("Forcing shutdown!"); Runtime.getRuntime().halt(reason.getExitCode()); }3L, TimeUnit.SECONDS);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 510 */     if (reason != ShutdownReason.SIGINT) System.exit(reason.getExitCode()); 
/*     */   }
/*     */   
/*     */   public void doneSetup(PluginBase plugin) {
/* 514 */     this.pluginsProgress++;
/* 515 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void doneStart(PluginBase plugin) {
/* 519 */     this.pluginsProgress++;
/* 520 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void doneStop(PluginBase plugin) {
/* 524 */     this.pluginsProgress--;
/* 525 */     sendSingleplayerProgress();
/*     */   }
/*     */   
/*     */   public void sendSingleplayerProgress() {
/* 529 */     List<PluginBase> plugins = this.pluginManager.getPlugins();
/* 530 */     if (this.shutdown.get() != null) {
/* 531 */       sendSingleplayerSignal("-=|Shutdown Modules|" + MathUtil.round((plugins.size() - this.pluginsProgress) / plugins.size(), 2) * 100.0D);
/* 532 */     } else if (this.pluginManager.getState() == PluginState.SETUP) {
/* 533 */       sendSingleplayerSignal("-=|Setup|" + MathUtil.round(this.pluginsProgress / plugins.size(), 2) * 100.0D);
/* 534 */     } else if (this.pluginManager.getState() == PluginState.START) {
/* 535 */       sendSingleplayerSignal("-=|Starting|" + MathUtil.round(this.pluginsProgress / plugins.size(), 2) * 100.0D);
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getServerName() {
/* 540 */     return getConfig().getServerName();
/*     */   }
/*     */   
/*     */   public boolean isBooting() {
/* 544 */     return this.booting.get();
/*     */   }
/*     */   
/*     */   public boolean isBooted() {
/* 548 */     return this.booted.get();
/*     */   }
/*     */   
/*     */   public boolean isShuttingDown() {
/* 552 */     return (this.shutdown.get() != null);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Instant getBoot() {
/* 557 */     return this.boot;
/*     */   }
/*     */   
/*     */   public long getBootStart() {
/* 561 */     return this.bootStart;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ShutdownReason getShutdownReason() {
/* 566 */     return this.shutdown.get();
/*     */   }
/*     */   
/*     */   private void sendSingleplayerSignal(String message) {
/* 570 */     if (Constants.SINGLEPLAYER) {
/* 571 */       HytaleLoggerBackend.rawLog(message);
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportSingleplayerStatus(String message) {
/* 576 */     if (Constants.SINGLEPLAYER) {
/* 577 */       HytaleLoggerBackend.rawLog("-=|" + message + "|0");
/*     */     }
/*     */   }
/*     */   
/*     */   public void reportSaveProgress(@Nonnull World world, int saved, int total) {
/* 582 */     if (!isShuttingDown())
/* 583 */       return;  double progress = MathUtil.round(saved / total, 2) * 100.0D;
/* 584 */     if (Constants.SINGLEPLAYER) {
/* 585 */       sendSingleplayerSignal("-=|Saving world " + world.getName() + " chunks|" + progress);
/*     */     
/*     */     }
/* 588 */     else if (total < 10 || saved % total / 10 == 0) {
/* 589 */       world.getLogger().at(Level.INFO).log("Saving chunks: %.0f%%", Double.valueOf(progress));
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
/* 600 */     return instance;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\HytaleServer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */