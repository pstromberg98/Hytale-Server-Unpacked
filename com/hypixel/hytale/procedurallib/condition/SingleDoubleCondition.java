/*    */ package com.hypixel.hytale.procedurallib.condition;
/*    */ 
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SingleDoubleCondition
/*    */   implements IDoubleCondition
/*    */ {
/*    */   protected final double value;
/*    */   
/*    */   public SingleDoubleCondition(double value) {
/* 13 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean eval(double value) {
/* 18 */     return (value < this.value);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 24 */     return "SingleDoubleCondition{value=" + this.value + "}";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\procedurallib\condition\SingleDoubleCondition.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */