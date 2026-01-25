/*     */ package com.hypixel.hytale.server.core.update.command;
/*     */ 
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.Options;
/*     */ import com.hypixel.hytale.server.core.ShutdownReason;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.FlagArg;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.update.UpdateService;
/*     */ import com.hypixel.hytale.server.core.util.io.FileUtil;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class UpdateApplyCommand
/*     */   extends CommandBase
/*     */ {
/*  27 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*  29 */   private static final Message MSG_NO_STAGED = Message.translation("server.commands.update.no_staged");
/*  30 */   private static final Message MSG_BACKUP_FAILED = Message.translation("server.commands.update.backup_failed");
/*     */   
/*  32 */   private final FlagArg confirmFlag = withFlagArg("confirm", "server.commands.update.apply.confirm.desc");
/*     */   
/*     */   public UpdateApplyCommand() {
/*  35 */     super("apply", "server.commands.update.apply.desc");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void executeSync(@Nonnull CommandContext context) {
/*  40 */     String stagedVersion = UpdateService.getStagedVersion();
/*  41 */     if (stagedVersion == null) {
/*  42 */       context.sendMessage(MSG_NO_STAGED);
/*     */       
/*     */       return;
/*     */     } 
/*  46 */     if (!((Boolean)this.confirmFlag.get(context)).booleanValue()) {
/*  47 */       context.sendMessage(Message.translation("server.commands.update.apply_confirm_required")
/*  48 */           .param("version", stagedVersion));
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  53 */     if (!UpdateService.isValidUpdateLayout()) {
/*  54 */       context.sendMessage(Message.translation("server.commands.update.applying_no_launcher")
/*  55 */           .param("version", stagedVersion));
/*  56 */       LOGGER.at(Level.WARNING).log("No launcher script detected - update must be applied manually after shutdown");
/*     */     } else {
/*  58 */       context.sendMessage(Message.translation("server.commands.update.applying")
/*  59 */           .param("version", stagedVersion));
/*     */     } 
/*     */ 
/*     */     
/*  63 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/*     */ 
/*     */     
/*     */     try {
/*  67 */       backupCurrentFiles();
/*     */ 
/*     */       
/*  70 */       if (config.isRunBackupBeforeUpdate() && Options.getOptionSet().has(Options.BACKUP_DIRECTORY)) {
/*  71 */         Universe universe = Universe.get();
/*  72 */         if (universe != null) {
/*  73 */           LOGGER.at(Level.INFO).log("Running server backup before update...");
/*  74 */           universe.runBackup().join();
/*  75 */           LOGGER.at(Level.INFO).log("Server backup completed");
/*     */         } 
/*  77 */       } else if (config.isRunBackupBeforeUpdate()) {
/*  78 */         LOGGER.at(Level.WARNING).log("RunBackupBeforeUpdate is enabled but backups are not configured (no --backup-dir)");
/*     */       } 
/*     */ 
/*     */       
/*  82 */       if (config.isBackupConfigBeforeUpdate()) {
/*  83 */         backupConfigFiles();
/*     */       }
/*     */     }
/*  86 */     catch (IOException e) {
/*  87 */       ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(e)).log("Failed to create backups before update");
/*  88 */       context.sendMessage(MSG_BACKUP_FAILED);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  93 */     HytaleServer.get().shutdownServer(ShutdownReason.UPDATE);
/*     */   }
/*     */   
/*     */   private void backupCurrentFiles() throws IOException {
/*  97 */     Path backupDir = UpdateService.getBackupDir();
/*  98 */     Path backupServerDir = backupDir.resolve("Server");
/*     */ 
/*     */     
/* 101 */     if (!UpdateService.deleteBackupDir()) {
/* 102 */       throw new IOException("Failed to clear existing backup directory");
/*     */     }
/* 104 */     Files.createDirectories(backupServerDir, (FileAttribute<?>[])new FileAttribute[0]);
/*     */ 
/*     */     
/* 107 */     Path currentJar = Path.of("HytaleServer.jar", new String[0]);
/* 108 */     if (Files.exists(currentJar, new java.nio.file.LinkOption[0])) {
/* 109 */       Files.copy(currentJar, backupServerDir.resolve("HytaleServer.jar"), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */     }
/*     */ 
/*     */     
/* 113 */     Path currentAot = Path.of("HytaleServer.aot", new String[0]);
/* 114 */     if (Files.exists(currentAot, new java.nio.file.LinkOption[0])) {
/* 115 */       Files.copy(currentAot, backupServerDir.resolve("HytaleServer.aot"), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */     }
/*     */ 
/*     */     
/* 119 */     Path licensesDir = Path.of("Licenses", new String[0]);
/* 120 */     if (Files.exists(licensesDir, new java.nio.file.LinkOption[0])) {
/* 121 */       FileUtil.copyDirectory(licensesDir, backupServerDir.resolve("Licenses"));
/*     */     }
/*     */ 
/*     */     
/* 125 */     Path assetsZip = Path.of("..", new String[0]).resolve("Assets.zip");
/* 126 */     if (Files.exists(assetsZip, new java.nio.file.LinkOption[0])) {
/* 127 */       Files.copy(assetsZip, backupDir.resolve("Assets.zip"), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */     }
/*     */     
/* 130 */     LOGGER.at(Level.INFO).log("Backed up current server files to %s", backupDir);
/*     */   }
/*     */   
/* 133 */   private static final String[] CONFIG_FILES = new String[] { "config.json", "permissions.json", "bans.json", "whitelist.json" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void backupConfigFiles() throws IOException {
/* 141 */     Path backupServerDir = UpdateService.getBackupDir().resolve("Server");
/* 142 */     Files.createDirectories(backupServerDir, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     
/* 144 */     int count = 0;
/* 145 */     for (String fileName : CONFIG_FILES) {
/* 146 */       Path file = Path.of(fileName, new String[0]);
/* 147 */       if (Files.exists(file, new java.nio.file.LinkOption[0])) {
/* 148 */         Files.copy(file, backupServerDir.resolve(fileName), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/* 149 */         count++;
/*     */       } 
/*     */     } 
/*     */     
/* 153 */     LOGGER.at(Level.INFO).log("Backed up %d config files to %s", count, backupServerDir);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdateApplyCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */