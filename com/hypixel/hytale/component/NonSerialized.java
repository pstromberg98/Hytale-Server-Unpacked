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
/*    */ public class NonSerialized<ECS_TYPE>
/*    */   implements Component<ECS_TYPE>
/*    */ {
/*    */   @Nonnull
/* 16 */   private static final NonSerialized<?> INSTANCE = new NonSerialized();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <ECS_TYPE> NonSerialized<ECS_TYPE> get() {
/* 26 */     return (NonSerialized)INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Component<ECS_TYPE> clone() {
/* 32 */     return get();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\component\NonSerialized.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */