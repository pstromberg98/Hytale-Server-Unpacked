/*    */ package com.hypixel.hytale.server.worldgen.prefab;
/*    */ 
/*    */ import com.hypixel.hytale.math.util.FastRandom;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateCondition;
/*    */ import com.hypixel.hytale.procedurallib.condition.DefaultCoordinateRndCondition;
/*    */ import com.hypixel.hytale.procedurallib.condition.ICoordinateCondition;
/*    */ import com.hypixel.hytale.procedurallib.condition.ICoordinateRndCondition;
/*    */ import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
/*    */ import com.hypixel.hytale.server.worldgen.chunk.ChunkGeneratorExecution;
/*    */ import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabSupplier;
/*    */ import com.hypixel.hytale.server.worldgen.util.condition.BlockMaskCondition;
/*    */ import java.util.Random;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PrefabPasteBuffer
/*    */   extends PrefabBufferCall
/*    */ {
/*    */   @Nullable
/*    */   public ChunkGeneratorExecution execution;
/* 31 */   public final Vector3i posWorld = new Vector3i();
/* 32 */   public final Vector3i posChunk = new Vector3i();
/* 33 */   public final Random childRandom = (Random)new FastRandom(0L);
/*    */   
/*    */   public int originHeight;
/*    */   public int yOffset;
/*    */   public int seed;
/*    */   public int specificSeed;
/*    */   public boolean fitHeightmap;
/*    */   public boolean deepSearch;
/*    */   public BlockMaskCondition blockMask;
/*    */   public int environmentId;
/*    */   public byte priority;
/*    */   public ICoordinateCondition heightCondition;
/*    */   public ICoordinateRndCondition spawnCondition;
/*    */   @Nullable
/*    */   public WorldGenPrefabSupplier supplier;
/*    */   private int depth;
/*    */   
/*    */   public PrefabPasteBuffer() {
/* 51 */     this.random = (Random)new FastRandom(0L);
/* 52 */     reset();
/*    */   }
/*    */   
/*    */   public void setSeed(int worldSeed, long externalSeed) {
/* 56 */     this.seed = worldSeed;
/* 57 */     this.specificSeed = (int)externalSeed;
/* 58 */     this.random.setSeed(externalSeed);
/* 59 */     this.childRandom.setSeed(externalSeed);
/*    */   }
/*    */   
/*    */   void reset() {
/* 63 */     this.execution = null;
/* 64 */     this.fitHeightmap = false;
/* 65 */     this.deepSearch = false;
/* 66 */     this.blockMask = BlockMaskCondition.DEFAULT_TRUE;
/* 67 */     this.environmentId = Integer.MIN_VALUE;
/* 68 */     this.heightCondition = (ICoordinateCondition)DefaultCoordinateCondition.DEFAULT_TRUE;
/* 69 */     this.spawnCondition = (ICoordinateRndCondition)DefaultCoordinateRndCondition.DEFAULT_TRUE;
/* 70 */     this.supplier = null;
/* 71 */     this.depth = 0;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldgen\prefab\PrefabPasteUtil$PrefabPasteBuffer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */