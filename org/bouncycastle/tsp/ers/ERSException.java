package org.bouncycastle.tsp.ers;

public class ERSException extends Exception {
  private final Throwable cause;
  
  public ERSException(String paramString) {
    this(paramString, null);
  }
  
  public ERSException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\tsp\ers\ERSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */