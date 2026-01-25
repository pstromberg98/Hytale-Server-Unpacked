/*      */ package com.hypixel.hytale.server.core.entity.entities;
/*      */ import com.hypixel.hytale.codec.Codec;
/*      */ import com.hypixel.hytale.codec.KeyedCodec;
/*      */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*      */ import com.hypixel.hytale.component.Component;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.Holder;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.component.Store;
/*      */ import com.hypixel.hytale.component.system.EcsEvent;
/*      */ import com.hypixel.hytale.event.IBaseEvent;
/*      */ import com.hypixel.hytale.event.IEventDispatcher;
/*      */ import com.hypixel.hytale.math.shape.Box;
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.math.vector.Transform;
/*      */ import com.hypixel.hytale.math.vector.Vector3d;
/*      */ import com.hypixel.hytale.math.vector.Vector3f;
/*      */ import com.hypixel.hytale.metrics.MetricResults;
/*      */ import com.hypixel.hytale.metrics.MetricsRegistry;
/*      */ import com.hypixel.hytale.protocol.GameMode;
/*      */ import com.hypixel.hytale.protocol.InteractionType;
/*      */ import com.hypixel.hytale.protocol.MovementStates;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.protocol.SavedMovementStates;
/*      */ import com.hypixel.hytale.protocol.SoundCategory;
/*      */ import com.hypixel.hytale.protocol.packets.player.SetBlockPlacementOverride;
/*      */ import com.hypixel.hytale.protocol.packets.player.SetGameMode;
/*      */ import com.hypixel.hytale.server.core.HytaleServer;
/*      */ import com.hypixel.hytale.server.core.Message;
/*      */ import com.hypixel.hytale.server.core.asset.type.gamemode.GameModeType;
/*      */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*      */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*      */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*      */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*      */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*      */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.CameraManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.HotbarManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
/*      */ import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
/*      */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*      */ import com.hypixel.hytale.server.core.event.events.ecs.ChangeGameModeEvent;
/*      */ import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
/*      */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*      */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*      */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*      */ import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionModule;
/*      */ import com.hypixel.hytale.server.core.modules.collision.CollisionResult;
/*      */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.RespondToHit;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.player.ChunkTracker;
/*      */ import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*      */ import com.hypixel.hytale.server.core.modules.physics.component.Velocity;
/*      */ import com.hypixel.hytale.server.core.permissions.PermissionHolder;
/*      */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*      */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*      */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
/*      */ import it.unimi.dsi.fastutil.longs.LongIterator;
/*      */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*      */ import java.util.Arrays;
/*      */ import java.util.List;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.CompletionStage;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.ScheduledFuture;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicReference;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ public class Player extends LivingEntity implements CommandSender, PermissionHolder, MetricProvider {
/*      */   @Nonnull
/*   91 */   public static final MetricsRegistry<Player> METRICS_REGISTRY = (new MetricsRegistry())
/*   92 */     .register("Uuid", Entity::getUuid, (Codec)Codec.UUID_STRING)
/*   93 */     .register("ClientViewRadius", Player::getClientViewRadius, (Codec)Codec.INTEGER);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*   99 */   public static final KeyedCodec<PlayerConfigData> PLAYER_CONFIG_DATA = new KeyedCodec("PlayerData", (Codec)PlayerConfigData.CODEC);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static final BuilderCodec<Player> CODEC;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final int DEFAULT_VIEW_RADIUS_CHUNKS = 6;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static {
/*  129 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Player.class, Player::new, LivingEntity.CODEC).append(PLAYER_CONFIG_DATA, (player, data) -> player.data = data, player -> player.data).add()).append(new KeyedCodec("BlockPlacementOverride", (Codec)Codec.BOOLEAN), (player, blockPlacementOverride) -> player.overrideBlockPlacementRestrictions = blockPlacementOverride.booleanValue(), player -> Boolean.valueOf(player.overrideBlockPlacementRestrictions)).add()).append(new KeyedCodec("HotbarManager", (Codec)HotbarManager.CODEC), (player, hotbarManager) -> player.hotbarManager = hotbarManager, player -> player.hotbarManager).add()).appendInherited(new KeyedCodec("GameMode", (Codec)ProtocolCodecs.GAMEMODE_LEGACY), (player, s) -> player.gameMode = s, player -> player.gameMode, (player, parent) -> player.gameMode = parent.gameMode).documentation("The last known game-mode of the entity.").add()).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static ComponentType<EntityStore, Player> getComponentType() {
/*  141 */     return EntityModule.get().getPlayerComponentType();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  147 */   public static final long RESPAWN_INVULNERABILITY_TIME_NANOS = TimeUnit.MILLISECONDS.toNanos(3000L);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final long MAX_TELEPORT_INVULNERABILITY_MILLIS = 10000L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated(forRemoval = true)
/*      */   private PlayerRef playerRef;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  164 */   private PlayerConfigData data = new PlayerConfigData();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  170 */   private final WorldMapTracker worldMapTracker = new WorldMapTracker(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  176 */   private final WindowManager windowManager = new WindowManager();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  182 */   private final PageManager pageManager = new PageManager();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  188 */   private final HudManager hudManager = new HudManager();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  194 */   private HotbarManager hotbarManager = new HotbarManager();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private GameMode gameMode;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  205 */   private int clientViewRadius = 6;
/*      */ 
/*      */   
/*      */   protected long lastSpawnTimeNanos;
/*      */ 
/*      */   
/*      */   private static final int MAX_VELOCITY_SAMPLE_COUNT = 2;
/*      */ 
/*      */   
/*      */   private static final int VELOCITY_SAMPLE_LENGTH = 12;
/*      */ 
/*      */   
/*  217 */   private static final double[][] velocitySampleWeights = new double[][] { { 1.0D }, { 0.9D, 0.1D } };
/*      */   
/*  219 */   private final double[] velocitySamples = new double[12];
/*      */   
/*      */   private int velocitySampleCount;
/*  222 */   private int velocitySampleIndex = 4;
/*      */   
/*      */   private boolean overrideBlockPlacementRestrictions;
/*  225 */   private final AtomicInteger readyId = new AtomicInteger();
/*      */   
/*  227 */   private final AtomicReference<ScheduledFuture<?>> waitingForClientReady = new AtomicReference<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean executeTriggers;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean executeBlockDamage;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean firstSpawn;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int mountEntityId;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void copyFrom(@Nonnull Player oldPlayerComponent) {
/*  262 */     init(this.legacyUuid, this.playerRef);
/*      */     
/*  264 */     this.worldMapTracker.copyFrom(oldPlayerComponent.worldMapTracker);
/*      */     
/*  266 */     this.clientViewRadius = oldPlayerComponent.clientViewRadius;
/*      */     
/*  268 */     this.readyId.set(oldPlayerComponent.readyId.get());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void init(@Nonnull UUID uuid, @Nonnull PlayerRef playerRef) {
/*  278 */     this.legacyUuid = uuid;
/*  279 */     this.playerRef = playerRef;
/*  280 */     this.windowManager.init(playerRef);
/*  281 */     this.pageManager.init(playerRef, this.windowManager);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setNetworkId(int id) {
/*  290 */     this.networkId = id;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   protected Inventory createDefaultInventory() {
/*  296 */     return new Inventory();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Inventory setInventory(Inventory inventory) {
/*  303 */     return setInventory(inventory, true);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean remove() {
/*  308 */     if (this.wasRemoved.getAndSet(true)) return false;
/*      */     
/*  310 */     this.removedBy = new Throwable();
/*  311 */     if (this.world != null && this.world.isAlive())
/*  312 */       if (this.world.isInThread()) {
/*  313 */         Ref<EntityStore> ref = this.playerRef.getReference();
/*  314 */         if (ref != null) {
/*  315 */           Store<EntityStore> store = ref.getStore();
/*  316 */           ChunkTracker tracker = (ChunkTracker)store.getComponent(ref, ChunkTracker.getComponentType());
/*  317 */           if (tracker != null) tracker.clear(); 
/*  318 */           this.playerRef.removeFromStore();
/*      */         } 
/*      */       } else {
/*  321 */         this.world.execute(() -> {
/*      */               Ref<EntityStore> ref = this.playerRef.getReference();
/*      */               if (ref == null) {
/*      */                 return;
/*      */               }
/*      */               Store<EntityStore> store = ref.getStore();
/*      */               ChunkTracker tracker = (ChunkTracker)store.getComponent(ref, ChunkTracker.getComponentType());
/*      */               if (tracker != null)
/*      */                 tracker.clear(); 
/*      */               this.playerRef.removeFromStore();
/*      */             });
/*      */       }  
/*  333 */     if (this.playerRef.getPacketHandler().getChannel().isActive()) {
/*  334 */       this.playerRef.getPacketHandler().disconnect("Player removed from world!");
/*  335 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(this.removedBy)).log("Player removed from world! %s", this);
/*      */     } 
/*      */     ScheduledFuture<?> task;
/*  338 */     if ((task = this.waitingForClientReady.getAndSet(null)) != null) {
/*  339 */       task.cancel(false);
/*      */     }
/*  341 */     return true;
/*      */   }
/*      */ 
/*      */   
/*      */   public void moveTo(@Nonnull Ref<EntityStore> ref, double locX, double locY, double locZ, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  346 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  347 */     assert transformComponent != null;
/*      */     
/*  349 */     Vector3d position = transformComponent.getPosition();
/*  350 */     addLocationChange(ref, locX - position.getX(), locY - position.getY(), locZ - position.getZ(), componentAccessor);
/*      */     
/*  352 */     super.moveTo(ref, locX, locY, locZ, componentAccessor);
/*      */     
/*  354 */     this.windowManager.validateWindows(ref, componentAccessor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public PlayerConfigData getPlayerConfigData() {
/*  362 */     return this.data;
/*      */   }
/*      */ 
/*      */   
/*      */   public void markNeedsSave() {
/*  367 */     this.data.markChanged();
/*      */   }
/*      */ 
/*      */   
/*      */   public void unloadFromWorld() {
/*  372 */     super.unloadFromWorld();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void applyMovementStates(@Nonnull Ref<EntityStore> ref, @Nonnull SavedMovementStates savedMovementStates, @Nonnull MovementStates movementStates, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  379 */     movementStates.flying = savedMovementStates.flying;
/*      */     
/*  381 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  382 */     assert playerRefComponent != null;
/*      */     
/*  384 */     playerRefComponent.getPacketHandler().writeNoCache((Packet)new SetMovementStates(new SavedMovementStates(movementStates.flying)));
/*      */   }
/*      */ 
/*      */   
/*      */   public void startClientReadyTimeout() {
/*  389 */     ScheduledFuture<?> task = HytaleServer.SCHEDULED_EXECUTOR.schedule(() -> handleClientReady(true), 10000L, TimeUnit.MILLISECONDS);
/*  390 */     ScheduledFuture<?> oldTask = this.waitingForClientReady.getAndSet(task);
/*  391 */     if (oldTask != null) oldTask.cancel(false); 
/*      */   }
/*      */   
/*      */   public void handleClientReady(boolean forced) {
/*      */     ScheduledFuture<?> task;
/*  396 */     if ((task = this.waitingForClientReady.getAndSet(null)) != null) {
/*  397 */       task.cancel(false);
/*      */       
/*  399 */       if (this.world == null)
/*  400 */         return;  IEventDispatcher<PlayerReadyEvent, PlayerReadyEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(PlayerReadyEvent.class, this.world.getName());
/*  401 */       if (dispatcher.hasListener()) dispatcher.dispatch((IBaseEvent)new PlayerReadyEvent(this.reference, this, this.readyId.getAndIncrement())); 
/*      */     } 
/*      */   }
/*      */   
/*      */   public void sendInventory() {
/*  406 */     getInventory().consumeIsDirty();
/*  407 */     this.playerRef.getPacketHandler().write((Packet)getInventory().toPacket());
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   public CompletableFuture<Void> saveConfig(@Nonnull World world, @Nonnull Holder<EntityStore> holder) {
/*  412 */     MovementStatesComponent movementStatesComponent = (MovementStatesComponent)holder.getComponent(MovementStatesComponent.getComponentType());
/*  413 */     assert movementStatesComponent != null;
/*      */     
/*  415 */     UUIDComponent uuidComponent = (UUIDComponent)holder.getComponent(UUIDComponent.getComponentType());
/*  416 */     assert uuidComponent != null;
/*      */ 
/*      */     
/*  419 */     this.data.getPerWorldData(world.getName())
/*  420 */       .setLastMovementStates(movementStatesComponent.getMovementStates(), false);
/*  421 */     return Universe.get().getPlayerStorage().save(uuidComponent.getUuid(), holder);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated(forRemoval = true)
/*      */   public PacketHandler getPlayerConnection() {
/*  432 */     return this.playerRef.getPacketHandler();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public WorldMapTracker getWorldMapTracker() {
/*  440 */     return this.worldMapTracker;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public WindowManager getWindowManager() {
/*  448 */     return this.windowManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public PageManager getPageManager() {
/*  456 */     return this.pageManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public HudManager getHudManager() {
/*  464 */     return this.hudManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public HotbarManager getHotbarManager() {
/*  472 */     return this.hotbarManager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isFirstSpawn() {
/*  479 */     return this.firstSpawn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setFirstSpawn(boolean firstSpawn) {
/*  488 */     this.firstSpawn = firstSpawn;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void resetManagers(@Nonnull Holder<EntityStore> holder) {
/*  497 */     PlayerRef playerRef = this.playerRef;
/*      */     
/*  499 */     LegacyEntityTrackerSystems.clear(this, holder);
/*  500 */     this.worldMapTracker.clear();
/*      */     
/*  502 */     this.hudManager.resetUserInterface(this.playerRef);
/*  503 */     this.hudManager.resetHud(this.playerRef);
/*      */     
/*  505 */     CameraManager cameraManagerComponent = (CameraManager)playerRef.getComponent(CameraManager.getComponentType());
/*  506 */     assert cameraManagerComponent != null;
/*  507 */     cameraManagerComponent.resetCamera(playerRef);
/*      */     
/*  509 */     MovementManager movementManagerComponent = (MovementManager)playerRef.getComponent(MovementManager.getComponentType());
/*  510 */     assert movementManagerComponent != null;
/*  511 */     movementManagerComponent.applyDefaultSettings();
/*  512 */     movementManagerComponent.update(playerRef.getPacketHandler());
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
/*      */   public void notifyPickupItem(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, @Nullable Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  527 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*      */     
/*  529 */     if (world.getGameplayConfig().getShowItemPickupNotifications()) {
/*  530 */       PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  531 */       assert playerRefComponent != null;
/*      */       
/*  533 */       Message itemNameMessage = Message.translation(itemStack.getItem().getTranslationKey());
/*  534 */       NotificationUtil.sendNotification(playerRefComponent
/*  535 */           .getPacketHandler(), 
/*  536 */           Message.translation("server.general.pickedUpItem").param("item", itemNameMessage), null, itemStack
/*      */           
/*  538 */           .toPacket());
/*      */     } 
/*      */     
/*  541 */     if (position != null) {
/*  542 */       SoundUtil.playSoundEvent3dToPlayer(ref, TempAssetIdUtil.getSoundEventIndex("SFX_Player_Pickup_Item"), SoundCategory.UI, position, componentAccessor);
/*      */     } else {
/*  544 */       SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("SFX_Player_Pickup_Item"), SoundCategory.UI, componentAccessor);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isOverrideBlockPlacementRestrictions() {
/*  554 */     return this.overrideBlockPlacementRestrictions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setOverrideBlockPlacementRestrictions(@Nonnull Ref<EntityStore> ref, boolean overrideBlockPlacementRestrictions, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  565 */     this.overrideBlockPlacementRestrictions = overrideBlockPlacementRestrictions;
/*      */     
/*  567 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  568 */     assert playerRefComponent != null;
/*      */     
/*  570 */     playerRefComponent.getPacketHandler().writeNoCache((Packet)new SetBlockPlacementOverride(overrideBlockPlacementRestrictions));
/*      */   }
/*      */ 
/*      */   
/*      */   public void sendMessage(@Nonnull Message message) {
/*  575 */     this.playerRef.sendMessage(message);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasPermission(@Nonnull String id) {
/*  580 */     return PermissionsModule.get().hasPermission(getUuid(), id);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean hasPermission(@Nonnull String id, boolean def) {
/*  585 */     return PermissionsModule.get().hasPermission(getUuid(), id, def);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addLocationChange(@Nonnull Ref<EntityStore> ref, double deltaX, double deltaY, double deltaZ, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  594 */     CollisionResultComponent collisionResultComponent = (CollisionResultComponent)componentAccessor.getComponent(ref, CollisionResultComponent.getComponentType());
/*  595 */     assert collisionResultComponent != null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  601 */     collisionResultComponent.getCollisionPositionOffset().add(deltaX, deltaY, deltaZ);
/*  602 */     if (!collisionResultComponent.isPendingCollisionCheck()) {
/*  603 */       TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  604 */       assert transformComponent != null;
/*      */       
/*  606 */       Vector3d position = transformComponent.getPosition();
/*  607 */       collisionResultComponent.getCollisionStartPosition().assign(position);
/*  608 */       collisionResultComponent.markPendingCollisionCheck();
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
/*      */   public void configTriggerBlockProcessing(boolean triggers, boolean blockDamage, @Nonnull CollisionResultComponent collisionResultComponent) {
/*  622 */     this.executeTriggers = triggers;
/*  623 */     this.executeBlockDamage = blockDamage;
/*      */     
/*  625 */     if (triggers || blockDamage) {
/*  626 */       collisionResultComponent.getCollisionResult().enableTriggerBlocks();
/*      */     } else {
/*  628 */       collisionResultComponent.getCollisionResult().disableTriggerBlocks();
/*      */     } 
/*      */   }
/*      */   
/*      */   public void resetVelocity(@Nonnull Velocity velocity) {
/*  633 */     Arrays.fill(this.velocitySamples, 0.0D);
/*  634 */     this.velocitySampleIndex = 4;
/*  635 */     this.velocitySampleCount = 0;
/*  636 */     velocity.setZero();
/*      */   }
/*      */   
/*      */   public void processVelocitySample(double dt, @Nonnull Vector3d position, @Nonnull Velocity velocity) {
/*  640 */     double x = position.x;
/*  641 */     double y = position.y;
/*  642 */     double z = position.z;
/*      */ 
/*      */ 
/*      */     
/*  646 */     if (dt == 0.0D) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  651 */     this.velocitySamples[this.velocitySampleIndex] = x;
/*  652 */     this.velocitySamples[this.velocitySampleIndex + 1] = y;
/*  653 */     this.velocitySamples[this.velocitySampleIndex + 2] = z;
/*  654 */     this.velocitySamples[this.velocitySampleIndex + 3] = dt;
/*      */     
/*  656 */     int index = this.velocitySampleIndex;
/*      */ 
/*      */     
/*  659 */     this.velocitySampleIndex += 4;
/*  660 */     if (this.velocitySampleIndex >= 12) {
/*  661 */       this.velocitySampleIndex = 4;
/*      */     }
/*  663 */     if (this.velocitySampleCount < 2) {
/*  664 */       this.velocitySampleCount++;
/*      */     }
/*      */     
/*  667 */     if (this.velocitySampleCount < 2) {
/*  668 */       velocity.setZero();
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*  673 */     for (int i = 0; i < 4; i++) {
/*  674 */       this.velocitySamples[i] = 0.0D;
/*      */     }
/*      */     
/*  677 */     double[] weights = velocitySampleWeights[this.velocitySampleCount - 2];
/*      */ 
/*      */     
/*  680 */     for (int j = 0; j < this.velocitySampleCount - 1; j++) {
/*  681 */       int previousIndex = index - 4;
/*  682 */       if (previousIndex < 4) {
/*  683 */         previousIndex = 8;
/*      */       }
/*  685 */       double k = weights[j] / this.velocitySamples[index + 3];
/*  686 */       this.velocitySamples[0] = this.velocitySamples[0] + k * (this.velocitySamples[index] - this.velocitySamples[previousIndex]);
/*  687 */       this.velocitySamples[1] = this.velocitySamples[1] + k * (this.velocitySamples[index + 1] - this.velocitySamples[previousIndex + 1]);
/*  688 */       this.velocitySamples[2] = this.velocitySamples[2] + k * (this.velocitySamples[index + 2] - this.velocitySamples[previousIndex + 2]);
/*  689 */       index = previousIndex;
/*      */     } 
/*      */     
/*  692 */     velocity.set(this.velocitySamples[0], this.velocitySamples[1], this.velocitySamples[2]);
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
/*      */   @Nonnull
/*      */   public static CompletableFuture<Transform> getRespawnPosition(@Nonnull Ref<EntityStore> ref, @Nonnull String worldName, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  705 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, getComponentType());
/*  706 */     assert playerComponent != null;
/*      */     
/*  708 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*  709 */     PlayerConfigData playerConfigData = playerComponent.data;
/*      */     
/*  711 */     PlayerRespawnPointData[] respawnPoints = playerConfigData.getPerWorldData(worldName).getRespawnPoints();
/*  712 */     if (respawnPoints == null || respawnPoints.length == 0) {
/*      */       
/*  714 */       Transform worldSpawnPoint = world.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, componentAccessor);
/*  715 */       worldSpawnPoint.setRotation(Vector3f.ZERO);
/*  716 */       return CompletableFuture.completedFuture(worldSpawnPoint);
/*      */     } 
/*      */     
/*  719 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  720 */     assert transformComponent != null;
/*      */     
/*  722 */     Vector3d playerPosition = transformComponent.getPosition();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  729 */     List<PlayerRespawnPointData> sortedRespawnPoints = Arrays.<PlayerRespawnPointData>stream(respawnPoints).sorted((a, b) -> { Vector3d posA = a.getRespawnPosition(); Vector3d posB = b.getRespawnPosition(); double distA = playerPosition.distanceSquaredTo(posA.x, playerPosition.y, posA.z); double distB = playerPosition.distanceSquaredTo(posB.x, playerPosition.y, posB.z); return Double.compare(distA, distB); }).toList();
/*      */     
/*  731 */     BoundingBox playerBoundingBoxComponent = (BoundingBox)componentAccessor.getComponent(ref, BoundingBox.getComponentType());
/*  732 */     if (playerBoundingBoxComponent == null) return CompletableFuture.completedFuture(new Transform(((PlayerRespawnPointData)sortedRespawnPoints.getFirst()).getRespawnPosition()));
/*      */     
/*  734 */     return tryUseSpawnPoint(world, sortedRespawnPoints, 0, ref, playerComponent, playerBoundingBoxComponent.getBoundingBox());
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   private static CompletableFuture<Transform> tryUseSpawnPoint(World world, List<PlayerRespawnPointData> sortedRespawnPoints, int index, Ref<EntityStore> ref, Player playerComponent, Box boundingBox) {
/*  739 */     if (sortedRespawnPoints == null || index >= sortedRespawnPoints.size()) {
/*  740 */       playerComponent.sendMessage(Message.translation("server.general.allRespawnPointsObstructed"));
/*  741 */       return CompletableFuture.supplyAsync(() -> { if (!ref.isValid()) return new Transform();  Transform worldSpawnPoint = world.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, (ComponentAccessor)ref.getStore()); worldSpawnPoint.setRotation(Vector3f.ZERO); return worldSpawnPoint; }(Executor)world);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  750 */     PlayerRespawnPointData respawnPoint = sortedRespawnPoints.get(index);
/*  751 */     LongOpenHashSet requiredChunks = new LongOpenHashSet();
/*  752 */     if (respawnPoint.getRespawnPosition() != null) {
/*  753 */       boundingBox.forEachBlock(respawnPoint.getRespawnPosition(), 2.0D, requiredChunks, (x, y, z, chunks) -> {
/*      */             chunks.add(ChunkUtil.indexChunkFromBlock(x, z));
/*      */             
/*      */             return true;
/*      */           });
/*      */     }
/*  759 */     if (respawnPoint.getBlockPosition() != null) {
/*  760 */       boundingBox.forEachBlock(respawnPoint.getBlockPosition().toVector3d(), 2.0D, requiredChunks, (x, y, z, chunks) -> {
/*      */             chunks.add(ChunkUtil.indexChunkFromBlock(x, z));
/*      */             
/*      */             return true;
/*      */           });
/*      */     }
/*      */     
/*  767 */     CompletableFuture[] arrayOfCompletableFuture = new CompletableFuture[requiredChunks.size()];
/*  768 */     int i = 0;
/*  769 */     for (LongIterator iterator = requiredChunks.iterator(); iterator.hasNext(); ) {
/*  770 */       long chunkIndex = iterator.nextLong();
/*  771 */       arrayOfCompletableFuture[i++] = world.getChunkStore().getChunkReferenceAsync(chunkIndex).thenApplyAsync(v -> { if (v == null || !v.isValid()) return null;  WorldChunk wc = (WorldChunk)v.getStore().getComponent(v, WorldChunk.getComponentType()); assert wc != null; wc.addKeepLoaded(); return wc; }(Executor)world);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  780 */     return CompletableFuture.allOf((CompletableFuture<?>[])arrayOfCompletableFuture)
/*  781 */       .thenApplyAsync(v -> {
/*      */           Vector3d pos = ensureNoCollisionAtRespawnPosition(respawnPoint, boundingBox, world);
/*      */           
/*      */           if (pos != null) {
/*      */             return new Transform(pos, Vector3f.ZERO);
/*      */           }
/*      */           
/*      */           playerComponent.sendMessage(Message.translation("server.general.respawnPointObstructed").param("respawnPointName", respawnPoint.getName()));
/*      */           return null;
/*  790 */         }(Executor)world).whenComplete((unused, throwable) -> {
/*      */ 
/*      */           
/*      */           for (CompletableFuture<WorldChunk> future : chunkFutures) {
/*      */             future.thenAccept(WorldChunk::removeKeepLoaded);
/*      */           }
/*  796 */         }).thenCompose(v -> (v != null) ? CompletableFuture.completedFuture(v) : tryUseSpawnPoint(world, sortedRespawnPoints, index + 1, ref, playerComponent, boundingBox));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private static Vector3d ensureNoCollisionAtRespawnPosition(PlayerRespawnPointData playerRespawnPointData, Box playerHitbox, World world) {
/*  804 */     Vector3d respawnPosition = new Vector3d(playerRespawnPointData.getRespawnPosition());
/*  805 */     if (CollisionModule.get().validatePosition(world, playerHitbox, respawnPosition, new CollisionResult()) != -1) {
/*  806 */       return respawnPosition;
/*      */     }
/*      */ 
/*      */     
/*  810 */     respawnPosition.x = ((playerRespawnPointData.getBlockPosition()).x + 0.5F);
/*  811 */     respawnPosition.y = (playerRespawnPointData.getBlockPosition()).y;
/*  812 */     respawnPosition.z = ((playerRespawnPointData.getBlockPosition()).z + 0.5F);
/*      */ 
/*      */ 
/*      */     
/*  816 */     for (int distance = 1; distance <= 2; distance++) {
/*  817 */       int offset; for (offset = -distance; offset <= distance; offset++) {
/*      */         
/*  819 */         Vector3d newPosition = new Vector3d(respawnPosition.x + offset, respawnPosition.y, respawnPosition.z - distance);
/*  820 */         if (CollisionModule.get().validatePosition(world, playerHitbox, newPosition, new CollisionResult()) != -1) {
/*  821 */           return newPosition;
/*      */         }
/*      */ 
/*      */         
/*  825 */         newPosition = new Vector3d(respawnPosition.x + offset, respawnPosition.y, respawnPosition.z + distance);
/*  826 */         if (CollisionModule.get().validatePosition(world, playerHitbox, newPosition, new CollisionResult()) != -1) {
/*  827 */           return newPosition;
/*      */         }
/*      */       } 
/*      */       
/*  831 */       for (offset = -distance + 1; offset < distance; offset++) {
/*      */         
/*  833 */         Vector3d newPosition = new Vector3d(respawnPosition.x - distance, respawnPosition.y, respawnPosition.z + offset);
/*  834 */         if (CollisionModule.get().validatePosition(world, playerHitbox, newPosition, new CollisionResult()) != -1) {
/*  835 */           return newPosition;
/*      */         }
/*      */ 
/*      */         
/*  839 */         newPosition = new Vector3d(respawnPosition.x + distance, respawnPosition.y, respawnPosition.z + offset);
/*  840 */         if (CollisionModule.get().validatePosition(world, playerHitbox, newPosition, new CollisionResult()) != -1) {
/*  841 */           return newPosition;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  846 */     return null;
/*      */   }
/*      */   
/*      */   public boolean hasSpawnProtection() {
/*  850 */     return (System.nanoTime() - this.lastSpawnTimeNanos <= RESPAWN_INVULNERABILITY_TIME_NANOS || this.waitingForClientReady
/*  851 */       .get() != null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWaitingForClientReady() {
/*  858 */     return (this.waitingForClientReady.get() != null);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean isHiddenFromLivingEntity(@Nonnull Ref<EntityStore> ref, @Nonnull Ref<EntityStore> targetRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  863 */     UUIDComponent uuidComponent = (UUIDComponent)componentAccessor.getComponent(ref, UUIDComponent.getComponentType());
/*  864 */     assert uuidComponent != null;
/*      */     
/*  866 */     PlayerRef targetPlayerComponent = (PlayerRef)componentAccessor.getComponent(targetRef, PlayerRef.getComponentType());
/*  867 */     return (targetPlayerComponent != null && targetPlayerComponent.getHiddenPlayersManager().isPlayerHidden(uuidComponent.getUuid()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setClientViewRadius(int clientViewRadius) {
/*  876 */     this.clientViewRadius = clientViewRadius;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getClientViewRadius() {
/*  883 */     return this.clientViewRadius;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getViewRadius() {
/*  890 */     return Math.min(this.clientViewRadius, HytaleServer.get().getConfig().getMaxViewRadius());
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canDecreaseItemStackDurability(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  895 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, getComponentType());
/*  896 */     assert playerComponent != null;
/*      */     
/*  898 */     return (playerComponent.gameMode != GameMode.Creative);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean canApplyItemStackPenalties(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  903 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, getComponentType());
/*  904 */     assert playerComponent != null;
/*      */     
/*  906 */     return (playerComponent.gameMode != GameMode.Creative);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   public ItemStackSlotTransaction updateItemStackDurability(@Nonnull Ref<EntityStore> ref, @Nonnull ItemStack itemStack, ItemContainer container, int slotId, double durabilityChange, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  917 */     ItemStackSlotTransaction transaction = super.updateItemStackDurability(ref, itemStack, container, slotId, durabilityChange, componentAccessor);
/*  918 */     if (transaction != null && transaction.getSlotAfter().isBroken() && !itemStack.isBroken()) {
/*  919 */       Message itemNameMessage = Message.translation(itemStack.getItem().getTranslationKey());
/*  920 */       sendMessage(Message.translation("server.general.repair.itemBroken").param("itemName", itemNameMessage).color("#ff5555"));
/*      */       
/*  922 */       PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  923 */       assert playerRefComponent != null;
/*      */       
/*  925 */       int soundEventIndex = TempAssetIdUtil.getSoundEventIndex("SFX_Item_Break");
/*  926 */       SoundUtil.playSoundEvent2dToPlayer(playerRefComponent, soundEventIndex, SoundCategory.SFX);
/*      */     } 
/*  928 */     return transaction;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public MetricResults toMetricResults() {
/*  934 */     return METRICS_REGISTRY.toMetricResults(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLastSpawnTimeNanos(long lastSpawnTimeNanos) {
/*  943 */     this.lastSpawnTimeNanos = lastSpawnTimeNanos;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public long getSinceLastSpawnNanos() {
/*  950 */     return System.nanoTime() - this.lastSpawnTimeNanos;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated(forRemoval = true)
/*      */   public PlayerRef getPlayerRef() {
/*  960 */     return this.playerRef;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMountEntityId() {
/*  967 */     return this.mountEntityId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMountEntityId(int mountEntityId) {
/*  976 */     this.mountEntityId = mountEntityId;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GameMode getGameMode() {
/*  983 */     return this.gameMode;
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
/*      */   public static void setGameMode(@Nonnull Ref<EntityStore> playerRef, @Nonnull GameMode gameMode, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  996 */     MovementManager movementManagerComponent = (MovementManager)componentAccessor.getComponent(playerRef, MovementManager.getComponentType());
/*  997 */     assert movementManagerComponent != null;
/*      */     
/*  999 */     Player playerComponent = (Player)componentAccessor.getComponent(playerRef, getComponentType());
/* 1000 */     assert playerComponent != null;
/*      */     
/* 1002 */     GameMode oldGameMode = playerComponent.gameMode;
/* 1003 */     if (oldGameMode != gameMode) {
/* 1004 */       ChangeGameModeEvent event = new ChangeGameModeEvent(gameMode);
/* 1005 */       componentAccessor.invoke(playerRef, (EcsEvent)event);
/* 1006 */       if (event.isCancelled())
/*      */         return; 
/* 1008 */       setGameModeInternal(playerRef, event.getGameMode(), movementManagerComponent, componentAccessor);
/* 1009 */       runOnSwitchToGameMode(playerRef, gameMode);
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
/*      */   public static void initGameMode(@Nonnull Ref<EntityStore> playerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1022 */     MovementManager movementManagerComponent = (MovementManager)componentAccessor.getComponent(playerRef, MovementManager.getComponentType());
/* 1023 */     assert movementManagerComponent != null;
/*      */     
/* 1025 */     Player playerComponent = (Player)componentAccessor.getComponent(playerRef, getComponentType());
/* 1026 */     assert playerComponent != null;
/*      */ 
/*      */     
/* 1029 */     GameMode gameMode = playerComponent.gameMode;
/* 1030 */     if (gameMode == null) {
/* 1031 */       World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 1032 */       gameMode = world.getWorldConfig().getGameMode();
/* 1033 */       LOGGER.at(Level.INFO).log("Assigning default gamemode %s to player!", gameMode);
/*      */     } 
/*      */     
/* 1036 */     setGameModeInternal(playerRef, gameMode, movementManagerComponent, componentAccessor);
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
/*      */   private static void setGameModeInternal(@Nonnull Ref<EntityStore> playerRef, @Nonnull GameMode gameMode, @Nonnull MovementManager movementManager, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1051 */     Player playerComponent = (Player)componentAccessor.getComponent(playerRef, getComponentType());
/* 1052 */     assert playerComponent != null;
/*      */     
/* 1054 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(playerRef, PlayerRef.getComponentType());
/* 1055 */     assert playerRefComponent != null;
/*      */     
/* 1057 */     GameMode oldGameMode = playerComponent.gameMode;
/* 1058 */     playerComponent.gameMode = gameMode;
/* 1059 */     playerRefComponent.getPacketHandler().writeNoCache((Packet)new SetGameMode(gameMode));
/*      */ 
/*      */ 
/*      */     
/* 1063 */     if (movementManager.getDefaultSettings() != null) {
/* 1064 */       (movementManager.getDefaultSettings()).canFly = (gameMode == GameMode.Creative);
/* 1065 */       (movementManager.getSettings()).canFly = (gameMode == GameMode.Creative);
/* 1066 */       movementManager.update(playerRefComponent.getPacketHandler());
/*      */     } 
/*      */     
/* 1069 */     PermissionsModule permissionsModule = PermissionsModule.get();
/* 1070 */     if (oldGameMode != null) {
/* 1071 */       GameModeType oldGameModeType = GameModeType.fromGameMode(oldGameMode);
/* 1072 */       for (String group : oldGameModeType.getPermissionGroups()) {
/* 1073 */         permissionsModule.removeUserFromGroup(playerRefComponent.getUuid(), group);
/*      */       }
/*      */     } 
/*      */     
/* 1077 */     GameModeType gameModeType = GameModeType.fromGameMode(gameMode);
/* 1078 */     for (String group : gameModeType.getPermissionGroups()) {
/* 1079 */       permissionsModule.addUserToGroup(playerRefComponent.getUuid(), group);
/*      */     }
/*      */ 
/*      */     
/* 1083 */     if (gameMode == GameMode.Creative) {
/* 1084 */       componentAccessor.putComponent(playerRef, Invulnerable.getComponentType(), (Component)Invulnerable.INSTANCE);
/*      */     } else {
/* 1086 */       componentAccessor.tryRemoveComponent(playerRef, Invulnerable.getComponentType());
/*      */     } 
/*      */     
/* 1089 */     if (gameMode == GameMode.Creative) {
/* 1090 */       PlayerSettings settings = (PlayerSettings)componentAccessor.getComponent(playerRef, PlayerSettings.getComponentType());
/* 1091 */       if (settings == null) settings = PlayerSettings.defaults(); 
/* 1092 */       if (settings.creativeSettings().respondToHit()) {
/* 1093 */         componentAccessor.putComponent(playerRef, RespondToHit.getComponentType(), (Component)RespondToHit.INSTANCE);
/*      */       } else {
/* 1095 */         componentAccessor.tryRemoveComponent(playerRef, RespondToHit.getComponentType());
/*      */       } 
/*      */     } else {
/* 1098 */       componentAccessor.tryRemoveComponent(playerRef, RespondToHit.getComponentType());
/*      */     } 
/*      */ 
/*      */     
/* 1102 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 1103 */     playerComponent.worldMapTracker.sendSettings(world);
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
/*      */   private static void runOnSwitchToGameMode(@Nonnull Ref<EntityStore> ref, @Nonnull GameMode gameMode) {
/* 1115 */     Store<EntityStore> store = ref.getStore();
/* 1116 */     GameModeType gameModeType = GameModeType.fromGameMode(gameMode);
/*      */     
/* 1118 */     InteractionManager interactionManagerComponent = (InteractionManager)store.getComponent(ref, InteractionModule.get().getInteractionManagerComponent());
/* 1119 */     if (interactionManagerComponent == null)
/*      */       return; 
/* 1121 */     String interactions = gameModeType.getInteractionsOnEnter();
/* 1122 */     if (interactions == null)
/*      */       return; 
/* 1124 */     InteractionContext context = InteractionContext.forInteraction(interactionManagerComponent, ref, InteractionType.GameModeSwap, (ComponentAccessor)store);
/* 1125 */     RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(interactions);
/* 1126 */     if (rootInteraction == null)
/*      */       return; 
/* 1128 */     InteractionChain chain = interactionManagerComponent.initChain(InteractionType.EntityStatEffect, context, rootInteraction, true);
/* 1129 */     interactionManagerComponent.queueExecuteChain(chain);
/*      */   }
/*      */ 
/*      */   
/*      */   public int hashCode() {
/* 1134 */     int result = super.hashCode();
/* 1135 */     result = 31 * result + ((getUuid() != null) ? getUuid().hashCode() : 0);
/* 1136 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(@Nullable Object o) {
/* 1141 */     if (this == o) return true; 
/* 1142 */     if (o == null || getClass() != o.getClass()) return false; 
/* 1143 */     if (!super.equals(o)) return false;
/*      */     
/* 1145 */     Player player = (Player)o;
/*      */     
/* 1147 */     return (getUuid() != null) ? getUuid().equals(player.getUuid()) : ((player.getUuid() == null));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public String toString() {
/* 1154 */     return "Player{uuid=" + String.valueOf(getUuid()) + ", clientViewRadius='" + this.clientViewRadius + "', " + super
/*      */       
/* 1156 */       .toString() + "}";
/*      */   }
/*      */ 
/*      */   
/*      */   public String getDisplayName() {
/* 1161 */     return this.playerRef.getUsername();
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\Player.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */