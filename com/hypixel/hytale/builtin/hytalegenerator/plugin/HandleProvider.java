/*    */ package com.hypixel.hytale.builtin.hytalegenerator.plugin;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkRequest;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
/*    */ import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ public class HandleProvider
/*    */   implements IWorldGenProvider
/*    */ {
/*    */   public static final String ID = "HytaleGenerator";
/*    */   public static final String DEFAULT_WORLD_STRUCTURE_NAME = "Default";
/*    */   @Nonnull
/*    */   private final HytaleGenerator plugin;
/*    */   @Nonnull
/* 19 */   private String worldStructureName = "Default";
/*    */ 
/*    */   
/*    */   public HandleProvider(@Nonnull HytaleGenerator plugin) {
/* 23 */     this.plugin = plugin;
/*    */   }
/*    */   
/*    */   public void setWorldStructureName(@Nullable String worldStructureName) {
/* 27 */     this.worldStructureName = worldStructureName;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public String getWorldStructureName() {
/* 32 */     return this.worldStructureName;
/*    */   }
/*    */ 
/*    */   
/*    */   public IWorldGen getGenerator() throws WorldGenLoadException {
/* 37 */     return new Handle(this.plugin, new ChunkRequest.GeneratorProfile(this.worldStructureName, 0));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\plugin\HandleProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */