package org.bouncycastle.operator;

import java.io.OutputStream;

public interface AADProcessor {
  OutputStream getAADStream();
  
  byte[] getMAC();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\AADProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */