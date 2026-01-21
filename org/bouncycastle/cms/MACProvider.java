package org.bouncycastle.cms;

import java.io.IOException;

interface MACProvider {
  byte[] getMAC();
  
  void init() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\cms\MACProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */