package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public interface GLVEndomorphism extends ECEndomorphism {
  BigInteger[] decomposeScalar(BigInteger paramBigInteger);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\math\ec\endo\GLVEndomorphism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */