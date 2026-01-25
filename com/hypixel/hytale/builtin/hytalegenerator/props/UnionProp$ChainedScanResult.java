/*     */ package com.hypixel.hytale.builtin.hytalegenerator.props;
/*     */ 
/*     */ import java.util.List;
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
/*     */ class ChainedScanResult
/*     */   implements ScanResult
/*     */ {
/*     */   List<ScanResult> scanResults;
/*     */   
/*     */   @Nonnull
/*     */   public static ChainedScanResult cast(ScanResult scanResult) {
/*  92 */     if (!(scanResult instanceof ChainedScanResult)) {
/*  93 */       throw new IllegalArgumentException("The provided ScanResult isn't compatible with this prop.");
/*     */     }
/*  95 */     return (ChainedScanResult)scanResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNegative() {
/* 100 */     return (this.scanResults == null || this.scanResults.isEmpty());
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\hytalegenerator\props\UnionProp$ChainedScanResult.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */