package org.bouncycastle.util.encoders;

public class DecoderException extends IllegalStateException {
  private Throwable cause;
  
  DecoderException(String paramString, Throwable paramThrowable) {
    super(paramString);
    this.cause = paramThrowable;
  }
  
  public Throwable getCause() {
    return this.cause;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastl\\util\encoders\DecoderException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */