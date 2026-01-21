package org.bouncycastle.math.ec;

public class ScaleYPointMap implements ECPointMap {
  protected final ECFieldElement scale;
  
  public ScaleYPointMap(ECFieldElement paramECFieldElement) {
    this.scale = paramECFieldElement;
  }
  
  public ECPoint map(ECPoint paramECPoint) {
    return paramECPoint.scaleY(this.scale);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\math\ec\ScaleYPointMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */