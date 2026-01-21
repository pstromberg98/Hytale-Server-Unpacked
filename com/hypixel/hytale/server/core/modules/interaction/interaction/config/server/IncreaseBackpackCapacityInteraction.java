/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.codec.validation.Validators;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IncreaseBackpackCapacityInteraction
/*    */   extends SimpleInstantInteraction
/*    */ {
/*    */   public static final BuilderCodec<IncreaseBackpackCapacityInteraction> CODEC;
/*    */   
/*    */   static {
/* 29 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(IncreaseBackpackCapacityInteraction.class, IncreaseBackpackCapacityInteraction::new, SimpleInstantInteraction.CODEC).documentation("Increase the player's backpack capacity.")).appendInherited(new KeyedCodec("Capacity", (Codec)Codec.SHORT), (i, s) -> i.capacity = s.shortValue(), i -> Short.valueOf(i.capacity), (i, parent) -> i.capacity = parent.capacity).documentation("Defines the amount by which the backpack capacity needs to be increased.").addValidator(Validators.min(Short.valueOf((short)1))).add()).build();
/*    */   }
/* 31 */   private short capacity = 1;
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public WaitForDataFrom getWaitForDataFrom() {
/* 36 */     return WaitForDataFrom.Server;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 41 */     Ref<EntityStore> ref = context.getEntity();
/* 42 */     Player playerComponent = (Player)context.getCommandBuffer().getComponent(ref, Player.getComponentType());
/* 43 */     if (playerComponent == null) {
/*    */       return;
/*    */     }
/*    */     
/* 47 */     Inventory inventory = playerComponent.getInventory();
/* 48 */     short newBackpackCapacity = (short)(inventory.getBackpack().getCapacity() + this.capacity);
/* 49 */     inventory.resizeBackpack(newBackpackCapacity, null);
/*    */     
/* 51 */     playerComponent.sendMessage(Message.translation("server.commands.inventory.backpack.size").param("capacity", inventory.getBackpack().getCapacity()));
/*    */     
/* 53 */     context.getHeldItemContainer().removeItemStackFromSlot((short)context.getHeldItemSlot(), context.getHeldItem(), 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     return "IncreaseBackpackCapacityInteraction{capacity=" + this.capacity + "}" + super
/*    */       
/* 60 */       .toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\IncreaseBackpackCapacityInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */