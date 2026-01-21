/*    */ package io.sentry;
/*    */ 
/*    */ import org.jetbrains.annotations.ApiStatus.Internal;
/*    */ 
/*    */ @Internal
/*    */ public final class SentryNanotimeDateProvider
/*    */   implements SentryDateProvider
/*    */ {
/*    */   public SentryDate now() {
/* 10 */     return new SentryNanotimeDate();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\SentryNanotimeDateProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */