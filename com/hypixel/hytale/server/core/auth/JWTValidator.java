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
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class JWTValidator
/*     */ {
/*  36 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */   
/*     */   private static final long CLOCK_SKEW_SECONDS = 300L;
/*     */   
/*  41 */   private static final JWSAlgorithm SUPPORTED_ALGORITHM = JWSAlgorithm.EdDSA;
/*     */   
/*     */   private static final int MIN_SIGNATURE_LENGTH = 80;
/*     */   
/*     */   private static final int MAX_SIGNATURE_LENGTH = 90;
/*     */   
/*     */   private final SessionServiceClient sessionServiceClient;
/*     */   
/*     */   private final String expectedIssuer;
/*     */   
/*     */   private final String expectedAudience;
/*     */   private volatile JWKSet cachedJwkSet;
/*     */   private volatile long jwksCacheExpiry;
/*  54 */   private final long jwksCacheDurationMs = TimeUnit.HOURS.toMillis(1L);
/*  55 */   private final ReentrantLock jwksFetchLock = new ReentrantLock();
/*  56 */   private volatile CompletableFuture<JWKSet> pendingFetch = null;
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
/*  70 */     this.sessionServiceClient = sessionServiceClient;
/*  71 */     this.expectedIssuer = expectedIssuer;
/*  72 */     this.expectedAudience = expectedAudience;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static String validateJwtStructure(@Nonnull String token, @Nonnull String tokenType) {
/*  81 */     if (token.isEmpty()) {
/*  82 */       return tokenType + " is empty";
/*     */     }
/*     */ 
/*     */     
/*  86 */     String[] parts = token.split("\\.", -1);
/*  87 */     if (parts.length != 3) {
/*  88 */       return String.format("%s has invalid format (expected 3 parts, got %d)", new Object[] { tokenType, Integer.valueOf(parts.length) });
/*     */     }
/*     */ 
/*     */     
/*  92 */     if (parts[2].isEmpty()) {
/*  93 */       return tokenType + " has empty signature - possible signature stripping attack";
/*     */     }
/*     */ 
/*     */     
/*  97 */     int sigLen = parts[2].length();
/*  98 */     if (sigLen < 80 || sigLen > 90) {
/*  99 */       return String.format("%s has invalid signature length: %d (expected %d-%d)", new Object[] { tokenType, 
/* 100 */             Integer.valueOf(sigLen), Integer.valueOf(80), Integer.valueOf(90) });
/*     */     }
/*     */     
/* 103 */     return null;
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
/*     */   public JWTClaims validateToken(@Nonnull String accessToken, @Nullable X509Certificate clientCert) {
/* 116 */     String structError = validateJwtStructure(accessToken, "Access token");
/* 117 */     if (structError != null) {
/* 118 */       LOGGER.at(Level.WARNING).log(structError);
/* 119 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 124 */       SignedJWT signedJWT = SignedJWT.parse(accessToken);
/*     */ 
/*     */       
/* 127 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/* 128 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/* 129 */         LOGGER.at(Level.WARNING).log("Unsupported JWT algorithm: %s (expected EdDSA)", algorithm);
/* 130 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 134 */       if (!verifySignatureWithRetry(signedJWT)) {
/* 135 */         LOGGER.at(Level.WARNING).log("JWT signature verification failed");
/* 136 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 140 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 141 */       JWTClaims claims = new JWTClaims();
/* 142 */       claims.issuer = claimsSet.getIssuer();
/* 143 */       claims
/* 144 */         .audience = (claimsSet.getAudience() != null && !claimsSet.getAudience().isEmpty()) ? claimsSet.getAudience().get(0) : null;
/* 145 */       claims.subject = claimsSet.getSubject();
/*     */       
/* 147 */       claims.username = claimsSet.getStringClaim("username");
/*     */       
/* 149 */       claims.ipAddress = claimsSet.getStringClaim("ip");
/* 150 */       claims
/* 151 */         .issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 152 */       claims
/* 153 */         .expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 154 */       claims
/* 155 */         .notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/*     */ 
/*     */       
/* 158 */       Map<String, Object> cnfClaim = claimsSet.getJSONObjectClaim("cnf");
/* 159 */       if (cnfClaim != null) {
/* 160 */         claims.certificateFingerprint = (String)cnfClaim.get("x5t#S256");
/*     */       }
/*     */ 
/*     */       
/* 164 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 165 */         LOGGER.at(Level.WARNING).log("Invalid issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 166 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 170 */       if (!this.expectedAudience.equals(claims.audience)) {
/* 171 */         LOGGER.at(Level.WARNING).log("Invalid audience: expected %s, got %s", this.expectedAudience, claims.audience);
/* 172 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 176 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 178 */       if (claims.expiresAt == null) {
/* 179 */         LOGGER.at(Level.WARNING).log("Access token missing expiration claim");
/* 180 */         return null;
/*     */       } 
/*     */       
/* 183 */       if (nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 184 */         LOGGER.at(Level.WARNING).log("Token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 185 */         return null;
/*     */       } 
/*     */       
/* 188 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 189 */         LOGGER.at(Level.WARNING).log("Token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 190 */         return null;
/*     */       } 
/*     */       
/* 193 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 194 */         LOGGER.at(Level.WARNING).log("Token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 195 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 199 */       if (!CertificateUtil.validateCertificateBinding(claims.certificateFingerprint, clientCert)) {
/* 200 */         LOGGER.at(Level.WARNING).log("Certificate binding validation failed");
/* 201 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 205 */       if (claims.getSubjectAsUUID() == null) {
/* 206 */         LOGGER.at(Level.WARNING).log("Access token has invalid or missing subject UUID");
/* 207 */         return null;
/*     */       } 
/*     */       
/* 210 */       LOGGER.at(Level.INFO).log("JWT validated successfully for user %s (UUID: %s)", claims.username, claims.subject);
/* 211 */       return claims;
/*     */     }
/* 213 */     catch (ParseException e) {
/* 214 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWT");
/* 215 */       return null;
/* 216 */     } catch (Exception e) {
/* 217 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT validation error");
/* 218 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean verifySignature(SignedJWT signedJWT, JWKSet jwkSet) {
/*     */     try {
/* 227 */       String keyId = signedJWT.getHeader().getKeyID();
/*     */ 
/*     */       
/* 230 */       OctetKeyPair ed25519Key = null;
/* 231 */       for (JWK jwk : jwkSet.getKeys()) {
/* 232 */         if (jwk instanceof OctetKeyPair) { OctetKeyPair okp = (OctetKeyPair)jwk; if (keyId == null || keyId
/* 233 */             .equals(jwk.getKeyID())) {
/* 234 */             ed25519Key = okp;
/*     */             break;
/*     */           }  }
/*     */       
/*     */       } 
/* 239 */       if (ed25519Key == null) {
/* 240 */         LOGGER.at(Level.WARNING).log("No Ed25519 key found for kid=%s", keyId);
/* 241 */         return false;
/*     */       } 
/*     */ 
/*     */       
/* 245 */       Ed25519Verifier verifier = new Ed25519Verifier(ed25519Key);
/* 246 */       boolean valid = signedJWT.verify((JWSVerifier)verifier);
/*     */       
/* 248 */       if (valid) {
/* 249 */         LOGGER.at(Level.FINE).log("JWT signature verified with key kid=%s", keyId);
/*     */       }
/* 251 */       return valid;
/*     */     }
/* 253 */     catch (Exception e) {
/* 254 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("JWT signature verification failed");
/* 255 */       return false;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWKSet getJwkSet() {
/* 264 */     return getJwkSet(false);
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
/* 275 */     long now = System.currentTimeMillis();
/*     */ 
/*     */     
/* 278 */     if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
/* 279 */       return this.cachedJwkSet;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 284 */     this.jwksFetchLock.lock();
/*     */     
/*     */     try {
/* 287 */       if (!forceRefresh && this.cachedJwkSet != null && now < this.jwksCacheExpiry) {
/* 288 */         return this.cachedJwkSet;
/*     */       }
/*     */ 
/*     */       
/* 292 */       CompletableFuture<JWKSet> existing = this.pendingFetch;
/* 293 */       if (existing != null && !existing.isDone()) {
/*     */         
/* 295 */         this.jwksFetchLock.unlock();
/*     */         try {
/* 297 */           return existing.join();
/*     */         } finally {
/* 299 */           this.jwksFetchLock.lock();
/*     */         } 
/*     */       } 
/*     */       
/* 303 */       if (forceRefresh) {
/* 304 */         LOGGER.at(Level.INFO).log("Force refreshing JWKS cache (key rotation or verification failure)");
/*     */       }
/*     */ 
/*     */       
/* 308 */       this.pendingFetch = CompletableFuture.supplyAsync(this::fetchJwksFromService);
/*     */     } finally {
/*     */       
/* 311 */       this.jwksFetchLock.unlock();
/*     */     } 
/*     */ 
/*     */     
/* 315 */     return this.pendingFetch.join();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWKSet fetchJwksFromService() {
/* 324 */     SessionServiceClient.JwksResponse jwksResponse = this.sessionServiceClient.getJwks();
/* 325 */     if (jwksResponse == null || jwksResponse.keys == null || jwksResponse.keys.length == 0) {
/* 326 */       LOGGER.at(Level.WARNING).log("Failed to fetch JWKS or no keys available");
/* 327 */       return this.cachedJwkSet;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 332 */       ArrayList<JWK> jwkList = new ArrayList<>();
/* 333 */       for (SessionServiceClient.JwkKey key : jwksResponse.keys) {
/* 334 */         JWK jwk = convertToJWK(key);
/* 335 */         if (jwk != null) {
/* 336 */           jwkList.add(jwk);
/*     */         }
/*     */       } 
/*     */       
/* 340 */       if (jwkList.isEmpty()) {
/* 341 */         LOGGER.at(Level.WARNING).log("No valid JWKs found in JWKS response");
/* 342 */         return this.cachedJwkSet;
/*     */       } 
/*     */       
/* 345 */       JWKSet newSet = new JWKSet(jwkList);
/* 346 */       this.cachedJwkSet = newSet;
/* 347 */       this.jwksCacheExpiry = System.currentTimeMillis() + this.jwksCacheDurationMs;
/*     */       
/* 349 */       LOGGER.at(Level.INFO).log("JWKS loaded with %d keys", jwkList.size());
/* 350 */       return newSet;
/*     */     }
/* 352 */     catch (Exception e) {
/* 353 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse JWKS");
/* 354 */       return this.cachedJwkSet;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean verifySignatureWithRetry(SignedJWT signedJWT) {
/* 364 */     JWKSet jwkSet = getJwkSet();
/* 365 */     if (jwkSet == null) {
/* 366 */       return false;
/*     */     }
/*     */     
/* 369 */     if (verifySignature(signedJWT, jwkSet)) {
/* 370 */       return true;
/*     */     }
/*     */ 
/*     */     
/* 374 */     LOGGER.at(Level.INFO).log("Signature verification failed with cached JWKS, retrying with fresh keys");
/* 375 */     JWKSet freshJwkSet = getJwkSet(true);
/* 376 */     if (freshJwkSet == null || freshJwkSet == jwkSet)
/*     */     {
/* 378 */       return false;
/*     */     }
/*     */     
/* 381 */     return verifySignature(signedJWT, freshJwkSet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private JWK convertToJWK(SessionServiceClient.JwkKey key) {
/* 390 */     if (!"OKP".equals(key.kty)) {
/* 391 */       LOGGER.at(Level.WARNING).log("Unsupported key type: %s (expected OKP)", key.kty);
/* 392 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 397 */       String json = String.format("{\"kty\":\"OKP\",\"crv\":\"%s\",\"x\":\"%s\",\"kid\":\"%s\",\"alg\":\"EdDSA\"}", new Object[] { key.crv, key.x, key.kid });
/*     */ 
/*     */ 
/*     */       
/* 401 */       return JWK.parse(json);
/* 402 */     } catch (Exception e) {
/* 403 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse Ed25519 key");
/* 404 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void invalidateJwksCache() {
/* 412 */     this.jwksFetchLock.lock();
/*     */     try {
/* 414 */       this.cachedJwkSet = null;
/* 415 */       this.jwksCacheExpiry = 0L;
/* 416 */       this.pendingFetch = null;
/*     */     } finally {
/* 418 */       this.jwksFetchLock.unlock();
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
/*     */   
/*     */   @Nullable
/*     */   public IdentityTokenClaims validateIdentityToken(@Nonnull String identityToken) {
/* 433 */     String structError = validateJwtStructure(identityToken, "Identity token");
/* 434 */     if (structError != null) {
/* 435 */       LOGGER.at(Level.WARNING).log(structError);
/* 436 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 441 */       SignedJWT signedJWT = SignedJWT.parse(identityToken);
/*     */ 
/*     */       
/* 444 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/* 445 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/* 446 */         LOGGER.at(Level.WARNING).log("Unsupported identity token algorithm: %s (expected EdDSA)", algorithm);
/* 447 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 451 */       if (!verifySignatureWithRetry(signedJWT)) {
/* 452 */         LOGGER.at(Level.WARNING).log("Identity token signature verification failed");
/* 453 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 457 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 458 */       IdentityTokenClaims claims = new IdentityTokenClaims();
/* 459 */       claims.issuer = claimsSet.getIssuer();
/* 460 */       claims.subject = claimsSet.getSubject();
/* 461 */       claims.issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 462 */       claims.expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 463 */       claims.notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/* 464 */       claims.scope = claimsSet.getStringClaim("scope");
/*     */ 
/*     */       
/* 467 */       Map<String, Object> profile = claimsSet.getJSONObjectClaim("profile");
/* 468 */       if (profile != null) {
/* 469 */         claims.username = (String)profile.get("username");
/* 470 */         claims.skin = (String)profile.get("skin");
/* 471 */         Object entitlements = profile.get("entitlements");
/* 472 */         if (entitlements instanceof List) { List<?> list = (List)entitlements;
/*     */           
/* 474 */           Objects.requireNonNull(String.class);
/* 475 */           Objects.requireNonNull(String.class);
/* 476 */           claims.entitlements = (String[])list.stream().filter(String.class::isInstance).map(String.class::cast).toArray(x$0 -> new String[x$0]); }
/*     */       
/*     */       } 
/*     */ 
/*     */       
/* 481 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 482 */         LOGGER.at(Level.WARNING).log("Invalid identity token issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 483 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 487 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 489 */       if (claims.expiresAt == null) {
/* 490 */         LOGGER.at(Level.WARNING).log("Identity token missing expiration claim");
/* 491 */         return null;
/*     */       } 
/*     */       
/* 494 */       if (nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 495 */         LOGGER.at(Level.WARNING).log("Identity token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 496 */         return null;
/*     */       } 
/*     */       
/* 499 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 500 */         LOGGER.at(Level.WARNING).log("Identity token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 501 */         return null;
/*     */       } 
/*     */       
/* 504 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 505 */         LOGGER.at(Level.WARNING).log("Identity token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 506 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 510 */       if (claims.getSubjectAsUUID() == null) {
/* 511 */         LOGGER.at(Level.WARNING).log("Identity token has invalid or missing subject UUID");
/* 512 */         return null;
/*     */       } 
/*     */       
/* 515 */       LOGGER.at(Level.INFO).log("Identity token validated successfully for user %s (UUID: %s)", claims.username, claims.subject);
/* 516 */       return claims;
/*     */     }
/* 518 */     catch (ParseException e) {
/* 519 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse identity token");
/* 520 */       return null;
/* 521 */     } catch (Exception e) {
/* 522 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Identity token validation error");
/* 523 */       return null;
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
/*     */   
/*     */   @Nullable
/*     */   public SessionTokenClaims validateSessionToken(@Nonnull String sessionToken) {
/* 538 */     String structError = validateJwtStructure(sessionToken, "Session token");
/* 539 */     if (structError != null) {
/* 540 */       LOGGER.at(Level.WARNING).log(structError);
/* 541 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 546 */       SignedJWT signedJWT = SignedJWT.parse(sessionToken);
/*     */ 
/*     */       
/* 549 */       JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
/* 550 */       if (!SUPPORTED_ALGORITHM.equals(algorithm)) {
/* 551 */         LOGGER.at(Level.WARNING).log("Unsupported session token algorithm: %s (expected EdDSA)", algorithm);
/* 552 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 556 */       if (!verifySignatureWithRetry(signedJWT)) {
/* 557 */         LOGGER.at(Level.WARNING).log("Session token signature verification failed");
/* 558 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 562 */       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
/* 563 */       SessionTokenClaims claims = new SessionTokenClaims();
/* 564 */       claims.issuer = claimsSet.getIssuer();
/* 565 */       claims.subject = claimsSet.getSubject();
/* 566 */       claims.issuedAt = (claimsSet.getIssueTime() != null) ? Long.valueOf(claimsSet.getIssueTime().toInstant().getEpochSecond()) : null;
/* 567 */       claims.expiresAt = (claimsSet.getExpirationTime() != null) ? Long.valueOf(claimsSet.getExpirationTime().toInstant().getEpochSecond()) : null;
/* 568 */       claims.notBefore = (claimsSet.getNotBeforeTime() != null) ? Long.valueOf(claimsSet.getNotBeforeTime().toInstant().getEpochSecond()) : null;
/* 569 */       claims.scope = claimsSet.getStringClaim("scope");
/*     */ 
/*     */       
/* 572 */       if (!this.expectedIssuer.equals(claims.issuer)) {
/* 573 */         LOGGER.at(Level.WARNING).log("Invalid session token issuer: expected %s, got %s", this.expectedIssuer, claims.issuer);
/* 574 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 578 */       long nowSeconds = Instant.now().getEpochSecond();
/*     */       
/* 580 */       if (claims.expiresAt == null) {
/* 581 */         LOGGER.at(Level.WARNING).log("Session token missing expiration claim");
/* 582 */         return null;
/*     */       } 
/*     */       
/* 585 */       if (nowSeconds >= claims.expiresAt.longValue() + 300L) {
/* 586 */         LOGGER.at(Level.WARNING).log("Session token expired (exp: %d, now: %d)", claims.expiresAt, nowSeconds);
/* 587 */         return null;
/*     */       } 
/*     */       
/* 590 */       if (claims.notBefore != null && nowSeconds < claims.notBefore.longValue() - 300L) {
/* 591 */         LOGGER.at(Level.WARNING).log("Session token not yet valid (nbf: %d, now: %d)", claims.notBefore, nowSeconds);
/* 592 */         return null;
/*     */       } 
/*     */       
/* 595 */       if (claims.issuedAt != null && claims.issuedAt.longValue() > nowSeconds + 300L) {
/* 596 */         LOGGER.at(Level.WARNING).log("Session token issued in the future (iat: %d, now: %d)", claims.issuedAt, nowSeconds);
/* 597 */         return null;
/*     */       } 
/*     */ 
/*     */       
/* 601 */       if (claims.getSubjectAsUUID() == null) {
/* 602 */         LOGGER.at(Level.WARNING).log("Session token has invalid or missing subject UUID");
/* 603 */         return null;
/*     */       } 
/*     */       
/* 606 */       LOGGER.at(Level.INFO).log("Session token validated successfully (UUID: %s)", claims.subject);
/* 607 */       return claims;
/*     */     }
/* 609 */     catch (ParseException e) {
/* 610 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Failed to parse session token");
/* 611 */       return null;
/* 612 */     } catch (Exception e) {
/* 613 */       ((HytaleLogger.Api)LOGGER.at(Level.WARNING).withCause(e)).log("Session token validation error");
/* 614 */       return null;
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
/*     */     public String scope;
/*     */     
/*     */     @Nullable
/*     */     public UUID getSubjectAsUUID() {
/* 634 */       if (this.subject == null) return null; 
/*     */       try {
/* 636 */         return UUID.fromString(this.subject);
/* 637 */       } catch (IllegalArgumentException e) {
/* 638 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String[] getScopes() {
/* 647 */       if (this.scope == null || this.scope.isEmpty()) return new String[0]; 
/* 648 */       return this.scope.split(" ");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasScope(@Nonnull String targetScope) {
/* 655 */       for (String s : getScopes()) {
/* 656 */         if (s.equals(targetScope)) return true; 
/*     */       } 
/* 658 */       return false;
/*     */     }
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
/*     */     public String[] entitlements;
/*     */     
/*     */     public String skin;
/*     */     
/*     */     public Long issuedAt;
/*     */     public Long expiresAt;
/*     */     public Long notBefore;
/*     */     public String scope;
/*     */     
/*     */     @Nullable
/*     */     public UUID getSubjectAsUUID() {
/* 682 */       if (this.subject == null) return null; 
/*     */       try {
/* 684 */         return UUID.fromString(this.subject);
/* 685 */       } catch (IllegalArgumentException e) {
/* 686 */         return null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     public String[] getScopes() {
/* 695 */       if (this.scope == null || this.scope.isEmpty()) return new String[0]; 
/* 696 */       return this.scope.split(" ");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasScope(@Nonnull String targetScope) {
/* 703 */       for (String s : getScopes()) {
/* 704 */         if (s.equals(targetScope)) return true; 
/*     */       } 
/* 706 */       return false;
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
/* 729 */       if (this.subject == null) return null; 
/*     */       try {
/* 731 */         return UUID.fromString(this.subject);
/* 732 */       } catch (IllegalArgumentException e) {
/* 733 */         return null;
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\hypixel\hytale\server\core\auth\JWTValidator.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */