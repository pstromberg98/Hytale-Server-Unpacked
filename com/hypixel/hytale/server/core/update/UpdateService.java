/*     */ package com.hypixel.hytale.server.core.update;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.EmptyExtraInfo;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.util.RawJsonReader;
/*     */ import com.hypixel.hytale.common.util.java.ManifestUtil;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.HytaleServer;
/*     */ import com.hypixel.hytale.server.core.HytaleServerConfig;
/*     */ import com.hypixel.hytale.server.core.auth.AuthConfig;
/*     */ import com.hypixel.hytale.server.core.auth.ServerAuthManager;
/*     */ import com.hypixel.hytale.server.core.util.io.FileUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.time.Duration;
/*     */ import java.util.HexFormat;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.jar.Attributes;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.Manifest;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public class UpdateService {
/*  41 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*  42 */   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30L);
/*  43 */   private static final Duration DOWNLOAD_TIMEOUT = Duration.ofMinutes(30L);
/*  44 */   private static final Path STAGING_DIR = Path.of("..", new String[0]).resolve("updater").resolve("staging");
/*  45 */   private static final Path BACKUP_DIR = Path.of("..", new String[0]).resolve("updater").resolve("backup");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private final String accountDataUrl = "https://account-data.hytale.com";
/*  52 */   private final HttpClient httpClient = HttpClient.newBuilder()
/*  53 */     .connectTimeout(REQUEST_TIMEOUT)
/*  54 */     .followRedirects(HttpClient.Redirect.NORMAL)
/*  55 */     .build();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public CompletableFuture<VersionManifest> checkForUpdate(@Nonnull String patchline) {
/*  66 */     return CompletableFuture.supplyAsync(() -> {
/*     */           try {
/*     */             ServerAuthManager authManager = ServerAuthManager.getInstance();
/*     */ 
/*     */             
/*     */             String accessToken = authManager.getOAuthAccessToken();
/*     */             
/*     */             if (accessToken == null) {
/*     */               LOGGER.at(Level.WARNING).log("Cannot check for updates - not authenticated");
/*     */               
/*     */               return null;
/*     */             } 
/*     */             
/*     */             String manifestPath = String.format("version/%s.json", new Object[] { patchline });
/*     */             
/*     */             String signedUrl = getSignedUrl(accessToken, manifestPath);
/*     */             
/*     */             if (signedUrl == null) {
/*     */               LOGGER.at(Level.WARNING).log("Failed to get signed URL for version manifest");
/*     */               
/*     */               return null;
/*     */             } 
/*     */             
/*     */             HttpRequest manifestRequest = HttpRequest.newBuilder().uri(URI.create(signedUrl)).header("Accept", "application/json").timeout(REQUEST_TIMEOUT).GET().build();
/*     */             
/*     */             HttpResponse<String> response = this.httpClient.send(manifestRequest, HttpResponse.BodyHandlers.ofString());
/*     */             
/*     */             if (response.statusCode() != 200) {
/*     */               LOGGER.at(Level.WARNING).log("Failed to fetch version manifest: HTTP %d", response.statusCode());
/*     */               
/*     */               return null;
/*     */             } 
/*     */             
/*     */             VersionManifest manifest = (VersionManifest)VersionManifest.CODEC.decodeJson(new RawJsonReader(((String)response.body()).toCharArray()), (ExtraInfo)EmptyExtraInfo.EMPTY);
/*     */             
/*     */             if (manifest == null || manifest.version == null) {
/*     */               LOGGER.at(Level.WARNING).log("Invalid version manifest response");
/*     */               
/*     */               return null;
/*     */             } 
/*     */             
/*     */             LOGGER.at(Level.INFO).log("Found version: %s", manifest.version);
/*     */             
/*     */             return manifest;
/* 110 */           } catch (IOException e) {
/*     */             LOGGER.at(Level.WARNING).log("IO error checking for updates: %s", e.getMessage());
/*     */             return null;
/* 113 */           } catch (InterruptedException e) {
/*     */             Thread.currentThread().interrupt();
/*     */             LOGGER.at(Level.WARNING).log("Update check interrupted");
/*     */             return null;
/* 117 */           } catch (Exception e) {
/*     */             ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Error checking for updates");
/*     */             return null;
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DownloadTask downloadUpdate(@Nonnull VersionManifest manifest, @Nonnull Path stagingDir, @Nullable ProgressCallback progressCallback) {
/* 137 */     CompletableFuture<Boolean> future = new CompletableFuture<>();
/* 138 */     Thread thread = new Thread(() -> {
/*     */           try {
/*     */             boolean result = performDownload(manifest, stagingDir, progressCallback);
/*     */             future.complete(Boolean.valueOf(result));
/* 142 */           } catch (CancellationException e) {
/*     */             future.completeExceptionally(e);
/* 144 */           } catch (InterruptedException e) {
/*     */             Thread.currentThread().interrupt();
/*     */             future.completeExceptionally(new CancellationException("Update download interrupted"));
/* 147 */           } catch (Exception e) {
/*     */             ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Error downloading update");
/*     */             future.complete(Boolean.valueOf(false));
/*     */           } 
/*     */         }"UpdateDownload");
/* 152 */     thread.setDaemon(true);
/* 153 */     thread.start();
/* 154 */     return new DownloadTask(future, thread);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean performDownload(@Nonnull VersionManifest manifest, @Nonnull Path stagingDir, @Nullable ProgressCallback progressCallback) throws IOException, InterruptedException {
/* 162 */     ServerAuthManager authManager = ServerAuthManager.getInstance();
/* 163 */     String accessToken = authManager.getOAuthAccessToken();
/* 164 */     if (accessToken == null) {
/* 165 */       LOGGER.at(Level.WARNING).log("Cannot download update - not authenticated");
/* 166 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 170 */     String signedUrl = getSignedUrl(accessToken, manifest.downloadUrl);
/* 171 */     if (signedUrl == null) {
/* 172 */       LOGGER.at(Level.WARNING).log("Failed to get signed URL for download");
/* 173 */       return false;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 181 */     HttpRequest downloadRequest = HttpRequest.newBuilder().uri(URI.create(signedUrl)).timeout(DOWNLOAD_TIMEOUT).GET().build();
/*     */     
/* 183 */     Path tempFile = Files.createTempFile("hytale-update-", ".zip", (FileAttribute<?>[])new FileAttribute[0]); try {
/*     */       MessageDigest digest;
/* 185 */       HttpResponse<InputStream> response = this.httpClient.send(downloadRequest, HttpResponse.BodyHandlers.ofInputStream());
/* 186 */       if (response.statusCode() != 200) {
/* 187 */         LOGGER.at(Level.WARNING).log("Failed to download update: HTTP %d", response.statusCode());
/* 188 */         return false;
/*     */       } 
/*     */       
/* 191 */       long contentLength = response.headers().firstValueAsLong("Content-Length").orElse(-1L);
/*     */       
/*     */       try {
/* 194 */         digest = MessageDigest.getInstance("SHA-256");
/* 195 */       } catch (NoSuchAlgorithmException e) {
/* 196 */         LOGGER.at(Level.SEVERE).log("SHA-256 not available - this should never happen");
/* 197 */         return false;
/*     */       } 
/*     */ 
/*     */       
/* 201 */       InputStream inputStream = response.body(); 
/* 202 */       try { OutputStream outputStream = Files.newOutputStream(tempFile, new java.nio.file.OpenOption[0]); 
/* 203 */         try { byte[] buffer = new byte[8192];
/* 204 */           long downloaded = 0L;
/*     */           
/*     */           int read;
/* 207 */           while ((read = inputStream.read(buffer)) != -1) {
/* 208 */             if (Thread.currentThread().isInterrupted()) {
/* 209 */               throw new CancellationException("Update download cancelled");
/*     */             }
/* 211 */             outputStream.write(buffer, 0, read);
/* 212 */             digest.update(buffer, 0, read);
/* 213 */             downloaded += read;
/*     */             
/* 215 */             if (progressCallback != null && contentLength > 0L) {
/* 216 */               int percent = (int)(downloaded * 100L / contentLength);
/* 217 */               progressCallback.onProgress(percent, downloaded, contentLength);
/*     */             } 
/*     */           } 
/* 220 */           if (outputStream != null) outputStream.close();  } catch (Throwable throwable) { if (outputStream != null) try { outputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  if (inputStream != null) inputStream.close();  } catch (Throwable throwable) { if (inputStream != null)
/*     */           try { inputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */             throw throwable; }
/* 223 */        String actualHash = HexFormat.of().formatHex(digest.digest());
/* 224 */       if (manifest.sha256 != null && !manifest.sha256.equalsIgnoreCase(actualHash)) {
/* 225 */         LOGGER.at(Level.WARNING).log("Checksum mismatch! Expected: %s, Got: %s", manifest.sha256, actualHash);
/* 226 */         return false;
/*     */       } 
/*     */ 
/*     */       
/* 230 */       if (!clearStagingDir(stagingDir)) {
/* 231 */         LOGGER.at(Level.WARNING).log("Failed to clear staging directory before extraction");
/* 232 */         return false;
/*     */       } 
/* 234 */       Files.createDirectories(stagingDir, (FileAttribute<?>[])new FileAttribute[0]);
/* 235 */       if (Thread.currentThread().isInterrupted()) {
/* 236 */         throw new CancellationException("Update download cancelled");
/*     */       }
/* 238 */       FileUtil.extractZip(tempFile, stagingDir);
/*     */       
/* 240 */       LOGGER.at(Level.INFO).log("Update %s downloaded and extracted to staging", manifest.version);
/* 241 */       return true;
/*     */     } finally {
/* 243 */       Files.deleteIfExists(tempFile);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private String getSignedUrl(String accessToken, String path) throws IOException, InterruptedException {
/* 249 */     String url = this.accountDataUrl + "/game-assets/" + this.accountDataUrl;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 258 */     HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Accept", "application/json").header("Authorization", "Bearer " + accessToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(REQUEST_TIMEOUT).GET().build();
/*     */     
/* 260 */     HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
/* 261 */     if (response.statusCode() != 200) {
/* 262 */       LOGGER.at(Level.WARNING).log("Failed to get signed URL: HTTP %d - %s", response.statusCode(), response.body());
/* 263 */       return null;
/*     */     } 
/*     */     
/* 266 */     SignedUrlResponse signedResponse = (SignedUrlResponse)SignedUrlResponse.CODEC.decodeJson(new RawJsonReader(((String)response
/* 267 */           .body()).toCharArray()), (ExtraInfo)EmptyExtraInfo.EMPTY);
/*     */ 
/*     */ 
/*     */     
/* 271 */     return (signedResponse != null) ? signedResponse.url : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public static String getEffectivePatchline() {
/* 279 */     HytaleServerConfig.UpdateConfig config = HytaleServer.get().getConfig().getUpdateConfig();
/* 280 */     String patchline = config.getPatchline();
/* 281 */     if (patchline != null && !patchline.isEmpty()) return patchline;
/*     */     
/* 283 */     patchline = ManifestUtil.getPatchline();
/* 284 */     return (patchline != null) ? patchline : "release";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isValidUpdateLayout() {
/* 292 */     Path parent = Path.of("..", new String[0]).toAbsolutePath();
/* 293 */     return (Files.exists(parent.resolve("Assets.zip"), new java.nio.file.LinkOption[0]) && (
/* 294 */       Files.exists(parent.resolve("start.sh"), new java.nio.file.LinkOption[0]) || Files.exists(parent.resolve("start.bat"), new java.nio.file.LinkOption[0])));
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Path getStagingDir() {
/* 299 */     return STAGING_DIR;
/*     */   }
/*     */   
/*     */   @Nonnull
/*     */   public static Path getBackupDir() {
/* 304 */     return BACKUP_DIR;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String getStagedVersion() {
/* 314 */     Path stagedJar = STAGING_DIR.resolve("Server").resolve("HytaleServer.jar");
/* 315 */     if (!Files.exists(stagedJar, new java.nio.file.LinkOption[0])) return null; 
/* 316 */     return readVersionFromJar(stagedJar);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean deleteStagedUpdate() {
/* 323 */     return safeDeleteUpdaterDir(STAGING_DIR, "staging");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean deleteBackupDir() {
/* 330 */     return safeDeleteUpdaterDir(BACKUP_DIR, "backup");
/*     */   }
/*     */   
/*     */   private static boolean clearStagingDir(@Nonnull Path stagingDir) {
/* 334 */     if (!Files.exists(stagingDir, new java.nio.file.LinkOption[0])) return true;
/*     */     
/* 336 */     if (stagingDir.toAbsolutePath().normalize().equals(STAGING_DIR.toAbsolutePath().normalize())) {
/* 337 */       return deleteStagedUpdate();
/*     */     }
/*     */     
/*     */     try {
/* 341 */       FileUtil.deleteDirectory(stagingDir);
/* 342 */       return true;
/* 343 */     } catch (IOException e) {
/* 344 */       LOGGER.at(Level.WARNING).log("Failed to delete staging dir %s: %s", stagingDir, e.getMessage());
/* 345 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean safeDeleteUpdaterDir(Path dir, String expectedName) {
/*     */     try {
/* 355 */       if (!Files.exists(dir, new java.nio.file.LinkOption[0])) return true;
/*     */ 
/*     */       
/* 358 */       Path absolute = dir.toAbsolutePath().normalize();
/* 359 */       Path parent = absolute.getParent();
/* 360 */       if (parent == null || !parent.getFileName().toString().equals("updater")) {
/* 361 */         LOGGER.at(Level.SEVERE).log("Refusing to delete %s - not within updater/ directory", absolute);
/* 362 */         return false;
/*     */       } 
/* 364 */       if (!absolute.getFileName().toString().equals(expectedName)) {
/* 365 */         LOGGER.at(Level.SEVERE).log("Refusing to delete %s - unexpected directory name", absolute);
/* 366 */         return false;
/*     */       } 
/*     */       
/* 369 */       FileUtil.deleteDirectory(dir);
/* 370 */       return true;
/* 371 */     } catch (IOException e) {
/* 372 */       LOGGER.at(Level.WARNING).log("Failed to delete %s: %s", dir, e.getMessage());
/* 373 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String readVersionFromJar(@Nonnull Path jarPath) {
/*     */     
/* 385 */     try { JarFile jarFile = new JarFile(jarPath.toFile()); 
/* 386 */       try { Manifest manifest = jarFile.getManifest();
/* 387 */         if (manifest == null) { String str = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 394 */           jarFile.close(); return str; }  Attributes attrs = manifest.getMainAttributes(); String vendorId = attrs.getValue("Implementation-Vendor-Id"); if (!"com.hypixel.hytale".equals(vendorId)) { String str = null; jarFile.close(); return str; }  String str1 = attrs.getValue("Implementation-Version"); jarFile.close(); return str1; } catch (Throwable throwable) { try { jarFile.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (IOException e)
/* 395 */     { LOGGER.at(Level.WARNING).log("Failed to read version from JAR: %s", e.getMessage());
/* 396 */       return null; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private static <T> KeyedCodec<T> externalKey(String key, Codec<T> codec) {
/* 402 */     return new KeyedCodec(key, codec, false, true);
/*     */   }
/*     */   public static final class DownloadTask extends Record {
/*     */     private final CompletableFuture<Boolean> future;
/*     */     private final Thread thread;
/*     */     
/*     */     public final String toString() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #416	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;
/*     */     }
/*     */     
/*     */     public final int hashCode() {
/*     */       // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #416	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;
/*     */     }
/*     */     
/* 416 */     public DownloadTask(CompletableFuture<Boolean> future, Thread thread) { this.future = future; this.thread = thread; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #416	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lcom/hypixel/hytale/server/core/update/UpdateService$DownloadTask;
/* 416 */       //   0	8	1	o	Ljava/lang/Object; } public CompletableFuture<Boolean> future() { return this.future; } public Thread thread() { return this.thread; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public static class VersionManifest
/*     */   {
/*     */     public String version;
/*     */     
/*     */     public String downloadUrl;
/*     */     public String sha256;
/*     */     public static final BuilderCodec<VersionManifest> CODEC;
/*     */     
/*     */     static {
/* 430 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(VersionManifest.class, VersionManifest::new).append(UpdateService.externalKey("version", (Codec<?>)Codec.STRING), (m, v) -> m.version = v, m -> m.version).add()).append(UpdateService.externalKey("download_url", (Codec<?>)Codec.STRING), (m, v) -> m.downloadUrl = v, m -> m.downloadUrl).add()).append(UpdateService.externalKey("sha256", (Codec<?>)Codec.STRING), (m, v) -> m.sha256 = v, m -> m.sha256).add()).build();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SignedUrlResponse
/*     */   {
/*     */     public String url;
/*     */     public static final BuilderCodec<SignedUrlResponse> CODEC;
/*     */     
/*     */     static {
/* 441 */       CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(SignedUrlResponse.class, SignedUrlResponse::new).append(UpdateService.externalKey("url", (Codec<?>)Codec.STRING), (r, v) -> r.url = v, r -> r.url).add()).build();
/*     */     }
/*     */   }
/*     */   
/*     */   @FunctionalInterface
/*     */   public static interface ProgressCallback {
/*     */     void onProgress(int param1Int, long param1Long1, long param1Long2);
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\cor\\update\UpdateService.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */