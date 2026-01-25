/*     */ package com.hypixel.hytale.server.core.command.commands.utility.git;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.PathUtil;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabStore;
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
/*     */ public class UpdatePrefabsCommand
/*     */   extends AbstractCommandCollection
/*     */ {
/*     */   public UpdatePrefabsCommand() {
/*  28 */     super("prefabs", "server.commands.git.prefabs.desc");
/*  29 */     addSubCommand((AbstractCommand)new UpdatePrefabsStatusCommand());
/*  30 */     addSubCommand((AbstractCommand)new UpdatePrefabsCommitCommand());
/*  31 */     addSubCommand((AbstractCommand)new UpdatePrefabsPullCommand());
/*  32 */     addSubCommand((AbstractCommand)new UpdatePrefabsPushCommand());
/*  33 */     addSubCommand((AbstractCommand)new UpdatePrefabsAllCommand());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static abstract class UpdatePrefabsGitCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     protected UpdatePrefabsGitCommand(@Nonnull String name, @Nonnull String description) {
/*  44 */       super(name, description);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected abstract String[][] getCommands(@Nonnull String param1String);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/*  59 */       return CompletableFuture.runAsync(() -> {
/*     */             Path prefabsPath = PrefabStore.get().getServerPrefabsPath();
/*     */             
/*     */             Path gitPath = null;
/*     */             
/*     */             if (Files.isDirectory(prefabsPath.resolve(".git"), new java.nio.file.LinkOption[0])) {
/*     */               gitPath = prefabsPath;
/*     */             } else {
/*     */               Path parent = PathUtil.getParent(prefabsPath);
/*     */               
/*     */               if (Files.isDirectory(parent.resolve(".git"), new java.nio.file.LinkOption[0])) {
/*     */                 gitPath = parent;
/*     */               }
/*     */             } 
/*     */             
/*     */             if (gitPath == null) {
/*     */               context.sendMessage(Message.translation("server.general.pathNotGitRepo").param("path", prefabsPath.toString()));
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             String senderDisplayName = context.sender().getDisplayName().replaceAll("[^a-zA-Z0-9 ._-]", "");
/*     */             
/*     */             if (senderDisplayName.isEmpty()) {
/*     */               senderDisplayName = "Unknown";
/*     */             }
/*     */             
/*     */             String finalSenderDisplayName = senderDisplayName;
/*     */             
/*     */             String[][] cmds = getCommands(finalSenderDisplayName);
/*     */             
/*     */             for (String[] processCommand : cmds) {
/*     */               try {
/*     */                 String commandDisplay = String.join(" ", (CharSequence[])processCommand);
/*     */                 
/*     */                 context.sendMessage(Message.translation("server.commands.git.runningCmd").param("cmd", commandDisplay));
/*     */                 
/*     */                 Process process = (new ProcessBuilder(processCommand)).directory(gitPath.toFile()).start();
/*     */                 
/*     */                 try {
/*     */                   process.waitFor();
/*     */                   
/*     */                   BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
/*     */                   String line;
/*     */                   while ((line = reader.readLine()) != null) {
/*     */                     context.sendMessage(Message.translation("server.commands.git.runningStdOut").param("cmd", commandDisplay).param("line", line));
/*     */                   }
/*     */                   reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
/*     */                   while ((line = reader.readLine()) != null) {
/*     */                     context.sendMessage(Message.translation("server.commands.git.runningStdErr").param("cmd", commandDisplay).param("line", line));
/*     */                   }
/*     */                   context.sendMessage(Message.translation("server.commands.git.done").param("cmd", commandDisplay));
/* 111 */                 } catch (InterruptedException e) {
/*     */                   Thread.currentThread().interrupt();
/*     */                   break;
/*     */                 } 
/* 115 */               } catch (IOException e) {
/*     */                 context.sendMessage(Message.translation("server.commands.git.failed").param("cmd", String.join(" ", (CharSequence[])processCommand)).param("msg", e.getMessage()));
/*     */                 break;
/*     */               } 
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
/*     */   private static class UpdatePrefabsStatusCommand
/*     */     extends UpdatePrefabsGitCommand
/*     */   {
/*     */     public UpdatePrefabsStatusCommand() {
/* 134 */       super("status", "server.commands.git.prefabs.status.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[][] getCommands(@Nonnull String senderDisplayName) {
/* 140 */       return new String[][] { { "git", "status" }, { "git", "submodule", "foreach", "git", "status" } };
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
/*     */   private static class UpdatePrefabsCommitCommand
/*     */     extends UpdatePrefabsGitCommand
/*     */   {
/*     */     public UpdatePrefabsCommitCommand() {
/* 155 */       super("commit", "server.commands.git.prefabs.commit.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[][] getCommands(@Nonnull String senderDisplayName) {
/* 161 */       return new String[][] { { "git", "add", "--all", "." }, { "git", "commit", "-am", "Update prefabs by " + senderDisplayName }, { "git", "submodule", "foreach", "git", "add", "--all", "." }, { "git", "submodule", "foreach", "git", "commit", "-am", "\"Update prefabs by " + senderDisplayName + "\"" } };
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
/*     */   
/*     */   private static class UpdatePrefabsPullCommand
/*     */     extends UpdatePrefabsGitCommand
/*     */   {
/*     */     public UpdatePrefabsPullCommand() {
/* 178 */       super("pull", "server.commands.git.prefabs.pull.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[][] getCommands(@Nonnull String senderDisplayName) {
/* 184 */       return new String[][] { { "git", "pull" }, { "git", "submodule", "foreach", "git", "pull" } };
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
/*     */   private static class UpdatePrefabsPushCommand
/*     */     extends UpdatePrefabsGitCommand
/*     */   {
/*     */     public UpdatePrefabsPushCommand() {
/* 199 */       super("push", "server.commands.git.prefabs.push.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[][] getCommands(@Nonnull String senderDisplayName) {
/* 205 */       return new String[][] { { "git", "push", "origin", "master" }, { "git", "submodule", "foreach", "git", "push" } };
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
/*     */   private static class UpdatePrefabsAllCommand
/*     */     extends UpdatePrefabsGitCommand
/*     */   {
/*     */     public UpdatePrefabsAllCommand() {
/* 220 */       super("all", "server.commands.git.prefabs.all.desc");
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected String[][] getCommands(@Nonnull String senderDisplayName) {
/* 226 */       return new String[][] { { "git", "submodule", "foreach", "git", "add", "--all", "." }, { "git", "submodule", "foreach", "git", "commit", "-am", "\"Update prefabs by " + senderDisplayName + "\"" }, { "git", "submodule", "foreach", "git", "pull" }, { "git", "submodule", "foreach", "git", "push" }, { "git", "add", "--all", "." }, { "git", "commit", "-am", "Update prefabs by " + senderDisplayName }, { "git", "pull" }, { "git", "push" } };
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\command\\utility\git\UpdatePrefabsCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */