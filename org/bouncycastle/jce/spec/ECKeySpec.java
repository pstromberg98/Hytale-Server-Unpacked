package org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;

public class ECKeySpec implements KeySpec {
  private ECParameterSpec spec;
  
  protected ECKeySpec(ECParameterSpec paramECParameterSpec) {
    this.spec = paramECParameterSpec;
  }
  
  public ECParameterSpec getParams() {
    return this.spec;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\jce\spec\ECKeySpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */