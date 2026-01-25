/*     */ package com.hypixel.hytale.builtin.teleport;
/*     */ import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
/*     */ import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
/*     */ import com.hypixel.hytale.builtin.teleport.commands.teleport.TeleportCommand;
/*     */ import com.hypixel.hytale.builtin.teleport.commands.warp.WarpCommand;
/*     */ import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.event.EventRegistry;
/*     */ import com.hypixel.hytale.math.util.MathUtil;
/*     */ import com.hypixel.hytale.math.vector.Transform;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.Model;
/*     */ import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandRegistry;
/*     */ import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPlugin;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
/*     */ import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
/*     */ import com.hypixel.hytale.server.core.universe.world.events.AllWorldsLoadedEvent;
/*     */ import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.universe.world.worldmap.markers.MapMarkerTracker;
/*     */ import com.hypixel.hytale.server.core.util.BsonUtil;
/*     */ import com.hypixel.hytale.server.core.util.PositionUtil;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.bson.BsonArray;
/*     */ import org.bson.BsonDocument;
/*     */ 
/*     */ public class TeleportPlugin extends JavaPlugin {
/*     */   private static TeleportPlugin instance;
/*     */   public static final String WARP_MODEL_ID = "Warp";
/*     */   private ComponentType<EntityStore, TeleportHistory> teleportHistoryComponentType;
/*     */   private ComponentType<EntityStore, WarpComponent> warpComponentType;
/*     */   
/*     */   @Nonnull
/*     */   public static TeleportPlugin get() {
/*  60 */     return instance;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  81 */   private final AtomicBoolean loaded = new AtomicBoolean();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  86 */   private final ReentrantLock saveLock = new ReentrantLock();
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  91 */   private final AtomicBoolean postSaveRedo = new AtomicBoolean(false);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  97 */   private final Map<String, Warp> warps = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Model warpModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TeleportPlugin(@Nonnull JavaPluginInit init) {
/* 111 */     super(init);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ComponentType<EntityStore, TeleportHistory> getTeleportHistoryComponentType() {
/* 119 */     return this.teleportHistoryComponentType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWarpsLoaded() {
/* 126 */     return this.loaded.get();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/* 131 */     instance = this;
/*     */     
/* 133 */     CommandRegistry commandRegistry = getCommandRegistry();
/* 134 */     EventRegistry eventRegistry = getEventRegistry();
/*     */     
/* 136 */     commandRegistry.registerCommand((AbstractCommand)new TeleportCommand());
/* 137 */     commandRegistry.registerCommand((AbstractCommand)new WarpCommand());
/* 138 */     commandRegistry.registerCommand((AbstractCommand)new SpawnCommand());
/*     */     
/* 140 */     eventRegistry.register(LoadedAssetsEvent.class, ModelAsset.class, this::onModelAssetChange);
/* 141 */     eventRegistry.registerGlobal(ChunkPreLoadProcessEvent.class, this::onChunkPreLoadProcess);
/*     */     
/* 143 */     eventRegistry.registerGlobal(AddWorldEvent.class, event -> event.getWorld().getWorldMapManager().addMarkerProvider("warps", WarpMarkerProvider.INSTANCE));
/* 144 */     eventRegistry.registerGlobal(AllWorldsLoadedEvent.class, event -> loadWarps());
/*     */     
/* 146 */     this.teleportHistoryComponentType = EntityStore.REGISTRY.registerComponent(TeleportHistory.class, TeleportHistory::new);
/*     */     
/* 148 */     this.warpComponentType = EntityStore.REGISTRY.registerComponent(WarpComponent.class, () -> {
/*     */           throw new UnsupportedOperationException("WarpComponent must be created manually");
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void start() {
/* 155 */     ModelAsset modelAsset = (ModelAsset)ModelAsset.getAssetMap().getAsset("Warp");
/* 156 */     if (modelAsset == null) {
/* 157 */       throw new IllegalStateException(String.format("Default warp model '%s' not found", new Object[] { "Warp" }));
/*     */     }
/*     */     
/* 160 */     this.warpModel = Model.createUnitScaleModel(modelAsset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void shutdown() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void loadWarps() {
/* 171 */     BsonDocument document = null;
/*     */     
/* 173 */     Path universePath = Universe.get().getPath();
/*     */     
/* 175 */     Path oldPath = universePath.resolve("warps.bson");
/* 176 */     Path path = universePath.resolve("warps.json");
/*     */     
/* 178 */     if (Files.exists(oldPath, new java.nio.file.LinkOption[0]) && !Files.exists(path, new java.nio.file.LinkOption[0])) {
/*     */       try {
/* 180 */         Files.move(oldPath, path, new java.nio.file.CopyOption[0]);
/* 181 */       } catch (IOException iOException) {}
/*     */     }
/*     */ 
/*     */     
/* 185 */     if (Files.exists(path, new java.nio.file.LinkOption[0])) {
/* 186 */       document = BsonUtil.readDocument(path).join();
/*     */     }
/*     */     
/* 189 */     if (document != null) {
/*     */ 
/*     */       
/* 192 */       BsonArray bsonWarps = document.containsKey("Warps") ? document.getArray("Warps") : document.getArray("warps");
/*     */       
/* 194 */       this.warps.clear();
/* 195 */       for (Warp warp : (Warp[])Warp.ARRAY_CODEC.decode((BsonValue)bsonWarps)) {
/* 196 */         this.warps.put(warp.getId().toLowerCase(), warp);
/*     */       }
/* 198 */       getLogger().at(Level.INFO).log("Loaded %d warps", bsonWarps.size());
/*     */     } else {
/* 200 */       getLogger().at(Level.INFO).log("Loaded 0 warps (No warps.json found)");
/*     */     } 
/*     */     
/* 203 */     this.loaded.set(true);
/*     */   }
/*     */   
/*     */   private void saveWarps0() {
/* 207 */     Warp[] array = (Warp[])this.warps.values().toArray(x$0 -> new Warp[x$0]);
/* 208 */     BsonDocument document = new BsonDocument("Warps", Warp.ARRAY_CODEC.encode(array));
/*     */     
/* 210 */     Path path = Universe.get().getPath().resolve("warps.json");
/* 211 */     BsonUtil.writeDocument(path, document).join();
/* 212 */     getLogger().at(Level.INFO).log("Saved %d warps to warps.json", array.length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void saveWarps() {
/* 219 */     if (this.saveLock.tryLock()) {
/*     */       try {
/* 221 */         saveWarps0();
/* 222 */       } catch (Throwable e) {
/* 223 */         ((HytaleLogger.Api)getLogger().at(Level.SEVERE).withCause(e)).log("Failed to save warps:");
/*     */       } finally {
/* 225 */         this.saveLock.unlock();
/*     */       } 
/* 227 */       if (this.postSaveRedo.getAndSet(false)) {
/* 228 */         saveWarps();
/*     */       }
/*     */     } else {
/* 231 */       this.postSaveRedo.set(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, Warp> getWarps() {
/* 239 */     return this.warps;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onModelAssetChange(@Nonnull LoadedAssetsEvent<String, ModelAsset, DefaultAssetMap<String, ModelAsset>> event) {
/* 248 */     Map<String, ModelAsset> modelMap = event.getLoadedAssets();
/* 249 */     ModelAsset modelAsset = modelMap.get("Warp");
/* 250 */     if (modelAsset == null)
/* 251 */       return;  this.warpModel = Model.createUnitScaleModel(modelAsset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void onChunkPreLoadProcess(@Nonnull ChunkPreLoadProcessEvent event) {
/* 260 */     WorldChunk chunk = event.getChunk();
/* 261 */     BlockChunk blockChunk = chunk.getBlockChunk();
/* 262 */     if (blockChunk == null)
/*     */       return; 
/* 264 */     int chunkX = blockChunk.getX();
/* 265 */     int chunkZ = blockChunk.getZ();
/* 266 */     World world = chunk.getWorld();
/* 267 */     String worldName = world.getName();
/*     */     
/* 269 */     for (Map.Entry<String, Warp> warpEntry : this.warps.entrySet()) {
/* 270 */       Warp warp = warpEntry.getValue();
/* 271 */       Transform transform = warp.getTransform();
/* 272 */       if (transform == null)
/*     */         continue; 
/* 274 */       Vector3d position = transform.getPosition();
/*     */       
/* 276 */       if (!ChunkUtil.isInsideChunk(chunkX, chunkZ, MathUtil.floor(position.x), MathUtil.floor(position.z)) || 
/* 277 */         !warp.getWorld().equals(worldName))
/*     */         continue; 
/* 279 */       world.execute(() -> {
/*     */             Store<EntityStore> store = world.getEntityStore().getStore();
/*     */             store.addEntity(createWarp(warp, store), AddReason.LOAD);
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Holder<EntityStore> createWarp(@Nonnull Warp warp, @Nonnull Store<EntityStore> store) {
/* 292 */     Transform transform = warp.getTransform();
/* 293 */     Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
/* 294 */     holder.addComponent(TransformComponent.getComponentType(), (Component)new TransformComponent(transform.getPosition(), transform.getRotation()));
/* 295 */     holder.addComponent(NetworkId.getComponentType(), (Component)new NetworkId(((EntityStore)store.getExternalData()).takeNextNetworkId()));
/* 296 */     holder.ensureComponent(Intangible.getComponentType());
/* 297 */     holder.addComponent(BoundingBox.getComponentType(), (Component)new BoundingBox(this.warpModel.getBoundingBox()));
/* 298 */     holder.addComponent(ModelComponent.getComponentType(), (Component)new ModelComponent(this.warpModel));
/* 299 */     holder.addComponent(Nameplate.getComponentType(), (Component)new Nameplate(warp.getId()));
/* 300 */     holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
/* 301 */     holder.ensureComponent(EntityStore.REGISTRY.getNonSerializedComponentType());
/* 302 */     holder.addComponent(this.warpComponentType, new WarpComponent(warp));
/* 303 */     return holder;
/*     */   }
/*     */   
/*     */   public static final class WarpComponent
/*     */     extends Record implements Component<EntityStore> {
/*     */     private final Warp warp;
/*     */     
/*     */     public WarpComponent(Warp warp) {
/* 311 */       this.warp = warp; } public final String toString() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #311	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/* 311 */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent; } public Warp warp() { return this.warp; }
/*     */     public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #311	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent; }
/*     */     public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #311	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/builtin/teleport/TeleportPlugin$WarpComponent;
/*     */       //   0	8	1	o	Ljava/lang/Object; } public static ComponentType<EntityStore, WarpComponent> getComponentType() {
/* 314 */       return (TeleportPlugin.get()).warpComponentType;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Component<EntityStore> clone() {
/* 320 */       return new WarpComponent(this.warp);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class WarpMarkerProvider
/*     */     implements WorldMapManager.MarkerProvider
/*     */   {
/* 333 */     public static final WarpMarkerProvider INSTANCE = new WarpMarkerProvider();
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
/*     */     
/*     */     public void update(@Nonnull World world, @Nonnull MapMarkerTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
/* 347 */       Map<String, Warp> warps = TeleportPlugin.get().getWarps();
/* 348 */       if (warps.isEmpty())
/*     */         return; 
/* 350 */       GameplayConfig gameplayConfig = world.getGameplayConfig();
/* 351 */       if (!gameplayConfig.getWorldMapConfig().isDisplayWarps())
/*     */         return; 
/* 353 */       for (Warp warp : warps.values()) {
/* 354 */         if (!warp.getWorld().equals(world.getName()))
/*     */           continue; 
/* 356 */         tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, warp
/* 357 */             .getTransform().getPosition(), warp.getTransform().getRotation().getYaw(), "Warp-" + warp.getId(), "Warp: " + warp.getId(), warp, (id, name, w) -> new MapMarker(id, name, "Warp.png", PositionUtil.toTransformPacket(w.getTransform()), null));
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\teleport\TeleportPlugin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */