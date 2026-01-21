/*     */ package com.hypixel.hytale.server.worldgen.loader.cave;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.hypixel.hytale.procedurallib.condition.ConstantIntCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.HeightCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
/*     */ import com.hypixel.hytale.procedurallib.condition.IIntCondition;
/*     */ import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.HeightThresholdInterpreterJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.NoiseMaskConditionJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.procedurallib.supplier.ConstantDoubleCoordinateHashSupplier;
/*     */ import com.hypixel.hytale.procedurallib.supplier.DoubleRangeCoordinateHashSupplier;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IDoubleCoordinateHashSupplier;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CavePrefabPlacement;
/*     */ import com.hypixel.hytale.server.worldgen.cave.prefab.CavePrefabContainer;
/*     */ import com.hypixel.hytale.server.worldgen.loader.biome.BiomeMaskJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
/*     */ import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Arrays;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class CavePrefabConfigJsonLoader
/*     */   extends JsonLoader<SeedStringResource, CavePrefabContainer.CavePrefabEntry.CavePrefabConfig> {
/*     */   public CavePrefabConfigJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, ZoneFileContext zoneContext) {
/*  36 */     super(seed.append(".CavePrefabConfig"), dataFolder, json);
/*  37 */     this.zoneContext = zoneContext;
/*     */   }
/*     */   private final ZoneFileContext zoneContext;
/*     */   
/*     */   @Nonnull
/*     */   public CavePrefabContainer.CavePrefabEntry.CavePrefabConfig load() {
/*  43 */     return new CavePrefabContainer.CavePrefabEntry.CavePrefabConfig(
/*  44 */         loadRotations(), 
/*  45 */         loadPlacement(), 
/*  46 */         loadBiomeMask(), 
/*  47 */         loadBlockMask(), 
/*  48 */         loadIterations(), 
/*  49 */         loadDisplacementSupplier(), 
/*  50 */         loadNoiseCondition(), 
/*  51 */         loadHeightCondition());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected PrefabRotation[] loadRotations() {
/*  57 */     PrefabRotation[] prefabRotations = PrefabRotation.VALUES;
/*  58 */     if (has("Rotations")) {
/*  59 */       JsonElement element = get("Rotations");
/*  60 */       if (element.isJsonArray()) {
/*  61 */         JsonArray array = element.getAsJsonArray();
/*  62 */         if (array.size() <= 0) throw new IllegalArgumentException("Array for rotations must have at least one entry or left away to allow random rotation"); 
/*  63 */         prefabRotations = new PrefabRotation[array.size()];
/*  64 */         for (int i = 0; i < prefabRotations.length; i++) {
/*  65 */           String name = array.get(i).getAsString();
/*     */           try {
/*  67 */             prefabRotations[i] = PrefabRotation.valueOf(name);
/*  68 */           } catch (Throwable e) {
/*  69 */             throw new Error(String.format(Constants.ERROR_ROTATIONS_UNKOWN, new Object[] { name }));
/*     */           } 
/*     */         } 
/*  72 */       } else if (element.isJsonPrimitive()) {
/*  73 */         prefabRotations = new PrefabRotation[1];
/*  74 */         String name = element.getAsString();
/*     */         try {
/*  76 */           prefabRotations[0] = PrefabRotation.valueOf(name);
/*  77 */         } catch (Throwable e) {
/*  78 */           throw new Error(String.format(Constants.ERROR_ROTATIONS_UNKOWN, new Object[] { name }));
/*     */         } 
/*     */       } else {
/*  81 */         throw new Error(String.format("\"Rotations\" is not an array nor a string, other types are not supported! Given: %s", new Object[] { element }));
/*     */       } 
/*     */     } 
/*  84 */     return prefabRotations;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CavePrefabPlacement loadPlacement() {
/*  89 */     CavePrefabPlacement placement = CavePrefabPlacement.DEFAULT;
/*  90 */     if (has("Placement")) {
/*  91 */       placement = CavePrefabPlacement.valueOf(get("Placement").getAsString());
/*     */     }
/*  93 */     return placement;
/*     */   }
/*     */   @Nullable
/*     */   protected IIntCondition loadBiomeMask() {
/*     */     IIntCondition iIntCondition;
/*  98 */     ConstantIntCondition constantIntCondition = ConstantIntCondition.DEFAULT_TRUE;
/*  99 */     if (has("BiomeMask")) {
/*     */       
/* 101 */       ZoneFileContext context = this.zoneContext.matchContext(this.json, "BiomeMask");
/*     */ 
/*     */       
/* 104 */       iIntCondition = (new BiomeMaskJsonLoader(this.seed, this.dataFolder, get("BiomeMask"), "Prefab", context)).load();
/*     */     } 
/* 106 */     return iIntCondition;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected BlockMaskCondition loadBlockMask() {
/* 111 */     BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
/* 112 */     if (has("Mask"))
/*     */     {
/* 114 */       configuration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("Mask"))).load();
/*     */     }
/* 116 */     return configuration;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IDoubleRange loadIterations() {
/* 121 */     return (new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("Iterations"), 5.0D))
/* 122 */       .load();
/*     */   }
/*     */   @Nonnull
/*     */   protected IDoubleCoordinateHashSupplier loadDisplacementSupplier() {
/*     */     DoubleRangeCoordinateHashSupplier doubleRangeCoordinateHashSupplier;
/* 127 */     ConstantDoubleCoordinateHashSupplier constantDoubleCoordinateHashSupplier = ConstantDoubleCoordinateHashSupplier.ZERO;
/* 128 */     if (has("Displacement"))
/*     */     {
/* 130 */       doubleRangeCoordinateHashSupplier = new DoubleRangeCoordinateHashSupplier((new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("Displacement"), 0.0D)).load());
/*     */     }
/* 132 */     return (IDoubleCoordinateHashSupplier)doubleRangeCoordinateHashSupplier;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected ICoordinateCondition loadNoiseCondition() {
/* 137 */     return (new NoiseMaskConditionJsonLoader(this.seed, this.dataFolder, get("NoiseMask")))
/* 138 */       .load();
/*     */   }
/*     */   @Nonnull
/*     */   protected ICoordinateRndCondition loadHeightCondition() {
/*     */     HeightCondition heightCondition;
/* 143 */     DefaultCoordinateRndCondition defaultCoordinateRndCondition = DefaultCoordinateRndCondition.DEFAULT_TRUE;
/* 144 */     if (has("HeightThreshold"))
/*     */     {
/* 146 */       heightCondition = new HeightCondition((new HeightThresholdInterpreterJsonLoader(this.seed, this.dataFolder, get("HeightThreshold"), 320)).load());
/*     */     }
/* 148 */     return (ICoordinateRndCondition)heightCondition;
/*     */   }
/*     */ 
/*     */   
/*     */   public static interface Constants
/*     */   {
/*     */     public static final String KEY_ROTATIONS = "Rotations";
/*     */     public static final String KEY_PLACEMENT = "Placement";
/*     */     public static final String KEY_BIOME_MASK = "BiomeMask";
/*     */     public static final String KEY_BLOCK_MASK = "Mask";
/*     */     public static final String KEY_ITERATIONS = "Iterations";
/*     */     public static final String KEY_DISPLACEMENT = "Displacement";
/*     */     public static final String KEY_NOISE_MASK = "NoiseMask";
/*     */     public static final String KEY_HEIGHT_THRESHOLD = "HeightThreshold";
/*     */     public static final String SEED_STRING_BIOME_MASK_TYPE = "Prefab";
/*     */     public static final String ERROR_ROTATIONS_MUST_POSITIVE = "Array for rotations must have at least one entry or left away to allow random rotation";
/* 164 */     public static final String ERROR_ROTATIONS_UNKOWN = "Could not find rotation \"%s\". Allowed: " + Arrays.toString((Object[])PrefabRotation.VALUES);
/*     */     public static final String ERROR_ROTATIONS_UNKOWN_TYPE = "\"Rotations\" is not an array nor a string, other types are not supported! Given: %s";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\cave\CavePrefabConfigJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */