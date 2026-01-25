/*     */ package com.hypixel.hytale.builtin.crafting.window;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventRegistration;
/*     */ import com.hypixel.hytale.protocol.BenchRequirement;
/*     */ import com.hypixel.hytale.protocol.ItemSoundEvent;
/*     */ import com.hypixel.hytale.protocol.SoundCategory;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.StructuralCraftingBench;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.BlockGroup;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.asset.type.itemsound.config.ItemSoundSet;
/*     */ import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
/*     */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
/*     */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectList;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class StructuralCraftingWindow extends CraftingWindow implements ItemContainerWindow {
/*     */   private static final int MAX_OPTIONS = 64;
/*     */   private final SimpleItemContainer inputContainer;
/*     */   private final SimpleItemContainer optionsContainer;
/*     */   private final CombinedItemContainer combinedItemContainer;
/*  48 */   private final Int2ObjectMap<String> optionSlotToRecipeMap = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap();
/*     */   
/*     */   private int selectedSlot;
/*     */   
/*     */   @Nullable
/*     */   private EventRegistration inventoryRegistration;
/*     */   
/*     */   public StructuralCraftingWindow(BenchState benchState) {
/*  56 */     super(WindowType.StructuralCrafting, benchState);
/*     */     
/*  58 */     this.inputContainer = new SimpleItemContainer((short)1);
/*  59 */     this.inputContainer.registerChangeEvent(e -> updateRecipes());
/*  60 */     this.inputContainer.setSlotFilter(FilterActionType.ADD, (short)0, this::isValidInput);
/*     */     
/*  62 */     this.optionsContainer = new SimpleItemContainer((short)64);
/*  63 */     this.optionsContainer.setGlobalFilter(FilterType.DENY_ALL);
/*     */     
/*  65 */     this.combinedItemContainer = new CombinedItemContainer(new ItemContainer[] { (ItemContainer)this.inputContainer, (ItemContainer)this.optionsContainer });
/*  66 */     this.windowData.addProperty("selected", Integer.valueOf(this.selectedSlot));
/*     */     
/*  68 */     StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
/*  69 */     this.windowData.addProperty("allowBlockGroupCycling", Boolean.valueOf(structuralBench.shouldAllowBlockGroupCycling()));
/*  70 */     this.windowData.addProperty("alwaysShowInventoryHints", Boolean.valueOf(structuralBench.shouldAlwaysShowInventoryHints()));
/*     */   }
/*     */   
/*     */   private boolean isValidInput(FilterActionType filterActionType, ItemContainer itemContainer, short i, ItemStack itemStack) {
/*  74 */     if (filterActionType != FilterActionType.ADD) return true;
/*     */     
/*  76 */     ObjectList<CraftingRecipe> matchingRecipes = getMatchingRecipes(itemStack);
/*     */     
/*  78 */     return (matchingRecipes != null && !matchingRecipes.isEmpty());
/*     */   }
/*     */   
/*     */   private static void sortRecipes(ObjectList<CraftingRecipe> matching, StructuralCraftingBench structuralBench) {
/*  82 */     matching.sort((a, b) -> {
/*     */           boolean aHasHeaderCategory = hasHeaderCategory(structuralBench, a);
/*     */           boolean bHasHeaderCategory = hasHeaderCategory(structuralBench, b);
/*     */           if (aHasHeaderCategory != bHasHeaderCategory) {
/*     */             return aHasHeaderCategory ? -1 : 1;
/*     */           }
/*     */           int categoryA = getSortingPriority(structuralBench, a);
/*     */           int categoryB = getSortingPriority(structuralBench, b);
/*     */           int categoryCompare = Integer.compare(categoryA, categoryB);
/*     */           return (categoryCompare != 0) ? categoryCompare : a.getId().compareTo(b.getId());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasHeaderCategory(StructuralCraftingBench bench, CraftingRecipe recipe) {
/*  99 */     for (BenchRequirement requirement : recipe.getBenchRequirement()) {
/* 100 */       if (requirement.type == bench.getType() && requirement.id.equals(bench.getId()) && requirement.categories != null) {
/* 101 */         for (String category : requirement.categories) {
/* 102 */           if (bench.isHeaderCategory(category)) {
/* 103 */             return true;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 108 */     return false;
/*     */   }
/*     */   
/*     */   private static int getSortingPriority(StructuralCraftingBench bench, CraftingRecipe recipe) {
/* 112 */     int priority = Integer.MAX_VALUE;
/*     */     
/* 114 */     for (BenchRequirement requirement : recipe.getBenchRequirement()) {
/* 115 */       if (requirement.type == bench.getType() && requirement.id.equals(bench.getId()) && requirement.categories != null) {
/* 116 */         for (String category : requirement.categories) {
/* 117 */           priority = Math.min(priority, bench.getCategoryIndex(category));
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 123 */     return priority;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
/*     */     // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: aload_1
/*     */     //   2: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   5: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   8: checkcast com/hypixel/hytale/builtin/crafting/component/CraftingManager
/*     */     //   11: astore #4
/*     */     //   13: aload_3
/*     */     //   14: dup
/*     */     //   15: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   18: pop
/*     */     //   19: astore #5
/*     */     //   21: iconst_0
/*     */     //   22: istore #6
/*     */     //   24: aload #5
/*     */     //   26: iload #6
/*     */     //   28: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   33: tableswitch default -> 306, 0 -> 60, 1 -> 123, 2 -> 272
/*     */     //   60: aload #5
/*     */     //   62: checkcast com/hypixel/hytale/protocol/packets/window/SelectSlotAction
/*     */     //   65: astore #7
/*     */     //   67: aload #7
/*     */     //   69: getfield slot : I
/*     */     //   72: iconst_0
/*     */     //   73: aload_0
/*     */     //   74: getfield optionsContainer : Lcom/hypixel/hytale/server/core/inventory/container/SimpleItemContainer;
/*     */     //   77: invokevirtual getCapacity : ()S
/*     */     //   80: invokestatic clamp : (III)I
/*     */     //   83: istore #8
/*     */     //   85: iload #8
/*     */     //   87: aload_0
/*     */     //   88: getfield selectedSlot : I
/*     */     //   91: if_icmpeq -> 120
/*     */     //   94: aload_0
/*     */     //   95: iload #8
/*     */     //   97: putfield selectedSlot : I
/*     */     //   100: aload_0
/*     */     //   101: getfield windowData : Lcom/google/gson/JsonObject;
/*     */     //   104: ldc 'selected'
/*     */     //   106: aload_0
/*     */     //   107: getfield selectedSlot : I
/*     */     //   110: invokestatic valueOf : (I)Ljava/lang/Integer;
/*     */     //   113: invokevirtual addProperty : (Ljava/lang/String;Ljava/lang/Number;)V
/*     */     //   116: aload_0
/*     */     //   117: invokevirtual invalidate : ()V
/*     */     //   120: goto -> 306
/*     */     //   123: aload #5
/*     */     //   125: checkcast com/hypixel/hytale/protocol/packets/window/CraftRecipeAction
/*     */     //   128: astore #8
/*     */     //   130: aload_0
/*     */     //   131: getfield optionsContainer : Lcom/hypixel/hytale/server/core/inventory/container/SimpleItemContainer;
/*     */     //   134: aload_0
/*     */     //   135: getfield selectedSlot : I
/*     */     //   138: i2s
/*     */     //   139: invokevirtual getItemStack : (S)Lcom/hypixel/hytale/server/core/inventory/ItemStack;
/*     */     //   142: astore #9
/*     */     //   144: aload #9
/*     */     //   146: ifnull -> 269
/*     */     //   149: aload #8
/*     */     //   151: getfield quantity : I
/*     */     //   154: istore #10
/*     */     //   156: aload_0
/*     */     //   157: getfield optionSlotToRecipeMap : Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;
/*     */     //   160: aload_0
/*     */     //   161: getfield selectedSlot : I
/*     */     //   164: invokeinterface get : (I)Ljava/lang/Object;
/*     */     //   169: checkcast java/lang/String
/*     */     //   172: astore #11
/*     */     //   174: aload #11
/*     */     //   176: ifnonnull -> 180
/*     */     //   179: return
/*     */     //   180: invokestatic getAssetMap : ()Lcom/hypixel/hytale/assetstore/map/DefaultAssetMap;
/*     */     //   183: aload #11
/*     */     //   185: invokevirtual getAsset : (Ljava/lang/Object;)Lcom/hypixel/hytale/assetstore/JsonAsset;
/*     */     //   188: checkcast com/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe
/*     */     //   191: astore #12
/*     */     //   193: aload #12
/*     */     //   195: ifnonnull -> 199
/*     */     //   198: return
/*     */     //   199: aload #12
/*     */     //   201: invokevirtual getPrimaryOutput : ()Lcom/hypixel/hytale/server/core/inventory/MaterialQuantity;
/*     */     //   204: astore #13
/*     */     //   206: aload #13
/*     */     //   208: invokevirtual getItemId : ()Ljava/lang/String;
/*     */     //   211: astore #14
/*     */     //   213: aload #14
/*     */     //   215: ifnull -> 244
/*     */     //   218: invokestatic getAssetMap : ()Lcom/hypixel/hytale/assetstore/map/DefaultAssetMap;
/*     */     //   221: aload #14
/*     */     //   223: invokevirtual getAsset : (Ljava/lang/Object;)Lcom/hypixel/hytale/assetstore/JsonAsset;
/*     */     //   226: checkcast com/hypixel/hytale/server/core/asset/type/item/config/Item
/*     */     //   229: astore #15
/*     */     //   231: aload #15
/*     */     //   233: ifnull -> 244
/*     */     //   236: aload_0
/*     */     //   237: aload_1
/*     */     //   238: aload_2
/*     */     //   239: aload #15
/*     */     //   241: invokevirtual playCraftSound : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Store;Lcom/hypixel/hytale/server/core/asset/type/item/config/Item;)V
/*     */     //   244: aload #4
/*     */     //   246: aload_1
/*     */     //   247: aload_2
/*     */     //   248: aload_0
/*     */     //   249: iconst_0
/*     */     //   250: aload #12
/*     */     //   252: iload #10
/*     */     //   254: aload_0
/*     */     //   255: getfield inputContainer : Lcom/hypixel/hytale/server/core/inventory/container/SimpleItemContainer;
/*     */     //   258: getstatic com/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType.ORDERED : Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType;
/*     */     //   261: invokevirtual queueCraft : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/builtin/crafting/window/CraftingWindow;ILcom/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe;ILcom/hypixel/hytale/server/core/inventory/container/ItemContainer;Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType;)Z
/*     */     //   264: pop
/*     */     //   265: aload_0
/*     */     //   266: invokevirtual invalidate : ()V
/*     */     //   269: goto -> 306
/*     */     //   272: aload #5
/*     */     //   274: checkcast com/hypixel/hytale/protocol/packets/window/ChangeBlockAction
/*     */     //   277: astore #9
/*     */     //   279: aload_0
/*     */     //   280: getfield bench : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/Bench;
/*     */     //   283: checkcast com/hypixel/hytale/server/core/asset/type/blocktype/config/bench/StructuralCraftingBench
/*     */     //   286: invokevirtual shouldAllowBlockGroupCycling : ()Z
/*     */     //   289: ifeq -> 306
/*     */     //   292: aload_0
/*     */     //   293: aload_1
/*     */     //   294: aload #9
/*     */     //   296: getfield down : Z
/*     */     //   299: aload_2
/*     */     //   300: invokevirtual changeBlockType : (Lcom/hypixel/hytale/component/Ref;ZLcom/hypixel/hytale/component/Store;)V
/*     */     //   303: goto -> 306
/*     */     //   306: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #128	-> 0
/*     */     //   #130	-> 13
/*     */     //   #131	-> 60
/*     */     //   #132	-> 67
/*     */     //   #133	-> 85
/*     */     //   #134	-> 94
/*     */     //   #135	-> 100
/*     */     //   #136	-> 116
/*     */     //   #138	-> 120
/*     */     //   #139	-> 123
/*     */     //   #140	-> 130
/*     */     //   #141	-> 144
/*     */     //   #142	-> 149
/*     */     //   #143	-> 156
/*     */     //   #145	-> 174
/*     */     //   #146	-> 179
/*     */     //   #149	-> 180
/*     */     //   #150	-> 193
/*     */     //   #151	-> 198
/*     */     //   #154	-> 199
/*     */     //   #155	-> 206
/*     */     //   #157	-> 213
/*     */     //   #158	-> 218
/*     */     //   #160	-> 231
/*     */     //   #161	-> 236
/*     */     //   #165	-> 244
/*     */     //   #166	-> 265
/*     */     //   #168	-> 269
/*     */     //   #169	-> 272
/*     */     //   #170	-> 279
/*     */     //   #171	-> 292
/*     */     //   #177	-> 306
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   85	35	8	newSlot	I
/*     */     //   67	56	7	selectAction	Lcom/hypixel/hytale/protocol/packets/window/SelectSlotAction;
/*     */     //   231	13	15	primaryOutputItem	Lcom/hypixel/hytale/server/core/asset/type/item/config/Item;
/*     */     //   156	113	10	quantity	I
/*     */     //   174	95	11	recipeId	Ljava/lang/String;
/*     */     //   193	76	12	recipe	Lcom/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe;
/*     */     //   206	63	13	primaryOutput	Lcom/hypixel/hytale/server/core/inventory/MaterialQuantity;
/*     */     //   213	56	14	primaryOutputItemId	Ljava/lang/String;
/*     */     //   144	125	9	output	Lcom/hypixel/hytale/server/core/inventory/ItemStack;
/*     */     //   130	142	8	craftAction	Lcom/hypixel/hytale/protocol/packets/window/CraftRecipeAction;
/*     */     //   279	27	9	changeBlockAction	Lcom/hypixel/hytale/protocol/packets/window/ChangeBlockAction;
/*     */     //   0	307	0	this	Lcom/hypixel/hytale/builtin/crafting/window/StructuralCraftingWindow;
/*     */     //   0	307	1	ref	Lcom/hypixel/hytale/component/Ref;
/*     */     //   0	307	2	store	Lcom/hypixel/hytale/component/Store;
/*     */     //   0	307	3	action	Lcom/hypixel/hytale/protocol/packets/window/WindowAction;
/*     */     //   13	294	4	craftingManager	Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	307	1	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */     //   0	307	2	store	Lcom/hypixel/hytale/component/Store<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
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
/*     */ 
/*     */ 
/*     */   
/*     */   private void playCraftSound(Ref<EntityStore> ref, Store<EntityStore> store, Item item) {
/* 180 */     ItemSoundSet soundSet = (ItemSoundSet)ItemSoundSet.getAssetMap().getAsset(item.getItemSoundSetIndex());
/* 181 */     if (soundSet == null)
/*     */       return; 
/* 183 */     String dragSound = (String)soundSet.getSoundEventIds().get(ItemSoundEvent.Drop);
/* 184 */     if (dragSound == null)
/*     */       return; 
/* 186 */     int dragSoundIndex = SoundEvent.getAssetMap().getIndex(dragSound);
/* 187 */     if (dragSoundIndex == 0)
/*     */       return; 
/* 189 */     SoundUtil.playSoundEvent2d(ref, dragSoundIndex, SoundCategory.UI, (ComponentAccessor)store);
/*     */   }
/*     */   private void changeBlockType(@Nonnull Ref<EntityStore> ref, boolean down, @Nonnull Store<EntityStore> store) {
/*     */     int newIndex;
/* 193 */     ItemStack item = this.inputContainer.getItemStack((short)0);
/* 194 */     if (item == null) {
/*     */       return;
/*     */     }
/*     */     
/* 198 */     BlockGroup set = BlockGroup.findItemGroup(item.getItem());
/* 199 */     if (set == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 204 */     int currentIndex = -1;
/* 205 */     for (int i = 0; i < set.size(); i++) {
/* 206 */       if (set.get(i).equals(item.getItem().getId())) {
/* 207 */         currentIndex = i;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 212 */     if (currentIndex == -1) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 218 */     if (down) {
/* 219 */       newIndex = (currentIndex - 1 + set.size()) % set.size();
/*     */     } else {
/* 221 */       newIndex = (currentIndex + 1) % set.size();
/*     */     } 
/* 223 */     String next = set.get(newIndex);
/* 224 */     Item desiredItem = (Item)Item.getAssetMap().getAsset(next);
/*     */     
/* 226 */     if (desiredItem == null) {
/*     */       return;
/*     */     }
/*     */     
/* 230 */     this.inputContainer.replaceItemStackInSlot((short)0, item, new ItemStack(next, item.getQuantity()));
/* 231 */     playCraftSound(ref, store, desiredItem);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ItemContainer getItemContainer() {
/* 237 */     return (ItemContainer)this.combinedItemContainer;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 242 */     super.onOpen0(ref, store);
/*     */     
/* 244 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 245 */     assert playerComponent != null;
/*     */     
/* 247 */     Inventory inventory = playerComponent.getInventory();
/*     */     
/* 249 */     this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
/*     */           this.windowData.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, (ItemContainer)inventory.getCombinedHotbarFirst()));
/*     */           
/*     */           invalidate();
/*     */         });
/* 254 */     this.windowData.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, (ItemContainer)inventory.getCombinedHotbarFirst()));
/* 255 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 260 */     super.onClose0(ref, componentAccessor);
/*     */     
/* 262 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 263 */     assert playerComponent != null;
/*     */     
/* 265 */     List<ItemStack> itemStacks = this.inputContainer.dropAllItemStacks();
/* 266 */     SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, (ItemContainer)playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
/*     */     
/* 268 */     CraftingManager craftingManagerComponent = (CraftingManager)componentAccessor.getComponent(ref, CraftingManager.getComponentType());
/* 269 */     assert craftingManagerComponent != null;
/*     */     
/* 271 */     craftingManagerComponent.cancelAllCrafting(ref, componentAccessor);
/*     */     
/* 273 */     if (this.inventoryRegistration != null) {
/* 274 */       this.inventoryRegistration.unregister();
/* 275 */       this.inventoryRegistration = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateRecipes() {
/* 280 */     invalidate();
/*     */     
/* 282 */     this.optionsContainer.clear();
/* 283 */     this.optionSlotToRecipeMap.clear();
/*     */     
/* 285 */     ItemStack inputStack = this.inputContainer.getItemStack((short)0);
/* 286 */     ObjectList<CraftingRecipe> matchingRecipes = getMatchingRecipes(inputStack);
/*     */     
/* 288 */     if (matchingRecipes == null)
/*     */       return; 
/* 290 */     StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
/* 291 */     sortRecipes(matchingRecipes, structuralBench);
/*     */     
/* 293 */     int dividerIndex = 0;
/*     */     
/* 295 */     while (dividerIndex < matchingRecipes.size()) {
/* 296 */       CraftingRecipe recipe = (CraftingRecipe)matchingRecipes.get(dividerIndex);
/* 297 */       if (!hasHeaderCategory(structuralBench, recipe)) {
/*     */         break;
/*     */       }
/* 300 */       dividerIndex++;
/*     */     } 
/*     */     
/* 303 */     this.windowData.addProperty("dividerIndex", Integer.valueOf(dividerIndex));
/*     */     
/* 305 */     this.optionsContainer.clear();
/* 306 */     short index = 0;
/*     */     
/* 308 */     for (int i = 0, bound = matchingRecipes.size(); i < bound; i++) {
/* 309 */       CraftingRecipe match = (CraftingRecipe)matchingRecipes.get(i);
/* 310 */       for (BenchRequirement requirement : match.getBenchRequirement()) {
/* 311 */         if (requirement.type == this.bench.getType() && requirement.id.equals(this.bench.getId())) {
/*     */ 
/*     */ 
/*     */           
/* 315 */           List<ItemStack> output = CraftingManager.getOutputItemStacks(match);
/* 316 */           this.optionsContainer.setItemStackForSlot(index, output.getFirst(), false);
/* 317 */           this.optionSlotToRecipeMap.put(index, match.getId());
/* 318 */           index = (short)(index + 1);
/*     */         } 
/*     */       } 
/*     */     } 
/* 322 */     JsonArray optionSlotRecipes = new JsonArray();
/* 323 */     for (int j = 0; j < this.optionsContainer.getCapacity(); j++) {
/* 324 */       String recipeId = (String)this.optionSlotToRecipeMap.get(j);
/* 325 */       if (recipeId != null) {
/* 326 */         optionSlotRecipes.add(recipeId);
/*     */       }
/*     */     } 
/*     */     
/* 330 */     this.windowData.add("optionSlotRecipes", (JsonElement)optionSlotRecipes);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private ObjectList<CraftingRecipe> getMatchingRecipes(@Nullable ItemStack inputStack) {
/* 335 */     if (inputStack == null) {
/* 336 */       return null;
/*     */     }
/*     */     
/* 339 */     List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
/* 340 */     if (recipes.isEmpty()) {
/* 341 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 345 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/*     */     
/* 347 */     for (int i = 0, bound = recipes.size(); i < bound; i++) {
/* 348 */       CraftingRecipe recipe = recipes.get(i);
/* 349 */       List<MaterialQuantity> inputMaterials = CraftingManager.getInputMaterials(recipe);
/*     */ 
/*     */       
/* 352 */       if (inputMaterials.size() == 1)
/*     */       {
/* 354 */         if (CraftingManager.matches(inputMaterials.getFirst(), inputStack)) {
/* 355 */           objectArrayList.add(recipe);
/*     */         }
/*     */       }
/*     */     } 
/* 359 */     if (objectArrayList.isEmpty()) {
/* 360 */       return null;
/*     */     }
/* 362 */     return (ObjectList<CraftingRecipe>)objectArrayList;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\StructuralCraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */