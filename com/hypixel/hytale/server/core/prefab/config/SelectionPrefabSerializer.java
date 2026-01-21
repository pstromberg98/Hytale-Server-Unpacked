/*     */ package com.hypixel.hytale.server.core.prefab.config;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.codec.DirectDecodeCodec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.lookup.ACodecMapCodec;
/*     */ import com.hypixel.hytale.component.Archetype;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.data.unknown.TempUnknownComponent;
/*     */ import com.hypixel.hytale.component.data.unknown.UnknownComponents;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMigration;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.BsonPrefabBufferDeserializer;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Function;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.bson.BsonArray;
/*     */ import org.bson.BsonDocument;
/*     */ import org.bson.BsonInt32;
/*     */ import org.bson.BsonString;
/*     */ import org.bson.BsonValue;
/*     */ 
/*     */ 
/*     */ public class SelectionPrefabSerializer
/*     */ {
/*     */   public static final int VERSION = 8;
/*     */   private static final Comparator<BsonDocument> COMPARE_BLOCK_POSITION;
/*     */   
/*     */   static {
/*  52 */     COMPARE_BLOCK_POSITION = Comparator.<BsonDocument>comparingInt(doc -> doc.getInt32("x").getValue()).thenComparingInt(doc -> doc.getInt32("z").getValue()).thenComparingInt(doc -> doc.getInt32("y").getValue());
/*     */   }
/*  54 */   private static final BsonInt32 DEFAULT_SUPPORT_VALUE = new BsonInt32(0);
/*  55 */   private static final BsonInt32 DEFAULT_FILLER_VALUE = new BsonInt32(0);
/*  56 */   private static final BsonInt32 DEFAULT_ROTATION_VALUE = new BsonInt32(0);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static BlockSelection deserialize(@Nonnull BsonDocument doc) {
/*  63 */     BsonValue versionValue = doc.get("version");
/*  64 */     int version = (versionValue != null) ? versionValue.asInt32().getValue() : -1;
/*  65 */     if (version <= 0) throw new IllegalArgumentException("Prefab version is too old: " + version); 
/*  66 */     if (version > 8) throw new IllegalArgumentException("Prefab version is too new: " + version + " by expected 8");
/*     */     
/*  68 */     int worldVersion = (version < 4) ? readWorldVersion(doc) : 0;
/*  69 */     BsonValue entityVersionValue = doc.get("entityVersion");
/*  70 */     int entityVersion = (entityVersionValue != null) ? entityVersionValue.asInt32().getValue() : 0;
/*     */     
/*  72 */     int anchorX = doc.getInt32("anchorX").getValue();
/*  73 */     int anchorY = doc.getInt32("anchorY").getValue();
/*  74 */     int anchorZ = doc.getInt32("anchorZ").getValue();
/*     */     
/*  76 */     BlockSelection selection = new BlockSelection();
/*  77 */     selection.setAnchor(anchorX, anchorY, anchorZ);
/*     */     
/*  79 */     int blockIdVersion = doc.getInt32("blockIdVersion", BsonPrefabBufferDeserializer.LEGACY_BLOCK_ID_VERSION).getValue();
/*     */     
/*  81 */     Function<String, String> blockMigration = null;
/*  82 */     Map<Integer, BlockMigration> blockMigrationMap = BlockMigration.getAssetMap().getAssetMap();
/*  83 */     int v = blockIdVersion;
/*  84 */     BlockMigration migration = blockMigrationMap.get(Integer.valueOf(v));
/*  85 */     while (migration != null) {
/*  86 */       if (blockMigration == null) { Objects.requireNonNull(migration); blockMigration = migration::getMigration; }
/*  87 */       else { Objects.requireNonNull(migration); blockMigration = blockMigration.andThen(migration::getMigration); }
/*  88 */        migration = blockMigrationMap.get(Integer.valueOf(++v));
/*     */     } 
/*     */     
/*  91 */     BsonValue blocksValue = doc.get("blocks");
/*  92 */     if (blocksValue != null) {
/*  93 */       BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/*     */       
/*  95 */       BsonArray bsonArray = blocksValue.asArray();
/*  96 */       for (int i = 0; i < bsonArray.size(); i++) {
/*  97 */         BsonDocument innerObj = bsonArray.get(i).asDocument();
/*  98 */         int x = innerObj.getInt32("x").getValue();
/*  99 */         int y = innerObj.getInt32("y").getValue();
/* 100 */         int z = innerObj.getInt32("z").getValue();
/* 101 */         String blockTypeStr = innerObj.getString("name").getValue();
/* 102 */         boolean legacyStripName = false;
/*     */         
/* 104 */         if (version <= 4) {
/* 105 */           Fluid.ConversionResult result = Fluid.convertBlockToFluid(blockTypeStr);
/*     */           
/* 107 */           if (result != null) {
/* 108 */             legacyStripName = true;
/* 109 */             selection.addFluidAtLocalPos(x, y, z, result.fluidId, result.fluidLevel);
/* 110 */             if (result.blockTypeStr == null)
/*     */               continue; 
/*     */           } 
/*     */         } 
/* 114 */         int support = 0;
/* 115 */         if (version >= 6) {
/* 116 */           support = innerObj.getInt32("support", DEFAULT_SUPPORT_VALUE).getValue();
/*     */         }
/* 118 */         else if (blockTypeStr.contains("|Deco")) {
/* 119 */           legacyStripName = true;
/* 120 */           support = 15;
/* 121 */         } else if (blockTypeStr.contains("|Support=")) {
/* 122 */           legacyStripName = true;
/* 123 */           int start = blockTypeStr.indexOf("|Support=") + "|Support=".length();
/* 124 */           int end = blockTypeStr.indexOf('|', start);
/* 125 */           if (end == -1) end = blockTypeStr.length(); 
/* 126 */           support = Integer.parseInt(blockTypeStr, start, end, 10);
/*     */         } else {
/* 128 */           support = 0;
/*     */         } 
/*     */ 
/*     */         
/* 132 */         int filler = 0;
/* 133 */         if (version >= 7) {
/* 134 */           filler = innerObj.getInt32("filler", DEFAULT_FILLER_VALUE).getValue();
/*     */         }
/* 136 */         else if (blockTypeStr.contains("|Filler=")) {
/* 137 */           legacyStripName = true;
/* 138 */           int start = blockTypeStr.indexOf("|Filler=") + "|Filler=".length();
/* 139 */           int firstComma = blockTypeStr.indexOf(',', start);
/* 140 */           if (firstComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing comma"); 
/* 141 */           int secondComma = blockTypeStr.indexOf(',', firstComma + 1);
/* 142 */           if (secondComma == -1) throw new IllegalArgumentException("Invalid filler metadata! Missing second comma");
/*     */           
/* 144 */           int end = blockTypeStr.indexOf('|', start);
/* 145 */           if (end == -1) end = blockTypeStr.length();
/*     */           
/* 147 */           int fillerX = Integer.parseInt(blockTypeStr, start, firstComma, 10);
/* 148 */           int fillerY = Integer.parseInt(blockTypeStr, firstComma + 1, secondComma, 10);
/* 149 */           int fillerZ = Integer.parseInt(blockTypeStr, secondComma + 1, end, 10);
/*     */           
/* 151 */           filler = FillerBlockUtil.pack(fillerX, fillerY, fillerZ);
/*     */         } else {
/* 153 */           filler = 0;
/*     */         } 
/*     */ 
/*     */         
/* 157 */         int rotation = 0;
/* 158 */         if (version >= 8) {
/* 159 */           rotation = innerObj.getInt32("rotation", DEFAULT_ROTATION_VALUE).getValue();
/*     */         } else {
/* 161 */           Rotation yaw = Rotation.None;
/* 162 */           Rotation pitch = Rotation.None;
/* 163 */           Rotation roll = Rotation.None;
/*     */           
/* 165 */           if (blockTypeStr.contains("|Yaw=")) {
/* 166 */             legacyStripName = true;
/* 167 */             int start = blockTypeStr.indexOf("|Yaw=") + "|Yaw=".length();
/* 168 */             int end = blockTypeStr.indexOf('|', start);
/* 169 */             if (end == -1) end = blockTypeStr.length(); 
/* 170 */             yaw = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
/*     */           } 
/*     */           
/* 173 */           if (blockTypeStr.contains("|Pitch=")) {
/* 174 */             legacyStripName = true;
/* 175 */             int start = blockTypeStr.indexOf("|Pitch=") + "|Pitch=".length();
/* 176 */             int end = blockTypeStr.indexOf('|', start);
/* 177 */             if (end == -1) end = blockTypeStr.length(); 
/* 178 */             pitch = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
/*     */           } 
/*     */           
/* 181 */           if (blockTypeStr.contains("|Roll=")) {
/* 182 */             legacyStripName = true;
/* 183 */             int start = blockTypeStr.indexOf("|Roll=") + "|Roll=".length();
/* 184 */             int end = blockTypeStr.indexOf('|', start);
/* 185 */             if (end == -1) end = blockTypeStr.length(); 
/* 186 */             pitch = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
/*     */           } 
/*     */           
/* 189 */           rotation = RotationTuple.index(yaw, pitch, roll);
/*     */         } 
/*     */         
/* 192 */         if (legacyStripName) {
/* 193 */           int endOfName = blockTypeStr.indexOf('|');
/* 194 */           if (endOfName != -1) {
/* 195 */             blockTypeStr = blockTypeStr.substring(0, endOfName);
/*     */           }
/*     */         } 
/*     */         
/* 199 */         String blockTypeKey = blockTypeStr;
/* 200 */         if (blockMigration != null) blockTypeKey = blockMigration.apply(blockTypeKey);
/*     */         
/* 202 */         int blockId = BlockType.getBlockIdOrUnknown(assetMap, blockTypeKey, "Failed to find block '%s' in unknown legacy prefab!", new Object[] { blockTypeStr });
/*     */         
/* 204 */         Holder<ChunkStore> wrapper = null;
/* 205 */         if (version <= 2) {
/* 206 */           BsonValue stateValue = innerObj.get("state");
/* 207 */           if (stateValue != null) {
/* 208 */             wrapper = legacyStateDecode(stateValue.asDocument());
/*     */           }
/*     */         } else {
/* 211 */           BsonValue stateValue = innerObj.get("components");
/* 212 */           if (stateValue != null) {
/* 213 */             if (version < 4) {
/* 214 */               wrapper = ChunkStore.REGISTRY.deserialize(stateValue.asDocument(), worldVersion);
/*     */             } else {
/* 216 */               wrapper = ChunkStore.REGISTRY.deserialize(stateValue.asDocument());
/*     */             } 
/*     */           }
/*     */         } 
/*     */         
/* 221 */         selection.addBlockAtLocalPos(x, y, z, blockId, rotation, filler, support, wrapper);
/*     */         continue;
/*     */       } 
/*     */     } 
/* 225 */     BsonValue fluidsValue = doc.get("fluids");
/* 226 */     if (fluidsValue != null) {
/* 227 */       IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
/*     */       
/* 229 */       BsonArray bsonArray = fluidsValue.asArray();
/* 230 */       for (int i = 0; i < bsonArray.size(); i++) {
/* 231 */         BsonDocument innerObj = bsonArray.get(i).asDocument();
/* 232 */         int x = innerObj.getInt32("x").getValue();
/* 233 */         int y = innerObj.getInt32("y").getValue();
/* 234 */         int z = innerObj.getInt32("z").getValue();
/* 235 */         String fluidName = innerObj.getString("name").getValue();
/*     */         
/* 237 */         int fluidId = Fluid.getFluidIdOrUnknown(assetMap, fluidName, "Failed to find fluid '%s' in unknown legacy prefab!", new Object[] { fluidName });
/* 238 */         byte fluidLevel = (byte)innerObj.getInt32("level").getValue();
/* 239 */         selection.addFluidAtLocalPos(x, y, z, fluidId, fluidLevel);
/*     */       } 
/*     */     } 
/*     */     
/* 243 */     BsonValue entitiesValues = doc.get("entities");
/* 244 */     if (entitiesValues != null) {
/* 245 */       BsonArray entities = entitiesValues.asArray();
/* 246 */       for (int i = 0; i < entities.size(); i++) {
/* 247 */         BsonDocument bsonDocument = entities.get(i).asDocument();
/* 248 */         if (version <= 1) {
/*     */           try {
/* 250 */             selection.addEntityHolderRaw(legacyEntityDecode(bsonDocument, entityVersion));
/* 251 */           } catch (Throwable t) {
/* 252 */             ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.WARNING).withCause(t)).log("Exception when loading entity state %s", bsonDocument);
/*     */           } 
/*     */         } else {
/* 255 */           selection.addEntityHolderRaw(EntityStore.REGISTRY.deserialize(bsonDocument));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 260 */     return selection;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static BsonDocument serialize(@Nonnull BlockSelection prefab) {
/* 265 */     Objects.requireNonNull(prefab, "null prefab");
/*     */     
/* 267 */     BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
/* 268 */     IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
/*     */     
/* 270 */     BsonDocument out = new BsonDocument();
/*     */     
/* 272 */     out.put("version", (BsonValue)new BsonInt32(8));
/* 273 */     out.put("blockIdVersion", (BsonValue)new BsonInt32(BlockMigration.getAssetMap().getAssetCount()));
/*     */     
/* 275 */     out.put("anchorX", (BsonValue)new BsonInt32(prefab.getAnchorX()));
/* 276 */     out.put("anchorY", (BsonValue)new BsonInt32(prefab.getAnchorY()));
/* 277 */     out.put("anchorZ", (BsonValue)new BsonInt32(prefab.getAnchorZ()));
/*     */     
/* 279 */     BsonArray contentOut = new BsonArray();
/* 280 */     prefab.forEachBlock((x, y, z, block) -> {
/*     */           BsonDocument innerObj = new BsonDocument(); innerObj.put("x", (BsonValue)new BsonInt32(x));
/*     */           innerObj.put("y", (BsonValue)new BsonInt32(y));
/*     */           innerObj.put("z", (BsonValue)new BsonInt32(z));
/*     */           innerObj.put("name", (BsonValue)new BsonString(((BlockType)assetMap.getAsset(block.blockId())).getId().toString()));
/*     */           if (block.holder() != null)
/*     */             innerObj.put("components", (BsonValue)ChunkStore.REGISTRY.serialize(block.holder())); 
/*     */           if (block.supportValue() != 0)
/*     */             innerObj.put("support", (BsonValue)new BsonInt32(block.supportValue())); 
/*     */           if (block.filler() != 0)
/*     */             innerObj.put("filler", (BsonValue)new BsonInt32(block.filler())); 
/*     */           if (block.rotation() != 0)
/*     */             innerObj.put("rotation", (BsonValue)new BsonInt32(block.rotation())); 
/*     */           contentOut.add((BsonValue)innerObj);
/*     */         });
/* 295 */     contentOut.sort((a, b) -> {
/*     */           BsonDocument aDoc = a.asDocument();
/*     */           BsonDocument bDoc = b.asDocument();
/*     */           return COMPARE_BLOCK_POSITION.compare(aDoc, bDoc);
/*     */         });
/* 300 */     out.put("blocks", (BsonValue)contentOut);
/*     */ 
/*     */     
/* 303 */     BsonArray fluidContentOut = new BsonArray();
/* 304 */     prefab.forEachFluid((x, y, z, fluid, level) -> {
/*     */           BsonDocument innerObj = new BsonDocument();
/*     */           
/*     */           innerObj.put("x", (BsonValue)new BsonInt32(x));
/*     */           innerObj.put("y", (BsonValue)new BsonInt32(y));
/*     */           innerObj.put("z", (BsonValue)new BsonInt32(z));
/*     */           innerObj.put("name", (BsonValue)new BsonString(((Fluid)fluidMap.getAsset(fluid)).getId()));
/*     */           innerObj.put("level", (BsonValue)new BsonInt32(level));
/*     */           fluidContentOut.add((BsonValue)innerObj);
/*     */         });
/* 314 */     fluidContentOut.sort((a, b) -> {
/*     */           BsonDocument aDoc = a.asDocument();
/*     */           BsonDocument bDoc = b.asDocument();
/*     */           return COMPARE_BLOCK_POSITION.compare(aDoc, bDoc);
/*     */         });
/* 319 */     if (!fluidContentOut.isEmpty()) {
/* 320 */       out.put("fluids", (BsonValue)fluidContentOut);
/*     */     }
/*     */     
/* 323 */     List<BsonDocument> entityList = new ArrayList<>();
/* 324 */     prefab.forEachEntity(holder -> entityList.add(EntityStore.REGISTRY.serialize(holder)));
/*     */ 
/*     */     
/* 327 */     if (!entityList.isEmpty()) {
/* 328 */       BsonArray entities = new BsonArray();
/* 329 */       Objects.requireNonNull(entities); entityList.forEach(entities::add);
/* 330 */       out.put("entities", (BsonValue)entities);
/*     */     } 
/*     */     
/* 333 */     return out;
/*     */   }
/*     */   
/*     */   public static int readWorldVersion(@Nonnull BsonDocument document) {
/*     */     int worldVersion;
/* 338 */     if (document.containsKey("worldVersion")) {
/* 339 */       worldVersion = document.getInt32("worldVersion").getValue();
/* 340 */     } else if (document.containsKey("worldver")) {
/* 341 */       worldVersion = document.getInt32("worldver").getValue();
/*     */     } else {
/* 343 */       worldVersion = 5;
/*     */     } 
/*     */     
/* 346 */     if (worldVersion == 18553)
/* 347 */       throw new IllegalArgumentException("WorldChunk version old format! Update!"); 
/* 348 */     if (worldVersion > 23) {
/* 349 */       throw new IllegalArgumentException("WorldChunk version is newer than we understand! Version: " + worldVersion + ", Latest Version: 23");
/*     */     }
/*     */ 
/*     */     
/* 353 */     return worldVersion;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static Holder<EntityStore> legacyEntityDecode(@Nonnull BsonDocument document, int version) {
/* 358 */     String entityTypeStr = document.getString("EntityType").getValue();
/* 359 */     Class<? extends Entity> entityType = EntityModule.get().getClass(entityTypeStr);
/* 360 */     if (entityType == null) {
/* 361 */       UnknownComponents unknownComponents = new UnknownComponents();
/* 362 */       unknownComponents.addComponent(entityTypeStr, new TempUnknownComponent(document));
/* 363 */       return EntityStore.REGISTRY.newHolder(Archetype.of(EntityStore.REGISTRY.getUnknownComponentType()), new Component[] { (Component)unknownComponents });
/*     */     } 
/*     */     
/* 366 */     Function<World, ? extends Entity> constructor = EntityModule.get().getConstructor(entityType);
/* 367 */     if (constructor == null) return null;
/*     */     
/* 369 */     DirectDecodeCodec<? extends Entity> codec = EntityModule.get().getCodec(entityType);
/* 370 */     Objects.requireNonNull(codec, "Unable to create entity because there is no associated codec");
/*     */     
/* 372 */     Entity entity = constructor.apply(null);
/*     */     
/* 374 */     codec.decode((BsonValue)document, entity, new ExtraInfo(version));
/* 375 */     return entity.toHolder();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Holder<ChunkStore> legacyStateDecode(@Nonnull BsonDocument document) {
/* 380 */     ExtraInfo extraInto = ExtraInfo.THREAD_LOCAL.get();
/* 381 */     String type = (String)BlockState.TYPE_STRUCTURE.getNow(document, extraInto);
/* 382 */     Class<? extends BlockState> blockStateClass = BlockState.CODEC.getClassFor(type);
/* 383 */     if (blockStateClass != null) {
/*     */       try {
/* 385 */         BlockState t = (BlockState)BlockState.CODEC.decode((BsonValue)document, extraInto);
/*     */         
/* 387 */         Holder<ChunkStore> holder1 = ChunkStore.REGISTRY.newHolder();
/* 388 */         ComponentType<ChunkStore, ? extends BlockState> componentType = BlockStateModule.get().getComponentType(blockStateClass);
/* 389 */         if (componentType == null) throw new IllegalArgumentException("Unable to find component type for: " + String.valueOf(blockStateClass));
/*     */         
/* 391 */         holder1.addComponent(componentType, (Component)t);
/* 392 */         return holder1;
/* 393 */       } catch (com.hypixel.hytale.codec.lookup.ACodecMapCodec.UnknownIdException unknownIdException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 398 */     Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
/* 399 */     UnknownComponents<ChunkStore> unknownComponents = new UnknownComponents();
/* 400 */     unknownComponents.addComponent(type, new TempUnknownComponent(document));
/* 401 */     holder.addComponent(ChunkStore.REGISTRY.getUnknownComponentType(), (Component)unknownComponents);
/* 402 */     return holder;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\prefab\config\SelectionPrefabSerializer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */