/*    */ package com.hypixel.hytale.procedurallib.condition;
/*    */ 
/*    */ import java.util.function.IntToDoubleFunction;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface IDoubleCondition
/*    */ {
/*    */   boolean eval(double paramDouble);
/*    */   
/*    */   default boolean eval(int seed, @Nonnull IntToDoubleFunction seedFunction) {
/* 15 */     return eval(seedFunction.applyAsDouble(seed));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\procedurallib\condition\IDoubleCondition.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */