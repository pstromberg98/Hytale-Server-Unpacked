package org.bouncycastle.jce.provider;

public class PKIXNameConstraintValidatorException extends Exception {
  private Throwable cause;
  
  public PKIXNameConstraintValidatorException(String paramString) {
    super(paramString);
  }
  
  public PKIXNameConstraintValidatorException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\jce\provider\PKIXNameConstraintValidatorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */