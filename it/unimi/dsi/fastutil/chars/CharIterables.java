/*    */ package it.unimi.dsi.fastutil.chars;
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
/*    */ 
/*    */ public final class CharIterables
/*    */ {
/*    */   public static long size(CharIterable iterable) {
/* 34 */     long c = 0L;
/*    */     
/* 36 */     for (CharIterator charIterator = iterable.iterator(); charIterator.hasNext(); ) { char dummy = ((Character)charIterator.next()).charValue(); c++; }
/* 37 */      return c;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\i\\unimi\dsi\fastutil\chars\CharIterables.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */