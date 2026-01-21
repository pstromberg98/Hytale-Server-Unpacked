/*    */ package ch.randelshofer.fastdoubleparser.chr;
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ConsecutiveCharDigitSet
/*    */   implements CharDigitSet
/*    */ {
/*    */   private final char zeroDigit;
/*    */   
/*    */   public ConsecutiveCharDigitSet(char zeroDigit) {
/* 11 */     this.zeroDigit = zeroDigit;
/*    */   }
/*    */ 
/*    */   
/*    */   public int toDigit(char ch) {
/* 16 */     return (char)(ch - this.zeroDigit);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\ch\randelshofer\fastdoubleparser\chr\ConsecutiveCharDigitSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */