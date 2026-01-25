/*     */ package com.hypixel.hytale.server.core.universe.world.worldmap.provider.chunk;
/*     */ 
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.protocol.ShaderType;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.environment.config.Environment;
/*     */ import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.CompletionStage;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ class ImageBuilder
/*     */ {
/*     */   private final long index;
/*     */   private final World world;
/*     */   @Nonnull
/*     */   private final MapImage image;
/*     */   private final int sampleWidth;
/*     */   private final int sampleHeight;
/*     */   private final int blockStepX;
/*     */   private final int blockStepZ;
/*     */   @Nonnull
/*     */   private final short[] heightSamples;
/*     */   @Nonnull
/*     */   private final int[] tintSamples;
/*     */   @Nonnull
/*     */   private final int[] blockSamples;
/*     */   @Nonnull
/*     */   private final short[] neighborHeightSamples;
/*     */   @Nonnull
/*     */   private final short[] fluidDepthSamples;
/*     */   @Nonnull
/*     */   private final int[] environmentSamples;
/*     */   @Nonnull
/*     */   private final int[] fluidSamples;
/*  47 */   private final Color outColor = new Color();
/*     */   
/*     */   @Nullable
/*     */   private WorldChunk worldChunk;
/*     */   private FluidSection[] fluidSections;
/*     */   
/*     */   public ImageBuilder(long index, int imageWidth, int imageHeight, World world) {
/*  54 */     this.index = index;
/*  55 */     this.world = world;
/*     */     
/*  57 */     this.image = new MapImage(imageWidth, imageHeight, new int[imageWidth * imageHeight]);
/*     */     
/*  59 */     this.sampleWidth = Math.min(32, this.image.width);
/*  60 */     this.sampleHeight = Math.min(32, this.image.height);
/*  61 */     this.blockStepX = Math.max(1, 32 / this.image.width);
/*  62 */     this.blockStepZ = Math.max(1, 32 / this.image.height);
/*     */     
/*  64 */     this.heightSamples = new short[this.sampleWidth * this.sampleHeight];
/*  65 */     this.tintSamples = new int[this.sampleWidth * this.sampleHeight];
/*  66 */     this.blockSamples = new int[this.sampleWidth * this.sampleHeight];
/*     */     
/*  68 */     this.neighborHeightSamples = new short[(this.sampleWidth + 2) * (this.sampleHeight + 2)];
/*     */     
/*  70 */     this.fluidDepthSamples = new short[this.sampleWidth * this.sampleHeight];
/*  71 */     this.environmentSamples = new int[this.sampleWidth * this.sampleHeight];
/*  72 */     this.fluidSamples = new int[this.sampleWidth * this.sampleHeight];
/*     */   }
/*     */   
/*     */   public long getIndex() {
/*  76 */     return this.index;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public MapImage getImage() {
/*  81 */     return this.image;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private CompletableFuture<ImageBuilder> fetchChunk() {
/*  86 */     return this.world.getChunkStore().getChunkReferenceAsync(this.index).thenApplyAsync(ref -> { if (ref == null || !ref.isValid()) return null;  this.worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType()); ChunkColumn chunkColumn = (ChunkColumn)ref.getStore().getComponent(ref, ChunkColumn.getComponentType()); this.fluidSections = new FluidSection[10]; for (int y = 0; y < 10; y++) { Ref<ChunkStore> sectionRef = chunkColumn.getSection(y); this.fluidSections[y] = (FluidSection)this.world.getChunkStore().getStore().getComponent(sectionRef, FluidSection.getComponentType()); }  return this; }(Executor)this.world);
/*     */   }
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
/*     */   @Nonnull
/*     */   private CompletableFuture<ImageBuilder> sampleNeighborsSync() {
/* 104 */     CompletableFuture<Void> north = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX(), this.worldChunk.getZ() - 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int z = (this.sampleHeight - 1) * this.blockStepZ;
/*     */           for (int ix = 0; ix < this.sampleWidth; ix++) {
/*     */             int x = ix * this.blockStepX;
/*     */             this.neighborHeightSamples[1 + ix] = worldChunk.getHeight(x, z);
/*     */           } 
/*     */         }(Executor)this.world);
/* 116 */     CompletableFuture<Void> south = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX(), this.worldChunk.getZ() + 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int z = 0;
/*     */           int neighbourStartIndex = (this.sampleHeight + 1) * (this.sampleWidth + 2) + 1;
/*     */           for (int ix = 0; ix < this.sampleWidth; ix++) {
/*     */             int x = ix * this.blockStepX;
/*     */             this.neighborHeightSamples[neighbourStartIndex + ix] = worldChunk.getHeight(x, z);
/*     */           } 
/*     */         }(Executor)this.world);
/* 129 */     CompletableFuture<Void> west = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() - 1, this.worldChunk.getZ())).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = (this.sampleWidth - 1) * this.blockStepX;
/*     */           for (int iz = 0; iz < this.sampleHeight; iz++) {
/*     */             int z = iz * this.blockStepZ;
/*     */             this.neighborHeightSamples[(iz + 1) * (this.sampleWidth + 2)] = worldChunk.getHeight(x, z);
/*     */           } 
/*     */         }(Executor)this.world);
/* 141 */     CompletableFuture<Void> east = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() + 1, this.worldChunk.getZ())).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = 0;
/*     */           for (int iz = 0; iz < this.sampleHeight; iz++) {
/*     */             int z = iz * this.blockStepZ;
/*     */             this.neighborHeightSamples[(iz + 1) * (this.sampleWidth + 2) + this.sampleWidth + 1] = worldChunk.getHeight(x, z);
/*     */           } 
/*     */         }(Executor)this.world);
/* 153 */     CompletableFuture<Void> northeast = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() + 1, this.worldChunk.getZ() - 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = 0;
/*     */           int z = (this.sampleHeight - 1) * this.blockStepZ;
/*     */           this.neighborHeightSamples[0] = worldChunk.getHeight(x, z);
/*     */         }(Executor)this.world);
/* 163 */     CompletableFuture<Void> northwest = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() - 1, this.worldChunk.getZ() - 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = (this.sampleWidth - 1) * this.blockStepX;
/*     */           int z = (this.sampleHeight - 1) * this.blockStepZ;
/*     */           this.neighborHeightSamples[this.sampleWidth + 1] = worldChunk.getHeight(x, z);
/*     */         }(Executor)this.world);
/* 173 */     CompletableFuture<Void> southeast = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() + 1, this.worldChunk.getZ() + 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = 0;
/*     */           int z = 0;
/*     */           this.neighborHeightSamples[(this.sampleHeight + 1) * (this.sampleWidth + 2) + this.sampleWidth + 1] = worldChunk.getHeight(x, z);
/*     */         }(Executor)this.world);
/* 183 */     CompletableFuture<Void> southwest = this.world.getChunkStore().getChunkReferenceAsync(ChunkUtil.indexChunk(this.worldChunk.getX() - 1, this.worldChunk.getZ() + 1)).thenAcceptAsync(ref -> {
/*     */           if (ref == null || !ref.isValid()) {
/*     */             return;
/*     */           }
/*     */           WorldChunk worldChunk = (WorldChunk)ref.getStore().getComponent(ref, WorldChunk.getComponentType());
/*     */           int x = (this.sampleWidth - 1) * this.blockStepX;
/*     */           int z = 0;
/*     */           this.neighborHeightSamples[(this.sampleHeight + 1) * (this.sampleWidth + 2)] = worldChunk.getHeight(x, z);
/*     */         }(Executor)this.world);
/* 192 */     return CompletableFuture.allOf((CompletableFuture<?>[])new CompletableFuture[] { north, south, west, east, northeast, northwest, southeast, southwest
/*     */ 
/*     */         
/* 195 */         }).thenApply(v -> this);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private ImageBuilder generateImageAsync() {
/* 200 */     for (int ix = 0; ix < this.sampleWidth; ix++) {
/* 201 */       for (int j = 0; j < this.sampleHeight; j++) {
/* 202 */         int sampleIndex = j * this.sampleWidth + ix;
/* 203 */         int x = ix * this.blockStepX;
/* 204 */         int z = j * this.blockStepZ;
/*     */         
/* 206 */         short height = this.worldChunk.getHeight(x, z);
/* 207 */         int tint = this.worldChunk.getTint(x, z);
/* 208 */         this.heightSamples[sampleIndex] = height;
/* 209 */         this.tintSamples[sampleIndex] = tint;
/*     */         
/* 211 */         int blockId = this.worldChunk.getBlock(x, height, z);
/* 212 */         this.blockSamples[sampleIndex] = blockId;
/*     */ 
/*     */         
/* 215 */         int fluidId = 0;
/* 216 */         int fluidTop = 320;
/* 217 */         Fluid fluid = null;
/*     */         
/* 219 */         int chunkYGround = ChunkUtil.chunkCoordinate(height);
/*     */ 
/*     */         
/* 222 */         int chunkY = 9;
/*     */         
/* 224 */         label83: while (chunkY >= 0 && chunkY >= chunkYGround) {
/* 225 */           FluidSection fluidSection = this.fluidSections[chunkY];
/*     */ 
/*     */           
/* 228 */           if (fluidSection == null || fluidSection.isEmpty()) {
/* 229 */             chunkY--;
/*     */ 
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/* 235 */           int minBlockY = Math.max(ChunkUtil.minBlock(chunkY), height);
/* 236 */           int maxBlockY = ChunkUtil.maxBlock(chunkY);
/* 237 */           for (int blockY = maxBlockY; blockY >= minBlockY; ) {
/* 238 */             fluidId = fluidSection.getFluidId(x, blockY, z);
/* 239 */             if (fluidId == 0) { blockY--; continue; }
/* 240 */              fluid = (Fluid)Fluid.getAssetMap().getAsset(fluidId);
/*     */ 
/*     */             
/* 243 */             fluidTop = blockY;
/*     */             
/*     */             break label83;
/*     */           } 
/* 247 */           chunkY--;
/*     */         } 
/*     */ 
/*     */         
/* 251 */         int fluidBottom = height;
/*     */         
/* 253 */         label84: while (chunkY >= 0 && chunkY >= chunkYGround) {
/* 254 */           FluidSection fluidSection = this.fluidSections[chunkY];
/*     */ 
/*     */           
/* 257 */           if (fluidSection == null || fluidSection.isEmpty()) {
/* 258 */             fluidBottom = Math.min(ChunkUtil.maxBlock(chunkY) + 1, fluidTop);
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/* 264 */           int minBlockY = Math.max(ChunkUtil.minBlock(chunkY), height);
/* 265 */           int maxBlockY = Math.min(ChunkUtil.maxBlock(chunkY), fluidTop - 1);
/* 266 */           for (int blockY = maxBlockY; blockY >= minBlockY; blockY--) {
/* 267 */             int nextFluidId = fluidSection.getFluidId(x, blockY, z);
/* 268 */             if (nextFluidId != fluidId) {
/* 269 */               Fluid nextFluid = (Fluid)Fluid.getAssetMap().getAsset(nextFluidId);
/* 270 */               if (!Objects.equals(fluid.getParticleColor(), nextFluid.getParticleColor())) {
/*     */ 
/*     */                 
/* 273 */                 fluidBottom = blockY + 1;
/*     */                 
/*     */                 break label84;
/*     */               } 
/*     */             } 
/*     */           } 
/* 279 */           chunkY--;
/*     */         } 
/*     */         
/* 282 */         short fluidDepth = (fluidId != 0) ? (short)(fluidTop - fluidBottom + 1) : 0;
/*     */         
/* 284 */         int environmentId = this.worldChunk.getBlockChunk().getEnvironment(x, fluidTop, z);
/*     */         
/* 286 */         this.fluidDepthSamples[sampleIndex] = fluidDepth;
/* 287 */         this.environmentSamples[sampleIndex] = environmentId;
/* 288 */         this.fluidSamples[sampleIndex] = fluidId;
/*     */       } 
/*     */     } 
/*     */     
/* 292 */     float imageToSampleRatioWidth = this.sampleWidth / this.image.width;
/* 293 */     float imageToSampleRatioHeight = this.sampleHeight / this.image.height;
/*     */ 
/*     */     
/* 296 */     int blockPixelWidth = Math.max(1, this.image.width / this.sampleWidth);
/* 297 */     int blockPixelHeight = Math.max(1, this.image.height / this.sampleHeight);
/*     */ 
/*     */     
/* 300 */     for (int iz = 0; iz < this.sampleHeight; iz++) {
/* 301 */       System.arraycopy(this.heightSamples, iz * this.sampleWidth, this.neighborHeightSamples, (iz + 1) * (this.sampleWidth + 2) + 1, this.sampleWidth);
/*     */     }
/*     */     
/* 304 */     for (int i = 0; i < this.image.width; i++) {
/* 305 */       for (int j = 0; j < this.image.height; j++) {
/* 306 */         int sampleX = Math.min((int)(i * imageToSampleRatioWidth), this.sampleWidth - 1);
/* 307 */         int sampleZ = Math.min((int)(j * imageToSampleRatioHeight), this.sampleHeight - 1);
/* 308 */         int sampleIndex = sampleZ * this.sampleWidth + sampleX;
/*     */         
/* 310 */         int blockPixelX = i % blockPixelWidth;
/* 311 */         int blockPixelZ = j % blockPixelHeight;
/*     */         
/* 313 */         short height = this.heightSamples[sampleIndex];
/* 314 */         int tint = this.tintSamples[sampleIndex];
/* 315 */         int blockId = this.blockSamples[sampleIndex];
/*     */ 
/*     */         
/* 318 */         if (height < 0 || blockId == 0) {
/* 319 */           this.outColor.r = this.outColor.g = this.outColor.b = this.outColor.a = 0;
/* 320 */           packImageData(i, j);
/*     */         }
/*     */         else {
/*     */           
/* 324 */           getBlockColor(blockId, tint, this.outColor);
/*     */           
/* 326 */           short north = this.neighborHeightSamples[sampleZ * (this.sampleWidth + 2) + sampleX + 1];
/* 327 */           short south = this.neighborHeightSamples[(sampleZ + 2) * (this.sampleWidth + 2) + sampleX + 1];
/* 328 */           short west = this.neighborHeightSamples[(sampleZ + 1) * (this.sampleWidth + 2) + sampleX];
/* 329 */           short east = this.neighborHeightSamples[(sampleZ + 1) * (this.sampleWidth + 2) + sampleX + 2];
/*     */           
/* 331 */           short northWest = this.neighborHeightSamples[sampleZ * (this.sampleWidth + 2) + sampleX];
/* 332 */           short northEast = this.neighborHeightSamples[sampleZ * (this.sampleWidth + 2) + sampleX + 2];
/* 333 */           short southWest = this.neighborHeightSamples[(sampleZ + 2) * (this.sampleWidth + 2) + sampleX];
/* 334 */           short southEast = this.neighborHeightSamples[(sampleZ + 2) * (this.sampleWidth + 2) + sampleX + 2];
/*     */           
/* 336 */           float shade = shadeFromHeights(blockPixelX, blockPixelZ, blockPixelWidth, blockPixelHeight, height, north, south, west, east, northWest, northEast, southWest, southEast);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 342 */           this.outColor.multiply(shade);
/*     */           
/* 344 */           if (height < 320) {
/* 345 */             int fluidId = this.fluidSamples[sampleIndex];
/* 346 */             if (fluidId != 0) {
/* 347 */               short fluidDepth = this.fluidDepthSamples[sampleIndex];
/* 348 */               int environmentId = this.environmentSamples[sampleIndex];
/*     */               
/* 350 */               getFluidColor(fluidId, environmentId, fluidDepth, this.outColor);
/*     */             } 
/*     */           } 
/*     */           
/* 354 */           packImageData(i, j);
/*     */         } 
/*     */       } 
/*     */     } 
/* 358 */     return this;
/*     */   }
/*     */   
/*     */   private void packImageData(int ix, int iz) {
/* 362 */     this.image.data[iz * this.image.width + ix] = this.outColor.pack();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static float shadeFromHeights(int blockPixelX, int blockPixelZ, int blockPixelWidth, int blockPixelHeight, short height, short north, short south, short west, short east, short northWest, short northEast, short southWest, short southEast) {
/* 374 */     float u = (blockPixelX + 0.5F) / blockPixelWidth;
/* 375 */     float v = (blockPixelZ + 0.5F) / blockPixelHeight;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 380 */     float ud = (u + v) / 2.0F;
/* 381 */     float vd = (1.0F - u + v) / 2.0F;
/*     */ 
/*     */ 
/*     */     
/* 385 */     float dhdx1 = (height - west) * (1.0F - u) + (east - height) * u;
/* 386 */     float dhdz1 = (height - north) * (1.0F - v) + (south - height) * v;
/*     */     
/* 388 */     float dhdx2 = (height - northWest) * (1.0F - ud) + (southEast - height) * ud;
/* 389 */     float dhdz2 = (height - northEast) * (1.0F - vd) + (southWest - height) * vd;
/*     */     
/* 391 */     float dhdx = dhdx1 * 2.0F + dhdx2;
/* 392 */     float dhdz = dhdz1 * 2.0F + dhdz2;
/*     */     
/* 394 */     float dy = 3.0F;
/*     */ 
/*     */     
/* 397 */     float nx = dhdx, ny = dy, nz = dhdz;
/* 398 */     float invS = 1.0F / (float)Math.sqrt((nx * nx + ny * ny + nz * nz));
/* 399 */     nx *= invS;
/* 400 */     ny *= invS;
/* 401 */     nz *= invS;
/*     */ 
/*     */     
/* 404 */     float lx = -0.2F, ly = 0.8F, lz = 0.5F;
/* 405 */     float invL = 1.0F / (float)Math.sqrt((lx * lx + ly * ly + lz * lz));
/* 406 */     lx *= invL;
/* 407 */     ly *= invL;
/* 408 */     lz *= invL;
/*     */     
/* 410 */     float lambert = Math.max(0.0F, nx * lx + ny * ly + nz * lz);
/* 411 */     float ambient = 0.4F, diffuse = 0.6F;
/* 412 */     return ambient + diffuse * lambert;
/*     */   }
/*     */   
/*     */   private static void getBlockColor(int blockId, int biomeTintColor, @Nonnull Color outColor) {
/* 416 */     BlockType block = (BlockType)BlockType.getAssetMap().getAsset(blockId);
/*     */     
/* 418 */     int biomeTintR = biomeTintColor >> 16 & 0xFF;
/* 419 */     int biomeTintG = biomeTintColor >> 8 & 0xFF;
/* 420 */     int biomeTintB = biomeTintColor >> 0 & 0xFF;
/*     */     
/* 422 */     com.hypixel.hytale.protocol.Color[] tintUp = block.getTintUp();
/* 423 */     boolean hasTint = (tintUp != null && tintUp.length > 0);
/* 424 */     int selfTintR = hasTint ? ((tintUp[0]).red & 0xFF) : 255;
/* 425 */     int selfTintG = hasTint ? ((tintUp[0]).green & 0xFF) : 255;
/* 426 */     int selfTintB = hasTint ? ((tintUp[0]).blue & 0xFF) : 255;
/*     */     
/* 428 */     float biomeTintMultiplier = block.getBiomeTintUp() / 100.0F;
/*     */     
/* 430 */     int tintColorR = (int)(selfTintR + (biomeTintR - selfTintR) * biomeTintMultiplier);
/* 431 */     int tintColorG = (int)(selfTintG + (biomeTintG - selfTintG) * biomeTintMultiplier);
/* 432 */     int tintColorB = (int)(selfTintB + (biomeTintB - selfTintB) * biomeTintMultiplier);
/*     */ 
/*     */     
/* 435 */     com.hypixel.hytale.protocol.Color particleColor = block.getParticleColor();
/* 436 */     if (particleColor != null && biomeTintMultiplier < 1.0F) {
/* 437 */       tintColorR = tintColorR * (particleColor.red & 0xFF) / 255;
/* 438 */       tintColorG = tintColorG * (particleColor.green & 0xFF) / 255;
/* 439 */       tintColorB = tintColorB * (particleColor.blue & 0xFF) / 255;
/*     */     } 
/*     */ 
/*     */     
/* 443 */     outColor.r = tintColorR & 0xFF;
/* 444 */     outColor.g = tintColorG & 0xFF;
/* 445 */     outColor.b = tintColorB & 0xFF;
/* 446 */     outColor.a = 255;
/*     */   }
/*     */   
/*     */   private static void getFluidColor(int fluidId, int environmentId, int fluidDepth, @Nonnull Color outColor) {
/* 450 */     int tintColorR = 255;
/* 451 */     int tintColorG = 255;
/* 452 */     int tintColorB = 255;
/*     */     
/* 454 */     Fluid fluid = (Fluid)Fluid.getAssetMap().getAsset(fluidId);
/* 455 */     if (fluid == null)
/* 456 */       return;  if (fluid.hasEffect(ShaderType.Water)) {
/* 457 */       Environment environment = (Environment)Environment.getAssetMap().getAsset(environmentId);
/* 458 */       com.hypixel.hytale.protocol.Color waterTint = environment.getWaterTint();
/* 459 */       if (waterTint != null) {
/* 460 */         tintColorR = tintColorR * (waterTint.red & 0xFF) / 255;
/* 461 */         tintColorG = tintColorG * (waterTint.green & 0xFF) / 255;
/* 462 */         tintColorB = tintColorB * (waterTint.blue & 0xFF) / 255;
/*     */       } 
/*     */     } 
/*     */     
/* 466 */     com.hypixel.hytale.protocol.Color partcileColor = fluid.getParticleColor();
/* 467 */     if (partcileColor != null) {
/* 468 */       tintColorR = tintColorR * (partcileColor.red & 0xFF) / 255;
/* 469 */       tintColorG = tintColorG * (partcileColor.green & 0xFF) / 255;
/* 470 */       tintColorB = tintColorB * (partcileColor.blue & 0xFF) / 255;
/*     */     } 
/*     */ 
/*     */     
/* 474 */     float depthMultiplier = Math.min(1.0F, 1.0F / fluidDepth);
/* 475 */     outColor.r = (int)(tintColorR + ((outColor.r & 0xFF) - tintColorR) * depthMultiplier) & 0xFF;
/* 476 */     outColor.g = (int)(tintColorG + ((outColor.g & 0xFF) - tintColorG) * depthMultiplier) & 0xFF;
/* 477 */     outColor.b = (int)(tintColorB + ((outColor.b & 0xFF) - tintColorB) * depthMultiplier) & 0xFF;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static CompletableFuture<ImageBuilder> build(long index, int imageWidth, int imageHeight, World world) {
/* 482 */     return CompletableFuture.<ImageBuilder>completedFuture(new ImageBuilder(index, imageWidth, imageHeight, world))
/* 483 */       .thenCompose(ImageBuilder::fetchChunk)
/* 484 */       .thenCompose(builder -> (builder != null) ? builder.sampleNeighborsSync() : CompletableFuture.completedFuture(null))
/* 485 */       .thenApplyAsync(builder -> (builder != null) ? builder.generateImageAsync() : null);
/*     */   }
/*     */   private static class Color { public int r; public int g;
/*     */     public int b;
/*     */     public int a;
/*     */     
/*     */     public int pack() {
/* 492 */       return (this.r & 0xFF) << 24 | (this.g & 0xFF) << 16 | (this.b & 0xFF) << 8 | this.a & 0xFF;
/*     */     }
/*     */     
/*     */     public void multiply(float value) {
/* 496 */       this.r = Math.min(255, Math.max(0, (int)(this.r * value)));
/* 497 */       this.g = Math.min(255, Math.max(0, (int)(this.g * value)));
/* 498 */       this.b = Math.min(255, Math.max(0, (int)(this.b * value)));
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\worldmap\provider\chunk\ImageBuilder.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */