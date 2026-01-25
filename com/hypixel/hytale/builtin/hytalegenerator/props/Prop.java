/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Prop
/*     */ {
/*     */   public abstract ScanResult scan(@Nonnull Vector3i paramVector3i, @Nonnull VoxelSpace<Material> paramVoxelSpace, @Nonnull WorkerIndexer.Id paramId);
/*     */   
/*     */   public abstract void place(@Nonnull Context paramContext);
/*     */   
/*     */   public abstract ContextDependency getContextDependency();
/*     */   
/*     */   @Nonnull
/*     */   public abstract Bounds3i getReadBounds_voxelGrid();
/*     */   
/*     */   @Nonnull
/*     */   public abstract Bounds3i getWriteBounds_voxelGrid();
/*     */   
/*     */   @Nonnull
/*     */   public static Prop noProp() {
/*  35 */     final ScanResult scanResult = new ScanResult()
/*     */       {
/*     */         public boolean isNegative() {
/*  38 */           return true;
/*     */         }
/*     */       };
/*  41 */     final ContextDependency contextDependency = new ContextDependency(new Vector3i(), new Vector3i());
/*  42 */     final Bounds3i zeroBounds_voxelGrid = new Bounds3i();
/*  43 */     return new Prop()
/*     */       {
/*     */         
/*     */         @Nonnull
/*     */         public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id)
/*     */         {
/*  49 */           return scanResult;
/*     */         }
/*     */ 
/*     */         
/*     */         public void place(@Nonnull Prop.Context context) {}
/*     */ 
/*     */         
/*     */         @Nonnull
/*     */         public ContextDependency getContextDependency() {
/*  58 */           return contextDependency;
/*     */         }
/*     */ 
/*     */         
/*     */         @NonNullDecl
/*     */         public Bounds3i getReadBounds_voxelGrid() {
/*  64 */           return zeroBounds_voxelGrid;
/*     */         }
/*     */ 
/*     */         
/*     */         @Nonnull
/*     */         public Bounds3i getWriteBounds_voxelGrid() {
/*  70 */           return zeroBounds_voxelGrid;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Context
/*     */   {
/*     */     public ScanResult scanResult;
/*     */     
/*     */     public VoxelSpace<Material> materialSpace;
/*     */     
/*     */     public EntityContainer entityBuffer;
/*     */     
/*     */     public WorkerIndexer.Id workerId;
/*     */     
/*     */     public double distanceFromBiomeEdge;
/*     */     
/*     */     public Context(@Nonnull ScanResult scanResult, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, WorkerIndexer.Id workerId, double distanceFromBiomeEdge) {
/*  89 */       this.scanResult = scanResult;
/*  90 */       this.materialSpace = materialSpace;
/*  91 */       this.entityBuffer = entityBuffer;
/*  92 */       this.workerId = workerId;
/*  93 */       this.distanceFromBiomeEdge = distanceFromBiomeEdge;
/*     */     }
/*     */     
/*     */     public Context(@Nonnull Context other) {
/*  97 */       this.scanResult = other.scanResult;
/*  98 */       this.materialSpace = other.materialSpace;
/*  99 */       this.entityBuffer = other.entityBuffer;
/* 100 */       this.workerId = other.workerId;
/* 101 */       this.distanceFromBiomeEdge = other.distanceFromBiomeEdge;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\Prop.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */