/*    */ package com.hypixel.hytale.assetstore;
/*    */ 
/*    */ import com.hypixel.hytale.codec.ExtraInfo;
/*    */ import com.hypixel.hytale.codec.validation.ValidationResults;
/*    */ import com.hypixel.hytale.logger.HytaleLogger;
/*    */ import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
/*    */ import com.hypixel.hytale.logger.util.GithubMessageUtil;
/*    */ import java.nio.file.Path;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public class AssetValidationResults
/*    */   extends ValidationResults {
/*    */   private Set<Class<? extends JsonAsset>> disabledMissingAssetClasses;
/*    */   
/*    */   public AssetValidationResults(ExtraInfo extraInfo) {
/* 19 */     super(extraInfo);
/*    */   }
/*    */   
/*    */   public void handleMissingAsset(String field, @Nonnull Class<? extends JsonAsset> assetType, Object assetId) {
/* 23 */     if (this.disabledMissingAssetClasses != null && this.disabledMissingAssetClasses.contains(assetType))
/* 24 */       return;  throw new MissingAssetException(field, assetType, assetId);
/*    */   }
/*    */   
/*    */   public void handleMissingAsset(String field, @Nonnull Class<? extends JsonAsset> assetType, Object assetId, String extra) {
/* 28 */     if (this.disabledMissingAssetClasses != null && this.disabledMissingAssetClasses.contains(assetType))
/* 29 */       return;  throw new MissingAssetException(field, assetType, assetId, extra);
/*    */   }
/*    */   
/*    */   public void disableMissingAssetFor(Class<? extends JsonAsset> assetType) {
/* 33 */     if (this.disabledMissingAssetClasses == null) this.disabledMissingAssetClasses = new HashSet<>(); 
/* 34 */     this.disabledMissingAssetClasses.add(assetType);
/*    */   }
/*    */ 
/*    */   
/*    */   public void logOrThrowValidatorExceptions(@Nonnull HytaleLogger logger, @Nonnull String msg) {
/* 39 */     logOrThrowValidatorExceptions(logger, msg, (Path)null, 0);
/*    */   }
/*    */   
/*    */   public void logOrThrowValidatorExceptions(@Nonnull HytaleLogger logger, @Nonnull String msg, @Nullable Path path, int lineOffset) {
/* 43 */     if (GithubMessageUtil.isGithub() && this.validatorExceptions != null && !this.validatorExceptions.isEmpty()) {
/* 44 */       for (ValidationResults.ValidatorResultsHolder holder : this.validatorExceptions) {
/* 45 */         String file = "unknown";
/* 46 */         if (path == null) { ExtraInfo extraInfo = this.extraInfo; if (extraInfo instanceof AssetExtraInfo) { AssetExtraInfo<?> assetExtraInfo = (AssetExtraInfo)extraInfo;
/* 47 */             path = assetExtraInfo.getAssetPath(); }
/*    */            }
/* 49 */          if (path != null) file = path.toString();
/*    */         
/* 51 */         for (ValidationResults.ValidationResult result : holder.results()) {
/* 52 */           switch (result.result()) { default: throw new MatchException(null, null);
/*    */             case SUCCESS: 
/*    */             case WARNING:
/* 55 */               if (holder.line() == -1);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */             
/*    */             case FAIL:
/* 62 */               if (holder.line() == -1);
/*    */               break; }
/*    */           
/* 65 */           HytaleLoggerBackend.rawLog(GithubMessageUtil.messageError(file, holder.line() + lineOffset, holder.column(), result.reason()));
/*    */         } 
/*    */       } 
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 72 */     super.logOrThrowValidatorExceptions(logger, msg);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\assetstore\AssetValidationResults.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */