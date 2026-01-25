/*     */ package com.hypixel.hytale.server.core.update;
/*     */ 
/*     */ import com.hypixel.hytale.common.plugin.PluginManifest;
/*     */ import com.hypixel.hytale.common.util.SystemUtil;
/*     */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.Constants;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.ShutdownReason;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPlugin;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.update.command.UpdateCommand;
/*     */ import java.awt.Color;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class UpdateModule
/*     */   extends JavaPlugin
/*     */ {
/*  49 */   public static final PluginManifest MANIFEST = PluginManifest.corePlugin(UpdateModule.class)
/*  50 */     .build();
/*     */   
/*  52 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*  54 */   public static final boolean KILL_SWITCH_ENABLED = SystemUtil.getEnvBoolean("HYTALE_DISABLE_UPDATES");
/*     */   
/*     */   private static UpdateModule instance;
/*     */   
/*     */   private final ScheduledExecutorService scheduler;
/*     */   
/*     */   @Nullable
/*     */   private ScheduledFuture<?> updateCheckTask;
/*     */   
/*     */   @Nullable
/*     */   private ScheduledFuture<?> autoApplyTask;
/*     */   
/*     */   private final AtomicReference<UpdateService.VersionManifest> latestKnownVersion;
/*     */   
/*     */   private final AtomicReference<CompletableFuture<?>> activeDownload;
/*     */   
/*     */   private final AtomicReference<Thread> activeDownloadThread;
/*     */   
/*     */   private final AtomicBoolean downloadLock;
/*     */   
/*     */   private final AtomicLong downloadStartTime;
/*     */   
/*     */   private final AtomicLong downloadedBytes;
/*     */   
/*     */   private final AtomicLong totalBytes;
/*     */   
/*     */   private final AtomicLong autoApplyScheduledTime;
/*     */   
/*     */   private final AtomicLong lastWarningTime;
/*     */   
/*     */   public UpdateModule(@Nonnull JavaPluginInit init) {
/*  85 */     super(init); this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> { Thread t = new Thread(r, "UpdateChecker"); t.setDaemon(true); return (ThreadFactory)t;
/*  86 */         }); this.latestKnownVersion = new AtomicReference<>(); this.activeDownload = new AtomicReference<>(); this.activeDownloadThread = new AtomicReference<>(); this.downloadLock = new AtomicBoolean(false); this.downloadStartTime = new AtomicLong(0L); this.downloadedBytes = new AtomicLong(0L); this.totalBytes = new AtomicLong(0L); this.autoApplyScheduledTime = new AtomicLong(0L); this.lastWarningTime = new AtomicLong(0L); instance = this;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static UpdateModule get() {
/*  91 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  96 */     if (KILL_SWITCH_ENABLED) {
/*  97 */       LOGGER.at(Level.INFO).log("Update commands disabled via HYTALE_DISABLE_UPDATES environment variable");
/*     */     }
/*  99 */     getCommandRegistry().registerCommand((AbstractCommand)new UpdateCommand());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void start() {
/* 104 */     if (KILL_SWITCH_ENABLED) {
/*     */       return;
/*     */     }
/* 107 */     String stagedVersion = UpdateService.getStagedVersion();
/* 108 */     if (stagedVersion != null) {
/* 109 */       logStagedUpdateWarning(stagedVersion, true);
/*     */       
/* 111 */       startAutoApplyTaskIfNeeded();
/*     */     } 
/*     */ 
/*     */     
/* 115 */     if (shouldEnableUpdateChecker()) {
/* 116 */       HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 117 */       int intervalSeconds = config.getCheckIntervalSeconds();
/*     */       
/* 119 */       LOGGER.at(Level.INFO).log("Update checker enabled (interval: %ds)", intervalSeconds);
/*     */       
/* 121 */       this.updateCheckTask = this.scheduler.scheduleAtFixedRate(this::performUpdateCheck, 60L, intervalSeconds, TimeUnit.SECONDS);
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
/*     */   private synchronized void startAutoApplyTaskIfNeeded() {
/* 134 */     if (this.autoApplyTask != null) {
/*     */       return;
/*     */     }
/*     */     
/* 138 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 139 */     HytaleServerConfig.UpdateConfig.AutoApplyMode autoApplyMode = config.getAutoApplyMode();
/*     */     
/* 141 */     if (autoApplyMode == HytaleServerConfig.UpdateConfig.AutoApplyMode.DISABLED) {
/*     */       return;
/*     */     }
/*     */     
/* 145 */     LOGGER.at(Level.INFO).log("Starting auto-apply task (mode: %s, delay: %d min)", autoApplyMode, config
/* 146 */         .getAutoApplyDelayMinutes());
/*     */     
/* 148 */     this.autoApplyTask = this.scheduler.scheduleAtFixedRate(this::performAutoApplyCheck, 0L, 60L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shutdown() {
/* 158 */     if (this.updateCheckTask != null) {
/* 159 */       this.updateCheckTask.cancel(false);
/*     */     }
/* 161 */     if (this.autoApplyTask != null) {
/* 162 */       this.autoApplyTask.cancel(false);
/*     */     }
/* 164 */     this.scheduler.shutdown();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void onServerReady() {
/* 171 */     if (KILL_SWITCH_ENABLED)
/*     */       return; 
/* 173 */     String stagedVersion = UpdateService.getStagedVersion();
/* 174 */     if (stagedVersion != null) {
/* 175 */       logStagedUpdateWarning(stagedVersion, false);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public UpdateService.VersionManifest getLatestKnownVersion() {
/* 184 */     return this.latestKnownVersion.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLatestKnownVersion(@Nullable UpdateService.VersionManifest version) {
/* 191 */     this.latestKnownVersion.set(version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDownloadInProgress() {
/* 198 */     return this.downloadLock.get();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean tryAcquireDownloadLock() {
/* 208 */     return this.downloadLock.compareAndSet(false, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setActiveDownload(@Nullable CompletableFuture<?> download, @Nullable Thread thread) {
/* 216 */     this.activeDownload.set(download);
/* 217 */     this.activeDownloadThread.set(thread);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void releaseDownloadLock() {
/* 224 */     this.activeDownload.set(null);
/* 225 */     this.activeDownloadThread.set(null);
/* 226 */     this.downloadLock.set(false);
/* 227 */     this.downloadStartTime.set(0L);
/* 228 */     this.downloadedBytes.set(0L);
/* 229 */     this.totalBytes.set(0L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateDownloadProgress(long downloaded, long total) {
/* 236 */     if (this.downloadStartTime.get() == 0L) {
/* 237 */       this.downloadStartTime.set(System.currentTimeMillis());
/*     */     }
/* 239 */     this.downloadedBytes.set(downloaded);
/* 240 */     this.totalBytes.set(total);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public DownloadProgress getDownloadProgress() {
/* 248 */     if (!this.downloadLock.get()) return null;
/*     */     
/* 250 */     long start = this.downloadStartTime.get();
/* 251 */     long downloaded = this.downloadedBytes.get();
/* 252 */     long total = this.totalBytes.get();
/*     */     
/* 254 */     if (start == 0L || total <= 0L) {
/* 255 */       return new DownloadProgress(0, 0L, total, -1L);
/*     */     }
/*     */     
/* 258 */     int percent = (int)(downloaded * 100L / total);
/* 259 */     long elapsed = System.currentTimeMillis() - start;
/* 260 */     long etaSeconds = -1L;
/*     */     
/* 262 */     if (elapsed > 0L && downloaded > 0L) {
/* 263 */       double bytesPerMs = downloaded / elapsed;
/* 264 */       long remaining = total - downloaded;
/* 265 */       etaSeconds = (long)(remaining / bytesPerMs / 1000.0D);
/*     */     } 
/*     */     
/* 268 */     return new DownloadProgress(percent, downloaded, total, etaSeconds);
/*     */   }
/*     */   public static final class DownloadProgress extends Record { private final int percent; private final long downloadedBytes;
/*     */     private final long totalBytes;
/*     */     private final long etaSeconds;
/*     */     
/* 274 */     public DownloadProgress(int percent, long downloadedBytes, long totalBytes, long etaSeconds) { this.percent = percent; this.downloadedBytes = downloadedBytes; this.totalBytes = totalBytes; this.etaSeconds = etaSeconds; } public final String toString() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #274	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/* 274 */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress; } public int percent() { return this.percent; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #274	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #274	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/update/UpdateModule$DownloadProgress;
/* 274 */       //   0	8	1	o	Ljava/lang/Object; } public long downloadedBytes() { return this.downloadedBytes; } public long totalBytes() { return this.totalBytes; } public long etaSeconds() { return this.etaSeconds; }
/*     */      }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean cancelDownload() {
/* 282 */     CompletableFuture<?> download = this.activeDownload.getAndSet(null);
/* 283 */     Thread thread = this.activeDownloadThread.getAndSet(null);
/* 284 */     if (thread != null) {
/* 285 */       thread.interrupt();
/*     */     }
/* 287 */     if (download == null && thread == null) return false;
/*     */     
/* 289 */     if (download != null) {
/* 290 */       download.cancel(true);
/*     */     }
/*     */ 
/*     */     
/* 294 */     releaseDownloadLock();
/* 295 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean shouldEnableUpdateChecker() {
/* 303 */     if (!ManifestUtil.isJar()) {
/* 304 */       LOGGER.at(Level.INFO).log("Update checker disabled: not running from JAR");
/* 305 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 309 */     if (Constants.SINGLEPLAYER) {
/* 310 */       LOGGER.at(Level.INFO).log("Update checker disabled: singleplayer mode");
/* 311 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 315 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 316 */     if (!config.isEnabled()) {
/* 317 */       LOGGER.at(Level.INFO).log("Update checker disabled: disabled in config");
/* 318 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 322 */     String manifestPatchline = ManifestUtil.getPatchline();
/* 323 */     String configPatchline = config.getPatchline();
/* 324 */     if ("dev".equals(manifestPatchline) && (configPatchline == null || configPatchline.isEmpty())) {
/* 325 */       LOGGER.at(Level.INFO).log("Update checker disabled: dev patchline (set Patchline in config to override)");
/* 326 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 330 */     if (!UpdateService.isValidUpdateLayout()) {
/* 331 */       LOGGER.at(Level.WARNING).log("Update checker disabled: invalid folder layout. Expected to run from Server/ with Assets.zip and start.sh/bat in parent directory.");
/*     */       
/* 333 */       return false;
/*     */     } 
/*     */     
/* 336 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void performUpdateCheck() {
/* 345 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/* 346 */     if (!authManager.hasSessionToken()) {
/* 347 */       LOGGER.at(Level.FINE).log("Not authenticated - skipping update check");
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 352 */     String stagedVersion = UpdateService.getStagedVersion();
/* 353 */     if (stagedVersion != null) {
/* 354 */       LOGGER.at(Level.FINE).log("Staged update already exists (%s) - skipping update check", stagedVersion);
/* 355 */       startAutoApplyTaskIfNeeded();
/*     */       return;
/*     */     } 
/* 358 */     if (isDownloadInProgress()) {
/* 359 */       LOGGER.at(Level.FINE).log("Download in progress - skipping update check");
/*     */       
/*     */       return;
/*     */     } 
/* 363 */     UpdateService updateService = new UpdateService();
/* 364 */     String patchline = UpdateService.getEffectivePatchline();
/*     */     
/* 366 */     updateService.checkForUpdate(patchline).thenAccept(manifest -> {
/*     */           if (manifest == null) {
/*     */             LOGGER.at(Level.FINE).log("Update check returned no result");
/*     */             return;
/*     */           } 
/*     */           setLatestKnownVersion(manifest);
/*     */           String currentVersion = ManifestUtil.getImplementationVersion();
/*     */           if (currentVersion != null && currentVersion.equals(manifest.version)) {
/*     */             LOGGER.at(Level.FINE).log("Already running latest version: %s", currentVersion);
/*     */             return;
/*     */           } 
/*     */           logUpdateAvailable(currentVersion, manifest.version);
/*     */           HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/*     */           if (config.isNotifyPlayersOnAvailable()) {
/*     */             notifyPlayers(manifest.version);
/*     */           }
/*     */           if (config.getAutoApplyMode() != HytaleServerConfig.UpdateConfig.AutoApplyMode.DISABLED) {
/*     */             LOGGER.at(Level.INFO).log("Auto-downloading update %s...", manifest.version);
/*     */             autoDownloadUpdate(updateService, manifest);
/*     */           } 
/*     */         });
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
/*     */   
/*     */   private void autoDownloadUpdate(@Nonnull UpdateService updateService, @Nonnull UpdateService.VersionManifest manifest) {
/* 402 */     if (UpdateService.getStagedVersion() != null || !tryAcquireDownloadLock()) {
/*     */       return;
/*     */     }
/*     */     
/* 406 */     UpdateService.DownloadTask downloadTask = updateService.downloadUpdate(manifest, UpdateService.getStagingDir(), (percent, downloaded, total) -> updateDownloadProgress(downloaded, total));
/*     */ 
/*     */     
/* 409 */     CompletableFuture<Boolean> downloadFuture = downloadTask.future().whenComplete((success, error) -> {
/*     */           releaseDownloadLock();
/*     */           
/*     */           if (Boolean.TRUE.equals(success)) {
/*     */             LOGGER.at(Level.INFO).log("Update %s downloaded and staged", manifest.version);
/*     */             
/*     */             startAutoApplyTaskIfNeeded();
/*     */           } else if (error instanceof java.util.concurrent.CancellationException) {
/*     */             LOGGER.at(Level.INFO).log("Download of update %s was cancelled", manifest.version);
/*     */             UpdateService.deleteStagedUpdate();
/*     */           } else {
/*     */             LOGGER.at(Level.WARNING).log("Failed to download update %s: %s", manifest.version, (error != null) ? error.getMessage() : "unknown error");
/*     */             UpdateService.deleteStagedUpdate();
/*     */           } 
/*     */         });
/* 424 */     setActiveDownload(downloadFuture, downloadTask.thread());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void performAutoApplyCheck() {
/* 432 */     String stagedVersion = UpdateService.getStagedVersion();
/* 433 */     if (stagedVersion == null) {
/*     */       
/* 435 */       if (this.autoApplyScheduledTime.getAndSet(0L) != 0L) {
/* 436 */         LOGGER.at(Level.FINE).log("No staged update - clearing auto-apply schedule");
/*     */       }
/* 438 */       this.lastWarningTime.set(0L);
/*     */       
/*     */       return;
/*     */     } 
/* 442 */     LOGGER.at(Level.FINE).log("Auto-apply check: staged version %s", stagedVersion);
/* 443 */     checkAutoApply(stagedVersion);
/*     */   }
/*     */   
/*     */   private void logUpdateAvailable(@Nullable String currentVersion, @Nonnull String latestVersion) {
/* 447 */     LOGGER.at(Level.INFO).log("Update available: %s (current: %s)", latestVersion, currentVersion);
/* 448 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 449 */     if (config.getAutoApplyMode() == HytaleServerConfig.UpdateConfig.AutoApplyMode.DISABLED) {
/* 450 */       LOGGER.at(Level.INFO).log("Run '/update download' to stage the update");
/*     */     }
/*     */   }
/*     */   
/*     */   private void logStagedUpdateWarning(@Nullable String version, boolean isStartup) {
/* 455 */     String border = "\033[0;33m===============================================================================================";
/* 456 */     LOGGER.at(Level.INFO).log(border);
/* 457 */     if (isStartup) {
/* 458 */       LOGGER.at(Level.INFO).log("%s         WARNING: Staged update %s not applied!", "\033[0;33m", 
/* 459 */           (version != null) ? version : "unknown");
/* 460 */       LOGGER.at(Level.INFO).log("%s         Use launcher script (start.sh/bat) or manually move files from updater/staging/", "\033[0;33m");
/*     */     } else {
/*     */       
/* 463 */       LOGGER.at(Level.INFO).log("%s         REMINDER: Staged update %s waiting to be applied", "\033[0;33m", 
/* 464 */           (version != null) ? version : "unknown");
/* 465 */       LOGGER.at(Level.INFO).log("%s         Run '/update status' for details or '/update cancel' to abort", "\033[0;33m");
/*     */     } 
/*     */     
/* 468 */     LOGGER.at(Level.INFO).log(border);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkAutoApply(@Nonnull String stagedVersion) {
/* 475 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 476 */     HytaleServerConfig.UpdateConfig.AutoApplyMode mode = config.getAutoApplyMode();
/*     */     
/* 478 */     if (mode == HytaleServerConfig.UpdateConfig.AutoApplyMode.DISABLED)
/*     */       return; 
/* 480 */     Universe universe = Universe.get();
/* 481 */     if (universe == null)
/*     */       return; 
/* 483 */     int playerCount = universe.getPlayers().size();
/*     */     
/* 485 */     if (playerCount == 0) {
/*     */       
/* 487 */       LOGGER.at(Level.INFO).log("No players online - auto-applying update %s", stagedVersion);
/* 488 */       triggerAutoApply();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 493 */     if (mode == HytaleServerConfig.UpdateConfig.AutoApplyMode.WHEN_EMPTY) {
/*     */       return;
/*     */     }
/* 496 */     int delayMinutes = config.getAutoApplyDelayMinutes();
/* 497 */     long now = System.currentTimeMillis();
/* 498 */     long applyTime = now + (delayMinutes * 60) * 1000L;
/*     */ 
/*     */     
/* 501 */     if (this.autoApplyScheduledTime.compareAndSet(0L, applyTime)) {
/* 502 */       LOGGER.at(Level.INFO).log("Update %s will be auto-applied in %d minutes (players online: %d)", stagedVersion, 
/* 503 */           Integer.valueOf(delayMinutes), Integer.valueOf(playerCount));
/* 504 */       broadcastToPlayers(Message.translation("server.update.auto_apply_warning")
/* 505 */           .param("version", stagedVersion)
/* 506 */           .param("minutes", delayMinutes)
/* 507 */           .color(Color.YELLOW));
/*     */       
/*     */       return;
/*     */     } 
/* 511 */     long scheduledTime = this.autoApplyScheduledTime.get();
/*     */     
/* 513 */     if (now >= scheduledTime - 2000L) {
/*     */       
/* 515 */       LOGGER.at(Level.INFO).log("Auto-apply delay expired - applying update %s", stagedVersion);
/* 516 */       broadcastToPlayers(Message.translation("server.update.auto_apply_now")
/* 517 */           .param("version", stagedVersion)
/* 518 */           .color(Color.RED));
/* 519 */       triggerAutoApply();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 524 */     long remainingMinutes = (scheduledTime - now) / 60000L;
/* 525 */     long warnInterval = (remainingMinutes <= 1L) ? 30000L : 300000L;
/* 526 */     long lastWarn = this.lastWarningTime.get();
/*     */     
/* 528 */     if (now - lastWarn >= warnInterval && this.lastWarningTime.compareAndSet(lastWarn, now)) {
/* 529 */       LOGGER.at(Level.INFO).log("Update %s will be auto-applied in %d minute(s)", stagedVersion, Math.max(1L, remainingMinutes));
/* 530 */       broadcastToPlayers(Message.translation("server.update.auto_apply_warning")
/* 531 */           .param("version", stagedVersion)
/* 532 */           .param("minutes", Math.max(1L, remainingMinutes))
/* 533 */           .color(Color.YELLOW));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void triggerAutoApply() {
/* 538 */     this.autoApplyScheduledTime.set(0L);
/* 539 */     HytaleServer.get().shutdownServer(ShutdownReason.UPDATE);
/*     */   }
/*     */   
/*     */   private void broadcastToPlayers(@Nonnull Message message) {
/* 543 */     Universe universe = Universe.get();
/* 544 */     if (universe == null)
/* 545 */       return;  for (PlayerRef player : universe.getPlayers()) {
/* 546 */       player.sendMessage(message);
/*     */     }
/*     */   }
/*     */   
/*     */   private void notifyPlayers(@Nonnull String version) {
/* 551 */     Universe universe = Universe.get();
/* 552 */     if (universe == null) {
/*     */       return;
/*     */     }
/* 555 */     Message message = Message.translation("server.update.notify_players").param("version", version);
/*     */     
/* 557 */     PermissionsModule permissionsModule = PermissionsModule.get();
/* 558 */     for (PlayerRef player : universe.getPlayers()) {
/* 559 */       if (permissionsModule.hasPermission(player.getUuid(), "hytale.system.update.notify"))
/* 560 */         player.sendMessage(message); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\UpdateModule.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */