/*    */ package io.netty.handler.codec.http3;
/*    */ 
/*    */ @FunctionalInterface
/*    */ interface Http3FrameTypeValidator {
/*    */   public static final Http3FrameTypeValidator NO_VALIDATION = (type, first) -> {
/*    */     
/*    */     };
/*    */   
/*    */   void validate(long paramLong, boolean paramBoolean) throws Http3Exception;
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\http3\Http3FrameTypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */