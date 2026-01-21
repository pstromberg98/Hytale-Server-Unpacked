package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;

public interface ContentVerifierProvider {
  boolean hasAssociatedCertificate();
  
  X509CertificateHolder getAssociatedCertificate();
  
  ContentVerifier get(AlgorithmIdentifier paramAlgorithmIdentifier) throws OperatorCreationException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\operator\ContentVerifierProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */