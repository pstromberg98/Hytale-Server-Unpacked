/*    */ package com.hypixel.hytale.builtin.adventure.objectives.completion;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.Objective;
/*    */ import com.hypixel.hytale.builtin.adventure.objectives.config.completion.ObjectiveCompletionAsset;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.server.core.entity.Entity;
/*    */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*    */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*    */ import java.util.UUID;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ClearObjectiveItemsCompletion extends ObjectiveCompletion {
/*    */   public ClearObjectiveItemsCompletion(@Nonnull ObjectiveCompletionAsset asset) {
/* 16 */     super(asset);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ClearObjectiveItemsCompletionAsset getAsset() {
/* 22 */     return (ClearObjectiveItemsCompletionAsset)super.getAsset();
/*    */   }
/*    */ 
/*    */   
/*    */   public void handle(@Nonnull Objective objective, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 27 */     objective.forEachParticipant((participantReference, objectiveUuid) -> { Entity patt0$temp = EntityUtils.getEntity(participantReference, componentAccessor); if (patt0$temp instanceof LivingEntity) { LivingEntity livingEntity = (LivingEntity)patt0$temp; CombinedItemContainer inventory = livingEntity.getInventory().getCombinedHotbarFirst(); short i; for (i = 0; i < inventory.getCapacity(); i = (short)(i + 1)) { ItemStack itemStack = inventory.getItemStack(i); if (itemStack != null) { UUID savedObjectiveUuid = (UUID)itemStack.getFromMetadataOrNull(StartObjectiveInteraction.OBJECTIVE_UUID); if (objectiveUuid.equals(savedObjectiveUuid)) inventory.removeItemStackFromSlot(i);  }  }  }  }objective
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 43 */         .getObjectiveUUID());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 49 */     return "ClearObjectiveItemsCompletion{} " + super.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\objectives\completion\ClearObjectiveItemsCompletion.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */