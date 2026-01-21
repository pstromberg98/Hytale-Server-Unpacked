/*     */ package com.hypixel.hytale.server.worldgen.loader.prefab.unique;
/*     */ 
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.math.vector.Vector2d;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.procedurallib.condition.ConstantBlockFluidCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.HeightCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IIntCondition;
/*     */ import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.loader.biome.BiomeMaskJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
/*     */ import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.prefab.PrefabPatternGeneratorJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.Vector2dJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.Vector3dJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.prefab.unique.UniquePrefabConfiguration;
/*     */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.HashSetBlockFluidCondition;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.nio.file.Path;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UniquePrefabConfigurationJsonLoader
/*     */   extends JsonLoader<SeedStringResource, UniquePrefabConfiguration>
/*     */ {
/*     */   protected final ZoneFileContext zoneContext;
/*     */   
/*     */   public UniquePrefabConfigurationJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, ZoneFileContext zoneContext) {
/*  45 */     super(seed, dataFolder, json);
/*  46 */     this.zoneContext = zoneContext;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public UniquePrefabConfiguration load() {
/*  52 */     return new UniquePrefabConfiguration(
/*  53 */         loadHeightThresholds(), 
/*  54 */         loadMask(), 
/*  55 */         loadRotations(), 
/*  56 */         loadBiomeMask(), 
/*  57 */         loadMapCondition(), 
/*  58 */         loadParent(), 
/*  59 */         loadAnchor(), 
/*  60 */         loadSpawnOffset(), 
/*  61 */         loadMaxDistance(), 
/*  62 */         loadFitHeightmap(), 
/*  63 */         loadSubmerge(), 
/*  64 */         loadOnWater(), 
/*  65 */         loadEnvironment(), 
/*  66 */         loadMaxAttempts(), 
/*  67 */         loadExclusionRadius(), 
/*  68 */         loadIsSpawn(), 
/*  69 */         loadZoneBorderExclusion(), 
/*  70 */         loadShowOnMap());
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected IBlockFluidCondition loadParent() {
/*     */     HashSetBlockFluidCondition hashSetBlockFluidCondition;
/*  76 */     ConstantBlockFluidCondition constantBlockFluidCondition = ConstantBlockFluidCondition.DEFAULT_TRUE;
/*  77 */     if (has("Parent")) {
/*     */       
/*  79 */       ResolvedBlockArray blockArray = (new ResolvedBlockArrayJsonLoader(this.seed, this.dataFolder, get("Parent"))).load();
/*  80 */       LongSet biomeSet = blockArray.getEntrySet();
/*  81 */       hashSetBlockFluidCondition = new HashSetBlockFluidCondition(biomeSet);
/*     */     } 
/*  83 */     return (IBlockFluidCondition)hashSetBlockFluidCondition;
/*     */   }
/*     */   @Nullable
/*     */   protected ICoordinateRndCondition loadHeightThresholds() {
/*     */     HeightCondition heightCondition1;
/*  88 */     ICoordinateRndCondition heightCondition = null;
/*  89 */     if (has("HeightThreshold")) {
/*  90 */       JsonObject heightThresholdObject = get("HeightThreshold").getAsJsonObject();
/*     */       
/*  92 */       heightCondition1 = new HeightCondition((new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, (JsonElement)heightThresholdObject, 320)).load());
/*     */     } 
/*  94 */     return (ICoordinateRndCondition)heightCondition1;
/*     */   }
/*     */   @Nullable
/*     */   protected IIntCondition loadBiomeMask() {
/*     */     IIntCondition iIntCondition;
/*  99 */     ConstantIntCondition constantIntCondition = ConstantIntCondition.DEFAULT_TRUE;
/* 100 */     if (has("BiomeMask"))
/*     */     {
/* 102 */       iIntCondition = (new BiomeMaskJsonLoader(this.seed, this.dataFolder, get("BiomeMask"), "UniquePrefab", this.zoneContext)).load();
/*     */     }
/* 104 */     return iIntCondition;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected PrefabRotation[] loadRotations() {
/* 109 */     PrefabRotation[] prefabRotations = null;
/* 110 */     if (has("Rotations")) {
/* 111 */       prefabRotations = PrefabPatternGeneratorJsonLoader.loadRotations(get("Rotations"));
/*     */     }
/* 113 */     return prefabRotations;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadMapCondition() {
/* 118 */     return (new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, get("NoiseMask")))
/* 119 */       .load();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected BlockMaskCondition loadMask() {
/* 124 */     BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
/* 125 */     if (has("Mask"))
/*     */     {
/* 127 */       configuration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("Mask"))).load();
/*     */     }
/* 129 */     return configuration;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Vector2d loadAnchor() {
/* 134 */     if (!has("Anchor")) throw new IllegalArgumentException("Could not find anchor for Unique prefab generator"); 
/* 135 */     return (new Vector2dJsonLoader(this.seed, this.dataFolder, get("Anchor")))
/* 136 */       .load();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Vector3d loadSpawnOffset() {
/* 142 */     Vector3d offset = new Vector3d(0.0D, -5000.0D, 0.0D);
/* 143 */     if (has("SpawnOffset"))
/*     */     {
/* 145 */       offset = (new Vector3dJsonLoader(this.seed, this.dataFolder, get("SpawnOffset"))).load();
/*     */     }
/* 147 */     return offset;
/*     */   }
/*     */   
/*     */   protected int loadEnvironment() {
/* 151 */     int environment = Integer.MIN_VALUE;
/* 152 */     if (has("Environment")) {
/* 153 */       String environmentId = get("Environment").getAsString();
/* 154 */       environment = Environment.getAssetMap().getIndex(environmentId);
/* 155 */       if (environment == Integer.MIN_VALUE) throw new Error(String.format("Error while looking up environment \"%s\"!", new Object[] { environmentId })); 
/*     */     } 
/* 157 */     return environment;
/*     */   }
/*     */   
/*     */   protected boolean loadFitHeightmap() {
/* 161 */     return (has("FitHeightmap") && get("FitHeightmap").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected boolean loadSubmerge() {
/* 165 */     return mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE).booleanValue();
/*     */   }
/*     */   
/*     */   protected boolean loadOnWater() {
/* 169 */     return (has("OnWater") && get("OnWater").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected double loadMaxDistance() {
/* 173 */     return has("MaxDistance") ? get("MaxDistance").getAsDouble() : 100.0D;
/*     */   }
/*     */   
/*     */   protected int loadMaxAttempts() {
/* 177 */     return has("MaxAttempts") ? get("MaxAttempts").getAsInt() : 5000;
/*     */   }
/*     */   
/*     */   protected double loadExclusionRadius() {
/* 181 */     return has("ExclusionRadius") ? get("ExclusionRadius").getAsDouble() : 50.0D;
/*     */   }
/*     */   
/*     */   protected boolean loadIsSpawn() {
/* 185 */     return (has("IsSpawn") && get("IsSpawn").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected double loadZoneBorderExclusion() {
/* 189 */     return has("BorderExclusion") ? get("BorderExclusion").getAsDouble() : 25.0D;
/*     */   }
/*     */   
/*     */   protected boolean loadShowOnMap() {
/* 193 */     return (has("ShowOnMap") && get("ShowOnMap").getAsBoolean());
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Constants
/*     */   {
/*     */     public static final String KEY_PARENT = "Parent";
/*     */     
/*     */     public static final String KEY_HEIGHT_THRESHOLD = "HeightThreshold";
/*     */     
/*     */     public static final String KEY_BIOME_MASK = "BiomeMask";
/*     */     public static final String KEY_NOISE_MASK = "NoiseMask";
/*     */     public static final String KEY_MASK = "Mask";
/*     */     public static final String KEY_ANCHOR = "Anchor";
/*     */     public static final String KEY_FIT_HEIGHTMAP = "FitHeightmap";
/*     */     public static final String KEY_SUBMERGE = "Submerge";
/*     */     public static final String KEY_ENVIRONMENT = "Environment";
/*     */     public static final String KEY_ON_WATER = "OnWater";
/*     */     public static final String KEY_MAX_DISTANCE = "MaxDistance";
/*     */     public static final String KEY_MAX_ATTEMPTS = "MaxAttempts";
/*     */     public static final String KEY_EXCLUSION_RADIUS = "ExclusionRadius";
/*     */     public static final String KEY_IS_SPAWN = "IsSpawn";
/*     */     public static final String KEY_SPAWN_OFFSET = "SpawnOffset";
/*     */     public static final String KEY_BORDER_EXCLUSION = "BorderExclusion";
/*     */     public static final String KEY_SHOW_ON_MAP = "ShowOnMap";
/*     */     public static final String SEED_STRING_BIOME_MASK_TYPE = "UniquePrefab";
/*     */     public static final String ERROR_BIOME_ERROR_MASK = "Could not find tile / custom biome \"%s\" for biome mask. Typo or disabled biome?";
/*     */     public static final String ERROR_NO_ANCHOR = "Could not find anchor for Unique prefab generator";
/*     */     public static final String ERROR_LOADING_ENVIRONMENT = "Error while looking up environment \"%s\"!";
/*     */     public static final double DEFAULT_MAX_DISTANCE = 100.0D;
/*     */     public static final int DEFAULT_MAX_ATTEMPTS = 5000;
/*     */     public static final double DEFAULT_EXCLUSION_RADIUS = 50.0D;
/*     */     public static final double DEFAULT_ZONE_BORDER_EXCLUSION = 25.0D;
/* 226 */     public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\prefa\\unique\UniquePrefabConfigurationJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */