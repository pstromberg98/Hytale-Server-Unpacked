/*     */ package com.hypixel.hytale.math.util;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChunkUtil
/*     */ {
/*     */   public static final int BITS = 5;
/*     */   public static final int SIZE = 32;
/*     */   public static final int SIZE_2 = 1024;
/*     */   public static final int SIZE_MINUS_1 = 31;
/*     */   public static final int SIZE_MASK = 31;
/*     */   public static final int SIZE_COLUMNS = 1024;
/*     */   public static final int SIZE_COLUMNS_MASK = 1023;
/*     */   public static final int SIZE_BLOCKS = 32768;
/*     */   public static final int BITS2 = 10;
/*     */   public static final int NON_CHUNK_MASK = -32;
/*     */   public static final int HEIGHT_SECTIONS = 10;
/*     */   public static final int HEIGHT = 320;
/*     */   public static final int HEIGHT_MINUS_1 = 319;
/*  25 */   public static final int HEIGHT_MASK = (Integer.highestOneBit(320) << 1) - 1;
/*     */   
/*     */   public static final int SIZE_BLOCKS_COLUMN = 327680;
/*  28 */   public static final long NOT_FOUND = indexChunk(-2147483648, -2147483648);
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int MIN_Y = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int MIN_ENTITY_Y = -32;
/*     */ 
/*     */   
/*     */   public static final int MIN_SECTION = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] shortToByteArray(@Nonnull short[] data) {
/*  44 */     ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 2).order(ByteOrder.LITTLE_ENDIAN);
/*  45 */     byteBuffer.asShortBuffer().put(data);
/*  46 */     return byteBuffer.array();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] intToByteArray(@Nonnull int[] data) {
/*  56 */     ByteBuffer byteBuffer = ByteBuffer.allocate(data.length * 4).order(ByteOrder.LITTLE_ENDIAN);
/*  57 */     byteBuffer.asIntBuffer().put(data);
/*  58 */     return byteBuffer.array();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexColumn(int x, int z) {
/*  69 */     return (z & 0x1F) << 5 | x & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int xFromColumn(int index) {
/*  79 */     return index & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int zFromColumn(int index) {
/*  89 */     return index >> 5 & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexSection(int y) {
/*  99 */     return y >> 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexBlockFromColumn(int column, int y) {
/* 110 */     return (y & 0x1F) << 10 | column & 0x3FF;
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
/*     */   public static int indexBlock(int x, int y, int z) {
/* 122 */     return (y & 0x1F) << 10 | (z & 0x1F) << 5 | x & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int xFromIndex(int index) {
/* 132 */     return index & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int yFromIndex(int index) {
/* 142 */     return index >> 10 & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int zFromIndex(int index) {
/* 152 */     return index >> 5 & 0x1F;
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
/*     */   public static int indexBlockInColumn(int x, int y, int z) {
/* 164 */     return (y & HEIGHT_MASK) << 10 | (z & 0x1F) << 5 | x & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int indexBlockInColumnFromColumn(int column, int y) {
/* 175 */     return (y & HEIGHT_MASK) << 10 | column & 0x3FF;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int xFromBlockInColumn(int index) {
/* 185 */     return index & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int yFromBlockInColumn(int index) {
/* 195 */     return index >> 10 & HEIGHT_MASK;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int zFromBlockInColumn(int index) {
/* 205 */     return index >> 5 & 0x1F;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int localCoordinate(long v) {
/* 215 */     return (int)(v & 0x1FL);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int chunkCoordinate(double block) {
/* 225 */     return MathUtil.floor(block) >> 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int chunkCoordinate(int block) {
/* 235 */     return block >> 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int chunkCoordinate(long block) {
/* 245 */     return (int)(block >> 5L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int minBlock(int index) {
/* 255 */     return index << 5;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int maxBlock(int index) {
/* 265 */     return (index << 5) + 31;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isWithinLocalChunk(int x, int z) {
/* 276 */     return (x >= 0 && z >= 0 && x < 32 && z < 32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBorderBlock(int x, int z) {
/* 287 */     return (x == 0 || z == 0 || x == 31 || z == 31);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBorderBlockGlobal(int x, int z) {
/* 298 */     x &= 0x1F;
/* 299 */     z &= 0x1F;
/* 300 */     return isBorderBlock(x, z);
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
/*     */   public static boolean isInsideChunk(int chunkX, int chunkZ, int x, int z) {
/* 313 */     return (chunkCoordinate(x) == chunkX && chunkCoordinate(z) == chunkZ);
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
/*     */   public static boolean isSameChunk(int x0, int z0, int x1, int z1) {
/* 326 */     return (chunkCoordinate(x0) == chunkCoordinate(x1) && chunkCoordinate(z0) == chunkCoordinate(z1));
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
/*     */   public static boolean isSameChunkSection(int x0, int y0, int z0, int x1, int y1, int z1) {
/* 341 */     return (chunkCoordinate(x0) == chunkCoordinate(x1) && 
/* 342 */       chunkCoordinate(y0) == chunkCoordinate(y1) && 
/* 343 */       chunkCoordinate(z0) == chunkCoordinate(z1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isInsideChunkRelative(int x, int z) {
/* 354 */     return ((x & 0x1F) == x && (z & 0x1F) == z);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int xOfChunkIndex(long index) {
/* 365 */     return (int)(index >> 32L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int zOfChunkIndex(long index) {
/* 376 */     return (int)index;
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
/*     */   public static long indexChunk(int x, int z) {
/* 390 */     return x << 32L | z & 0xFFFFFFFFL;
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
/*     */   public static long indexChunkFromBlock(int blockX, int blockZ) {
/* 402 */     return indexChunk(chunkCoordinate(blockX), chunkCoordinate(blockZ));
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
/*     */   public static long indexChunkFromBlock(double blockX, double blockZ) {
/* 415 */     return indexChunkFromBlock(MathUtil.floor(blockX), MathUtil.floor(blockZ));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int worldCoordFromLocalCoord(int chunkCoord, int localCoord) {
/* 426 */     return chunkCoord << 5 | localCoord;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\mat\\util\ChunkUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */