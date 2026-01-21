package org.bouncycastle.jce.interfaces;

import javax.crypto.interfaces.DHKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;

public interface ElGamalKey extends DHKey {
  ElGamalParameterSpec getParameters();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\jce\interfaces\ElGamalKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */