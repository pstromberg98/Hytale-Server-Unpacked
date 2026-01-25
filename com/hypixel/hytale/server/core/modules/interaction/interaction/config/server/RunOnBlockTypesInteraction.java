/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.SimpleInteraction;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
/*     */ import com.hypixel.hytale.server.core.meta.MetaKey;
/*     */ import com.hypixel.hytale.server.core.modules.blockset.BlockSetModule;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntCollection;
/*     */ import it.unimi.dsi.fastutil.ints.IntConsumer;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.ints.IntSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.function.IntConsumer;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RunOnBlockTypesInteraction
/*     */   extends SimpleInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<RunOnBlockTypesInteraction> CODEC;
/*     */   
/*     */   static {
/*  91 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RunOnBlockTypesInteraction.class, RunOnBlockTypesInteraction::new, SimpleInteraction.CODEC).documentation("Searches for matching block types within a radius and runs interactions on each found block up to a configured maximum number of blocks.")).appendInherited(new KeyedCodec("Range", (Codec)Codec.INTEGER, true), (interaction, value) -> interaction.range = value.intValue(), interaction -> Integer.valueOf(interaction.range), (interaction, parent) -> interaction.range = parent.range).documentation("The spherical radius to search for matching block types.").addValidator(Validators.greaterThan(Integer.valueOf(0))).add()).appendInherited(new KeyedCodec("BlockSets", (Codec)new ArrayCodec((Codec)Codec.STRING, x$0 -> new String[x$0]), true), (interaction, value) -> interaction.blockSets = value, interaction -> interaction.blockSets, (interaction, parent) -> interaction.blockSets = parent.blockSets).documentation("Array of BlockSet IDs to match within the search radius.").addValidator(Validators.nonEmptyArray()).addValidatorLate(() -> BlockSet.VALIDATOR_CACHE.getArrayValidator().late()).add()).appendInherited(new KeyedCodec("MaxCount", (Codec)Codec.INTEGER, true), (interaction, value) -> interaction.maxCount = value.intValue(), interaction -> Integer.valueOf(interaction.maxCount), (interaction, parent) -> interaction.maxCount = parent.maxCount).documentation("Maximum number of block positions to select for running interactions (uses reservoir sampling).").addValidator(Validators.greaterThan(Integer.valueOf(0))).add()).appendInherited(new KeyedCodec("Interactions", (Codec)RootInteraction.CHILD_ASSET_CODEC, true), (interaction, value) -> interaction.interactions = value, interaction -> interaction.interactions, (interaction, parent) -> interaction.interactions = parent.interactions).documentation("The interaction chain to run on each found block. Can be defined inline or as a reference.").addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).build();
/*     */   }
/*  93 */   private static final MetaKey<List<InteractionChain>> FORKED_CHAINS = Interaction.META_REGISTRY.registerMetaObject(i -> null);
/*  94 */   private static final MetaKey<Boolean> ANY_SUCCEEDED = Interaction.META_REGISTRY.registerMetaObject(i -> Boolean.FALSE);
/*  95 */   public static final String[] EMPTY_BLOCKSETS = new String[0];
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int range;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/* 105 */   protected String[] blockSets = EMPTY_BLOCKSETS;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected int maxCount;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String interactions;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 125 */     return WaitForDataFrom.Server;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @Nonnull InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
/* 130 */     DynamicMetaStore<Interaction> instanceStore = context.getInstanceStore();
/*     */     
/* 132 */     if (firstRun) {
/* 133 */       CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 134 */       assert commandBuffer != null;
/*     */       
/* 136 */       Ref<EntityStore> ref = context.getEntity();
/* 137 */       TransformComponent transformComponent = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 138 */       if (transformComponent == null) {
/* 139 */         (context.getState()).state = InteractionState.Failed;
/* 140 */         super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */         
/*     */         return;
/*     */       } 
/* 144 */       World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/* 145 */       Vector3d position = transformComponent.getPosition();
/*     */ 
/*     */       
/* 148 */       List<Vector3i> selectedPositions = searchBlocks(world, position);
/* 149 */       if (selectedPositions.isEmpty()) {
/* 150 */         (context.getState()).state = InteractionState.Failed;
/* 151 */         super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */         
/*     */         return;
/*     */       } 
/* 155 */       if (this.interactions == null) {
/* 156 */         (context.getState()).state = InteractionState.Failed;
/* 157 */         super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 162 */       RootInteraction rootInteraction = RootInteraction.getRootInteractionOrUnknown(this.interactions);
/* 163 */       ObjectArrayList<InteractionChain> objectArrayList = new ObjectArrayList(selectedPositions.size());
/*     */       
/* 165 */       for (Vector3i blockPos : selectedPositions) {
/*     */         
/* 167 */         InteractionContext forkedContext = context.duplicate();
/* 168 */         BlockPosition blockPosition = new BlockPosition(blockPos.x, blockPos.y, blockPos.z);
/* 169 */         forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, blockPosition);
/* 170 */         forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, blockPosition);
/*     */ 
/*     */         
/* 173 */         BlockPosition baseBlock = world.getBaseBlock(blockPosition);
/* 174 */         forkedContext.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, baseBlock);
/*     */         
/* 176 */         InteractionChain chain = context.fork(forkedContext, rootInteraction, false);
/* 177 */         objectArrayList.add(chain);
/*     */       } 
/*     */       
/* 180 */       instanceStore.putMetaObject(FORKED_CHAINS, objectArrayList);
/* 181 */       instanceStore.putMetaObject(ANY_SUCCEEDED, Boolean.FALSE);
/* 182 */       (context.getState()).state = InteractionState.NotFinished;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 187 */     List<InteractionChain> chains = (List<InteractionChain>)instanceStore.getMetaObject(FORKED_CHAINS);
/* 188 */     if (chains == null || chains.isEmpty()) {
/* 189 */       (context.getState()).state = InteractionState.Failed;
/* 190 */       super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */       
/*     */       return;
/*     */     } 
/* 194 */     boolean allFinished = true;
/* 195 */     boolean anySucceeded = ((Boolean)instanceStore.getMetaObject(ANY_SUCCEEDED)).booleanValue();
/*     */     
/* 197 */     for (InteractionChain chain : chains) {
/* 198 */       switch (chain.getServerState()) { case NotFinished:
/* 199 */           allFinished = false;
/* 200 */         case Finished: anySucceeded = true; }
/*     */ 
/*     */ 
/*     */     
/*     */     } 
/* 205 */     instanceStore.putMetaObject(ANY_SUCCEEDED, Boolean.valueOf(anySucceeded));
/*     */     
/* 207 */     if (!allFinished) {
/* 208 */       (context.getState()).state = InteractionState.NotFinished;
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 213 */     if (anySucceeded) {
/* 214 */       (context.getState()).state = InteractionState.Finished;
/*     */     } else {
/* 216 */       (context.getState()).state = InteractionState.Failed;
/*     */     } 
/* 218 */     super.tick0(firstRun, time, type, context, cooldownHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @NonNullDecl InteractionType type, @Nonnull InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 230 */     if (this.next == null && this.failed == null) {
/* 231 */       builder.addOperation((Operation)this);
/*     */       
/*     */       return;
/*     */     } 
/* 235 */     Label failedLabel = builder.createUnresolvedLabel();
/* 236 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 238 */     builder.addOperation((Operation)this, new Label[] { failedLabel });
/*     */ 
/*     */     
/* 241 */     if (this.next != null) {
/* 242 */       Interaction nextInteraction = Interaction.getInteractionOrUnknown(this.next);
/* 243 */       nextInteraction.compile(builder);
/*     */     } 
/*     */     
/* 246 */     if (this.failed != null) builder.jump(endLabel);
/*     */ 
/*     */     
/* 249 */     builder.resolveLabel(failedLabel);
/* 250 */     if (this.failed != null) {
/* 251 */       Interaction failedInteraction = Interaction.getInteractionOrUnknown(this.failed);
/* 252 */       failedInteraction.compile(builder);
/*     */     } 
/*     */     
/* 255 */     builder.resolveLabel(endLabel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private List<Vector3i> searchBlocks(@Nonnull World world, @Nonnull Vector3d position) {
/* 267 */     IntList blockIds = getBlockIds();
/* 268 */     if (blockIds.isEmpty()) {
/* 269 */       return List.of();
/*     */     }
/*     */     
/* 272 */     int originX = MathUtil.floor(position.x);
/* 273 */     int originY = MathUtil.floor(position.y);
/* 274 */     int originZ = MathUtil.floor(position.z);
/* 275 */     int radiusSquared = this.range * this.range;
/*     */     
/* 277 */     BlockSearchConsumer consumer = new BlockSearchConsumer(originX, originY, originZ, radiusSquared, this.maxCount);
/* 278 */     IntOpenHashSet internalIdHolder = new IntOpenHashSet();
/*     */     
/* 280 */     int minY = Math.max(0, originY - this.range);
/* 281 */     int maxY = Math.min(319, originY + this.range);
/*     */ 
/*     */     
/* 284 */     for (int x = originX - this.range & 0xFFFFFFE0; x < originX + this.range; x += 32) {
/* 285 */       for (int z = originZ - this.range & 0xFFFFFFE0; z < originZ + this.range; z += 32) {
/* 286 */         WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
/* 287 */         if (chunk != null) {
/*     */           
/* 289 */           BlockChunk blockChunk = chunk.getBlockChunk();
/*     */ 
/*     */           
/* 292 */           for (int y = minY; y < maxY; y += 32) {
/* 293 */             int sectionIndex = ChunkUtil.indexSection(y);
/* 294 */             if (sectionIndex >= 0 && sectionIndex < 10) {
/*     */               
/* 296 */               BlockSection section = blockChunk.getSectionAtIndex(sectionIndex);
/*     */ 
/*     */               
/* 299 */               if (!section.isSolidAir() && section.containsAny(blockIds))
/*     */               
/* 301 */               { consumer.setSection(x, z, sectionIndex);
/* 302 */                 section.find(blockIds, (IntSet)internalIdHolder, (IntConsumer)consumer);
/* 303 */                 internalIdHolder.clear(); } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 308 */     }  return consumer.getPickedPositions();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private IntList getBlockIds() {
/* 318 */     IntArrayList result = new IntArrayList();
/* 319 */     BlockSetModule blockSetModule = BlockSetModule.getInstance();
/* 320 */     Int2ObjectMap<IntSet> blockSetMap = blockSetModule.getBlockSets();
/*     */     
/* 322 */     for (String blockSetName : this.blockSets) {
/* 323 */       int blockSetIndex = BlockSet.getAssetMap().getIndex(blockSetName);
/* 324 */       if (blockSetIndex != Integer.MIN_VALUE) {
/*     */         
/* 326 */         IntSet blockIdsInSet = (IntSet)blockSetMap.get(blockSetIndex);
/* 327 */         if (blockIdsInSet != null) {
/* 328 */           result.addAll((IntCollection)blockIdsInSet);
/*     */         }
/*     */       } 
/*     */     } 
/* 332 */     return (IntList)result;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 337 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 343 */     return (Interaction)new SimpleInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 348 */     super.configurePacket(packet);
/* 349 */     SimpleInteraction p = (SimpleInteraction)packet;
/* 350 */     p.next = Interaction.getInteractionIdOrUnknown(this.next);
/* 351 */     p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
/*     */   }
/*     */ 
/*     */   
/*     */   private static class BlockSearchConsumer
/*     */     implements IntConsumer
/*     */   {
/*     */     private final int originX;
/*     */     
/*     */     private final int originY;
/*     */     private final int originZ;
/*     */     private final int radiusSquared;
/*     */     private final int maxCount;
/*     */     private final List<Vector3i> picked;
/* 365 */     private int seen = 0;
/*     */     private int chunkWorldX;
/*     */     private int chunkWorldZ;
/*     */     private int sectionBaseY;
/*     */     
/*     */     BlockSearchConsumer(int originX, int originY, int originZ, int radiusSquared, int maxCount) {
/* 371 */       this.originX = originX;
/* 372 */       this.originY = originY;
/* 373 */       this.originZ = originZ;
/* 374 */       this.radiusSquared = radiusSquared;
/* 375 */       this.maxCount = maxCount;
/* 376 */       this.picked = (List<Vector3i>)new ObjectArrayList(maxCount);
/*     */     }
/*     */     
/*     */     void setSection(int chunkWorldX, int chunkWorldZ, int sectionIndex) {
/* 380 */       this.chunkWorldX = chunkWorldX;
/* 381 */       this.chunkWorldZ = chunkWorldZ;
/* 382 */       this.sectionBaseY = sectionIndex * 32;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void accept(int blockIndex) {
/* 388 */       int localX = ChunkUtil.xFromIndex(blockIndex);
/* 389 */       int localY = ChunkUtil.yFromIndex(blockIndex);
/* 390 */       int localZ = ChunkUtil.zFromIndex(blockIndex);
/*     */       
/* 392 */       int worldX = this.chunkWorldX + localX;
/* 393 */       int worldY = this.sectionBaseY + localY;
/* 394 */       int worldZ = this.chunkWorldZ + localZ;
/*     */ 
/*     */       
/* 397 */       int dx = worldX - this.originX;
/* 398 */       int dy = worldY - this.originY;
/* 399 */       int dz = worldZ - this.originZ;
/* 400 */       if (dx * dx + dy * dy + dz * dz > this.radiusSquared) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 405 */       if (this.picked.size() < this.maxCount) {
/* 406 */         this.picked.add(new Vector3i(worldX, worldY, worldZ));
/*     */       } else {
/* 408 */         int j = ThreadLocalRandom.current().nextInt(this.seen + 1);
/* 409 */         if (j < this.maxCount) {
/* 410 */           this.picked.set(j, new Vector3i(worldX, worldY, worldZ));
/*     */         }
/*     */       } 
/* 413 */       this.seen++;
/*     */     }
/*     */     
/*     */     @Nonnull
/*     */     List<Vector3i> getPickedPositions() {
/* 418 */       return this.picked;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 425 */     return "RunOnBlockTypesInteraction{range=" + this.range + ", maxCount=" + this.maxCount + ", interactions='" + this.interactions + "'} " + super
/*     */ 
/*     */ 
/*     */       
/* 429 */       .toString();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\RunOnBlockTypesInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */