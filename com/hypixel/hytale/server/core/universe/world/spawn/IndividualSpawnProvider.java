/*    */ package com.hypixel.hytale.server.core.universe.world.spawn;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*    */ import com.hypixel.hytale.math.util.HashUtil;
/*    */ import com.hypixel.hytale.math.vector.Transform;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import java.util.UUID;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
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
/*    */ 
/*    */ 
/*    */ public class IndividualSpawnProvider
/*    */   implements ISpawnProvider
/*    */ {
/*    */   @Nonnull
/*    */   public static BuilderCodec<IndividualSpawnProvider> CODEC;
/*    */   private Transform[] spawnPoints;
/*    */   
/*    */   static {
/* 36 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IndividualSpawnProvider.class, IndividualSpawnProvider::new).documentation("A spawn provider that selects a spawn point from a list based on the player being spawned in. This gives random but consistent spawn points for players.")).append(new KeyedCodec("SpawnPoints", (Codec)new ArrayCodec((Codec)Transform.CODEC, x$0 -> new Transform[x$0])), (o, i) -> o.spawnPoints = i, o -> o.spawnPoints).documentation("The list of spawn points to select from.").add()).build();
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
/*    */   public IndividualSpawnProvider() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IndividualSpawnProvider(@Nonnull Transform spawnPoint) {
/* 57 */     this.spawnPoints = new Transform[1];
/* 58 */     this.spawnPoints[0] = spawnPoint;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IndividualSpawnProvider(@Nonnull Transform[] spawnPoints) {
/* 67 */     this.spawnPoints = spawnPoints;
/*    */   }
/*    */ 
/*    */   
/*    */   public Transform getSpawnPoint(@Nonnull World world, @Nonnull UUID uuid) {
/* 72 */     return this.spawnPoints[Math.abs((int)HashUtil.hashUuid(uuid)) % this.spawnPoints.length].clone();
/*    */   }
/*    */ 
/*    */   
/*    */   public Transform[] getSpawnPoints() {
/* 77 */     return this.spawnPoints;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Transform getFirstSpawnPoint() {
/* 82 */     return (this.spawnPoints.length == 0) ? null : this.spawnPoints[0];
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isWithinSpawnDistance(@Nonnull Vector3d position, double distance) {
/* 87 */     double distanceSquared = distance * distance;
/* 88 */     for (Transform point : this.spawnPoints) {
/* 89 */       if (position.distanceSquaredTo(point.getPosition()) < distanceSquared) {
/* 90 */         return true;
/*    */       }
/*    */     } 
/* 93 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\universe\world\spawn\IndividualSpawnProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */