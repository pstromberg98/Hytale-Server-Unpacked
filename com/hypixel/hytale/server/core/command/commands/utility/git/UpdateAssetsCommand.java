/*     */ package com.hypixel.hytale.server.core.command.commands.utility.git;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.PathUtil;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*     */ import com.hypixel.hytale.server.core.util.AssetUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UpdateAssetsCommand
/*     */   extends AbstractCommandCollection
/*     */ {
/*     */   public UpdateAssetsCommand() {
/*  28 */     super("assets", "server.commands.git.assets.desc");
/*  29 */     addSubCommand((AbstractCommand)new UpdateAssetsStatusCommand());
/*  30 */     addSubCommand((AbstractCommand)new UpdateAssetsResetCommand());
/*  31 */     addSubCommand((AbstractCommand)new UpdateAssetsPullCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static abstract class UpdateAssetsGitCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     protected UpdateAssetsGitCommand(@Nonnull String name, @Nonnull String description) {
/*  42 */       super(name, description);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected abstract String[] getCommand(@Nonnull Path param1Path);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/*  57 */       return CompletableFuture.runAsync(() -> {
/*     */             Path assetPath = AssetUtil.getHytaleAssetsPath();
/*     */             
/*     */             Path gitPath = null;
/*     */             
/*     */             if (Files.exists(assetPath.resolve(".git"), new java.nio.file.LinkOption[0])) {
/*     */               gitPath = assetPath;
/*     */             } else {
/*     */               Path parent = PathUtil.getParent(assetPath.toAbsolutePath());
/*     */               
/*     */               if (Files.exists(parent.resolve(".git"), new java.nio.file.LinkOption[0])) {
/*     */                 gitPath = parent;
/*     */               }
/*     */             } 
/*     */             
/*     */             if (gitPath == null) {
/*     */               context.sendMessage(Message.translation("server.general.pathNotGitRepo").param("path", assetPath.toString()));
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             String[] processCommand = getCommand(gitPath);
/*     */             
/*     */             String commandDisplay = String.join(" ", (CharSequence[])processCommand);
/*     */             
/*     */             try {
/*     */               context.sendMessage(Message.translation("server.commands.git.running").param("cmd", commandDisplay));
/*     */               
/*     */               Process process = (new ProcessBuilder(processCommand)).directory(gitPath.toFile()).start();
/*     */               
/*     */               try {
/*     */                 process.waitFor();
/*     */                 
/*     */                 BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
/*     */                 
/*     */                 String line;
/*     */                 
/*     */                 while ((line = reader.readLine()) != null) {
/*     */                   context.sendMessage(Message.translation("server.commands.git.runningStdOut").param("cmd", commandDisplay).param("line", line));
/*     */                 }
/*     */                 reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
/*     */                 while ((line = reader.readLine()) != null) {
/*     */                   context.sendMessage(Message.translation("server.commands.git.runningStdErr").param("cmd", commandDisplay).param("line", line));
/*     */                 }
/*     */                 context.sendMessage(Message.translation("server.commands.git.done").param("cmd", commandDisplay));
/* 102 */               } catch (InterruptedException e) {
/*     */                 Thread.currentThread().interrupt();
/*     */               } 
/* 105 */             } catch (IOException e) {
/*     */               context.sendMessage(Message.translation("server.commands.git.failed").param("cmd", commandDisplay).param("msg", e.getMessage()));
/*     */             } 
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class UpdateAssetsStatusCommand
/*     */     extends UpdateAssetsGitCommand
/*     */   {
/*     */     public UpdateAssetsStatusCommand() {
/* 122 */       super("status", "server.commands.git.assets.status.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[] getCommand(@Nonnull Path gitPath) {
/* 128 */       return new String[] { "git", "status" };
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class UpdateAssetsResetCommand
/*     */     extends UpdateAssetsGitCommand
/*     */   {
/*     */     public UpdateAssetsResetCommand() {
/* 140 */       super("reset", "server.commands.git.assets.reset.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[] getCommand(@Nonnull Path gitPath) {
/* 146 */       return new String[] { "git", "reset", "--hard", "head" };
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class UpdateAssetsPullCommand
/*     */     extends UpdateAssetsGitCommand
/*     */   {
/*     */     public UpdateAssetsPullCommand() {
/* 158 */       super("pull", "server.commands.git.assets.pull.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[] getCommand(@Nonnull Path gitPath) {
/* 164 */       Path script = gitPath.resolve("../../updateAssets.sh");
/* 165 */       if (Files.exists(script, new java.nio.file.LinkOption[0])) {
/* 166 */         Path relative = gitPath.relativize(script);
/* 167 */         return new String[] { "sh", relative.toString() };
/*     */       } 
/* 169 */       return new String[] { "git", "pull" };
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\git\UpdateAssetsCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */