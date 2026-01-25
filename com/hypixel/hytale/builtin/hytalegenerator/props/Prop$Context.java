/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
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
/*     */ public class Context
/*     */ {
/*     */   public ScanResult scanResult;
/*     */   public VoxelSpace<Material> materialSpace;
/*     */   public EntityContainer entityBuffer;
/*     */   public WorkerIndexer.Id workerId;
/*     */   public double distanceFromBiomeEdge;
/*     */   
/*     */   public Context(@Nonnull ScanResult scanResult, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, WorkerIndexer.Id workerId, double distanceFromBiomeEdge) {
/*  89 */     this.scanResult = scanResult;
/*  90 */     this.materialSpace = materialSpace;
/*  91 */     this.entityBuffer = entityBuffer;
/*  92 */     this.workerId = workerId;
/*  93 */     this.distanceFromBiomeEdge = distanceFromBiomeEdge;
/*     */   }
/*     */   
/*     */   public Context(@Nonnull Context other) {
/*  97 */     this.scanResult = other.scanResult;
/*  98 */     this.materialSpace = other.materialSpace;
/*  99 */     this.entityBuffer = other.entityBuffer;
/* 100 */     this.workerId = other.workerId;
/* 101 */     this.distanceFromBiomeEdge = other.distanceFromBiomeEdge;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\Prop$Context.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */