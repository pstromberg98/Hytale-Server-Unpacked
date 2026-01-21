/*    */ package com.hypixel.hytale.server.core.universe.world.spawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
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
/*    */ public class GlobalSpawnProvider
/*    */   implements ISpawnProvider
/*    */ {
/*    */   @Nonnull
/*    */   public static BuilderCodec<GlobalSpawnProvider> CODEC;
/*    */   private Transform spawnPoint;
/*    */   
/*    */   static {
/* 30 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(GlobalSpawnProvider.class, GlobalSpawnProvider::new).documentation("A spawn provider that provides a single static spawn point for all players.")).append(new KeyedCodec("SpawnPoint", (Codec)Transform.CODEC_DEGREES), (o, i) -> o.spawnPoint = i, o -> o.spawnPoint).documentation("The spawn point for all players to spawn at").add()).build();
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
/*    */   public GlobalSpawnProvider() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GlobalSpawnProvider(@Nonnull Transform spawnPoint) {
/* 51 */     this.spawnPoint = spawnPoint;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transform getSpawnPoint(@Nonnull World world, @Nonnull UUID uuid) {
/* 56 */     return this.spawnPoint.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Transform[] getSpawnPoints() {
/* 62 */     return new Transform[] { this.spawnPoint };
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isWithinSpawnDistance(@Nonnull Vector3d position, double distance) {
/* 67 */     return (position.distanceSquaredTo(this.spawnPoint.getPosition()) < distance * distance);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\spawn\GlobalSpawnProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */