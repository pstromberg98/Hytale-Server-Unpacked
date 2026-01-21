package org.jline.reader;

import java.io.IOException;
import java.util.List;

public interface Editor {
  void open(List<String> paramList) throws IOException;
  
  void run() throws IOException;
  
  void setRestricted(boolean paramBoolean);
}


/* Location:              C:\Users\ranor\AppData\Roaming\Hytale\install\release\package\game\latest\Server\HytaleServer.jar!\org\jline\reader\Editor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */