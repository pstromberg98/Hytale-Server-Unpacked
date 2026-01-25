/*     */ package com.hypixel.hytale.builtin.crafting.window;
/*     */ 
/*     */ import com.google.gson.JsonObject;
/*     */ import com.hypixel.hytale.builtin.adventure.memories.MemoriesPlugin;
/*     */ import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
/*     */ import com.hypixel.hytale.builtin.crafting.state.BenchState;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.protocol.SoundCategory;
/*     */ import com.hypixel.hytale.protocol.packets.window.WindowType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.CraftingConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.item.config.Item;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.BlockWindow;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialContainerWindow;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialExtraResourcesSection;
/*     */ import com.hypixel.hytale.server.core.universe.world.SoundUtil;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public abstract class BenchWindow
/*     */   extends BlockWindow
/*     */   implements MaterialContainerWindow
/*     */ {
/*     */   private static final float CRAFTING_UPDATE_MIN_PERCENT = 0.05F;
/*     */   private static final long CRAFTING_UPDATE_INTERVAL_MS = 500L;
/*     */   protected static final String BENCH_UPGRADING = "BenchUpgrading";
/*     */   private float lastUpdatePercent;
/*     */   private long lastUpdateTimeMs;
/*     */   protected final Bench bench;
/*     */   protected final BenchState benchState;
/*  34 */   protected final JsonObject windowData = new JsonObject();
/*     */   @Nonnull
/*  36 */   private final MaterialExtraResourcesSection extraResourcesSection = new MaterialExtraResourcesSection();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BenchWindow(@Nonnull WindowType windowType, @Nonnull BenchState benchState) {
/*  46 */     super(windowType, benchState.getBlockX(), benchState.getBlockY(), benchState.getBlockZ(), benchState.getRotationIndex(), benchState.getBlockType());
/*     */     
/*  48 */     this.bench = this.blockType.getBench();
/*  49 */     this.benchState = benchState;
/*     */     
/*  51 */     Item item = this.blockType.getItem();
/*  52 */     if (item == null) {
/*  53 */       throw new IllegalStateException("Bench block type " + this.blockType.getId() + " does not have an associated item!");
/*     */     }
/*     */     
/*  56 */     this.windowData.addProperty("type", Integer.valueOf(this.bench.getType().ordinal()));
/*  57 */     this.windowData.addProperty("id", this.bench.getId());
/*  58 */     this.windowData.addProperty("name", item.getTranslationKey());
/*  59 */     this.windowData.addProperty("blockItemId", item.getId());
/*     */     
/*  61 */     this.windowData.addProperty("tierLevel", Integer.valueOf(getBenchTierLevel()));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public JsonObject getData() {
/*  67 */     return this.windowData;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean onOpen0(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
/*  72 */     CraftingManager craftingManagerComponent = (CraftingManager)store.getComponent(ref, CraftingManager.getComponentType());
/*  73 */     if (craftingManagerComponent == null) {
/*  74 */       return false;
/*     */     }
/*     */     
/*  77 */     craftingManagerComponent.setBench(this.x, this.y, this.z, this.blockType);
/*     */     
/*  79 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/*  80 */     int memoriesLevel = MemoriesPlugin.get().getMemoriesLevel(world.getGameplayConfig());
/*  81 */     this.windowData.addProperty("worldMemoriesLevel", Integer.valueOf(memoriesLevel));
/*     */ 
/*     */     
/*  84 */     int chestCount = CraftingManager.feedExtraResourcesSection(this.benchState, this.extraResourcesSection);
/*  85 */     CraftingConfig craftingConfig = world.getGameplayConfig().getCraftingConfig();
/*  86 */     int maxChestCount = craftingConfig.getBenchMaterialChestLimit();
/*  87 */     int horizontalRadius = craftingConfig.getBenchMaterialHorizontalChestSearchRadius();
/*  88 */     int verticalRadius = craftingConfig.getBenchMaterialVerticalChestSearchRadius();
/*     */     
/*  90 */     this.windowData.addProperty("nearbyChestCount", Integer.valueOf(chestCount));
/*  91 */     this.windowData.addProperty("maxChestCount", Integer.valueOf(maxChestCount));
/*  92 */     this.windowData.addProperty("chestHorizontalRadius", Integer.valueOf(horizontalRadius));
/*  93 */     this.windowData.addProperty("chestVerticalRadius", Integer.valueOf(verticalRadius));
/*     */     
/*  95 */     return true;
/*     */   }
/*     */   
/*     */   protected int getBenchTierLevel() {
/*  99 */     return (this.benchState != null) ? this.benchState.getTierLevel() : 1;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void onClose0(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
/* 105 */     CraftingManager craftingManagerComponent = (CraftingManager)componentAccessor.getComponent(ref, CraftingManager.getComponentType());
/* 106 */     if (craftingManagerComponent == null) {
/*     */       return;
/*     */     }
/*     */     
/* 110 */     if (craftingManagerComponent.clearBench(ref, componentAccessor) && this.bench.getFailedSoundEventIndex() != 0) {
/* 111 */       SoundUtil.playSoundEvent2d(ref, this.bench.getFailedSoundEventIndex(), SoundCategory.UI, componentAccessor);
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateCraftingJob(float percent) {
/* 116 */     this.windowData.addProperty("progress", Float.valueOf(percent));
/* 117 */     checkProgressInvalidate(percent);
/*     */   }
/*     */   
/*     */   public void updateBenchUpgradeJob(float percent) {
/* 121 */     this.windowData.addProperty("tierUpgradeProgress", Float.valueOf(percent));
/* 122 */     checkProgressInvalidate(percent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkProgressInvalidate(float percent) {
/* 133 */     if (this.lastUpdatePercent != percent) {
/* 134 */       long time = System.currentTimeMillis();
/* 135 */       if (percent >= 1.0F || percent < this.lastUpdatePercent || percent - this.lastUpdatePercent > 0.05F || time - this.lastUpdateTimeMs > 500L || this.lastUpdateTimeMs == 0L) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         this.lastUpdatePercent = percent;
/* 141 */         this.lastUpdateTimeMs = time;
/* 142 */         invalidate();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void updateBenchTierLevel(int newValue) {
/* 148 */     this.windowData.addProperty("tierLevel", Integer.valueOf(newValue));
/* 149 */     updateBenchUpgradeJob(0.0F);
/* 150 */     setNeedRebuild();
/* 151 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public MaterialExtraResourcesSection getExtraResourcesSection() {
/* 157 */     if (!this.extraResourcesSection.isValid()) {
/* 158 */       CraftingManager.feedExtraResourcesSection(this.benchState, this.extraResourcesSection);
/*     */     }
/* 160 */     return this.extraResourcesSection;
/*     */   }
/*     */ 
/*     */   
/*     */   public void invalidateExtraResources() {
/* 165 */     this.extraResourcesSection.setValid(false);
/* 166 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isValid() {
/* 171 */     return this.extraResourcesSection.isValid();
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\window\BenchWindow.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */