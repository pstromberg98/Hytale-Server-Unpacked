/*    */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.selector;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.protocol.EntityMatcher;
/*    */ import com.hypixel.hytale.protocol.EntityMatcherType;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.none.SelectInteraction;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerMatcher
/*    */   extends SelectInteraction.EntityMatcher
/*    */ {
/*    */   @Nonnull
/* 23 */   public static final BuilderCodec<PlayerMatcher> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PlayerMatcher.class, PlayerMatcher::new, BASE_CODEC)
/* 24 */     .documentation("Matches only players"))
/* 25 */     .build();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean test0(@Nonnull Ref<EntityStore> sourceRef, @Nonnull Ref<EntityStore> targetRef, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 31 */     return commandBuffer.getArchetype(targetRef).contains(Player.getComponentType());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public EntityMatcher toPacket() {
/* 37 */     EntityMatcher packet = super.toPacket();
/* 38 */     packet.type = EntityMatcherType.Player;
/* 39 */     return packet;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\selector\PlayerMatcher.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */