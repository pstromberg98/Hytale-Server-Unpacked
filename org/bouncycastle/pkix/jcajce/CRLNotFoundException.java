package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPathValidatorException;

class CRLNotFoundException extends CertPathValidatorException {
  CRLNotFoundException(String paramString) {
    super(paramString);
  }
  
  public CRLNotFoundException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pkix\jcajce\CRLNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */