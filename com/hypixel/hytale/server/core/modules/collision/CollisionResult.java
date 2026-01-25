/*     */ package com.hypixel.hytale.server.core.modules.collision;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.block.BlockUtil;
/*     */ import com.hypixel.hytale.math.iterator.BoxBlockIterator;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.BlockPosition;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ import it.unimi.dsi.fastutil.longs.LongIterator;
/*     */ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.function.Predicate;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class CollisionResult
/*     */   implements BoxBlockIterator.BoxIterationConsumer {
/*     */   public static final Comparator<BlockCollisionData> BLOCK_COLLISION_DATA_COMPARATOR;
/*     */   
/*     */   static {
/*  42 */     BLOCK_COLLISION_DATA_COMPARATOR = Comparator.<BlockCollisionData>comparingDouble(a -> a.collisionStart).thenComparingDouble(a -> a.collisionEnd);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private final CollisionConfig collisionConfig;
/*     */   
/*     */   @Nonnull
/*     */   private final CollisionDataArray<BlockCollisionData> blockCollisions;
/*     */   
/*     */   @Nonnull
/*     */   private final CollisionDataArray<BlockCollisionData> blockSlides;
/*     */   
/*     */   @Nonnull
/*     */   private final CollisionDataArray<BlockCollisionData> blockTriggers;
/*     */   
/*     */   @Nonnull
/*     */   private final CollisionDataArray<CharacterCollisionData> characterCollisions;
/*     */   
/*     */   @Nonnull
/*     */   private final MovingBoxBoxCollisionEvaluator movingBoxBoxCollision;
/*     */   
/*     */   @Nonnull
/*     */   private final BoxBlockIntersectionEvaluator boxBlockIntersection;
/*     */   
/*     */   public List<Entity> collisionEntities;
/*     */   
/*     */   private boolean continueAfterCollision = true;
/*     */   
/*     */   private boolean haveNoCollision = true;
/*     */   
/*     */   private HytaleLogger logger;
/*     */   public double slideStart;
/*     */   public double slideEnd;
/*     */   public boolean isSliding;
/*     */   public int validate;
/*     */   private boolean checkForCharacterCollisions;
/*     */   private int walkableMaterialMask;
/*     */   public Predicate<CollisionConfig> isNonWalkable;
/*  81 */   private LongSet lastTriggers = (LongSet)new LongOpenHashSet();
/*  82 */   private LongSet newTriggers = (LongSet)new LongOpenHashSet();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CollisionResult() {
/*  88 */     this(true, false);
/*     */   }
/*     */   
/*     */   public CollisionResult(boolean enableSlides, boolean enableCharacters) {
/*  92 */     ObjectArrayList<BlockCollisionData> blockCollisionDataFreePool = new ObjectArrayList();
/*  93 */     ObjectArrayList<CharacterCollisionData> characterCollisionDataFreePool = new ObjectArrayList();
/*     */     
/*  95 */     this.blockCollisions = new CollisionDataArray<>(BlockCollisionData::new, BlockCollisionData::clear, (List<BlockCollisionData>)blockCollisionDataFreePool);
/*  96 */     this.blockSlides = new CollisionDataArray<>(BlockCollisionData::new, BlockCollisionData::clear, (List<BlockCollisionData>)blockCollisionDataFreePool);
/*  97 */     this.blockTriggers = new CollisionDataArray<>(BlockCollisionData::new, BlockCollisionData::clear, (List<BlockCollisionData>)blockCollisionDataFreePool);
/*  98 */     this.characterCollisions = new CollisionDataArray<>(CharacterCollisionData::new, null, (List<CharacterCollisionData>)characterCollisionDataFreePool);
/*     */     
/* 100 */     this.collisionConfig = new CollisionConfig();
/* 101 */     this.collisionConfig.setDefaultCollisionBehaviour();
/* 102 */     this.movingBoxBoxCollision = new MovingBoxBoxCollisionEvaluator();
/* 103 */     this.movingBoxBoxCollision.setCheckForOnGround(enableSlides);
/* 104 */     this.boxBlockIntersection = new BoxBlockIntersectionEvaluator();
/* 105 */     this.checkForCharacterCollisions = enableCharacters;
/*     */     
/* 107 */     setDefaultWalkableBehaviour();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CollisionConfig getConfig() {
/* 112 */     return this.collisionConfig;
/*     */   }
/*     */   
/*     */   public List<Entity> getCollisionEntities() {
/* 116 */     return this.collisionEntities;
/*     */   }
/*     */   
/*     */   public void setCollisionEntities(List<Entity> collisionEntities) {
/* 120 */     this.collisionEntities = collisionEntities;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public BoxBlockIntersectionEvaluator getBoxBlockIntersection() {
/* 125 */     return this.boxBlockIntersection;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public MovingBoxBoxCollisionEvaluator getMovingBoxBoxCollision() {
/* 130 */     return this.movingBoxBoxCollision;
/*     */   }
/*     */   
/*     */   public CharacterCollisionData allocCharacterCollision() {
/* 134 */     return this.characterCollisions.alloc();
/*     */   }
/*     */   
/*     */   public void addCollision(@Nonnull IBlockCollisionEvaluator blockCollisionEvaluator, int index) {
/* 138 */     if (blockCollisionEvaluator.getCollisionStart() > 1.0D)
/* 139 */       return;  blockCollisionEvaluator.setCollisionData(newCollision(), this.collisionConfig, index);
/*     */   }
/*     */   
/*     */   public BlockCollisionData newCollision() {
/* 143 */     return this.blockCollisions.alloc();
/*     */   }
/*     */   
/*     */   public void addSlide(@Nonnull IBlockCollisionEvaluator blockCollisionEvaluator, int index) {
/* 147 */     if (blockCollisionEvaluator.getCollisionStart() > 1.0D)
/* 148 */       return;  blockCollisionEvaluator.setCollisionData(newSlide(), this.collisionConfig, index);
/*     */   }
/*     */   
/*     */   public BlockCollisionData newSlide() {
/* 152 */     return this.blockSlides.alloc();
/*     */   }
/*     */   
/*     */   public void addTrigger(@Nonnull IBlockCollisionEvaluator blockCollisionEvaluator, int index) {
/* 156 */     if (blockCollisionEvaluator.getCollisionStart() > 1.0D)
/* 157 */       return;  blockCollisionEvaluator.setCollisionData(newTrigger(), this.collisionConfig, index);
/*     */   }
/*     */   
/*     */   public BlockCollisionData newTrigger() {
/* 161 */     return this.blockTriggers.alloc();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 169 */     this.blockCollisions.reset();
/* 170 */     this.blockSlides.reset();
/* 171 */     this.blockTriggers.reset();
/* 172 */     this.characterCollisions.reset();
/*     */   }
/*     */   
/*     */   public void process() {
/* 176 */     this.blockCollisions.sort(BasicCollisionData.COLLISION_START_COMPARATOR);
/* 177 */     this.blockTriggers.sort(BasicCollisionData.COLLISION_START_COMPARATOR);
/* 178 */     this.characterCollisions.sort(BasicCollisionData.COLLISION_START_COMPARATOR);
/*     */     
/* 180 */     if (this.blockSlides.getCount() > 0) {
/*     */       
/* 182 */       this.blockSlides.sort(BLOCK_COLLISION_DATA_COMPARATOR);
/*     */ 
/*     */       
/* 185 */       BlockCollisionData slide = this.blockSlides.get(0);
/*     */       
/* 187 */       this.slideStart = slide.collisionStart;
/* 188 */       this.slideEnd = slide.collisionEnd;
/*     */       
/* 190 */       for (int i = 1; i < this.blockSlides.getCount(); i++) {
/* 191 */         slide = this.blockSlides.get(i);
/* 192 */         if (slide.collisionStart <= this.slideEnd && 
/* 193 */           slide.collisionEnd > this.slideEnd) {
/* 194 */           this.slideEnd = slide.collisionEnd;
/*     */         }
/*     */       } 
/*     */       
/* 198 */       this.isSliding = (this.slideStart <= 0.0D);
/* 199 */       if (this.slideEnd > 1.0D) {
/* 200 */         this.slideEnd = 1.0D;
/*     */       }
/*     */     } else {
/* 203 */       this.isSliding = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getBlockCollisionCount() {
/* 208 */     return this.blockCollisions.getCount();
/*     */   }
/*     */   
/*     */   public BlockCollisionData getBlockCollision(int i) {
/* 212 */     return this.blockCollisions.get(i);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public BlockCollisionData getFirstBlockCollision() {
/* 217 */     return this.blockCollisions.getFirst();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public BlockCollisionData forgetFirstBlockCollision() {
/* 222 */     return this.blockCollisions.forgetFirst();
/*     */   }
/*     */   
/*     */   public int getCharacterCollisionCount() {
/* 226 */     return this.characterCollisions.getCount();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public CharacterCollisionData getFirstCharacterCollision() {
/* 231 */     return this.characterCollisions.getFirst();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public CharacterCollisionData forgetFirstCharacterCollision() {
/* 236 */     return this.characterCollisions.forgetFirst();
/*     */   }
/*     */   
/*     */   public void pruneTriggerBlocks(double distance) {
/* 240 */     int l = this.blockTriggers.size() - 1;
/*     */     
/* 242 */     while (l >= 0) {
/* 243 */       BlockCollisionData blockCollisionData = this.blockTriggers.get(l);
/* 244 */       if (blockCollisionData.collisionStart <= distance) {
/*     */         break;
/*     */       }
/* 247 */       this.blockTriggers.remove(l);
/* 248 */       l--;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public CollisionDataArray<BlockCollisionData> getTriggerBlocks() {
/* 254 */     return this.blockTriggers;
/*     */   }
/*     */   
/*     */   public int defaultTriggerBlocksProcessing(@Nonnull InteractionManager manager, @Nonnull Entity entity, @Nonnull Ref<EntityStore> ref, boolean executeTriggers, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 258 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 259 */     LongSet temp = this.lastTriggers;
/* 260 */     this.lastTriggers = this.newTriggers;
/* 261 */     this.newTriggers = temp;
/* 262 */     this.newTriggers.clear();
/*     */     
/* 264 */     int damageToEntity = 0;
/* 265 */     CollisionDataArray<BlockCollisionData> triggerBlocks = getTriggerBlocks();
/* 266 */     for (int i = 0, size = triggerBlocks.size(); i < size; i++) {
/* 267 */       BlockCollisionData triggerCollision = triggerBlocks.get(i);
/* 268 */       if (triggerCollision.blockType != null) {
/* 269 */         int damageToEntities = Math.max(triggerCollision.blockType.getDamageToEntities(), triggerCollision.fluid.getDamageToEntities());
/* 270 */         if (damageToEntities > damageToEntity) {
/* 271 */           damageToEntity = damageToEntities;
/*     */         }
/*     */       } 
/* 274 */       if (executeTriggers && entity instanceof com.hypixel.hytale.server.core.entity.LivingEntity) {
/* 275 */         WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(triggerCollision.x, triggerCollision.z));
/* 276 */         if (chunk != null) {
/* 277 */           BlockType blockType = chunk.getBlockType(triggerCollision.x, triggerCollision.y, triggerCollision.z);
/* 278 */           Fluid fluidType = (Fluid)Fluid.getAssetMap().getAsset(chunk.getFluidId(triggerCollision.x, triggerCollision.y, triggerCollision.z));
/*     */           
/* 280 */           String interactionsEnter = (String)blockType.getInteractions().get(InteractionType.CollisionEnter);
/* 281 */           if (interactionsEnter == null) interactionsEnter = (String)fluidType.getInteractions().get(InteractionType.CollisionEnter);
/*     */           
/* 283 */           String interactions = (String)blockType.getInteractions().get(InteractionType.Collision);
/* 284 */           if (interactions == null) interactions = (String)fluidType.getInteractions().get(InteractionType.Collision);
/*     */           
/* 286 */           if (interactionsEnter != null || interactions != null) {
/*     */             
/* 288 */             int filler = chunk.getFiller(triggerCollision.x, triggerCollision.y, triggerCollision.z);
/*     */             
/* 290 */             int x = triggerCollision.x;
/* 291 */             int y = triggerCollision.y;
/* 292 */             int z = triggerCollision.z;
/* 293 */             if (filler != 0) {
/* 294 */               x -= FillerBlockUtil.unpackX(filler);
/* 295 */               y -= FillerBlockUtil.unpackY(filler);
/* 296 */               z -= FillerBlockUtil.unpackZ(filler);
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 301 */             long index = BlockUtil.packUnchecked(x, y, z);
/* 302 */             if (this.newTriggers.add(index)) {
/*     */ 
/*     */ 
/*     */               
/* 306 */               BlockPosition pos = new BlockPosition(x, y, z);
/*     */               
/* 308 */               if (!this.lastTriggers.remove(index) && 
/* 309 */                 interactionsEnter != null) {
/* 310 */                 doCollisionInteraction(manager, InteractionType.CollisionEnter, ref, interactionsEnter, pos, componentAccessor);
/*     */               }
/*     */ 
/*     */               
/* 314 */               if (interactions != null) {
/* 315 */                 doCollisionInteraction(manager, InteractionType.Collision, ref, interactions, pos, componentAccessor);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 322 */     if (executeTriggers && entity instanceof com.hypixel.hytale.server.core.entity.LivingEntity && !this.lastTriggers.isEmpty())
/*     */     {
/* 324 */       for (LongIterator<Long> longIterator = this.lastTriggers.iterator(); longIterator.hasNext(); ) { Long old = longIterator.next();
/* 325 */         int x = BlockUtil.unpackX(old.longValue());
/* 326 */         int y = BlockUtil.unpackY(old.longValue());
/* 327 */         int z = BlockUtil.unpackZ(old.longValue());
/*     */         
/* 329 */         WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
/* 330 */         if (chunk != null) {
/* 331 */           BlockType blockType = chunk.getBlockType(x, y, z);
/* 332 */           Fluid fluidType = (Fluid)Fluid.getAssetMap().getAsset(chunk.getFluidId(x, y, z));
/* 333 */           String interactions = (String)blockType.getInteractions().get(InteractionType.CollisionLeave);
/* 334 */           if (interactions == null) interactions = (String)fluidType.getInteractions().get(InteractionType.CollisionLeave); 
/* 335 */           if (interactions != null) {
/* 336 */             doCollisionInteraction(manager, InteractionType.CollisionLeave, ref, interactions, new BlockPosition(x, y, z), componentAccessor);
/*     */           }
/*     */         }  }
/*     */     
/*     */     }
/*     */     
/* 342 */     return damageToEntity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doCollisionInteraction(@Nonnull InteractionManager manager, @Nonnull InteractionType type, @Nonnull Ref<EntityStore> ref, @Nonnull String interactions, @Nonnull BlockPosition pos, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 352 */     RootInteraction root = RootInteraction.getRootInteractionOrUnknown(interactions);
/*     */     
/* 354 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 355 */     InteractionContext context = InteractionContext.forInteraction(manager, ref, type, componentAccessor);
/* 356 */     context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK_RAW, pos);
/* 357 */     context.getMetaStore().putMetaObject(Interaction.TARGET_BLOCK, world.getBaseBlock(pos));
/*     */     
/* 359 */     InteractionChain chain = manager.initChain(type, context, root, -1, pos, false);
/* 360 */     manager.queueExecuteChain(chain);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean next() {
/* 366 */     return (this.continueAfterCollision || this.haveNoCollision);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean accept(long x, long y, long z) {
/* 372 */     if (this.collisionConfig.canCollide((int)x, (int)y, (int)z)) {
/*     */       
/* 374 */       x += this.collisionConfig.getBoundingBoxOffsetX();
/* 375 */       y += this.collisionConfig.getBoundingBoxOffsetY();
/* 376 */       z += this.collisionConfig.getBoundingBoxOffsetZ();
/*     */       
/* 378 */       int numDetails = this.collisionConfig.getDetailCount();
/* 379 */       boolean haveCollision = this.movingBoxBoxCollision.isBoundingBoxColliding(this.collisionConfig.getBoundingBox(), x, y, z);
/*     */       
/* 381 */       if (this.logger != null) {
/* 382 */         Object arg7 = (this.collisionConfig.blockType != null) ? this.collisionConfig.blockType.getId() : "null";
/* 383 */         this.logger.at(Level.INFO).log("?? Block Test at %s/%s/%s numDet=%d haveColl=%s overlap=%s blockType=%s", Long.valueOf(x), Long.valueOf(y), Long.valueOf(z), Integer.valueOf(numDetails), Boolean.valueOf(haveCollision), Boolean.valueOf(this.movingBoxBoxCollision.isOverlapping()), arg7);
/*     */       } 
/* 385 */       if (numDetails <= 1) {
/* 386 */         processCollisionResult(haveCollision, 0);
/* 387 */       } else if (haveCollision || this.movingBoxBoxCollision.isOverlapping() || this.movingBoxBoxCollision.isTouching()) {
/* 388 */         for (int i = 0; i < numDetails; i++) {
/* 389 */           haveCollision = this.movingBoxBoxCollision.isBoundingBoxColliding(this.collisionConfig.getBoundingBox(i), x, y, z);
/* 390 */           processCollisionResult(haveCollision, i);
/*     */         } 
/*     */       } 
/* 393 */     } else if (this.logger != null) {
/* 394 */       Object arg4 = (this.collisionConfig.blockType != null) ? this.collisionConfig.blockType.getId() : "null";
/* 395 */       this.logger.at(Level.INFO).log("-- Ignoring block at %s/%s/%s blockType=%s", Long.valueOf(x), Long.valueOf(y), Long.valueOf(z), arg4);
/*     */     } 
/* 397 */     return true;
/*     */   }
/*     */   
/*     */   private void processCollisionResult(boolean haveCollision, int hitboxIndex) {
/* 401 */     if (this.logger != null) {
/* 402 */       this.logger.at(Level.INFO).log("?? Further testing block haveCol=%s hitBoxIndex=%s onGround=%s touching=%s canCollide=%s canTrigger=%s", Boolean.valueOf(haveCollision), Integer.valueOf(hitboxIndex), Boolean.valueOf(this.movingBoxBoxCollision.isOnGround()), Boolean.valueOf(this.movingBoxBoxCollision.isTouching()), Boolean.valueOf(this.collisionConfig.blockCanCollide), Boolean.valueOf(this.collisionConfig.blockCanTrigger));
/*     */     }
/* 404 */     if (this.collisionConfig.blockCanCollide) {
/* 405 */       boolean isNoSlideCollision = true;
/* 406 */       if (this.movingBoxBoxCollision.onGround) {
/*     */ 
/*     */         
/* 409 */         haveCollision = (this.collisionConfig.blockType == null || this.isNonWalkable.test(this.collisionConfig));
/* 410 */         if (!haveCollision) {
/* 411 */           addSlide(this.movingBoxBoxCollision, hitboxIndex);
/* 412 */           if (this.collisionConfig.blockCanTrigger) {
/* 413 */             addTrigger(this.movingBoxBoxCollision, hitboxIndex);
/*     */           }
/* 415 */           if (this.logger != null) {
/* 416 */             this.logger.at(Level.INFO).log("++ Sliding block start=%s end=%s normal=%s", Double.valueOf(this.movingBoxBoxCollision.getCollisionStart()), Double.valueOf(this.movingBoxBoxCollision.getCollisionEnd()), Vector3d.formatShortString(this.movingBoxBoxCollision.getCollisionNormal()));
/*     */           }
/*     */           
/*     */           return;
/*     */         } 
/* 421 */         isNoSlideCollision = false;
/* 422 */         if (this.logger != null) {
/* 423 */           this.logger.at(Level.INFO).log("?? Sliding block is unwalkable start=%s end=%s normal=%s", Double.valueOf(this.movingBoxBoxCollision.getCollisionStart()), Double.valueOf(this.movingBoxBoxCollision.getCollisionEnd()), Vector3d.formatShortString(this.movingBoxBoxCollision.getCollisionNormal()));
/*     */         }
/*     */       } 
/* 426 */       if (haveCollision) {
/* 427 */         addCollision(this.movingBoxBoxCollision, hitboxIndex);
/* 428 */         if (isNoSlideCollision) this.haveNoCollision = false; 
/* 429 */         if (this.logger != null) {
/* 430 */           this.logger.at(Level.INFO).log("++ Collision with block start=%s end=%s normal=%s", Double.valueOf(this.movingBoxBoxCollision.collisionStart), Double.valueOf(this.movingBoxBoxCollision.collisionEnd), Vector3d.formatShortString(this.movingBoxBoxCollision.collisionNormal));
/*     */         }
/*     */       } 
/*     */     } 
/* 434 */     if (this.collisionConfig.blockCanTrigger && (haveCollision || this.movingBoxBoxCollision.isTouching())) {
/* 435 */       if (this.logger != null) {
/* 436 */         this.logger.at(Level.INFO).log("++ Trigger block start=%s end=%s normal=%s", Double.valueOf(this.movingBoxBoxCollision.getCollisionStart()), Double.valueOf(this.movingBoxBoxCollision.getCollisionEnd()), Vector3d.formatShortString(this.movingBoxBoxCollision.getCollisionNormal()));
/*     */       }
/* 438 */       addTrigger(this.movingBoxBoxCollision, hitboxIndex);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void iterateBlocks(@Nonnull Box collider, @Nonnull Vector3d pos, @Nonnull Vector3d direction, double length, boolean stopOnCollisionFound) {
/* 443 */     this.continueAfterCollision = !stopOnCollisionFound;
/* 444 */     BoxBlockIterator.iterate(collider, pos, direction, length, this);
/*     */   }
/*     */   
/*     */   public void acquireCollisionModule() {
/* 448 */     this.haveNoCollision = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disableSlides() {
/* 456 */     this.movingBoxBoxCollision.setCheckForOnGround(false);
/*     */   }
/*     */   
/*     */   public void enableSlides() {
/* 460 */     this.movingBoxBoxCollision.setCheckForOnGround(true);
/*     */   }
/*     */   
/*     */   public void disableCharacterCollisions() {
/* 464 */     this.checkForCharacterCollisions = false;
/*     */   }
/*     */   
/*     */   public void enableCharacterCollsions() {
/* 468 */     this.checkForCharacterCollisions = true;
/*     */   }
/*     */   
/*     */   public boolean isCheckingForCharacterCollisions() {
/* 472 */     return this.checkForCharacterCollisions;
/*     */   }
/*     */   
/*     */   public void enableTriggerBlocks() {
/* 476 */     this.collisionConfig.setCheckTriggerBlocks(true);
/*     */   }
/*     */   
/*     */   public void disableTriggerBlocks() {
/* 480 */     this.collisionConfig.setCheckTriggerBlocks(false);
/*     */   }
/*     */   
/*     */   public boolean isCheckingTriggerBlocks() {
/* 484 */     return this.collisionConfig.isCheckTriggerBlocks();
/*     */   }
/*     */   
/*     */   public void enableDamageBlocks() {
/* 488 */     this.collisionConfig.setCheckDamageBlocks(true);
/*     */   }
/*     */   
/*     */   public void disableDamageBlocks() {
/* 492 */     this.collisionConfig.setCheckDamageBlocks(false);
/*     */   }
/*     */   
/*     */   public boolean isCheckingDamageBlocks() {
/* 496 */     return this.collisionConfig.isCheckDamageBlocks();
/*     */   }
/*     */   
/*     */   public boolean setDamageBlocking(boolean blocking) {
/* 500 */     boolean oldState = this.collisionConfig.setCollideWithDamageBlocks(blocking);
/* 501 */     updateDamageWalkableFlag();
/* 502 */     return oldState;
/*     */   }
/*     */   
/*     */   public boolean isDamageBlocking() {
/* 506 */     return this.collisionConfig.isCollidingWithDamageBlocks();
/*     */   }
/*     */   
/*     */   public void setCollisionByMaterial(int collidingMaterials) {
/* 510 */     this.collisionConfig.setCollisionByMaterial(collidingMaterials);
/*     */   }
/*     */   
/*     */   public void setCollisionByMaterial(int collidingMaterials, int walkableMaterials) {
/* 514 */     this.collisionConfig.setCollisionByMaterial(collidingMaterials);
/* 515 */     setWalkableByMaterial(walkableMaterials);
/*     */   }
/*     */   
/*     */   public int getCollisionByMaterial() {
/* 519 */     return this.collisionConfig.getCollisionByMaterial();
/*     */   }
/*     */   
/*     */   public void setDefaultCollisionBehaviour() {
/* 523 */     this.collisionConfig.setDefaultCollisionBehaviour();
/*     */   }
/*     */   
/*     */   public void setDefaultBlockCollisionPredicate() {
/* 527 */     this.collisionConfig.setDefaultBlockCollisionPredicate();
/*     */   }
/*     */   
/*     */   public void setDefaultNonWalkablePredicate() {
/* 531 */     this.isNonWalkable = (collisionConfig -> {
/*     */         int matches = collisionConfig.blockMaterialMask & this.walkableMaterialMask;
/* 533 */         return (matches == 0 || (matches & 0x10) != 0);
/*     */       });
/*     */   }
/*     */   
/*     */   public void setNonWalkablePredicate(Predicate<CollisionConfig> classifier) {
/* 538 */     this.isNonWalkable = classifier;
/*     */   }
/*     */   
/*     */   public void setWalkableByMaterial(int walkableMaterial) {
/* 542 */     this.walkableMaterialMask = 0xF & walkableMaterial;
/* 543 */     updateDamageWalkableFlag();
/*     */   }
/*     */   
/*     */   protected void updateDamageWalkableFlag() {
/* 547 */     if (this.collisionConfig.isCollidingWithDamageBlocks()) {
/* 548 */       this.walkableMaterialMask |= 0x10;
/*     */     } else {
/* 550 */       this.walkableMaterialMask &= 0xFFFFFFEF;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setDefaultWalkableBehaviour() {
/* 555 */     setDefaultNonWalkablePredicate();
/* 556 */     setWalkableByMaterial(5);
/*     */   }
/*     */   
/*     */   public void setDefaultPlayerSettings() {
/* 560 */     enableSlides();
/* 561 */     disableCharacterCollisions();
/* 562 */     setDefaultNonWalkablePredicate();
/* 563 */     setDefaultBlockCollisionPredicate();
/* 564 */     setCollisionByMaterial(4);
/* 565 */     setWalkableByMaterial(15);
/*     */   }
/*     */   
/*     */   public boolean isComputeOverlaps() {
/* 569 */     return this.movingBoxBoxCollision.isComputeOverlaps();
/*     */   }
/*     */   
/*     */   public void setComputeOverlaps(boolean computeOverlaps) {
/* 573 */     this.movingBoxBoxCollision.setComputeOverlaps(computeOverlaps);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HytaleLogger getLogger() {
/* 579 */     return this.logger;
/*     */   }
/*     */   
/*     */   public boolean shouldLog() {
/* 583 */     return (this.logger != null);
/*     */   }
/*     */   
/*     */   public void setLogger(HytaleLogger logger) {
/* 587 */     this.logger = logger;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\collision\CollisionResult.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */