/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.MapCodec;
/*     */ import com.hypixel.hytale.codec.validation.LateValidator;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.system.EcsEvent;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.util.TrigMathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.math.vector.Vector4d;
/*     */ import com.hypixel.hytale.protocol.DamageEffects;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntitySnapshot;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.knockback.KnockbackComponent;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.io.NetworkSerializable;
/*     */ import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
/*     */ import com.hypixel.hytale.server.core.meta.MetaKey;
/*     */ import com.hypixel.hytale.server.core.modules.debug.DebugUtils;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageCalculatorSystems;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageCalculator;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageClass;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.DamageEffects;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.Knockback;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.combat.TargetEntityEffect;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Label;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.Operation;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.operation.OperationsBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.Object2FloatMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.IntStream;
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
/*     */ public class DamageEntityInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*     */   public static final BuilderCodec<DamageEntityInteraction> CODEC;
/*     */   private static final int FAILED_LABEL_INDEX = 0;
/*     */   private static final int SUCCESS_LABEL_INDEX = 1;
/*     */   private static final int BLOCKED_LABEL_INDEX = 2;
/*     */   private static final int ANGLED_LABEL_OFFSET = 3;
/*     */   public static final int ARMOR_RESISTANCE_FLAT_MODIFIER = 0;
/*     */   public static final int ARMOR_RESISTANCE_MULTIPLIER_MODIFIER = 1;
/*     */   
/*     */   static {
/* 139 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DamageEntityInteraction.class, DamageEntityInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Damages the target entity.")).appendInherited(new KeyedCodec("DamageCalculator", (Codec)DamageCalculator.CODEC), (i, a) -> i.damageCalculator = a, i -> i.damageCalculator, (i, parent) -> i.damageCalculator = parent.damageCalculator).add()).appendInherited(new KeyedCodec("DamageEffects", (Codec)DamageEffects.CODEC), (i, o) -> i.damageEffects = o, i -> i.damageEffects, (i, parent) -> i.damageEffects = parent.damageEffects).add()).appendInherited(new KeyedCodec("AngledDamage", (Codec)new ArrayCodec((Codec)AngledDamage.CODEC, x$0 -> new AngledDamage[x$0])), (i, o) -> i.angledDamage = o, i -> i.angledDamage, (i, parent) -> i.angledDamage = parent.angledDamage).add()).appendInherited(new KeyedCodec("TargetedDamage", (Codec)new MapCodec((Codec)TargetedDamage.CODEC, java.util.HashMap::new)), (i, o) -> i.targetedDamage = o, i -> i.targetedDamage, (i, parent) -> i.targetedDamage = parent.targetedDamage).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("EntityStatsOnHit", (Codec)new ArrayCodec((Codec)EntityStatOnHit.CODEC, x$0 -> new EntityStatOnHit[x$0])), (damageEntityInteraction, entityStatOnHit) -> damageEntityInteraction.entityStatsOnHit = entityStatOnHit, damageEntityInteraction -> damageEntityInteraction.entityStatsOnHit, (damageEntityInteraction, parent) -> damageEntityInteraction.entityStatsOnHit = parent.entityStatsOnHit).documentation("EntityStats to apply based on the hits resulting from this interaction.").add()).appendInherited(new KeyedCodec("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.next = s, interaction -> interaction.next, (interaction, parent) -> interaction.next = parent.next).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Failed", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.failed = s, interaction -> interaction.failed, (interaction, parent) -> interaction.failed = parent.failed).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).appendInherited(new KeyedCodec("Blocked", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.blocked = s, interaction -> interaction.blocked, (interaction, parent) -> interaction.blocked = parent.blocked).documentation("The interactions to run when this interaction fails.").addValidatorLate(() -> VALIDATOR_CACHE.getValidator().late()).add()).afterDecode(o -> { String[] keys = o.sortedTargetDamageKeys = (String[])o.targetedDamage.keySet().toArray(()); Arrays.sort((Object[])keys); for (int i = 0; i < keys.length; i++) { String k = keys[i]; ((TargetedDamage)o.targetedDamage.get(k)).index = i; }  })).build();
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 164 */   private static final MetaKey<DamageCalculatorSystems.Sequence> SEQUENTIAL_HITS = META_REGISTRY.registerMetaObject(i -> new DamageCalculatorSystems.Sequence());
/* 165 */   private static final MetaKey<Integer> NEXT_INDEX = META_REGISTRY.registerMetaObject();
/* 166 */   private static final MetaKey<Damage[]> QUEUED_DAMAGE = META_REGISTRY.registerMetaObject();
/*     */   
/*     */   protected DamageCalculator damageCalculator;
/*     */   @Nullable
/*     */   protected DamageEffects damageEffects;
/*     */   protected AngledDamage[] angledDamage;
/*     */   protected EntityStatOnHit[] entityStatsOnHit;
/* 173 */   protected Map<String, TargetedDamage> targetedDamage = Collections.emptyMap();
/*     */ 
/*     */ 
/*     */   
/*     */   protected String[] sortedTargetDamageKeys;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String next;
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String blocked;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected String failed;
/*     */ 
/*     */ 
/*     */   
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 196 */     Ref<EntityStore> targetRef = context.getTargetEntity();
/* 197 */     if (targetRef == null || !targetRef.isValid() || !context.getEntity().isValid()) {
/* 198 */       context.jump(context.getLabel(0));
/* 199 */       (context.getState()).nextLabel = 0;
/* 200 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 204 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 205 */     assert commandBuffer != null;
/*     */     
/* 207 */     if (processDamage(context, (Damage[])context.getInstanceStore().getIfPresentMetaObject(QUEUED_DAMAGE)))
/*     */       return; 
/* 209 */     Ref<EntityStore> ref = context.getOwningEntity();
/* 210 */     Vector4d hit = (Vector4d)context.getMetaStore().getMetaObject(Interaction.HIT_LOCATION);
/*     */     
/* 212 */     Damage.EntitySource source = new Damage.EntitySource(ref);
/* 213 */     attemptEntityDamage0((Damage.Source)source, context, context.getEntity(), targetRef, hit);
/*     */ 
/*     */     
/* 216 */     if (SelectInteraction.SHOW_VISUAL_DEBUG && hit != null) {
/* 217 */       DebugUtils.addSphere(((EntityStore)commandBuffer.getExternalData()).getWorld(), new Vector3d(hit.x, hit.y, hit.z), new Vector3f(1.0F, 0.0F, 0.0F), 0.20000000298023224D, 5.0F);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 223 */     tick0(firstRun, time, type, context, cooldownHandler);
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
/*     */   private boolean processDamage(@Nonnull InteractionContext context, @Nullable Damage[] queuedDamage) {
/* 236 */     if (queuedDamage == null) return false;
/*     */     
/* 238 */     boolean failed = true;
/* 239 */     boolean blocked = false;
/* 240 */     for (Damage queue : queuedDamage) {
/* 241 */       if (!queue.isCancelled()) {
/* 242 */         failed = false;
/*     */       }
/* 244 */       if (((Boolean)queue.getMetaObject(Damage.BLOCKED)).booleanValue()) {
/* 245 */         blocked = true;
/*     */       }
/*     */     } 
/*     */     
/* 249 */     if (failed) {
/* 250 */       context.jump(context.getLabel(0));
/* 251 */       (context.getState()).nextLabel = 0;
/* 252 */       (context.getState()).state = InteractionState.Failed;
/* 253 */     } else if (blocked) {
/* 254 */       context.jump(context.getLabel(2));
/* 255 */       (context.getState()).nextLabel = 2;
/* 256 */       (context.getState()).state = InteractionState.Finished;
/*     */     } else {
/* 258 */       int index = ((Integer)context.getInstanceStore().getMetaObject(NEXT_INDEX)).intValue();
/* 259 */       (context.getState()).nextLabel = index;
/* 260 */       context.jump(context.getLabel(index));
/* 261 */       (context.getState()).state = InteractionState.Finished;
/*     */     } 
/*     */     
/* 264 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void compile(@Nonnull OperationsBuilder builder) {
/* 269 */     Label[] labels = new Label[3 + ((this.angledDamage != null) ? this.angledDamage.length : 0) + this.targetedDamage.size()];
/* 270 */     builder.addOperation((Operation)this, labels);
/* 271 */     Label endLabel = builder.createUnresolvedLabel();
/*     */     
/* 273 */     labels[0] = builder.createLabel();
/* 274 */     if (this.failed != null) Interaction.getInteractionOrUnknown(this.failed).compile(builder); 
/* 275 */     builder.jump(endLabel);
/*     */     
/* 277 */     labels[1] = builder.createLabel();
/* 278 */     if (this.next != null) Interaction.getInteractionOrUnknown(this.next).compile(builder); 
/* 279 */     builder.jump(endLabel);
/*     */     
/* 281 */     labels[2] = builder.createLabel();
/* 282 */     if (this.blocked != null) Interaction.getInteractionOrUnknown(this.blocked).compile(builder); 
/* 283 */     builder.jump(endLabel);
/*     */     
/* 285 */     int offset = 3;
/* 286 */     if (this.angledDamage != null) {
/* 287 */       for (AngledDamage damage : this.angledDamage) {
/* 288 */         labels[offset++] = builder.createLabel();
/* 289 */         String next = damage.next;
/* 290 */         if (next == null) next = this.next; 
/* 291 */         if (next != null) Interaction.getInteractionOrUnknown(next).compile(builder); 
/* 292 */         builder.jump(endLabel);
/*     */       } 
/*     */     }
/*     */     
/* 296 */     if (!this.targetedDamage.isEmpty()) {
/* 297 */       for (String k : this.sortedTargetDamageKeys) {
/* 298 */         TargetedDamage entry = this.targetedDamage.get(k);
/*     */         
/* 300 */         labels[offset++] = builder.createLabel();
/* 301 */         String next = entry.next;
/* 302 */         if (next == null) next = this.next; 
/* 303 */         if (next != null) Interaction.getInteractionOrUnknown(next).compile(builder); 
/* 304 */         builder.jump(endLabel);
/*     */       } 
/*     */     }
/*     */     
/* 308 */     builder.resolveLabel(endLabel);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 313 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 319 */     return (Interaction)new com.hypixel.hytale.protocol.DamageEntityInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 324 */     super.configurePacket(packet);
/* 325 */     com.hypixel.hytale.protocol.DamageEntityInteraction p = (com.hypixel.hytale.protocol.DamageEntityInteraction)packet;
/* 326 */     p.damageEffects = (this.damageEffects != null) ? this.damageEffects.toPacket() : null;
/* 327 */     p.next = Interaction.getInteractionIdOrUnknown(this.next);
/* 328 */     p.failed = Interaction.getInteractionIdOrUnknown(this.failed);
/* 329 */     p.blocked = Interaction.getInteractionIdOrUnknown(this.blocked);
/*     */     
/* 331 */     if (this.angledDamage != null) {
/* 332 */       p.angledDamage = new com.hypixel.hytale.protocol.AngledDamage[this.angledDamage.length];
/* 333 */       for (int i = 0; i < this.angledDamage.length; i++) {
/* 334 */         p.angledDamage[i] = this.angledDamage[i].toAngledDamagePacket();
/*     */       }
/*     */     } 
/*     */     
/* 338 */     if (this.entityStatsOnHit != null) {
/* 339 */       p.entityStatsOnHit = new com.hypixel.hytale.protocol.EntityStatOnHit[this.entityStatsOnHit.length];
/* 340 */       for (int i = 0; i < this.entityStatsOnHit.length; i++) {
/* 341 */         p.entityStatsOnHit[i] = this.entityStatsOnHit[i].toPacket();
/*     */       }
/*     */     } 
/*     */     
/* 345 */     p.targetedDamage = (Map)new Object2ObjectOpenHashMap();
/* 346 */     for (Map.Entry<String, TargetedDamage> e : this.targetedDamage.entrySet()) {
/* 347 */       p.targetedDamage.put(e.getKey(), ((TargetedDamage)e.getValue()).toTargetedDamagePacket());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 353 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/* 359 */     return WaitForDataFrom.None;
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
/*     */   private void attemptEntityDamage0(@Nonnull Damage.Source source, @Nonnull InteractionContext context, @Nonnull Ref<EntityStore> attackerRef, @Nonnull Ref<EntityStore> targetRef, @Nullable Vector4d hit) {
/*     */     Vector3f attackerDirection;
/* 377 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 378 */     assert commandBuffer != null;
/*     */     
/* 380 */     DamageCalculator damageCalculator = this.damageCalculator;
/* 381 */     DamageEffects damageEffects = this.damageEffects;
/*     */     
/* 383 */     EntitySnapshot targetSnapshot = context.getSnapshot(targetRef, (ComponentAccessor)commandBuffer);
/* 384 */     EntitySnapshot attackerSnapshot = context.getSnapshot(attackerRef, (ComponentAccessor)commandBuffer);
/* 385 */     Vector3d targetPos = targetSnapshot.getPosition();
/* 386 */     Vector3d attackerPos = attackerSnapshot.getPosition();
/* 387 */     float angleBetween = TrigMathUtil.atan2(attackerPos.x - targetPos.x, attackerPos.z - targetPos.z);
/*     */     
/* 389 */     int nextLabel = 1;
/*     */     
/* 391 */     if (this.angledDamage != null) {
/* 392 */       float angleBetweenRotation = MathUtil.wrapAngle(angleBetween + 3.1415927F - targetSnapshot.getBodyRotation().getYaw());
/*     */       
/* 394 */       for (int i = 0; i < this.angledDamage.length; i++) {
/* 395 */         AngledDamage angledDamage = this.angledDamage[i];
/* 396 */         if (Math.abs(MathUtil.compareAngle(angleBetweenRotation, angledDamage.angleRad)) < angledDamage.angleDistanceRad) {
/* 397 */           damageCalculator = (angledDamage.damageCalculator == null) ? damageCalculator : angledDamage.damageCalculator;
/* 398 */           damageEffects = (angledDamage.damageEffects == null) ? damageEffects : angledDamage.damageEffects;
/* 399 */           nextLabel = 3 + i;
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/* 405 */     String hitDetail = (String)context.getMetaStore().getIfPresentMetaObject(HIT_DETAIL);
/* 406 */     if (hitDetail != null) {
/*     */       
/* 408 */       TargetedDamage entry = this.targetedDamage.get(hitDetail);
/* 409 */       if (entry != null) {
/* 410 */         damageCalculator = (entry.damageCalculator == null) ? damageCalculator : entry.damageCalculator;
/* 411 */         damageEffects = (entry.damageEffects == null) ? damageEffects : entry.damageEffects;
/* 412 */         nextLabel = entry.index;
/*     */       } 
/*     */     } 
/*     */     
/* 416 */     context.getInstanceStore().putMetaObject(NEXT_INDEX, Integer.valueOf(nextLabel));
/* 417 */     if (damageCalculator == null)
/*     */       return; 
/* 419 */     DynamicMetaStore<Interaction> metaStore = (DynamicMetaStore<Interaction>)context.getMetaStore().getMetaObject(SelectInteraction.SELECT_META_STORE);
/* 420 */     DamageCalculatorSystems.Sequence sequentialHits = (metaStore == null) ? new DamageCalculatorSystems.Sequence() : (DamageCalculatorSystems.Sequence)metaStore.getMetaObject(SEQUENTIAL_HITS);
/* 421 */     Object2FloatMap<DamageCause> damage = damageCalculator.calculateDamage(getRunTime());
/*     */ 
/*     */     
/* 424 */     HeadRotation attackerHeadRotationComponent = (HeadRotation)commandBuffer.getComponent(attackerRef, HeadRotation.getComponentType());
/* 425 */     if (attackerHeadRotationComponent != null) {
/* 426 */       attackerDirection = attackerHeadRotationComponent.getRotation();
/*     */     } else {
/* 428 */       attackerDirection = Vector3f.ZERO;
/*     */     } 
/*     */     
/* 431 */     if (damage != null && !damage.isEmpty()) {
/*     */ 
/*     */ 
/*     */       
/* 435 */       double[] knockbackMultiplier = { 1.0D };
/* 436 */       float[] armorDamageModifiers = { 0.0F, 1.0F };
/*     */       
/* 438 */       calculateKnockbackAndArmorModifiers(damageCalculator.getDamageClass(), damage, targetRef, attackerRef, armorDamageModifiers, knockbackMultiplier, (ComponentAccessor<EntityStore>)commandBuffer);
/*     */       
/* 440 */       KnockbackComponent knockbackComponent = null;
/* 441 */       if (damageEffects != null && damageEffects.getKnockback() != null) {
/* 442 */         knockbackComponent = (KnockbackComponent)commandBuffer.getComponent(targetRef, KnockbackComponent.getComponentType());
/* 443 */         if (knockbackComponent == null) {
/* 444 */           knockbackComponent = new KnockbackComponent();
/* 445 */           commandBuffer.putComponent(targetRef, KnockbackComponent.getComponentType(), (Component)knockbackComponent);
/*     */         } 
/* 447 */         Knockback knockback = damageEffects.getKnockback();
/* 448 */         knockbackComponent.setVelocity(knockback.calculateVector(attackerPos, attackerDirection.getYaw(), targetPos).scale(knockbackMultiplier[0]));
/* 449 */         knockbackComponent.setVelocityType(knockback.getVelocityType());
/* 450 */         knockbackComponent.setVelocityConfig(knockback.getVelocityConfig());
/* 451 */         knockbackComponent.setDuration(knockback.getDuration());
/*     */       } 
/*     */       
/* 454 */       Player attackerPlayerComponent = (Player)commandBuffer.getComponent(attackerRef, Player.getComponentType());
/* 455 */       ItemStack itemInHand = (attackerPlayerComponent == null || attackerPlayerComponent.canApplyItemStackPenalties(attackerRef, (ComponentAccessor)commandBuffer)) ? context.getHeldItem() : null;
/* 456 */       Damage[] hits = DamageCalculatorSystems.queueDamageCalculator(((EntityStore)commandBuffer.getExternalData()).getWorld(), damage, targetRef, context.getCommandBuffer(), source, itemInHand);
/*     */       
/* 458 */       if (hits.length > 0) {
/* 459 */         Damage firstDamage = hits[0];
/* 460 */         DamageCalculatorSystems.DamageSequence seq = new DamageCalculatorSystems.DamageSequence(sequentialHits, damageCalculator);
/* 461 */         seq.setEntityStatOnHit(this.entityStatsOnHit);
/* 462 */         firstDamage.putMetaObject(DamageCalculatorSystems.DAMAGE_SEQUENCE, seq);
/* 463 */         if (damageEffects != null) {
/* 464 */           damageEffects.addToDamage(firstDamage);
/*     */         }
/*     */         
/* 467 */         for (Damage damageEvent : hits) {
/* 468 */           if (knockbackComponent != null) damageEvent.putMetaObject(Damage.KNOCKBACK_COMPONENT, knockbackComponent); 
/* 469 */           float damageValue = damageEvent.getAmount();
/* 470 */           damageValue += armorDamageModifiers[0];
/* 471 */           damageEvent.setAmount(damageValue * Math.max(0.0F, armorDamageModifiers[1]));
/* 472 */           if (hit != null) {
/* 473 */             damageEvent.putMetaObject(Damage.HIT_LOCATION, hit);
/*     */             
/* 475 */             float hitAngleRad = TrigMathUtil.atan2(attackerPos.x - hit.x, attackerPos.z - hit.z);
/* 476 */             hitAngleRad = MathUtil.wrapAngle(hitAngleRad - attackerDirection.getYaw());
/*     */             
/* 478 */             float hitAngleDeg = hitAngleRad * 57.295776F;
/* 479 */             damageEvent.putMetaObject(Damage.HIT_ANGLE, Float.valueOf(hitAngleDeg));
/*     */           } 
/*     */           
/* 482 */           commandBuffer.invoke(targetRef, (EcsEvent)damageEvent);
/*     */         } 
/*     */         
/* 485 */         processDamage(context, hits);
/*     */       } 
/*     */ 
/*     */       
/* 489 */       context.getInstanceStore().putMetaObject(QUEUED_DAMAGE, hits);
/*     */     } 
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
/*     */   private static void calculateKnockbackAndArmorModifiers(@Nonnull DamageClass damageClass, @Nonnull Object2FloatMap<DamageCause> damage, @Nonnull Ref<EntityStore> targetRef, @Nonnull Ref<EntityStore> attackerRef, float[] armorDamageModifiers, double[] knockbackMultiplier, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*     */     LivingEntity livingEntity;
/* 512 */     EffectControllerComponent effectControllerComponent = (EffectControllerComponent)componentAccessor.getComponent(targetRef, EffectControllerComponent.getComponentType());
/*     */ 
/*     */     
/* 515 */     if (effectControllerComponent != null) {
/* 516 */       knockbackMultiplier[0] = IntStream.of(effectControllerComponent
/* 517 */           .getActiveEffectIndexes())
/* 518 */         .mapToObj(i -> (EntityEffect)((IndexedLookupTableAssetMap)EntityEffect.getAssetStore().getAssetMap()).getAsset(i))
/* 519 */         .filter(effect -> (effect != null && effect.getApplicationEffects() != null))
/* 520 */         .mapToDouble(effect -> effect.getApplicationEffects().getKnockbackMultiplier())
/* 521 */         .reduce(1.0D, (a, b) -> a * b);
/*     */     }
/*     */     
/* 524 */     Entity entity = EntityUtils.getEntity(attackerRef, componentAccessor); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; } else { return; }
/* 525 */      Inventory inventory = livingEntity.getInventory();
/* 526 */     if (inventory == null) {
/*     */       return;
/*     */     }
/* 529 */     ItemContainer armorContainer = inventory.getArmor();
/* 530 */     if (armorContainer == null)
/* 531 */       return;  float knockbackEnhancementModifier = 1.0F; short i;
/* 532 */     for (i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
/* 533 */       ItemStack itemStack = armorContainer.getItemStack(i);
/* 534 */       if (itemStack != null && !itemStack.isEmpty()) {
/*     */         
/* 536 */         Item item = itemStack.getItem();
/* 537 */         if (item.getArmor() != null) {
/*     */           
/* 539 */           Map<DamageCause, StaticModifier[]> armorDamageEnhancementMap = item.getArmor().getDamageEnhancementValues();
/*     */           
/* 541 */           for (ObjectIterator<DamageCause> objectIterator = damage.keySet().iterator(); objectIterator.hasNext(); ) { DamageCause damageCause = objectIterator.next();
/* 542 */             if (armorDamageEnhancementMap != null) {
/* 543 */               StaticModifier[] armorDamageEnhancementValue = armorDamageEnhancementMap.get(damageCause);
/* 544 */               if (armorDamageEnhancementValue != null) {
/* 545 */                 for (StaticModifier staticModifier : armorDamageEnhancementValue) {
/* 546 */                   if (staticModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
/* 547 */                     armorDamageModifiers[0] = armorDamageModifiers[0] + staticModifier.getAmount();
/*     */                   } else {
/*     */                     
/* 550 */                     armorDamageModifiers[1] = armorDamageModifiers[1] + staticModifier.getAmount();
/*     */                   } 
/*     */                 } 
/*     */               }
/*     */             } 
/* 555 */             Map<DamageCause, Float> knockbackEnhancements = item.getArmor().getKnockbackEnhancements();
/* 556 */             if (knockbackEnhancements == null)
/* 557 */               continue;  knockbackEnhancementModifier += ((Float)knockbackEnhancements.get(damageCause)).floatValue(); }
/*     */ 
/*     */           
/* 560 */           StaticModifier[] damageClassModifier = (StaticModifier[])item.getArmor().getDamageClassEnhancement().get(damageClass);
/* 561 */           if (damageClassModifier != null)
/* 562 */             for (StaticModifier modifier : damageClassModifier) {
/* 563 */               if (modifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE) {
/* 564 */                 armorDamageModifiers[0] = armorDamageModifiers[0] + modifier.getAmount();
/*     */               } else {
/*     */                 
/* 567 */                 armorDamageModifiers[1] = armorDamageModifiers[1] + modifier.getAmount();
/*     */               } 
/*     */             }  
/*     */         } 
/*     */       } 
/* 572 */     }  knockbackMultiplier[0] = knockbackMultiplier[0] * knockbackEnhancementModifier;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class TargetedDamage
/*     */   {
/*     */     public static final BuilderCodec<TargetedDamage> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     protected int index;
/*     */ 
/*     */ 
/*     */     
/*     */     protected DamageCalculator damageCalculator;
/*     */ 
/*     */ 
/*     */     
/*     */     protected Map<String, TargetEntityEffect> targetEntityEffects;
/*     */ 
/*     */     
/*     */     protected DamageEffects damageEffects;
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     protected String next;
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 604 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TargetedDamage.class, TargetedDamage::new).appendInherited(new KeyedCodec("DamageCalculator", (Codec)DamageCalculator.CODEC), (i, a) -> i.damageCalculator = a, i -> i.damageCalculator, (i, parent) -> i.damageCalculator = parent.damageCalculator).add()).appendInherited(new KeyedCodec("TargetEntityEffects", (Codec)new MapCodec((Codec)TargetEntityEffect.CODEC, java.util.HashMap::new)), (i, map) -> i.targetEntityEffects = map, i -> i.targetEntityEffects, (i, parent) -> i.targetEntityEffects = parent.targetEntityEffects).add()).appendInherited(new KeyedCodec("DamageEffects", (Codec)DamageEffects.CODEC), (i, o) -> i.damageEffects = o, i -> i.damageEffects, (i, parent) -> i.damageEffects = parent.damageEffects).add()).appendInherited(new KeyedCodec("Next", Interaction.CHILD_ASSET_CODEC), (interaction, s) -> interaction.next = s, interaction -> interaction.next, (interaction, parent) -> interaction.next = parent.next).documentation("The interactions to run when this interaction succeeds.").addValidatorLate(() -> Interaction.VALIDATOR_CACHE.getValidator().late()).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public com.hypixel.hytale.protocol.TargetedDamage toTargetedDamagePacket() {
/* 615 */       return new com.hypixel.hytale.protocol.TargetedDamage(this.index, this.damageEffects.toPacket(), Interaction.getInteractionIdOrUnknown(this.next));
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 621 */       return "TargetedDamage{damageCalculator=" + String.valueOf(this.damageCalculator) + ", targetEntityEffects=" + String.valueOf(this.targetEntityEffects) + ", damageEffects=" + String.valueOf(this.damageEffects) + ", next='" + this.next + "'}";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class AngledDamage
/*     */     extends TargetedDamage
/*     */   {
/*     */     public static final BuilderCodec<AngledDamage> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     protected float angleRad;
/*     */ 
/*     */ 
/*     */     
/*     */     protected float angleDistanceRad;
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 644 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(AngledDamage.class, AngledDamage::new, DamageEntityInteraction.TargetedDamage.CODEC).appendInherited(new KeyedCodec("Angle", (Codec)Codec.FLOAT), (o, i) -> o.angleRad = i.floatValue() * 0.017453292F, o -> Float.valueOf(o.angleRad * 57.295776F), (o, p) -> o.angleRad = p.angleRad).add()).appendInherited(new KeyedCodec("AngleDistance", (Codec)Codec.FLOAT), (o, i) -> o.angleDistanceRad = i.floatValue() * 0.017453292F, o -> Float.valueOf(o.angleDistanceRad * 57.295776F), (o, p) -> o.angleDistanceRad = p.angleDistanceRad).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public com.hypixel.hytale.protocol.AngledDamage toAngledDamagePacket() {
/* 651 */       DamageEffects damageEffectsPacket = (this.damageEffects == null) ? null : this.damageEffects.toPacket();
/* 652 */       return new com.hypixel.hytale.protocol.AngledDamage(this.angleRad, this.angleDistanceRad, damageEffectsPacket, Interaction.getInteractionIdOrUnknown(this.next));
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 658 */       return "AngledDamage{angleRad=" + this.angleRad + ", angleDistanceRad=" + this.angleDistanceRad + "} " + super
/*     */ 
/*     */         
/* 661 */         .toString();
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class EntityStatOnHit
/*     */     implements NetworkSerializable<com.hypixel.hytale.protocol.EntityStatOnHit>
/*     */   {
/*     */     public static final BuilderCodec<EntityStatOnHit> CODEC;
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
/*     */     public EntityStatOnHit() {
/* 716 */       this.multipliersPerEntitiesHit = DEFAULT_MULTIPLIERS_PER_ENTITIES_HIT;
/* 717 */       this.multiplierPerExtraEntityHit = 0.05F;
/*     */     } static { CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(EntityStatOnHit.class, EntityStatOnHit::new).appendInherited(new KeyedCodec("EntityStatId", (Codec)Codec.STRING), (entityStatOnHitInteraction, s) -> entityStatOnHitInteraction.entityStatId = s, entityStatOnHitInteraction -> entityStatOnHitInteraction.entityStatId, (entityStatOnHitInteraction, parent) -> entityStatOnHitInteraction.entityStatId = parent.entityStatId).documentation("The id of the EntityStat that will be affected by the interaction.").addValidator(Validators.nonNull()).addValidator(EntityStatType.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec("Amount", (Codec)Codec.FLOAT), (entityStatOnHitInteraction, integer) -> entityStatOnHitInteraction.amount = integer.floatValue(), entityStatOnHitInteraction -> Float.valueOf(entityStatOnHitInteraction.amount), (entityStatOnHitInteraction, parent) -> entityStatOnHitInteraction.amount = parent.amount).documentation("The base amount for a single entity hit.").add()).appendInherited(new KeyedCodec("MultipliersPerEntitiesHit", (Codec)Codec.FLOAT_ARRAY), (entityStatOnHitInteraction, doubles) -> entityStatOnHitInteraction.multipliersPerEntitiesHit = doubles, entityStatOnHitInteraction -> entityStatOnHitInteraction.multipliersPerEntitiesHit, (entityStatOnHitInteraction, parent) -> entityStatOnHitInteraction.multipliersPerEntitiesHit = parent.multipliersPerEntitiesHit).documentation("An array of multipliers corresponding to how much the amount should be multiplied by for each entity hit.").addValidator(Validators.nonEmptyFloatArray()).add()).appendInherited(new KeyedCodec("MultiplierPerExtraEntityHit", (Codec)Codec.FLOAT), (entityStatOnHitInteraction, aDouble) -> entityStatOnHitInteraction.multiplierPerExtraEntityHit = aDouble.floatValue(), entityStatOnHitInteraction -> Float.valueOf(entityStatOnHitInteraction.multiplierPerExtraEntityHit), (entityStatOnHitInteraction, parent) -> entityStatOnHitInteraction.multiplierPerExtraEntityHit = parent.multiplierPerExtraEntityHit).documentation("When the number of entity hit is higher than the number of multipliers defined, the amount will be multiplied by this multiplier for each extra entity hit.").add()).afterDecode(entityStatOnHitInteraction -> {
/*     */             if (entityStatOnHitInteraction.entityStatId == null)
/*     */               return;  entityStatOnHitInteraction.entityStatIndex = EntityStatType.getAssetMap().getIndex(entityStatOnHitInteraction.entityStatId);
/*     */           })).build(); } public static final float[] DEFAULT_MULTIPLIERS_PER_ENTITIES_HIT = new float[] { 1.0F, 0.6F, 0.4F, 0.2F, 0.1F }; public static final float DEFAULT_MULTIPLIER_PER_EXTRA_ENTITY_HIT = 0.05F; protected String entityStatId; public void processEntityStatsOnHit(int hits, @Nonnull EntityStatMap statMap) { float multiplier;
/* 722 */       if (hits == 0) {
/*     */         return;
/*     */       }
/*     */       
/* 726 */       if (hits <= this.multipliersPerEntitiesHit.length) {
/* 727 */         multiplier = this.multipliersPerEntitiesHit[hits - 1];
/*     */       } else {
/* 729 */         multiplier = this.multiplierPerExtraEntityHit;
/*     */       } 
/*     */       
/* 732 */       statMap.addStatValue(EntityStatMap.Predictable.SELF, this.entityStatIndex, multiplier * this.amount); }
/*     */     
/*     */     protected float amount; protected float[] multipliersPerEntitiesHit; protected float multiplierPerExtraEntityHit; private int entityStatIndex;
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 738 */       return "EntityStatOnHit{entityStatId='" + this.entityStatId + "', amount=" + this.amount + ", multipliersPerEntitiesHit=" + 
/*     */ 
/*     */         
/* 741 */         Arrays.toString(this.multipliersPerEntitiesHit) + ", multiplierPerExtraEntityHit=" + this.multiplierPerExtraEntityHit + ", entityStatIndex=" + this.entityStatIndex + "}";
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public com.hypixel.hytale.protocol.EntityStatOnHit toPacket() {
/* 750 */       return new com.hypixel.hytale.protocol.EntityStatOnHit(this.entityStatIndex, this.amount, this.multipliersPerEntitiesHit, this.multiplierPerExtraEntityHit);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\DamageEntityInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */