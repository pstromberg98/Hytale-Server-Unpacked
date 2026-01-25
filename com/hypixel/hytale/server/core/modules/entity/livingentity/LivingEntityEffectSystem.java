/*     */ package com.hypixel.hytale.server.core.modules.entity.livingentity;
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.math.shape.Box;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
/*     */ import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class LivingEntityEffectSystem extends EntityTickingSystem<EntityStore> implements DisableProcessingAssert {
/*     */   @Nonnull
/*  31 */   private static final Query<EntityStore> QUERY = (Query<EntityStore>)Query.and(new Query[] {
/*  32 */         (Query)EffectControllerComponent.getComponentType(), 
/*  33 */         (Query)TransformComponent.getComponentType(), 
/*  34 */         (Query)BoundingBox.getComponentType()
/*     */       });
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static final String EFFECT_NAME_BURN = "Burn";
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static final String BLOCK_TYPE_FLUID_WATER = "Fluid_Water";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  52 */     return QUERY;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/*  57 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  64 */     EffectControllerComponent effectControllerComponent = (EffectControllerComponent)archetypeChunk.getComponent(index, EffectControllerComponent.getComponentType());
/*  65 */     assert effectControllerComponent != null;
/*     */     
/*  67 */     Int2ObjectMap<ActiveEntityEffect> activeEffects = effectControllerComponent.getActiveEffects();
/*  68 */     if (activeEffects.isEmpty())
/*     */       return; 
/*  70 */     IndexedLookupTableAssetMap<String, EntityEffect> entityEffectAssetMap = EntityEffect.getAssetMap();
/*  71 */     Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
/*  72 */     ObjectIterator<ActiveEntityEffect> iterator = activeEffects.values().iterator();
/*     */     
/*  74 */     EntityStatMap entityStatMapComponent = (EntityStatMap)commandBuffer.getComponent(entityRef, EntityStatMap.getComponentType());
/*  75 */     if (entityStatMapComponent == null)
/*     */       return; 
/*  77 */     boolean invalidated = false;
/*  78 */     boolean invulnerable = false;
/*     */     
/*  80 */     while (iterator.hasNext()) {
/*  81 */       ActiveEntityEffect activeEntityEffect = (ActiveEntityEffect)iterator.next();
/*  82 */       int entityEffectIndex = activeEntityEffect.getEntityEffectIndex();
/*  83 */       EntityEffect entityEffect = (EntityEffect)entityEffectAssetMap.getAsset(entityEffectIndex);
/*     */ 
/*     */ 
/*     */       
/*  87 */       if (entityEffect == null) {
/*  88 */         iterator.remove();
/*  89 */         invalidated = true;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/*  94 */       if (!canApplyEffect(entityRef, entityEffect, (ComponentAccessor<EntityStore>)commandBuffer)) {
/*  95 */         iterator.remove();
/*  96 */         invalidated = true;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 101 */       float tickDelta = Math.min(activeEntityEffect.getRemainingDuration(), dt);
/* 102 */       activeEntityEffect.tick(commandBuffer, entityRef, entityEffect, entityStatMapComponent, tickDelta);
/*     */ 
/*     */       
/* 105 */       if (activeEffects.isEmpty())
/*     */         return; 
/* 107 */       if (!activeEntityEffect.isInfinite() && activeEntityEffect.getRemainingDuration() <= 0.0F) {
/* 108 */         iterator.remove();
/*     */         
/* 110 */         effectControllerComponent.tryResetModelChange(entityRef, activeEntityEffect.getEntityEffectIndex(), (ComponentAccessor)commandBuffer);
/*     */         
/* 112 */         invalidated = true;
/*     */       } 
/*     */       
/* 115 */       if (activeEntityEffect.isInvulnerable()) {
/* 116 */         invulnerable = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 121 */     effectControllerComponent.setInvulnerable(invulnerable);
/*     */ 
/*     */     
/* 124 */     if (invalidated) {
/* 125 */       effectControllerComponent.invalidateCache();
/*     */       
/* 127 */       Entity entity = EntityUtils.getEntity(index, archetypeChunk); if (entity instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)entity;
/* 128 */         livingEntity.getStatModifiersManager().setRecalculate(true); }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 136 */     return DamageModule.get().getGatherDamageGroup();
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
/*     */   public static boolean canApplyEffect(@Nonnull Ref<EntityStore> ownerRef, @Nonnull EntityEffect entityEffect, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 153 */     if ("Burn".equals(entityEffect.getId())) {
/* 154 */       TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(ownerRef, TransformComponent.getComponentType());
/* 155 */       assert transformComponent != null;
/*     */       
/* 157 */       Ref<ChunkStore> chunkRef = transformComponent.getChunkRef();
/* 158 */       if (chunkRef == null || !chunkRef.isValid()) return false;
/*     */       
/* 160 */       World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/* 161 */       Store<ChunkStore> chunkComponentStore = world.getChunkStore().getStore();
/* 162 */       WorldChunk worldChunkComponent = (WorldChunk)chunkComponentStore.getComponent(chunkRef, WorldChunk.getComponentType());
/* 163 */       assert worldChunkComponent != null;
/*     */       
/* 165 */       BoundingBox boundingBoxComponent = (BoundingBox)componentAccessor.getComponent(ownerRef, BoundingBox.getComponentType());
/* 166 */       assert boundingBoxComponent != null;
/*     */       
/* 168 */       Vector3d position = transformComponent.getPosition();
/* 169 */       Box boundingBox = boundingBoxComponent.getBoundingBox();
/*     */       
/* 171 */       LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atChunkCoords((ChunkAccessor)world, worldChunkComponent.getX(), worldChunkComponent.getZ(), 1);
/* 172 */       return boundingBox.forEachBlock(position, chunkAccessor, (x, y, z, _chunkAccessor) -> {
/*     */             WorldChunk localChunk = _chunkAccessor.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
/*     */             
/*     */             if (localChunk == null) {
/*     */               return true;
/*     */             }
/*     */             
/*     */             BlockType blockType = localChunk.getBlockType(x, y, z);
/*     */             return (blockType == null) ? true : (!blockType.getId().contains("Fluid_Water"));
/*     */           });
/*     */     } 
/* 183 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\livingentity\LivingEntityEffectSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */