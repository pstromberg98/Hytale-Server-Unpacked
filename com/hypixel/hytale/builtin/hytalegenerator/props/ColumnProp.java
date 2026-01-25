/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.BlockMask;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.Directionality;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPosition;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPositionsScanResult;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnProp
/*     */   extends Prop
/*     */ {
/*     */   private final int[] yPositions;
/*     */   private final Material[] blocks0;
/*     */   private final Material[] blocks90;
/*     */   private final Material[] blocks180;
/*     */   private final Material[] blocks270;
/*     */   private final BlockMask blockMask;
/*     */   private final Scanner scanner;
/*     */   private final ContextDependency contextDependency;
/*     */   private final Directionality directionality;
/*     */   @Nonnull
/*     */   private final Bounds3i readBounds_voxelGrid;
/*     */   @Nonnull
/*     */   private final Bounds3i writeBounds_voxelGrid;
/*     */   
/*     */   public ColumnProp(@Nonnull List<Integer> propYPositions, @Nonnull List<Material> blocks, @Nonnull BlockMask blockMask, @Nonnull Scanner scanner, @Nonnull Directionality directionality, @Nonnull MaterialCache materialCache) {
/*  47 */     if (propYPositions.size() != blocks.size()) {
/*  48 */       throw new IllegalArgumentException("blocks and positions sizes don't match");
/*     */     }
/*  50 */     this.blockMask = blockMask;
/*     */     
/*  52 */     this.yPositions = new int[propYPositions.size()];
/*  53 */     this.blocks0 = new Material[blocks.size()];
/*  54 */     this.blocks90 = new Material[blocks.size()];
/*  55 */     this.blocks180 = new Material[blocks.size()];
/*  56 */     this.blocks270 = new Material[blocks.size()];
/*  57 */     for (int i = 0; i < this.yPositions.length; i++) {
/*  58 */       this.yPositions[i] = ((Integer)propYPositions.get(i)).intValue();
/*  59 */       this.blocks0[i] = blocks.get(i);
/*  60 */       this.blocks90[i] = new Material(materialCache.getSolidMaterialRotatedY(((Material)blocks.get(i)).solid(), Rotation.Ninety), ((Material)blocks.get(i)).fluid());
/*  61 */       this.blocks180[i] = new Material(materialCache.getSolidMaterialRotatedY(((Material)blocks.get(i)).solid(), Rotation.OneEighty), ((Material)blocks.get(i)).fluid());
/*  62 */       this.blocks270[i] = new Material(materialCache.getSolidMaterialRotatedY(((Material)blocks.get(i)).solid(), Rotation.TwoSeventy), ((Material)blocks.get(i)).fluid());
/*     */     } 
/*     */     
/*  65 */     this.scanner = scanner;
/*  66 */     this.directionality = directionality;
/*     */ 
/*     */     
/*  69 */     SpaceSize writeSpace = new SpaceSize(new Vector3i(0, 0, 0), new Vector3i(1, 0, 1));
/*  70 */     writeSpace = SpaceSize.stack(writeSpace, scanner.readSpaceWith(directionality.getGeneralPattern()));
/*  71 */     Vector3i writeRange = writeSpace.getRange();
/*     */     
/*  73 */     Vector3i readRange = directionality.getReadRangeWith(scanner);
/*     */     
/*  75 */     this.contextDependency = new ContextDependency(readRange, writeRange);
/*  76 */     this.readBounds_voxelGrid = this.contextDependency.getReadBounds_voxelGrid();
/*  77 */     this.writeBounds_voxelGrid = this.contextDependency.getWriteBounds_voxelGrid();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
/*  84 */     Scanner.Context scannerContext = new Scanner.Context(position, this.directionality.getGeneralPattern(), materialSpace, id);
/*  85 */     List<Vector3i> validPositions = this.scanner.scan(scannerContext);
/*     */     
/*  87 */     Vector3i patternPosition = new Vector3i();
/*  88 */     Pattern.Context patternContext = new Pattern.Context(patternPosition, materialSpace, id);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  95 */     RotatedPositionsScanResult scanResult = new RotatedPositionsScanResult(new ArrayList());
/*  96 */     for (Vector3i validPosition : validPositions) {
/*  97 */       patternPosition.assign(validPosition);
/*     */       
/*  99 */       PrefabRotation rotation = this.directionality.getRotationAt(patternContext);
/* 100 */       if (rotation == null) {
/*     */         continue;
/*     */       }
/* 103 */       scanResult.positions.add(new RotatedPosition(validPosition.x, validPosition.y, validPosition.z, rotation));
/*     */     } 
/*     */     
/* 106 */     return (ScanResult)scanResult;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void place(@Nonnull Prop.Context context) {
/* 112 */     List<RotatedPosition> positions = (RotatedPositionsScanResult.cast(context.scanResult)).positions;
/*     */     
/* 114 */     for (RotatedPosition position : positions) {
/* 115 */       place(position, context.materialSpace);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void place(@Nonnull RotatedPosition position, @Nonnull VoxelSpace<Material> materialSpace) {
/* 121 */     PrefabRotation rotation = position.rotation;
/* 122 */     switch (rotation) { default: throw new MatchException(null, null);case ROTATION_0: case ROTATION_90: case ROTATION_180: case ROTATION_270: break; }  Material[] blocks = 
/*     */ 
/*     */ 
/*     */       
/* 126 */       this.blocks270;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     for (int i = 0; i < this.yPositions.length; i++) {
/* 134 */       int y = this.yPositions[i] + position.y;
/* 135 */       Material propBlock = blocks[i];
/*     */       
/* 137 */       if (materialSpace.isInsideSpace(position.x, y, position.z))
/*     */       {
/* 139 */         if (this.blockMask.canPlace(propBlock)) {
/*     */ 
/*     */           
/* 142 */           Material worldMaterial = (Material)materialSpace.getContent(position.x, y, position.z);
/* 143 */           assert worldMaterial != null;
/* 144 */           int worldMaterialHash = worldMaterial.hashMaterialIds();
/*     */           
/* 146 */           if (this.blockMask.canReplace(propBlock.hashMaterialIds(), worldMaterialHash))
/*     */           {
/*     */             
/* 149 */             materialSpace.set(propBlock, position.x, y, position.z); } 
/*     */         }  } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public ContextDependency getContextDependency() {
/* 155 */     return this.contextDependency.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNullDecl
/*     */   public Bounds3i getReadBounds_voxelGrid() {
/* 161 */     return this.readBounds_voxelGrid;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getWriteBounds_voxelGrid() {
/* 167 */     return this.writeBounds_voxelGrid;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\ColumnProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */