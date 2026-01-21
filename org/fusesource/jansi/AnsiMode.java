/*    */ package org.fusesource.jansi;
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
/*    */ public enum AnsiMode
/*    */ {
/* 24 */   Strip("Strip all ansi sequences"),
/* 25 */   Default("Print ansi sequences if the stream is a terminal"),
/* 26 */   Force("Always print ansi sequences, even if the stream is redirected");
/*    */   
/*    */   private final String description;
/*    */   
/*    */   AnsiMode(String description) {
/* 31 */     this.description = description;
/*    */   }
/*    */   
/*    */   String getDescription() {
/* 35 */     return this.description;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\fusesource\jansi\AnsiMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */