/*     */ package com.hypixel.hytale.builtin.adventure.memories.memories.npc;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.Memory;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.memories.MemoryProvider;
/*     */ import com.hypixel.hytale.logger.sentry.SkipSentryException;
/*     */ import com.hypixel.hytale.server.npc.NPCPlugin;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.Builder;
/*     */ import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
/*     */ import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
/*     */ import com.hypixel.hytale.server.npc.util.expression.Scope;
/*     */ import com.hypixel.hytale.server.spawning.ISpawnableWithModel;
/*     */ import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
/*     */ import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class NPCMemoryProvider
/*     */   extends MemoryProvider<NPCMemory> {
/*     */   public NPCMemoryProvider() {
/*  26 */     super("NPC", NPCMemory.CODEC, 10.0D);
/*     */   }
/*     */   public static final double DEFAULT_RADIUS = 10.0D;
/*     */   
/*     */   @Nonnull
/*     */   public Map<String, Set<Memory>> getAllMemories() {
/*  32 */     Object2ObjectOpenHashMap<String, Set<NPCMemory>> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
/*     */     
/*  34 */     Int2ObjectMap<BuilderInfo> allBuilders = NPCPlugin.get().getBuilderManager().getAllBuilders();
/*  35 */     for (ObjectIterator<BuilderInfo> objectIterator = allBuilders.values().iterator(); objectIterator.hasNext(); ) { BuilderInfo builderInfo = objectIterator.next(); try {
/*     */         NPCMemory memory;
/*  37 */         Builder<?> builder = builderInfo.getBuilder();
/*  38 */         if (!builder.isSpawnable() || 
/*  39 */           builder.isDeprecated() || 
/*  40 */           !builderInfo.isValid() || 
/*  41 */           !isMemory(builder))
/*     */           continue; 
/*  43 */         String category = getCategory(builder);
/*  44 */         if (category == null) {
/*     */           continue;
/*     */         }
/*     */         
/*  48 */         String memoriesNameOverride = getMemoriesNameOverride(builder);
/*  49 */         String translationKey = getNPCNameTranslationKey(builder);
/*  50 */         if (memoriesNameOverride != null && !memoriesNameOverride.isEmpty()) {
/*  51 */           memory = new NPCMemory(memoriesNameOverride, translationKey, true);
/*     */         } else {
/*  53 */           memory = new NPCMemory(builderInfo.getKeyName(), translationKey, false);
/*     */         } 
/*     */         
/*  56 */         ((Set<NPCMemory>)object2ObjectOpenHashMap.computeIfAbsent(category, s -> new HashSet())).add(memory);
/*  57 */       } catch (SkipSentryException e) {
/*  58 */         MemoriesPlugin.get().getLogger().at(Level.SEVERE).log(e.getMessage());
/*     */       }  }
/*     */     
/*  61 */     return (Map)object2ObjectOpenHashMap;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String getCategory(@Nonnull Builder<?> builder) {
/*  66 */     if (builder instanceof ISpawnableWithModel) { ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)builder;
/*  67 */       ExecutionContext executionContext = new ExecutionContext();
/*  68 */       executionContext.setScope(spawnableWithModel.createExecutionScope());
/*  69 */       Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
/*  70 */       return spawnableWithModel.getMemoriesCategory(executionContext, modifierScope); }
/*     */ 
/*     */     
/*  73 */     return "Other";
/*     */   }
/*     */   
/*     */   private static boolean isMemory(@Nonnull Builder<?> builder) {
/*  77 */     if (builder instanceof ISpawnableWithModel) { ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)builder;
/*  78 */       ExecutionContext executionContext = new ExecutionContext();
/*  79 */       executionContext.setScope(spawnableWithModel.createExecutionScope());
/*  80 */       Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
/*  81 */       return spawnableWithModel.isMemory(executionContext, modifierScope); }
/*     */ 
/*     */     
/*  84 */     return false;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String getMemoriesNameOverride(@Nonnull Builder<?> builder) {
/*  89 */     if (builder instanceof ISpawnableWithModel) { ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)builder;
/*  90 */       ExecutionContext executionContext = new ExecutionContext();
/*  91 */       executionContext.setScope(spawnableWithModel.createExecutionScope());
/*  92 */       Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
/*  93 */       return spawnableWithModel.getMemoriesNameOverride(executionContext, modifierScope); }
/*     */ 
/*     */     
/*  96 */     return null;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   private static String getNPCNameTranslationKey(@Nonnull Builder<?> builder) {
/* 101 */     if (builder instanceof ISpawnableWithModel) { ISpawnableWithModel spawnableWithModel = (ISpawnableWithModel)builder;
/* 102 */       ExecutionContext executionContext = new ExecutionContext();
/* 103 */       executionContext.setScope(spawnableWithModel.createExecutionScope());
/* 104 */       Scope modifierScope = spawnableWithModel.createModifierScope(executionContext);
/* 105 */       return spawnableWithModel.getNameTranslationKey(executionContext, modifierScope); }
/*     */ 
/*     */     
/* 108 */     throw new SkipSentryException(new IllegalStateException("Cannot get translation key for a non spawnable NPC role!"));
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\memories\npc\NPCMemoryProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */