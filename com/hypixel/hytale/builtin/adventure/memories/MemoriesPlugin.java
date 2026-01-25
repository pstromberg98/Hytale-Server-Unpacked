/*     */ package com.hypixel.hytale.builtin.adventure.memories;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.commands.MemoriesCommand;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.component.PlayerMemories;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.interactions.MemoriesConditionInteraction;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.interactions.SetMemoriesCapacityInteraction;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.MemoryProvider;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.npc.NPCMemory;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.npc.NPCMemoryProvider;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.page.MemoriesPage;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.page.MemoriesPageSupplier;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.temple.ForgottenTempleConfig;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.temple.TempleRespawnPlayersSystem;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.codec.util.RawJsonReader;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentRegistryProxy;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.dependency.Dependency;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.ISystem;
/*     */ import com.hypixel.hytale.component.system.RefSystem;
/*     */ import com.hypixel.hytale.server.core.Constants;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
/*     */ import com.hypixel.hytale.server.core.io.PacketHandler;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
/*     */ import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPlugin;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.plugin.PluginBase;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.BsonUtil;
/*     */ import com.hypixel.hytale.server.npc.AllNPCsLoadedEvent;
/*     */ import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectArrayList;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class MemoriesPlugin extends JavaPlugin {
/*     */   public static MemoriesPlugin get() {
/*  59 */     return instance;
/*     */   }
/*     */   private static MemoriesPlugin instance;
/*  62 */   private final Config<MemoriesPluginConfig> config = withConfig(MemoriesPluginConfig.CODEC);
/*     */   
/*  64 */   private final List<MemoryProvider<?>> providers = (List<MemoryProvider<?>>)new ObjectArrayList();
/*     */   
/*  66 */   private final Map<String, Set<Memory>> allMemories = (Map<String, Set<Memory>>)new Object2ObjectRBTreeMap();
/*     */   
/*     */   private ComponentType<EntityStore, PlayerMemories> playerMemoriesComponentType;
/*     */   @Nullable
/*     */   private RecordedMemories recordedMemories;
/*     */   private boolean hasInitializedMemories;
/*     */   
/*     */   public MemoriesPlugin(@Nonnull JavaPluginInit init) {
/*  74 */     super(init);
/*  75 */     instance = this;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  80 */     ComponentRegistryProxy<EntityStore> entityStoreRegistry = getEntityStoreRegistry();
/*     */     
/*  82 */     getCommandRegistry().registerCommand((AbstractCommand)new MemoriesCommand());
/*     */     
/*  84 */     OpenCustomUIInteraction.registerCustomPageSupplier((PluginBase)this, MemoriesPage.class, "Memories", (OpenCustomUIInteraction.CustomPageSupplier)new MemoriesPageSupplier());
/*  85 */     OpenCustomUIInteraction.registerCustomPageSupplier((PluginBase)this, MemoriesUnlockedPage.class, "MemoriesUnlocked", (OpenCustomUIInteraction.CustomPageSupplier)new MemoriesUnlockedPageSuplier());
/*  86 */     Window.CLIENT_REQUESTABLE_WINDOW_TYPES.put(WindowType.Memories, com.hypixel.hytale.builtin.adventure.memories.window.MemoriesWindow::new);
/*     */     
/*  88 */     this.playerMemoriesComponentType = entityStoreRegistry.registerComponent(PlayerMemories.class, "PlayerMemories", PlayerMemories.CODEC);
/*     */ 
/*     */     
/*  91 */     NPCMemoryProvider npcMemoryProvider = new NPCMemoryProvider();
/*  92 */     registerMemoryProvider((MemoryProvider<Memory>)npcMemoryProvider);
/*  93 */     entityStoreRegistry.registerSystem((ISystem)new NPCMemory.GatherMemoriesSystem(npcMemoryProvider.getCollectionRadius()));
/*     */ 
/*     */ 
/*     */     
/*  97 */     for (MemoryProvider<?> provider : this.providers) {
/*  98 */       BuilderCodec<? extends Memory> codec = provider.getCodec();
/*  99 */       getCodecRegistry((StringCodecMapCodec)Memory.CODEC).register(provider.getId(), codec.getInnerClass(), (Codec)codec);
/*     */     } 
/*     */ 
/*     */     
/* 103 */     getEventRegistry().register(AllNPCsLoadedEvent.class, event -> onAssetsLoad());
/*     */     
/* 105 */     entityStoreRegistry.registerSystem((ISystem)new PlayerAddedSystem());
/*     */     
/* 107 */     getCodecRegistry(Interaction.CODEC).register("SetMemoriesCapacity", SetMemoriesCapacityInteraction.class, SetMemoriesCapacityInteraction.CODEC);
/* 108 */     getCodecRegistry(GameplayConfig.PLUGIN_CODEC).register(MemoriesGameplayConfig.class, "Memories", (Codec)MemoriesGameplayConfig.CODEC);
/*     */     
/* 110 */     getCodecRegistry(Interaction.CODEC).register("MemoriesCondition", MemoriesConditionInteraction.class, MemoriesConditionInteraction.CODEC);
/*     */     
/* 112 */     entityStoreRegistry.registerSystem((ISystem)new TempleRespawnPlayersSystem());
/* 113 */     getCodecRegistry(GameplayConfig.PLUGIN_CODEC).register(ForgottenTempleConfig.class, "ForgottenTemple", (Codec)ForgottenTempleConfig.CODEC);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void start() {
/*     */     try {
/* 122 */       Path path = Constants.UNIVERSE_PATH.resolve("memories.json");
/* 123 */       if (Files.exists(path, new java.nio.file.LinkOption[0])) {
/* 124 */         this.recordedMemories = (RecordedMemories)RawJsonReader.readSync(path, (Codec)RecordedMemories.CODEC, getLogger());
/*     */       } else {
/* 126 */         this.recordedMemories = new RecordedMemories();
/*     */       } 
/* 128 */     } catch (IOException e) {
/* 129 */       throw new RuntimeException(e);
/*     */     } 
/*     */     
/* 132 */     this.hasInitializedMemories = true;
/* 133 */     onAssetsLoad();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void shutdown() {
/* 138 */     if (!this.hasInitializedMemories)
/* 139 */       return;  this.recordedMemories.lock.readLock().lock();
/*     */     try {
/* 141 */       BsonUtil.writeSync(Constants.UNIVERSE_PATH.resolve("memories.json"), (Codec)RecordedMemories.CODEC, this.recordedMemories, getLogger());
/* 142 */     } catch (IOException e) {
/* 143 */       throw new RuntimeException(e);
/*     */     } finally {
/* 145 */       this.recordedMemories.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void onAssetsLoad() {
/* 150 */     if (!this.hasInitializedMemories)
/*     */       return; 
/* 152 */     this.allMemories.clear();
/* 153 */     for (MemoryProvider<?> provider : this.providers) {
/* 154 */       for (Map.Entry<String, Set<Memory>> entry : (Iterable<Map.Entry<String, Set<Memory>>>)provider.getAllMemories().entrySet()) {
/* 155 */         ((Set)this.allMemories.computeIfAbsent(entry.getKey(), k -> new HashSet())).addAll(entry.getValue());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public MemoriesPluginConfig getConfig() {
/* 161 */     return (MemoriesPluginConfig)this.config.get();
/*     */   }
/*     */   
/*     */   public ComponentType<EntityStore, PlayerMemories> getPlayerMemoriesComponentType() {
/* 165 */     return this.playerMemoriesComponentType;
/*     */   }
/*     */   
/*     */   public <T extends Memory> void registerMemoryProvider(MemoryProvider<T> memoryProvider) {
/* 169 */     this.providers.add(memoryProvider);
/*     */   }
/*     */   
/*     */   public Map<String, Set<Memory>> getAllMemories() {
/* 173 */     return this.allMemories;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemoriesLevel(@Nonnull GameplayConfig gameplayConfig) {
/* 183 */     MemoriesGameplayConfig config = MemoriesGameplayConfig.get(gameplayConfig);
/* 184 */     int memoriesLevel = 1;
/* 185 */     if (config == null) {
/* 186 */       return memoriesLevel;
/*     */     }
/* 188 */     int recordedMemoriesCount = getRecordedMemories().size();
/*     */     
/* 190 */     int[] memoriesAmountPerLevel = config.getMemoriesAmountPerLevel();
/*     */ 
/*     */     
/* 193 */     for (int i = memoriesAmountPerLevel.length - 1; i >= 0; i--) {
/* 194 */       if (recordedMemoriesCount >= memoriesAmountPerLevel[i])
/*     */       {
/* 196 */         return i + 2;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 201 */     return memoriesLevel;
/*     */   }
/*     */   
/*     */   public boolean hasRecordedMemory(Memory memory) {
/* 205 */     this.recordedMemories.lock.readLock().lock();
/*     */     try {
/* 207 */       return this.recordedMemories.memories.contains(memory);
/*     */     } finally {
/* 209 */       this.recordedMemories.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean recordPlayerMemories(@Nonnull PlayerMemories playerMemories) {
/* 214 */     this.recordedMemories.lock.writeLock().lock();
/*     */     try {
/* 216 */       if (playerMemories.takeMemories(this.recordedMemories.memories)) {
/* 217 */         BsonUtil.writeSync(Constants.UNIVERSE_PATH.resolve("memories.json"), (Codec)RecordedMemories.CODEC, this.recordedMemories, getLogger());
/* 218 */         return true;
/*     */       } 
/* 220 */     } catch (IOException e) {
/* 221 */       throw new RuntimeException(e);
/*     */     } finally {
/* 223 */       this.recordedMemories.lock.writeLock().unlock();
/*     */     } 
/* 225 */     return false;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Set<Memory> getRecordedMemories() {
/* 230 */     this.recordedMemories.lock.readLock().lock();
/*     */     try {
/* 232 */       return new HashSet<>(this.recordedMemories.memories);
/*     */     } finally {
/* 234 */       this.recordedMemories.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void clearRecordedMemories() {
/* 239 */     this.recordedMemories.lock.writeLock().lock();
/*     */     try {
/* 241 */       this.recordedMemories.memories.clear();
/* 242 */       BsonUtil.writeSync(Constants.UNIVERSE_PATH.resolve("memories.json"), (Codec)RecordedMemories.CODEC, this.recordedMemories, getLogger());
/* 243 */     } catch (IOException e) {
/* 244 */       throw new RuntimeException(e);
/*     */     } finally {
/* 246 */       this.recordedMemories.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recordAllMemories() {
/* 251 */     this.recordedMemories.lock.writeLock().lock();
/*     */     try {
/* 253 */       for (Map.Entry<String, Set<Memory>> entry : this.allMemories.entrySet()) {
/* 254 */         this.recordedMemories.memories.addAll(entry.getValue());
/*     */       }
/* 256 */       BsonUtil.writeSync(Constants.UNIVERSE_PATH.resolve("memories.json"), (Codec)RecordedMemories.CODEC, this.recordedMemories, getLogger());
/* 257 */     } catch (IOException e) {
/* 258 */       throw new RuntimeException(e);
/*     */     } finally {
/* 260 */       this.recordedMemories.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int setRecordedMemoriesCount(int count) {
/* 272 */     if (count < 0) {
/* 273 */       count = 0;
/*     */     }
/*     */     
/* 276 */     this.recordedMemories.lock.writeLock().lock();
/*     */     try {
/* 278 */       this.recordedMemories.memories.clear();
/*     */ 
/*     */       
/* 281 */       ObjectArrayList<Memory> objectArrayList = new ObjectArrayList();
/* 282 */       for (Map.Entry<String, Set<Memory>> entry : this.allMemories.entrySet()) {
/* 283 */         objectArrayList.addAll(entry.getValue());
/*     */       }
/*     */ 
/*     */       
/* 287 */       int actualCount = Math.min(count, objectArrayList.size()); int i;
/* 288 */       for (i = 0; i < actualCount; i++) {
/* 289 */         this.recordedMemories.memories.add(objectArrayList.get(i));
/*     */       }
/*     */       
/* 292 */       BsonUtil.writeSync(Constants.UNIVERSE_PATH.resolve("memories.json"), (Codec)RecordedMemories.CODEC, this.recordedMemories, getLogger());
/* 293 */       i = actualCount; return i;
/* 294 */     } catch (IOException e) {
/* 295 */       throw new RuntimeException(e);
/*     */     } finally {
/* 297 */       this.recordedMemories.lock.writeLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static class MemoriesPluginConfig
/*     */   {
/*     */     public static final BuilderCodec<MemoriesPluginConfig> CODEC;
/*     */     
/*     */     private Object2DoubleMap<String> collectionRadius;
/*     */     
/*     */     static {
/* 309 */       CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(MemoriesPluginConfig.class, MemoriesPluginConfig::new).append(new KeyedCodec("CollectionRadius", (Codec)new Object2DoubleMapCodec((Codec)Codec.STRING, it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap::new)), (config, map) -> config.collectionRadius = map, config -> config.collectionRadius).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Object2DoubleMap<String> getCollectionRadius() {
/* 319 */       return (this.collectionRadius != null) ? this.collectionRadius : (Object2DoubleMap<String>)Object2DoubleMaps.EMPTY_MAP;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class RecordedMemories
/*     */   {
/*     */     public static final BuilderCodec<RecordedMemories> CODEC;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/* 334 */       CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(RecordedMemories.class, RecordedMemories::new).append(new KeyedCodec("Memories", (Codec)new ArrayCodec((Codec)Memory.CODEC, x$0 -> new Memory[x$0])), (recordedMemories, memories) -> { if (memories == null) return;  Collections.addAll(recordedMemories.memories, memories); }recordedMemories -> (Memory[])recordedMemories.memories.toArray(())).add()).build();
/*     */     }
/* 336 */     private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
/* 337 */     private final Set<Memory> memories = new HashSet<>();
/*     */   }
/*     */   
/*     */   public static class PlayerAddedSystem
/*     */     extends RefSystem<EntityStore> {
/*     */     @Nonnull
/* 343 */     private final Set<Dependency<EntityStore>> dependencies = (Set)Set.of(new SystemDependency(Order.AFTER, PlayerSystems.PlayerSpawnedSystem.class));
/*     */     
/*     */     @Nonnull
/*     */     private final Query<EntityStore> query;
/*     */     
/*     */     public PlayerAddedSystem() {
/* 349 */       this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)Player.getComponentType(), (Query)PlayerRef.getComponentType() });
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Query<EntityStore> getQuery() {
/* 355 */       return this.query;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public Set<Dependency<EntityStore>> getDependencies() {
/* 361 */       return this.dependencies;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onEntityAdded(@Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 366 */       Player playerComponent = (Player)store.getComponent(ref, Player.getComponentType());
/* 367 */       assert playerComponent != null;
/*     */       
/* 369 */       PlayerRef playerRefComponent = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/* 370 */       assert playerRefComponent != null;
/*     */       
/* 372 */       PlayerMemories playerMemoriesComponent = (PlayerMemories)store.getComponent(ref, PlayerMemories.getComponentType());
/* 373 */       boolean isFeatureUnlockedByPlayer = (playerMemoriesComponent != null);
/*     */       
/* 375 */       PacketHandler playerConnection = playerRefComponent.getPacketHandler();
/* 376 */       playerConnection.writeNoCache((Packet)new UpdateMemoriesFeatureStatus(isFeatureUnlockedByPlayer));
/*     */     }
/*     */     
/*     */     public void onEntityRemove(@Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\MemoriesPlugin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */