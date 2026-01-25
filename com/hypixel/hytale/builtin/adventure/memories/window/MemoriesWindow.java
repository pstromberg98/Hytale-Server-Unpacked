/*    */ package com.hypixel.hytale.builtin.adventure.memories.window;
/*    */ import com.google.gson.JsonArray;
/*    */ import com.google.gson.JsonElement;
/*    */ import com.google.gson.JsonObject;
/*    */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*    */ import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
/*    */ import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
/*    */ import com.hypixel.hytale.codec.EmptyExtraInfo;
/*    */ import com.hypixel.hytale.component.ComponentAccessor;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import com.hypixel.hytale.server.core.util.BsonUtil;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class MemoriesWindow extends Window {
/* 22 */   private final JsonObject windowData = new JsonObject();
/*    */   
/*    */   public MemoriesWindow() {
/* 25 */     super(WindowType.Memories);
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public JsonObject getData() {
/* 30 */     return this.windowData;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 35 */     JsonArray array = new JsonArray();
/*    */     
/* 37 */     PlayerMemories playerMemoriesComponent = (PlayerMemories)store.getComponent(ref, PlayerMemories.getComponentType());
/* 38 */     if (playerMemoriesComponent != null) {
/*    */       
/* 40 */       this.windowData.addProperty("capacity", Integer.valueOf(playerMemoriesComponent.getMemoriesCapacity()));
/* 41 */       for (Memory memory : playerMemoriesComponent.getRecordedMemories()) {
/* 42 */         JsonObject obj = new JsonObject();
/* 43 */         obj.addProperty("title", memory.getTitle());
/* 44 */         obj.add("tooltipText", BsonUtil.translateBsonToJson(Message.CODEC.encode(memory.getTooltipText(), (ExtraInfo)EmptyExtraInfo.EMPTY).asDocument()));
/*    */         
/* 46 */         String iconPath = memory.getIconPath();
/* 47 */         if (iconPath != null && !iconPath.isEmpty()) obj.addProperty("icon", iconPath);
/*    */         
/* 49 */         String category = GetCategoryIconPathForMemory(memory);
/* 50 */         if (category != null) {
/* 51 */           obj.addProperty("categoryIcon", category);
/*    */         }
/*    */         
/* 54 */         array.add((JsonElement)obj);
/*    */       } 
/*    */     } else {
/* 57 */       this.windowData.addProperty("capacity", Integer.valueOf(0));
/*    */     } 
/*    */     
/* 60 */     this.windowData.add("memories", (JsonElement)array);
/* 61 */     invalidate();
/* 62 */     return true;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private static String GetCategoryIconPathForMemory(@Nonnull Memory memory) {
/* 67 */     Map<String, Set<Memory>> allMemories = MemoriesPlugin.get().getAllMemories();
/*    */     
/* 69 */     for (Map.Entry<String, Set<Memory>> entry : allMemories.entrySet()) {
/* 70 */       if (!((Set)entry.getValue()).contains(memory)) {
/*    */         continue;
/*    */       }
/*    */       
/* 74 */       String memoryCategoryIconBasePath = "UI/Custom/Pages/Memories/categories/%s.png";
/* 75 */       return String.format("UI/Custom/Pages/Memories/categories/%s.png", new Object[] { entry.getKey() });
/*    */     } 
/*    */     
/* 78 */     return null;
/*    */   }
/*    */   
/*    */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\window\MemoriesWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */