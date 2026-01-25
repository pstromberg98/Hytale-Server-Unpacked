/*    */ package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;
/*    */ 
/*    */ 
/*    */ public final class ChunkRequest extends Record {
/*    */   @Nonnull
/*    */   private final GeneratorProfile generatorProfile;
/*    */   @Nonnull
/*    */   private final Arguments arguments;
/*    */   
/* 10 */   public ChunkRequest(@Nonnull GeneratorProfile generatorProfile, @Nonnull Arguments arguments) { this.generatorProfile = generatorProfile; this.arguments = arguments; } public final String toString() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #10	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/* 10 */     //   0	7	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest; } @Nonnull public GeneratorProfile generatorProfile() { return this.generatorProfile; } public final int hashCode() { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #10	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest; } public final boolean equals(Object o) { // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #10	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest;
/* 10 */     //   0	8	1	o	Ljava/lang/Object; } @Nonnull public Arguments arguments() { return this.arguments; }
/*    */ 
/*    */   
/*    */   public static final class GeneratorProfile
/*    */   {
/*    */     @Nonnull
/*    */     private final String worldStructureName;
/*    */     private int seed;
/*    */     
/*    */     public GeneratorProfile(@Nonnull String worldStructureName, int seed) {
/* 20 */       this.worldStructureName = worldStructureName;
/* 21 */       this.seed = seed;
/*    */     }
/*    */     @Nonnull
/*    */     public String worldStructureName() {
/* 25 */       return this.worldStructureName;
/*    */     } public int seed() {
/* 27 */       return this.seed;
/*    */     } public void setSeed(int seed) {
/* 29 */       this.seed = seed;
/*    */     }
/*    */     
/*    */     public boolean equals(Object obj) {
/* 33 */       if (obj == this) return true; 
/* 34 */       if (obj == null || obj.getClass() != getClass()) return false; 
/* 35 */       GeneratorProfile that = (GeneratorProfile)obj;
/* 36 */       return (Objects.equals(this.worldStructureName, that.worldStructureName) && this.seed == that.seed);
/*    */     }
/*    */ 
/*    */     
/*    */     public int hashCode() {
/* 41 */       return Objects.hash(new Object[] { this.worldStructureName, Integer.valueOf(this.seed) });
/*    */     }
/*    */ 
/*    */     
/*    */     public String toString() {
/* 46 */       return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", seed=" + this.seed + "]";
/*    */     } }
/*    */   public static final class Arguments extends Record { private final int seed; private final long index; private final int x;
/*    */     private final int z;
/*    */     @Nullable
/*    */     private final LongPredicate stillNeeded;
/*    */     
/* 53 */     public Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) { this.seed = seed; this.index = index; this.x = x; this.z = z; this.stillNeeded = stillNeeded; } public final String toString() { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments;)Ljava/lang/String;
/*    */       //   6: areturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #53	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments; } public final int hashCode() { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments;)I
/*    */       //   6: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #53	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments; } public final boolean equals(Object o) { // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: aload_1
/*    */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments;Ljava/lang/Object;)Z
/*    */       //   7: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #53	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	8	0	this	Lcom/hypixel/hytale/builtin/hytalegenerator/chunkgenerator/ChunkRequest$Arguments;
/* 53 */       //   0	8	1	o	Ljava/lang/Object; } public int seed() { return this.seed; } public long index() { return this.index; } public int x() { return this.x; } public int z() { return this.z; } @Nullable public LongPredicate stillNeeded() { return this.stillNeeded; }
/*    */      }
/*    */ 
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\chunkgenerator\ChunkRequest.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */