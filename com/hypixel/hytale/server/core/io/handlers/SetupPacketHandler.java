/*     */ package com.hypixel.hytale.server.core.io.handlers;
/*     */ 
/*     */ import com.hypixel.hytale.common.util.CompletableFutureUtil;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.event.IBaseEvent;
/*     */ import com.hypixel.hytale.event.IEventDispatcher;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.protocol.Asset;
/*     */ import com.hypixel.hytale.protocol.HostAddress;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.io.netty.ProtocolUtil;
/*     */ import com.hypixel.hytale.protocol.packets.auth.ClientReferral;
/*     */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*     */ import com.hypixel.hytale.protocol.packets.connection.DisconnectType;
/*     */ import com.hypixel.hytale.protocol.packets.interface_.ServerInfo;
/*     */ import com.hypixel.hytale.protocol.packets.setup.PlayerOptions;
/*     */ import com.hypixel.hytale.protocol.packets.setup.RequestAssets;
/*     */ import com.hypixel.hytale.protocol.packets.setup.ViewRadius;
/*     */ import com.hypixel.hytale.protocol.packets.setup.WorldLoadFinished;
/*     */ import com.hypixel.hytale.protocol.packets.setup.WorldLoadProgress;
/*     */ import com.hypixel.hytale.protocol.packets.setup.WorldSettings;
/*     */ import com.hypixel.hytale.server.core.Constants;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.AssetRegistryLoader;
/*     */ import com.hypixel.hytale.server.core.asset.common.CommonAssetModule;
/*     */ import com.hypixel.hytale.server.core.asset.common.PlayerCommonAssets;
/*     */ import com.hypixel.hytale.server.core.asset.common.events.SendCommonAssetsEvent;
/*     */ import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
/*     */ import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
/*     */ import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
/*     */ import com.hypixel.hytale.server.core.event.events.player.PlayerSetupDisconnectEvent;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.io.ProtocolVersion;
/*     */ import com.hypixel.hytale.server.core.io.netty.NettyUtil;
/*     */ import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
/*     */ import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.DumpUtil;
/*     */ import io.netty.channel.Channel;
/*     */ import io.netty.channel.ChannelHandlerContext;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class SetupPacketHandler
/*     */   extends GenericConnectionPacketHandler
/*     */ {
/*     */   private final UUID uuid;
/*     */   private final String username;
/*     */   private final byte[] referralData;
/*     */   private final HostAddress referralSource;
/*     */   private PlayerCommonAssets assets;
/*     */   private boolean receivedRequest;
/*  62 */   private int clientViewRadiusChunks = 6;
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, UUID uuid, String username) {
/*  65 */     this(channel, protocolVersion, language, uuid, username, (byte[])null, (HostAddress)null);
/*     */   }
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, UUID uuid, String username, byte[] referralData, HostAddress referralSource) {
/*  69 */     super(channel, protocolVersion, language);
/*  70 */     this.uuid = uuid;
/*  71 */     this.username = username;
/*  72 */     this.referralData = referralData;
/*  73 */     this.referralSource = referralSource;
/*  74 */     this.auth = null;
/*     */ 
/*     */     
/*  77 */     if (referralData != null && referralData.length > 0) {
/*  78 */       HytaleLogger.getLogger().at(Level.INFO).log("Player %s connecting with %d bytes of referral data from %s:%d (unauthenticated - plugins must validate!)", username, 
/*     */           
/*  80 */           Integer.valueOf(referralData.length), 
/*  81 */           (referralSource != null) ? referralSource.host : "unknown", 
/*  82 */           Short.valueOf((referralSource != null) ? referralSource.port : 0));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, @Nonnull PlayerAuthentication auth) {
/*  88 */     super(channel, protocolVersion, language);
/*  89 */     this.uuid = auth.getUuid();
/*  90 */     this.username = auth.getUsername();
/*  91 */     this.auth = auth;
/*  92 */     this.referralData = auth.getReferralData();
/*  93 */     this.referralSource = auth.getReferralSource();
/*     */     
/*  95 */     if (this.referralData != null && this.referralData.length > 0) {
/*  96 */       HytaleLogger.getLogger().at(Level.INFO).log("Player %s connecting with %d bytes of referral data from %s:%d (authenticated)", this.username, 
/*     */           
/*  98 */           Integer.valueOf(this.referralData.length), 
/*  99 */           (this.referralSource != null) ? this.referralSource.host : "unknown", 
/* 100 */           Short.valueOf((this.referralSource != null) ? this.referralSource.port : 0));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String getIdentifier() {
/* 107 */     return "{Setup(" + NettyUtil.formatRemoteAddress(this.channel) + "), " + this.username + ", " + String.valueOf(this.uuid) + ", " + ((this.auth != null) ? "SECURE" : "INSECURE") + "}";
/*     */   }
/*     */ 
/*     */   
/*     */   public void registered0(@Nonnull PacketHandler oldHandler) {
/* 112 */     setTimeout("send-world-settings", () -> (this.assets != null), 10L, TimeUnit.SECONDS);
/*     */ 
/*     */     
/* 115 */     PlayerSetupConnectEvent event = (PlayerSetupConnectEvent)HytaleServer.get().getEventBus().dispatchFor(PlayerSetupConnectEvent.class).dispatch((IBaseEvent)new PlayerSetupConnectEvent(this, this.username, this.uuid, this.auth, this.referralData, this.referralSource));
/* 116 */     if (event.isCancelled()) {
/* 117 */       disconnect(event.getReason());
/*     */       
/*     */       return;
/*     */     } 
/* 121 */     ClientReferral clientReferral = event.getClientReferral();
/* 122 */     if (clientReferral != null) {
/* 123 */       writeNoCache((Packet)clientReferral);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 129 */     PlayerRef otherPlayer = Universe.get().getPlayer(this.uuid);
/* 130 */     if (otherPlayer != null) {
/* 131 */       HytaleLogger.getLogger().at(Level.INFO).log("Found match of player %s on %s", this.uuid, otherPlayer.getUsername());
/*     */       
/* 133 */       Channel otherPlayerChannel = otherPlayer.getPacketHandler().getChannel();
/*     */       
/* 135 */       if (NettyUtil.isFromSameOrigin(otherPlayerChannel, this.channel)) {
/*     */ 
/*     */         
/* 138 */         Ref<EntityStore> reference = otherPlayer.getReference();
/* 139 */         if (reference != null) {
/* 140 */           World world = ((EntityStore)reference.getStore().getExternalData()).getWorld();
/* 141 */           if (world != null) {
/* 142 */             CompletableFuture<Void> removalFuture = new CompletableFuture<>();
/*     */             
/* 144 */             world.execute(() -> {
/*     */                   otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
/*     */ 
/*     */                   
/*     */                   world.execute(());
/*     */                 });
/*     */ 
/*     */             
/* 152 */             removalFuture.join();
/*     */           } else {
/* 154 */             otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 161 */         disconnect("You are already logged in on that account!");
/* 162 */         otherPlayer.sendMessage(Message.translation("server.io.setuppackethandler.otherLoginAttempt"));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 167 */     PacketHandler.logConnectionTimings(this.channel, "Load Player Config", Level.FINE);
/*     */     
/* 169 */     WorldSettings worldSettings = new WorldSettings();
/* 170 */     worldSettings.worldHeight = 320;
/*     */     
/* 172 */     Asset[] requiredAssets = CommonAssetModule.get().getRequiredAssets();
/* 173 */     this.assets = new PlayerCommonAssets(requiredAssets);
/* 174 */     worldSettings.requiredAssets = requiredAssets;
/*     */     
/* 176 */     write((Packet)worldSettings);
/*     */     
/* 178 */     HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
/* 179 */     write((Packet)new ServerInfo(HytaleServer.get().getServerName(), serverConfig.getMotd(), serverConfig.getMaxPlayers()));
/*     */     
/* 181 */     setTimeout("receive-assets-request", () -> this.receivedRequest, 120L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(@Nonnull Packet packet) {
/* 186 */     switch (packet.getId()) { case 1:
/* 187 */         handle((Disconnect)packet); return;
/* 188 */       case 23: handle((RequestAssets)packet); return;
/* 189 */       case 32: handle((ViewRadius)packet); return;
/* 190 */       case 33: handle((PlayerOptions)packet); return; }
/* 191 */      disconnect("Protocol error: unexpected packet " + packet.getId());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closed(ChannelHandlerContext ctx) {
/* 197 */     super.closed(ctx);
/* 198 */     IEventDispatcher<PlayerSetupDisconnectEvent, PlayerSetupDisconnectEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(PlayerSetupDisconnectEvent.class);
/* 199 */     if (dispatcher.hasListener()) dispatcher.dispatch((IBaseEvent)new PlayerSetupDisconnectEvent(this.username, this.uuid, this.auth, this.disconnectReason));
/*     */     
/* 201 */     if (Constants.SINGLEPLAYER) {
/* 202 */       if (Universe.get().getPlayerCount() == 0) {
/* 203 */         HytaleLogger.getLogger().at(Level.INFO).log("No players left on singleplayer server shutting down!");
/* 204 */         HytaleServer.get().shutdownServer();
/* 205 */       } else if (SingleplayerModule.isOwner(this.auth, this.uuid)) {
/* 206 */         HytaleLogger.getLogger().at(Level.INFO).log("Owner left the singleplayer server shutting down!");
/* 207 */         Universe.get().getPlayers().forEach(p -> p.getPacketHandler().disconnect(this.username + " left! Shutting down singleplayer world!"));
/* 208 */         HytaleServer.get().shutdownServer();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull Disconnect packet) {
/* 214 */     this.disconnectReason.setClientDisconnectType(packet.type);
/* 215 */     HytaleLogger.getLogger().at(Level.INFO).log("%s - %s at %s left with reason: %s - %s", this.uuid, this.username, 
/*     */         
/* 217 */         NettyUtil.formatRemoteAddress(this.channel), packet.type
/* 218 */         .name(), packet.reason);
/* 219 */     ProtocolUtil.closeApplicationConnection(this.channel);
/*     */     
/* 221 */     if (packet.type == DisconnectType.Crash && Constants.SINGLEPLAYER && (
/* 222 */       Universe.get().getPlayerCount() == 0 || SingleplayerModule.isOwner(this.auth, this.uuid))) {
/* 223 */       DumpUtil.dump(true, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull RequestAssets packet) {
/* 228 */     if (this.receivedRequest) throw new IllegalArgumentException("Received duplicate RequestAssets!"); 
/* 229 */     this.receivedRequest = true;
/*     */     
/* 231 */     PacketHandler.logConnectionTimings(this.channel, "Request Assets", Level.FINE);
/*     */     
/* 233 */     CompletableFuture<Void> future = CompletableFutureUtil._catch(((CompletableFuture)HytaleServer.get().getEventBus().dispatchForAsync(SendCommonAssetsEvent.class).dispatch((IBaseEvent)new SendCommonAssetsEvent(this, packet.assets)))
/* 234 */         .thenAccept(event -> {
/*     */             if (!this.channel.isActive()) {
/*     */               return;
/*     */             }
/*     */             
/*     */             PacketHandler.logConnectionTimings(this.channel, "Send Common Assets", Level.FINE);
/*     */             
/*     */             this.assets.sent(event.getRequestedAssets());
/*     */             AssetRegistryLoader.sendAssets(this);
/*     */             I18nModule.get().sendTranslations(this, this.language);
/*     */             PacketHandler.logConnectionTimings(this.channel, "Send Config Assets", Level.FINE);
/*     */             write((Packet)new WorldLoadProgress("Loading world...", 0, 0));
/*     */             write((Packet)new WorldLoadFinished());
/* 247 */           }).exceptionally(throwable -> {
/*     */             if (!this.channel.isActive())
/*     */               return null; 
/*     */             disconnect("An exception occurred while trying to login!");
/*     */             throw new RuntimeException("Exception when player was joining", throwable);
/*     */           }));
/* 253 */     setTimeout("send-assets", () -> (future.isDone() || !future.cancel(true)), 120L, TimeUnit.SECONDS);
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull ViewRadius packet) {
/* 257 */     this.clientViewRadiusChunks = MathUtil.ceil((packet.value / 32.0F));
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull PlayerOptions packet) {
/* 261 */     if (!this.receivedRequest) throw new IllegalArgumentException("Hasn't received RequestAssets yet!"); 
/* 262 */     PacketHandler.logConnectionTimings(this.channel, "Player Options", Level.FINE);
/*     */     
/* 264 */     if (!this.channel.isActive())
/*     */       return; 
/* 266 */     if (packet.skin != null) {
/*     */       try {
/* 268 */         CosmeticsModule.get().validateSkin(packet.skin);
/* 269 */       } catch (com.hypixel.hytale.server.core.cosmetics.CosmeticsModule.InvalidSkinException e) {
/* 270 */         disconnect("Invalid skin! " + e.getMessage());
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 275 */     CompletableFuture<Void> future = CompletableFutureUtil._catch(Universe.get().addPlayer(this.channel, this.language, this.protocolVersion, this.uuid, this.username, this.auth, this.clientViewRadiusChunks, packet.skin)
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 280 */         .thenAccept(player -> {
/*     */             if (!this.channel.isActive())
/*     */               return;  PacketHandler.logConnectionTimings(this.channel, "Add To Universe", Level.FINE);
/*     */             clearTimeout();
/* 284 */           }).exceptionally(throwable -> {
/*     */             if (!this.channel.isActive())
/*     */               return null; 
/*     */             disconnect("An exception occurred when adding to the universe!");
/*     */             throw new RuntimeException("Exception when player adding to universe", throwable);
/*     */           }));
/* 290 */     setTimeout("add-to-universe", () -> (future.isDone() || !future.cancel(true)), 60L, TimeUnit.SECONDS);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\handlers\SetupPacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */