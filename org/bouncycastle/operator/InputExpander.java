package org.bouncycastle.operator;

import java.io.InputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputExpander {
  AlgorithmIdentifier getAlgorithmIdentifier();
  
  InputStream getInputStream(InputStream paramInputStream);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\InputExpander.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */