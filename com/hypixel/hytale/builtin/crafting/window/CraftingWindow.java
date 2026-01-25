/*     */ package com.hypixel.hytale.builtin.crafting.window;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesGameplayConfig;
/*     */ import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.util.ChunkUtil;
/*     */ import com.hypixel.hytale.protocol.SoundCategory;
/*     */ import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.CraftingBench;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public abstract class CraftingWindow extends BenchWindow {
/*     */   @Nonnull
/*     */   protected static final String CRAFT_COMPLETED = "CraftCompleted";
/*     */   @Nonnull
/*     */   protected static final String CRAFT_COMPLETED_INSTANT = "CraftCompletedInstant";
/*     */   
/*     */   public CraftingWindow(@Nonnull WindowType windowType, @Nonnull BenchState benchState) {
/*  39 */     super(windowType, benchState);
/*     */     
/*  41 */     JsonArray categories = new JsonArray();
/*     */     
/*  43 */     Bench bench = this.bench; if (bench instanceof CraftingBench) { CraftingBench craftingBench = (CraftingBench)bench;
/*     */ 
/*     */       
/*  46 */       for (CraftingBench.BenchCategory benchCategory : craftingBench.getCategories()) {
/*  47 */         JsonObject category = new JsonObject();
/*  48 */         categories.add((JsonElement)category);
/*     */         
/*  50 */         category.addProperty("id", benchCategory.getId());
/*  51 */         category.addProperty("name", benchCategory.getName());
/*  52 */         category.addProperty("icon", benchCategory.getIcon());
/*     */         
/*  54 */         Set<String> recipes = CraftingPlugin.getAvailableRecipesForCategory(this.bench.getId(), benchCategory.getId());
/*  55 */         if (recipes != null) {
/*  56 */           JsonArray recipesArray = new JsonArray();
/*  57 */           for (String recipeId : recipes) {
/*  58 */             recipesArray.add(recipeId);
/*     */           }
/*  60 */           category.add("craftableRecipes", (JsonElement)recipesArray);
/*     */         } 
/*     */         
/*  63 */         if (benchCategory.getItemCategories() != null) {
/*     */           
/*  65 */           JsonArray itemCategories = new JsonArray();
/*  66 */           for (CraftingBench.BenchItemCategory benchItemCategory : benchCategory.getItemCategories()) {
/*  67 */             JsonObject itemCategory = new JsonObject();
/*  68 */             itemCategory.addProperty("id", benchItemCategory.getId());
/*  69 */             itemCategory.addProperty("icon", benchItemCategory.getIcon());
/*  70 */             itemCategory.addProperty("diagram", benchItemCategory.getDiagram());
/*  71 */             itemCategory.addProperty("slots", Integer.valueOf(benchItemCategory.getSlots()));
/*  72 */             itemCategory.addProperty("specialSlot", Boolean.valueOf(benchItemCategory.isSpecialSlot()));
/*  73 */             itemCategories.add((JsonElement)itemCategory);
/*     */           } 
/*  75 */           category.add("itemCategories", (JsonElement)itemCategories);
/*     */         } 
/*  77 */       }  this.windowData.add("categories", (JsonElement)categories); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/*  83 */     super.onOpen0(ref, store);
/*     */     
/*  85 */     GameplayConfig gameplayConfig = ((EntityStore)store.getExternalData()).getWorld().getGameplayConfig();
/*  86 */     MemoriesGameplayConfig memoriesConfig = MemoriesGameplayConfig.get(gameplayConfig);
/*     */     
/*  88 */     if (memoriesConfig != null) {
/*  89 */       int[] memoriesAmountPerLevel = memoriesConfig.getMemoriesAmountPerLevel();
/*  90 */       if (memoriesAmountPerLevel != null && memoriesAmountPerLevel.length > 1) {
/*  91 */         JsonArray memoriesPerLevel = new JsonArray();
/*  92 */         for (int i = 0; i < memoriesAmountPerLevel.length; i++) {
/*  93 */           memoriesPerLevel.add(Integer.valueOf(memoriesAmountPerLevel[i]));
/*     */         }
/*  95 */         this.windowData.add("memoriesPerLevel", (JsonElement)memoriesPerLevel);
/*     */       } 
/*     */     } 
/*     */     
/*  99 */     if (this.bench.getLocalOpenSoundEventIndex() != 0) {
/* 100 */       SoundUtil.playSoundEvent2d(ref, this.bench.getLocalOpenSoundEventIndex(), SoundCategory.UI, (ComponentAccessor)store);
/*     */     }
/*     */     
/* 103 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 108 */     super.onClose0(ref, componentAccessor);
/* 109 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*     */ 
/*     */     
/* 112 */     setBlockInteractionState(this.benchState.getTierStateName(), world);
/*     */     
/* 114 */     if (this.bench.getLocalCloseSoundEventIndex() != 0) {
/* 115 */       SoundUtil.playSoundEvent2d(ref, this.bench.getLocalCloseSoundEventIndex(), SoundCategory.UI, componentAccessor);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBlockInteractionState(@Nonnull String state, @Nonnull World world) {
/* 126 */     WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(this.x, this.z));
/* 127 */     if (worldChunk == null)
/*     */       return; 
/* 129 */     BlockType blockType = worldChunk.getBlockType(this.x, this.y, this.z);
/* 130 */     if (blockType == null)
/*     */       return; 
/* 132 */     worldChunk.setBlockInteractionState(this.x, this.y, this.z, blockType, state, true);
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
/*     */   public static boolean craftSimpleItem(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull CraftingManager craftingManager, @Nonnull CraftRecipeAction action) {
/* 145 */     String recipeId = action.recipeId;
/* 146 */     int quantity = action.quantity;
/*     */     
/* 148 */     if (recipeId == null) return false;
/*     */     
/* 150 */     CraftingRecipe recipe = (CraftingRecipe)CraftingRecipe.getAssetMap().getAsset(recipeId);
/* 151 */     if (recipe == null) {
/* 152 */       PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/* 153 */       assert playerRef != null;
/*     */ 
/*     */       
/* 156 */       playerRef.getPacketHandler().disconnect("Attempted to craft unknown recipe!");
/* 157 */       return false;
/*     */     } 
/*     */     
/* 160 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 161 */     assert playerComponent != null;
/*     */ 
/*     */     
/* 164 */     craftingManager.craftItem(ref, (ComponentAccessor)store, recipe, quantity, (ItemContainer)playerComponent.getInventory().getCombinedBackpackStorageHotbar());
/* 165 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\CraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */