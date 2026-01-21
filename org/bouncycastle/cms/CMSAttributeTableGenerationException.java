package org.bouncycastle.cms;

public class CMSAttributeTableGenerationException extends CMSRuntimeException {
  Exception e;
  
  public CMSAttributeTableGenerationException(String paramString) {
    super(paramString);
  }
  
  public CMSAttributeTableGenerationException(String paramString, Exception paramException) {
    super(paramString);
    this.e = paramException;
  }
  
  public Exception getUnderlyingException() {
    return this.e;
  }
  
  public Throwable getCause() {
    return this.e;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\cms\CMSAttributeTableGenerationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */