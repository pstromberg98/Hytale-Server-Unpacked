/*    */ package com.hypixel.hytale.sneakythrow.function;
/*    */ 
/*    */ import com.hypixel.hytale.sneakythrow.SneakyThrow;
/*    */ import java.util.function.BiFunction;
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface ThrowableBiFunction<T, U, R, E extends Throwable>
/*    */   extends BiFunction<T, U, R>
/*    */ {
/*    */   default R apply(T t, U u) {
/*    */     try {
/* 13 */       return applyNow(t, u);
/* 14 */     } catch (Throwable e) {
/* 15 */       throw SneakyThrow.sneakyThrow(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   R applyNow(T paramT, U paramU) throws E;
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\sneakythrow\function\ThrowableBiFunction.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */