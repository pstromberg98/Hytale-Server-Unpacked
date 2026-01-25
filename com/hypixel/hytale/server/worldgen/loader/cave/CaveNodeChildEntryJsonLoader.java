/*     */ package com.hypixel.hytale.server.worldgen.loader.cave;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.hypixel.hytale.common.map.IWeightedMap;
/*     */ import com.hypixel.hytale.common.map.WeightedMap;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.FloatRangeJsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.procedurallib.supplier.DoubleRange;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
/*     */ import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
/*     */ import com.hypixel.hytale.server.worldgen.cave.CaveYawMode;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Random;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CaveNodeChildEntryJsonLoader
/*     */   extends JsonLoader<SeedStringResource, CaveNodeType.CaveNodeChildEntry>
/*     */ {
/*     */   protected final CaveNodeTypeStorage storage;
/*     */   
/*     */   public CaveNodeChildEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, CaveNodeTypeStorage storage) {
/*  35 */     super(seed.append(".CaveNodeChildEntry"), dataFolder, json);
/*  36 */     this.storage = storage;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public CaveNodeType.CaveNodeChildEntry load() {
/*  42 */     return new CaveNodeType.CaveNodeChildEntry(
/*  43 */         loadNodes(), 
/*  44 */         loadAnchor(), 
/*  45 */         loadOffset(), 
/*  46 */         loadRotations(), 
/*  47 */         loadChildrenLimit(), 
/*  48 */         loadRepeat(), 
/*  49 */         loadPitchModifier(), 
/*  50 */         loadYawModifier(), 
/*  51 */         loadChance(), 
/*  52 */         loadYawMode());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected IWeightedMap<CaveNodeType> loadNodes() {
/*  58 */     WeightedMap.Builder<CaveNodeType> builder = WeightedMap.builder((Object[])CaveNodeType.EMPTY_ARRAY);
/*  59 */     JsonElement nodeElement = get("Node");
/*  60 */     if (nodeElement.isJsonArray()) {
/*  61 */       JsonArray weightsArray, nodeArray = nodeElement.getAsJsonArray();
/*     */       
/*  63 */       if (has("Weights")) {
/*  64 */         JsonElement weightsElement = get("Weights");
/*  65 */         if (!weightsElement.isJsonArray()) throw new IllegalArgumentException("'Weights' must be an array if set"); 
/*  66 */         weightsArray = weightsElement.getAsJsonArray();
/*  67 */         if (weightsArray.size() != nodeArray.size()) throw new IllegalArgumentException("Weight array size is different from node name array."); 
/*     */       } else {
/*  69 */         weightsArray = null;
/*     */       } 
/*  71 */       for (int i = 0; i < nodeArray.size(); i++) {
/*  72 */         JsonElement nodeEntryElement = getOrLoad(nodeArray.get(i));
/*  73 */         CaveNodeType caveNodeType = loadCaveNodeType(i, nodeEntryElement);
/*  74 */         double weight = (weightsArray != null) ? weightsArray.get(i).getAsDouble() : 1.0D;
/*  75 */         builder.put(caveNodeType, weight);
/*     */       } 
/*  77 */     } else if (nodeElement.isJsonObject() || nodeElement.isJsonPrimitive()) {
/*  78 */       CaveNodeType caveNodeType = loadCaveNodeType(0, nodeElement);
/*  79 */       builder.put(caveNodeType, 1.0D);
/*     */     } 
/*  81 */     if (builder.size() <= 0) throw new IllegalArgumentException("There are no valid nodes in this child entry!"); 
/*  82 */     return builder.build();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveNodeType loadCaveNodeType(int index, @Nonnull JsonElement element) {
/*  87 */     if (element.isJsonObject()) {
/*  88 */       String caveNodeTypeName = ((SeedStringResource)this.seed.get()).getUniqueName("ChildCaveType#");
/*  89 */       return this.storage.loadCaveNodeType(caveNodeTypeName, element.getAsJsonObject());
/*  90 */     }  if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
/*  91 */       String caveNodeTypeName = element.getAsString();
/*  92 */       return this.storage.getOrLoadCaveNodeType(caveNodeTypeName);
/*     */     } 
/*  94 */     throw error("Invalid cave node type entry: %d", new Object[] { Integer.valueOf(index) });
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Vector3d loadAnchor() {
/*  99 */     Vector3d anchor = Vector3d.ZERO;
/* 100 */     if (has("Anchor")) {
/* 101 */       anchor = loadVector(anchor.clone(), get("Anchor"));
/*     */     }
/* 103 */     return anchor;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Vector3d loadOffset() {
/* 108 */     Vector3d offset = Vector3d.ZERO;
/* 109 */     if (has("Offset")) {
/* 110 */       offset = loadVector(offset.clone(), get("Offset"));
/*     */     }
/* 112 */     return offset;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected PrefabRotation[] loadRotations() {
/* 117 */     PrefabRotation[] rotations = { PrefabRotation.ROTATION_0 };
/* 118 */     if (has("Rotation")) {
/* 119 */       JsonElement rotationElement = get("Rotation");
/* 120 */       if (rotationElement.isJsonPrimitive()) {
/* 121 */         rotations = new PrefabRotation[] { PrefabRotation.valueOfExtended(rotationElement.getAsString()) };
/* 122 */       } else if (rotationElement.isJsonArray()) {
/* 123 */         JsonArray rotationArray = rotationElement.getAsJsonArray();
/* 124 */         rotations = new PrefabRotation[rotationArray.size()];
/* 125 */         for (int i = 0; i < rotations.length; i++) {
/* 126 */           rotations[i] = PrefabRotation.valueOfExtended(rotationArray.get(i).getAsString());
/*     */         }
/*     */       } 
/*     */     } 
/* 130 */     return rotations;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IDoubleRange loadChildrenLimit() {
/* 135 */     if (has("ChildrenLimit")) {
/* 136 */       return (new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("ChildrenLimit"), 0.0D))
/* 137 */         .load();
/*     */     }
/* 139 */     return null;
/*     */   }
/*     */   @Nonnull
/*     */   protected IDoubleRange loadRepeat() {
/*     */     IDoubleRange iDoubleRange;
/* 144 */     DoubleRange.Constant constant = DoubleRange.ONE;
/* 145 */     if (has("Repeat"))
/*     */     {
/* 147 */       iDoubleRange = (new DoubleRangeJsonLoader(this.seed, this.dataFolder, get("Repeat"), 0.0D)).load();
/*     */     }
/* 149 */     return iDoubleRange;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveNodeType.CaveNodeChildEntry.OrientationModifier loadYawModifier() {
/* 154 */     IFloatRange yawAdd = loadYawAdd();
/* 155 */     if (yawAdd != null) {
/* 156 */       return (current, random) -> current + yawAdd.getValue(random);
/*     */     }
/* 158 */     IFloatRange yawSet = loadYawSet();
/* 159 */     if (yawSet != null) {
/* 160 */       return (current, random) -> yawSet.getValue(random);
/*     */     }
/* 162 */     return (current, random) -> current;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CaveNodeType.CaveNodeChildEntry.OrientationModifier loadPitchModifier() {
/* 169 */     IFloatRange pitchAdd = loadPitchAdd();
/* 170 */     if (pitchAdd != null) {
/* 171 */       return (current, random) -> current + pitchAdd.getValue(random);
/*     */     }
/* 173 */     IFloatRange pitchSet = loadPitchSet();
/* 174 */     if (pitchSet != null) {
/* 175 */       return (current, random) -> pitchSet.getValue(random);
/*     */     }
/* 177 */     return (current, random) -> current;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected IFloatRange loadYawAdd() {
/* 184 */     IFloatRange yawAdd = null;
/* 185 */     if (has("YawAdd"))
/*     */     {
/* 187 */       yawAdd = (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("YawAdd"), 0.0F, deg -> deg * 0.017453292F)).load();
/*     */     }
/* 189 */     return yawAdd;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IFloatRange loadPitchAdd() {
/* 194 */     IFloatRange pitchAdd = null;
/* 195 */     if (has("PitchAdd"))
/*     */     {
/* 197 */       pitchAdd = (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("PitchAdd"), 0.0F, deg -> deg * 0.017453292F)).load();
/*     */     }
/* 199 */     return pitchAdd;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IFloatRange loadYawSet() {
/* 204 */     IFloatRange yawSet = null;
/* 205 */     if (has("YawSet")) {
/* 206 */       yawSet = (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("YawSet"), 0.0F, deg -> deg * 0.017453292F)).load();
/*     */     }
/* 208 */     return yawSet;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   protected IFloatRange loadPitchSet() {
/* 213 */     IFloatRange pitchSet = null;
/* 214 */     if (has("PitchSet"))
/*     */     {
/* 216 */       pitchSet = (new FloatRangeJsonLoader(this.seed, this.dataFolder, get("PitchSet"), 0.0F, deg -> deg * 0.017453292F)).load();
/*     */     }
/* 218 */     return pitchSet;
/*     */   }
/*     */   
/*     */   protected double loadChance() {
/* 222 */     double chance = 1.0D;
/* 223 */     if (has("Chance")) {
/* 224 */       chance = get("Chance").getAsDouble();
/*     */     }
/* 226 */     return chance;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected CaveYawMode loadYawMode() {
/* 231 */     CaveYawMode combiner = CaveYawMode.NODE;
/* 232 */     if (has("YawMode")) {
/* 233 */       combiner = CaveYawMode.valueOf(get("YawMode").getAsString());
/*     */     }
/* 235 */     return combiner;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected Vector3d loadVector(@Nonnull Vector3d vector, @Nonnull JsonElement jsonElement) {
/* 240 */     JsonArray array = jsonElement.getAsJsonArray();
/* 241 */     vector.x = array.get(0).getAsDouble();
/* 242 */     vector.y = array.get(1).getAsDouble();
/* 243 */     vector.z = array.get(2).getAsDouble();
/* 244 */     return vector;
/*     */   }
/*     */   
/*     */   public static interface Constants {
/*     */     public static final String KEY_NODE = "Node";
/*     */     public static final String KEY_SEED = "Seed";
/*     */     public static final String KEY_WEIGHTS = "Weights";
/*     */     public static final String KEY_ANCHOR = "Anchor";
/*     */     public static final String KEY_OFFSET = "Offset";
/*     */     public static final String KEY_ROTATION = "Rotation";
/*     */     public static final String KEY_CHILDREN_LIMIT = "ChildrenLimit";
/*     */     public static final String KEY_REPEAT = "Repeat";
/*     */     public static final String KEY_PITCH_ADD = "PitchAdd";
/*     */     public static final String KEY_PITCH_SET = "PitchSet";
/*     */     public static final String KEY_YAW_ADD = "YawAdd";
/*     */     public static final String KEY_YAW_SET = "YawSet";
/*     */     public static final String KEY_CHANCE = "Chance";
/*     */     public static final String KEY_YAW_MODE = "YawMode";
/*     */     public static final String ERROR_WEIGHTS_ARRAY = "'Weights' must be an array if set";
/*     */     public static final String ERROR_ENTRY_WEIGHT_SIZE = "Weight array size is different from node name array.";
/*     */     public static final String ERROR_NO_NODES = "There are no valid nodes in this child entry!";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\cave\CaveNodeChildEntryJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */