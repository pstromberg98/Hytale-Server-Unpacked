/*    */ package com.hypixel.hytale.server.core.update.command;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.HytaleServer;
/*    */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import com.hypixel.hytale.server.core.update.UpdateService;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdatePatchlineCommand
/*    */   extends CommandBase
/*    */ {
/*    */   public UpdatePatchlineCommand() {
/* 26 */     super("patchline", "server.commands.update.patchline.desc");
/* 27 */     addUsageVariant((AbstractCommand)new SetPatchlineVariant());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 33 */     String current = UpdateService.getEffectivePatchline();
/* 34 */     context.sendMessage(Message.translation("server.commands.update.patchline.current")
/* 35 */         .param("patchline", current));
/*    */   }
/*    */ 
/*    */   
/*    */   private static class SetPatchlineVariant
/*    */     extends CommandBase
/*    */   {
/*    */     @Nonnull
/* 43 */     private final RequiredArg<String> patchlineArg = withRequiredArg("patchline", "server.commands.update.patchline.arg.desc", (ArgumentType)ArgTypes.STRING);
/*    */     
/*    */     SetPatchlineVariant() {
/* 46 */       super("server.commands.update.patchline.set.desc");
/*    */     }
/*    */ 
/*    */     
/*    */     protected void executeSync(@Nonnull CommandContext context) {
/* 51 */       String newPatchline = (String)this.patchlineArg.get(context);
/*    */       
/* 53 */       HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 54 */       config.setPatchline(newPatchline);
/*    */       
/* 56 */       context.sendMessage(Message.translation("server.commands.update.patchline.changed")
/* 57 */           .param("patchline", newPatchline));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\command\UpdatePatchlineCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */