/*    */ package org.bson.codecs;
/*    */ 
/*    */ import org.bson.BsonReader;
/*    */ import org.bson.BsonTimestamp;
/*    */ import org.bson.BsonWriter;
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
/*    */ public class BsonTimestampCodec
/*    */   implements Codec<BsonTimestamp>
/*    */ {
/*    */   public void encode(BsonWriter writer, BsonTimestamp value, EncoderContext encoderContext) {
/* 31 */     writer.writeTimestamp(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public BsonTimestamp decode(BsonReader reader, DecoderContext decoderContext) {
/* 36 */     return reader.readTimestamp();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<BsonTimestamp> getEncoderClass() {
/* 41 */     return BsonTimestamp.class;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\BsonTimestampCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */