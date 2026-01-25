/*     */ package com.hypixel.hytale.server.core.modules.interaction.interaction.config.server;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.codecs.map.MapCodec;
/*     */ import com.hypixel.hytale.codec.validation.Validators;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.InteractionState;
/*     */ import com.hypixel.hytale.protocol.InteractionSyncData;
/*     */ import com.hypixel.hytale.protocol.InteractionType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
/*     */ import com.hypixel.hytale.server.core.asset.type.fluid.FluidTicker;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionContext;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.InteractionConfiguration;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.section.FluidSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*     */ 
/*     */ public class RefillContainerInteraction extends SimpleInstantInteraction {
/*     */   public static final BuilderCodec<RefillContainerInteraction> CODEC;
/*     */   protected Map<String, RefillState> refillStateMap;
/*     */   
/*     */   static {
/*  54 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RefillContainerInteraction.class, RefillContainerInteraction::new, SimpleInstantInteraction.CODEC).documentation("Refills a container item that is currently held.")).appendInherited(new KeyedCodec("States", (Codec)new MapCodec((Codec)RefillState.CODEC, java.util.HashMap::new)), (interaction, value) -> interaction.refillStateMap = value, interaction -> interaction.refillStateMap, (o, p) -> o.refillStateMap = p.refillStateMap).addValidator(Validators.nonNull()).add()).afterDecode(refillContainerInteraction -> { refillContainerInteraction.allowedFluidIds = null; refillContainerInteraction.fluidToState = null; })).build();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected int[] allowedFluidIds;
/*     */   
/*     */   @Nullable
/*     */   protected Int2ObjectMap<String> fluidToState;
/*     */ 
/*     */   
/*     */   protected int[] getAllowedFluidIds() {
/*  66 */     if (this.allowedFluidIds != null) return this.allowedFluidIds;
/*     */     
/*  68 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  73 */       .allowedFluidIds = this.refillStateMap.values().stream().map(RefillState::getAllowedFluids).flatMap(Arrays::stream).mapToInt(key -> Fluid.getAssetMap().getIndex(key)).sorted().toArray();
/*     */     
/*  75 */     return this.allowedFluidIds;
/*     */   }
/*     */   
/*     */   protected Int2ObjectMap<String> getFluidToState() {
/*  79 */     if (this.fluidToState != null) return this.fluidToState;
/*     */     
/*  81 */     this.fluidToState = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap();
/*  82 */     this.refillStateMap.forEach((s, refillState) -> {
/*     */           for (String key : refillState.getAllowedFluids()) {
/*     */             this.fluidToState.put(Fluid.getAssetMap().getIndex(key), s);
/*     */           }
/*     */         });
/*     */     
/*  88 */     return this.fluidToState;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void firstRun(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
/*  93 */     CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
/*  94 */     assert commandBuffer != null;
/*     */     
/*  96 */     InteractionSyncData state = context.getState();
/*  97 */     World world = ((EntityStore)commandBuffer.getExternalData()).getWorld();
/*  98 */     Ref<EntityStore> ref = context.getEntity();
/*     */     
/* 100 */     Ref<EntityStore> targetRef = context.getTargetEntity();
/* 101 */     if (targetRef != null) {
/* 102 */       (context.getState()).state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 106 */     Player playerComponent = (Player)commandBuffer.getComponent(ref, Player.getComponentType());
/* 107 */     if (playerComponent == null) {
/* 108 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 112 */     Inventory inventory = playerComponent.getInventory();
/* 113 */     if (inventory == null) {
/* 114 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 118 */     TransformComponent transformComponent = (TransformComponent)commandBuffer.getComponent(ref, TransformComponent.getComponentType());
/* 119 */     if (transformComponent == null) {
/* 120 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 124 */     HeadRotation headRotationComponent = (HeadRotation)commandBuffer.getComponent(ref, HeadRotation.getComponentType());
/* 125 */     if (headRotationComponent == null) {
/* 126 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 130 */     ModelComponent modelComponent = (ModelComponent)commandBuffer.getComponent(ref, ModelComponent.getComponentType());
/* 131 */     if (modelComponent == null) {
/* 132 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 136 */     ItemStack heldItem = context.getHeldItem();
/* 137 */     if (heldItem == null) {
/* 138 */       state.state = InteractionState.Failed;
/*     */       
/*     */       return;
/*     */     } 
/* 142 */     InteractionConfiguration heldItemInteractionConfig = heldItem.getItem().getInteractionConfig();
/* 143 */     float distance = heldItemInteractionConfig.getUseDistance(playerComponent.getGameMode());
/*     */     
/* 145 */     Vector3d fromPos = transformComponent.getPosition().clone();
/* 146 */     fromPos.y += modelComponent.getModel().getEyeHeight(ref, (ComponentAccessor)commandBuffer);
/*     */     
/* 148 */     Vector3d lookDir = headRotationComponent.getDirection();
/* 149 */     Vector3d toPos = fromPos.clone().add(lookDir.scale(distance));
/*     */     
/* 151 */     AtomicBoolean refilled = new AtomicBoolean(false);
/*     */     
/* 153 */     BlockIterator.iterateFromTo(fromPos, toPos, (x, y, z, px, py, pz, qx, qy, qz) -> {
/*     */           Ref<ChunkStore> section = world.getChunkStore().getChunkSectionReference(ChunkUtil.chunkCoordinate(x), ChunkUtil.chunkCoordinate(y), ChunkUtil.chunkCoordinate(z));
/*     */           
/*     */           if (section == null) {
/*     */             return true;
/*     */           }
/*     */           
/*     */           BlockSection blockSection = (BlockSection)section.getStore().getComponent(section, BlockSection.getComponentType());
/*     */           
/*     */           if (blockSection == null) {
/*     */             return true;
/*     */           }
/*     */           
/*     */           if (FluidTicker.isSolid((BlockType)BlockType.getAssetMap().getAsset(blockSection.get(x, y, z)))) {
/*     */             state.state = InteractionState.Failed;
/*     */             
/*     */             return false;
/*     */           } 
/*     */           
/*     */           FluidSection fluidSection = (FluidSection)section.getStore().getComponent(section, FluidSection.getComponentType());
/*     */           
/*     */           if (fluidSection == null) {
/*     */             return true;
/*     */           }
/*     */           
/*     */           int fluidId = fluidSection.getFluidId(x, y, z);
/*     */           
/*     */           int[] allowedBlockIds = getAllowedFluidIds();
/*     */           
/*     */           if (allowedBlockIds != null && Arrays.binarySearch(allowedBlockIds, fluidId) < 0) {
/*     */             state.state = InteractionState.Failed;
/*     */             
/*     */             return true;
/*     */           } 
/*     */           
/*     */           String newState = (String)getFluidToState().get(fluidId);
/*     */           
/*     */           if (newState == null) {
/*     */             state.state = InteractionState.Failed;
/*     */             
/*     */             return false;
/*     */           } 
/*     */           
/*     */           ItemStack current = context.getHeldItem();
/*     */           
/*     */           Item newItemAsset = current.getItem().getItemForState(newState);
/*     */           
/*     */           if (newItemAsset == null) {
/*     */             state.state = InteractionState.Failed;
/*     */             
/*     */             return false;
/*     */           } 
/*     */           
/*     */           RefillState refillState = this.refillStateMap.get(newState);
/*     */           
/*     */           if (newItemAsset.getId().equals(current.getItemId())) {
/*     */             if (refillState != null) {
/*     */               double newDurability = MathUtil.maxValue(refillState.durability, current.getMaxDurability());
/*     */               
/*     */               if (newDurability <= current.getDurability()) {
/*     */                 state.state = InteractionState.Failed;
/*     */                 
/*     */                 return false;
/*     */               } 
/*     */               
/*     */               ItemStack newItem = current.withIncreasedDurability(newDurability);
/*     */               
/*     */               ItemStackSlotTransaction transaction = context.getHeldItemContainer().setItemStackForSlot((short)context.getHeldItemSlot(), newItem);
/*     */               
/*     */               if (!transaction.succeeded()) {
/*     */                 state.state = InteractionState.Failed;
/*     */                 return false;
/*     */               } 
/*     */               context.setHeldItem(newItem);
/*     */               refilled.set(true);
/*     */             } 
/*     */           } else {
/*     */             ItemStackSlotTransaction removeEmptyTransaction = context.getHeldItemContainer().removeItemStackFromSlot((short)context.getHeldItemSlot(), current, 1);
/*     */             if (!removeEmptyTransaction.succeeded()) {
/*     */               state.state = InteractionState.Failed;
/*     */               return false;
/*     */             } 
/*     */             ItemStack refilledContainer = new ItemStack(newItemAsset.getId(), 1);
/*     */             if (refillState != null && refillState.durability > 0.0D) {
/*     */               refilledContainer = refilledContainer.withDurability(refillState.durability);
/*     */             }
/*     */             if (current.getQuantity() == 1) {
/*     */               ItemStackSlotTransaction addFilledTransaction = context.getHeldItemContainer().setItemStackForSlot((short)context.getHeldItemSlot(), refilledContainer);
/*     */               if (!addFilledTransaction.succeeded()) {
/*     */                 state.state = InteractionState.Failed;
/*     */                 return false;
/*     */               } 
/*     */               context.setHeldItem(refilledContainer);
/*     */             } else {
/*     */               SimpleItemContainer.addOrDropItemStack((ComponentAccessor)commandBuffer, ref, (ItemContainer)inventory.getCombinedHotbarFirst(), refilledContainer);
/*     */               context.setHeldItem(context.getHeldItemContainer().getItemStack((short)context.getHeldItemSlot()));
/*     */             } 
/*     */           } 
/*     */           if (refillState != null && refillState.getTransformFluid() != null) {
/*     */             int transformedFluid = Fluid.getFluidIdOrUnknown(refillState.getTransformFluid(), "Unknown fluid %s", new Object[] { refillState.getTransformFluid() });
/*     */             boolean placed = fluidSection.setFluid(x, y, z, transformedFluid, (byte)((Fluid)Fluid.getAssetMap().getAsset(transformedFluid)).getMaxFluidLevel());
/*     */             if (!placed) {
/*     */               state.state = InteractionState.Failed;
/*     */             }
/*     */             world.performBlockUpdate(x, y, z);
/*     */             refilled.set(true);
/*     */           } 
/*     */           return false;
/*     */         });
/* 262 */     if (!refilled.get()) {
/* 263 */       (context.getState()).state = InteractionState.Failed;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 270 */     return "RefillContainerInteraction{refillStateMap=" + String.valueOf(this.refillStateMap) + ", allowedBlockIds=" + 
/*     */       
/* 272 */       Arrays.toString(this.allowedFluidIds) + ", blockToState=" + String.valueOf(this.fluidToState) + "} " + super
/*     */       
/* 274 */       .toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static class RefillState
/*     */   {
/*     */     public static final BuilderCodec<RefillState> CODEC;
/*     */ 
/*     */ 
/*     */     
/*     */     protected String[] allowedFluids;
/*     */ 
/*     */     
/*     */     protected String transformFluid;
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 294 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RefillState.class, RefillState::new).append(new KeyedCodec("AllowedFluids", (Codec)new ArrayCodec((Codec)Codec.STRING, x$0 -> new String[x$0])), (interaction, value) -> interaction.allowedFluids = value, interaction -> interaction.allowedFluids).addValidator(Validators.nonNull()).add()).addField(new KeyedCodec("TransformFluid", (Codec)Codec.STRING), (interaction, value) -> interaction.transformFluid = value, interaction -> interaction.transformFluid)).addField(new KeyedCodec("Durability", (Codec)Codec.DOUBLE), (interaction, value) -> interaction.durability = value.doubleValue(), interaction -> Double.valueOf(interaction.durability))).build();
/*     */     }
/*     */ 
/*     */     
/* 298 */     protected double durability = -1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String[] getAllowedFluids() {
/* 304 */       return this.allowedFluids;
/*     */     }
/*     */     
/*     */     public String getTransformFluid() {
/* 308 */       return this.transformFluid;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getDurability() {
/* 315 */       return this.durability;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String toString() {
/* 322 */       return "RefillState{allowedFluids=" + Arrays.toString((Object[])this.allowedFluids) + ", transformFluid='" + this.transformFluid + "', durability=" + this.durability + "}";
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\interaction\interaction\config\server\RefillContainerInteraction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */