/*   */ package com.github.luben.zstd;
/*   */ 
/*   */ abstract class SharedDictBase
/*   */   extends AutoCloseBase
/*   */ {
/*   */   protected void finalize() {
/* 7 */     close();
/*   */   }
/*   */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\github\luben\zstd\SharedDictBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */