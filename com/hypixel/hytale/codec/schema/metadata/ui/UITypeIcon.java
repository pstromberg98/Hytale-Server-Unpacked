/*    */ package com.hypixel.hytale.codec.schema.metadata.ui;
/*    */ 
/*    */ import com.hypixel.hytale.codec.schema.config.Schema;
/*    */ import com.hypixel.hytale.codec.schema.metadata.Metadata;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UITypeIcon
/*    */   implements Metadata
/*    */ {
/*    */   private final String icon;
/*    */   
/*    */   public UITypeIcon(String icon) {
/* 15 */     this.icon = icon;
/*    */   }
/*    */ 
/*    */   
/*    */   public void modify(@Nonnull Schema schema) {
/* 20 */     schema.getHytale().setUiTypeIcon(this.icon);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\codec\schema\metadat\\ui\UITypeIcon.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */