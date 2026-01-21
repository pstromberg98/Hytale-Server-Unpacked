/*    */ package org.bson.codecs;
/*    */ 
/*    */ import org.bson.BsonReader;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LongCodec
/*    */   implements Codec<Long>
/*    */ {
/*    */   public void encode(BsonWriter writer, Long value, EncoderContext encoderContext) {
/* 34 */     writer.writeInt64(value.longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Long decode(BsonReader reader, DecoderContext decoderContext) {
/* 39 */     return Long.valueOf(NumberCodecHelper.decodeLong(reader));
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<Long> getEncoderClass() {
/* 44 */     return Long.class;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\LongCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */