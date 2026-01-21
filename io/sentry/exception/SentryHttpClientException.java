/*    */ package io.sentry.exception;
/*    */ 
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SentryHttpClientException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 348162238030337390L;
/*    */   
/*    */   public SentryHttpClientException(@Nullable String message) {
/* 13 */     super(message);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\exception\SentryHttpClientException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */