/*     */ package com.hypixel.hytale.server.spawning.local;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.weather.components.WeatherTracker;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.spatial.SpatialResource;
/*     */ import com.hypixel.hytale.component.system.tick.TickingSystem;
/*     */ import com.hypixel.hytale.function.function.TriIntObjectDoubleToByteFunction;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.BlockMaterial;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.spawning.SpawningPlugin;
/*     */ import com.hypixel.hytale.server.spawning.assets.spawns.LightType;
/*     */ import com.hypixel.hytale.server.spawning.assets.spawns.config.BeaconNPCSpawn;
/*     */ import com.hypixel.hytale.server.spawning.beacons.LegacySpawnBeaconEntity;
/*     */ import com.hypixel.hytale.server.spawning.util.LightRangePredicate;
/*     */ import com.hypixel.hytale.server.spawning.wrappers.BeaconSpawnWrapper;
/*     */ import it.unimi.dsi.fastutil.Pair;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ByteMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class LocalSpawnControllerSystem
/*     */   extends TickingSystem<EntityStore>
/*     */ {
/*     */   public static final double RUN_FREQUENCY_SECONDS = 5.0D;
/*     */   private static final int LIGHT_LEVEL_EVALUATION_RADIUS = 4;
/*     */   private final Archetype<EntityStore> controllerArchetype;
/*     */   private final ComponentType<EntityStore, LocalSpawnController> spawnControllerComponentType;
/*     */   private final ComponentType<EntityStore, TransformComponent> transformComponentype;
/*     */   private final ComponentType<EntityStore, WeatherTracker> weatherTrackerComponentType;
/*     */   private final ComponentType<EntityStore, LocalSpawnBeacon> localSpawnBeaconComponentType;
/*     */   private final ComponentType<EntityStore, LegacySpawnBeaconEntity> spawnBeaconComponentType;
/*     */   private final ResourceType<EntityStore, LocalSpawnState> localSpawnStateResourceType;
/*     */   private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> beaconSpatialComponent;
/*     */   
/*     */   public LocalSpawnControllerSystem(ComponentType<EntityStore, LocalSpawnController> spawnControllerComponentType, ComponentType<EntityStore, TransformComponent> transformComponentype, ComponentType<EntityStore, WeatherTracker> weatherTrackerComponentType, ComponentType<EntityStore, LocalSpawnBeacon> localSpawnBeaconComponentType, ComponentType<EntityStore, LegacySpawnBeaconEntity> spawnBeaconComponentType, ResourceType<EntityStore, LocalSpawnState> localSpawnStateResourceType, ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> beaconSpatialComponent) {
/*  57 */     this.spawnControllerComponentType = spawnControllerComponentType;
/*  58 */     this.transformComponentype = transformComponentype;
/*  59 */     this.weatherTrackerComponentType = weatherTrackerComponentType;
/*  60 */     this.localSpawnBeaconComponentType = localSpawnBeaconComponentType;
/*  61 */     this.spawnBeaconComponentType = spawnBeaconComponentType;
/*  62 */     this.localSpawnStateResourceType = localSpawnStateResourceType;
/*  63 */     this.beaconSpatialComponent = beaconSpatialComponent;
/*  64 */     this.controllerArchetype = Archetype.of(new ComponentType[] { spawnControllerComponentType, 
/*     */           
/*  66 */           PlayerRef.getComponentType(), transformComponentype, weatherTrackerComponentType });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
/*  74 */     LocalSpawnState localSpawnState = (LocalSpawnState)store.getResource(this.localSpawnStateResourceType);
/*     */ 
/*     */     
/*  77 */     List<Ref<EntityStore>> controllers = localSpawnState.getLocalControllerList();
/*     */ 
/*     */     
/*  80 */     store.forEachChunk((Query)this.controllerArchetype, (archetypeChunk, commandBuffer) -> {
/*     */           for (int index = 0; index < archetypeChunk.size(); index++) {
/*     */             LocalSpawnController spawnControllerComponent = (LocalSpawnController)archetypeChunk.getComponent(index, this.spawnControllerComponentType);
/*     */ 
/*     */             
/*     */             assert spawnControllerComponent != null;
/*     */ 
/*     */             
/*     */             if (spawnControllerComponent.tickTimeToNextRunSeconds(dt)) {
/*     */               controllers.add(archetypeChunk.getReferenceTo(index));
/*     */             }
/*     */           } 
/*     */         });
/*     */     
/*  94 */     if (controllers.isEmpty())
/*     */       return; 
/*  96 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*  97 */     List<LegacySpawnBeaconEntity> pendingSpawns = localSpawnState.getLocalPendingSpawns();
/*  98 */     ObjectList<Ref<EntityStore>> existingBeacons = SpatialResource.getThreadLocalReferenceList();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 103 */     for (int index = 0; index < controllers.size(); index++) {
/* 104 */       Ref<EntityStore> reference = controllers.get(index);
/* 105 */       LocalSpawnController spawnControllerComponent = (LocalSpawnController)store.getComponent(reference, this.spawnControllerComponentType);
/* 106 */       assert spawnControllerComponent != null;
/*     */       
/* 108 */       PlayerRef playerRefComponent = (PlayerRef)store.getComponent(reference, PlayerRef.getComponentType());
/* 109 */       assert playerRefComponent != null;
/*     */       
/* 111 */       SpawningPlugin.get().getLogger().at(Level.FINE).log("Running local spawn controller for player %s", playerRefComponent.getUsername());
/* 112 */       TransformComponent transformComponent = (TransformComponent)store.getComponent(reference, this.transformComponentype);
/* 113 */       assert transformComponent != null;
/*     */ 
/*     */       
/* 116 */       WeatherTracker weatherTrackerComponent = (WeatherTracker)store.getComponent(reference, this.weatherTrackerComponentType);
/* 117 */       assert weatherTrackerComponent != null;
/*     */       
/* 119 */       weatherTrackerComponent.updateEnvironment(transformComponent, (ComponentAccessor)store);
/* 120 */       int environmentIndex = weatherTrackerComponent.getEnvironmentId();
/*     */ 
/*     */       
/* 123 */       List<BeaconSpawnWrapper> possibleBeacons = SpawningPlugin.get().getBeaconSpawnsForEnvironment(environmentIndex);
/* 124 */       if (possibleBeacons == null || possibleBeacons.isEmpty()) {
/* 125 */         spawnControllerComponent.setTimeToNextRunSeconds(5.0D);
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 130 */         BeaconSpawnWrapper firstBeacon = possibleBeacons.getFirst();
/* 131 */         double largestDistance = firstBeacon.getBeaconRadius();
/* 132 */         int[] firstRange = ((BeaconNPCSpawn)firstBeacon.getSpawn()).getYRange();
/* 133 */         int lowestY = firstRange[0];
/* 134 */         int highestY = firstRange[1];
/* 135 */         for (int i = 1; i < possibleBeacons.size(); i++) {
/* 136 */           BeaconSpawnWrapper beacon = possibleBeacons.get(i);
/* 137 */           double radius = beacon.getBeaconRadius();
/* 138 */           if (radius > largestDistance) largestDistance = radius; 
/* 139 */           int[] yRange = ((BeaconNPCSpawn)beacon.getSpawn()).getYRange();
/* 140 */           if (yRange[0] < lowestY) lowestY = yRange[0]; 
/* 141 */           if (yRange[1] > highestY) highestY = yRange[1];
/*     */         
/*     */         } 
/*     */         
/* 145 */         largestDistance *= 2.0D;
/*     */         
/* 147 */         Vector3d position = transformComponent.getPosition();
/* 148 */         double largestDistanceSquared = largestDistance * largestDistance;
/*     */ 
/*     */         
/* 151 */         int yDistance = Math.abs(lowestY) + Math.abs(highestY);
/* 152 */         int y = MathUtil.floor(position.getY());
/* 153 */         int minY = Math.max(0, y - yDistance);
/* 154 */         int maxY = Math.min(319, y + yDistance);
/*     */         
/* 156 */         SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = (SpatialResource<Ref<EntityStore>, EntityStore>)store.getResource(this.beaconSpatialComponent);
/* 157 */         spatialResource.getSpatialStructure().ordered(position, largestDistance, (List)existingBeacons);
/*     */         
/* 159 */         WorldTimeResource worldTimeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/*     */         
/* 161 */         double sunlightFactor = worldTimeResource.getSunlightFactor();
/* 162 */         int xPos = MathUtil.floor(position.getX());
/* 163 */         int yPos = MathUtil.floor(position.getY());
/* 164 */         int zPos = MathUtil.floor(position.getZ());
/*     */         
/* 166 */         Object2ByteOpenHashMap<LightType> averageLightValues = new Object2ByteOpenHashMap();
/* 167 */         averageLightValues.defaultReturnValue((byte)-1);
/*     */         int j;
/* 169 */         label101: for (j = 0; j < possibleBeacons.size(); j++) {
/* 170 */           BeaconSpawnWrapper possibleBeacon = possibleBeacons.get(j);
/*     */ 
/*     */           
/* 173 */           if (possibleBeacon.spawnParametersMatch((ComponentAccessor)store)) {
/*     */             int k;
/* 175 */             for (k = 0; k < existingBeacons.size(); k++) {
/* 176 */               Ref<EntityStore> existingBeaconReference = (Ref<EntityStore>)existingBeacons.get(k);
/*     */               
/* 178 */               LegacySpawnBeaconEntity existingBeaconComponent = (LegacySpawnBeaconEntity)store.getComponent(existingBeaconReference, this.spawnBeaconComponentType);
/* 179 */               assert existingBeaconComponent != null;
/*     */               
/* 181 */               TransformComponent existingBeaconTransformComponent = (TransformComponent)store.getComponent(existingBeaconReference, this.transformComponentype);
/* 182 */               assert existingBeaconTransformComponent != null;
/*     */               
/* 184 */               double existingY = existingBeaconTransformComponent.getPosition().getY();
/*     */               
/* 186 */               if (existingY <= maxY && existingY >= minY) {
/*     */                 
/* 188 */                 int existingBeaconIndex = existingBeaconComponent.getSpawnWrapper().getSpawnIndex();
/* 189 */                 if (existingBeaconIndex == possibleBeacon.getSpawnIndex()) {
/*     */                   continue label101;
/*     */                 }
/*     */               } 
/*     */             } 
/* 194 */             for (k = 0; k < pendingSpawns.size(); k++) {
/*     */               
/* 196 */               LegacySpawnBeaconEntity pending = pendingSpawns.get(k);
/* 197 */               Ref<EntityStore> pendingReference = pending.getReference();
/*     */               
/* 199 */               TransformComponent pendingTransformComponent = (TransformComponent)store.getComponent(pendingReference, TransformComponent.getComponentType());
/* 200 */               assert pendingTransformComponent != null;
/*     */               
/* 202 */               Vector3d pendingPosition = pendingTransformComponent.getPosition();
/* 203 */               double pendingY = pendingPosition.getY();
/* 204 */               if (pendingY <= maxY && pendingY >= minY) {
/*     */                 
/* 206 */                 double xDiff = position.x - pendingPosition.x;
/* 207 */                 double zDiff = position.z - pendingPosition.z;
/* 208 */                 double distSquared = xDiff * xDiff + zDiff * zDiff;
/* 209 */                 if (distSquared <= largestDistanceSquared) {
/*     */                   
/* 211 */                   int existingBeaconIndex = pending.getSpawnWrapper().getSpawnIndex();
/* 212 */                   if (existingBeaconIndex == possibleBeacon.getSpawnIndex()) {
/*     */                     continue label101;
/*     */                   }
/*     */                 } 
/*     */               } 
/*     */             } 
/* 218 */             if (spawnLightLevelMatches(world, xPos, yPos, zPos, sunlightFactor, possibleBeacon, (Object2ByteMap<LightType>)averageLightValues)) {
/*     */ 
/*     */               
/* 221 */               Pair<Ref<EntityStore>, LegacySpawnBeaconEntity> beaconEntityPair = LegacySpawnBeaconEntity.create(possibleBeacon, transformComponent.getPosition(), transformComponent.getRotation(), (ComponentAccessor)store);
/* 222 */               Ref<EntityStore> beaconRef = (Ref<EntityStore>)beaconEntityPair.first();
/*     */ 
/*     */ 
/*     */               
/* 226 */               if (beaconRef != null && beaconRef.isValid())
/*     */               
/*     */               { 
/*     */                 
/* 230 */                 store.ensureComponent(beaconRef, this.localSpawnBeaconComponentType);
/* 231 */                 SpawningPlugin.get().getLogger().at(Level.FINE).log("Placed spawn beacon of type %s at position %s for player %s", ((BeaconNPCSpawn)possibleBeacon
/* 232 */                     .getSpawn()).getId(), position, playerRefComponent.getUsername());
/* 233 */                 pendingSpawns.add((LegacySpawnBeaconEntity)beaconEntityPair.second()); } 
/*     */             } 
/*     */           } 
/* 236 */         }  existingBeacons.clear();
/* 237 */         averageLightValues.clear();
/* 238 */         spawnControllerComponent.setTimeToNextRunSeconds(5.0D);
/*     */       } 
/*     */     } 
/* 241 */     controllers.clear();
/* 242 */     pendingSpawns.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean spawnLightLevelMatches(@Nonnull World world, int x, int y, int z, double sunlightFactor, @Nonnull BeaconSpawnWrapper wrapper, @Nonnull Object2ByteMap<LightType> averageValues) {
/* 248 */     LightRangePredicate lightRangePredicate = wrapper.getLightRangePredicate();
/* 249 */     if (lightRangePredicate.isTestLightValue()) {
/* 250 */       byte lightValue = getCachedAverageLightValue(LightType.Light, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> LightRangePredicate.calculateLightValue(_chunk, _x, _y, _z, _sunlightFactor), averageValues);
/*     */       
/* 252 */       if (!lightRangePredicate.testLight(lightValue)) return false;
/*     */     
/*     */     } 
/* 255 */     if (lightRangePredicate.isTestSkyLightValue()) {
/* 256 */       byte lightValue = getCachedAverageLightValue(LightType.SkyLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getSkyLight(_x, _y, _z), averageValues);
/*     */       
/* 258 */       if (!lightRangePredicate.testSkyLight(lightValue)) return false;
/*     */     
/*     */     } 
/* 261 */     if (lightRangePredicate.isTestSunlightValue()) {
/* 262 */       byte lightValue = getCachedAverageLightValue(LightType.Sunlight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> (byte)(int)(_chunk.getSkyLight(_x, _y, _z) * _sunlightFactor), averageValues);
/*     */       
/* 264 */       if (!lightRangePredicate.testSunlight(lightValue)) return false;
/*     */     
/*     */     } 
/* 267 */     if (lightRangePredicate.isTestRedLightValue()) {
/* 268 */       byte lightValue = getCachedAverageLightValue(LightType.RedLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getRedBlockLight(_x, _y, _z), averageValues);
/*     */       
/* 270 */       if (!lightRangePredicate.testRedLight(lightValue)) return false;
/*     */     
/*     */     } 
/* 273 */     if (lightRangePredicate.isTestGreenLightValue()) {
/* 274 */       byte lightValue = getCachedAverageLightValue(LightType.GreenLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getGreenBlockLight(_x, _y, _z), averageValues);
/*     */       
/* 276 */       if (!lightRangePredicate.testGreenLight(lightValue)) return false;
/*     */     
/*     */     } 
/* 279 */     if (lightRangePredicate.isTestBlueLightValue()) {
/* 280 */       byte lightValue = getCachedAverageLightValue(LightType.BlueLight, world, x, y, z, sunlightFactor, (_x, _y, _z, _chunk, _sunlightFactor) -> _chunk.getBlueBlockLight(_x, _y, _z), averageValues);
/*     */       
/* 282 */       return lightRangePredicate.testBlueLight(lightValue);
/*     */     } 
/*     */     
/* 285 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static byte getCachedAverageLightValue(LightType lightType, @Nonnull World world, int x, int y, int z, double sunlightFactor, @Nonnull TriIntObjectDoubleToByteFunction<BlockChunk> valueCalculator, @Nonnull Object2ByteMap<LightType> averageValues) {
/* 290 */     byte cachedValue = averageValues.getByte(lightType);
/* 291 */     if (cachedValue < 0) {
/* 292 */       int counted = 0;
/* 293 */       int total = 0;
/*     */ 
/*     */       
/* 296 */       for (int xOffset = x - 4; xOffset < x + 4; xOffset++) {
/* 297 */         for (int zOffset = z - 4; zOffset < z + 4; zOffset++) {
/* 298 */           WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(xOffset, zOffset));
/* 299 */           if (chunk != null) {
/*     */             
/* 301 */             BlockChunk blockChunk = chunk.getBlockChunk();
/* 302 */             for (int yOffset = y; yOffset < y + 4; yOffset++) {
/*     */               
/* 304 */               int blockId = chunk.getBlock(xOffset, yOffset, zOffset);
/* 305 */               if (blockId == 0 || ((BlockType)BlockType.getAssetMap().getAsset(blockId)).getMaterial() != BlockMaterial.Solid) {
/* 306 */                 counted++;
/* 307 */                 total += valueCalculator.apply(xOffset, yOffset, zOffset, blockChunk, sunlightFactor);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 313 */       cachedValue = (counted > 0) ? (byte)(int)(total / counted) : 0;
/* 314 */       averageValues.put(lightType, cachedValue);
/*     */     } 
/* 316 */     return cachedValue;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\spawning\local\LocalSpawnControllerSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */