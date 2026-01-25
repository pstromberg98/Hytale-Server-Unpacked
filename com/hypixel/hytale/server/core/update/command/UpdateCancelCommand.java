/*    */ package com.hypixel.hytale.server.core.update.command;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.update.UpdateModule;
/*    */ import com.hypixel.hytale.server.core.update.UpdateService;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateCancelCommand
/*    */   extends CommandBase
/*    */ {
/* 16 */   private static final Message MSG_NOTHING_TO_CANCEL = Message.translation("server.commands.update.nothing_to_cancel");
/* 17 */   private static final Message MSG_DOWNLOAD_CANCELLED = Message.translation("server.commands.update.download_cancelled");
/* 18 */   private static final Message MSG_CANCEL_FAILED = Message.translation("server.commands.update.cancel_failed");
/*    */   
/*    */   public UpdateCancelCommand() {
/* 21 */     super("cancel", "server.commands.update.cancel.desc");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 26 */     UpdateModule updateModule = UpdateModule.get();
/* 27 */     boolean didSomething = false;
/*    */ 
/*    */     
/* 30 */     if (updateModule != null && updateModule.cancelDownload()) {
/* 31 */       context.sendMessage(MSG_DOWNLOAD_CANCELLED);
/* 32 */       didSomething = true;
/*    */     } 
/*    */ 
/*    */     
/* 36 */     String stagedVersion = UpdateService.getStagedVersion();
/* 37 */     if (stagedVersion != null || didSomething) {
/* 38 */       if (UpdateService.deleteStagedUpdate()) {
/* 39 */         if (stagedVersion != null) {
/* 40 */           context.sendMessage(Message.translation("server.commands.update.cancelled")
/* 41 */               .param("version", stagedVersion));
/*    */         }
/* 43 */       } else if (stagedVersion != null) {
/* 44 */         context.sendMessage(MSG_CANCEL_FAILED);
/*    */       } 
/*    */       
/*    */       return;
/*    */     } 
/* 49 */     context.sendMessage(MSG_NOTHING_TO_CANCEL);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdateCancelCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */