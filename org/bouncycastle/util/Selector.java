package org.bouncycastle.util;

public interface Selector<T> extends Cloneable {
  boolean match(T paramT);
  
  Object clone();
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastl\\util\Selector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */