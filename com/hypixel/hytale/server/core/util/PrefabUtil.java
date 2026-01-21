/*     */ package com.hypixel.hytale.server.core.util;
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.system.EcsEvent;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabWeights;
/*     */ import com.hypixel.hytale.server.core.prefab.event.PrefabPasteEvent;
/*     */ import com.hypixel.hytale.server.core.prefab.event.PrefabPlaceEntityEvent;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
/*     */ import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.IntSet;
/*     */ import java.util.Random;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class PrefabUtil {
/*     */   protected static final String EDITOR_BLOCK = "Editor_Block";
/*     */   protected static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
/*     */   protected static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";
/*     */   
/*     */   public static boolean prefabMatchesAtPosition(@Nonnull IPrefabBuffer prefabBuffer, World world, @Nonnull Vector3i position, @Nonnull Rotation yaw, Random random) {
/*  45 */     double xLength = (prefabBuffer.getMaxX() - prefabBuffer.getMinX());
/*  46 */     double zLength = (prefabBuffer.getMaxZ() - prefabBuffer.getMinZ());
/*  47 */     int prefabRadius = (int)MathUtil.fastFloor(0.5D * Math.sqrt(xLength * xLength + zLength * zLength));
/*     */     
/*  49 */     LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords((ChunkAccessor)world, position.getX(), position.getZ(), prefabRadius);
/*     */     
/*  51 */     return prefabBuffer.compare((x, y, z, blockId, rotation, holder, prefabBufferCall) -> { int bx = position.x + x; int by = position.y + y; int bz = position.z + z; WorldChunk chunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz)); int blockIdAtPos = chunk.getBlock(bx, by, bz); return (blockIdAtPos == blockId); }new PrefabBufferCall(random, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  60 */           PrefabRotation.fromRotation(yaw)));
/*     */   }
/*     */   
/*     */   public static boolean canPlacePrefab(@Nonnull IPrefabBuffer prefabBuffer, World world, @Nonnull Vector3i position, @Nonnull Rotation yaw, @Nullable IntSet mask, Random random, boolean ignoreOrigin) {
/*  64 */     double xLength = (prefabBuffer.getMaxX() - prefabBuffer.getMinX());
/*  65 */     double zLength = (prefabBuffer.getMaxZ() - prefabBuffer.getMinZ());
/*  66 */     int prefabRadius = (int)MathUtil.fastFloor(0.5D * Math.sqrt(xLength * xLength + zLength * zLength));
/*     */     
/*  68 */     LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords((ChunkAccessor)world, position.getX(), position.getZ(), prefabRadius);
/*     */     
/*  70 */     return prefabBuffer.compare((x, y, z, blockId, rotation, holder, prefabBufferCall) -> { if (ignoreOrigin && x == 0 && y == 0 && z == 0) return true;  int bx = position.x + x; int by = position.y + y; int bz = position.z + z; WorldChunk chunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz)); return chunk.testPlaceBlock(bx, by, bz, (BlockType)BlockType.getAssetMap().getAsset(blockId), rotation, ()); }new PrefabBufferCall(random, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  80 */           PrefabRotation.fromRotation(yaw)));
/*     */   }
/*     */   
/*     */   public static void paste(@Nonnull IPrefabBuffer buffer, @Nonnull World world, @Nonnull Vector3i position, @Nonnull Rotation yaw, boolean force, Random random, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  84 */     paste(buffer, world, position, yaw, force, random, 0, componentAccessor);
/*     */   }
/*     */   
/*     */   public static void paste(@Nonnull IPrefabBuffer buffer, @Nonnull World world, @Nonnull Vector3i position, @Nonnull Rotation yaw, boolean force, Random random, int setBlockSettings, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  88 */     paste(buffer, world, position, yaw, force, random, setBlockSettings, false, false, false, componentAccessor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   private static final AtomicInteger PREFAB_ID_SOURCE = new AtomicInteger(0);
/*     */   
/*     */   public static int getNextPrefabId() {
/*  99 */     return PREFAB_ID_SOURCE.getAndIncrement();
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
/*     */   public static void paste(@Nonnull IPrefabBuffer buffer, @Nonnull World world, @Nonnull Vector3i position, @Nonnull Rotation yaw, boolean force, Random random, int setBlockSettings, boolean technicalPaste, boolean pasteAnchorAsBlock, boolean loadEntities, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 129 */     double xLength = (buffer.getMaxX() - buffer.getMinX());
/* 130 */     double zLength = (buffer.getMaxZ() - buffer.getMinZ());
/* 131 */     int prefabRadius = (int)MathUtil.fastFloor(0.5D * Math.sqrt(xLength * xLength + zLength * zLength));
/*     */     
/* 133 */     LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords((ChunkAccessor)world, position.getX(), position.getZ(), prefabRadius);
/* 134 */     BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
/*     */     
/* 136 */     int editorBlock = blockTypeMap.getIndex("Editor_Block");
/* 137 */     if (editorBlock == Integer.MIN_VALUE) throw new IllegalArgumentException("Unknown key! Editor_Block");
/*     */     
/* 139 */     PrefabRotation rotation = PrefabRotation.fromRotation(yaw);
/*     */     
/* 141 */     int prefabId = getNextPrefabId();
/*     */     
/* 143 */     PrefabPasteEvent startEvent = new PrefabPasteEvent(prefabId, true);
/* 144 */     componentAccessor.invoke((EcsEvent)startEvent);
/* 145 */     if (startEvent.isCancelled())
/*     */       return; 
/* 147 */     buffer.forEach(IPrefabBuffer.iterateAllColumns(), (x, y, z, blockId, holder, supportValue, blockRotation, filler, call, fluidId, fluidLevel) -> { BlockType block; int bx = position.x + x; int by = position.y + y; int bz = position.z + z; WorldChunk chunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz)); Store<ChunkStore> fluidStore = world.getChunkStore().getStore(); ChunkColumn fluidColumn = (ChunkColumn)fluidStore.getComponent(chunk.getReference(), ChunkColumn.getComponentType()); Ref<ChunkStore> section = fluidColumn.getSection(ChunkUtil.chunkCoordinate(by)); FluidSection fluidSection = (FluidSection)fluidStore.ensureAndGetComponent(section, FluidSection.getComponentType()); fluidSection.setFluid(bx, by, bz, fluidId, (byte)fluidLevel); if (technicalPaste) { if (blockId == 0 && fluidId == 0) { block = (BlockType)blockTypeMap.getAsset("Editor_Empty"); } else { block = (BlockType)blockTypeMap.getAsset(blockId); }  } else { block = (BlockType)blockTypeMap.getAsset(blockId); }  String blockKey = block.getId(); if (filler != 0) { if (holder != null) chunk.setState(bx, by, bz, holder.clone());  return; }  if (pasteAnchorAsBlock && technicalPaste && x == buffer.getAnchorX() && y == buffer.getAnchorY() && z == buffer.getAnchorZ()) { int index = blockTypeMap.getIndex("Editor_Anchor"); BlockType type = (BlockType)blockTypeMap.getAsset(index); chunk.setBlock(bx, by, bz, index, type, blockRotation, filler, setBlockSettings); } else if (!force) { RotationTuple rot = RotationTuple.get(blockRotation); chunk.placeBlock(bx, by, bz, blockKey, rot.yaw(), rot.pitch(), rot.roll(), setBlockSettings); } else { int index = blockTypeMap.getIndex(blockKey); BlockType type = (BlockType)blockTypeMap.getAsset(index); chunk.setBlock(bx, by, bz, index, type, blockRotation, filler, setBlockSettings); }  if (supportValue != 0) if (!world.isInThread()) { CompletableFutureUtil._catch(CompletableFuture.runAsync((), (Executor)world)); } else { Ref<ChunkStore> ref = chunk.getReference(); Store<ChunkStore> store = ref.getStore(); ChunkColumn column = (ChunkColumn)store.getComponent(ref, ChunkColumn.getComponentType()); BlockPhysics.setSupportValue(store, column.getSection(ChunkUtil.chunkCoordinate(by)), bx, by, bz, supportValue); }   if (holder != null) chunk.setState(bx, by, bz, holder.clone());  }(x, z, entityWrappers, t) -> { if (!loadEntities) return;  if (entityWrappers == null || entityWrappers.length == 0) return;  for (int i = 0; i < entityWrappers.length; i++) { Holder<EntityStore> entityToAdd = entityWrappers[i].clone(); TransformComponent transformComp = (TransformComponent)entityToAdd.getComponent(TransformComponent.getComponentType()); if (transformComp != null) { Vector3d entityPosition = transformComp.getPosition().clone(); rotation.rotate(entityPosition); Vector3d entityWorldPosition = entityPosition.add(position); transformComp = (TransformComponent)entityToAdd.getComponent(TransformComponent.getComponentType()); if (transformComp != null) { entityPosition = transformComp.getPosition(); entityPosition.x = entityWorldPosition.x; entityPosition.y = entityWorldPosition.y; entityPosition.z = entityWorldPosition.z; PrefabPlaceEntityEvent prefabPlaceEntityEvent = new PrefabPlaceEntityEvent(prefabId, entityToAdd); componentAccessor.invoke((EcsEvent)prefabPlaceEntityEvent); entityToAdd.ensureComponent(FromPrefab.getComponentType()); if (technicalPaste) entityToAdd.ensureComponent(PrefabCopyableComponent.getComponentType());  componentAccessor.addEntity(entityToAdd, AddReason.LOAD); }  }  }  }(x, y, z, path, fitHeightmap, inheritSeed, inheritHeightCondition, weights, rot, t) -> {  }new PrefabBufferCall(random, rotation));
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
/* 253 */     PrefabPasteEvent endEvent = new PrefabPasteEvent(prefabId, false);
/* 254 */     componentAccessor.invoke((EcsEvent)endEvent);
/*     */   }
/*     */   
/*     */   public static void remove(@Nonnull IPrefabBuffer prefabBuffer, @Nonnull World world, @Nonnull Vector3i position, boolean force, @Nonnull Random random, int setBlockSettings) {
/* 258 */     remove(prefabBuffer, world, position, force, random, setBlockSettings, 1.0D);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void remove(@Nonnull IPrefabBuffer prefabBuffer, @Nonnull World world, @Nonnull Vector3i position, boolean force, @Nonnull Random random, int setBlockSettings, double brokenParticlesRate) {}
/*     */ 
/*     */   
/*     */   public static void remove(@Nonnull IPrefabBuffer prefabBuffer, @Nonnull World world, @Nonnull Vector3i position, Rotation prefabRotation, boolean force, @Nonnull Random random, int setBlockSettings, double brokenParticlesRate) {
/* 266 */     double xLength = (prefabBuffer.getMaxX() - prefabBuffer.getMinX());
/* 267 */     double zLength = (prefabBuffer.getMaxZ() - prefabBuffer.getMinZ());
/* 268 */     int prefabRadius = (int)MathUtil.fastFloor(0.5D * Math.sqrt(xLength * xLength + zLength * zLength));
/*     */     
/* 270 */     LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords((ChunkAccessor)world, position.getX(), position.getZ(), prefabRadius);
/* 271 */     BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
/*     */     
/* 273 */     prefabBuffer.forEach(IPrefabBuffer.iterateAllColumns(), (x, y, z, blockId, state, support, rotation, filler, call, fluidId, fluidLevel) -> { int bx = position.x + x; int by = position.y + y; int bz = position.z + z; WorldChunk chunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz)); Store<ChunkStore> store = world.getChunkStore().getStore(); if (fluidId != 0) { ChunkColumn column = (ChunkColumn)store.getComponent(chunk.getReference(), ChunkColumn.getComponentType()); Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(by)); FluidSection fluidSection = (FluidSection)store.ensureAndGetComponent(section, FluidSection.getComponentType()); fluidSection.setFluid(bx, by, bz, 0, (byte)0); }  if (blockId == 0) return;  if (filler != 0) return;  int updatedSetBlockSettings = setBlockSettings; if ((setBlockSettings & 0x4) != 4 && random.nextDouble() > brokenParticlesRate) updatedSetBlockSettings |= 0x4;  if (!force) { chunk.breakBlock(bx, by, bz, updatedSetBlockSettings); } else { chunk.setBlock(bx, by, bz, "Empty", updatedSetBlockSettings); }  }(x, z, entityWrappers, t) -> {  }(x, y, z, path, fitHeightmap, inheritSeed, inheritHeightCondition, weights, rotation, t) -> {  }new PrefabBufferCall(random, 
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
/* 310 */           PrefabRotation.fromRotation(prefabRotation)));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\util\PrefabUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */