/*    */ package com.hypixel.hytale.builtin.asseteditor.event;
/*    */ 
/*    */ import com.hypixel.hytale.builtin.asseteditor.EditorClient;
/*    */ import com.hypixel.hytale.event.IEvent;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ public abstract class EditorClientEvent<KeyType>
/*    */   implements IEvent<KeyType> {
/*    */   private final EditorClient editorClient;
/*    */   
/*    */   public EditorClientEvent(EditorClient editorClient) {
/* 12 */     this.editorClient = editorClient;
/*    */   }
/*    */   
/*    */   public EditorClient getEditorClient() {
/* 16 */     return this.editorClient;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 22 */     return "EditorClientEvent{editorClient=" + String.valueOf(this.editorClient) + "}";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\asseteditor\event\EditorClientEvent.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */