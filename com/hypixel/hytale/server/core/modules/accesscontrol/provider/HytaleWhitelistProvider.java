/*     */ package com.hypixel.hytale.server.core.modules.accesscontrol.provider;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.JsonParser;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Optional;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ import java.util.function.Function;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class HytaleWhitelistProvider
/*     */   extends BlockingDiskFile
/*     */   implements AccessProvider
/*     */ {
/*     */   @Nonnull
/*     */   private static final String WHITELIST_FILE_PATH = "whitelist.json";
/*     */   @Nonnull
/*  32 */   private final ReadWriteLock lock = new ReentrantReadWriteLock();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*  38 */   private final Set<UUID> whitelist = new HashSet<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isEnabled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HytaleWhitelistProvider() {
/*  50 */     super(Paths.get("whitelist.json", new String[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void read(@Nonnull BufferedReader fileReader) {
/*  55 */     JsonElement element = JsonParser.parseReader(fileReader);
/*  56 */     if (element instanceof JsonObject) { JsonObject jsonObject = (JsonObject)element;
/*  57 */       this.isEnabled = jsonObject.get("enabled").getAsBoolean();
/*  58 */       jsonObject.get("list").getAsJsonArray().forEach(entry -> this.whitelist.add(UUID.fromString(entry.getAsString()))); }
/*     */     else
/*  60 */     { throw new JsonParseException("element is not JsonObject!"); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   protected void write(@Nonnull BufferedWriter fileWriter) throws IOException {
/*  66 */     JsonObject jsonObject = new JsonObject();
/*     */     
/*  68 */     jsonObject.addProperty("enabled", Boolean.valueOf(this.isEnabled));
/*     */ 
/*     */     
/*  71 */     JsonArray jsonArray = new JsonArray();
/*     */     
/*  73 */     for (UUID uuid : this.whitelist) {
/*  74 */       jsonArray.add(uuid.toString());
/*     */     }
/*     */     
/*  77 */     jsonObject.add("list", (JsonElement)jsonArray);
/*     */ 
/*     */     
/*  80 */     fileWriter.write(jsonObject.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void create(@Nonnull BufferedWriter fileWriter) throws IOException {
/*  85 */     JsonWriter jsonWriter = new JsonWriter(fileWriter); 
/*  86 */     try { jsonWriter.beginObject()
/*  87 */         .name("enabled").value(false)
/*  88 */         .name("list")
/*  89 */         .beginArray()
/*  90 */         .endArray()
/*  91 */         .endObject();
/*  92 */       jsonWriter.close(); }
/*     */     catch (Throwable throwable) { try {
/*     */         jsonWriter.close();
/*     */       } catch (Throwable throwable1) {
/*     */         throwable.addSuppressed(throwable1);
/*     */       }  throw throwable; }
/*  98 */      } @Nonnull public CompletableFuture<Optional<String>> getDisconnectReason(UUID uuid) { this.lock.readLock().lock();
/*     */     
/*     */     try {
/* 101 */       if (this.isEnabled && !this.whitelist.contains(uuid)) {
/* 102 */         return (CompletableFuture)CompletableFuture.completedFuture(Optional.of("You are not whitelisted!"));
/*     */       }
/*     */     } finally {
/* 105 */       this.lock.readLock().unlock();
/*     */     } 
/*     */     
/* 108 */     return CompletableFuture.completedFuture(Optional.empty()); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnabled(boolean isEnabled) {
/* 117 */     this.lock.writeLock().lock();
/*     */     
/*     */     try {
/* 120 */       this.isEnabled = isEnabled;
/*     */     } finally {
/* 122 */       this.lock.writeLock().unlock();
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
/*     */   public boolean modify(@Nonnull Function<Set<UUID>, Boolean> consumer) {
/*     */     boolean result;
/* 135 */     this.lock.writeLock().lock();
/*     */     
/*     */     try {
/* 138 */       result = ((Boolean)consumer.apply(this.whitelist)).booleanValue();
/*     */     } finally {
/* 140 */       this.lock.writeLock().unlock();
/*     */     } 
/*     */ 
/*     */     
/* 144 */     if (result) {
/* 145 */       syncSave();
/*     */     }
/*     */     
/* 148 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Set<UUID> getList() {
/* 156 */     this.lock.readLock().lock();
/*     */     
/*     */     try {
/* 159 */       return Collections.unmodifiableSet(this.whitelist);
/*     */     } finally {
/* 161 */       this.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnabled() {
/* 169 */     this.lock.readLock().lock();
/*     */     
/*     */     try {
/* 172 */       return this.isEnabled;
/*     */     } finally {
/* 174 */       this.lock.readLock().unlock();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\accesscontrol\provider\HytaleWhitelistProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */