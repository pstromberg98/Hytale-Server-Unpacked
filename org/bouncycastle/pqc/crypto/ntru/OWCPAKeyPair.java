package org.bouncycastle.pqc.crypto.ntru;

class OWCPAKeyPair {
  public final byte[] publicKey;
  
  public final byte[] privateKey;
  
  public OWCPAKeyPair(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.publicKey = paramArrayOfbyte1;
    this.privateKey = paramArrayOfbyte2;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\ntru\OWCPAKeyPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */