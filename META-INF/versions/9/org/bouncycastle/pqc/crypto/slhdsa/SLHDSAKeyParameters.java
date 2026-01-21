package META-INF.versions.9.org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.slhdsa.SLHDSAParameters;

public class SLHDSAKeyParameters extends AsymmetricKeyParameter {
  private final SLHDSAParameters parameters;
  
  protected SLHDSAKeyParameters(boolean paramBoolean, SLHDSAParameters paramSLHDSAParameters) {
    super(paramBoolean);
    this.parameters = paramSLHDSAParameters;
  }
  
  public SLHDSAParameters getParameters() {
    return this.parameters;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\pqc\crypto\slhdsa\SLHDSAKeyParameters.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */