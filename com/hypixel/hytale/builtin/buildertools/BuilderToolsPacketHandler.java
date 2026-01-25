/*     */ package com.hypixel.hytale.builtin.buildertools;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.Axis;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.ColorLight;
/*     */ import com.hypixel.hytale.protocol.ModelTransform;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolAction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolEntityAction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolExtrudeAction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolGeneralAction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolLineAction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolPasteClipboard;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolRotateClipboard;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionTransform;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSelectionUpdate;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityLight;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityPickupEnabled;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetEntityScale;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolSetNPCDebug;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolStackArea;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.EntityToolAction;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.buildertool.config.BuilderTool;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.Interactions;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class BuilderToolsPacketHandler implements SubPacketHandler {
/*  47 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private final IPacketHandler packetHandler;
/*     */   
/*     */   public BuilderToolsPacketHandler(IPacketHandler packetHandler) {
/*  52 */     this.packetHandler = packetHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerHandlers() {
/*  57 */     if (BuilderToolsPlugin.get().isDisabled()) {
/*  58 */       this.packetHandler.registerNoOpHandlers(new int[] { 400, 401, 412, 409, 403, 406, 407, 413, 414, 417 });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  73 */     this.packetHandler.registerHandler(106, p -> handle((LoadHotbar)p));
/*  74 */     this.packetHandler.registerHandler(107, p -> handle((SaveHotbar)p));
/*  75 */     this.packetHandler.registerHandler(400, p -> handle((BuilderToolArgUpdate)p));
/*  76 */     this.packetHandler.registerHandler(401, p -> handle((BuilderToolEntityAction)p));
/*  77 */     this.packetHandler.registerHandler(412, p -> handle((BuilderToolGeneralAction)p));
/*  78 */     this.packetHandler.registerHandler(409, p -> handle((BuilderToolSelectionUpdate)p));
/*  79 */     this.packetHandler.registerHandler(403, p -> handle((BuilderToolExtrudeAction)p));
/*  80 */     this.packetHandler.registerHandler(406, p -> handle((BuilderToolRotateClipboard)p));
/*  81 */     this.packetHandler.registerHandler(407, p -> handle((BuilderToolPasteClipboard)p));
/*  82 */     this.packetHandler.registerHandler(413, p -> handle((BuilderToolOnUseInteraction)p));
/*  83 */     this.packetHandler.registerHandler(410, p -> handle((BuilderToolSelectionToolAskForClipboard)p));
/*  84 */     this.packetHandler.registerHandler(414, p -> handle((BuilderToolLineAction)p));
/*  85 */     this.packetHandler.registerHandler(402, p -> handle((BuilderToolSetEntityTransform)p));
/*  86 */     this.packetHandler.registerHandler(420, p -> handle((BuilderToolSetEntityScale)p));
/*  87 */     this.packetHandler.registerHandler(405, p -> handle((BuilderToolSelectionTransform)p));
/*  88 */     this.packetHandler.registerHandler(404, p -> handle((BuilderToolStackArea)p));
/*  89 */     this.packetHandler.registerHandler(408, p -> handle((BuilderToolSetTransformationModeState)p));
/*  90 */     this.packetHandler.registerHandler(417, p -> handle((PrefabUnselectPrefab)p));
/*  91 */     this.packetHandler.registerHandler(421, p -> handle((BuilderToolSetEntityPickupEnabled)p));
/*  92 */     this.packetHandler.registerHandler(422, p -> handle((BuilderToolSetEntityLight)p));
/*  93 */     this.packetHandler.registerHandler(423, p -> handle((BuilderToolSetNPCDebug)p));
/*     */   }
/*     */   
/*     */   static boolean hasPermission(@Nonnull Player player) {
/*  97 */     if (!player.hasPermission("hytale.editor.builderTools")) {
/*  98 */       player.sendMessage(Message.translation("server.builderTools.usageDenied"));
/*  99 */       return false;
/*     */     } 
/*     */     
/* 102 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   static boolean hasPermission(@Nonnull Player player, @Nonnull String permission) {
/* 107 */     if (player.hasPermission(permission) || player.hasPermission("hytale.editor.builderTools")) {
/* 108 */       return true;
/*     */     }
/* 110 */     player.sendMessage(Message.translation("server.builderTools.usageDenied"));
/* 111 */     return false;
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetTransformationModeState packet) {
/* 115 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 116 */     Ref<EntityStore> ref = playerRef.getReference();
/* 117 */     if (ref == null || !ref.isValid()) {
/* 118 */       throw new RuntimeException("Unable to process BuilderToolSetTransformationModeState packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 121 */     Store<EntityStore> store = ref.getStore();
/* 122 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 124 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent))
/*     */             return; 
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid()).setInSelectionTransformationMode(packet.enabled);
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolArgUpdate packet) {
/* 134 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 135 */     Ref<EntityStore> ref = playerRef.getReference();
/* 136 */     if (ref == null || !ref.isValid()) {
/* 137 */       throw new RuntimeException("Unable to process BuilderToolArgUpdate packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 140 */     Store<EntityStore> store = ref.getStore();
/* 141 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 143 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.brush.config"))
/*     */             return; 
/*     */           BuilderToolsPlugin.get().onToolArgUpdate(playerRef, playerComponent, packet);
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull LoadHotbar packet) {
/* 152 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 153 */     Ref<EntityStore> ref = playerRef.getReference();
/* 154 */     if (ref == null || !ref.isValid()) {
/* 155 */       throw new RuntimeException("Unable to process LoadHotbar packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 158 */     Store<EntityStore> store = ref.getStore();
/* 159 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 161 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           playerComponent.getHotbarManager().loadHotbar(ref, (short)packet.inventoryRow, (ComponentAccessor)store);
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull SaveHotbar packet) {
/* 169 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 170 */     Ref<EntityStore> ref = playerRef.getReference();
/* 171 */     if (ref == null || !ref.isValid()) {
/* 172 */       throw new RuntimeException("Unable to process SaveHotbar packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 175 */     Store<EntityStore> store = ref.getStore();
/* 176 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 178 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           playerComponent.getHotbarManager().saveHotbar(ref, (short)packet.inventoryRow, (ComponentAccessor)store);
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolEntityAction packet) {
/* 186 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 187 */     Ref<EntityStore> ref = playerRef.getReference();
/* 188 */     if (ref == null || !ref.isValid()) {
/* 189 */       throw new RuntimeException("Unable to process BuilderToolEntityAction packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 192 */     Store<EntityStore> store = ref.getStore();
/* 193 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 195 */     world.execute(() -> {
/*     */           UUIDComponent uuidComponent;
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent)) {
/*     */             return;
/*     */           }
/*     */           int entityId = packet.entityId;
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(entityId);
/*     */           if (entityReference == null) {
/*     */             playerComponent.sendMessage(Message.translation("server.general.entityNotFound").param("id", entityId));
/*     */             return;
/*     */           } 
/*     */           Player targetPlayerComponent = (Player)store.getComponent(entityReference, Player.getComponentType());
/*     */           if (targetPlayerComponent != null) {
/*     */             playerComponent.sendMessage(Message.translation("server.builderTools.entityTool.cannotTargetPlayer"));
/*     */             return;
/*     */           } 
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           switch (packet.action) {
/*     */             case HistoryUndo:
/*     */               uuidComponent = (UUIDComponent)store.getComponent(entityReference, UUIDComponent.getComponentType());
/*     */               if (uuidComponent != null) {
/*     */                 CommandManager.get().handleCommand((CommandSender)playerComponent, "npc freeze --toggle --entity " + String.valueOf(uuidComponent.getUuid()));
/*     */               }
/*     */               break;
/*     */             case HistoryRedo:
/*     */               world.execute(());
/*     */               break;
/*     */             case SelectionCopy:
/*     */               world.execute(());
/*     */               break;
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolGeneralAction packet) {
/* 231 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 232 */     Ref<EntityStore> ref = playerRef.getReference();
/* 233 */     if (ref == null || !ref.isValid()) {
/* 234 */       throw new RuntimeException("Unable to process BuilderToolGeneralAction packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 237 */     Store<EntityStore> store = ref.getStore();
/* 238 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 240 */     world.execute(() -> {
/*     */           TransformComponent transformComponent;
/*     */           BuilderToolsPlugin.BuilderState builderState;
/*     */           Vector3d position;
/*     */           Vector3i intTriple;
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           switch (packet.action) {
/*     */             case HistoryUndo:
/*     */               if (!hasPermission(playerComponent, "hytale.editor.history"))
/*     */                 return; 
/*     */               BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */               break;
/*     */             case HistoryRedo:
/*     */               if (!hasPermission(playerComponent, "hytale.editor.history"))
/*     */                 return; 
/*     */               BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */               break;
/*     */             case SelectionCopy:
/*     */               if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard"))
/*     */                 return; 
/*     */               CopyCommand.copySelection(ref, (ComponentAccessor)store);
/*     */               break;
/*     */             case SelectionPosition1:
/*     */             case SelectionPosition2:
/*     */               if (!hasPermission(playerComponent, "hytale.editor.selection.use"))
/*     */                 return; 
/*     */               transformComponent = (TransformComponent)store.getComponent(ref, TransformComponent.getComponentType());
/*     */               builderState = BuilderToolsPlugin.getState(playerComponent, playerRef);
/*     */               position = transformComponent.getPosition();
/*     */               intTriple = new Vector3i(MathUtil.floor(position.getX()), MathUtil.floor(position.getY()), MathUtil.floor(position.getZ()));
/*     */               BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */               break;
/*     */             case ActivateToolMode:
/*     */               if (!hasPermission(playerComponent))
/*     */                 return; 
/*     */               playerComponent.getInventory().setUsingToolsItem(true);
/*     */               break;
/*     */             case DeactivateToolMode:
/*     */               if (!hasPermission(playerComponent))
/*     */                 return; 
/*     */               playerComponent.getInventory().setUsingToolsItem(false);
/*     */               break;
/*     */           } 
/*     */         });
/*     */   } public void handle(@Nonnull BuilderToolSelectionUpdate packet) {
/* 287 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 288 */     Ref<EntityStore> ref = playerRef.getReference();
/* 289 */     if (ref == null || !ref.isValid()) {
/* 290 */       throw new RuntimeException("Unable to process BuilderToolSelectionUpdate packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 293 */     Store<EntityStore> store = ref.getStore();
/* 294 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 296 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.use")) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(BuilderToolSelectionToolAskForClipboard packet) {
/* 307 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 308 */     Ref<EntityStore> ref = playerRef.getReference();
/* 309 */     if (ref == null || !ref.isValid()) {
/* 310 */       throw new RuntimeException("Unable to process BuilderToolSelectionToolAskForClipboard packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 313 */     Store<EntityStore> store = ref.getStore();
/* 314 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 316 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int toInt(float value) {
/* 355 */     return (int)Math.floor(value + 0.1D);
/*     */   }
/*     */   
/*     */   private void handle(@Nonnull BuilderToolSelectionTransform packet) {
/* 359 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 360 */     Ref<EntityStore> ref = playerRef.getReference();
/* 361 */     if (ref == null || !ref.isValid()) {
/* 362 */       throw new RuntimeException("Unable to process BuilderToolSelectionTransform packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 365 */     Store<EntityStore> store = ref.getStore();
/* 366 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 368 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           boolean keepEmptyBlocks = true;
/*     */           BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
/*     */           if (builderTool != null && builderTool.getId().equals("Selection")) {
/*     */             BuilderTool.ArgData args = builderTool.getItemArgData(playerComponent.getInventory().getItemInHand());
/*     */             if (args != null && args.tool() != null) {
/*     */               keepEmptyBlocks = ((Boolean)args.tool().getOrDefault("KeepEmptyBlocks", Boolean.valueOf(true))).booleanValue();
/*     */             }
/*     */           } 
/*     */           boolean finalKeepEmptyBlocks = keepEmptyBlocks;
/*     */           float[] tmx = new float[16];
/*     */           for (int i = 0; i < packet.transformationMatrix.length; i++) {
/*     */             tmx[i] = toInt(packet.transformationMatrix[i]);
/*     */           }
/*     */           Matrix4d transformationMatrix = (new Matrix4d()).assign(tmx[0], tmx[4], tmx[8], tmx[12], tmx[1], tmx[5], tmx[9], tmx[13], tmx[2], tmx[6], tmx[10], tmx[14], tmx[3], tmx[7], tmx[11], tmx[15]);
/*     */           Vector3i initialSelectionMin = new Vector3i(packet.initialSelectionMin.x, packet.initialSelectionMin.y, packet.initialSelectionMin.z);
/*     */           Vector3i initialSelectionMax = new Vector3i(packet.initialSelectionMax.x, packet.initialSelectionMax.y, packet.initialSelectionMax.z);
/*     */           Vector3f rotationOrigin = new Vector3f(packet.initialRotationOrigin.x, packet.initialRotationOrigin.y, packet.initialRotationOrigin.z);
/*     */           PrototypePlayerBuilderToolSettings prototypeSettings = ToolOperation.getOrCreatePrototypeSettings(playerRef.getUuid());
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull BuilderToolExtrudeAction packet) {
/* 478 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 479 */     Ref<EntityStore> ref = playerRef.getReference();
/* 480 */     if (ref == null || !ref.isValid()) {
/* 481 */       throw new RuntimeException("Unable to process BuilderToolExtrudeAction packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 484 */     Store<EntityStore> store = ref.getStore();
/* 485 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 487 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.modify")) {
/*     */             return;
/*     */           }
/*     */           BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
/*     */           if (builderTool == null || !builderTool.getId().equals("Extrude")) {
/*     */             return;
/*     */           }
/*     */           ItemStack activeItemStack = playerComponent.getInventory().getItemInHand();
/*     */           BuilderTool.ArgData args = builderTool.getItemArgData(activeItemStack);
/*     */           int extrudeDepth = ((Integer)args.tool().get("ExtrudeDepth")).intValue();
/*     */           int extrudeRadius = ((Integer)args.tool().get("ExtrudeRadius")).intValue();
/*     */           int blockId = ((BlockPattern)args.tool().get("ExtrudeMaterial")).firstBlock();
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull BuilderToolStackArea packet) {
/* 513 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 514 */     Ref<EntityStore> ref = playerRef.getReference();
/* 515 */     if (ref == null || !ref.isValid()) {
/* 516 */       throw new RuntimeException("Unable to process BuilderToolStackArea packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 519 */     Store<EntityStore> store = ref.getStore();
/* 520 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 522 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i fromBlockPosition(@Nonnull BlockPosition position) {
/* 538 */     return new Vector3i(position.x, position.y, position.z);
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolRotateClipboard packet) {
/* 542 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 543 */     Ref<EntityStore> ref = playerRef.getReference();
/* 544 */     if (ref == null || !ref.isValid()) {
/* 545 */       throw new RuntimeException("Unable to process BuilderToolPasteClipboard packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 548 */     Store<EntityStore> store = ref.getStore();
/* 549 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 551 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
/*     */             return;
/*     */           }
/*     */           Axis axis = (packet.axis == Axis.X) ? Axis.X : ((packet.axis == Axis.Y) ? Axis.Y : Axis.Z);
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull BuilderToolPasteClipboard packet) {
/* 566 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 567 */     Ref<EntityStore> ref = playerRef.getReference();
/* 568 */     if (ref == null || !ref.isValid()) {
/* 569 */       throw new RuntimeException("Unable to process BuilderToolPasteClipboard packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 572 */     Store<EntityStore> store = ref.getStore();
/* 573 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 575 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.selection.clipboard")) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolLineAction packet) {
/* 586 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 587 */     Ref<EntityStore> ref = playerRef.getReference();
/* 588 */     if (ref == null || !ref.isValid()) {
/* 589 */       throw new RuntimeException("Unable to process BuilderToolLineAction packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 592 */     Store<EntityStore> store = ref.getStore();
/* 593 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 595 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.brush.use")) {
/*     */             return;
/*     */           }
/*     */           BuilderTool builderTool = BuilderTool.getActiveBuilderTool(playerComponent);
/*     */           if (builderTool == null || !builderTool.getId().equals("Line")) {
/*     */             return;
/*     */           }
/*     */           BuilderTool.ArgData args = builderTool.getItemArgData(playerComponent.getInventory().getItemInHand());
/*     */           BrushData.Values brushData = args.brush();
/*     */           int lineWidth = ((Integer)args.tool().get("bLineWidth")).intValue();
/*     */           int lineHeight = ((Integer)args.tool().get("cLineHeight")).intValue();
/*     */           BrushShape lineShape = BrushShape.valueOf((String)args.tool().get("dLineShape"));
/*     */           BrushOrigin lineOrigin = BrushOrigin.valueOf((String)args.tool().get("eLineOrigin"));
/*     */           int lineWallThickness = ((Integer)args.tool().get("fLineWallThickness")).intValue();
/*     */           int lineSpacing = ((Integer)args.tool().get("gLineSpacing")).intValue();
/*     */           int lineDensity = ((Integer)args.tool().get("hLineDensity")).intValue();
/*     */           BlockPattern lineMaterial = (BlockPattern)args.tool().get("aLineMaterial");
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull BuilderToolOnUseInteraction packet) {
/* 628 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 629 */     Ref<EntityStore> ref = playerRef.getReference();
/* 630 */     if (ref == null || !ref.isValid()) {
/* 631 */       throw new RuntimeException("Unable to process BuilderToolOnUseInteraction packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 634 */     Store<EntityStore> store = ref.getStore();
/* 635 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 637 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent, "hytale.editor.brush.use"))
/*     */             return; 
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           BuilderToolsPlugin.addToQueue(playerComponent, playerRef, ());
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetEntityTransform packet) {
/* 647 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 648 */     Ref<EntityStore> ref = playerRef.getReference();
/* 649 */     if (ref == null || !ref.isValid()) {
/* 650 */       throw new RuntimeException("Unable to process BuilderToolSetEntityTransform packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 653 */     Store<EntityStore> store = ref.getStore();
/* 654 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 656 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           assert playerComponent != null;
/*     */           if (!hasPermission(playerComponent)) {
/*     */             return;
/*     */           }
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
/*     */           if (entityReference == null) {
/*     */             return;
/*     */           }
/*     */           TransformComponent transformComponent = (TransformComponent)store.getComponent(entityReference, TransformComponent.getComponentType());
/*     */           assert transformComponent != null;
/*     */           HeadRotation headRotation = (HeadRotation)store.getComponent(entityReference, HeadRotation.getComponentType());
/*     */           ModelTransform modelTransform = packet.modelTransform;
/*     */           if (modelTransform != null) {
/*     */             boolean hasPosition = (modelTransform.position != null);
/*     */             boolean hasLookOrientation = (modelTransform.lookOrientation != null);
/*     */             boolean hasBodyOrientation = (modelTransform.bodyOrientation != null);
/*     */             if (hasPosition) {
/*     */               transformComponent.getPosition().assign(modelTransform.position.x, modelTransform.position.y, modelTransform.position.z);
/*     */             }
/*     */             if (hasLookOrientation && headRotation != null) {
/*     */               headRotation.getRotation().assign(modelTransform.lookOrientation.pitch, modelTransform.lookOrientation.yaw, modelTransform.lookOrientation.roll);
/*     */             }
/*     */             if (hasBodyOrientation) {
/*     */               transformComponent.getRotation().assign(modelTransform.bodyOrientation.pitch, modelTransform.bodyOrientation.yaw, modelTransform.bodyOrientation.roll);
/*     */             }
/*     */             if (hasPosition || hasLookOrientation || hasBodyOrientation) {
/*     */               transformComponent.markChunkDirty((ComponentAccessor)store);
/*     */             }
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull PrefabUnselectPrefab packet) {
/* 695 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 696 */     Ref<EntityStore> ref = playerRef.getReference();
/* 697 */     if (ref == null || !ref.isValid()) {
/* 698 */       throw new RuntimeException("Unable to process PrefabUnselectPrefab packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 701 */     Store<EntityStore> store = ref.getStore();
/* 702 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 704 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent)) {
/*     */             return;
/*     */           }
/*     */           LOGGER.at(Level.INFO).log("%s: %s", this.packetHandler.getIdentifier(), packet);
/*     */           PrefabEditSessionManager prefabEditSessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
/*     */           PrefabEditSession prefabEditSession = prefabEditSessionManager.getPrefabEditSession(playerRef.getUuid());
/*     */           if (prefabEditSession == null) {
/*     */             playerComponent.sendMessage(Message.translation("server.commands.editprefab.notInEditSession"));
/*     */             return;
/*     */           } 
/*     */           if (prefabEditSession.clearSelectedPrefab(ref, (ComponentAccessor)store)) {
/*     */             playerComponent.sendMessage(Message.translation("server.commands.editprefab.unselected"));
/*     */           } else {
/*     */             playerComponent.sendMessage(Message.translation("server.commands.editprefab.noPrefabSelected"));
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetEntityScale packet) {
/* 727 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 728 */     Ref<EntityStore> ref = playerRef.getReference();
/* 729 */     if (ref == null || !ref.isValid()) {
/* 730 */       throw new RuntimeException("Unable to process BuilderToolSetEntityScale packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 733 */     Store<EntityStore> store = ref.getStore();
/* 734 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 736 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent)) {
/*     */             return;
/*     */           }
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
/*     */           if (entityReference == null) {
/*     */             return;
/*     */           }
/*     */           PropComponent propComponent = (PropComponent)store.getComponent(entityReference, PropComponent.getComponentType());
/*     */           if (propComponent == null)
/*     */             return; 
/*     */           EntityScaleComponent scaleComponent = (EntityScaleComponent)store.getComponent(entityReference, EntityScaleComponent.getComponentType());
/*     */           if (scaleComponent == null) {
/*     */             scaleComponent = new EntityScaleComponent(packet.scale);
/*     */             store.addComponent(entityReference, EntityScaleComponent.getComponentType(), (Component)scaleComponent);
/*     */           } else {
/*     */             scaleComponent.setScale(packet.scale);
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetEntityPickupEnabled packet) {
/* 759 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 760 */     Ref<EntityStore> ref = playerRef.getReference();
/* 761 */     if (ref == null || !ref.isValid()) {
/* 762 */       throw new RuntimeException("Unable to process BuilderToolSetEntityPickupEnabled packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 765 */     Store<EntityStore> store = ref.getStore();
/* 766 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 768 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent)) {
/*     */             return;
/*     */           }
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
/*     */           if (entityReference == null) {
/*     */             return;
/*     */           }
/*     */           PropComponent propComponent = (PropComponent)store.getComponent(entityReference, PropComponent.getComponentType());
/*     */           if (propComponent == null) {
/*     */             return;
/*     */           }
/*     */           if (packet.enabled) {
/*     */             store.ensureComponent(entityReference, Interactable.getComponentType());
/*     */             if (store.getComponent(entityReference, PreventPickup.getComponentType()) != null) {
/*     */               store.removeComponent(entityReference, PreventPickup.getComponentType());
/*     */             }
/*     */             Interactions interactionsComponent = (Interactions)store.getComponent(entityReference, Interactions.getComponentType());
/*     */             if (interactionsComponent == null) {
/*     */               interactionsComponent = new Interactions();
/*     */               store.addComponent(entityReference, Interactions.getComponentType(), (Component)interactionsComponent);
/*     */             } 
/*     */             interactionsComponent.setInteractionId(InteractionType.Use, "*PickupItem");
/*     */             interactionsComponent.setInteractionHint("server.interactionHints.pickup");
/*     */           } else {
/*     */             if (store.getComponent(entityReference, Interactable.getComponentType()) != null)
/*     */               store.removeComponent(entityReference, Interactable.getComponentType()); 
/*     */             if (store.getComponent(entityReference, Interactions.getComponentType()) != null)
/*     */               store.removeComponent(entityReference, Interactions.getComponentType()); 
/*     */             store.ensureComponent(entityReference, PreventPickup.getComponentType());
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetEntityLight packet) {
/* 804 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 805 */     Ref<EntityStore> ref = playerRef.getReference();
/* 806 */     if (ref == null || !ref.isValid()) {
/* 807 */       throw new RuntimeException("Unable to process BuilderToolSetEntityLight packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 810 */     Store<EntityStore> store = ref.getStore();
/* 811 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 813 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent))
/*     */             return; 
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
/*     */           if (entityReference == null)
/*     */             return; 
/*     */           if (packet.light == null) {
/*     */             store.removeComponent(entityReference, DynamicLight.getComponentType());
/*     */             store.removeComponent(entityReference, PersistentDynamicLight.getComponentType());
/*     */           } else {
/*     */             ColorLight colorLight = new ColorLight(packet.light.radius, packet.light.red, packet.light.green, packet.light.blue);
/*     */             store.putComponent(entityReference, DynamicLight.getComponentType(), (Component)new DynamicLight(colorLight));
/*     */             store.putComponent(entityReference, PersistentDynamicLight.getComponentType(), (Component)new PersistentDynamicLight(colorLight));
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public void handle(@Nonnull BuilderToolSetNPCDebug packet) {
/* 832 */     PlayerRef playerRef = this.packetHandler.getPlayerRef();
/* 833 */     Ref<EntityStore> ref = playerRef.getReference();
/* 834 */     if (ref == null || !ref.isValid()) {
/* 835 */       throw new RuntimeException("Unable to process BuilderToolSetNPCDebug packet. Player ref is invalid!");
/*     */     }
/*     */     
/* 838 */     Store<EntityStore> store = ref.getStore();
/* 839 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*     */     
/* 841 */     world.execute(() -> {
/*     */           Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*     */           if (!hasPermission(playerComponent))
/*     */             return; 
/*     */           Ref<EntityStore> entityReference = world.getEntityStore().getRefFromNetworkId(packet.entityId);
/*     */           if (entityReference == null)
/*     */             return; 
/*     */           UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(entityReference, UUIDComponent.getComponentType());
/*     */           if (uuidComponent == null)
/*     */             return; 
/*     */           UUID uuid = uuidComponent.getUuid();
/*     */           String command = packet.enabled ? ("npc debug set display --entity " + String.valueOf(uuid)) : ("npc debug clear --entity " + String.valueOf(uuid));
/*     */           CommandManager.get().handleCommand((CommandSender)playerComponent, command);
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\BuilderToolsPacketHandler.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */