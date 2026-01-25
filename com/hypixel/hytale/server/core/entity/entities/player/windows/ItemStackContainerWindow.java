/*    */ package com.hypixel.hytale.server.core.entity.entities.player.windows;
/*    */ 
/*    */ import com.google.gson.JsonObject;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.event.EventRegistration;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemStackItemContainer;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ItemStackContainerWindow
/*    */   extends Window
/*    */   implements ItemContainerWindow
/*    */ {
/*    */   @Nonnull
/* 24 */   private final JsonObject windowData = new JsonObject();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   private final ItemStackItemContainer itemStackItemContainer;
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private EventRegistration eventRegistration;
/*    */ 
/*    */ 
/*    */   
/*    */   public ItemStackContainerWindow(@Nonnull ItemStackItemContainer itemStackItemContainer) {
/* 37 */     super(WindowType.Container);
/* 38 */     this.itemStackItemContainer = itemStackItemContainer;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public JsonObject getData() {
/* 43 */     return this.windowData;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 48 */     this.eventRegistration = this.itemStackItemContainer.getParentContainer().registerChangeEvent(event -> {
/*    */           if (!this.itemStackItemContainer.isItemStackValid()) {
/*    */             close(ref, (ComponentAccessor<EntityStore>)store);
/*    */           }
/*    */         });
/* 53 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 58 */     this.eventRegistration.unregister();
/* 59 */     this.eventRegistration = null;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public ItemContainer getItemContainer() {
/* 65 */     return (ItemContainer)this.itemStackItemContainer;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\windows\ItemStackContainerWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */