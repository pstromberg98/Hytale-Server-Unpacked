package org.bouncycastle.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface StagedAgreement extends BasicAgreement {
  AsymmetricKeyParameter calculateStage(CipherParameters paramCipherParameters);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\StagedAgreement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */