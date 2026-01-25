/*     */ package com.hypixel.hytale.server.core.modules.entity.damage;
/*     */ 
/*     */ import com.hypixel.hytale.assetstore.AssetRegistry;
/*     */ import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentAccessor;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
/*     */ import com.hypixel.hytale.server.core.asset.type.gameplay.respawn.RespawnController;
/*     */ import com.hypixel.hytale.server.core.entity.InteractionChain;
/*     */ import com.hypixel.hytale.server.core.inventory.ItemStack;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeathComponent
/*     */   implements Component<EntityStore>
/*     */ {
/*  34 */   public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   public static ComponentType<EntityStore, DeathComponent> getComponentType() {
/*  37 */     return DamageModule.get().getDeathComponentType();
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
/*     */   public static final BuilderCodec<DeathComponent> CODEC;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String deathCause;
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
/*     */   private Message deathMessage;
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
/*     */   static {
/*  83 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DeathComponent.class, DeathComponent::new).append(new KeyedCodec("DeathCause", (Codec)Codec.STRING), (o, i) -> o.deathCause = i, o -> o.deathCause).add()).append(new KeyedCodec("DeathMessage", (Codec)Message.CODEC), (deathComponent, message) -> deathComponent.deathMessage = message, deathComponent -> deathComponent.deathMessage).add()).append(new KeyedCodec("ShowDeathMenu", (Codec)BuilderCodec.BOOLEAN), (deathComponent, showDeathMenu) -> deathComponent.showDeathMenu = showDeathMenu.booleanValue(), deathComponent -> Boolean.valueOf(deathComponent.showDeathMenu)).add()).append(new KeyedCodec("ItemsLostOnDeath", (Codec)new ArrayCodec((Codec)ItemStack.CODEC, x$0 -> new ItemStack[x$0])), (deathComponent, itemStacks) -> deathComponent.itemsLostOnDeath = itemStacks, deathComponent -> deathComponent.itemsLostOnDeath).add()).append(new KeyedCodec("ItemsAmountLossPercentage", (Codec)Codec.DOUBLE), (deathComponent, aDouble) -> deathComponent.itemsAmountLossPercentage = aDouble.doubleValue(), deathComponent -> Double.valueOf(deathComponent.itemsAmountLossPercentage)).add()).append(new KeyedCodec("ItemsDurabilityLossPercentage", (Codec)Codec.DOUBLE), (deathComponent, aDouble) -> deathComponent.itemsDurabilityLossPercentage = aDouble.doubleValue(), deathComponent -> Double.valueOf(deathComponent.itemsDurabilityLossPercentage)).add()).append(new KeyedCodec("DisplayDataOnDeathScreen", (Codec)Codec.BOOLEAN), (deathComponent, aBoolean) -> deathComponent.displayDataOnDeathScreen = aBoolean.booleanValue(), deathComponent -> Boolean.valueOf(deathComponent.displayDataOnDeathScreen)).add()).build();
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean showDeathMenu = true;
/*     */   
/*     */   private ItemStack[] itemsLostOnDeath;
/*     */   
/*     */   private double itemsAmountLossPercentage;
/*     */   
/*     */   private double itemsDurabilityLossPercentage;
/*     */   
/*     */   private boolean displayDataOnDeathScreen;
/*     */   @Nullable
/*     */   private transient Damage deathInfo;
/*  98 */   private transient DeathConfig.ItemsLossMode itemsLossMode = DeathConfig.ItemsLossMode.ALL;
/*     */   
/*     */   @Nullable
/*     */   private transient InteractionChain interactionChain;
/* 102 */   private transient CompletableFuture<Void> respawnFuture = null;
/*     */   
/*     */   protected DeathComponent(@Nonnull Damage deathInfo) {
/* 105 */     this.deathInfo = deathInfo;
/* 106 */     this.deathCause = deathInfo.getCause().getId();
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
/*     */   @Nullable
/*     */   public DamageCause getDeathCause() {
/* 123 */     return (DamageCause)((IndexedLookupTableAssetMap)AssetRegistry.getAssetStore(DamageCause.class).getAssetMap()).getAsset(this.deathCause);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Message getDeathMessage() {
/* 128 */     return this.deathMessage;
/*     */   }
/*     */   
/*     */   public void setDeathMessage(@Nullable Message deathMessage) {
/* 132 */     this.deathMessage = deathMessage;
/*     */   }
/*     */   
/*     */   public boolean isShowDeathMenu() {
/* 136 */     return this.showDeathMenu;
/*     */   }
/*     */   
/*     */   public void setShowDeathMenu(boolean showDeathMenu) {
/* 140 */     this.showDeathMenu = showDeathMenu;
/*     */   }
/*     */   
/*     */   public ItemStack[] getItemsLostOnDeath() {
/* 144 */     return this.itemsLostOnDeath;
/*     */   }
/*     */   
/*     */   public void setItemsLostOnDeath(List<ItemStack> itemsLostOnDeath) {
/* 148 */     this.itemsLostOnDeath = (ItemStack[])itemsLostOnDeath.toArray(x$0 -> new ItemStack[x$0]);
/*     */   }
/*     */   
/*     */   public double getItemsAmountLossPercentage() {
/* 152 */     return this.itemsAmountLossPercentage;
/*     */   }
/*     */   
/*     */   public void setItemsAmountLossPercentage(double itemsAmountLossPercentage) {
/* 156 */     this.itemsAmountLossPercentage = itemsAmountLossPercentage;
/*     */   }
/*     */   
/*     */   public double getItemsDurabilityLossPercentage() {
/* 160 */     return this.itemsDurabilityLossPercentage;
/*     */   }
/*     */   
/*     */   public void setItemsDurabilityLossPercentage(double itemsDurabilityLossPercentage) {
/* 164 */     this.itemsDurabilityLossPercentage = itemsDurabilityLossPercentage;
/*     */   }
/*     */   
/*     */   public boolean displayDataOnDeathScreen() {
/* 168 */     return this.displayDataOnDeathScreen;
/*     */   }
/*     */   
/*     */   public void setDisplayDataOnDeathScreen(boolean displayDataOnDeathScreen) {
/* 172 */     this.displayDataOnDeathScreen = displayDataOnDeathScreen;
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
/*     */   public Damage getDeathInfo() {
/* 187 */     return this.deathInfo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeathConfig.ItemsLossMode getItemsLossMode() {
/* 196 */     return this.itemsLossMode;
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
/*     */   public void setItemsLossMode(DeathConfig.ItemsLossMode itemsLossMode) {
/* 210 */     this.itemsLossMode = itemsLossMode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DeathItemLoss getDeathItemLoss() {
/* 220 */     return new DeathItemLoss(this.itemsLossMode, this.itemsLostOnDeath, this.itemsAmountLossPercentage, this.itemsDurabilityLossPercentage);
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
/*     */   public InteractionChain getInteractionChain() {
/* 233 */     return this.interactionChain;
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
/*     */   public void setInteractionChain(@Nullable InteractionChain interactionChain) {
/* 245 */     this.interactionChain = interactionChain;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<EntityStore> clone() {
/* 251 */     DeathComponent death = new DeathComponent();
/* 252 */     death.deathCause = this.deathCause;
/* 253 */     death.deathMessage = this.deathMessage;
/* 254 */     death.showDeathMenu = this.showDeathMenu;
/* 255 */     death.itemsLostOnDeath = this.itemsLostOnDeath;
/* 256 */     death.itemsAmountLossPercentage = this.itemsAmountLossPercentage;
/* 257 */     death.itemsDurabilityLossPercentage = this.itemsDurabilityLossPercentage;
/* 258 */     death.displayDataOnDeathScreen = this.displayDataOnDeathScreen;
/*     */     
/* 260 */     death.deathInfo = this.deathInfo;
/* 261 */     death.itemsLossMode = this.itemsLossMode;
/* 262 */     return death;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void tryAddComponent(@Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull Ref<EntityStore> ref, @Nonnull Damage damage) {
/* 273 */     if (commandBuffer.getArchetype(ref).contains(getComponentType()))
/* 274 */       return;  commandBuffer.run(store -> tryAddComponent(store, ref, damage));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void tryAddComponent(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Damage damage) {
/* 284 */     if (store.getArchetype(ref).contains(getComponentType()))
/* 285 */       return;  store.addComponent(ref, getComponentType(), new DeathComponent(damage));
/*     */   }
/*     */   
/*     */   public static CompletableFuture<Void> respawn(@Nonnull ComponentAccessor<EntityStore> componentAccessor, @Nonnull Ref<EntityStore> ref) {
/* 289 */     DeathComponent deathComponent = (DeathComponent)componentAccessor.getComponent(ref, getComponentType());
/* 290 */     if (deathComponent == null)
/*     */     {
/* 292 */       return CompletableFuture.completedFuture(null);
/*     */     }
/* 294 */     if (deathComponent.respawnFuture != null)
/*     */     {
/* 296 */       return deathComponent.respawnFuture;
/*     */     }
/* 298 */     World world = ((EntityStore)componentAccessor.getExternalData()).getWorld();
/*     */     
/* 300 */     RespawnController respawnController = world.getDeathConfig().getRespawnController();
/* 301 */     deathComponent.respawnFuture = respawnController.respawnPlayer(world, ref, componentAccessor).whenComplete((ignore, ex) -> {
/*     */           if (ex != null) {
/*     */             ((HytaleLogger.Api)((HytaleLogger.Api)LOGGER.atSevere()).withCause(ex)).log("Failed to respawn entity");
/*     */           }
/*     */           Store<EntityStore> store = world.getEntityStore().getStore();
/*     */           if (!ref.isValid())
/*     */             return; 
/*     */           store.tryRemoveComponent(ref, getComponentType());
/*     */         });
/* 310 */     return deathComponent.respawnFuture;
/*     */   }
/*     */   
/*     */   protected DeathComponent() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\damage\DeathComponent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */