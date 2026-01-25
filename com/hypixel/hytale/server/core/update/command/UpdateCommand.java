/*    */ package com.hypixel.hytale.server.core.update.command;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*    */ import com.hypixel.hytale.server.core.command.system.ParseResult;
/*    */ import com.hypixel.hytale.server.core.command.system.ParserContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*    */ import com.hypixel.hytale.server.core.update.UpdateModule;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateCommand
/*    */   extends AbstractCommandCollection
/*    */ {
/* 29 */   private static final Message MSG_DISABLED = Message.translation("server.commands.update.disabled");
/*    */   
/*    */   public UpdateCommand() {
/* 32 */     super("update", "server.commands.update.desc");
/* 33 */     addSubCommand((AbstractCommand)new UpdateCheckCommand());
/* 34 */     addSubCommand((AbstractCommand)new UpdateDownloadCommand());
/* 35 */     addSubCommand((AbstractCommand)new UpdateApplyCommand());
/* 36 */     addSubCommand((AbstractCommand)new UpdateCancelCommand());
/* 37 */     addSubCommand((AbstractCommand)new UpdateStatusCommand());
/* 38 */     addSubCommand((AbstractCommand)new UpdatePatchlineCommand());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public CompletableFuture<Void> acceptCall(@Nonnull CommandSender sender, @Nonnull ParserContext parserContext, @Nonnull ParseResult parseResult) {
/* 46 */     if (UpdateModule.KILL_SWITCH_ENABLED) {
/* 47 */       sender.sendMessage(MSG_DISABLED);
/* 48 */       return CompletableFuture.completedFuture(null);
/*    */     } 
/* 50 */     return super.acceptCall(sender, parserContext, parseResult);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdateCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */