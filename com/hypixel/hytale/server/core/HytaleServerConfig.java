/*      */ package com.hypixel.hytale.server.core;
/*      */ 
/*      */ import com.hypixel.hytale.codec.Codec;
/*      */ import com.hypixel.hytale.codec.DocumentContainingCodec;
/*      */ import com.hypixel.hytale.codec.ExtraInfo;
/*      */ import com.hypixel.hytale.codec.KeyedCodec;
/*      */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*      */ import com.hypixel.hytale.codec.codecs.map.MapCodec;
/*      */ import com.hypixel.hytale.codec.codecs.map.ObjectMapCodec;
/*      */ import com.hypixel.hytale.codec.lookup.Priority;
/*      */ import com.hypixel.hytale.codec.util.RawJsonReader;
/*      */ import com.hypixel.hytale.common.plugin.PluginIdentifier;
/*      */ import com.hypixel.hytale.common.semver.SemverRange;
/*      */ import com.hypixel.hytale.logger.HytaleLogger;
/*      */ import com.hypixel.hytale.protocol.GameMode;
/*      */ import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
/*      */ import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
/*      */ import com.hypixel.hytale.server.core.universe.playerdata.DefaultPlayerStorageProvider;
/*      */ import com.hypixel.hytale.server.core.universe.playerdata.DiskPlayerStorageProvider;
/*      */ import com.hypixel.hytale.server.core.universe.playerdata.PlayerStorageProvider;
/*      */ import com.hypixel.hytale.server.core.util.BsonUtil;
/*      */ import java.nio.file.Files;
/*      */ import java.nio.file.Path;
/*      */ import java.time.Duration;
/*      */ import java.time.temporal.ChronoUnit;
/*      */ import java.util.Collections;
/*      */ import java.util.Map;
/*      */ import java.util.Optional;
/*      */ import java.util.concurrent.CompletableFuture;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.function.Supplier;
/*      */ import java.util.logging.Level;
/*      */ import javax.annotation.Nonnull;
/*      */ import javax.annotation.Nullable;
/*      */ import org.bson.BsonDocument;
/*      */ import org.bson.BsonValue;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class HytaleServerConfig
/*      */ {
/*      */   public static final int VERSION = 3;
/*      */   public static final int DEFAULT_MAX_VIEW_RADIUS = 32;
/*      */   @Nonnull
/*   59 */   public static final Path PATH = Path.of("config.json", new String[0]);
/*      */   
/*      */   @Nonnull
/*      */   public static final BuilderCodec<HytaleServerConfig> CODEC;
/*      */   
/*      */   static {
/*   65 */     PlayerStorageProvider.CODEC.register(Priority.DEFAULT, "Hytale", DefaultPlayerStorageProvider.class, (Codec)DefaultPlayerStorageProvider.CODEC);
/*   66 */     PlayerStorageProvider.CODEC.register("Disk", DiskPlayerStorageProvider.class, (Codec)DiskPlayerStorageProvider.CODEC);
/*      */ 
/*      */     
/*   69 */     Module.BUILDER_CODEC_BUILDER.addField(new KeyedCodec("Modules", (Codec)new MapCodec((Codec)Module.CODEC, ConcurrentHashMap::new, false)), (o, m) -> o.modules = m, o -> o.modules);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  181 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(HytaleServerConfig.class, HytaleServerConfig::new).versioned()).codecVersion(3)).append(new KeyedCodec("ServerName", (Codec)Codec.STRING), (o, s) -> o.serverName = s, o -> o.serverName).add()).append(new KeyedCodec("MOTD", (Codec)Codec.STRING), (o, s) -> o.motd = s, o -> o.motd).add()).append(new KeyedCodec("Password", (Codec)Codec.STRING), (o, s) -> o.password = s, o -> o.password).add()).append(new KeyedCodec("MaxPlayers", (Codec)Codec.INTEGER), (o, i) -> o.maxPlayers = i.intValue(), o -> Integer.valueOf(o.maxPlayers)).add()).append(new KeyedCodec("MaxViewRadius", (Codec)Codec.INTEGER), (o, i) -> o.maxViewRadius = i.intValue(), o -> Integer.valueOf(o.maxViewRadius)).add()).append(new KeyedCodec("Defaults", (Codec)Defaults.CODEC), (o, obj) -> o.defaults = obj, o -> o.defaults).add()).append(new KeyedCodec("ConnectionTimeouts", ConnectionTimeouts.CODEC), (o, m) -> o.connectionTimeouts = m, o -> o.connectionTimeouts).add()).append(new KeyedCodec("RateLimit", RateLimitConfig.CODEC), (o, m) -> o.rateLimitConfig = m, o -> o.rateLimitConfig).add()).append(new KeyedCodec("Modules", (Codec)new MapCodec((Codec)Module.CODEC, ConcurrentHashMap::new, false)), (o, m) -> { o.modules = m; o.unmodifiableModules = Collections.unmodifiableMap(m); }o -> o.modules).add()).append(new KeyedCodec("LogLevels", (Codec)new MapCodec((Codec)Codec.LOG_LEVEL, ConcurrentHashMap::new, false)), (o, m) -> { o.logLevels = m; o.unmodifiableLogLevels = Collections.unmodifiableMap(o.logLevels); }o -> o.logLevels).add()).append(new KeyedCodec("Plugins", (Codec)new ObjectMapCodec((Codec)ModConfig.CODEC, it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false)), (o, i) -> o.legacyPluginConfig = i, o -> null).setVersionRange(0, 2).add()).append(new KeyedCodec("Mods", (Codec)new ObjectMapCodec((Codec)ModConfig.CODEC, ConcurrentHashMap::new, PluginIdentifier::toString, PluginIdentifier::fromString, false)), (o, i) -> o.modConfig = i, o -> o.modConfig).add()).append(new KeyedCodec("DisplayTmpTagsInStrings", (Codec)Codec.BOOLEAN), (o, displayTmpTagsInStrings) -> o.displayTmpTagsInStrings = displayTmpTagsInStrings.booleanValue(), o -> Boolean.valueOf(o.displayTmpTagsInStrings)).add()).append(new KeyedCodec("PlayerStorage", (Codec)PlayerStorageProvider.CODEC), (o, obj) -> o.playerStorageProvider = obj, o -> o.playerStorageProvider).add()).append(new KeyedCodec("AuthCredentialStore", (Codec)Codec.BSON_DOCUMENT), (o, value) -> o.authCredentialStoreConfig = value, o -> o.authCredentialStoreConfig).add()).afterDecode(config -> { config.defaults.hytaleServerConfig = config; config.connectionTimeouts.hytaleServerConfig = config; config.rateLimitConfig.hytaleServerConfig = config; config.modules.values().forEach(()); if (config.legacyPluginConfig != null && !config.legacyPluginConfig.isEmpty()) { for (Map.Entry<PluginIdentifier, ModConfig> entry : config.legacyPluginConfig.entrySet()) config.modConfig.putIfAbsent(entry.getKey(), entry.getValue());  config.legacyPluginConfig = null; config.markChanged(); }  })).build();
/*      */   }
/*      */ 
/*      */   
/*      */   @Nonnull
/*  186 */   private final transient AtomicBoolean hasChanged = new AtomicBoolean();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  192 */   private String serverName = "Hytale Server";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  197 */   private String motd = "";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  202 */   private String password = "";
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  207 */   private int maxPlayers = 100;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  212 */   private int maxViewRadius = 32;
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  217 */   private Defaults defaults = new Defaults(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  223 */   private ConnectionTimeouts connectionTimeouts = new ConnectionTimeouts(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  229 */   private RateLimitConfig rateLimitConfig = new RateLimitConfig(this);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  235 */   private Map<String, Module> modules = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  242 */   private Map<String, Level> logLevels = Collections.emptyMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*      */   private transient Map<PluginIdentifier, ModConfig> legacyPluginConfig;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  255 */   private Map<PluginIdentifier, ModConfig> modConfig = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  262 */   private Map<String, Module> unmodifiableModules = Collections.unmodifiableMap(this.modules);
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  268 */   private Map<String, Level> unmodifiableLogLevels = Collections.unmodifiableMap(this.logLevels);
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*  273 */   private PlayerStorageProvider playerStorageProvider = (PlayerStorageProvider)PlayerStorageProvider.CODEC
/*  274 */     .getDefault();
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*  280 */   private BsonDocument authCredentialStoreConfig = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nullable
/*  286 */   private transient AuthCredentialStoreProvider authCredentialStoreProvider = null;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean displayTmpTagsInStrings;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getServerName() {
/*  298 */     return this.serverName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setServerName(@Nonnull String serverName) {
/*  307 */     this.serverName = serverName;
/*  308 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getMotd() {
/*  315 */     return this.motd;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMotd(@Nonnull String motd) {
/*  324 */     this.motd = motd;
/*  325 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPassword() {
/*  332 */     return this.password;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPassword(@Nonnull String password) {
/*  341 */     this.password = password;
/*  342 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isDisplayTmpTagsInStrings() {
/*  349 */     return this.displayTmpTagsInStrings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDisplayTmpTagsInStrings(boolean displayTmpTagsInStrings) {
/*  358 */     this.displayTmpTagsInStrings = displayTmpTagsInStrings;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxPlayers() {
/*  365 */     return this.maxPlayers;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxPlayers(int maxPlayers) {
/*  374 */     this.maxPlayers = maxPlayers;
/*  375 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getMaxViewRadius() {
/*  382 */     return this.maxViewRadius;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setMaxViewRadius(int maxViewRadius) {
/*  391 */     this.maxViewRadius = maxViewRadius;
/*  392 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Defaults getDefaults() {
/*  400 */     return this.defaults;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setDefaults(@Nonnull Defaults defaults) {
/*  409 */     this.defaults = defaults;
/*  410 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public ConnectionTimeouts getConnectionTimeouts() {
/*  418 */     return this.connectionTimeouts;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setConnectionTimeouts(@Nonnull ConnectionTimeouts connectionTimeouts) {
/*  427 */     this.connectionTimeouts = connectionTimeouts;
/*  428 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public RateLimitConfig getRateLimitConfig() {
/*  436 */     return this.rateLimitConfig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setRateLimitConfig(@Nonnull RateLimitConfig rateLimitConfig) {
/*  445 */     this.rateLimitConfig = rateLimitConfig;
/*  446 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Map<String, Module> getModules() {
/*  454 */     return this.unmodifiableModules;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Module getModule(String moduleName) {
/*  465 */     return this.modules.computeIfAbsent(moduleName, k -> new Module(this));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModules(@Nonnull Map<String, Module> modules) {
/*  474 */     this.modules = modules;
/*  475 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Map<String, Level> getLogLevels() {
/*  483 */     return this.unmodifiableLogLevels;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setLogLevels(@Nonnull Map<String, Level> logLevels) {
/*  492 */     this.logLevels = logLevels;
/*  493 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public Map<PluginIdentifier, ModConfig> getModConfig() {
/*  501 */     return this.modConfig;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setModConfig(@Nonnull Map<PluginIdentifier, ModConfig> modConfig) {
/*  510 */     this.modConfig = modConfig;
/*  511 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public PlayerStorageProvider getPlayerStorageProvider() {
/*  519 */     return this.playerStorageProvider;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPlayerStorageProvider(@Nonnull PlayerStorageProvider playerStorageProvider) {
/*  528 */     this.playerStorageProvider = playerStorageProvider;
/*  529 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public AuthCredentialStoreProvider getAuthCredentialStoreProvider() {
/*  540 */     if (this.authCredentialStoreProvider != null) {
/*  541 */       return this.authCredentialStoreProvider;
/*      */     }
/*      */     
/*  544 */     if (this.authCredentialStoreConfig != null) {
/*  545 */       this.authCredentialStoreProvider = (AuthCredentialStoreProvider)AuthCredentialStoreProvider.CODEC.decode((BsonValue)this.authCredentialStoreConfig);
/*      */     } else {
/*  547 */       this.authCredentialStoreProvider = (AuthCredentialStoreProvider)AuthCredentialStoreProvider.CODEC.getDefault();
/*      */     } 
/*      */     
/*  550 */     return this.authCredentialStoreProvider;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAuthCredentialStoreProvider(@Nonnull AuthCredentialStoreProvider provider) {
/*  559 */     this.authCredentialStoreProvider = provider;
/*  560 */     this.authCredentialStoreConfig = (BsonDocument)AuthCredentialStoreProvider.CODEC.encode(provider);
/*  561 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void removeModule(@Nonnull String module) {
/*  570 */     this.modules.remove(module);
/*  571 */     markChanged();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void markChanged() {
/*  578 */     this.hasChanged.set(true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean consumeHasChanged() {
/*  585 */     return this.hasChanged.getAndSet(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static HytaleServerConfig load() {
/*  593 */     return load(PATH);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static HytaleServerConfig load(@Nonnull Path path) {
/*  606 */     if (!Files.isRegularFile(path, new java.nio.file.LinkOption[0])) {
/*  607 */       HytaleServerConfig hytaleServerConfig = new HytaleServerConfig();
/*      */ 
/*      */       
/*  610 */       if (!Options.getOptionSet().has(Options.BARE)) {
/*  611 */         save(hytaleServerConfig).join();
/*      */       }
/*  613 */       return hytaleServerConfig;
/*      */     } 
/*      */     
/*      */     try {
/*  617 */       HytaleServerConfig config = (HytaleServerConfig)RawJsonReader.readSyncWithBak(path, (Codec)CODEC, HytaleLogger.getLogger());
/*  618 */       if (config == null) {
/*  619 */         throw new RuntimeException("Failed to load server config from " + String.valueOf(path));
/*      */       }
/*  621 */       return config;
/*  622 */     } catch (Exception e) {
/*  623 */       throw new RuntimeException("Failed to read server config!", e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static CompletableFuture<Void> save(@Nonnull HytaleServerConfig hytaleServerConfig) {
/*  635 */     return save(PATH, hytaleServerConfig);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Nonnull
/*      */   public static CompletableFuture<Void> save(@Nonnull Path path, @Nonnull HytaleServerConfig hytaleServerConfig) {
/*  647 */     BsonDocument document = CODEC.encode(hytaleServerConfig, ExtraInfo.THREAD_LOCAL.get()).asDocument();
/*  648 */     return BsonUtil.writeDocument(path, document);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Module
/*      */   {
/*      */     @Nonnull
/*      */     protected static BuilderCodec.Builder<Module> BUILDER_CODEC_BUILDER;
/*      */ 
/*      */ 
/*      */     
/*      */     static {
/*  662 */       BUILDER_CODEC_BUILDER = (BuilderCodec.Builder<Module>)BuilderCodec.builder(Module.class, Module::new).addField(new KeyedCodec("Enabled", (Codec)Codec.BOOLEAN), (o, i) -> o.enabled = i, o -> o.enabled);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*  668 */     protected static BuilderCodec<Module> BUILDER_CODEC = BUILDER_CODEC_BUILDER.build(); @Nonnull
/*      */     public static final DocumentContainingCodec<Module> CODEC;
/*      */     private transient HytaleServerConfig hytaleServerConfig;
/*      */     private Boolean enabled;
/*      */     
/*      */     static {
/*  674 */       CODEC = new DocumentContainingCodec(BUILDER_CODEC, (o, i) -> o.document = i, o -> o.document);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*  690 */     private Map<String, Module> modules = new ConcurrentHashMap<>();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*  696 */     private BsonDocument document = new BsonDocument();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Module(@Nonnull HytaleServerConfig hytaleServerConfig) {
/*  713 */       this.hytaleServerConfig = hytaleServerConfig;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean isEnabled(boolean def) {
/*  723 */       return (this.enabled != null) ? this.enabled.booleanValue() : def;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setEnabled(boolean enabled) {
/*  732 */       this.enabled = Boolean.valueOf(enabled);
/*  733 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Boolean getEnabled() {
/*  740 */       return this.enabled;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*      */     public Map<String, Module> getModules() {
/*  748 */       return Collections.unmodifiableMap(this.modules);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*      */     public Module getModule(@Nonnull String moduleName) {
/*  759 */       return this.modules.computeIfAbsent(moduleName, k -> new Module(this.hytaleServerConfig));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setModules(@Nonnull Map<String, Module> modules) {
/*  768 */       this.modules = modules;
/*  769 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*      */     public BsonDocument getDocument() {
/*  777 */       return this.document;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public <T> T decode(@Nonnull Codec<T> codec) {
/*  789 */       return (T)codec.decode((BsonValue)this.document);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void encode(@Nonnull Codec<T> codec, @Nonnull T t) {
/*  800 */       this.document = codec.encode(t).asDocument();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nonnull
/*      */     public <T> Optional<T> getData(@Nonnull KeyedCodec<T> keyedCodec) {
/*  812 */       return keyedCodec.get(this.document);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public <T> T getDataOrNull(@Nonnull KeyedCodec<T> keyedCodec) {
/*  824 */       return (T)keyedCodec.getOrNull(this.document);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> T getDataNow(@Nonnull KeyedCodec<T> keyedCodec) {
/*  835 */       return (T)keyedCodec.getNow(this.document);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public <T> void put(@Nonnull KeyedCodec<T> keyedCodec, T t) {
/*  846 */       keyedCodec.put(this.document, t);
/*  847 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void setDocument(@Nonnull BsonDocument document) {
/*  856 */       this.document = document;
/*  857 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void setHytaleServerConfig(@Nonnull HytaleServerConfig hytaleServerConfig) {
/*  866 */       this.hytaleServerConfig = hytaleServerConfig;
/*  867 */       this.modules.values().forEach(module -> module.setHytaleServerConfig(hytaleServerConfig));
/*      */     }
/*      */     
/*      */     private Module() {} }
/*      */   
/*  872 */   public static class Defaults { public static final KeyedCodec<String> WORLD = new KeyedCodec("World", (Codec)Codec.STRING);
/*  873 */     public static final KeyedCodec<GameMode> GAMEMODE = new KeyedCodec("GameMode", (Codec)ProtocolCodecs.GAMEMODE_LEGACY);
/*      */     public static final BuilderCodec<Defaults> CODEC;
/*      */     private transient HytaleServerConfig hytaleServerConfig;
/*      */     
/*      */     static {
/*  878 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(Defaults.class, Defaults::new).addField(WORLD, (o, i) -> o.world = i, o -> o.world)).addField(GAMEMODE, (o, s) -> o.gameMode = s, o -> o.gameMode)).build();
/*      */     }
/*      */ 
/*      */     
/*  882 */     private String world = "default";
/*  883 */     private GameMode gameMode = GameMode.Adventure;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private Defaults(HytaleServerConfig hytaleServerConfig) {
/*  889 */       this.hytaleServerConfig = hytaleServerConfig;
/*      */     }
/*      */     
/*      */     public String getWorld() {
/*  893 */       return this.world;
/*      */     }
/*      */     
/*      */     public void setWorld(String world) {
/*  897 */       this.world = world;
/*  898 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */     
/*      */     public GameMode getGameMode() {
/*  902 */       return this.gameMode;
/*      */     }
/*      */     
/*      */     public void setGameMode(GameMode gameMode) {
/*  906 */       this.gameMode = gameMode;
/*  907 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */     
/*      */     private Defaults() {} }
/*      */   
/*  912 */   public static class ConnectionTimeouts { public static final Duration DEFAULT_INITIAL_TIMEOUT = Duration.of(10L, ChronoUnit.SECONDS);
/*  913 */     public static final Duration DEFAULT_AUTH_TIMEOUT = Duration.of(30L, ChronoUnit.SECONDS);
/*  914 */     public static final Duration DEFAULT_PLAY_TIMEOUT = Duration.of(1L, ChronoUnit.MINUTES);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static final Codec<ConnectionTimeouts> CODEC;
/*      */ 
/*      */ 
/*      */     
/*      */     private Duration initialTimeout;
/*      */ 
/*      */ 
/*      */     
/*      */     private Duration authTimeout;
/*      */ 
/*      */ 
/*      */     
/*      */     private Duration playTimeout;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static {
/*  937 */       CODEC = (Codec<ConnectionTimeouts>)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConnectionTimeouts.class, ConnectionTimeouts::new).addField(new KeyedCodec("InitialTimeout", (Codec)Codec.DURATION), (o, d) -> o.initialTimeout = d, o -> o.initialTimeout)).addField(new KeyedCodec("AuthTimeout", (Codec)Codec.DURATION), (o, d) -> o.authTimeout = d, o -> o.authTimeout)).addField(new KeyedCodec("PlayTimeout", (Codec)Codec.DURATION), (o, d) -> o.playTimeout = d, o -> o.playTimeout)).addField(new KeyedCodec("JoinTimeouts", (Codec)new MapCodec((Codec)Codec.DURATION, ConcurrentHashMap::new, false)), (o, m) -> o.joinTimeouts = m, o -> o.joinTimeouts)).build();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  942 */     private Map<String, Duration> joinTimeouts = new ConcurrentHashMap<>();
/*      */     @Nonnull
/*  944 */     private Map<String, Duration> unmodifiableJoinTimeouts = Collections.unmodifiableMap(this.joinTimeouts);
/*      */ 
/*      */     
/*      */     private transient HytaleServerConfig hytaleServerConfig;
/*      */ 
/*      */ 
/*      */     
/*      */     public ConnectionTimeouts(HytaleServerConfig hytaleServerConfig) {
/*  952 */       this.hytaleServerConfig = hytaleServerConfig;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Duration getInitialTimeout() {
/*  960 */       return (this.initialTimeout != null) ? this.initialTimeout : DEFAULT_INITIAL_TIMEOUT;
/*      */     }
/*      */     
/*      */     public void setInitialTimeout(Duration initialTimeout) {
/*  964 */       this.initialTimeout = initialTimeout;
/*  965 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Duration getAuthTimeout() {
/*  973 */       return (this.authTimeout != null) ? this.authTimeout : DEFAULT_AUTH_TIMEOUT;
/*      */     }
/*      */     
/*      */     public void setAuthTimeout(Duration authTimeout) {
/*  977 */       this.authTimeout = authTimeout;
/*  978 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */     
/*      */     public Duration getPlayTimeout() {
/*  982 */       return (this.playTimeout != null) ? this.playTimeout : DEFAULT_PLAY_TIMEOUT;
/*      */     }
/*      */     
/*      */     public void setPlayTimeout(Duration playTimeout) {
/*  986 */       this.playTimeout = playTimeout;
/*  987 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */     
/*      */     @Nonnull
/*      */     public Map<String, Duration> getJoinTimeouts() {
/*  992 */       return this.unmodifiableJoinTimeouts;
/*      */     }
/*      */     
/*      */     public void setJoinTimeouts(Map<String, Duration> joinTimeouts) {
/*  996 */       this.joinTimeouts = joinTimeouts;
/*  997 */       this.hytaleServerConfig.markChanged();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public ConnectionTimeouts() {} }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class RateLimitConfig
/*      */   {
/*      */     public static final int DEFAULT_PACKETS_PER_SECOND = 2000;
/*      */     
/*      */     public static final int DEFAULT_BURST_CAPACITY = 500;
/*      */     
/*      */     public static final Codec<RateLimitConfig> CODEC;
/*      */     
/*      */     private Boolean enabled;
/*      */     
/*      */     private Integer packetsPerSecond;
/*      */     
/*      */     private Integer burstCapacity;
/*      */     
/*      */     transient HytaleServerConfig hytaleServerConfig;
/*      */ 
/*      */     
/*      */     static {
/* 1024 */       CODEC = (Codec<RateLimitConfig>)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(RateLimitConfig.class, RateLimitConfig::new).addField(new KeyedCodec("Enabled", (Codec)Codec.BOOLEAN), (o, b) -> o.enabled = b, o -> o.enabled)).addField(new KeyedCodec("PacketsPerSecond", (Codec)Codec.INTEGER), (o, i) -> o.packetsPerSecond = i, o -> o.packetsPerSecond)).addField(new KeyedCodec("BurstCapacity", (Codec)Codec.INTEGER), (o, i) -> o.burstCapacity = i, o -> o.burstCapacity)).build();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public RateLimitConfig() {}
/*      */ 
/*      */ 
/*      */     
/*      */     public RateLimitConfig(HytaleServerConfig hytaleServerConfig) {
/* 1035 */       this.hytaleServerConfig = hytaleServerConfig;
/*      */     }
/*      */     
/*      */     public boolean isEnabled() {
/* 1039 */       return (this.enabled != null) ? this.enabled.booleanValue() : true;
/*      */     }
/*      */     
/*      */     public void setEnabled(boolean enabled) {
/* 1043 */       this.enabled = Boolean.valueOf(enabled);
/* 1044 */       if (this.hytaleServerConfig != null) this.hytaleServerConfig.markChanged(); 
/*      */     }
/*      */     
/*      */     public int getPacketsPerSecond() {
/* 1048 */       return (this.packetsPerSecond != null) ? this.packetsPerSecond.intValue() : 2000;
/*      */     }
/*      */     
/*      */     public void setPacketsPerSecond(int packetsPerSecond) {
/* 1052 */       this.packetsPerSecond = Integer.valueOf(packetsPerSecond);
/* 1053 */       if (this.hytaleServerConfig != null) this.hytaleServerConfig.markChanged(); 
/*      */     }
/*      */     
/*      */     public int getBurstCapacity() {
/* 1057 */       return (this.burstCapacity != null) ? this.burstCapacity.intValue() : 500;
/*      */     }
/*      */     
/*      */     public void setBurstCapacity(int burstCapacity) {
/* 1061 */       this.burstCapacity = Integer.valueOf(burstCapacity);
/* 1062 */       if (this.hytaleServerConfig != null) this.hytaleServerConfig.markChanged();
/*      */     
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class ModConfig
/*      */   {
/*      */     public static final BuilderCodec<ModConfig> CODEC;
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     private Boolean enabled;
/*      */     
/*      */     @Nullable
/*      */     private SemverRange requiredVersion;
/*      */ 
/*      */     
/*      */     static {
/* 1082 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ModConfig.class, ModConfig::new).append(new KeyedCodec("Enabled", (Codec)Codec.BOOLEAN), (modConfig, enabled) -> modConfig.enabled = enabled, modConfig -> modConfig.enabled).add()).append(new KeyedCodec("RequiredVersion", SemverRange.CODEC), (modConfig, semverRange) -> modConfig.requiredVersion = semverRange, modConfig -> modConfig.requiredVersion).add()).build();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Nullable
/*      */     public Boolean getEnabled() {
/* 1092 */       return this.enabled;
/*      */     }
/*      */     
/*      */     public void setEnabled(Boolean enabled) {
/* 1096 */       this.enabled = enabled;
/*      */     }
/*      */     
/*      */     @Nullable
/*      */     public SemverRange getRequiredVersion() {
/* 1101 */       return this.requiredVersion;
/*      */     }
/*      */     
/*      */     public void setRequiredVersion(SemverRange requiredVersion) {
/* 1105 */       this.requiredVersion = requiredVersion;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public static void setBoot(HytaleServerConfig serverConfig, PluginIdentifier identifier, boolean enabled) {
/* 1116 */       ((ModConfig)serverConfig.getModConfig()
/* 1117 */         .computeIfAbsent((K)identifier, id -> new ModConfig()))
/* 1118 */         .enabled = Boolean.valueOf(enabled);
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\HytaleServerConfig.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */