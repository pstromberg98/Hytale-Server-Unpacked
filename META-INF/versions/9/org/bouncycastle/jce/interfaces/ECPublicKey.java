package META-INF.versions.9.org.bouncycastle.jce.interfaces;

import java.security.PublicKey;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.math.ec.ECPoint;

public interface ECPublicKey extends ECKey, PublicKey {
  ECPoint getQ();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\jce\interfaces\ECPublicKey.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */