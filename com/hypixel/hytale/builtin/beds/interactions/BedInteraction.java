/*     */ package com.hypixel.hytale.builtin.beds.interactions;
/*     */ import com.hypixel.hytale.builtin.beds.respawn.SelectOverrideRespawnPointPage;
/*     */ import com.hypixel.hytale.builtin.beds.respawn.SetNameRespawnPointPage;
/*     */ import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
/*     */ import com.hypixel.hytale.builtin.mounts.BlockMountAPI;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.RespawnConfig;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.state.RespawnBlock;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class BedInteraction extends SimpleBlockInteraction {
/*     */   @Nonnull
/*  42 */   private static final Message MESSAGE_SERVER_CUSTOM_UI_RESPAWN_POINT_CLAIMED = Message.translation("server.customUI.respawnPointClaimed");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  48 */   public static final BuilderCodec<BedInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BedInteraction.class, BedInteraction::new, SimpleBlockInteraction.CODEC)
/*  49 */     .documentation("Interact with a bed block, ostensibly to sleep in it."))
/*  50 */     .build();
/*     */ 
/*     */   
/*     */   protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i pos, @Nonnull CooldownHandler cooldownHandler) {
/*  54 */     Ref<EntityStore> ref = context.getEntity();
/*     */     
/*  56 */     Player player = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/*  57 */     if (player == null)
/*     */       return; 
/*  59 */     Store<EntityStore> store = commandBuffer.getStore();
/*     */     
/*  61 */     PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
/*  62 */     assert playerRefComponent != null;
/*     */     
/*  64 */     UUIDComponent playerUuidComponent = (UUIDComponent)commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
/*  65 */     assert playerUuidComponent != null;
/*     */     
/*  67 */     UUID playerUuid = playerUuidComponent.getUuid();
/*     */     
/*  69 */     Ref<ChunkStore> chunkReference = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
/*  70 */     if (chunkReference == null)
/*     */       return; 
/*  72 */     Store<ChunkStore> chunkStore = chunkReference.getStore();
/*  73 */     BlockComponentChunk blockComponentChunk = (BlockComponentChunk)chunkStore.getComponent(chunkReference, BlockComponentChunk.getComponentType());
/*  74 */     assert blockComponentChunk != null;
/*     */     
/*  76 */     int blockIndex = ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z);
/*  77 */     Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
/*  78 */     if (blockRef == null) {
/*  79 */       Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
/*  80 */       holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), (Component)new BlockModule.BlockStateInfo(blockIndex, chunkReference));
/*  81 */       holder.ensureComponent(RespawnBlock.getComponentType());
/*  82 */       blockRef = chunkStore.addEntity(holder, AddReason.SPAWN);
/*     */     } 
/*     */     
/*  85 */     RespawnBlock respawnBlockComponent = (RespawnBlock)chunkStore.getComponent(blockRef, RespawnBlock.getComponentType());
/*  86 */     if (respawnBlockComponent == null)
/*     */       return; 
/*  88 */     UUID ownerUUID = respawnBlockComponent.getOwnerUUID();
/*  89 */     PageManager pageManager = player.getPageManager();
/*     */     
/*  91 */     boolean isOwner = playerUuid.equals(ownerUUID);
/*  92 */     if (isOwner) {
/*  93 */       BlockPosition rawTarget = (BlockPosition)context.getMetaStore().getMetaObject(TARGET_BLOCK_RAW);
/*  94 */       Vector3f whereWasHit = new Vector3f(rawTarget.x + 0.5F, rawTarget.y + 0.5F, rawTarget.z + 0.5F);
/*  95 */       BlockMountAPI.BlockMountResult result = BlockMountAPI.mountOnBlock(ref, commandBuffer, pos, whereWasHit);
/*  96 */       if (result instanceof BlockMountAPI.DidNotMount) {
/*  97 */         player.sendMessage(Message.translation("server.interactions.didNotMount")
/*  98 */             .param("state", result.toString()));
/*  99 */       } else if (result instanceof BlockMountAPI.Mounted) {
/* 100 */         commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), (Component)PlayerSleep.NoddingOff.createComponent());
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 105 */     if (ownerUUID != null) {
/*     */       
/* 107 */       player.sendMessage(MESSAGE_SERVER_CUSTOM_UI_RESPAWN_POINT_CLAIMED);
/*     */       
/*     */       return;
/*     */     } 
/* 111 */     PlayerRespawnPointData[] respawnPoints = player.getPlayerConfigData().getPerWorldData(world.getName()).getRespawnPoints();
/* 112 */     RespawnConfig respawnConfig = world.getGameplayConfig().getRespawnConfig();
/*     */     
/* 114 */     int radiusLimitRespawnPoint = respawnConfig.getRadiusLimitRespawnPoint();
/* 115 */     PlayerRespawnPointData[] nearbyRespawnPoints = getNearbySavedRespawnPoints(pos, respawnBlockComponent, respawnPoints, radiusLimitRespawnPoint);
/* 116 */     if (nearbyRespawnPoints != null) {
/* 117 */       pageManager.openCustomPage(ref, store, (CustomUIPage)new OverrideNearbyRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent, nearbyRespawnPoints, radiusLimitRespawnPoint));
/*     */       
/*     */       return;
/*     */     } 
/* 121 */     if (respawnPoints != null && respawnPoints.length >= respawnConfig.getMaxRespawnPointsPerPlayer()) {
/* 122 */       pageManager.openCustomPage(ref, store, (CustomUIPage)new SelectOverrideRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent, respawnPoints));
/*     */       
/*     */       return;
/*     */     } 
/* 126 */     pageManager.openCustomPage(ref, store, (CustomUIPage)new SetNameRespawnPointPage(playerRefComponent, type, pos, respawnBlockComponent));
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
/*     */   protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PlayerRespawnPointData[] getNearbySavedRespawnPoints(@Nonnull Vector3i currentRespawnPointPosition, @Nonnull RespawnBlock respawnBlock, @Nullable PlayerRespawnPointData[] respawnPoints, int radiusLimitRespawnPoint) {
/* 148 */     if (respawnPoints == null || respawnPoints.length == 0) return null;
/*     */     
/* 150 */     ObjectArrayList<PlayerRespawnPointData> nearbyRespawnPointList = new ObjectArrayList();
/*     */     
/* 152 */     for (int i = 0; i < respawnPoints.length; i++) {
/* 153 */       PlayerRespawnPointData respawnPoint = respawnPoints[i];
/* 154 */       Vector3i respawnPointPosition = respawnPoint.getBlockPosition();
/*     */ 
/*     */       
/* 157 */       if (respawnPointPosition.distanceTo(currentRespawnPointPosition.x, respawnPointPosition.y, currentRespawnPointPosition.z) < radiusLimitRespawnPoint) {
/* 158 */         nearbyRespawnPointList.add(respawnPoint);
/*     */       }
/*     */     } 
/*     */     
/* 162 */     return nearbyRespawnPointList.isEmpty() ? null : (PlayerRespawnPointData[])nearbyRespawnPointList.toArray(x$0 -> new PlayerRespawnPointData[x$0]);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 168 */     return "BedInteraction{} " + super.toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\beds\interactions\BedInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */