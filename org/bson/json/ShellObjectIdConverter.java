/*    */ package org.bson.json;
/*    */ 
/*    */ import org.bson.types.ObjectId;
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
/*    */ class ShellObjectIdConverter
/*    */   implements Converter<ObjectId>
/*    */ {
/*    */   public void convert(ObjectId value, StrictJsonWriter writer) {
/* 26 */     writer.writeRaw(String.format("ObjectId(\"%s\")", new Object[] { value.toHexString() }));
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\json\ShellObjectIdConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */