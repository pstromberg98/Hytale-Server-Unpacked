/*     */ package com.hypixel.hytale.server.worldgen.loader.prefab;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.hypixel.hytale.procedurallib.condition.ConstantBlockFluidCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.HeightCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IBlockFluidCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
/*     */ import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.PointGeneratorJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
/*     */ import com.hypixel.hytale.server.worldgen.prefab.PrefabPatternGenerator;
/*     */ import com.hypixel.hytale.server.worldgen.util.LogUtil;
/*     */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.HashSetBlockFluidCondition;
/*     */ import com.hypixel.hytale.server.worldgen.util.function.ConstantCoordinateDoubleSupplier;
/*     */ import com.hypixel.hytale.server.worldgen.util.function.ICoordinateDoubleSupplier;
/*     */ import com.hypixel.hytale.server.worldgen.util.function.RandomCoordinateDoubleSupplier;
/*     */ import it.unimi.dsi.fastutil.longs.LongSet;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class PrefabPatternGeneratorJsonLoader extends JsonLoader<SeedStringResource, PrefabPatternGenerator> {
/*     */   public PrefabPatternGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, FileLoadingContext context) {
/*  41 */     super(seed.append(".PrefabPatternGenerator"), dataFolder, json);
/*  42 */     this.context = context;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public PrefabPatternGenerator load() {
/*  48 */     IHeightThresholdInterpreter heightThresholds = loadHeightThresholds();
/*  49 */     return new PrefabPatternGenerator(this.seed
/*  50 */         .hashCode(), 
/*  51 */         loadCategory(), 
/*  52 */         loadPattern(), 
/*  53 */         loadHeightCondition(heightThresholds), heightThresholds, 
/*     */         
/*  55 */         loadMask(), 
/*  56 */         loadMapCondition(), 
/*  57 */         loadParent(), 
/*  58 */         loadRotations(), 
/*  59 */         loadDisplacement(), 
/*  60 */         loadFitHeightmap(), 
/*  61 */         loadOnWater(), 
/*  62 */         loadDeepSearch(heightThresholds), 
/*  63 */         loadSubmerge(), 
/*  64 */         loadMaxSize(), 
/*  65 */         loadExclusionRadius());
/*     */   }
/*     */   private final FileLoadingContext context;
/*     */   
/*     */   @Nullable
/*     */   protected IPointGenerator loadPattern() {
/*  71 */     if (!has("GridGenerator")) throw new IllegalArgumentException("Could not find point generator to place prefabs at! Keyword: GridGenerator"); 
/*  72 */     return (new PointGeneratorJsonLoader(this.seed, this.dataFolder, get("GridGenerator"))).load();
/*     */   }
/*     */   
/*     */   protected PrefabCategory loadCategory() {
/*  76 */     String category = mustGetString("Category", "");
/*     */     
/*  78 */     if (category.isEmpty()) {
/*  79 */       return PrefabCategory.NONE;
/*     */     }
/*     */     
/*  82 */     if (!this.context.getPrefabCategories().contains(category)) {
/*  83 */       LogUtil.getLogger().at(Level.WARNING).log("Could not find prefab category: %s, defaulting to None", category);
/*  84 */       return PrefabCategory.NONE;
/*     */     } 
/*     */     
/*  87 */     return (PrefabCategory)this.context.getPrefabCategories().get(category);
/*     */   }
/*     */   @Nonnull
/*     */   protected IBlockFluidCondition loadParent() {
/*     */     HashSetBlockFluidCondition hashSetBlockFluidCondition;
/*  92 */     ConstantBlockFluidCondition constantBlockFluidCondition = ConstantBlockFluidCondition.DEFAULT_TRUE;
/*  93 */     if (has("Parent")) {
/*     */       
/*  95 */       ResolvedBlockArray blockArray = (new ResolvedBlockArrayJsonLoader(this.seed, this.dataFolder, get("Parent"))).load();
/*  96 */       LongSet biomeSet = blockArray.getEntrySet();
/*  97 */       hashSetBlockFluidCondition = new HashSetBlockFluidCondition(biomeSet);
/*     */     } 
/*  99 */     return (IBlockFluidCondition)hashSetBlockFluidCondition;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IHeightThresholdInterpreter loadHeightThresholds() {
/* 104 */     IHeightThresholdInterpreter heightThreshold = null;
/* 105 */     if (has("HeightThreshold"))
/*     */     {
/* 107 */       heightThreshold = (new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, get("HeightThreshold"), 320)).load();
/*     */     }
/* 109 */     return heightThreshold;
/*     */   }
/*     */   @Nonnull
/*     */   protected ICoordinateRndCondition loadHeightCondition(@Nullable IHeightThresholdInterpreter thresholdInterpreter) {
/*     */     HeightCondition heightCondition;
/* 114 */     DefaultCoordinateRndCondition defaultCoordinateRndCondition = DefaultCoordinateRndCondition.DEFAULT_TRUE;
/* 115 */     if (thresholdInterpreter != null) {
/* 116 */       heightCondition = new HeightCondition(thresholdInterpreter);
/*     */     }
/* 118 */     return (ICoordinateRndCondition)heightCondition;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadMapCondition() {
/* 123 */     return (new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, get("NoiseMask")))
/* 124 */       .load();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected BlockMaskCondition loadMask() {
/* 129 */     BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
/* 130 */     if (has("Mask"))
/*     */     {
/* 132 */       configuration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("Mask"))).load();
/*     */     }
/* 134 */     return configuration;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected PrefabRotation[] loadRotations() {
/* 139 */     PrefabRotation[] prefabRotations = null;
/* 140 */     if (has("Rotations")) {
/* 141 */       prefabRotations = loadRotations(get("Rotations"));
/*     */     }
/* 143 */     return prefabRotations;
/*     */   }
/*     */   @Nonnull
/*     */   protected ICoordinateDoubleSupplier loadDisplacement() {
/*     */     RandomCoordinateDoubleSupplier randomCoordinateDoubleSupplier;
/* 148 */     ConstantCoordinateDoubleSupplier constantCoordinateDoubleSupplier = ConstantCoordinateDoubleSupplier.DEFAULT_ZERO;
/* 149 */     if (has("Displacement"))
/*     */     {
/* 151 */       randomCoordinateDoubleSupplier = new RandomCoordinateDoubleSupplier((new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("Displacement"), 0.0D)).load());
/*     */     }
/* 153 */     return (ICoordinateDoubleSupplier)randomCoordinateDoubleSupplier;
/*     */   }
/*     */   
/*     */   protected boolean loadFitHeightmap() {
/* 157 */     return (has("FitHeightmap") && get("FitHeightmap").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected boolean loadOnWater() {
/* 161 */     return (has("OnWater") && get("OnWater").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected boolean loadDeepSearch(@Nonnull IHeightThresholdInterpreter interpreter) {
/* 165 */     boolean deepSearch = (has("DeepSearch") && get("DeepSearch").getAsBoolean());
/* 166 */     if (deepSearch && interpreter == null) throw new IllegalArgumentException("DeepSearch is enabled but HeightThreshold is not set!"); 
/* 167 */     return deepSearch;
/*     */   }
/*     */   
/*     */   protected boolean loadSubmerge() {
/* 171 */     return mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE).booleanValue();
/*     */   }
/*     */   
/*     */   protected int loadMaxSize() {
/* 175 */     return mustGetNumber("MaxSize", Constants.DEFAULT_MAX_SIZE).intValue();
/*     */   }
/*     */   
/*     */   protected int loadExclusionRadius() {
/* 179 */     return mustGetNumber("ExclusionRadius", Constants.DEFAULT_EXCLUSION_RADIUS).intValue();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static PrefabRotation[] loadRotations(@Nullable JsonElement element) {
/* 184 */     if (element == null) {
/* 185 */       return null;
/*     */     }
/*     */     
/* 188 */     PrefabRotation[] prefabRotations = null;
/*     */     
/* 190 */     if (element.isJsonArray()) {
/* 191 */       JsonArray array = element.getAsJsonArray();
/* 192 */       if (array.size() <= 0) throw new IllegalArgumentException("Array for rotations must be greater than 0 or left away to allow random rotation."); 
/* 193 */       prefabRotations = new PrefabRotation[array.size()];
/* 194 */       for (int i = 0; i < prefabRotations.length; i++) {
/* 195 */         String name = array.get(i).getAsString();
/*     */         try {
/* 197 */           prefabRotations[i] = PrefabRotation.valueOf(name);
/* 198 */         } catch (Throwable e) {
/* 199 */           throw new Error("Could not find rotation \"" + name + "\". Allowed: " + Arrays.toString(PrefabRotation.VALUES));
/*     */         } 
/*     */       } 
/* 202 */     } else if (element.isJsonPrimitive()) {
/* 203 */       prefabRotations = new PrefabRotation[1];
/* 204 */       String name = element.getAsString();
/*     */       try {
/* 206 */         prefabRotations[0] = PrefabRotation.valueOf(name);
/* 207 */       } catch (Throwable e) {
/* 208 */         throw new Error("Could not find rotation \"" + name + "\". Allowed: " + Arrays.toString(PrefabRotation.VALUES));
/*     */       } 
/*     */     } else {
/* 211 */       throw new IllegalArgumentException("rotations is not an array nor a string, other types are not supported! Given: " + String.valueOf(element));
/*     */     } 
/*     */     
/* 214 */     return prefabRotations;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Constants
/*     */   {
/*     */     public static final String KEY_GRID_GENERATOR = "GridGenerator";
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_PARENT = "Parent";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_HEIGHT_THRESHOLD = "HeightThreshold";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_NOISE_MASK = "NoiseMask";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_MASK = "Mask";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_ROTATIONS = "Rotations";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_DISPLACEMENT = "Displacement";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_FIT_HEIGHTMAP = "FitHeightmap";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_ON_WATER = "OnWater";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_DEEP_SEARCH = "DeepSearch";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_SUBMERGE = "Submerge";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_MAX_SIZE = "MaxSize";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_EXCLUSION_RADIUS = "ExclusionRadius";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_CATEGORY = "Category";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String ERROR_NO_GRID_GENERATOR = "Could not find point generator to place prefabs at! Keyword: GridGenerator";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String ERROR_DEEP_SEARCH = "DeepSearch is enabled but HeightThreshold is not set!";
/*     */ 
/*     */ 
/*     */     
/* 288 */     public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
/* 289 */     public static final Integer DEFAULT_MAX_SIZE = Integer.valueOf(5);
/* 290 */     public static final Integer DEFAULT_EXCLUSION_RADIUS = Integer.valueOf(0);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\prefab\PrefabPatternGeneratorJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */