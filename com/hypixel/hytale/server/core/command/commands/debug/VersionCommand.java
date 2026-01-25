/*    */ package com.hypixel.hytale.server.core.command.commands.debug;
/*    */ 
/*    */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class VersionCommand
/*    */   extends CommandBase
/*    */ {
/*    */   public VersionCommand() {
/* 13 */     super("version", "Displays version information about the currently running server");
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeSync(@Nonnull CommandContext context) {
/* 18 */     String version = ManifestUtil.getImplementationVersion();
/* 19 */     String patchline = ManifestUtil.getPatchline();
/*    */     
/* 21 */     if ("release".equals(patchline)) {
/* 22 */       context.sendMessage(Message.translation("server.commands.version.response")
/* 23 */           .param("version", version)
/* 24 */           .param("patchline", patchline));
/*    */     } else {
/* 26 */       context.sendMessage(Message.translation("server.commands.version.response.withEnvironment")
/* 27 */           .param("version", version)
/* 28 */           .param("patchline", patchline)
/* 29 */           .param("environment", "release"));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\command\commands\debug\VersionCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */