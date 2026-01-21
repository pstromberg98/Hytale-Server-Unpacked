package org.bouncycastle.crypto.modes.kgcm;

public class BasicKGCMMultiplier_512 implements KGCMMultiplier {
  private final long[] H = new long[8];
  
  public void init(long[] paramArrayOflong) {
    KGCMUtil_512.copy(paramArrayOflong, this.H);
  }
  
  public void multiplyH(long[] paramArrayOflong) {
    KGCMUtil_512.multiply(paramArrayOflong, this.H, paramArrayOflong);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\modes\kgcm\BasicKGCMMultiplier_512.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */