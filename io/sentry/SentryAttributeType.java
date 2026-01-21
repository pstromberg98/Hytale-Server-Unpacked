/*    */ package io.sentry;
/*    */ 
/*    */ import java.util.Locale;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public enum SentryAttributeType {
/*  7 */   STRING,
/*  8 */   BOOLEAN,
/*  9 */   INTEGER,
/* 10 */   DOUBLE;
/*    */   @NotNull
/*    */   public String apiName() {
/* 13 */     return name().toLowerCase(Locale.ROOT);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\sentry\SentryAttributeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */