/*     */ package com.hypixel.hytale.server.worldgen.loader.cave;
/*     */ 
/*     */ import com.google.gson.JsonElement;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.HeightThresholdCoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IHeightThresholdInterpreter;
/*     */ import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.FloatRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.NoisePropertyJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.PointGeneratorJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.procedurallib.logic.point.IPointGenerator;
/*     */ import com.hypixel.hytale.procedurallib.property.NoiseProperty;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
/*     */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CaveBiomeMaskFlags;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CaveType;
/*     */ import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
/*     */ import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.util.ConstantNoiseProperty;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.flag.Int2FlagsCondition;
/*     */ import java.nio.file.Path;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class CaveTypeJsonLoader
/*     */   extends JsonLoader<SeedStringResource, CaveType> {
/*     */   protected final Path caveFolder;
/*     */   protected final String name;
/*     */   protected final ZoneFileContext zoneContext;
/*     */   
/*     */   public CaveTypeJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, Path caveFolder, String name, ZoneFileContext zoneContext) {
/*  42 */     super(seed.append(".CaveType"), dataFolder, json);
/*  43 */     this.caveFolder = caveFolder;
/*  44 */     this.name = name;
/*  45 */     this.zoneContext = zoneContext;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public CaveType load() {
/*  51 */     IPointGenerator pointGenerator = loadEntryPointGenerator();
/*  52 */     return new CaveType(this.name, 
/*     */         
/*  54 */         loadEntryNodeType(), 
/*  55 */         loadYaw(), loadPitch(), 
/*  56 */         loadDepth(), 
/*  57 */         loadHeightFactors(), pointGenerator, 
/*     */         
/*  59 */         loadBiomeMask(), 
/*  60 */         loadBlockMask(), 
/*  61 */         loadMapCondition(), 
/*  62 */         loadHeightCondition(), 
/*  63 */         loadFixedEntryHeight(), 
/*  64 */         loadFixedEntryHeightNoise(), 
/*  65 */         loadFluidLevel(), 
/*  66 */         loadEnvironment(), 
/*  67 */         loadSurfaceLimited(), 
/*  68 */         loadSubmerge(), 
/*  69 */         loadMaximumSize(pointGenerator));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected IFloatRange loadYaw() {
/*  75 */     return (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("Yaw"), -180.0F, 180.0F, deg -> deg * 0.017453292F))
/*  76 */       .load();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected IFloatRange loadPitch() {
/*  81 */     return (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("Pitch"), -15.0F, deg -> deg * 0.017453292F))
/*  82 */       .load();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected IFloatRange loadDepth() {
/*  87 */     return (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("Depth"), 80.0F))
/*  88 */       .load();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IHeightThresholdInterpreter loadHeightFactors() {
/*  93 */     return (new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, get("HeightRadiusFactor"), 320))
/*  94 */       .load();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveNodeType loadEntryNodeType() {
/*  99 */     JsonElement entry = get("Entry");
/* 100 */     if (entry == null) {
/* 101 */       throw new IllegalArgumentException("\"Entry\" is not defined. Define an entry node type");
/*     */     }
/*     */     
/* 104 */     CaveNodeTypeStorage caveNodeTypeStorage = new CaveNodeTypeStorage(this.seed, this.dataFolder, this.caveFolder, this.zoneContext);
/*     */     
/* 106 */     if (entry.isJsonObject()) {
/* 107 */       String entryNodeTypeString = ((SeedStringResource)this.seed.get()).getUniqueName("CaveType#");
/* 108 */       return caveNodeTypeStorage.loadCaveNodeType(entryNodeTypeString, entry.getAsJsonObject());
/*     */     } 
/*     */     
/* 111 */     if (entry.isJsonPrimitive() && entry.getAsJsonPrimitive().isString()) {
/* 112 */       String entryNodeTypeString = entry.getAsString();
/* 113 */       return caveNodeTypeStorage.loadCaveNodeType(entryNodeTypeString);
/*     */     } 
/*     */     
/* 116 */     throw error("Invalid entry node type definition! Expected String or JsonObject: " + String.valueOf(entry), new Object[0]);
/*     */   }
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadHeightCondition() {
/*     */     HeightThresholdCoordinateCondition heightThresholdCoordinateCondition;
/* 121 */     DefaultCoordinateCondition defaultCoordinateCondition = DefaultCoordinateCondition.DEFAULT_TRUE;
/* 122 */     if (has("HeightThreshold")) {
/*     */       
/* 124 */       IHeightThresholdInterpreter interpreter = (new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, get("HeightThreshold"), 320)).load();
/* 125 */       heightThresholdCoordinateCondition = new HeightThresholdCoordinateCondition(interpreter);
/*     */     } 
/* 127 */     return (ICoordinateCondition)heightThresholdCoordinateCondition;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IPointGenerator loadEntryPointGenerator() {
/* 132 */     if (!has("EntryPoints")) throw new IllegalArgumentException("\"EntryPoints\" is not defined, no spawn information for caves available"); 
/* 133 */     return (new PointGeneratorJsonLoader(this.seed, this.dataFolder, get("EntryPoints")))
/* 134 */       .load();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Int2FlagsCondition loadBiomeMask() {
/* 139 */     Int2FlagsCondition mask = CaveBiomeMaskFlags.DEFAULT_ALLOW;
/* 140 */     if (has("BiomeMask")) {
/*     */       
/* 142 */       ZoneFileContext context = this.zoneContext.matchContext(this.json, "BiomeMask");
/*     */ 
/*     */       
/* 145 */       mask = (new CaveBiomeMaskJsonLoader(this.seed, this.dataFolder, get("BiomeMask"), context)).load();
/*     */     } 
/* 147 */     return mask;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected BlockMaskCondition loadBlockMask() {
/* 152 */     BlockMaskCondition placementConfiguration = BlockMaskCondition.DEFAULT_TRUE;
/* 153 */     if (has("BlockMask"))
/*     */     {
/* 155 */       placementConfiguration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("BlockMask"))).load();
/*     */     }
/* 157 */     return placementConfiguration;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadMapCondition() {
/* 162 */     return (new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, get("NoiseMask")))
/* 163 */       .load();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IDoubleRange loadFixedEntryHeight() {
/* 168 */     IDoubleRange fixedEntryHeight = null;
/* 169 */     if (has("FixedEntryHeight"))
/*     */     {
/* 171 */       fixedEntryHeight = (new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("FixedEntryHeight"), 0.0D)).load();
/*     */     }
/* 173 */     return fixedEntryHeight;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected NoiseProperty loadFixedEntryHeightNoise() {
/* 178 */     NoiseProperty maxNoise = ConstantNoiseProperty.DEFAULT_ZERO;
/* 179 */     if (has("FixedEntryHeightNoise"))
/*     */     {
/* 181 */       maxNoise = (new NoisePropertyJsonLoader(this.seed, this.dataFolder, get("FixedEntryHeightNoise"))).load();
/*     */     }
/* 183 */     return maxNoise;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveType.FluidLevel loadFluidLevel() {
/* 188 */     CaveType.FluidLevel fluidLevel = CaveType.FluidLevel.EMPTY;
/* 189 */     if (has("FluidLevel"))
/*     */     {
/* 191 */       fluidLevel = (new FluidLevelJsonLoader(this.seed, this.dataFolder, get("FluidLevel"))).load();
/*     */     }
/* 193 */     return fluidLevel;
/*     */   }
/*     */   
/*     */   protected int loadEnvironment() {
/* 197 */     int environment = Integer.MIN_VALUE;
/* 198 */     if (has("Environment")) {
/* 199 */       String environmentId = get("Environment").getAsString();
/* 200 */       environment = Environment.getAssetMap().getIndex(environmentId);
/* 201 */       if (environment == Integer.MIN_VALUE) throw new Error(String.format("Error while looking up environment \"%s\"!", new Object[] { environmentId })); 
/*     */     } 
/* 203 */     return environment;
/*     */   }
/*     */   
/*     */   protected boolean loadSurfaceLimited() {
/* 207 */     return (!has("SurfaceLimited") || get("SurfaceLimited").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected boolean loadSubmerge() {
/* 211 */     return mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE).booleanValue();
/*     */   }
/*     */   
/*     */   protected double loadMaximumSize(@Nonnull IPointGenerator pointGenerator) {
/* 215 */     return has("MaximumSize") ? get("MaximumSize").getAsLong() : MathUtil.fastFloor(pointGenerator.getInterval());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface Constants
/*     */   {
/*     */     public static final String KEY_YAW = "Yaw";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_PITCH = "Pitch";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_DEPTH = "Depth";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_HEIGHT_RADIUS_FACTOR = "HeightRadiusFactor";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_ENTRY = "Entry";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_ENTRY_POINTS = "EntryPoints";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_HEIGHT_THRESHOLDS = "HeightThreshold";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_BIOME_MASK = "BiomeMask";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_BLOCK_MASK = "BlockMask";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_NOISE_MASK = "NoiseMask";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_FIXED_ENTRY_HEIGHT = "FixedEntryHeight";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_FIXED_ENTRY_HEIGHT_NOISE = "FixedEntryHeightNoise";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_FLUID_LEVEL = "FluidLevel";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_SURFACE_LIMITTED = "SurfaceLimited";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_SUBMERGE = "Submerge";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_MAXIMUM_SIZE = "MaximumSize";
/*     */ 
/*     */ 
/*     */     
/*     */     public static final String KEY_ENVIRONMENT = "Environment";
/*     */ 
/*     */ 
/*     */     
/* 292 */     public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
/*     */     public static final String ERROR_NO_ENTRY = "\"Entry\" is not defined. Define an entry node type";
/*     */     public static final String ERROR_NO_ENTRY_POINTS = "\"EntryPoints\" is not defined, no spawn information for caves available";
/*     */     public static final String ERROR_LOADING_ENVIRONMENT = "Error while looking up environment \"%s\"!";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\cave\CaveTypeJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */