/*      */ package com.hypixel.hytale.server.core.io.handlers.game;
/*      */ import com.hypixel.hytale.component.Component;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.ComponentType;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.component.Store;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.math.util.MathUtil;
/*      */ import com.hypixel.hytale.math.vector.Transform;
/*      */ import com.hypixel.hytale.math.vector.Vector3d;
/*      */ import com.hypixel.hytale.math.vector.Vector3i;
/*      */ import com.hypixel.hytale.protocol.BlockRotation;
/*      */ import com.hypixel.hytale.protocol.GameMode;
/*      */ import com.hypixel.hytale.protocol.HostAddress;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.protocol.packets.camera.RequestFlyCameraMode;
/*      */ import com.hypixel.hytale.protocol.packets.camera.SetFlyCameraMode;
/*      */ import com.hypixel.hytale.protocol.packets.connection.Disconnect;
/*      */ import com.hypixel.hytale.protocol.packets.connection.Pong;
/*      */ import com.hypixel.hytale.protocol.packets.entities.MountMovement;
/*      */ import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
/*      */ import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
/*      */ import com.hypixel.hytale.protocol.packets.interface_.ChatMessage;
/*      */ import com.hypixel.hytale.protocol.packets.interface_.CustomPageEvent;
/*      */ import com.hypixel.hytale.protocol.packets.interface_.UpdateLanguage;
/*      */ import com.hypixel.hytale.protocol.packets.machinima.RequestMachinimaActorModel;
/*      */ import com.hypixel.hytale.protocol.packets.machinima.UpdateMachinimaScene;
/*      */ import com.hypixel.hytale.protocol.packets.player.ClientMovement;
/*      */ import com.hypixel.hytale.protocol.packets.player.ClientPlaceBlock;
/*      */ import com.hypixel.hytale.protocol.packets.player.ClientReady;
/*      */ import com.hypixel.hytale.protocol.packets.player.MouseInteraction;
/*      */ import com.hypixel.hytale.protocol.packets.player.RemoveMapMarker;
/*      */ import com.hypixel.hytale.protocol.packets.player.SyncPlayerPreferences;
/*      */ import com.hypixel.hytale.protocol.packets.serveraccess.SetServerAccess;
/*      */ import com.hypixel.hytale.protocol.packets.serveraccess.UpdateServerAccess;
/*      */ import com.hypixel.hytale.protocol.packets.setup.RequestAssets;
/*      */ import com.hypixel.hytale.protocol.packets.setup.ViewRadius;
/*      */ import com.hypixel.hytale.protocol.packets.window.ClientOpenWindow;
/*      */ import com.hypixel.hytale.protocol.packets.window.CloseWindow;
/*      */ import com.hypixel.hytale.protocol.packets.window.SendWindowAction;
/*      */ import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
/*      */ import com.hypixel.hytale.protocol.packets.world.SetPaused;
/*      */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*      */ import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapMarker;
/*      */ import com.hypixel.hytale.protocol.packets.worldmap.TeleportToWorldMapPosition;
/*      */ import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapVisible;
/*      */ import com.hypixel.hytale.server.core.Constants;
/*      */ import com.hypixel.hytale.server.core.HytaleServer;
/*      */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*      */ import com.hypixel.hytale.server.core.Message;
/*      */ import com.hypixel.hytale.server.core.NameMatching;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*      */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*      */ import com.hypixel.hytale.server.core.auth.PlayerAuthentication;
/*      */ import com.hypixel.hytale.server.core.console.ConsoleModule;
/*      */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.windows.ValidatedWindow;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
/*      */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*      */ import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
/*      */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*      */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*      */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*      */ import com.hypixel.hytale.server.core.io.ProtocolVersion;
/*      */ import com.hypixel.hytale.server.core.io.netty.NettyUtil;
/*      */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerCreativeSettings;
/*      */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
/*      */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
/*      */ import com.hypixel.hytale.server.core.modules.entity.teleport.PendingTeleport;
/*      */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*      */ import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.BlockPlaceUtils;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
/*      */ import com.hypixel.hytale.server.core.modules.singleplayer.SingleplayerModule;
/*      */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*      */ import com.hypixel.hytale.server.core.universe.Universe;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.util.MessageUtil;
/*      */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*      */ import com.hypixel.hytale.server.core.util.ValidateUtil;
/*      */ import io.netty.channel.Channel;
/*      */ import io.netty.channel.ChannelHandlerContext;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.util.Deque;
/*      */ import java.util.List;
/*      */ import java.util.Objects;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.function.Supplier;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ 
/*      */ public class GamePacketHandler extends GenericPacketHandler implements IPacketHandler {
/*      */   @Nonnull
/*  107 */   private final Deque<SyncInteractionChain> interactionPacketQueue = new ConcurrentLinkedDeque<>();
/*      */ 
/*      */   
/*      */   private static final double RELATIVE_POSITION_DELTA_SCALE = 10000.0D;
/*      */ 
/*      */   
/*      */   private PlayerRef playerRef;
/*      */   
/*      */   @Deprecated
/*      */   private Player playerComponent;
/*      */ 
/*      */   
/*      */   public GamePacketHandler(@Nonnull Channel channel, @Nonnull ProtocolVersion protocolVersion, @Nonnull PlayerAuthentication auth) {
/*  120 */     super(channel, protocolVersion);
/*  121 */     this.auth = auth;
/*  122 */     ServerManager.get().populateSubPacketHandlers(this);
/*  123 */     registerHandlers();
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   public Deque<SyncInteractionChain> getInteractionPacketQueue() {
/*  128 */     return this.interactionPacketQueue;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public PlayerRef getPlayerRef() {
/*  134 */     return this.playerRef;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPlayerRef(@Nonnull PlayerRef playerRef, @Nonnull Player playerComponent) {
/*  144 */     this.playerRef = playerRef;
/*  145 */     this.playerComponent = playerComponent;
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   public String getIdentifier() {
/*  150 */     return "{Playing(" + NettyUtil.formatRemoteAddress(this.channel) + "), " + ((this.playerRef != null) ? (String.valueOf(this.playerRef.getUuid()) + ", " + String.valueOf(this.playerRef.getUuid())) : "null player") + "}";
/*      */   }
/*      */ 
/*      */   
/*      */   protected void registered0(PacketHandler oldHandler) {
/*  155 */     HytaleServerConfig.TimeoutProfile timeouts = HytaleServer.get().getConfig().getConnectionTimeouts();
/*  156 */     enterStage("play", timeouts.getPlay());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void registerHandlers() {
/*  163 */     registerHandler(1, p -> handle((Disconnect)p));
/*  164 */     registerHandler(3, p -> handlePong((Pong)p));
/*  165 */     registerHandler(108, p -> handle((ClientMovement)p));
/*  166 */     registerHandler(211, p -> handle((ChatMessage)p));
/*  167 */     registerHandler(23, p -> handle((RequestAssets)p));
/*  168 */     registerHandler(219, p -> handle((CustomPageEvent)p));
/*  169 */     registerHandler(32, p -> handle((ViewRadius)p));
/*  170 */     registerHandler(232, p -> handle((UpdateLanguage)p));
/*  171 */     registerHandler(111, p -> handle((MouseInteraction)p));
/*  172 */     registerHandler(251, p -> handle((UpdateServerAccess)p));
/*  173 */     registerHandler(252, p -> handle((SetServerAccess)p));
/*  174 */     registerHandler(204, p -> handle((ClientOpenWindow)p));
/*  175 */     registerHandler(203, p -> handle((SendWindowAction)p));
/*  176 */     registerHandler(202, p -> handle((CloseWindow)p));
/*  177 */     registerHandler(260, p -> handle((RequestMachinimaActorModel)p));
/*  178 */     registerHandler(262, p -> handle((UpdateMachinimaScene)p));
/*  179 */     registerHandler(105, p -> handle((ClientReady)p));
/*  180 */     registerHandler(166, p -> handle((MountMovement)p));
/*  181 */     registerHandler(116, p -> handle((SyncPlayerPreferences)p));
/*  182 */     registerHandler(117, p -> handle((ClientPlaceBlock)p));
/*      */     
/*  184 */     registerHandler(119, p -> handle((RemoveMapMarker)p));
/*      */     
/*  186 */     registerHandler(243, p -> handle((UpdateWorldMapVisible)p));
/*  187 */     registerHandler(244, p -> handle((TeleportToWorldMapMarker)p));
/*  188 */     registerHandler(245, p -> handle((TeleportToWorldMapPosition)p));
/*      */     
/*  190 */     registerHandler(290, p -> handle((SyncInteractionChains)p));
/*      */     
/*  192 */     registerHandler(158, p -> handle((SetPaused)p));
/*      */     
/*  194 */     registerHandler(282, p -> handle((RequestFlyCameraMode)p));
/*      */     
/*  196 */     this.packetHandlers.forEach(SubPacketHandler::registerHandlers);
/*      */   }
/*      */ 
/*      */   
/*      */   public void closed(ChannelHandlerContext ctx) {
/*  201 */     super.closed(ctx);
/*  202 */     Universe.get().removePlayer(this.playerRef);
/*      */   }
/*      */ 
/*      */   
/*      */   public void disconnect(@Nonnull String message) {
/*  207 */     this.disconnectReason.setServerDisconnectReason(message);
/*      */     
/*  209 */     if (this.playerRef != null) {
/*      */       
/*  211 */       HytaleLogger.getLogger().at(Level.INFO).log("Disconnecting %s at %s with the message: %s", this.playerRef
/*  212 */           .getUsername(), 
/*  213 */           NettyUtil.formatRemoteAddress(this.channel), message);
/*      */ 
/*      */ 
/*      */       
/*  217 */       disconnect0(message);
/*      */       
/*  219 */       Universe.get().removePlayer(this.playerRef);
/*      */     } else {
/*  221 */       super.disconnect(message);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull Disconnect packet) {
/*  231 */     this.disconnectReason.setClientDisconnectType(packet.type);
/*      */     
/*  233 */     HytaleLogger.getLogger().at(Level.INFO).log("%s - %s at %s left with reason: %s - %s", this.playerRef
/*  234 */         .getUuid(), this.playerRef
/*  235 */         .getUsername(), 
/*  236 */         NettyUtil.formatRemoteAddress(this.channel), packet.type
/*  237 */         .name(), packet.reason);
/*      */ 
/*      */     
/*  240 */     ProtocolUtil.closeApplicationConnection(this.channel);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull MouseInteraction packet) {
/*  249 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  250 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  254 */     Store<EntityStore> store = ref.getStore();
/*  255 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  257 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           InteractionModule.get().doMouseInteraction(ref, (ComponentAccessor)store, packet, playerComponent, this.playerRef);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull ClientMovement packet) {
/*  274 */     if (packet.absolutePosition != null && !ValidateUtil.isSafePosition(packet.absolutePosition)) {
/*  275 */       disconnect("Sent impossible position data!");
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  280 */     if ((packet.bodyOrientation != null && !ValidateUtil.isSafeDirection(packet.bodyOrientation)) || (packet.lookOrientation != null && !ValidateUtil.isSafeDirection(packet.lookOrientation))) {
/*  281 */       disconnect("Sent impossible orientation data!");
/*      */       
/*      */       return;
/*      */     } 
/*  285 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  286 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  292 */     Store<EntityStore> store = ref.getStore();
/*  293 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  295 */     world.execute(() -> {
/*      */           if (!ref.isValid()) {
/*      */             return;
/*      */           }
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           if (playerComponent.isWaitingForClientReady()) {
/*      */             return;
/*      */           }
/*      */           PlayerInput playerInputComponent = (PlayerInput)store.getComponent(ref, PlayerInput.getComponentType());
/*      */           if (playerInputComponent == null) {
/*      */             return;
/*      */           }
/*      */           if (packet.movementStates != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.SetMovementStates(packet.movementStates));
/*      */           }
/*      */           if (packet.velocity != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.SetClientVelocity(packet.velocity));
/*      */           }
/*      */           PendingTeleport pendingTeleport = (PendingTeleport)store.getComponent(ref, PendingTeleport.getComponentType());
/*      */           if (pendingTeleport != null) {
/*      */             if (packet.teleportAck == null) {
/*      */               return;
/*      */             }
/*      */             switch (pendingTeleport.validate(packet.teleportAck.teleportId, packet.absolutePosition)) {
/*      */               case INVALID_ID:
/*      */                 disconnect("Incorrect teleportId");
/*      */                 return;
/*      */               case INVALID_POSITION:
/*      */                 disconnect("Invalid teleport");
/*      */                 return;
/*      */             } 
/*      */             if (!pendingTeleport.isEmpty()) {
/*      */               return;
/*      */             }
/*      */             store.removeComponent(ref, PendingTeleport.getComponentType());
/*      */           } 
/*      */           if (packet.mountedTo != 0) {
/*      */             if (packet.mountedTo != playerInputComponent.getMountId()) {
/*      */               return;
/*      */             }
/*      */             if (packet.riderMovementStates != null) {
/*      */               playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.SetRiderMovementStates(packet.riderMovementStates));
/*      */             }
/*      */           } 
/*      */           if (packet.bodyOrientation != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.SetBody(packet.bodyOrientation));
/*      */           }
/*      */           if (packet.lookOrientation != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.SetHead(packet.lookOrientation));
/*      */           }
/*      */           if (packet.wishMovement != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.WishMovement(packet.wishMovement.x, packet.wishMovement.y, packet.wishMovement.z));
/*      */           }
/*      */           if (packet.absolutePosition != null) {
/*      */             playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.AbsoluteMovement(packet.absolutePosition.x, packet.absolutePosition.y, packet.absolutePosition.z));
/*      */           } else if (packet.relativePosition != null) {
/*      */             if (packet.relativePosition.x != 0 || packet.relativePosition.y != 0 || packet.relativePosition.z != 0 || packet.movementStates != null) {
/*      */               playerInputComponent.queue((PlayerInput.InputUpdate)new PlayerInput.RelativeMovement(packet.relativePosition.x / 10000.0D, packet.relativePosition.y / 10000.0D, packet.relativePosition.z / 10000.0D));
/*      */             }
/*      */           } 
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull ChatMessage packet) {
/*  384 */     if (packet.message == null || packet.message.isEmpty()) {
/*  385 */       disconnect("Invalid chat message packet! Message was empty.");
/*      */       
/*      */       return;
/*      */     } 
/*  389 */     String message = packet.message;
/*  390 */     char firstChar = message.charAt(0);
/*  391 */     if (firstChar == '/') {
/*  392 */       CommandManager.get().handleCommand((CommandSender)this.playerComponent, message.substring(1));
/*  393 */     } else if (firstChar == '.') {
/*  394 */       this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.localCommandDenied")
/*  395 */           .param("msg", message));
/*      */     } else {
/*  397 */       Ref<EntityStore> ref = this.playerRef.getReference();
/*  398 */       if (ref == null || !ref.isValid())
/*      */         return; 
/*  400 */       UUID playerUUID = this.playerRef.getUuid();
/*      */       
/*  402 */       ObjectArrayList objectArrayList = new ObjectArrayList(Universe.get().getPlayers());
/*  403 */       objectArrayList.removeIf(targetPlayerRef -> targetPlayerRef.getHiddenPlayersManager().isPlayerHidden(playerUUID));
/*      */       
/*  405 */       ((CompletableFuture)HytaleServer.get().getEventBus().dispatchForAsync(PlayerChatEvent.class)
/*  406 */         .dispatch((IBaseEvent)new PlayerChatEvent(this.playerRef, (List)objectArrayList, message)))
/*  407 */         .whenComplete((playerChatEvent, throwable) -> {
/*      */             if (throwable != null) {
/*      */               ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(throwable)).log("An error occurred while dispatching PlayerChatEvent for player %s", this.playerRef.getUsername());
/*      */               return;
/*      */             } 
/*      */             if (playerChatEvent.isCancelled()) {
/*      */               return;
/*      */             }
/*      */             Message sentMessage = playerChatEvent.getFormatter().format(this.playerRef, playerChatEvent.getContent());
/*      */             HytaleLogger.getLogger().at(Level.INFO).log(MessageUtil.toAnsiString(sentMessage).toAnsi(ConsoleModule.get().getTerminal()));
/*      */             for (PlayerRef targetPlayerRef : playerChatEvent.getTargets()) {
/*      */               targetPlayerRef.sendMessage(sentMessage);
/*      */             }
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull RequestAssets packet) {
/*  436 */     CommonAssetModule.get().sendAssetsToPlayer((PacketHandler)this, packet.assets, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull CustomPageEvent packet) {
/*  446 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  447 */     if (ref == null || !ref.isValid()) {
/*      */ 
/*      */       
/*  450 */       this.playerRef.getPacketHandler().writeNoCache((Packet)new SetPage(Page.None, true));
/*      */       
/*      */       return;
/*      */     } 
/*  454 */     Store<EntityStore> store = ref.getStore();
/*  455 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  457 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           PageManager pageManager = playerComponent.getPageManager();
/*      */           pageManager.handleEvent(ref, store, packet);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull ViewRadius packet) {
/*  472 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  473 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  477 */     Store<EntityStore> store = ref.getStore();
/*  478 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  480 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           EntityTrackerSystems.EntityViewer entityViewerComponent = (EntityTrackerSystems.EntityViewer)store.getComponent(ref, EntityTrackerSystems.EntityViewer.getComponentType());
/*      */           assert entityViewerComponent != null;
/*      */           int viewRadiusChunks = MathUtil.ceil((packet.value / 32.0F));
/*      */           playerComponent.setClientViewRadius(viewRadiusChunks);
/*      */           entityViewerComponent.viewRadiusBlocks = playerComponent.getViewRadius() * 32;
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull UpdateLanguage packet) {
/*  500 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  501 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  505 */     this.playerRef.setLanguage(packet.language);
/*  506 */     I18nModule.get().sendTranslations((PacketHandler)this, packet.language);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void handle(@Nonnull ClientOpenWindow packet) {
/*  515 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  516 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  520 */     Supplier<? extends Window> supplier = (Supplier<? extends Window>)Window.CLIENT_REQUESTABLE_WINDOW_TYPES.get(packet.type);
/*  521 */     if (supplier == null) throw new RuntimeException("Unable to process ClientOpenWindow packet. Window type is not supported!");
/*      */     
/*  523 */     Store<EntityStore> store = ref.getStore();
/*  524 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  526 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           UpdateWindow updateWindowPacket = playerComponent.getWindowManager().clientOpenWindow(ref, supplier.get(), store);
/*      */           if (updateWindowPacket != null) {
/*      */             writeNoCache((Packet)updateWindowPacket);
/*      */           }
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull SendWindowAction packet) {
/*  541 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  542 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  546 */     Store<EntityStore> store = ref.getStore();
/*  547 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  549 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           Window window = playerComponent.getWindowManager().getWindow(packet.id);
/*      */           if (window == null) {
/*      */             return;
/*      */           }
/*      */           if (window instanceof ValidatedWindow) {
/*      */             ValidatedWindow validatedWindow = (ValidatedWindow)window;
/*      */             if (!validatedWindow.validate(ref, (ComponentAccessor)store)) {
/*      */               window.close(ref, (ComponentAccessor)store);
/*      */               return;
/*      */             } 
/*      */           } 
/*      */           window.handleAction(this.playerRef.getReference(), store, packet.action);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull SyncPlayerPreferences packet) {
/*  573 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  574 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  578 */     Store<EntityStore> store = ref.getStore();
/*  579 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  581 */     world.execute(() -> {
/*      */           ComponentType<EntityStore, PlayerSettings> componentType = EntityModule.get().getPlayerSettingsComponentType();
/*      */           store.putComponent(ref, componentType, (Component)new PlayerSettings(packet.showEntityMarkers, packet.armorItemsPreferredPickupLocation, packet.weaponAndToolItemsPreferredPickupLocation, packet.usableItemsItemsPreferredPickupLocation, packet.solidBlockItemsPreferredPickupLocation, packet.miscItemsPreferredPickupLocation, new PlayerCreativeSettings(packet.allowNPCDetection, packet.respondToHit), packet.hideHelmet, packet.hideCuirass, packet.hideGauntlets, packet.hidePants));
/*      */           ((Player)store.getComponent(ref, Player.getComponentType())).invalidateEquipmentNetwork();
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull ClientPlaceBlock packet) {
/*  598 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  599 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  603 */     Store<EntityStore> store = ref.getStore();
/*  604 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  606 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           Inventory inventory = playerComponent.getInventory();
/*      */           Vector3i targetBlock = new Vector3i(packet.position.x, packet.position.y, packet.position.z);
/*      */           BlockRotation blockRotation = new BlockRotation(packet.rotation.rotationYaw, packet.rotation.rotationPitch, packet.rotation.rotationRoll);
/*      */           TransformComponent transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*      */           if (transformComponent != null && playerComponent.getGameMode() != GameMode.Creative) {
/*      */             Vector3d position = transformComponent.getPosition();
/*      */             Vector3d blockCenter = new Vector3d(targetBlock.x + 0.5D, targetBlock.y + 0.5D, targetBlock.z + 0.5D);
/*      */             if (position.distanceSquaredTo(blockCenter) > 36.0D) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
/*      */           long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
/*      */           Ref<ChunkStore> chunkReference = ((ChunkStore)chunkStore.getExternalData()).getChunkReference(chunkIndex);
/*      */           if (chunkReference == null) {
/*      */             return;
/*      */           }
/*      */           BlockChunk blockChunk = (BlockChunk)chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
/*      */           if (blockChunk == null) {
/*      */             return;
/*      */           }
/*      */           BlockSection section = blockChunk.getSectionAtBlockY(targetBlock.y);
/*      */           if (section == null) {
/*      */             return;
/*      */           }
/*      */           ItemStack itemInHand = playerComponent.getInventory().getItemInHand();
/*      */           if (itemInHand == null) {
/*      */             section.invalidateBlock(targetBlock.x, targetBlock.y, targetBlock.z);
/*      */             return;
/*      */           } 
/*      */           String heldBlockKey = itemInHand.getBlockKey();
/*      */           if (heldBlockKey == null) {
/*      */             section.invalidateBlock(targetBlock.x, targetBlock.y, targetBlock.z);
/*      */             return;
/*      */           } 
/*      */           if (packet.placedBlockId != -1) {
/*      */             String clientPlacedBlockTypeKey = ((BlockType)BlockType.getAssetMap().getAsset(packet.placedBlockId)).getId();
/*      */             BlockType heldBlockType = (BlockType)BlockType.getAssetMap().getAsset(heldBlockKey);
/*      */             if (heldBlockType != null && BlockPlaceUtils.canPlaceBlock(heldBlockType, clientPlacedBlockTypeKey)) {
/*      */               heldBlockKey = clientPlacedBlockTypeKey;
/*      */             }
/*      */           } 
/*      */           BlockPlaceUtils.placeBlock(ref, itemInHand, heldBlockKey, inventory.getHotbar(), Vector3i.ZERO, targetBlock, blockRotation, inventory, inventory.getActiveHotbarSlot(), (playerComponent.getGameMode() != GameMode.Creative), chunkReference, (ComponentAccessor)chunkStore, (ComponentAccessor)store);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull RemoveMapMarker packet) {
/*  680 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  681 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  685 */     Store<EntityStore> store = ref.getStore();
/*  686 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  688 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           PlayerWorldData perWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
/*      */           perWorldData.removeLastDeath(packet.markerId);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull CloseWindow packet) {
/*  703 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  704 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  708 */     Store<EntityStore> store = ref.getStore();
/*  709 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  711 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           playerComponent.getWindowManager().closeWindow(ref, packet.id, (ComponentAccessor)store);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull UpdateServerAccess packet) {
/*  725 */     if (!Constants.SINGLEPLAYER) throw new IllegalArgumentException("UpdateServerAccess can only be used in singleplayer!"); 
/*  726 */     if (!SingleplayerModule.isOwner(this.playerRef)) {
/*  727 */       throw new IllegalArgumentException("UpdateServerAccess can only be by the owner of the singleplayer world!");
/*      */     }
/*      */     
/*  730 */     List<InetSocketAddress> publicAddresses = new CopyOnWriteArrayList<>();
/*      */     
/*  732 */     for (HostAddress host : packet.hosts) {
/*  733 */       publicAddresses.add(InetSocketAddress.createUnresolved(host.host, host.port & 0xFFFF));
/*      */     }
/*      */     
/*  736 */     SingleplayerModule singleplayerModule = SingleplayerModule.get();
/*  737 */     singleplayerModule.setPublicAddresses(publicAddresses);
/*  738 */     singleplayerModule.updateAccess(packet.access);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull SetServerAccess packet) {
/*  747 */     if (!Constants.SINGLEPLAYER) throw new IllegalArgumentException("SetServerAccess can only be used in singleplayer!"); 
/*  748 */     if (!SingleplayerModule.isOwner(this.playerRef)) {
/*  749 */       throw new IllegalArgumentException("SetServerAccess can only be used by the owner of the singleplayer world!");
/*      */     }
/*      */     
/*  752 */     HytaleServerConfig config = HytaleServer.get().getConfig();
/*  753 */     if (config != null) {
/*  754 */       config.setPassword((packet.password != null) ? packet.password : "");
/*  755 */       HytaleServerConfig.save(config);
/*      */     } 
/*      */     
/*  758 */     SingleplayerModule.get().requestServerAccess(packet.access);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull RequestMachinimaActorModel packet) {
/*  767 */     ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset(packet.modelId);
/*      */     
/*  769 */     writeNoCache((Packet)new SetMachinimaActorModel(
/*  770 */           Model.createUnitScaleModel(modelAsset).toPacket(), packet.sceneName, packet.actorName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull UpdateMachinimaScene packet) {
/*  781 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  782 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  786 */     Store<EntityStore> store = ref.getStore();
/*  787 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  789 */     world.execute(() -> {
/*      */           UpdateMachinimaScene updatePacket = new UpdateMachinimaScene(this.playerRef.getUsername(), packet.sceneName, packet.frame, packet.updateType, packet.scene);
/*      */           if ("*".equals(packet.player)) {
/*      */             for (PlayerRef otherPlayerRef : world.getPlayerRefs()) {
/*      */               if (!Objects.equals(otherPlayerRef, this.playerRef)) {
/*      */                 otherPlayerRef.getPacketHandler().writeNoCache((Packet)updatePacket);
/*      */               }
/*      */             } 
/*      */             this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.sceneUpdateSent"));
/*      */           } else {
/*      */             PlayerRef target = (PlayerRef)NameMatching.DEFAULT.find(Universe.get().getPlayers(), packet.player, PlayerRef::getUsername);
/*      */             if (target != null && ((EntityStore)target.getReference().getStore().getExternalData()).getWorld() == world) {
/*      */               target.getPacketHandler().write((Packet)updatePacket);
/*      */               this.playerRef.sendMessage(Message.translation("server.io.gamepackethander.sceneUpdateSentToPlayer").param("name", target.getUsername()));
/*      */             } else {
/*      */               this.playerRef.sendMessage(Message.translation("server.io.gamepackethandler.playerNotFound").param("name", packet.player));
/*      */             } 
/*      */           } 
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull ClientReady packet) {
/*  819 */     HytaleLogger.getLogger().at(Level.WARNING).log("%s: Received %s", getIdentifier(), packet);
/*      */     
/*  821 */     CompletableFuture<Void> future = this.clientReadyForChunksFuture;
/*  822 */     if (packet.readyForChunks && !packet.readyForGameplay && future != null) {
/*  823 */       this.clientReadyForChunksFutureStack = null;
/*  824 */       this.clientReadyForChunksFuture = null;
/*      */       
/*  826 */       future.completeAsync(() -> null);
/*      */     } 
/*      */     
/*  829 */     if (packet.readyForGameplay) {
/*  830 */       Ref<EntityStore> ref = this.playerRef.getReference();
/*  831 */       if (ref == null || !ref.isValid()) {
/*      */         return;
/*      */       }
/*      */       
/*  835 */       Store<EntityStore> store = ref.getStore();
/*  836 */       World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */       
/*  838 */       world.execute(() -> {
/*      */             Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */             assert playerComponent != null;
/*      */             playerComponent.handleClientReady(false);
/*      */           });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull UpdateWorldMapVisible packet) {
/*  853 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  854 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  858 */     Store<EntityStore> store = ref.getStore();
/*  859 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  861 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           playerComponent.getWorldMapTracker().setClientHasWorldMapVisible(packet.visible);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull TeleportToWorldMapMarker packet) {
/*  875 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  876 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  880 */     Store<EntityStore> store = ref.getStore();
/*  881 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  883 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           WorldMapTracker worldMapTracker = playerComponent.getWorldMapTracker();
/*      */           if (!worldMapTracker.isAllowTeleportToMarkers()) {
/*      */             disconnect("You are not allowed to use TeleportToWorldMapMarker!");
/*      */             return;
/*      */           } 
/*      */           MapMarker marker = (MapMarker)worldMapTracker.getSentMarkers().get(packet.id);
/*      */           if (marker != null) {
/*      */             Transform transform = PositionUtil.toTransform(marker.transform);
/*      */             Teleport teleportComponent = Teleport.createForPlayer(transform);
/*      */             world.getEntityStore().getStore().addComponent(this.playerRef.getReference(), Teleport.getComponentType(), (Component)teleportComponent);
/*      */           } 
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull TeleportToWorldMapPosition packet) {
/*  914 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  915 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  919 */     Store<EntityStore> store = ref.getStore();
/*  920 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  922 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           WorldMapTracker worldMapTracker = playerComponent.getWorldMapTracker();
/*      */           if (!worldMapTracker.isAllowTeleportToCoordinates()) {
/*      */             disconnect("You are not allowed to use TeleportToWorldMapMarker!");
/*      */             return;
/*      */           } 
/*      */           world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunkFromBlock(packet.x, packet.y)).thenAcceptAsync((), (Executor)world);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull SyncInteractionChains packet) {
/*  956 */     Collections.addAll(this.interactionPacketQueue, packet.updates);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull MountMovement packet) {
/*  965 */     Ref<EntityStore> ref = this.playerRef.getReference();
/*  966 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/*  970 */     Store<EntityStore> store = ref.getStore();
/*  971 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  973 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(playerComponent.getMountEntityId());
/*      */           if (entityReference == null || !entityReference.isValid()) {
/*      */             return;
/*      */           }
/*      */           TransformComponent transformComponent = (TransformComponent)store.getComponent(entityReference, TransformComponent.getComponentType());
/*      */           assert transformComponent != null;
/*      */           transformComponent.setPosition(PositionUtil.toVector3d(packet.absolutePosition));
/*      */           transformComponent.setRotation(PositionUtil.toRotation(packet.bodyOrientation));
/*      */           MovementStatesComponent movementStatesComponent = (MovementStatesComponent)store.getComponent(entityReference, MovementStatesComponent.getComponentType());
/*      */           assert movementStatesComponent != null;
/*      */           movementStatesComponent.setMovementStates(packet.movementStates);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull SetPaused packet) {
/*  999 */     Ref<EntityStore> ref = this.playerRef.getReference();
/* 1000 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1005 */     Store<EntityStore> store = ref.getStore();
/* 1006 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 1007 */     world.execute(() -> {
/*      */           if (world.getPlayerCount() != 1 || !Constants.SINGLEPLAYER) {
/*      */             return;
/*      */           }
/*      */           world.setPaused(packet.paused);
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void handle(@Nonnull RequestFlyCameraMode packet) {
/* 1023 */     Ref<EntityStore> ref = this.playerRef.getReference();
/* 1024 */     if (ref == null || !ref.isValid()) {
/*      */       return;
/*      */     }
/*      */     
/* 1028 */     Store<EntityStore> store = ref.getStore();
/* 1029 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/* 1031 */     world.execute(() -> {
/*      */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           if (playerComponent.hasPermission("hytale.camera.flycam")) {
/*      */             writeNoCache((Packet)new SetFlyCameraMode(packet.entering));
/*      */             if (packet.entering) {
/*      */               this.playerRef.sendMessage(Message.translation("server.general.flyCamera.enabled"));
/*      */             } else {
/*      */               this.playerRef.sendMessage(Message.translation("server.general.flyCamera.disabled"));
/*      */             } 
/*      */           } else {
/*      */             this.playerRef.sendMessage(Message.translation("server.general.flyCamera.noPermission"));
/*      */           } 
/*      */         });
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\handlers\game\GamePacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */