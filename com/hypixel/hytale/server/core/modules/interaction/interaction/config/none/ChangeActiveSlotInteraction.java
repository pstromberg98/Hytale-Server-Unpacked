/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.none;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.protocol.Interaction;
/*     */ import com.hypixel.hytale.protocol.InteractionCooldown;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.protocol.WaitForDataFrom;
/*     */ import com.hypixel.hytale.server.core.entity.Entity;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionManager;
/*     */ import com.hypixel.hytale.server.core.entity.LivingEntity;
/*     */ import com.hypixel.hytale.server.core.meta.DynamicMetaStore;
/*     */ import com.hypixel.hytale.server.core.meta.MetaKey;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.data.Collector;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class ChangeActiveSlotInteraction
/*     */   extends Interaction
/*     */ {
/*     */   @Nonnull
/*  34 */   public static final ChangeActiveSlotInteraction DEFAULT_INTERACTION = new ChangeActiveSlotInteraction("*Change_Active_Slot");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  40 */   public static final RootInteraction DEFAULT_ROOT = new RootInteraction("*Default_Swap", new InteractionCooldown("ChangeActiveSlot", 0.0F, false, InteractionManager.DEFAULT_CHARGE_TIMES, true, false), new String[] { DEFAULT_INTERACTION.getId() });
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*  46 */   public static final MetaKey<Runnable> PLACE_MOVED_ITEM = CONTEXT_META_REGISTRY.registerMetaObject(i -> null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int UNSET_INT = -2147483648;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static final BuilderCodec<ChangeActiveSlotInteraction> CODEC;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/*  70 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChangeActiveSlotInteraction.class, ChangeActiveSlotInteraction::new, Interaction.ABSTRACT_CODEC).documentation("Changes the active hotbar slot for the user of the interaction.")).appendInherited(new KeyedCodec("TargetSlot", (Codec)Codec.INTEGER), (o, i) -> o.targetSlot = (i == null) ? Integer.MIN_VALUE : i.intValue(), o -> (o.targetSlot == Integer.MIN_VALUE) ? null : Integer.valueOf(o.targetSlot), (o, p) -> o.targetSlot = p.targetSlot).addValidator(Validators.range(Integer.valueOf(0), Integer.valueOf(8))).add()).afterDecode(i -> i.cancelOnItemChange = false)).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  75 */   protected int targetSlot = Integer.MIN_VALUE;
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
/*     */   private ChangeActiveSlotInteraction(@Nonnull String id) {
/*  91 */     super(id);
/*  92 */     this.cancelOnItemChange = false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WaitForDataFrom getWaitForDataFrom() {
/*  98 */     return WaitForDataFrom.None;
/*     */   }
/*     */   protected void tick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/*     */     LivingEntity livingEntity;
/*     */     byte slot;
/* 103 */     if (!firstRun) {
/* 104 */       (context.getState()).state = InteractionState.Finished;
/*     */       
/*     */       return;
/*     */     } 
/* 108 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/* 109 */     assert commandBuffer != null;
/*     */     
/* 111 */     Ref<EntityStore> ref = context.getEntity();
/* 112 */     Entity entity = EntityUtils.getEntity(ref, (ComponentAccessor)commandBuffer); if (entity instanceof LivingEntity) { livingEntity = (LivingEntity)entity; }
/*     */     else { return; }
/* 114 */      DynamicMetaStore<InteractionContext> metaStore = context.getMetaStore();
/*     */ 
/*     */     
/* 117 */     if (this.targetSlot == Integer.MIN_VALUE) {
/* 118 */       slot = ((Integer)metaStore.getMetaObject(TARGET_SLOT)).byteValue();
/*     */     } else {
/*     */       
/* 121 */       if (livingEntity.getInventory().getActiveHotbarSlot() == this.targetSlot) {
/* 122 */         (context.getState()).state = InteractionState.Finished;
/*     */         
/*     */         return;
/*     */       } 
/* 126 */       slot = (byte)this.targetSlot;
/* 127 */       metaStore.putMetaObject(TARGET_SLOT, Integer.valueOf(slot));
/*     */     } 
/*     */     
/* 130 */     livingEntity.getInventory().setActiveHotbarSlot(slot);
/*     */     
/* 132 */     Runnable action = (Runnable)metaStore.removeMetaObject(PLACE_MOVED_ITEM);
/* 133 */     if (action != null) {
/* 134 */       action.run();
/*     */     }
/*     */     
/* 137 */     InteractionManager interactionManager = context.getInteractionManager();
/* 138 */     assert interactionManager != null;
/*     */     
/* 140 */     InteractionContext forkContext = InteractionContext.forInteraction(interactionManager, ref, InteractionType.SwapTo, (ComponentAccessor)commandBuffer);
/* 141 */     String forkInteractions = forkContext.getRootInteractionId(InteractionType.SwapTo);
/*     */     
/* 143 */     if (forkInteractions != null) {
/* 144 */       if (this.targetSlot != Integer.MIN_VALUE) {
/* 145 */         forkContext.getMetaStore().putMetaObject(TARGET_SLOT, Integer.valueOf(slot));
/*     */       }
/* 147 */       context.fork(InteractionType.SwapTo, forkContext, RootInteraction.getRootInteractionOrUnknown(forkInteractions), (action == null));
/*     */     } 
/* 149 */     (context.getState()).state = InteractionState.Finished;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void simulateTick0(boolean firstRun, float time, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
/* 157 */     (context.getState()).state = (context.getServerState()).state;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean walk(@Nonnull Collector collector, @Nonnull InteractionContext context) {
/* 162 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean needsRemoteSync() {
/* 168 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected Interaction generatePacket() {
/* 174 */     return (Interaction)new com.hypixel.hytale.protocol.ChangeActiveSlotInteraction();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void configurePacket(Interaction packet) {
/* 179 */     super.configurePacket(packet);
/* 180 */     com.hypixel.hytale.protocol.ChangeActiveSlotInteraction p = (com.hypixel.hytale.protocol.ChangeActiveSlotInteraction)packet;
/* 181 */     p.targetSlot = this.targetSlot;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 187 */     return "ChangeActiveSlotInteraction{targetSlot=" + this.targetSlot + "} " + super
/*     */       
/* 189 */       .toString();
/*     */   }
/*     */   
/*     */   public ChangeActiveSlotInteraction() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\none\ChangeActiveSlotInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */