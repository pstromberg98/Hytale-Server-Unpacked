/*     */ package com.hypixel.hytale.server.core.modules.entity.player;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.protocol.PickupLocation;
/*     */ import com.hypixel.hytale.server.core.modules.entity.EntityModule;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PlayerSettings
/*     */   extends Record
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   private final boolean showEntityMarkers;
/*     */   private final PickupLocation armorItemsPreferredPickupLocation;
/*     */   private final PickupLocation weaponAndToolItemsPreferredPickupLocation;
/*     */   private final PickupLocation usableItemsItemsPreferredPickupLocation;
/*     */   private final PickupLocation solidBlockItemsPreferredPickupLocation;
/*     */   private final PickupLocation miscItemsPreferredPickupLocation;
/*     */   private final PlayerCreativeSettings creativeSettings;
/*     */   
/*     */   public boolean showEntityMarkers() {
/*  26 */     return this.showEntityMarkers; } private final boolean hideHelmet; private final boolean hideCuirass; private final boolean hideGauntlets; private final boolean hidePants; public final String toString() { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings;)Ljava/lang/String;
/*     */     //   6: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #26	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings; } public final int hashCode() { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings;)I
/*     */     //   6: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #26	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	7	0	this	Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings; } public final boolean equals(Object o) { // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: aload_1
/*     */     //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings;Ljava/lang/Object;)Z
/*     */     //   7: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #26	-> 0
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   0	8	0	this	Lcom/hypixel/hytale/server/core/modules/entity/player/PlayerSettings;
/*  26 */     //   0	8	1	o	Ljava/lang/Object; } public PickupLocation armorItemsPreferredPickupLocation() { return this.armorItemsPreferredPickupLocation; } public PickupLocation weaponAndToolItemsPreferredPickupLocation() { return this.weaponAndToolItemsPreferredPickupLocation; } public PickupLocation usableItemsItemsPreferredPickupLocation() { return this.usableItemsItemsPreferredPickupLocation; } public PickupLocation solidBlockItemsPreferredPickupLocation() { return this.solidBlockItemsPreferredPickupLocation; } public PickupLocation miscItemsPreferredPickupLocation() { return this.miscItemsPreferredPickupLocation; } public PlayerCreativeSettings creativeSettings() { return this.creativeSettings; } public boolean hideHelmet() { return this.hideHelmet; } public boolean hideCuirass() { return this.hideCuirass; } public boolean hideGauntlets() { return this.hideGauntlets; } public boolean hidePants() { return this.hidePants; }
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
/*  42 */   private static final PlayerSettings INSTANCE = new PlayerSettings(false, PickupLocation.Hotbar, PickupLocation.Hotbar, PickupLocation.Hotbar, PickupLocation.Hotbar, PickupLocation.Hotbar, new PlayerCreativeSettings(), false, false, false, false);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static ComponentType<EntityStore, PlayerSettings> getComponentType() {
/*  49 */     return EntityModule.get().getPlayerSettingsComponentType();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PlayerSettings(boolean showEntityMarkers, @Nonnull PickupLocation armorItemsPreferredPickupLocation, @Nonnull PickupLocation weaponAndToolItemsPreferredPickupLocation, @Nonnull PickupLocation usableItemsItemsPreferredPickupLocation, @Nonnull PickupLocation solidBlockItemsPreferredPickupLocation, @Nonnull PickupLocation miscItemsPreferredPickupLocation, PlayerCreativeSettings creativeSettings, boolean hideHelmet, boolean hideCuirass, boolean hideGauntlets, boolean hidePants) {
/*  78 */     this.showEntityMarkers = showEntityMarkers;
/*  79 */     this.armorItemsPreferredPickupLocation = armorItemsPreferredPickupLocation;
/*  80 */     this.weaponAndToolItemsPreferredPickupLocation = weaponAndToolItemsPreferredPickupLocation;
/*  81 */     this.usableItemsItemsPreferredPickupLocation = usableItemsItemsPreferredPickupLocation;
/*  82 */     this.solidBlockItemsPreferredPickupLocation = solidBlockItemsPreferredPickupLocation;
/*  83 */     this.miscItemsPreferredPickupLocation = miscItemsPreferredPickupLocation;
/*  84 */     this.creativeSettings = creativeSettings;
/*  85 */     this.hideHelmet = hideHelmet;
/*  86 */     this.hideCuirass = hideCuirass;
/*  87 */     this.hideGauntlets = hideGauntlets;
/*  88 */     this.hidePants = hidePants;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static PlayerSettings defaults() {
/*  96 */     return INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Component<EntityStore> clone() {
/* 102 */     return new PlayerSettings(this.showEntityMarkers, this.armorItemsPreferredPickupLocation, this.weaponAndToolItemsPreferredPickupLocation, this.usableItemsItemsPreferredPickupLocation, this.solidBlockItemsPreferredPickupLocation, this.miscItemsPreferredPickupLocation, this.creativeSettings.clone(), this.hideHelmet, this.hideCuirass, this.hideGauntlets, this.hidePants);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\modules\entity\player\PlayerSettings.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */