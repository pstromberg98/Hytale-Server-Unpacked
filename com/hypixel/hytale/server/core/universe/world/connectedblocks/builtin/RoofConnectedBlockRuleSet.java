/*     */ package com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.simple.IntegerCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.ConnectedBlockRuleSet;
/*     */ import com.hypixel.hytale.protocol.ConnectedBlockRuleSetType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFaceSupport;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
/*     */ import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;
/*     */ import com.hypixel.hytale.server.core.util.FillerBlockUtil;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIntPair;
/*     */ import java.util.Map;
/*     */ import java.util.Optional;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nullable;
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
/*     */ public class RoofConnectedBlockRuleSet
/*     */   extends ConnectedBlockRuleSet
/*     */   implements StairLikeConnectedBlockRuleSet
/*     */ {
/*     */   public static final BuilderCodec<RoofConnectedBlockRuleSet> CODEC;
/*     */   private StairConnectedBlockRuleSet regular;
/*     */   private StairConnectedBlockRuleSet hollow;
/*     */   private ConnectedBlockOutput topper;
/*     */   private String materialName;
/*     */   
/*     */   static {
/*  58 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RoofConnectedBlockRuleSet.class, RoofConnectedBlockRuleSet::new).append(new KeyedCodec("Regular", (Codec)StairConnectedBlockRuleSet.CODEC), (ruleSet, output) -> ruleSet.regular = output, ruleSet -> ruleSet.regular).addValidator(Validators.nonNull()).add()).append(new KeyedCodec("Hollow", (Codec)StairConnectedBlockRuleSet.CODEC), (ruleSet, output) -> ruleSet.hollow = output, ruleSet -> ruleSet.hollow).add()).append(new KeyedCodec("Topper", (Codec)ConnectedBlockOutput.CODEC), (ruleSet, output) -> ruleSet.topper = output, ruleSet -> ruleSet.topper).add()).append(new KeyedCodec("Width", (Codec)new IntegerCodec()), (ruleSet, output) -> ruleSet.width = output.intValue(), ruleSet -> Integer.valueOf(ruleSet.width)).add()).append(new KeyedCodec("MaterialName", (Codec)Codec.STRING), (ruleSet, materialName) -> ruleSet.materialName = materialName, ruleSet -> ruleSet.materialName).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   private int width = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static StairConnectedBlockRuleSet.StairType getConnectedBlockStairType(World world, Vector3i coordinate, StairLikeConnectedBlockRuleSet currentRuleSet, int blockId, int rotation, int width) {
/*  72 */     RotationTuple currentRotation = RotationTuple.get(rotation);
/*  73 */     Rotation currentYaw = currentRotation.yaw();
/*  74 */     Rotation currentPitch = currentRotation.pitch();
/*     */     
/*  76 */     boolean upsideDown = (currentPitch != Rotation.None);
/*     */     
/*  78 */     if (upsideDown) {
/*  79 */       currentYaw = currentYaw.flip();
/*     */     }
/*     */     
/*  82 */     Vector3i mutablePos = new Vector3i();
/*     */     
/*  84 */     StairConnectedBlockRuleSet.StairType resultingStair = StairConnectedBlockRuleSet.StairType.STRAIGHT;
/*  85 */     StairConnectedBlockRuleSet.StairConnection frontConnection = StairConnectedBlockRuleSet.getInvertedCornerConnection(world, currentRuleSet, coordinate, mutablePos, currentYaw, upsideDown);
/*     */     
/*  87 */     if (frontConnection != null) {
/*  88 */       boolean valid = isWidthFulfilled(world, coordinate, mutablePos, frontConnection, currentYaw, blockId, rotation, width);
/*     */       
/*  90 */       if (valid) {
/*  91 */         resultingStair = frontConnection.getStairType(true);
/*     */       }
/*     */     } 
/*     */     
/*  95 */     StairConnectedBlockRuleSet.StairConnection backConnection = StairConnectedBlockRuleSet.getCornerConnection(world, currentRuleSet, coordinate, mutablePos, rotation, currentYaw, upsideDown, width);
/*     */     
/*  97 */     if (backConnection != null) {
/*  98 */       boolean valid = isWidthFulfilled(world, coordinate, mutablePos, backConnection, currentYaw, blockId, rotation, width);
/*     */       
/* 100 */       if (valid) {
/* 101 */         resultingStair = backConnection.getStairType(false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 106 */     if (resultingStair == StairConnectedBlockRuleSet.StairType.STRAIGHT) {
/* 107 */       Vector3i aboveCoordinate = (new Vector3i(coordinate)).add(0, 1, 0);
/* 108 */       StairConnectedBlockRuleSet.StairConnection resultingConnection = getValleyConnection(world, coordinate, aboveCoordinate, currentRuleSet, currentRotation, mutablePos, false, blockId, rotation, width);
/*     */       
/* 110 */       if (resultingConnection != null) {
/* 111 */         resultingStair = resultingConnection.getStairType(true);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 116 */     if (resultingStair == StairConnectedBlockRuleSet.StairType.STRAIGHT) {
/* 117 */       Vector3i belowCoordinate = (new Vector3i(coordinate)).add(0, -1, 0);
/* 118 */       StairConnectedBlockRuleSet.StairConnection resultingConnection = getValleyConnection(world, coordinate, belowCoordinate, currentRuleSet, currentRotation, mutablePos, true, blockId, rotation, width);
/*     */       
/* 120 */       if (resultingConnection != null) {
/* 121 */         resultingStair = resultingConnection.getStairType(false);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 126 */     if (upsideDown) {
/* 127 */       switch (resultingStair) { case CORNER_LEFT: case CORNER_RIGHT: case INVERTED_CORNER_LEFT: case INVERTED_CORNER_RIGHT: default: break; }  resultingStair = 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 132 */         resultingStair;
/*     */     } 
/*     */ 
/*     */     
/* 136 */     return resultingStair;
/*     */   }
/*     */   
/*     */   private static boolean isWidthFulfilled(World world, Vector3i coordinate, Vector3i mutablePos, StairConnectedBlockRuleSet.StairConnection backConnection, Rotation currentYaw, int blockId, int rotation, int width) {
/* 140 */     boolean valid = true;
/*     */     
/* 142 */     for (int i = 0; i < width - 1; i++) {
/* 143 */       mutablePos.assign((backConnection == StairConnectedBlockRuleSet.StairConnection.CORNER_LEFT) ? Vector3i.WEST : Vector3i.EAST).scale(i + 1);
/* 144 */       currentYaw.rotateY(mutablePos, mutablePos);
/* 145 */       int requiredFiller = FillerBlockUtil.pack(mutablePos.x, mutablePos.y, mutablePos.z);
/* 146 */       mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
/*     */       
/* 148 */       WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(mutablePos.x, mutablePos.z));
/* 149 */       if (chunk != null) {
/*     */         
/* 151 */         int otherRotation = chunk.getRotationIndex(mutablePos.x, mutablePos.y, mutablePos.z);
/* 152 */         int otherFiller = chunk.getFiller(mutablePos.x, mutablePos.y, mutablePos.z);
/* 153 */         int otherBlockId = chunk.getBlock(mutablePos);
/*     */         
/* 155 */         if ((otherFiller != 0 || otherBlockId != blockId || otherRotation != rotation) && (otherFiller != requiredFiller || otherBlockId != blockId || otherRotation != rotation)) {
/* 156 */           valid = false;
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 161 */     return valid;
/*     */   }
/*     */   
/*     */   private static StairConnectedBlockRuleSet.StairConnection getValleyConnection(World world, Vector3i placementCoordinate, Vector3i checkCoordinate, StairLikeConnectedBlockRuleSet currentRuleSet, RotationTuple rotation, Vector3i mutablePos, boolean reverse, int blockId, int blockRotation, int width) {
/* 165 */     Rotation yaw = rotation.yaw();
/*     */     
/* 167 */     mutablePos.assign(reverse ? Vector3i.SOUTH : Vector3i.NORTH).scale(width);
/* 168 */     yaw.rotateY(mutablePos, mutablePos);
/* 169 */     mutablePos.add(checkCoordinate.x, checkCoordinate.y, checkCoordinate.z);
/*     */     
/* 171 */     ObjectIntPair<StairConnectedBlockRuleSet.StairType> backStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
/*     */     
/* 173 */     if (backStair == null) {
/* 174 */       return null;
/*     */     }
/*     */     
/* 177 */     boolean backConnection = reverse ? isTopperConnectionCompatible(rotation, backStair, Rotation.None) : isValleyConnectionCompatible(rotation, backStair, Rotation.None, false);
/* 178 */     if (!backConnection) {
/* 179 */       return null;
/*     */     }
/*     */     
/* 182 */     mutablePos.assign(reverse ? Vector3i.EAST : Vector3i.WEST).scale(width);
/* 183 */     yaw.rotateY(mutablePos, mutablePos);
/* 184 */     mutablePos.add(checkCoordinate.x, checkCoordinate.y, checkCoordinate.z);
/* 185 */     ObjectIntPair<StairConnectedBlockRuleSet.StairType> leftStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
/*     */     
/* 187 */     mutablePos.assign(reverse ? Vector3i.WEST : Vector3i.EAST).scale(width);
/* 188 */     yaw.rotateY(mutablePos, mutablePos);
/* 189 */     mutablePos.add(checkCoordinate.x, checkCoordinate.y, checkCoordinate.z);
/* 190 */     ObjectIntPair<StairConnectedBlockRuleSet.StairType> rightStair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
/*     */     
/* 192 */     boolean leftConnection = reverse ? isTopperConnectionCompatible(rotation, leftStair, Rotation.Ninety) : isValleyConnectionCompatible(rotation, leftStair, Rotation.Ninety, false);
/* 193 */     boolean rightConnection = reverse ? isTopperConnectionCompatible(rotation, rightStair, Rotation.TwoSeventy) : isValleyConnectionCompatible(rotation, rightStair, Rotation.TwoSeventy, false);
/*     */     
/* 195 */     if (leftConnection == rightConnection) {
/* 196 */       return null;
/*     */     }
/*     */     
/* 199 */     StairConnectedBlockRuleSet.StairConnection connection = leftConnection ? StairConnectedBlockRuleSet.StairConnection.CORNER_LEFT : StairConnectedBlockRuleSet.StairConnection.CORNER_RIGHT;
/*     */     
/* 201 */     if (!isWidthFulfilled(world, placementCoordinate, mutablePos, connection, yaw, blockId, blockRotation, width)) {
/* 202 */       return null;
/*     */     }
/*     */     
/* 205 */     return connection;
/*     */   }
/*     */   
/*     */   private static boolean isTopperConnectionCompatible(RotationTuple rotation, ObjectIntPair<StairConnectedBlockRuleSet.StairType> otherStair, Rotation yawOffset) {
/* 209 */     return isValleyConnectionCompatible(rotation, otherStair, yawOffset, true);
/*     */   }
/*     */   
/*     */   private static boolean canBeTopper(World world, Vector3i coordinate, StairLikeConnectedBlockRuleSet currentRuleSet, RotationTuple rotation, Vector3i mutablePos) {
/* 213 */     Rotation yaw = rotation.yaw();
/*     */     
/* 215 */     Vector3i[] directions = { Vector3i.NORTH, Vector3i.SOUTH, Vector3i.EAST, Vector3i.WEST };
/* 216 */     Rotation[] yawOffsets = { Rotation.OneEighty, Rotation.None, Rotation.Ninety, Rotation.TwoSeventy };
/*     */     
/* 218 */     for (int i = 0; i < directions.length; i++) {
/* 219 */       mutablePos.assign(directions[i]);
/* 220 */       yaw.rotateY(mutablePos, mutablePos);
/* 221 */       mutablePos.add(coordinate.x, coordinate.y, coordinate.z);
/*     */       
/* 223 */       ObjectIntPair<StairConnectedBlockRuleSet.StairType> stair = StairConnectedBlockRuleSet.getStairData(world, mutablePos, currentRuleSet.getMaterialName());
/*     */       
/* 225 */       if (stair == null || !isTopperConnectionCompatible(rotation, stair, yawOffsets[i])) {
/* 226 */         return false;
/*     */       }
/*     */     } 
/*     */     
/* 230 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean isValleyConnectionCompatible(RotationTuple rotation, ObjectIntPair<StairConnectedBlockRuleSet.StairType> otherStair, Rotation yawOffset, boolean inverted) {
/* 234 */     Rotation targetYaw = rotation.yaw().add(yawOffset);
/*     */     
/* 236 */     if (otherStair == null) {
/* 237 */       return false;
/*     */     }
/*     */     
/* 240 */     RotationTuple stairRotation = RotationTuple.get(otherStair.rightInt());
/* 241 */     StairConnectedBlockRuleSet.StairType otherStairType = (StairConnectedBlockRuleSet.StairType)otherStair.first();
/*     */     
/* 243 */     if (stairRotation.pitch() != rotation.pitch()) {
/* 244 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 248 */     if (inverted && otherStairType.isCorner())
/* 249 */       return false; 
/* 250 */     if (!inverted && otherStairType.isInvertedCorner()) {
/* 251 */       return false;
/*     */     }
/*     */     
/* 254 */     return (stairRotation.yaw() == targetYaw || (otherStairType == StairConnectedBlockRuleSet.StairConnection.CORNER_RIGHT
/* 255 */       .getStairType(inverted) && stairRotation.yaw() == targetYaw.add(Rotation.Ninety)) || (otherStairType == StairConnectedBlockRuleSet.StairConnection.CORNER_LEFT
/* 256 */       .getStairType(inverted) && stairRotation.yaw() == targetYaw.add(Rotation.TwoSeventy)));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onlyUpdateOnPlacement() {
/* 261 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(World world, Vector3i coordinate, BlockType blockType, int rotation, Vector3i placementNormal, boolean isPlacement) {
/* 267 */     WorldChunk chunk = world.getChunkIfLoaded(ChunkUtil.indexChunkFromBlock(coordinate.x, coordinate.z));
/* 268 */     if (chunk == null) return Optional.empty();
/*     */     
/* 270 */     int belowBlockId = chunk.getBlock(coordinate.x, coordinate.y - 1, coordinate.z);
/* 271 */     BlockType belowBlockType = (BlockType)BlockType.getAssetMap().getAsset(belowBlockId);
/* 272 */     int belowBlockRotation = chunk.getRotationIndex(coordinate.x, coordinate.y - 1, coordinate.z);
/*     */     
/* 274 */     boolean hollow = true;
/*     */     
/* 276 */     if (belowBlockType != null) {
/* 277 */       Map<BlockFace, BlockFaceSupport[]> supporting = belowBlockType.getSupporting(belowBlockRotation);
/*     */       
/* 279 */       if (supporting != null) {
/* 280 */         BlockFaceSupport[] support = supporting.get(BlockFace.UP);
/* 281 */         hollow = (support == null);
/*     */       } 
/*     */     } 
/*     */     
/* 285 */     int blockId = BlockType.getAssetMap().getIndex(blockType.getId());
/* 286 */     StairConnectedBlockRuleSet.StairType stairType = getConnectedBlockStairType(world, coordinate, this, blockId, rotation, this.width);
/*     */ 
/*     */     
/* 289 */     if (this.topper != null && stairType == StairConnectedBlockRuleSet.StairType.STRAIGHT) {
/* 290 */       Vector3i belowCoordinate = (new Vector3i(coordinate)).add(0, -1, 0);
/* 291 */       RotationTuple currentRotation = RotationTuple.get(rotation);
/* 292 */       currentRotation = RotationTuple.of(Rotation.None, currentRotation.pitch(), currentRotation.roll());
/*     */       
/* 294 */       Vector3i mutablePos = new Vector3i();
/* 295 */       boolean topper = canBeTopper(world, belowCoordinate, this, currentRotation, mutablePos);
/*     */       
/* 297 */       if (topper) {
/* 298 */         BlockType topperBlockType = (BlockType)BlockType.getAssetMap().getAsset(this.topper.blockTypeKey);
/*     */         
/* 300 */         if (topperBlockType != null) {
/* 301 */           return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(topperBlockType.getId(), rotation));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 306 */     if (this.hollow != null && hollow) {
/* 307 */       BlockType hollowBlockType = this.hollow.getStairBlockType(stairType);
/*     */       
/* 309 */       if (hollowBlockType != null) {
/* 310 */         return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(hollowBlockType.getId(), rotation));
/*     */       }
/*     */     } 
/*     */     
/* 314 */     BlockType regularBlockType = this.regular.getStairBlockType(stairType);
/*     */     
/* 316 */     if (regularBlockType != null) {
/* 317 */       ConnectedBlocksUtil.ConnectedBlockResult result = new ConnectedBlocksUtil.ConnectedBlockResult(regularBlockType.getId(), rotation);
/*     */ 
/*     */       
/* 320 */       if (this.regular != null && this.width > 0) {
/* 321 */         StairConnectedBlockRuleSet.StairType existingStairType = this.regular.getStairType(BlockType.getAssetMap().getIndex(blockType.getId()));
/*     */         
/* 323 */         if (existingStairType != null && existingStairType != StairConnectedBlockRuleSet.StairType.STRAIGHT) {
/*     */ 
/*     */           
/* 326 */           int previousWidth = existingStairType.isLeft() ? -(this.width - 1) : (existingStairType.isRight() ? (this.width - 1) : 0);
/*     */ 
/*     */           
/* 329 */           int newWidth = stairType.isLeft() ? -(this.width - 1) : (stairType.isRight() ? (this.width - 1) : 0);
/*     */           
/* 331 */           if (newWidth != previousWidth) {
/* 332 */             Vector3i mutablePos = new Vector3i();
/* 333 */             Rotation currentYaw = RotationTuple.get(rotation).yaw();
/*     */             
/* 335 */             mutablePos.assign(Vector3i.EAST).scale(previousWidth);
/* 336 */             currentYaw.rotateY(mutablePos, mutablePos);
/*     */             
/* 338 */             result.addAdditionalBlock(mutablePos, regularBlockType.getId(), rotation);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 343 */       return Optional.of(result);
/*     */     } 
/*     */     
/* 346 */     return Optional.empty();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateCachedBlockTypes(BlockType baseBlockType, BlockTypeAssetMap<String, BlockType> assetMap) {
/* 351 */     if (this.regular != null) {
/* 352 */       this.regular.updateCachedBlockTypes(baseBlockType, assetMap);
/*     */     }
/*     */     
/* 355 */     if (this.hollow != null) {
/* 356 */       this.hollow.updateCachedBlockTypes(baseBlockType, assetMap);
/*     */     }
/*     */     
/* 359 */     if (this.topper != null) {
/* 360 */       this.topper.resolve(baseBlockType, assetMap);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public StairConnectedBlockRuleSet.StairType getStairType(int blockId) {
/* 366 */     StairConnectedBlockRuleSet.StairType regularStairType = this.regular.getStairType(blockId);
/* 367 */     if (regularStairType != null) {
/* 368 */       return regularStairType;
/*     */     }
/*     */     
/* 371 */     if (this.hollow != null) {
/* 372 */       return this.hollow.getStairType(blockId);
/*     */     }
/*     */     
/* 375 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getMaterialName() {
/* 381 */     return this.materialName;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ConnectedBlockRuleSet toPacket(BlockTypeAssetMap<String, BlockType> assetMap) {
/* 387 */     ConnectedBlockRuleSet packet = new ConnectedBlockRuleSet();
/* 388 */     packet.type = ConnectedBlockRuleSetType.Roof;
/*     */     
/* 390 */     com.hypixel.hytale.protocol.RoofConnectedBlockRuleSet roofPacket = new com.hypixel.hytale.protocol.RoofConnectedBlockRuleSet();
/*     */     
/* 392 */     if (this.regular != null) {
/* 393 */       roofPacket.regular = this.regular.toProtocol(assetMap);
/*     */     }
/*     */     
/* 396 */     if (this.hollow != null) {
/* 397 */       roofPacket.hollow = this.hollow.toProtocol(assetMap);
/*     */     }
/*     */     
/* 400 */     if (this.topper != null) {
/* 401 */       roofPacket.topperBlockId = assetMap.getIndex(this.topper.blockTypeKey);
/*     */     } else {
/* 403 */       roofPacket.topperBlockId = -1;
/*     */     } 
/*     */     
/* 406 */     roofPacket.width = this.width;
/* 407 */     roofPacket.materialName = this.materialName;
/*     */     
/* 409 */     packet.roof = roofPacket;
/* 410 */     return packet;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\connectedblocks\builtin\RoofConnectedBlockRuleSet.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */