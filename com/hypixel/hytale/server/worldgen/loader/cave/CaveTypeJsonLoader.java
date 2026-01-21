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
/*  99 */     if (!has("Entry")) throw new IllegalArgumentException("\"Entry\" is not defined. Define an entry node type"); 
/* 100 */     String entryNodeTypeString = get("Entry").getAsString();
/* 101 */     CaveNodeTypeStorage caveNodeTypeStorage = new CaveNodeTypeStorage(this.seed, this.dataFolder, this.caveFolder, this.zoneContext);
/* 102 */     return caveNodeTypeStorage.loadCaveNodeType(entryNodeTypeString);
/*     */   }
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadHeightCondition() {
/*     */     HeightThresholdCoordinateCondition heightThresholdCoordinateCondition;
/* 107 */     DefaultCoordinateCondition defaultCoordinateCondition = DefaultCoordinateCondition.DEFAULT_TRUE;
/* 108 */     if (has("HeightThreshold")) {
/*     */       
/* 110 */       IHeightThresholdInterpreter interpreter = (new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, get("HeightThreshold"), 320)).load();
/* 111 */       heightThresholdCoordinateCondition = new HeightThresholdCoordinateCondition(interpreter);
/*     */     } 
/* 113 */     return (ICoordinateCondition)heightThresholdCoordinateCondition;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IPointGenerator loadEntryPointGenerator() {
/* 118 */     if (!has("EntryPoints")) throw new IllegalArgumentException("\"EntryPoints\" is not defined, no spawn information for caves available"); 
/* 119 */     return (new PointGeneratorJsonLoader(this.seed, this.dataFolder, get("EntryPoints")))
/* 120 */       .load();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Int2FlagsCondition loadBiomeMask() {
/* 125 */     Int2FlagsCondition mask = CaveBiomeMaskFlags.DEFAULT_ALLOW;
/* 126 */     if (has("BiomeMask")) {
/*     */       
/* 128 */       ZoneFileContext context = this.zoneContext.matchContext(this.json, "BiomeMask");
/*     */ 
/*     */       
/* 131 */       mask = (new CaveBiomeMaskJsonLoader(this.seed, this.dataFolder, get("BiomeMask"), context)).load();
/*     */     } 
/* 133 */     return mask;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected BlockMaskCondition loadBlockMask() {
/* 138 */     BlockMaskCondition placementConfiguration = BlockMaskCondition.DEFAULT_TRUE;
/* 139 */     if (has("BlockMask"))
/*     */     {
/* 141 */       placementConfiguration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("BlockMask"))).load();
/*     */     }
/* 143 */     return placementConfiguration;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadMapCondition() {
/* 148 */     return (new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, get("NoiseMask")))
/* 149 */       .load();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IDoubleRange loadFixedEntryHeight() {
/* 154 */     IDoubleRange fixedEntryHeight = null;
/* 155 */     if (has("FixedEntryHeight"))
/*     */     {
/* 157 */       fixedEntryHeight = (new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("FixedEntryHeight"), 0.0D)).load();
/*     */     }
/* 159 */     return fixedEntryHeight;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected NoiseProperty loadFixedEntryHeightNoise() {
/* 164 */     NoiseProperty maxNoise = ConstantNoiseProperty.DEFAULT_ZERO;
/* 165 */     if (has("FixedEntryHeightNoise"))
/*     */     {
/* 167 */       maxNoise = (new NoisePropertyJsonLoader(this.seed, this.dataFolder, get("FixedEntryHeightNoise"))).load();
/*     */     }
/* 169 */     return maxNoise;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveType.FluidLevel loadFluidLevel() {
/* 174 */     CaveType.FluidLevel fluidLevel = CaveType.FluidLevel.EMPTY;
/* 175 */     if (has("FluidLevel"))
/*     */     {
/* 177 */       fluidLevel = (new FluidLevelJsonLoader(this.seed, this.dataFolder, get("FluidLevel"))).load();
/*     */     }
/* 179 */     return fluidLevel;
/*     */   }
/*     */   
/*     */   protected int loadEnvironment() {
/* 183 */     int environment = Integer.MIN_VALUE;
/* 184 */     if (has("Environment")) {
/* 185 */       String environmentId = get("Environment").getAsString();
/* 186 */       environment = Environment.getAssetMap().getIndex(environmentId);
/* 187 */       if (environment == Integer.MIN_VALUE) throw new Error(String.format("Error while looking up environment \"%s\"!", new Object[] { environmentId })); 
/*     */     } 
/* 189 */     return environment;
/*     */   }
/*     */   
/*     */   protected boolean loadSurfaceLimited() {
/* 193 */     return (!has("SurfaceLimited") || get("SurfaceLimited").getAsBoolean());
/*     */   }
/*     */   
/*     */   protected boolean loadSubmerge() {
/* 197 */     return mustGetBool("Submerge", Constants.DEFAULT_SUBMERGE).booleanValue();
/*     */   }
/*     */   
/*     */   protected double loadMaximumSize(@Nonnull IPointGenerator pointGenerator) {
/* 201 */     return has("MaximumSize") ? get("MaximumSize").getAsLong() : MathUtil.fastFloor(pointGenerator.getInterval());
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
/* 278 */     public static final Boolean DEFAULT_SUBMERGE = Boolean.FALSE;
/*     */     public static final String ERROR_NO_ENTRY = "\"Entry\" is not defined. Define an entry node type";
/*     */     public static final String ERROR_NO_ENTRY_POINTS = "\"EntryPoints\" is not defined, no spawn information for caves available";
/*     */     public static final String ERROR_LOADING_ENVIRONMENT = "Error while looking up environment \"%s\"!";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\cave\CaveTypeJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */