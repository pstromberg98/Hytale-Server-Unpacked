/*     */ package com.hypixel.hytale.server.core.auth;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.EmptyExtraInfo;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.codec.util.RawJsonReader;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import java.net.URLEncoder;
/*     */ import java.net.http.HttpClient;
/*     */ import java.net.http.HttpRequest;
/*     */ import java.net.http.HttpResponse;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.Duration;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ public class ProfileServiceClient
/*     */ {
/*  28 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*  29 */   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5L);
/*     */ 
/*     */   
/*     */   private final HttpClient httpClient;
/*     */ 
/*     */   
/*     */   private final String profileServiceUrl;
/*     */ 
/*     */ 
/*     */   
/*     */   public ProfileServiceClient(@Nonnull String profileServiceUrl) {
/*  40 */     if (profileServiceUrl == null || profileServiceUrl.isEmpty()) {
/*  41 */       throw new IllegalArgumentException("Profile Service URL cannot be null or empty");
/*     */     }
/*     */     
/*  44 */     this
/*     */       
/*  46 */       .profileServiceUrl = profileServiceUrl.endsWith("/") ? profileServiceUrl.substring(0, profileServiceUrl.length() - 1) : profileServiceUrl;
/*     */     
/*  48 */     this
/*     */       
/*  50 */       .httpClient = HttpClient.newBuilder().connectTimeout(REQUEST_TIMEOUT).build();
/*     */     
/*  52 */     LOGGER.at(Level.INFO).log("Profile Service client initialized for: %s", this.profileServiceUrl);
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
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public PublicGameProfile getProfileByUuid(@Nonnull UUID uuid, @Nonnull String bearerToken) {
/*     */     try {
/*  73 */       HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.profileServiceUrl + "/profile/uuid/" + this.profileServiceUrl)).header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(REQUEST_TIMEOUT).GET().build();
/*     */       
/*  75 */       LOGGER.at(Level.FINE).log("Fetching profile by UUID: %s", uuid);
/*     */       
/*  77 */       HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
/*     */       
/*  79 */       if (response.statusCode() != 200) {
/*  80 */         LOGGER.at(Level.WARNING).log("Failed to fetch profile by UUID: HTTP %d - %s", response
/*     */             
/*  82 */             .statusCode(), response.body());
/*     */         
/*  84 */         return null;
/*     */       } 
/*     */       
/*  87 */       PublicGameProfile profile = (PublicGameProfile)PublicGameProfile.CODEC.decodeJson(new RawJsonReader(((String)response
/*  88 */             .body()).toCharArray()), (ExtraInfo)EmptyExtraInfo.EMPTY);
/*     */ 
/*     */ 
/*     */       
/*  92 */       if (profile == null) {
/*  93 */         LOGGER.at(Level.WARNING).log("Profile Service returned invalid response for UUID: %s", uuid);
/*  94 */         return null;
/*     */       } 
/*     */       
/*  97 */       LOGGER.at(Level.FINE).log("Successfully fetched profile: %s (%s)", profile.getUsername(), profile.getUuid());
/*  98 */       return profile;
/*     */     }
/* 100 */     catch (IOException e) {
/* 101 */       LOGGER.at(Level.WARNING).log("IO error while fetching profile by UUID: %s", e.getMessage());
/* 102 */       return null;
/* 103 */     } catch (InterruptedException e) {
/* 104 */       LOGGER.at(Level.WARNING).log("Request interrupted while fetching profile by UUID");
/* 105 */       Thread.currentThread().interrupt();
/* 106 */       return null;
/* 107 */     } catch (Exception e) {
/* 108 */       LOGGER.at(Level.WARNING).log("Unexpected error fetching profile by UUID: %s", e.getMessage());
/* 109 */       return null;
/*     */     } 
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
/*     */   public CompletableFuture<PublicGameProfile> getProfileByUuidAsync(@Nonnull UUID uuid, @Nonnull String bearerToken) {
/* 122 */     return CompletableFuture.supplyAsync(() -> getProfileByUuid(uuid, bearerToken));
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
/*     */   @Nullable
/*     */   public PublicGameProfile getProfileByUsername(@Nonnull String username, @Nonnull String bearerToken) {
/*     */     try {
/* 136 */       String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 145 */       HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.profileServiceUrl + "/profile/username/" + this.profileServiceUrl)).header("Accept", "application/json").header("Authorization", "Bearer " + bearerToken).header("User-Agent", AuthConfig.USER_AGENT).timeout(REQUEST_TIMEOUT).GET().build();
/*     */       
/* 147 */       LOGGER.at(Level.FINE).log("Fetching profile by username: %s", username);
/*     */       
/* 149 */       HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
/*     */       
/* 151 */       if (response.statusCode() != 200) {
/* 152 */         LOGGER.at(Level.WARNING).log("Failed to fetch profile by username: HTTP %d - %s", response
/*     */             
/* 154 */             .statusCode(), response.body());
/*     */         
/* 156 */         return null;
/*     */       } 
/*     */       
/* 159 */       PublicGameProfile profile = (PublicGameProfile)PublicGameProfile.CODEC.decodeJson(new RawJsonReader(((String)response
/* 160 */             .body()).toCharArray()), (ExtraInfo)EmptyExtraInfo.EMPTY);
/*     */ 
/*     */ 
/*     */       
/* 164 */       if (profile == null) {
/* 165 */         LOGGER.at(Level.WARNING).log("Profile Service returned invalid response for username: %s", username);
/* 166 */         return null;
/*     */       } 
/*     */       
/* 169 */       LOGGER.at(Level.FINE).log("Successfully fetched profile: %s (%s)", profile.getUsername(), profile.getUuid());
/* 170 */       return profile;
/*     */     }
/* 172 */     catch (IOException e) {
/* 173 */       LOGGER.at(Level.WARNING).log("IO error while fetching profile by username: %s", e.getMessage());
/* 174 */       return null;
/* 175 */     } catch (InterruptedException e) {
/* 176 */       LOGGER.at(Level.WARNING).log("Request interrupted while fetching profile by username");
/* 177 */       Thread.currentThread().interrupt();
/* 178 */       return null;
/* 179 */     } catch (Exception e) {
/* 180 */       LOGGER.at(Level.WARNING).log("Unexpected error fetching profile by username: %s", e.getMessage());
/* 181 */       return null;
/*     */     } 
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
/*     */   public CompletableFuture<PublicGameProfile> getProfileByUsernameAsync(@Nonnull String username, @Nonnull String bearerToken) {
/* 194 */     return CompletableFuture.supplyAsync(() -> getProfileByUsername(username, bearerToken));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> KeyedCodec<T> externalKey(String key, Codec<T> codec) {
/* 200 */     return new KeyedCodec(key, codec, false, true);
/*     */   }
/*     */   
/*     */   public static class PublicGameProfile
/*     */   {
/*     */     public static final BuilderCodec<PublicGameProfile> CODEC;
/*     */     private UUID uuid;
/*     */     private String username;
/*     */     
/*     */     static {
/* 210 */       CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PublicGameProfile.class, PublicGameProfile::new).append(ProfileServiceClient.externalKey("uuid", (Codec<?>)Codec.UUID_STRING), (p, v) -> p.uuid = v, PublicGameProfile::getUuid).add()).append(ProfileServiceClient.externalKey("username", (Codec<?>)Codec.STRING), (p, v) -> p.username = v, PublicGameProfile::getUsername).add()).build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PublicGameProfile() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public PublicGameProfile(UUID uuid, String username) {
/* 225 */       this.uuid = uuid;
/* 226 */       this.username = username;
/*     */     }
/*     */     
/*     */     public UUID getUuid() {
/* 230 */       return this.uuid;
/*     */     }
/*     */     
/*     */     public String getUsername() {
/* 234 */       return this.username;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\auth\ProfileServiceClient.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */