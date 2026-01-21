package org.bouncycastle.math.ec;

public interface ECLookupTable {
  int getSize();
  
  ECPoint lookup(int paramInt);
  
  ECPoint lookupVar(int paramInt);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\math\ec\ECLookupTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */