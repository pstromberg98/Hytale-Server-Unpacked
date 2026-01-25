/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props.prefab;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.BlockMask;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.SpaceSize;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor.ContextDependency;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.WeightedMap;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.ArrayVoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.framework.math.SeedGenerator;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.FluidMaterial;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.newsystem.views.EntityContainer;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.patterns.Pattern;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.Prop;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.ScanResult;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.Directionality;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPosition;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.RotatedPositionsScanResult;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.directionality.StaticDirectionality;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.props.entity.EntityPlacementData;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.scanners.OriginScanner;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.scanners.Scanner;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabRotation;
/*     */ import com.hypixel.hytale.server.core.prefab.PrefabWeights;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.function.Function;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PrefabProp
/*     */   extends Prop
/*     */ {
/*     */   private final WeightedMap<List<PrefabBuffer>> prefabPool;
/*     */   private final Scanner scanner;
/*     */   private ContextDependency contextDependency;
/*     */   private final MaterialCache materialCache;
/*     */   private final SeedGenerator seedGenerator;
/*     */   private final BlockMask materialMask;
/*     */   private final Directionality directionality;
/*     */   private final Bounds3i readBounds_voxelGrid;
/*     */   private final Bounds3i writeBounds_voxelGrid;
/*     */   private final Bounds3i prefabBounds_voxelGrid;
/*     */   private final List<PrefabProp> childProps;
/*     */   private final List<RotatedPosition> childPositions;
/*     */   private final Function<String, List<PrefabBuffer>> childPrefabLoader;
/*     */   private final Scanner moldingScanner;
/*     */   private final Pattern moldingPattern;
/*     */   private final MoldingDirection moldingDirection;
/*     */   private final boolean moldChildren;
/*     */   private final int prefabId;
/*     */   private boolean loadEntities;
/*     */   
/*     */   public PrefabProp(@Nonnull WeightedMap<List<PrefabBuffer>> prefabPool, @Nonnull Scanner scanner, @Nonnull Directionality directionality, @Nonnull MaterialCache materialCache, @Nonnull BlockMask materialMask, @Nonnull PrefabMoldingConfiguration prefabMoldingConfiguration, @Nullable Function<String, List<PrefabBuffer>> childPrefabLoader, @Nonnull SeedBox seedBox, boolean loadEntities) {
/*  78 */     this.prefabId = hashCode();
/*  79 */     this.prefabPool = prefabPool;
/*  80 */     this.scanner = scanner;
/*  81 */     this.directionality = directionality;
/*  82 */     this.materialCache = materialCache;
/*  83 */     this.seedGenerator = new SeedGenerator(((Integer)seedBox.createSupplier().get()).intValue());
/*  84 */     this.materialMask = materialMask;
/*  85 */     this.loadEntities = loadEntities;
/*     */     
/*  87 */     this.childProps = new ArrayList<>();
/*  88 */     this.childPositions = new ArrayList<>();
/*  89 */     this.childPrefabLoader = (childPrefabLoader == null) ? (s -> null) : childPrefabLoader;
/*     */     
/*  91 */     this.moldingScanner = prefabMoldingConfiguration.moldingScanner;
/*  92 */     this.moldingPattern = prefabMoldingConfiguration.moldingPattern;
/*  93 */     this.moldingDirection = prefabMoldingConfiguration.moldingDirection;
/*  94 */     this.moldChildren = prefabMoldingConfiguration.moldChildren;
/*     */ 
/*     */     
/*  97 */     this.contextDependency = new ContextDependency();
/*  98 */     Vector3i readRange = directionality.getReadRangeWith(scanner);
/*  99 */     for (List<PrefabBuffer> prefabList : (Iterable<List<PrefabBuffer>>)prefabPool.allElements()) {
/* 100 */       if (prefabList.isEmpty()) throw new IllegalArgumentException("prefab pool contains empty list");
/*     */       
/* 102 */       for (PrefabBuffer prefab : prefabList) {
/* 103 */         if (prefab == null) throw new IllegalArgumentException("prefab pool contains list with null element");
/*     */         
/* 105 */         PrefabBuffer.PrefabBufferAccessor prefabAccess = prefab.newAccess();
/*     */ 
/*     */         
/* 108 */         PrefabBuffer.ChildPrefab[] childPrefabs = prefabAccess.getChildPrefabs();
/* 109 */         int childId = 0;
/* 110 */         for (PrefabBuffer.ChildPrefab child : childPrefabs) {
/* 111 */           RotatedPosition childPosition = new RotatedPosition(child.getX(), child.getY(), child.getZ(), child.getRotation());
/* 112 */           String childPath = child.getPath().replace('.', '/');
/* 113 */           childPath = childPath.replace("*", "");
/* 114 */           List<PrefabBuffer> childPrefabBuffers = this.childPrefabLoader.apply(childPath);
/*     */           
/* 116 */           WeightedMap<List<PrefabBuffer>> weightedChildPrefabs = new WeightedMap();
/* 117 */           weightedChildPrefabs.add(childPrefabBuffers, 1.0D);
/*     */           
/* 119 */           StaticDirectionality childDirectionality = new StaticDirectionality(child.getRotation(), Pattern.yesPattern());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 128 */           PrefabProp childProp = new PrefabProp(weightedChildPrefabs, (Scanner)OriginScanner.getInstance(), (Directionality)childDirectionality, materialCache, materialMask, this.moldChildren ? prefabMoldingConfiguration : PrefabMoldingConfiguration.none(), childPrefabLoader, seedBox.child(String.valueOf(childId++)), loadEntities);
/*     */           
/* 130 */           this.childProps.add(childProp);
/* 131 */           this.childPositions.add(childPosition);
/*     */         } 
/*     */ 
/*     */         
/* 135 */         Vector3i writeRange = getWriteRange(prefabAccess);
/* 136 */         for (int i = 0; i < this.childPositions.size(); i++) {
/* 137 */           PrefabProp child = this.childProps.get(i);
/* 138 */           Vector3i position = ((RotatedPosition)this.childPositions.get(i)).toVector3i();
/* 139 */           Vector3i childWriteRange = child.getContextDependency().getWriteRange();
/*     */           
/* 141 */           int maxRange = Calculator.max(new int[] { position.x, position.y, position.z });
/* 142 */           maxRange += Calculator.max(new int[] { childWriteRange.x, childWriteRange.y, childWriteRange.z });
/*     */           
/* 144 */           writeRange.x = Math.max(writeRange.x, maxRange);
/* 145 */           writeRange.y = Math.max(writeRange.y, maxRange);
/* 146 */           writeRange.z = Math.max(writeRange.z, maxRange);
/*     */         } 
/*     */         
/* 149 */         ContextDependency contextDependency = new ContextDependency(readRange, writeRange);
/* 150 */         this.contextDependency = ContextDependency.mostOf(this.contextDependency, contextDependency);
/*     */         
/* 152 */         prefabAccess.release();
/*     */       } 
/*     */     } 
/*     */     
/* 156 */     this.readBounds_voxelGrid = this.contextDependency.getReadBounds_voxelGrid();
/* 157 */     this.writeBounds_voxelGrid = this.contextDependency.getWriteBounds_voxelGrid();
/* 158 */     this.prefabBounds_voxelGrid = new Bounds3i();
/* 159 */     this.prefabBounds_voxelGrid.min.assign(this.contextDependency.getWriteRange()).scale(-1);
/* 160 */     this.prefabBounds_voxelGrid.max.assign(this.contextDependency.getWriteRange()).add(Vector3i.ALL_ONES);
/*     */   }
/*     */   
/*     */   private Vector3i getWriteRange(PrefabBuffer.PrefabBufferAccessor prefabAccess) {
/* 164 */     SpaceSize space = new SpaceSize();
/* 165 */     for (PrefabRotation rotation : this.directionality.getPossibleRotations()) {
/* 166 */       Vector3i max = PropPrefabUtil.getMax(prefabAccess, rotation);
/* 167 */       max.add(1, 1, 1);
/* 168 */       Vector3i min = PropPrefabUtil.getMin(prefabAccess, rotation);
/* 169 */       space = SpaceSize.merge(space, new SpaceSize(min, max));
/*     */     } 
/* 171 */     space = SpaceSize.stack(space, this.scanner.readSpaceWith(this.directionality.getGeneralPattern()));
/* 172 */     return space.getRange();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScanResult scan(@Nonnull Vector3i position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull WorkerIndexer.Id id) {
/* 179 */     Scanner.Context scannerContext = new Scanner.Context(position, this.directionality.getGeneralPattern(), materialSpace, id);
/* 180 */     List<Vector3i> validPositions = this.scanner.scan(scannerContext);
/*     */     
/* 182 */     Vector3i patternPosition = new Vector3i();
/* 183 */     Pattern.Context patternContext = new Pattern.Context(patternPosition, materialSpace, id);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     RotatedPositionsScanResult scanResult = new RotatedPositionsScanResult(new ArrayList());
/* 191 */     for (Vector3i validPosition : validPositions) {
/* 192 */       patternPosition.assign(validPosition);
/*     */       
/* 194 */       PrefabRotation rotation = this.directionality.getRotationAt(patternContext);
/* 195 */       if (rotation == null) {
/*     */         continue;
/*     */       }
/* 198 */       scanResult.positions.add(new RotatedPosition(validPosition.x, validPosition.y, validPosition.z, rotation));
/*     */     } 
/*     */     
/* 201 */     return (ScanResult)scanResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public void place(@Nonnull Prop.Context context) {
/* 206 */     if (this.prefabPool.size() == 0)
/*     */       return; 
/* 208 */     List<RotatedPosition> positions = (RotatedPositionsScanResult.cast(context.scanResult)).positions;
/* 209 */     if (positions == null)
/*     */       return; 
/* 211 */     Bounds3i writeSpaceBounds_voxelGrid = context.materialSpace.getBounds();
/*     */     
/* 213 */     for (RotatedPosition position : positions) {
/* 214 */       Bounds3i localPrefabWriteBounds_voxelGrid = this.prefabBounds_voxelGrid.clone().offset(position.toVector3i());
/* 215 */       if (!localPrefabWriteBounds_voxelGrid.intersects(writeSpaceBounds_voxelGrid)) {
/*     */         continue;
/*     */       }
/* 218 */       place(position, context.materialSpace, context.entityBuffer, context.workerId);
/*     */     } 
/*     */   }
/*     */   
/*     */   private PrefabBuffer pickPrefab(Random rand) {
/* 223 */     List<PrefabBuffer> list = (List<PrefabBuffer>)this.prefabPool.pick(rand);
/* 224 */     int randomIndex = rand.nextInt(list.size());
/* 225 */     return list.get(randomIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void place(RotatedPosition position, @Nonnull VoxelSpace<Material> materialSpace, @Nonnull EntityContainer entityBuffer, @Nonnull WorkerIndexer.Id id) {
/*     */     ArrayVoxelSpace arrayVoxelSpace;
/* 232 */     Random random = new Random(this.seedGenerator.seedAt(position.x, position.y, position.z));
/* 233 */     PrefabBufferCall callInstance = new PrefabBufferCall(random, position.rotation);
/* 234 */     PrefabBuffer prefab = pickPrefab(random);
/* 235 */     PrefabBuffer.PrefabBufferAccessor prefabAccess = prefab.newAccess();
/*     */     
/* 237 */     VoxelSpace<Integer> moldingOffsets = null;
/* 238 */     if (this.moldingDirection != MoldingDirection.NONE) {
/* 239 */       int prefabMinX = prefabAccess.getMinX(position.rotation);
/* 240 */       int prefabMinZ = prefabAccess.getMinZ(position.rotation);
/* 241 */       int prefabMaxX = prefabAccess.getMaxX(position.rotation);
/* 242 */       int prefabMaxZ = prefabAccess.getMaxZ(position.rotation);
/* 243 */       int prefabSizeX = prefabMaxX - prefabMinX;
/* 244 */       int prefabSizeZ = prefabMaxZ - prefabMinZ;
/* 245 */       arrayVoxelSpace = new ArrayVoxelSpace(prefabSizeX, 1, prefabSizeZ);
/* 246 */       arrayVoxelSpace.setOrigin(-position.x - prefabMinX, 0, -position.z - prefabMinZ);
/*     */       
/* 248 */       if (this.moldingDirection == MoldingDirection.DOWN || this.moldingDirection == MoldingDirection.UP) {
/* 249 */         Vector3i pointer = new Vector3i(0, position.y, 0);
/* 250 */         Scanner.Context scannerContext = new Scanner.Context(pointer, this.moldingPattern, materialSpace, id);
/*     */         
/* 252 */         for (pointer.x = arrayVoxelSpace.minX(); pointer.x < arrayVoxelSpace.maxX(); pointer.x++) {
/* 253 */           for (pointer.z = arrayVoxelSpace.minZ(); pointer.z < arrayVoxelSpace.maxZ(); pointer.z++) {
/*     */             
/* 255 */             List<Vector3i> scanResult = this.moldingScanner.scan(scannerContext);
/*     */ 
/*     */             
/* 258 */             Integer offset = scanResult.isEmpty() ? null : Integer.valueOf(((Vector3i)scanResult.getFirst()).y - position.y);
/*     */             
/* 260 */             if (offset != null && this.moldingDirection == MoldingDirection.UP) {
/* 261 */               offset = Integer.valueOf(offset.intValue() - 1);
/*     */             }
/* 263 */             arrayVoxelSpace.set(offset, pointer.x, 0, pointer.z);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 272 */       Vector3i prefabPositionVector = position.toVector3i();
/* 273 */       ArrayVoxelSpace arrayVoxelSpace1 = arrayVoxelSpace;
/* 274 */       prefabAccess.forEach(IPrefabBuffer.iterateAllColumns(), (x, y, z, blockId, holder, support, rotation, filler, call, fluidId, fluidLevel) -> { int worldX = position.x + x; int worldY = position.y + y; int worldZ = position.z + z; if (!materialSpace.isInsideSpace(worldX, worldY, worldZ)) return;  SolidMaterial solid = this.materialCache.getSolidMaterial(blockId, support, rotation, filler, (holder != null) ? holder.clone() : null); FluidMaterial fluid = this.materialCache.getFluidMaterial(fluidId, (byte)fluidLevel); Material material = this.materialCache.getMaterial(solid, fluid); int materialHash = material.hashMaterialIds(); if (!this.materialMask.canPlace(materialHash)) return;  if (this.moldingDirection == MoldingDirection.DOWN || this.moldingDirection == MoldingDirection.UP) { Integer offset = null; if (moldingOffsetsFinal.isInsideSpace(worldX, 0, worldZ)) offset = (Integer)moldingOffsetsFinal.getContent(worldX, 0, worldZ);  if (offset == null) return;  worldY += offset.intValue(); }  Material worldMaterial = (Material)materialSpace.getContent(worldX, worldY, worldZ); int worldMaterialHash = worldMaterial.hashMaterialIds(); if (!this.materialMask.canReplace(materialHash, worldMaterialHash)) return;  materialSpace.set(material, worldX, worldY, worldZ); }(cx, cz, entityWrappers, buffer) -> { if (!this.loadEntities) return;  if (entityWrappers == null) return;  for (int i = 0; i < entityWrappers.length; i++) { TransformComponent transformComp = (TransformComponent)entityWrappers[i].getComponent(TransformComponent.getComponentType()); if (transformComp != null) { Vector3d entityPosition = transformComp.getPosition().clone(); buffer.rotation.rotate(entityPosition); Vector3d entityWorldPosition = entityPosition.add(prefabPositionVector); if (entityBuffer.isInsideBuffer((int)entityWorldPosition.x, (int)entityWorldPosition.y, (int)entityWorldPosition.z)) { Holder<EntityStore> entityClone = entityWrappers[i].clone(); transformComp = (TransformComponent)entityClone.getComponent(TransformComponent.getComponentType()); if (transformComp != null) { entityPosition = transformComp.getPosition(); entityPosition.x = entityWorldPosition.x; entityPosition.y = entityWorldPosition.y; entityPosition.z = entityWorldPosition.z; if (!materialSpace.isInsideSpace((int)Math.floor(entityPosition.x), (int)Math.floor(entityPosition.y), (int)Math.floor(entityPosition.z))) return;  EntityPlacementData placementData = new EntityPlacementData(new Vector3i(), PrefabRotation.ROTATION_0, entityClone, this.prefabId); entityBuffer.addEntity(placementData); }  }  }  }  }(x, y, z, path, fitHeightmap, inheritSeed, inheritHeightCondition, weights, rotation, t) -> {  }callInstance);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 361 */     catch (Exception e) {
/* 362 */       String msg = "Couldn't place prefab prop.";
/* 363 */       msg = msg + "\n";
/* 364 */       msg = msg + msg;
/* 365 */       ((HytaleLogger.Api)HytaleLogger.getLogger().atWarning()).log(msg);
/*     */     } finally {
/*     */       
/* 368 */       prefabAccess.release();
/*     */     } 
/*     */ 
/*     */     
/* 372 */     for (int i = 0; i < this.childProps.size(); i++) {
/* 373 */       PrefabProp prop = this.childProps.get(i);
/* 374 */       RotatedPosition childPosition = ((RotatedPosition)this.childPositions.get(i)).getRelativeTo(position);
/* 375 */       Vector3i rotatedChildPositionVec = new Vector3i(childPosition.x, childPosition.y, childPosition.z);
/* 376 */       position.rotation.rotate(rotatedChildPositionVec);
/*     */ 
/*     */       
/* 379 */       if (arrayVoxelSpace != null && arrayVoxelSpace.isInsideSpace(childPosition.x, 0, childPosition.z)) {
/* 380 */         Integer offset = (Integer)arrayVoxelSpace.getContent(childPosition.x, 0, childPosition.z);
/* 381 */         if (offset == null)
/* 382 */           continue;  int y = childPosition.y + offset.intValue();
/* 383 */         childPosition = new RotatedPosition(childPosition.x, y, childPosition.z, childPosition.rotation);
/*     */       } 
/*     */       
/* 386 */       prop.place(childPosition, materialSpace, entityBuffer, id);
/*     */       continue;
/*     */     } 
/*     */   }
/*     */   
/*     */   public ContextDependency getContextDependency() {
/* 392 */     return this.contextDependency;
/*     */   }
/*     */ 
/*     */   
/*     */   @NonNullDecl
/*     */   public Bounds3i getReadBounds_voxelGrid() {
/* 398 */     return this.readBounds_voxelGrid;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getWriteBounds_voxelGrid() {
/* 404 */     return this.writeBounds_voxelGrid;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\prefab\PrefabProp.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */