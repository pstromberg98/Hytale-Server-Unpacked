/*    */ package com.hypixel.hytale.server.core.universe.world.spawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.math.util.ChunkUtil;
/*    */ import com.hypixel.hytale.math.util.MathUtil;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*    */ import java.util.UUID;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
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
/*    */ public class FitToHeightMapSpawnProvider
/*    */   implements ISpawnProvider
/*    */ {
/*    */   @Nonnull
/*    */   public static BuilderCodec<FitToHeightMapSpawnProvider> CODEC;
/*    */   private ISpawnProvider spawnProvider;
/*    */   
/*    */   static {
/* 34 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(FitToHeightMapSpawnProvider.class, FitToHeightMapSpawnProvider::new).documentation("A spawn provider that takes a spawn point from another provider and attempts to fit it to the heightmap of the world whenever the spawn point would place the player out of bounds.")).append(new KeyedCodec("SpawnProvider", (Codec)ISpawnProvider.CODEC), (o, i) -> o.spawnProvider = i, o -> o.spawnProvider).documentation("The target spawn provider to take the initial spawn point from.").add()).build();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected FitToHeightMapSpawnProvider() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FitToHeightMapSpawnProvider(@Nonnull ISpawnProvider spawnProvider) {
/* 55 */     this.spawnProvider = spawnProvider;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Transform getSpawnPoint(@Nonnull World world, @Nonnull UUID uuid) {
/* 61 */     Transform spawnPoint = this.spawnProvider.getSpawnPoint(world, uuid);
/* 62 */     Vector3d position = spawnPoint.getPosition();
/*    */     
/* 64 */     if (position.getY() < 0.0D) {
/* 65 */       WorldChunk worldChunk = world.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(position.getX(), position.getZ()));
/* 66 */       if (worldChunk != null) {
/* 67 */         int x = MathUtil.floor(position.getX());
/* 68 */         int z = MathUtil.floor(position.getZ());
/* 69 */         position.setY((worldChunk.getHeight(x, z) + 1));
/*    */       } 
/*    */     } 
/*    */     
/* 73 */     return spawnPoint;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transform[] getSpawnPoints() {
/* 78 */     return this.spawnProvider.getSpawnPoints();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isWithinSpawnDistance(@Nonnull Vector3d position, double distance) {
/* 83 */     return this.spawnProvider.isWithinSpawnDistance(position, distance);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\spawn\FitToHeightMapSpawnProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */