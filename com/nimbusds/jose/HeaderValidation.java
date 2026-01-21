/*    */ package com.nimbusds.jose;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class HeaderValidation
/*    */ {
/*    */   static void ensureDisjoint(Header header, UnprotectedHeader unprotectedHeader) throws IllegalHeaderException {
/* 44 */     if (header == null || unprotectedHeader == null) {
/*    */       return;
/*    */     }
/*    */     
/* 48 */     for (String unprotectedParamName : unprotectedHeader.getIncludedParams()) {
/* 49 */       if (header.getIncludedParams().contains(unprotectedParamName))
/* 50 */         throw new IllegalHeaderException("The parameters in the protected header and the unprotected header must be disjoint"); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\HeaderValidation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */