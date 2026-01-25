/*    */ package com.hypixel.hytale.protocol;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ProtocolSettings
/*    */ {
/*    */   public static final int PROTOCOL_CRC = 1789265863;
/*    */   public static final int PROTOCOL_VERSION = 2;
/*    */   public static final int PROTOCOL_BUILD_NUMBER = 2;
/*    */   public static final int PACKET_COUNT = 268;
/*    */   public static final int STRUCT_COUNT = 314;
/*    */   public static final int ENUM_COUNT = 136;
/*    */   public static final int MAX_PACKET_SIZE = 1677721600;
/*    */   
/*    */   public static boolean validateCrc(int crc) {
/* 16 */     return (1789265863 == crc);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\protocol\ProtocolSettings.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */