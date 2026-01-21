/*    */ package io.sentry;
/*    */ 
/*    */ public final class NoOpScopesLifecycleToken
/*    */   implements ISentryLifecycleToken {
/*  5 */   private static final NoOpScopesLifecycleToken instance = new NoOpScopesLifecycleToken();
/*    */ 
/*    */ 
/*    */   
/*    */   public static NoOpScopesLifecycleToken getInstance() {
/* 10 */     return instance;
/*    */   }
/*    */   
/*    */   public void close() {}
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\NoOpScopesLifecycleToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */