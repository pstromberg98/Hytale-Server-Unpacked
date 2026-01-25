/*     */ package com.hypixel.hytale.builtin.crafting.state;
/*     */ import com.google.common.flogger.LazyArgs;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.window.BenchWindow;
/*     */ import com.hypixel.hytale.builtin.crafting.window.ProcessingBenchWindow;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventPriority;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.math.vector.Vector3i;
/*     */ import com.hypixel.hytale.protocol.Transform;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.server.core.asset.type.blockhitbox.BlockBoundingBoxes;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchTierLevel;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.ProcessingBench;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
/*     */ import com.hypixel.hytale.server.core.inventory.ResourceQuantity;
/*     */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.TestRemoveItemSlotResult;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.ResourceFilter;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.ListTransaction;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.MaterialSlotTransaction;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.MaterialTransaction;
/*     */ import com.hypixel.hytale.server.core.inventory.transaction.ResourceTransaction;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.state.MarkerBlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ThreadLocalRandom;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class ProcessingBenchState extends BenchState implements TickableBlockState, ItemContainerBlockState, DestroyableBlockState, MarkerBlockState, PlacedByBlockState {
/*  62 */   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   public static final boolean EXACT_RESOURCE_AMOUNTS = true;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final Codec<ProcessingBenchState> CODEC;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float EJECT_VELOCITY = 2.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float EJECT_SPREAD_VELOCITY = 1.0F;
/*     */ 
/*     */ 
/*     */   
/*     */   private static final float EJECT_VERTICAL_VELOCITY = 3.25F;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROCESSING = "Processing";
/*     */ 
/*     */ 
/*     */   
/*     */   public static final String PROCESS_COMPLETED = "ProcessCompleted";
/*     */ 
/*     */ 
/*     */   
/*     */   protected WorldMapManager.MarkerReference marker;
/*     */ 
/*     */ 
/*     */   
/*     */   private ProcessingBench processingBench;
/*     */ 
/*     */   
/*     */   private ItemContainer inputContainer;
/*     */ 
/*     */   
/*     */   private ItemContainer fuelContainer;
/*     */ 
/*     */   
/*     */   private ItemContainer outputContainer;
/*     */ 
/*     */   
/*     */   private CombinedItemContainer combinedItemContainer;
/*     */ 
/*     */   
/*     */   private float inputProgress;
/*     */ 
/*     */   
/*     */   private float fuelTime;
/*     */ 
/*     */   
/*     */   private int lastConsumedFuelTotal;
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 124 */     CODEC = (Codec<ProcessingBenchState>)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ProcessingBenchState.class, ProcessingBenchState::new, BenchState.CODEC).append(new KeyedCodec("InputContainer", (Codec)ItemContainer.CODEC), (state, o) -> state.inputContainer = o, state -> state.inputContainer).add()).append(new KeyedCodec("FuelContainer", (Codec)ItemContainer.CODEC), (state, o) -> state.fuelContainer = o, state -> state.fuelContainer).add()).append(new KeyedCodec("OutputContainer", (Codec)ItemContainer.CODEC), (state, o) -> state.outputContainer = o, state -> state.outputContainer).add()).append(new KeyedCodec("Progress", (Codec)Codec.DOUBLE), (state, d) -> state.inputProgress = d.floatValue(), state -> Double.valueOf(state.inputProgress)).add()).append(new KeyedCodec("FuelTime", (Codec)Codec.DOUBLE), (state, d) -> state.fuelTime = d.floatValue(), state -> Double.valueOf(state.fuelTime)).add()).append(new KeyedCodec("Active", (Codec)Codec.BOOLEAN), (state, b) -> state.active = b.booleanValue(), state -> Boolean.valueOf(state.active)).add()).append(new KeyedCodec("NextExtra", (Codec)Codec.INTEGER), (state, b) -> state.nextExtra = b.intValue(), state -> Integer.valueOf(state.nextExtra)).add()).append(new KeyedCodec("Marker", (Codec)WorldMapManager.MarkerReference.CODEC), (state, o) -> state.marker = o, state -> state.marker).add()).append(new KeyedCodec("RecipeId", (Codec)Codec.STRING), (state, o) -> state.recipeId = o, state -> state.recipeId).add()).build();
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
/* 148 */   private int nextExtra = -1;
/* 149 */   private final Set<Short> processingSlots = new HashSet<>();
/* 150 */   private final Set<Short> processingFuelSlots = new HashSet<>();
/*     */   
/*     */   @Nullable
/*     */   private String recipeId;
/*     */   
/*     */   @Nullable
/*     */   private CraftingRecipe recipe;
/*     */   
/*     */   private boolean active = false;
/*     */   
/*     */   public boolean initialize(@Nonnull BlockType blockType) {
/* 161 */     if (!super.initialize(blockType)) {
/* 162 */       if (this.bench == null) {
/* 163 */         ObjectArrayList objectArrayList = new ObjectArrayList();
/* 164 */         if (this.inputContainer != null) objectArrayList.addAll(this.inputContainer.dropAllItemStacks()); 
/* 165 */         if (this.fuelContainer != null) objectArrayList.addAll(this.fuelContainer.dropAllItemStacks()); 
/* 166 */         if (this.outputContainer != null) objectArrayList.addAll(this.outputContainer.dropAllItemStacks());
/*     */         
/* 168 */         World world = getChunk().getWorld();
/* 169 */         Store<EntityStore> store = world.getEntityStore().getStore();
/*     */ 
/*     */         
/* 172 */         Holder[] arrayOfHolder = (Holder[])ejectItems((ComponentAccessor<EntityStore>)store, (List<ItemStack>)objectArrayList);
/*     */         
/* 174 */         if (arrayOfHolder.length > 0) {
/* 175 */           world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
/*     */         }
/*     */       } 
/* 178 */       return false;
/*     */     } 
/*     */     
/* 181 */     if (!(this.bench instanceof ProcessingBench)) {
/* 182 */       LOGGER.at(Level.SEVERE).log("Wrong bench type for processing. Got %s", this.bench.getClass().getName());
/* 183 */       return false;
/*     */     } 
/*     */     
/* 186 */     this.processingBench = (ProcessingBench)this.bench;
/*     */     
/* 188 */     if (this.nextExtra == -1) {
/* 189 */       this.nextExtra = (this.processingBench.getExtraOutput() != null) ? this.processingBench.getExtraOutput().getPerFuelItemsConsumed() : 0;
/*     */     }
/*     */     
/* 192 */     setupSlots();
/* 193 */     return true;
/*     */   }
/*     */   
/*     */   private void setupSlots() {
/* 197 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/* 198 */     int tierLevel = getTierLevel();
/*     */ 
/*     */     
/* 201 */     ProcessingBench.ProcessingSlot[] input = this.processingBench.getInput(tierLevel);
/* 202 */     short inputSlotsCount = (short)input.length;
/* 203 */     this.inputContainer = ItemContainer.ensureContainerCapacity(this.inputContainer, inputSlotsCount, SimpleItemContainer::getNewContainer, (List)objectArrayList);
/* 204 */     this.inputContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
/*     */     
/*     */     short slot;
/* 207 */     for (slot = 0; slot < inputSlotsCount; slot = (short)(slot + 1)) {
/* 208 */       ProcessingBench.ProcessingSlot inputSlot = input[slot];
/* 209 */       String resourceTypeId = inputSlot.getResourceTypeId();
/* 210 */       boolean shouldFilterValidIngredients = inputSlot.shouldFilterValidIngredients();
/*     */       
/* 212 */       if (resourceTypeId != null) {
/* 213 */         this.inputContainer.setSlotFilter(FilterActionType.ADD, slot, (SlotFilter)new ResourceFilter(new ResourceQuantity(resourceTypeId, 1)));
/* 214 */       } else if (shouldFilterValidIngredients) {
/* 215 */         ObjectArrayList<MaterialQuantity> validIngredients = new ObjectArrayList();
/* 216 */         List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
/*     */         
/* 218 */         for (CraftingRecipe recipe : recipes) {
/* 219 */           if (recipe.isRestrictedByBenchTierLevel(this.bench.getId(), tierLevel))
/*     */             continue; 
/* 221 */           List<MaterialQuantity> inputMaterials = CraftingManager.getInputMaterials(recipe);
/* 222 */           validIngredients.addAll(inputMaterials);
/*     */         } 
/*     */         
/* 225 */         this.inputContainer.setSlotFilter(FilterActionType.ADD, slot, (actionType, container, slotIndex, itemStack) -> {
/*     */               if (itemStack == null) {
/*     */                 return true;
/*     */               }
/*     */               ObjectListIterator<MaterialQuantity> objectListIterator = validIngredients.iterator();
/*     */               while (objectListIterator.hasNext()) {
/*     */                 MaterialQuantity ingredient = objectListIterator.next();
/*     */                 if (CraftingManager.matches(ingredient, itemStack)) {
/*     */                   return true;
/*     */                 }
/*     */               } 
/*     */               return false;
/*     */             });
/*     */       } 
/*     */     } 
/* 240 */     ProcessingBench.ProcessingSlot[] benchFuel = this.processingBench.getFuel();
/* 241 */     short fuelCapacity = (short)((benchFuel != null) ? benchFuel.length : 0);
/* 242 */     this.fuelContainer = ItemContainer.ensureContainerCapacity(this.fuelContainer, fuelCapacity, SimpleItemContainer::getNewContainer, (List)objectArrayList);
/* 243 */     this.fuelContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
/*     */ 
/*     */     
/* 246 */     if (fuelCapacity > 0) {
/* 247 */       for (int i = 0; i < benchFuel.length; i++) {
/* 248 */         ProcessingBench.ProcessingSlot fuel = benchFuel[i];
/* 249 */         String resourceTypeId = fuel.getResourceTypeId();
/*     */         
/* 251 */         if (resourceTypeId != null) {
/* 252 */           this.fuelContainer.setSlotFilter(FilterActionType.ADD, (short)i, (SlotFilter)new ResourceFilter(new ResourceQuantity(resourceTypeId, 1)));
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 259 */     short outputSlotsCount = (short)this.processingBench.getOutputSlotsCount(tierLevel);
/* 260 */     this.outputContainer = ItemContainer.ensureContainerCapacity(this.outputContainer, outputSlotsCount, SimpleItemContainer::getNewContainer, (List)objectArrayList);
/* 261 */     this.outputContainer.registerChangeEvent(EventPriority.LAST, this::onItemChange);
/*     */ 
/*     */     
/* 264 */     if (outputSlotsCount > 0) this.outputContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
/*     */ 
/*     */     
/* 267 */     this.combinedItemContainer = new CombinedItemContainer(new ItemContainer[] { this.fuelContainer, this.inputContainer, this.outputContainer });
/*     */     
/* 269 */     World world = getChunk().getWorld();
/* 270 */     Store<EntityStore> store = world.getEntityStore().getStore();
/* 271 */     Holder[] arrayOfHolder = (Holder[])ejectItems((ComponentAccessor<EntityStore>)store, (List<ItemStack>)objectArrayList);
/* 272 */     if (arrayOfHolder.length > 0) {
/* 273 */       world.execute(() -> store.addEntities(itemEntityHolders, AddReason.SPAWN));
/*     */     }
/*     */     
/* 276 */     this.inputContainer.registerChangeEvent(EventPriority.LAST, event -> updateRecipe());
/*     */ 
/*     */     
/* 279 */     if (this.processingBench.getFuel() == null) {
/* 280 */       setActive(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archetypeChunk, @Nonnull Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
/* 286 */     World world = ((ChunkStore)store.getExternalData()).getWorld();
/* 287 */     Store<EntityStore> entityStore = world.getEntityStore().getStore();
/*     */     
/* 289 */     BlockType blockType = getBlockType();
/* 290 */     String currentState = BlockAccessor.getCurrentInteractionState(blockType);
/*     */     
/* 292 */     List<ItemStack> outputItemStacks = null;
/* 293 */     List<MaterialQuantity> inputMaterials = null;
/*     */     
/* 295 */     this.processingSlots.clear();
/* 296 */     checkForRecipeUpdate();
/*     */     
/* 298 */     if (this.recipe != null) {
/*     */       
/* 300 */       outputItemStacks = CraftingManager.getOutputItemStacks(this.recipe);
/* 301 */       if (!this.outputContainer.canAddItemStacks(outputItemStacks, false, false)) {
/* 302 */         if ("Processing".equals(currentState)) {
/* 303 */           setBlockInteractionState("default", blockType);
/* 304 */           playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 305 */         } else if ("ProcessCompleted".equals(currentState)) {
/* 306 */           setBlockInteractionState("default", blockType);
/* 307 */           playSound(world, this.processingBench.getEndSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */         } 
/*     */         
/* 310 */         setActive(false);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 315 */       inputMaterials = CraftingManager.getInputMaterials(this.recipe);
/* 316 */       List<TestRemoveItemSlotResult> result = this.inputContainer.getSlotMaterialsToRemove(inputMaterials, true, true);
/*     */       
/* 318 */       if (result.isEmpty()) {
/* 319 */         if ("Processing".equals(currentState)) {
/* 320 */           setBlockInteractionState("default", blockType);
/* 321 */           playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 322 */         } else if ("ProcessCompleted".equals(currentState)) {
/* 323 */           setBlockInteractionState("default", blockType);
/* 324 */           playSound(world, this.processingBench.getEndSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */         } 
/* 326 */         this.inputProgress = 0.0F;
/* 327 */         setActive(false);
/* 328 */         this.recipeId = null;
/* 329 */         this.recipe = null;
/*     */         
/*     */         return;
/*     */       } 
/* 333 */       for (TestRemoveItemSlotResult item : result) {
/* 334 */         this.processingSlots.addAll(item.getPickedSlots());
/*     */       }
/* 336 */       sendProcessingSlots();
/*     */     } else {
/* 338 */       if (this.processingBench.getFuel() == null) {
/*     */         
/* 340 */         if ("Processing".equals(currentState)) {
/* 341 */           setBlockInteractionState("default", blockType);
/* 342 */           playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 343 */         } else if ("ProcessCompleted".equals(currentState)) {
/* 344 */           setBlockInteractionState("default", blockType);
/* 345 */           playSound(world, this.processingBench.getEndSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */         } 
/*     */         return;
/*     */       } 
/* 349 */       boolean allowNoInputProcessing = this.processingBench.shouldAllowNoInputProcessing();
/*     */       
/* 351 */       if (!allowNoInputProcessing && "Processing".equals(currentState)) {
/* 352 */         setBlockInteractionState("default", blockType);
/* 353 */         playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 354 */       } else if ("ProcessCompleted".equals(currentState)) {
/* 355 */         setBlockInteractionState("default", blockType);
/* 356 */         playSound(world, this.processingBench.getEndSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 357 */         setActive(false);
/* 358 */         sendProgress(0.0F);
/*     */         
/*     */         return;
/*     */       } 
/* 362 */       sendProgress(0.0F);
/*     */       
/* 364 */       if (!allowNoInputProcessing) {
/* 365 */         setActive(false);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 371 */     boolean needsUpdate = false;
/* 372 */     if (this.fuelTime > 0.0F && this.active) {
/* 373 */       this.fuelTime -= dt;
/* 374 */       if (this.fuelTime < 0.0F) this.fuelTime = 0.0F; 
/* 375 */       needsUpdate = true;
/*     */     } 
/*     */     
/* 378 */     ProcessingBench.ProcessingSlot[] fuelSlots = this.processingBench.getFuel();
/* 379 */     boolean hasFuelSlots = (fuelSlots != null && fuelSlots.length > 0);
/*     */     
/* 381 */     if ((this.processingBench.getMaxFuel() <= 0 || this.fuelTime < this.processingBench.getMaxFuel()) && !this.fuelContainer.isEmpty()) {
/* 382 */       if (!hasFuelSlots)
/*     */         return; 
/* 384 */       if (this.active)
/*     */       {
/* 386 */         if (this.fuelTime > 0.0F) {
/*     */ 
/*     */           
/* 389 */           for (int i = 0; i < fuelSlots.length; i++) {
/* 390 */             ItemStack itemInSlot = this.fuelContainer.getItemStack((short)i);
/* 391 */             if (itemInSlot != null) {
/* 392 */               this.processingFuelSlots.add(Short.valueOf((short)i));
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } else {
/* 398 */           if (this.fuelTime < 0.0F) this.fuelTime = 0.0F; 
/* 399 */           this.processingFuelSlots.clear();
/*     */ 
/*     */           
/* 402 */           for (int i = 0; i < fuelSlots.length; i++) {
/* 403 */             ProcessingBench.ProcessingSlot fuelSlot = fuelSlots[i];
/*     */             
/* 405 */             String resourceTypeId = (fuelSlot.getResourceTypeId() != null) ? fuelSlot.getResourceTypeId() : "Fuel";
/* 406 */             ResourceQuantity resourceQuantity = new ResourceQuantity(resourceTypeId, 1);
/*     */             
/* 408 */             ItemStack slot = this.fuelContainer.getItemStack((short)i);
/*     */             
/* 410 */             if (slot != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 416 */               double fuelQuality = slot.getItem().getFuelQuality();
/* 417 */               ResourceTransaction transaction = this.fuelContainer.removeResource(resourceQuantity, true, true, true);
/* 418 */               this.processingFuelSlots.add(Short.valueOf((short)i));
/*     */ 
/*     */               
/* 421 */               if (transaction.getRemainder() <= 0) {
/*     */ 
/*     */                 
/* 424 */                 ProcessingBench.ExtraOutput extra = this.processingBench.getExtraOutput();
/* 425 */                 if (extra != null && 
/* 426 */                   !extra.isIgnoredFuelSource(slot.getItem())) {
/* 427 */                   this.nextExtra--;
/* 428 */                   if (this.nextExtra <= 0) {
/* 429 */                     this.nextExtra = extra.getPerFuelItemsConsumed();
/*     */                     
/* 431 */                     ObjectArrayList<ItemStack> extraItemStacks = new ObjectArrayList((extra.getOutputs()).length);
/* 432 */                     for (MaterialQuantity e : extra.getOutputs()) {
/* 433 */                       extraItemStacks.add(e.toItemStack());
/*     */                     }
/* 435 */                     ListTransaction<ItemStackTransaction> addTransaction = this.outputContainer.addItemStacks((List)extraItemStacks, false, false, false);
/*     */ 
/*     */                     
/* 438 */                     ObjectArrayList<ItemStack> objectArrayList1 = new ObjectArrayList();
/* 439 */                     for (ItemStackTransaction itemStackTransaction : addTransaction.getList()) {
/* 440 */                       ItemStack remainder = itemStackTransaction.getRemainder();
/* 441 */                       if (remainder != null && !remainder.isEmpty()) {
/* 442 */                         objectArrayList1.add(remainder);
/*     */                       }
/*     */                     } 
/*     */                     
/* 446 */                     if (!objectArrayList1.isEmpty()) {
/* 447 */                       LOGGER.at(Level.WARNING).log("Dropping excess items at %s", getBlockPosition());
/*     */                       
/* 449 */                       Holder[] arrayOfHolder = (Holder[])ejectItems((ComponentAccessor<EntityStore>)entityStore, (List<ItemStack>)objectArrayList1);
/* 450 */                       entityStore.addEntities(arrayOfHolder, AddReason.SPAWN);
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */                 
/* 455 */                 this.fuelTime = (float)(this.fuelTime + transaction.getConsumed() * fuelQuality);
/* 456 */                 needsUpdate = true;
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 464 */     if (needsUpdate) {
/* 465 */       updateFuelValues();
/*     */     }
/*     */ 
/*     */     
/* 469 */     if (hasFuelSlots && (!this.active || this.fuelTime <= 0.0F)) {
/* 470 */       this.lastConsumedFuelTotal = 0;
/*     */       
/* 472 */       if ("Processing".equals(currentState)) {
/* 473 */         setBlockInteractionState("default", blockType);
/* 474 */         playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 475 */         if (this.processingBench.getFuel() != null) {
/* 476 */           setActive(false);
/*     */         }
/* 478 */       } else if ("ProcessCompleted".equals(currentState)) {
/* 479 */         setBlockInteractionState("default", blockType);
/* 480 */         playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/* 481 */         if (this.processingBench.getFuel() != null) {
/* 482 */           setActive(false);
/*     */         }
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/* 488 */     if (!"Processing".equals(currentState)) setBlockInteractionState("Processing", blockType);
/*     */     
/* 490 */     if (this.recipe != null && (this.fuelTime > 0.0F || this.processingBench.getFuel() == null)) {
/* 491 */       this.inputProgress += dt;
/*     */     }
/*     */ 
/*     */     
/* 495 */     if (this.recipe != null) {
/*     */       
/* 497 */       float recipeTime = this.recipe.getTimeSeconds();
/*     */       
/* 499 */       float craftingTimeReductionModifier = getCraftingTimeReductionModifier();
/* 500 */       if (craftingTimeReductionModifier > 0.0F) {
/* 501 */         recipeTime -= recipeTime * craftingTimeReductionModifier;
/*     */       }
/*     */       
/* 504 */       if (this.inputProgress > recipeTime) {
/* 505 */         if (recipeTime > 0.0F) {
/* 506 */           this.inputProgress -= recipeTime;
/* 507 */           float progressPercent = this.inputProgress / recipeTime;
/* 508 */           sendProgress(progressPercent);
/*     */         } else {
/* 510 */           this.inputProgress = 0.0F;
/* 511 */           sendProgress(0.0F);
/*     */         } 
/*     */         
/* 514 */         LOGGER.at(Level.FINE).log("Do Process for %s %s", this.recipeId, this.recipe);
/*     */ 
/*     */         
/* 517 */         if (inputMaterials != null) {
/* 518 */           ObjectArrayList<ItemStack> objectArrayList1 = new ObjectArrayList();
/* 519 */           int success = 0;
/* 520 */           IntArrayList slots = new IntArrayList();
/* 521 */           for (int j = 0; j < this.inputContainer.getCapacity(); j++) {
/* 522 */             slots.add(j);
/*     */           }
/* 524 */           for (MaterialQuantity material : inputMaterials) {
/* 525 */             for (int i = 0; i < slots.size(); i++) {
/* 526 */               int slot = slots.getInt(i);
/* 527 */               MaterialSlotTransaction materialSlotTransaction = this.inputContainer.removeMaterialFromSlot((short)slot, material, true, true, true);
/* 528 */               if (materialSlotTransaction.succeeded()) {
/* 529 */                 success++;
/* 530 */                 slots.removeInt(i);
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           } 
/* 536 */           ListTransaction<ItemStackTransaction> listTransaction = this.outputContainer.addItemStacks(outputItemStacks, false, false, false);
/* 537 */           if (!listTransaction.succeeded()) {
/*     */             return;
/*     */           }
/* 540 */           for (ItemStackTransaction itemStackTransaction : listTransaction.getList()) {
/* 541 */             ItemStack remainder = itemStackTransaction.getRemainder();
/* 542 */             if (remainder != null && !remainder.isEmpty()) {
/* 543 */               objectArrayList1.add(remainder);
/*     */             }
/*     */           } 
/*     */           
/* 547 */           if (success == inputMaterials.size()) {
/* 548 */             setBlockInteractionState("ProcessCompleted", blockType);
/* 549 */             playSound(world, this.bench.getCompletedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */             
/* 551 */             if (!objectArrayList1.isEmpty()) {
/* 552 */               LOGGER.at(Level.WARNING).log("Dropping excess items at %s", getBlockPosition());
/*     */               
/* 554 */               Holder[] arrayOfHolder1 = (Holder[])ejectItems((ComponentAccessor<EntityStore>)entityStore, (List<ItemStack>)objectArrayList1);
/* 555 */               entityStore.addEntities(arrayOfHolder1, AddReason.SPAWN);
/*     */             } 
/*     */             
/*     */             return;
/*     */           } 
/*     */         } 
/* 561 */         ObjectArrayList<ItemStack> objectArrayList = new ObjectArrayList();
/*     */         
/* 563 */         ListTransaction<MaterialTransaction> transaction = this.inputContainer.removeMaterials(inputMaterials, true, true, true);
/* 564 */         if (!transaction.succeeded()) {
/* 565 */           LOGGER.at(Level.WARNING).log("Failed to remove input materials at %s", getBlockPosition());
/* 566 */           setBlockInteractionState("default", blockType);
/* 567 */           playSound(world, this.processingBench.getFailedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */           
/*     */           return;
/*     */         } 
/* 571 */         setBlockInteractionState("ProcessCompleted", blockType);
/* 572 */         playSound(world, this.bench.getCompletedSoundEventIndex(), (ComponentAccessor<EntityStore>)entityStore);
/*     */         
/* 574 */         ListTransaction<ItemStackTransaction> addTransaction = this.outputContainer.addItemStacks(outputItemStacks, false, false, false);
/* 575 */         if (addTransaction.succeeded())
/*     */           return; 
/* 577 */         LOGGER.at(Level.WARNING).log("Dropping excess items at %s", getBlockPosition());
/*     */ 
/*     */         
/* 580 */         for (ItemStackTransaction itemStackTransaction : addTransaction.getList()) {
/* 581 */           ItemStack remainder = itemStackTransaction.getRemainder();
/* 582 */           if (remainder != null && !remainder.isEmpty()) {
/* 583 */             objectArrayList.add(remainder);
/*     */           }
/*     */         } 
/*     */         
/* 587 */         Holder[] arrayOfHolder = (Holder[])ejectItems((ComponentAccessor<EntityStore>)entityStore, (List<ItemStack>)objectArrayList);
/* 588 */         entityStore.addEntities(arrayOfHolder, AddReason.SPAWN);
/*     */       }
/* 590 */       else if (this.recipe != null && recipeTime > 0.0F) {
/* 591 */         float progressPercent = this.inputProgress / recipeTime;
/* 592 */         sendProgress(progressPercent);
/*     */       } else {
/* 594 */         sendProgress(0.0F);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private float getCraftingTimeReductionModifier() {
/* 601 */     BenchTierLevel levelData = this.bench.getTierLevel(getTierLevel());
/* 602 */     return (levelData != null) ? levelData.getCraftingTimeReductionModifier() : 0.0F;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private Holder<EntityStore>[] ejectItems(@Nonnull ComponentAccessor<EntityStore> accessor, @Nonnull List<ItemStack> itemStacks) {
/*     */     Vector3d dropPosition;
/* 608 */     if (itemStacks.isEmpty()) {
/* 609 */       return (Holder<EntityStore>[])Holder.emptyArray();
/*     */     }
/*     */     
/* 612 */     RotationTuple rotation = RotationTuple.get(getRotationIndex());
/* 613 */     Vector3d frontDir = new Vector3d(0.0D, 0.0D, 1.0D);
/* 614 */     rotation.yaw().rotateY(frontDir, frontDir);
/*     */ 
/*     */     
/* 617 */     BlockType blockType = getBlockType();
/* 618 */     if (blockType == null) {
/* 619 */       dropPosition = getBlockPosition().toVector3d().add(0.5D, 0.0D, 0.5D);
/*     */     } else {
/* 621 */       BlockBoundingBoxes hitboxAsset = (BlockBoundingBoxes)BlockBoundingBoxes.getAssetMap().getAsset(blockType.getHitboxTypeIndex());
/* 622 */       if (hitboxAsset == null) {
/* 623 */         dropPosition = getBlockPosition().toVector3d().add(0.5D, 0.0D, 0.5D);
/*     */       } else {
/* 625 */         double depth = hitboxAsset.get(0).getBoundingBox().depth();
/* 626 */         double frontOffset = depth / 2.0D + 0.10000000149011612D;
/*     */         
/* 628 */         dropPosition = getCenteredBlockPosition();
/* 629 */         dropPosition.add(frontDir.x * frontOffset, 0.0D, frontDir.z * frontOffset);
/*     */       } 
/*     */     } 
/*     */     
/* 633 */     ThreadLocalRandom random = ThreadLocalRandom.current();
/* 634 */     ObjectArrayList<Holder<EntityStore>> result = new ObjectArrayList(itemStacks.size());
/*     */     
/* 636 */     for (ItemStack item : itemStacks) {
/* 637 */       float velocityX = (float)(frontDir.x * 2.0D + 2.0D * (random.nextDouble() - 0.5D));
/* 638 */       float velocityZ = (float)(frontDir.z * 2.0D + 2.0D * (random.nextDouble() - 0.5D));
/*     */       
/* 640 */       Holder<EntityStore> holder = ItemComponent.generateItemDrop(accessor, item, dropPosition, Vector3f.ZERO, velocityX, 3.25F, velocityZ);
/*     */       
/* 642 */       if (holder != null) {
/* 643 */         result.add(holder);
/*     */       }
/*     */     } 
/*     */     
/* 647 */     return (Holder<EntityStore>[])result.toArray(x$0 -> new Holder[x$0]);
/*     */   }
/*     */   
/*     */   private void sendProgress(float progress) {
/* 651 */     this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProgress(progress));
/*     */   }
/*     */   
/*     */   private void sendProcessingSlots() {
/* 655 */     this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProcessingSlots(this.processingSlots));
/*     */   }
/*     */   
/*     */   private void sendProcessingFuelSlots() {
/* 659 */     this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setProcessingFuelSlots(this.processingFuelSlots));
/*     */   }
/*     */   
/*     */   public boolean isActive() {
/* 663 */     return this.active;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setActive(boolean active) {
/* 672 */     if (this.active != active) {
/* 673 */       if (active && this.processingBench.getFuel() != null && this.fuelContainer.isEmpty()) return false; 
/* 674 */       this.active = active;
/*     */       
/* 676 */       if (!active) {
/* 677 */         this.processingSlots.clear();
/* 678 */         this.processingFuelSlots.clear();
/* 679 */         sendProcessingSlots();
/* 680 */         sendProcessingFuelSlots();
/*     */       } 
/* 682 */       updateRecipe();
/* 683 */       this.windows.forEach((uuid, window) -> ((ProcessingBenchWindow)window).setActive(active));
/* 684 */       markNeedsSave();
/* 685 */       return true;
/*     */     } 
/* 687 */     return false;
/*     */   }
/*     */   
/*     */   public void updateFuelValues() {
/* 691 */     if (this.fuelTime > this.lastConsumedFuelTotal) {
/* 692 */       this.lastConsumedFuelTotal = MathUtil.ceil(this.fuelTime);
/*     */     }
/*     */     
/* 695 */     float fuelPercent = (this.lastConsumedFuelTotal > 0) ? (this.fuelTime / this.lastConsumedFuelTotal) : 0.0F;
/* 696 */     this.windows.forEach((uuid, window) -> {
/*     */           ProcessingBenchWindow processingBenchWindow = (ProcessingBenchWindow)window;
/*     */           processingBenchWindow.setFuelTime(fuelPercent);
/*     */           processingBenchWindow.setMaxFuel(this.lastConsumedFuelTotal);
/*     */           processingBenchWindow.setProcessingFuelSlots(this.processingFuelSlots);
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDestroy() {
/* 706 */     super.onDestroy();
/*     */     
/* 708 */     if (this.combinedItemContainer != null) {
/* 709 */       List<ItemStack> itemStacks = this.combinedItemContainer.dropAllItemStacks();
/* 710 */       dropFuelItems(itemStacks);
/*     */       
/* 712 */       World world = getChunk().getWorld();
/* 713 */       Store<EntityStore> entityStore = world.getEntityStore().getStore();
/* 714 */       Vector3d dropPosition = getBlockPosition().toVector3d().add(0.5D, 0.0D, 0.5D);
/* 715 */       Holder[] arrayOfHolder = ItemComponent.generateItemDrops((ComponentAccessor)entityStore, itemStacks, dropPosition, Vector3f.ZERO);
/* 716 */       if (arrayOfHolder.length > 0) {
/* 717 */         world.execute(() -> entityStore.addEntities(itemEntityHolders, AddReason.SPAWN));
/*     */       }
/*     */     } 
/*     */     
/* 721 */     if (this.marker != null) this.marker.remove();
/*     */   
/*     */   }
/*     */   
/*     */   public CombinedItemContainer getItemContainer() {
/* 726 */     return this.combinedItemContainer;
/*     */   }
/*     */   
/*     */   private void checkForRecipeUpdate() {
/* 730 */     if (this.recipe == null && this.recipeId != null) {
/* 731 */       updateRecipe();
/*     */     }
/*     */   }
/*     */   
/*     */   private void updateRecipe() {
/* 736 */     List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
/* 737 */     if (recipes.isEmpty()) {
/* 738 */       clearRecipe();
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 743 */     ObjectArrayList<CraftingRecipe> objectArrayList = new ObjectArrayList();
/* 744 */     for (CraftingRecipe craftingRecipe : recipes) {
/* 745 */       if (craftingRecipe.isRestrictedByBenchTierLevel(this.bench.getId(), getTierLevel()))
/*     */         continue; 
/* 747 */       MaterialQuantity[] input = craftingRecipe.getInput();
/* 748 */       int matches = 0;
/*     */       
/* 750 */       IntArrayList slots = new IntArrayList();
/* 751 */       for (int j = 0; j < this.inputContainer.getCapacity(); j++) {
/* 752 */         slots.add(j);
/*     */       }
/*     */       
/* 755 */       for (MaterialQuantity craftingMaterial : input) {
/* 756 */         String itemId = craftingMaterial.getItemId();
/* 757 */         String resourceTypeId = craftingMaterial.getResourceTypeId();
/* 758 */         int materialQuantity = craftingMaterial.getQuantity();
/* 759 */         BsonDocument metadata = craftingMaterial.getMetadata();
/* 760 */         MaterialQuantity material = new MaterialQuantity(itemId, resourceTypeId, null, materialQuantity, metadata);
/*     */         
/* 762 */         for (int k = 0; k < slots.size(); k++) {
/* 763 */           int i = slots.getInt(k);
/* 764 */           int out = InternalContainerUtilMaterial.testRemoveMaterialFromSlot(this.inputContainer, (short)i, material, material.getQuantity(), true);
/* 765 */           if (out == 0) {
/* 766 */             matches++;
/* 767 */             slots.removeInt(k);
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 773 */       if (matches == input.length) {
/* 774 */         objectArrayList.add(craftingRecipe);
/*     */       }
/*     */     } 
/*     */     
/* 778 */     if (objectArrayList.isEmpty()) {
/* 779 */       clearRecipe();
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 785 */     objectArrayList.sort(Comparator.comparingInt(o -> CraftingManager.getInputMaterials(o).size()));
/* 786 */     Collections.reverse((List<?>)objectArrayList);
/*     */     
/* 788 */     if (this.recipeId != null)
/*     */     {
/* 790 */       for (CraftingRecipe rec : objectArrayList) {
/* 791 */         if (Objects.equals(this.recipeId, rec.getId())) {
/* 792 */           LOGGER.at(Level.FINE).log("%s - Keeping existing Recipe %s %s", LazyArgs.lazy(this::getBlockPosition), this.recipeId, rec);
/* 793 */           this.recipe = rec;
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     }
/* 799 */     CraftingRecipe recipe = objectArrayList.getFirst();
/* 800 */     if (this.recipeId == null || !Objects.equals(this.recipeId, recipe.getId())) {
/* 801 */       this.inputProgress = 0.0F;
/* 802 */       sendProgress(0.0F);
/*     */     } 
/* 804 */     this.recipeId = recipe.getId();
/* 805 */     this.recipe = recipe;
/*     */     
/* 807 */     LOGGER.at(Level.FINE).log("%s - Found Recipe %s %s", LazyArgs.lazy(this::getBlockPosition), this.recipeId, this.recipe);
/*     */   }
/*     */   
/*     */   private void clearRecipe() {
/* 811 */     this.recipeId = null;
/* 812 */     this.recipe = null;
/* 813 */     this.lastConsumedFuelTotal = 0;
/* 814 */     this.inputProgress = 0.0F;
/* 815 */     sendProgress(0.0F);
/* 816 */     LOGGER.at(Level.FINE).log("%s - Cleared Recipe", LazyArgs.lazy(this::getBlockPosition));
/*     */   }
/*     */ 
/*     */   
/*     */   public void dropFuelItems(@Nonnull List<ItemStack> itemStacks) {
/* 821 */     String fuelDropItemId = this.processingBench.getFuelDropItemId();
/* 822 */     if (fuelDropItemId != null) {
/* 823 */       Item item = (Item)Item.getAssetMap().getAsset(fuelDropItemId);
/*     */ 
/*     */       
/* 826 */       int dropAmount = (int)this.fuelTime;
/* 827 */       this.fuelTime = 0.0F;
/*     */       
/* 829 */       while (dropAmount > 0) {
/* 830 */         int quantity = Math.min(dropAmount, item.getMaxStack());
/* 831 */         itemStacks.add(new ItemStack(fuelDropItemId, quantity));
/* 832 */         dropAmount -= quantity;
/*     */       } 
/*     */     } else {
/* 835 */       LOGGER.at(Level.WARNING).log("No FuelDropItemId defined for %s fuel value of %s will be lost!", this.bench.getId(), this.fuelTime);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public CraftingRecipe getRecipe() {
/* 841 */     return this.recipe;
/*     */   }
/*     */   
/*     */   public float getInputProgress() {
/* 845 */     return this.inputProgress;
/*     */   }
/*     */   
/*     */   public void onItemChange(ItemContainer.ItemContainerChangeEvent event) {
/* 849 */     markNeedsSave();
/*     */   }
/*     */   
/*     */   public void setBlockInteractionState(@Nonnull String state, @Nonnull BlockType blockType) {
/* 853 */     getChunk().setBlockInteractionState(getBlockPosition(), blockType, state);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMarker(WorldMapManager.MarkerReference marker) {
/* 858 */     this.marker = marker;
/* 859 */     markNeedsSave();
/*     */   }
/*     */ 
/*     */   
/*     */   public void placedBy(@Nonnull Ref<EntityStore> playerRef, @Nonnull String blockTypeKey, @Nonnull BlockState blockState, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 864 */     if (blockTypeKey.equals(this.processingBench.getIconItem()) && this.processingBench.getIcon() != null) {
/* 865 */       Player playerComponent = (Player)componentAccessor.getComponent(playerRef, Player.getComponentType());
/* 866 */       assert playerComponent != null;
/*     */       
/* 868 */       TransformComponent transformComponent = (TransformComponent)componentAccessor.getComponent(playerRef, TransformComponent.getComponentType());
/* 869 */       assert transformComponent != null;
/*     */       
/* 871 */       Transform transformPacket = PositionUtil.toTransformPacket(transformComponent.getTransform());
/* 872 */       transformPacket.orientation.yaw = 0.0F;
/* 873 */       transformPacket.orientation.pitch = 0.0F;
/* 874 */       transformPacket.orientation.roll = 0.0F;
/*     */       
/* 876 */       MapMarker marker = new MapMarker(this.processingBench.getIconId() + "-" + this.processingBench.getIconId(), this.processingBench.getIconName(), this.processingBench.getIcon(), transformPacket, null);
/*     */       
/* 878 */       ((MarkerBlockState)blockState).setMarker((WorldMapManager.MarkerReference)WorldMapManager.createPlayerMarker(playerRef, marker, componentAccessor));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void playSound(@Nonnull World world, int soundEventIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 883 */     if (soundEventIndex == 0)
/*     */       return; 
/* 885 */     Vector3i pos = getBlockPosition();
/* 886 */     SoundUtil.playSoundEvent3d(soundEventIndex, SoundCategory.SFX, pos.x + 0.5D, pos.y + 0.5D, pos.z + 0.5D, componentAccessor);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onTierLevelChange() {
/* 891 */     super.onTierLevelChange();
/* 892 */     setupSlots();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\state\ProcessingBenchState.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */