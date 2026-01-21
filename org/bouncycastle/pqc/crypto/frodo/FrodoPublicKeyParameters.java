package org.bouncycastle.pqc.crypto.frodo;

import org.bouncycastle.util.Arrays;

public class FrodoPublicKeyParameters extends FrodoKeyParameters {
  public byte[] publicKey;
  
  public byte[] getPublicKey() {
    return Arrays.clone(this.publicKey);
  }
  
  public byte[] getEncoded() {
    return getPublicKey();
  }
  
  public FrodoPublicKeyParameters(FrodoParameters paramFrodoParameters, byte[] paramArrayOfbyte) {
    super(false, paramFrodoParameters);
    this.publicKey = Arrays.clone(paramArrayOfbyte);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\frodo\FrodoPublicKeyParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */