package com.nimbusds.jose;

import com.nimbusds.jose.jca.JCAAware;
import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Set;

public interface JWEProvider extends JOSEProvider, JCAAware<JWEJCAContext> {
  Set<JWEAlgorithm> supportedJWEAlgorithms();
  
  Set<EncryptionMethod> supportedEncryptionMethods();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\com\nimbusds\jose\JWEProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */