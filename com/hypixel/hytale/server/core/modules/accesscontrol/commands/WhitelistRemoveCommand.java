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
/*    */ public class WhitelistRemoveCommand
/*    */   extends AbstractAsyncCommand
/*    */ {
/*    */   @Nonnull
/*    */   private final HytaleWhitelistProvider whitelistProvider;
/*    */   @Nonnull
/* 30 */   private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg = withRequiredArg("player", "server.commands.whitelist.remove.player.desc", (ArgumentType)ArgTypes.GAME_PROFILE_LOOKUP);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WhitelistRemoveCommand(@Nonnull HytaleWhitelistProvider whitelistProvider) {
/* 38 */     super("remove", "server.commands.whitelist.remove.desc");
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
/* 52 */     if (this.whitelistProvider.modify(list -> Boolean.valueOf(list.remove(uuid)))) {
/* 53 */       context.sendMessage(Message.translation("server.modules.whitelist.removalSuccess")
/* 54 */           .param("uuid", displayMessage));
/*    */     } else {
/* 56 */       context.sendMessage(Message.translation("server.modules.whitelist.uuidNotWhitelisted")
/* 57 */           .param("uuid", displayMessage));
/*    */     } 
/*    */     
/* 60 */     return CompletableFuture.completedFuture(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\commands\WhitelistRemoveCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */