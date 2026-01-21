package org.jline.builtins.telnet;

import java.net.InetAddress;

public interface ConnectionFilter {
  boolean isAllowed(InetAddress paramInetAddress);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\jline\builtins\telnet\ConnectionFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */