/*     */ package com.hypixel.hytale.server.worldgen.loader.prefab;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.procedurallib.json.JsonLoader;
/*     */ import com.hypixel.hytale.procedurallib.json.SeedString;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.worldgen.SeedStringResource;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.ResolvedBlockArrayJsonLoader;
/*     */ import com.hypixel.hytale.server.worldgen.loader.util.ResolvedVariantsBlockArrayLoader;
/*     */ import com.hypixel.hytale.server.worldgen.util.BlockFluidEntry;
/*     */ import com.hypixel.hytale.server.worldgen.util.ResolvedBlockArray;
/*     */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
/*     */ import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.nio.file.Path;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockPlacementMaskJsonLoader
/*     */   extends JsonLoader<SeedStringResource, BlockMaskCondition>
/*     */ {
/*     */   private String fileName;
/*     */   
/*     */   public BlockPlacementMaskJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json) {
/*  34 */     super(seed.append(".BlockMaskCondition"), dataFolder, json);
/*     */   }
/*     */   public BlockMaskCondition load() {
/*     */     BlockMaskCondition.Mask defaultMask;
/*     */     Long2ObjectOpenHashMap long2ObjectOpenHashMap;
/*  39 */     BlockPlacementMaskRegistry registry = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry();
/*  40 */     if (this.fileName != null) {
/*  41 */       BlockMaskCondition blockMaskCondition = (BlockMaskCondition)registry.getIfPresentFileMask(this.fileName);
/*  42 */       if (blockMaskCondition != null) return blockMaskCondition;
/*     */     
/*     */     } 
/*     */     
/*  46 */     Long2ObjectMap<BlockMaskCondition.Mask> specificMasks = Long2ObjectMaps.emptyMap();
/*     */     
/*  48 */     if (this.json == null || this.json.isJsonNull()) {
/*  49 */       defaultMask = BlockMaskCondition.DEFAULT_MASK;
/*     */     } else {
/*  51 */       if (has("Default")) {
/*     */         
/*  53 */         defaultMask = new BlockMaskCondition.Mask(loadEntries(get("Default").getAsJsonArray()));
/*     */       } else {
/*     */         
/*  56 */         defaultMask = BlockMaskCondition.DEFAULT_MASK;
/*     */       } 
/*     */       
/*  59 */       if (has("Specific")) {
/*  60 */         BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/*  61 */         long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
/*  62 */         JsonArray array = get("Specific").getAsJsonArray();
/*  63 */         for (int i = 0; i < array.size(); i++) {
/*     */           try {
/*  65 */             JsonObject specificObject = array.get(i).getAsJsonObject();
/*  66 */             JsonElement blocksElement = specificObject.get("Block");
/*  67 */             ResolvedBlockArray blocks = (new ResolvedBlockArrayJsonLoader(this.seed, this.dataFolder, blocksElement)).load();
/*  68 */             for (BlockFluidEntry blockEntry : blocks.getEntries()) {
/*  69 */               String key = ((BlockType)assetMap.getAsset(blockEntry.blockId())).getId();
/*  70 */               for (ObjectIterator<String> objectIterator = assetMap.getSubKeys(key).iterator(); objectIterator.hasNext(); ) { String variant = objectIterator.next();
/*  71 */                 int index = assetMap.getIndex(variant);
/*  72 */                 if (index == Integer.MIN_VALUE) throw new IllegalArgumentException("Unknown key! " + variant); 
/*  73 */                 JsonArray rule = specificObject.getAsJsonArray("Rule");
/*  74 */                 long2ObjectOpenHashMap.put(MathUtil.packLong(index, blockEntry.fluidId()), new BlockMaskCondition.Mask(
/*  75 */                       loadEntries(rule))); }
/*     */ 
/*     */             
/*     */             } 
/*  79 */           } catch (Throwable e) {
/*  80 */             throw new Error(String.format("Error while reading specific block mask #%s!", new Object[] { Integer.valueOf(i) }), e);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  86 */     BlockMaskCondition mask = registry.retainOrAllocateMask(defaultMask, (Long2ObjectMap<BlockMaskCondition.Mask>)long2ObjectOpenHashMap);
/*  87 */     if (this.fileName != null) {
/*  88 */       registry.putFileMask(this.fileName, mask);
/*     */     }
/*  90 */     return mask;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   protected BlockMaskCondition.MaskEntry[] loadEntries(@Nonnull JsonArray jsonArray) {
/*  95 */     BlockMaskCondition.MaskEntry[] entries = new BlockMaskCondition.MaskEntry[jsonArray.size()];
/*     */     
/*  97 */     int head = 0, tail = entries.length;
/*  98 */     for (JsonElement element : jsonArray) {
/*  99 */       if (element.isJsonObject()) {
/* 100 */         JsonObject obj = element.getAsJsonObject();
/* 101 */         boolean bool = true;
/* 102 */         if (obj.has("Replace")) {
/* 103 */           bool = obj.get("Replace").getAsBoolean();
/*     */         }
/* 105 */         ResolvedBlockArray resolvedBlockArray = ResolvedVariantsBlockArrayLoader.loadSingleBlock(obj);
/* 106 */         entries[head++] = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().retainOrAllocateEntry(resolvedBlockArray, bool);
/*     */         continue;
/*     */       } 
/* 109 */       String string = element.getAsString();
/* 110 */       boolean replace = true;
/* 111 */       int beginIndex = 0;
/* 112 */       if (string.charAt(0) == '!') {
/* 113 */         replace = false;
/* 114 */         beginIndex = 1;
/*     */       } 
/* 116 */       if (string.length() == beginIndex + 1 && string.charAt(beginIndex) == '*') {
/*     */         
/* 118 */         if (tail < entries.length) System.arraycopy(entries, tail, entries, tail - 1, entries.length - tail);
/*     */         
/* 120 */         entries[entries.length - 1] = replace ? BlockMaskCondition.MaskEntry.WILDCARD_TRUE : BlockMaskCondition.MaskEntry.WILDCARD_FALSE;
/* 121 */         tail--; continue;
/*     */       } 
/* 123 */       string = string.substring(beginIndex);
/* 124 */       ResolvedBlockArray blocks = ResolvedVariantsBlockArrayLoader.loadSingleBlock(string);
/* 125 */       entries[head++] = ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().retainOrAllocateEntry(blocks, replace);
/*     */     } 
/*     */     
/* 128 */     return entries;
/*     */   }
/*     */ 
/*     */   
/*     */   protected JsonElement loadFileConstructor(String filePath) {
/* 133 */     this.fileName = filePath;
/* 134 */     return ((SeedStringResource)this.seed.get()).getBlockMaskRegistry().cachedFile(filePath, file -> super.loadFileConstructor(file));
/*     */   }
/*     */   
/*     */   public static interface Constants {
/*     */     public static final String KEY_DEFAULT = "Default";
/*     */     public static final String KEY_SPECIFIC = "Specific";
/*     */     public static final String KEY_BLOCK = "Block";
/*     */     public static final String KEY_RULE = "Rule";
/*     */     public static final String ERROR_FAIL_SPECIFIC = "Error while reading specific block mask #%s!";
/*     */     public static final String ERROR_BLOCK_INVALID = "Failed to resolve block \"%s\"";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\loader\prefab\BlockPlacementMaskJsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */