/*      */ package com.hypixel.hytale.server.core.entity;
/*      */ 
/*      */ import com.hypixel.hytale.common.util.FormatUtil;
/*      */ import com.hypixel.hytale.common.util.ListUtil;
/*      */ import com.hypixel.hytale.component.CommandBuffer;
/*      */ import com.hypixel.hytale.component.Component;
/*      */ import com.hypixel.hytale.component.ComponentAccessor;
/*      */ import com.hypixel.hytale.component.Ref;
/*      */ import com.hypixel.hytale.function.function.TriFunction;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.math.vector.Vector4d;
/*      */ import com.hypixel.hytale.protocol.BlockPosition;
/*      */ import com.hypixel.hytale.protocol.ForkedChainId;
/*      */ import com.hypixel.hytale.protocol.GameMode;
/*      */ import com.hypixel.hytale.protocol.InteractionChainData;
/*      */ import com.hypixel.hytale.protocol.InteractionCooldown;
/*      */ import com.hypixel.hytale.protocol.InteractionState;
/*      */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*      */ import com.hypixel.hytale.protocol.InteractionType;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.protocol.RootInteractionSettings;
/*      */ import com.hypixel.hytale.protocol.Vector3f;
/*      */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*      */ import com.hypixel.hytale.protocol.packets.interaction.CancelInteractionChain;
/*      */ import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
/*      */ import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
/*      */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*      */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*      */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*      */ import com.hypixel.hytale.server.core.io.handlers.game.GamePacketHandler;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.IInteractionSimulationHandler;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionTypeUtils;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.CollectorTag;
/*      */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*      */ import com.hypixel.hytale.server.core.modules.time.TimeResource;
/*      */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*      */ import com.hypixel.hytale.server.core.universe.world.World;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.util.UUIDUtil;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*      */ import java.util.Arrays;
/*      */ import java.util.Deque;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.function.Predicate;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class InteractionManager
/*      */   implements Component<EntityStore>
/*      */ {
/*      */   public static final double MAX_REACH_DISTANCE = 8.0D;
/*  199 */   public static final float[] DEFAULT_CHARGE_TIMES = new float[] { 0.0F };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  204 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  209 */   private final Int2ObjectMap<InteractionChain> chains = (Int2ObjectMap<InteractionChain>)new Int2ObjectOpenHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  216 */   private final Int2ObjectMap<InteractionChain> unmodifiableChains = Int2ObjectMaps.unmodifiable(this.chains);
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  221 */   private final CooldownHandler cooldownHandler = new CooldownHandler();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private final LivingEntity entity;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private final PlayerRef playerRef;
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean hasRemoteClient;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   private final IInteractionSimulationHandler interactionSimulationHandler;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  247 */   private final ObjectList<InteractionSyncData> tempSyncDataList = (ObjectList<InteractionSyncData>)new ObjectArrayList();
/*      */   
/*      */   private int lastServerChainId;
/*      */   
/*      */   private int lastClientChainId;
/*      */   
/*      */   private long packetQueueTime;
/*      */   
/*  255 */   private final float[] globalTimeShift = new float[InteractionType.VALUES.length];
/*  256 */   private final boolean[] globalTimeShiftDirty = new boolean[InteractionType.VALUES.length];
/*      */   
/*      */   private boolean timeShiftsDirty;
/*      */   
/*  260 */   private final ObjectList<SyncInteractionChain> syncPackets = (ObjectList<SyncInteractionChain>)new ObjectArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  267 */   private long currentTime = 1L;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  272 */   private final ObjectList<InteractionChain> chainStartQueue = (ObjectList<InteractionChain>)new ObjectArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  280 */   private final Predicate<InteractionChain> cachedTickChain = this::tickChain;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   protected CommandBuffer<EntityStore> commandBuffer;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InteractionManager(@Nonnull LivingEntity entity, @Nullable PlayerRef playerRef, @Nonnull IInteractionSimulationHandler simulationHandler) {
/*  299 */     this.entity = entity;
/*  300 */     this.playerRef = playerRef;
/*  301 */     this.hasRemoteClient = (playerRef != null);
/*  302 */     this.interactionSimulationHandler = simulationHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Int2ObjectMap<InteractionChain> getChains() {
/*  310 */     return this.unmodifiableChains;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public IInteractionSimulationHandler getInteractionSimulationHandler() {
/*  318 */     return this.interactionSimulationHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private long getOperationTimeoutThreshold() {
/*  329 */     if (this.playerRef != null) {
/*  330 */       return this.playerRef.getPacketHandler().getOperationTimeoutThreshold();
/*      */     }
/*      */     
/*  333 */     assert this.commandBuffer != null;
/*  334 */     World world = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/*  335 */     return (world.getTickStepNanos() / 1000000 * 10);
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
/*      */   private boolean waitingForClient(@Nonnull Ref<EntityStore> ref) {
/*  348 */     assert this.commandBuffer != null;
/*  349 */     Player playerComponent = (Player)this.commandBuffer.getComponent(ref, Player.getComponentType());
/*      */     
/*  351 */     if (playerComponent != null) {
/*  352 */       return playerComponent.isWaitingForClientReady();
/*      */     }
/*  354 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated(forRemoval = true)
/*      */   public void setHasRemoteClient(boolean hasRemoteClient) {
/*  365 */     this.hasRemoteClient = hasRemoteClient;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void copyFrom(@Nonnull InteractionManager interactionManager) {
/*  377 */     this.chains.putAll((Map)interactionManager.chains);
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
/*      */   public void tick(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, float dt) {
/*  389 */     this.currentTime += ((EntityStore)commandBuffer.getExternalData()).getWorld().getTickStepNanos();
/*      */     
/*  391 */     this.commandBuffer = commandBuffer;
/*  392 */     clearAllGlobalTimeShift(dt);
/*  393 */     this.cooldownHandler.tick(dt);
/*      */     
/*  395 */     for (ObjectListIterator<InteractionChain> objectListIterator = this.chainStartQueue.iterator(); objectListIterator.hasNext(); ) { InteractionChain interactionChain = objectListIterator.next();
/*  396 */       executeChain0(ref, interactionChain); }
/*      */     
/*  398 */     this.chainStartQueue.clear();
/*      */     
/*  400 */     Deque<SyncInteractionChain> packetQueue = null;
/*      */     
/*  402 */     if (this.playerRef != null) {
/*  403 */       packetQueue = ((GamePacketHandler)this.playerRef.getPacketHandler()).getInteractionPacketQueue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  414 */     if (packetQueue != null && !packetQueue.isEmpty()) {
/*  415 */       boolean first = true;
/*  416 */       while (tryConsumePacketQueue(ref, packetQueue) || first) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  421 */         if (!this.chains.isEmpty()) {
/*  422 */           this.chains.values().removeIf(this.cachedTickChain);
/*      */         }
/*  424 */         float cooldownDt = 0.0F;
/*  425 */         for (float shift : this.globalTimeShift) {
/*  426 */           cooldownDt = Math.max(cooldownDt, shift);
/*      */         }
/*  428 */         if (cooldownDt > 0.0F) {
/*  429 */           this.cooldownHandler.tick(cooldownDt);
/*      */         }
/*  431 */         first = false;
/*      */       } 
/*  433 */       this.commandBuffer = null;
/*      */       
/*      */       return;
/*      */     } 
/*  437 */     if (!this.chains.isEmpty()) {
/*  438 */       this.chains.values().removeIf(this.cachedTickChain);
/*      */     }
/*      */     
/*  441 */     this.commandBuffer = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean tryConsumePacketQueue(@Nonnull Ref<EntityStore> ref, @Nonnull Deque<SyncInteractionChain> packetQueue) {
/*  452 */     Iterator<SyncInteractionChain> it = packetQueue.iterator();
/*      */     
/*  454 */     boolean finished = false;
/*  455 */     boolean desynced = false;
/*  456 */     int highestChainId = -1;
/*      */     
/*  458 */     boolean changed = false;
/*      */ 
/*      */     
/*  461 */     label67: while (it.hasNext()) {
/*  462 */       SyncInteractionChain packet = it.next();
/*      */       
/*  464 */       if (packet.desync) {
/*  465 */         HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  466 */         if (context.isEnabled()) context.log("Client packet flagged as desync"); 
/*  467 */         desynced = true;
/*      */       } 
/*      */       
/*  470 */       InteractionChain chain = (InteractionChain)this.chains.get(packet.chainId);
/*  471 */       if (chain != null && packet.forkedId != null) {
/*      */         
/*  473 */         ForkedChainId id = packet.forkedId;
/*  474 */         while (id != null) {
/*      */           
/*  476 */           InteractionChain subChain = chain.getForkedChain(id);
/*  477 */           if (subChain == null) {
/*      */             
/*  479 */             InteractionChain.TempChain tempChain = chain.getTempForkedChain(id);
/*  480 */             if (tempChain == null)
/*  481 */               continue label67;  tempChain.setBaseForkedChainId(id);
/*  482 */             ForkedChainId lastId = id;
/*  483 */             id = id.forkedId;
/*  484 */             while (id != null) {
/*  485 */               tempChain = tempChain.getOrCreateTempForkedChain(id);
/*  486 */               tempChain.setBaseForkedChainId(id);
/*  487 */               lastId = id;
/*  488 */               id = id.forkedId;
/*      */             } 
/*  490 */             tempChain.setForkedChainId(packet.forkedId);
/*  491 */             tempChain.setBaseForkedChainId(lastId);
/*  492 */             tempChain.setChainData(packet.data);
/*  493 */             sync(ref, tempChain, packet);
/*  494 */             changed = true;
/*  495 */             it.remove();
/*  496 */             this.packetQueueTime = 0L;
/*      */             continue label67;
/*      */           } 
/*  499 */           chain = subChain;
/*  500 */           id = id.forkedId;
/*      */         } 
/*      */       } 
/*      */       
/*  504 */       highestChainId = Math.max(highestChainId, packet.chainId);
/*      */       
/*  506 */       if (chain == null && !finished) {
/*      */         
/*  508 */         if (syncStart(ref, packet)) {
/*      */           
/*  510 */           changed = true;
/*  511 */           it.remove();
/*  512 */           this.packetQueueTime = 0L; continue;
/*      */         } 
/*  514 */         if (!waitingForClient(ref)) {
/*      */           long queuedTime;
/*      */           
/*  517 */           if (this.packetQueueTime == 0L) {
/*  518 */             this.packetQueueTime = this.currentTime;
/*  519 */             queuedTime = 0L;
/*      */           } else {
/*  521 */             queuedTime = this.currentTime - this.packetQueueTime;
/*      */           } 
/*      */ 
/*      */           
/*  525 */           HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  526 */           if (context.isEnabled()) {
/*  527 */             context.log("Queued chain %d for %s", packet.chainId, FormatUtil.nanosToString(queuedTime));
/*      */           }
/*      */           
/*  530 */           if (queuedTime > TimeUnit.MILLISECONDS.toNanos(getOperationTimeoutThreshold())) {
/*  531 */             sendCancelPacket(packet.chainId, packet.forkedId);
/*  532 */             it.remove();
/*  533 */             context = LOGGER.at(Level.FINE);
/*  534 */             if (context.isEnabled()) context.log("Discarding packet due to queuing for too long: %s", packet); 
/*      */           } 
/*      */         } 
/*  537 */         if (!desynced) finished = true;  continue;
/*  538 */       }  if (chain != null) {
/*      */         
/*  540 */         sync(ref, chain, packet);
/*  541 */         changed = true;
/*  542 */         it.remove();
/*  543 */         this.packetQueueTime = 0L; continue;
/*  544 */       }  if (desynced) {
/*      */ 
/*      */         
/*  547 */         sendCancelPacket(packet.chainId, packet.forkedId);
/*  548 */         it.remove();
/*  549 */         HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/*  550 */         ctx.log("Discarding packet due to desync: %s", packet);
/*      */       } 
/*      */     } 
/*      */     
/*  554 */     if (desynced && !packetQueue.isEmpty()) {
/*      */       
/*  556 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/*  557 */       if (ctx.isEnabled()) ctx.log("Discarding previous packets in queue: (before) %d", packetQueue.size()); 
/*  558 */       packetQueue.removeIf(v -> {
/*  559 */             boolean shouldRemove = (getChain(v.chainId, v.forkedId) == null && UUIDUtil.isEmptyOrNull(v.data.proxyId) && v.initial);
/*      */             
/*      */             if (shouldRemove) {
/*      */               HytaleLogger.Api ctx1 = LOGGER.at(Level.FINE);
/*      */               if (ctx1.isEnabled()) {
/*      */                 ctx1.log("Discarding: %s", v);
/*      */               }
/*      */               sendCancelPacket(v.chainId, v.forkedId);
/*      */             } 
/*      */             return shouldRemove;
/*      */           });
/*  570 */       ctx = LOGGER.at(Level.FINE);
/*  571 */       if (ctx.isEnabled()) ctx.log("Discarded previous packets in queue: (after) %d", packetQueue.size());
/*      */     
/*      */     } 
/*  574 */     return changed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private InteractionChain getChain(int chainId, @Nullable ForkedChainId forkedChainId) {
/*  586 */     InteractionChain chain = (InteractionChain)this.chains.get(chainId);
/*  587 */     if (chain != null && forkedChainId != null) {
/*  588 */       ForkedChainId id = forkedChainId;
/*  589 */       while (id != null) {
/*  590 */         InteractionChain subChain = chain.getForkedChain(id);
/*  591 */         if (subChain == null) {
/*  592 */           return null;
/*      */         }
/*  594 */         chain = subChain;
/*  595 */         id = id.forkedId;
/*      */       } 
/*      */     } 
/*  598 */     return chain;
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
/*      */   private boolean tickChain(@Nonnull InteractionChain chain) {
/*  611 */     if (chain.wasPreTicked()) {
/*  612 */       chain.setPreTicked(false);
/*  613 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  617 */     if (!this.hasRemoteClient) chain.updateSimulatedState();
/*      */     
/*  619 */     chain.getForkedChains().values().removeIf(this.cachedTickChain);
/*      */     
/*  621 */     Ref<EntityStore> ref = this.entity.getReference();
/*  622 */     assert ref != null;
/*      */ 
/*      */     
/*  625 */     if (chain.getServerState() != InteractionState.NotFinished) {
/*      */       
/*  627 */       if (!chain.requiresClient() || chain.getClientState() != InteractionState.NotFinished) {
/*      */         
/*  629 */         LOGGER.at(Level.FINE).log("Remove Chain: %d, %s", chain.getChainId(), chain);
/*  630 */         handleCancelledChain(ref, chain);
/*  631 */         chain.onCompletion(this.cooldownHandler, this.hasRemoteClient);
/*  632 */         return chain.getForkedChains().isEmpty();
/*  633 */       }  if (!waitingForClient(ref)) {
/*      */         
/*  635 */         if (chain.getWaitingForClientFinished() == 0L) {
/*  636 */           chain.setWaitingForClientFinished(this.currentTime);
/*      */         }
/*      */         
/*  639 */         long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - chain.getWaitingForClientFinished());
/*  640 */         HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  641 */         if (context.isEnabled()) {
/*  642 */           context.log("Server finished chain but client hasn't! %d, %s, %s", Integer.valueOf(chain.getChainId()), chain, Long.valueOf(waitMillis));
/*      */         }
/*  644 */         long threshold = getOperationTimeoutThreshold();
/*      */         
/*  646 */         TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  647 */         if (timeResource.getTimeDilationModifier() == 1.0F && waitMillis > threshold) {
/*      */           
/*  649 */           sendCancelPacket(chain);
/*  650 */           return chain.getForkedChains().isEmpty();
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  661 */       return false;
/*      */     } 
/*      */     
/*  664 */     int baseOpIndex = chain.getOperationIndex();
/*      */     
/*      */     try {
/*  667 */       doTickChain(ref, chain);
/*  668 */     } catch (ChainCancelledException e) {
/*  669 */       chain.setServerState(e.state);
/*  670 */       chain.setClientState(e.state);
/*      */ 
/*      */ 
/*      */       
/*  674 */       chain.updateServerState();
/*  675 */       if (!this.hasRemoteClient) chain.updateSimulatedState(); 
/*  676 */       if (chain.requiresClient()) {
/*  677 */         sendSyncPacket(chain, baseOpIndex, (List<InteractionSyncData>)this.tempSyncDataList);
/*  678 */         sendCancelPacket(chain);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  684 */     if (chain.getServerState() != InteractionState.NotFinished) {
/*      */       
/*  686 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  687 */       if (context.isEnabled()) {
/*  688 */         context.log("Server finished chain: %d-%s, %s in %fs", Integer.valueOf(chain.getChainId()), chain.getForkedChainId(), chain, Float.valueOf(chain.getTimeInSeconds()));
/*      */       }
/*      */       
/*  691 */       if (!chain.requiresClient() || chain.getClientState() != InteractionState.NotFinished) {
/*  692 */         context = LOGGER.at(Level.FINE);
/*  693 */         if (context.isEnabled()) context.log("Remove Chain: %d-%s, %s", Integer.valueOf(chain.getChainId()), chain.getForkedChainId(), chain); 
/*  694 */         handleCancelledChain(ref, chain);
/*  695 */         chain.onCompletion(this.cooldownHandler, this.hasRemoteClient);
/*  696 */         return chain.getForkedChains().isEmpty();
/*      */       }
/*      */     
/*  699 */     } else if (chain.getClientState() != InteractionState.NotFinished && !waitingForClient(ref)) {
/*      */       
/*  701 */       if (chain.getWaitingForServerFinished() == 0L) chain.setWaitingForServerFinished(this.currentTime); 
/*  702 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - chain.getWaitingForServerFinished());
/*  703 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  704 */       if (context.isEnabled()) {
/*  705 */         context.log("Client finished chain but server hasn't! %d, %s, %s", Integer.valueOf(chain.getChainId()), chain, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/*  708 */       long threshold = getOperationTimeoutThreshold();
/*  709 */       if (waitMillis > threshold)
/*      */       {
/*      */ 
/*      */         
/*  713 */         LOGGER.at(Level.SEVERE).log("Client finished chain earlier than server! %d, %s", chain.getChainId(), chain);
/*      */       }
/*      */     } 
/*      */     
/*  717 */     return false;
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
/*      */   private void handleCancelledChain(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain) {
/*  731 */     assert this.commandBuffer != null;
/*      */     
/*  733 */     RootInteraction root = chain.getRootInteraction();
/*  734 */     int maxOperations = root.getOperationMax();
/*      */ 
/*      */     
/*  737 */     if (chain.getOperationCounter() >= maxOperations) {
/*      */       return;
/*      */     }
/*  740 */     InteractionEntry entry = chain.getInteraction(chain.getOperationIndex());
/*  741 */     if (entry == null)
/*      */       return; 
/*  743 */     Operation operation = root.getOperation(chain.getOperationCounter());
/*  744 */     if (operation == null) {
/*  745 */       throw new IllegalStateException("Failed to find operation during simulation tick of chain '" + root.getId() + "'");
/*      */     }
/*      */     
/*  748 */     InteractionContext context = chain.getContext();
/*      */ 
/*      */     
/*  751 */     (entry.getServerState()).state = InteractionState.Failed;
/*  752 */     if (entry.getClientState() != null) (entry.getClientState()).state = InteractionState.Failed;
/*      */     
/*      */     try {
/*  755 */       context.initEntry(chain, entry, this.entity);
/*      */       
/*  757 */       TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  758 */       operation.handle(ref, false, entry.getTimeInSeconds(this.currentTime) * timeResource.getTimeDilationModifier(), chain.getType(), context);
/*      */     } finally {
/*  760 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/*      */ 
/*      */     
/*  764 */     chain.setOperationCounter(maxOperations);
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
/*      */   private void doTickChain(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain) {
/*  781 */     ObjectList<InteractionSyncData> interactionData = this.tempSyncDataList;
/*  782 */     interactionData.clear();
/*      */     
/*  784 */     RootInteraction root = chain.getRootInteraction();
/*  785 */     int maxOperations = root.getOperationMax();
/*      */     
/*  787 */     int currentOp = chain.getOperationCounter();
/*  788 */     int baseOpIndex = chain.getOperationIndex();
/*      */     
/*  790 */     int callDepth = chain.getCallDepth();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  796 */     if (chain.consumeFirstRun()) {
/*  797 */       if (chain.getForkedChainId() == null) {
/*  798 */         chain.setTimeShift(getGlobalTimeShift(chain.getType()));
/*      */       } else {
/*  800 */         InteractionChain parent = (InteractionChain)this.chains.get(chain.getChainId());
/*  801 */         chain.setFirstRun((parent != null && parent.isFirstRun()));
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  807 */       chain.setTimeShift(0.0F);
/*      */     } 
/*      */     
/*  810 */     if (!chain.getContext().getEntity().isValid())
/*      */     {
/*  812 */       throw new ChainCancelledException(chain.getServerState());
/*      */     }
/*      */     
/*      */     while (true) {
/*  816 */       Operation simOp = !this.hasRemoteClient ? root.getOperation(chain.getSimulatedOperationCounter()) : null;
/*  817 */       WaitForDataFrom simWaitFrom = (simOp != null) ? simOp.getWaitForDataFrom() : null;
/*      */       
/*  819 */       long tickTime = this.currentTime;
/*      */ 
/*      */ 
/*      */       
/*  823 */       if (!this.hasRemoteClient && simWaitFrom != WaitForDataFrom.Server) {
/*  824 */         simulationTick(ref, chain, tickTime);
/*      */       }
/*      */       
/*  827 */       interactionData.add(serverTick(ref, chain, tickTime));
/*      */ 
/*      */ 
/*      */       
/*  831 */       if (!chain.getContext().getEntity().isValid() && chain.getServerState() != InteractionState.Finished && chain.getServerState() != InteractionState.Failed) {
/*  832 */         throw new ChainCancelledException(chain.getServerState());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  837 */       if (!this.hasRemoteClient && simWaitFrom == WaitForDataFrom.Server) {
/*  838 */         simulationTick(ref, chain, tickTime);
/*      */       }
/*      */       
/*  841 */       if (!this.hasRemoteClient) {
/*      */         
/*  843 */         if (chain.getRootInteraction() != chain.getSimulatedRootInteraction()) {
/*  844 */           throw new IllegalStateException("Simulation and server tick are not in sync (root interaction).\n" + chain
/*  845 */               .getRootInteraction().getId() + " vs " + String.valueOf(chain.getSimulatedRootInteraction()));
/*      */         }
/*  847 */         if (chain.getOperationCounter() != chain.getSimulatedOperationCounter()) {
/*  848 */           throw new IllegalStateException("Simulation and server tick are not in sync (operation position).\nRoot: " + chain
/*  849 */               .getRootInteraction().getId() + "\nCounter: " + chain
/*  850 */               .getOperationCounter() + " vs " + chain.getSimulatedOperationCounter() + "\nIndex: " + chain
/*  851 */               .getOperationIndex());
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  857 */       if (callDepth != chain.getCallDepth()) {
/*  858 */         callDepth = chain.getCallDepth();
/*  859 */         root = chain.getRootInteraction();
/*  860 */         maxOperations = root.getOperationMax();
/*  861 */       } else if (currentOp == chain.getOperationCounter()) {
/*      */         break;
/*      */       } 
/*      */       
/*  865 */       chain.nextOperationIndex();
/*      */       
/*  867 */       currentOp = chain.getOperationCounter();
/*  868 */       if (currentOp >= maxOperations) {
/*      */         
/*  870 */         while (callDepth > 0) {
/*  871 */           chain.popRoot();
/*  872 */           callDepth = chain.getCallDepth();
/*  873 */           currentOp = chain.getOperationCounter();
/*  874 */           root = chain.getRootInteraction();
/*  875 */           maxOperations = root.getOperationMax();
/*      */ 
/*      */           
/*  878 */           if (currentOp < maxOperations || callDepth == 0) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  884 */         if (callDepth == 0 && currentOp >= maxOperations) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*  889 */     chain.updateServerState();
/*  890 */     if (!this.hasRemoteClient) chain.updateSimulatedState(); 
/*  891 */     if (chain.requiresClient()) {
/*  892 */       sendSyncPacket(chain, baseOpIndex, (List<InteractionSyncData>)interactionData);
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
/*      */   @Nullable
/*      */   private InteractionSyncData serverTick(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain, long tickTime) {
/*      */     int i;
/*  910 */     assert this.commandBuffer != null;
/*      */     
/*  912 */     RootInteraction root = chain.getRootInteraction();
/*  913 */     Operation operation = root.getOperation(chain.getOperationCounter());
/*  914 */     assert operation != null;
/*      */     
/*  916 */     InteractionEntry entry = chain.getOrCreateInteractionEntry(chain.getOperationIndex());
/*      */     
/*  918 */     InteractionSyncData returnData = null;
/*      */ 
/*      */     
/*  921 */     boolean wasWrong = entry.consumeDesyncFlag();
/*  922 */     if (entry.getClientState() == null) {
/*  923 */       i = wasWrong | (!entry.setClientState(chain.removeInteractionSyncData(chain.getOperationIndex())) ? 1 : 0);
/*      */     }
/*      */     
/*  926 */     if (i != 0) {
/*      */       
/*  928 */       returnData = entry.getServerState();
/*  929 */       chain.flagDesync();
/*  930 */       chain.clearInteractionSyncData(chain.getOperationIndex());
/*      */     } 
/*      */     
/*  933 */     TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  934 */     float tickTimeDilation = timeResource.getTimeDilationModifier();
/*      */ 
/*      */     
/*  937 */     if (operation.getWaitForDataFrom() == WaitForDataFrom.Client && entry.getClientState() == null) {
/*  938 */       if (waitingForClient(ref)) return null; 
/*  939 */       if (entry.getWaitingForSyncData() == 0L) entry.setWaitingForSyncData(this.currentTime); 
/*  940 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - entry.getWaitingForSyncData());
/*      */       
/*  942 */       HytaleLogger.Api api = LOGGER.at(Level.FINE);
/*  943 */       if (api.isEnabled()) {
/*  944 */         api.log("Wait for interaction clientData: %d, %s, %s", Integer.valueOf(chain.getOperationIndex()), entry, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/*  947 */       long threshold = getOperationTimeoutThreshold();
/*      */       
/*  949 */       if (tickTimeDilation == 1.0F && waitMillis > threshold)
/*      */       {
/*      */         
/*  952 */         throw new RuntimeException("Client took too long to send clientData! Millis: " + waitMillis + ", Threshold: " + threshold + ",\nChain: " + String.valueOf(chain) + ",\nEntry: " + chain.getOperationIndex() + ", " + String.valueOf(entry) + ",\nWaiting for data from: " + String.valueOf(operation.getWaitForDataFrom()));
/*      */       }
/*      */ 
/*      */       
/*  956 */       if (entry.consumeSendInitial() || i != 0) {
/*  957 */         returnData = entry.getServerState();
/*      */       }
/*  959 */       return returnData;
/*      */     } 
/*      */     
/*  962 */     int serverDataHashCode = entry.getServerDataHashCode();
/*  963 */     InteractionContext context = chain.getContext();
/*      */     
/*  965 */     float time = entry.getTimeInSeconds(tickTime);
/*  966 */     boolean firstRun = false;
/*  967 */     if (entry.getTimestamp() == 0L) {
/*  968 */       time = chain.getTimeShift();
/*  969 */       entry.setTimestamp(tickTime, time);
/*  970 */       firstRun = true;
/*      */     } 
/*      */     
/*  973 */     time *= tickTimeDilation;
/*      */     
/*      */     try {
/*  976 */       context.initEntry(chain, entry, this.entity);
/*  977 */       operation.tick(ref, this.entity, firstRun, time, chain.getType(), context, this.cooldownHandler);
/*      */     } finally {
/*  979 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/*      */     
/*  982 */     InteractionSyncData serverData = entry.getServerState();
/*      */     
/*  984 */     if (firstRun || serverDataHashCode != entry.getServerDataHashCode()) returnData = serverData;
/*      */     
/*      */     try {
/*  987 */       context.initEntry(chain, entry, this.entity);
/*  988 */       operation.handle(ref, firstRun, time, chain.getType(), context);
/*      */     } finally {
/*  990 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/*  992 */     removeInteractionIfFinished(ref, chain, entry);
/*      */     
/*  994 */     return returnData;
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
/*      */   private void removeInteractionIfFinished(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain, @Nonnull InteractionEntry entry) {
/* 1010 */     if (chain.getOperationIndex() == entry.getIndex() && (entry.getServerState()).state != InteractionState.NotFinished) {
/* 1011 */       chain.setFinalState((entry.getServerState()).state);
/*      */     }
/*      */ 
/*      */     
/* 1015 */     if ((entry.getServerState()).state != InteractionState.NotFinished) {
/*      */       
/* 1017 */       LOGGER.at(Level.FINE).log("Server finished interaction: %d, %s", entry.getIndex(), entry);
/* 1018 */       if (!chain.requiresClient() || (entry.getClientState() != null && (entry.getClientState()).state != InteractionState.NotFinished)) {
/* 1019 */         LOGGER.at(Level.FINER).log("Remove Interaction: %d, %s", entry.getIndex(), entry);
/* 1020 */         chain.removeInteractionEntry(this, entry.getIndex());
/*      */       }
/*      */     
/* 1023 */     } else if (entry.getClientState() != null && (entry.getClientState()).state != InteractionState.NotFinished && !waitingForClient(ref)) {
/*      */       
/* 1025 */       if (entry.getWaitingForServerFinished() == 0L) entry.setWaitingForServerFinished(this.currentTime); 
/* 1026 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - entry.getWaitingForServerFinished());
/* 1027 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/* 1028 */       if (context.isEnabled()) {
/* 1029 */         context.log("Client finished interaction but server hasn't! %s, %d, %s, %s", (entry.getClientState()).state, Integer.valueOf(entry.getIndex()), entry, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/* 1032 */       long threshold = getOperationTimeoutThreshold();
/* 1033 */       if (waitMillis > threshold) {
/*      */ 
/*      */ 
/*      */         
/* 1037 */         HytaleLogger.Api ctx = LOGGER.at(Level.SEVERE);
/* 1038 */         if (ctx.isEnabled()) ctx.log("Client finished interaction earlier than server! %d, %s", entry.getIndex(), entry);
/*      */       
/*      */       } 
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
/*      */   private void simulationTick(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain, long tickTime) {
/* 1053 */     assert this.commandBuffer != null;
/*      */     
/* 1055 */     RootInteraction rootInteraction = chain.getRootInteraction();
/* 1056 */     Operation operation = rootInteraction.getOperation(chain.getSimulatedOperationCounter());
/* 1057 */     if (operation == null) {
/* 1058 */       throw new IllegalStateException("Failed to find operation during simulation tick of chain '" + rootInteraction.getId() + "'");
/*      */     }
/*      */     
/* 1061 */     InteractionEntry entry = chain.getOrCreateInteractionEntry(chain.getClientOperationIndex());
/* 1062 */     InteractionContext context = chain.getContext();
/*      */     
/* 1064 */     entry.setUseSimulationState(true);
/*      */     try {
/* 1066 */       context.initEntry(chain, entry, this.entity);
/*      */       
/* 1068 */       float time = entry.getTimeInSeconds(tickTime);
/* 1069 */       boolean firstRun = false;
/* 1070 */       if (entry.getTimestamp() == 0L) {
/* 1071 */         time = chain.getTimeShift();
/* 1072 */         entry.setTimestamp(tickTime, time);
/* 1073 */         firstRun = true;
/*      */       } 
/*      */       
/* 1076 */       TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/* 1077 */       float tickTimeDilation = timeResource.getTimeDilationModifier();
/* 1078 */       time *= tickTimeDilation;
/*      */       
/* 1080 */       operation.simulateTick(ref, this.entity, firstRun, time, chain.getType(), context, this.cooldownHandler);
/*      */     } finally {
/* 1082 */       context.deinitEntry(chain, entry, this.entity);
/* 1083 */       entry.setUseSimulationState(false);
/*      */     } 
/*      */     
/* 1086 */     if (!entry.setClientState(entry.getSimulationState())) {
/* 1087 */       throw new RuntimeException("Simulation failed");
/*      */     }
/*      */     
/* 1090 */     removeInteractionIfFinished(ref, chain, entry);
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
/*      */   private boolean syncStart(@Nonnull Ref<EntityStore> ref, @Nonnull SyncInteractionChain packet) {
/*      */     InteractionContext context;
/* 1104 */     assert this.commandBuffer != null;
/* 1105 */     int index = packet.chainId;
/*      */     
/* 1107 */     if (!packet.initial) {
/* 1108 */       if (packet.forkedId == null) {
/* 1109 */         HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1110 */         if (ctx.isEnabled()) ctx.log("Got syncStart for %d-%s but packet wasn't the first.", index, packet.forkedId); 
/*      */       } 
/* 1112 */       return true;
/*      */     } 
/*      */     
/* 1115 */     if (packet.forkedId != null) {
/* 1116 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1117 */       if (ctx.isEnabled()) ctx.log("Can't start a forked chain from the client: %d %s", index, packet.forkedId); 
/* 1118 */       return true;
/*      */     } 
/*      */     
/* 1121 */     InteractionType type = packet.interactionType;
/*      */     
/* 1123 */     if (index <= 0) {
/* 1124 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1125 */       if (ctx.isEnabled()) ctx.log("Invalid client chainId! Got %d but client id's should be > 0", index); 
/* 1126 */       sendCancelPacket(index, packet.forkedId);
/* 1127 */       return true;
/*      */     } 
/* 1129 */     if (index <= this.lastClientChainId) {
/* 1130 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1131 */       if (ctx.isEnabled()) ctx.log("Invalid client chainId! The last clientChainId was %d but just got %d", this.lastClientChainId, index); 
/* 1132 */       sendCancelPacket(index, packet.forkedId);
/* 1133 */       return true;
/*      */     } 
/*      */     
/* 1136 */     UUID proxyId = packet.data.proxyId;
/*      */ 
/*      */     
/* 1139 */     if (!UUIDUtil.isEmptyOrNull(proxyId)) {
/* 1140 */       World world1 = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/* 1141 */       Ref<EntityStore> proxyTarget = world1.getEntityStore().getRefFromUUID(proxyId);
/*      */       
/* 1143 */       if (proxyTarget == null) {
/* 1144 */         if (this.packetQueueTime != 0L && this.currentTime - this.packetQueueTime > TimeUnit.MILLISECONDS.toNanos(getOperationTimeoutThreshold()) / 2L) {
/* 1145 */           HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1146 */           if (ctx.isEnabled()) ctx.log("Proxy entity never spawned"); 
/* 1147 */           sendCancelPacket(index, packet.forkedId);
/* 1148 */           return true;
/*      */         } 
/* 1150 */         return false;
/*      */       } 
/* 1152 */       context = InteractionContext.forProxyEntity(this, this.entity, proxyTarget);
/*      */     } else {
/*      */       
/* 1155 */       context = InteractionContext.forInteraction(this, ref, type, packet.equipSlot, (ComponentAccessor<EntityStore>)this.commandBuffer);
/*      */     } 
/*      */     
/* 1158 */     String rootInteractionId = context.getRootInteractionId(type);
/* 1159 */     if (rootInteractionId == null) {
/* 1160 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1161 */       if (ctx.isEnabled()) ctx.log("Missing root interaction: %d, %s, %s", Integer.valueOf(index), this.entity.getInventory().getItemInHand(), type); 
/* 1162 */       sendCancelPacket(index, packet.forkedId);
/* 1163 */       return true;
/*      */     } 
/*      */     
/* 1166 */     RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(rootInteractionId);
/* 1167 */     if (rootInteraction == null) return false;
/*      */     
/* 1169 */     if (!applyRules(context, packet.data, type, rootInteraction)) {
/* 1170 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1175 */     Inventory entityInventory = this.entity.getInventory();
/* 1176 */     ItemStack itemInHand = entityInventory.getActiveHotbarItem();
/* 1177 */     ItemStack utilityItem = entityInventory.getUtilityItem();
/*      */     
/* 1179 */     String serverItemInHandId = (itemInHand != null) ? itemInHand.getItemId() : null;
/* 1180 */     String serverUtilityItemId = (utilityItem != null) ? utilityItem.getItemId() : null;
/*      */     
/* 1182 */     if (packet.activeHotbarSlot != entityInventory.getActiveHotbarSlot()) {
/* 1183 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1184 */       if (ctx.isEnabled()) {
/* 1185 */         ctx.log("Active slot miss match: %d, %d != %d, %s, %s, %s", Integer.valueOf(index), Byte.valueOf(entityInventory.getActiveHotbarSlot()), Integer.valueOf(packet.activeHotbarSlot), serverItemInHandId, packet.itemInHandId, type);
/*      */       }
/* 1187 */       sendCancelPacket(index, packet.forkedId);
/*      */ 
/*      */       
/* 1190 */       if (this.playerRef != null) {
/* 1191 */         this.playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, entityInventory.getActiveHotbarSlot()));
/*      */       }
/* 1193 */       return true;
/*      */     } 
/*      */     
/* 1196 */     if (packet.activeUtilitySlot != entityInventory.getActiveUtilitySlot()) {
/* 1197 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1198 */       if (ctx.isEnabled()) {
/* 1199 */         ctx.log("Active slot miss match: %d, %d != %d, %s, %s, %s", Integer.valueOf(index), Byte.valueOf(entityInventory.getActiveUtilitySlot()), Integer.valueOf(packet.activeUtilitySlot), serverItemInHandId, packet.itemInHandId, type);
/*      */       }
/* 1201 */       sendCancelPacket(index, packet.forkedId);
/*      */ 
/*      */       
/* 1204 */       if (this.playerRef != null) {
/* 1205 */         this.playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-5, entityInventory.getActiveUtilitySlot()));
/*      */       }
/* 1207 */       return true;
/*      */     } 
/*      */     
/* 1210 */     if (!Objects.equals(serverItemInHandId, packet.itemInHandId)) {
/* 1211 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1212 */       if (ctx.isEnabled()) ctx.log("ItemInHand miss match: %d, %s, %s, %s", Integer.valueOf(index), serverItemInHandId, packet.itemInHandId, type); 
/* 1213 */       sendCancelPacket(index, packet.forkedId);
/* 1214 */       return true;
/*      */     } 
/*      */     
/* 1217 */     if (!Objects.equals(serverUtilityItemId, packet.utilityItemId)) {
/* 1218 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1219 */       if (ctx.isEnabled()) ctx.log("UtilityItem miss match: %d, %s, %s, %s", Integer.valueOf(index), serverUtilityItemId, packet.utilityItemId, type); 
/* 1220 */       sendCancelPacket(index, packet.forkedId);
/* 1221 */       return true;
/*      */     } 
/*      */     
/* 1224 */     if (isOnCooldown(ref, type, rootInteraction, true)) {
/* 1225 */       return false;
/*      */     }
/*      */     
/* 1228 */     InteractionChain chain = initChain(packet.data, type, context, rootInteraction, (Runnable)null, true);
/* 1229 */     chain.setChainId(index);
/* 1230 */     sync(ref, chain, packet);
/*      */     
/* 1232 */     World world = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/*      */ 
/*      */ 
/*      */     
/* 1236 */     if (packet.data.blockPosition != null) {
/*      */       
/* 1238 */       BlockPosition targetBlock = world.getBaseBlock(packet.data.blockPosition);
/* 1239 */       context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, targetBlock);
/* 1240 */       context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, packet.data.blockPosition);
/*      */     } 
/* 1242 */     if (packet.data.entityId >= 0) {
/*      */       
/* 1244 */       EntityStore entityComponentStore = world.getEntityStore();
/* 1245 */       Ref<EntityStore> entityReference = entityComponentStore.getRefFromNetworkId(packet.data.entityId);
/* 1246 */       if (entityReference != null) {
/* 1247 */         context.getMetaStore().putMetaObject(Interaction.TARGET_ENTITY, entityReference);
/*      */       }
/*      */     } 
/*      */     
/* 1251 */     if (packet.data.targetSlot != Integer.MIN_VALUE) {
/* 1252 */       context.getMetaStore().putMetaObject(Interaction.TARGET_SLOT, Integer.valueOf(packet.data.targetSlot));
/*      */     }
/*      */     
/* 1255 */     if (packet.data.hitLocation != null) {
/* 1256 */       Vector3f hit = packet.data.hitLocation;
/* 1257 */       context.getMetaStore().putMetaObject(Interaction.HIT_LOCATION, new Vector4d(hit.x, hit.y, hit.z, 1.0D));
/*      */     } 
/*      */     
/* 1260 */     if (packet.data.hitDetail != null) {
/* 1261 */       context.getMetaStore().putMetaObject(Interaction.HIT_DETAIL, packet.data.hitDetail);
/*      */     }
/*      */     
/* 1264 */     this.lastClientChainId = index;
/*      */ 
/*      */     
/* 1267 */     if (!tickChain(chain)) {
/* 1268 */       chain.setPreTicked(true);
/* 1269 */       this.chains.put(index, chain);
/*      */     } 
/*      */     
/* 1272 */     return true;
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
/*      */   public void sync(@Nonnull Ref<EntityStore> ref, @Nonnull ChainSyncStorage chainSyncStorage, @Nonnull SyncInteractionChain packet) {
/* 1284 */     assert this.commandBuffer != null;
/*      */     
/* 1286 */     if (packet.newForks != null) {
/* 1287 */       for (SyncInteractionChain fork : packet.newForks) {
/* 1288 */         chainSyncStorage.syncFork(ref, this, fork);
/*      */       }
/*      */     }
/*      */     
/* 1292 */     if (packet.interactionData == null) {
/* 1293 */       chainSyncStorage.setClientState(packet.state);
/*      */       
/*      */       return;
/*      */     } 
/* 1297 */     for (int i = 0; i < packet.interactionData.length; i++) {
/* 1298 */       InteractionSyncData syncData = packet.interactionData[i];
/* 1299 */       if (syncData != null) {
/*      */ 
/*      */         
/* 1302 */         int index = packet.operationBaseIndex + i;
/* 1303 */         if (!chainSyncStorage.isSyncDataOutOfOrder(index)) {
/*      */ 
/*      */ 
/*      */           
/* 1307 */           InteractionEntry interaction = chainSyncStorage.getInteraction(index);
/* 1308 */           if (interaction != null && chainSyncStorage instanceof InteractionChain) { InteractionChain interactionChain = (InteractionChain)chainSyncStorage;
/*      */             
/* 1310 */             if ((interaction.getClientState() != null && (interaction.getClientState()).state != InteractionState.NotFinished && syncData.state == InteractionState.NotFinished) || 
/* 1311 */               !interaction.setClientState(syncData)) {
/* 1312 */               chainSyncStorage.clearInteractionSyncData(index);
/*      */ 
/*      */               
/* 1315 */               interaction.flagDesync();
/* 1316 */               interactionChain.flagDesync();
/*      */               
/*      */               return;
/*      */             } 
/* 1320 */             chainSyncStorage.updateSyncPosition(index);
/*      */             
/* 1322 */             HytaleLogger.Api context = LOGGER.at(Level.FINEST);
/* 1323 */             if (context.isEnabled()) {
/* 1324 */               TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/* 1325 */               float tickTimeDilation = timeResource.getTimeDilationModifier();
/*      */               
/* 1327 */               context.log("%d, %d: Time (Sync) - Server: %s vs Client: %s", 
/* 1328 */                   Integer.valueOf(packet.chainId), Integer.valueOf(index), 
/* 1329 */                   Float.valueOf(interaction.getTimeInSeconds(this.currentTime) * tickTimeDilation), Float.valueOf((interaction.getClientState()).progress));
/*      */             } 
/*      */             
/* 1332 */             removeInteractionIfFinished(ref, interactionChain, interaction); }
/*      */           else
/* 1334 */           { chainSyncStorage.putInteractionSyncData(index, syncData); }
/*      */         
/*      */         } 
/*      */       } 
/* 1338 */     }  int last = packet.operationBaseIndex + packet.interactionData.length;
/* 1339 */     chainSyncStorage.clearInteractionSyncData(last);
/*      */     
/* 1341 */     chainSyncStorage.setClientState(packet.state);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean canRun(@Nonnull InteractionType type, @Nonnull RootInteraction rootInteraction) {
/* 1352 */     return canRun(type, (short)-1, rootInteraction);
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
/*      */   public boolean canRun(@Nonnull InteractionType type, short equipSlot, @Nonnull RootInteraction rootInteraction) {
/* 1364 */     return applyRules(null, type, equipSlot, rootInteraction, (Map)this.chains, null);
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
/*      */   public boolean applyRules(@Nonnull InteractionContext context, @Nonnull InteractionChainData data, @Nonnull InteractionType type, @Nonnull RootInteraction rootInteraction) {
/* 1385 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/*      */     
/* 1387 */     if (!applyRules(data, type, context.getHeldItemSlot(), rootInteraction, (Map)this.chains, (List<InteractionChain>)objectArrayList)) return false;
/*      */ 
/*      */     
/* 1390 */     for (InteractionChain interactionChain : objectArrayList) {
/* 1391 */       cancelChains(interactionChain);
/*      */     }
/* 1393 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cancelChains(@Nonnull InteractionChain chain) {
/* 1402 */     chain.setServerState(InteractionState.Failed);
/* 1403 */     chain.setClientState(InteractionState.Failed);
/* 1404 */     sendCancelPacket(chain);
/*      */     
/* 1406 */     for (ObjectIterator<InteractionChain> objectIterator = chain.getForkedChains().values().iterator(); objectIterator.hasNext(); ) { InteractionChain fork = objectIterator.next();
/* 1407 */       cancelChains(fork); }
/*      */   
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
/*      */   private static boolean applyRules(@Nullable InteractionChainData data, @Nonnull InteractionType type, int heldItemSlot, @Nullable RootInteraction rootInteraction, @Nonnull Map<?, InteractionChain> chains, @Nullable List<InteractionChain> chainsToCancel) {
/* 1430 */     if (chains.isEmpty() || rootInteraction == null) return true;
/*      */     
/* 1432 */     for (InteractionChain chain : chains.values()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1439 */       if (chain.getForkedChainId() != null && !chain.isPredicted()) {
/*      */         continue;
/*      */       }
/*      */       
/* 1443 */       if (data != null && !Objects.equals((chain.getChainData()).proxyId, data.proxyId)) {
/*      */         continue;
/*      */       }
/* 1446 */       if (type == InteractionType.Equipped && chain.getType() == InteractionType.Equipped && chain.getContext().getHeldItemSlot() != heldItemSlot) {
/*      */         continue;
/*      */       }
/*      */       
/* 1450 */       if (chain.getServerState() == InteractionState.NotFinished) {
/*      */         
/* 1452 */         RootInteraction currentRoot = chain.getRootInteraction();
/* 1453 */         Operation currentOp = currentRoot.getOperation(chain.getOperationCounter());
/*      */ 
/*      */         
/* 1456 */         if (rootInteraction.getRules().validateInterrupts(type, rootInteraction.getData().getTags(), chain.getType(), currentRoot.getData().getTags(), currentRoot.getRules())) {
/* 1457 */           if (chainsToCancel != null) chainsToCancel.add(chain); 
/* 1458 */         } else if (currentOp != null && currentOp.getRules() != null && rootInteraction.getRules().validateInterrupts(type, rootInteraction.getData().getTags(), chain.getType(), currentOp.getTags(), currentOp.getRules())) {
/*      */           
/* 1460 */           if (chainsToCancel != null) chainsToCancel.add(chain); 
/*      */         } else {
/* 1462 */           if (rootInteraction.getRules().validateBlocked(type, rootInteraction.getData().getTags(), chain.getType(), currentRoot.getData().getTags(), currentRoot.getRules())) {
/* 1463 */             return false;
/*      */           }
/*      */ 
/*      */           
/* 1467 */           if (currentOp != null && currentOp.getRules() != null && rootInteraction.getRules().validateBlocked(type, rootInteraction.getData().getTags(), chain.getType(), currentOp.getTags(), currentOp.getRules())) {
/* 1468 */             return false;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1473 */       if ((chainsToCancel == null || chainsToCancel.isEmpty()) && !applyRules(data, type, heldItemSlot, rootInteraction, (Map)chain.getForkedChains(), chainsToCancel)) {
/* 1474 */         return false;
/*      */       }
/*      */     } 
/* 1477 */     return true;
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
/*      */   public boolean tryStartChain(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull RootInteraction rootInteraction) {
/* 1496 */     InteractionChain chain = initChain(type, context, rootInteraction, false);
/* 1497 */     if (!applyRules(context, chain.getChainData(), type, rootInteraction)) {
/* 1498 */       return false;
/*      */     }
/* 1500 */     executeChain(ref, commandBuffer, chain);
/* 1501 */     return true;
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
/*      */   public void startChain(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull RootInteraction rootInteraction) {
/* 1519 */     InteractionChain chain = initChain(type, context, rootInteraction, false);
/* 1520 */     executeChain(ref, commandBuffer, chain);
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
/*      */   @Nonnull
/*      */   public InteractionChain initChain(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull RootInteraction rootInteraction, boolean forceRemoteSync) {
/* 1539 */     return initChain(type, context, rootInteraction, -1, (BlockPosition)null, forceRemoteSync);
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
/*      */   @Nonnull
/*      */   public InteractionChain initChain(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull RootInteraction rootInteraction, int entityId, @Nullable BlockPosition blockPosition, boolean forceRemoteSync) {
/* 1562 */     InteractionChainData data = new InteractionChainData(entityId, UUIDUtil.EMPTY_UUID, null, null, blockPosition, -2147483648, null);
/* 1563 */     return initChain(data, type, context, rootInteraction, (Runnable)null, forceRemoteSync);
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
/*      */   @Nonnull
/*      */   public InteractionChain initChain(@Nonnull InteractionChainData data, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull RootInteraction rootInteraction, @Nullable Runnable onCompletion, boolean forceRemoteSync) {
/* 1593 */     return new InteractionChain(type, context, data, rootInteraction, onCompletion, (forceRemoteSync || !this.hasRemoteClient));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void queueExecuteChain(@Nonnull InteractionChain chain) {
/* 1602 */     this.chainStartQueue.add(chain);
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
/*      */   public void executeChain(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionChain chain) {
/* 1617 */     this.commandBuffer = commandBuffer;
/* 1618 */     executeChain0(ref, chain);
/* 1619 */     this.commandBuffer = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void executeChain0(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain) {
/* 1629 */     if (isOnCooldown(ref, chain.getType(), chain.getInitialRootInteraction(), false)) {
/* 1630 */       chain.setServerState(InteractionState.Failed);
/* 1631 */       chain.setClientState(InteractionState.Failed);
/*      */       
/*      */       return;
/*      */     } 
/* 1635 */     int index = --this.lastServerChainId;
/* 1636 */     if (index >= 0) index = this.lastServerChainId = -1; 
/* 1637 */     chain.setChainId(index);
/*      */     
/* 1639 */     if (tickChain(chain)) {
/*      */       return;
/*      */     }
/* 1642 */     LOGGER.at(Level.FINE).log("Add Chain: %d, %s", index, chain);
/* 1643 */     chain.setPreTicked(true);
/* 1644 */     this.chains.put(index, chain);
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
/*      */   private boolean isOnCooldown(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionType type, @Nonnull RootInteraction root, boolean remote) {
/* 1659 */     assert this.commandBuffer != null;
/*      */     
/* 1661 */     InteractionCooldown cooldown = root.getCooldown();
/* 1662 */     String cooldownId = root.getId();
/* 1663 */     float cooldownTime = InteractionTypeUtils.getDefaultCooldown(type);
/* 1664 */     float[] cooldownChargeTimes = DEFAULT_CHARGE_TIMES;
/* 1665 */     boolean interruptRecharge = false;
/*      */     
/* 1667 */     if (cooldown != null) {
/* 1668 */       cooldownTime = cooldown.cooldown;
/* 1669 */       if (cooldown.chargeTimes != null && cooldown.chargeTimes.length > 0) {
/* 1670 */         cooldownChargeTimes = cooldown.chargeTimes;
/*      */       }
/* 1672 */       if (cooldown.cooldownId != null) cooldownId = cooldown.cooldownId; 
/* 1673 */       if (cooldown.interruptRecharge) interruptRecharge = true;
/*      */ 
/*      */       
/* 1676 */       if (cooldown.clickBypass && remote) {
/* 1677 */         this.cooldownHandler.resetCooldown(cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
/* 1678 */         return false;
/*      */       } 
/*      */     } 
/*      */     
/* 1682 */     Player playerComponent = (Player)this.commandBuffer.getComponent(ref, Player.getComponentType());
/* 1683 */     GameMode gameMode = (playerComponent != null) ? playerComponent.getGameMode() : GameMode.Adventure;
/* 1684 */     RootInteractionSettings settings = (RootInteractionSettings)root.getSettings().get(gameMode);
/* 1685 */     if (settings != null && settings.allowSkipChainOnClick && remote) {
/* 1686 */       this.cooldownHandler.resetCooldown(cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
/* 1687 */       return false;
/*      */     } 
/*      */     
/* 1690 */     return this.cooldownHandler.isOnCooldown(root, cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
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
/*      */   public void tryRunHeldInteraction(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type) {
/* 1703 */     tryRunHeldInteraction(ref, commandBuffer, type, (short)-1);
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
/*      */   public void tryRunHeldInteraction(@Nonnull Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, short equipSlot) {
/*      */     ItemStack itemStack;
/* 1718 */     Inventory inventory = this.entity.getInventory();
/*      */ 
/*      */     
/* 1721 */     switch (type) { case Held:
/* 1722 */         itemStack = inventory.getItemInHand(); break;
/* 1723 */       case HeldOffhand: itemStack = inventory.getUtilityItem(); break;
/*      */       case Equipped:
/* 1725 */         if (equipSlot == -1) throw new IllegalArgumentException(); 
/* 1726 */         itemStack = inventory.getArmor().getItemStack(equipSlot); break;
/*      */       default:
/* 1728 */         throw new IllegalArgumentException(); }
/*      */ 
/*      */     
/* 1731 */     if (itemStack == null || itemStack.isEmpty())
/*      */       return; 
/* 1733 */     String rootId = (String)itemStack.getItem().getInteractions().get(type);
/* 1734 */     if (rootId == null)
/*      */       return; 
/* 1736 */     RootInteraction root = (RootInteraction)RootInteraction.getAssetMap().getAsset(rootId);
/* 1737 */     if (root == null || !canRun(type, equipSlot, root))
/*      */       return; 
/* 1739 */     InteractionContext context = InteractionContext.forInteraction(this, ref, type, equipSlot, (ComponentAccessor<EntityStore>)commandBuffer);
/* 1740 */     startChain(ref, commandBuffer, type, context, root);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendSyncPacket(@Nonnull InteractionChain chain, int operationBaseIndex, @Nullable List<InteractionSyncData> interactionData) {
/* 1751 */     if (chain.hasSentInitial() && (interactionData == null || 
/* 1752 */       ListUtil.emptyOrAllNull(interactionData)) && chain
/* 1753 */       .getNewForks().isEmpty()) {
/*      */       return;
/*      */     }
/*      */     
/* 1757 */     if (this.playerRef != null) {
/* 1758 */       SyncInteractionChain packet = makeSyncPacket(chain, operationBaseIndex, interactionData);
/* 1759 */       this.syncPackets.add(packet);
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
/*      */   @Nonnull
/*      */   private static SyncInteractionChain makeSyncPacket(@Nonnull InteractionChain chain, int operationBaseIndex, @Nullable List<InteractionSyncData> interactionData) {
/* 1773 */     SyncInteractionChain[] forks = null;
/* 1774 */     List<InteractionChain> newForks = chain.getNewForks();
/* 1775 */     if (!newForks.isEmpty()) {
/* 1776 */       forks = new SyncInteractionChain[newForks.size()];
/* 1777 */       for (int i = 0; i < newForks.size(); i++) {
/* 1778 */         InteractionChain fc = newForks.get(i);
/* 1779 */         forks[i] = makeSyncPacket(fc, 0, null);
/*      */       } 
/* 1781 */       newForks.clear();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1800 */     SyncInteractionChain packet = new SyncInteractionChain(0, 0, 0, null, null, null, !chain.hasSentInitial(), false, chain.hasSentInitial() ? Integer.MIN_VALUE : RootInteraction.getRootInteractionIdOrUnknown(chain.getInitialRootInteraction().getId()), chain.getType(), chain.getContext().getHeldItemSlot(), chain.getChainId(), chain.getForkedChainId(), chain.getChainData(), chain.getServerState(), forks, operationBaseIndex, (interactionData == null) ? null : (InteractionSyncData[])interactionData.toArray(x$0 -> new InteractionSyncData[x$0]));
/*      */     
/* 1802 */     chain.setSentInitial(true);
/* 1803 */     return packet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendCancelPacket(@Nonnull InteractionChain chain) {
/* 1812 */     sendCancelPacket(chain.getChainId(), chain.getForkedChainId());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendCancelPacket(int chainId, @Nonnull ForkedChainId forkedChainId) {
/* 1822 */     if (this.playerRef != null) {
/* 1823 */       this.playerRef.getPacketHandler().writeNoCache((Packet)new CancelInteractionChain(chainId, forkedChainId));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/* 1832 */     forEachInteraction((chain, _i, _a) -> { chain.setServerState(InteractionState.Failed); chain.setClientState(InteractionState.Failed); sendCancelPacket(chain); return null; }null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1839 */     this.chainStartQueue.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearAllGlobalTimeShift(float dt) {
/* 1848 */     if (this.timeShiftsDirty) {
/*      */       
/* 1850 */       boolean clearFlag = true;
/* 1851 */       for (int i = 0; i < this.globalTimeShift.length; i++) {
/* 1852 */         if (!this.globalTimeShiftDirty[i]) {
/* 1853 */           this.globalTimeShift[i] = 0.0F;
/*      */         } else {
/* 1855 */           clearFlag = false;
/* 1856 */           this.globalTimeShift[i] = this.globalTimeShift[i] + dt;
/*      */         } 
/*      */       } 
/* 1859 */       Arrays.fill(this.globalTimeShiftDirty, false);
/* 1860 */       if (clearFlag) this.timeShiftsDirty = false;
/*      */     
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setGlobalTimeShift(@Nonnull InteractionType type, float shift) {
/* 1911 */     if (shift < 0.0F) throw new IllegalArgumentException("Can't shift backwards"); 
/* 1912 */     this.globalTimeShift[type.ordinal()] = shift;
/* 1913 */     this.globalTimeShiftDirty[type.ordinal()] = true;
/* 1914 */     this.timeShiftsDirty = true;
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
/*      */   public float getGlobalTimeShift(@Nonnull InteractionType type) {
/* 1928 */     return this.globalTimeShift[type.ordinal()];
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
/*      */   public <T> T forEachInteraction(@Nonnull TriFunction<InteractionChain, Interaction, T, T> func, @Nonnull T val) {
/* 1945 */     return forEachInteraction((Map)this.chains, func, val);
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
/*      */   private static <T> T forEachInteraction(@Nonnull Map<?, InteractionChain> chains, @Nonnull TriFunction<InteractionChain, Interaction, T, T> func, @Nonnull T val) {
/* 1958 */     if (chains.isEmpty()) return val;
/*      */     
/* 1960 */     for (InteractionChain chain : chains.values()) {
/* 1961 */       Operation operation = chain.getRootInteraction().getOperation(chain.getOperationCounter());
/* 1962 */       if (operation != null) {
/* 1963 */         operation = operation.getInnerOperation();
/* 1964 */         if (operation instanceof Interaction) { Interaction interaction = (Interaction)operation;
/* 1965 */           val = (T)func.apply(chain, interaction, val); }
/*      */       
/*      */       } 
/* 1968 */       val = forEachInteraction((Map)chain.getForkedChains(), func, val);
/*      */     } 
/* 1970 */     return val;
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
/*      */   public void walkChain(@Nonnull Ref<EntityStore> ref, @Nonnull Collector collector, @Nonnull InteractionType type, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 1986 */     walkChain(ref, collector, type, null, componentAccessor);
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
/*      */   public void walkChain(@Nonnull Ref<EntityStore> ref, @Nonnull Collector collector, @Nonnull InteractionType type, @Nullable RootInteraction rootInteraction, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 2003 */     walkChain(collector, type, InteractionContext.forInteraction(this, ref, type, componentAccessor), rootInteraction);
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
/*      */   public static void walkChain(@Nonnull Collector collector, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable RootInteraction rootInteraction) {
/* 2020 */     if (rootInteraction == null) {
/* 2021 */       String rootInteractionId = context.getRootInteractionId(type);
/* 2022 */       if (rootInteractionId == null) {
/* 2023 */         throw new IllegalArgumentException("No interaction ID found for " + String.valueOf(type) + ", " + String.valueOf(context));
/*      */       }
/*      */       
/* 2026 */       rootInteraction = (RootInteraction)RootInteraction.getAssetMap().getAsset(rootInteractionId);
/*      */     } 
/*      */ 
/*      */     
/* 2030 */     if (rootInteraction == null) {
/* 2031 */       throw new IllegalArgumentException("No interactions are defined for " + String.valueOf(type) + ", " + String.valueOf(context));
/*      */     }
/*      */     
/* 2034 */     collector.start();
/* 2035 */     collector.into(context, null);
/* 2036 */     walkInteractions(collector, context, CollectorTag.ROOT, rootInteraction.getInteractionIds());
/* 2037 */     collector.outof();
/* 2038 */     collector.finished();
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
/*      */   public static boolean walkInteractions(@Nonnull Collector collector, @Nonnull InteractionContext context, @Nonnull CollectorTag tag, @Nonnull String[] interactionIds) {
/* 2054 */     for (String id : interactionIds) {
/* 2055 */       if (walkInteraction(collector, context, tag, id)) return true; 
/*      */     } 
/* 2057 */     return false;
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
/*      */   public static boolean walkInteraction(@Nonnull Collector collector, @Nonnull InteractionContext context, @Nonnull CollectorTag tag, @Nullable String id) {
/* 2073 */     if (id == null) return false; 
/* 2074 */     Interaction interaction = (Interaction)Interaction.getAssetMap().getAsset(id);
/* 2075 */     if (interaction == null) throw new IllegalArgumentException("Failed to find interaction: " + id);
/*      */     
/* 2077 */     if (collector.collect(tag, context, interaction)) return true;
/*      */     
/* 2079 */     collector.into(context, interaction);
/* 2080 */     interaction.walk(collector, context);
/* 2081 */     collector.outof();
/* 2082 */     return false;
/*      */   }
/*      */   
/*      */   public ObjectList<SyncInteractionChain> getSyncPackets() {
/* 2086 */     return this.syncPackets;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Component<EntityStore> clone() {
/* 2092 */     InteractionManager manager = new InteractionManager(this.entity, this.playerRef, this.interactionSimulationHandler);
/* 2093 */     manager.copyFrom(this);
/* 2094 */     return manager;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ChainCancelledException
/*      */     extends RuntimeException
/*      */   {
/*      */     @Nonnull
/*      */     private final InteractionState state;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public ChainCancelledException(@Nonnull InteractionState state) {
/* 2115 */       this.state = state;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\InteractionManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */