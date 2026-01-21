package META-INF.versions.9.org.bouncycastle.pqc.crypto.snova;

import org.bouncycastle.pqc.crypto.snova.SnovaParameters;

class MapGroup2 {
  public final byte[][][][] f11;
  
  public final byte[][][][] f12;
  
  public final byte[][][][] f21;
  
  public MapGroup2(SnovaParameters paramSnovaParameters) {
    int i = paramSnovaParameters.getM();
    int j = paramSnovaParameters.getV();
    int k = paramSnovaParameters.getO();
    int m = paramSnovaParameters.getLsq();
    this.f11 = new byte[i][j][j][m];
    this.f12 = new byte[i][j][k][m];
    this.f21 = new byte[i][k][j][m];
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\pqc\crypto\snova\MapGroup2.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */