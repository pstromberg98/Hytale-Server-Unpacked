package org.bouncycastle.its.operator;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;

public interface ITSContentVerifierProvider {
  boolean hasAssociatedCertificate();
  
  ITSCertificate getAssociatedCertificate();
  
  ContentVerifier get(int paramInt) throws OperatorCreationException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\its\operator\ITSContentVerifierProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */