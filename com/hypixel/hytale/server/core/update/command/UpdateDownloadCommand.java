/*     */ package com.hypixel.hytale.server.core.update.command;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.FormatUtil;
/*     */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.update.UpdateModule;
/*     */ import com.hypixel.hytale.server.core.update.UpdateService;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateDownloadCommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*  23 */   private static final Message MSG_NOT_AUTHENTICATED = Message.translation("server.commands.update.not_authenticated");
/*  24 */   private static final Message MSG_CHECK_FAILED = Message.translation("server.commands.update.check_failed");
/*  25 */   private static final Message MSG_NO_UPDATE = Message.translation("server.commands.update.no_update");
/*  26 */   private static final Message MSG_DOWNLOAD_FAILED = Message.translation("server.commands.update.download_failed");
/*  27 */   private static final Message MSG_DOWNLOAD_COMPLETE = Message.translation("server.commands.update.download_complete");
/*  28 */   private static final Message MSG_DOWNLOAD_IN_PROGRESS = Message.translation("server.commands.update.download_in_progress");
/*  29 */   private static final Message MSG_INVALID_LAYOUT = Message.translation("server.commands.update.invalid_layout_download");
/*     */   
/*  31 */   private final FlagArg forceFlag = withFlagArg("force", "server.commands.update.download.force.desc");
/*     */   
/*     */   public UpdateDownloadCommand() {
/*  34 */     super("download", "server.commands.update.download.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/*  40 */     UpdateModule updateModule = UpdateModule.get();
/*     */ 
/*     */     
/*  43 */     if (updateModule != null && updateModule.isDownloadInProgress()) {
/*  44 */       context.sendMessage(MSG_DOWNLOAD_IN_PROGRESS);
/*  45 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/*  48 */     if (!UpdateService.isValidUpdateLayout() && !((Boolean)this.forceFlag.get(context)).booleanValue()) {
/*  49 */       context.sendMessage(MSG_INVALID_LAYOUT);
/*  50 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/*  53 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/*  54 */     if (!authManager.hasSessionToken()) {
/*  55 */       context.sendMessage(MSG_NOT_AUTHENTICATED);
/*  56 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/*  59 */     UpdateService updateService = new UpdateService();
/*     */     
/*  61 */     return updateService.checkForUpdate(UpdateService.getEffectivePatchline()).thenCompose(manifest -> {
/*     */           if (manifest == null) {
/*     */             context.sendMessage(MSG_CHECK_FAILED);
/*     */             return CompletableFuture.completedFuture(null);
/*     */           } 
/*     */           if (updateModule != null)
/*     */             updateModule.setLatestKnownVersion(manifest); 
/*     */           String currentVersion = ManifestUtil.getImplementationVersion();
/*     */           if (currentVersion != null && currentVersion.equals(manifest.version)) {
/*     */             context.sendMessage(MSG_NO_UPDATE);
/*     */             return CompletableFuture.completedFuture(null);
/*     */           } 
/*     */           if (updateModule != null && !updateModule.tryAcquireDownloadLock()) {
/*     */             context.sendMessage(MSG_DOWNLOAD_IN_PROGRESS);
/*     */             return CompletableFuture.completedFuture(null);
/*     */           } 
/*     */           context.sendMessage(Message.translation("server.commands.update.downloading").param("version", manifest.version));
/*     */           AtomicInteger lastPercent = new AtomicInteger(-1);
/*     */           UpdateService.DownloadTask downloadTask = updateService.downloadUpdate(manifest, UpdateService.getStagingDir(), ());
/*     */           CompletableFuture<Boolean> downloadFuture = downloadTask.future().whenComplete(());
/*     */           if (updateModule != null)
/*     */             updateModule.setActiveDownload(downloadFuture, downloadTask.thread()); 
/*     */           return downloadFuture.thenApply(());
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdateDownloadCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */