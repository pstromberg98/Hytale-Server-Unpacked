/*    */ package com.hypixel.hytale.server.core.asset.type.buildertool.config.args;
/*    */ 
/*    */ import com.hypixel.hytale.server.core.Message;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public class ToolArgException
/*    */   extends Exception {
/*    */   @Nonnull
/*    */   private final Message translationMessage;
/*    */   
/*    */   public ToolArgException(@Nonnull Message translationMessage) {
/* 12 */     super(translationMessage.toString());
/* 13 */     this.translationMessage = translationMessage;
/*    */   }
/*    */   
/*    */   public ToolArgException(@Nonnull Message translationMessage, Throwable cause) {
/* 17 */     super(translationMessage.toString(), cause);
/* 18 */     this.translationMessage = translationMessage;
/*    */   }
/*    */   
/*    */   @Nonnull
/*    */   public Message getTranslationMessage() {
/* 23 */     return this.translationMessage;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\asset\type\buildertool\config\args\ToolArgException.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */