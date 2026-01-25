/*     */ package com.hypixel.hytale.procedurallib.json;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import java.io.File;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class JsonLoader<K extends SeedResource, T>
/*     */   extends Loader<K, T>
/*     */ {
/*     */   @Nullable
/*     */   protected final JsonElement json;
/*     */   
/*     */   public JsonLoader(SeedString<K> seed, Path dataFolder, @Nullable JsonElement json) {
/*  29 */     super(seed, dataFolder);
/*  30 */     if (json != null && json.isJsonObject() && json.getAsJsonObject().has("File")) {
/*  31 */       this.json = loadFileConstructor(json.getAsJsonObject().get("File").getAsString());
/*     */     } else {
/*  33 */       this.json = json;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean has(String name) {
/*  38 */     return (this.json != null && this.json.isJsonObject() && this.json.getAsJsonObject().has(name));
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public JsonElement getOrLoad(@Nonnull JsonElement element) {
/*  43 */     if (element.isJsonObject()) {
/*  44 */       JsonObject obj = element.getAsJsonObject();
/*  45 */       JsonElement path = obj.get("File");
/*     */       
/*  47 */       if (path != null && path.isJsonPrimitive() && path.getAsJsonPrimitive().isString()) {
/*  48 */         JsonElement loaded = loadFileElem(path.getAsString());
/*  49 */         element = Objects.<JsonElement>requireNonNullElse(loaded, element);
/*     */       } 
/*     */     } 
/*  52 */     return element;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public JsonElement get(String name) {
/*  57 */     if (this.json == null || !this.json.isJsonObject()) return null; 
/*  58 */     JsonElement element = this.json.getAsJsonObject().get(name);
/*  59 */     if (element != null && element.isJsonObject()) {
/*  60 */       element = getOrLoad(element);
/*     */     }
/*  62 */     return element;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public JsonElement getRaw(String name) {
/*  67 */     if (this.json == null || !this.json.isJsonObject()) return null; 
/*  68 */     return this.json.getAsJsonObject().get(name);
/*     */   }
/*     */   
/*     */   protected JsonElement loadFile(@Nonnull String filePath) {
/*  72 */     Path file = this.dataFolder.resolve(filePath.replace('.', File.separatorChar) + ".json");
/*     */     try {
/*  74 */       JsonReader reader = new JsonReader(Files.newBufferedReader(file)); 
/*  75 */       try { JsonElement jsonElement = JsonParser.parseReader(reader);
/*  76 */         reader.close(); return jsonElement; } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; } 
/*  77 */     } catch (Throwable e) {
/*  78 */       throw new Error("Error while loading file reference." + file.toString(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected JsonElement loadFileElem(@Nonnull String filePath) {
/*  83 */     return loadFile(filePath);
/*     */   }
/*     */   
/*     */   protected JsonElement loadFileConstructor(@Nonnull String filePath) {
/*  87 */     return loadFile(filePath);
/*     */   }
/*     */   @Nonnull
/*     */   protected JsonObject mustGetObject(@Nonnull String key, @Nullable JsonObject defaultValue) {
/*  91 */     return mustGet(key, defaultValue, JsonObject.class, JsonElement::isJsonObject, JsonElement::getAsJsonObject);
/*     */   }
/*     */   @Nonnull
/*     */   protected JsonArray mustGetArray(@Nonnull String key, @Nullable JsonArray defaultValue) {
/*  95 */     return mustGet(key, defaultValue, JsonArray.class, JsonElement::isJsonArray, JsonElement::getAsJsonArray);
/*     */   }
/*     */   @Nonnull
/*     */   protected String mustGetString(@Nonnull String key, @Nullable String defaultValue) {
/*  99 */     return mustGet(key, defaultValue, String.class, JsonLoader::isString, JsonElement::getAsString);
/*     */   }
/*     */   @Nonnull
/*     */   protected Boolean mustGetBool(@Nonnull String key, @Nullable Boolean defaultValue) {
/* 103 */     return mustGet(key, defaultValue, Boolean.class, JsonLoader::isBoolean, JsonElement::getAsBoolean);
/*     */   }
/*     */   @Nonnull
/*     */   protected Number mustGetNumber(@Nonnull String key, @Nullable Number defaultValue) {
/* 107 */     return mustGet(key, defaultValue, Number.class, JsonLoader::isNumber, JsonElement::getAsNumber);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected <V> V mustGet(@Nonnull String key, @Nullable V defaultValue, @Nonnull Class<V> type, @Nonnull Predicate<JsonElement> predicate, @Nonnull Function<JsonElement, V> mapper) {
/* 115 */     return mustGet(key, get(key), defaultValue, type, predicate, mapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static <V> V mustGet(@Nonnull String key, @Nullable JsonElement element, @Nullable V defaultValue, @Nonnull Class<V> type, @Nonnull Predicate<JsonElement> predicate, @Nonnull Function<JsonElement, V> mapper) {
/* 125 */     if (element == null) {
/* 126 */       if (defaultValue != null) {
/* 127 */         return defaultValue;
/*     */       }
/*     */       
/* 130 */       throw error("Missing property '%s'", new Object[] { key });
/*     */     } 
/*     */     
/* 133 */     if (!predicate.test(element)) {
/* 134 */       throw error("Property '%s' must be of type '%s'", new Object[] { key, type.getSimpleName() });
/*     */     }
/*     */     
/* 137 */     return mapper.apply(element);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static Error error(String format, Object... args) {
/* 146 */     return new Error(String.format(format, args));
/*     */   }
/*     */   
/*     */   protected static Error error(Throwable parent, String format, Object... args) {
/* 150 */     return new Error(String.format(format, args), parent);
/*     */   }
/*     */   
/*     */   private static boolean isString(JsonElement element) {
/* 154 */     return (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString());
/*     */   }
/*     */   
/*     */   protected static boolean isNumber(JsonElement element) {
/* 158 */     return (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber());
/*     */   }
/*     */   
/*     */   protected static boolean isBoolean(JsonElement element) {
/* 162 */     return (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean());
/*     */   }
/*     */   
/*     */   public static interface Constants {
/*     */     public static final char JSON_FILEPATH_SEPARATOR = '.';
/*     */     public static final String KEY_FILE = "File";
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\procedurallib\json\JsonLoader.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */