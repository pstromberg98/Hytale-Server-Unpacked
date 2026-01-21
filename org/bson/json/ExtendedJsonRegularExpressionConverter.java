/*    */ package org.bson.json;
/*    */ 
/*    */ import org.bson.BsonRegularExpression;
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
/*    */ class ExtendedJsonRegularExpressionConverter
/*    */   implements Converter<BsonRegularExpression>
/*    */ {
/*    */   public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
/* 24 */     writer.writeStartObject();
/* 25 */     writer.writeStartObject("$regularExpression");
/* 26 */     writer.writeString("pattern", value.getPattern());
/* 27 */     writer.writeString("options", value.getOptions());
/* 28 */     writer.writeEndObject();
/* 29 */     writer.writeEndObject();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ExtendedJsonRegularExpressionConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */