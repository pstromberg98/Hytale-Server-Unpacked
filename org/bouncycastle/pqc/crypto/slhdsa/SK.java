package org.bouncycastle.pqc.crypto.slhdsa;

class SK {
  final byte[] seed;
  
  final byte[] prf;
  
  SK(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.seed = paramArrayOfbyte1;
    this.prf = paramArrayOfbyte2;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\slhdsa\SK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */