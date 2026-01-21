/*    */ package org.bson.json;
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
/*    */ class ExtendedJsonInt32Converter
/*    */   implements Converter<Integer>
/*    */ {
/*    */   public void convert(Integer value, StrictJsonWriter writer) {
/* 22 */     writer.writeStartObject();
/* 23 */     writer.writeName("$numberInt");
/* 24 */     writer.writeString(Integer.toString(value.intValue()));
/* 25 */     writer.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ExtendedJsonInt32Converter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */