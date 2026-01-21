package META-INF.versions.9.org.bouncycastle.pqc.crypto.hqc;

import org.bouncycastle.pqc.crypto.hqc.HQCKeyParameters;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.util.Arrays;

public class HQCPublicKeyParameters extends HQCKeyParameters {
  private final byte[] pk;
  
  public HQCPublicKeyParameters(HQCParameters paramHQCParameters, byte[] paramArrayOfbyte) {
    super(true, paramHQCParameters);
    this.pk = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getPublicKey() {
    return Arrays.clone(this.pk);
  }
  
  public byte[] getEncoded() {
    return getPublicKey();
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\pqc\crypto\hqc\HQCPublicKeyParameters.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */