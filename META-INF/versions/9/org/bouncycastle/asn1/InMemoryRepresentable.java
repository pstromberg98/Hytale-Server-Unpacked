package META-INF.versions.9.org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;

public interface InMemoryRepresentable {
  ASN1Primitive getLoadedObject() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\asn1\InMemoryRepresentable.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */