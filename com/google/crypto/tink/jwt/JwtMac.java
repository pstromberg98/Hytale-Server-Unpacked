package com.google.crypto.tink.jwt;

import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;

@Immutable
public interface JwtMac {
  String computeMacAndEncode(RawJwt paramRawJwt) throws GeneralSecurityException;
  
  VerifiedJwt verifyMacAndDecode(String paramString, JwtValidator paramJwtValidator) throws GeneralSecurityException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\google\crypto\tink\jwt\JwtMac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */