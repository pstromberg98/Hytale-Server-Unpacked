/*     */ package com.hypixel.hytale.builtin.crafting.window;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventPriority;
/*     */ import com.hypixel.hytale.event.EventRegistration;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.CraftingBench;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.DiagramCraftingBench;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.ItemContainerWindow;
/*     */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.FilterActionType;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
/*     */ import com.hypixel.hytale.server.core.inventory.container.filter.SlotFilter;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DiagramCraftingWindow
/*     */   extends CraftingWindow
/*     */   implements ItemContainerWindow
/*     */ {
/*  49 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private String category;
/*     */   
/*     */   private String itemCategory;
/*     */   
/*     */   @Nullable
/*     */   private CraftingBench.BenchItemCategory benchItemCategory;
/*     */   
/*     */   private SimpleItemContainer inputPrimaryContainer;
/*     */   
/*     */   private SimpleItemContainer inputSecondaryContainer;
/*     */   private CombinedItemContainer combinedInputItemContainer;
/*     */   private SimpleItemContainer outputContainer;
/*     */   private CombinedItemContainer combinedItemContainer;
/*     */   private EventRegistration<?, ?> inventoryRegistration;
/*     */   
/*     */   public DiagramCraftingWindow(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> store, @Nonnull BenchState benchState) {
/*  67 */     super(WindowType.DiagramCrafting, benchState);
/*  68 */     DiagramCraftingBench bench = (DiagramCraftingBench)this.bench;
/*     */     
/*  70 */     if (bench.getCategories() != null && (bench.getCategories()).length > 0) {
/*  71 */       CraftingBench.BenchCategory benchCategory = bench.getCategories()[0];
/*  72 */       this.category = benchCategory.getId();
/*  73 */       if (benchCategory.getItemCategories() != null && (benchCategory.getItemCategories()).length > 0) {
/*  74 */         this.itemCategory = benchCategory.getItemCategories()[0].getId();
/*     */       }
/*     */     } 
/*     */     
/*  78 */     this.benchItemCategory = getBenchItemCategory(this.category, this.itemCategory);
/*  79 */     if (this.benchItemCategory == null) {
/*  80 */       throw new IllegalArgumentException("Failed to get category!");
/*     */     }
/*     */     
/*  83 */     updateInventory(ref, store, this.benchItemCategory);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() {
/*  88 */     if (this.inventoryRegistration.isRegistered()) throw new IllegalStateException("Failed to unregister inventory event!");
/*     */   
/*     */   }
/*     */   
/*     */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/*  93 */     boolean result = super.onOpen0(ref, store);
/*     */     
/*  95 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/*  96 */     assert playerComponent != null;
/*     */     
/*  98 */     Inventory inventory = playerComponent.getInventory();
/*     */     
/* 100 */     updateInput((ItemContainer)null, ref, store);
/* 101 */     this.inventoryRegistration = inventory.getCombinedHotbarFirst().registerChangeEvent(event -> {
/*     */           ObjectArrayList objectArrayList = new ObjectArrayList();
/*     */           
/*     */           this.windowData.add("slots", (JsonElement)generateSlots(inventory.getCombinedHotbarFirst(), (List<CraftingRecipe>)objectArrayList));
/*     */           invalidate();
/*     */         });
/* 107 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 112 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 113 */     assert playerComponent != null;
/*     */     
/* 115 */     List<ItemStack> itemStacks = this.combinedInputItemContainer.dropAllItemStacks();
/* 116 */     SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, (ItemContainer)playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
/*     */     
/* 118 */     CraftingManager craftingManagerComponent = (CraftingManager)componentAccessor.getComponent(ref, CraftingManager.getComponentType());
/* 119 */     assert craftingManagerComponent != null;
/* 120 */     craftingManagerComponent.cancelAllCrafting(ref, componentAccessor);
/*     */     
/* 122 */     this.inventoryRegistration.unregister();
/*     */     
/* 124 */     super.onClose0(ref, componentAccessor);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
/*     */     // Byte code:
/*     */     //   0: aload_2
/*     */     //   1: invokevirtual getExternalData : ()Ljava/lang/Object;
/*     */     //   4: checkcast com/hypixel/hytale/server/core/universe/world/storage/EntityStore
/*     */     //   7: invokevirtual getWorld : ()Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   10: astore #4
/*     */     //   12: aload_2
/*     */     //   13: aload_1
/*     */     //   14: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   17: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   20: checkcast com/hypixel/hytale/server/core/universe/PlayerRef
/*     */     //   23: astore #5
/*     */     //   25: getstatic com/hypixel/hytale/builtin/crafting/window/DiagramCraftingWindow.$assertionsDisabled : Z
/*     */     //   28: ifne -> 44
/*     */     //   31: aload #5
/*     */     //   33: ifnonnull -> 44
/*     */     //   36: new java/lang/AssertionError
/*     */     //   39: dup
/*     */     //   40: invokespecial <init> : ()V
/*     */     //   43: athrow
/*     */     //   44: aload_2
/*     */     //   45: aload_1
/*     */     //   46: invokestatic getComponentType : ()Lcom/hypixel/hytale/component/ComponentType;
/*     */     //   49: invokevirtual getComponent : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentType;)Lcom/hypixel/hytale/component/Component;
/*     */     //   52: checkcast com/hypixel/hytale/builtin/crafting/component/CraftingManager
/*     */     //   55: astore #6
/*     */     //   57: getstatic com/hypixel/hytale/builtin/crafting/window/DiagramCraftingWindow.$assertionsDisabled : Z
/*     */     //   60: ifne -> 76
/*     */     //   63: aload #6
/*     */     //   65: ifnonnull -> 76
/*     */     //   68: new java/lang/AssertionError
/*     */     //   71: dup
/*     */     //   72: invokespecial <init> : ()V
/*     */     //   75: athrow
/*     */     //   76: aload_3
/*     */     //   77: dup
/*     */     //   78: invokestatic requireNonNull : (Ljava/lang/Object;)Ljava/lang/Object;
/*     */     //   81: pop
/*     */     //   82: astore #7
/*     */     //   84: iconst_0
/*     */     //   85: istore #8
/*     */     //   87: aload #7
/*     */     //   89: iload #8
/*     */     //   91: <illegal opcode> typeSwitch : (Ljava/lang/Object;I)I
/*     */     //   96: tableswitch default -> 446, 0 -> 124, 1 -> 142, 2 -> 222
/*     */     //   124: aload #7
/*     */     //   126: checkcast com/hypixel/hytale/protocol/packets/window/CancelCraftingAction
/*     */     //   129: astore #9
/*     */     //   131: aload #6
/*     */     //   133: aload_1
/*     */     //   134: aload_2
/*     */     //   135: invokevirtual cancelAllCrafting : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentAccessor;)Z
/*     */     //   138: pop
/*     */     //   139: goto -> 446
/*     */     //   142: aload #7
/*     */     //   144: checkcast com/hypixel/hytale/protocol/packets/window/UpdateCategoryAction
/*     */     //   147: astore #10
/*     */     //   149: aload_0
/*     */     //   150: aload #10
/*     */     //   152: getfield category : Ljava/lang/String;
/*     */     //   155: putfield category : Ljava/lang/String;
/*     */     //   158: aload_0
/*     */     //   159: aload #10
/*     */     //   161: getfield itemCategory : Ljava/lang/String;
/*     */     //   164: putfield itemCategory : Ljava/lang/String;
/*     */     //   167: aload_0
/*     */     //   168: aload_0
/*     */     //   169: aload_0
/*     */     //   170: getfield category : Ljava/lang/String;
/*     */     //   173: aload_0
/*     */     //   174: getfield itemCategory : Ljava/lang/String;
/*     */     //   177: invokevirtual getBenchItemCategory : (Ljava/lang/String;Ljava/lang/String;)Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/CraftingBench$BenchItemCategory;
/*     */     //   180: putfield benchItemCategory : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/CraftingBench$BenchItemCategory;
/*     */     //   183: aload_0
/*     */     //   184: getfield benchItemCategory : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/CraftingBench$BenchItemCategory;
/*     */     //   187: ifnull -> 203
/*     */     //   190: aload_0
/*     */     //   191: aload_1
/*     */     //   192: aload_2
/*     */     //   193: aload_0
/*     */     //   194: getfield benchItemCategory : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/CraftingBench$BenchItemCategory;
/*     */     //   197: invokevirtual updateInventory : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/CraftingBench$BenchItemCategory;)V
/*     */     //   200: goto -> 446
/*     */     //   203: aload #5
/*     */     //   205: ldc 'server.ui.diagramcraftingwindow.invalidCategory'
/*     */     //   207: invokestatic translation : (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/Message;
/*     */     //   210: invokevirtual sendMessage : (Lcom/hypixel/hytale/server/core/Message;)V
/*     */     //   213: aload_0
/*     */     //   214: aload_1
/*     */     //   215: aload_2
/*     */     //   216: invokevirtual close : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentAccessor;)V
/*     */     //   219: goto -> 446
/*     */     //   222: aload #7
/*     */     //   224: checkcast com/hypixel/hytale/protocol/packets/window/CraftItemAction
/*     */     //   227: astore #11
/*     */     //   229: aload_0
/*     */     //   230: getfield outputContainer : Lcom/hypixel/hytale/server/core/inventory/container/SimpleItemContainer;
/*     */     //   233: iconst_0
/*     */     //   234: invokevirtual getItemStack : (S)Lcom/hypixel/hytale/server/core/inventory/ItemStack;
/*     */     //   237: astore #12
/*     */     //   239: aload #12
/*     */     //   241: ifnull -> 252
/*     */     //   244: aload #12
/*     */     //   246: invokevirtual isEmpty : ()Z
/*     */     //   249: ifeq -> 263
/*     */     //   252: aload #5
/*     */     //   254: ldc 'server.ui.diagramcraftingwindow.noOutputItem'
/*     */     //   256: invokestatic translation : (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/Message;
/*     */     //   259: invokevirtual sendMessage : (Lcom/hypixel/hytale/server/core/Message;)V
/*     */     //   262: return
/*     */     //   263: new it/unimi/dsi/fastutil/objects/ObjectArrayList
/*     */     //   266: dup
/*     */     //   267: invokespecial <init> : ()V
/*     */     //   270: astore #13
/*     */     //   272: aload_0
/*     */     //   273: aload_1
/*     */     //   274: aload #13
/*     */     //   276: aload_2
/*     */     //   277: invokevirtual collectRecipes : (Lcom/hypixel/hytale/component/Ref;Ljava/util/List;Lcom/hypixel/hytale/component/Store;)Z
/*     */     //   280: istore #14
/*     */     //   282: aload #13
/*     */     //   284: invokeinterface size : ()I
/*     */     //   289: iconst_1
/*     */     //   290: if_icmpne -> 298
/*     */     //   293: iload #14
/*     */     //   295: ifne -> 309
/*     */     //   298: aload #5
/*     */     //   300: ldc 'server.ui.diagramcraftingwindow.failedVerifyRecipy'
/*     */     //   302: invokestatic translation : (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/Message;
/*     */     //   305: invokevirtual sendMessage : (Lcom/hypixel/hytale/server/core/Message;)V
/*     */     //   308: return
/*     */     //   309: aload #13
/*     */     //   311: invokeinterface getFirst : ()Ljava/lang/Object;
/*     */     //   316: checkcast com/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe
/*     */     //   319: astore #15
/*     */     //   321: aload #6
/*     */     //   323: aload_1
/*     */     //   324: aload_2
/*     */     //   325: aload_0
/*     */     //   326: iconst_0
/*     */     //   327: aload #15
/*     */     //   329: iconst_1
/*     */     //   330: aload_0
/*     */     //   331: getfield combinedInputItemContainer : Lcom/hypixel/hytale/server/core/inventory/container/CombinedItemContainer;
/*     */     //   334: getstatic com/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType.ORDERED : Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType;
/*     */     //   337: invokevirtual queueCraft : (Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/ComponentAccessor;Lcom/hypixel/hytale/builtin/crafting/window/CraftingWindow;ILcom/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe;ILcom/hypixel/hytale/server/core/inventory/container/ItemContainer;Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager$InputRemovalType;)Z
/*     */     //   340: pop
/*     */     //   341: aload #15
/*     */     //   343: invokevirtual getTimeSeconds : ()F
/*     */     //   346: fconst_0
/*     */     //   347: fcmpl
/*     */     //   348: ifle -> 356
/*     */     //   351: ldc 'CraftCompleted'
/*     */     //   353: goto -> 359
/*     */     //   356: ldc_w 'CraftCompletedInstant'
/*     */     //   359: astore #16
/*     */     //   361: aload_0
/*     */     //   362: aload #16
/*     */     //   364: aload #4
/*     */     //   366: invokevirtual setBlockInteractionState : (Ljava/lang/String;Lcom/hypixel/hytale/server/core/universe/world/World;)V
/*     */     //   369: aload_0
/*     */     //   370: getfield bench : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/Bench;
/*     */     //   373: invokevirtual getCompletedSoundEventIndex : ()I
/*     */     //   376: ifeq -> 420
/*     */     //   379: aload_0
/*     */     //   380: getfield bench : Lcom/hypixel/hytale/server/core/asset/type/blocktype/config/bench/Bench;
/*     */     //   383: invokevirtual getCompletedSoundEventIndex : ()I
/*     */     //   386: getstatic com/hypixel/hytale/protocol/SoundCategory.SFX : Lcom/hypixel/hytale/protocol/SoundCategory;
/*     */     //   389: aload_0
/*     */     //   390: getfield x : I
/*     */     //   393: i2d
/*     */     //   394: ldc2_w 0.5
/*     */     //   397: dadd
/*     */     //   398: aload_0
/*     */     //   399: getfield y : I
/*     */     //   402: i2d
/*     */     //   403: ldc2_w 0.5
/*     */     //   406: dadd
/*     */     //   407: aload_0
/*     */     //   408: getfield z : I
/*     */     //   411: i2d
/*     */     //   412: ldc2_w 0.5
/*     */     //   415: dadd
/*     */     //   416: aload_2
/*     */     //   417: invokestatic playSoundEvent3d : (ILcom/hypixel/hytale/protocol/SoundCategory;DDDLcom/hypixel/hytale/component/ComponentAccessor;)V
/*     */     //   420: aload_1
/*     */     //   421: aload #15
/*     */     //   423: invokevirtual getId : ()Ljava/lang/String;
/*     */     //   426: aload_2
/*     */     //   427: invokestatic learnRecipe : (Lcom/hypixel/hytale/component/Ref;Ljava/lang/String;Lcom/hypixel/hytale/component/ComponentAccessor;)Z
/*     */     //   430: ifeq -> 443
/*     */     //   433: aload_0
/*     */     //   434: aload_0
/*     */     //   435: getfield outputContainer : Lcom/hypixel/hytale/server/core/inventory/container/SimpleItemContainer;
/*     */     //   438: aload_1
/*     */     //   439: aload_2
/*     */     //   440: invokevirtual updateInput : (Lcom/hypixel/hytale/server/core/inventory/container/ItemContainer;Lcom/hypixel/hytale/component/Ref;Lcom/hypixel/hytale/component/Store;)V
/*     */     //   443: goto -> 446
/*     */     //   446: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #129	-> 0
/*     */     //   #131	-> 12
/*     */     //   #132	-> 25
/*     */     //   #134	-> 44
/*     */     //   #135	-> 57
/*     */     //   #137	-> 76
/*     */     //   #138	-> 124
/*     */     //   #139	-> 142
/*     */     //   #140	-> 149
/*     */     //   #141	-> 158
/*     */     //   #142	-> 167
/*     */     //   #143	-> 183
/*     */     //   #144	-> 190
/*     */     //   #146	-> 203
/*     */     //   #147	-> 213
/*     */     //   #149	-> 219
/*     */     //   #150	-> 222
/*     */     //   #151	-> 229
/*     */     //   #152	-> 239
/*     */     //   #153	-> 252
/*     */     //   #154	-> 262
/*     */     //   #157	-> 263
/*     */     //   #158	-> 272
/*     */     //   #160	-> 282
/*     */     //   #161	-> 298
/*     */     //   #162	-> 308
/*     */     //   #165	-> 309
/*     */     //   #166	-> 321
/*     */     //   #168	-> 341
/*     */     //   #169	-> 361
/*     */     //   #171	-> 369
/*     */     //   #172	-> 379
/*     */     //   #176	-> 420
/*     */     //   #177	-> 433
/*     */     //   #179	-> 443
/*     */     //   #183	-> 446
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   131	11	9	ignored	Lcom/hypixel/hytale/protocol/packets/window/CancelCraftingAction;
/*     */     //   149	73	10	updateAction	Lcom/hypixel/hytale/protocol/packets/window/UpdateCategoryAction;
/*     */     //   239	204	12	itemStack	Lcom/hypixel/hytale/server/core/inventory/ItemStack;
/*     */     //   272	171	13	recipes	Lit/unimi/dsi/fastutil/objects/ObjectList;
/*     */     //   282	161	14	allSlotsFull	Z
/*     */     //   321	122	15	recipe	Lcom/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe;
/*     */     //   361	82	16	completedState	Ljava/lang/String;
/*     */     //   229	217	11	ignored	Lcom/hypixel/hytale/protocol/packets/window/CraftItemAction;
/*     */     //   0	447	0	this	Lcom/hypixel/hytale/builtin/crafting/window/DiagramCraftingWindow;
/*     */     //   0	447	1	ref	Lcom/hypixel/hytale/component/Ref;
/*     */     //   0	447	2	store	Lcom/hypixel/hytale/component/Store;
/*     */     //   0	447	3	action	Lcom/hypixel/hytale/protocol/packets/window/WindowAction;
/*     */     //   12	435	4	world	Lcom/hypixel/hytale/server/core/universe/world/World;
/*     */     //   25	422	5	playerRefComponent	Lcom/hypixel/hytale/server/core/universe/PlayerRef;
/*     */     //   57	390	6	craftingManagerComponent	Lcom/hypixel/hytale/builtin/crafting/component/CraftingManager;
/*     */     // Local variable type table:
/*     */     //   start	length	slot	name	signature
/*     */     //   272	171	13	recipes	Lit/unimi/dsi/fastutil/objects/ObjectList<Lcom/hypixel/hytale/server/core/asset/type/item/config/CraftingRecipe;>;
/*     */     //   0	447	1	ref	Lcom/hypixel/hytale/component/Ref<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
/*     */     //   0	447	2	store	Lcom/hypixel/hytale/component/Store<Lcom/hypixel/hytale/server/core/universe/world/storage/EntityStore;>;
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
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ItemContainer getItemContainer() {
/* 188 */     return (ItemContainer)this.combinedItemContainer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private CraftingBench.BenchItemCategory getBenchItemCategory(@Nullable String category, @Nullable String itemCategory) {
/* 200 */     if (category == null || itemCategory == null) return null;
/*     */     
/* 202 */     DiagramCraftingBench craftingBench = (DiagramCraftingBench)this.bench;
/* 203 */     for (CraftingBench.BenchCategory benchCategory : craftingBench.getCategories()) {
/*     */       
/* 205 */       if (category.equals(benchCategory.getId())) {
/* 206 */         for (CraftingBench.BenchItemCategory benchItemCategory : benchCategory.getItemCategories()) {
/* 207 */           if (itemCategory.equals(benchItemCategory.getId())) {
/* 208 */             return benchItemCategory;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 213 */     return null;
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
/*     */   private void updateInventory(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull CraftingBench.BenchItemCategory benchItemCategory) {
/* 226 */     if (this.combinedInputItemContainer != null) {
/* 227 */       Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 228 */       assert playerComponent != null;
/*     */       
/* 230 */       List<ItemStack> itemStacks = this.combinedInputItemContainer.dropAllItemStacks();
/* 231 */       SimpleItemContainer.addOrDropItemStacks(componentAccessor, ref, (ItemContainer)playerComponent.getInventory().getCombinedHotbarFirst(), itemStacks);
/*     */     } 
/*     */     
/* 234 */     this.inputPrimaryContainer = new SimpleItemContainer((short)1);
/* 235 */     this
/*     */       
/* 237 */       .inputSecondaryContainer = new SimpleItemContainer((short)(benchItemCategory.getSlots() + (benchItemCategory.isSpecialSlot() ? 1 : 0)));
/*     */     
/* 239 */     this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
/* 240 */     this.combinedInputItemContainer = new CombinedItemContainer(new ItemContainer[] { (ItemContainer)this.inputPrimaryContainer, (ItemContainer)this.inputSecondaryContainer });
/* 241 */     this.combinedInputItemContainer.registerChangeEvent(EventPriority.LAST, this::updateInput);
/*     */     
/* 243 */     this.outputContainer = new SimpleItemContainer((short)1);
/* 244 */     this.outputContainer.setGlobalFilter(FilterType.DENY_ALL);
/*     */     
/* 246 */     this.combinedItemContainer = new CombinedItemContainer(new ItemContainer[] { (ItemContainer)this.combinedInputItemContainer, (ItemContainer)this.outputContainer });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateInput(@Nonnull ItemContainer.ItemContainerChangeEvent event) {
/* 255 */     PlayerRef playerRef = getPlayerRef();
/* 256 */     if (playerRef == null)
/*     */       return; 
/* 258 */     Ref<EntityStore> ref = playerRef.getReference();
/* 259 */     if (ref == null || !ref.isValid())
/*     */       return; 
/* 261 */     Store<EntityStore> store = ref.getStore();
/* 262 */     updateInput(event.container(), ref, store);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateInput(@Nullable ItemContainer container, @Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 273 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 274 */     assert playerComponent != null;
/*     */     
/* 276 */     ItemStack primaryItemStack = this.inputPrimaryContainer.getItemStack((short)0);
/* 277 */     CombinedItemContainer combinedStorage = playerComponent.getInventory().getCombinedHotbarFirst();
/* 278 */     if (primaryItemStack != null && !primaryItemStack.isEmpty()) {
/*     */       
/* 280 */       this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_ALL);
/*     */ 
/*     */       
/* 283 */       boolean needsDropSlot = true; short i;
/* 284 */       for (i = 0; i < this.inputSecondaryContainer.getCapacity(); i = (short)(i + 1)) {
/* 285 */         ItemStack itemStack = this.inputSecondaryContainer.getItemStack(i);
/* 286 */         if (itemStack != null && !itemStack.isEmpty()) {
/* 287 */           this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, null);
/* 288 */         } else if (needsDropSlot) {
/* 289 */           this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, null);
/* 290 */           needsDropSlot = false;
/*     */         } else {
/* 292 */           this.inputSecondaryContainer.setSlotFilter(FilterActionType.ADD, i, SlotFilter.DENY);
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 297 */       this.inputSecondaryContainer.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
/*     */ 
/*     */       
/* 300 */       if (container != this.inputSecondaryContainer && !this.inputSecondaryContainer.isEmpty()) {
/*     */         
/* 302 */         List<ItemStack> itemStacks = this.inputSecondaryContainer.dropAllItemStacks();
/* 303 */         SimpleItemContainer.addOrDropItemStacks((ComponentAccessor)store, ref, (ItemContainer)combinedStorage, itemStacks);
/*     */       } 
/*     */     } 
/*     */     
/* 307 */     ObjectArrayList<CraftingRecipe> objectArrayList = new ObjectArrayList();
/* 308 */     boolean allSlotsFull = collectRecipes(ref, (List<CraftingRecipe>)objectArrayList, store);
/*     */     
/* 310 */     this.windowData.add("slots", (JsonElement)generateSlots(combinedStorage, (List<CraftingRecipe>)objectArrayList));
/*     */     
/* 312 */     if (objectArrayList.size() == 1 && allSlotsFull) {
/* 313 */       CraftingRecipe recipe = objectArrayList.getFirst();
/* 314 */       ItemStack output = CraftingManager.getOutputItemStacks(recipe).getFirst();
/*     */       
/* 316 */       if (playerComponent.getPlayerConfigData().getKnownRecipes().contains(recipe.getId())) {
/* 317 */         this.outputContainer.setItemStackForSlot((short)0, output);
/*     */       } else {
/*     */         
/* 320 */         this.outputContainer.setItemStackForSlot((short)0, new ItemStack("Unknown", 1));
/*     */       } 
/*     */     } else {
/* 323 */       if (!objectArrayList.isEmpty() && allSlotsFull) {
/* 324 */         LOGGER.at(Level.WARNING).log("Multiple recipes defined for the same materials! %s", objectArrayList);
/*     */       }
/* 326 */       this.outputContainer.setItemStackForSlot((short)0, ItemStack.EMPTY);
/*     */     } 
/*     */ 
/*     */     
/* 330 */     invalidate();
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
/*     */   private boolean collectRecipes(@Nonnull Ref<EntityStore> ref, @Nonnull List<CraftingRecipe> recipes, @Nonnull Store<EntityStore> store) {
/* 342 */     assert this.benchItemCategory != null;
/*     */ 
/*     */     
/* 345 */     ItemStack primaryItemStack = this.inputPrimaryContainer.getItemStack((short)0);
/* 346 */     if (primaryItemStack == null || primaryItemStack.isEmpty()) return false;
/*     */     
/* 348 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 349 */     assert playerComponent != null;
/*     */     
/* 351 */     Set<String> knownRecipes = playerComponent.getPlayerConfigData().getKnownRecipes();
/*     */     
/* 353 */     short inputCapacity = this.combinedInputItemContainer.getCapacity();
/* 354 */     boolean allSlotsFull = true;
/*     */ 
/*     */ 
/*     */     
/* 358 */     label41: for (CraftingRecipe recipe : getBenchRecipes()) {
/*     */ 
/*     */       
/* 361 */       if ((recipe.getInput()).length != inputCapacity && (!this.benchItemCategory.isSpecialSlot() || (recipe.getInput()).length != inputCapacity - 1)) {
/* 362 */         LOGGER.at(Level.WARNING).log("Recipe for %s has different input length than the diagram! %s - %s, %s, %s", recipe.getId(), recipe, this.bench, this.category, this.itemCategory);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 367 */       if (recipe.isKnowledgeRequired() && !knownRecipes.contains(recipe.getId())) {
/*     */         continue;
/*     */       }
/*     */       short i;
/* 371 */       for (i = 0; i < inputCapacity; i = (short)(i + 1)) {
/* 372 */         ItemStack itemStack = this.combinedInputItemContainer.getItemStack(i);
/* 373 */         if (itemStack == null || itemStack.isEmpty()) {
/* 374 */           if (!this.benchItemCategory.isSpecialSlot() && i == inputCapacity - 1) {
/* 375 */             allSlotsFull = false;
/*     */ 
/*     */           
/*     */           }
/*     */         
/*     */         }
/* 381 */         else if (!CraftingManager.matches(recipe.getInput()[i], itemStack)) {
/*     */           continue label41;
/*     */         } 
/*     */       } 
/*     */       
/* 386 */       recipes.add(recipe);
/*     */     } 
/*     */     
/* 389 */     return allSlotsFull;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private JsonArray generateSlots(@Nonnull CombinedItemContainer combinedStorage, @Nonnull List<CraftingRecipe> recipes) {
/* 394 */     JsonArray slots = new JsonArray();
/* 395 */     if (recipes.isEmpty()) {
/* 396 */       List<CraftingRecipe> benchRecipes = getBenchRecipes();
/*     */       
/* 398 */       JsonObject slot = new JsonObject();
/* 399 */       slot.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(benchRecipes, 0, (ItemContainer)combinedStorage));
/* 400 */       slots.add((JsonElement)slot);
/*     */     } else {
/* 402 */       short i; for (i = 0; i < this.combinedInputItemContainer.getCapacity(); i = (short)(i + 1)) {
/* 403 */         JsonObject slot = new JsonObject();
/*     */         
/* 405 */         ItemStack itemStack = this.combinedInputItemContainer.getItemStack(i);
/*     */         
/* 407 */         if (itemStack == null || itemStack.isEmpty()) {
/* 408 */           slot.add("inventoryHints", (JsonElement)CraftingManager.generateInventoryHints(recipes, i, (ItemContainer)combinedStorage));
/*     */         }
/*     */         
/* 411 */         int requiredAmount = -1;
/* 412 */         if (recipes.size() == 1) {
/* 413 */           CraftingRecipe recipe = recipes.getFirst();
/* 414 */           if (i < (recipe.getInput()).length) {
/* 415 */             requiredAmount = recipe.getInput()[i].getQuantity();
/*     */           }
/*     */         } 
/* 418 */         slot.addProperty("requiredAmount", Integer.valueOf(requiredAmount));
/*     */         
/* 420 */         slots.add((JsonElement)slot);
/*     */       } 
/*     */     } 
/* 423 */     return slots;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public List<CraftingRecipe> getBenchRecipes() {
/* 428 */     return CraftingPlugin.getBenchRecipes(this.bench.getType(), this.bench.getId(), this.category + "." + this.category);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\DiagramCraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */