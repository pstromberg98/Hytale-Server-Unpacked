/*    */ package com.hypixel.hytale.component;
/*    */ 
/*    */ import javax.annotation.Nonnull;
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
/*    */ public class NonTicking<ECS_TYPE>
/*    */   implements Component<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/* 22 */   private static final NonTicking<?> INSTANCE = new NonTicking();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <ECS_TYPE> NonTicking<ECS_TYPE> get() {
/* 32 */     return (NonTicking)INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Component<ECS_TYPE> clone() {
/* 38 */     return get();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\NonTicking.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */