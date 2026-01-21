/*    */ package io.netty.handler.codec.http.websocketx.extensions;
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
/*    */ public interface WebSocketExtensionFilterProvider
/*    */ {
/* 23 */   public static final WebSocketExtensionFilterProvider DEFAULT = new WebSocketExtensionFilterProvider()
/*    */     {
/*    */       public WebSocketExtensionFilter encoderFilter() {
/* 26 */         return WebSocketExtensionFilter.NEVER_SKIP;
/*    */       }
/*    */ 
/*    */       
/*    */       public WebSocketExtensionFilter decoderFilter() {
/* 31 */         return WebSocketExtensionFilter.NEVER_SKIP;
/*    */       }
/*    */     };
/*    */   
/*    */   WebSocketExtensionFilter encoderFilter();
/*    */   
/*    */   WebSocketExtensionFilter decoderFilter();
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\http\websocketx\extensions\WebSocketExtensionFilterProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */