package org.bouncycastle.jcajce.interfaces;

import java.security.PrivateKey;

public interface MLKEMPrivateKey extends PrivateKey, MLKEMKey {
  MLKEMPublicKey getPublicKey();
  
  byte[] getPrivateData();
  
  byte[] getSeed();
  
  MLKEMPrivateKey getPrivateKey(boolean paramBoolean);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\jcajce\interfaces\MLKEMPrivateKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */