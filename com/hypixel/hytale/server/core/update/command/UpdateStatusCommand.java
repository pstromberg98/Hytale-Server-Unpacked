/*    */ package com.hypixel.hytale.server.core.update.command;
/*    */ 
/*    */ import com.hypixel.hytale.common.util.FormatUtil;
/*    */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.update.UpdateModule;
/*    */ import com.hypixel.hytale.server.core.update.UpdateService;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateStatusCommand
/*    */   extends CommandBase
/*    */ {
/*    */   public UpdateStatusCommand() {
/* 21 */     super("status", "server.commands.update.status.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 26 */     String currentVersion = ManifestUtil.getImplementationVersion();
/* 27 */     String patchline = UpdateService.getEffectivePatchline();
/* 28 */     String stagedVersion = UpdateService.getStagedVersion();
/*    */     
/* 30 */     UpdateModule updateModule = UpdateModule.get();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 37 */     Message msg = Message.translation("server.commands.update.status").param("version", (currentVersion != null) ? currentVersion : "unknown").param("patchline", patchline).param("staged", (stagedVersion != null) ? stagedVersion : "none").param("latest", getLatestStatus(currentVersion, updateModule)).param("downloading", getDownloadStatus(updateModule));
/*    */     
/* 39 */     context.sendMessage(msg);
/*    */   }
/*    */   
/*    */   private String getLatestStatus(String currentVersion, UpdateModule updateModule) {
/* 43 */     if (updateModule == null) return "unknown";
/*    */     
/* 45 */     UpdateService.VersionManifest latestKnown = updateModule.getLatestKnownVersion();
/* 46 */     if (latestKnown == null) return "not checked";
/*    */     
/* 48 */     if (currentVersion != null && currentVersion.equals(latestKnown.version)) {
/* 49 */       return "up to date";
/*    */     }
/* 51 */     return latestKnown.version + " available";
/*    */   }
/*    */   
/*    */   private String getDownloadStatus(UpdateModule updateModule) {
/* 55 */     if (updateModule == null || !updateModule.isDownloadInProgress()) {
/* 56 */       return "no";
/*    */     }
/*    */     
/* 59 */     UpdateModule.DownloadProgress progress = updateModule.getDownloadProgress();
/* 60 */     if (progress == null) {
/* 61 */       return "starting...";
/*    */     }
/*    */     
/* 64 */     StringBuilder sb = new StringBuilder();
/* 65 */     sb.append(progress.percent()).append("% (");
/* 66 */     sb.append(FormatUtil.bytesToString(progress.downloadedBytes()));
/* 67 */     sb.append("/");
/* 68 */     sb.append(FormatUtil.bytesToString(progress.totalBytes()));
/* 69 */     sb.append(")");
/*    */     
/* 71 */     if (progress.etaSeconds() >= 0L) {
/* 72 */       sb.append(" ETA: ").append(FormatUtil.timeUnitToString(progress.etaSeconds(), TimeUnit.SECONDS));
/*    */     }
/*    */     
/* 75 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdateStatusCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */