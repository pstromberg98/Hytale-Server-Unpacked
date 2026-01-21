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
/*    */ public enum AnsiType
/*    */ {
/* 24 */   Native("Supports ansi sequences natively"),
/* 25 */   Unsupported("Ansi sequences are stripped out"),
/* 26 */   VirtualTerminal("Supported through windows virtual terminal"),
/* 27 */   Emulation("Emulated through using windows API console commands"),
/* 28 */   Redirected("The stream is redirected to a file or a pipe");
/*    */   
/*    */   private final String description;
/*    */   
/*    */   AnsiType(String description) {
/* 33 */     this.description = description;
/*    */   }
/*    */   
/*    */   String getDescription() {
/* 37 */     return this.description;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\fusesource\jansi\AnsiType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */