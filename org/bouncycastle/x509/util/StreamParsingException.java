package org.bouncycastle.x509.util;

public class StreamParsingException extends Exception {
  Throwable _e;
  
  public StreamParsingException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this._e = paramThrowable;
  }
  
  public Throwable getCause() {
    return this._e;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\x50\\util\StreamParsingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */