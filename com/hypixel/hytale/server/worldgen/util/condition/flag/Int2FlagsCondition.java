/*    */ package com.hypixel.hytale.server.worldgen.util.condition.flag;
/*    */ 
/*    */ import java.util.function.IntUnaryOperator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface Int2FlagsCondition
/*    */   extends IntUnaryOperator
/*    */ {
/*    */   int eval(int paramInt);
/*    */   
/*    */   default int applyAsInt(int operand) {
/* 15 */     return eval(operand);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\worldge\\util\condition\flag\Int2FlagsCondition.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */