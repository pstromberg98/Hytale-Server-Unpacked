/*     */ package com.hypixel.hytale.server.core.auth;
/*     */ 
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.nimbusds.jose.JWSAlgorithm;
/*     */ import com.nimbusds.jose.JWSVerifier;
/*     */ import com.nimbusds.jose.crypto.Ed25519Verifier;
/*     */ import com.nimbusds.jose.jwk.JWK;
/*     */ import com.nimbusds.jose.jwk.JWKSet;
/*     */ import com.nimbusds.jose.jwk.OctetKeyPair;
/*     */ import com.nimbusds.jwt.JWTClaimsSet;
/*     */ import com.nimbusds.jwt.SignedJWT;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.text.ParseException;
/*     */ import java.time.Instant;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.logging.Level;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JWTValidator
/*     */ {
/*  35 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */   
/*     */   private static final long CLOCK_SKEW_SECONDS = 300L;
/*     */   
/*  40 */   private static final JWSAlgorithm SUPPORTED_ALGORITHM = JWSAlgorithm.EdDSA;
/*     */   
/*     */   private final SessionServiceClient sessionServiceClient;
/*     */   
/*     */   private final String expectedIssuer;
/*     */   
/*     */   private final String expectedAudience;
/*     */   private volatile JWKSet cachedJwkSet;
/*     */   private volatile long jwksCacheExpiry;
/*  49 */   private final long jwksCacheDurationMs = TimeUnit.HOURS.toMillis(1L);
/*  50 */   private final ReentrantLock jwksFetchLock = new ReentrantLock();
/*  51 */   private volatile CompletableFuture<JWKSet> pendingFetch = null;
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
/*     */   public JWTValidator(@Nonnull SessionServiceClient sessionServiceClient, @Nonnull String expectedIssuer, @Nonnull String expectedAudience) {
/*  65 */     this.sessionServiceClient = sessionServiceClient;
/*  66 */     this.expectedIssuer = expectedIssuer;
/*  67 */     this.expectedAudience = expectedAudience;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public JWTClaims validateToken(@Nonnull String accessToken, @Nullable X509Certificate clientCert) {
/*  79 */     if (accessToken.isEmpty()) {
/*  80 */       LOGGER.at(Level.WARNING).log("Access token is empty");
/*  81 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  86 */       SignedJWT signedJWT = SignedJWT.parse(accessToken);
/*     */ 
/*     */       
/*  89 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/*  90 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/*  91 */         LOGGER.at(Level.WARNING).log("Unsupported JWT algorithm: %s (expected EdDSA)", algorithm);
/*  92 */         return null;
/*     */       } 
/*     */ 
/*     */       
/*  96 */       if (!verifySignatureWithRetry(signedJWT)) {
/*  97 */         LOGGER.at(Level.WARNING).log("JWT signature verification failed");
/*  98 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 102 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 103 */       JWTClaims claims = new JWTClaims();
/* 104 */       claims.issuer = claimsSet.getIssuer();
/* 105 */       claims
/* 106 */         .audience = (claimsSet.getAudience() != null && !claimsSet.getAudience().isEmpty()) ? claimsSet.getAudience().get(0) : null;
/* 107 */       claims.subject = claimsSet.getSubject();
/*     */       
/* 109 */       claims.username = claimsSet.getStringClaim("username");
/*     */       
/* 111 */       claims.ipAddress = claimsSet.getStringClaim("ip");
/* 112 */       claims
/* 113 */         .issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 114 */       claims
/* 115 */         .expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 116 */       claims
/* 117 */         .notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/*     */ 
/*     */       
/* 120 */       Map<String, Object> cnfClaim = claimsSet.getJSONObjectClaim("cnf");
/* 121 */       if (cnfClaim != null) {
/* 122 */         claims.certificateFingerprint = (String)cnfClaim.get("x5t#S256");
/*     */       }
/*     */ 
/*     */       
/* 126 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 127 */         LOGGER.at(Level.WARNING).log("Invalid issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 128 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 132 */       if (!this.expectedAudience.equals(claims.audience)) {
/* 133 */         LOGGER.at(Level.WARNING).log("Invalid audience: expected %s, got %s", this.expectedAudience, claims.audience);
/* 134 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 138 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 140 */       if (claims.expiresAt != null && nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 141 */         LOGGER.at(Level.WARNING).log("Token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 142 */         return null;
/*     */       } 
/*     */       
/* 145 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 146 */         LOGGER.at(Level.WARNING).log("Token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 147 */         return null;
/*     */       } 
/*     */       
/* 150 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 151 */         LOGGER.at(Level.WARNING).log("Token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 152 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 156 */       if (!CertificateUtil.validateCertificateBinding(claims.certificateFingerprint, clientCert)) {
/* 157 */         LOGGER.at(Level.WARNING).log("Certificate binding validation failed");
/* 158 */         return null;
/*     */       } 
/*     */       
/* 161 */       LOGGER.at(Level.INFO).log("JWT validated successfully for user %s (UUID: %s)", claims.username, claims.subject);
/* 162 */       return claims;
/*     */     }
/* 164 */     catch (ParseException e) {
/* 165 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWT");
/* 166 */       return null;
/* 167 */     } catch (Exception e) {
/* 168 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT validation error");
/* 169 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean verifySignature(SignedJWT signedJWT, JWKSet jwkSet) {
/*     */     try {
/* 178 */       String keyId = signedJWT.getHeader().getKeyID();
/*     */ 
/*     */       
/* 181 */       OctetKeyPair ed25519Key = null;
/* 182 */       for (JWK jwk : jwkSet.getKeys()) {
/* 183 */         if (jwk instanceof OctetKeyPair) { OctetKeyPair okp = (OctetKeyPair)jwk; if (keyId == null || keyId
/* 184 */             .equals(jwk.getKeyID())) {
/* 185 */             ed25519Key = okp;
/*     */             break;
/*     */           }  }
/*     */       
/*     */       } 
/* 190 */       if (ed25519Key == null) {
/* 191 */         LOGGER.at(Level.WARNING).log("No Ed25519 key found for kid=%s", keyId);
/* 192 */         return false;
/*     */       } 
/*     */ 
/*     */       
/* 196 */       Ed25519Verifier verifier = new Ed25519Verifier(ed25519Key);
/* 197 */       boolean valid = signedJWT.verify((JWSVerifier)verifier);
/*     */       
/* 199 */       if (valid) {
/* 200 */         LOGGER.at(Level.FINE).log("JWT signature verified with key kid=%s", keyId);
/*     */       }
/* 202 */       return valid;
/*     */     }
/* 204 */     catch (Exception e) {
/* 205 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT signature verification failed");
/* 206 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWKSet getJwkSet() {
/* 215 */     return getJwkSet(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWKSet getJwkSet(boolean forceRefresh) {
/* 226 */     long now = System.currentTimeMillis();
/*     */ 
/*     */     
/* 229 */     if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
/* 230 */       return this.cachedJwkSet;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 235 */     this.jwksFetchLock.lock();
/*     */     
/*     */     try {
/* 238 */       if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
/* 239 */         return this.cachedJwkSet;
/*     */       }
/*     */ 
/*     */       
/* 243 */       CompletableFuture<JWKSet> existing = this.pendingFetch;
/* 244 */       if (existing != null && !existing.isDone()) {
/*     */         
/* 246 */         this.jwksFetchLock.unlock();
/*     */         try {
/* 248 */           return existing.join();
/*     */         } finally {
/* 250 */           this.jwksFetchLock.lock();
/*     */         } 
/*     */       } 
/*     */       
/* 254 */       if (forceRefresh) {
/* 255 */         LOGGER.at(Level.INFO).log("Force refreshing JWKS cache (key rotation or verification failure)");
/*     */       }
/*     */ 
/*     */       
/* 259 */       this.pendingFetch = CompletableFuture.supplyAsync(this::fetchJwksFromService);
/*     */     } finally {
/*     */       
/* 262 */       this.jwksFetchLock.unlock();
/*     */     } 
/*     */ 
/*     */     
/* 266 */     return this.pendingFetch.join();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWKSet fetchJwksFromService() {
/* 275 */     SessionServiceClient.JwksResponse jwksResponse = this.sessionServiceClient.getJwks();
/* 276 */     if (jwksResponse == null || jwksResponse.keys == null || jwksResponse.keys.length == 0) {
/* 277 */       LOGGER.at(Level.WARNING).log("Failed to fetch JWKS or no keys available");
/* 278 */       return this.cachedJwkSet;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 283 */       ArrayList<JWK> jwkList = new ArrayList<>();
/* 284 */       for (SessionServiceClient.JwkKey key : jwksResponse.keys) {
/* 285 */         JWK jwk = convertToJWK(key);
/* 286 */         if (jwk != null) {
/* 287 */           jwkList.add(jwk);
/*     */         }
/*     */       } 
/*     */       
/* 291 */       if (jwkList.isEmpty()) {
/* 292 */         LOGGER.at(Level.WARNING).log("No valid JWKs found in JWKS response");
/* 293 */         return this.cachedJwkSet;
/*     */       } 
/*     */       
/* 296 */       JWKSet newSet = new JWKSet(jwkList);
/* 297 */       this.cachedJwkSet = newSet;
/* 298 */       this.jwksCacheExpiry = System.currentTimeMillis() + this.jwksCacheDurationMs;
/*     */       
/* 300 */       LOGGER.at(Level.INFO).log("JWKS loaded with %d keys", jwkList.size());
/* 301 */       return newSet;
/*     */     }
/* 303 */     catch (Exception e) {
/* 304 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWKS");
/* 305 */       return this.cachedJwkSet;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean verifySignatureWithRetry(SignedJWT signedJWT) {
/* 315 */     JWKSet jwkSet = getJwkSet();
/* 316 */     if (jwkSet == null) {
/* 317 */       return false;
/*     */     }
/*     */     
/* 320 */     if (verifySignature(signedJWT, jwkSet)) {
/* 321 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 325 */     LOGGER.at(Level.INFO).log("Signature verification failed with cached JWKS, retrying with fresh keys");
/* 326 */     JWKSet freshJwkSet = getJwkSet(true);
/* 327 */     if (freshJwkSet == null || freshJwkSet == jwkSet)
/*     */     {
/* 329 */       return false;
/*     */     }
/*     */     
/* 332 */     return verifySignature(signedJWT, freshJwkSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWK convertToJWK(SessionServiceClient.JwkKey key) {
/* 341 */     if (!"OKP".equals(key.kty)) {
/* 342 */       LOGGER.at(Level.WARNING).log("Unsupported key type: %s (expected OKP)", key.kty);
/* 343 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 348 */       String json = String.format("{\"kty\":\"OKP\",\"crv\":\"%s\",\"x\":\"%s\",\"kid\":\"%s\",\"alg\":\"EdDSA\"}", new Object[] { key.crv, key.x, key.kid });
/*     */ 
/*     */ 
/*     */       
/* 352 */       return JWK.parse(json);
/* 353 */     } catch (Exception e) {
/* 354 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse Ed25519 key");
/* 355 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void invalidateJwksCache() {
/* 363 */     this.jwksFetchLock.lock();
/*     */     try {
/* 365 */       this.cachedJwkSet = null;
/* 366 */       this.jwksCacheExpiry = 0L;
/* 367 */       this.pendingFetch = null;
/*     */     } finally {
/* 369 */       this.jwksFetchLock.unlock();
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
/*     */   @Nullable
/*     */   public IdentityTokenClaims validateIdentityToken(@Nonnull String identityToken) {
/* 383 */     if (identityToken.isEmpty()) {
/* 384 */       LOGGER.at(Level.WARNING).log("Identity token is empty");
/* 385 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 390 */       SignedJWT signedJWT = SignedJWT.parse(identityToken);
/*     */ 
/*     */       
/* 393 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/* 394 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/* 395 */         LOGGER.at(Level.WARNING).log("Unsupported identity token algorithm: %s (expected EdDSA)", algorithm);
/* 396 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 400 */       if (!verifySignatureWithRetry(signedJWT)) {
/* 401 */         LOGGER.at(Level.WARNING).log("Identity token signature verification failed");
/* 402 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 406 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 407 */       IdentityTokenClaims claims = new IdentityTokenClaims();
/* 408 */       claims.issuer = claimsSet.getIssuer();
/* 409 */       claims.subject = claimsSet.getSubject();
/* 410 */       claims.username = claimsSet.getStringClaim("username");
/* 411 */       claims.issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 412 */       claims.expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 413 */       claims.notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/* 414 */       claims.scope = claimsSet.getStringClaim("scope");
/*     */ 
/*     */       
/* 417 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 418 */         LOGGER.at(Level.WARNING).log("Invalid identity token issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 419 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 423 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 425 */       if (claims.expiresAt == null) {
/* 426 */         LOGGER.at(Level.WARNING).log("Identity token missing expiration claim");
/* 427 */         return null;
/*     */       } 
/*     */       
/* 430 */       if (nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 431 */         LOGGER.at(Level.WARNING).log("Identity token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 432 */         return null;
/*     */       } 
/*     */       
/* 435 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 436 */         LOGGER.at(Level.WARNING).log("Identity token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 437 */         return null;
/*     */       } 
/*     */       
/* 440 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 441 */         LOGGER.at(Level.WARNING).log("Identity token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 442 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 446 */       if (claims.getSubjectAsUUID() == null) {
/* 447 */         LOGGER.at(Level.WARNING).log("Identity token has invalid or missing subject UUID");
/* 448 */         return null;
/*     */       } 
/*     */       
/* 451 */       LOGGER.at(Level.INFO).log("Identity token validated successfully for user %s (UUID: %s)", claims.username, claims.subject);
/* 452 */       return claims;
/*     */     }
/* 454 */     catch (ParseException e) {
/* 455 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse identity token");
/* 456 */       return null;
/* 457 */     } catch (Exception e) {
/* 458 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Identity token validation error");
/* 459 */       return null;
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
/*     */   @Nullable
/*     */   public SessionTokenClaims validateSessionToken(@Nonnull String sessionToken) {
/* 473 */     if (sessionToken.isEmpty()) {
/* 474 */       LOGGER.at(Level.WARNING).log("Session token is empty");
/* 475 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 480 */       SignedJWT signedJWT = SignedJWT.parse(sessionToken);
/*     */ 
/*     */       
/* 483 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/* 484 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/* 485 */         LOGGER.at(Level.WARNING).log("Unsupported session token algorithm: %s (expected EdDSA)", algorithm);
/* 486 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 490 */       if (!verifySignatureWithRetry(signedJWT)) {
/* 491 */         LOGGER.at(Level.WARNING).log("Session token signature verification failed");
/* 492 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 496 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 497 */       SessionTokenClaims claims = new SessionTokenClaims();
/* 498 */       claims.issuer = claimsSet.getIssuer();
/* 499 */       claims.subject = claimsSet.getSubject();
/* 500 */       claims.issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 501 */       claims.expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 502 */       claims.notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/*     */ 
/*     */       
/* 505 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 506 */         LOGGER.at(Level.WARNING).log("Invalid session token issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 507 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 511 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 513 */       if (claims.expiresAt == null) {
/* 514 */         LOGGER.at(Level.WARNING).log("Session token missing expiration claim");
/* 515 */         return null;
/*     */       } 
/*     */       
/* 518 */       if (nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 519 */         LOGGER.at(Level.WARNING).log("Session token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 520 */         return null;
/*     */       } 
/*     */       
/* 523 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 524 */         LOGGER.at(Level.WARNING).log("Session token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 525 */         return null;
/*     */       } 
/*     */       
/* 528 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 529 */         LOGGER.at(Level.WARNING).log("Session token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 530 */         return null;
/*     */       } 
/*     */       
/* 533 */       LOGGER.at(Level.INFO).log("Session token validated successfully");
/* 534 */       return claims;
/*     */     }
/* 536 */     catch (ParseException e) {
/* 537 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse session token");
/* 538 */       return null;
/* 539 */     } catch (Exception e) {
/* 540 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Session token validation error");
/* 541 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static class SessionTokenClaims
/*     */   {
/*     */     public String issuer;
/*     */     
/*     */     public String subject;
/*     */     
/*     */     public Long issuedAt;
/*     */     
/*     */     public Long expiresAt;
/*     */     
/*     */     public Long notBefore;
/*     */   }
/*     */ 
/*     */   
/*     */   public static class IdentityTokenClaims
/*     */   {
/*     */     public String issuer;
/*     */     
/*     */     public String subject;
/*     */     
/*     */     public String username;
/*     */     
/*     */     public Long issuedAt;
/*     */     public Long expiresAt;
/*     */     public Long notBefore;
/*     */     public String scope;
/*     */     
/*     */     @Nullable
/*     */     public UUID getSubjectAsUUID() {
/* 575 */       if (this.subject == null) return null; 
/*     */       try {
/* 577 */         return UUID.fromString(this.subject);
/* 578 */       } catch (IllegalArgumentException e) {
/* 579 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String[] getScopes() {
/* 588 */       if (this.scope == null || this.scope.isEmpty()) return new String[0]; 
/* 589 */       return this.scope.split(" ");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasScope(@Nonnull String targetScope) {
/* 596 */       for (String s : getScopes()) {
/* 597 */         if (s.equals(targetScope)) return true; 
/*     */       } 
/* 599 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class JWTClaims
/*     */   {
/*     */     public String issuer;
/*     */     
/*     */     public String audience;
/*     */     
/*     */     public String subject;
/*     */     
/*     */     public String username;
/*     */     
/*     */     public String ipAddress;
/*     */     public Long issuedAt;
/*     */     public Long expiresAt;
/*     */     public Long notBefore;
/*     */     public String certificateFingerprint;
/*     */     
/*     */     @Nullable
/*     */     public UUID getSubjectAsUUID() {
/* 622 */       if (this.subject == null) return null; 
/*     */       try {
/* 624 */         return UUID.fromString(this.subject);
/* 625 */       } catch (IllegalArgumentException e) {
/* 626 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\auth\JWTValidator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */