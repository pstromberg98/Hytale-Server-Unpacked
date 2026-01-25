/*     */ package com.hypixel.hytale.server.core.entity.entities.player.windows;
/*     */ 
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventPriority;
/*     */ import com.hypixel.hytale.event.EventRegistration;
/*     */ import com.hypixel.hytale.event.IBaseEvent;
/*     */ import com.hypixel.hytale.event.IEvent;
/*     */ import com.hypixel.hytale.event.SyncEventBusRegistry;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowAction;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Window
/*     */ {
/*  33 */   public static final Map<WindowType, Supplier<? extends Window>> CLIENT_REQUESTABLE_WINDOW_TYPES = new ConcurrentHashMap<>();
/*     */   
/*     */   @Nonnull
/*  36 */   protected static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  41 */   protected final SyncEventBusRegistry<Void, WindowCloseEvent> closeEventRegistry = new SyncEventBusRegistry(LOGGER, WindowCloseEvent.class);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected final WindowType windowType;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  53 */   protected final AtomicBoolean isDirty = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  59 */   protected final AtomicBoolean needRebuild = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int id;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private WindowManager manager;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private PlayerRef playerRef;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Window(@Nonnull WindowType windowType) {
/*  85 */     this.windowType = windowType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(@Nonnull PlayerRef playerRef, @Nonnull WindowManager manager) {
/*  95 */     this.playerRef = playerRef;
/*  96 */     this.manager = manager;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public abstract JsonObject getData();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean onOpen(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/* 113 */     return onOpen0(ref, store);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract boolean onOpen0(@Nonnull Ref<EntityStore> paramRef, @Nonnull Store<EntityStore> paramStore);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void onClose(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/*     */     try {
/* 133 */       onClose0(ref, componentAccessor);
/*     */     } finally {
/* 135 */       this.closeEventRegistry.dispatchFor(null).dispatch((IBaseEvent)new WindowCloseEvent());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void onClose0(@Nonnull Ref<EntityStore> paramRef, @Nonnull ComponentAccessor<EntityStore> paramComponentAccessor);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleAction(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull WindowAction action) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public WindowType getType() {
/* 162 */     return this.windowType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setId(int id) {
/* 171 */     this.id = id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getId() {
/* 178 */     return this.id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PlayerRef getPlayerRef() {
/* 186 */     return this.playerRef;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 193 */     assert this.manager != null;
/* 194 */     this.manager.closeWindow(ref, this.id, componentAccessor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void invalidate() {
/* 201 */     this.isDirty.set(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setNeedRebuild() {
/* 208 */     this.needRebuild.set(true);
/* 209 */     getData().addProperty("needRebuild", Boolean.TRUE);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean consumeIsDirty() {
/* 216 */     return this.isDirty.getAndSet(false);
/*     */   }
/*     */   
/*     */   protected void consumeNeedRebuild() {
/* 220 */     if (this.needRebuild.get()) {
/* 221 */       getData().remove("needRebuild");
/* 222 */       this.needRebuild.set(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public EventRegistration registerCloseEvent(@Nonnull Consumer<WindowCloseEvent> consumer) {
/* 234 */     return this.closeEventRegistry.register((short)0, null, consumer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public EventRegistration registerCloseEvent(short priority, @Nonnull Consumer<WindowCloseEvent> consumer) {
/* 246 */     return this.closeEventRegistry.register(priority, null, consumer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public EventRegistration registerCloseEvent(@Nonnull EventPriority priority, @Nonnull Consumer<WindowCloseEvent> consumer) {
/* 258 */     return this.closeEventRegistry.register(priority.getValue(), null, consumer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(@Nullable Object o) {
/* 263 */     if (this == o) return true; 
/* 264 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 266 */     Window window = (Window)o;
/*     */     
/* 268 */     if (this.id != window.id) return false; 
/* 269 */     if (!Objects.equals(this.windowType, window.windowType)) return false; 
/* 270 */     return Objects.equals(this.playerRef, window.playerRef);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 275 */     int result = this.windowType.hashCode();
/* 276 */     result = 31 * result + this.id;
/* 277 */     result = 31 * result + ((this.playerRef != null) ? this.playerRef.hashCode() : 0);
/* 278 */     return result;
/*     */   }
/*     */   
/*     */   public static class WindowCloseEvent implements IEvent<Void> {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\entity\entities\player\windows\Window.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */