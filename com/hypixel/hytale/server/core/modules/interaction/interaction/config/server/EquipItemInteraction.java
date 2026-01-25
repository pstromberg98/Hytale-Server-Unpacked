/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.ItemArmor;
/*    */ import com.hypixel.hytale.server.core.entity.Entity;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*    */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
/*    */ import com.hypixel.hytale.server.core.inventory.transaction.MoveTransaction;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class EquipItemInteraction extends SimpleInstantInteraction {
/* 22 */   public static final BuilderCodec<EquipItemInteraction> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EquipItemInteraction.class, EquipItemInteraction::new, SimpleInstantInteraction.CODEC)
/* 23 */     .documentation("Equips the item being held."))
/* 24 */     .build();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public WaitForDataFrom getWaitForDataFrom() {
/* 29 */     return WaitForDataFrom.Server;
/*    */   }
/*    */   
/*    */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*    */     LivingEntity livingEntity;
/* 34 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 35 */     assert commandBuffer != null;
/*    */     
/* 37 */     Ref<EntityStore> ref = context.getEntity();
/* 38 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*    */     else { return; }
/* 40 */      Inventory inventory = livingEntity.getInventory();
/* 41 */     byte activeSlot = context.getHeldItemSlot();
/* 42 */     ItemStack itemInHand = context.getHeldItem();
/* 43 */     if (itemInHand == null)
/*    */       return; 
/* 45 */     Item item = itemInHand.getItem();
/* 46 */     if (item == null)
/*    */       return; 
/* 48 */     ItemArmor armor = item.getArmor();
/* 49 */     if (armor == null)
/*    */       return; 
/* 51 */     short slotId = (short)armor.getArmorSlot().ordinal();
/* 52 */     ItemContainer armorContainer = inventory.getArmor();
/* 53 */     if (slotId > armorContainer.getCapacity())
/*    */       return; 
/* 55 */     MoveTransaction<ItemStackTransaction> stackTransaction = context.getHeldItemContainer().moveItemStackFromSlot((short)activeSlot, itemInHand.getQuantity(), armorContainer);
/* 56 */     if (!stackTransaction.succeeded()) {
/* 57 */       (context.getState()).state = InteractionState.Failed;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 64 */     return "EquipItemInteraction{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\EquipItemInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */