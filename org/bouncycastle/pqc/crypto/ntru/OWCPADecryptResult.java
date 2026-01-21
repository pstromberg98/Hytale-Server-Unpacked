package org.bouncycastle.pqc.crypto.ntru;

class OWCPADecryptResult {
  final byte[] rm;
  
  final int fail;
  
  public OWCPADecryptResult(byte[] paramArrayOfbyte, int paramInt) {
    this.rm = paramArrayOfbyte;
    this.fail = paramInt;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\ntru\OWCPADecryptResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */