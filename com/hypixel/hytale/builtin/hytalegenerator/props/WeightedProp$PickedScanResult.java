/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class PickedScanResult
/*     */   implements ScanResult
/*     */ {
/*     */   ScanResult scanResult;
/*     */   Prop prop;
/*     */   
/*     */   @Nonnull
/*     */   public static PickedScanResult cast(ScanResult scanResult) {
/* 104 */     if (!(scanResult instanceof PickedScanResult)) {
/* 105 */       throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
/*     */     }
/* 107 */     return (PickedScanResult)scanResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNegative() {
/* 112 */     return (this.scanResult == null || this.scanResult.isNegative());
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\WeightedProp$PickedScanResult.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */