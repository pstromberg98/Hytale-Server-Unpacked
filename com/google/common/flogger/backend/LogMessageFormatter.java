/*    */ package com.google.common.flogger.backend;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class LogMessageFormatter
/*    */ {
/*    */   public String format(LogData logData, MetadataProcessor metadata) {
/* 44 */     return append(logData, metadata, new StringBuilder()).toString();
/*    */   }
/*    */   
/*    */   public abstract StringBuilder append(LogData paramLogData, MetadataProcessor paramMetadataProcessor, StringBuilder paramStringBuilder);
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\common\flogger\backend\LogMessageFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */