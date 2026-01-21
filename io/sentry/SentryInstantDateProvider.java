/*   */ package io.sentry;
/*   */ 
/*   */ import org.jetbrains.annotations.ApiStatus.Internal;
/*   */ 
/*   */ @Internal
/*   */ public final class SentryInstantDateProvider
/*   */   implements SentryDateProvider {
/*   */   public SentryDate now() {
/* 9 */     return new SentryInstantDate();
/*   */   }
/*   */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\SentryInstantDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */