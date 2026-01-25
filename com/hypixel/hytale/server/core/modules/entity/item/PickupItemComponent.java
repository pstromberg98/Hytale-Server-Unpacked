/*     */ package com.hypixel.hytale.server.core.modules.entity.item;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PickupItemComponent
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   public static final float PICKUP_TRAVEL_TIME_DEFAULT = 0.15F;
/*     */   private Ref<EntityStore> targetRef;
/*     */   private Vector3d startPosition;
/*     */   private float originalLifeTime;
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, PickupItemComponent> getComponentType() {
/*  29 */     return EntityModule.get().getPickupItemComponentType();
/*     */   }
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
/*  50 */   private float lifeTime = 0.15F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean finished;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PickupItemComponent(@Nonnull Ref<EntityStore> targetRef, @Nonnull Vector3d startPosition) {
/*  70 */     this(targetRef, startPosition, 0.15F);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PickupItemComponent(@Nonnull Ref<EntityStore> targetRef, @Nonnull Vector3d startPosition, float lifeTime) {
/*  81 */     this.targetRef = targetRef;
/*  82 */     this.startPosition = startPosition;
/*  83 */     this.lifeTime = lifeTime;
/*  84 */     this.originalLifeTime = lifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PickupItemComponent(@Nonnull PickupItemComponent pickupItemComponent) {
/*  93 */     this.targetRef = pickupItemComponent.targetRef;
/*  94 */     this.lifeTime = pickupItemComponent.lifeTime;
/*  95 */     this.startPosition = pickupItemComponent.startPosition;
/*  96 */     this.originalLifeTime = pickupItemComponent.originalLifeTime;
/*  97 */     this.finished = pickupItemComponent.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasFinished() {
/* 104 */     return this.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFinished(boolean finished) {
/* 113 */     this.finished = finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decreaseLifetime(float amount) {
/* 122 */     this.lifeTime -= amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getLifeTime() {
/* 129 */     return this.lifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOriginalLifeTime() {
/* 136 */     return this.originalLifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInitialLifeTime(float lifeTimeS) {
/* 145 */     this.originalLifeTime = lifeTimeS;
/* 146 */     this.lifeTime = lifeTimeS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Vector3d getStartPosition() {
/* 154 */     return this.startPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Ref<EntityStore> getTargetRef() {
/* 162 */     return this.targetRef;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public PickupItemComponent clone() {
/* 168 */     return new PickupItemComponent(this);
/*     */   }
/*     */   
/*     */   public PickupItemComponent() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\item\PickupItemComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */