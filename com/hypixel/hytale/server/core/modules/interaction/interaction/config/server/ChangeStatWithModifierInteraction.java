/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.codecs.EnumCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
/*    */ import com.hypixel.hytale.server.core.entity.Entity;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*    */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*    */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import it.unimi.dsi.fastutil.ints.Int2FloatMap;
/*    */ import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
/*    */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*    */ import it.unimi.dsi.fastutil.ints.IntIterator;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ChangeStatWithModifierInteraction extends ChangeStatBaseInteraction {
/*    */   static {
/* 33 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChangeStatWithModifierInteraction.class, ChangeStatWithModifierInteraction::new, ChangeStatBaseInteraction.CODEC).documentation("Changes the given stats.")).append(new KeyedCodec("InteractionModifierId", (Codec)new EnumCodec(ItemArmor.InteractionModifierId.class)), (changeStatWithModifierInteraction, s) -> changeStatWithModifierInteraction.interactionModifierId = s, changeStatWithModifierInteraction -> changeStatWithModifierInteraction.interactionModifierId).addValidator(Validators.nonNull()).add()).build();
/*    */   }
/*    */   public static final BuilderCodec<ChangeStatWithModifierInteraction> CODEC;
/*    */   protected ItemArmor.InteractionModifierId interactionModifierId;
/*    */   
/*    */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 39 */     Ref<EntityStore> ref = context.getEntity();
/* 40 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 41 */     assert commandBuffer != null;
/*    */     
/* 43 */     EntityStatMap entityStatMapComponent = (EntityStatMap)commandBuffer.getComponent(ref, EntityStatMap.getComponentType());
/* 44 */     assert entityStatMapComponent != null;
/*    */     
/* 46 */     Int2FloatOpenHashMap int2FloatOpenHashMap = new Int2FloatOpenHashMap(this.entityStats);
/*    */     
/* 48 */     Inventory inventory = null;
/* 49 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)entity;
/* 50 */       inventory = livingEntity.getInventory(); }
/*    */ 
/*    */     
/* 53 */     for (IntIterator<Integer> intIterator = int2FloatOpenHashMap.keySet().iterator(); intIterator.hasNext(); ) { int index = ((Integer)intIterator.next()).intValue();
/* 54 */       if (inventory == null)
/* 55 */         continue;  ItemContainer armorContainer = inventory.getArmor();
/* 56 */       if (armorContainer == null)
/* 57 */         continue;  float flatModifier = 0.0F;
/* 58 */       float multiplierModifier = 0.0F;
/*    */       short i;
/* 60 */       for (i = 0; i < armorContainer.getCapacity(); i = (short)(i + 1)) {
/* 61 */         ItemStack itemStack = armorContainer.getItemStack(i);
/* 62 */         if (itemStack != null && !itemStack.isEmpty()) {
/*    */           
/* 64 */           Item item = itemStack.getItem();
/* 65 */           if (item != null && item.getArmor() != null) {
/*    */             
/* 67 */             Int2ObjectMap<StaticModifier> statModifierMap = item.getArmor().getInteractionModifier(this.interactionModifierId.toString());
/* 68 */             if (statModifierMap != null)
/*    */             
/* 70 */             { StaticModifier statModifier = (StaticModifier)statModifierMap.get(index);
/* 71 */               if (statModifier != null)
/*    */               {
/* 73 */                 if (statModifier.getCalculationType() == StaticModifier.CalculationType.ADDITIVE)
/* 74 */                 { flatModifier += statModifier.getAmount(); }
/*    */                 else
/*    */                 
/* 77 */                 { multiplierModifier = statModifier.getAmount(); }  }  } 
/*    */           } 
/*    */         } 
/* 80 */       }  float cost = this.entityStats.get(index);
/* 81 */       cost += flatModifier;
/* 82 */       cost *= Math.max(0.0F, 1.0F - multiplierModifier);
/* 83 */       int2FloatOpenHashMap.replace(index, cost); }
/*    */     
/* 85 */     entityStatMapComponent.processStatChanges(EntityStatMap.Predictable.NONE, (Int2FloatMap)int2FloatOpenHashMap, this.valueType, this.changeStatBehaviour);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 91 */     return "ChangeStatWithModifierInteraction{interactionModifierId=" + String.valueOf(this.interactionModifierId) + "}" + super
/*    */       
/* 93 */       .toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\ChangeStatWithModifierInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */