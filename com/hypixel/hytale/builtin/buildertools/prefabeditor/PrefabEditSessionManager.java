/*      */ package com.hypixel.hytale.builtin.buildertools.prefabeditor;
/*      */ import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabRowSplitMode;
/*      */ import com.hypixel.hytale.builtin.buildertools.prefabeditor.enums.PrefabStackingAxis;
/*      */ import com.hypixel.hytale.common.map.IWeightedMap;
/*      */ import com.hypixel.hytale.component.Component;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.Holder;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.component.Store;
/*      */ import com.hypixel.hytale.event.EventRegistry;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.math.vector.Transform;
/*      */ import com.hypixel.hytale.math.vector.Vector3i;
/*      */ import com.hypixel.hytale.protocol.Color;
/*      */ import com.hypixel.hytale.protocol.MovementStates;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.protocol.SavedMovementStates;
/*      */ import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
/*      */ import com.hypixel.hytale.server.core.Message;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*      */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*      */ import com.hypixel.hytale.server.core.asset.type.environment.config.WeatherForecast;
/*      */ import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
/*      */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*      */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*      */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*      */ import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
/*      */ import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
/*      */ import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
/*      */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*      */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*      */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*      */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*      */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*      */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*      */ import com.hypixel.hytale.server.core.plugin.JavaPlugin;
/*      */ import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
/*      */ import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
/*      */ import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
/*      */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*      */ import com.hypixel.hytale.server.core.universe.Universe;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.WorldConfig;
/*      */ import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
/*      */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.universe.world.worldgen.provider.FlatWorldGenProvider;
/*      */ import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
/*      */ import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider;
/*      */ import com.hypixel.hytale.server.core.util.PrefabUtil;
/*      */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*      */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.nio.file.attribute.FileAttribute;
/*      */ import java.util.Collection;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.CompletionStage;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.function.Consumer;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ public class PrefabEditSessionManager {
/*      */   @Nonnull
/*   75 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*      */   
/*      */   @Nonnull
/*   78 */   private static final Message MESSAGE_COMMANDS_PREFAB_EDIT_SESSION_MANAGER_EXISTING_EDIT_SESSION = Message.translation("server.commands.prefabeditsessionmanager.existingEditSession");
/*      */   @Nonnull
/*   80 */   private static final Message MESSAGE_COMMANDS_EDIT_PREFAB_SOMETHING_WENT_WRONG = Message.translation("server.commands.editprefab.somethingWentWrong");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final float NOON_TIME = 0.5F;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_NEW_WORLD_ZERO_COORDINATE_BLOCK_NAME = "Rock_Stone";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_ENVIRONMENT = "Zone1_Sunny";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final String PREFAB_SELECTOR_TOOL_ID = "EditorTool_PrefabEditing_SelectPrefab";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_CHUNK_ENVIRONMENT = "Env_Zone1_Plains";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String PREFAB_EDITING_WORLD_NAME_PREFIX = "prefabEditor-";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  117 */   public static final Color DEFAULT_TINT = new Color((byte)91, (byte)-98, (byte)40);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static final long PROGRESS_UPDATE_INTERVAL_NANOS = 100000000L;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static final String DEFAULT_GRASS_TINT_HEX = "#5B9E28";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  133 */   private final Map<UUID, PrefabEditSession> activeEditSessions = (Map<UUID, PrefabEditSession>)new Object2ObjectOpenHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  140 */   private final HashSet<Path> prefabsBeingEdited = new HashSet<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  149 */   private final Map<UUID, UUID> inProgressTeleportations = (Map<UUID, UUID>)new Object2ObjectOpenHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  156 */   private final HashSet<UUID> inProgressLoading = new HashSet<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  163 */   private final HashSet<UUID> cancelledLoading = new HashSet<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PrefabEditSessionManager(@Nonnull JavaPlugin plugin) {
/*  172 */     EventRegistry eventRegistry = plugin.getEventRegistry();
/*      */     
/*  174 */     eventRegistry.registerGlobal(AddPlayerToWorldEvent.class, this::onPlayerAddedToWorld);
/*  175 */     eventRegistry.registerGlobal(PlayerReadyEvent.class, this::onPlayerReady);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void onPlayerReady(@Nonnull PlayerReadyEvent event) {
/*  185 */     Ref<EntityStore> playerRef = event.getPlayer().getReference();
/*  186 */     assert playerRef != null && !playerRef.isValid();
/*      */     
/*  188 */     Store<EntityStore> store = playerRef.getStore();
/*  189 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*      */     
/*  191 */     world.execute(() -> {
/*      */           UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(playerRef, UUIDComponent.getComponentType());
/*      */           assert uuidComponent != null;
/*      */           UUID playerUUID = uuidComponent.getUuid();
/*      */           if (!this.inProgressTeleportations.containsKey(playerUUID)) {
/*      */             return;
/*      */           }
/*      */           this.inProgressTeleportations.remove(playerUUID);
/*      */           MovementStatesComponent movementStatesComponent = (MovementStatesComponent)store.getComponent(playerRef, MovementStatesComponent.getComponentType());
/*      */           assert movementStatesComponent != null;
/*      */           MovementStates movementStates = movementStatesComponent.getMovementStates();
/*      */           Player playerComponent = (Player)store.getComponent(playerRef, Player.getComponentType());
/*      */           assert playerComponent != null;
/*      */           playerComponent.applyMovementStates(playerRef, new SavedMovementStates(true), movementStates, (ComponentAccessor)store);
/*      */           PlayerRef playerRefComponent = (PlayerRef)store.getComponent(playerRef, PlayerRef.getComponentType());
/*      */           if (playerRefComponent != null) {
/*      */             givePrefabSelectorTool(playerComponent, playerRefComponent);
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
/*      */   private void givePrefabSelectorTool(@Nonnull Player playerComponent, @Nonnull PlayerRef playerRef) {
/*  232 */     Inventory inventory = playerComponent.getInventory();
/*  233 */     ItemContainer hotbar = inventory.getHotbar();
/*      */     
/*  235 */     int hotbarSize = hotbar.getCapacity();
/*      */     
/*      */     short slot;
/*  238 */     for (slot = 0; slot < hotbarSize; slot = (short)(slot + 1)) {
/*  239 */       ItemStack itemStack = hotbar.getItemStack(slot);
/*  240 */       if (itemStack != null && !itemStack.isEmpty() && 
/*  241 */         "EditorTool_PrefabEditing_SelectPrefab".equals(itemStack.getItemId())) {
/*      */         
/*  243 */         inventory.setActiveHotbarSlot((byte)slot);
/*  244 */         playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, (byte)slot));
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  252 */     short emptySlot = -1; short s1;
/*  253 */     for (s1 = 0; s1 < hotbarSize; s1 = (short)(s1 + 1)) {
/*  254 */       ItemStack itemStack = hotbar.getItemStack(s1);
/*  255 */       if (itemStack == null || itemStack.isEmpty()) {
/*  256 */         emptySlot = s1;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/*  261 */     if (emptySlot == -1)
/*      */     {
/*  263 */       emptySlot = 0;
/*      */     }
/*      */ 
/*      */     
/*  267 */     hotbar.setItemStackForSlot(emptySlot, new ItemStack("EditorTool_PrefabEditing_SelectPrefab"));
/*      */ 
/*      */     
/*  270 */     inventory.setActiveHotbarSlot((byte)emptySlot);
/*  271 */     playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, (byte)emptySlot));
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
/*      */   public void onPlayerAddedToWorld(@Nonnull AddPlayerToWorldEvent event) {
/*  283 */     World world = event.getWorld();
/*      */     
/*  285 */     if (world.getName().startsWith("prefabEditor-")) {
/*  286 */       world.execute(() -> {
/*      */             Holder<EntityStore> playerHolder = event.getHolder();
/*      */             UUIDComponent uuidComponent = (UUIDComponent)playerHolder.getComponent(UUIDComponent.getComponentType());
/*      */             assert uuidComponent != null;
/*      */             this.inProgressTeleportations.put(uuidComponent.getUuid(), world.getWorldConfig().getUuid());
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
/*      */   public void updatePathOfLoadedPrefab(@Nonnull Path oldPath, @Nonnull Path newPath) {
/*  303 */     this.prefabsBeingEdited.remove(oldPath);
/*  304 */     this.prefabsBeingEdited.add(newPath);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEditingAPrefab(@Nonnull UUID playerUUID) {
/*  312 */     return this.activeEditSessions.containsKey(playerUUID);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PrefabEditSession getPrefabEditSession(@Nonnull UUID playerUUID) {
/*  322 */     return this.activeEditSessions.get(playerUUID);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Map<UUID, PrefabEditSession> getActiveEditSessions() {
/*  330 */     return this.activeEditSessions;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void populateActiveEditSession(@Nonnull UUID playerUuid, @Nonnull PrefabEditSession editSession) {
/*  338 */     this.activeEditSessions.put(playerUuid, editSession);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void populatePrefabsBeingEdited(@Nonnull Path prefabPath) {
/*  346 */     this.prefabsBeingEdited.add(prefabPath);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void scheduleAnchorEntityRecreation(@Nonnull PrefabEditSession editSession) {
/*  357 */     CompletableFuture.runAsync(() -> {
/*      */           World world = Universe.get().getWorld(editSession.getWorldName());
/*      */           if (world != null) {
/*      */             world.execute(());
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
/*      */   public boolean hasInProgressLoading(@Nonnull UUID playerUuid) {
/*  376 */     return this.inProgressLoading.contains(playerUuid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cancelLoading(@Nonnull UUID playerUuid) {
/*  386 */     this.cancelledLoading.add(playerUuid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isLoadingCancelled(@Nonnull UUID playerUuid) {
/*  396 */     return this.cancelledLoading.contains(playerUuid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearLoadingState(@Nonnull UUID playerUuid) {
/*  406 */     this.inProgressLoading.remove(playerUuid);
/*  407 */     this.cancelledLoading.remove(playerUuid);
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
/*      */   @Nonnull
/*      */   public CompletableFuture<Void> createEditSessionForNewPrefab(@Nonnull Ref<EntityStore> ref, @Nonnull Player editor, @Nonnull PrefabEditorCreationSettings settings, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  424 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  425 */     assert playerRefComponent != null;
/*      */     
/*  427 */     PrefabEditorCreationContext prefabEditorCreationContext = settings.finishProcessing(editor, playerRefComponent, true);
/*  428 */     if (prefabEditorCreationContext == null) {
/*  429 */       playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SOMETHING_WENT_WRONG);
/*  430 */       return CompletableFuture.completedFuture(null);
/*      */     } 
/*      */     
/*  433 */     return createEditSession(ref, prefabEditorCreationContext, true, componentAccessor);
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
/*      */   @Nullable
/*      */   public CompletableFuture<Void> loadPrefabAndCreateEditSession(@Nonnull Ref<EntityStore> ref, @Nonnull Player editor, @Nonnull PrefabEditorCreationSettings settings, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  451 */     return loadPrefabAndCreateEditSession(ref, editor, settings, componentAccessor, null);
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
/*      */   @Nullable
/*      */   public CompletableFuture<Void> loadPrefabAndCreateEditSession(@Nonnull Ref<EntityStore> ref, @Nonnull Player editor, @Nonnull PrefabEditorCreationSettings settings, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nullable Consumer<PrefabLoadingState> progressCallback) {
/*  471 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  472 */     assert playerRefComponent != null;
/*      */     
/*  474 */     UUID playerUuid = playerRefComponent.getUuid();
/*      */ 
/*      */     
/*  477 */     if (this.inProgressLoading.contains(playerUuid)) {
/*  478 */       PrefabLoadingState prefabLoadingState = new PrefabLoadingState();
/*  479 */       prefabLoadingState.addError("server.commands.editprefab.error.loadingInProgress");
/*  480 */       notifyProgress(progressCallback, prefabLoadingState);
/*  481 */       playerRefComponent.sendMessage(Message.translation("server.commands.editprefab.error.loadingInProgress"));
/*  482 */       return null;
/*      */     } 
/*      */ 
/*      */     
/*  486 */     this.inProgressLoading.add(playerUuid);
/*  487 */     this.cancelledLoading.remove(playerUuid);
/*      */     
/*  489 */     PrefabLoadingState loadingState = new PrefabLoadingState();
/*  490 */     loadingState.setPhase(PrefabLoadingState.Phase.INITIALIZING);
/*  491 */     notifyProgress(progressCallback, loadingState);
/*      */     
/*  493 */     PrefabEditorCreationContext prefabEditorCreationContext = settings.finishProcessing(editor, playerRefComponent, false);
/*  494 */     if (prefabEditorCreationContext == null) {
/*  495 */       loadingState.addError("server.commands.editprefab.error.processingFailed");
/*  496 */       notifyProgress(progressCallback, loadingState);
/*  497 */       playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SOMETHING_WENT_WRONG);
/*  498 */       return null;
/*      */     } 
/*      */     
/*  501 */     if (prefabEditorCreationContext.getPrefabPaths().isEmpty()) {
/*  502 */       loadingState.addError("server.commands.editprefab.error.noPrefabsFound");
/*  503 */       notifyProgress(progressCallback, loadingState);
/*  504 */       playerRefComponent.sendMessage(MESSAGE_COMMANDS_EDIT_PREFAB_SOMETHING_WENT_WRONG);
/*  505 */       return null;
/*      */     } 
/*      */     
/*  508 */     loadingState.setTotalPrefabs(prefabEditorCreationContext.getPrefabPaths().size());
/*  509 */     notifyProgress(progressCallback, loadingState);
/*      */     
/*  511 */     return createEditSession(ref, prefabEditorCreationContext, false, componentAccessor, loadingState, progressCallback);
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
/*      */   private void notifyProgress(@Nullable Consumer<PrefabLoadingState> progressCallback, @Nonnull PrefabLoadingState loadingState) {
/*  523 */     if (progressCallback == null) {
/*      */       return;
/*      */     }
/*      */     
/*  527 */     PrefabLoadingState.Phase phase = loadingState.getCurrentPhase();
/*  528 */     if (phase != PrefabLoadingState.Phase.LOADING_PREFABS && phase != PrefabLoadingState.Phase.PASTING_PREFABS) {
/*      */       
/*  530 */       progressCallback.accept(loadingState);
/*      */       
/*      */       return;
/*      */     } 
/*  534 */     long now = System.nanoTime();
/*  535 */     if (now - loadingState.getLastNotifyTimeNanos() >= 100000000L) {
/*  536 */       loadingState.setLastNotifyTimeNanos(now);
/*  537 */       progressCallback.accept(loadingState);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private CompletableFuture<Void> createEditSession(@Nonnull Ref<EntityStore> ref, @Nonnull PrefabEditorCreationContext context, boolean createNewPrefab, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  546 */     return createEditSession(ref, context, createNewPrefab, componentAccessor, null, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private CompletableFuture<Void> createEditSession(@Nonnull Ref<EntityStore> ref, @Nonnull PrefabEditorCreationContext context, boolean createNewPrefab, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nullable PrefabLoadingState loadingState, @Nullable Consumer<PrefabLoadingState> progressCallback) {
/*      */     CompletableFuture<World> future;
/*  556 */     World sourceWorld = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*      */     
/*  558 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/*  559 */     assert playerRefComponent != null;
/*      */     
/*  561 */     UUID playerUUID = playerRefComponent.getUuid();
/*      */ 
/*      */     
/*  564 */     if (this.activeEditSessions.containsKey(playerUUID)) {
/*  565 */       if (loadingState != null) {
/*  566 */         loadingState.addError("server.commands.editprefab.error.existingSession");
/*  567 */         notifyProgress(progressCallback, loadingState);
/*      */       } 
/*  569 */       playerRefComponent.sendMessage(MESSAGE_COMMANDS_PREFAB_EDIT_SESSION_MANAGER_EXISTING_EDIT_SESSION);
/*  570 */       return CompletableFuture.completedFuture(null);
/*      */     } 
/*      */ 
/*      */     
/*  574 */     for (Path prefabPath : context.getPrefabPaths()) {
/*  575 */       if (this.prefabsBeingEdited.contains(prefabPath)) {
/*  576 */         if (loadingState != null) {
/*  577 */           loadingState.addError("server.commands.editprefab.error.alreadyBeingEdited", prefabPath.toString());
/*  578 */           notifyProgress(progressCallback, loadingState);
/*      */         } 
/*  580 */         playerRefComponent.sendMessage(Message.translation("server.commands.prefabeditsessionmanager.alreadyBeingEdited")
/*  581 */             .param("path", prefabPath.toString()));
/*  582 */         return CompletableFuture.completedFuture(null);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  587 */     if (loadingState != null) {
/*  588 */       loadingState.setPhase(PrefabLoadingState.Phase.CREATING_WORLD);
/*  589 */       notifyProgress(progressCallback, loadingState);
/*      */     } 
/*      */ 
/*      */     
/*  593 */     WorldConfig config = new WorldConfig();
/*      */     
/*  595 */     boolean enableTicking = context.isWorldTickingEnabled();
/*  596 */     config.setBlockTicking(enableTicking);
/*  597 */     config.setSpawningNPC(false);
/*  598 */     config.setIsSpawnMarkersEnabled(false);
/*  599 */     config.setObjectiveMarkersEnabled(false);
/*  600 */     config.setGameMode(GameMode.Creative);
/*  601 */     config.setDeleteOnRemove(true);
/*  602 */     config.setUuid(UUID.randomUUID());
/*  603 */     config.setGameTimePaused(true);
/*  604 */     config.setIsAllNPCFrozen(true);
/*  605 */     config.setSavingPlayers(true);
/*  606 */     config.setCanSaveChunks(true);
/*  607 */     config.setTicking(enableTicking);
/*  608 */     config.setForcedWeather(getWeatherFromEnvironment(context.getEnvironment()));
/*      */     
/*  610 */     String worldName = getWorldName(context);
/*      */ 
/*      */     
/*      */     try {
/*  614 */       Files.createDirectories(getSavePath(context), (FileAttribute<?>[])new FileAttribute[0]);
/*  615 */     } catch (IOException e) {
/*  616 */       if (loadingState != null) {
/*  617 */         loadingState.addError("server.commands.editprefab.error.createDirectoryFailed", e.getMessage());
/*  618 */         notifyProgress(progressCallback, loadingState);
/*      */       } 
/*  620 */       playerRefComponent.sendMessage(Message.translation("server.commands.instances.createDirectory.failed")
/*  621 */           .param("errormsg", e.getMessage()));
/*  622 */       return CompletableFuture.completedFuture(null);
/*      */     } 
/*      */     
/*  625 */     TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ref, TransformComponent.getComponentType());
/*  626 */     assert transformComponent != null;
/*      */ 
/*      */     
/*  629 */     Transform transform = transformComponent.getTransform().clone();
/*  630 */     PrefabEditSession prefabEditSession = new PrefabEditSession(worldName, playerUUID, sourceWorld.getWorldConfig().getUuid(), transform);
/*      */ 
/*      */ 
/*      */     
/*  634 */     if (createNewPrefab) {
/*  635 */       future = getPrefabCreatingCompletableFuture(context, prefabEditSession, config);
/*      */     } else {
/*  637 */       future = getPrefabLoadingCompletableFuture(context, prefabEditSession, config, loadingState, progressCallback, playerUUID);
/*      */     } 
/*      */ 
/*      */     
/*  641 */     if (future == null) {
/*  642 */       if (loadingState != null) {
/*  643 */         loadingState.addError("server.commands.editprefab.error.loadFailed");
/*  644 */         notifyProgress(progressCallback, loadingState);
/*      */       } 
/*  646 */       return CompletableFuture.completedFuture(null);
/*      */     } 
/*      */     
/*  649 */     return future
/*  650 */       .exceptionally(throwable -> {
/*      */           if (isLoadingCancelled(playerUUID)) {
/*      */             return null;
/*      */           }
/*      */           
/*      */           ((HytaleLogger.Api)LOGGER.at(Level.SEVERE).withCause(throwable)).log("Error occurred during prefab editor session creation");
/*      */           
/*      */           if (loadingState != null) {
/*      */             loadingState.addError("server.commands.editprefab.error.exception", throwable.getMessage());
/*      */             
/*      */             notifyProgress(progressCallback, loadingState);
/*      */           } 
/*      */           
/*      */           playerRefComponent.sendMessage(Message.translation("server.commands.editprefab.error.exception").param("details", (throwable.getMessage() != null) ? throwable.getMessage() : "Unknown error"));
/*      */           return null;
/*  665 */         }).thenAcceptAsync(targetWorld -> {
/*      */           if (isLoadingCancelled(playerUUID)) {
/*      */             return;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*      */           if (targetWorld == null) {
/*      */             return;
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*      */           if (loadingState != null) {
/*      */             loadingState.setPhase(PrefabLoadingState.Phase.FINALIZING);
/*      */ 
/*      */ 
/*      */             
/*      */             notifyProgress(progressCallback, loadingState);
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*      */           Vector3i spawnPoint = prefabEditSession.getSpawnPoint();
/*      */ 
/*      */ 
/*      */           
/*      */           targetWorld.getWorldConfig().setSpawnProvider((ISpawnProvider)new GlobalSpawnProvider(new Transform(spawnPoint)));
/*      */ 
/*      */ 
/*      */           
/*      */           CompletableFuture.runAsync((), (Executor)targetWorld);
/*      */ 
/*      */ 
/*      */           
/*      */           CompletableFuture.runAsync((), (Executor)sourceWorld);
/*  701 */         }).thenRun(() -> {
/*      */           if (isLoadingCancelled(playerUUID)) {
/*      */             return;
/*      */           }
/*      */ 
/*      */           
/*      */           this.prefabsBeingEdited.addAll(context.getPrefabPaths());
/*      */           
/*      */           this.activeEditSessions.put(playerUUID, prefabEditSession);
/*      */           
/*      */           if (loadingState != null) {
/*      */             loadingState.markComplete();
/*      */             
/*      */             notifyProgress(progressCallback, loadingState);
/*      */           } 
/*      */           
/*      */           playerRefComponent.sendMessage(Message.translation("server.commands.prefabeditsessionmanager.success." + (createNewPrefab ? "new" : "load")));
/*  718 */         }).whenComplete((result, throwable) -> this.inProgressLoading.remove(playerUUID));
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
/*      */   @Nonnull
/*      */   private CompletableFuture<World> getWorldCreatingFuture(@Nonnull PrefabEditorCreationContext context, @Nonnull WorldConfig config) {
/*  732 */     return Universe.get().makeWorld(getWorldName(context), getSavePath(context), config, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private String getWorldName(@Nonnull PrefabEditorCreationContext context) {
/*  740 */     return "prefabEditor-" + context.getEditorRef().getUsername();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private String getWeatherFromEnvironment(@Nullable String environmentId) {
/*  749 */     if (environmentId == null || environmentId.isEmpty()) {
/*  750 */       return "Zone1_Sunny";
/*      */     }
/*      */     
/*  753 */     Environment environment = (Environment)Environment.getAssetMap().getAsset(environmentId);
/*  754 */     if (environment == null) {
/*  755 */       return "Zone1_Sunny";
/*      */     }
/*      */     
/*  758 */     IWeightedMap<WeatherForecast> forecast = environment.getWeatherForecast(12);
/*  759 */     if (forecast == null || forecast.size() == 0) {
/*  760 */       return "Zone1_Sunny";
/*      */     }
/*      */     
/*  763 */     String[] bestWeatherId = { null };
/*  764 */     double[] highestWeight = { Double.NEGATIVE_INFINITY };
/*      */     
/*  766 */     forecast.forEachEntry((weatherForecast, weight) -> {
/*      */           if (weight > highestWeight[0]) {
/*      */             highestWeight[0] = weight;
/*      */             
/*      */             bestWeatherId[0] = weatherForecast.getWeatherId();
/*      */           } 
/*      */         });
/*  773 */     return (bestWeatherId[0] != null) ? bestWeatherId[0] : "Zone1_Sunny";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private Path getSavePath(@Nonnull PrefabEditorCreationContext context) {
/*  781 */     return Constants.UNIVERSE_PATH.resolve("worlds").resolve(getWorldName(context));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void applyWorldGenWorldConfig(@Nonnull PrefabEditorCreationContext context, int yLevelToPastePrefabsAt, @Nonnull WorldConfig worldConfig) {
/*  791 */     String environment = (context.getEnvironment() != null) ? context.getEnvironment() : "Env_Zone1_Plains";
/*  792 */     Color tint = DEFAULT_TINT;
/*  793 */     if (context.getGrassTint() != null && !context.getGrassTint().isEmpty()) {
/*  794 */       Color parsed = ColorParseUtil.parseColor(context.getGrassTint());
/*  795 */       if (parsed != null) {
/*  796 */         tint = parsed;
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  801 */     if (context.getWorldGenType().equals(WorldGenType.FLAT)) {
/*      */ 
/*      */       
/*  804 */       int yLevelForFlatWorldExclusive = Math.max(1, yLevelToPastePrefabsAt - context.getBlocksAboveSurface());
/*      */       
/*  806 */       FlatWorldGenProvider.Layer topLayer = new FlatWorldGenProvider.Layer();
/*  807 */       topLayer.blockType = "Soil_Grass";
/*  808 */       topLayer.to = yLevelForFlatWorldExclusive;
/*  809 */       topLayer.from = yLevelForFlatWorldExclusive - 1;
/*  810 */       topLayer.environment = environment;
/*      */       
/*  812 */       FlatWorldGenProvider.Layer airLayer = new FlatWorldGenProvider.Layer();
/*  813 */       airLayer.blockType = "Empty";
/*  814 */       airLayer.to = 320;
/*  815 */       airLayer.from = yLevelForFlatWorldExclusive;
/*  816 */       airLayer.environment = environment;
/*      */       
/*  818 */       if (yLevelForFlatWorldExclusive - 2 >= 0) {
/*  819 */         FlatWorldGenProvider.Layer bottomLayer = new FlatWorldGenProvider.Layer();
/*  820 */         bottomLayer.blockType = "Soil_Clay";
/*  821 */         bottomLayer.to = yLevelForFlatWorldExclusive - 1;
/*  822 */         bottomLayer.from = 0;
/*  823 */         bottomLayer.environment = environment;
/*  824 */         worldConfig.setWorldGenProvider((IWorldGenProvider)new FlatWorldGenProvider(tint, new FlatWorldGenProvider.Layer[] { airLayer, topLayer, bottomLayer }));
/*      */       } else {
/*  826 */         worldConfig.setWorldGenProvider((IWorldGenProvider)new FlatWorldGenProvider(tint, new FlatWorldGenProvider.Layer[] { airLayer, topLayer }));
/*      */       } 
/*      */     } else {
/*  829 */       worldConfig.setWorldGenProvider((IWorldGenProvider)new VoidWorldGenProvider(tint, environment));
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
/*      */   @Nonnull
/*      */   private CompletableFuture<World> getPrefabCreatingCompletableFuture(@Nonnull PrefabEditorCreationContext context, @Nonnull PrefabEditSession editSession, @Nonnull WorldConfig worldConfig) {
/*  844 */     applyWorldGenWorldConfig(context, context.getPasteLevelGoal() - 1, worldConfig);
/*      */ 
/*      */     
/*  847 */     return getWorldCreatingFuture(context, worldConfig).thenCompose(world -> CompletableFuture.supplyAsync((), (Executor)world));
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
/*      */   @Nullable
/*      */   private CompletableFuture<World> getPrefabLoadingCompletableFuture(@Nonnull PrefabEditorCreationContext context, @Nonnull PrefabEditSession editSession, @Nonnull WorldConfig worldConfig, @Nullable PrefabLoadingState loadingState, @Nullable Consumer<PrefabLoadingState> progressCallback, @Nonnull UUID playerUuid) {
/*  891 */     CompletableFuture[] initializationFutures = new CompletableFuture[context.getPrefabPaths().size()];
/*      */     
/*  893 */     if (loadingState != null) {
/*  894 */       loadingState.setPhase(PrefabLoadingState.Phase.LOADING_PREFABS);
/*  895 */       notifyProgress(progressCallback, loadingState);
/*      */     } 
/*      */ 
/*      */     
/*  899 */     for (int i = 0; i < context.getPrefabPaths().size(); i++) {
/*      */       
/*  901 */       Path prefabPath = context.getPrefabPaths().get(i);
/*  902 */       CompletableFuture<IPrefabBuffer> prefabLoadingFuture = getPrefabBuffer((CommandSender)context.getEditor(), prefabPath);
/*      */       
/*  904 */       if (prefabLoadingFuture == null) {
/*  905 */         if (loadingState != null) {
/*  906 */           loadingState.addError("server.commands.editprefab.error.prefabLoadFailed", prefabPath.toString());
/*  907 */           notifyProgress(progressCallback, loadingState);
/*      */         } 
/*  909 */         return null;
/*      */       } 
/*      */ 
/*      */       
/*  913 */       Path pathForCallback = prefabPath;
/*  914 */       initializationFutures[i] = prefabLoadingFuture.thenApply(buffer -> {
/*      */             if (loadingState != null) {
/*      */               loadingState.onPrefabLoaded(pathForCallback);
/*      */               
/*      */               notifyProgress(progressCallback, loadingState);
/*      */             } 
/*      */             return buffer;
/*      */           });
/*      */     } 
/*  923 */     return 
/*      */ 
/*      */       
/*  926 */       CompletableFuture.allOf((CompletableFuture<?>[])initializationFutures)
/*  927 */       .thenApply(unused -> {
/*      */           if (isLoadingCancelled(playerUuid)) {
/*      */             return null;
/*      */           }
/*      */ 
/*      */           
/*      */           ObjectArrayList<IPrefabBuffer> objectArrayList = new ObjectArrayList(initializationFutures.length);
/*      */ 
/*      */           
/*      */           int heightOfTallestPrefab = 0;
/*      */ 
/*      */           
/*      */           for (CompletableFuture<IPrefabBuffer> initializationFuture : initializationFutures) {
/*      */             IPrefabBuffer prefabAccessor = initializationFuture.join();
/*      */ 
/*      */             
/*      */             objectArrayList.add(prefabAccessor);
/*      */ 
/*      */             
/*      */             if (context.loadChildPrefabs()) {
/*      */               for (PrefabBuffer.ChildPrefab childPrefab : prefabAccessor.getChildPrefabs());
/*      */             }
/*      */ 
/*      */             
/*      */             int prefabHeight = Math.abs(prefabAccessor.getMaxY() - prefabAccessor.getMinY());
/*      */             
/*      */             if (prefabHeight > heightOfTallestPrefab) {
/*      */               heightOfTallestPrefab = prefabHeight;
/*      */             }
/*      */           } 
/*      */           
/*      */           int yLevelToPastePrefabsAt = getAmountOfBlocksBelowPrefab(heightOfTallestPrefab, context.getPasteLevelGoal());
/*      */           
/*      */           applyWorldGenWorldConfig(context, yLevelToPastePrefabsAt, worldConfig);
/*      */           
/*      */           if (loadingState != null) {
/*      */             loadingState.setPhase(PrefabLoadingState.Phase.PASTING_PREFABS);
/*      */             
/*      */             notifyProgress(progressCallback, loadingState);
/*      */           } 
/*      */           
/*      */           if (isLoadingCancelled(playerUuid)) {
/*      */             return null;
/*      */           }
/*      */           
/*      */           String worldName = getWorldName(context);
/*      */           
/*      */           if (Universe.get().getWorld(worldName) != null) {
/*      */             LOGGER.at(Level.WARNING).log("Aborting prefab editor creation for %s: world '%s' already exists", playerUuid, worldName);
/*      */             
/*      */             return null;
/*      */           } 
/*      */           
/*      */           return new Tri<>(objectArrayList, Integer.valueOf(yLevelToPastePrefabsAt), getWorldCreatingFuture(context, worldConfig).join());
/*  981 */         }).thenCompose(passedData -> (passedData == null) ? CompletableFuture.completedFuture(null) : CompletableFuture.supplyAsync((), (Executor)passedData.getRight()));
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
/*      */   private int[] calculateRowGroups(@Nonnull PrefabEditorCreationContext context, int prefabCount) {
/* 1169 */     int[] rowGroups = new int[prefabCount];
/* 1170 */     PrefabRowSplitMode rowSplitMode = context.getRowSplitMode();
/* 1171 */     List<Path> prefabPaths = context.getPrefabPaths();
/*      */     
/* 1173 */     if (rowSplitMode == PrefabRowSplitMode.NONE || prefabCount == 0)
/*      */     {
/* 1175 */       return rowGroups;
/*      */     }
/*      */     
/* 1178 */     if (rowSplitMode == PrefabRowSplitMode.BY_SPECIFIED_FOLDER) {
/*      */       
/* 1180 */       List<String> unprocessedPaths = context.getUnprocessedPrefabPaths();
/* 1181 */       Path rootPath = context.getPrefabRootDirectory().getPrefabPath();
/*      */       
/* 1183 */       int currentGroup = 0;
/* 1184 */       int prefabIndex = 0;
/*      */       
/* 1186 */       for (String unprocessedPath : unprocessedPaths) {
/*      */         
/* 1188 */         Path resolvedPath = rootPath.resolve(unprocessedPath.replace('/', File.separatorChar)
/* 1189 */             .replace('\\', File.separatorChar));
/*      */         
/* 1191 */         while (prefabIndex < prefabCount) {
/* 1192 */           Path prefabPath = prefabPaths.get(prefabIndex);
/*      */           
/* 1194 */           if (prefabPath.startsWith(resolvedPath) || (
/* 1195 */             unprocessedPath.endsWith("\\") ? prefabPath
/* 1196 */             .startsWith(resolvedPath) : prefabPath
/* 1197 */             .equals(resolvedPath))) {
/* 1198 */             rowGroups[prefabIndex] = currentGroup;
/* 1199 */             prefabIndex++;
/*      */           } 
/*      */         } 
/*      */ 
/*      */         
/* 1204 */         currentGroup++;
/*      */       } 
/* 1206 */     } else if (rowSplitMode == PrefabRowSplitMode.BY_ALL_SUBFOLDERS) {
/*      */       
/* 1208 */       Object2ObjectOpenHashMap<Path, Integer> parentDirToGroup = new Object2ObjectOpenHashMap();
/* 1209 */       int nextGroup = 0;
/*      */       
/* 1211 */       for (int i = 0; i < prefabCount; i++) {
/* 1212 */         Path prefabPath = prefabPaths.get(i);
/* 1213 */         Path parentDir = prefabPath.getParent();
/*      */         
/* 1215 */         if (parentDir != null) {
/* 1216 */           Integer group = (Integer)parentDirToGroup.get(parentDir);
/* 1217 */           if (group == null) {
/* 1218 */             parentDirToGroup.put(parentDir, Integer.valueOf(nextGroup));
/* 1219 */             rowGroups[i] = nextGroup;
/* 1220 */             nextGroup++;
/*      */           } else {
/* 1222 */             rowGroups[i] = group.intValue();
/*      */           } 
/*      */         } else {
/* 1225 */           rowGroups[i] = 0;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1230 */     return rowGroups;
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
/*      */   private int getAmountOfBlocksBelowPrefab(int prefabHeight, int desiredYLevel) {
/* 1242 */     if (desiredYLevel < 0) throw new IllegalArgumentException("Cannot have a negative y level for pasting prefabs"); 
/* 1243 */     if (desiredYLevel >= 320) throw new IllegalArgumentException("Cannot paste above or at the world height"); 
/* 1244 */     return Math.min(desiredYLevel, 320 - prefabHeight);
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
/*      */   @Nullable
/*      */   public CompletableFuture<Void> exitEditSession(@Nonnull Ref<EntityStore> ref, @Nonnull World world, @Nonnull PlayerRef playerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1261 */     PrefabEditSession prefabEditSession = this.activeEditSessions.get(playerRef.getUuid());
/* 1262 */     if (prefabEditSession == null) return null;
/*      */     
/* 1264 */     prefabEditSession.hidePrefabAnchors(playerRef.getPacketHandler());
/*      */     
/* 1266 */     World returnWorld = Universe.get().getWorld(prefabEditSession.getWorldArrivedFrom());
/* 1267 */     Transform returnLocation = prefabEditSession.getTransformArrivedFrom();
/*      */ 
/*      */     
/* 1270 */     if (returnWorld == null || returnLocation == null) {
/* 1271 */       LOGGER.at(Level.WARNING).log("Prefab editor exit fallback triggered for player %s: returnWorld=%s (worldArrivedFrom=%s), returnLocation=%s. Using default world spawn.", playerRef
/*      */           
/* 1273 */           .getUuid(), 
/* 1274 */           (returnWorld != null) ? returnWorld.getName() : "null", prefabEditSession
/* 1275 */           .getWorldArrivedFrom(), returnLocation);
/*      */ 
/*      */       
/* 1278 */       returnWorld = Universe.get().getDefaultWorld();
/* 1279 */       returnLocation = returnWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, componentAccessor);
/*      */     } 
/*      */     
/* 1282 */     World finalReturnWorld = returnWorld;
/* 1283 */     Transform finalReturnLocation = returnLocation;
/* 1284 */     Teleport teleportComponent = Teleport.createForPlayer(finalReturnWorld, finalReturnLocation);
/*      */     
/* 1286 */     return CompletableFuture.runAsync(() -> componentAccessor.putComponent(ref, Teleport.getComponentType(), (Component)teleportComponent), (Executor)world)
/* 1287 */       .thenRunAsync(() -> {
/*      */           World worldToRemove = Universe.get().getWorld(prefabEditSession.getWorldName());
/*      */           if (worldToRemove != null) {
/*      */             Universe.get().removeWorld(prefabEditSession.getWorldName());
/*      */           }
/*      */           Collection<PrefabEditingMetadata> prefabsBeingEditedInEditSession = prefabEditSession.getLoadedPrefabMetadata().values();
/*      */           for (PrefabEditingMetadata prefab : prefabsBeingEditedInEditSession) {
/*      */             this.prefabsBeingEdited.remove(prefab.getPrefabPath());
/*      */           }
/*      */           this.activeEditSessions.remove(playerRef.getUuid());
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
/*      */   @Nonnull
/*      */   public CompletableFuture<Void> cleanupCancelledSession(@Nonnull UUID playerUuid, @Nonnull String worldName, @Nullable Consumer<PrefabLoadingState> progressCallback) {
/* 1317 */     cancelLoading(playerUuid);
/*      */     
/* 1319 */     PrefabLoadingState loadingState = new PrefabLoadingState();
/* 1320 */     loadingState.setPhase(PrefabLoadingState.Phase.CANCELLING);
/* 1321 */     notifyProgress(progressCallback, loadingState);
/*      */     
/* 1323 */     return CompletableFuture.runAsync(() -> {
/*      */           World world = Universe.get().getWorld(worldName);
/*      */           if (world != null) {
/*      */             loadingState.setPhase(PrefabLoadingState.Phase.SHUTTING_DOWN_WORLD);
/*      */             notifyProgress(progressCallback, loadingState);
/*      */             world.getWorldConfig().setDeleteOnRemove(true);
/*      */             loadingState.setPhase(PrefabLoadingState.Phase.DELETING_WORLD);
/*      */             notifyProgress(progressCallback, loadingState);
/*      */             Universe.get().removeWorld(worldName);
/*      */           } 
/*      */           PrefabEditSession session = this.activeEditSessions.remove(playerUuid);
/*      */           if (session != null) {
/*      */             Collection<PrefabEditingMetadata> prefabsInSession = session.getLoadedPrefabMetadata().values();
/*      */             for (PrefabEditingMetadata prefab : prefabsInSession) {
/*      */               this.prefabsBeingEdited.remove(prefab.getPrefabPath());
/*      */             }
/*      */           } 
/*      */           this.inProgressLoading.remove(playerUuid);
/*      */           loadingState.setPhase(PrefabLoadingState.Phase.SHUTDOWN_COMPLETE);
/*      */           notifyProgress(progressCallback, loadingState);
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
/*      */   @Nonnull
/*      */   public CompletableFuture<Void> cleanupCancelledSession(@Nonnull UUID playerUuid, @Nonnull String worldName) {
/* 1369 */     return cleanupCancelledSession(playerUuid, worldName, null);
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
/*      */   @Nullable
/*      */   private CompletableFuture<IPrefabBuffer> getPrefabBuffer(@Nonnull CommandSender sender, @Nonnull Path path) {
/* 1382 */     if (!Files.exists(path, new java.nio.file.LinkOption[0])) {
/* 1383 */       sender.sendMessage(Message.translation("server.commands.editprefab.prefabNotFound")
/* 1384 */           .param("name", path.toString()));
/* 1385 */       return null;
/*      */     } 
/* 1387 */     return CompletableFuture.supplyAsync(() -> PrefabBufferUtil.getCached(path));
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\prefabeditor\PrefabEditSessionManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */