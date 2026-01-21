package META-INF.versions.9.org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;

public class MayoKeyParameters extends AsymmetricKeyParameter {
  private final MayoParameters params;
  
  public MayoKeyParameters(boolean paramBoolean, MayoParameters paramMayoParameters) {
    super(paramBoolean);
    this.params = paramMayoParameters;
  }
  
  public MayoParameters getParameters() {
    return this.params;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\pqc\crypto\mayo\MayoKeyParameters.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */