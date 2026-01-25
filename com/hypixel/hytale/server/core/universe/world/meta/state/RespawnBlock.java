/*     */ package com.hypixel.hytale.server.core.universe.world.meta.state;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import java.util.UUID;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class RespawnBlock implements Component<ChunkStore> {
/*     */   public static ComponentType<ChunkStore, RespawnBlock> getComponentType() {
/*  27 */     return BlockModule.get().getRespawnBlockComponentType();
/*     */   }
/*     */ 
/*     */   
/*     */   public static final BuilderCodec<RespawnBlock> CODEC;
/*     */   
/*     */   private UUID ownerUUID;
/*     */   
/*     */   static {
/*  36 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(RespawnBlock.class, RespawnBlock::new).append(new KeyedCodec("OwnerUUID", (Codec)Codec.UUID_BINARY), (respawnBlockState, uuid) -> respawnBlockState.ownerUUID = uuid, respawnBlockState -> respawnBlockState.ownerUUID).add()).build();
/*     */   }
/*     */ 
/*     */   
/*     */   public RespawnBlock() {}
/*     */ 
/*     */   
/*     */   public RespawnBlock(UUID ownerUUID) {
/*  44 */     this.ownerUUID = ownerUUID;
/*     */   }
/*     */   
/*     */   public UUID getOwnerUUID() {
/*  48 */     return this.ownerUUID;
/*     */   }
/*     */   
/*     */   public void setOwnerUUID(UUID ownerUUID) {
/*  52 */     this.ownerUUID = ownerUUID;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Component<ChunkStore> clone() {
/*  58 */     return new RespawnBlock(this.ownerUUID);
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
/*     */   public static class OnRemove
/*     */     extends RefSystem<ChunkStore>
/*     */   {
/*     */     @Nonnull
/*  73 */     private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     public static final ComponentType<ChunkStore, RespawnBlock> COMPONENT_TYPE_RESPAWN_BLOCK = RespawnBlock.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     public static final ComponentType<ChunkStore, BlockModule.BlockStateInfo> COMPONENT_TYPE_BLOCK_STATE_INFO = BlockModule.BlockStateInfo.getComponentType();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*  89 */     public static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] { (Query)COMPONENT_TYPE_RESPAWN_BLOCK, (Query)COMPONENT_TYPE_BLOCK_STATE_INFO });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {
/* 103 */       if (reason == RemoveReason.UNLOAD)
/*     */         return; 
/* 105 */       RespawnBlock respawnState = (RespawnBlock)commandBuffer.getComponent(ref, COMPONENT_TYPE_RESPAWN_BLOCK);
/* 106 */       assert respawnState != null;
/* 107 */       if (respawnState.ownerUUID == null)
/*     */         return; 
/* 109 */       BlockModule.BlockStateInfo blockStateInfoComponent = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, COMPONENT_TYPE_BLOCK_STATE_INFO);
/* 110 */       assert blockStateInfoComponent != null;
/*     */       
/* 112 */       PlayerRef playerRef = Universe.get().getPlayer(respawnState.ownerUUID);
/* 113 */       if (playerRef == null) {
/*     */         
/* 115 */         LOGGER.at(Level.WARNING).log("Failed to fetch player ref during removal of respawn block entity.");
/*     */         
/*     */         return;
/*     */       } 
/* 119 */       Player playerComponent = (Player)playerRef.getComponent(Player.getComponentType());
/* 120 */       if (playerComponent == null) {
/* 121 */         LOGGER.at(Level.WARNING).log("Failed to fetch player component during removal of respawn block entity.");
/*     */         
/*     */         return;
/*     */       } 
/* 125 */       Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
/* 126 */       if (!chunkRef.isValid())
/*     */         return; 
/* 128 */       World world = ((ChunkStore)commandBuffer.getExternalData()).getWorld();
/* 129 */       PlayerWorldData playerWorldData = playerComponent.getPlayerConfigData().getPerWorldData(world.getName());
/* 130 */       PlayerRespawnPointData[] respawnPoints = playerWorldData.getRespawnPoints();
/* 131 */       if (respawnPoints == null) {
/* 132 */         LOGGER.at(Level.WARNING).log("Failed to find valid respawn points for player " + String.valueOf(respawnState.ownerUUID) + " during removal of respawn block entity.");
/*     */         
/*     */         return;
/*     */       } 
/* 136 */       WorldChunk worldChunkComponent = (WorldChunk)commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
/* 137 */       assert worldChunkComponent != null;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 142 */       Vector3i blockPosition = new Vector3i(ChunkUtil.worldCoordFromLocalCoord(worldChunkComponent.getX(), ChunkUtil.xFromBlockInColumn(blockStateInfoComponent.getIndex())), ChunkUtil.yFromBlockInColumn(blockStateInfoComponent.getIndex()), ChunkUtil.worldCoordFromLocalCoord(worldChunkComponent.getZ(), ChunkUtil.zFromBlockInColumn(blockStateInfoComponent.getIndex())));
/*     */ 
/*     */       
/* 145 */       for (int i = 0; i < respawnPoints.length; i++) {
/* 146 */         PlayerRespawnPointData respawnPoint = respawnPoints[i];
/* 147 */         if (respawnPoint.getBlockPosition().equals(blockPosition)) {
/* 148 */           LOGGER.at(Level.INFO).log("Removing respawn point for player " + String.valueOf(respawnState.ownerUUID) + " at position " + String.valueOf(blockPosition) + " due to respawn block removal.");
/* 149 */           playerWorldData.setRespawnPoints((PlayerRespawnPointData[])ArrayUtil.remove((Object[])respawnPoints, i));
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Query<ChunkStore> getQuery() {
/* 158 */       return QUERY;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\meta\state\RespawnBlock.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */