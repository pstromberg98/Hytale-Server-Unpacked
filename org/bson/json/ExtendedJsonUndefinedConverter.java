/*    */ package org.bson.json;
/*    */ 
/*    */ import org.bson.BsonUndefined;
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
/*    */ class ExtendedJsonUndefinedConverter
/*    */   implements Converter<BsonUndefined>
/*    */ {
/*    */   public void convert(BsonUndefined value, StrictJsonWriter writer) {
/* 24 */     writer.writeStartObject();
/* 25 */     writer.writeBoolean("$undefined", true);
/* 26 */     writer.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ExtendedJsonUndefinedConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */