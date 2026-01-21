package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException extends IOException {
  private Throwable cause;
  
  public OperatorStreamException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\OperatorStreamException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */