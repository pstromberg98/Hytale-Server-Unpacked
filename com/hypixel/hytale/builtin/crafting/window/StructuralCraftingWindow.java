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
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
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
/*  47 */   private final Int2ObjectMap<String> optionSlotToRecipeMap = (Int2ObjectMap<String>)new Int2ObjectOpenHashMap();
/*     */   private final SimpleItemContainer optionsContainer;
/*     */   private final CombinedItemContainer combinedItemContainer;
/*     */   private int selectedSlot;
/*     */   @Nullable
/*     */   private EventRegistration inventoryRegistration;
/*     */   
/*     */   public StructuralCraftingWindow(BenchState benchState) {
/*  55 */     super(WindowType.StructuralCrafting, benchState);
/*     */     
/*  57 */     this.inputContainer = new SimpleItemContainer((short)1);
/*  58 */     this.inputContainer.registerChangeEvent(e -> updateRecipes());
/*  59 */     this.inputContainer.setSlotFilter(FilterActionType.ADD, (short)0, this::isValidInput);
/*     */     
/*  61 */     this.optionsContainer = new SimpleItemContainer((short)64);
/*  62 */     this.optionsContainer.setGlobalFilter(FilterType.DENY_ALL);
/*     */     
/*  64 */     this.combinedItemContainer = new CombinedItemContainer(new ItemContainer[] { (ItemContainer)this.inputContainer, (ItemContainer)this.optionsContainer });
/*  65 */     this.windowData.addProperty("selected", Integer.valueOf(this.selectedSlot));
/*     */     
/*  67 */     StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
/*  68 */     this.windowData.addProperty("allowBlockGroupCycling", Boolean.valueOf(structuralBench.shouldAllowBlockGroupCycling()));
/*  69 */     this.windowData.addProperty("alwaysShowInventoryHints", Boolean.valueOf(structuralBench.shouldAlwaysShowInventoryHints()));
/*     */   }
/*     */   
/*     */   private boolean isValidInput(FilterActionType filterActionType, ItemContainer itemContainer, short i, ItemStack itemStack) {
/*  73 */     if (filterActionType != FilterActionType.ADD) return true;
/*     */     
/*  75 */     ObjectList<CraftingRecipe> matchingRecipes = getMatchingRecipes(itemStack);
/*     */     
/*  77 */     return (matchingRecipes != null && !matchingRecipes.isEmpty());
/*     */   }
/*     */   
/*     */   private static void sortRecipes(ObjectList<CraftingRecipe> matching, StructuralCraftingBench structuralBench) {
/*  81 */     matching.sort((a, b) -> {
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
/*  98 */     for (BenchRequirement requirement : recipe.getBenchRequirement()) {
/*  99 */       if (requirement.type == bench.getType() && requirement.id.equals(bench.getId()) && requirement.categories != null) {
/* 100 */         for (String category : requirement.categories) {
/* 101 */           if (bench.isHeaderCategory(category)) {
/* 102 */             return true;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 107 */     return false;
/*     */   }
/*     */   
/*     */   private static int getSortingPriority(StructuralCraftingBench bench, CraftingRecipe recipe) {
/* 111 */     int priority = Integer.MAX_VALUE;
/*     */     
/* 113 */     for (BenchRequirement requirement : recipe.getBenchRequirement()) {
/* 114 */       if (requirement.type == bench.getType() && requirement.id.equals(bench.getId()) && requirement.categories != null) {
/* 115 */         for (String category : requirement.categories) {
/* 116 */           priority = Math.min(priority, bench.getCategoryIndex(category));
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 122 */     return priority;
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
/*     */     //   #127	-> 0
/*     */     //   #129	-> 13
/*     */     //   #130	-> 60
/*     */     //   #131	-> 67
/*     */     //   #132	-> 85
/*     */     //   #133	-> 94
/*     */     //   #134	-> 100
/*     */     //   #135	-> 116
/*     */     //   #137	-> 120
/*     */     //   #138	-> 123
/*     */     //   #139	-> 130
/*     */     //   #140	-> 144
/*     */     //   #141	-> 149
/*     */     //   #142	-> 156
/*     */     //   #144	-> 174
/*     */     //   #145	-> 179
/*     */     //   #148	-> 180
/*     */     //   #149	-> 193
/*     */     //   #150	-> 198
/*     */     //   #153	-> 199
/*     */     //   #154	-> 206
/*     */     //   #156	-> 213
/*     */     //   #157	-> 218
/*     */     //   #159	-> 231
/*     */     //   #160	-> 236
/*     */     //   #164	-> 244
/*     */     //   #165	-> 265
/*     */     //   #167	-> 269
/*     */     //   #168	-> 272
/*     */     //   #169	-> 279
/*     */     //   #170	-> 292
/*     */     //   #176	-> 306
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
/* 179 */     ItemSoundSet soundSet = (ItemSoundSet)ItemSoundSet.getAssetMap().getAsset(item.getItemSoundSetIndex());
/* 180 */     if (soundSet == null)
/*     */       return; 
/* 182 */     String dragSound = (String)soundSet.getSoundEventIds().get(ItemSoundEvent.Drop);
/* 183 */     if (dragSound == null)
/*     */       return; 
/* 185 */     int dragSoundIndex = SoundEvent.getAssetMap().getIndex(dragSound);
/* 186 */     if (dragSoundIndex == 0)
/*     */       return; 
/* 188 */     SoundUtil.playSoundEvent2d(ref, dragSoundIndex, SoundCategory.UI, (ComponentAccessor)store);
/*     */   }
/*     */   private void changeBlockType(@Nonnull Ref<EntityStore> ref, boolean down, @Nonnull Store<EntityStore> store) {
/*     */     int newIndex;
/* 192 */     ItemStack item = this.inputContainer.getItemStack((short)0);
/* 193 */     if (item == null) {
/*     */       return;
/*     */     }
/*     */     
/* 197 */     BlockGroup set = BlockGroup.findItemGroup(item.getItem());
/* 198 */     if (set == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 203 */     int currentIndex = -1;
/* 204 */     for (int i = 0; i < set.size(); i++) {
/* 205 */       if (set.get(i).equals(item.getItem().getId())) {
/* 206 */         currentIndex = i;
/*     */         
/*     */         break;
/*     */       } 
/*     */     } 
/* 211 */     if (currentIndex == -1) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 217 */     if (down) {
/* 218 */       newIndex = (currentIndex - 1 + set.size()) % set.size();
/*     */     } else {
/* 220 */       newIndex = (currentIndex + 1) % set.size();
/*     */     } 
/* 222 */     String next = set.get(newIndex);
/* 223 */     Item desiredItem = (Item)Item.getAssetMap().getAsset(next);
/*     */     
/* 225 */     if (desiredItem == null) {
/*     */       return;
/*     */     }
/*     */     
/* 229 */     this.inputContainer.replaceItemStackInSlot((short)0, item, new ItemStack(next, item.getQuantity()));
/* 230 */     playCraftSound(ref, store, desiredItem);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ItemContainer getItemContainer() {
/* 236 */     return (ItemContainer)this.combinedItemContainer;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean onOpen0() {
/* 241 */     super.onOpen0();
/*     */     
/* 243 */     PlayerRef playerRef = getPlayerRef();
/* 244 */     Ref<EntityStore> ref = playerRef.getReference();
/* 245 */     Store<EntityStore> store = ref.getStore();
/*     */     
/* 247 */     Player player = (Player)store.getComponent(ref, Player.getComponentType());
/* 248 */     Inventory inventory = player.getInventory();
/*     */     
/* 250 */     this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
/*     */           this.windowData.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, (ItemContainer)inventory.getCombinedHotbarFirst()));
/*     */           
/*     */           invalidate();
/*     */         });
/* 255 */     this.windowData.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(CraftingPlugin.getBenchRecipes(this.bench), 0, (ItemContainer)inventory.getCombinedHotbarFirst()));
/* 256 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClose0() {
/* 261 */     super.onClose0();
/*     */     
/* 263 */     PlayerRef playerRef = getPlayerRef();
/* 264 */     Ref<EntityStore> ref = playerRef.getReference();
/* 265 */     Store<EntityStore> store = ref.getStore();
/*     */     
/* 267 */     Player player = (Player)store.getComponent(ref, Player.getComponentType());
/* 268 */     List<ItemStack> itemStacks = this.inputContainer.dropAllItemStacks();
/* 269 */     SimpleItemContainer.addOrDropItemStacks((ComponentAccessor)store, ref, (ItemContainer)player.getInventory().getCombinedHotbarFirst(), itemStacks);
/*     */     
/* 271 */     CraftingManager craftingManager = (CraftingManager)store.getComponent(ref, CraftingManager.getComponentType());
/* 272 */     craftingManager.cancelAllCrafting(ref, (ComponentAccessor)store);
/*     */     
/* 274 */     if (this.inventoryRegistration != null) {
/* 275 */       this.inventoryRegistration.unregister();
/* 276 */       this.inventoryRegistration = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateRecipes() {
/* 281 */     invalidate();
/*     */     
/* 283 */     this.optionsContainer.clear();
/* 284 */     this.optionSlotToRecipeMap.clear();
/*     */     
/* 286 */     ItemStack inputStack = this.inputContainer.getItemStack((short)0);
/* 287 */     ObjectList<CraftingRecipe> matchingRecipes = getMatchingRecipes(inputStack);
/*     */     
/* 289 */     if (matchingRecipes == null)
/*     */       return; 
/* 291 */     StructuralCraftingBench structuralBench = (StructuralCraftingBench)this.bench;
/* 292 */     sortRecipes(matchingRecipes, structuralBench);
/*     */     
/* 294 */     int dividerIndex = 0;
/*     */     
/* 296 */     while (dividerIndex < matchingRecipes.size()) {
/* 297 */       CraftingRecipe recipe = (CraftingRecipe)matchingRecipes.get(dividerIndex);
/* 298 */       if (!hasHeaderCategory(structuralBench, recipe)) {
/*     */         break;
/*     */       }
/* 301 */       dividerIndex++;
/*     */     } 
/*     */     
/* 304 */     this.windowData.addProperty("dividerIndex", Integer.valueOf(dividerIndex));
/*     */     
/* 306 */     this.optionsContainer.clear();
/* 307 */     short index = 0;
/*     */     
/* 309 */     for (int i = 0, bound = matchingRecipes.size(); i < bound; i++) {
/* 310 */       CraftingRecipe match = (CraftingRecipe)matchingRecipes.get(i);
/* 311 */       for (BenchRequirement requirement : match.getBenchRequirement()) {
/* 312 */         if (requirement.type == this.bench.getType() && requirement.id.equals(this.bench.getId())) {
/*     */ 
/*     */ 
/*     */           
/* 316 */           List<ItemStack> output = CraftingManager.getOutputItemStacks(match);
/* 317 */           this.optionsContainer.setItemStackForSlot(index, output.getFirst(), false);
/* 318 */           this.optionSlotToRecipeMap.put(index, match.getId());
/* 319 */           index = (short)(index + 1);
/*     */         } 
/*     */       } 
/*     */     } 
/* 323 */     JsonArray optionSlotRecipes = new JsonArray();
/* 324 */     for (int j = 0; j < this.optionsContainer.getCapacity(); j++) {
/* 325 */       String recipeId = (String)this.optionSlotToRecipeMap.get(j);
/* 326 */       if (recipeId != null) {
/* 327 */         optionSlotRecipes.add(recipeId);
/*     */       }
/*     */     } 
/*     */     
/* 331 */     this.windowData.add("optionSlotRecipes", (JsonElement)optionSlotRecipes);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private ObjectList<CraftingRecipe> getMatchingRecipes(@Nullable ItemStack inputStack) {
/* 336 */     if (inputStack == null) {
/* 337 */       return null;
/*     */     }
/*     */     
/* 340 */     List<CraftingRecipe> recipes = CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId());
/* 341 */     if (recipes.isEmpty()) {
/* 342 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 346 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/*     */     
/* 348 */     for (int i = 0, bound = recipes.size(); i < bound; i++) {
/* 349 */       CraftingRecipe recipe = recipes.get(i);
/* 350 */       List<MaterialQuantity> inputMaterials = CraftingManager.getInputMaterials(recipe);
/*     */ 
/*     */       
/* 353 */       if (inputMaterials.size() == 1)
/*     */       {
/* 355 */         if (CraftingManager.matches(inputMaterials.getFirst(), inputStack)) {
/* 356 */           objectArrayList.add(recipe);
/*     */         }
/*     */       }
/*     */     } 
/* 360 */     if (objectArrayList.isEmpty()) {
/* 361 */       return null;
/*     */     }
/* 363 */     return (ObjectList<CraftingRecipe>)objectArrayList;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\StructuralCraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */