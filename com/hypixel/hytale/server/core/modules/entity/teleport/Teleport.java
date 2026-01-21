/*     */ package com.hypixel.hytale.server.core.modules.entity.teleport;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.math.vector.Transform;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Teleport
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   @Nullable
/*     */   private final World world;
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, Teleport> getComponentType() {
/*  25 */     return EntityModule.get().getTeleportComponentType();
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
/*     */   @Nonnull
/*  37 */   private final Vector3d position = new Vector3d();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  43 */   private final Vector3f rotation = new Vector3f();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Vector3f headRotation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean resetVelocity = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Teleport(@Nullable World world, @Nonnull Vector3d position, @Nonnull Vector3f rotation) {
/*  65 */     this.world = world;
/*  66 */     this.position.assign(position);
/*  67 */     this.rotation.assign(rotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Teleport(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
/*  77 */     this.world = null;
/*  78 */     this.position.assign(position);
/*  79 */     this.rotation.assign(rotation);
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
/*     */   @Nonnull
/*     */   public static Teleport createForPlayer(@Nullable World world, @Nonnull Transform transform) {
/*  93 */     Vector3f headRotation = transform.getRotation();
/*  94 */     Vector3f bodyRotation = new Vector3f(0.0F, headRotation.getYaw(), 0.0F);
/*  95 */     return (new Teleport(world, transform.getPosition(), bodyRotation))
/*  96 */       .setHeadRotation(headRotation);
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
/*     */   @Nonnull
/*     */   public static Teleport createForPlayer(@Nullable World world, @Nonnull Vector3d position, @Nonnull Vector3f rotation) {
/* 113 */     Vector3f headRotation = rotation.clone();
/* 114 */     Vector3f bodyRotation = new Vector3f(0.0F, headRotation.getYaw(), 0.0F);
/* 115 */     return (new Teleport(world, position, bodyRotation))
/* 116 */       .setHeadRotation(headRotation);
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
/*     */   @Nonnull
/*     */   public static Teleport createForPlayer(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
/* 131 */     return createForPlayer(null, position, rotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static Teleport createForPlayer(@Nonnull Transform transform) {
/* 142 */     return createForPlayer((World)null, transform);
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
/*     */   @Nonnull
/*     */   public static Teleport createExact(@Nonnull Vector3d position, @Nonnull Vector3f bodyRotation, @Nonnull Vector3f headRotation) {
/* 159 */     return (new Teleport(position, bodyRotation)).setHeadRotation(headRotation);
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
/*     */   @Nonnull
/*     */   public static Teleport createExact(@Nonnull Vector3d position, @Nonnull Vector3f bodyRotation) {
/* 174 */     return new Teleport(position, bodyRotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPosition(@Nonnull Vector3d position) {
/* 183 */     this.position.assign(position);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRotation(@Nonnull Vector3f rotation) {
/* 192 */     this.rotation.assign(rotation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Teleport setHeadRotation(@Nonnull Vector3f headRotation) {
/* 203 */     this.headRotation = headRotation.clone();
/* 204 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Teleport withoutVelocityReset() {
/* 211 */     this.resetVelocity = false;
/* 212 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public World getWorld() {
/* 220 */     return this.world;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Vector3d getPosition() {
/* 228 */     return this.position;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Vector3f getRotation() {
/* 236 */     return this.rotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Vector3f getHeadRotation() {
/* 244 */     return this.headRotation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isResetVelocity() {
/* 251 */     return this.resetVelocity;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Teleport clone() {
/* 257 */     return new Teleport(this.world, this.position, this.rotation);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\teleport\Teleport.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */