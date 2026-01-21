/*    */ package io.sentry.internal.debugmeta;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Properties;
/*    */ import org.jetbrains.annotations.ApiStatus.Internal;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ @Internal
/*    */ public final class NoOpDebugMetaLoader
/*    */   implements IDebugMetaLoader {
/* 11 */   private static final NoOpDebugMetaLoader instance = new NoOpDebugMetaLoader();
/*    */   
/*    */   public static NoOpDebugMetaLoader getInstance() {
/* 14 */     return instance;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public List<Properties> loadDebugMeta() {
/* 21 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\internal\debugmeta\NoOpDebugMetaLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */