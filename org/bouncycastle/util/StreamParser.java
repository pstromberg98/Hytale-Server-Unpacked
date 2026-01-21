package org.bouncycastle.util;

import java.util.Collection;

public interface StreamParser {
  Object read() throws StreamParsingException;
  
  Collection readAll() throws StreamParsingException;
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\bouncycastl\\util\StreamParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */