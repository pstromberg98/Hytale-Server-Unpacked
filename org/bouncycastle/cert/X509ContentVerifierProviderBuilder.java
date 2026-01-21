package org.bouncycastle.cert;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public interface X509ContentVerifierProviderBuilder {
  ContentVerifierProvider build(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws OperatorCreationException;
  
  ContentVerifierProvider build(X509CertificateHolder paramX509CertificateHolder) throws OperatorCreationException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\cert\X509ContentVerifierProviderBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */