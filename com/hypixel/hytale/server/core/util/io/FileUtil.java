/*     */ package com.hypixel.hytale.server.core.util.io;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.FileVisitOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Set;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipException;
/*     */ import java.util.zip.ZipInputStream;
/*     */ import javax.annotation.Nonnull;
/*     */ 
/*     */ public class FileUtil {
/*  19 */   public static final Set<OpenOption> DEFAULT_WRITE_OPTIONS = Set.of(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
/*  20 */   public static final Set<FileVisitOption> DEFAULT_WALK_TREE_OPTIONS_SET = Set.of();
/*  21 */   public static final FileVisitOption[] DEFAULT_WALK_TREE_OPTIONS_ARRAY = new FileVisitOption[0];
/*     */ 
/*     */   
/*  24 */   public static final Pattern INVALID_FILENAME_CHARACTERS = Pattern.compile("[<>:\"|?*/\\\\]");
/*     */   
/*     */   public static void unzipFile(@Nonnull Path path, @Nonnull byte[] buffer, @Nonnull ZipInputStream zipStream, @Nonnull ZipEntry zipEntry, @Nonnull String name) throws IOException {
/*  27 */     Path filePath = path.resolve(name);
/*     */     
/*  29 */     if (!filePath.toAbsolutePath().startsWith(path)) {
/*  30 */       throw new ZipException("Entry is outside of the target dir: " + zipEntry.getName());
/*     */     }
/*     */     
/*  33 */     if (zipEntry.isDirectory()) {
/*  34 */       Files.createDirectory(filePath, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     } else {
/*  36 */       OutputStream stream = Files.newOutputStream(filePath, new OpenOption[0]); 
/*     */       try { int len;
/*  38 */         while ((len = zipStream.read(buffer)) > 0) {
/*  39 */           stream.write(buffer, 0, len);
/*     */         }
/*  41 */         if (stream != null) stream.close();  } catch (Throwable throwable) { if (stream != null)
/*     */           try { stream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; } 
/*  43 */     }  zipStream.closeEntry();
/*     */   }
/*     */   
/*     */   public static void copyDirectory(@Nonnull Path origin, @Nonnull Path destination) throws IOException {
/*  47 */     Stream<Path> paths = Files.walk(origin, new FileVisitOption[0]); 
/*  48 */     try { paths.forEach(originSubPath -> {
/*     */             try {
/*     */               Path relative = origin.relativize(originSubPath);
/*     */               Path destinationSubPath = destination.resolve(relative);
/*     */               Files.copy(originSubPath, destinationSubPath, new CopyOption[0]);
/*  53 */             } catch (Throwable t) {
/*     */               throw new RuntimeException("Error copying path", t);
/*     */             } 
/*     */           });
/*  57 */       if (paths != null) paths.close();  } catch (Throwable throwable) { if (paths != null)
/*     */         try { paths.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  61 */      } public static void moveDirectoryContents(@Nonnull Path origin, @Nonnull Path destination, CopyOption... options) throws IOException { Stream<Path> paths = Files.walk(origin, new FileVisitOption[0]); 
/*  62 */     try { paths.forEach(originSubPath -> {
/*     */             if (originSubPath.equals(origin))
/*     */               return;  try {
/*     */               Path relative = origin.relativize(originSubPath);
/*     */               Path destinationSubPath = destination.resolve(relative);
/*     */               Files.move(originSubPath, destinationSubPath, options);
/*  68 */             } catch (Throwable t) {
/*     */               throw new RuntimeException("Error moving path", t);
/*     */             } 
/*     */           });
/*  72 */       if (paths != null) paths.close();  } catch (Throwable throwable) { if (paths != null)
/*     */         try { paths.close(); }
/*     */         catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */           throw throwable; }
/*  76 */      } public static void deleteDirectory(@Nonnull Path path) throws IOException { Stream<Path> stream = Files.walk(path, new FileVisitOption[0]); try {
/*  77 */       stream.sorted(Comparator.reverseOrder())
/*  78 */         .forEach(SneakyThrow.sneakyConsumer(Files::delete));
/*  79 */       if (stream != null) stream.close(); 
/*     */     } catch (Throwable throwable) {
/*     */       if (stream != null)
/*     */         try {
/*     */           stream.close();
/*     */         } catch (Throwable throwable1) {
/*     */           throwable.addSuppressed(throwable1);
/*     */         }  
/*     */       throw throwable;
/*     */     }  } public static void extractZip(@Nonnull Path zipFile, @Nonnull Path destDir) throws IOException {
/*  89 */     extractZip(Files.newInputStream(zipFile, new OpenOption[0]), destDir);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void extractZip(@Nonnull InputStream inputStream, @Nonnull Path destDir) throws IOException {
/*  99 */     ZipInputStream zis = new ZipInputStream(inputStream); try {
/*     */       ZipEntry entry;
/* 101 */       while ((entry = zis.getNextEntry()) != null) {
/* 102 */         Path destPath = destDir.resolve(entry.getName()).normalize();
/*     */ 
/*     */         
/* 105 */         if (!destPath.startsWith(destDir)) {
/* 106 */           throw new ZipException("Zip entry outside target directory: " + entry.getName());
/*     */         }
/*     */         
/* 109 */         if (entry.isDirectory()) {
/* 110 */           Files.createDirectories(destPath, (FileAttribute<?>[])new FileAttribute[0]);
/*     */         } else {
/* 112 */           Files.createDirectories(destPath.getParent(), (FileAttribute<?>[])new FileAttribute[0]);
/* 113 */           Files.copy(zis, destPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */         } 
/* 115 */         zis.closeEntry();
/*     */       } 
/* 117 */       zis.close();
/*     */     } catch (Throwable throwable) {
/*     */       try {
/*     */         zis.close();
/*     */       } catch (Throwable throwable1) {
/*     */         throwable.addSuppressed(throwable1);
/*     */       } 
/*     */       throw throwable;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\util\io\FileUtil.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */