package org.bouncycastle.pqc.crypto.mldsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MLDSAKeyParameters extends AsymmetricKeyParameter {
  private final MLDSAParameters params;
  
  public MLDSAKeyParameters(boolean paramBoolean, MLDSAParameters paramMLDSAParameters) {
    super(paramBoolean);
    this.params = paramMLDSAParameters;
  }
  
  public MLDSAParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\mldsa\MLDSAKeyParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */