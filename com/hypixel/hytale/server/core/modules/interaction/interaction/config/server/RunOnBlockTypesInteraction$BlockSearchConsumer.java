/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*     */ 
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import it.unimi.dsi.fastutil.ints.IntConsumer;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import javax.annotation.Nonnull;
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
/*     */ class BlockSearchConsumer
/*     */   implements IntConsumer
/*     */ {
/*     */   private final int originX;
/*     */   private final int originY;
/*     */   private final int originZ;
/*     */   private final int radiusSquared;
/*     */   private final int maxCount;
/*     */   private final List<Vector3i> picked;
/* 365 */   private int seen = 0;
/*     */   private int chunkWorldX;
/*     */   private int chunkWorldZ;
/*     */   private int sectionBaseY;
/*     */   
/*     */   BlockSearchConsumer(int originX, int originY, int originZ, int radiusSquared, int maxCount) {
/* 371 */     this.originX = originX;
/* 372 */     this.originY = originY;
/* 373 */     this.originZ = originZ;
/* 374 */     this.radiusSquared = radiusSquared;
/* 375 */     this.maxCount = maxCount;
/* 376 */     this.picked = (List<Vector3i>)new ObjectArrayList(maxCount);
/*     */   }
/*     */   
/*     */   void setSection(int chunkWorldX, int chunkWorldZ, int sectionIndex) {
/* 380 */     this.chunkWorldX = chunkWorldX;
/* 381 */     this.chunkWorldZ = chunkWorldZ;
/* 382 */     this.sectionBaseY = sectionIndex * 32;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void accept(int blockIndex) {
/* 388 */     int localX = ChunkUtil.xFromIndex(blockIndex);
/* 389 */     int localY = ChunkUtil.yFromIndex(blockIndex);
/* 390 */     int localZ = ChunkUtil.zFromIndex(blockIndex);
/*     */     
/* 392 */     int worldX = this.chunkWorldX + localX;
/* 393 */     int worldY = this.sectionBaseY + localY;
/* 394 */     int worldZ = this.chunkWorldZ + localZ;
/*     */ 
/*     */     
/* 397 */     int dx = worldX - this.originX;
/* 398 */     int dy = worldY - this.originY;
/* 399 */     int dz = worldZ - this.originZ;
/* 400 */     if (dx * dx + dy * dy + dz * dz > this.radiusSquared) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 405 */     if (this.picked.size() < this.maxCount) {
/* 406 */       this.picked.add(new Vector3i(worldX, worldY, worldZ));
/*     */     } else {
/* 408 */       int j = ThreadLocalRandom.current().nextInt(this.seen + 1);
/* 409 */       if (j < this.maxCount) {
/* 410 */         this.picked.set(j, new Vector3i(worldX, worldY, worldZ));
/*     */       }
/*     */     } 
/* 413 */     this.seen++;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   List<Vector3i> getPickedPositions() {
/* 418 */     return this.picked;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\RunOnBlockTypesInteraction$BlockSearchConsumer.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */