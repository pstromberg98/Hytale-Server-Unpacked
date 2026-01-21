/*    */ package io.netty.handler.ssl;
/*    */ 
/*    */ import javax.net.ssl.SSLException;
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
/*    */ public final class SslClosedEngineException
/*    */   extends SSLException
/*    */ {
/*    */   private static final long serialVersionUID = -5204207600474401904L;
/*    */   
/*    */   public SslClosedEngineException(String reason) {
/* 29 */     super(reason);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\ssl\SslClosedEngineException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */