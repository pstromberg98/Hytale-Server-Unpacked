/*    */ package io.sentry.util;
/*    */ 
/*    */ import io.sentry.ISentryLifecycleToken;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ public final class LifecycleHelper
/*    */ {
/*    */   public static void close(@Nullable Object tokenObject) {
/* 10 */     if (tokenObject != null && tokenObject instanceof ISentryLifecycleToken) {
/* 11 */       ISentryLifecycleToken token = (ISentryLifecycleToken)tokenObject;
/* 12 */       token.close();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentr\\util\LifecycleHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */