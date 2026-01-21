package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

@Deprecated
public interface JWKSetCache {
  void put(JWKSet paramJWKSet);
  
  JWKSet get();
  
  boolean requiresRefresh();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\jwk\source\JWKSetCache.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */