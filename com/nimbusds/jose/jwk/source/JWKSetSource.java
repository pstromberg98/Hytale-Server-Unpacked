package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import java.io.Closeable;

public interface JWKSetSource<C extends com.nimbusds.jose.proc.SecurityContext> extends Closeable {
  JWKSet getJWKSet(JWKSetCacheRefreshEvaluator paramJWKSetCacheRefreshEvaluator, long paramLong, C paramC) throws KeySourceException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\jwk\source\JWKSetSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */