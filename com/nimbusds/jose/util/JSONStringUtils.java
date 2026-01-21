/*    */ package com.nimbusds.jose.util;
/*    */ 
/*    */ import com.nimbusds.jose.shaded.gson.Gson;
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
/*    */ public class JSONStringUtils
/*    */ {
/*    */   public static String toJSONString(String string) {
/* 41 */     return (new Gson()).toJson(string);
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jos\\util\JSONStringUtils.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */