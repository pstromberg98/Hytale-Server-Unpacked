/*    */ package ch.randelshofer.fastdoubleparser.chr;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FormatCharSet
/*    */   implements CharSet
/*    */ {
/*    */   public boolean containsKey(char ch) {
/* 13 */     return (Character.getType(ch) == 16);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\ch\randelshofer\fastdoubleparser\chr\FormatCharSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */