/*    */ package com.hypixel.hytale.server.core.entity.entities.player.windows;
/*    */ 
/*    */ import com.google.gson.JsonObject;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.protocol.packets.window.SortItemsAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*    */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*    */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.inventory.container.SortType;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContainerBlockWindow
/*    */   extends BlockWindow
/*    */   implements ItemContainerWindow
/*    */ {
/*    */   @Nonnull
/*    */   private final JsonObject windowData;
/*    */   @Nonnull
/*    */   private final ItemContainer itemContainer;
/*    */   
/*    */   public ContainerBlockWindow(int x, int y, int z, int rotationIndex, @Nonnull BlockType blockType, @Nonnull ItemContainer itemContainer) {
/* 50 */     super(WindowType.Container, x, y, z, rotationIndex, blockType);
/* 51 */     this.itemContainer = itemContainer;
/*    */     
/* 53 */     this.windowData = new JsonObject();
/* 54 */     Item item = blockType.getItem();
/* 55 */     this.windowData.addProperty("blockItemId", item.getId());
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public JsonObject getData() {
/* 60 */     return this.windowData;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 65 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {}
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ItemContainer getItemContainer() {
/* 75 */     return this.itemContainer;
/*    */   }
/*    */   
/*    */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {
/*    */     SortItemsAction sortAction;
/* 80 */     if (action instanceof SortItemsAction) { sortAction = (SortItemsAction)action; } else { return; }
/* 81 */      SortType sortType = SortType.fromPacket(sortAction.sortType);
/*    */     
/* 83 */     Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 84 */     assert playerComponent != null;
/*    */     
/* 86 */     playerComponent.getInventory().setSortType(sortType);
/* 87 */     this.itemContainer.sortItems(sortType);
/* 88 */     invalidate();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\windows\ContainerBlockWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */