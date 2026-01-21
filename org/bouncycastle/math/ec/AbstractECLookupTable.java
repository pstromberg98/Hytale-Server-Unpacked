package org.bouncycastle.math.ec;

public abstract class AbstractECLookupTable implements ECLookupTable {
  public ECPoint lookupVar(int paramInt) {
    return lookup(paramInt);
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\math\ec\AbstractECLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */