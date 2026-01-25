/*    */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*    */ 
/*    */ public class QueueProp
/*    */   extends Prop {
/*    */   @Nonnull
/*    */   private final List<Prop> props;
/*    */   @Nonnull
/*    */   private final ContextDependency contextDependency;
/*    */   @Nonnull
/*    */   private final Bounds3i readBounds_voxelGrid;
/*    */   @Nonnull
/*    */   private final Bounds3i writeBounds_voxelGrid;
/*    */   
/*    */   public QueueProp(@Nonnull List<Prop> propsQueue) {
/* 26 */     this.props = new ArrayList<>(propsQueue);
/* 27 */     this.readBounds_voxelGrid = new Bounds3i();
/* 28 */     this.writeBounds_voxelGrid = new Bounds3i();
/*    */     
/* 30 */     Vector3i writeRange = new Vector3i();
/* 31 */     Vector3i readRange = new Vector3i();
/* 32 */     for (Prop prop : propsQueue) {
/* 33 */       writeRange = Vector3i.max(writeRange, prop.getContextDependency().getWriteRange());
/* 34 */       readRange = Vector3i.max(readRange, prop.getContextDependency().getReadRange());
/*    */       
/* 36 */       this.readBounds_voxelGrid.encompass(prop.getReadBounds_voxelGrid());
/* 37 */       this.writeBounds_voxelGrid.encompass(prop.getWriteBounds_voxelGrid());
/*    */     } 
/*    */     
/* 40 */     this.contextDependency = new ContextDependency(readRange, writeRange);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
/* 48 */     QueueScanResult queueScanResult = new QueueScanResult();
/* 49 */     for (Prop prop : this.props) {
/* 50 */       ScanResult propScanResult = prop.scan(position, materialSpace, id);
/* 51 */       if (propScanResult.isNegative())
/*    */         continue; 
/* 53 */       queueScanResult.propScanResult = propScanResult;
/* 54 */       queueScanResult.prop = prop;
/* 55 */       return queueScanResult;
/*    */     } 
/* 57 */     return queueScanResult;
/*    */   }
/*    */ 
/*    */   
/*    */   public void place(@Nonnull Prop.Context context) {
/* 62 */     QueueScanResult conditionalScanResult = QueueScanResult.cast(context.scanResult);
/* 63 */     if (conditionalScanResult.isNegative())
/* 64 */       return;  conditionalScanResult.prop.place(context);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ContextDependency getContextDependency() {
/* 70 */     return this.contextDependency.clone();
/*    */   }
/*    */ 
/*    */   
/*    */   @NonNullDecl
/*    */   public Bounds3i getReadBounds_voxelGrid() {
/* 76 */     return this.readBounds_voxelGrid;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public Bounds3i getWriteBounds_voxelGrid() {
/* 82 */     return this.writeBounds_voxelGrid;
/*    */   }
/*    */   
/*    */   private static class QueueScanResult implements ScanResult {
/*    */     ScanResult propScanResult;
/*    */     Prop prop;
/*    */     
/*    */     @Nonnull
/*    */     public static QueueScanResult cast(ScanResult scanResult) {
/* 91 */       if (!(scanResult instanceof QueueScanResult)) {
/* 92 */         throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
/*    */       }
/* 94 */       return (QueueScanResult)scanResult;
/*    */     }
/*    */ 
/*    */     
/*    */     public boolean isNegative() {
/* 99 */       return (this.propScanResult == null || this.propScanResult.isNegative());
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\QueueProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */