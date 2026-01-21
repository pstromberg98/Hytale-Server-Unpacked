package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.BIKEParameterSpec;

public interface BIKEKey extends Key {
  BIKEParameterSpec getParameterSpec();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\jcajce\interfaces\BIKEKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */