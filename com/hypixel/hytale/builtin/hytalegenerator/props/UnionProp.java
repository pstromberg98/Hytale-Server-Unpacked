/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ public class UnionProp
/*     */   extends Prop {
/*     */   @Nonnull
/*     */   private final List<Prop> props;
/*     */   @Nonnull
/*     */   private final ContextDependency contextDependency;
/*     */   @Nonnull
/*     */   private final Bounds3i readBounds_voxelGrid;
/*     */   @Nonnull
/*     */   private final Bounds3i writeBounds_voxelGrid;
/*     */   
/*     */   public UnionProp(@Nonnull List<Prop> propChain) {
/*  26 */     this.props = new ArrayList<>(propChain);
/*  27 */     this.readBounds_voxelGrid = new Bounds3i();
/*  28 */     this.writeBounds_voxelGrid = new Bounds3i();
/*     */     
/*  30 */     Vector3i writeRange = new Vector3i();
/*  31 */     Vector3i readRange = new Vector3i();
/*  32 */     for (Prop prop : propChain) {
/*  33 */       writeRange = Vector3i.max(writeRange, prop.getContextDependency().getWriteRange());
/*  34 */       readRange = Vector3i.max(readRange, prop.getContextDependency().getReadRange());
/*     */       
/*  36 */       this.readBounds_voxelGrid.encompass(prop.getReadBounds_voxelGrid());
/*  37 */       this.writeBounds_voxelGrid.encompass(prop.getWriteBounds_voxelGrid());
/*     */     } 
/*     */     
/*  40 */     this.contextDependency = new ContextDependency(readRange, writeRange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
/*  48 */     ChainedScanResult scanResult = new ChainedScanResult();
/*  49 */     scanResult.scanResults = new ArrayList<>(this.props.size());
/*  50 */     for (Prop prop : this.props) {
/*  51 */       scanResult.scanResults.add(prop.scan(position, materialSpace, id));
/*     */     }
/*  53 */     return scanResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void place(@Nonnull Prop.Context context) {
/*  58 */     List<ScanResult> scanResults = (ChainedScanResult.cast(context.scanResult)).scanResults;
/*     */     
/*  60 */     for (int i = 0; i < this.props.size(); i++) {
/*  61 */       Prop prop = this.props.get(i);
/*  62 */       Prop.Context childContext = new Prop.Context(context);
/*  63 */       childContext.scanResult = scanResults.get(i);
/*     */       
/*  65 */       prop.place(childContext);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ContextDependency getContextDependency() {
/*  72 */     return this.contextDependency.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNullDecl
/*     */   public Bounds3i getReadBounds_voxelGrid() {
/*  78 */     return this.readBounds_voxelGrid;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getWriteBounds_voxelGrid() {
/*  84 */     return this.writeBounds_voxelGrid;
/*     */   }
/*     */   
/*     */   private static class ChainedScanResult implements ScanResult {
/*     */     List<ScanResult> scanResults;
/*     */     
/*     */     @Nonnull
/*     */     public static ChainedScanResult cast(ScanResult scanResult) {
/*  92 */       if (!(scanResult instanceof ChainedScanResult)) {
/*  93 */         throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
/*     */       }
/*  95 */       return (ChainedScanResult)scanResult;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isNegative() {
/* 100 */       return (this.scanResults == null || this.scanResults.isEmpty());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\UnionProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */