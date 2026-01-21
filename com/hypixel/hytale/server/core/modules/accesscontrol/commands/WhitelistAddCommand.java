/*    */ package com.hypixel.hytale.server.core.modules.accesscontrol.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*    */ import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleWhitelistProvider;
/*    */ import java.util.Set;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.CompletableFuture;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WhitelistAddCommand
/*    */   extends AbstractAsyncCommand
/*    */ {
/*    */   @Nonnull
/*    */   private final HytaleWhitelistProvider whitelistProvider;
/*    */   @Nonnull
/* 30 */   private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg = withRequiredArg("player", "server.commands.whitelist.add.player.desc", (ArgumentType)ArgTypes.GAME_PROFILE_LOOKUP);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WhitelistAddCommand(@Nonnull HytaleWhitelistProvider whitelistProvider) {
/* 38 */     super("add", "server.commands.whitelist.add.desc");
/* 39 */     this.whitelistProvider = whitelistProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/* 45 */     ProfileServiceClient.PublicGameProfile profile = (ProfileServiceClient.PublicGameProfile)this.playerArg.get(context);
/* 46 */     if (profile == null) {
/* 47 */       return CompletableFuture.completedFuture(null);
/*    */     }
/* 49 */     UUID uuid = profile.getUuid();
/* 50 */     Message displayMessage = Message.raw(profile.getUsername()).bold(true);
/*    */     
/* 52 */     if (this.whitelistProvider.modify(list -> Boolean.valueOf(list.add(uuid)))) {
/* 53 */       context.sendMessage(Message.translation("server.modules.whitelist.addSuccess")
/* 54 */           .param("name", displayMessage));
/*    */     } else {
/* 56 */       context.sendMessage(Message.translation("server.modules.whitelist.alreadyWhitelisted")
/* 57 */           .param("name", displayMessage));
/*    */     } 
/*    */     
/* 60 */     return CompletableFuture.completedFuture(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\commands\WhitelistAddCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */