package META-INF.versions.25.org.bouncycastle.jcajce.provider.kdf;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public class Mappings extends AlgorithmProvider {
  public void configure(ConfigurableProvider paramConfigurableProvider) {
    paramConfigurableProvider.addAlgorithm("KDF.HKDF", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi");
    paramConfigurableProvider.addAlgorithm("KDF.HKDF-SHA256", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA256");
    paramConfigurableProvider.addAlgorithm("KDF.HKDF-SHA384", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA384");
    paramConfigurableProvider.addAlgorithm("KDF.HKDF-SHA512", "org.bouncycastle.jcajce.provider.kdf.hkdf.HKDFSpi$HKDFwithSHA512");
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\25\org\bouncycastle\jcajce\provider\kdf\HKDF$Mappings.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */