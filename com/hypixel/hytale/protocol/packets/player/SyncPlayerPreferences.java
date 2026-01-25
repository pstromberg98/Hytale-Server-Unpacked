/*     */ package com.hypixel.hytale.protocol.packets.player;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.Packet;
/*     */ import com.hypixel.hytale.protocol.PickupLocation;
/*     */ import com.hypixel.hytale.protocol.io.ValidationResult;
/*     */ import io.netty.buffer.ByteBuf;
/*     */ import java.util.Objects;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SyncPlayerPreferences
/*     */   implements Packet
/*     */ {
/*     */   public static final int PACKET_ID = 116;
/*     */   public static final boolean IS_COMPRESSED = false;
/*     */   public static final int NULLABLE_BIT_FIELD_SIZE = 0;
/*     */   public static final int FIXED_BLOCK_SIZE = 12;
/*     */   public static final int VARIABLE_FIELD_COUNT = 0;
/*     */   public static final int VARIABLE_BLOCK_START = 12;
/*     */   public static final int MAX_SIZE = 12;
/*     */   public boolean showEntityMarkers;
/*     */   
/*     */   public int getId() {
/*  25 */     return 116;
/*     */   }
/*     */   
/*     */   @Nonnull
/*  29 */   public PickupLocation armorItemsPreferredPickupLocation = PickupLocation.Hotbar; @Nonnull
/*  30 */   public PickupLocation weaponAndToolItemsPreferredPickupLocation = PickupLocation.Hotbar; @Nonnull
/*  31 */   public PickupLocation usableItemsItemsPreferredPickupLocation = PickupLocation.Hotbar; @Nonnull
/*  32 */   public PickupLocation solidBlockItemsPreferredPickupLocation = PickupLocation.Hotbar; @Nonnull
/*  33 */   public PickupLocation miscItemsPreferredPickupLocation = PickupLocation.Hotbar;
/*     */   
/*     */   public boolean allowNPCDetection;
/*     */   
/*     */   public boolean respondToHit;
/*     */   
/*     */   public boolean hideHelmet;
/*     */   public boolean hideCuirass;
/*     */   public boolean hideGauntlets;
/*     */   public boolean hidePants;
/*     */   
/*     */   public SyncPlayerPreferences(boolean showEntityMarkers, @Nonnull PickupLocation armorItemsPreferredPickupLocation, @Nonnull PickupLocation weaponAndToolItemsPreferredPickupLocation, @Nonnull PickupLocation usableItemsItemsPreferredPickupLocation, @Nonnull PickupLocation solidBlockItemsPreferredPickupLocation, @Nonnull PickupLocation miscItemsPreferredPickupLocation, boolean allowNPCDetection, boolean respondToHit, boolean hideHelmet, boolean hideCuirass, boolean hideGauntlets, boolean hidePants) {
/*  45 */     this.showEntityMarkers = showEntityMarkers;
/*  46 */     this.armorItemsPreferredPickupLocation = armorItemsPreferredPickupLocation;
/*  47 */     this.weaponAndToolItemsPreferredPickupLocation = weaponAndToolItemsPreferredPickupLocation;
/*  48 */     this.usableItemsItemsPreferredPickupLocation = usableItemsItemsPreferredPickupLocation;
/*  49 */     this.solidBlockItemsPreferredPickupLocation = solidBlockItemsPreferredPickupLocation;
/*  50 */     this.miscItemsPreferredPickupLocation = miscItemsPreferredPickupLocation;
/*  51 */     this.allowNPCDetection = allowNPCDetection;
/*  52 */     this.respondToHit = respondToHit;
/*  53 */     this.hideHelmet = hideHelmet;
/*  54 */     this.hideCuirass = hideCuirass;
/*  55 */     this.hideGauntlets = hideGauntlets;
/*  56 */     this.hidePants = hidePants;
/*     */   }
/*     */   
/*     */   public SyncPlayerPreferences(@Nonnull SyncPlayerPreferences other) {
/*  60 */     this.showEntityMarkers = other.showEntityMarkers;
/*  61 */     this.armorItemsPreferredPickupLocation = other.armorItemsPreferredPickupLocation;
/*  62 */     this.weaponAndToolItemsPreferredPickupLocation = other.weaponAndToolItemsPreferredPickupLocation;
/*  63 */     this.usableItemsItemsPreferredPickupLocation = other.usableItemsItemsPreferredPickupLocation;
/*  64 */     this.solidBlockItemsPreferredPickupLocation = other.solidBlockItemsPreferredPickupLocation;
/*  65 */     this.miscItemsPreferredPickupLocation = other.miscItemsPreferredPickupLocation;
/*  66 */     this.allowNPCDetection = other.allowNPCDetection;
/*  67 */     this.respondToHit = other.respondToHit;
/*  68 */     this.hideHelmet = other.hideHelmet;
/*  69 */     this.hideCuirass = other.hideCuirass;
/*  70 */     this.hideGauntlets = other.hideGauntlets;
/*  71 */     this.hidePants = other.hidePants;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static SyncPlayerPreferences deserialize(@Nonnull ByteBuf buf, int offset) {
/*  76 */     SyncPlayerPreferences obj = new SyncPlayerPreferences();
/*     */     
/*  78 */     obj.showEntityMarkers = (buf.getByte(offset + 0) != 0);
/*  79 */     obj.armorItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 1));
/*  80 */     obj.weaponAndToolItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 2));
/*  81 */     obj.usableItemsItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 3));
/*  82 */     obj.solidBlockItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 4));
/*  83 */     obj.miscItemsPreferredPickupLocation = PickupLocation.fromValue(buf.getByte(offset + 5));
/*  84 */     obj.allowNPCDetection = (buf.getByte(offset + 6) != 0);
/*  85 */     obj.respondToHit = (buf.getByte(offset + 7) != 0);
/*  86 */     obj.hideHelmet = (buf.getByte(offset + 8) != 0);
/*  87 */     obj.hideCuirass = (buf.getByte(offset + 9) != 0);
/*  88 */     obj.hideGauntlets = (buf.getByte(offset + 10) != 0);
/*  89 */     obj.hidePants = (buf.getByte(offset + 11) != 0);
/*     */ 
/*     */     
/*  92 */     return obj;
/*     */   }
/*     */   
/*     */   public static int computeBytesConsumed(@Nonnull ByteBuf buf, int offset) {
/*  96 */     return 12;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void serialize(@Nonnull ByteBuf buf) {
/* 102 */     buf.writeByte(this.showEntityMarkers ? 1 : 0);
/* 103 */     buf.writeByte(this.armorItemsPreferredPickupLocation.getValue());
/* 104 */     buf.writeByte(this.weaponAndToolItemsPreferredPickupLocation.getValue());
/* 105 */     buf.writeByte(this.usableItemsItemsPreferredPickupLocation.getValue());
/* 106 */     buf.writeByte(this.solidBlockItemsPreferredPickupLocation.getValue());
/* 107 */     buf.writeByte(this.miscItemsPreferredPickupLocation.getValue());
/* 108 */     buf.writeByte(this.allowNPCDetection ? 1 : 0);
/* 109 */     buf.writeByte(this.respondToHit ? 1 : 0);
/* 110 */     buf.writeByte(this.hideHelmet ? 1 : 0);
/* 111 */     buf.writeByte(this.hideCuirass ? 1 : 0);
/* 112 */     buf.writeByte(this.hideGauntlets ? 1 : 0);
/* 113 */     buf.writeByte(this.hidePants ? 1 : 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int computeSize() {
/* 119 */     return 12;
/*     */   }
/*     */   
/*     */   public static ValidationResult validateStructure(@Nonnull ByteBuf buffer, int offset) {
/* 123 */     if (buffer.readableBytes() - offset < 12) {
/* 124 */       return ValidationResult.error("Buffer too small: expected at least 12 bytes");
/*     */     }
/*     */ 
/*     */     
/* 128 */     return ValidationResult.OK;
/*     */   }
/*     */   
/*     */   public SyncPlayerPreferences clone() {
/* 132 */     SyncPlayerPreferences copy = new SyncPlayerPreferences();
/* 133 */     copy.showEntityMarkers = this.showEntityMarkers;
/* 134 */     copy.armorItemsPreferredPickupLocation = this.armorItemsPreferredPickupLocation;
/* 135 */     copy.weaponAndToolItemsPreferredPickupLocation = this.weaponAndToolItemsPreferredPickupLocation;
/* 136 */     copy.usableItemsItemsPreferredPickupLocation = this.usableItemsItemsPreferredPickupLocation;
/* 137 */     copy.solidBlockItemsPreferredPickupLocation = this.solidBlockItemsPreferredPickupLocation;
/* 138 */     copy.miscItemsPreferredPickupLocation = this.miscItemsPreferredPickupLocation;
/* 139 */     copy.allowNPCDetection = this.allowNPCDetection;
/* 140 */     copy.respondToHit = this.respondToHit;
/* 141 */     copy.hideHelmet = this.hideHelmet;
/* 142 */     copy.hideCuirass = this.hideCuirass;
/* 143 */     copy.hideGauntlets = this.hideGauntlets;
/* 144 */     copy.hidePants = this.hidePants;
/* 145 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*     */     SyncPlayerPreferences other;
/* 151 */     if (this == obj) return true; 
/* 152 */     if (obj instanceof SyncPlayerPreferences) { other = (SyncPlayerPreferences)obj; } else { return false; }
/* 153 */      return (this.showEntityMarkers == other.showEntityMarkers && Objects.equals(this.armorItemsPreferredPickupLocation, other.armorItemsPreferredPickupLocation) && Objects.equals(this.weaponAndToolItemsPreferredPickupLocation, other.weaponAndToolItemsPreferredPickupLocation) && Objects.equals(this.usableItemsItemsPreferredPickupLocation, other.usableItemsItemsPreferredPickupLocation) && Objects.equals(this.solidBlockItemsPreferredPickupLocation, other.solidBlockItemsPreferredPickupLocation) && Objects.equals(this.miscItemsPreferredPickupLocation, other.miscItemsPreferredPickupLocation) && this.allowNPCDetection == other.allowNPCDetection && this.respondToHit == other.respondToHit && this.hideHelmet == other.hideHelmet && this.hideCuirass == other.hideCuirass && this.hideGauntlets == other.hideGauntlets && this.hidePants == other.hidePants);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 158 */     return Objects.hash(new Object[] { Boolean.valueOf(this.showEntityMarkers), this.armorItemsPreferredPickupLocation, this.weaponAndToolItemsPreferredPickupLocation, this.usableItemsItemsPreferredPickupLocation, this.solidBlockItemsPreferredPickupLocation, this.miscItemsPreferredPickupLocation, Boolean.valueOf(this.allowNPCDetection), Boolean.valueOf(this.respondToHit), Boolean.valueOf(this.hideHelmet), Boolean.valueOf(this.hideCuirass), Boolean.valueOf(this.hideGauntlets), Boolean.valueOf(this.hidePants) });
/*     */   }
/*     */   
/*     */   public SyncPlayerPreferences() {}
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\packets\player\SyncPlayerPreferences.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */