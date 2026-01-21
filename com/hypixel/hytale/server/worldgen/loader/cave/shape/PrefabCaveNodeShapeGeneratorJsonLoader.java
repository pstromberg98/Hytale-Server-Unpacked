/*    */ package com.hypixel.hytale.server.worldgen.loader.cave.shape;
/*    */ 
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*    */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*    */ import com.hypixel.hytale.server.worldgen.cave.shape.PrefabCaveNodeShape;
/*    */ import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabLoader;
/*    */ import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
/*    */ import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskJsonLoader;
/*    */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*    */ import java.nio.file.Path;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrefabCaveNodeShapeGeneratorJsonLoader
/*    */   extends CaveNodeShapeGeneratorJsonLoader
/*    */ {
/*    */   public PrefabCaveNodeShapeGeneratorJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
/* 30 */     super(seed.append(".PrefabCaveNodeShapeGenerator"), dataFolder, json);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public PrefabCaveNodeShape.PrefabCaveNodeShapeGenerator load() {
/* 36 */     return new PrefabCaveNodeShape.PrefabCaveNodeShapeGenerator(
/* 37 */         loadPrefabs(), 
/* 38 */         loadMask());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   protected List<WorldGenPrefabSupplier> loadPrefabs() {
/* 44 */     WorldGenPrefabLoader loader = ((SeedStringResource)this.seed.get()).getLoader();
/* 45 */     List<WorldGenPrefabSupplier> prefabs = new ArrayList<>();
/* 46 */     JsonElement prefabElement = get("Prefab");
/* 47 */     if (prefabElement.isJsonArray()) {
/* 48 */       JsonArray prefabArray = prefabElement.getAsJsonArray();
/* 49 */       for (JsonElement prefabArrayElement : prefabArray) {
/* 50 */         String prefabString = prefabArrayElement.getAsString();
/* 51 */         Collections.addAll(prefabs, loader.get(prefabString));
/*    */       } 
/*    */     } else {
/* 54 */       String prefabString = prefabElement.getAsString();
/* 55 */       Collections.addAll(prefabs, loader.get(prefabString));
/*    */     } 
/* 57 */     if (prefabs.isEmpty()) throw new IllegalArgumentException("Prefabs are empty! Key: Prefab"); 
/* 58 */     return prefabs;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   protected BlockMaskCondition loadMask() {
/* 63 */     BlockMaskCondition configuration = BlockMaskCondition.DEFAULT_TRUE;
/* 64 */     if (has("Mask"))
/*    */     {
/* 66 */       configuration = (new BlockPlacementMaskJsonLoader(this.seed, this.dataFolder, getRaw("Mask"))).load();
/*    */     }
/* 68 */     return configuration;
/*    */   }
/*    */   
/*    */   public static interface Constants {
/*    */     public static final String KEY_PREFAB = "Prefab";
/*    */     public static final String KEY_MASK = "Mask";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\cave\shape\PrefabCaveNodeShapeGeneratorJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */