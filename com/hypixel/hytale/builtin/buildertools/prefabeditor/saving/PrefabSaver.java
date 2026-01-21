/*     */ package com.hypixel.hytale.builtin.buildertools.prefabeditor.saving;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
/*     */ import com.hypixel.hytale.component.ComponentRegistry;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.math.vector.VectorBoxUtil;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.BlockEntity;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabSaveException;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabStore;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.EntityChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.longs.LongIterator;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.nio.file.FileSystems;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrefabSaver
/*     */ {
/*     */   protected static final String EDITOR_BLOCK = "Editor_Block";
/*     */   protected static final String EDITOR_BLOCK_PREFAB_AIR = "Editor_Empty";
/*     */   protected static final String EDITOR_BLOCK_PREFAB_ANCHOR = "Editor_Anchor";
/*     */   
/*     */   @Nonnull
/*     */   public static CompletableFuture<Boolean> savePrefab(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Path pathToSave, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings) {
/*  70 */     return copyBlocksAsync(sender, world, anchorPoint, minPoint, maxPoint, pastePosition, originalFileAnchor, settings)
/*  71 */       .thenApplyAsync(blockSelection -> (blockSelection == null) ? Boolean.valueOf(false) : Boolean.valueOf(save(sender, blockSelection, pathToSave, settings)), (Executor)world);
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
/*     */   @Nonnull
/*     */   private static CompletableFuture<BlockSelection> copyBlocksAsync(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings) {
/*  90 */     ChunkStore chunkStore = world.getChunkStore();
/*     */     
/*  92 */     BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/*     */     
/*  94 */     int editorBlock = assetMap.getIndex("Editor_Block");
/*  95 */     if (editorBlock == Integer.MIN_VALUE) {
/*  96 */       sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey")
/*  97 */           .param("key", "Editor_Block".toString()));
/*  98 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/* 101 */     int editorBlockPrefabAir = assetMap.getIndex("Editor_Empty");
/* 102 */     if (editorBlockPrefabAir == Integer.MIN_VALUE) {
/* 103 */       sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey")
/* 104 */           .param("key", "Editor_Empty".toString()));
/* 105 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/* 108 */     int editorBlockPrefabAnchor = assetMap.getIndex("Editor_Anchor");
/* 109 */     if (editorBlockPrefabAnchor == Integer.MIN_VALUE) {
/* 110 */       sender.sendMessage(Message.translation("server.commands.editprefab.save.error.unknownBlockIdKey")
/* 111 */           .param("key", "Editor_Anchor".toString()));
/* 112 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */ 
/*     */     
/* 116 */     return preloadChunksInSelectionAsync(chunkStore, minPoint, maxPoint)
/* 117 */       .thenApplyAsync(loadedChunks -> copyBlocksWithLoadedChunks(sender, world, anchorPoint, minPoint, maxPoint, pastePosition, originalFileAnchor, settings, loadedChunks, editorBlock, editorBlockPrefabAir, editorBlockPrefabAnchor), (Executor)world);
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
/*     */   @Nullable
/*     */   private static BlockSelection copyBlocksWithLoadedChunks(@Nonnull CommandSender sender, @Nonnull World world, @Nonnull Vector3i anchorPoint, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint, @Nonnull Vector3i pastePosition, @Nonnull Vector3i originalFileAnchor, @Nonnull PrefabSaverSettings settings, @Nonnull Long2ObjectMap<Ref<ChunkStore>> loadedChunks, int editorBlock, int editorBlockPrefabAir, int editorBlockPrefabAnchor) {
/* 139 */     ChunkStore chunkStore = world.getChunkStore();
/*     */     
/* 141 */     long start = System.nanoTime();
/*     */     
/* 143 */     int width = maxPoint.x - minPoint.x;
/* 144 */     int height = maxPoint.y - minPoint.y;
/* 145 */     int depth = maxPoint.z - minPoint.z;
/*     */ 
/*     */     
/* 148 */     int newAnchorX = anchorPoint.x - pastePosition.x;
/* 149 */     int newAnchorY = anchorPoint.y - pastePosition.y;
/* 150 */     int newAnchorZ = anchorPoint.z - pastePosition.z;
/*     */     
/* 152 */     BlockSelection selection = new BlockSelection();
/*     */ 
/*     */     
/* 155 */     selection.setPosition(pastePosition.x - originalFileAnchor.x, pastePosition.y - originalFileAnchor.y, pastePosition.z - originalFileAnchor.z);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     selection.setSelectionArea(minPoint, maxPoint);
/*     */     
/* 162 */     selection.setAnchor(newAnchorX, newAnchorY, newAnchorZ);
/*     */     
/* 164 */     int blockCount = 0;
/* 165 */     int fluidCount = 0;
/*     */     
/* 167 */     int top = Math.max(minPoint.y, maxPoint.y);
/* 168 */     int bottom = Math.min(minPoint.y, maxPoint.y);
/*     */     
/* 170 */     for (int x = minPoint.x; x <= maxPoint.x; x++) {
/* 171 */       for (int z = minPoint.z; z <= maxPoint.z; z++) {
/* 172 */         long chunkIndex = ChunkUtil.indexChunkFromBlock(x, z);
/* 173 */         Ref<ChunkStore> chunkRef = (Ref<ChunkStore>)loadedChunks.get(chunkIndex);
/* 174 */         if (chunkRef != null && chunkRef.isValid()) {
/*     */           
/* 176 */           WorldChunk worldChunkComponent = (WorldChunk)chunkStore.getStore().getComponent(chunkRef, WorldChunk.getComponentType());
/* 177 */           assert worldChunkComponent != null;
/*     */           
/* 179 */           ChunkColumn chunkColumnComponent = (ChunkColumn)chunkStore.getStore().getComponent(chunkRef, ChunkColumn.getComponentType());
/* 180 */           assert chunkColumnComponent != null;
/*     */           
/* 182 */           for (int y = top; y >= bottom; y--) {
/* 183 */             int sectionIndex = ChunkUtil.indexSection(y);
/* 184 */             Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(sectionIndex);
/* 185 */             if (sectionRef != null && sectionRef.isValid()) {
/*     */               
/* 187 */               BlockSection sectionComponent = (BlockSection)chunkStore.getStore().getComponent(sectionRef, BlockSection.getComponentType());
/* 188 */               assert sectionComponent != null;
/*     */               
/* 190 */               BlockPhysics blockPhysicsComponent = (BlockPhysics)chunkStore.getStore().getComponent(sectionRef, BlockPhysics.getComponentType());
/*     */               
/* 192 */               int block = sectionComponent.get(x, y, z);
/* 193 */               int filler = sectionComponent.getFiller(x, y, z);
/* 194 */               if (settings.isBlocks() && (block != 0 || settings.isEmpty()) && block != editorBlock) {
/* 195 */                 if (block == editorBlockPrefabAir) {
/* 196 */                   block = 0;
/*     */                 }
/*     */ 
/*     */                 
/* 200 */                 Holder<ChunkStore> holder = worldChunkComponent.getBlockComponentHolder(x, y, z);
/* 201 */                 if (holder != null) {
/* 202 */                   holder = holder.clone();
/*     */ 
/*     */                   
/* 205 */                   BlockState blockState = BlockState.getBlockState(holder);
/* 206 */                   if (blockState != null) {
/* 207 */                     blockState.clearPositionForSerialization();
/*     */                   }
/*     */                 } 
/*     */                 
/* 211 */                 selection.addBlockAtWorldPos(x, y, z, block, sectionComponent
/*     */                     
/* 213 */                     .getRotationIndex(x, y, z), filler, 
/*     */                     
/* 215 */                     (blockPhysicsComponent != null) ? blockPhysicsComponent.get(x, y, z) : 0, holder);
/*     */ 
/*     */                 
/* 218 */                 blockCount++;
/*     */               } 
/*     */               
/* 221 */               FluidSection fluidSectionComponent = (FluidSection)chunkStore.getStore().getComponent(sectionRef, FluidSection.getComponentType());
/* 222 */               assert fluidSectionComponent != null;
/*     */               
/* 224 */               int fluid = fluidSectionComponent.getFluidId(x, y, z);
/* 225 */               if (settings.isBlocks() && (fluid != 0 || settings.isEmpty())) {
/* 226 */                 byte fluidLevel = fluidSectionComponent.getFluidLevel(x, y, z);
/* 227 */                 selection.addFluidAtWorldPos(x, y, z, fluid, fluidLevel);
/* 228 */                 fluidCount++;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 234 */     }  if (settings.isEntities()) {
/* 235 */       Store<EntityStore> store = world.getEntityStore().getStore();
/* 236 */       ComponentType<EntityStore, BlockEntity> blockEntityType = BlockEntity.getComponentType();
/* 237 */       Set<UUID> addedEntityUuids = new HashSet<>();
/* 238 */       ComponentRegistry.Data<EntityStore> data = EntityStore.REGISTRY.getData();
/* 239 */       ComponentType<EntityStore, PrefabCopyableComponent> prefabCopyableType = PrefabCopyableComponent.getComponentType();
/* 240 */       ComponentType<EntityStore, TransformComponent> transformType = TransformComponent.getComponentType();
/*     */ 
/*     */       
/* 243 */       BuilderToolsPlugin.forEachCopyableInSelection(world, minPoint.x, minPoint.y, minPoint.z, width, height, depth, e -> {
/*     */             BlockEntity blockEntity = (BlockEntity)store.getComponent(e, blockEntityType);
/*     */ 
/*     */             
/*     */             if (blockEntity != null) {
/*     */               String key = blockEntity.getBlockTypeKey();
/*     */               
/*     */               if (key != null && (key.equals("Editor_Block") || key.equals("Editor_Empty") || key.equals("Editor_Anchor"))) {
/*     */                 return;
/*     */               }
/*     */             } 
/*     */             
/*     */             Holder<EntityStore> holder = store.copyEntity(e);
/*     */             
/*     */             UUIDComponent uuidComp = (UUIDComponent)holder.getComponent(UUIDComponent.getComponentType());
/*     */             
/*     */             if (uuidComp != null) {
/*     */               addedEntityUuids.add(uuidComp.getUuid());
/*     */             }
/*     */             
/*     */             TransformComponent transform = (TransformComponent)holder.getComponent(transformType);
/*     */             
/*     */             if (transform != null && transform.getPosition() != null) {
/*     */               transform.getPosition().subtract(selection.getX(), selection.getY(), selection.getZ());
/*     */             }
/*     */             
/*     */             selection.addEntityHolderRaw(holder);
/*     */           });
/*     */       
/* 272 */       for (ObjectIterator<Ref<ChunkStore>> objectIterator = loadedChunks.values().iterator(); objectIterator.hasNext(); ) { Ref<ChunkStore> chunkRef = objectIterator.next();
/* 273 */         EntityChunk entityChunk = (EntityChunk)chunkStore.getStore().getComponent(chunkRef, EntityChunk.getComponentType());
/* 274 */         if (entityChunk == null)
/*     */           continue; 
/* 276 */         List<Holder<EntityStore>> holders = entityChunk.getEntityHolders();
/*     */         
/* 278 */         for (Holder<EntityStore> holder : holders) {
/* 279 */           UUIDComponent uuidComp = (UUIDComponent)holder.getComponent(UUIDComponent.getComponentType());
/* 280 */           TransformComponent transform = (TransformComponent)holder.getComponent(transformType);
/* 281 */           Vector3d position = (transform != null) ? transform.getPosition() : null;
/* 282 */           boolean hasPrefabCopyable = holder.getArchetype().contains(prefabCopyableType);
/* 283 */           boolean hasSerializable = holder.hasSerializableComponents(data);
/*     */ 
/*     */           
/* 286 */           if (!hasPrefabCopyable)
/*     */             continue; 
/* 288 */           if (!hasSerializable)
/*     */             continue; 
/* 290 */           BlockEntity blockEntity = (BlockEntity)holder.getComponent(blockEntityType);
/* 291 */           if (blockEntity != null) {
/* 292 */             String key = blockEntity.getBlockTypeKey();
/* 293 */             if (key != null && (key.equals("Editor_Block") || key
/* 294 */               .equals("Editor_Empty") || key
/* 295 */               .equals("Editor_Anchor"))) {
/*     */               continue;
/*     */             }
/*     */           } 
/*     */           
/* 300 */           if (transform == null || position == null)
/*     */             continue; 
/* 302 */           if (!VectorBoxUtil.isInside(minPoint.x, minPoint.y, minPoint.z, 0.0D, 0.0D, 0.0D, (width + 1), (height + 1), (depth + 1), position)) {
/*     */             continue;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/* 308 */           if (uuidComp != null && addedEntityUuids.contains(uuidComp.getUuid()))
/*     */             continue; 
/* 310 */           if (uuidComp != null) addedEntityUuids.add(uuidComp.getUuid());
/*     */ 
/*     */           
/* 313 */           Holder<EntityStore> clonedHolder = holder.clone();
/* 314 */           TransformComponent clonedTransform = (TransformComponent)clonedHolder.getComponent(transformType);
/* 315 */           if (clonedTransform != null && clonedTransform.getPosition() != null) {
/* 316 */             clonedTransform.getPosition().subtract(selection.getX(), selection.getY(), selection.getZ());
/*     */           }
/* 318 */           selection.addEntityHolderRaw(clonedHolder);
/*     */         }  }
/*     */ 
/*     */       
/* 322 */       selection.sortEntitiesByPosition();
/*     */     } 
/*     */     
/* 325 */     long end = System.nanoTime();
/* 326 */     long diff = end - start;
/* 327 */     BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute copy of %d blocks, %d fluids", Long.valueOf(diff), Long.valueOf(TimeUnit.NANOSECONDS.toMillis(diff)), Integer.valueOf(blockCount), Integer.valueOf(fluidCount));
/* 328 */     return selection;
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
/*     */   @Nonnull
/*     */   private static CompletableFuture<Long2ObjectMap<Ref<ChunkStore>>> preloadChunksInSelectionAsync(@Nonnull ChunkStore chunkStore, @Nonnull Vector3i minPoint, @Nonnull Vector3i maxPoint) {
/* 346 */     LongOpenHashSet longOpenHashSet = new LongOpenHashSet();
/* 347 */     int minChunkX = minPoint.x >> 5;
/* 348 */     int maxChunkX = maxPoint.x >> 5;
/* 349 */     int minChunkZ = minPoint.z >> 5;
/* 350 */     int maxChunkZ = maxPoint.z >> 5;
/*     */     
/* 352 */     for (int cx = minChunkX; cx <= maxChunkX; cx++) {
/* 353 */       for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
/* 354 */         longOpenHashSet.add(ChunkUtil.indexChunk(cx, cz));
/*     */       }
/*     */     } 
/*     */     
/* 358 */     Long2ObjectOpenHashMap long2ObjectOpenHashMap = new Long2ObjectOpenHashMap(longOpenHashSet.size());
/* 359 */     List<CompletableFuture<Void>> chunkFutures = new ArrayList<>(longOpenHashSet.size());
/*     */ 
/*     */     
/* 362 */     for (LongIterator<Long> longIterator = longOpenHashSet.iterator(); longIterator.hasNext(); ) { long chunkIndex = ((Long)longIterator.next()).longValue();
/*     */       
/* 364 */       CompletableFuture<Void> future = chunkStore.getChunkReferenceAsync(chunkIndex).thenAccept(reference -> {
/*     */             if (reference != null && reference.isValid()) {
/*     */               synchronized (loadedChunks) {
/*     */                 loadedChunks.put(chunkIndex, reference);
/*     */               } 
/*     */             }
/*     */           });
/* 371 */       chunkFutures.add(future); }
/*     */ 
/*     */ 
/*     */     
/* 375 */     return CompletableFuture.allOf((CompletableFuture<?>[])chunkFutures.toArray(x$0 -> new CompletableFuture[x$0]))
/* 376 */       .thenApply(v -> loadedChunks);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean save(@Nonnull CommandSender sender, @Nonnull BlockSelection copiedSelection, @Nonnull Path saveFilePath, @Nonnull PrefabSaverSettings settings) {
/* 384 */     if (saveFilePath.getFileSystem() != FileSystems.getDefault()) {
/* 385 */       sender.sendMessage(Message.translation("server.builderTools.cannotSaveToReadOnlyPath").param("path", saveFilePath.toString()));
/* 386 */       return false;
/*     */     } 
/*     */     
/*     */     try {
/* 390 */       long start = System.nanoTime();
/*     */       
/* 392 */       BlockSelection postClone = settings.isRelativize() ? copiedSelection.relativize() : copiedSelection.cloneSelection();
/* 393 */       PrefabStore.get().savePrefab(saveFilePath, postClone, settings.isOverwriteExisting());
/*     */       
/* 395 */       long diff = System.nanoTime() - start;
/* 396 */       BuilderToolsPlugin.get().getLogger().at(Level.FINE).log("Took: %dns (%dms) to execute save of %d blocks", Long.valueOf(diff), Long.valueOf(TimeUnit.NANOSECONDS.toMillis(diff)), Integer.valueOf(copiedSelection.getBlockCount()));
/* 397 */       return true;
/* 398 */     } catch (PrefabSaveException e) {
/* 399 */       switch (e.getType()) {
/*     */         case ERROR:
/* 401 */           ((HytaleLogger.Api)BuilderToolsPlugin.get().getLogger().at(Level.WARNING).withCause((Throwable)e)).log("Exception saving prefab %s", saveFilePath);
/* 402 */           sender.sendMessage(Message.translation("server.builderTools.errorSavingPrefab").param("name", saveFilePath.toString()).param("message", e.getCause().getMessage()));
/*     */           break;
/*     */         case ALREADY_EXISTS:
/* 405 */           BuilderToolsPlugin.get().getLogger().at(Level.WARNING).log("Prefab already exists %s", saveFilePath.toString());
/* 406 */           sender.sendMessage(Message.translation("server.builderTools.prefabAlreadyExists"));
/*     */           break;
/*     */       } 
/* 409 */       return false;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\prefabeditor\saving\PrefabSaver.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */