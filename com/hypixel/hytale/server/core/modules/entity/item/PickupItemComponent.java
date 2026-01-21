/*     */ package com.hypixel.hytale.server.core.modules.entity.item;
/*     */ 
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.function.Supplier;
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
/*     */ 
/*     */ 
/*     */ public class PickupItemComponent
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   public static final float PICKUP_TRAVEL_TIME_DEFAULT = 0.15F;
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, PickupItemComponent> getComponentType() {
/*  30 */     return EntityModule.get().getPickupItemComponentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  37 */   public static final BuilderCodec<PickupItemComponent> CODEC = BuilderCodec.builder(PickupItemComponent.class, PickupItemComponent::new).build();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Ref<EntityStore> targetRef;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Vector3d startPosition;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private float originalLifeTime;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  57 */   private float lifeTime = 0.15F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean finished = false;
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
/*  77 */     this(targetRef, startPosition, 0.15F);
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
/*  88 */     this.targetRef = targetRef;
/*  89 */     this.startPosition = startPosition;
/*  90 */     this.lifeTime = lifeTime;
/*  91 */     this.originalLifeTime = lifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PickupItemComponent(@Nonnull PickupItemComponent pickupItemComponent) {
/* 100 */     this.targetRef = pickupItemComponent.targetRef;
/* 101 */     this.lifeTime = pickupItemComponent.lifeTime;
/* 102 */     this.startPosition = pickupItemComponent.startPosition;
/* 103 */     this.originalLifeTime = pickupItemComponent.originalLifeTime;
/* 104 */     this.finished = pickupItemComponent.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasFinished() {
/* 111 */     return this.finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFinished(boolean finished) {
/* 120 */     this.finished = finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void decreaseLifetime(float amount) {
/* 129 */     this.lifeTime -= amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getLifeTime() {
/* 136 */     return this.lifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public float getOriginalLifeTime() {
/* 143 */     return this.originalLifeTime;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setInitialLifeTime(float lifeTimeS) {
/* 152 */     this.originalLifeTime = lifeTimeS;
/* 153 */     this.lifeTime = lifeTimeS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Vector3d getStartPosition() {
/* 161 */     return this.startPosition;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Ref<EntityStore> getTargetRef() {
/* 169 */     return this.targetRef;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public PickupItemComponent clone() {
/* 175 */     return new PickupItemComponent(this);
/*     */   }
/*     */   
/*     */   public PickupItemComponent() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\item\PickupItemComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */