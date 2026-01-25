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
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class SetupPacketHandler
/*     */   extends GenericConnectionPacketHandler {
/*     */   private final UUID uuid;
/*     */   private final String username;
/*     */   private final byte[] referralData;
/*  57 */   private int clientViewRadiusChunks = 6; private final HostAddress referralSource; private PlayerCommonAssets assets; private boolean receivedRequest;
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, UUID uuid, String username) {
/*  60 */     this(channel, protocolVersion, language, uuid, username, (byte[])null, (HostAddress)null);
/*     */   }
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, UUID uuid, String username, byte[] referralData, HostAddress referralSource) {
/*  64 */     super(channel, protocolVersion, language);
/*  65 */     this.uuid = uuid;
/*  66 */     this.username = username;
/*  67 */     this.referralData = referralData;
/*  68 */     this.referralSource = referralSource;
/*  69 */     this.auth = null;
/*     */   }
/*     */   
/*     */   public SetupPacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, String language, @Nonnull PlayerAuthentication auth) {
/*  73 */     super(channel, protocolVersion, language);
/*  74 */     this.uuid = auth.getUuid();
/*  75 */     this.username = auth.getUsername();
/*  76 */     this.auth = auth;
/*  77 */     this.referralData = auth.getReferralData();
/*  78 */     this.referralSource = auth.getReferralSource();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public String getIdentifier() {
/*  83 */     return "{Setup(" + NettyUtil.formatRemoteAddress(this.channel) + "), " + this.username + ", " + String.valueOf(this.uuid) + ", " + ((this.auth != null) ? "SECURE" : "INSECURE") + "}";
/*     */   }
/*     */ 
/*     */   
/*     */   public void registered0(@Nonnull PacketHandler oldHandler) {
/*  88 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/*  89 */     enterStage("setup:world-settings", timeouts.getSetupWorldSettings(), () -> (this.assets != null));
/*     */     
/*  91 */     if (this.referralSource != null) {
/*  92 */       HytaleLogger.getLogger().at(Level.INFO).log("Player %s referred from %s:%d with %d bytes of data", this.username, this.referralSource.host, 
/*     */           
/*  94 */           Short.valueOf(this.referralSource.port), 
/*  95 */           Integer.valueOf((this.referralData != null) ? this.referralData.length : 0));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 100 */     PlayerSetupConnectEvent event = (PlayerSetupConnectEvent)HytaleServer.get().getEventBus().dispatchFor(PlayerSetupConnectEvent.class).dispatch((IBaseEvent)new PlayerSetupConnectEvent(this, this.username, this.uuid, this.auth, this.referralData, this.referralSource));
/* 101 */     if (event.isCancelled()) {
/* 102 */       disconnect(event.getReason());
/*     */       
/*     */       return;
/*     */     } 
/* 106 */     ClientReferral clientReferral = event.getClientReferral();
/* 107 */     if (clientReferral != null) {
/* 108 */       writeNoCache((Packet)clientReferral);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 114 */     PlayerRef otherPlayer = Universe.get().getPlayer(this.uuid);
/* 115 */     if (otherPlayer != null) {
/* 116 */       HytaleLogger.getLogger().at(Level.INFO).log("Found match of player %s on %s", this.uuid, otherPlayer.getUsername());
/*     */       
/* 118 */       Channel otherPlayerChannel = otherPlayer.getPacketHandler().getChannel();
/*     */       
/* 120 */       if (NettyUtil.isFromSameOrigin(otherPlayerChannel, this.channel)) {
/*     */ 
/*     */         
/* 123 */         Ref<EntityStore> reference = otherPlayer.getReference();
/* 124 */         if (reference != null) {
/* 125 */           World world = ((EntityStore)reference.getStore().getExternalData()).getWorld();
/* 126 */           if (world != null) {
/* 127 */             CompletableFuture<Void> removalFuture = new CompletableFuture<>();
/*     */             
/* 129 */             world.execute(() -> {
/*     */                   otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
/*     */ 
/*     */                   
/*     */                   world.execute(());
/*     */                 });
/*     */ 
/*     */             
/* 137 */             removalFuture.join();
/*     */           } else {
/* 139 */             otherPlayer.getPacketHandler().disconnect("You logged in again with the account!");
/*     */           }
/*     */         
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 146 */         disconnect("You are already logged in on that account!");
/* 147 */         otherPlayer.sendMessage(Message.translation("server.io.setuppackethandler.otherLoginAttempt"));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 152 */     PacketHandler.logConnectionTimings(this.channel, "Load Player Config", Level.FINE);
/*     */     
/* 154 */     WorldSettings worldSettings = new WorldSettings();
/* 155 */     worldSettings.worldHeight = 320;
/*     */     
/* 157 */     Asset[] requiredAssets = CommonAssetModule.get().getRequiredAssets();
/* 158 */     this.assets = new PlayerCommonAssets(requiredAssets);
/* 159 */     worldSettings.requiredAssets = requiredAssets;
/*     */     
/* 161 */     write((Packet)worldSettings);
/*     */     
/* 163 */     HytaleServerConfig serverConfig = HytaleServer.get().getConfig();
/* 164 */     write((Packet)new ServerInfo(HytaleServer.get().getServerName(), serverConfig.getMotd(), serverConfig.getMaxPlayers()));
/*     */     
/* 166 */     continueStage("setup:assets-request", timeouts.getSetupAssetsRequest(), () -> this.receivedRequest);
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(@Nonnull Packet packet) {
/* 171 */     switch (packet.getId()) { case 1:
/* 172 */         handle((Disconnect)packet); return;
/* 173 */       case 23: handle((RequestAssets)packet); return;
/* 174 */       case 32: handle((ViewRadius)packet); return;
/* 175 */       case 33: handle((PlayerOptions)packet); return; }
/* 176 */      disconnect("Protocol error: unexpected packet " + packet.getId());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void closed(ChannelHandlerContext ctx) {
/* 182 */     super.closed(ctx);
/* 183 */     IEventDispatcher<PlayerSetupDisconnectEvent, PlayerSetupDisconnectEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(PlayerSetupDisconnectEvent.class);
/* 184 */     if (dispatcher.hasListener()) dispatcher.dispatch((IBaseEvent)new PlayerSetupDisconnectEvent(this.username, this.uuid, this.auth, this.disconnectReason));
/*     */     
/* 186 */     if (Constants.SINGLEPLAYER) {
/* 187 */       if (Universe.get().getPlayerCount() == 0) {
/* 188 */         HytaleLogger.getLogger().at(Level.INFO).log("No players left on singleplayer server shutting down!");
/* 189 */         HytaleServer.get().shutdownServer();
/* 190 */       } else if (SingleplayerModule.isOwner(this.auth, this.uuid)) {
/* 191 */         HytaleLogger.getLogger().at(Level.INFO).log("Owner left the singleplayer server shutting down!");
/* 192 */         Universe.get().getPlayers().forEach(p -> p.getPacketHandler().disconnect(this.username + " left! Shutting down singleplayer world!"));
/* 193 */         HytaleServer.get().shutdownServer();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull Disconnect packet) {
/* 199 */     this.disconnectReason.setClientDisconnectType(packet.type);
/* 200 */     HytaleLogger.getLogger().at(Level.INFO).log("%s - %s at %s left with reason: %s - %s", this.uuid, this.username, 
/*     */         
/* 202 */         NettyUtil.formatRemoteAddress(this.channel), packet.type
/* 203 */         .name(), packet.reason);
/* 204 */     ProtocolUtil.closeApplicationConnection(this.channel);
/*     */     
/* 206 */     if (packet.type == DisconnectType.Crash && Constants.SINGLEPLAYER && (
/* 207 */       Universe.get().getPlayerCount() == 0 || SingleplayerModule.isOwner(this.auth, this.uuid))) {
/* 208 */       DumpUtil.dump(true, false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull RequestAssets packet) {
/* 213 */     if (this.receivedRequest) throw new IllegalArgumentException("Received duplicate RequestAssets!"); 
/* 214 */     this.receivedRequest = true;
/*     */     
/* 216 */     PacketHandler.logConnectionTimings(this.channel, "Request Assets", Level.FINE);
/*     */     
/* 218 */     CompletableFuture<Void> future = CompletableFutureUtil._catch(((CompletableFuture)HytaleServer.get().getEventBus().dispatchForAsync(SendCommonAssetsEvent.class).dispatch((IBaseEvent)new SendCommonAssetsEvent(this, packet.assets)))
/* 219 */         .thenAccept(event -> {
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
/* 232 */           }).exceptionally(throwable -> {
/*     */             if (!this.channel.isActive())
/*     */               return null; 
/*     */             disconnect("An exception occurred while trying to login!");
/*     */             throw new RuntimeException("Exception when player was joining", throwable);
/*     */           }));
/* 238 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/* 239 */     continueStage("setup:send-assets", timeouts.getSetupSendAssets(), () -> (future.isDone() || !future.cancel(true)));
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull ViewRadius packet) {
/* 243 */     this.clientViewRadiusChunks = MathUtil.ceil((packet.value / 32.0F));
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull PlayerOptions packet) {
/* 247 */     if (!this.receivedRequest) throw new IllegalArgumentException("Hasn't received RequestAssets yet!"); 
/* 248 */     PacketHandler.logConnectionTimings(this.channel, "Player Options", Level.FINE);
/*     */     
/* 250 */     if (!this.channel.isActive())
/*     */       return; 
/* 252 */     if (packet.skin != null) {
/*     */       try {
/* 254 */         CosmeticsModule.get().validateSkin(packet.skin);
/* 255 */       } catch (com.hypixel.hytale.server.core.cosmetics.CosmeticsModule.InvalidSkinException e) {
/*     */ 
/*     */         
/* 258 */         String msg = "Your skin contains parts that aren't available on this server.\nThis usually happens when assets are out of sync.\n\n" + e.getMessage();
/* 259 */         disconnect(msg);
/*     */         
/*     */         return;
/*     */       } 
/*     */     }
/* 264 */     CompletableFuture<Void> future = CompletableFutureUtil._catch(Universe.get().addPlayer(this.channel, this.language, this.protocolVersion, this.uuid, this.username, this.auth, this.clientViewRadiusChunks, packet.skin)
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 269 */         .thenAccept(player -> {
/*     */             if (!this.channel.isActive())
/*     */               return;  PacketHandler.logConnectionTimings(this.channel, "Add To Universe", Level.FINE);
/*     */             clearTimeout();
/* 273 */           }).exceptionally(throwable -> {
/*     */             if (!this.channel.isActive())
/*     */               return null; 
/*     */             disconnect("An exception occurred when adding to the universe!");
/*     */             throw new RuntimeException("Exception when player adding to universe", throwable);
/*     */           }));
/* 279 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/* 280 */     continueStage("setup:add-to-universe", timeouts.getSetupAddToUniverse(), () -> (future.isDone() || !future.cancel(true)));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\handlers\SetupPacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */