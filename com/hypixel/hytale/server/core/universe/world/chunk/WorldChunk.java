/*     */ package com.hypixel.hytale.server.core.universe.world.chunk;
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.common.collection.Flags;
/*     */ import com.hypixel.hytale.common.util.CompletableFutureUtil;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.RemoveReason;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockParticleEvent;
/*     */ import com.hypixel.hytale.protocol.Opacity;
/*     */ import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickManager;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktick.config.TickProcedure;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.LegacyModule;
/*     */ import com.hypixel.hytale.server.core.modules.block.BlockModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.WorldNotificationHandler;
/*     */ import com.hypixel.hytale.server.core.universe.world.accessor.ChunkAccessor;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.environment.EnvironmentChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import java.util.concurrent.locks.StampedLock;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class WorldChunk implements BlockAccessor, Component<ChunkStore> {
/*  50 */   public static final BuilderCodec<WorldChunk> CODEC = BuilderCodec.builder(WorldChunk.class, WorldChunk::new)
/*  51 */     .build(); public static final int KEEP_ALIVE_DEFAULT = 15;
/*     */   
/*     */   public static ComponentType<ChunkStore, WorldChunk> getComponentType() {
/*  54 */     return LegacyModule.get().getWorldChunkComponentType();
/*     */   }
/*     */   
/*  57 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private World world;
/*     */   
/*  61 */   private final StampedLock flagsLock = new StampedLock();
/*     */   
/*     */   private final Flags<ChunkFlag> flags;
/*     */   
/*     */   private Ref<ChunkStore> reference;
/*     */   @Nullable
/*     */   private BlockChunk blockChunk;
/*     */   @Nullable
/*     */   private BlockComponentChunk blockComponentChunk;
/*     */   @Nullable
/*     */   private EntityChunk entityChunk;
/*  72 */   private int keepAlive = 15;
/*  73 */   private int activeTimer = 15;
/*     */   private boolean needsSaving;
/*     */   private boolean isSaving;
/*  76 */   private final AtomicInteger keepLoaded = new AtomicInteger();
/*     */   private boolean lightingUpdatesEnabled = true;
/*     */   @Deprecated
/*  79 */   public final AtomicLong chunkLightTiming = new AtomicLong();
/*     */ 
/*     */   
/*     */   private WorldChunk() {
/*  83 */     this.flags = new Flags((Flag[])new ChunkFlag[0]);
/*     */   }
/*     */   
/*     */   private WorldChunk(World world, Flags<ChunkFlag> flags) {
/*  87 */     this.world = world;
/*  88 */     this.flags = flags;
/*     */   }
/*     */   
/*     */   public WorldChunk(World world, Flags<ChunkFlag> state, BlockChunk blockChunk, BlockComponentChunk blockComponentChunk, EntityChunk entityChunk) {
/*  92 */     this(world, state);
/*  93 */     this.blockChunk = blockChunk;
/*  94 */     this.blockComponentChunk = blockComponentChunk;
/*  95 */     this.entityChunk = entityChunk;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Holder<ChunkStore> toHolder() {
/* 101 */     if (this.reference != null && this.reference.isValid() && this.world != null) {
/* 102 */       Holder<ChunkStore> holder1 = ChunkStore.REGISTRY.newHolder();
/* 103 */       Store<ChunkStore> componentStore = this.world.getChunkStore().getStore();
/* 104 */       Archetype<ChunkStore> archetype = componentStore.getArchetype(this.reference);
/* 105 */       for (int i = archetype.getMinIndex(); i < archetype.length(); i++) {
/* 106 */         ComponentType componentType = archetype.get(i);
/* 107 */         if (componentType != null)
/* 108 */           holder1.addComponent(componentType, componentStore.getComponent(this.reference, componentType)); 
/*     */       } 
/* 110 */       return holder1;
/*     */     } 
/*     */     
/* 113 */     Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
/* 114 */     holder.addComponent(getComponentType(), this);
/* 115 */     holder.addComponent(BlockChunk.getComponentType(), this.blockChunk);
/* 116 */     holder.addComponent(EnvironmentChunk.getComponentType(), (Component)this.blockChunk.getEnvironmentChunk());
/* 117 */     holder.addComponent(EntityChunk.getComponentType(), this.entityChunk);
/* 118 */     holder.addComponent(BlockComponentChunk.getComponentType(), this.blockComponentChunk);
/* 119 */     return holder;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setReference(Ref<ChunkStore> reference) {
/* 124 */     if (this.reference != null && this.reference.isValid()) {
/* 125 */       throw new IllegalArgumentException("Chunk already has a valid EntityReference: " + String.valueOf(this.reference) + " new reference " + String.valueOf(reference));
/*     */     }
/* 127 */     this.reference = reference;
/*     */   }
/*     */   
/*     */   public Ref<ChunkStore> getReference() {
/* 131 */     return this.reference;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<ChunkStore> clone() {
/* 137 */     return new WorldChunk();
/*     */   }
/*     */   
/*     */   public boolean is(@Nonnull ChunkFlag flag) {
/* 141 */     long stamp = this.flagsLock.tryOptimisticRead();
/* 142 */     boolean value = this.flags.is(flag);
/* 143 */     if (this.flagsLock.validate(stamp)) {
/* 144 */       return value;
/*     */     }
/* 146 */     stamp = this.flagsLock.readLock();
/*     */     try {
/* 148 */       return this.flags.is(flag);
/*     */     } finally {
/* 150 */       this.flagsLock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean not(@Nonnull ChunkFlag flag) {
/* 155 */     long stamp = this.flagsLock.tryOptimisticRead();
/* 156 */     boolean value = this.flags.not(flag);
/* 157 */     if (this.flagsLock.validate(stamp)) {
/* 158 */       return value;
/*     */     }
/* 160 */     stamp = this.flagsLock.readLock();
/*     */     try {
/* 162 */       return this.flags.not(flag);
/*     */     } finally {
/* 164 */       this.flagsLock.unlockRead(stamp);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setFlag(@Nonnull ChunkFlag flag, boolean value) {
/*     */     boolean isInit;
/* 170 */     long lock = this.flagsLock.writeLock();
/*     */     
/*     */     try {
/* 173 */       if (!this.flags.set(flag, value))
/* 174 */         return;  isInit = this.flags.is(ChunkFlag.INIT);
/*     */     } finally {
/* 176 */       this.flagsLock.unlockWrite(lock);
/*     */     } 
/* 178 */     if (isInit)
/*     */     {
/* 180 */       updateFlag(flag, value);
/*     */     }
/* 182 */     LOGGER.at(Level.FINER).log("[%d, %d] updated chunk flag (init: %s): %s, %s ", Integer.valueOf(getX()), Integer.valueOf(getZ()), Boolean.valueOf(isInit), flag, Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean toggleFlag(@Nonnull ChunkFlag flag) {
/*     */     boolean value, isInit;
/* 188 */     long lock = this.flagsLock.writeLock();
/*     */     try {
/* 190 */       value = this.flags.toggle(flag);
/* 191 */       isInit = this.flags.is(ChunkFlag.INIT);
/*     */     } finally {
/* 193 */       this.flagsLock.unlockWrite(lock);
/*     */     } 
/* 195 */     if (isInit)
/*     */     {
/* 197 */       updateFlag(flag, value);
/*     */     }
/* 199 */     LOGGER.at(Level.FINER).log("[%d, %d] updated chunk flag (init: %s): %s, %s ", Integer.valueOf(getX()), Integer.valueOf(getZ()), Boolean.valueOf(isInit), flag, Boolean.valueOf(value));
/* 200 */     return value;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void loadFromHolder(World world, int x, int z, @Nonnull Holder<ChunkStore> holder) {
/* 205 */     this.world = world;
/* 206 */     this.blockChunk = (BlockChunk)holder.getComponent(BlockChunk.getComponentType());
/* 207 */     this.blockChunk.setEnvironmentChunk((EnvironmentChunk)holder.getComponent(EnvironmentChunk.getComponentType()));
/* 208 */     this.blockComponentChunk = (BlockComponentChunk)holder.getComponent(BlockComponentChunk.getComponentType());
/* 209 */     this.entityChunk = (EntityChunk)holder.getComponent(EntityChunk.getComponentType());
/* 210 */     this.blockChunk.load(x, z);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setBlockComponentChunk(BlockComponentChunk blockComponentChunk) {
/* 215 */     this.blockComponentChunk = blockComponentChunk;
/*     */   }
/*     */   
/*     */   public void initFlags() {
/* 219 */     this.world.debugAssertInTickingThread();
/* 220 */     if (!is(ChunkFlag.START_INIT)) throw new IllegalArgumentException("START_INIT hasn't been set!"); 
/* 221 */     if (is(ChunkFlag.INIT)) throw new IllegalArgumentException("INIT is already set!");
/*     */ 
/*     */     
/* 224 */     for (int i = 0; i < ChunkFlag.VALUES.length; i++) {
/* 225 */       ChunkFlag flag = ChunkFlag.VALUES[i];
/* 226 */       updateFlag(flag, is(flag));
/*     */     } 
/* 228 */     setFlag(ChunkFlag.INIT, true);
/*     */   }
/*     */   
/*     */   private void updateFlag(ChunkFlag flag, boolean value) {
/* 232 */     if (flag == ChunkFlag.TICKING) {
/*     */ 
/*     */       
/* 235 */       this.world.debugAssertInTickingThread();
/* 236 */       resetKeepAlive();
/* 237 */       if (value) {
/* 238 */         startsTicking();
/*     */       } else {
/* 240 */         stopsTicking();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void startsTicking() {
/* 246 */     this.world.debugAssertInTickingThread();
/* 247 */     LOGGER.at(Level.FINER).log("Chunk started ticking %s", this);
/*     */ 
/*     */     
/* 250 */     Store<ChunkStore> componentStore = this.world.getChunkStore().getStore();
/* 251 */     componentStore.tryRemoveComponent(this.reference, ChunkStore.REGISTRY.getNonTickingComponentType());
/*     */   }
/*     */   
/*     */   private void stopsTicking() {
/* 255 */     this.world.debugAssertInTickingThread();
/* 256 */     LOGGER.at(Level.FINER).log("Chunk stopped ticking %s", this);
/*     */ 
/*     */     
/* 259 */     Store<ChunkStore> componentStore = this.world.getChunkStore().getStore();
/* 260 */     componentStore.ensureComponent(this.reference, ChunkStore.REGISTRY.getNonTickingComponentType());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public BlockChunk getBlockChunk() {
/* 265 */     return this.blockChunk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public BlockComponentChunk getBlockComponentChunk() {
/* 275 */     return this.blockComponentChunk;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public EntityChunk getEntityChunk() {
/* 285 */     return this.entityChunk;
/*     */   }
/*     */   
/*     */   public boolean shouldKeepLoaded() {
/* 289 */     return (this.keepLoaded.get() > 0);
/*     */   }
/*     */   
/*     */   public void addKeepLoaded() {
/* 293 */     this.keepLoaded.incrementAndGet();
/*     */   }
/*     */   
/*     */   public void removeKeepLoaded() {
/* 297 */     this.keepLoaded.decrementAndGet();
/*     */   }
/*     */   
/*     */   public int pollKeepAlive(int pollCount) {
/* 301 */     return this.keepAlive = Math.max(this.keepAlive - pollCount, 0);
/*     */   }
/*     */   
/*     */   public void resetKeepAlive() {
/* 305 */     this.keepAlive = 15;
/*     */   }
/*     */   
/*     */   public int pollActiveTimer(int pollCount) {
/* 309 */     return this.activeTimer = Math.max(this.activeTimer - pollCount, 0);
/*     */   }
/*     */   
/*     */   public void resetActiveTimer() {
/* 313 */     this.activeTimer = 15;
/*     */   }
/*     */ 
/*     */   
/*     */   public ChunkAccessor getChunkAccessor() {
/* 318 */     return (ChunkAccessor)this.world;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBlock(int x, int y, int z) {
/* 323 */     if (y < 0 || y >= 320) return 0; 
/* 324 */     return this.blockChunk.getBlock(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setBlock(int x, int y, int z, int id, @Nonnull BlockType blockType, int rotation, int filler, int settings) {
/* 329 */     if (y < 0 || y >= 320) return false;
/*     */     
/* 331 */     short oldHeight = this.blockChunk.getHeight(x, z);
/* 332 */     BlockSection blockSection = this.blockChunk.getSectionAtBlockY(y);
/* 333 */     int oldRotation = blockSection.getRotationIndex(x, y, z);
/* 334 */     int oldFiller = blockSection.getFiller(x, y, z);
/* 335 */     int oldBlock = blockSection.get(x, y, z);
/*     */     
/* 337 */     boolean changed = ((oldBlock != id || rotation != oldRotation) && blockSection.set(x, y, z, id, rotation, filler));
/*     */     
/* 339 */     if (changed || (settings & 0x40) != 0) {
/* 340 */       int worldX = (getX() << 5) + (x & 0x1F);
/* 341 */       int worldZ = (getZ() << 5) + (z & 0x1F);
/*     */       
/* 343 */       if ((settings & 0x40) != 0) {
/* 344 */         blockSection.invalidateBlock(x, y, z);
/*     */       }
/*     */       
/* 347 */       short newHeight = oldHeight;
/* 348 */       if ((settings & 0x200) == 0)
/*     */       {
/* 350 */         if (oldHeight <= y) {
/* 351 */           if (oldHeight == y && id == 0) {
/*     */             
/* 353 */             newHeight = this.blockChunk.updateHeight(x, z, (short)y);
/* 354 */           } else if (oldHeight < y && id != 0 && 
/* 355 */             blockType.getOpacity() != Opacity.Transparent) {
/*     */             
/* 357 */             newHeight = (short)y;
/* 358 */             this.blockChunk.setHeight(x, z, newHeight);
/*     */           } 
/*     */         }
/*     */       }
/*     */ 
/*     */       
/* 364 */       WorldNotificationHandler notificationHandler = getWorld().getNotificationHandler();
/*     */       
/* 366 */       if ((settings & 0x4) == 0)
/*     */       {
/*     */         
/* 369 */         if (oldBlock == 0 && id != 0) {
/* 370 */           notificationHandler.sendBlockParticle(worldX + 0.5D, y + 0.5D, worldZ + 0.5D, id, BlockParticleEvent.Build);
/*     */         
/*     */         }
/* 373 */         else if (oldBlock != 0 && id == 0) {
/* 374 */           BlockParticleEvent particleType = ((settings & 0x20) != 0) ? BlockParticleEvent.Physics : BlockParticleEvent.Break;
/* 375 */           notificationHandler.sendBlockParticle(worldX + 0.5D, y + 0.5D, worldZ + 0.5D, oldBlock, particleType);
/*     */         }
/*     */         else {
/*     */           
/* 379 */           notificationHandler.sendBlockParticle(worldX + 0.5D, y + 0.5D, worldZ + 0.5D, oldBlock, BlockParticleEvent.Break);
/* 380 */           notificationHandler.sendBlockParticle(worldX + 0.5D, y + 0.5D, worldZ + 0.5D, id, BlockParticleEvent.Build);
/*     */         } 
/*     */       }
/*     */       
/* 384 */       BlockTypeAssetMap<String, BlockType> blockTypeAssetMap = BlockType.getAssetMap();
/* 385 */       IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitboxAssetMap = BlockBoundingBoxes.getAssetMap();
/*     */       
/* 387 */       String blockTypeKey = blockType.getId();
/* 388 */       if ((settings & 0x2) == 0) {
/* 389 */         Holder<ChunkStore> blockEntity = blockType.getBlockEntity();
/* 390 */         if (blockEntity != null && filler == 0) {
/* 391 */           Holder<ChunkStore> newComponents = blockEntity.clone();
/* 392 */           setState(x, y, z, newComponents);
/*     */         } else {
/* 394 */           BlockState blockState = null;
/* 395 */           String blockStateType = (blockType.getState() != null) ? blockType.getState().getId() : null;
/* 396 */           if (id != 0 && blockStateType != null && filler == 0) {
/* 397 */             blockState = BlockStateModule.get().createBlockState(blockStateType, this, new Vector3i(x, y, z), blockType);
/* 398 */             if (blockState == null) {
/* 399 */               LOGGER.at(Level.WARNING).log("Failed to create BlockState: %s for BlockType: %s", blockStateType, blockTypeKey);
/*     */             }
/*     */           } 
/* 402 */           BlockState oldState = getState(x, y, z);
/*     */ 
/*     */           
/* 405 */           if (blockState instanceof ItemContainerState) { ItemContainerState newState = (ItemContainerState)blockState;
/* 406 */             FillerBlockUtil.forEachFillerBlock(((BlockBoundingBoxes)hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex())).get(rotation), (x1, y1, z1) -> {
/*     */                   int blockX = worldX + x1;
/*     */                   
/*     */                   int blockY = y + y1;
/*     */                   int blockZ = worldZ + z1;
/* 411 */                   boolean isZero = (x1 == 0 && y1 == 0 && z1 == 0);
/*     */                   BlockState stateAtFiller = isZero ? oldState : getState(blockX, blockY, blockZ);
/*     */                   if (stateAtFiller instanceof ItemContainerState) {
/*     */                     ItemContainerState oldContainer = (ItemContainerState)stateAtFiller;
/*     */                     oldContainer.getItemContainer().moveAllItemStacksTo(new ItemContainer[] { newState.getItemContainer() });
/*     */                   } 
/*     */                 }); }
/*     */           
/* 419 */           setState(x, y, z, blockState, ((settings & 0x1) == 0));
/*     */         } 
/*     */       } 
/*     */       
/* 423 */       if (this.lightingUpdatesEnabled) {
/* 424 */         this.world.getChunkLighting().invalidateLightAtBlock(this, x, y, z, blockType, oldHeight, newHeight);
/*     */       }
/*     */       
/* 427 */       TickProcedure tickProcedure = BlockTickManager.getBlockTickProvider().getTickProcedure(id);
/* 428 */       this.blockChunk.setTicking(x, y, z, (tickProcedure != null));
/*     */       
/* 430 */       if ((settings & 0x10) == 0) {
/* 431 */         int settingsWithoutFiller = settings | 0x8 | 0x10;
/*     */         
/* 433 */         BlockType oldBlockType = (BlockType)blockTypeAssetMap.getAsset(oldBlock);
/* 434 */         String oldBlockKey = oldBlockType.getId();
/*     */         
/* 436 */         int baseX = worldX - FillerBlockUtil.unpackX(oldFiller);
/* 437 */         int baseY = y - FillerBlockUtil.unpackY(oldFiller);
/* 438 */         int baseZ = worldZ - FillerBlockUtil.unpackZ(oldFiller);
/*     */ 
/*     */         
/* 441 */         FillerBlockUtil.forEachFillerBlock(((BlockBoundingBoxes)hitboxAssetMap.getAsset(oldBlockType.getHitboxTypeIndex())).get(oldRotation), (x1, y1, z1) -> {
/*     */               if (x1 == 0 && y1 == 0 && z1 == 0) {
/*     */                 return;
/*     */               }
/*     */               
/*     */               int blockX = baseX + x1;
/*     */               int blockY = baseY + y1;
/*     */               int blockZ = baseZ + z1;
/*     */               if (ChunkUtil.isSameChunk(worldX, worldZ, blockX, blockZ)) {
/*     */                 String blockTypeKey1 = getBlockType(blockX, blockY, blockZ).getId();
/*     */                 if (blockTypeKey1.equals(oldBlockKey)) {
/*     */                   breakBlock(blockX, blockY, blockZ, settingsWithoutFiller);
/*     */                 }
/*     */               } else {
/*     */                 String blockTypeKey1 = getWorld().getBlockType(blockX, blockY, blockZ).getId();
/*     */                 if (blockTypeKey1.equals(oldBlockKey)) {
/*     */                   getWorld().breakBlock(blockX, blockY, blockZ, settingsWithoutFiller);
/*     */                 }
/*     */               } 
/*     */             });
/*     */       } 
/* 462 */       if ((settings & 0x8) == 0 && filler == 0) {
/* 463 */         int settingsWithoutSetFiller = settings | 0x8;
/*     */ 
/*     */ 
/*     */         
/* 467 */         BlockType finalBlockType = blockType;
/* 468 */         int blockId = id;
/* 469 */         int finalRotation = rotation;
/* 470 */         FillerBlockUtil.forEachFillerBlock(((BlockBoundingBoxes)hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex())).get(rotation), (x1, y1, z1) -> {
/*     */               if (x1 == 0 && y1 == 0 && z1 == 0) {
/*     */                 return;
/*     */               }
/*     */               
/*     */               int blockX = worldX + x1;
/*     */               
/*     */               int blockY = y + y1;
/*     */               int blockZ = worldZ + z1;
/*     */               boolean sameChunk = ChunkUtil.isSameChunk(worldX, worldZ, blockX, blockZ);
/*     */               if (sameChunk) {
/*     */                 setBlock(blockX, blockY, blockZ, blockId, finalBlockType, finalRotation, FillerBlockUtil.pack(x1, y1, z1), settingsWithoutSetFiller);
/*     */               } else {
/*     */                 getWorld().getNonTickingChunk(ChunkUtil.indexChunkFromBlock(blockX, blockZ)).setBlock(blockX, blockY, blockZ, blockId, finalBlockType, finalRotation, FillerBlockUtil.pack(x1, y1, z1), settingsWithoutSetFiller);
/*     */               } 
/*     */             });
/*     */       } 
/* 487 */       if ((settings & 0x100) != 0)
/*     */       {
/* 489 */         FillerBlockUtil.forEachFillerBlock(((BlockBoundingBoxes)hitboxAssetMap.getAsset(blockType.getHitboxTypeIndex())).get(rotation), (x1, y1, z1) -> getChunkAccessor().performBlockUpdate(worldX + x1, y + y1, worldZ + z1));
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 495 */       if (this.reference != null && this.reference.isValid()) {
/* 496 */         if (this.world.isInThread()) {
/* 497 */           setBlockPhysics(x, y, z, blockType);
/*     */         } else {
/* 499 */           BlockType tempFinalBlockType = blockType;
/* 500 */           CompletableFutureUtil._catch(CompletableFuture.runAsync(() -> setBlockPhysics(x, y, z, tempFinalBlockType), (Executor)this.world));
/*     */         } 
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 507 */     return changed;
/*     */   }
/*     */   
/*     */   private void setBlockPhysics(int x, int y, int z, @Nonnull BlockType blockType) {
/* 511 */     Store<ChunkStore> store = this.reference.getStore();
/* 512 */     ChunkColumn column = (ChunkColumn)store.getComponent(this.reference, ChunkColumn.getComponentType());
/* 513 */     Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 514 */     if (section != null) {
/* 515 */       if (!blockType.hasSupport()) {
/* 516 */         BlockPhysics.clear(store, section, x, y, z);
/*     */       }
/*     */       else {
/*     */         
/* 520 */         BlockPhysics.reset(store, section, x, y, z);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public int getFiller(int x, int y, int z) {
/* 528 */     if (y < 0 || y >= 320) return 0; 
/* 529 */     return this.blockChunk.getSectionAtBlockY(y).getFiller(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public int getRotationIndex(int x, int y, int z) {
/* 535 */     if (y < 0 || y >= 320) return 0; 
/* 536 */     return this.blockChunk.getSectionAtBlockY(y).getRotationIndex(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setTicking(int x, int y, int z, boolean ticking) {
/* 541 */     return this.blockChunk.setTicking(x, y, z, ticking);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTicking(int x, int y, int z) {
/* 546 */     return this.blockChunk.isTicking(x, y, z);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getHeight(int x, int z) {
/* 553 */     return this.blockChunk.getHeight(x, z);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public short getHeight(int index) {
/* 560 */     return this.blockChunk.getHeight(index);
/*     */   }
/*     */   
/*     */   public int getTint(int x, int z) {
/* 564 */     return this.blockChunk.getTint(x, z);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public BlockState getState(int x, int y, int z) {
/* 570 */     if (y < 0 || y >= 320) return null;
/*     */     
/* 572 */     if (!this.world.isInThread()) {
/* 573 */       return CompletableFuture.<BlockState>supplyAsync(() -> getState(x, y, z), (Executor)this.world).join();
/*     */     }
/*     */     
/* 576 */     int index = ChunkUtil.indexBlockInColumn(x, y, z);
/*     */     
/* 578 */     Ref<ChunkStore> reference = this.blockComponentChunk.getEntityReference(index);
/* 579 */     if (reference != null) return BlockState.getBlockState(reference, (ComponentAccessor)reference.getStore());
/*     */     
/* 581 */     Holder<ChunkStore> holder = this.blockComponentChunk.getEntityHolder(index);
/* 582 */     if (holder != null) return BlockState.getBlockState(holder);
/*     */     
/* 584 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Ref<ChunkStore> getBlockComponentEntity(int x, int y, int z) {
/* 589 */     if (y < 0 || y >= 320) return null;
/*     */     
/* 591 */     if (!this.world.isInThread()) {
/* 592 */       return CompletableFuture.<Ref<ChunkStore>>supplyAsync(() -> getBlockComponentEntity(x, y, z), (Executor)this.world).join();
/*     */     }
/*     */     
/* 595 */     int index = ChunkUtil.indexBlockInColumn(x, y, z);
/*     */     
/* 597 */     return this.blockComponentChunk.getEntityReference(index);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Holder<ChunkStore> getBlockComponentHolder(int x, int y, int z) {
/* 603 */     if (y < 0 || y >= 320) return null;
/*     */     
/* 605 */     if (!this.world.isInThread()) {
/* 606 */       return CompletableFuture.<Holder<ChunkStore>>supplyAsync(() -> getBlockComponentHolder(x, y, z), (Executor)this.world).join();
/*     */     }
/*     */     
/* 609 */     int index = ChunkUtil.indexBlockInColumn(x, y, z);
/*     */     
/* 611 */     Ref<ChunkStore> reference = this.blockComponentChunk.getEntityReference(index);
/* 612 */     if (reference != null) {
/* 613 */       return reference.getStore().copyEntity(reference);
/*     */     }
/*     */     
/* 616 */     Holder<ChunkStore> holder = this.blockComponentChunk.getEntityHolder(index);
/* 617 */     return (holder != null) ? holder.clone() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setState(int x, int y, int z, @Nullable BlockState state, boolean notify) {
/* 622 */     if (y < 0 || y >= 320)
/*     */       return; 
/* 624 */     Holder<ChunkStore> holder = (state != null) ? state.toHolder() : null;
/* 625 */     setState(x, y, z, holder);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public int getFluidId(int x, int y, int z) {
/* 631 */     Ref<ChunkStore> columnRef = getReference();
/* 632 */     Store<ChunkStore> store = columnRef.getStore();
/* 633 */     ChunkColumn column = (ChunkColumn)store.getComponent(columnRef, ChunkColumn.getComponentType());
/* 634 */     Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 635 */     if (section == null) return Integer.MIN_VALUE; 
/* 636 */     FluidSection fluidSection = (FluidSection)store.getComponent(section, FluidSection.getComponentType());
/* 637 */     return fluidSection.getFluidId(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public byte getFluidLevel(int x, int y, int z) {
/* 643 */     Ref<ChunkStore> columnRef = getReference();
/* 644 */     Store<ChunkStore> store = columnRef.getStore();
/* 645 */     ChunkColumn column = (ChunkColumn)store.getComponent(columnRef, ChunkColumn.getComponentType());
/* 646 */     Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 647 */     if (section == null) return 0; 
/* 648 */     FluidSection fluidSection = (FluidSection)store.getComponent(section, FluidSection.getComponentType());
/* 649 */     return fluidSection.getFluidLevel(x, y, z);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated(forRemoval = true)
/*     */   public int getSupportValue(int x, int y, int z) {
/* 655 */     Ref<ChunkStore> columnRef = getReference();
/* 656 */     Store<ChunkStore> store = columnRef.getStore();
/* 657 */     ChunkColumn column = (ChunkColumn)store.getComponent(columnRef, ChunkColumn.getComponentType());
/* 658 */     Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(y));
/* 659 */     if (section == null) return 0; 
/* 660 */     BlockPhysics blockPhysics = (BlockPhysics)store.getComponent(section, BlockPhysics.getComponentType());
/* 661 */     return (blockPhysics != null) ? blockPhysics.get(x, y, z) : 0;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setState(int x, int y, int z, @Nullable Holder<ChunkStore> holder) {
/* 666 */     if (y < 0 || y >= 320)
/*     */       return; 
/* 668 */     if (!this.world.isInThread()) {
/* 669 */       CompletableFutureUtil._catch(CompletableFuture.runAsync(() -> setState(x, y, z, holder), (Executor)this.world));
/*     */       
/*     */       return;
/*     */     } 
/* 673 */     boolean notify = true;
/*     */     
/* 675 */     int index = ChunkUtil.indexBlockInColumn(x, y, z);
/*     */     
/* 677 */     if (holder == null) {
/* 678 */       Ref<ChunkStore> reference = this.blockComponentChunk.getEntityReference(index);
/* 679 */       if (reference != null) {
/* 680 */         Holder<ChunkStore> oldHolder = reference.getStore().removeEntity(reference, RemoveReason.REMOVE);
/* 681 */         BlockState blockState = BlockState.getBlockState(oldHolder);
/*     */         
/* 683 */         if (notify) {
/* 684 */           this.world.getNotificationHandler().updateState(x, y, z, null, blockState);
/*     */         }
/*     */       } else {
/* 687 */         this.blockComponentChunk.removeEntityHolder(index);
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 692 */     BlockState state = BlockState.getBlockState(holder);
/* 693 */     if (state != null) {
/* 694 */       state.setPosition(this, new Vector3i(x, y, z));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 701 */     Store<ChunkStore> blockComponentStore = this.world.getChunkStore().getStore();
/*     */     
/* 703 */     if (!is(ChunkFlag.TICKING)) {
/* 704 */       Holder<ChunkStore> oldHolder = this.blockComponentChunk.getEntityHolder(index);
/*     */       
/* 706 */       BlockState blockState = null;
/* 707 */       if (oldHolder != null) {
/* 708 */         blockState = BlockState.getBlockState(oldHolder);
/*     */       }
/* 710 */       this.blockComponentChunk.removeEntityHolder(index);
/*     */       
/* 712 */       holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), (Component)new BlockModule.BlockStateInfo(index, this.reference));
/* 713 */       this.blockComponentChunk.addEntityHolder(index, holder);
/*     */       
/* 715 */       if (notify) {
/* 716 */         this.world.getNotificationHandler().updateState(x, y, z, state, blockState);
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 721 */     Ref<ChunkStore> oldReference = this.blockComponentChunk.getEntityReference(index);
/*     */     
/* 723 */     BlockState oldState = null;
/* 724 */     if (oldReference != null) {
/* 725 */       Holder<ChunkStore> oldEntityHolder = blockComponentStore.removeEntity(oldReference, RemoveReason.REMOVE);
/* 726 */       oldState = BlockState.getBlockState(oldEntityHolder);
/*     */     } else {
/* 728 */       this.blockComponentChunk.removeEntityHolder(index);
/*     */     } 
/*     */     
/* 731 */     holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), (Component)new BlockModule.BlockStateInfo(index, this.reference));
/* 732 */     blockComponentStore.addEntity(holder, AddReason.SPAWN);
/*     */     
/* 734 */     if (notify) {
/* 735 */       this.world.getNotificationHandler().updateState(x, y, z, state, oldState);
/*     */     }
/*     */   }
/*     */   
/*     */   public void markNeedsSaving() {
/* 740 */     this.needsSaving = true;
/*     */   }
/*     */   
/*     */   public boolean getNeedsSaving() {
/* 744 */     return (this.needsSaving || this.blockChunk.getNeedsSaving() || this.blockComponentChunk.getNeedsSaving() || this.entityChunk.getNeedsSaving());
/*     */   }
/*     */   
/*     */   public boolean consumeNeedsSaving() {
/* 748 */     boolean out = this.needsSaving;
/* 749 */     if (this.blockChunk.consumeNeedsSaving()) out = true; 
/* 750 */     if (this.blockComponentChunk.consumeNeedsSaving()) out = true; 
/* 751 */     if (this.entityChunk.consumeNeedsSaving()) out = true; 
/* 752 */     this.needsSaving = false;
/* 753 */     return out;
/*     */   }
/*     */   
/*     */   public boolean isSaving() {
/* 757 */     return this.isSaving;
/*     */   }
/*     */   
/*     */   public void setSaving(boolean saving) {
/* 761 */     this.isSaving = saving;
/*     */   }
/*     */   
/*     */   public long getIndex() {
/* 765 */     return this.blockChunk.getIndex();
/*     */   }
/*     */   
/*     */   public int getX() {
/* 769 */     return this.blockChunk.getX();
/*     */   }
/*     */   
/*     */   public int getZ() {
/* 773 */     return this.blockChunk.getZ();
/*     */   }
/*     */   
/*     */   public void setLightingUpdatesEnabled(boolean enableLightUpdates) {
/* 777 */     this.lightingUpdatesEnabled = enableLightUpdates;
/*     */   }
/*     */   
/*     */   public boolean isLightingUpdatesEnabled() {
/* 781 */     return this.lightingUpdatesEnabled;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public World getWorld() {
/* 788 */     return this.world;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 794 */     return "WorldChunk{x=" + this.blockChunk
/* 795 */       .getX() + ", z=" + this.blockChunk
/* 796 */       .getZ() + ", flags=" + String.valueOf(this.flags) + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\chunk\WorldChunk.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */