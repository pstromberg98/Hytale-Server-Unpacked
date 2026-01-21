/*    */ package org.bson.json;
/*    */ 
/*    */ import org.bson.types.Decimal128;
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
/*    */ class ShellDecimal128Converter
/*    */   implements Converter<Decimal128>
/*    */ {
/*    */   public void convert(Decimal128 value, StrictJsonWriter writer) {
/* 26 */     writer.writeRaw(String.format("NumberDecimal(\"%s\")", new Object[] { value.toString() }));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ShellDecimal128Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */