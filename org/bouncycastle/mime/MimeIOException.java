package org.bouncycastle.mime;

import java.io.IOException;

public class MimeIOException extends IOException {
  private Throwable cause;
  
  public MimeIOException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public MimeIOException(String paramString) {
    super(paramString);
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\mime\MimeIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */