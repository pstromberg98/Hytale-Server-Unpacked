/*    */ package com.hypixel.hytale.builtin.adventure.memories;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*    */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelParticle;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemoriesGameplayConfig
/*    */ {
/*    */   public static final String ID = "Memories";
/*    */   public static final BuilderCodec<MemoriesGameplayConfig> CODEC;
/*    */   private int[] memoriesAmountPerLevel;
/*    */   private String memoriesRecordParticles;
/*    */   private String memoriesCatchItemId;
/*    */   private ModelParticle memoriesCatchEntityParticle;
/*    */   
/*    */   static {
/* 59 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(MemoriesGameplayConfig.class, MemoriesGameplayConfig::new).appendInherited(new KeyedCodec("MemoriesAmountPerLevel", (Codec)Codec.INT_ARRAY), (config, value) -> config.memoriesAmountPerLevel = value, config -> config.memoriesAmountPerLevel, (config, parent) -> config.memoriesAmountPerLevel = parent.memoriesAmountPerLevel).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("MemoriesRecordParticles", (Codec)Codec.STRING), (config, value) -> config.memoriesRecordParticles = value, config -> config.memoriesRecordParticles, (config, parent) -> config.memoriesRecordParticles = parent.memoriesRecordParticles).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("MemoriesCatchItemId", (Codec)Codec.STRING), (memoriesGameplayConfig, s) -> memoriesGameplayConfig.memoriesCatchItemId = s, memoriesGameplayConfig -> memoriesGameplayConfig.memoriesCatchItemId, (memoriesGameplayConfig, parent) -> memoriesGameplayConfig.memoriesCatchItemId = parent.memoriesCatchItemId).addValidator(Validators.nonNull()).addValidator(Item.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec("MemoriesCatchEntityParticle", (Codec)ModelParticle.CODEC), (activationEffects, s) -> activationEffects.memoriesCatchEntityParticle = s, activationEffects -> activationEffects.memoriesCatchEntityParticle, (activationEffects, parent) -> activationEffects.memoriesCatchEntityParticle = parent.memoriesCatchEntityParticle).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("MemoriesCatchParticleViewDistance", (Codec)Codec.INTEGER), (memoriesGameplayConfig, integer) -> memoriesGameplayConfig.memoriesCatchParticleViewDistance = integer.intValue(), memoriesGameplayConfig -> Integer.valueOf(memoriesGameplayConfig.memoriesCatchParticleViewDistance), (memoriesGameplayConfig, parent) -> memoriesGameplayConfig.memoriesCatchParticleViewDistance = parent.memoriesCatchParticleViewDistance).addValidator(Validators.greaterThan(Integer.valueOf(16))).add()).build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 65 */   private int memoriesCatchParticleViewDistance = 64;
/*    */   
/*    */   @Nullable
/*    */   public static MemoriesGameplayConfig get(@Nonnull GameplayConfig config) {
/* 69 */     return (MemoriesGameplayConfig)config.getPluginConfig().get(MemoriesGameplayConfig.class);
/*    */   }
/*    */   
/*    */   public int[] getMemoriesAmountPerLevel() {
/* 73 */     return this.memoriesAmountPerLevel;
/*    */   }
/*    */   
/*    */   public String getMemoriesRecordParticles() {
/* 77 */     return this.memoriesRecordParticles;
/*    */   }
/*    */   
/*    */   public String getMemoriesCatchItemId() {
/* 81 */     return this.memoriesCatchItemId;
/*    */   }
/*    */   
/*    */   public ModelParticle getMemoriesCatchEntityParticle() {
/* 85 */     return this.memoriesCatchEntityParticle;
/*    */   }
/*    */   
/*    */   public int getMemoriesCatchParticleViewDistance() {
/* 89 */     return this.memoriesCatchParticleViewDistance;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\MemoriesGameplayConfig.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */