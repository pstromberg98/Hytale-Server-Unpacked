/*    */ package com.hypixel.hytale.builtin.crafting.window;
/*    */ 
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*    */ import com.hypixel.hytale.builtin.crafting.CraftingPlugin;
/*    */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.protocol.BenchType;
/*    */ import com.hypixel.hytale.protocol.SoundCategory;
/*    */ import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.FieldcraftCategory;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
/*    */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import com.hypixel.hytale.server.core.util.TempAssetIdUtil;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldCraftingWindow
/*    */   extends Window
/*    */ {
/*    */   @Nonnull
/*    */   private final JsonObject windowData;
/*    */   
/*    */   public FieldCraftingWindow() {
/* 35 */     super(WindowType.PocketCrafting);
/*    */     
/* 37 */     this.windowData = new JsonObject();
/* 38 */     this.windowData.addProperty("type", Integer.valueOf(BenchType.Crafting.ordinal()));
/*    */     
/* 40 */     this.windowData.addProperty("id", "Fieldcraft");
/* 41 */     this.windowData.addProperty("name", "server.ui.inventory.fieldcraft.title");
/* 42 */     JsonArray categories = new JsonArray();
/*    */ 
/*    */     
/* 45 */     for (FieldcraftCategory fieldcraftCategory : FieldcraftCategory.getAssetMap().getAssetMap().values()) {
/* 46 */       JsonObject category = new JsonObject();
/* 47 */       category.addProperty("id", fieldcraftCategory.getId());
/* 48 */       category.addProperty("icon", fieldcraftCategory.getIcon());
/* 49 */       category.addProperty("name", fieldcraftCategory.getName());
/*    */       
/* 51 */       Set<String> recipes = CraftingPlugin.getAvailableRecipesForCategory("Fieldcraft", fieldcraftCategory.getId());
/*    */       
/* 53 */       if (recipes != null) {
/* 54 */         JsonArray itemsArray = new JsonArray();
/* 55 */         for (String recipeId : recipes) {
/* 56 */           itemsArray.add(recipeId);
/*    */         }
/* 58 */         category.add("craftableRecipes", (JsonElement)itemsArray);
/*    */       } 
/*    */     } 
/*    */     
/* 62 */     this.windowData.add("categories", (JsonElement)categories);
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public JsonObject getData() {
/* 67 */     return this.windowData;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 72 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*    */     
/* 74 */     this.windowData.addProperty("worldMemoriesLevel", Integer.valueOf(MemoriesPlugin.get().getMemoriesLevel(world.getGameplayConfig())));
/* 75 */     invalidate();
/*    */     
/* 77 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {}
/*    */ 
/*    */   
/*    */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
/* 86 */     if (action instanceof CraftRecipeAction) { CraftRecipeAction craftAction = (CraftRecipeAction)action;
/* 87 */       CraftingManager craftingManager = (CraftingManager)store.getComponent(ref, CraftingManager.getComponentType());
/* 88 */       if (CraftingWindow.craftSimpleItem(store, ref, craftingManager, craftAction))
/* 89 */         SoundUtil.playSoundEvent2d(ref, TempAssetIdUtil.getSoundEventIndex("SFX_Player_Craft_Item_Inventory"), SoundCategory.UI, (ComponentAccessor)store);  }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\FieldCraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */