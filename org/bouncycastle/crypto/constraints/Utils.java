package org.bouncycastle.crypto.constraints;

import java.util.Set;

class Utils {
  static void addAliases(Set<String> paramSet) {
    if (paramSet.contains("RC4")) {
      paramSet.add("ARC4");
    } else if (paramSet.contains("ARC4")) {
      paramSet.add("RC4");
    } 
  }
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\crypto\constraints\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */