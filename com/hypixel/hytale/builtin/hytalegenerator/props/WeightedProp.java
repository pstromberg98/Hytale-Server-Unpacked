/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import java.util.Random;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ 
/*     */ public class WeightedProp
/*     */   extends Prop
/*     */ {
/*     */   @Nonnull
/*     */   private final WeightedMap<Prop> props;
/*     */   @Nonnull
/*     */   private final ContextDependency contextDependency;
/*     */   @Nonnull
/*     */   private final Bounds3i readBounds_voxelGrid;
/*     */   @Nonnull
/*     */   private final Bounds3i writeBounds_voxelGrid;
/*     */   @Nonnull
/*     */   private final SeedGenerator seedGenerator;
/*     */   
/*     */   public WeightedProp(@Nonnull WeightedMap<Prop> props, int seed) {
/*  31 */     this.props = new WeightedMap(props);
/*  32 */     this.readBounds_voxelGrid = new Bounds3i();
/*  33 */     this.writeBounds_voxelGrid = new Bounds3i();
/*  34 */     this.seedGenerator = new SeedGenerator(seed);
/*     */     
/*  36 */     Vector3i writeRange = new Vector3i();
/*  37 */     Vector3i readRange = new Vector3i();
/*  38 */     for (Prop prop : this.props.allElements()) {
/*  39 */       writeRange = Vector3i.max(writeRange, prop.getContextDependency().getWriteRange());
/*  40 */       readRange = Vector3i.max(readRange, prop.getContextDependency().getReadRange());
/*     */       
/*  42 */       this.readBounds_voxelGrid.encompass(prop.getReadBounds_voxelGrid());
/*  43 */       this.writeBounds_voxelGrid.encompass(prop.getWriteBounds_voxelGrid());
/*     */     } 
/*     */     
/*  46 */     this.contextDependency = new ContextDependency(readRange, writeRange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
/*  54 */     if (this.props.size() == 0) {
/*  55 */       return new PickedScanResult();
/*     */     }
/*  57 */     Random rand = new Random(this.seedGenerator.seedAt(position.x, position.y, position.z));
/*  58 */     Prop pickedProp = (Prop)this.props.pick(rand);
/*  59 */     ScanResult scanResult = pickedProp.scan(position, materialSpace, id);
/*     */     
/*  61 */     PickedScanResult pickedScanResult = new PickedScanResult();
/*  62 */     pickedScanResult.prop = pickedProp;
/*  63 */     pickedScanResult.scanResult = scanResult;
/*     */     
/*  65 */     return pickedScanResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void place(@Nonnull Prop.Context context) {
/*  70 */     if (context.scanResult.isNegative()) {
/*     */       return;
/*     */     }
/*  73 */     PickedScanResult pickedScanResult = PickedScanResult.cast(context.scanResult);
/*     */     
/*  75 */     Prop.Context childContext = new Prop.Context(context);
/*  76 */     childContext.scanResult = pickedScanResult.scanResult;
/*  77 */     pickedScanResult.prop.place(childContext);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ContextDependency getContextDependency() {
/*  83 */     return this.contextDependency.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNullDecl
/*     */   public Bounds3i getReadBounds_voxelGrid() {
/*  89 */     return this.readBounds_voxelGrid;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getWriteBounds_voxelGrid() {
/*  95 */     return this.writeBounds_voxelGrid;
/*     */   }
/*     */   
/*     */   private static class PickedScanResult implements ScanResult {
/*     */     ScanResult scanResult;
/*     */     Prop prop;
/*     */     
/*     */     @Nonnull
/*     */     public static PickedScanResult cast(ScanResult scanResult) {
/* 104 */       if (!(scanResult instanceof PickedScanResult)) {
/* 105 */         throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
/*     */       }
/* 107 */       return (PickedScanResult)scanResult;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isNegative() {
/* 112 */       return (this.scanResult == null || this.scanResult.isNegative());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\WeightedProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */