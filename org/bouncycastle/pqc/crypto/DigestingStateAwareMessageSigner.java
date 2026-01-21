package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class DigestingStateAwareMessageSigner extends DigestingMessageSigner {
  private final StateAwareMessageSigner signer;
  
  public DigestingStateAwareMessageSigner(StateAwareMessageSigner paramStateAwareMessageSigner, Digest paramDigest) {
    super(paramStateAwareMessageSigner, paramDigest);
    this.signer = paramStateAwareMessageSigner;
  }
  
  public AsymmetricKeyParameter getUpdatedPrivateKey() {
    return this.signer.getUpdatedPrivateKey();
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\DigestingStateAwareMessageSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */