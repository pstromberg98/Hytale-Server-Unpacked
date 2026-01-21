package org.bouncycastle.pqc.crypto.picnic;

import org.bouncycastle.util.Arrays;

public class PicnicPublicKeyParameters extends PicnicKeyParameters {
  private final byte[] publicKey;
  
  public PicnicPublicKeyParameters(PicnicParameters paramPicnicParameters, byte[] paramArrayOfbyte) {
    super(false, paramPicnicParameters);
    this.publicKey = Arrays.clone(paramArrayOfbyte);
  }
  
  public byte[] getEncoded() {
    return Arrays.clone(this.publicKey);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\picnic\PicnicPublicKeyParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */