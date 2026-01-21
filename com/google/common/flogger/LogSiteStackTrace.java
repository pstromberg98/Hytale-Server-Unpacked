/*    */ package com.google.common.flogger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class LogSiteStackTrace
/*    */   extends Exception
/*    */ {
/*    */   LogSiteStackTrace(@NullableDecl Throwable cause, StackSize stackSize, StackTraceElement[] syntheticStackTrace) {
/* 40 */     super(stackSize.toString(), cause);
/*    */ 
/*    */ 
/*    */     
/* 44 */     setStackTrace(syntheticStackTrace);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Throwable fillInStackTrace() {
/* 52 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\common\flogger\LogSiteStackTrace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */