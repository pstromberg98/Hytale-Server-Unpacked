/*    */ package com.hypixel.hytale.server.core.auth;
/*    */ 
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import java.util.function.Supplier;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MemoryAuthCredentialStoreProvider
/*    */   implements AuthCredentialStoreProvider
/*    */ {
/*    */   public static final String ID = "Memory";
/* 15 */   public static final BuilderCodec<MemoryAuthCredentialStoreProvider> CODEC = BuilderCodec.builder(MemoryAuthCredentialStoreProvider.class, MemoryAuthCredentialStoreProvider::new).build();
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public IAuthCredentialStore createStore() {
/* 20 */     return new DefaultAuthCredentialStore();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 26 */     return "MemoryAuthCredentialStoreProvider{}";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\auth\MemoryAuthCredentialStoreProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */