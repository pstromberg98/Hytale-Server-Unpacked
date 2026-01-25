/*    */ package com.hypixel.hytale.server.core.modules.entitystats.asset.condition;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.math.util.MathUtil;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.protocol.BlockMaterial;
/*    */ import com.hypixel.hytale.server.core.entity.Entity;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*    */ import com.hypixel.hytale.server.core.modules.collision.WorldUtil;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import com.hypixel.hytale.server.core.util.TargetUtil;
/*    */ import java.time.Instant;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class SuffocatingCondition
/*    */   extends Condition
/*    */ {
/*    */   @Nonnull
/* 27 */   public static final BuilderCodec<SuffocatingCondition> CODEC = BuilderCodec.builder(SuffocatingCondition.class, SuffocatingCondition::new, Condition.BASE_CODEC)
/* 28 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected SuffocatingCondition() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SuffocatingCondition(boolean inverse) {
/* 44 */     super(inverse);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean eval0(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref, @Nonnull Instant currentTime) {
/*    */     LivingEntity livingEntity;
/* 51 */     Entity entity = EntityUtils.getEntity(ref, componentAccessor); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/* 52 */     else { return false; }
/*    */ 
/*    */     
/* 55 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*    */     
/* 57 */     Transform lookVec = TargetUtil.getLook(ref, componentAccessor);
/* 58 */     Vector3d position = lookVec.getPosition();
/*    */     
/* 60 */     ChunkStore chunkStore = world.getChunkStore();
/* 61 */     long chunkIndex = ChunkUtil.indexChunkFromBlock(position.x, position.z);
/* 62 */     Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
/*    */ 
/*    */     
/* 65 */     if (chunkRef == null || !chunkRef.isValid()) {
/* 66 */       return false;
/*    */     }
/*    */     
/* 69 */     long packed = WorldUtil.getPackedMaterialAndFluidAtPosition(chunkRef, (ComponentAccessor)chunkStore.getStore(), position.x, position.y, position.z);
/* 70 */     BlockMaterial material = BlockMaterial.VALUES[MathUtil.unpackLeft(packed)];
/* 71 */     int fluidId = MathUtil.unpackRight(packed);
/*    */     
/* 73 */     return !livingEntity.canBreathe(ref, material, fluidId, componentAccessor);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 79 */     return "SuffocatingCondition{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entitystats\asset\condition\SuffocatingCondition.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */