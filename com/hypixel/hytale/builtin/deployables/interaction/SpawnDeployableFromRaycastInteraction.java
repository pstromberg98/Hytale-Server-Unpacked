/*     */ package com.hypixel.hytale.builtin.deployables.interaction;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.deployables.DeployablesUtils;
/*     */ import com.hypixel.hytale.builtin.deployables.config.DeployableConfig;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.protocol.Direction;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.Position;
/*     */ import com.hypixel.hytale.protocol.Vector3f;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2FloatMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2FloatMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
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
/*     */ public class SpawnDeployableFromRaycastInteraction
/*     */   extends SimpleInstantInteraction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<SpawnDeployableFromRaycastInteraction> CODEC;
/*     */   protected Object2FloatMap<String> unknownEntityStats;
/*     */   protected Int2FloatMap entityStats;
/*     */   protected float maxPlacementDistance;
/*     */   private DeployableConfig config;
/*     */   
/*     */   static {
/*  62 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(SpawnDeployableFromRaycastInteraction.class, SpawnDeployableFromRaycastInteraction::new, SimpleInstantInteraction.CODEC).append(new KeyedCodec("Config", (Codec)DeployableConfig.CODEC), (i, s) -> i.config = s, i -> i.config).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("PreviewStatConditions", (Codec)new Object2FloatMapCodec((Codec)Codec.STRING, it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap::new)), (changeStatInteraction, stringObject2DoubleMap) -> changeStatInteraction.unknownEntityStats = stringObject2DoubleMap, changeStatInteraction -> changeStatInteraction.unknownEntityStats).addValidator((Validator)EntityStatType.VALIDATOR_CACHE.getMapKeyValidator()).documentation("Modifiers to apply to EntityStats.").add()).appendInherited(new KeyedCodec("MaxPlacementDistance", (Codec)Codec.FLOAT), (o, i) -> o.maxPlacementDistance = i.floatValue(), o -> Float.valueOf(o.maxPlacementDistance), (i, o) -> i.maxPlacementDistance = o.maxPlacementDistance).documentation("The max distance at which the player can deploy the deployable.").add()).afterDecode(SpawnDeployableFromRaycastInteraction::processConfig)).build();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void processConfig() {
/*  88 */     if (this.unknownEntityStats != null) {
/*  89 */       this.entityStats = EntityStatsModule.resolveEntityStats(this.unknownEntityStats);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isSurface(@Nonnull Vector3f normal) {
/* 100 */     return (normal.x == 0.0F && (normal.y - 1.0F) < 0.01D && normal.z == 0.0F);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 105 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 111 */     return WaitForDataFrom.Client;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 116 */     Ref<EntityStore> entityRef = context.getOwningEntity();
/* 117 */     Store<EntityStore> store = entityRef.getStore();
/*     */     
/* 119 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 120 */     assert commandBuffer != null;
/*     */     
/* 122 */     InteractionSyncData clientState = context.getClientState();
/* 123 */     assert clientState != null;
/*     */     
/* 125 */     if (!canAfford(context.getEntity(), (ComponentAccessor<EntityStore>)commandBuffer)) {
/* 126 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     Position raycastHit = clientState.raycastHit;
/* 131 */     if (raycastHit == null) {
/* 132 */       TransformComponent transformComponent = (TransformComponent)store.getComponent(entityRef, TransformComponent.getComponentType());
/* 133 */       assert transformComponent != null;
/*     */       
/* 135 */       Vector3d position = transformComponent.getPosition();
/* 136 */       raycastHit = new Position((float)position.x, (float)position.y, (float)position.z);
/*     */     } 
/*     */     
/* 139 */     Vector3f raycastNormal = clientState.raycastNormal;
/* 140 */     float correctedRaycastDistance = clientState.raycastDistance;
/*     */     
/* 142 */     Vector3f spawnPosition = new Vector3f((float)raycastHit.x, (float)raycastHit.y, (float)raycastHit.z);
/* 143 */     Vector3f norm = new Vector3f(raycastNormal.x, raycastNormal.y, raycastNormal.z);
/*     */     
/* 145 */     if (correctedRaycastDistance > 0.0F && correctedRaycastDistance <= this.maxPlacementDistance && (this.config.getAllowPlaceOnWalls() || isSurface(norm))) {
/* 146 */       Direction attackerRot = clientState.attackerRot;
/* 147 */       Vector3f rot = new Vector3f(0.0F, attackerRot.yaw, 0.0F);
/* 148 */       DeployablesUtils.spawnDeployable(commandBuffer, store, this.config, entityRef, new Vector3f(spawnPosition.x, spawnPosition.y, spawnPosition.z), rot, "UP");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean canAfford(@Nonnull Ref<EntityStore> entityRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 160 */     if (this.entityStats == null || this.entityStats.isEmpty()) {
/* 161 */       return true;
/*     */     }
/*     */     
/* 164 */     EntityStatMap entityStatMapComponent = (EntityStatMap)componentAccessor.getComponent(entityRef, EntityStatMap.getComponentType());
/* 165 */     if (entityStatMapComponent == null) {
/* 166 */       return false;
/*     */     }
/*     */     
/* 169 */     for (ObjectIterator<Int2FloatMap.Entry> objectIterator = this.entityStats.int2FloatEntrySet().iterator(); objectIterator.hasNext(); ) { Int2FloatMap.Entry cost = objectIterator.next();
/* 170 */       EntityStatValue stat = entityStatMapComponent.get(cost.getIntKey());
/* 171 */       if (stat == null || stat.get() < cost.getFloatValue()) {
/* 172 */         return false;
/*     */       } }
/*     */ 
/*     */     
/* 176 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 182 */     return (Interaction)new com.hypixel.hytale.protocol.SpawnDeployableFromRaycastInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 187 */     super.configurePacket(packet);
/* 188 */     com.hypixel.hytale.protocol.SpawnDeployableFromRaycastInteraction p = (com.hypixel.hytale.protocol.SpawnDeployableFromRaycastInteraction)packet;
/* 189 */     p.deployableConfig = this.config.toPacket();
/* 190 */     p.maxDistance = this.maxPlacementDistance;
/* 191 */     p.costs = (Map)this.entityStats;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\deployables\interaction\SpawnDeployableFromRaycastInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */