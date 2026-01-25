/*     */ package com.hypixel.hytale.builtin.blockspawner;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.blockspawner.state.BlockSpawner;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.math.Axis;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.HashUtil;
/*     */ import com.hypixel.hytale.protocol.GameMode;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.VariantRotation;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.WorldConfig;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockRotationUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
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
/*     */ class BlockSpawnerSystem
/*     */   extends RefSystem<ChunkStore>
/*     */ {
/* 108 */   private static final ComponentType<ChunkStore, BlockSpawner> COMPONENT_TYPE = BlockSpawner.getComponentType();
/* 109 */   private static final ComponentType<ChunkStore, BlockModule.BlockStateInfo> BLOCK_INFO_COMPONENT_TYPE = BlockModule.BlockStateInfo.getComponentType();
/* 110 */   private static final Query<ChunkStore> QUERY = (Query<ChunkStore>)Query.and(new Query[] { (Query)COMPONENT_TYPE, (Query)BLOCK_INFO_COMPONENT_TYPE });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Query<ChunkStore> getQuery() {
/* 117 */     return QUERY; } public void onEntityAdded(@Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) { String key;
/*     */     VariantRotation variantRotation;
/*     */     int randomHash;
/*     */     RotationTuple spawnerRotation;
/*     */     Rotation rotationYaw, spawnerYaw;
/* 122 */     WorldConfig worldConfig = ((ChunkStore)store.getExternalData()).getWorld().getWorldConfig();
/* 123 */     if (worldConfig.getGameMode() == GameMode.Creative)
/*     */       return; 
/* 125 */     BlockSpawner state = (BlockSpawner)commandBuffer.getComponent(ref, COMPONENT_TYPE);
/* 126 */     assert state != null;
/*     */     
/* 128 */     BlockModule.BlockStateInfo info = (BlockModule.BlockStateInfo)commandBuffer.getComponent(ref, BLOCK_INFO_COMPONENT_TYPE);
/* 129 */     assert info != null;
/*     */     
/* 131 */     String blockSpawnerId = state.getBlockSpawnerId();
/* 132 */     if (blockSpawnerId == null)
/*     */       return; 
/* 134 */     BlockSpawnerTable table = (BlockSpawnerTable)BlockSpawnerTable.getAssetMap().getAsset(blockSpawnerId);
/* 135 */     if (table == null) {
/* 136 */       BlockSpawnerPlugin.LOGGER.at(Level.WARNING).log("Failed to find BlockSpawner Asset by name: %s", blockSpawnerId);
/*     */       
/*     */       return;
/*     */     } 
/* 140 */     Ref<ChunkStore> chunk = info.getChunkRef();
/* 141 */     if (chunk == null)
/* 142 */       return;  WorldChunk wc = (WorldChunk)commandBuffer.getComponent(chunk, WorldChunk.getComponentType());
/*     */     
/* 144 */     int x = ChunkUtil.worldCoordFromLocalCoord(wc.getX(), ChunkUtil.xFromBlockInColumn(info.getIndex()));
/* 145 */     int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
/* 146 */     int z = ChunkUtil.worldCoordFromLocalCoord(wc.getZ(), ChunkUtil.zFromBlockInColumn(info.getIndex()));
/*     */ 
/*     */     
/* 149 */     long seed = worldConfig.getSeed();
/* 150 */     double randomRnd = HashUtil.random(x, y, z, seed + -1699164769L);
/*     */     
/* 152 */     BlockSpawnerEntry entry = (BlockSpawnerEntry)table.getEntries().get(randomRnd);
/* 153 */     if (entry == null)
/*     */       return; 
/* 155 */     String blockKey = entry.getBlockName();
/* 156 */     switch (BlockSpawnerPlugin.null.$SwitchMap$com$hypixel$hytale$builtin$blockspawner$BlockSpawnerEntry$RotationMode[entry.getRotationMode().ordinal()]) { default: throw new MatchException(null, null);
/*     */       case 1: 
/*     */       case 2:
/* 159 */         key = entry.getBlockName();
/* 160 */         variantRotation = ((BlockType)BlockType.getAssetMap().getAsset(key)).getVariantRotation();
/* 161 */         if (variantRotation == VariantRotation.None);
/*     */ 
/*     */ 
/*     */         
/* 165 */         randomHash = (int)HashUtil.rehash(x, y, z, seed + -1699164769L);
/* 166 */         rotationYaw = Rotation.NORMAL[(randomHash & 0xFFFF) % Rotation.NORMAL.length];
/*     */ 
/*     */       
/*     */       case 3:
/* 170 */         key = entry.getBlockName();
/* 171 */         variantRotation = ((BlockType)BlockType.getAssetMap().getAsset(key)).getVariantRotation();
/* 172 */         if (variantRotation == VariantRotation.None);
/*     */ 
/*     */ 
/*     */         
/* 176 */         spawnerRotation = RotationTuple.get(wc.getRotationIndex(x, y, z));
/* 177 */         spawnerYaw = spawnerRotation.yaw(); }
/* 178 */      RotationTuple rotation = BlockRotationUtil.getRotated(RotationTuple.NONE, Axis.Y, spawnerYaw, variantRotation);
/*     */ 
/*     */ 
/*     */     
/* 182 */     Holder<ChunkStore> holder = entry.getBlockComponents();
/*     */ 
/*     */ 
/*     */     
/* 186 */     commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
/* 187 */     commandBuffer.run(_store -> {
/*     */           int flags = 4;
/*     */           if (holder != null)
/*     */             flags |= 0x2; 
/*     */           int blockId = BlockType.getAssetMap().getIndex(blockKey);
/*     */           BlockType blockType = (BlockType)BlockType.getAssetMap().getAsset(blockId);
/*     */           wc.setBlock(x, y, z, blockId, blockType, rotation.index(), 0, flags);
/*     */           if (holder != null)
/*     */             wc.setState(x, y, z, holder.clone()); 
/*     */         }); }
/*     */ 
/*     */   
/*     */   public void onEntityRemove(@Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer) {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\blockspawner\BlockSpawnerPlugin$BlockSpawnerSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */