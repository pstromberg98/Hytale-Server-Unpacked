/*    */ package com.hypixel.hytale.event;
/*    */ 
/*    */ import javax.annotation.Nullable;
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
/*    */ public interface IEventDispatcher<EventType extends IBaseEvent, ReturnType>
/*    */ {
/*    */   default boolean hasListener() {
/* 17 */     return true;
/*    */   }
/*    */   
/*    */   ReturnType dispatch(@Nullable EventType paramEventType);
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\event\IEventDispatcher.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */