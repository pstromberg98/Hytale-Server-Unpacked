package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.util.Arrays;

public class MayoPrivateKeyParameters extends MayoKeyParameters {
  private final byte[] seed_sk;
  
  public MayoPrivateKeyParameters(MayoParameters paramMayoParameters, byte[] paramArrayOfbyte) {
    super(true, paramMayoParameters);
    this.seed_sk = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getEncoded() {
    return Arrays.clone(this.seed_sk);
  }
  
  public byte[] getSeedSk() {
    return Arrays.clone(this.seed_sk);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\mayo\MayoPrivateKeyParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */