/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.InteractionState;
/*    */ import com.hypixel.hytale.protocol.InteractionType;
/*    */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*    */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.data.UniqueItemUsagesComponent;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import com.hypixel.hytale.server.core.util.NotificationUtil;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class CheckUniqueItemUsageInteraction extends SimpleInstantInteraction {
/* 18 */   public static final BuilderCodec<CheckUniqueItemUsageInteraction> CODEC = BuilderCodec.builder(CheckUniqueItemUsageInteraction.class, CheckUniqueItemUsageInteraction::new, SimpleInstantInteraction.CODEC)
/* 19 */     .build();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public WaitForDataFrom getWaitForDataFrom() {
/* 24 */     return WaitForDataFrom.Server;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 29 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 30 */     assert commandBuffer != null;
/*    */     
/* 32 */     Ref<EntityStore> ref = context.getEntity();
/* 33 */     PlayerRef playerRefComponent = (PlayerRef)commandBuffer.getComponent(ref, PlayerRef.getComponentType());
/* 34 */     if (playerRefComponent == null) {
/* 35 */       (context.getState()).state = InteractionState.Failed;
/*    */       
/*    */       return;
/*    */     } 
/* 39 */     UniqueItemUsagesComponent uniqueItemUsagesComponent = (UniqueItemUsagesComponent)commandBuffer.getComponent(ref, UniqueItemUsagesComponent.getComponentType());
/* 40 */     assert uniqueItemUsagesComponent != null;
/* 41 */     if (uniqueItemUsagesComponent.hasUsedUniqueItem(context.getHeldItem().getItemId())) {
/* 42 */       (context.getState()).state = InteractionState.Failed;
/* 43 */       NotificationUtil.sendNotification(playerRefComponent.getPacketHandler(), 
/* 44 */           Message.translation("server.commands.checkUniqueItemUsage.uniqueItemAlreadyUsed"));
/*    */       
/*    */       return;
/*    */     } 
/* 48 */     uniqueItemUsagesComponent.recordUniqueItemUsage(context.getHeldItem().getItemId());
/* 49 */     (context.getState()).state = InteractionState.Finished;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "CheckUniqueItemUsageInteraction{}" + super
/* 55 */       .toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\CheckUniqueItemUsageInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */