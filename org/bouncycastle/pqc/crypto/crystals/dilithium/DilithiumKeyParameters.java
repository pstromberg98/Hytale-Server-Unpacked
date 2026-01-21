package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DilithiumKeyParameters extends AsymmetricKeyParameter {
  private final DilithiumParameters params;
  
  public DilithiumKeyParameters(boolean paramBoolean, DilithiumParameters paramDilithiumParameters) {
    super(paramBoolean);
    this.params = paramDilithiumParameters;
  }
  
  public DilithiumParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\crystals\dilithium\DilithiumKeyParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */