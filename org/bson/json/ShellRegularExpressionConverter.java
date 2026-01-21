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
/*    */ class ShellRegularExpressionConverter
/*    */   implements Converter<BsonRegularExpression>
/*    */ {
/*    */   public void convert(BsonRegularExpression value, StrictJsonWriter writer) {
/* 24 */     String escaped = value.getPattern().equals("") ? "(?:)" : value.getPattern().replace("/", "\\/");
/* 25 */     writer.writeRaw("/" + escaped + "/" + value.getOptions());
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ShellRegularExpressionConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */