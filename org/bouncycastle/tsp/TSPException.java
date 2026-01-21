package org.bouncycastle.tsp;

public class TSPException extends Exception {
  Throwable underlyingException;
  
  public TSPException(String paramString) {
    super(paramString);
  }
  
  public TSPException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.underlyingException = paramThrowable;
  }
  
  public Exception getUnderlyingException() {
    return (Exception)this.underlyingException;
  }
  
  public Throwable getCause() {
    return this.underlyingException;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\tsp\TSPException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */