/*     */ package com.hypixel.hytale.builtin.buildertools.tooloperations;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.packets.buildertools.BuilderToolOnUseInteraction;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LayersOperation
/*     */   extends ToolOperation
/*     */ {
/*     */   private final Vector3i depthDirection;
/*     */   private final int layerOneLength;
/*     */   private final int layerTwoLength;
/*     */   private final boolean enableLayerTwo;
/*     */   private final int layerThreeLength;
/*     */   private final boolean enableLayerThree;
/*     */   private final BlockPattern layerOneBlockPattern;
/*     */   private final BlockPattern layerTwoBlockPattern;
/*     */   private final BlockPattern layerThreeBlockPattern;
/*     */   private final int brushDensity;
/*     */   private final int maxDepthNecessary;
/*     */   private boolean failed;
/*     */   private final int layerTwoDepthEnd;
/*     */   private final int layerThreeDepthEnd;
/*     */   private final boolean skipLayerOne;
/*     */   private final boolean skipLayerTwo;
/*     */   private final boolean skipLayerThree;
/*     */   
/*     */   public LayersOperation(@Nonnull Ref<EntityStore> ref, @Nonnull Player player, @Nonnull BuilderToolOnUseInteraction packet, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*  40 */     super(ref, packet, componentAccessor);
/*     */     
/*  42 */     HeadRotation headRotationComponent = (HeadRotation)componentAccessor.getComponent(ref, HeadRotation.getComponentType());
/*  43 */     assert headRotationComponent != null;
/*     */     
/*  45 */     switch ((String)this.args.tool().get("aDirection")) { case "Up":
/*  46 */         this.depthDirection = Vector3i.UP; break;
/*  47 */       case "Down": this.depthDirection = Vector3i.DOWN; break;
/*  48 */       case "North": this.depthDirection = Vector3i.NORTH; break;
/*  49 */       case "South": this.depthDirection = Vector3i.SOUTH; break;
/*  50 */       case "East": this.depthDirection = Vector3i.EAST; break;
/*  51 */       case "West": this.depthDirection = Vector3i.WEST; break;
/*  52 */       case "Camera": this.depthDirection = headRotationComponent.getAxisDirection(); break;
/*  53 */       default: this.depthDirection = Vector3i.DOWN;
/*     */         break; }
/*     */     
/*  56 */     this.brushDensity = ((Integer)this.args.tool().get("jBrushDensity")).intValue();
/*     */     
/*  58 */     this.layerOneLength = ((Integer)this.args.tool().get("bLayerOneLength")).intValue();
/*  59 */     this.layerTwoLength = ((Integer)this.args.tool().get("eLayerTwoLength")).intValue();
/*  60 */     this.layerThreeLength = ((Integer)this.args.tool().get("hLayerThreeLength")).intValue();
/*     */     
/*  62 */     this.layerOneBlockPattern = (BlockPattern)this.args.tool().get("cLayerOneMaterial");
/*  63 */     this.layerTwoBlockPattern = (BlockPattern)this.args.tool().get("fLayerTwoMaterial");
/*  64 */     this.layerThreeBlockPattern = (BlockPattern)this.args.tool().get("iLayerThreeMaterial");
/*     */     
/*  66 */     this.enableLayerTwo = ((Boolean)this.args.tool().get("dEnableLayerTwo")).booleanValue();
/*  67 */     this.enableLayerThree = ((Boolean)this.args.tool().get("gEnableLayerThree")).booleanValue();
/*     */     
/*  69 */     this.skipLayerOne = ((Boolean)this.args.tool().getOrDefault("kSkipLayerOne", Boolean.valueOf(false))).booleanValue();
/*  70 */     this.skipLayerTwo = ((Boolean)this.args.tool().getOrDefault("lSkipLayerTwo", Boolean.valueOf(false))).booleanValue();
/*  71 */     this.skipLayerThree = ((Boolean)this.args.tool().getOrDefault("mSkipLayerThree", Boolean.valueOf(false))).booleanValue();
/*     */     
/*  73 */     this.maxDepthNecessary = this.layerOneLength + (this.enableLayerTwo ? this.layerTwoLength : 0) + (this.enableLayerThree ? this.layerThreeLength : 0);
/*     */     
/*  75 */     this.layerTwoDepthEnd = this.layerOneLength + this.layerTwoLength;
/*  76 */     this.layerThreeDepthEnd = this.layerTwoDepthEnd + this.layerThreeLength;
/*     */     
/*  78 */     if (this.enableLayerThree && !this.enableLayerTwo) {
/*  79 */       player.sendMessage(Message.translation("server.builderTools.layerOperation.layerTwoRequired"));
/*  80 */       this.failed = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean execute0(int x, int y, int z) {
/*  87 */     if (this.failed) {
/*  88 */       return false;
/*     */     }
/*     */     
/*  91 */     if (this.random.nextInt(100) > this.brushDensity) {
/*  92 */       return true;
/*     */     }
/*     */     
/*  95 */     int currentBlock = this.edit.getBlock(x, y, z);
/*     */     
/*  97 */     if (currentBlock <= 0) {
/*  98 */       return true;
/*     */     }
/*     */     
/* 101 */     if (this.depthDirection.x == 1) {
/* 102 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 103 */         if (this.edit.getBlock(x - i - 1, y, z) <= 0 && attemptSetBlock(x, y, z, i)) return true; 
/*     */       } 
/* 105 */     } else if (this.depthDirection.x == -1) {
/* 106 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 107 */         if (this.edit.getBlock(x + i + 1, y, z) <= 0 && attemptSetBlock(x, y, z, i)) return true; 
/*     */       } 
/* 109 */     } else if (this.depthDirection.y == 1) {
/* 110 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 111 */         if (this.edit.getBlock(x, y - i - 1, z) <= 0 && attemptSetBlock(x, y, z, i)) return true; 
/*     */       } 
/* 113 */     } else if (this.depthDirection.y == -1) {
/* 114 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 115 */         if (this.edit.getBlock(x, y + i + 1, z) <= 0 && attemptSetBlock(x, y, z, i)) return true; 
/*     */       } 
/* 117 */     } else if (this.depthDirection.z == 1) {
/* 118 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 119 */         if (this.edit.getBlock(x, y, z - i - 1) <= 0 && attemptSetBlock(x, y, z, i)) return true; 
/*     */       } 
/* 121 */     } else if (this.depthDirection.z == -1) {
/* 122 */       for (int i = 0; i < this.maxDepthNecessary; i++) {
/* 123 */         if (this.edit.getBlock(x, y, z + i + 1) <= 0 && attemptSetBlock(x, y, z, i)) return true;
/*     */       
/*     */       } 
/*     */     } 
/* 127 */     return true;
/*     */   }
/*     */   
/*     */   public boolean attemptSetBlock(int x, int y, int z, int depth) {
/* 131 */     if (depth < this.layerOneLength) {
/* 132 */       if (!this.skipLayerOne) {
/* 133 */         this.edit.setBlock(x, y, z, this.layerOneBlockPattern.nextBlock(this.random));
/*     */       }
/* 135 */       return true;
/* 136 */     }  if (this.enableLayerTwo && depth < this.layerTwoDepthEnd) {
/* 137 */       if (!this.skipLayerTwo && !this.layerTwoBlockPattern.isEmpty()) {
/* 138 */         this.edit.setBlock(x, y, z, this.layerTwoBlockPattern.nextBlock(this.random));
/*     */       }
/* 140 */       return true;
/* 141 */     }  if (this.enableLayerThree && depth < this.layerThreeDepthEnd) {
/* 142 */       if (!this.skipLayerThree && !this.layerThreeBlockPattern.isEmpty()) {
/* 143 */         this.edit.setBlock(x, y, z, this.layerThreeBlockPattern.nextBlock(this.random));
/*     */       }
/* 145 */       return true;
/*     */     } 
/* 147 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertools\tooloperations\LayersOperation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */