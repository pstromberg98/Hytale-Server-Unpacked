/*    */ package com.hypixel.hytale.server.core.modules.accesscontrol.commands;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
/*    */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*    */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*    */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*    */ import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
/*    */ import java.util.Map;
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
/*    */ public class UnbanCommand
/*    */   extends AbstractAsyncCommand
/*    */ {
/*    */   @Nonnull
/*    */   private final HytaleBanProvider banProvider;
/*    */   @Nonnull
/* 30 */   private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg = withRequiredArg("player", "server.commands.unban.player.desc", (ArgumentType)ArgTypes.GAME_PROFILE_LOOKUP);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public UnbanCommand(@Nonnull HytaleBanProvider banProvider) {
/* 38 */     super("unban", "server.commands.unban.desc");
/* 39 */     setUnavailableInSingleplayer(true);
/* 40 */     this.banProvider = banProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/* 46 */     ProfileServiceClient.PublicGameProfile profile = (ProfileServiceClient.PublicGameProfile)this.playerArg.get(context);
/* 47 */     if (profile == null) {
/* 48 */       return CompletableFuture.completedFuture(null);
/*    */     }
/* 50 */     UUID uuid = profile.getUuid();
/* 51 */     Message displayMessage = Message.raw(profile.getUsername()).bold(true);
/*    */     
/* 53 */     if (!this.banProvider.hasBan(uuid)) {
/* 54 */       context.sendMessage(Message.translation("server.modules.unban.playerNotBanned")
/* 55 */           .param("name", displayMessage));
/*    */     } else {
/* 57 */       this.banProvider.modify(map -> Boolean.valueOf((map.remove(uuid) != null)));
/* 58 */       context.sendMessage(Message.translation("server.modules.unban.success")
/* 59 */           .param("name", displayMessage));
/*    */     } 
/*    */     
/* 62 */     return CompletableFuture.completedFuture(null);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\commands\UnbanCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */