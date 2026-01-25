/*    */ package com.hypixel.hytale.server.core.ui.browser;
/*    */ 
/*    */ import java.nio.file.Path;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface FileListProvider {
/*    */   @Nonnull
/*    */   List<FileEntry> getFiles(@Nonnull Path paramPath, @Nonnull String paramString);
/*    */   
/*    */   public static final class FileEntry extends Record {
/*    */     @Nonnull
/*    */     private final String name;
/*    */     @Nonnull
/*    */     private final String displayName;
/*    */     private final boolean isDirectory;
/*    */     private final boolean isTerminal;
/*    */     private final int matchScore;
/*    */     
/*    */     public final String toString() {
/*    */       // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;)Ljava/lang/String;
/*    */       //   6: areturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #33	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;
/*    */     }
/*    */     
/*    */     public final int hashCode() {
/*    */       // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;)I
/*    */       //   6: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #33	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;
/*    */     }
/*    */     
/*    */     public final boolean equals(Object o) {
/*    */       // Byte code:
/*    */       //   0: aload_0
/*    */       //   1: aload_1
/*    */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;Ljava/lang/Object;)Z
/*    */       //   7: ireturn
/*    */       // Line number table:
/*    */       //   Java source line number -> byte code offset
/*    */       //   #33	-> 0
/*    */       // Local variable table:
/*    */       //   start	length	slot	name	descriptor
/*    */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/ui/browser/FileListProvider$FileEntry;
/*    */       //   0	8	1	o	Ljava/lang/Object;
/*    */     }
/*    */     
/* 33 */     public FileEntry(@Nonnull String name, @Nonnull String displayName, boolean isDirectory, boolean isTerminal, int matchScore) { this.name = name; this.displayName = displayName; this.isDirectory = isDirectory; this.isTerminal = isTerminal; this.matchScore = matchScore; } @Nonnull public String name() { return this.name; } @Nonnull public String displayName() { return this.displayName; } public boolean isDirectory() { return this.isDirectory; } public boolean isTerminal() { return this.isTerminal; } public int matchScore() { return this.matchScore; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public FileEntry(@Nonnull String name, boolean isDirectory) {
/* 41 */       this(name, name, isDirectory, false, 0);
/*    */     }
/*    */     
/*    */     public FileEntry(@Nonnull String name, @Nonnull String displayName, boolean isDirectory) {
/* 45 */       this(name, displayName, isDirectory, false, 0);
/*    */     }
/*    */     
/*    */     public FileEntry(@Nonnull String name, @Nonnull String displayName, boolean isDirectory, boolean isTerminal) {
/* 49 */       this(name, displayName, isDirectory, isTerminal, 0);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\ui\browser\FileListProvider.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */