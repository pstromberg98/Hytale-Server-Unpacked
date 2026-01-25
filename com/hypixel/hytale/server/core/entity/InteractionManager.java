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
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.math.vector.Vector4d;
/*      */ import com.hypixel.hytale.metrics.metric.HistoricMetric;
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
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
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
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*      */ import com.hypixel.hytale.server.core.util.UUIDUtil;
/*      */ import io.sentry.Sentry;
/*      */ import io.sentry.SentryEvent;
/*      */ import io.sentry.SentryLevel;
/*      */ import io.sentry.protocol.Message;
/*      */ import io.sentry.protocol.SentryId;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*      */ import java.util.Arrays;
/*      */ import java.util.Deque;
/*      */ import java.util.HashMap;
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
/*      */ public class InteractionManager
/*      */   implements Component<EntityStore>
/*      */ {
/*      */   public static final double MAX_REACH_DISTANCE = 8.0D;
/*  208 */   public static final float[] DEFAULT_CHARGE_TIMES = new float[] { 0.0F };
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  213 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  218 */   private final Int2ObjectMap<InteractionChain> chains = (Int2ObjectMap<InteractionChain>)new Int2ObjectOpenHashMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  225 */   private final Int2ObjectMap<InteractionChain> unmodifiableChains = Int2ObjectMaps.unmodifiable(this.chains);
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  230 */   private final CooldownHandler cooldownHandler = new CooldownHandler();
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
/*  256 */   private final ObjectList<InteractionSyncData> tempSyncDataList = (ObjectList<InteractionSyncData>)new ObjectArrayList();
/*      */   
/*      */   private int lastServerChainId;
/*      */   
/*      */   private int lastClientChainId;
/*      */   
/*      */   private long packetQueueTime;
/*      */   
/*  264 */   private final float[] globalTimeShift = new float[InteractionType.VALUES.length];
/*  265 */   private final boolean[] globalTimeShiftDirty = new boolean[InteractionType.VALUES.length];
/*      */   
/*      */   private boolean timeShiftsDirty;
/*      */   
/*  269 */   private final ObjectList<SyncInteractionChain> syncPackets = (ObjectList<SyncInteractionChain>)new ObjectArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  276 */   private long currentTime = 1L;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  281 */   private final ObjectList<InteractionChain> chainStartQueue = (ObjectList<InteractionChain>)new ObjectArrayList();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  289 */   private final Predicate<InteractionChain> cachedTickChain = this::tickChain;
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
/*  308 */     this.entity = entity;
/*  309 */     this.playerRef = playerRef;
/*  310 */     this.hasRemoteClient = (playerRef != null);
/*  311 */     this.interactionSimulationHandler = simulationHandler;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Int2ObjectMap<InteractionChain> getChains() {
/*  319 */     return this.unmodifiableChains;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public IInteractionSimulationHandler getInteractionSimulationHandler() {
/*  327 */     return this.interactionSimulationHandler;
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
/*  338 */     if (this.playerRef != null) {
/*  339 */       return this.playerRef.getPacketHandler().getOperationTimeoutThreshold();
/*      */     }
/*      */     
/*  342 */     assert this.commandBuffer != null;
/*  343 */     World world = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/*  344 */     return (world.getTickStepNanos() / 1000000 * 10);
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
/*  357 */     assert this.commandBuffer != null;
/*  358 */     Player playerComponent = (Player)this.commandBuffer.getComponent(ref, Player.getComponentType());
/*      */     
/*  360 */     if (playerComponent != null) {
/*  361 */       return playerComponent.isWaitingForClientReady();
/*      */     }
/*  363 */     return false;
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
/*  374 */     this.hasRemoteClient = hasRemoteClient;
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
/*  386 */     this.chains.putAll((Map)interactionManager.chains);
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
/*  398 */     this.currentTime += ((EntityStore)commandBuffer.getExternalData()).getWorld().getTickStepNanos();
/*      */     
/*  400 */     this.commandBuffer = commandBuffer;
/*  401 */     clearAllGlobalTimeShift(dt);
/*  402 */     this.cooldownHandler.tick(dt);
/*      */     
/*  404 */     for (ObjectListIterator<InteractionChain> objectListIterator = this.chainStartQueue.iterator(); objectListIterator.hasNext(); ) { InteractionChain interactionChain = objectListIterator.next();
/*  405 */       executeChain0(ref, interactionChain); }
/*      */     
/*  407 */     this.chainStartQueue.clear();
/*      */     
/*  409 */     Deque<SyncInteractionChain> packetQueue = null;
/*      */     
/*  411 */     if (this.playerRef != null) {
/*  412 */       packetQueue = ((GamePacketHandler)this.playerRef.getPacketHandler()).getInteractionPacketQueue();
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
/*  423 */     if (packetQueue != null && !packetQueue.isEmpty()) {
/*  424 */       boolean first = true;
/*  425 */       while (tryConsumePacketQueue(ref, packetQueue) || first) {
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  430 */         if (!this.chains.isEmpty()) {
/*  431 */           this.chains.values().removeIf(this.cachedTickChain);
/*      */         }
/*  433 */         float cooldownDt = 0.0F;
/*  434 */         for (float shift : this.globalTimeShift) {
/*  435 */           cooldownDt = Math.max(cooldownDt, shift);
/*      */         }
/*  437 */         if (cooldownDt > 0.0F) {
/*  438 */           this.cooldownHandler.tick(cooldownDt);
/*      */         }
/*  440 */         first = false;
/*      */       } 
/*  442 */       this.commandBuffer = null;
/*      */       
/*      */       return;
/*      */     } 
/*  446 */     if (!this.chains.isEmpty()) {
/*  447 */       this.chains.values().removeIf(this.cachedTickChain);
/*      */     }
/*      */     
/*  450 */     this.commandBuffer = null;
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
/*  461 */     Iterator<SyncInteractionChain> it = packetQueue.iterator();
/*      */     
/*  463 */     boolean finished = false;
/*  464 */     boolean desynced = false;
/*  465 */     int highestChainId = -1;
/*      */     
/*  467 */     boolean changed = false;
/*      */ 
/*      */     
/*  470 */     label67: while (it.hasNext()) {
/*  471 */       SyncInteractionChain packet = it.next();
/*      */       
/*  473 */       if (packet.desync) {
/*  474 */         HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  475 */         if (context.isEnabled()) context.log("Client packet flagged as desync"); 
/*  476 */         desynced = true;
/*      */       } 
/*      */       
/*  479 */       InteractionChain chain = (InteractionChain)this.chains.get(packet.chainId);
/*  480 */       if (chain != null && packet.forkedId != null) {
/*      */         
/*  482 */         ForkedChainId id = packet.forkedId;
/*  483 */         while (id != null) {
/*      */           
/*  485 */           InteractionChain subChain = chain.getForkedChain(id);
/*  486 */           if (subChain == null) {
/*      */             
/*  488 */             InteractionChain.TempChain tempChain = chain.getTempForkedChain(id);
/*  489 */             if (tempChain == null)
/*  490 */               continue label67;  tempChain.setBaseForkedChainId(id);
/*  491 */             ForkedChainId lastId = id;
/*  492 */             id = id.forkedId;
/*  493 */             while (id != null) {
/*  494 */               tempChain = tempChain.getOrCreateTempForkedChain(id);
/*  495 */               tempChain.setBaseForkedChainId(id);
/*  496 */               lastId = id;
/*  497 */               id = id.forkedId;
/*      */             } 
/*  499 */             tempChain.setForkedChainId(packet.forkedId);
/*  500 */             tempChain.setBaseForkedChainId(lastId);
/*  501 */             tempChain.setChainData(packet.data);
/*  502 */             sync(ref, tempChain, packet);
/*  503 */             changed = true;
/*  504 */             it.remove();
/*  505 */             this.packetQueueTime = 0L;
/*      */             continue label67;
/*      */           } 
/*  508 */           chain = subChain;
/*  509 */           id = id.forkedId;
/*      */         } 
/*      */       } 
/*      */       
/*  513 */       highestChainId = Math.max(highestChainId, packet.chainId);
/*      */       
/*  515 */       if (chain == null && !finished) {
/*      */         
/*  517 */         if (syncStart(ref, packet)) {
/*      */           
/*  519 */           changed = true;
/*  520 */           it.remove();
/*  521 */           this.packetQueueTime = 0L; continue;
/*      */         } 
/*  523 */         if (!waitingForClient(ref)) {
/*      */           long queuedTime;
/*      */           
/*  526 */           if (this.packetQueueTime == 0L) {
/*  527 */             this.packetQueueTime = this.currentTime;
/*  528 */             queuedTime = 0L;
/*      */           } else {
/*  530 */             queuedTime = this.currentTime - this.packetQueueTime;
/*      */           } 
/*      */ 
/*      */           
/*  534 */           HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  535 */           if (context.isEnabled()) {
/*  536 */             context.log("Queued chain %d for %s", packet.chainId, FormatUtil.nanosToString(queuedTime));
/*      */           }
/*      */           
/*  539 */           if (queuedTime > TimeUnit.MILLISECONDS.toNanos(getOperationTimeoutThreshold())) {
/*  540 */             sendCancelPacket(packet.chainId, packet.forkedId);
/*  541 */             it.remove();
/*  542 */             context = LOGGER.at(Level.FINE);
/*  543 */             if (context.isEnabled()) context.log("Discarding packet due to queuing for too long: %s", packet); 
/*      */           } 
/*      */         } 
/*  546 */         if (!desynced) finished = true;  continue;
/*  547 */       }  if (chain != null) {
/*      */         
/*  549 */         sync(ref, chain, packet);
/*  550 */         changed = true;
/*  551 */         it.remove();
/*  552 */         this.packetQueueTime = 0L; continue;
/*  553 */       }  if (desynced) {
/*      */ 
/*      */         
/*  556 */         sendCancelPacket(packet.chainId, packet.forkedId);
/*  557 */         it.remove();
/*  558 */         HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/*  559 */         ctx.log("Discarding packet due to desync: %s", packet);
/*      */       } 
/*      */     } 
/*      */     
/*  563 */     if (desynced && !packetQueue.isEmpty()) {
/*      */       
/*  565 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/*  566 */       if (ctx.isEnabled()) ctx.log("Discarding previous packets in queue: (before) %d", packetQueue.size()); 
/*  567 */       packetQueue.removeIf(v -> {
/*  568 */             boolean shouldRemove = (getChain(v.chainId, v.forkedId) == null && UUIDUtil.isEmptyOrNull(v.data.proxyId) && v.initial);
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
/*  579 */       ctx = LOGGER.at(Level.FINE);
/*  580 */       if (ctx.isEnabled()) ctx.log("Discarded previous packets in queue: (after) %d", packetQueue.size());
/*      */     
/*      */     } 
/*  583 */     return changed;
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
/*  595 */     InteractionChain chain = (InteractionChain)this.chains.get(chainId);
/*  596 */     if (chain != null && forkedChainId != null) {
/*  597 */       ForkedChainId id = forkedChainId;
/*  598 */       while (id != null) {
/*  599 */         InteractionChain subChain = chain.getForkedChain(id);
/*  600 */         if (subChain == null) {
/*  601 */           return null;
/*      */         }
/*  603 */         chain = subChain;
/*  604 */         id = id.forkedId;
/*      */       } 
/*      */     } 
/*  607 */     return chain;
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
/*      */   private boolean tickChain(@Nonnull InteractionChain chain) {
/*  619 */     if (chain.wasPreTicked()) {
/*  620 */       chain.setPreTicked(false);
/*  621 */       return false;
/*      */     } 
/*      */ 
/*      */     
/*  625 */     if (!this.hasRemoteClient) chain.updateSimulatedState();
/*      */     
/*  627 */     chain.getForkedChains().values().removeIf(this.cachedTickChain);
/*      */     
/*  629 */     Ref<EntityStore> ref = this.entity.getReference();
/*  630 */     assert ref != null;
/*      */ 
/*      */     
/*  633 */     if (chain.getServerState() != InteractionState.NotFinished) {
/*      */       
/*  635 */       if (!chain.requiresClient() || chain.getClientState() != InteractionState.NotFinished) {
/*      */         
/*  637 */         LOGGER.at(Level.FINE).log("Remove Chain: %d, %s", chain.getChainId(), chain);
/*  638 */         handleCancelledChain(ref, chain);
/*  639 */         chain.onCompletion(this.cooldownHandler, this.hasRemoteClient);
/*  640 */         return chain.getForkedChains().isEmpty();
/*  641 */       }  if (!waitingForClient(ref)) {
/*      */         
/*  643 */         if (chain.getWaitingForClientFinished() == 0L) {
/*  644 */           chain.setWaitingForClientFinished(this.currentTime);
/*      */         }
/*      */         
/*  647 */         long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - chain.getWaitingForClientFinished());
/*  648 */         HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  649 */         if (context.isEnabled()) {
/*  650 */           context.log("Server finished chain but client hasn't! %d, %s, %s", Integer.valueOf(chain.getChainId()), chain, Long.valueOf(waitMillis));
/*      */         }
/*  652 */         long threshold = getOperationTimeoutThreshold();
/*      */         
/*  654 */         TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  655 */         if (timeResource.getTimeDilationModifier() == 1.0F && waitMillis > threshold) {
/*      */           
/*  657 */           sendCancelPacket(chain);
/*  658 */           return chain.getForkedChains().isEmpty();
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
/*  669 */       return false;
/*      */     } 
/*      */     
/*  672 */     int baseOpIndex = chain.getOperationIndex();
/*      */     
/*      */     try {
/*  675 */       doTickChain(ref, chain);
/*  676 */     } catch (ChainCancelledException e) {
/*  677 */       chain.setServerState(e.state);
/*  678 */       chain.setClientState(e.state);
/*      */ 
/*      */ 
/*      */       
/*  682 */       chain.updateServerState();
/*  683 */       if (!this.hasRemoteClient) chain.updateSimulatedState(); 
/*  684 */       if (chain.requiresClient()) {
/*  685 */         sendSyncPacket(chain, baseOpIndex, (List<InteractionSyncData>)this.tempSyncDataList);
/*  686 */         sendCancelPacket(chain);
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  692 */     if (chain.getServerState() != InteractionState.NotFinished) {
/*      */       
/*  694 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  695 */       if (context.isEnabled()) {
/*  696 */         context.log("Server finished chain: %d-%s, %s in %fs", Integer.valueOf(chain.getChainId()), chain.getForkedChainId(), chain, Float.valueOf(chain.getTimeInSeconds()));
/*      */       }
/*      */       
/*  699 */       if (!chain.requiresClient() || chain.getClientState() != InteractionState.NotFinished) {
/*  700 */         context = LOGGER.at(Level.FINE);
/*  701 */         if (context.isEnabled()) context.log("Remove Chain: %d-%s, %s", Integer.valueOf(chain.getChainId()), chain.getForkedChainId(), chain); 
/*  702 */         handleCancelledChain(ref, chain);
/*  703 */         chain.onCompletion(this.cooldownHandler, this.hasRemoteClient);
/*  704 */         return chain.getForkedChains().isEmpty();
/*      */       }
/*      */     
/*  707 */     } else if (chain.getClientState() != InteractionState.NotFinished && !waitingForClient(ref)) {
/*      */       
/*  709 */       if (chain.getWaitingForServerFinished() == 0L) chain.setWaitingForServerFinished(this.currentTime); 
/*  710 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - chain.getWaitingForServerFinished());
/*  711 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/*  712 */       if (context.isEnabled()) {
/*  713 */         context.log("Client finished chain but server hasn't! %d, %s, %s", Integer.valueOf(chain.getChainId()), chain, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/*  716 */       long threshold = getOperationTimeoutThreshold();
/*  717 */       if (waitMillis > threshold)
/*      */       {
/*      */ 
/*      */         
/*  721 */         LOGGER.at(Level.SEVERE).log("Client finished chain earlier than server! %d, %s", chain.getChainId(), chain);
/*      */       }
/*      */     } 
/*      */     
/*  725 */     return false;
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
/*  739 */     assert this.commandBuffer != null;
/*      */     
/*  741 */     RootInteraction root = chain.getRootInteraction();
/*  742 */     int maxOperations = root.getOperationMax();
/*      */ 
/*      */     
/*  745 */     if (chain.getOperationCounter() >= maxOperations) {
/*      */       return;
/*      */     }
/*  748 */     InteractionEntry entry = chain.getInteraction(chain.getOperationIndex());
/*  749 */     if (entry == null)
/*      */       return; 
/*  751 */     Operation operation = root.getOperation(chain.getOperationCounter());
/*  752 */     if (operation == null) {
/*  753 */       throw new IllegalStateException("Failed to find operation during simulation tick of chain '" + root.getId() + "'");
/*      */     }
/*      */     
/*  756 */     InteractionContext context = chain.getContext();
/*      */ 
/*      */     
/*  759 */     (entry.getServerState()).state = InteractionState.Failed;
/*  760 */     if (entry.getClientState() != null) (entry.getClientState()).state = InteractionState.Failed;
/*      */     
/*      */     try {
/*  763 */       context.initEntry(chain, entry, this.entity);
/*      */       
/*  765 */       TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  766 */       operation.handle(ref, false, entry.getTimeInSeconds(this.currentTime) * timeResource.getTimeDilationModifier(), chain.getType(), context);
/*      */     } finally {
/*  768 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/*      */ 
/*      */     
/*  772 */     chain.setOperationCounter(maxOperations);
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
/*  789 */     ObjectList<InteractionSyncData> interactionData = this.tempSyncDataList;
/*  790 */     interactionData.clear();
/*      */     
/*  792 */     RootInteraction root = chain.getRootInteraction();
/*  793 */     int maxOperations = root.getOperationMax();
/*      */     
/*  795 */     int currentOp = chain.getOperationCounter();
/*  796 */     int baseOpIndex = chain.getOperationIndex();
/*      */     
/*  798 */     int callDepth = chain.getCallDepth();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  804 */     if (chain.consumeFirstRun()) {
/*  805 */       if (chain.getForkedChainId() == null) {
/*  806 */         chain.setTimeShift(getGlobalTimeShift(chain.getType()));
/*      */       } else {
/*  808 */         InteractionChain parent = (InteractionChain)this.chains.get(chain.getChainId());
/*  809 */         chain.setFirstRun((parent != null && parent.isFirstRun()));
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  815 */       chain.setTimeShift(0.0F);
/*      */     } 
/*      */     
/*  818 */     if (!chain.getContext().getEntity().isValid())
/*      */     {
/*  820 */       throw new ChainCancelledException(chain.getServerState());
/*      */     }
/*      */     
/*      */     while (true) {
/*  824 */       Operation simOp = !this.hasRemoteClient ? root.getOperation(chain.getSimulatedOperationCounter()) : null;
/*  825 */       WaitForDataFrom simWaitFrom = (simOp != null) ? simOp.getWaitForDataFrom() : null;
/*      */       
/*  827 */       long tickTime = this.currentTime;
/*      */ 
/*      */ 
/*      */       
/*  831 */       if (!this.hasRemoteClient && simWaitFrom != WaitForDataFrom.Server) {
/*  832 */         simulationTick(ref, chain, tickTime);
/*      */       }
/*      */       
/*  835 */       interactionData.add(serverTick(ref, chain, tickTime));
/*      */ 
/*      */ 
/*      */       
/*  839 */       if (!chain.getContext().getEntity().isValid() && chain.getServerState() != InteractionState.Finished && chain.getServerState() != InteractionState.Failed) {
/*  840 */         throw new ChainCancelledException(chain.getServerState());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  845 */       if (!this.hasRemoteClient && simWaitFrom == WaitForDataFrom.Server) {
/*  846 */         simulationTick(ref, chain, tickTime);
/*      */       }
/*      */       
/*  849 */       if (!this.hasRemoteClient) {
/*      */         
/*  851 */         if (chain.getRootInteraction() != chain.getSimulatedRootInteraction()) {
/*  852 */           throw new IllegalStateException("Simulation and server tick are not in sync (root interaction).\n" + chain
/*  853 */               .getRootInteraction().getId() + " vs " + String.valueOf(chain.getSimulatedRootInteraction()));
/*      */         }
/*  855 */         if (chain.getOperationCounter() != chain.getSimulatedOperationCounter()) {
/*  856 */           throw new IllegalStateException("Simulation and server tick are not in sync (operation position).\nRoot: " + chain
/*  857 */               .getRootInteraction().getId() + "\nCounter: " + chain
/*  858 */               .getOperationCounter() + " vs " + chain.getSimulatedOperationCounter() + "\nIndex: " + chain
/*  859 */               .getOperationIndex());
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  865 */       if (callDepth != chain.getCallDepth()) {
/*  866 */         callDepth = chain.getCallDepth();
/*  867 */         root = chain.getRootInteraction();
/*  868 */         maxOperations = root.getOperationMax();
/*  869 */       } else if (currentOp == chain.getOperationCounter()) {
/*      */         break;
/*      */       } 
/*      */       
/*  873 */       chain.nextOperationIndex();
/*      */       
/*  875 */       currentOp = chain.getOperationCounter();
/*  876 */       if (currentOp >= maxOperations) {
/*      */         
/*  878 */         while (callDepth > 0) {
/*  879 */           chain.popRoot();
/*  880 */           callDepth = chain.getCallDepth();
/*  881 */           currentOp = chain.getOperationCounter();
/*  882 */           root = chain.getRootInteraction();
/*  883 */           maxOperations = root.getOperationMax();
/*      */ 
/*      */           
/*  886 */           if (currentOp < maxOperations || callDepth == 0) {
/*      */             break;
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/*  892 */         if (callDepth == 0 && currentOp >= maxOperations) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*  897 */     chain.updateServerState();
/*  898 */     if (!this.hasRemoteClient) chain.updateSimulatedState(); 
/*  899 */     if (chain.requiresClient()) {
/*  900 */       sendSyncPacket(chain, baseOpIndex, (List<InteractionSyncData>)interactionData);
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
/*  918 */     assert this.commandBuffer != null;
/*      */     
/*  920 */     RootInteraction root = chain.getRootInteraction();
/*  921 */     Operation operation = root.getOperation(chain.getOperationCounter());
/*  922 */     assert operation != null;
/*      */     
/*  924 */     InteractionEntry entry = chain.getOrCreateInteractionEntry(chain.getOperationIndex());
/*      */     
/*  926 */     InteractionSyncData returnData = null;
/*      */ 
/*      */     
/*  929 */     boolean wasWrong = entry.consumeDesyncFlag();
/*  930 */     if (entry.getClientState() == null) {
/*  931 */       i = wasWrong | (!entry.setClientState(chain.removeInteractionSyncData(chain.getOperationIndex())) ? 1 : 0);
/*      */     }
/*      */     
/*  934 */     if (i != 0) {
/*      */       
/*  936 */       returnData = entry.getServerState();
/*  937 */       chain.flagDesync();
/*  938 */       chain.clearInteractionSyncData(chain.getOperationIndex());
/*      */     } 
/*      */     
/*  941 */     TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/*  942 */     float tickTimeDilation = timeResource.getTimeDilationModifier();
/*      */ 
/*      */     
/*  945 */     if (operation.getWaitForDataFrom() == WaitForDataFrom.Client && entry.getClientState() == null) {
/*  946 */       if (waitingForClient(ref)) return null; 
/*  947 */       if (entry.getWaitingForSyncData() == 0L) entry.setWaitingForSyncData(this.currentTime); 
/*  948 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - entry.getWaitingForSyncData());
/*      */       
/*  950 */       HytaleLogger.Api api = LOGGER.at(Level.FINE);
/*  951 */       if (api.isEnabled()) {
/*  952 */         api.log("Wait for interaction clientData: %d, %s, %s", Integer.valueOf(chain.getOperationIndex()), entry, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/*  955 */       long threshold = getOperationTimeoutThreshold();
/*      */       
/*  957 */       if (tickTimeDilation == 1.0F && waitMillis > threshold) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  963 */         SentryEvent event = new SentryEvent();
/*  964 */         event.setLevel(SentryLevel.ERROR);
/*  965 */         Message message = new Message();
/*  966 */         message.setMessage("Client failed to send client data, ending early to prevent desync");
/*      */         
/*  968 */         HashMap<String, Object> unknown = new HashMap<>();
/*  969 */         unknown.put("Threshold", Long.valueOf(threshold));
/*  970 */         unknown.put("Wait Millis", Long.valueOf(waitMillis));
/*  971 */         unknown.put("Current Root", (chain.getRootInteraction() != null) ? chain.getRootInteraction().getId() : "<null>");
/*  972 */         Operation innerOp = operation.getInnerOperation();
/*  973 */         unknown.put("Current Op", innerOp.getClass().getName());
/*  974 */         if (innerOp instanceof Interaction) { Interaction interaction = (Interaction)innerOp;
/*  975 */           unknown.put("Current Interaction", interaction.getId()); }
/*      */         
/*  977 */         unknown.put("Current Index", Integer.valueOf(chain.getOperationIndex()));
/*  978 */         unknown.put("Current Op Counter", Integer.valueOf(chain.getOperationCounter()));
/*      */         
/*  980 */         HistoricMetric metric = ((EntityStore)ref.getStore().getExternalData()).getWorld().getBufferedTickLengthMetricSet();
/*  981 */         long[] periods = metric.getPeriodsNanos();
/*  982 */         for (int j = 0; j < periods.length; j++) {
/*  983 */           String length = FormatUtil.timeUnitToString(periods[j], TimeUnit.NANOSECONDS, true);
/*  984 */           double average = metric.getAverage(j);
/*  985 */           long min = metric.calculateMin(j);
/*  986 */           long max = metric.calculateMax(j);
/*      */           
/*  988 */           String value = FormatUtil.simpleTimeUnitFormat(min, average, max, TimeUnit.NANOSECONDS, TimeUnit.MILLISECONDS, 3);
/*  989 */           unknown.put(String.format("World Perf %s", new Object[] { length }), value);
/*      */         } 
/*      */         
/*  992 */         event.setExtras(unknown);
/*  993 */         event.setMessage(message);
/*  994 */         SentryId eventId = Sentry.captureEvent(event);
/*  995 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Client failed to send client data, ending early to prevent desync. %s", eventId);
/*      */         
/*  997 */         chain.setServerState(InteractionState.Failed);
/*  998 */         chain.setClientState(InteractionState.Failed);
/*  999 */         sendCancelPacket(chain);
/* 1000 */         return null;
/*      */       } 
/*      */ 
/*      */       
/* 1004 */       if (entry.consumeSendInitial() || i != 0) {
/* 1005 */         returnData = entry.getServerState();
/*      */       }
/* 1007 */       return returnData;
/*      */     } 
/*      */     
/* 1010 */     int serverDataHashCode = entry.getServerDataHashCode();
/* 1011 */     InteractionContext context = chain.getContext();
/*      */     
/* 1013 */     float time = entry.getTimeInSeconds(tickTime);
/* 1014 */     boolean firstRun = false;
/* 1015 */     if (entry.getTimestamp() == 0L) {
/* 1016 */       time = chain.getTimeShift();
/* 1017 */       entry.setTimestamp(tickTime, time);
/* 1018 */       firstRun = true;
/*      */     } 
/*      */     
/* 1021 */     time *= tickTimeDilation;
/*      */     
/*      */     try {
/* 1024 */       context.initEntry(chain, entry, this.entity);
/* 1025 */       operation.tick(ref, this.entity, firstRun, time, chain.getType(), context, this.cooldownHandler);
/*      */     } finally {
/* 1027 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/*      */     
/* 1030 */     InteractionSyncData serverData = entry.getServerState();
/*      */     
/* 1032 */     if (firstRun || serverDataHashCode != entry.getServerDataHashCode()) returnData = serverData;
/*      */     
/*      */     try {
/* 1035 */       context.initEntry(chain, entry, this.entity);
/* 1036 */       operation.handle(ref, firstRun, time, chain.getType(), context);
/*      */     } finally {
/* 1038 */       context.deinitEntry(chain, entry, this.entity);
/*      */     } 
/* 1040 */     removeInteractionIfFinished(ref, chain, entry);
/*      */     
/* 1042 */     return returnData;
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
/* 1058 */     if (chain.getOperationIndex() == entry.getIndex() && (entry.getServerState()).state != InteractionState.NotFinished) {
/* 1059 */       chain.setFinalState((entry.getServerState()).state);
/*      */     }
/*      */ 
/*      */     
/* 1063 */     if ((entry.getServerState()).state != InteractionState.NotFinished) {
/*      */       
/* 1065 */       LOGGER.at(Level.FINE).log("Server finished interaction: %d, %s", entry.getIndex(), entry);
/* 1066 */       if (!chain.requiresClient() || (entry.getClientState() != null && (entry.getClientState()).state != InteractionState.NotFinished)) {
/* 1067 */         LOGGER.at(Level.FINER).log("Remove Interaction: %d, %s", entry.getIndex(), entry);
/* 1068 */         chain.removeInteractionEntry(this, entry.getIndex());
/*      */       }
/*      */     
/* 1071 */     } else if (entry.getClientState() != null && (entry.getClientState()).state != InteractionState.NotFinished && !waitingForClient(ref)) {
/*      */       
/* 1073 */       if (entry.getWaitingForServerFinished() == 0L) entry.setWaitingForServerFinished(this.currentTime); 
/* 1074 */       long waitMillis = TimeUnit.NANOSECONDS.toMillis(this.currentTime - entry.getWaitingForServerFinished());
/* 1075 */       HytaleLogger.Api context = LOGGER.at(Level.FINE);
/* 1076 */       if (context.isEnabled()) {
/* 1077 */         context.log("Client finished interaction but server hasn't! %s, %d, %s, %s", (entry.getClientState()).state, Integer.valueOf(entry.getIndex()), entry, Long.valueOf(waitMillis));
/*      */       }
/*      */       
/* 1080 */       long threshold = getOperationTimeoutThreshold();
/* 1081 */       if (waitMillis > threshold) {
/*      */ 
/*      */ 
/*      */         
/* 1085 */         HytaleLogger.Api ctx = LOGGER.at(Level.SEVERE);
/* 1086 */         if (ctx.isEnabled()) ctx.log("Client finished interaction earlier than server! %d, %s", entry.getIndex(), entry);
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
/* 1101 */     assert this.commandBuffer != null;
/*      */     
/* 1103 */     RootInteraction rootInteraction = chain.getRootInteraction();
/* 1104 */     Operation operation = rootInteraction.getOperation(chain.getSimulatedOperationCounter());
/* 1105 */     if (operation == null) {
/* 1106 */       throw new IllegalStateException("Failed to find operation during simulation tick of chain '" + rootInteraction.getId() + "'");
/*      */     }
/*      */     
/* 1109 */     InteractionEntry entry = chain.getOrCreateInteractionEntry(chain.getClientOperationIndex());
/* 1110 */     InteractionContext context = chain.getContext();
/*      */     
/* 1112 */     entry.setUseSimulationState(true);
/*      */     try {
/* 1114 */       context.initEntry(chain, entry, this.entity);
/*      */       
/* 1116 */       float time = entry.getTimeInSeconds(tickTime);
/* 1117 */       boolean firstRun = false;
/* 1118 */       if (entry.getTimestamp() == 0L) {
/* 1119 */         time = chain.getTimeShift();
/* 1120 */         entry.setTimestamp(tickTime, time);
/* 1121 */         firstRun = true;
/*      */       } 
/*      */       
/* 1124 */       TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/* 1125 */       float tickTimeDilation = timeResource.getTimeDilationModifier();
/* 1126 */       time *= tickTimeDilation;
/*      */       
/* 1128 */       operation.simulateTick(ref, this.entity, firstRun, time, chain.getType(), context, this.cooldownHandler);
/*      */     } finally {
/* 1130 */       context.deinitEntry(chain, entry, this.entity);
/* 1131 */       entry.setUseSimulationState(false);
/*      */     } 
/*      */     
/* 1134 */     if (!entry.setClientState(entry.getSimulationState())) {
/* 1135 */       throw new RuntimeException("Simulation failed");
/*      */     }
/*      */     
/* 1138 */     removeInteractionIfFinished(ref, chain, entry);
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
/* 1152 */     assert this.commandBuffer != null;
/* 1153 */     int index = packet.chainId;
/*      */     
/* 1155 */     if (!packet.initial) {
/* 1156 */       if (packet.forkedId == null) {
/* 1157 */         HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1158 */         if (ctx.isEnabled()) ctx.log("Got syncStart for %d-%s but packet wasn't the first.", index, packet.forkedId); 
/*      */       } 
/* 1160 */       return true;
/*      */     } 
/*      */     
/* 1163 */     if (packet.forkedId != null) {
/* 1164 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1165 */       if (ctx.isEnabled()) ctx.log("Can't start a forked chain from the client: %d %s", index, packet.forkedId); 
/* 1166 */       return true;
/*      */     } 
/*      */     
/* 1169 */     InteractionType type = packet.interactionType;
/*      */     
/* 1171 */     if (index <= 0) {
/* 1172 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1173 */       if (ctx.isEnabled()) ctx.log("Invalid client chainId! Got %d but client id's should be > 0", index); 
/* 1174 */       sendCancelPacket(index, packet.forkedId);
/* 1175 */       return true;
/*      */     } 
/* 1177 */     if (index <= this.lastClientChainId) {
/* 1178 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1179 */       if (ctx.isEnabled()) ctx.log("Invalid client chainId! The last clientChainId was %d but just got %d", this.lastClientChainId, index); 
/* 1180 */       sendCancelPacket(index, packet.forkedId);
/* 1181 */       return true;
/*      */     } 
/*      */     
/* 1184 */     UUID proxyId = packet.data.proxyId;
/*      */ 
/*      */     
/* 1187 */     if (!UUIDUtil.isEmptyOrNull(proxyId)) {
/* 1188 */       World world1 = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/* 1189 */       Ref<EntityStore> proxyTarget = world1.getEntityStore().getRefFromUUID(proxyId);
/*      */       
/* 1191 */       if (proxyTarget == null) {
/* 1192 */         if (this.packetQueueTime != 0L && this.currentTime - this.packetQueueTime > TimeUnit.MILLISECONDS.toNanos(getOperationTimeoutThreshold()) / 2L) {
/* 1193 */           HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1194 */           if (ctx.isEnabled()) ctx.log("Proxy entity never spawned"); 
/* 1195 */           sendCancelPacket(index, packet.forkedId);
/* 1196 */           return true;
/*      */         } 
/* 1198 */         return false;
/*      */       } 
/* 1200 */       context = InteractionContext.forProxyEntity(this, this.entity, proxyTarget);
/*      */     } else {
/*      */       
/* 1203 */       context = InteractionContext.forInteraction(this, ref, type, packet.equipSlot, (ComponentAccessor<EntityStore>)this.commandBuffer);
/*      */     } 
/*      */     
/* 1206 */     String rootInteractionId = context.getRootInteractionId(type);
/* 1207 */     if (rootInteractionId == null) {
/* 1208 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1209 */       if (ctx.isEnabled()) ctx.log("Missing root interaction: %d, %s, %s", Integer.valueOf(index), this.entity.getInventory().getItemInHand(), type); 
/* 1210 */       sendCancelPacket(index, packet.forkedId);
/* 1211 */       return true;
/*      */     } 
/*      */     
/* 1214 */     RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(rootInteractionId);
/* 1215 */     if (rootInteraction == null) return false;
/*      */     
/* 1217 */     if (!applyRules(context, packet.data, type, rootInteraction)) {
/* 1218 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1223 */     Inventory entityInventory = this.entity.getInventory();
/* 1224 */     ItemStack itemInHand = entityInventory.getActiveHotbarItem();
/* 1225 */     ItemStack utilityItem = entityInventory.getUtilityItem();
/*      */     
/* 1227 */     String serverItemInHandId = (itemInHand != null) ? itemInHand.getItemId() : null;
/* 1228 */     String serverUtilityItemId = (utilityItem != null) ? utilityItem.getItemId() : null;
/*      */     
/* 1230 */     if (packet.activeHotbarSlot != entityInventory.getActiveHotbarSlot()) {
/* 1231 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1232 */       if (ctx.isEnabled()) {
/* 1233 */         ctx.log("Active slot miss match: %d, %d != %d, %s, %s, %s", Integer.valueOf(index), Byte.valueOf(entityInventory.getActiveHotbarSlot()), Integer.valueOf(packet.activeHotbarSlot), serverItemInHandId, packet.itemInHandId, type);
/*      */       }
/* 1235 */       sendCancelPacket(index, packet.forkedId);
/*      */ 
/*      */       
/* 1238 */       if (this.playerRef != null) {
/* 1239 */         this.playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, entityInventory.getActiveHotbarSlot()));
/*      */       }
/* 1241 */       return true;
/*      */     } 
/*      */     
/* 1244 */     if (packet.activeUtilitySlot != entityInventory.getActiveUtilitySlot()) {
/* 1245 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1246 */       if (ctx.isEnabled()) {
/* 1247 */         ctx.log("Active slot miss match: %d, %d != %d, %s, %s, %s", Integer.valueOf(index), Byte.valueOf(entityInventory.getActiveUtilitySlot()), Integer.valueOf(packet.activeUtilitySlot), serverItemInHandId, packet.itemInHandId, type);
/*      */       }
/* 1249 */       sendCancelPacket(index, packet.forkedId);
/*      */ 
/*      */       
/* 1252 */       if (this.playerRef != null) {
/* 1253 */         this.playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-5, entityInventory.getActiveUtilitySlot()));
/*      */       }
/* 1255 */       return true;
/*      */     } 
/*      */     
/* 1258 */     if (!Objects.equals(serverItemInHandId, packet.itemInHandId)) {
/* 1259 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1260 */       if (ctx.isEnabled()) ctx.log("ItemInHand miss match: %d, %s, %s, %s", Integer.valueOf(index), serverItemInHandId, packet.itemInHandId, type); 
/* 1261 */       sendCancelPacket(index, packet.forkedId);
/* 1262 */       return true;
/*      */     } 
/*      */     
/* 1265 */     if (!Objects.equals(serverUtilityItemId, packet.utilityItemId)) {
/* 1266 */       HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1267 */       if (ctx.isEnabled()) ctx.log("UtilityItem miss match: %d, %s, %s, %s", Integer.valueOf(index), serverUtilityItemId, packet.utilityItemId, type); 
/* 1268 */       sendCancelPacket(index, packet.forkedId);
/* 1269 */       return true;
/*      */     } 
/*      */     
/* 1272 */     if (isOnCooldown(ref, type, rootInteraction, true)) {
/* 1273 */       return false;
/*      */     }
/*      */     
/* 1276 */     InteractionChain chain = initChain(packet.data, type, context, rootInteraction, (Runnable)null, true);
/* 1277 */     chain.setChainId(index);
/* 1278 */     sync(ref, chain, packet);
/*      */     
/* 1280 */     World world = ((EntityStore)this.commandBuffer.getExternalData()).getWorld();
/*      */ 
/*      */ 
/*      */     
/* 1284 */     if (packet.data.blockPosition != null) {
/*      */       
/* 1286 */       BlockPosition targetBlock = world.getBaseBlock(packet.data.blockPosition);
/* 1287 */       context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, targetBlock);
/* 1288 */       context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, packet.data.blockPosition);
/*      */       
/* 1290 */       if (!packet.data.blockPosition.equals(targetBlock)) {
/* 1291 */         WorldChunk otherChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(packet.data.blockPosition.x, packet.data.blockPosition.z));
/* 1292 */         if (otherChunk == null) {
/* 1293 */           HytaleLogger.Api ctx = LOGGER.at(Level.FINE);
/* 1294 */           if (ctx.isEnabled()) ctx.log("Unloaded chunk interacted with: %d, %s", index, type); 
/* 1295 */           sendCancelPacket(index, packet.forkedId);
/* 1296 */           return true;
/*      */         } 
/* 1298 */         int blockId = world.getBlock(targetBlock.x, targetBlock.y, targetBlock.z);
/* 1299 */         int otherBlockId = world.getBlock(packet.data.blockPosition.x, packet.data.blockPosition.y, packet.data.blockPosition.z);
/* 1300 */         if (blockId != otherBlockId) {
/* 1301 */           otherChunk.setBlock(packet.data.blockPosition.x, packet.data.blockPosition.y, packet.data.blockPosition.z, 0, BlockType.EMPTY, 0, 0, 1052);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1308 */     if (packet.data.entityId >= 0) {
/*      */       
/* 1310 */       EntityStore entityComponentStore = world.getEntityStore();
/* 1311 */       Ref<EntityStore> entityReference = entityComponentStore.getRefFromNetworkId(packet.data.entityId);
/* 1312 */       if (entityReference != null) {
/* 1313 */         context.getMetaStore().putMetaObject(Interaction.TARGET_ENTITY, entityReference);
/*      */       }
/*      */     } 
/*      */     
/* 1317 */     if (packet.data.targetSlot != Integer.MIN_VALUE) {
/* 1318 */       context.getMetaStore().putMetaObject(Interaction.TARGET_SLOT, Integer.valueOf(packet.data.targetSlot));
/*      */     }
/*      */     
/* 1321 */     if (packet.data.hitLocation != null) {
/* 1322 */       Vector3f hit = packet.data.hitLocation;
/* 1323 */       context.getMetaStore().putMetaObject(Interaction.HIT_LOCATION, new Vector4d(hit.x, hit.y, hit.z, 1.0D));
/*      */     } 
/*      */     
/* 1326 */     if (packet.data.hitDetail != null) {
/* 1327 */       context.getMetaStore().putMetaObject(Interaction.HIT_DETAIL, packet.data.hitDetail);
/*      */     }
/*      */     
/* 1330 */     this.lastClientChainId = index;
/*      */ 
/*      */     
/* 1333 */     if (!tickChain(chain)) {
/* 1334 */       chain.setPreTicked(true);
/* 1335 */       this.chains.put(index, chain);
/*      */     } 
/*      */     
/* 1338 */     return true;
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
/* 1350 */     assert this.commandBuffer != null;
/*      */     
/* 1352 */     if (packet.newForks != null) {
/* 1353 */       for (SyncInteractionChain fork : packet.newForks) {
/* 1354 */         chainSyncStorage.syncFork(ref, this, fork);
/*      */       }
/*      */     }
/*      */     
/* 1358 */     if (packet.interactionData == null) {
/* 1359 */       chainSyncStorage.setClientState(packet.state);
/*      */       
/*      */       return;
/*      */     } 
/* 1363 */     for (int i = 0; i < packet.interactionData.length; i++) {
/* 1364 */       InteractionSyncData syncData = packet.interactionData[i];
/* 1365 */       if (syncData != null) {
/*      */ 
/*      */         
/* 1368 */         int index = packet.operationBaseIndex + i;
/* 1369 */         if (!chainSyncStorage.isSyncDataOutOfOrder(index)) {
/*      */ 
/*      */ 
/*      */           
/* 1373 */           InteractionEntry interaction = chainSyncStorage.getInteraction(index);
/* 1374 */           if (interaction != null && chainSyncStorage instanceof InteractionChain) { InteractionChain interactionChain = (InteractionChain)chainSyncStorage;
/*      */             
/* 1376 */             if ((interaction.getClientState() != null && (interaction.getClientState()).state != InteractionState.NotFinished && syncData.state == InteractionState.NotFinished) || 
/* 1377 */               !interaction.setClientState(syncData)) {
/* 1378 */               chainSyncStorage.clearInteractionSyncData(index);
/*      */ 
/*      */               
/* 1381 */               interaction.flagDesync();
/* 1382 */               interactionChain.flagDesync();
/*      */               
/*      */               return;
/*      */             } 
/* 1386 */             chainSyncStorage.updateSyncPosition(index);
/*      */             
/* 1388 */             HytaleLogger.Api context = LOGGER.at(Level.FINEST);
/* 1389 */             if (context.isEnabled()) {
/* 1390 */               TimeResource timeResource = (TimeResource)this.commandBuffer.getResource(TimeResource.getResourceType());
/* 1391 */               float tickTimeDilation = timeResource.getTimeDilationModifier();
/*      */               
/* 1393 */               context.log("%d, %d: Time (Sync) - Server: %s vs Client: %s", 
/* 1394 */                   Integer.valueOf(packet.chainId), Integer.valueOf(index), 
/* 1395 */                   Float.valueOf(interaction.getTimeInSeconds(this.currentTime) * tickTimeDilation), Float.valueOf((interaction.getClientState()).progress));
/*      */             } 
/*      */             
/* 1398 */             removeInteractionIfFinished(ref, interactionChain, interaction); }
/*      */           else
/* 1400 */           { chainSyncStorage.putInteractionSyncData(index, syncData); }
/*      */         
/*      */         } 
/*      */       } 
/* 1404 */     }  int last = packet.operationBaseIndex + packet.interactionData.length;
/* 1405 */     chainSyncStorage.clearInteractionSyncData(last);
/*      */     
/* 1407 */     chainSyncStorage.setClientState(packet.state);
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
/* 1418 */     return canRun(type, (short)-1, rootInteraction);
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
/* 1430 */     return applyRules(null, type, equipSlot, rootInteraction, (Map)this.chains, null);
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
/* 1451 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/*      */     
/* 1453 */     if (!applyRules(data, type, context.getHeldItemSlot(), rootInteraction, (Map)this.chains, (List<InteractionChain>)objectArrayList)) return false;
/*      */ 
/*      */     
/* 1456 */     for (InteractionChain interactionChain : objectArrayList) {
/* 1457 */       cancelChains(interactionChain);
/*      */     }
/* 1459 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void cancelChains(@Nonnull InteractionChain chain) {
/* 1468 */     chain.setServerState(InteractionState.Failed);
/* 1469 */     chain.setClientState(InteractionState.Failed);
/* 1470 */     sendCancelPacket(chain);
/*      */     
/* 1472 */     for (ObjectIterator<InteractionChain> objectIterator = chain.getForkedChains().values().iterator(); objectIterator.hasNext(); ) { InteractionChain fork = objectIterator.next();
/* 1473 */       cancelChains(fork); }
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
/* 1496 */     if (chains.isEmpty() || rootInteraction == null) return true;
/*      */     
/* 1498 */     for (InteractionChain chain : chains.values()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1505 */       if (chain.getForkedChainId() != null && !chain.isPredicted()) {
/*      */         continue;
/*      */       }
/*      */       
/* 1509 */       if (data != null && !Objects.equals((chain.getChainData()).proxyId, data.proxyId)) {
/*      */         continue;
/*      */       }
/* 1512 */       if (type == InteractionType.Equipped && chain.getType() == InteractionType.Equipped && chain.getContext().getHeldItemSlot() != heldItemSlot) {
/*      */         continue;
/*      */       }
/*      */       
/* 1516 */       if (chain.getServerState() == InteractionState.NotFinished) {
/*      */         
/* 1518 */         RootInteraction currentRoot = chain.getRootInteraction();
/* 1519 */         Operation currentOp = currentRoot.getOperation(chain.getOperationCounter());
/*      */ 
/*      */         
/* 1522 */         if (rootInteraction.getRules().validateInterrupts(type, rootInteraction.getData().getTags(), chain.getType(), currentRoot.getData().getTags(), currentRoot.getRules())) {
/* 1523 */           if (chainsToCancel != null) chainsToCancel.add(chain); 
/* 1524 */         } else if (currentOp != null && currentOp.getRules() != null && rootInteraction.getRules().validateInterrupts(type, rootInteraction.getData().getTags(), chain.getType(), currentOp.getTags(), currentOp.getRules())) {
/*      */           
/* 1526 */           if (chainsToCancel != null) chainsToCancel.add(chain); 
/*      */         } else {
/* 1528 */           if (rootInteraction.getRules().validateBlocked(type, rootInteraction.getData().getTags(), chain.getType(), currentRoot.getData().getTags(), currentRoot.getRules())) {
/* 1529 */             return false;
/*      */           }
/*      */ 
/*      */           
/* 1533 */           if (currentOp != null && currentOp.getRules() != null && rootInteraction.getRules().validateBlocked(type, rootInteraction.getData().getTags(), chain.getType(), currentOp.getTags(), currentOp.getRules())) {
/* 1534 */             return false;
/*      */           }
/*      */         } 
/*      */       } 
/*      */       
/* 1539 */       if ((chainsToCancel == null || chainsToCancel.isEmpty()) && !applyRules(data, type, heldItemSlot, rootInteraction, (Map)chain.getForkedChains(), chainsToCancel)) {
/* 1540 */         return false;
/*      */       }
/*      */     } 
/* 1543 */     return true;
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
/* 1562 */     InteractionChain chain = initChain(type, context, rootInteraction, false);
/* 1563 */     if (!applyRules(context, chain.getChainData(), type, rootInteraction)) {
/* 1564 */       return false;
/*      */     }
/* 1566 */     executeChain(ref, commandBuffer, chain);
/* 1567 */     return true;
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
/* 1585 */     InteractionChain chain = initChain(type, context, rootInteraction, false);
/* 1586 */     executeChain(ref, commandBuffer, chain);
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
/* 1605 */     return initChain(type, context, rootInteraction, -1, (BlockPosition)null, forceRemoteSync);
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
/* 1628 */     InteractionChainData data = new InteractionChainData(entityId, UUIDUtil.EMPTY_UUID, null, null, blockPosition, -2147483648, null);
/* 1629 */     return initChain(data, type, context, rootInteraction, (Runnable)null, forceRemoteSync);
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
/* 1659 */     return new InteractionChain(type, context, data, rootInteraction, onCompletion, (forceRemoteSync || !this.hasRemoteClient));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void queueExecuteChain(@Nonnull InteractionChain chain) {
/* 1668 */     this.chainStartQueue.add(chain);
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
/* 1683 */     this.commandBuffer = commandBuffer;
/* 1684 */     executeChain0(ref, chain);
/* 1685 */     this.commandBuffer = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void executeChain0(@Nonnull Ref<EntityStore> ref, @Nonnull InteractionChain chain) {
/* 1695 */     if (isOnCooldown(ref, chain.getType(), chain.getInitialRootInteraction(), false)) {
/* 1696 */       chain.setServerState(InteractionState.Failed);
/* 1697 */       chain.setClientState(InteractionState.Failed);
/*      */       
/*      */       return;
/*      */     } 
/* 1701 */     int index = --this.lastServerChainId;
/* 1702 */     if (index >= 0) index = this.lastServerChainId = -1; 
/* 1703 */     chain.setChainId(index);
/*      */     
/* 1705 */     if (tickChain(chain)) {
/*      */       return;
/*      */     }
/* 1708 */     LOGGER.at(Level.FINE).log("Add Chain: %d, %s", index, chain);
/* 1709 */     chain.setPreTicked(true);
/* 1710 */     this.chains.put(index, chain);
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
/* 1725 */     assert this.commandBuffer != null;
/*      */     
/* 1727 */     InteractionCooldown cooldown = root.getCooldown();
/* 1728 */     String cooldownId = root.getId();
/* 1729 */     float cooldownTime = InteractionTypeUtils.getDefaultCooldown(type);
/* 1730 */     float[] cooldownChargeTimes = DEFAULT_CHARGE_TIMES;
/* 1731 */     boolean interruptRecharge = false;
/*      */     
/* 1733 */     if (cooldown != null) {
/* 1734 */       cooldownTime = cooldown.cooldown;
/* 1735 */       if (cooldown.chargeTimes != null && cooldown.chargeTimes.length > 0) {
/* 1736 */         cooldownChargeTimes = cooldown.chargeTimes;
/*      */       }
/* 1738 */       if (cooldown.cooldownId != null) cooldownId = cooldown.cooldownId; 
/* 1739 */       if (cooldown.interruptRecharge) interruptRecharge = true;
/*      */ 
/*      */       
/* 1742 */       if (cooldown.clickBypass && remote) {
/* 1743 */         this.cooldownHandler.resetCooldown(cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
/* 1744 */         return false;
/*      */       } 
/*      */     } 
/*      */     
/* 1748 */     Player playerComponent = (Player)this.commandBuffer.getComponent(ref, Player.getComponentType());
/* 1749 */     GameMode gameMode = (playerComponent != null) ? playerComponent.getGameMode() : GameMode.Adventure;
/* 1750 */     RootInteractionSettings settings = (RootInteractionSettings)root.getSettings().get(gameMode);
/* 1751 */     if (settings != null) {
/* 1752 */       cooldown = settings.cooldown;
/* 1753 */       if (cooldown != null) {
/* 1754 */         cooldownTime = cooldown.cooldown;
/* 1755 */         if (cooldown.chargeTimes != null && cooldown.chargeTimes.length > 0) {
/* 1756 */           cooldownChargeTimes = cooldown.chargeTimes;
/*      */         }
/* 1758 */         if (cooldown.cooldownId != null) cooldownId = cooldown.cooldownId; 
/* 1759 */         if (cooldown.interruptRecharge) interruptRecharge = true;
/*      */ 
/*      */         
/* 1762 */         if (cooldown.clickBypass && remote) {
/* 1763 */           this.cooldownHandler.resetCooldown(cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
/* 1764 */           return false;
/*      */         } 
/*      */       } 
/* 1767 */       if (settings.allowSkipChainOnClick && remote) {
/* 1768 */         this.cooldownHandler.resetCooldown(cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
/* 1769 */         return false;
/*      */       } 
/*      */     } 
/*      */     
/* 1773 */     return this.cooldownHandler.isOnCooldown(root, cooldownId, cooldownTime, cooldownChargeTimes, interruptRecharge);
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
/* 1786 */     tryRunHeldInteraction(ref, commandBuffer, type, (short)-1);
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
/* 1801 */     Inventory inventory = this.entity.getInventory();
/*      */ 
/*      */     
/* 1804 */     switch (type) { case Held:
/* 1805 */         itemStack = inventory.getItemInHand(); break;
/* 1806 */       case HeldOffhand: itemStack = inventory.getUtilityItem(); break;
/*      */       case Equipped:
/* 1808 */         if (equipSlot == -1) throw new IllegalArgumentException(); 
/* 1809 */         itemStack = inventory.getArmor().getItemStack(equipSlot); break;
/*      */       default:
/* 1811 */         throw new IllegalArgumentException(); }
/*      */ 
/*      */     
/* 1814 */     if (itemStack == null || itemStack.isEmpty())
/*      */       return; 
/* 1816 */     String rootId = (String)itemStack.getItem().getInteractions().get(type);
/* 1817 */     if (rootId == null)
/*      */       return; 
/* 1819 */     RootInteraction root = (RootInteraction)RootInteraction.getAssetMap().getAsset(rootId);
/* 1820 */     if (root == null || !canRun(type, equipSlot, root))
/*      */       return; 
/* 1822 */     InteractionContext context = InteractionContext.forInteraction(this, ref, type, equipSlot, (ComponentAccessor<EntityStore>)commandBuffer);
/* 1823 */     startChain(ref, commandBuffer, type, context, root);
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
/* 1834 */     if (chain.hasSentInitial() && (interactionData == null || 
/* 1835 */       ListUtil.emptyOrAllNull(interactionData)) && chain
/* 1836 */       .getNewForks().isEmpty()) {
/*      */       return;
/*      */     }
/*      */     
/* 1840 */     if (this.playerRef != null) {
/* 1841 */       SyncInteractionChain packet = makeSyncPacket(chain, operationBaseIndex, interactionData);
/* 1842 */       this.syncPackets.add(packet);
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
/* 1856 */     SyncInteractionChain[] forks = null;
/* 1857 */     List<InteractionChain> newForks = chain.getNewForks();
/* 1858 */     if (!newForks.isEmpty()) {
/* 1859 */       forks = new SyncInteractionChain[newForks.size()];
/* 1860 */       for (int i = 0; i < newForks.size(); i++) {
/* 1861 */         InteractionChain fc = newForks.get(i);
/* 1862 */         forks[i] = makeSyncPacket(fc, 0, null);
/*      */       } 
/* 1864 */       newForks.clear();
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
/* 1883 */     SyncInteractionChain packet = new SyncInteractionChain(0, 0, 0, null, null, null, !chain.hasSentInitial(), false, chain.hasSentInitial() ? Integer.MIN_VALUE : RootInteraction.getRootInteractionIdOrUnknown(chain.getInitialRootInteraction().getId()), chain.getType(), chain.getContext().getHeldItemSlot(), chain.getChainId(), chain.getForkedChainId(), chain.getChainData(), chain.getServerState(), forks, operationBaseIndex, (interactionData == null) ? null : (InteractionSyncData[])interactionData.toArray(x$0 -> new InteractionSyncData[x$0]));
/*      */     
/* 1885 */     chain.setSentInitial(true);
/* 1886 */     return packet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendCancelPacket(@Nonnull InteractionChain chain) {
/* 1895 */     sendCancelPacket(chain.getChainId(), chain.getForkedChainId());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void sendCancelPacket(int chainId, @Nonnull ForkedChainId forkedChainId) {
/* 1905 */     if (this.playerRef != null) {
/* 1906 */       this.playerRef.getPacketHandler().writeNoCache((Packet)new CancelInteractionChain(chainId, forkedChainId));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/* 1915 */     forEachInteraction((chain, _i, _a) -> { chain.setServerState(InteractionState.Failed); chain.setClientState(InteractionState.Failed); sendCancelPacket(chain); return null; }null);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1922 */     this.chainStartQueue.clear();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clearAllGlobalTimeShift(float dt) {
/* 1931 */     if (this.timeShiftsDirty) {
/*      */       
/* 1933 */       boolean clearFlag = true;
/* 1934 */       for (int i = 0; i < this.globalTimeShift.length; i++) {
/* 1935 */         if (!this.globalTimeShiftDirty[i]) {
/* 1936 */           this.globalTimeShift[i] = 0.0F;
/*      */         } else {
/* 1938 */           clearFlag = false;
/* 1939 */           this.globalTimeShift[i] = this.globalTimeShift[i] + dt;
/*      */         } 
/*      */       } 
/* 1942 */       Arrays.fill(this.globalTimeShiftDirty, false);
/* 1943 */       if (clearFlag) this.timeShiftsDirty = false;
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
/* 1994 */     if (shift < 0.0F) throw new IllegalArgumentException("Can't shift backwards"); 
/* 1995 */     this.globalTimeShift[type.ordinal()] = shift;
/* 1996 */     this.globalTimeShiftDirty[type.ordinal()] = true;
/* 1997 */     this.timeShiftsDirty = true;
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
/* 2011 */     return this.globalTimeShift[type.ordinal()];
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
/* 2028 */     return forEachInteraction((Map)this.chains, func, val);
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
/* 2041 */     if (chains.isEmpty()) return val;
/*      */     
/* 2043 */     for (InteractionChain chain : chains.values()) {
/* 2044 */       Operation operation = chain.getRootInteraction().getOperation(chain.getOperationCounter());
/* 2045 */       if (operation != null) {
/* 2046 */         operation = operation.getInnerOperation();
/* 2047 */         if (operation instanceof Interaction) { Interaction interaction = (Interaction)operation;
/* 2048 */           val = (T)func.apply(chain, interaction, val); }
/*      */       
/*      */       } 
/* 2051 */       val = forEachInteraction((Map)chain.getForkedChains(), func, val);
/*      */     } 
/* 2053 */     return val;
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
/* 2069 */     walkChain(ref, collector, type, null, componentAccessor);
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
/* 2086 */     walkChain(collector, type, InteractionContext.forInteraction(this, ref, type, componentAccessor), rootInteraction);
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
/* 2103 */     if (rootInteraction == null) {
/* 2104 */       String rootInteractionId = context.getRootInteractionId(type);
/* 2105 */       if (rootInteractionId == null) {
/* 2106 */         throw new IllegalArgumentException("No interaction ID found for " + String.valueOf(type) + ", " + String.valueOf(context));
/*      */       }
/*      */       
/* 2109 */       rootInteraction = (RootInteraction)RootInteraction.getAssetMap().getAsset(rootInteractionId);
/*      */     } 
/*      */ 
/*      */     
/* 2113 */     if (rootInteraction == null) {
/* 2114 */       throw new IllegalArgumentException("No interactions are defined for " + String.valueOf(type) + ", " + String.valueOf(context));
/*      */     }
/*      */     
/* 2117 */     collector.start();
/* 2118 */     collector.into(context, null);
/* 2119 */     walkInteractions(collector, context, CollectorTag.ROOT, rootInteraction.getInteractionIds());
/* 2120 */     collector.outof();
/* 2121 */     collector.finished();
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
/* 2137 */     for (String id : interactionIds) {
/* 2138 */       if (walkInteraction(collector, context, tag, id)) return true; 
/*      */     } 
/* 2140 */     return false;
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
/* 2156 */     if (id == null) return false; 
/* 2157 */     Interaction interaction = (Interaction)Interaction.getAssetMap().getAsset(id);
/* 2158 */     if (interaction == null) throw new IllegalArgumentException("Failed to find interaction: " + id);
/*      */     
/* 2160 */     if (collector.collect(tag, context, interaction)) return true;
/*      */     
/* 2162 */     collector.into(context, interaction);
/* 2163 */     interaction.walk(collector, context);
/* 2164 */     collector.outof();
/* 2165 */     return false;
/*      */   }
/*      */   
/*      */   public ObjectList<SyncInteractionChain> getSyncPackets() {
/* 2169 */     return this.syncPackets;
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Component<EntityStore> clone() {
/* 2175 */     InteractionManager manager = new InteractionManager(this.entity, this.playerRef, this.interactionSimulationHandler);
/* 2176 */     manager.copyFrom(this);
/* 2177 */     return manager;
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
/* 2198 */       this.state = state;
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\InteractionManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */