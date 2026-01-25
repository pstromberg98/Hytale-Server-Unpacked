/*     */ package com.hypixel.hytale.builtin.crafting.state;
/*     */ 
/*     */ import com.hypixel.hytale.builtin.crafting.window.BenchWindow;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.component.AddReason;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.math.vector.Vector3d;
/*     */ import com.hypixel.hytale.math.vector.Vector3f;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
/*     */ import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.BenchUpgradeRequirement;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.windows.WindowManager;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.meta.state.DestroyableBlockState;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BenchState
/*     */   extends BlockState
/*     */   implements DestroyableBlockState
/*     */ {
/*     */   public static BuilderCodec<BenchState> CODEC;
/*     */   
/*     */   static {
/*  47 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BenchState.class, BenchState::new, BlockState.BASE_CODEC).appendInherited(new KeyedCodec("TierLevel", (Codec)Codec.INTEGER), (state, o) -> state.tierLevel = o.intValue(), state -> Integer.valueOf(state.tierLevel), (state, parent) -> state.tierLevel = parent.tierLevel).add()).appendInherited(new KeyedCodec("UpgradeItems", (Codec)new ArrayCodec((Codec)ItemStack.CODEC, x$0 -> new ItemStack[x$0])), (state, o) -> state.upgradeItems = o, state -> state.upgradeItems, (state, parent) -> state.upgradeItems = parent.upgradeItems).add()).build();
/*     */   }
/*  49 */   private int tierLevel = 1;
/*     */   
/*     */   public int getTierLevel() {
/*  52 */     return this.tierLevel;
/*     */   }
/*     */   
/*  55 */   protected ItemStack[] upgradeItems = ItemStack.EMPTY_ARRAY;
/*     */   
/*     */   protected Bench bench;
/*     */   
/*  59 */   protected final Map<UUID, BenchWindow> windows = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   public boolean initialize(@Nonnull BlockType blockType) {
/*  63 */     if (!super.initialize(blockType)) return false;
/*     */     
/*  65 */     this.bench = blockType.getBench();
/*     */     
/*  67 */     if (this.bench == null) {
/*  68 */       if (this.upgradeItems.length > 0) {
/*  69 */         dropUpgradeItems();
/*     */       }
/*  71 */       return false;
/*     */     } 
/*     */     
/*  74 */     return true;
/*     */   }
/*     */   
/*     */   public void addUpgradeItems(List<ItemStack> consumed) {
/*  78 */     consumed.addAll(Arrays.asList(this.upgradeItems));
/*  79 */     this.upgradeItems = (ItemStack[])consumed.toArray(x$0 -> new ItemStack[x$0]);
/*  80 */     markNeedsSave();
/*     */   }
/*     */   
/*     */   private void dropUpgradeItems() {
/*  84 */     if (this.upgradeItems.length == 0)
/*     */       return; 
/*  86 */     World world = getChunk().getWorld();
/*  87 */     Store<EntityStore> entityStore = world.getEntityStore().getStore();
/*  88 */     Vector3d dropPosition = getBlockPosition().toVector3d().add(0.5D, 0.0D, 0.5D);
/*  89 */     Holder[] arrayOfHolder = ItemComponent.generateItemDrops((ComponentAccessor)entityStore, List.of(this.upgradeItems), dropPosition, Vector3f.ZERO);
/*  90 */     if (arrayOfHolder.length > 0) {
/*  91 */       world.execute(() -> entityStore.addEntities(itemEntityHolders, AddReason.SPAWN));
/*     */     }
/*     */     
/*  94 */     this.upgradeItems = ItemStack.EMPTY_ARRAY;
/*     */   }
/*     */   
/*     */   public Bench getBench() {
/*  98 */     return this.bench;
/*     */   }
/*     */   
/*     */   public void setTierLevel(int newTierLevel) {
/* 102 */     if (this.tierLevel != newTierLevel) {
/* 103 */       this.tierLevel = newTierLevel;
/* 104 */       onTierLevelChange();
/* 105 */       markNeedsSave();
/*     */     } 
/*     */   }
/*     */   
/*     */   public BenchUpgradeRequirement getNextLevelUpgradeMaterials() {
/* 110 */     return this.bench.getUpgradeRequirement(this.tierLevel);
/*     */   }
/*     */   
/*     */   protected void onTierLevelChange() {
/* 114 */     getChunk().setBlockInteractionState(getBlockPosition(), getBaseBlockType(), getTierStateName());
/*     */   }
/*     */   
/*     */   public BlockType getBaseBlockType() {
/* 118 */     BlockType currentBlockType = getBlockType();
/*     */     
/* 120 */     String baseBlockKey = currentBlockType.getDefaultStateKey();
/* 121 */     BlockType baseBlockType = (BlockType)BlockType.getAssetMap().getAsset(baseBlockKey);
/* 122 */     if (baseBlockType == null) baseBlockType = currentBlockType;
/*     */     
/* 124 */     return baseBlockType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTierStateName() {
/* 132 */     return (this.tierLevel > 1) ? ("Tier" + this.tierLevel) : "default";
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDestroy() {
/* 137 */     WindowManager.closeAndRemoveAll(this.windows);
/* 138 */     dropUpgradeItems();
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public Map<UUID, BenchWindow> getWindows() {
/* 143 */     return this.windows;
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\crafting\state\BenchState.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */