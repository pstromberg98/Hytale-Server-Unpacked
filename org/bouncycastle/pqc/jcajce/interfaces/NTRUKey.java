package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

public interface NTRUKey extends Key {
  NTRUParameterSpec getParameterSpec();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\jcajce\interfaces\NTRUKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */