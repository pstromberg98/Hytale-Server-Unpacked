package org.bouncycastle.pqc.crypto.sphincsplus;

class PK {
  final byte[] seed;
  
  final byte[] root;
  
  PK(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.seed = paramArrayOfbyte1;
    this.root = paramArrayOfbyte2;
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\pqc\crypto\sphincsplus\PK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */