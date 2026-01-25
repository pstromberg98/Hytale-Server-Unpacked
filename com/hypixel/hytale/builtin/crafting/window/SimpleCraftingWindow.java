/*    */ package com.hypixel.hytale.builtin.crafting.window;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*    */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.math.vector.Vector3d;
/*    */ import com.hypixel.hytale.protocol.SoundCategory;
/*    */ import com.hypixel.hytale.protocol.packets.window.CraftRecipeAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.CraftingRecipe;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialContainerWindow;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
/*    */ import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*    */ import com.hypixel.hytale.server.core.universe.world.World;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleCraftingWindow
/*    */   extends CraftingWindow
/*    */   implements MaterialContainerWindow
/*    */ {
/*    */   public SimpleCraftingWindow(BenchState benchState) {
/* 33 */     super(WindowType.BasicCrafting, benchState);
/*    */   }
/*    */ 
/*    */   
/*    */   public void init(@Nonnull PlayerRef playerRef, @Nonnull WindowManager manager) {
/* 38 */     super.init(playerRef, manager);
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
/* 43 */     CraftingManager craftingManager = (CraftingManager)store.getComponent(ref, CraftingManager.getComponentType());
/* 44 */     if (craftingManager == null)
/*    */       return; 
/* 46 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*    */     
/* 48 */     if (action instanceof CraftRecipeAction) { boolean accepted; CraftRecipeAction craftAction = (CraftRecipeAction)action;
/* 49 */       String recipeId = craftAction.recipeId;
/* 50 */       int quantity = craftAction.quantity;
/*    */       
/* 52 */       CraftingRecipe craftRecipe = (CraftingRecipe)CraftingRecipe.getAssetMap().getAsset(recipeId);
/*    */       
/* 54 */       if (craftRecipe == null) {
/* 55 */         PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*    */         
/* 57 */         playerRef.getPacketHandler().disconnect("Attempted to craft unknown recipe!");
/*    */         
/*    */         return;
/*    */       } 
/* 61 */       Player player = (Player)store.getComponent(ref, Player.getComponentType());
/* 62 */       CombinedItemContainer combined = player.getInventory().getCombinedBackpackStorageHotbar();
/* 63 */       CombinedItemContainer playerAndContainerInventory = new CombinedItemContainer(new ItemContainer[] { (ItemContainer)combined, getExtraResourcesSection().getItemContainer() });
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 68 */       if (craftRecipe.getTimeSeconds() > 0.0F) {
/* 69 */         accepted = craftingManager.queueCraft(ref, (ComponentAccessor)store, this, 0, craftRecipe, quantity, (ItemContainer)playerAndContainerInventory, CraftingManager.InputRemovalType.NORMAL);
/*    */       } else {
/* 71 */         accepted = craftingManager.craftItem(ref, (ComponentAccessor)store, craftRecipe, quantity, (ItemContainer)playerAndContainerInventory);
/*    */       } 
/*    */ 
/*    */       
/* 75 */       invalidateExtraResources();
/*    */       
/* 77 */       if (accepted) {
/* 78 */         String completedState = (craftRecipe.getTimeSeconds() > 0.0F) ? "CraftCompleted" : "CraftCompletedInstant";
/* 79 */         setBlockInteractionState(completedState, world);
/*    */         
/* 81 */         if (this.bench.getCompletedSoundEventIndex() != 0) {
/* 82 */           Vector3d pos = new Vector3d();
/* 83 */           this.blockType.getBlockCenter(this.rotationIndex, pos);
/* 84 */           pos.add(this.x, this.y, this.z);
/* 85 */           SoundUtil.playSoundEvent3d(this.bench.getCompletedSoundEventIndex(), SoundCategory.SFX, pos, (ComponentAccessor)store);
/*    */         } 
/*    */       }  }
/* 88 */     else if (action instanceof com.hypixel.hytale.protocol.packets.window.TierUpgradeAction && 
/* 89 */       craftingManager.startTierUpgrade(ref, (ComponentAccessor)store, this))
/* 90 */     { setBlockInteractionState("BenchUpgrading", world);
/* 91 */       if (this.bench.getBenchUpgradeSoundEventIndex() != 0) {
/* 92 */         Vector3d pos = new Vector3d();
/* 93 */         this.blockType.getBlockCenter(this.rotationIndex, pos);
/* 94 */         pos.add(this.x, this.y, this.z);
/* 95 */         SoundUtil.playSoundEvent3d(this.bench.getBenchUpgradeSoundEventIndex(), SoundCategory.SFX, pos, (ComponentAccessor)store);
/*    */       }  }
/*    */   
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\SimpleCraftingWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */