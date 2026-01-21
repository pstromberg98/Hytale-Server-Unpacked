package org.bouncycastle.crypto;

public interface Committer {
  Commitment commit(byte[] paramArrayOfbyte);
  
  boolean isRevealed(Commitment paramCommitment, byte[] paramArrayOfbyte);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\Committer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */