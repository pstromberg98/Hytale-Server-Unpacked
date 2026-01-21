package org.bouncycastle.tsp;

import java.io.IOException;

public class TSPIOException extends IOException {
  Throwable underlyingException;
  
  public TSPIOException(String paramString) {
    super(paramString);
  }
  
  public TSPIOException(String paramString, Throwable paramThrowable) {
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


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\tsp\TSPIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */