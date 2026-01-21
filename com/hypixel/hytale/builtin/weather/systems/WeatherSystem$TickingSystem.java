/*     */ package com.hypixel.hytale.builtin.weather.systems;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.builtin.weather.components.WeatherTracker;
/*     */ import com.hypixel.hytale.builtin.weather.resources.WeatherResource;
/*     */ import com.hypixel.hytale.common.map.IWeightedMap;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.ResourceType;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.ArchetypeTickingSystem;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*     */ import com.hypixel.hytale.server.core.asset.type.environment.config.WeatherForecast;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2IntMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ThreadLocalRandom;
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
/*     */ public class TickingSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*  92 */   private static final ComponentType<EntityStore, PlayerRef> PLAYER_REF_COMPONENT_TYPE = PlayerRef.getComponentType();
/*  93 */   private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();
/*  94 */   private static final ComponentType<EntityStore, WeatherTracker> WEATHER_TRACKER_COMPONENT_TYPE = WeatherTracker.getComponentType();
/*  95 */   private static final ResourceType<EntityStore, WeatherResource> WEATHER_RESOURCE_TYPE = WeatherResource.getResourceType();
/*  96 */   private static final Query<EntityStore> QUERY = (Query<EntityStore>)Archetype.of(new ComponentType[] { PLAYER_REF_COMPONENT_TYPE, TRANSFORM_COMPONENT_TYPE, WEATHER_TRACKER_COMPONENT_TYPE });
/*     */ 
/*     */   
/*     */   public Query<EntityStore> getQuery() {
/* 100 */     return QUERY;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
/* 105 */     WeatherResource weatherResource = (WeatherResource)store.getResource(WEATHER_RESOURCE_TYPE);
/*     */ 
/*     */     
/* 108 */     if (weatherResource.consumeForcedWeatherChange()) {
/* 109 */       weatherResource.playerUpdateDelay = 1.0F;
/* 110 */       store.tick((ArchetypeTickingSystem)this, dt, systemIndex);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 116 */     if (weatherResource.getForcedWeatherIndex() == 0) {
/* 117 */       WorldTimeResource worldTimeResource = (WorldTimeResource)store.getResource(WorldTimeResource.getResourceType());
/*     */ 
/*     */       
/* 120 */       int currentHour = worldTimeResource.getCurrentHour();
/* 121 */       if (weatherResource.compareAndSwapHour(currentHour)) {
/* 122 */         Int2IntMap environmentWeather = weatherResource.getEnvironmentWeather();
/*     */         
/* 124 */         ThreadLocalRandom random = ThreadLocalRandom.current();
/* 125 */         IndexedLookupTableAssetMap<String, Environment> assetMap = Environment.getAssetMap();
/* 126 */         for (Map.Entry<String, Environment> entry : (Iterable<Map.Entry<String, Environment>>)assetMap.getAssetMap().entrySet()) {
/* 127 */           String key = entry.getKey();
/* 128 */           int index = assetMap.getIndex(key);
/* 129 */           if (index == Integer.MIN_VALUE) throw new IllegalArgumentException("Unknown key! " + key);
/*     */           
/* 131 */           IWeightedMap<WeatherForecast> weatherForecast = ((Environment)entry.getValue()).getWeatherForecast(currentHour);
/* 132 */           int selectedWeatherIndex = ((WeatherForecast)weatherForecast.get(random)).getWeatherIndex();
/* 133 */           environmentWeather.put(index, selectedWeatherIndex);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 142 */     weatherResource.playerUpdateDelay -= dt;
/* 143 */     if (weatherResource.playerUpdateDelay <= 0.0F) {
/* 144 */       weatherResource.playerUpdateDelay = 1.0F;
/* 145 */       store.tick((ArchetypeTickingSystem)this, dt, systemIndex);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isParallel(int archetypeChunkSize, int taskCount) {
/* 151 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 156 */     WeatherResource weatherResource = (WeatherResource)store.getResource(WEATHER_RESOURCE_TYPE);
/*     */     
/* 158 */     PlayerRef playerRefComponent = (PlayerRef)archetypeChunk.getComponent(index, PLAYER_REF_COMPONENT_TYPE);
/* 159 */     assert playerRefComponent != null;
/*     */     
/* 161 */     TransformComponent transformComponent = (TransformComponent)archetypeChunk.getComponent(index, TRANSFORM_COMPONENT_TYPE);
/* 162 */     assert transformComponent != null;
/*     */     
/* 164 */     WeatherTracker weatherTrackerComponent = (WeatherTracker)archetypeChunk.getComponent(index, WEATHER_TRACKER_COMPONENT_TYPE);
/* 165 */     assert weatherTrackerComponent != null;
/*     */ 
/*     */ 
/*     */     
/* 169 */     float transitionSeconds = weatherTrackerComponent.consumeFirstSendForWorld() ? 0.5F : 10.0F;
/*     */     
/* 171 */     weatherTrackerComponent.updateWeather(playerRefComponent, weatherResource, transformComponent, transitionSeconds, (ComponentAccessor)commandBuffer);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\weather\systems\WeatherSystem$TickingSystem.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */