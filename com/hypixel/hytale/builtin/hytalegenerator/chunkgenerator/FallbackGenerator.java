/*    */ package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockStateChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedEntityChunk;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class FallbackGenerator
/*    */   implements ChunkGenerator {
/* 11 */   public static final FallbackGenerator INSTANCE = new FallbackGenerator();
/*    */ 
/*    */   
/*    */   public GeneratedChunk generate(@Nonnull ChunkRequest.Arguments arguments) {
/* 15 */     return new GeneratedChunk(new GeneratedBlockChunk(arguments.index(), arguments.x(), arguments.z()), new GeneratedBlockStateChunk(), new GeneratedEntityChunk(), GeneratedChunk.makeSections());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\chunkgenerator\FallbackGenerator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */