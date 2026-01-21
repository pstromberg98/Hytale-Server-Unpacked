/*    */ package com.google.common.flogger.backend;
/*    */ 
/*    */ import org.checkerframework.checker.nullness.compatqual.NullableDecl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoggingException
/*    */   extends RuntimeException
/*    */ {
/*    */   public LoggingException(@NullableDecl String message) {
/* 34 */     super(message);
/*    */   }
/*    */   
/*    */   public LoggingException(@NullableDecl String message, @NullableDecl Throwable cause) {
/* 38 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\common\flogger\backend\LoggingException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */