/*    */ package com.hypixel.hytale.builtin.buildertools.utils;
/*    */ 
/*    */ import com.hypixel.hytale.protocol.Packet;
/*    */ import com.hypixel.hytale.protocol.packets.inventory.SetActiveSlot;
/*    */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*    */ import com.hypixel.hytale.server.core.inventory.Inventory;
/*    */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*    */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ public final class PasteToolUtil
/*    */ {
/*    */   private static final String PASTE_TOOL_ID = "EditorTool_Paste";
/*    */   
/*    */   public static void switchToPasteTool(@Nonnull Player player, @Nonnull PlayerRef playerRef) {
/* 18 */     Inventory inventory = player.getInventory();
/* 19 */     ItemContainer hotbar = inventory.getHotbar();
/* 20 */     ItemContainer storage = inventory.getStorage();
/* 21 */     ItemContainer tools = inventory.getTools();
/*    */     
/* 23 */     int hotbarSize = hotbar.getCapacity();
/*    */     short slot;
/* 25 */     for (slot = 0; slot < hotbarSize; slot = (short)(slot + 1)) {
/* 26 */       ItemStack itemStack = hotbar.getItemStack(slot);
/* 27 */       if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
/* 28 */         inventory.setActiveHotbarSlot((byte)slot);
/* 29 */         playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, (byte)slot));
/*    */         
/*    */         return;
/*    */       } 
/*    */     } 
/* 34 */     short emptySlot = -1; short s1;
/* 35 */     for (s1 = 0; s1 < hotbarSize; s1 = (short)(s1 + 1)) {
/* 36 */       ItemStack itemStack = hotbar.getItemStack(s1);
/* 37 */       if (itemStack == null || itemStack.isEmpty()) {
/* 38 */         emptySlot = s1;
/*    */         
/*    */         break;
/*    */       } 
/*    */     } 
/* 43 */     if (emptySlot == -1) {
/*    */       return;
/*    */     }
/*    */     
/* 47 */     for (s1 = 0; s1 < storage.getCapacity(); s1 = (short)(s1 + 1)) {
/* 48 */       ItemStack itemStack = storage.getItemStack(s1);
/* 49 */       if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
/* 50 */         storage.moveItemStackFromSlotToSlot(s1, 1, hotbar, emptySlot);
/* 51 */         inventory.setActiveHotbarSlot((byte)emptySlot);
/* 52 */         playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, (byte)emptySlot));
/*    */         
/*    */         return;
/*    */       } 
/*    */     } 
/* 57 */     ItemStack pasteToolStack = null; short s2;
/* 58 */     for (s2 = 0; s2 < tools.getCapacity(); s2 = (short)(s2 + 1)) {
/* 59 */       ItemStack itemStack = tools.getItemStack(s2);
/* 60 */       if (itemStack != null && !itemStack.isEmpty() && "EditorTool_Paste".equals(itemStack.getItemId())) {
/* 61 */         pasteToolStack = itemStack;
/*    */         
/*    */         break;
/*    */       } 
/*    */     } 
/* 66 */     if (pasteToolStack == null) {
/*    */       return;
/*    */     }
/*    */     
/* 70 */     hotbar.setItemStackForSlot(emptySlot, new ItemStack(pasteToolStack.getItemId()));
/* 71 */     inventory.setActiveHotbarSlot((byte)emptySlot);
/* 72 */     playerRef.getPacketHandler().writeNoCache((Packet)new SetActiveSlot(-1, (byte)emptySlot));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\buildertool\\utils\PasteToolUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */