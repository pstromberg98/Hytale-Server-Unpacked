package META-INF.versions.9.org.bouncycastle.math.ec;

import org.bouncycastle.math.ec.ECPoint;

public interface ECLookupTable {
  int getSize();
  
  ECPoint lookup(int paramInt);
  
  ECPoint lookupVar(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\META-INF\versions\9\org\bouncycastle\math\ec\ECLookupTable.class
 * Java compiler version: 9 (53.0)
 * JD-Core Version:       1.1.3
 */