package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface InputExpanderProvider {
  InputExpander get(AlgorithmIdentifier paramAlgorithmIdentifier);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\InputExpanderProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */