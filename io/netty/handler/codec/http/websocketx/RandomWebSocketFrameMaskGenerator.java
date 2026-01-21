/*    */ package io.netty.handler.codec.http.websocketx;
/*    */ 
/*    */ import java.util.concurrent.ThreadLocalRandom;
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
/*    */ public final class RandomWebSocketFrameMaskGenerator
/*    */   implements WebSocketFrameMaskGenerator
/*    */ {
/* 24 */   public static final RandomWebSocketFrameMaskGenerator INSTANCE = new RandomWebSocketFrameMaskGenerator();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int nextMask() {
/* 31 */     return ThreadLocalRandom.current().nextInt();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\http\websocketx\RandomWebSocketFrameMaskGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */