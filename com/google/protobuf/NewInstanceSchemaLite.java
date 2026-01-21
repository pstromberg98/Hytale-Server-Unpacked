/*    */ package com.google.protobuf;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @CheckReturnValue
/*    */ final class NewInstanceSchemaLite
/*    */   implements NewInstanceSchema
/*    */ {
/*    */   public Object newInstance(Object defaultInstance) {
/* 16 */     return ((GeneratedMessageLite)defaultInstance).newMutableInstance();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\protobuf\NewInstanceSchemaLite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */