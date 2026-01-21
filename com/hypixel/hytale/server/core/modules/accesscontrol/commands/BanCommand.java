/*     */ package com.hypixel.hytale.server.core.modules.accesscontrol.commands;
/*     */ 
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandUtil;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.modules.accesscontrol.ban.InfiniteBan;
/*     */ import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import java.time.Instant;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
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
/*     */ 
/*     */ public class BanCommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*     */   @Nonnull
/*     */   private final HytaleBanProvider banProvider;
/*     */   @Nonnull
/*  38 */   private final RequiredArg<ProfileServiceClient.PublicGameProfile> playerArg = withRequiredArg("player", "server.commands.ban.player.desc", (ArgumentType)ArgTypes.GAME_PROFILE_LOOKUP);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BanCommand(@Nonnull HytaleBanProvider banProvider) {
/*  46 */     super("ban", "server.commands.ban.desc");
/*  47 */     setUnavailableInSingleplayer(true);
/*  48 */     this.banProvider = banProvider;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext context) {
/*     */     String reason;
/*  54 */     ProfileServiceClient.PublicGameProfile profile = (ProfileServiceClient.PublicGameProfile)this.playerArg.get(context);
/*  55 */     if (profile == null) {
/*  56 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*  58 */     UUID uuid = profile.getUuid();
/*     */ 
/*     */     
/*  61 */     String rawArgs = CommandUtil.stripCommandName(context.getInputString());
/*  62 */     int firstSpaceIndex = rawArgs.indexOf(' ');
/*     */     
/*  64 */     if (firstSpaceIndex != -1) {
/*  65 */       String afterPlayer = rawArgs.substring(firstSpaceIndex + 1).trim();
/*  66 */       reason = afterPlayer.isEmpty() ? "No reason." : afterPlayer;
/*     */     } else {
/*  68 */       reason = "No reason.";
/*     */     } 
/*     */     
/*  71 */     Message displayMessage = Message.raw(profile.getUsername()).bold(true);
/*  72 */     PlayerRef playerRef = Universe.get().getPlayer(uuid);
/*     */     
/*  74 */     if (this.banProvider.hasBan(uuid)) {
/*  75 */       context.sendMessage(Message.translation("server.modules.ban.alreadyBanned")
/*  76 */           .param("name", displayMessage));
/*  77 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/*  80 */     InfiniteBan ban = new InfiniteBan(uuid, context.sender().getUuid(), Instant.now(), reason);
/*     */ 
/*     */     
/*  83 */     this.banProvider.modify(banMap -> {
/*     */           banMap.put(uuid, ban);
/*     */           
/*     */           return Boolean.TRUE;
/*     */         });
/*     */     
/*  89 */     if (playerRef != null) {
/*  90 */       CompletableFuture<Optional<String>> disconnectReason = ban.getDisconnectReason(uuid);
/*     */       
/*  92 */       return disconnectReason.whenComplete((string, disconnectEx) -> {
/*     */             Optional<String> optional = string;
/*     */ 
/*     */             
/*     */             if (disconnectEx != null) {
/*     */               context.sendMessage(Message.translation("server.modules.ban.failedDisconnectReason").param("name", displayMessage));
/*     */ 
/*     */               
/*     */               disconnectEx.printStackTrace();
/*     */             } 
/*     */ 
/*     */             
/*     */             if (optional == null || !optional.isPresent()) {
/*     */               optional = Optional.of("Failed to get disconnect reason.");
/*     */             }
/*     */ 
/*     */             
/*     */             playerRef.getPacketHandler().disconnect(optional.get());
/*     */             
/*     */             context.sendMessage(Message.translation("server.modules.ban.bannedWithReason").param("name", displayMessage).param("reason", reason));
/* 112 */           }).thenApply(v -> null);
/*     */     } 
/* 114 */     context.sendMessage(Message.translation("server.modules.ban.bannedWithReason")
/* 115 */         .param("name", displayMessage).param("reason", reason));
/* 116 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\commands\BanCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */