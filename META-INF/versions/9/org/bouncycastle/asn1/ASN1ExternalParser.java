package META-INF.versions.9.org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.InMemoryRepresentable;

public interface ASN1ExternalParser extends ASN1Encodable, InMemoryRepresentable {
  ASN1Encodable readObject() throws IOException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\asn1\ASN1ExternalParser.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */