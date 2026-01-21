/*    */ package com.hypixel.hytale.builtin.adventure.memories.temple;
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*    */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*    */ import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class TempleRespawnPlayersSystem extends DelayedEntitySystem<EntityStore> {
/* 19 */   public static final Query<EntityStore> QUERY = (Query<EntityStore>)Query.and(new Query[] { (Query)PlayerRef.getComponentType(), (Query)TransformComponent.getComponentType() });
/*    */   
/*    */   public TempleRespawnPlayersSystem() {
/* 22 */     super(1.0F);
/*    */   }
/*    */ 
/*    */   
/*    */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 27 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 28 */     GameplayConfig gameplayConfig = world.getGameplayConfig();
/*    */     
/* 30 */     ForgottenTempleConfig config = (ForgottenTempleConfig)gameplayConfig.getPluginConfig().get(ForgottenTempleConfig.class);
/* 31 */     if (config == null)
/*    */       return; 
/* 33 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TransformComponent.getComponentType());
/* 34 */     assert transformComponent != null;
/*    */     
/* 36 */     Vector3d position = transformComponent.getPosition();
/* 37 */     if (position.getY() > config.getMinYRespawn())
/*    */       return; 
/* 39 */     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
/* 40 */     ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
/* 41 */     Transform spawnTransform = spawnProvider.getSpawnPoint(ref, (ComponentAccessor)commandBuffer);
/* 42 */     Teleport teleportComponent = Teleport.createForPlayer(null, spawnTransform);
/*    */     
/* 44 */     commandBuffer.addComponent(ref, Teleport.getComponentType(), (Component)teleportComponent);
/*    */     
/* 46 */     PlayerRef playerRef = (PlayerRef)archetypeChunk.getComponent(index, PlayerRef.getComponentType());
/* 47 */     SoundUtil.playSoundEvent2dToPlayer(playerRef, config.getRespawnSoundIndex(), SoundCategory.SFX);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Query<EntityStore> getQuery() {
/* 53 */     return QUERY;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\temple\TempleRespawnPlayersSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */