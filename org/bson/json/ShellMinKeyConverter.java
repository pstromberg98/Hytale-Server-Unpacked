/*    */ package org.bson.json;
/*    */ 
/*    */ import org.bson.BsonMinKey;
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
/*    */ class ShellMinKeyConverter
/*    */   implements Converter<BsonMinKey>
/*    */ {
/*    */   public void convert(BsonMinKey value, StrictJsonWriter writer) {
/* 24 */     writer.writeRaw("MinKey");
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ShellMinKeyConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */