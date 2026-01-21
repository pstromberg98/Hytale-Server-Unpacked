/*    */ package com.hypixel.hytale.builtin.adventure.memories.page;
/*    */ 
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import java.util.function.Supplier;
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
/*    */ public class PageEventData
/*    */ {
/*    */   public static final String KEY_ACTION = "Action";
/*    */   public static final BuilderCodec<PageEventData> CODEC;
/*    */   public MemoriesUnlockedPage.PageAction action;
/*    */   
/*    */   static {
/* 62 */     CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(PageEventData.class, PageEventData::new).append(new KeyedCodec("Action", MemoriesUnlockedPage.PageAction.CODEC), (pageEventData, pageAction) -> pageEventData.action = pageAction, pageEventData -> pageEventData.action).add()).build();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\builtin\adventure\memories\page\MemoriesUnlockedPage$PageEventData.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */