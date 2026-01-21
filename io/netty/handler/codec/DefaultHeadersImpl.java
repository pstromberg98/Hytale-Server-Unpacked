/*    */ package io.netty.handler.codec;
/*    */ 
/*    */ import io.netty.util.HashingStrategy;
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
/*    */ public final class DefaultHeadersImpl<K, V>
/*    */   extends DefaultHeaders<K, V, DefaultHeadersImpl<K, V>>
/*    */ {
/*    */   public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator) {
/* 27 */     super(nameHashingStrategy, valueConverter, nameValidator);
/*    */   }
/*    */ 
/*    */   
/*    */   public DefaultHeadersImpl(HashingStrategy<K> nameHashingStrategy, ValueConverter<V> valueConverter, DefaultHeaders.NameValidator<K> nameValidator, int arraySizeHint, DefaultHeaders.ValueValidator<V> valueValidator) {
/* 32 */     super(nameHashingStrategy, valueConverter, nameValidator, arraySizeHint, valueValidator);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\io\netty\handler\codec\DefaultHeadersImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */