/*    */ package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class GeneratorProfile
/*    */ {
/*    */   @Nonnull
/*    */   private final String worldStructureName;
/*    */   private int seed;
/*    */   
/*    */   public GeneratorProfile(@Nonnull String worldStructureName, int seed) {
/* 20 */     this.worldStructureName = worldStructureName;
/* 21 */     this.seed = seed;
/*    */   }
/*    */   @Nonnull
/*    */   public String worldStructureName() {
/* 25 */     return this.worldStructureName;
/*    */   } public int seed() {
/* 27 */     return this.seed;
/*    */   } public void setSeed(int seed) {
/* 29 */     this.seed = seed;
/*    */   }
/*    */   
/*    */   public boolean equals(Object obj) {
/* 33 */     if (obj == this) return true; 
/* 34 */     if (obj == null || obj.getClass() != getClass()) return false; 
/* 35 */     GeneratorProfile that = (GeneratorProfile)obj;
/* 36 */     return (Objects.equals(this.worldStructureName, that.worldStructureName) && this.seed == that.seed);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 41 */     return Objects.hash(new Object[] { this.worldStructureName, Integer.valueOf(this.seed) });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 46 */     return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", seed=" + this.seed + "]";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\chunkgenerator\ChunkRequest$GeneratorProfile.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */