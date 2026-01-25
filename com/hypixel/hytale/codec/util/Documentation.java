/*    */ package com.hypixel.hytale.codec.util;
/*    */ 
/*    */ import it.unimi.dsi.fastutil.chars.CharArrayList;
/*    */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Documentation
/*    */ {
/*    */   public static String stripMarkdown(@Nullable String markdown) {
/* 17 */     if (markdown == null) return null;
/*    */     
/* 19 */     StringBuilder output = new StringBuilder();
/* 20 */     IntArrayList counts = new IntArrayList();
/* 21 */     CharArrayList expectedChars = new CharArrayList();
/* 22 */     for (int i = 0; i < markdown.length(); i++) {
/* 23 */       int start; boolean isEnding; int targetCount, matchingCount; char c = markdown.charAt(i);
/* 24 */       switch (c) { case '*':
/*    */         case '_':
/* 26 */           start = i;
/* 27 */           isEnding = (start >= 1 && !Character.isWhitespace(markdown.charAt(start - 1)));
/* 28 */           if (isEnding && (expectedChars.isEmpty() || expectedChars.getChar(expectedChars.size() - 1) != c)) {
/* 29 */             output.append(markdown.charAt(i));
/*    */             break;
/*    */           } 
/* 32 */           targetCount = (!counts.isEmpty() && isEnding) ? counts.getInt(counts.size() - 1) : -1;
/* 33 */           for (; i < markdown.length() && 
/* 34 */             markdown.charAt(i) == c && i - start != targetCount; i++);
/*    */           
/* 36 */           matchingCount = i - start;
/* 37 */           if (!counts.isEmpty() && counts.getInt(counts.size() - 1) == matchingCount) {
/* 38 */             if (!isEnding) {
/* 39 */               output.append(String.valueOf(c).repeat(matchingCount));
/*    */               break;
/*    */             } 
/* 42 */             counts.removeInt(counts.size() - 1);
/* 43 */             expectedChars.removeChar(expectedChars.size() - 1);
/*    */           } else {
/* 45 */             if (i < markdown.length() && Character.isWhitespace(markdown.charAt(i))) {
/* 46 */               output.append(String.valueOf(c).repeat(matchingCount));
/* 47 */               output.append(markdown.charAt(i));
/*    */               break;
/*    */             } 
/* 50 */             counts.add(matchingCount);
/* 51 */             expectedChars.add(c);
/*    */           } 
/* 53 */           i--; break;
/*    */         default:
/* 55 */           output.append(c); break; }
/*    */     
/*    */     } 
/* 58 */     if (!counts.isEmpty()) {
/* 59 */       throw new IllegalArgumentException("Unbalanced markdown formatting");
/*    */     }
/* 61 */     return output.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\code\\util\Documentation.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */