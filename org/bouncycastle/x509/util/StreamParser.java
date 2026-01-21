package org.bouncycastle.x509.util;

import java.util.Collection;

public interface StreamParser {
  Object read() throws StreamParsingException;
  
  Collection readAll() throws StreamParsingException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastle\x50\\util\StreamParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */