/*     */ package com.hypixel.hytale.builtin.crafting;
/*     */ import com.hypixel.hytale.assetstore.AssetRegistry;
/*     */ import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
/*     */ import com.hypixel.hytale.assetstore.event.RemovedAssetsEvent;
/*     */ import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.interaction.LearnRecipeInteraction;
/*     */ import com.hypixel.hytale.builtin.crafting.interaction.OpenBenchPageInteraction;
/*     */ import com.hypixel.hytale.builtin.crafting.interaction.OpenProcessingBenchInteraction;
/*     */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*     */ import com.hypixel.hytale.builtin.crafting.state.ProcessingBenchState;
/*     */ import com.hypixel.hytale.builtin.crafting.system.PlayerCraftingSystems;
/*     */ import com.hypixel.hytale.common.util.ArrayUtil;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentRegistryProxy;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.ISystem;
/*     */ import com.hypixel.hytale.protocol.BenchRequirement;
/*     */ import com.hypixel.hytale.protocol.BenchType;
/*     */ import com.hypixel.hytale.protocol.CraftingRecipe;
/*     */ import com.hypixel.hytale.protocol.ItemResourceType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchUpgradeRequirement;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerConfigData;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.inventory.MaterialQuantity;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockStateRegistry;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class CraftingPlugin extends JavaPlugin {
/*  53 */   private static final Map<String, BenchRecipeRegistry> registries = (Map<String, BenchRecipeRegistry>)new Object2ObjectOpenHashMap(); private static CraftingPlugin instance;
/*  54 */   private static final Map<String, String[]> itemGeneratedRecipes = (Map<String, String[]>)new Object2ObjectOpenHashMap();
/*     */   private ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;
/*     */   
/*     */   public CraftingPlugin(@Nonnull JavaPluginInit init) {
/*  58 */     super(init);
/*  59 */     instance = this;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static Set<String> getAvailableRecipesForCategory(String benchId, String benchCategoryId) {
/*  64 */     BenchRecipeRegistry benchRecipeRegistry = registries.get(benchId);
/*  65 */     if (benchRecipeRegistry == null) {
/*  66 */       return null;
/*     */     }
/*  68 */     return benchRecipeRegistry.getRecipesForCategory(benchCategoryId);
/*     */   }
/*     */   
/*     */   public static boolean isValidCraftingMaterialForBench(BenchState benchState, ItemStack itemStack) {
/*  72 */     BenchRecipeRegistry benchRecipeRegistry = registries.get(benchState.getBench().getId());
/*  73 */     if (benchRecipeRegistry == null) {
/*  74 */       return false;
/*     */     }
/*  76 */     return benchRecipeRegistry.isValidCraftingMaterial(itemStack);
/*     */   }
/*     */   
/*     */   public static boolean isValidUpgradeMaterialForBench(BenchState benchState, ItemStack itemStack) {
/*  80 */     BenchUpgradeRequirement nextLevelUpgradeMaterials = benchState.getNextLevelUpgradeMaterials();
/*  81 */     if (nextLevelUpgradeMaterials == null) return false;
/*     */     
/*  83 */     for (MaterialQuantity upgradeMaterial : nextLevelUpgradeMaterials.getInput()) {
/*  84 */       if (itemStack.getItemId().equals(upgradeMaterial.getItemId())) return true; 
/*  85 */       ItemResourceType[] resourceTypeId = itemStack.getItem().getResourceTypes();
/*  86 */       if (resourceTypeId != null)
/*  87 */         for (ItemResourceType resTypeId : resourceTypeId) {
/*  88 */           if (resTypeId.id.equals(upgradeMaterial.getResourceTypeId())) return true;
/*     */         
/*     */         }  
/*     */     } 
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  97 */     AssetRegistry.getAssetStore(Interaction.class).loadAssets("Hytale:Hytale", List.of(OpenBenchPageInteraction.SIMPLE_CRAFTING, OpenBenchPageInteraction.DIAGRAM_CRAFTING, OpenBenchPageInteraction.STRUCTURAL_CRAFTING));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 102 */     AssetRegistry.getAssetStore(RootInteraction.class).loadAssets("Hytale:Hytale", List.of(OpenBenchPageInteraction.SIMPLE_CRAFTING_ROOT, OpenBenchPageInteraction.DIAGRAM_CRAFTING_ROOT, OpenBenchPageInteraction.STRUCTURAL_CRAFTING_ROOT));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 108 */     ComponentRegistryProxy<EntityStore> entityStoreRegistry = getEntityStoreRegistry();
/* 109 */     this.craftingManagerComponentType = entityStoreRegistry.registerComponent(CraftingManager.class, CraftingManager::new);
/* 110 */     entityStoreRegistry.registerSystem((ISystem)new PlayerCraftingSystems.CraftingTickingSystem(this.craftingManagerComponentType));
/* 111 */     entityStoreRegistry.registerSystem((ISystem)new PlayerCraftingSystems.CraftingHolderSystem(this.craftingManagerComponentType));
/* 112 */     entityStoreRegistry.registerSystem((ISystem)new PlayerCraftingSystems.CraftingRefSystem(this.craftingManagerComponentType));
/*     */     
/* 114 */     getCodecRegistry(Interaction.CODEC)
/* 115 */       .register("OpenBenchPage", OpenBenchPageInteraction.class, OpenBenchPageInteraction.CODEC)
/* 116 */       .register("OpenProcessingBench", OpenProcessingBenchInteraction.class, OpenProcessingBenchInteraction.CODEC);
/*     */     
/* 118 */     Bench.registerRootInteraction(BenchType.Crafting, OpenBenchPageInteraction.SIMPLE_CRAFTING_ROOT);
/* 119 */     Bench.registerRootInteraction(BenchType.DiagramCrafting, OpenBenchPageInteraction.DIAGRAM_CRAFTING_ROOT);
/* 120 */     Bench.registerRootInteraction(BenchType.StructuralCrafting, OpenBenchPageInteraction.STRUCTURAL_CRAFTING_ROOT);
/*     */     
/* 122 */     BlockStateRegistry blockStateRegistry = getBlockStateRegistry();
/* 123 */     blockStateRegistry.registerBlockState(ProcessingBenchState.class, "processingBench", ProcessingBenchState.CODEC);
/* 124 */     blockStateRegistry.registerBlockState(BenchState.class, "crafting", (Codec)BenchState.CODEC);
/*     */     
/* 126 */     Window.CLIENT_REQUESTABLE_WINDOW_TYPES.put(WindowType.PocketCrafting, com.hypixel.hytale.builtin.crafting.window.FieldCraftingWindow::new);
/*     */     
/* 128 */     getEventRegistry().register(LoadedAssetsEvent.class, CraftingRecipe.class, CraftingPlugin::onRecipeLoad);
/* 129 */     getEventRegistry().register(RemovedAssetsEvent.class, CraftingRecipe.class, CraftingPlugin::onRecipeRemove);
/*     */     
/* 131 */     getEventRegistry().register(LoadedAssetsEvent.class, Item.class, CraftingPlugin::onItemAssetLoad);
/* 132 */     getEventRegistry().register(RemovedAssetsEvent.class, Item.class, CraftingPlugin::onItemAssetRemove);
/*     */     
/* 134 */     Interaction.CODEC.register("LearnRecipe", LearnRecipeInteraction.class, LearnRecipeInteraction.CODEC);
/*     */     
/* 136 */     CommandManager.get().registerSystemCommand((AbstractCommand)new RecipeCommand());
/* 137 */     entityStoreRegistry.registerSystem((ISystem)new PlayerAddedSystem());
/*     */   }
/*     */   
/*     */   private static void onItemAssetLoad(LoadedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event) {
/* 141 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/*     */     
/* 143 */     for (Item item : event.getLoadedAssets().values()) {
/* 144 */       if (!item.hasRecipesToGenerate()) {
/*     */         continue;
/*     */       }
/* 147 */       ObjectArrayList objectArrayList1 = new ObjectArrayList();
/* 148 */       item.collectRecipesToGenerate((Collection)objectArrayList1);
/*     */       
/* 150 */       ObjectArrayList<String> objectArrayList2 = new ObjectArrayList();
/* 151 */       for (CraftingRecipe generatedRecipe : objectArrayList1) {
/* 152 */         String id = generatedRecipe.getId();
/* 153 */         objectArrayList2.add(id);
/*     */       } 
/*     */       
/* 156 */       itemGeneratedRecipes.put(item.getId(), (String[])objectArrayList2.toArray(x$0 -> new String[x$0]));
/* 157 */       objectArrayList.addAll((Collection)objectArrayList1);
/*     */     } 
/*     */     
/* 160 */     if (!objectArrayList.isEmpty()) {
/* 161 */       CraftingRecipe.getAssetStore().loadAssets("Hytale:Hytale", (List)objectArrayList);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void onItemAssetRemove(@Nonnull RemovedAssetsEvent<String, Item, DefaultAssetMap<String, Item>> event) {
/* 166 */     for (String id : event.getRemovedAssets()) {
/* 167 */       String[] generatedRecipes = itemGeneratedRecipes.get(id);
/* 168 */       if (generatedRecipes == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 172 */       CraftingRecipe.getAssetStore().removeAssets(List.of(generatedRecipes));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void onRecipeLoad(LoadedAssetsEvent<String, CraftingRecipe, DefaultAssetMap<String, CraftingRecipe>> event) {
/* 177 */     for (CraftingRecipe recipe : event.getLoadedAssets().values()) {
/* 178 */       for (BenchRecipeRegistry registry : registries.values()) {
/* 179 */         registry.removeRecipe(recipe.getId());
/*     */       }
/*     */       
/* 182 */       if (recipe.getBenchRequirement() == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 186 */       for (BenchRequirement benchRequirement : recipe.getBenchRequirement()) {
/* 187 */         BenchRecipeRegistry benchRecipeRegistry = registries.computeIfAbsent(benchRequirement.id, BenchRecipeRegistry::new);
/* 188 */         benchRecipeRegistry.addRecipe(benchRequirement, recipe);
/*     */       } 
/*     */     } 
/*     */     
/* 192 */     computeBenchRecipeRegistries();
/*     */   }
/*     */   
/*     */   private static void onRecipeRemove(RemovedAssetsEvent<String, CraftingRecipe, DefaultAssetMap<String, CraftingRecipe>> event) {
/* 196 */     for (String removedRecipeId : event.getRemovedAssets()) {
/* 197 */       for (BenchRecipeRegistry registry : registries.values()) {
/* 198 */         registry.removeRecipe(removedRecipeId);
/*     */       }
/*     */     } 
/*     */     
/* 202 */     computeBenchRecipeRegistries();
/*     */   }
/*     */   
/*     */   private static void computeBenchRecipeRegistries() {
/* 206 */     for (BenchRecipeRegistry registry : registries.values()) {
/* 207 */       registry.recompute();
/*     */     }
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static List<CraftingRecipe> getBenchRecipes(@Nonnull Bench bench) {
/* 213 */     return getBenchRecipes(bench.getType(), bench.getId());
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static List<CraftingRecipe> getBenchRecipes(BenchType benchType, String name) {
/* 218 */     return getBenchRecipes(benchType, name, (String)null);
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static List<CraftingRecipe> getBenchRecipes(BenchType benchType, String benchId, @Nullable String category) {
/* 223 */     BenchRecipeRegistry registry = registries.get(benchId);
/* 224 */     if (registry == null) {
/* 225 */       return List.of();
/*     */     }
/*     */     
/* 228 */     ObjectArrayList<CraftingRecipe> objectArrayList = new ObjectArrayList();
/*     */ 
/*     */     
/* 231 */     for (CraftingRecipe recipe : registry.getAllRecipes()) {
/* 232 */       BenchRequirement[] benchRequirement = recipe.getBenchRequirement();
/* 233 */       if (benchRequirement != null)
/*     */       {
/* 235 */         for (BenchRequirement requirement : benchRequirement) {
/* 236 */           if (requirement.type == benchType && requirement.id.equals(benchId) && (category == null || 
/* 237 */             hasCategory(recipe, category))) {
/* 238 */             objectArrayList.add(recipe);
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 244 */     return (List<CraftingRecipe>)objectArrayList;
/*     */   }
/*     */   
/*     */   private static boolean hasCategory(@Nonnull CraftingRecipe recipe, String category) {
/* 248 */     for (BenchRequirement benchRequirement : recipe.getBenchRequirement()) {
/* 249 */       if (benchRequirement.categories != null && ArrayUtil.contains((Object[])benchRequirement.categories, category)) {
/* 250 */         return true;
/*     */       }
/*     */     } 
/* 253 */     return false;
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
/*     */   public static boolean learnRecipe(@Nonnull Ref<EntityStore> ref, @Nonnull String recipeId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 267 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 268 */     assert playerComponent != null;
/*     */     
/* 270 */     PlayerConfigData playerConfigData = playerComponent.getPlayerConfigData();
/*     */ 
/*     */     
/* 273 */     Set<String> knownRecipes = new HashSet<>(playerConfigData.getKnownRecipes());
/* 274 */     if (knownRecipes.add(recipeId)) {
/* 275 */       playerConfigData.setKnownRecipes(knownRecipes);
/* 276 */       sendKnownRecipes(ref, componentAccessor);
/* 277 */       return true;
/*     */     } 
/* 279 */     return false;
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
/*     */   public static boolean forgetRecipe(@Nonnull Ref<EntityStore> ref, @Nonnull String itemId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 291 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 292 */     assert playerComponent != null;
/*     */     
/* 294 */     PlayerConfigData playerConfigData = playerComponent.getPlayerConfigData();
/*     */ 
/*     */     
/* 297 */     ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet(playerConfigData.getKnownRecipes());
/* 298 */     if (objectOpenHashSet.remove(itemId)) {
/* 299 */       playerConfigData.setKnownRecipes((Set)objectOpenHashSet);
/* 300 */       sendKnownRecipes(ref, componentAccessor);
/* 301 */       return true;
/*     */     } 
/* 303 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendKnownRecipes(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 313 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/* 314 */     assert playerRefComponent != null;
/*     */     
/* 316 */     Player playerComponent = (Player)componentAccessor.getComponent(ref, Player.getComponentType());
/* 317 */     assert playerComponent != null;
/*     */     
/* 319 */     PlayerConfigData playerConfigData = playerComponent.getPlayerConfigData();
/* 320 */     DefaultAssetMap<String, Item> itemAssetMap = Item.getAssetMap();
/*     */     
/* 322 */     Object2ObjectOpenHashMap<String, CraftingRecipe> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
/* 323 */     for (String id : playerConfigData.getKnownRecipes()) {
/* 324 */       Item item = (Item)itemAssetMap.getAsset(id);
/* 325 */       if (item == null)
/*     */         continue; 
/* 327 */       for (BenchRecipeRegistry registry : registries.values()) {
/* 328 */         Iterable<String> incomingRecipes = registry.getIncomingRecipesForItem(item.getId());
/*     */         
/* 330 */         for (String recipeId : incomingRecipes) {
/* 331 */           CraftingRecipe recipe = (CraftingRecipe)CraftingRecipe.getAssetMap().getAsset(recipeId);
/* 332 */           if (recipe == null) {
/*     */             continue;
/*     */           }
/*     */           
/* 336 */           object2ObjectOpenHashMap.put(id, recipe.toPacket(id));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 341 */     playerRefComponent.getPacketHandler().writeNoCache((Packet)new UpdateKnownRecipes((Map)object2ObjectOpenHashMap));
/*     */   }
/*     */   
/*     */   public ComponentType<EntityStore, CraftingManager> getCraftingManagerComponentType() {
/* 345 */     return this.craftingManagerComponentType;
/*     */   }
/*     */   
/*     */   public static CraftingPlugin get() {
/* 349 */     return instance;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class PlayerAddedSystem
/*     */     extends RefSystem<EntityStore>
/*     */   {
/* 356 */     private static final Query<EntityStore> QUERY = (Query<EntityStore>)Archetype.of(new ComponentType[] { Player.getComponentType(), PlayerRef.getComponentType() });
/*     */ 
/*     */     
/*     */     public Query<EntityStore> getQuery() {
/* 360 */       return QUERY;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 365 */       CraftingPlugin.sendKnownRecipes(ref, (ComponentAccessor<EntityStore>)commandBuffer);
/*     */     }
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\CraftingPlugin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */