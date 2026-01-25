/*     */ package com.hypixel.hytale.builtin.hytalegenerator.conveyor.stagedconveyor;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
/*     */ import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ContextDependency
/*     */ {
/*     */   @Nonnull
/*  20 */   public static ContextDependency EMPTY = new ContextDependency(new Vector3i(), new Vector3i());
/*     */   
/*     */   private final Vector3i readRange;
/*     */   private final Vector3i writeRange;
/*     */   private Vector3i trashRange;
/*     */   private Vector3i externalDependencyRange;
/*     */   private Vector3i positioningRange;
/*     */   
/*     */   public ContextDependency(@Nonnull Vector3i readRange, @Nonnull Vector3i writeRange) {
/*  29 */     this.readRange = readRange.clone();
/*  30 */     this.writeRange = writeRange.clone();
/*  31 */     update();
/*     */   }
/*     */   
/*     */   public ContextDependency() {
/*  35 */     this(new Vector3i(), new Vector3i());
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getReadBounds_voxelGrid() {
/*  40 */     return new Bounds3i(
/*  41 */         getReadRange().clone().scale(-1), 
/*  42 */         getReadRange().clone().add(Vector3i.ALL_ONES));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Bounds3i getWriteBounds_voxelGrid() {
/*  48 */     Vector3i readMin_voxelGrid = getReadRange().scale(-1);
/*  49 */     Vector3i readMax_voxelGrid = getReadRange().add(Vector3i.ALL_ONES);
/*     */     
/*  51 */     Vector3i writeMin_voxelGrid = getWriteRange().scale(-1);
/*  52 */     Vector3i writeMax_voxelGrid = getWriteRange().add(Vector3i.ALL_ONES);
/*     */     
/*  54 */     Bounds3i readBounds_voxelGrid = new Bounds3i(readMin_voxelGrid, readMax_voxelGrid);
/*  55 */     Bounds3i writeBounds_voxelGrid = new Bounds3i(writeMin_voxelGrid, writeMax_voxelGrid);
/*     */     
/*  57 */     writeBounds_voxelGrid.stack(readBounds_voxelGrid);
/*  58 */     return writeBounds_voxelGrid;
/*     */   }
/*     */   
/*     */   private void update() {
/*  62 */     this.trashRange = VectorUtil.fromOperation(this.readRange, this.writeRange, (r, w, retriever) -> 
/*  63 */         (r < 0 || w < 0) ? 0 : (r + w));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     this.externalDependencyRange = VectorUtil.fromOperation(this.readRange, this.writeRange, (r, w, retriever) -> (r < 0) ? -w : Math.max(r, retriever.from(this.trashRange)));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     this.positioningRange = VectorUtil.fromOperation(this.readRange, this.writeRange, (r, w, retriever) -> (r < 0) ? -w : r);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  87 */     this.trashRange.y = 0;
/*  88 */     this.externalDependencyRange.y = 0;
/*  89 */     this.positioningRange.y = 0;
/*  90 */     this.readRange.y = 0;
/*  91 */     this.writeRange.y = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ContextDependency stackOver(@Nonnull ContextDependency other) {
/*  98 */     Vector3i totalRead = new Vector3i();
/*  99 */     Vector3i totalWrite = new Vector3i();
/*     */     
/* 101 */     Vector3i r1 = getReadRange();
/* 102 */     Vector3i w1 = getWriteRange();
/* 103 */     Vector3i r2 = other.getReadRange();
/* 104 */     Vector3i w2 = other.getWriteRange();
/*     */     
/* 106 */     totalRead = VectorUtil.fromOperation(value -> 
/* 107 */         (value.from(r1) < 0 && value.from(r2) < 0) ? -1 : ((value.from(r1) < 0) ? value.from(r2) : ((value.from(r2) < 0) ? value.from(r1) : (value.from(r1) + value.from(w1) + value.from(r2)))));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 120 */     totalWrite = VectorUtil.fromOperation(value -> 
/* 121 */         (value.from(r1) < 0 && value.from(r2) < 0) ? -Math.min(value.from(w1), value.from(w2)) : ((value.from(r1) < 0) ? value.from(w2) : ((value.from(r2) < 0) ? value.from(w1) : value.from(w2))));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 135 */     return new ContextDependency(totalRead, totalWrite);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i getReadRange() {
/* 140 */     return this.readRange.clone();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i getWriteRange() {
/* 145 */     return this.writeRange.clone();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i getTrashRange() {
/* 150 */     return this.trashRange.clone();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i getExternalDependencyRange() {
/* 155 */     return this.externalDependencyRange.clone();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Vector3i getPositioningRange() {
/* 160 */     return this.positioningRange.clone();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Vector3i getRequiredPadOf(@Nonnull List<ContextDependency> dependencies) {
/* 165 */     Vector3i pad = new Vector3i();
/* 166 */     for (ContextDependency dependency : dependencies) {
/* 167 */       pad.add(dependency.getExternalDependencyRange());
/*     */     }
/* 169 */     return pad;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Map<Integer, ContextDependency> cloneMap(@Nonnull Map<Integer, ContextDependency> map) {
/* 174 */     HashMap<Integer, ContextDependency> out = new HashMap<>(map.size());
/* 175 */     map.forEach((k, v) -> out.put(k, v.clone()));
/* 176 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static Map<Integer, ContextDependency> stackMaps(@Nonnull Map<Integer, ContextDependency> under, @Nonnull Map<Integer, ContextDependency> over) {
/* 182 */     Map<Integer, ContextDependency> out = new HashMap<>();
/* 183 */     for (Map.Entry<Integer, ContextDependency> entry : over.entrySet()) {
/* 184 */       if (!under.containsKey(entry.getKey())) {
/*     */         
/* 186 */         out.put(entry.getKey(), entry.getValue());
/*     */         
/*     */         continue;
/*     */       } 
/* 190 */       out.put(entry.getKey(), ((ContextDependency)entry.getValue()).stackOver(under.get(entry.getKey())));
/*     */     } 
/*     */ 
/*     */     
/* 194 */     for (Map.Entry<Integer, ContextDependency> entry : under.entrySet()) {
/* 195 */       if (!over.containsKey(entry.getKey())) {
/* 196 */         out.put(entry.getKey(), entry.getValue());
/*     */       }
/*     */     } 
/* 199 */     return out;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static ContextDependency mostOf(@Nonnull List<ContextDependency> dependencies) {
/* 204 */     ContextDependency out = EMPTY;
/* 205 */     for (ContextDependency d : dependencies) {
/* 206 */       out = mostOf(out, d);
/*     */     }
/* 208 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static ContextDependency mostOf(@Nonnull ContextDependency a, @Nonnull ContextDependency b) {
/* 214 */     Vector3i read = Vector3i.max(a.readRange, b.readRange);
/* 215 */     Vector3i write = Vector3i.max(a.writeRange, b.writeRange);
/*     */     
/* 217 */     return new ContextDependency(read, write);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static ContextDependency from(@Nonnull Bounds3i readBounds, @Nonnull Bounds3i writeBounds) {
/* 222 */     return new ContextDependency(rangeFromBounds(readBounds), rangeFromBounds(writeBounds));
/*     */   }
/*     */   
/*     */   private static Vector3i rangeFromBounds(@Nonnull Bounds3i readBounds) {
/* 226 */     Vector3i readRange = new Vector3i();
/* 227 */     readRange.x = Math.max(Math.abs(readBounds.min.x), Math.abs(readBounds.max.x - 1));
/* 228 */     readRange.y = Math.max(Math.abs(readBounds.min.y), Math.abs(readBounds.max.y - 1));
/* 229 */     readRange.z = Math.max(Math.abs(readBounds.min.z), Math.abs(readBounds.max.z - 1));
/* 230 */     return readRange;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ContextDependency clone() {
/* 236 */     return new ContextDependency(this.readRange, this.writeRange);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\conveyor\stagedconveyor\ContextDependency.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */