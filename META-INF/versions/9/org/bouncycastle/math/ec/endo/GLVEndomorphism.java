package META-INF.versions.9.org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ECEndomorphism;

public interface GLVEndomorphism extends ECEndomorphism {
  BigInteger[] decomposeScalar(BigInteger paramBigInteger);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\math\ec\endo\GLVEndomorphism.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */