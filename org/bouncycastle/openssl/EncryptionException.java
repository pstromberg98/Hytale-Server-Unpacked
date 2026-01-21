package org.bouncycastle.openssl;

public class EncryptionException extends PEMException {
  private Throwable cause;
  
  public EncryptionException(String paramString) {
    super(paramString);
  }
  
  public EncryptionException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\openssl\EncryptionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */