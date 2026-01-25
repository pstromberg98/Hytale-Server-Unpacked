/*    */ package com.hypixel.hytale.server.core.io;
/*    */ 
/*    */ import javax.annotation.Nonnull;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProtocolVersion
/*    */ {
/*    */   private final int crc;
/*    */   
/*    */   public ProtocolVersion(int crc) {
/* 15 */     this.crc = crc;
/*    */   }
/*    */   
/*    */   public int getCrc() {
/* 19 */     return this.crc;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(@Nullable Object o) {
/* 24 */     if (this == o) return true; 
/* 25 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 27 */     ProtocolVersion that = (ProtocolVersion)o;
/*    */     
/* 29 */     return (this.crc == that.crc);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 34 */     return 31 * this.crc;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public String toString() {
/* 40 */     return "ProtocolVersion{crc=" + this.crc + "}";
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\io\ProtocolVersion.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */