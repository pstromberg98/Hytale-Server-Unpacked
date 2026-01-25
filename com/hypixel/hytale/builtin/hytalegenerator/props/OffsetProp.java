/*    */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*    */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*    */ import com.hypixel.hytale.math.vector.Vector3i;
/*    */ import javax.annotation.Nonnull;
/*    */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*    */ 
/*    */ public class OffsetProp
/*    */   extends Prop {
/*    */   private final Vector3i offset_voxelGrid;
/*    */   private final Prop childProp;
/*    */   private final Bounds3i readBounds_voxelGrid;
/*    */   private final Bounds3i writeBounds_voxelGrid;
/*    */   private final ContextDependency contextDependency;
/*    */   
/*    */   public OffsetProp(@Nonnull Vector3i offset_voxelGrid, @Nonnull Prop childProp) {
/* 21 */     this.offset_voxelGrid = offset_voxelGrid.clone();
/* 22 */     this.childProp = childProp;
/* 23 */     this.readBounds_voxelGrid = childProp.getReadBounds_voxelGrid().clone().offset(offset_voxelGrid);
/* 24 */     this.writeBounds_voxelGrid = childProp.getWriteBounds_voxelGrid().clone().offset(offset_voxelGrid);
/* 25 */     this.contextDependency = ContextDependency.from(this.readBounds_voxelGrid, this.writeBounds_voxelGrid);
/*    */   }
/*    */ 
/*    */   
/*    */   public ScanResult scan(@NonNullDecl Vector3i position_voxelGrid, @NonNullDecl VoxelSpace<Material> materialSpace, @NonNullDecl WorkerIndexer.Id id) {
/* 30 */     Vector3i childPosition_voxelGrid = position_voxelGrid.clone().add(this.offset_voxelGrid);
/* 31 */     return this.childProp.scan(childPosition_voxelGrid, materialSpace, id);
/*    */   }
/*    */ 
/*    */   
/*    */   public void place(@NonNullDecl Prop.Context context) {
/* 36 */     this.childProp.place(context);
/*    */   }
/*    */ 
/*    */   
/*    */   public ContextDependency getContextDependency() {
/* 41 */     return this.contextDependency;
/*    */   }
/*    */ 
/*    */   
/*    */   @NonNullDecl
/*    */   public Bounds3i getReadBounds_voxelGrid() {
/* 47 */     return this.readBounds_voxelGrid;
/*    */   }
/*    */ 
/*    */   
/*    */   @NonNullDecl
/*    */   public Bounds3i getWriteBounds_voxelGrid() {
/* 53 */     return this.writeBounds_voxelGrid;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\OffsetProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */