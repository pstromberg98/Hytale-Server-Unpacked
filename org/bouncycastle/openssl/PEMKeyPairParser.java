package org.bouncycastle.openssl;

import java.io.IOException;

interface PEMKeyPairParser {
  PEMKeyPair parse(byte[] paramArrayOfbyte) throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\openssl\PEMKeyPairParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */