package org.bouncycastle.jce.interfaces;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface IESKey extends Key {
  PublicKey getPublic();
  
  PrivateKey getPrivate();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\jce\interfaces\IESKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */