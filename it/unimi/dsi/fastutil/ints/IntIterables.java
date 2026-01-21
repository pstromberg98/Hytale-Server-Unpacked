/*    */ package it.unimi.dsi.fastutil.ints;
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
/*    */ public final class IntIterables
/*    */ {
/*    */   public static long size(IntIterable iterable) {
/* 34 */     long c = 0L;
/*    */     
/* 36 */     for (IntIterator intIterator = iterable.iterator(); intIterator.hasNext(); ) { int dummy = ((Integer)intIterator.next()).intValue(); c++; }
/* 37 */      return c;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\i\\unimi\dsi\fastutil\ints\IntIterables.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */