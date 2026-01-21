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
/*    */ public class DoubleCodec
/*    */   implements Codec<Double>
/*    */ {
/*    */   public void encode(BsonWriter writer, Double value, EncoderContext encoderContext) {
/* 32 */     writer.writeDouble(value.doubleValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public Double decode(BsonReader reader, DecoderContext decoderContext) {
/* 37 */     return Double.valueOf(NumberCodecHelper.decodeDouble(reader));
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<Double> getEncoderClass() {
/* 42 */     return Double.class;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bson\codecs\DoubleCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */