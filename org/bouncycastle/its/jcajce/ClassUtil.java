package org.bouncycastle.its.jcajce;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;

class ClassUtil {
  public static AlgorithmParameterSpec getGCMSpec(byte[] paramArrayOfbyte, int paramInt) {
    return new GCMParameterSpec(paramInt, paramArrayOfbyte);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\its\jcajce\ClassUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */