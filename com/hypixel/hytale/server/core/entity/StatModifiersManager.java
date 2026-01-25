/*     */ package com.hypixel.hytale.server.core.entity;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.BrokenPenalties;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
/*     */ import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
/*     */ import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.ints.IntIterator;
/*     */ import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.ints.IntSet;
/*     */ import it.unimi.dsi.fastutil.objects.Object2FloatMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Function;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ public class StatModifiersManager
/*     */ {
/*     */   @Nonnull
/*  36 */   private final AtomicBoolean recalculate = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  42 */   private final IntSet statsToClear = (IntSet)new IntOpenHashSet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRecalculate(boolean value) {
/*  51 */     this.recalculate.set(value);
/*     */   }
/*     */   
/*     */   public void queueEntityStatsToClear(@Nonnull int[] entityStatsToClear) {
/*  55 */     for (int i = 0; i < entityStatsToClear.length; i++) {
/*  56 */       this.statsToClear.add(entityStatsToClear[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void recalculateEntityStatModifiers(@Nonnull Ref<EntityStore> ref, @Nonnull EntityStatMap statMap, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*     */     LivingEntity livingEntity;
/*  63 */     if (!this.recalculate.getAndSet(false))
/*     */       return; 
/*  65 */     if (!this.statsToClear.isEmpty()) {
/*     */       
/*  67 */       IntIterator iterator = this.statsToClear.iterator();
/*  68 */       while (iterator.hasNext()) {
/*  69 */         statMap.minimizeStatValue(EntityStatMap.Predictable.SELF, iterator.nextInt());
/*     */       }
/*     */       
/*  72 */       this.statsToClear.clear();
/*     */     } 
/*     */     
/*  75 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*  76 */     Entity entity = EntityUtils.getEntity(ref, componentAccessor); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*     */     else
/*     */     { return; }
/*  79 */      Inventory inventory = livingEntity.getInventory();
/*     */ 
/*     */     
/*  82 */     Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> effectModifiers = calculateEffectStatModifiers(ref, componentAccessor);
/*  83 */     applyEffectModifiers(statMap, (Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>>)effectModifiers);
/*     */ 
/*     */     
/*  86 */     BrokenPenalties brokenPenalties = world.getGameplayConfig().getItemDurabilityConfig().getBrokenPenalties();
/*  87 */     Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = computeStatModifiers(brokenPenalties, inventory);
/*  88 */     applyStatModifiers(statMap, statModifiers);
/*     */     
/*  90 */     ItemStack itemInHand = inventory.getItemInHand();
/*  91 */     addItemStatModifiers(itemInHand, statMap, "*Weapon_", v -> (v.getWeapon() != null) ? v.getWeapon().getStatModifiers() : null);
/*     */ 
/*     */     
/*  94 */     if (itemInHand == null || itemInHand.getItem().getUtility().isCompatible()) {
/*  95 */       addItemStatModifiers(inventory.getUtilityItem(), statMap, "*Utility_", v -> v.getUtility().getStatModifiers());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> calculateEffectStatModifiers(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 102 */     Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = new Int2ObjectOpenHashMap();
/* 103 */     EffectControllerComponent effectControllerComponent = (EffectControllerComponent)componentAccessor.getComponent(ref, EffectControllerComponent.getComponentType());
/* 104 */     if (effectControllerComponent == null) return statModifiers;
/*     */     
/* 106 */     effectControllerComponent.getActiveEffects().forEach((k, v) -> {
/*     */           if (!v.isInfinite() && v.getRemainingDuration() <= 0.0F)
/*     */             return;  int index = v.getEntityEffectIndex(); EntityEffect effect = (EntityEffect)EntityEffect.getAssetMap().getAsset(index);
/*     */           if (effect == null || effect.getStatModifiers() == null)
/*     */             return; 
/*     */           ObjectIterator<Int2ObjectMap.Entry<StaticModifier[]>> objectIterator = effect.getStatModifiers().int2ObjectEntrySet().iterator();
/*     */           while (objectIterator.hasNext()) {
/*     */             Int2ObjectMap.Entry<StaticModifier[]> entry = objectIterator.next();
/*     */             int entityStatType = entry.getIntKey();
/*     */             for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
/*     */               float value = modifier.getAmount();
/*     */               Object2FloatMap<StaticModifier.CalculationType> statModifierToApply = (Object2FloatMap<StaticModifier.CalculationType>)statModifiers.computeIfAbsent(entityStatType, ());
/*     */               statModifierToApply.mergeFloat(modifier.getCalculationType(), value, Float::sum);
/*     */             } 
/*     */           } 
/*     */         });
/* 122 */     return statModifiers;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void applyEffectModifiers(@Nonnull EntityStatMap statMap, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
/* 127 */     for (int i = 0; i < statMap.size(); i++) {
/* 128 */       Object2FloatMap<StaticModifier.CalculationType> statModifiersForEntityStat = (Object2FloatMap<StaticModifier.CalculationType>)statModifiers.get(i);
/* 129 */       if (statModifiersForEntityStat == null) {
/*     */         
/* 131 */         for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
/* 132 */           statMap.removeModifier(i, calculationType.createKey("Effect"));
/*     */         
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 138 */         for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
/* 139 */           if (!statModifiersForEntityStat.containsKey(calculationType)) {
/* 140 */             statMap.removeModifier(i, calculationType.createKey("Effect"));
/*     */           }
/*     */         } 
/*     */         
/* 144 */         for (ObjectIterator<Object2FloatMap.Entry<StaticModifier.CalculationType>> objectIterator = statModifiersForEntityStat.object2FloatEntrySet().iterator(); objectIterator.hasNext(); ) { Object2FloatMap.Entry<StaticModifier.CalculationType> entry = objectIterator.next();
/* 145 */           StaticModifier.CalculationType calculationType = (StaticModifier.CalculationType)entry.getKey();
/* 146 */           StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, calculationType, entry.getFloatValue());
/* 147 */           statMap.putModifier(i, calculationType.createKey("Effect"), (Modifier)modifier); }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void computeStatModifiers(double brokenPenalty, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers, @Nonnull ItemStack itemInHand, @Nonnull Int2ObjectMap<StaticModifier[]> itemStatModifiers) {
/* 156 */     boolean broken = itemInHand.isBroken();
/* 157 */     for (ObjectIterator<Int2ObjectMap.Entry<StaticModifier[]>> objectIterator = itemStatModifiers.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Int2ObjectMap.Entry<StaticModifier[]> entry = objectIterator.next();
/* 158 */       int entityStatType = entry.getIntKey();
/* 159 */       for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
/* 160 */         float value = modifier.getAmount();
/*     */ 
/*     */         
/* 163 */         if (broken) value = (float)(value * brokenPenalty);
/*     */         
/* 165 */         Object2FloatMap<StaticModifier.CalculationType> statModifierToApply = (Object2FloatMap<StaticModifier.CalculationType>)statModifiers.computeIfAbsent(entityStatType, x -> new Object2FloatOpenHashMap());
/* 166 */         statModifierToApply.mergeFloat(modifier.getCalculationType(), value, Float::sum);
/*     */       }  }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   private static Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> computeStatModifiers(@Nonnull BrokenPenalties brokenPenalties, @Nonnull Inventory inventory) {
/* 175 */     Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers = new Int2ObjectOpenHashMap();
/*     */     
/* 177 */     double armorBrokenPenalty = brokenPenalties.getArmor(0.0D);
/* 178 */     ItemContainer armorContainer = inventory.getArmor(); short i;
/* 179 */     for (i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
/* 180 */       ItemStack armorItemStack = armorContainer.getItemStack(i);
/* 181 */       if (armorItemStack != null) {
/* 182 */         addArmorStatModifiers(armorItemStack, armorBrokenPenalty, statModifiers);
/*     */       }
/*     */     } 
/* 185 */     return (Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>>)statModifiers;
/*     */   }
/*     */   
/*     */   private static void addArmorStatModifiers(@Nonnull ItemStack itemStack, double brokenPenalties, @Nonnull Int2ObjectOpenHashMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
/* 189 */     if (ItemStack.isEmpty(itemStack))
/*     */       return; 
/* 191 */     ItemArmor armorItem = itemStack.getItem().getArmor();
/* 192 */     if (armorItem == null)
/*     */       return; 
/* 194 */     Int2ObjectMap<StaticModifier[]> itemStatModifiers = armorItem.getStatModifiers();
/* 195 */     if (itemStatModifiers == null)
/*     */       return; 
/* 197 */     computeStatModifiers(brokenPenalties, (Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>>)statModifiers, itemStack, itemStatModifiers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void addItemStatModifiers(@Nullable ItemStack itemStack, @Nonnull EntityStatMap entityStatMap, @Nonnull String prefix, @Nonnull Function<Item, Int2ObjectMap<StaticModifier[]>> toStatModifiers) {
/* 204 */     if (ItemStack.isEmpty(itemStack)) {
/* 205 */       clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, null);
/*     */       
/*     */       return;
/*     */     } 
/* 209 */     Int2ObjectMap<StaticModifier[]> itemStatModifiers = toStatModifiers.apply(itemStack.getItem());
/* 210 */     if (itemStatModifiers == null) {
/* 211 */       clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, null);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 216 */     for (ObjectIterator<Int2ObjectMap.Entry<StaticModifier[]>> objectIterator = itemStatModifiers.int2ObjectEntrySet().iterator(); objectIterator.hasNext(); ) { Int2ObjectMap.Entry<StaticModifier[]> entry = objectIterator.next();
/* 217 */       int offset = 0;
/* 218 */       int statIndex = entry.getIntKey();
/* 219 */       for (StaticModifier modifier : (StaticModifier[])entry.getValue()) {
/*     */         
/* 221 */         String key = prefix + prefix;
/* 222 */         offset++;
/*     */         
/* 224 */         Modifier existing = entityStatMap.getModifier(statIndex, key);
/* 225 */         if (existing instanceof StaticModifier) { StaticModifier existingStatic = (StaticModifier)existing;
/* 226 */           if (existingStatic.equals(modifier)) {
/*     */             continue;
/*     */           } }
/*     */ 
/*     */         
/* 231 */         entityStatMap.putModifier(EntityStatMap.Predictable.SELF, statIndex, key, (Modifier)modifier);
/*     */         continue;
/*     */       } 
/* 234 */       clearStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, statIndex, prefix, offset); }
/*     */ 
/*     */     
/* 237 */     clearAllStatModifiers(EntityStatMap.Predictable.SELF, entityStatMap, prefix, itemStatModifiers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void clearAllStatModifiers(@Nonnull EntityStatMap.Predictable predictable, @Nonnull EntityStatMap entityStatMap, @Nonnull String prefix, @Nullable Int2ObjectMap<StaticModifier[]> excluding) {
/* 244 */     for (int i = 0; i < entityStatMap.size(); i++) {
/* 245 */       if (excluding == null || !excluding.containsKey(i)) {
/* 246 */         clearStatModifiers(predictable, entityStatMap, i, prefix, 0);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void clearStatModifiers(@Nonnull EntityStatMap.Predictable predictable, @Nonnull EntityStatMap entityStatMap, int statIndex, @Nonnull String prefix, int offset) {
/*     */     String key;
/*     */     do {
/* 257 */       key = prefix + prefix;
/* 258 */       offset++;
/* 259 */     } while (entityStatMap.removeModifier(predictable, statIndex, key) != null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void applyStatModifiers(@Nonnull EntityStatMap statMap, @Nonnull Int2ObjectMap<Object2FloatMap<StaticModifier.CalculationType>> statModifiers) {
/* 267 */     for (int i = 0; i < statMap.size(); i++) {
/* 268 */       Object2FloatMap<StaticModifier.CalculationType> statModifiersForEntityStat = (Object2FloatMap<StaticModifier.CalculationType>)statModifiers.get(i);
/* 269 */       if (statModifiersForEntityStat == null) {
/*     */ 
/*     */         
/* 272 */         for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
/* 273 */           statMap.removeModifier(i, calculationType.createKey("Armor"));
/*     */         
/*     */         }
/*     */       }
/*     */       else {
/*     */         
/* 279 */         for (StaticModifier.CalculationType calculationType : StaticModifier.CalculationType.values()) {
/* 280 */           if (!statModifiersForEntityStat.containsKey(calculationType)) {
/* 281 */             statMap.removeModifier(i, calculationType.createKey("Armor"));
/*     */           }
/*     */         } 
/*     */         
/* 285 */         for (ObjectIterator<Object2FloatMap.Entry<StaticModifier.CalculationType>> objectIterator = statModifiersForEntityStat.object2FloatEntrySet().iterator(); objectIterator.hasNext(); ) { Object2FloatMap.Entry<StaticModifier.CalculationType> entry = objectIterator.next();
/* 286 */           StaticModifier.CalculationType calculationType = (StaticModifier.CalculationType)entry.getKey();
/* 287 */           StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, calculationType, entry.getFloatValue());
/* 288 */           statMap.putModifier(i, calculationType.createKey("Armor"), (Modifier)modifier); }
/*     */       
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\StatModifiersManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */