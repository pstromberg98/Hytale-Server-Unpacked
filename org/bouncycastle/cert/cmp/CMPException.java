package org.bouncycastle.cert.cmp;

public class CMPException extends Exception {
  private Throwable cause;
  
  public CMPException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public CMPException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\cert\cmp\CMPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */