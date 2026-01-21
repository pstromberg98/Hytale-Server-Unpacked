package org.bouncycastle.operator;

public class OperatorException extends Exception {
  private Throwable cause;
  
  public OperatorException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public OperatorException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\OperatorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */