/*      */ package com.hypixel.hytale.server.core.universe.world.chunk.section;
/*      */ 
/*      */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*      */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*      */ import com.hypixel.hytale.codec.Codec;
/*      */ import com.hypixel.hytale.codec.ExtraInfo;
/*      */ import com.hypixel.hytale.codec.KeyedCodec;
/*      */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*      */ import com.hypixel.hytale.common.util.BitSetUtil;
/*      */ import com.hypixel.hytale.component.Component;
/*      */ import com.hypixel.hytale.component.ComponentType;
/*      */ import com.hypixel.hytale.function.predicate.ObjectPositionBlockFunction;
/*      */ import com.hypixel.hytale.math.util.ChunkUtil;
/*      */ import com.hypixel.hytale.protocol.CachedPacket;
/*      */ import com.hypixel.hytale.protocol.Packet;
/*      */ import com.hypixel.hytale.protocol.packets.world.PaletteType;
/*      */ import com.hypixel.hytale.protocol.packets.world.SetChunk;
/*      */ import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMigration;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*      */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*      */ import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
/*      */ import com.hypixel.hytale.server.core.blocktype.component.BlockPhysics;
/*      */ import com.hypixel.hytale.server.core.modules.LegacyModule;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.EmptySectionPalette;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.ISectionPalette;
/*      */ import com.hypixel.hytale.server.core.universe.world.chunk.section.palette.PaletteTypeEnum;
/*      */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*      */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*      */ import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
/*      */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*      */ import io.netty.buffer.ByteBuf;
/*      */ import io.netty.buffer.ByteBufAllocator;
/*      */ import io.netty.buffer.Unpooled;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*      */ import it.unimi.dsi.fastutil.ints.Int2ShortMap;
/*      */ import it.unimi.dsi.fastutil.ints.IntList;
/*      */ import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
/*      */ import it.unimi.dsi.fastutil.ints.IntSet;
/*      */ import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
/*      */ import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.time.Instant;
/*      */ import java.util.BitSet;
/*      */ import java.util.Comparator;
/*      */ import java.util.Map;
/*      */ import java.util.Objects;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.locks.StampedLock;
/*      */ import java.util.function.Function;
/*      */ import java.util.function.IntConsumer;
/*      */ import java.util.function.Supplier;
/*      */ import java.util.function.ToIntFunction;
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
/*      */ public class BlockSection
/*      */   implements Component<ChunkStore>
/*      */ {
/*      */   public static final int VERSION = 6;
/*   71 */   public static final BuilderCodec<BlockSection> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockSection.class, BlockSection::new)
/*   72 */     .versioned())
/*   73 */     .codecVersion(6))
/*   74 */     .append(new KeyedCodec("Data", Codec.BYTE_ARRAY), BlockSection::deserialize, BlockSection::serialize)
/*      */ 
/*      */ 
/*      */     
/*   78 */     .add())
/*   79 */     .build();
/*      */   
/*      */   public static ComponentType<ChunkStore, BlockSection> getComponentType() {
/*   82 */     return LegacyModule.get().getBlockSectionComponentType();
/*      */   }
/*      */   
/*   85 */   private final StampedLock chunkSectionLock = new StampedLock();
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean loaded = false;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*   94 */   private IntOpenHashSet changedPositions = new IntOpenHashSet(0);
/*      */   
/*      */   @Nonnull
/*   97 */   private IntOpenHashSet swapChangedPositions = new IntOpenHashSet(0);
/*      */   
/*      */   private ISectionPalette chunkSection;
/*      */   
/*      */   private ISectionPalette fillerSection;
/*      */   
/*      */   private ISectionPalette rotationSection;
/*      */   
/*      */   private ChunkLightData localLight;
/*      */   
/*      */   private short localChangeCounter;
/*      */   
/*      */   private ChunkLightData globalLight;
/*      */   
/*      */   private short globalChangeCounter;
/*      */   
/*      */   private BitSet tickingBlocks;
/*      */   private final BitSet tickingBlocksCopy;
/*      */   @Nonnull
/*      */   private final BitSet tickingWaitAdjacentBlocks;
/*      */   private int tickingBlocksCount;
/*      */   private int tickingBlocksCountCopy;
/*      */   private int tickingWaitAdjacentBlockCount;
/*  120 */   private final ObjectHeapPriorityQueue<TickRequest> tickRequests = new ObjectHeapPriorityQueue(TICK_REQUEST_COMPARATOR);
/*      */   
/*  122 */   private double maximumHitboxExtent = -1.0D;
/*      */   
/*      */   @Nullable
/*      */   private transient SoftReference<CompletableFuture<CachedPacket<SetChunk>>> cachedChunkPacket;
/*      */   @Nullable
/*      */   @Deprecated(forRemoval = true)
/*      */   private FluidSection migratedFluidSection;
/*      */   @Nullable
/*      */   @Deprecated(forRemoval = true)
/*      */   private BlockPhysics migratedBlockPhysics;
/*      */   private static final Comparator<TickRequest> TICK_REQUEST_COMPARATOR;
/*      */   
/*      */   public BlockSection() {
/*  135 */     this((ISectionPalette)EmptySectionPalette.INSTANCE, (ISectionPalette)EmptySectionPalette.INSTANCE, (ISectionPalette)EmptySectionPalette.INSTANCE);
/*      */   }
/*      */   
/*      */   public BlockSection(ISectionPalette chunkSection, ISectionPalette fillerSection, ISectionPalette rotationSection) {
/*  139 */     this.chunkSection = chunkSection;
/*  140 */     this.fillerSection = fillerSection;
/*  141 */     this.rotationSection = rotationSection;
/*  142 */     this.tickingBlocks = new BitSet();
/*  143 */     this.tickingBlocksCopy = new BitSet();
/*  144 */     this.tickingWaitAdjacentBlocks = new BitSet();
/*  145 */     this.tickingBlocksCount = 0;
/*  146 */     this.tickingBlocksCountCopy = 0;
/*      */     
/*  148 */     this.localLight = ChunkLightData.EMPTY;
/*  149 */     this.localChangeCounter = 0;
/*  150 */     this.globalLight = ChunkLightData.EMPTY;
/*  151 */     this.globalChangeCounter = 0;
/*      */   }
/*      */   
/*      */   public ISectionPalette getChunkSection() {
/*  155 */     return this.chunkSection;
/*      */   }
/*      */   
/*      */   public void setChunkSection(ISectionPalette chunkSection) {
/*  159 */     this.chunkSection = chunkSection;
/*      */   }
/*      */   
/*      */   public void setLocalLight(@Nonnull ChunkLightDataBuilder localLight) {
/*  163 */     Objects.requireNonNull(localLight);
/*  164 */     this.localLight = localLight.build();
/*      */   }
/*      */   
/*      */   public void setGlobalLight(@Nonnull ChunkLightDataBuilder globalLight) {
/*  168 */     Objects.requireNonNull(globalLight);
/*  169 */     this.globalLight = globalLight.build();
/*      */   }
/*      */   
/*      */   public ChunkLightData getLocalLight() {
/*  173 */     return this.localLight;
/*      */   }
/*      */   
/*      */   public ChunkLightData getGlobalLight() {
/*  177 */     return this.globalLight;
/*      */   }
/*      */   
/*      */   public boolean hasLocalLight() {
/*  181 */     return (this.localLight.getChangeId() == this.localChangeCounter);
/*      */   }
/*      */   
/*      */   public boolean hasGlobalLight() {
/*  185 */     return (this.globalLight.getChangeId() == this.globalChangeCounter);
/*      */   }
/*      */   
/*      */   public void invalidateLocalLight() {
/*  189 */     this.localChangeCounter = (short)(this.localChangeCounter + 1);
/*  190 */     invalidateGlobalLight();
/*      */   }
/*      */   
/*      */   public void invalidateGlobalLight() {
/*  194 */     this.globalChangeCounter = (short)(this.globalChangeCounter + 1);
/*      */   }
/*      */   
/*      */   public short getLocalChangeCounter() {
/*  198 */     return this.localChangeCounter;
/*      */   }
/*      */   
/*      */   public short getGlobalChangeCounter() {
/*  202 */     return this.globalChangeCounter;
/*      */   }
/*      */   
/*      */   public void invalidate() {
/*  206 */     this.cachedChunkPacket = null;
/*      */   }
/*      */   
/*      */   public int get(int index) {
/*  210 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  211 */     int i = this.chunkSection.get(index);
/*  212 */     if (!this.chunkSectionLock.validate(lock)) {
/*  213 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  215 */         return this.chunkSection.get(index);
/*      */       } finally {
/*  217 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  221 */     return i;
/*      */   }
/*      */   
/*      */   public int getFiller(int index) {
/*  225 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  226 */     int i = this.fillerSection.get(index);
/*  227 */     if (!this.chunkSectionLock.validate(lock)) {
/*  228 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  230 */         return this.fillerSection.get(index);
/*      */       } finally {
/*  232 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  236 */     return i;
/*      */   }
/*      */   
/*      */   public int getFiller(int x, int y, int z) {
/*  240 */     return getFiller(ChunkUtil.indexBlock(x, y, z));
/*      */   }
/*      */   
/*      */   public int getRotationIndex(int index) {
/*  244 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  245 */     int i = this.rotationSection.get(index);
/*  246 */     if (!this.chunkSectionLock.validate(lock)) {
/*  247 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  249 */         return this.rotationSection.get(index);
/*      */       } finally {
/*  251 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  255 */     return i;
/*      */   }
/*      */   
/*      */   public int getRotationIndex(int x, int y, int z) {
/*  259 */     return getRotationIndex(ChunkUtil.indexBlock(x, y, z));
/*      */   }
/*      */   
/*      */   public RotationTuple getRotation(int index) {
/*  263 */     return RotationTuple.get(getRotationIndex(index));
/*      */   }
/*      */   
/*      */   public RotationTuple getRotation(int x, int y, int z) {
/*  267 */     return getRotation(ChunkUtil.indexBlock(x, y, z));
/*      */   }
/*      */   
/*      */   public boolean set(int blockIdx, int blockId, int rotation, int filler) {
/*      */     int i;
/*  272 */     long lock = this.chunkSectionLock.writeLock();
/*      */     try {
/*  274 */       ISectionPalette.SetResult result = this.chunkSection.set(blockIdx, blockId);
/*  275 */       if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
/*      */         
/*  277 */         this.chunkSection = this.chunkSection.promote();
/*      */         
/*  279 */         ISectionPalette.SetResult repeatResult = this.chunkSection.set(blockIdx, blockId);
/*  280 */         if (repeatResult != ISectionPalette.SetResult.ADDED_OR_REMOVED) {
/*  281 */           throw new IllegalStateException("Promoted chunk section failed to correctly add the new block!");
/*      */         }
/*      */       } else {
/*      */         
/*  285 */         if (result == ISectionPalette.SetResult.ADDED_OR_REMOVED) {
/*  286 */           this.maximumHitboxExtent = -1.0D;
/*      */         }
/*      */ 
/*      */         
/*  290 */         if (this.chunkSection.shouldDemote())
/*      */         {
/*  292 */           this.chunkSection = this.chunkSection.demote();
/*      */         }
/*      */       } 
/*      */       
/*  296 */       boolean changed = (result != ISectionPalette.SetResult.UNCHANGED);
/*      */       
/*  298 */       result = this.fillerSection.set(blockIdx, filler);
/*  299 */       if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
/*  300 */         this.fillerSection = this.fillerSection.promote();
/*  301 */         ISectionPalette.SetResult repeatResult = this.fillerSection.set(blockIdx, filler);
/*  302 */         if (repeatResult != ISectionPalette.SetResult.ADDED_OR_REMOVED) {
/*  303 */           throw new IllegalStateException("Promoted chunk section failed to correctly add the new block!");
/*      */         }
/*      */       }
/*  306 */       else if (this.fillerSection.shouldDemote()) {
/*  307 */         this.fillerSection = this.fillerSection.demote();
/*      */       } 
/*      */ 
/*      */       
/*  311 */       i = changed | ((result != ISectionPalette.SetResult.UNCHANGED) ? 1 : 0);
/*      */       
/*  313 */       result = this.rotationSection.set(blockIdx, rotation);
/*  314 */       if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
/*  315 */         this.rotationSection = this.rotationSection.promote();
/*  316 */         ISectionPalette.SetResult repeatResult = this.rotationSection.set(blockIdx, rotation);
/*  317 */         if (repeatResult != ISectionPalette.SetResult.ADDED_OR_REMOVED) {
/*  318 */           throw new IllegalStateException("Promoted chunk section failed to correctly add the new block!");
/*      */         }
/*      */       }
/*  321 */       else if (this.rotationSection.shouldDemote()) {
/*  322 */         this.rotationSection = this.rotationSection.demote();
/*      */       } 
/*      */ 
/*      */       
/*  326 */       i |= (result != ISectionPalette.SetResult.UNCHANGED) ? 1 : 0;
/*      */       
/*  328 */       if (i != 0 && this.loaded) {
/*  329 */         this.changedPositions.add(blockIdx);
/*      */       }
/*      */     } finally {
/*  332 */       this.chunkSectionLock.unlockWrite(lock);
/*      */     } 
/*      */     
/*  335 */     if (i != 0) {
/*  336 */       invalidateLocalLight();
/*      */     }
/*  338 */     return i;
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   public IntOpenHashSet getAndClearChangedPositions() {
/*  343 */     long stamp = this.chunkSectionLock.writeLock();
/*      */     try {
/*  345 */       this.swapChangedPositions.clear();
/*      */       
/*  347 */       IntOpenHashSet tmp = this.changedPositions;
/*  348 */       this.changedPositions = this.swapChangedPositions;
/*  349 */       this.swapChangedPositions = tmp;
/*  350 */       return tmp;
/*      */     } finally {
/*  352 */       this.chunkSectionLock.unlockWrite(stamp);
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean contains(int id) {
/*  357 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  358 */     boolean contains = this.chunkSection.contains(id);
/*  359 */     if (!this.chunkSectionLock.validate(lock)) {
/*  360 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  362 */         return this.chunkSection.contains(id);
/*      */       } finally {
/*  364 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  368 */     return contains;
/*      */   }
/*      */   
/*      */   public boolean containsAny(IntList ids) {
/*  372 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  373 */     boolean contains = this.chunkSection.containsAny(ids);
/*  374 */     if (!this.chunkSectionLock.validate(lock)) {
/*  375 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  377 */         return this.chunkSection.containsAny(ids);
/*      */       } finally {
/*  379 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  383 */     return contains;
/*      */   }
/*      */   
/*      */   public int count() {
/*  387 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  388 */     int count = this.chunkSection.count();
/*  389 */     if (!this.chunkSectionLock.validate(lock)) {
/*  390 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  392 */         return this.chunkSection.count();
/*      */       } finally {
/*  394 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  398 */     return count;
/*      */   }
/*      */   
/*      */   public int count(int id) {
/*  402 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  403 */     int count = this.chunkSection.count(id);
/*  404 */     if (!this.chunkSectionLock.validate(lock)) {
/*  405 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  407 */         return this.chunkSection.count(id);
/*      */       } finally {
/*  409 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  413 */     return count;
/*      */   }
/*      */   
/*      */   public IntSet values() {
/*  417 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  418 */     IntSet values = this.chunkSection.values();
/*  419 */     if (!this.chunkSectionLock.validate(lock)) {
/*  420 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  422 */         return this.chunkSection.values();
/*      */       } finally {
/*  424 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  428 */     return values;
/*      */   }
/*      */   
/*      */   public void forEachValue(IntConsumer consumer) {
/*  432 */     long lock = this.chunkSectionLock.readLock();
/*      */     try {
/*  434 */       this.chunkSection.forEachValue(consumer);
/*      */     } finally {
/*  436 */       this.chunkSectionLock.unlockRead(lock);
/*      */     } 
/*      */   }
/*      */   
/*      */   public Int2ShortMap valueCounts() {
/*  441 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  442 */     Int2ShortMap valueCounts = this.chunkSection.valueCounts();
/*  443 */     if (!this.chunkSectionLock.validate(lock)) {
/*  444 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  446 */         return this.chunkSection.valueCounts();
/*      */       } finally {
/*  448 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  452 */     return valueCounts;
/*      */   }
/*      */   
/*      */   public boolean isSolidAir() {
/*  456 */     long lock = this.chunkSectionLock.tryOptimisticRead();
/*  457 */     boolean isSolid = this.chunkSection.isSolid(0);
/*  458 */     if (!this.chunkSectionLock.validate(lock)) {
/*  459 */       lock = this.chunkSectionLock.readLock();
/*      */       try {
/*  461 */         return this.chunkSection.isSolid(0);
/*      */       } finally {
/*  463 */         this.chunkSectionLock.unlockRead(lock);
/*      */       } 
/*      */     } 
/*      */     
/*  467 */     return isSolid;
/*      */   }
/*      */   
/*      */   public void find(IntList ids, IntSet internalIdHolder, IntConsumer indexConsumer) {
/*  471 */     long lock = this.chunkSectionLock.readLock();
/*      */     try {
/*  473 */       this.chunkSection.find(ids, internalIdHolder, indexConsumer);
/*      */     } finally {
/*  475 */       this.chunkSectionLock.unlockRead(lock);
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean setTicking(int blockIdx, boolean ticking) {
/*  480 */     long readStamp = this.chunkSectionLock.readLock();
/*      */     try {
/*  482 */       if (this.tickingBlocks.get(blockIdx) == ticking) {
/*  483 */         return false;
/*      */       }
/*      */     } finally {
/*  486 */       this.chunkSectionLock.unlockRead(readStamp);
/*      */     } 
/*      */     
/*  489 */     long writeStamp = this.chunkSectionLock.writeLock();
/*      */     try {
/*  491 */       if (this.tickingBlocks.get(blockIdx) != ticking) {
/*  492 */         if (ticking) {
/*  493 */           this.tickingBlocksCount++;
/*      */         } else {
/*  495 */           this.tickingBlocksCount--;
/*      */         } 
/*  497 */         this.tickingBlocks.set(blockIdx, ticking);
/*  498 */         return true;
/*      */       } 
/*  500 */       return false;
/*      */     } finally {
/*  502 */       this.chunkSectionLock.unlockWrite(writeStamp);
/*      */     } 
/*      */   }
/*      */   
/*      */   public int getTickingBlocksCount() {
/*  507 */     return (this.tickingBlocksCount > 0) ? this.tickingBlocksCount : 0;
/*      */   }
/*      */   
/*      */   public int getTickingBlocksCountCopy() {
/*  511 */     return this.tickingBlocksCountCopy;
/*      */   }
/*      */   
/*      */   public boolean hasTicking() {
/*  515 */     return (this.tickingBlocksCount > 0);
/*      */   }
/*      */   
/*      */   public boolean isTicking(int blockIdx) {
/*  519 */     if (this.tickingBlocksCount > 0) {
/*  520 */       long readStamp = this.chunkSectionLock.readLock();
/*      */       try {
/*  522 */         return this.tickingBlocks.get(blockIdx);
/*      */       } finally {
/*  524 */         this.chunkSectionLock.unlockRead(readStamp);
/*      */       } 
/*      */     } 
/*  527 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void scheduleTick(int index, @Nullable Instant gameTime) {
/*  532 */     if (gameTime == null) {
/*      */       return;
/*      */     }
/*      */     
/*  536 */     this.tickRequests.enqueue(new TickRequest(index, gameTime));
/*      */   }
/*      */   
/*      */   public void preTick(Instant gameTime) {
/*      */     TickRequest request;
/*  541 */     while (!this.tickRequests.isEmpty() && (request = (TickRequest)this.tickRequests.first()).requestedGameTime.isBefore(gameTime)) {
/*  542 */       this.tickRequests.dequeue();
/*  543 */       setTicking(request.index, true);
/*      */     } 
/*      */     
/*  546 */     long writeStamp = this.chunkSectionLock.writeLock();
/*      */     
/*      */     try {
/*  549 */       if (this.tickingBlocksCount == 0) {
/*  550 */         this.tickingBlocksCountCopy = 0;
/*      */         
/*      */         return;
/*      */       } 
/*  554 */       BitSetUtil.copyValues(this.tickingBlocks, this.tickingBlocksCopy);
/*  555 */       this.tickingBlocksCountCopy = this.tickingBlocksCount;
/*      */       
/*  557 */       this.tickingBlocks.clear();
/*  558 */       this.tickingBlocksCount = 0;
/*      */     } finally {
/*  560 */       this.chunkSectionLock.unlockWrite(writeStamp);
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
/*      */   public <T, V> int forEachTicking(T t, V v, int sectionIndex, @Nonnull ObjectPositionBlockFunction<T, V, BlockTickStrategy> acceptor) {
/*  574 */     if (this.tickingBlocksCountCopy == 0) return 0;
/*      */     
/*  576 */     int sectionStartYBlock = sectionIndex << 5;
/*      */     
/*  578 */     int ticked = 0; int index;
/*  579 */     for (index = this.tickingBlocksCopy.nextSetBit(0); index >= 0; index = this.tickingBlocksCopy.nextSetBit(index + 1)) {
/*  580 */       int x = ChunkUtil.xFromIndex(index);
/*  581 */       int y = ChunkUtil.yFromIndex(index);
/*  582 */       int z = ChunkUtil.zFromIndex(index);
/*  583 */       BlockTickStrategy strategy = (BlockTickStrategy)acceptor.accept(t, v, x, y | sectionStartYBlock, z, get(index));
/*      */       
/*  585 */       long writeStamp = this.chunkSectionLock.writeLock();
/*      */       try {
/*  587 */         switch (strategy) {
/*      */           case WAIT_FOR_ADJACENT_CHUNK_LOAD:
/*  589 */             if (!this.tickingWaitAdjacentBlocks.get(index)) {
/*  590 */               this.tickingWaitAdjacentBlockCount++;
/*  591 */               this.tickingWaitAdjacentBlocks.set(index, true);
/*      */             } 
/*      */             break;
/*      */           case CONTINUE:
/*  595 */             if (!this.tickingBlocks.get(index)) {
/*  596 */               this.tickingBlocksCount++;
/*  597 */               this.tickingBlocks.set(index, true);
/*      */             } 
/*      */             break;
/*      */         } 
/*      */ 
/*      */       
/*      */       } finally {
/*  604 */         this.chunkSectionLock.unlockWrite(writeStamp);
/*      */       } 
/*  606 */       ticked++;
/*      */     } 
/*  608 */     return ticked;
/*      */   }
/*      */   
/*      */   public void mergeTickingBlocks() {
/*  612 */     long writeStamp = this.chunkSectionLock.writeLock();
/*      */     try {
/*  614 */       this.tickingBlocks.or(this.tickingWaitAdjacentBlocks);
/*  615 */       this.tickingBlocksCount = this.tickingBlocks.cardinality();
/*      */       
/*  617 */       this.tickingWaitAdjacentBlocks.clear();
/*  618 */       this.tickingWaitAdjacentBlockCount = 0;
/*      */     } finally {
/*  620 */       this.chunkSectionLock.unlockWrite(writeStamp);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public double getMaximumHitboxExtent() {
/*  626 */     double extent = this.maximumHitboxExtent;
/*  627 */     if (extent != -1.0D) return extent;
/*      */     
/*  629 */     double maximumExtent = BlockBoundingBoxes.UNIT_BOX_MAXIMUM_EXTENT;
/*  630 */     long lock = this.chunkSectionLock.readLock();
/*      */ 
/*      */     
/*  633 */     try { IndexedLookupTableAssetMap<String, BlockBoundingBoxes> hitBoxAssetMap = BlockBoundingBoxes.getAssetMap();
/*  634 */       BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
/*      */       
/*  636 */       for (int idx = 0; idx < 32768; idx++) {
/*  637 */         int blockId = this.chunkSection.get(idx);
/*  638 */         if (blockId != 0) {
/*  639 */           int rotation = this.rotationSection.get(idx);
/*  640 */           BlockType blockType = (BlockType)blockTypeMap.getAsset(blockId);
/*  641 */           if (blockType != null && !blockType.isUnknown())
/*  642 */           { BlockBoundingBoxes asset = (BlockBoundingBoxes)hitBoxAssetMap.getAsset(blockType.getHitboxTypeIndex());
/*  643 */             if (asset != BlockBoundingBoxes.UNIT_BOX)
/*  644 */             { double boxMaximumExtent = asset.get(rotation).getBoundingBox().getMaximumExtent();
/*  645 */               if (boxMaximumExtent > maximumExtent) maximumExtent = boxMaximumExtent;  }  } 
/*      */         } 
/*      */       }  }
/*  648 */     finally { this.chunkSectionLock.unlockRead(lock); }
/*      */ 
/*      */     
/*  651 */     return this.maximumHitboxExtent = maximumExtent;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void invalidateBlock(int x, int y, int z) {
/*  660 */     long stamp = this.chunkSectionLock.writeLock();
/*      */     try {
/*  662 */       this.changedPositions.add(ChunkUtil.indexBlock(x, y, z));
/*      */     } finally {
/*  664 */       this.chunkSectionLock.unlockWrite(stamp);
/*      */     } 
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   @Deprecated(forRemoval = true)
/*      */   public FluidSection takeMigratedFluid() {
/*  671 */     FluidSection temp = this.migratedFluidSection;
/*  672 */     this.migratedFluidSection = null;
/*  673 */     return temp;
/*      */   }
/*      */   
/*      */   @Nullable
/*      */   @Deprecated(forRemoval = true)
/*      */   public BlockPhysics takeMigratedDecoBlocks() {
/*  679 */     BlockPhysics temp = this.migratedBlockPhysics;
/*  680 */     this.migratedBlockPhysics = null;
/*  681 */     return temp;
/*      */   }
/*      */   
/*      */   public void serializeForPacket(@Nonnull ByteBuf buf) {
/*  685 */     long lock = this.chunkSectionLock.readLock();
/*      */     try {
/*  687 */       PaletteType paletteType = this.chunkSection.getPaletteType();
/*  688 */       byte paletteTypeId = (byte)paletteType.ordinal();
/*  689 */       buf.writeByte(paletteTypeId);
/*  690 */       this.chunkSection.serializeForPacket(buf);
/*      */       
/*  692 */       PaletteType fillerType = this.fillerSection.getPaletteType();
/*  693 */       byte fillerTypeId = (byte)fillerType.ordinal();
/*  694 */       buf.writeByte(fillerTypeId);
/*  695 */       this.fillerSection.serializeForPacket(buf);
/*      */       
/*  697 */       PaletteType rotationType = this.rotationSection.getPaletteType();
/*  698 */       byte rotationTypeId = (byte)rotationType.ordinal();
/*  699 */       buf.writeByte(rotationTypeId);
/*  700 */       this.rotationSection.serializeForPacket(buf);
/*      */     } finally {
/*  702 */       this.chunkSectionLock.unlockRead(lock);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void serialize(ISectionPalette.KeySerializer keySerializer, @Nonnull ByteBuf buf) {
/*  707 */     long lock = this.chunkSectionLock.readLock();
/*      */     try {
/*  709 */       buf.writeInt(BlockMigration.getAssetMap().getAssetCount());
/*  710 */       PaletteType paletteType = this.chunkSection.getPaletteType();
/*  711 */       buf.writeByte(paletteType.ordinal());
/*  712 */       this.chunkSection.serialize(keySerializer, buf);
/*      */       
/*  714 */       if (paletteType != PaletteType.Empty) {
/*  715 */         BitSet combinedTickingBlock = (BitSet)this.tickingBlocks.clone();
/*  716 */         combinedTickingBlock.or(this.tickingWaitAdjacentBlocks);
/*      */         
/*  718 */         buf.writeShort(combinedTickingBlock.cardinality());
/*      */         
/*  720 */         long[] data = combinedTickingBlock.toLongArray();
/*  721 */         buf.writeShort(data.length);
/*  722 */         for (long l : data) {
/*  723 */           buf.writeLong(l);
/*      */         }
/*      */       } 
/*      */       
/*  727 */       buf.writeByte(this.fillerSection.getPaletteType().ordinal());
/*  728 */       this.fillerSection.serialize(ByteBuf::writeShort, buf);
/*      */       
/*  730 */       buf.writeByte(this.rotationSection.getPaletteType().ordinal());
/*  731 */       this.rotationSection.serialize(ByteBuf::writeByte, buf);
/*      */       
/*  733 */       this.localLight.serialize(buf);
/*  734 */       this.globalLight.serialize(buf);
/*  735 */       buf.writeShort(this.localChangeCounter);
/*  736 */       buf.writeShort(this.globalChangeCounter);
/*      */     } finally {
/*  738 */       this.chunkSectionLock.unlockRead(lock);
/*      */     } 
/*      */   }
/*      */   
/*      */   public byte[] serialize(ExtraInfo extraInfo) {
/*  743 */     ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
/*      */     
/*      */     try {
/*  746 */       serialize(BlockType.KEY_SERIALIZER, buf);
/*  747 */       return ByteBufUtil.getBytesRelease(buf);
/*  748 */     } catch (Throwable t) {
/*  749 */       buf.release();
/*  750 */       throw SneakyThrow.sneakyThrow(t);
/*      */     } 
/*      */   }
/*      */   
/*      */   public void deserialize(ToIntFunction<ByteBuf> keyDeserializer, @Nonnull ByteBuf buf, int version) {
/*  755 */     int blockMigrationVersion = 0;
/*  756 */     if (version >= 6) {
/*  757 */       blockMigrationVersion = buf.readInt();
/*      */     }
/*      */     
/*  760 */     Function<String, String> blockMigration = null;
/*  761 */     Map<Integer, BlockMigration> blockMigrationMap = BlockMigration.getAssetMap().getAssetMap();
/*  762 */     BlockMigration migration = blockMigrationMap.get(Integer.valueOf(blockMigrationVersion));
/*  763 */     while (migration != null) {
/*  764 */       if (blockMigration == null) {
/*  765 */         Objects.requireNonNull(migration); blockMigration = migration::getMigration;
/*      */       } else {
/*  767 */         Objects.requireNonNull(migration); blockMigration = blockMigration.andThen(migration::getMigration);
/*      */       } 
/*  769 */       migration = blockMigrationMap.get(Integer.valueOf(++blockMigrationVersion));
/*      */     } 
/*      */     
/*  772 */     PaletteTypeEnum typeEnum = PaletteTypeEnum.get(buf.readByte());
/*  773 */     PaletteType paletteType = typeEnum.getPaletteType();
/*  774 */     this.chunkSection = typeEnum.getConstructor().get();
/*  775 */     if (version <= 4) {
/*      */       
/*  777 */       ISectionPalette tempSection = typeEnum.getConstructor().get();
/*  778 */       boolean[] foundMigratable = { false };
/*  779 */       boolean[] needsPhysics = { false };
/*  780 */       int[] nextTempIndex = { -1 };
/*  781 */       Int2ObjectOpenHashMap<String> types = new Int2ObjectOpenHashMap();
/*  782 */       Object2IntOpenHashMap<String> typesRev = new Object2IntOpenHashMap();
/*  783 */       typesRev.defaultReturnValue(-2147483648);
/*  784 */       Function<String, String> finalBlockMigration = blockMigration;
/*  785 */       tempSection.deserialize(bytebuf -> {
/*      */             String key = ByteBufUtil.readUTF(bytebuf);
/*      */             if (finalBlockMigration != null) {
/*      */               key = finalBlockMigration.apply(key);
/*      */             }
/*      */             int index = typesRev.getInt(key);
/*      */             if (index != Integer.MIN_VALUE)
/*      */               return index; 
/*  793 */             boolean migratable = (key.startsWith("Fluid_") || key.contains("|Fluid=") || key.contains("|Deco") || key.contains("|Support") || key.contains("|Filler") || key.contains("|Yaw=") || key.contains("|Pitch=") || key.contains("|Roll="));
/*      */             
/*      */             foundMigratable[0] = foundMigratable[0] | migratable;
/*      */             
/*      */             if (migratable) {
/*      */               nextTempIndex[0] = nextTempIndex[0] - 1;
/*      */               
/*      */               index = nextTempIndex[0];
/*      */             } else {
/*      */               index = BlockType.getBlockIdOrUnknown(key, "Unknown BlockType %s", new Object[] { key });
/*      */               
/*      */               needsPhysics[0] = needsPhysics[0] | ((BlockType)BlockType.getAssetMap().getAsset(index)).hasSupport();
/*      */             } 
/*      */             types.put(index, key);
/*      */             typesRev.put(key, index);
/*      */             return index;
/*      */           }buf, version);
/*  810 */       if (needsPhysics[0]) {
/*  811 */         this.migratedBlockPhysics = new BlockPhysics();
/*      */       }
/*      */       
/*  814 */       if (foundMigratable[0]) {
/*  815 */         int index = 0; while (true) { if (index < 32768)
/*  816 */           { int id = tempSection.get(index);
/*  817 */             if (id >= 0)
/*  818 */             { this.chunkSection.set(index, id); }
/*      */             
/*      */             else
/*      */             
/*  822 */             { Rotation rotationYaw = Rotation.None;
/*  823 */               Rotation rotationPitch = Rotation.None;
/*  824 */               Rotation rotationRoll = Rotation.None;
/*      */               
/*  826 */               String key = (String)types.get(id);
/*  827 */               if (key.startsWith("Fluid_") || key.contains("|Fluid=")) {
/*  828 */                 if (this.migratedFluidSection == null) {
/*  829 */                   this.migratedFluidSection = new FluidSection();
/*      */                 }
/*      */                 
/*  832 */                 Fluid.ConversionResult result = Fluid.convertBlockToFluid(key);
/*  833 */                 if (result == null) {
/*  834 */                   throw new RuntimeException("Invalid Fluid Key " + key);
/*      */                 }
/*      */                 
/*  837 */                 if (result.blockTypeStr != null) {
/*  838 */                   key = result.blockTypeStr;
/*  839 */                   this.migratedFluidSection.setFluid(index, result.fluidId, result.fluidLevel);
/*      */                 } else {
/*  841 */                   this.migratedFluidSection.setFluid(index, result.fluidId, result.fluidLevel);
/*      */                   
/*      */                   index++;
/*      */                 } 
/*      */               } 
/*  846 */               if (key.contains("|Deco")) {
/*  847 */                 if (this.migratedBlockPhysics == null) {
/*  848 */                   this.migratedBlockPhysics = new BlockPhysics();
/*      */                 }
/*  850 */                 this.migratedBlockPhysics.set(index, 15);
/*      */               } 
/*  852 */               if (key.contains("|Support=")) {
/*  853 */                 if (this.migratedBlockPhysics == null) {
/*  854 */                   this.migratedBlockPhysics = new BlockPhysics();
/*      */                 }
/*  856 */                 int start = key.indexOf("|Support=") + "|Support=".length();
/*  857 */                 int end = key.indexOf('|', start);
/*  858 */                 if (end == -1) end = key.length(); 
/*  859 */                 this.migratedBlockPhysics.set(index, Integer.parseInt(key, start, end, 10));
/*      */               } 
/*  861 */               if (key.contains("|Filler=")) {
/*  862 */                 int start = key.indexOf("|Filler=") + "|Filler=".length();
/*  863 */                 int firstComma = key.indexOf(',', start);
/*  864 */                 if (firstComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing comma"); 
/*  865 */                 int secondComma = key.indexOf(',', firstComma + 1);
/*  866 */                 if (secondComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing second comma");
/*      */                 
/*  868 */                 int end = key.indexOf('|', start);
/*  869 */                 if (end == -1) end = key.length();
/*      */                 
/*  871 */                 int fillerX = Integer.parseInt(key, start, firstComma, 10);
/*  872 */                 int fillerY = Integer.parseInt(key, firstComma + 1, secondComma, 10);
/*  873 */                 int fillerZ = Integer.parseInt(key, secondComma + 1, end, 10);
/*      */                 
/*  875 */                 int filler = FillerBlockUtil.pack(fillerX, fillerY, fillerZ);
/*  876 */                 ISectionPalette.SetResult result = this.fillerSection.set(index, filler);
/*  877 */                 if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
/*  878 */                   this.fillerSection = this.fillerSection.promote();
/*  879 */                   this.fillerSection.set(index, filler);
/*      */                 } 
/*      */               } 
/*      */ 
/*      */               
/*  884 */               if (key.contains("|Yaw=")) {
/*  885 */                 int start = key.indexOf("|Yaw=") + "|Yaw=".length();
/*  886 */                 int end = key.indexOf('|', start);
/*  887 */                 if (end == -1) end = key.length(); 
/*  888 */                 rotationYaw = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
/*      */               } 
/*  890 */               if (key.contains("|Pitch=")) {
/*  891 */                 int start = key.indexOf("|Pitch=") + "|Pitch=".length();
/*  892 */                 int end = key.indexOf('|', start);
/*  893 */                 if (end == -1) end = key.length(); 
/*  894 */                 rotationPitch = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
/*      */               } 
/*  896 */               if (key.contains("|Roll=")) {
/*  897 */                 int start = key.indexOf("|Roll=") + "|Roll=".length();
/*  898 */                 int end = key.indexOf('|', start);
/*  899 */                 if (end == -1) end = key.length(); 
/*  900 */                 rotationRoll = Rotation.ofDegrees(Integer.parseInt(key, start, end, 10));
/*      */               } 
/*      */               
/*  903 */               if (rotationYaw != Rotation.None || rotationPitch != Rotation.None || rotationRoll != Rotation.None) {
/*  904 */                 int rotation = RotationTuple.index(rotationYaw, rotationPitch, rotationRoll);
/*  905 */                 ISectionPalette.SetResult result = this.rotationSection.set(index, rotation);
/*  906 */                 if (result == ISectionPalette.SetResult.REQUIRES_PROMOTE) {
/*  907 */                   this.rotationSection = this.rotationSection.promote();
/*  908 */                   this.rotationSection.set(index, rotation);
/*      */                 } 
/*      */               } 
/*      */               
/*  912 */               int endOfName = key.indexOf('|');
/*  913 */               if (endOfName != -1) {
/*  914 */                 key = key.substring(0, endOfName);
/*      */               }
/*  916 */               this.chunkSection.set(index, BlockType.getBlockIdOrUnknown(key, "Unknown BlockType: %s", new Object[] { key })); }  }
/*      */           else { break; }
/*      */            index++; }
/*  919 */          if (this.chunkSection.shouldDemote()) {
/*  920 */           this.chunkSection.demote();
/*      */         }
/*      */       } else {
/*      */         
/*  924 */         this.chunkSection = tempSection;
/*      */       }
/*      */     
/*  927 */     } else if (blockMigration != null) {
/*  928 */       Function<String, String> finalBlockMigration1 = blockMigration;
/*  929 */       this.chunkSection.deserialize(bytebuf -> { String key = ByteBufUtil.readUTF(bytebuf); key = finalBlockMigration1.apply(key); return BlockType.getBlockIdOrUnknown(key, "Unknown BlockType %s", new Object[] { key }); }buf, version);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/*  935 */       this.chunkSection.deserialize(keyDeserializer, buf, version);
/*      */     } 
/*      */ 
/*      */     
/*  939 */     if (paletteType != PaletteType.Empty) {
/*      */       
/*  941 */       this.tickingBlocksCount = buf.readUnsignedShort();
/*  942 */       int len = buf.readUnsignedShort();
/*  943 */       long[] tickingBlocksData = new long[len];
/*  944 */       for (int i = 0; i < tickingBlocksData.length; i++) {
/*  945 */         tickingBlocksData[i] = buf.readLong();
/*      */       }
/*  947 */       this.tickingBlocks = BitSet.valueOf(tickingBlocksData);
/*  948 */       this.tickingBlocksCount = this.tickingBlocks.cardinality();
/*      */     } 
/*      */     
/*  951 */     if (version >= 4) {
/*  952 */       PaletteTypeEnum fillerTypeEnum = PaletteTypeEnum.get(buf.readByte());
/*  953 */       this.fillerSection = fillerTypeEnum.getConstructor().get();
/*  954 */       this.fillerSection.deserialize(ByteBuf::readUnsignedShort, buf, version);
/*      */     } 
/*      */     
/*  957 */     if (version >= 5) {
/*  958 */       PaletteTypeEnum rotationTypeEnum = PaletteTypeEnum.get(buf.readByte());
/*  959 */       this.rotationSection = rotationTypeEnum.getConstructor().get();
/*  960 */       this.rotationSection.deserialize(ByteBuf::readUnsignedByte, buf, version);
/*      */     } 
/*      */     
/*  963 */     this.localLight = ChunkLightData.deserialize(buf, version);
/*  964 */     this.globalLight = ChunkLightData.deserialize(buf, version);
/*      */     
/*  966 */     this.localChangeCounter = buf.readShort();
/*  967 */     this.globalChangeCounter = buf.readShort();
/*      */   }
/*      */   
/*      */   public void deserialize(@Nonnull byte[] bytes, @Nonnull ExtraInfo extraInfo) {
/*  971 */     ByteBuf buf = Unpooled.wrappedBuffer(bytes);
/*  972 */     deserialize(BlockType.KEY_DESERIALIZER, buf, extraInfo.getVersion());
/*      */   }
/*      */ 
/*      */   
/*      */   public Component<ChunkStore> clone() {
/*  977 */     throw new UnsupportedOperationException("Not implemented!");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Component<ChunkStore> cloneSerializable() {
/*  986 */     return this;
/*      */   }
/*      */   
/*      */   @Nonnull
/*      */   public CompletableFuture<CachedPacket<SetChunk>> getCachedChunkPacket(int x, int y, int z) {
/*  991 */     SoftReference<CompletableFuture<CachedPacket<SetChunk>>> ref = this.cachedChunkPacket;
/*  992 */     CompletableFuture<CachedPacket<SetChunk>> future = (ref != null) ? ref.get() : null;
/*  993 */     if (future != null) return future;
/*      */     
/*  995 */     future = CompletableFuture.supplyAsync(() -> {
/*      */           byte[] localLightArr = null;
/*      */           
/*      */           byte[] globalLightArr = null;
/*      */           
/*      */           byte[] data = null;
/*      */           
/*      */           if (BlockChunk.SEND_LOCAL_LIGHTING_DATA) {
/*      */             if (hasLocalLight()) {
/*      */               ChunkLightData localLight = getLocalLight();
/*      */               
/*      */               ByteBuf buffer = Unpooled.buffer();
/*      */               
/*      */               localLight.serializeForPacket(buffer);
/*      */               
/*      */               if (getLocalChangeCounter() == localLight.getChangeId()) {
/*      */                 localLightArr = ByteBufUtil.getBytesRelease(buffer);
/*      */               }
/*      */             } 
/*      */           }
/*      */           
/*      */           if (BlockChunk.SEND_GLOBAL_LIGHTING_DATA) {
/*      */             if (hasGlobalLight()) {
/*      */               ByteBuf buffer = Unpooled.buffer();
/*      */               
/*      */               ChunkLightData globalLight = getGlobalLight();
/*      */               globalLight.serializeForPacket(buffer);
/*      */               if (getGlobalChangeCounter() == globalLight.getChangeId()) {
/*      */                 globalLightArr = ByteBufUtil.getBytesRelease(buffer);
/*      */               }
/*      */             } 
/*      */           }
/*      */           if (!isSolidAir()) {
/*      */             ByteBuf buf = Unpooled.buffer(65536);
/*      */             serializeForPacket(buf);
/*      */             data = ByteBufUtil.getBytesRelease(buf);
/*      */           } 
/*      */           SetChunk setChunk = new SetChunk(x, y, z, localLightArr, globalLightArr, data);
/*      */           return CachedPacket.cache((Packet)setChunk);
/*      */         });
/* 1035 */     this.cachedChunkPacket = new SoftReference<>(future);
/* 1036 */     return future;
/*      */   }
/*      */   
/*      */   public int get(int x, int y, int z) {
/* 1040 */     return get(ChunkUtil.indexBlock(x, y, z));
/*      */   }
/*      */   
/*      */   public boolean set(int x, int y, int z, int blockId, int rotation, int filler) {
/* 1044 */     return set(ChunkUtil.indexBlock(x, y, z), blockId, rotation, filler);
/*      */   }
/*      */   
/*      */   public boolean setTicking(int x, int y, int z, boolean ticking) {
/* 1048 */     return setTicking(ChunkUtil.indexBlock(x, y, z), ticking);
/*      */   }
/*      */   
/*      */   public boolean isTicking(int x, int y, int z) {
/* 1052 */     return isTicking(ChunkUtil.indexBlock(x, y, z));
/*      */   } private static final class TickRequest extends Record { private final int index; @Nonnull
/*      */     private final Instant requestedGameTime;
/* 1055 */     private TickRequest(int index, @Nonnull Instant requestedGameTime) { this.index = index; this.requestedGameTime = requestedGameTime; } public final String toString() { // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest;)Ljava/lang/String;
/*      */       //   6: areturn
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #1055	-> 0
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/* 1055 */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest; } public int index() { return this.index; } public final int hashCode() { // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest;)I
/*      */       //   6: ireturn
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #1055	-> 0
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest; } public final boolean equals(Object o) { // Byte code:
/*      */       //   0: aload_0
/*      */       //   1: aload_1
/*      */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest;Ljava/lang/Object;)Z
/*      */       //   7: ireturn
/*      */       // Line number table:
/*      */       //   Java source line number -> byte code offset
/*      */       //   #1055	-> 0
/*      */       // Local variable table:
/*      */       //   start	length	slot	name	descriptor
/*      */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/universe/world/chunk/section/BlockSection$TickRequest;
/* 1055 */       //   0	8	1	o	Ljava/lang/Object; } @Nonnull public Instant requestedGameTime() { return this.requestedGameTime; }
/*      */      }
/*      */   
/*      */   static {
/* 1059 */     TICK_REQUEST_COMPARATOR = Comparator.comparing(t -> t.requestedGameTime);
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\chunk\section\BlockSection.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */