/*    */ package com.hypixel.hytale.builtin.deployables.interaction;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.deployables.DeployablesUtils;
/*    */ import com.hypixel.hytale.builtin.deployables.config.DeployableConfig;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.util.MathUtil;
/*    */ import com.hypixel.hytale.math.vector.Vector3f;
/*    */ import com.hypixel.hytale.protocol.InteractionChainData;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.protocol.Vector3f;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SpawnDeployableAtHitLocationInteraction
/*    */   extends SimpleInstantInteraction
/*    */ {
/*    */   @Nonnull
/*    */   public static final BuilderCodec<SpawnDeployableAtHitLocationInteraction> CODEC;
/*    */   private DeployableConfig config;
/*    */   
/*    */   static {
/* 34 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SpawnDeployableAtHitLocationInteraction.class, SpawnDeployableAtHitLocationInteraction::new, SimpleInstantInteraction.CODEC).append(new KeyedCodec("Config", (Codec)DeployableConfig.CODEC), (i, s) -> i.config = s, i -> i.config).addValidator(Validators.nonNull()).add()).build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean needsRemoteSync() {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 48 */     InteractionChain contextChain = context.getChain();
/* 49 */     assert contextChain != null;
/*    */     
/* 51 */     InteractionChainData chainData = contextChain.getChainData();
/* 52 */     Vector3f hitLocation = chainData.hitLocation;
/* 53 */     if (hitLocation == null)
/*    */       return; 
/* 55 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 56 */     assert commandBuffer != null;
/*    */     
/* 58 */     Store<EntityStore> store = commandBuffer.getStore();
/* 59 */     Vector3f hitNormal = chainData.hitNormal;
/* 60 */     Vector3f hitNormalVec = new Vector3f(hitNormal.x, hitNormal.y, hitNormal.z);
/* 61 */     DeployablesUtils.spawnDeployable(commandBuffer, store, this.config, context
/* 62 */         .getEntity(), new Vector3f(hitLocation.x, hitLocation.y, hitLocation.z), 
/* 63 */         MathUtil.getRotationForHitNormal(hitNormalVec), 
/* 64 */         MathUtil.getNameForHitNormal(hitNormalVec));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\deployables\interaction\SpawnDeployableAtHitLocationInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */