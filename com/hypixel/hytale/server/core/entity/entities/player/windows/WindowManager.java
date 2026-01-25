/*     */ package com.hypixel.hytale.server.core.entity.entities.player.windows;
/*     */ 
/*     */ import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventPriority;
/*     */ import com.hypixel.hytale.event.EventRegistration;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.ExtraResources;
/*     */ import com.hypixel.hytale.protocol.InventorySection;
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.packets.window.CloseWindow;
/*     */ import com.hypixel.hytale.protocol.packets.window.OpenWindow;
/*     */ import com.hypixel.hytale.protocol.packets.window.UpdateWindow;
/*     */ import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectListIterator;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class WindowManager
/*     */ {
/*     */   @Nonnull
/*  34 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   private final AtomicInteger windowId = new AtomicInteger(1);
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  44 */   private final Int2ObjectConcurrentHashMap<Window> windows = new Int2ObjectConcurrentHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  50 */   private final Int2ObjectConcurrentHashMap<EventRegistration<?, ?>> windowChangeEvents = new Int2ObjectConcurrentHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private PlayerRef playerRef;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(@Nonnull PlayerRef playerRef) {
/*  64 */     this.playerRef = playerRef;
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
/*     */   @Nullable
/*     */   public UpdateWindow clientOpenWindow(@Nonnull Ref<EntityStore> ref, @Nonnull Window window, @Nonnull Store<EntityStore> store) {
/*  77 */     if (!Window.CLIENT_REQUESTABLE_WINDOW_TYPES.containsKey(window.getType())) {
/*  78 */       throw new IllegalArgumentException("Client opened window must be registered in Window.CLIENT_REQUESTABLE_WINDOW_TYPES but got: " + String.valueOf(window.getType()));
/*     */     }
/*     */ 
/*     */     
/*  82 */     int id = 0;
/*  83 */     Window oldWindow = (Window)this.windows.remove(0);
/*  84 */     if (oldWindow != null) {
/*  85 */       if (oldWindow instanceof ItemContainerWindow) {
/*  86 */         ((EventRegistration)this.windowChangeEvents.remove(oldWindow.getId())).unregister();
/*     */       }
/*     */       
/*  89 */       oldWindow.onClose(ref, (ComponentAccessor<EntityStore>)store);
/*  90 */       LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), oldWindow.getType(), Integer.valueOf(0));
/*     */     } 
/*     */ 
/*     */     
/*  94 */     setWindow0(0, window);
/*     */     
/*  96 */     if (!window.onOpen(ref, store)) {
/*  97 */       closeWindow(ref, 0, (ComponentAccessor<EntityStore>)store);
/*  98 */       window.setId(-1);
/*  99 */       return null;
/*     */     } 
/*     */     
/* 102 */     if (!window.consumeIsDirty()) return null;
/*     */     
/* 104 */     InventorySection section = null;
/* 105 */     if (window instanceof ItemContainerWindow) { ItemContainerWindow itemContainerWindow = (ItemContainerWindow)window;
/* 106 */       section = itemContainerWindow.getItemContainer().toPacket(); }
/*     */ 
/*     */     
/* 109 */     ExtraResources extraResources = null;
/* 110 */     if (window instanceof MaterialContainerWindow) { MaterialContainerWindow materialContainerWindow = (MaterialContainerWindow)window;
/* 111 */       extraResources = materialContainerWindow.getExtraResourcesSection().toPacket(); }
/*     */ 
/*     */     
/* 114 */     return new UpdateWindow(0, window.getData().toString(), section, extraResources);
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
/*     */   @Nullable
/*     */   public OpenWindow openWindow(@Nonnull Ref<EntityStore> ref, @Nonnull Window window, @Nonnull Store<EntityStore> store) {
/* 129 */     int id = this.windowId.getAndUpdate(operand -> (++operand > 0) ? operand : 1);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 134 */     setWindow(id, window);
/*     */     
/* 136 */     if (!window.onOpen(ref, store)) {
/* 137 */       closeWindow(ref, id, (ComponentAccessor<EntityStore>)store);
/* 138 */       window.setId(-1);
/* 139 */       return null;
/*     */     } 
/*     */     
/* 142 */     window.consumeIsDirty();
/*     */     
/* 144 */     LOGGER.at(Level.FINE).log("%s opened window %s with id %s and data %s", this.playerRef.getUuid(), window.getType(), Integer.valueOf(id), window.getData());
/*     */     
/* 146 */     InventorySection section = null;
/* 147 */     if (window instanceof ItemContainerWindow) { ItemContainerWindow itemContainerWindow = (ItemContainerWindow)window;
/* 148 */       section = itemContainerWindow.getItemContainer().toPacket(); }
/*     */ 
/*     */     
/* 151 */     ExtraResources extraResources = null;
/* 152 */     if (window instanceof MaterialContainerWindow) { MaterialContainerWindow materialContainerWindow = (MaterialContainerWindow)window;
/* 153 */       extraResources = materialContainerWindow.getExtraResourcesSection().toPacket(); }
/*     */ 
/*     */     
/* 156 */     return new OpenWindow(id, window.getType(), window.getData().toString(), section, extraResources);
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
/*     */   @Nullable
/*     */   public List<OpenWindow> openWindows(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull Window... windows) {
/* 169 */     ObjectArrayList objectArrayList = new ObjectArrayList();
/* 170 */     for (Window window : windows) {
/*     */       
/* 172 */       OpenWindow packet = openWindow(ref, window, store);
/* 173 */       if (packet != null) {
/* 174 */         objectArrayList.add(packet);
/*     */       } else {
/* 176 */         for (ObjectListIterator<OpenWindow> objectListIterator = objectArrayList.iterator(); objectListIterator.hasNext(); ) { OpenWindow addedPacket = objectListIterator.next();
/* 177 */           closeWindow(ref, addedPacket.id, (ComponentAccessor<EntityStore>)store); }
/*     */         
/* 179 */         return null;
/*     */       } 
/*     */     } 
/* 182 */     return (List<OpenWindow>)objectArrayList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWindow(int id, @Nonnull Window window) {
/* 192 */     if (id >= this.windowId.get()) throw new IllegalArgumentException("id is outside of the range, use addWindow"); 
/* 193 */     if (id == 0 || id == -1) throw new IllegalArgumentException("id is invalid, can't be 0 or -1"); 
/* 194 */     setWindow0(id, window);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void setWindow0(int id, @Nonnull Window window) {
/* 204 */     if (this.windows.putIfAbsent(id, window) != null) {
/* 205 */       throw new IllegalArgumentException("Window " + id + " already exists");
/*     */     }
/* 207 */     window.setId(id);
/* 208 */     window.init(this.playerRef, this);
/*     */     
/* 210 */     if (window instanceof ItemContainerWindow) { ItemContainerWindow itemContainerWindow = (ItemContainerWindow)window;
/* 211 */       ItemContainer itemContainer = itemContainerWindow.getItemContainer();
/* 212 */       this.windowChangeEvents.put(id, itemContainer.registerChangeEvent(EventPriority.LAST, e -> markWindowChanged(id))); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Window getWindow(int id) {
/* 224 */     if (id == -1) throw new IllegalArgumentException("Window id -1 is invalid!"); 
/* 225 */     return (Window)this.windows.get(id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public List<Window> getWindows() {
/* 235 */     return (List<Window>)new ObjectArrayList((Collection)this.windows.values());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWindow(@Nonnull Window window) {
/* 244 */     InventorySection section = null;
/*     */ 
/*     */     
/* 247 */     if (window instanceof ItemContainerWindow) { ItemContainerWindow itemContainerWindow = (ItemContainerWindow)window;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 253 */       section = itemContainerWindow.getItemContainer().toPacket(); }
/*     */ 
/*     */     
/* 256 */     ExtraResources extraResources = null;
/* 257 */     if (window instanceof MaterialContainerWindow) { MaterialContainerWindow materialContainerWindow = (MaterialContainerWindow)window;
/* 258 */       if (!materialContainerWindow.isValid()) {
/* 259 */         extraResources = materialContainerWindow.getExtraResourcesSection().toPacket();
/*     */       } }
/*     */ 
/*     */ 
/*     */     
/* 264 */     this.playerRef.getPacketHandler().writeNoCache((Packet)new UpdateWindow(window.getId(), window.getData().toString(), section, extraResources));
/*     */ 
/*     */     
/* 267 */     window.consumeNeedRebuild();
/*     */     
/* 269 */     LOGGER.at(Level.FINER).log("%s update window %s with id %s and data %s", this.playerRef.getUuid(), window.getType(), Integer.valueOf(window.getId()), window.getData());
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
/*     */   @Nonnull
/*     */   public Window closeWindow(@Nonnull Ref<EntityStore> ref, int id, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 282 */     if (id == -1) throw new IllegalArgumentException("Window id -1 is invalid!");
/*     */     
/* 284 */     PlayerRef playerRefComponent = (PlayerRef)componentAccessor.getComponent(ref, PlayerRef.getComponentType());
/* 285 */     assert playerRefComponent != null;
/* 286 */     playerRefComponent.getPacketHandler().writeNoCache((Packet)new CloseWindow(id));
/*     */     
/* 288 */     Window window = (Window)this.windows.remove(id);
/* 289 */     if (window instanceof ItemContainerWindow) {
/* 290 */       ((EventRegistration)this.windowChangeEvents.remove(window.getId())).unregister();
/*     */     }
/*     */     
/* 293 */     if (window == null) {
/* 294 */       throw new IllegalStateException("Window id " + id + " is invalid!");
/*     */     }
/*     */     
/* 297 */     window.onClose(ref, componentAccessor);
/* 298 */     LOGGER.at(Level.FINE).log("%s close window %s with id %s", this.playerRef.getUuid(), window.getType(), Integer.valueOf(id));
/* 299 */     return window;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeAllWindows(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 309 */     for (Window window : this.windows.values()) {
/* 310 */       window.close(ref, componentAccessor);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void markWindowChanged(int id) {
/* 320 */     Window window = getWindow(id);
/* 321 */     if (window != null) {
/* 322 */       window.invalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateWindows() {
/* 330 */     this.windows.forEach((id, window, _windowManager) -> { if (window.consumeIsDirty()) _windowManager.updateWindow(window);  }this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void validateWindows(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 339 */     for (Window value : this.windows.values()) {
/* 340 */       if (value instanceof ValidatedWindow) { ValidatedWindow validatedWindow = (ValidatedWindow)value;
/* 341 */         if (!validatedWindow.validate(ref, componentAccessor)) {
/* 342 */           value.close(ref, componentAccessor);
/*     */         } }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <W extends Window> void closeAndRemoveAll(@Nonnull Map<UUID, W> windows) {
/* 355 */     for (Iterator<W> iterator = windows.values().iterator(); iterator.hasNext(); ) {
/* 356 */       Window window = (Window)iterator.next();
/*     */       
/* 358 */       PlayerRef playerRef = window.getPlayerRef();
/* 359 */       if (playerRef != null) {
/* 360 */         Ref<EntityStore> ref = playerRef.getReference();
/* 361 */         if (ref != null && ref.isValid()) {
/* 362 */           Store<EntityStore> store = ref.getStore();
/* 363 */           window.close(ref, (ComponentAccessor<EntityStore>)store);
/*     */         } 
/*     */       } 
/*     */       
/* 367 */       iterator.remove();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public String toString() {
/* 374 */     return "WindowManager{windowId=" + String.valueOf(this.windowId) + ", windows=" + String.valueOf(this.windows) + "}";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\windows\WindowManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */