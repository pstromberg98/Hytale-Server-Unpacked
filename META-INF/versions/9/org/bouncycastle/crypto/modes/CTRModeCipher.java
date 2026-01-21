package META-INF.versions.9.org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.SkippingStreamCipher;

public interface CTRModeCipher extends MultiBlockCipher, SkippingStreamCipher {
  BlockCipher getUnderlyingCipher();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\crypto\modes\CTRModeCipher.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */