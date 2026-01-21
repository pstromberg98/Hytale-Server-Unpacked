/*    */ package io.netty.handler.codec.quic;
/*    */ 
/*    */ import io.netty.util.internal.EmptyArrays;
/*    */ import java.security.PrivateKey;
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
/*    */ final class BoringSSLKeylessPrivateKey
/*    */   implements PrivateKey
/*    */ {
/* 24 */   static final BoringSSLKeylessPrivateKey INSTANCE = new BoringSSLKeylessPrivateKey();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 31 */     return "keyless";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getFormat() {
/* 36 */     return "keyless";
/*    */   }
/*    */ 
/*    */   
/*    */   public byte[] getEncoded() {
/* 41 */     return EmptyArrays.EMPTY_BYTES;
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\quic\BoringSSLKeylessPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */