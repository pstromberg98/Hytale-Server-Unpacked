package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;

public class AESWrapPadEngine extends RFC5649WrapEngine {
  public AESWrapPadEngine() {
    super((BlockCipher)AESEngine.newInstance());
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\engines\AESWrapPadEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */