/*    */ package com.hypixel.hytale.component;
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
/*    */ public interface Resource<ECS_TYPE>
/*    */   extends Cloneable
/*    */ {
/* 16 */   public static final Resource[] EMPTY_ARRAY = new Resource[0];
/*    */   
/*    */   @Nullable
/*    */   Resource<ECS_TYPE> clone();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\Resource.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */